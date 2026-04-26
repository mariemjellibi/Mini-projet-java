
package UI;

import Service.QuizService;
import UI.Panel.LeaderboardPanel;
import UI.Panel.QuestionsPanel;
import UI.Panel.QuizListPanel;
import UI.Support.AppStyle;
import UI.Support.LeaderboardSubscription;
import UI.Support.QuizCatalogSubscription;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StudentUI extends JFrame {

    public StudentUI(QuizService service, String userId) {
        super("Student — " + userId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppStyle.APP_BACKGROUND);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        LeaderboardPanel        leaderboard  = new LeaderboardPanel();
        LeaderboardSubscription subscription = new LeaderboardSubscription(service);
        QuizCatalogSubscription quizCatalogSubscription = new QuizCatalogSubscription(service);
        QuestionsPanel          questions    = new QuestionsPanel(
                service, userId, score -> { /* score already shown by panel */ });

        QuizListPanel quizList = new QuizListPanel(
            service, "Available Quizzes",
                quiz -> {
                    questions.loadQuiz(quiz.getId());
                    try { subscription.watch(quiz.getId(), leaderboard::render); }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                                "Leaderboard subscribe failed:\n" + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

        add(quizList,    BorderLayout.WEST);
        add(questions,   BorderLayout.CENTER);
        add(leaderboard, BorderLayout.EAST);

        try {
            quizCatalogSubscription.watch(quizzes -> {
                quizList.render(quizzes);
                questions.refreshCurrentQuiz();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Auto quiz updates unavailable:\n" + ex.getMessage(),
                    "Warning", JOptionPane.WARNING_MESSAGE);
            quizList.refresh();
        }

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                subscription.cancel();
                quizCatalogSubscription.cancel();
            }
        });
    }

    public static void main(String[] args) {
        try {
            String userId = JOptionPane.showInputDialog(null, "Your student ID:", "student1");
            if (userId == null || userId.trim().isEmpty()) return;

            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            QuizService service = (QuizService) registry.lookup("QuizService");

            SwingUtilities.invokeLater(() -> new StudentUI(service, userId.trim()).setVisible(true));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Could not connect to server:\n" + e.getMessage(),
                    "Connection error", JOptionPane.ERROR_MESSAGE);
        }
    }
}