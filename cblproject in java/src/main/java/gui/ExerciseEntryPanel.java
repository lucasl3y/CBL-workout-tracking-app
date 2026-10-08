package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import model.ExerciseCatalog;

public class ExerciseEntryPanel extends JPanel {

    private JComboBox<String> categoryCombo;
    private JComboBox<String> exerciseCombo;
    private JLabel muscleLabel;
    private JTextField weightField;
    private JTextField setsField;
    private JTextField repsField;
    
    private ExerciseCatalog catalog;

    public ExerciseEntryPanel(ExerciseCatalog catalog) {
        this.catalog = catalog;
        initUI();
    }

    private void initUI() {
        setLayout(new java.awt.GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));

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

        // NEW: Added two buttons next to each other using FlowLayout
        JButton customButton = new JButton("Create Custom Exercise");
        JButton addButton = new JButton("Add to Session");
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 0));
        buttonPanel.add(customButton);
        buttonPanel.add(addButton);

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

        // NEW: Action listener for creating a custom exercise
        customButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ask for the exercise name
                String name = JOptionPane.showInputDialog(ExerciseEntryPanel.this, "Enter custom exercise name:");
                if (name == null || name.trim().isEmpty()) return; // Cancel if empty

                // Ask for the category via a dropdown popup
                String category = (String) JOptionPane.showInputDialog(ExerciseEntryPanel.this, 
                        "Select Category for " + name + ":", "Category", 
                        JOptionPane.QUESTION_MESSAGE, null, 
                        catalog.getCategories(), catalog.getCategories()[0]);
                if (category == null) return;

                // Ask for the target muscles
                String muscles = JOptionPane.showInputDialog(ExerciseEntryPanel.this, "Enter target muscles for " + name + ":");
                if (muscles == null || muscles.trim().isEmpty()) return;

                // 1. Add to the logic (Catalog)
                catalog.addCustomExercise(category, name, muscles);

                // 2. Update the GUI directly so the new exercise is visible
                String currentCategory = (String) categoryCombo.getSelectedItem();
                if (currentCategory.equals(category)) {
                    exerciseCombo.setModel(new DefaultComboBoxModel<>(catalog.getExercisesByCategory(category)));
                    exerciseCombo.setSelectedItem(name);
                }

                JOptionPane.showMessageDialog(ExerciseEntryPanel.this, name + " added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Selected: " + exerciseCombo.getSelectedItem());
                System.out.println("Muscles: " + muscleLabel.getText());
                System.out.println("Weight: " + weightField.getText());
                System.out.println("Sets: " + setsField.getText());
                System.out.println("Reps: " + repsField.getText());
            }
        });
    }
}