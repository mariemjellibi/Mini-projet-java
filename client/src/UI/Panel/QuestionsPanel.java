package UI.Panel;

import Model.Question;
import Service.QuizService;
import UI.Support.AsyncTask;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Renders quiz questions, lets the student answer and submit. */
public class QuestionsPanel extends JPanel {

    private final QuizService service;
    private final String userId;

    private final JPanel questionsContainer = new JPanel();
    private final Map<Integer, ButtonGroup> answerGroups   = new HashMap<>();
    private final Map<Integer, Question>    currentByQuiId = new HashMap<>();

    private int currentQuizId = -1;
    private final Consumer<Double> onSubmitted;

    public QuestionsPanel(QuizService service, String userId, Consumer<Double> onSubmitted) {
        super(new BorderLayout(5, 5));
        this.service = service;
        this.userId = userId;
        this.onSubmitted = onSubmitted;
        setBorder(BorderFactory.createTitledBorder("Quiz"));

        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(questionsContainer);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JButton submit = new JButton("Submit Answers");
        submit.addActionListener(e -> submit());

        add(scroll, BorderLayout.CENTER);
        add(submit, BorderLayout.SOUTH);
    }

    public void loadQuiz(int quizId) {
        currentQuizId = quizId;
        AsyncTask.run(this,
                () -> { service.joinQuiz(quizId, userId); return service.getQuestions(quizId); },
                this::render,
                "Load quiz failed");
    }

    private void render(List<Question> qs) {
        questionsContainer.removeAll();
        answerGroups.clear();
        currentByQuiId.clear();

        int n = 1;
        for (Question q : qs) {
            currentByQuiId.put(q.getId(), q);
            JPanel block = new JPanel();
            block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
            block.setBorder(BorderFactory.createTitledBorder("Q" + (n++) + " — " + q.getQuestionText()));

            ButtonGroup grp = new ButtonGroup();
            answerGroups.put(q.getId(), grp);

            if (q.getOptions() != null) {
                for (String opt : q.getOptions()) {
                    JRadioButton rb = new JRadioButton(opt);
                    rb.setActionCommand(opt);
                    grp.add(rb);
                    block.add(rb);
                }
            }
            questionsContainer.add(block);
            questionsContainer.add(Box.createVerticalStrut(6));
        }
        questionsContainer.revalidate();
        questionsContainer.repaint();
    }

    private void submit() {
        if (currentQuizId == -1) {
            JOptionPane.showMessageDialog(this, "Join a quiz first.");
            return;
        }
        Map<Integer, String> answers = new HashMap<>();
        for (Map.Entry<Integer, ButtonGroup> e : answerGroups.entrySet()) {
            ButtonModel sel = e.getValue().getSelection();
            if (sel != null) answers.put(e.getKey(), sel.getActionCommand());
        }
        if (answers.size() < currentByQuiId.size()) {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Some questions are unanswered. Submit anyway?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
        }

        AsyncTask.run(this,
                () -> service.submitAnswers(currentQuizId, userId, answers),
                score -> {
                    JOptionPane.showMessageDialog(this,
                            "Your score: " + score + " %", "Result",
                            JOptionPane.INFORMATION_MESSAGE);
                    onSubmitted.accept(score);
                },
                "Submit failed");
    }
}