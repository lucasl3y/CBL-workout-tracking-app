package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import model.ExerciseCatalog;
import model.ExerciseSet;
import model.TemplateManager;
import model.WorkoutSession;
import storage.FileStorage;

/**
 * Provides the workout entry form and editable draft session table.
 * Builds a WorkoutSession from validated rows and delegates saving to FileStorage.
 */
public class ExerciseEntryPanel extends JPanel {

    private JComboBox<String> categoryCombo;
    private JComboBox<String> exerciseCombo;
    private JLabel muscleLabel;
    private JTextField weightField;
    private JTextField repsField;
    private DatePickerField dateField;
    private JTextField durationField;

    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private JLabel volumeLabel;

    private ExerciseCatalog catalog;
    private FileStorage fileStorage;
    private TemplateManager templateManager;

    public ExerciseEntryPanel(ExerciseCatalog catalog) {
        this.catalog = catalog;
        this.fileStorage = new FileStorage();
        this.templateManager = new TemplateManager();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));

        formPanel.add(new JLabel("Date:"));
        dateField = new DatePickerField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("Duration (minutes):"));
        durationField = new JTextField(10);
        formPanel.add(durationField);

        formPanel.add(new JLabel("Category:"));
        String[] categories = catalog.getCategories();
        categoryCombo = new JComboBox<>(categories);
        formPanel.add(categoryCombo);

        formPanel.add(new JLabel("Exercise:"));
        String initialCategory = categories[0];
        exerciseCombo = new JComboBox<>(catalog.getExercisesByCategory(initialCategory));
        formPanel.add(exerciseCombo);

        formPanel.add(new JLabel("Target Muscles:"));
        String initialExercise = (String) exerciseCombo.getSelectedItem();
        muscleLabel = new JLabel(catalog.getTargetMuscles(initialExercise));
        muscleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 12));
        formPanel.add(muscleLabel);

        formPanel.add(new JLabel("Weight (kg):"));
        weightField = new JTextField(10);
        formPanel.add(weightField);

        formPanel.add(new JLabel("Reps for this set:"));
        repsField = new JTextField(10);
        formPanel.add(repsField);

        JButton customButton = new JButton("Custom Exercise");
        JButton addButton = new JButton("Add 1 Set");
        JButton saveButton = new JButton("Save Workout");

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));
        buttonPanel.add(customButton);
        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);

        JPanel templatePanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        templatePanel.setBorder(BorderFactory.createTitledBorder("Quick Load Templates"));

        JComboBox<String> templateCombo = new JComboBox<>(templateManager.getTemplateNames());
        JButton loadTemplateButton = new JButton("Load");
        templatePanel.add(templateCombo);
        templatePanel.add(loadTemplateButton);

        JPanel leftContainer = new JPanel(new BorderLayout(0, 10));
        leftContainer.add(templatePanel, BorderLayout.NORTH);
        leftContainer.add(formPanel, BorderLayout.CENTER);
        leftContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(leftContainer, BorderLayout.WEST);

        String[] columnNames = {"Exercise", "Set", "Weight (kg)", "Reps"};

        // Validate direct table edits and keep exercise names and set numbers read-only.
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3;
            }

            @Override
            public void setValueAt(Object value, int row, int column) {
                if (column != 2 && column != 3) {
                    return;
                }

                String text = value == null ? "" : value.toString().trim();

                try {
                    if (column == 2) {
                        double weight = Double.parseDouble(text);

                        if (!Double.isFinite(weight) || weight < 0) {
                            JOptionPane.showMessageDialog(
                                    ExerciseEntryPanel.this,
                                    "Weight must be 0 or greater.",
                                    "Invalid Weight",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        super.setValueAt(weight, row, column);
                    } else {
                        int reps = Integer.parseInt(text);

                        if (reps <= 0) {
                            JOptionPane.showMessageDialog(
                                    ExerciseEntryPanel.this,
                                    "Reps must be a positive whole number.",
                                    "Invalid Reps",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        super.setValueAt(reps, row, column);
                    }
                } catch (NumberFormatException ex) {
                    String message = column == 2
                            ? "Weight must be 0 or greater."
                            : "Reps must be a positive whole number.";

                    JOptionPane.showMessageDialog(
                            ExerciseEntryPanel.this,
                            message,
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        sessionTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(sessionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Workout Session"));

        JPanel bottomTablePanel = new JPanel(new BorderLayout());
        JButton deleteButton = new JButton("Delete Selected Row");

        volumeLabel = new JLabel("Total Volume: 0.0 kg");
        volumeLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));

        bottomTablePanel.add(deleteButton, BorderLayout.WEST);
        bottomTablePanel.add(volumeLabel, BorderLayout.EAST);

        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(bottomTablePanel, BorderLayout.SOUTH);

        add(tableContainer, BorderLayout.CENTER);

        // Recalculate total volume after rows are added, edited, or removed.
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                updateVolumeCounter();
            }
        });

        categoryCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryCombo.getSelectedItem();
                String[] newExercises = catalog.getExercisesByCategory(selectedCategory);
                exerciseCombo.setModel(new DefaultComboBoxModel<>(newExercises));
                if (newExercises.length > 0) {
                    muscleLabel.setText(catalog.getTargetMuscles(newExercises[0]));
                }
            }
        });

        exerciseCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedExercise = (String) exerciseCombo.getSelectedItem();
                if (selectedExercise != null) {
                    muscleLabel.setText(catalog.getTargetMuscles(selectedExercise));
                }
            }
        });

        loadTemplateButton.addActionListener(e -> loadTemplate(templateCombo));

        customButton.addActionListener(e -> addCustomExercise());

        addButton.addActionListener(e -> addSet());

        deleteButton.addActionListener(e -> deleteSelectedRow());

        saveButton.addActionListener(e -> saveWorkout());
    }


    /**
     * Appends template sets using the next available number for each exercise.
     */
    private void loadTemplate(JComboBox<String> templateCombo) {
        String selected = (String) templateCombo.getSelectedItem();
        if (selected == null || selected.equals("Select Template...")) {
            return;
        }

        ArrayList<ExerciseSet> templateSets = templateManager.getTemplate(selected);

        // Template rows are drafts; fill in valid reps for every row before saving.
        for (ExerciseSet set : templateSets) {
            tableModel.addRow(new Object[]{
                    set.getExerciseName(),
                    getNextSetNumber(set.getExerciseName()),
                    set.getWeight(),
                    set.getReps()
            });
        }

        templateCombo.setSelectedIndex(0);
    }

    /**
     * Collects custom exercise details and updates the exercise catalog.
     */
    private void addCustomExercise() {
        String name = JOptionPane.showInputDialog(ExerciseEntryPanel.this,
                "Enter custom exercise name:");

        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String category = (String) JOptionPane.showInputDialog(ExerciseEntryPanel.this,
                "Select Category for " + name + ":", "Category",
                JOptionPane.QUESTION_MESSAGE, null,
                catalog.getCategories(), catalog.getCategories()[0]);

        if (category == null) {
            return;
        }

        String muscles;

        // Keep requesting target muscles until the user provides a value or cancels.
        while (true) {
            muscles = JOptionPane.showInputDialog(this,
                    "Enter target muscles for " + name + ":");

            if (muscles == null) {
                return; // Cancel exits without creating an exercise.
            }

            if (!muscles.trim().isEmpty()) {
                break;
            }

            JOptionPane.showMessageDialog(this,
                    "Please enter the target muscles.",
                    "Missing Target Muscles",
                    JOptionPane.WARNING_MESSAGE);
        }

        name = name.trim();
        muscles = muscles.trim();
        if (!catalog.addCustomExercise(category, name, muscles)) {
            JOptionPane.showMessageDialog(this,
                    "An exercise with this name already exists. Please choose a different name.",
                    "Duplicate Exercise",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Switch to the chosen category and select the newly created exercise.
        categoryCombo.setSelectedItem(category);
        exerciseCombo.setModel(new DefaultComboBoxModel<>(
                catalog.getExercisesByCategory(category)));
        exerciseCombo.setSelectedItem(name);
        muscleLabel.setText(catalog.getTargetMuscles(name));

        JOptionPane.showMessageDialog(this,
                "Custom exercise added. Enter weight and reps, then click Add 1 Set.",
                "Exercise Added", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Validates the weight and reps, then adds one set to the draft.
     */
    private void addSet() {
        try {
            String name = (String) exerciseCombo.getSelectedItem();
            double weight = Double.parseDouble(weightField.getText());
            int reps = Integer.parseInt(repsField.getText());

            if (!Double.isFinite(weight) || weight < 0 || reps <= 0) {
                JOptionPane.showMessageDialog(
                        ExerciseEntryPanel.this,
                        "Weight must be 0 or greater.\n"
                                + "Reps must be a positive whole number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int setNumber = getNextSetNumber(name);
            tableModel.addRow(new Object[]{name, setNumber, weight, reps});

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    ExerciseEntryPanel.this,
                    "Weight must be 0 or greater.\n"
                            + "Reps must be a positive whole number.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Removes the selected set from the draft session.
     */
    private void deleteSelectedRow() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(ExerciseEntryPanel.this,
                    "Please select a row in the table first.",
                    "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Validates and saves the complete draft workout.
     * Keeps the draft unchanged if validation or saving fails.
     */
    private void saveWorkout() {
        // Commit the active cell edit before reading values from the table.
        if (sessionTable.isEditing()
                && !sessionTable.getCellEditor().stopCellEditing()) {
            return;
        }

        String date = dateField.getText();
        String durationText = durationField.getText();

        if (date.trim().isEmpty()) {
            JOptionPane.showMessageDialog(ExerciseEntryPanel.this,
                    "Please enter a date first.",
                    "Missing Date", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            // Normalize the selected value before saving and validate the date format.
            date = LocalDate.parse(date.trim()).toString();
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(ExerciseEntryPanel.this,
                    "Enter the date in YYYY-MM-DD format.",
                    "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(ExerciseEntryPanel.this,
                    "Your session is empty! Add exercises first.",
                    "Empty Session", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationText.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    ExerciseEntryPanel.this,
                    "Duration must be a whole number from 1 to 600 minutes.",
                    "Invalid Duration",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (duration < 1 || duration > 600) {
            JOptionPane.showMessageDialog(
                    ExerciseEntryPanel.this,
                    "Duration must be a whole number from 1 to 600 minutes.",
                    "Invalid Duration",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Collect each target muscle once across all exercises in the session.
        HashSet<String> uniqueMuscles = new HashSet<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String exerciseName = tableModel.getValueAt(i, 0).toString();
            String muscles = catalog.getTargetMuscles(exerciseName);
            if (muscles != null && !muscles.isEmpty()) {
                String[] muscleArray = muscles.split(",\\s*");
                for (String muscle : muscleArray) {
                    uniqueMuscles.add(muscle);
                }
            }
        }
        String trainedMuscles = String.join(", ", uniqueMuscles);

        WorkoutSession session = new WorkoutSession(date, duration, trainedMuscles);

        // Validate every row before writing the complete session to disk.
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                String name = tableModel.getValueAt(i, 0).toString();
                int setNumber = Integer.parseInt(
                        tableModel.getValueAt(i, 1).toString().trim());
                double weight = Double.parseDouble(
                        tableModel.getValueAt(i, 2).toString().trim());
                int reps = Integer.parseInt(
                        tableModel.getValueAt(i, 3).toString().trim());

                if (!Double.isFinite(weight) || weight < 0 || reps <= 0) {
                    JOptionPane.showMessageDialog(
                            ExerciseEntryPanel.this,
                            "Row " + (i + 1)
                                    + ": Weight must be 0 or greater.\n"
                                    + "Reps must be a positive whole number.\n"
                                    + "Fill in or delete unfinished template rows.",
                            "Invalid Workout Set",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                session.addExercise(
                        new ExerciseSet(name, setNumber, weight, reps));

            } catch (NumberFormatException | NullPointerException ex) {
                JOptionPane.showMessageDialog(
                        ExerciseEntryPanel.this,
                        "Row " + (i + 1)
                                + ": please enter valid weight and reps.\n"
                                + "Fill in or delete unfinished template rows.",
                        "Invalid Workout Set",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (!fileStorage.saveSession(session)) {
            JOptionPane.showMessageDialog(ExerciseEntryPanel.this,
                    "Workout could not be saved. Check file permissions and try again.",
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(ExerciseEntryPanel.this,
                "Workout saved successfully to file!",
                "Workout Saved", JOptionPane.INFORMATION_MESSAGE);

        // Clear the draft only after the session has been saved successfully.
        tableModel.setRowCount(0);
        durationField.setText("");
    }

    /**
     * Returns one more than the highest existing set number for this exercise.
     * Gaps left by deleted sets are preserved.
     */
    private int getNextSetNumber(String exerciseName) {
        int maxSetNumber = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (exerciseName.equals(tableModel.getValueAt(i, 0))) {
                int currentSetNumber = Integer.parseInt(
                        tableModel.getValueAt(i, 1).toString());
                maxSetNumber = Math.max(maxSetNumber, currentSetNumber);
            }
        }

        return maxSetNumber + 1;
    }

    /**
     * Updates the draft's total training volume as the sum of weight times reps.
     */
    private void updateVolumeCounter() {
        double totalVolume = 0.0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                double weight = Double.parseDouble(tableModel.getValueAt(i, 2).toString());
                int reps = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                totalVolume += (weight * reps);
            } catch (NumberFormatException ex) {
                // Ignore non-numeric rows in the preview; saving validates every row separately.
            }
        }

        volumeLabel.setText(String.format(Locale.US, "Total Volume: %.1f kg", totalVolume));
    }
}
