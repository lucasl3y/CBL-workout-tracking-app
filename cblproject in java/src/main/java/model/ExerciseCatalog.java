package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ExerciseCatalog {

    private HashMap<String, ArrayList<String>> categoryMap;
    private HashMap<String, String> muscleMap;

    public ExerciseCatalog() {
        categoryMap = new HashMap<>();
        muscleMap = new HashMap<>();
        loadStandardExercises();
    }

    private void loadStandardExercises() {
        categoryMap.put("Chest & Triceps", new ArrayList<>(Arrays.asList("Bench Press", "Push-ups", "Incline Dumbbell Press")));
        categoryMap.put("Back & Biceps", new ArrayList<>(Arrays.asList("Deadlift", "Pull-ups", "Barbell Row", "Lat Pulldown")));
        categoryMap.put("Legs", new ArrayList<>(Arrays.asList("Squat", "Leg Press", "Calf Raises", "Bulgarian Split Squat")));
        categoryMap.put("Shoulders & Arms", new ArrayList<>(Arrays.asList("Overhead Press", "Lateral Raises", "Bicep Curls", "Tricep Extensions")));
        categoryMap.put("Core", new ArrayList<>(Arrays.asList("Crunches", "Plank")));

        muscleMap.put("Bench Press", "Chest, Triceps, Shoulders");
        muscleMap.put("Push-ups", "Chest, Triceps, Core");
        muscleMap.put("Incline Dumbbell Press", "Upper Chest, Triceps");
        muscleMap.put("Deadlift", "Lower Back, Hamstrings, Glutes");
        muscleMap.put("Pull-ups", "Lats, Biceps, Upper Back");
        muscleMap.put("Barbell Row", "Upper Back, Lats, Biceps");
        muscleMap.put("Lat Pulldown", "Lats, Biceps");
        muscleMap.put("Squat", "Quadriceps, Glutes, Hamstrings");
        muscleMap.put("Leg Press", "Quadriceps, Glutes, Hamstrings");
        muscleMap.put("Calf Raises", "Calves");
        muscleMap.put("Bulgarian Split Squat", "Quadriceps, Glutes");
        muscleMap.put("Overhead Press", "Shoulders, Triceps");
        muscleMap.put("Lateral Raises", "Side Shoulders");
        muscleMap.put("Bicep Curls", "Biceps");
        muscleMap.put("Tricep Extensions", "Triceps");
        muscleMap.put("Crunches", "Abs, Core");
        muscleMap.put("Plank", "Core");
    }

    // Adds a custom user-defined exercise to the catalog
    public void addCustomExercise(String category, String name, String muscles) {
        categoryMap.putIfAbsent(category, new ArrayList<>());
        categoryMap.get(category).add(name);
        muscleMap.put(name, muscles);
    }

    public String[] getCategories() {
        return categoryMap.keySet().toArray(new String[0]);
    }

    public String[] getExercisesByCategory(String category) {
        if (categoryMap.containsKey(category)) {
            return categoryMap.get(category).toArray(new String[0]);
        }
        return new String[]{};
    }

    public String getTargetMuscles(String exerciseName) {
        return muscleMap.getOrDefault(exerciseName, "Unknown muscles");
    }
}