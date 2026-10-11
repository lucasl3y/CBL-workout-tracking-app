package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import storage.FileStorage;

public class HistoryPanel extends JPanel {

    private FileStorage fileStorage;
    private YearMonth currentMonth;
    private HashMap<String, String> workoutHistory;
    
    private JPanel calendarGrid;
    private JLabel monthLabel;

    public HistoryPanel(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        this.currentMonth = YearMonth.now();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton prevButton = new JButton("<< Previous");
        JButton nextButton = new JButton("Next >>");
        JButton refreshButton = new JButton("Refresh Data");
        
        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(new Font("Dialog", Font.BOLD, 18));

        headerPanel.add(prevButton);
        headerPanel.add(monthLabel);
        headerPanel.add(nextButton);
        headerPanel.add(refreshButton);
        add(headerPanel, BorderLayout.NORTH);

        calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        add(calendarGrid, BorderLayout.CENTER);

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentMonth = currentMonth.minusMonths(1);
                updateCalendar();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentMonth = currentMonth.plusMonths(1);
                updateCalendar();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCalendar(); 
            }
        });

        updateCalendar();
    }

    private void updateCalendar() {
        calendarGrid.removeAll();
        workoutHistory = fileStorage.loadHistory(); 
        
        String monthName = currentMonth.getMonth().name();
        monthLabel.setText(monthName + " " + currentMonth.getYear());

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            JLabel label = new JLabel(day, JLabel.CENTER);
            label.setFont(new Font("Dialog", Font.BOLD, 14));
            calendarGrid.add(label);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); 
        
        for (int i = 1; i < dayOfWeek; i++) {
            calendarGrid.add(new JLabel("")); 
        }

        int daysInMonth = currentMonth.lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            String dateString = String.format("%04d-%02d-%02d", 
                    currentMonth.getYear(), currentMonth.getMonthValue(), i);
            
            JButton dayButton = new JButton(String.valueOf(i));
            
            if (workoutHistory.containsKey(dateString)) {
                dayButton.setBackground(new Color(144, 238, 144)); 
                dayButton.setOpaque(true);
                
                dayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String details = workoutHistory.get(dateString);
                        
                        // NEW: Custom options for the popup dialog
                        Object[] options = {"Close", "Delete Workout"};
                        
                        // Show dialog with a delete button instead of a standard OK button
                        int choice = JOptionPane.showOptionDialog(HistoryPanel.this,
                                details,
                                "Workout on " + dateString,
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                options,
                                options[0]);
                                
                        // If the user clicked "Delete Workout"
                        if (choice == 1) {
                            int confirm = JOptionPane.showConfirmDialog(HistoryPanel.this,
                                    "Are you sure you want to delete the workout on " + dateString + "?\nThis action cannot be undone.",
                                    "Confirm Deletion",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE);
                                    
                            if (confirm == JOptionPane.YES_OPTION) {
                                fileStorage.deleteSession(dateString);
                                updateCalendar(); // Refresh the grid to remove the green color
                                JOptionPane.showMessageDialog(HistoryPanel.this, 
                                        "Workout successfully deleted.", 
                                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                });
            } else {
                dayButton.setEnabled(false); 
            }
            
            calendarGrid.add(dayButton);
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }
}