package model;

import java.util.ArrayList;

/**
 * Creates predefined workout templates with fresh exercise sets on each request.
 */
public class TemplateManager {

    /**
     * Creates a fresh list of draft sets for the requested template.
     * Returns an empty list if the name does not match a predefined template.
     */
    public ArrayList<ExerciseSet> getTemplate(String templateName) {
        ArrayList<ExerciseSet> templateExercises = new ArrayList<>();

        if (templateName.equals("Push")) {
            addSets(templateExercises, "Bench Press", 3);
            addSets(templateExercises, "Incline Dumbbell Press", 3);
            addSets(templateExercises, "Overhead Press", 3);
            addSets(templateExercises, "Tricep Extensions", 3);
        } else if (templateName.equals("Pull")) {
            addSets(templateExercises, "Deadlift", 3);
            addSets(templateExercises, "Pull-ups", 3);
            addSets(templateExercises, "Barbell Row", 3);
            addSets(templateExercises, "Bicep Curls", 3);
        } else if (templateName.equals("Legs")) {
            addSets(templateExercises, "Squat", 3);
            addSets(templateExercises, "Leg Press", 3);
            addSets(templateExercises, "Bulgarian Split Squat", 3);
            addSets(templateExercises, "Calf Raises", 4);
        }

        return templateExercises;
    }

    // Template sets start with zero weight and reps; users complete them before saving.
    private void addSets(ArrayList<ExerciseSet> list, String name, int totalSets) {
        for (int i = 1; i <= totalSets; i++) {
            list.add(new ExerciseSet(name, i, 0.0, 0)); 
        }
    }

    public String[] getTemplateNames() {
        return new String[]{"Select Template...", "Push", "Pull", "Legs"};
    }
}