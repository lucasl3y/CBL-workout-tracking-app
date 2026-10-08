package storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import model.ExerciseSet;
import model.WorkoutSession;

public class FileStorage {

    private final String fileName = "workout_history.txt";

    public void saveSession(WorkoutSession session) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println("=== Date: " + session.getDate() + " ===");
            for (ExerciseSet exercise : session.getExercises()) {
                writer.println(exercise.toString());
            }
            writer.println("============================\n");
            System.out.println("Session successfully saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error: Could not save the workout session.");
            e.printStackTrace();
        }
    }

    // NEW METHOD: Reads the file and returns a map of Date -> Workout Details
    public HashMap<String, String> loadHistory() {
        HashMap<String, String> history = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String currentDate = null;
            StringBuilder sessionData = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("=== Date: ")) {
                    currentDate = line.replace("=== Date: ", "").replace(" ===", "").trim();
                    sessionData = new StringBuilder();
                } else if (line.startsWith("============================")) {
                    if (currentDate != null) {
                        history.put(currentDate, sessionData.toString());
                    }
                } else if (!line.trim().isEmpty()) {
                    sessionData.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("No history file found yet. Starting fresh.");
        }
        
        return history;
    }
}