package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.ExerciseSet;
import model.WorkoutSession;
import storage.FileStorage;

public class ProgressPanel extends JPanel {

    private final FileStorage fileStorage;
    private final JComboBox<String> exerciseCombo;
    private final DefaultTableModel tableModel;
    private final JLabel emptyLabel;

    public ProgressPanel(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.add(new JLabel("Select Exercise:"));
        exerciseCombo = new JComboBox<>();
        exerciseCombo.setPrototypeDisplayValue("A long custom exercise name");
        selectorPanel.add(exerciseCombo);
        add(selectorPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[] {"Date", "Session", "Sets", "Weight x Reps"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = new JTable(tableModel);
        historyTable.setFillsViewportHeight(true);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        emptyLabel = new JLabel("No saved workout history yet.", JLabel.CENTER);
        add(emptyLabel, BorderLayout.SOUTH);
        exerciseCombo.addActionListener(e -> updateTable());
        refresh();
    }

    public void refresh() {
        // Only exercises found in saved sessions are offered in the selector.
        String previousSelection = (String) exerciseCombo.getSelectedItem();
        List<WorkoutSession> sessions = fileStorage.loadSessions();
        LinkedHashSet<String> names = new LinkedHashSet<>();
        LinkedHashSet<String> normalizedNames = new LinkedHashSet<>();

        for (WorkoutSession session : sessions) {
            for (ExerciseSet set : session.getExercises()) {
                String name = set.getExerciseName();
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                // Merge names case-insensitively while preserving the first spelling for display.
                String displayName = name.trim();
                if (normalizedNames.add(normalizeName(displayName))) {
                    names.add(displayName);
                }
            }
        }

        ArrayList<String> sortedNames = new ArrayList<>(names);
        Collections.sort(sortedNames, String.CASE_INSENSITIVE_ORDER);
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Select Exercise");
        for (String name : sortedNames) {
            model.addElement(name);
        }
        exerciseCombo.setModel(model);
        if (previousSelection != null && names.contains(previousSelection)) {
            exerciseCombo.setSelectedItem(previousSelection);
        }
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        String selectedName = (String) exerciseCombo.getSelectedItem();
        if (selectedName == null || "Select Exercise".equals(selectedName)) {
            showSelectionPrompt();
            return;
        }

        List<WorkoutSession> sessions = fileStorage.loadSessions();
        addHistoryRows(selectedName, sessions);
        emptyLabel.setVisible(tableModel.getRowCount() == 0);
    }

    private void showSelectionPrompt() {
        emptyLabel.setText(exerciseCombo.getItemCount() > 1
                ? "Select an exercise to view history."
                : "No saved workout history yet.");
        emptyLabel.setVisible(true);
    }

    private void addHistoryRows(String exerciseName, List<WorkoutSession> sessions) {
        Map<String, Integer> sessionNumbers = getSessionNumbers(sessions);
        ArrayList<WorkoutSession> newestFirst = sortNewestFirst(sessions);
        String normalizedName = normalizeName(exerciseName);
        for (WorkoutSession session : newestFirst) {
            addSessionRow(session, normalizedName,
                    sessionNumbers.get(session.getSessionId()));
        }
    }

    private Map<String, Integer> getSessionNumbers(List<WorkoutSession> sessions) {
        // Number sessions per date in save order and associate each number with its session ID.
        Map<String, Integer> dateCounts = new HashMap<>();
        Map<String, Integer> sessionNumbers = new HashMap<>();

        for (WorkoutSession session : sessions) {
            String date = session.getDate();
            int number = dateCounts.getOrDefault(date, 0) + 1;
            dateCounts.put(date, number);
            sessionNumbers.put(session.getSessionId(), number);
        }

        return sessionNumbers;
    }

    private ArrayList<WorkoutSession> sortNewestFirst(List<WorkoutSession> sessions) {
        // Reverse first to keep same-day sessions in reverse save order after the stable sort.
        ArrayList<WorkoutSession> newestFirst = new ArrayList<>(sessions);
        Collections.reverse(newestFirst);
        newestFirst.sort(Comparator.comparing(WorkoutSession::getDate).reversed());
        return newestFirst;
    }

    private void addSessionRow(WorkoutSession session, String exerciseName, int sessionNumber) {
        ArrayList<ExerciseSet> exerciseSets = findExerciseSets(session, exerciseName);
        if (exerciseSets.isEmpty()) {
            return;
        }
        tableModel.addRow(new Object[] {
            session.getDate(),
            sessionNumber,
            exerciseSets.size(),
            formatSets(exerciseSets)
        });
    }

    private ArrayList<ExerciseSet> findExerciseSets(WorkoutSession session, String exerciseName) {
        ArrayList<ExerciseSet> matchingSets = new ArrayList<>();
        for (ExerciseSet set : session.getExercises()) {
            if (exerciseName.equals(normalizeName(set.getExerciseName()))) {
                matchingSets.add(set);
            }
        }
        return matchingSets;
    }

    private String formatSets(List<ExerciseSet> exerciseSets) {
        StringBuilder details = new StringBuilder();
        for (ExerciseSet set : exerciseSets) {
            if (details.length() > 0) {
                details.append("; ");
            }
            details.append(String.format(Locale.US, "%.1f kg x %d", set.getWeight(), set.getReps()));
        }
        return details.toString();
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
    }
}
