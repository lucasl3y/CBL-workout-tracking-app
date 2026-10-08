package storage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import model.ExerciseSet;
import model.WorkoutSession;

public class FileStorage {

    // Renamed to comply with Checkstyle standard
    private final String fileName = "workout_history.txt";

    // Saves the completed session to a text file
    public void saveSession(WorkoutSession session) {
        // The 'true' parameter in FileWriter means it appends to the file, 
        // instead of overwriting it
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
}