import gui.ExerciseEntryPanel;
import gui.HistoryPanel;
import gui.ProgressPanel;
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
                ExerciseCatalog catalog = new ExerciseCatalog();
                FileStorage storage = new FileStorage();

                JFrame frame = new JFrame("Workout Tracker App");
                frame.setSize(850, 500); 
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null); 

                JTabbedPane tabbedPane = new JTabbedPane();

                ExerciseEntryPanel entryPanel = new ExerciseEntryPanel(catalog);
                HistoryPanel historyPanel = new HistoryPanel(storage);
                ProgressPanel progressPanel = new ProgressPanel(storage);

                tabbedPane.addTab("Log Workout", entryPanel);
                tabbedPane.addTab("Attendance History", historyPanel);
                tabbedPane.addTab("Progress", progressPanel);
                // Refresh data-backed tabs whenever the user returns to them.
                tabbedPane.addChangeListener(e -> {
                    if (tabbedPane.getSelectedComponent() == historyPanel) {
                        historyPanel.refresh();
                    } else if (tabbedPane.getSelectedComponent() == progressPanel) {
                        progressPanel.refresh();
                    }
                });

                frame.add(tabbedPane);
                frame.setVisible(true);
            }
        });
    }
}
