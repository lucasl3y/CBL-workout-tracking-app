package model;

public class ExerciseSet {
    
    private String exerciseName;
    private double weight;
    private int sets;
    private int reps;

    public ExerciseSet(String exerciseName, double weight, int sets, int reps) {
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.sets = sets;
        this.reps = reps;
    }

    // Converts the object data into a structured string for the text file
    @Override
    public String toString() {
        return exerciseName + " | Weight: " + weight + "kg | Sets: " + sets + " | Reps: " + reps;
    }
}