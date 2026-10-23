# Workout Tracker App

## Overview

Our Workout Tracker App is a Java Swing desktop app that helps you record and review your workouts.

You can choose exercises already included in the app or create your own, add several sets to a session, and save its date and duration. The app also has workout templates, a monthly attendance calendar, monthly totals, and a progress table for each exercise.

You can choose past, present, or future dates for your workouts.

## Requirements

- JDK 25.
- NetBeans with Java and Maven support.
- Internet access to download Maven plugins and dependencies during the first setup.

We have successfully compiled and run the project in NetBeans using JDK 25.

## Build and Run

1. Extract the ZIP file.
2. In NetBeans, select **File → Open Project**.
3. Open the `cblproject in java` folder containing `pom.xml`.
4. Wait for NetBeans to load the Maven project and download any required plugins and dependencies.
5. Make sure the project uses **JDK 25**.
6. Right-click the project in the Projects panel and select **Clean and Build**.
7. Right-click the project and select **Run**. If NetBeans asks you to choose a main class, select **WorkoutTrackerApp**.

Keep `2IP90_checks.xml` in the same folder as `pom.xml`. The Maven Checkstyle configuration uses this file.

You do not need to create a history file yourself. If there are no saved records yet, Attendance History and Progress will be empty. Follow the Quick Start below to add your first workout.

## Quick Start

To create your first workout:

1. In **Log Workout**, choose today's date and enter **30** for the duration.
2. Select **Back & Biceps**, then **Deadlift**.
3. Enter **5** for weight and **10** for reps. Click **Add 1 Set**.
4. Change reps to **8** and click **Add 1 Set** again.
5. You should now see two sets in the table and a total volume of **90.0 kg**.
6. Click **Save Workout** once to save both sets together as one session.
7. Open **Attendance History** and click the highlighted date to view the saved session.
8. Open **Progress** and select **Deadlift** to see the record containing both sets.

## Main Features

### Log Workout

- Choose a category and an exercise to see its target muscles.
- Use **Custom Exercise** to add your own exercise with a name, category, and target muscles.
- Use **Quick Load Templates** to load a Push, Pull, or Legs template.
- Click **Add 1 Set** to add a set to the current session.
- Change weight and reps directly in the session table.
- Use **Delete Selected Row** to remove a set before saving.
- Click **Save Workout** to save the whole session.

Template rows start with zero weight and reps. Enter valid reps for every row before saving.

### Attendance History

- Use **Previous** and **Next** to move between months.
- Dates with saved sessions are highlighted.
- Click a highlighted date to view its sessions and their details.
- Use **Delete This Session** to delete a saved session. The app asks you to confirm first.
- Use **Refresh Data** to reload the calendar and monthly totals.

The monthly summary shows how many sessions you saved, how many different days you trained, and the total duration. Future-dated sessions are also included in the totals for the month being shown.

### Progress

Select an exercise to see its saved records. Each row shows one session, including its date, session number, number of sets, and weight × reps for all matching sets.

Newer dates appear first.

## Input Rules

- Weight must be a finite number of 0 or more. Decimal weights are allowed.
- Reps must be a positive whole number.
- Duration must be a whole number from 1 to 600 minutes.
- Add at least one set before saving a session.
- Every row must have valid values before the session can be saved.
- Custom exercise names cannot repeat an existing name, even with different capital letters or extra spaces at the beginning or end.
- Custom exercises need both a name and target muscles.

## Data Storage

Workout history is saved in `workout_tracker_history.txt` in the current user's home directory.

Typical locations are:

- Windows: `C:\Users\<username>\workout_tracker_history.txt`

The app uses Java's `user.home` property to find this folder. The save location stays the same even if you start the app from a different folder.

The history file is created when you save your first workout. Saved sessions are still there when you close and reopen the app. Deleting a session also updates the file.

## Known Limitations

Custom exercises stay in the exercise catalog only while the app is running. After restarting, you need to create them again if you want to add them to new workouts. Their previously saved workout records are still available in Attendance History and Progress.

The app allows future dates, but it does not mark workouts as planned or completed. Both are included in the monthly totals.

## Manual Testing

We manually tested the final version using normal user actions and some boundary cases, including:

- Adding, editing, deleting, and saving several sets.
- Loading the same template twice without repeating set numbers.
- Rejecting invalid workout input and duplicate custom exercise names.
- Saving several sessions on the same date and deleting only the selected one.
- Including future-dated workouts in calendar highlights and monthly totals.
- Showing all saved sets in session details and exercise progress.
- Keeping saved workout records after restarting the app.
- Checking long session details and using the app with a smaller window.

## Authors

- Qun Cao — 2086816
- Lucas Ley — 2378450
- CBL Project Group 118

## GitHub Repository

https://github.com/lucasl3y/CBL-workout-tracking-app
 