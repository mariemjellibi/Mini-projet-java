import Service.QuizService;
import UI.TeacherUI;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TeacherMain {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            QuizService service = (QuizService) registry.lookup("QuizService");

            SwingUtilities.invokeLater(() -> new TeacherUI(service).setVisible(true));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Could not connect to server:\n" + e.getMessage(),
                    "Connection error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
