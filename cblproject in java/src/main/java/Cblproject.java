import gui.ExerciseEntryPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Cblproject {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Create the main application window
                JFrame frame = new JFrame("Workout App - Test Window");
                frame.setSize(400, 300);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                
                // 2. Instantiate YOUR modular panel
                ExerciseEntryPanel entryPanel = new ExerciseEntryPanel();
                
                // 3. Add your panel to the window
                frame.add(entryPanel);
                
                frame.setVisible(true);
            }
        });
    }
}