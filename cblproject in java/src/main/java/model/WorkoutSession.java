package model;

import java.util.ArrayList;

public class WorkoutSession {
    
    private String date;
    private ArrayList<ExerciseSet> exercises;

    public WorkoutSession(String date) {
        this.date = date;
        this.exercises = new ArrayList<>();
    }

    public void addExercise(ExerciseSet exercise) {
        exercises.add(exercise);
    }

    public String getDate() {
        return date;
    }

    public ArrayList<ExerciseSet> getExercises() {
        return exercises;
    }
}