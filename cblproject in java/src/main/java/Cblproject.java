import gui.ExerciseEntryPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import model.ExerciseCatalog; // Import the catalog

public class Cblproject {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Create the data catalog first
                ExerciseCatalog catalog = new ExerciseCatalog();
                
                // 2. Create the main application window
                JFrame frame = new JFrame("Workout App - Test Window");
                frame.setSize(450, 350);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                
                // 3. Instantiate your modular panel, PASSING the catalog into it
                ExerciseEntryPanel entryPanel = new ExerciseEntryPanel(catalog);
                
                // 4. Add panel to the window
                frame.add(entryPanel);
                
                frame.setVisible(true);
            }
        });
    }
}