package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ExerciseEntryPanel extends JPanel {

    // Declare variables here so they can be accessed inside the button listener
    private JComboBox<String> exerciseCombo;
    private JTextField weightField;
    private JTextField setsField;
    private JTextField repsField;

    public ExerciseEntryPanel() {
        initUI();
    }

private void initUI() {
        // Use GridBagLayout for the main panel. 
        // By default, it perfectly centers its contents both vertically and horizontally!
        setLayout(new java.awt.GridBagLayout());

        // --- 1. Form Area ---
        // GridLayout keeps the labels and text fields neatly aligned
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        formPanel.add(new JLabel("Exercise:"));
        String[] exercises = {"Bench Press", "Squat", "Deadlift", "Pull-ups"}; 
        exerciseCombo = new JComboBox<>(exercises);
        formPanel.add(exerciseCombo);

        formPanel.add(new JLabel("Weight (kg):"));
        weightField = new JTextField(10);
        formPanel.add(weightField);

        formPanel.add(new JLabel("Sets:"));
        setsField = new JTextField(10);
        formPanel.add(setsField);

        formPanel.add(new JLabel("Reps per set:"));
        repsField = new JTextField(10);
        formPanel.add(repsField);

        // --- 2. Button Area ---
        JButton addButton = new JButton("Add to Session");
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        buttonPanel.add(addButton);

        // --- 3. Combine Form and Button ---
        // Put the form and the button together in one container
        // BorderLayout(0, 20) adds a 20-pixel vertical gap between the form and the button
        JPanel containerPanel = new JPanel(new BorderLayout(0, 20)); 
        containerPanel.add(formPanel, BorderLayout.CENTER);
        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- 4. Add the combined container to the main panel ---
        // Because the main layout is GridBagLayout, it centers 'containerPanel' perfectly
        add(containerPanel);

        // --- Event Handling ---
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedExercise = (String) exerciseCombo.getSelectedItem();
                System.out.println("Selected: " + selectedExercise);
                System.out.println("Weight: " + weightField.getText());
                System.out.println("Sets: " + setsField.getText());
                System.out.println("Reps: " + repsField.getText());
            }
        });
    }
}