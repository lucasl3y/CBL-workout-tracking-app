package model;

import java.util.ArrayList;

public class TemplateManager {

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
    
    // Reps parameter removed, everything defaults to 0
    private void addSets(ArrayList<ExerciseSet> list, String name, int totalSets) {
        for (int i = 1; i <= totalSets; i++) {
            list.add(new ExerciseSet(name, i, 0.0, 0)); 
        }
    }

    public String[] getTemplateNames() {
        return new String[]{"Select Template...", "Push", "Pull", "Legs"};
    }
}