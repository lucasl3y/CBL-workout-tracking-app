package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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

        // Changed to 6 rows to accommodate the new category dropdown
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        // 1. Category Dropdown
        formPanel.add(new JLabel("Category:"));
        String[] categories = catalog.getCategories();
        categoryCombo = new JComboBox<>(categories);
        formPanel.add(categoryCombo);

        // 2. Exercise Dropdown (Dependent on Category)
        formPanel.add(new JLabel("Exercise:"));
        String initialCategory = categories[0];
        exerciseCombo = new JComboBox<>(catalog.getExercisesByCategory(initialCategory));
        formPanel.add(exerciseCombo);

        // 3. Target Muscles
        formPanel.add(new JLabel("Target Muscles:"));
        String initialExercise = (String) exerciseCombo.getSelectedItem();
        muscleLabel = new JLabel(catalog.getTargetMuscles(initialExercise));
        muscleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 12));
        formPanel.add(muscleLabel);

        // 4. Weight
        formPanel.add(new JLabel("Weight (kg):"));
        weightField = new JTextField(10);
        formPanel.add(weightField);

        // 5. Sets
        formPanel.add(new JLabel("Sets:"));
        setsField = new JTextField(10);
        formPanel.add(setsField);

        // 6. Reps
        formPanel.add(new JLabel("Reps per set:"));
        repsField = new JTextField(10);
        formPanel.add(repsField);

        JButton addButton = new JButton("Add to Session");
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        buttonPanel.add(addButton);

        JPanel containerPanel = new JPanel(new BorderLayout(0, 20)); 
        containerPanel.add(formPanel, BorderLayout.CENTER);
        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(containerPanel);

        // --- Event Listeners ---

        // When the category changes, update the exercise dropdown
        categoryCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryCombo.getSelectedItem();
                String[] newExercises = catalog.getExercisesByCategory(selectedCategory);
                
                // Replace the items in the second dropdown dynamically
                exerciseCombo.setModel(new DefaultComboBoxModel<>(newExercises));
                
                // Auto-update the muscle label for the first exercise in the new category
                if (newExercises.length > 0) {
                    String firstNewExercise = newExercises[0];
                    muscleLabel.setText(catalog.getTargetMuscles(firstNewExercise));
                }
            }
        });

        // When the exercise changes, update the muscle label
        exerciseCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedExercise = (String) exerciseCombo.getSelectedItem();
                if (selectedExercise != null) {
                    muscleLabel.setText(catalog.getTargetMuscles(selectedExercise));
                }
            }
        });

        // Button click
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