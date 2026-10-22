package model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents one workout session containing its date, duration,
 * trained muscles, and exercise sets.
 */
public class WorkoutSession {

    private final String date;
    private final int duration;
    private final String trainedMuscles;
    private final ArrayList<ExerciseSet> exercises;
    private final String sessionId;

    /**
     * Creates a new workout session with a generated unique ID.
     */
    public WorkoutSession(String date, int duration, String trainedMuscles) {
        this(date, duration, trainedMuscles, UUID.randomUUID().toString());
    }

    /**
     * Reconstructs a session using an existing ID, such as when loading saved history.
     */
    public WorkoutSession(String date, int duration, String trainedMuscles, String sessionId) {
        this.date = date;
        this.duration = duration;
        this.trainedMuscles = trainedMuscles;
        this.sessionId = sessionId;
        this.exercises = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public String getTrainedMuscles() {
        return trainedMuscles;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void addExercise(ExerciseSet set) {
        exercises.add(set);
    }

    public ArrayList<ExerciseSet> getExercises() {
        return exercises;
    }
}
