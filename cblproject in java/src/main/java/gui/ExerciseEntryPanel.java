package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class ExerciseEntryPanel extends JPanel {

    private JComboBox<String> categoryCombo;
    private JComboBox<String> exerciseCombo;
    private JLabel muscleLabel;
    private JTextField weightField;
    private JTextField repsField;
    private JTextField dateField; 
    private JTextField durationField; 
    
    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private JLabel volumeLabel; 
    
    private ExerciseCatalog catalog;
    private FileStorage fileStorage; 
    private TemplateManager templateManager; 
    
    private boolean isTableUpdating = false;

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

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(10);
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
        tableModel = new DefaultTableModel(columnNames, 0);
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

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (isTableUpdating) return;

                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();

                    if (row >= 0 && (col == 2 || col == 3)) {
                        isTableUpdating = true; 
                        try {
                            String exerciseName = (String) tableModel.getValueAt(row, 0);
                            Object newValue = tableModel.getValueAt(row, col);

                            for (int i = row + 1; i < tableModel.getRowCount(); i++) {
                                String nextExercise = (String) tableModel.getValueAt(i, 0);
                                if (exerciseName.equals(nextExercise)) {
                                    tableModel.setValueAt(newValue, i, col);
                                } else {
                                    break; 
                                }
                            }
                        } finally {
                            isTableUpdating = false; 
                        }
                    }
                }
                
                if (!isTableUpdating) {
                    updateVolumeCounter();
                }
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
                    int reps = Integer.parseInt(repsField.getText());

                    int setNumber = 1;
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (tableModel.getValueAt(i, 0).equals(name)) {
                            setNumber++;
                        }
                    }

                    tableModel.addRow(new Object[]{name, setNumber, weight, reps});
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "Please enter valid numbers for weight and reps.", 
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

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
                String durationText = durationField.getText(); 
                
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

                int duration = 0;
                try {
                    duration = Integer.parseInt(durationText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ExerciseEntryPanel.this, 
                            "Please enter a valid whole number for duration (e.g. 60).", 
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                HashSet<String> uniqueMuscles = new HashSet<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String exerciseName = tableModel.getValueAt(i, 0).toString();
                    String muscles = catalog.getTargetMuscles(exerciseName);
                    if (muscles != null && !muscles.isEmpty()) {
                        String[] muscleArray = muscles.split(",\\s*");
                        for (String m : muscleArray) {
                            uniqueMuscles.add(m);
                        }
                    }
                }
                String trainedMuscles = String.join(", ", uniqueMuscles);

                WorkoutSession session = new WorkoutSession(date, duration, trainedMuscles); 
                
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
                durationField.setText(""); 
            }
        });
    }

    private void updateVolumeCounter() {
        double totalVolume = 0.0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                double weight = Double.parseDouble(tableModel.getValueAt(i, 2).toString());
                int reps = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                totalVolume += (weight * reps);
            } catch (NumberFormatException ex) {
            }
        }
        
        volumeLabel.setText(String.format(Locale.US, "Total Volume: %.1f kg", totalVolume));
    }
}