package UI.Panel;

import Model.Question;
import Model.Quiz;
import Service.QuizService;
import UI.Support.AsyncTask;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class AddQuestionPanel extends JPanel {

    private final QuizService service;
    private final Supplier<Quiz> selectedQuizSupplier;

    private final JTextField textField    = new JTextField(20);
    private final JTextField opt1         = new JTextField();
    private final JTextField opt2         = new JTextField();
    private final JTextField opt3         = new JTextField();
    private final JTextField opt4         = new JTextField();
    private final JTextField correctField = new JTextField();

    public AddQuestionPanel(QuizService service, Supplier<Quiz> selectedQuizSupplier) {
        super(new GridBagLayout());
        this.service = service;
        this.selectedQuizSupplier = selectedQuizSupplier;
        setBorder(BorderFactory.createTitledBorder("Add Question (to selected quiz)"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        JButton addBtn = new JButton("Add Question");
        addBtn.addActionListener(e -> addQuestion());

        int row = 0;
        addRow(g, row++, "Question:",       textField);
        addRow(g, row++, "Option 1:",       opt1);
        addRow(g, row++, "Option 2:",       opt2);
        addRow(g, row++, "Option 3:",       opt3);
        addRow(g, row++, "Option 4:",       opt4);
        addRow(g, row++, "Correct answer:", correctField);

        g.gridx = 1; g.gridy = row;
        add(addBtn, g);
    }

    private void addRow(GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0; g.gridy = row; add(new JLabel(label), g);
        g.gridx = 1;                add(field, g);
    }

    private void addQuestion() {
        Quiz quiz = selectedQuizSupplier.get();
        if (quiz == null) {
            JOptionPane.showMessageDialog(this, "Select a quiz first.");
            return;
        }
        String txt = textField.getText().trim();
        List<String> opts = new ArrayList<>(Arrays.asList(
                opt1.getText().trim(), opt2.getText().trim(),
                opt3.getText().trim(), opt4.getText().trim()));
        opts.removeIf(String::isEmpty);
        String correct = correctField.getText().trim();

        if (txt.isEmpty() || opts.size() < 2 || correct.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Need question text, at least 2 options, and a correct answer.");
            return;
        }
        if (!opts.contains(correct)) {
            JOptionPane.showMessageDialog(this,
                    "Correct answer must be one of the options.");
            return;
        }

        Question question = new Question();
        question.setQuizId(quiz.getId());
        question.setQuestionText(txt);
        question.setOptions(opts);
        question.setCorrectAnswer(correct);

        AsyncTask.run(this,
                () -> { service.addQuestion(question); return null; },
                v -> { JOptionPane.showMessageDialog(this, "Question added."); clearForm(); },
                "Add question failed");
    }

    private void clearForm() {
        textField.setText(""); opt1.setText(""); opt2.setText("");
        opt3.setText(""); opt4.setText(""); correctField.setText("");
    }
}