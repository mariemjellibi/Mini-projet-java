package gui;

import Model.Quiz;
import Model.Question;
import Service.QuizService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
public class StudentQuizFrame extends JFrame {
    private QuizService service;
    private String userId;
    private List<Quiz> quizzes;
    private JComboBox<String> quizCombo;
    private JButton joinBtn, startBtn, finishBtn;
    private JPanel quizArea;
    private CardLayout quizLayout;
    private JLabel timerLabel;
    private Timer timer;
    private int secondsLeft;
    private List<Question> questions;
    private Map<Integer, ButtonGroup> optionGroups = new HashMap<>();
    private int currentQuizId;
    private JButton nextBtn, prevBtn, leaderboardBtn;
    private JPanel navPanel;
    private int currentCard;
    private boolean quizActive = false;

    public StudentQuizFrame(QuizService service, String userId) {
        this.service = service;
        this.userId = userId;

        setTitle("Student - " + userId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 550);
        setLocationRelativeTo(null);

        // ----- Top: quiz selection & timer -----
        JPanel topPanel = new JPanel(new FlowLayout());
        quizCombo = new JComboBox<>();
        joinBtn = new JButton("Join");
        startBtn = new JButton("Start Quiz");
        timerLabel = new JLabel("Time: 60s");
        topPanel.add(new JLabel("Quiz:"));
        topPanel.add(quizCombo);
        topPanel.add(joinBtn);
        topPanel.add(startBtn);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        // ----- Centre: question cards (empty initially) -----
        quizLayout = new CardLayout();
        quizArea = new JPanel(quizLayout);
        JLabel welcome = new JLabel("Join a quiz, then click Start Quiz.", SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.PLAIN, 14));
        quizArea.add(welcome, "welcome");
        quizLayout.show(quizArea, "welcome");
        add(quizArea, BorderLayout.CENTER);

        // ----- Bottom: navigation buttons (hidden until quiz starts) -----
        navPanel = new JPanel(new FlowLayout());
        prevBtn = new JButton("Previous");
        nextBtn = new JButton("Next");
        finishBtn = new JButton("Finish Quiz");   // renamed
        leaderboardBtn = new JButton("Leaderboard");
        navPanel.add(prevBtn);
        navPanel.add(nextBtn);
        navPanel.add(finishBtn);
        navPanel.add(leaderboardBtn);
        navPanel.setVisible(false);
        add(navPanel, BorderLayout.SOUTH);

        // ----- Actions -----
        joinBtn.addActionListener(e -> joinQuiz());
        startBtn.addActionListener(e -> startQuiz());
        prevBtn.addActionListener(e -> navigate(-1));
        nextBtn.addActionListener(e -> navigate(1));
        finishBtn.addActionListener(e -> confirmAndSubmit());
        leaderboardBtn.addActionListener(e -> {
            if (currentQuizId > 0)
                new LeaderboardFrame(service, currentQuizId, userId, true);
        });

        populateQuizCombo();
        setVisible(true);
    }

    private void populateQuizCombo() {
        try {
            quizzes = service.getQuizzes();
            quizCombo.removeAllItems();
            for (Quiz q : quizzes) {
                quizCombo.addItem(q.getId() + " - " + q.getTitle());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading quizzes: " + ex.getMessage());
        }
    }

    private void joinQuiz() {
        int idx = quizCombo.getSelectedIndex();
        if (idx == -1) return;
        Quiz sel = quizzes.get(idx);
        try {
            boolean joined = service.joinQuiz(sel.getId(), userId);
            JOptionPane.showMessageDialog(this,
                    joined ? "Joined! You can now start the quiz." : "You are already in this quiz.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Join error: " + ex.getMessage());
        }
    }

    private void startQuiz() {
        int idx = quizCombo.getSelectedIndex();
        if (idx == -1) return;
        Quiz sel = quizzes.get(idx);
        currentQuizId = sel.getId();

        try {
            questions = service.getQuestions(currentQuizId);
            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "This quiz has no questions yet.");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching questions: " + ex.getMessage());
            return;
        }

        // Stop any previous timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        // Build new cards
        quizArea.removeAll();
        optionGroups.clear();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            JPanel card = new JPanel(new BorderLayout(10, 10));
            card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel qLabel = new JLabel("<html><b>Q" + (i+1) + ": " + q.getQuestionText() + "</b></html>");
            card.add(qLabel, BorderLayout.NORTH);

            JPanel choicesPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            ButtonGroup group = new ButtonGroup();
            for (String opt : q.getOptions()) {
                JRadioButton rb = new JRadioButton(opt);
                group.add(rb);
                choicesPanel.add(rb);
            }
            card.add(choicesPanel, BorderLayout.CENTER);

            optionGroups.put(q.getId(), group);
            quizArea.add(card, "q" + i);
        }

        // Reset navigation
        currentCard = 0;
        quizLayout.show(quizArea, "q0");
        quizArea.revalidate();
        quizArea.repaint();
        updateNavigationButtons();

        // Start fresh timer
        secondsLeft = 60;
        timerLabel.setText("Time: " + secondsLeft + "s");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    secondsLeft--;
                    timerLabel.setText("Time: " + secondsLeft + "s");
                    if (secondsLeft <= 0) {
                        timer.cancel();
                        JOptionPane.showMessageDialog(StudentQuizFrame.this, "Time's up!");
                        submitAllAnswers();
                    }
                });
            }
        }, 1000, 1000);

        // Update UI state
        quizActive = true;
        navPanel.setVisible(true);
        joinBtn.setEnabled(false);
        startBtn.setEnabled(false);
        leaderboardBtn.setEnabled(false);
        finishBtn.setEnabled(true);
    }

    private void navigate(int delta) {
        if (!quizActive) return;
        int nextCard = currentCard + delta;
        if (nextCard >= 0 && nextCard < questions.size()) {
            currentCard = nextCard;
            quizLayout.show(quizArea, "q" + currentCard);
            updateNavigationButtons();
        }
    }

    private void updateNavigationButtons() {
        prevBtn.setEnabled(currentCard > 0);
        nextBtn.setEnabled(currentCard < questions.size() - 1);
    }

    // Show confirmation dialog before submitting
    private void confirmAndSubmit() {
        if (!quizActive) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to finish the quiz and submit all answers?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            submitAllAnswers();
        }
    }

    private void submitAllAnswers() {
        if (!quizActive) return;  // prevent double submission
        quizActive = false;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        // Collect answers
        Map<Integer, String> answers = new HashMap<>();
        for (Map.Entry<Integer, ButtonGroup> entry : optionGroups.entrySet()) {
            int qId = entry.getKey();
            ButtonGroup group = entry.getValue();
            for (java.util.Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();) {
                AbstractButton btn = e.nextElement();
                if (btn.isSelected()) {
                    answers.put(qId, btn.getText());
                    break;
                }
            }
        }

        try {
            double score = service.submitAnswers(currentQuizId, userId, answers);
            JOptionPane.showMessageDialog(this, "Your score: " + String.format("%.1f%%", score));
            leaderboardBtn.setEnabled(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Submit error: " + ex.getMessage());
        }

        // Disable all navigation after submission
        prevBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        finishBtn.setEnabled(false);
        joinBtn.setEnabled(false);
        startBtn.setEnabled(false);
    }
}