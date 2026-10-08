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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.ExerciseCatalog;
import model.ExerciseSet;
import model.TemplateManager;
import model.WorkoutSession;
import storage.FileStorage;

public class ExerciseEntryPanel extends JPanel {

    private JComboBox<String> categoryCombo;
    private JComboBox<String> exerciseCombo;
    private JLabel muscleLabel;
    private JTextField weightField;
    // Removed setsField
    private JTextField repsField;
    private JTextField dateField; 
    
    private JTable sessionTable;
    private DefaultTableModel tableModel;
    
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

        // --- LEFT SIDE: The Form ---
        // Reduced grid rows because we removed the "Sets" input field
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));

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

        // --- RIGHT SIDE: The Interactive Table ---
        String[] columnNames = {"Exercise", "Set", "Weight (kg)", "Reps"};
        tableModel = new DefaultTableModel(columnNames, 0);
        sessionTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(sessionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Workout Session"));
        
        // NEW: Delete button for the table
        JButton deleteButton = new JButton("Delete Selected Row");
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(deleteButton, BorderLayout.SOUTH);
        
        add(tableContainer, BorderLayout.CENTER);

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

        loadTemplateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) templateCombo.getSelectedItem();
                if (selected == null || selected.equals("Select Template...")) {
                    return; 
                }
                
                ArrayList<ExerciseSet> templateSets = templateManager.getTemplate(selected);
                
                for (ExerciseSet set : templateSets) {
                    tableModel.addRow(new Object[]{
                        set.getExerciseName(), 
                        set.getSetNumber(), 
                        set.getWeight(), 
                        set.getReps()
                    });
                }
                
                templateCombo.setSelectedIndex(0);
            }
        });

        customButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(ExerciseEntryPanel.this, 
                        "Enter custom exercise name:");
                if (name == null || name.trim().isEmpty()) return; 

                String category = (String) JOptionPane.showInputDialog(ExerciseEntryPanel.this, 
                        "Select Category for " + name + ":", "Category", 
                        JOptionPane.QUESTION_MESSAGE, null, 
                        catalog.getCategories(), catalog.getCategories()[0]);
                if (category == null) return;

                String muscles = JOptionPane.showInputDialog(ExerciseEntryPanel.this, 
                        "Enter target muscles for " + name + ":");
                if (muscles == null || muscles.trim().isEmpty()) return;

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
                    int reps = Integer.parseInt(repsField.getText());

                    // Calculate which set number this is by counting rows with the same exercise name
                    int setNumber = 1;
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (tableModel.getValueAt(i, 0).equals(name)) {
                            setNumber++;
                        }
                    }

                    // Add as a single set row
                    tableModel.addRow(new Object[]{name, setNumber, weight, reps});

                    // Only clear reps and keep weight so they can easily click 'Add' again for the next set
                    repsField.setText("");
                    repsField.requestFocus(); 

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "Please enter valid numbers for weight and reps.", 
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // NEW: Action for the Delete Button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = sessionTable.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "Please select a row in the table first.", 
                            "No Row Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sessionTable.isEditing()) {
                    sessionTable.getCellEditor().stopCellEditing();
                }
                
                String date = dateField.getText();
                
                if (date.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "Please enter a date first.", 
                            "Missing Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "Your session is empty! Add exercises first.", 
                            "Empty Session", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                WorkoutSession session = new WorkoutSession(date);
                
                try {
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        String name = tableModel.getValueAt(i, 0).toString();
                        int setNumber = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                        double weight = Double.parseDouble(tableModel.getValueAt(i, 2).toString());
                        int reps = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                        
                        session.addExercise(new ExerciseSet(name, setNumber, weight, reps));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "A table cell contains invalid text. Please fix it and try again.", 
                            "Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                fileStorage.saveSession(session);
                
                JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                        "Workout saved successfully to file!", 
                        "Workout Saved", JOptionPane.INFORMATION_MESSAGE);
                
                tableModel.setRowCount(0);
                dateField.setText("");
            }
        });
    }
}