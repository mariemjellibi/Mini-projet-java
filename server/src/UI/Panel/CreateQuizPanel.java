package UI.Panel;

import Model.Quiz;
import Service.QuizService;
import UI.Support.AsyncTask;

import javax.swing.*;
import java.awt.*;

public class CreateQuizPanel extends JPanel {

    public interface Listener { void onQuizCreated(int id); }

    private final QuizService service;
    private final Listener listener;

    private final JTextField titleField   = new JTextField(20);
    private final JTextField teacherField = new JTextField("teacher1", 20);

    public CreateQuizPanel(QuizService service, Listener listener) {
        super(new GridBagLayout());
        this.service = service;
        this.listener = listener;
        UI.Support.AppStyle.stylePanelWithTitle(this, "Create Quiz");

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        UI.Support.AppStyle.styleTextField(titleField);
        UI.Support.AppStyle.styleTextField(teacherField);
        JButton createBtn = new JButton("Create Quiz");
        UI.Support.AppStyle.styleButton(createBtn);
        createBtn.addActionListener(e -> create());

        g.gridx = 0; g.gridy = 0; add(new JLabel("Title:"), g);
        g.gridx = 1;              add(titleField, g);
        g.gridx = 0; g.gridy = 1; add(new JLabel("Created by:"), g);
        g.gridx = 1;              add(teacherField, g);
        g.gridx = 1; g.gridy = 2; add(createBtn, g);
    }

    private void create() {
        String title = titleField.getText().trim();
        String by    = teacherField.getText().trim();
        if (title.isEmpty() || by.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and creator are required.");
            return;
        }
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setCreatedBy(by);

        AsyncTask.run(this,
                () -> service.createQuiz(quiz),
                id -> {
                    JOptionPane.showMessageDialog(this, "Quiz created. ID = " + id);
                    titleField.setText("");
                    listener.onQuizCreated(id);
                },
                "Create quiz failed");
    }
}