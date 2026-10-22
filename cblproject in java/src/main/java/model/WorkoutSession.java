package model;

import java.util.ArrayList;
import java.util.UUID;

public class WorkoutSession {
    
    private String date;
    private int duration;
    private String trainedMuscles; // NEW: Stores a comma-separated list of muscles
    private ArrayList<ExerciseSet> exercises;
    private String sessionId;

    public WorkoutSession(String date, int duration, String trainedMuscles) {
        this(date, duration, trainedMuscles, UUID.randomUUID().toString());
    }

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
