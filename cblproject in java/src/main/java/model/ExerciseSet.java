package model;

public class ExerciseSet {

    private final String exerciseName;
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

    @Override
    public String toString() {
        return exerciseName + " (Set " + setNumber + ") | Weight: " + weight + "kg | Reps: " + reps;
    }
}