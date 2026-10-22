package model;

/**
 * Represents one exercise set with its number, weight in kilograms, and reps.
 * Template drafts may initially contain zero weight and reps.
 */
public class ExerciseSet {

    private final String exerciseName;

    // Set numbers may contain gaps after rows are deleted from the draft.
    private final int setNumber;
    private final double weight;
    private final int reps;

    public ExerciseSet(String exerciseName, int setNumber, double weight, int reps) {
        this.exerciseName = exerciseName;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
    }

    public String getExerciseName() { 
        return exerciseName; 
    }
    
    public int getSetNumber() { 
        return setNumber; 
    }
    
    public double getWeight() { 
        return weight; 
    }
    
    public int getReps() { 
        return reps; 
    }

    /**
     * Formats this set for workout details and history file storage.
     */
    @Override
    public String toString() {
        return exerciseName + " (Set " + setNumber + ") | Weight: " + weight + "kg | Reps: " + reps;
    }
}