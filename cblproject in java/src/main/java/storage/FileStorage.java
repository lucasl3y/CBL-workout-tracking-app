package storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import constant.WorkoutFileFormat;
import model.ExerciseSet;
import model.WorkoutSession;

public class FileStorage {

    public boolean saveSession(WorkoutSession session) {
        // New sessions include an ID so two workouts on the same date can be separated.
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(WorkoutFileFormat.FILE_NAME, true))) {
            WorkoutHistoryCodec.writeSession(writer, session);
            writer.flush();
            return !writer.checkError();
        } catch (IOException e) {
            System.err.println("Could not save workout session: " + e.getMessage());
            return false;
        }
    }

    public List<WorkoutSession> loadSessions() {
        // Parse each delimited block into one complete workout session.
        ArrayList<WorkoutSession> sessions = new ArrayList<>();
        Path path = Path.of(WorkoutFileFormat.FILE_NAME);
        if (!Files.exists(path)) {
            return sessions;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(WorkoutFileFormat.FILE_NAME))) {
            ArrayList<String> block = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (WorkoutHistoryCodec.isDateHeader(line)) {
                    block.clear();
                    block.add(line);
                } else if (!block.isEmpty()) {
                    if (line.startsWith(WorkoutFileFormat.SESSION_END)) {
                        WorkoutHistoryCodec.addSession(block, sessions);
                        block = new ArrayList<>();
                    } else {
                        block.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read workout history: " + e.getMessage());
        }
        return sessions;
    }

    public HashMap<String, String> loadHistory() {
        // Build a date-grouped text summary from the saved workout sessions.
        HashMap<String, StringBuilder> grouped = new HashMap<>();
        for (WorkoutSession session : loadSessions()) {
            StringBuilder details = grouped.computeIfAbsent(session.getDate(), key -> new StringBuilder());
            if (details.length() > 0) {
                details.append("\n--- Additional Workout ---\n\n");
            }
            details.append("Duration: ").append(session.getDuration()).append(" minutes\n");
            if (session.getTrainedMuscles() != null && !session.getTrainedMuscles().isEmpty()) {
                details.append("Trained Muscles: ").append(session.getTrainedMuscles()).append("\n");
            }
            double volume = 0.0;
            for (ExerciseSet set : session.getExercises()) {
                details.append(set).append("\n");
                volume += set.getWeight() * set.getReps();
            }
            details.append(String.format(Locale.US, "Total Volume: %.1f kg", volume));
        }

        HashMap<String, String> history = new HashMap<>();
        for (String date : grouped.keySet()) {
            history.put(date, grouped.get(date).toString());
        }
        return history;
    }

    public boolean deleteSession(String sessionId) {
        // Rewrite through a temporary file so a failed delete does not truncate the history.
        Path path = Path.of(WorkoutFileFormat.FILE_NAME);
        if (!Files.exists(path)) {
            return false;
        }

        try {
            HistoryFile historyFile = parseHistoryFile(Files.readAllLines(path));
            int targetIndex = findSessionIndex(historyFile.getSessionBlocks(), sessionId);
            if (targetIndex == -1) {
                return false;
            }
            rewriteHistoryFile(path, historyFile, targetIndex);
            return true;
        } catch (IOException e) {
            System.err.println("Could not delete workout session: " + e.getMessage());
            return false;
        }
    }

    private HistoryFile parseHistoryFile(List<String> lines) {
        ArrayList<String> prefixLines = new ArrayList<>();
        ArrayList<List<String>> sessionBlocks = new ArrayList<>();
        ArrayList<String> currentBlock = null;
        for (String line : lines) {
            if (WorkoutHistoryCodec.isDateHeader(line)) {
                currentBlock = new ArrayList<>();
                currentBlock.add(line);
                sessionBlocks.add(currentBlock);
            } else if (currentBlock == null) {
                prefixLines.add(line);
            } else {
                currentBlock.add(line);
                if (line.startsWith(WorkoutFileFormat.SESSION_END)) {
                    currentBlock = null;
                }
            }
        }
        return new HistoryFile(prefixLines, sessionBlocks);
    }

    private int findSessionIndex(List<List<String>> blocks, String sessionId) {
        ArrayList<WorkoutSession> sessions = new ArrayList<>();
        for (List<String> block : blocks) {
            WorkoutHistoryCodec.addSession(block, sessions);
        }
        for (int i = 0; i < sessions.size(); i++) {
            if (sessionId.equals(sessions.get(i).getSessionId())) {
                return i;
            }
        }
        return -1;
    }

    private void rewriteHistoryFile(Path path, HistoryFile historyFile, int excludedIndex)
            throws IOException {
        Path absolutePath = path.toAbsolutePath();
        Path tempPath = Files.createTempFile(absolutePath.getParent(), "workout-history-", ".tmp");
        try {
            writeHistoryFile(tempPath, historyFile, excludedIndex);
            replaceHistoryFile(tempPath, absolutePath);
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }

    private void writeHistoryFile(Path tempPath, HistoryFile historyFile, int excludedIndex)
            throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(tempPath))) {
            writeLines(writer, historyFile.getPrefixLines());
            List<List<String>> blocks = historyFile.getSessionBlocks();
            for (int i = 0; i < blocks.size(); i++) {
                if (i != excludedIndex) {
                    writeLines(writer, blocks.get(i));
                }
            }
            writer.flush();
            if (writer.checkError()) {
                throw new IOException("Failed while writing updated workout history");
            }
        }
    }

    private void writeLines(PrintWriter writer, List<String> lines) {
        for (String line : lines) {
            writer.println(line);
        }
    }

    private void replaceHistoryFile(Path tempPath, Path path) throws IOException {
        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
