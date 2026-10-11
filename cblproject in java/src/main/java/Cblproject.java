import gui.ExerciseEntryPanel;
import gui.HistoryPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import model.ExerciseCatalog;
import storage.FileStorage;

public class Cblproject {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Initialize core logic components
                ExerciseCatalog catalog = new ExerciseCatalog();
                FileStorage storage = new FileStorage();

                // 2. Create the main application window
                JFrame frame = new JFrame("Workout Tracker App");
                frame.setSize(850, 500); // Slightly larger for tabs
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null); // Center on screen

                // 3. Create a Tabbed Pane to hold different screens
                JTabbedPane tabbedPane = new JTabbedPane();

                // 4. Create the two main panels
                ExerciseEntryPanel entryPanel = new ExerciseEntryPanel(catalog);
                HistoryPanel historyPanel = new HistoryPanel(storage);

                // 5. Add panels to the tabs
                tabbedPane.addTab("Log Workout", entryPanel);
                tabbedPane.addTab("Attendance History", historyPanel);

                // 6. Add tabs to frame and show
                frame.add(tabbedPane);
                frame.setVisible(true);
            }
        });
    }
}