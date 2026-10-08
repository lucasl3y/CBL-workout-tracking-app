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

        // Top section: Month navigation
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

        // Center section: Calendar Grid
        calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        add(calendarGrid, BorderLayout.CENTER);

        // Event Listeners for navigation
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
                updateCalendar(); // Reloads the file data
            }
        });

        // Initial draw
        updateCalendar();
    }

    // Re-draws the calendar based on the selected month and updated file data
    private void updateCalendar() {
        calendarGrid.removeAll();
        workoutHistory = fileStorage.loadHistory(); 
        
        String monthName = currentMonth.getMonth().name();
        monthLabel.setText(monthName + " " + currentMonth.getYear());

        // Add Day Headers
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            JLabel label = new JLabel(day, JLabel.CENTER);
            label.setFont(new Font("Dialog", Font.BOLD, 14));
            calendarGrid.add(label);
        }

        // Calculate empty slots before the 1st of the month
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // Monday = 1, Sunday = 7
        
        for (int i = 1; i < dayOfWeek; i++) {
            calendarGrid.add(new JLabel("")); // Empty filler
        }

        // Add the actual days as buttons
        int daysInMonth = currentMonth.lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            String dateString = String.format("%04d-%02d-%02d", 
                    currentMonth.getYear(), currentMonth.getMonthValue(), i);
            
            JButton dayButton = new JButton(String.valueOf(i));
            
            // Highlight days that have a workout logged
            if (workoutHistory.containsKey(dateString)) {
                dayButton.setBackground(new Color(144, 238, 144)); // Light Green
                dayButton.setOpaque(true);
                
                dayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String details = workoutHistory.get(dateString);
                        JOptionPane.showMessageDialog(HistoryPanel.this, 
                                details, "Workout on " + dateString, 
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            } else {
                dayButton.setEnabled(false); // No workout to click on
            }
            
            calendarGrid.add(dayButton);
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }
}