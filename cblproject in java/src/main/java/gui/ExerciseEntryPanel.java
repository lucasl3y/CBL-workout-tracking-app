package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
    private JTextField dateField; 
    private JTextArea summaryArea; // NEW: The text area to show the live session
    
    private ExerciseCatalog catalog;
    private FileStorage fileStorage; 
    private ArrayList<ExerciseSet> pendingExercises; 

    public ExerciseEntryPanel(ExerciseCatalog catalog) {
        this.catalog = catalog;
        this.fileStorage = new FileStorage();
        this.pendingExercises = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        // Changed layout to BorderLayout with 20px padding between components
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- LEFT SIDE: The Form ---
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));

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

        JButton customButton = new JButton("Custom Exercise");
        JButton addButton = new JButton("Add to Session");
        JButton saveButton = new JButton("Save Workout"); 

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));
        buttonPanel.add(customButton);
        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);

        JPanel leftContainer = new JPanel(new BorderLayout(0, 20)); 
        leftContainer.add(formPanel, BorderLayout.CENTER);
        leftContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Add the entire form to the left side of the screen
        add(leftContainer, BorderLayout.WEST);

        // --- RIGHT SIDE: The Summary Area ---
        summaryArea = new JTextArea(15, 35);
        summaryArea.setEditable(false); // Users cannot type in this box
        
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Workout Session"));
        
        // Add the scrolling text area to the center/right of the screen
        add(scrollPane, BorderLayout.CENTER);

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

                String muscles = JOptionPane.showInputDialog(ExerciseEntryPanel.this, 
                        "Enter target muscles for " + name + ":");
                        
                if (muscles == null || muscles.trim().isEmpty()) {
                    return;
                }

                catalog.addCustomExercise(category, name, muscles);

                String currentCategory = (String) categoryCombo.getSelectedItem();
                if (currentCategory.equals(category)) {
                    exerciseCombo.setModel(new DefaultComboBoxModel<>(
                            catalog.getExercisesByCategory(category)));
                    exerciseCombo.setSelectedItem(name);
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = (String) exerciseCombo.getSelectedItem();
                    double weight = Double.parseDouble(weightField.getText());
                    int sets = Integer.parseInt(setsField.getText());
                    int reps = Integer.parseInt(repsField.getText());

                    ExerciseSet newSet = new ExerciseSet(name, weight, sets, reps);
                    pendingExercises.add(newSet);
                    
                    // UPDATE THE SCREEN
                    updateSummaryArea();

                    weightField.setText("");
                    setsField.setText("");
                    repsField.setText("");
                    weightField.requestFocus(); // Put cursor back for fast typing

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "Please enter valid numbers for weight, sets and reps.", 
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText();
                
                if (date.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "Please enter a date first.", 
                            "Missing Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (pendingExercises.isEmpty()) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "Your session is empty! Add exercises first.", 
                            "Empty Session", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                WorkoutSession session = new WorkoutSession(date);
                for (ExerciseSet set : pendingExercises) {
                    session.addExercise(set);
                }

                fileStorage.saveSession(session);
                
                JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                        "Workout saved successfully to file!", 
                        "Workout Saved", JOptionPane.INFORMATION_MESSAGE);
                
                pendingExercises.clear();
                updateSummaryArea(); // Clear the screen
                dateField.setText("");
            }
        });
    }

    // NEW METHOD: Refreshes the text area based on the current list of exercises
    private void updateSummaryArea() {
        StringBuilder sb = new StringBuilder();
        
        if (pendingExercises.isEmpty()) {
            summaryArea.setText("No exercises added yet.\nStart building your session on the left!");
            return;
        }

        for (int i = 0; i < pendingExercises.size(); i++) {
            ExerciseSet set = pendingExercises.get(i);
            // Example: "1. Bench Press | Weight: 60.0kg | Sets: 3 | Reps: 10"
            sb.append(i + 1).append(". ").append(set.toString()).append("\n");
        }
        
        summaryArea.setText(sb.toString());
    }
}