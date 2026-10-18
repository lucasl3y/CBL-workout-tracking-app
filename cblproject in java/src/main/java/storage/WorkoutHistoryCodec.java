package storage;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import model.ExerciseSet;
import model.WorkoutSession;
import constant.WorkoutFileFormat;

/** Converts workout sessions between the domain model and the text-file format. */
final class WorkoutHistoryCodec {

    private WorkoutHistoryCodec() {
        // Utility class; instances are not needed.
    }

    static void writeSession(PrintWriter writer, WorkoutSession session) {
        writer.println(WorkoutFileFormat.DATE_PREFIX + session.getDate()
                + WorkoutFileFormat.DATE_SUFFIX);
        writer.println(WorkoutFileFormat.SESSION_ID_PREFIX + session.getSessionId());
        writer.println(WorkoutFileFormat.DURATION_PREFIX + session.getDuration() + " minutes");
        writer.println(WorkoutFileFormat.MUSCLES_PREFIX + session.getTrainedMuscles());
        for (ExerciseSet exercise : session.getExercises()) {
            writer.println(exercise);
        }
        writer.println(WorkoutFileFormat.SESSION_END);
        writer.println();
    }

    static boolean isDateHeader(String line) {
        return line.startsWith(WorkoutFileFormat.DATE_PREFIX)
                && line.endsWith(WorkoutFileFormat.DATE_SUFFIX);
    }

    static void addSession(List<String> block, List<WorkoutSession> sessions) {
        if (block.isEmpty() || !isDateHeader(block.get(0))) {
            return;
        }
        String date = extractDate(block.get(0));
        String sessionId = null;
        String muscles = "";
        int duration = 0;
        ArrayList<ExerciseSet> sets = new ArrayList<>();

        for (String line : block.subList(1, block.size())) {
            if (line.startsWith(WorkoutFileFormat.SESSION_ID_PREFIX)) {
                sessionId = line.substring(WorkoutFileFormat.SESSION_ID_PREFIX.length()).trim();
            } else if (line.startsWith(WorkoutFileFormat.DURATION_PREFIX)) {
                duration = parseDuration(line);
            } else if (line.startsWith(WorkoutFileFormat.MUSCLES_PREFIX)) {
                muscles = line.substring(WorkoutFileFormat.MUSCLES_PREFIX.length()).trim();
            } else {
                parseSet(line, sets);
            }
        }

        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = createLegacySessionId(date, sessions);
        }
        WorkoutSession session = new WorkoutSession(date, duration, muscles, sessionId);
        for (ExerciseSet set : sets) {
            session.addExercise(set);
        }
        sessions.add(session);
    }

    private static String extractDate(String line) {
        return line.substring(WorkoutFileFormat.DATE_PREFIX.length(),
                line.length() - WorkoutFileFormat.DATE_SUFFIX.length()).trim();
    }

    private static int parseDuration(String line) {
        String value = line.substring(WorkoutFileFormat.DURATION_PREFIX.length());
        int separator = value.indexOf(' ');
        if (separator >= 0) {
            value = value.substring(0, separator);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String createLegacySessionId(String date, List<WorkoutSession> sessions) {
        int sameDateCount = 0;
        for (WorkoutSession session : sessions) {
            if (date.equals(session.getDate())) {
                sameDateCount++;
            }
        }
        return "legacy:" + date + ":" + sameDateCount;
    }

    private static void parseSet(String line, List<ExerciseSet> sets) {
        int marker = line.lastIndexOf(WorkoutFileFormat.SET_MARKER);
        if (marker >= 0 && parseCurrentSet(line, marker, sets)) {
            return;
        }
        parseLegacySets(line, sets);
    }

    private static boolean parseCurrentSet(String line, int marker, List<ExerciseSet> sets) {
        int setEnd = line.indexOf(WorkoutFileFormat.SET_WEIGHT_SEPARATOR, marker);
        if (setEnd < 0) {
            return false;
        }
        int weightStart = setEnd + WorkoutFileFormat.SET_WEIGHT_SEPARATOR.length();
        int weightEnd = line.indexOf(WorkoutFileFormat.WEIGHT_REPS_SEPARATOR, weightStart);
        if (weightEnd < 0) {
            return false;
        }

        try {
            String name = line.substring(0, marker).trim();
            int setNumber = Integer.parseInt(line.substring(
                    marker + WorkoutFileFormat.SET_MARKER.length(), setEnd));
            double weight = Double.parseDouble(line.substring(weightStart, weightEnd));
            int reps = Integer.parseInt(line.substring(
                    weightEnd + WorkoutFileFormat.WEIGHT_REPS_SEPARATOR.length()));
            sets.add(new ExerciseSet(name, setNumber, weight, reps));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void parseLegacySets(String line, List<ExerciseSet> sets) {
        int weightStart = line.indexOf(WorkoutFileFormat.LEGACY_WEIGHT_SEPARATOR);
        if (weightStart < 0) {
            return;
        }
        int weightEnd = line.indexOf(WorkoutFileFormat.LEGACY_WEIGHT_SETS_SEPARATOR, weightStart);
        if (weightEnd < 0) {
            return;
        }
        int setsStart = weightEnd + WorkoutFileFormat.LEGACY_WEIGHT_SETS_SEPARATOR.length();
        int setsEnd = line.indexOf(WorkoutFileFormat.REPS_SEPARATOR, setsStart);
        if (setsEnd < 0) {
            return;
        }

        try {
            String name = line.substring(0, weightStart).trim();
            double weight = Double.parseDouble(line.substring(
                    weightStart + WorkoutFileFormat.LEGACY_WEIGHT_SEPARATOR.length(), weightEnd));
            int setCount = Integer.parseInt(line.substring(setsStart, setsEnd));
            int reps = Integer.parseInt(line.substring(
                    setsEnd + WorkoutFileFormat.REPS_SEPARATOR.length()));
            addLegacySets(name, weight, setCount, reps, sets);
        } catch (NumberFormatException e) {
            // Ignore malformed legacy lines, matching the old parser behavior.
        }
    }

    private static void addLegacySets(String name, double weight, int setCount, int reps,
            List<ExerciseSet> sets) {
        // Expand the old one-line set count into individual set records.
        for (int setNumber = 1; setNumber <= setCount; setNumber++) {
            sets.add(new ExerciseSet(name, setNumber, weight, reps));
        }
    }
}
