package UI.Panel;

import Model.Question;
import Service.QuizService;
import UI.Support.AppStyle;
import UI.Support.AsyncTask;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/** Renders quiz questions, lets the student answer and submit. */
public class QuestionsPanel extends JPanel {

    private final QuizService service;
    private final String userId;

    private final JPanel questionsContainer = new JPanel();
    private final Map<Integer, ButtonGroup> answerGroups   = new HashMap<>();
    private final Map<Integer, Question>    currentByQuiId = new HashMap<>();
    private final Set<Integer> submittedQuizIds = new HashSet<>();
    private final Map<Integer, Set<Integer>> submittedQuestionIdsByQuiz = new HashMap<>();
    private final Map<Integer, Map<Integer, String>> submittedAnswersByQuiz = new HashMap<>();
    private final JButton submitButton = new JButton("Submit Answers");

    private int currentQuizId = -1;
    private final Consumer<Double> onSubmitted;

    private static final class LoadQuizResult {
        private final boolean canSubmit;
        private final List<Question> questions;

        private LoadQuizResult(boolean canSubmit, List<Question> questions) {
            this.canSubmit = canSubmit;
            this.questions = questions;
        }
    }

    public QuestionsPanel(QuizService service, String userId, Consumer<Double> onSubmitted) {
        super(new BorderLayout(5, 5));
        this.service = service;
        this.userId = userId;
        this.onSubmitted = onSubmitted;
        AppStyle.stylePanelWithTitle(this, "Quiz");

        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));
        questionsContainer.setBackground(AppStyle.PANEL_BACKGROUND);
        JScrollPane scroll = new JScrollPane(questionsContainer);
        scroll.setBorder(BorderFactory.createLineBorder(AppStyle.BORDER));
        scroll.getViewport().setBackground(AppStyle.PANEL_BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        // Make the quiz/question area even bigger for better visibility
        scroll.setPreferredSize(new Dimension(1000, 700));

        AppStyle.styleButton(submitButton);
        submitButton.addActionListener(e -> submit());

        add(scroll, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    public void loadQuiz(int quizId) {
        currentQuizId = quizId;
        AsyncTask.run(this,
                () -> {
                    // Always check participation status from server
                    boolean canSubmit = service.joinQuiz(quizId, userId);
                    boolean alreadyParticipated = !canSubmit;
                    List<Question> questions = service.getQuestions(quizId);
                    return new LoadQuizResult(canSubmit, questions);
                },
                result -> {
                    if (result.canSubmit) {
                        submittedQuizIds.remove(quizId);
                        submittedQuestionIdsByQuiz.remove(quizId);
                        submittedAnswersByQuiz.remove(quizId);
                    } else {
                        submittedQuizIds.add(quizId);
                    }
                    render(result.questions);
                    // Force parent frame to preferred size for visibility
                    SwingUtilities.invokeLater(() -> {
                        Window win = SwingUtilities.getWindowAncestor(this);
                        if (win != null) {
                            win.pack();
                        }
                    });
                },
                "Load quiz failed");
    }

    public void refreshCurrentQuiz() {
        if (currentQuizId == -1) {
            return;
        }
        AsyncTask.run(this,
                () -> service.getQuestions(currentQuizId),
                this::render,
                "Refresh quiz failed");
    }

    private void render(List<Question> qs) {
        Map<Integer, String> previousSelections = captureSelections();
        questionsContainer.removeAll();
        answerGroups.clear();
        currentByQuiId.clear();

        // Always check participation status from memory (set by loadQuiz)
        boolean alreadySubmitted = submittedQuizIds.contains(currentQuizId);
        Set<Integer> submittedQuestionIds = submittedQuestionIdsByQuiz.get(currentQuizId);
        Map<Integer, String> submittedAnswers = submittedAnswersByQuiz.get(currentQuizId);
        boolean hasNewQuestions = hasNewQuestions(qs, submittedQuestionIds);
        boolean canSubmit = !alreadySubmitted || hasNewQuestions;

        // If already submitted and no new questions, disable all answer options and submit button
        boolean lockAll = alreadySubmitted && !hasNewQuestions;

        int n = 1;
        for (Question q : qs) {
            currentByQuiId.put(q.getId(), q);
            JPanel block = new JPanel();
            block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
            block.setOpaque(true);
            block.setBackground(new Color(250, 252, 255));
            TitledBorder blockBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER),
                "Q" + (n++) + " - " + q.getQuestionText());
            blockBorder.setTitleColor(AppStyle.ACCENT_DARK);
            blockBorder.setTitleFont(AppStyle.FONT_BODY.deriveFont(Font.BOLD)); // keep original font size
            block.setBorder(BorderFactory.createCompoundBorder(
                blockBorder,
                BorderFactory.createEmptyBorder(32, 48, 32, 48))); // much more padding for a bigger box
            block.setMaximumSize(new Dimension(900, 300)); // make the box itself much bigger
            block.setPreferredSize(new Dimension(900, 300));

            ButtonGroup grp = new ButtonGroup();
            answerGroups.put(q.getId(), grp);

            if (q.getOptions() != null) {
                for (String opt : q.getOptions()) {
                    JRadioButton rb = new JRadioButton(opt);
                    rb.setActionCommand(opt);
                    rb.setBackground(block.getBackground());
                    rb.setForeground(AppStyle.TEXT_PRIMARY);
                    rb.setFont(AppStyle.FONT_BODY);
                    boolean lockExisting = alreadySubmitted && submittedQuestionIds != null && submittedQuestionIds.contains(q.getId());
                    boolean shouldDisable = lockAll || lockExisting;
                    rb.setEnabled(!shouldDisable && canSubmit);
                    String submittedAnswer = submittedAnswers == null ? null : submittedAnswers.get(q.getId());
                    if (shouldDisable) {
                        if (submittedAnswer != null && submittedAnswer.equals(opt)) {
                            rb.setSelected(true);
                        }
                    } else {
                        String previous = previousSelections.get(q.getId());
                        if (previous != null && previous.equals(opt)) {
                            rb.setSelected(true);
                        }
                    }
                    grp.add(rb);
                    block.add(rb);
                }
            }
            questionsContainer.add(block);
            questionsContainer.add(Box.createVerticalStrut(6));
        }
        submitButton.setEnabled(canSubmit && !lockAll);
        questionsContainer.revalidate();
        questionsContainer.repaint();
    }

    private Map<Integer, String> captureSelections() {
        Map<Integer, String> selected = new HashMap<>();
        for (Map.Entry<Integer, ButtonGroup> e : answerGroups.entrySet()) {
            ButtonModel sel = e.getValue().getSelection();
            if (sel != null) {
                selected.put(e.getKey(), sel.getActionCommand());
            }
        }
        return selected;
    }

    private void setAnswersEnabled(boolean enabled) {
        for (ButtonGroup group : answerGroups.values()) {
            Enumeration<AbstractButton> buttons = group.getElements();
            while (buttons.hasMoreElements()) {
                buttons.nextElement().setEnabled(enabled);
            }
        }
    }

    private boolean hasNewQuestions(List<Question> questions, Set<Integer> submittedQuestionIds) {
        if (!submittedQuizIds.contains(currentQuizId)) {
            return false;
        }
        if (submittedQuestionIds == null) {
            // Student submitted in a previous app session; allow answering if quiz changed.
            return true;
        }
        for (Question q : questions) {
            if (!submittedQuestionIds.contains(q.getId())) {
                return true;
            }
        }
        return false;
    }

    private void submit() {
        if (currentQuizId == -1) {
            JOptionPane.showMessageDialog(this, "Join a quiz first.");
            return;
        }
        Set<Integer> submittedQuestionIds = submittedQuestionIdsByQuiz.get(currentQuizId);
        if (submittedQuizIds.contains(currentQuizId) && !hasNewQuestions(new java.util.ArrayList<>(currentByQuiId.values()), submittedQuestionIds)) {
            JOptionPane.showMessageDialog(this,
                    "You already submitted this quiz.",
                    "Already Submitted", JOptionPane.INFORMATION_MESSAGE);
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
                submittedQuizIds.add(currentQuizId);
                submittedQuestionIdsByQuiz.put(currentQuizId, new HashSet<>(currentByQuiId.keySet()));
                submittedAnswersByQuiz.put(currentQuizId, new HashMap<>(answers));
                setAnswersEnabled(false);
                submitButton.setEnabled(false);
                    JOptionPane.showMessageDialog(this,
                            "Your score: " + score + " %", "Result",
                            JOptionPane.INFORMATION_MESSAGE);
                    onSubmitted.accept(score);
                },
                "Submit failed");
    }
}