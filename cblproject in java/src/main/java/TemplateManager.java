package model;

import java.util.ArrayList;

public class TemplateManager {

    public ArrayList<ExerciseSet> getTemplate(String templateName) {
        ArrayList<ExerciseSet> templateExercises = new ArrayList<>();

        if (templateName.equals("Push")) {
            addSets(templateExercises, "Bench Press", 3, 10);
            addSets(templateExercises, "Incline Dumbbell Press", 3, 10);
            addSets(templateExercises, "Overhead Press", 3, 8);
            addSets(templateExercises, "Tricep Extensions", 3, 12);
        } else if (templateName.equals("Pull")) {
            addSets(templateExercises, "Deadlift", 3, 5);
            addSets(templateExercises, "Pull-ups", 3, 8);
            addSets(templateExercises, "Barbell Row", 3, 10);
            addSets(templateExercises, "Bicep Curls", 3, 12);
        } else if (templateName.equals("Legs")) {
            addSets(templateExercises, "Squat", 3, 8);
            addSets(templateExercises, "Leg Press", 3, 10);
            addSets(templateExercises, "Bulgarian Split Squat", 3, 10);
            addSets(templateExercises, "Calf Raises", 4, 15);
        }

        return templateExercises;
    }
    
    // Helper method to automatically generate multiple sets for one exercise
    private void addSets(ArrayList<ExerciseSet> list, String name, int totalSets, int reps) {
        for (int i = 1; i <= totalSets; i++) {
            // Generates Set 1, Set 2, Set 3, etc.
            list.add(new ExerciseSet(name, i, 0.0, reps)); 
        }
    }

    public String[] getTemplateNames() {
        return new String[]{"Select Template...", "Push", "Pull", "Legs"};
    }
}