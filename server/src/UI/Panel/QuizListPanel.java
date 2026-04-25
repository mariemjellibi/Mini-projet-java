package UI.Panel;

import Model.Quiz;
import Service.QuizService;
import UI.Support.AsyncTask;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/** Shows the list of quizzes and notifies a listener on selection / action. */
public class QuizListPanel extends JPanel {

    private final QuizService service;
    private final DefaultListModel<Quiz> model = new DefaultListModel<>();
    private final JList<Quiz> list = new JList<>(model);
    private final JButton actionButton;

    public QuizListPanel(QuizService service, String title, String actionLabel,
                         Consumer<Quiz> onAction) {
        super(new BorderLayout(5, 5));
        this.service = service;
        setBorder(BorderFactory.createTitledBorder(title));
        setPreferredSize(new Dimension(260, 0));

        list.setCellRenderer((l, v, i, sel, foc) -> {
            JLabel lbl = new JLabel(v == null ? "" : "#" + v.getId() + "  " + v.getTitle());
            lbl.setOpaque(true);
            if (sel) { lbl.setBackground(l.getSelectionBackground()); lbl.setForeground(l.getSelectionForeground()); }
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return lbl;
        });

        actionButton = new JButton(actionLabel);
        actionButton.addActionListener(e -> {
            Quiz q = list.getSelectedValue();
            if (q != null) onAction.accept(q);
            else JOptionPane.showMessageDialog(this, "Select a quiz first.");
        });

        add(new JScrollPane(list), BorderLayout.CENTER);
        add(actionButton, BorderLayout.SOUTH);
    }

    public Quiz getSelected() { return list.getSelectedValue(); }

    public void refresh() {
        AsyncTask.run(this,
                service::getQuizzes,
                this::render,
                "Refresh quizzes failed");
    }

    public void render(List<Quiz> quizzes) {
        model.clear();
        for (Quiz q : quizzes) model.addElement(q);
    }
}