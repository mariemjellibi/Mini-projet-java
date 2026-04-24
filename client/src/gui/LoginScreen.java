package gui;

import Service.QuizService;
import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginScreen extends JFrame {
    private JTextField nameField;
    private JRadioButton teacherBtn, studentBtn;
    private QuizService service;

    public LoginScreen() {
        // Connect to server
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (QuizService) registry.lookup("QuizService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        setTitle("Online Quiz System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Username row
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameRow.add(new JLabel("Username:"));
        nameField = new JTextField(15);
        nameRow.add(nameField);
        mainPanel.add(nameRow);

        // Role selection
        JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        teacherBtn = new JRadioButton("Teacher");
        studentBtn = new JRadioButton("Student");
        ButtonGroup group = new ButtonGroup();
        group.add(teacherBtn);
        group.add(studentBtn);
        roleRow.add(teacherBtn);
        roleRow.add(studentBtn);
        mainPanel.add(roleRow);

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> login());
        mainPanel.add(loginBtn);

        add(mainPanel);
        setVisible(true);
    }

    private void login() {
        String userId = nameField.getText().trim();
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a username.");
            return;
        }
        if (!teacherBtn.isSelected() && !studentBtn.isSelected()) {
            JOptionPane.showMessageDialog(this, "Select Teacher or Student.");
            return;
        }

        if (teacherBtn.isSelected()) {
            new TeacherDashboard(service, userId);
        } else {
            new StudentQuizFrame(service, userId);
        }
        dispose(); // close login
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}