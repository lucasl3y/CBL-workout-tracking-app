package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import storage.FileStorage;
import model.WorkoutSession;

public class HistoryPanel extends JPanel {

    private final FileStorage fileStorage;
    private YearMonth currentMonth;
    private Set<String> workoutDates;
    private List<WorkoutSession> sessions;
    private JPanel calendarGrid;
    private JLabel monthLabel;
    private JLabel summaryLabel;

    public HistoryPanel(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        this.currentMonth = YearMonth.now();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton prevButton = new JButton("<< Previous");
        JButton nextButton = new JButton("Next >>");
        JButton refreshButton = new JButton("Refresh Data");

        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        headerPanel.add(prevButton);
        headerPanel.add(monthLabel);
        headerPanel.add(nextButton);
        headerPanel.add(refreshButton);
        topPanel.add(headerPanel, BorderLayout.NORTH);

        summaryLabel = new JLabel("", JLabel.CENTER);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        summaryLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        topPanel.add(summaryLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        add(calendarGrid, BorderLayout.CENTER);

        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });
        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });
        refreshButton.addActionListener(e -> updateCalendar());
        updateCalendar();
    }

    public void refresh() {
        updateCalendar();
    }

    private void updateCalendar() {
        // Reload from disk each time so save/delete changes are immediately reflected.
        calendarGrid.removeAll();
        sessions = fileStorage.loadSessions();
        workoutDates = new HashSet<>();
        for (WorkoutSession session : sessions) {
            workoutDates.add(session.getDate());
        }

        String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        monthLabel.setText(monthName + " " + currentMonth.getYear());
        updateSummary();

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            JLabel label = new JLabel(day, JLabel.CENTER);
            label.setFont(new Font("Dialog", Font.BOLD, 14));
            calendarGrid.add(label);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        for (int i = 1; i < dayOfWeek; i++) {
            calendarGrid.add(new JLabel(""));
        }

        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            String dateString = String.format(Locale.ROOT, "%04d-%02d-%02d",
                    currentMonth.getYear(), currentMonth.getMonthValue(), day);
            JButton dayButton = new JButton(String.valueOf(day));
            if (workoutDates.contains(dateString)) {
                dayButton.setBackground(new Color(144, 238, 144));
                dayButton.setOpaque(true);
                dayButton.addActionListener(e -> showSessionsForDate(dateString));
            } else {
                dayButton.setEnabled(false);
            }
            calendarGrid.add(dayButton);
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private void updateSummary() {
        MonthlySummary summary = calculateMonthlySummary();
        summaryLabel.setText("Workouts: " + summary.workouts + " | Training Days: "
                + summary.trainingDays.size() + "  ·  Total Duration: " + summary.duration + " min");
    }

    private MonthlySummary calculateMonthlySummary() {
        // Include all saved sessions in the displayed month, including future dates.
        MonthlySummary summary = new MonthlySummary();
        for (WorkoutSession session : sessions) {
            if (isWorkoutInCurrentMonth(session)) {
                summary.add(session);
            }
        }
        return summary;
    }

    private boolean isWorkoutInCurrentMonth(WorkoutSession session) {
        try {
            LocalDate date = LocalDate.parse(session.getDate());
            return YearMonth.from(date).equals(currentMonth);
        } catch (DateTimeParseException ignored) {
            // Exclude records with invalid dates from monthly totals.
            return false;
        }
    }

    private static class MonthlySummary {

        private int workouts;
        private int duration;
        private final HashSet<String> trainingDays = new HashSet<>();

        private void add(WorkoutSession session) {
            workouts++;
            duration += session.getDuration();
            trainingDays.add(session.getDate());
        }
    }

    private void showSessionsForDate(String date) {
        ArrayList<WorkoutSession> dateSessions = new ArrayList<>();
        for (WorkoutSession session : sessions) {
            if (date.equals(session.getDate())) {
                dateSessions.add(session);
            }
        }
        if (dateSessions.isEmpty()) {
            return;
        }

        // A date may contain multiple independent sessions; let the user choose one.
        String[] choices = new String[dateSessions.size()];
        for (int i = 0; i < dateSessions.size(); i++) {
            choices[i] = "Session " + (i + 1) + " - " + dateSessions.get(i).getDuration() + " min";
        }
        Object selection = JOptionPane.showInputDialog(this, "Select a workout session:",
                "Workouts on " + date, JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
        if (selection == null) {
            return;
        }
        int index = java.util.Arrays.asList(choices).indexOf(selection);
        WorkoutSession selected = dateSessions.get(index);
        String[] options = {"Close", "Delete This Session"};
        int action = JOptionPane.showOptionDialog(this, detailsFor(selected), "Workout on " + date,
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (action == 1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete only this workout session? This action cannot be undone.",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (fileStorage.deleteSession(selected.getSessionId())) {
                    updateCalendar();
                    JOptionPane.showMessageDialog(this, "Workout successfully deleted.",
                            "Deleted", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "The workout could not be deleted.",
                            "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private String detailsFor(WorkoutSession session) {
        StringBuilder details = new StringBuilder();
        details.append("Duration: ").append(session.getDuration()).append(" minutes\n");
        if (session.getTrainedMuscles() != null && !session.getTrainedMuscles().isEmpty()) {
            details.append("Trained Muscles: ").append(session.getTrainedMuscles()).append("\n");
        }
        for (model.ExerciseSet set : session.getExercises()) {
            details.append(set).append("\n");
        }
        return details.toString();
    }
}
