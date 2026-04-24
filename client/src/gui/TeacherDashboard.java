package gui;

import Model.Quiz;
import Model.Question;
import Service.QuizService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

public class TeacherDashboard extends JFrame {
    private QuizService service;
    private String userId;
    private JTable quizTable;
    private DefaultTableModel tableModel;
    private JButton createBtn, addQuestionBtn, refreshBtn, leaderboardBtn;

    public TeacherDashboard(QuizService service, String userId) {
        this.service = service;
        this.userId = userId;

        setTitle("Teacher Dashboard - " + userId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Quizzes", createQuizPanel());
        tabs.addTab("Add Questions", createQuestionPanel());

        add(tabs);
        setVisible(true);
        refreshQuizList();
    }

    private JPanel createQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Created By"}, 0);
        quizTable = new JTable(tableModel);
        quizTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(quizTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        createBtn = new JButton("Create Quiz");
        addQuestionBtn = new JButton("Add Question");
        refreshBtn = new JButton("Refresh");
        leaderboardBtn = new JButton("View Leaderboard");

        btnPanel.add(createBtn);
        btnPanel.add(addQuestionBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(leaderboardBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> createQuiz());
        addQuestionBtn.addActionListener(e -> openAddQuestionDialog());
        refreshBtn.addActionListener(e -> refreshQuizList());
        leaderboardBtn.addActionListener(e -> viewLeaderboard());

        return panel;
    }

    private JPanel createQuestionPanel() {
        return new JPanel(); // We'll use a dialog instead; this tab just informational.
    }

    private void createQuiz() {
        String title = JOptionPane.showInputDialog(this, "Enter quiz title:");
        if (title == null || title.trim().isEmpty()) return;
        try {
            Quiz q = new Quiz();
            q.setTitle(title.trim());
            q.setCreatedBy(userId);
            int id = service.createQuiz(q);
            JOptionPane.showMessageDialog(this, "Quiz created with ID: " + id);
            refreshQuizList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void openAddQuestionDialog() {
        int selectedRow = quizTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a quiz from the table first.");
            return;
        }
        int quizId = (int) tableModel.getValueAt(selectedRow, 0);
        String quizTitle = (String) tableModel.getValueAt(selectedRow, 1);

        JTextField questionField = new JTextField(30);
        JTextField optionsField = new JTextField(30);   // comma separated
        JTextField answerField = new JTextField(30);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.add(new JLabel("Question:"));
        form.add(questionField);
        form.add(new JLabel("Options (comma separated):"));
        form.add(optionsField);
        form.add(new JLabel("Correct Answer:"));
        form.add(answerField);

        int result = JOptionPane.showConfirmDialog(this, form, "Add Question to " + quizTitle,
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Question q = new Question();
                q.setQuizId(quizId);
                q.setQuestionText(questionField.getText().trim());
                q.setOptions(Arrays.asList(optionsField.getText().split("\\s*,\\s*")));
                q.setCorrectAnswer(answerField.getText().trim());
                service.addQuestion(q);
                JOptionPane.showMessageDialog(this, "Question added.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void refreshQuizList() {
        try {
            List<Quiz> quizzes = service.getQuizzes();
            tableModel.setRowCount(0);
            for (Quiz q : quizzes) {
                tableModel.addRow(new Object[]{q.getId(), q.getTitle(), q.getCreatedBy()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error refreshing: " + ex.getMessage());
        }
    }

    private void viewLeaderboard() {
        int selectedRow = quizTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a quiz.");
            return;
        }
        int quizId = (int) tableModel.getValueAt(selectedRow, 0);
        new LeaderboardFrame(service, quizId, userId, false); // teacher, no listener needed? Actually teacher can also see live.
        // We'll make a simple LeaderboardFrame that can register listener.
        // LeaderboardFrame will handle listener registration.
    }
}