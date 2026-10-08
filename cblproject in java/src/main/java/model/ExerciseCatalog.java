package model;

import java.util.HashMap;

public class ExerciseCatalog {

    private HashMap<String, String[]> categoryMap;
    private HashMap<String, String> muscleMap;

    public ExerciseCatalog() {
        categoryMap = new HashMap<>();
        muscleMap = new HashMap<>();
        loadStandardExercises();
    }

    private void loadStandardExercises() {
        // 1. Categorize exercises
        categoryMap.put("Chest & Triceps", new String[]{"Bench Press", "Push-ups", "Incline Dumbbell Press"});
        categoryMap.put("Back & Biceps", new String[]{"Deadlift", "Pull-ups", "Barbell Row", "Lat Pulldown"});
        categoryMap.put("Legs", new String[]{"Squat", "Leg Press", "Calf Raises", "Bulgarian Split Squat"});
        categoryMap.put("Shoulders & Arms", new String[]{"Overhead Press", "Lateral Raises", "Bicep Curls", "Tricep Extensions"});
        categoryMap.put("Core", new String[]{"Crunches", "Plank"});

        // 2. Map exercises to specific muscles
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

    public String[] getCategories() {
        return new String[]{"Chest & Triceps", "Back & Biceps", "Legs", "Shoulders & Arms", "Core"};
    }

    public String[] getExercisesByCategory(String category) {
        return categoryMap.getOrDefault(category, new String[]{});
    }

    public String getTargetMuscles(String exerciseName) {
        return muscleMap.getOrDefault(exerciseName, "Unknown muscles");
    }
}