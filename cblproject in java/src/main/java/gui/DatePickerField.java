package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Provides a read-only date field with a calendar dialog.
 * Selected dates are returned in ISO format (yyyy-mm-dd).
 */
public class DatePickerField extends JPanel {

    private LocalDate selectedDate = LocalDate.now();
    private final JTextField dateField = new JTextField(selectedDate.toString(), 10);

    public DatePickerField() {
        super(new BorderLayout(4, 0));
        dateField.setEditable(false);
        dateField.setFocusable(false);
        add(dateField, BorderLayout.CENTER);

        JButton calendarButton = new JButton("...");
        calendarButton.setToolTipText("Choose date");
        calendarButton.addActionListener(e -> showCalendar());
        add(calendarButton, BorderLayout.EAST);
    }

    public String getText() {
        return selectedDate.toString();
    }

    private void showCalendar() {
        // Block interaction with the workout form while the calendar is open.
        // Closing the dialog without selecting a date keeps the previous value.
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Select Date",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(new CalendarPanel(dialog));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Keep calendar navigation and rendering inside the date picker component.
    private class CalendarPanel extends JPanel {

        private final JDialog dialog;
        private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        private final JPanel daysPanel = new JPanel(new GridLayout(0, 7, 3, 3));
        private YearMonth displayedMonth = YearMonth.from(selectedDate);

        CalendarPanel(JDialog dialog) {
            super(new BorderLayout(6, 6));
            this.dialog = dialog;
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            JPanel header = new JPanel(new BorderLayout());
            JButton previous = new JButton("<");
            previous.setToolTipText("Previous month");
            JButton next = new JButton(">");
            next.setToolTipText("Next month");
            header.add(previous, BorderLayout.WEST);
            header.add(monthLabel, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);
            add(daysPanel, BorderLayout.CENTER);

            previous.addActionListener(e -> {
                displayedMonth = displayedMonth.minusMonths(1);
                renderMonth();
            });
            next.addActionListener(e -> {
                displayedMonth = displayedMonth.plusMonths(1);
                renderMonth();
            });
            renderMonth();
        }

        private void renderMonth() {
            // Rebuild the grid whenever the user changes month.
            String monthName = displayedMonth.getMonth().getDisplayName(
                    TextStyle.FULL, Locale.ENGLISH);
            monthLabel.setText(monthName + " " + displayedMonth.getYear());
            
            daysPanel.removeAll();
            String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String weekday : weekdays) {
                daysPanel.add(new JLabel(weekday, SwingConstants.CENTER));
            }

            // Insert empty cells to align the first day with the Monday-first weekday header.
            int firstDayOfWeek = displayedMonth.atDay(1).getDayOfWeek().getValue();
            for (int blank = 1; blank < firstDayOfWeek; blank++) {
                daysPanel.add(new JLabel(""));
            }
            
            for (int day = 1; day <= displayedMonth.lengthOfMonth(); day++) {
                LocalDate date = displayedMonth.atDay(day);
                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
                
                if (date.equals(selectedDate)) {
                    dayButton.setBackground(new java.awt.Color(190, 220, 245));
                    dayButton.setOpaque(true);
                }

                // Apply the selected date to the field and close the calendar.
                dayButton.addActionListener(e -> {
                    selectedDate = date;
                    dateField.setText(date.toString());
                    dialog.dispose();
                });
                daysPanel.add(dayButton);
            }
            daysPanel.revalidate();
            daysPanel.repaint();
        }
    }
}