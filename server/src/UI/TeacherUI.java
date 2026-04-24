
package UI;

import Model.Quiz;
import Service.QuizService;
import UI.Panel.AddQuestionPanel;
import UI.Panel.CreateQuizPanel;
import UI.Panel.LeaderboardPanel;
import UI.Panel.QuizListPanel;
import UI.Support.LeaderboardSubscription;

import javax.swing.*;
import java.awt.*;

public class TeacherUI extends JFrame {

    public TeacherUI(QuizService service) {
        super("Teacher Console — Quiz Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        LeaderboardPanel        leaderboard  = new LeaderboardPanel();
        LeaderboardSubscription subscription = new LeaderboardSubscription(service);

        QuizListPanel quizList = new QuizListPanel(
                service, "Quizzes", "Watch Leaderboard",
                quiz -> watchLeaderboard(service, quiz, leaderboard, subscription));

        CreateQuizPanel  createPanel  = new CreateQuizPanel(service, id -> quizList.refresh());
        AddQuestionPanel addQuestion  = new AddQuestionPanel(service, quizList::getSelected);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createTitledBorder("Create Quiz / Add Question"));
        center.add(createPanel);
        center.add(Box.createVerticalStrut(8));
        center.add(addQuestion);
        center.add(Box.createVerticalGlue());

        add(quizList,    BorderLayout.WEST);
        add(center,      BorderLayout.CENTER);
        add(leaderboard, BorderLayout.EAST);

        // Cleanly drop the listener when the window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                subscription.cancel();
            }
        });
    }

    private void watchLeaderboard(QuizService service, Quiz quiz,
                                  LeaderboardPanel panel, LeaderboardSubscription sub) {
        try {
            sub.watch(quiz.getId(), panel::render);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Subscribe failed:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}