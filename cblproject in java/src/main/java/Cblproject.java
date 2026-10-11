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
                ExerciseCatalog catalog = new ExerciseCatalog();
                FileStorage storage = new FileStorage();

                JFrame frame = new JFrame("Workout Tracker App");
                frame.setSize(850, 500); 
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null); 

                JTabbedPane tabbedPane = new JTabbedPane();

                ExerciseEntryPanel entryPanel = new ExerciseEntryPanel(catalog);
                HistoryPanel historyPanel = new HistoryPanel(storage);

                tabbedPane.addTab("Log Workout", entryPanel);
                tabbedPane.addTab("Attendance History", historyPanel);

                frame.add(tabbedPane);
                frame.setVisible(true);
            }
        });
    }
}