package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.ExerciseCatalog;
import model.ExerciseSet;
import model.WorkoutSession;
import storage.FileStorage;

public class ExerciseEntryPanel extends JPanel {

    private JComboBox<String> categoryCombo;
    private JComboBox<String> exerciseCombo;
    private JLabel muscleLabel;
    private JTextField weightField;
    private JTextField setsField;
    private JTextField repsField;
    private JTextField dateField; // NEW: Field for the date
    
    private ExerciseCatalog catalog;
    private FileStorage fileStorage; // NEW: Reference to our saving logic
    private ArrayList<ExerciseSet> pendingExercises; // NEW: Temporary list for the current session

    public ExerciseEntryPanel(ExerciseCatalog catalog) {
        this.catalog = catalog;
        this.fileStorage = new FileStorage();
        this.pendingExercises = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setLayout(new java.awt.GridBagLayout());

        // Changed to 7 rows to make room for the Date field
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));

        // NEW: Date Row
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(10);
        formPanel.add(dateField);

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

        formPanel.add(new JLabel("Sets:"));
        setsField = new JTextField(10);
        formPanel.add(setsField);

        formPanel.add(new JLabel("Reps per set:"));
        repsField = new JTextField(10);
        formPanel.add(repsField);

        // NEW: We now have three buttons
        JButton customButton = new JButton("Custom Exercise");
        JButton addButton = new JButton("Add to Session");
        JButton saveButton = new JButton("Save Workout"); // Saves the file

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 0));
        buttonPanel.add(customButton);
        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);

        JPanel containerPanel = new JPanel(new BorderLayout(0, 20)); 
        containerPanel.add(formPanel, BorderLayout.CENTER);
        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(containerPanel);

        // --- Event Listeners ---

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

        customButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(ExerciseEntryPanel.this, "Enter custom exercise name:");
                if (name == null || name.trim().isEmpty()) return; 

                String category = (String) JOptionPane.showInputDialog(ExerciseEntryPanel.this, 
                        "Select Category for " + name + ":", "Category", 
                        JOptionPane.QUESTION_MESSAGE, null, 
                        catalog.getCategories(), catalog.getCategories()[0]);
                if (category == null) return;

                String muscles = JOptionPane.showInputDialog(ExerciseEntryPanel.this, "Enter target muscles for " + name + ":");
                if (muscles == null || muscles.trim().isEmpty()) return;

                catalog.addCustomExercise(category, name, muscles);

                String currentCategory = (String) categoryCombo.getSelectedItem();
                if (currentCategory.equals(category)) {
                    exerciseCombo.setModel(new DefaultComboBoxModel<>(catalog.getExercisesByCategory(category)));
                    exerciseCombo.setSelectedItem(name);
                }

                JOptionPane.showMessageDialog(ExerciseEntryPanel.this, name + " added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // REWRITTEN: Now creates an object instead of just printing to console
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = (String) exerciseCombo.getSelectedItem();
                    double weight = Double.parseDouble(weightField.getText());
                    int sets = Integer.parseInt(setsField.getText());
                    int reps = Integer.parseInt(repsField.getText());

                    // Create the backend object and store it temporarily
                    ExerciseSet newSet = new ExerciseSet(name, weight, sets, reps);
                    pendingExercises.add(newSet);
                    
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                        name + " added to the current session list!", "Exercise Added", JOptionPane.INFORMATION_MESSAGE);

                    // Clear the fields for the next exercise
                    weightField.setText("");
                    setsField.setText("");
                    repsField.setText("");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                        "Please enter valid numbers for weight, sets and reps.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // NEW: Saves the list of objects to the text file
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText();
                
                // Basic validation
                if (date.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, "Please enter a date first.", "Missing Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (pendingExercises.isEmpty()) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, "Your session is empty! Add exercises first.", "Empty Session", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Compile the session
                WorkoutSession session = new WorkoutSession(date);
                for (ExerciseSet set : pendingExercises) {
                    session.addExercise(set);
                }

                // Send to storage
                fileStorage.saveSession(session);
                
                JOptionPane.showMessageDialog(ExerciseEntryPanel.this, "Workout saved successfully to file!", "Workout Saved", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear everything for a fresh start
                pendingExercises.clear();
                dateField.setText("");
            }
        });
    }
}