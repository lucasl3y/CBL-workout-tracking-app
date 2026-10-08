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
    private JTextField dateField; 
    
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
        setLayout(new java.awt.GridBagLayout());

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
                String name = JOptionPane.showInputDialog(ExerciseEntryPanel.this, 
                        "Enter custom exercise name:");
                
                // Add curly brackets to comply with Checkstyle
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

                JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                        name + " added successfully!", "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
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
                    
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            name + " added to the current session list!", 
                            "Exercise Added", JOptionPane.INFORMATION_MESSAGE);

                    weightField.setText("");
                    setsField.setText("");
                    repsField.setText("");

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
                dateField.setText("");
            }
        });
    }
}