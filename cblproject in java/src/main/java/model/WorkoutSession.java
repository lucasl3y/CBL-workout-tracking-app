package model;

import java.util.ArrayList;

public class WorkoutSession {
    
    private String date;
    private int duration;
    private String trainedMuscles; // NEW: Stores a comma-separated list of muscles
    private ArrayList<ExerciseSet> exercises;

    public WorkoutSession(String date, int duration, String trainedMuscles) {
        this.date = date;
        this.duration = duration;
        this.trainedMuscles = trainedMuscles;
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

    public void addExercise(ExerciseSet set) {
        exercises.add(set);
    }

    public ArrayList<ExerciseSet> getExercises() {
        return exercises;
    }
}