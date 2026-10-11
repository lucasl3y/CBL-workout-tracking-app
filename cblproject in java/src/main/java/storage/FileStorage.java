package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import model.ExerciseSet;
import model.WorkoutSession;

public class FileStorage {

    private final String fileName = "workout_history.txt";

    public void saveSession(WorkoutSession session) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println("=== Date: " + session.getDate() + " ===");
            writer.println("Duration: " + session.getDuration() + " minutes");
            writer.println("Trained Muscles: " + session.getTrainedMuscles()); // NEW: Save muscles
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

    public HashMap<String, String> loadHistory() {
        HashMap<String, String> history = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String currentDate = null;
            StringBuilder sessionData = new StringBuilder();
            double currentVolume = 0.0; 

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("=== Date: ")) {
                    currentDate = line.replace("=== Date: ", "").replace(" ===", "").trim();
                    sessionData = new StringBuilder();
                    currentVolume = 0.0; 
                    
                } else if (line.startsWith("============================")) {
                    if (currentDate != null) {
                        sessionData.append("\n----------------------------\n");
                        sessionData.append(String.format(Locale.US, "Total Volume: %.1f kg\n", currentVolume));
                        
                        if (history.containsKey(currentDate)) {
                            String existingData = history.get(currentDate);
                            history.put(currentDate, existingData + "\n\n--- Additional Workout ---\n\n" + sessionData.toString());
                        } else {
                            history.put(currentDate, sessionData.toString());
                        }
                    }
                    
                } else if (!line.trim().isEmpty()) {
                    sessionData.append(line).append("\n");
                    
                    if (line.contains("| Weight:") && line.contains("| Reps:")) {
                        try {
                            String[] parts = line.split("\\|");
                            if (parts.length >= 3) {
                                String weightStr = parts[1].replaceAll("[^0-9.]", "");
                                String repsStr = parts[2].replaceAll("[^0-9]", "");
                                
                                if (!weightStr.isEmpty() && !repsStr.isEmpty()) {
                                    double weight = Double.parseDouble(weightStr);
                                    int reps = Integer.parseInt(repsStr);
                                    currentVolume += (weight * reps);
                                }
                            }
                        } catch (Exception ex) {
                            // Ignore formatting errors for volume calculation
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No history file found yet. Starting fresh.");
        }
        
        return history;
    }

    public void deleteSession(String dateToRemove) {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }
        
        ArrayList<String> allLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allLines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file for deletion.");
            e.printStackTrace();
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
            boolean skipMode = false;
            for (String line : allLines) {
                
                if (line.startsWith("=== Date: " + dateToRemove + " ===")) {
                    skipMode = true; 
                }
                
                if (!skipMode) {
                    writer.println(line);
                }
                
                if (skipMode && line.startsWith("============================")) {
                    skipMode = false; 
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving file after deletion.");
            e.printStackTrace();
        }
    }
}