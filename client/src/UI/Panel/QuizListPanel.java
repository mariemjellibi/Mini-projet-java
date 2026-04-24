package UI.Panel;

import Model.Quiz;
import Service.QuizService;
import UI.Support.AsyncTask;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class QuizListPanel extends JPanel {

    private final QuizService service;
    private final DefaultListModel<Quiz> model = new DefaultListModel<>();
    private final JList<Quiz> list = new JList<>(model);

    public QuizListPanel(QuizService service, String title, String actionLabel,
                         Consumer<Quiz> onAction) {
        super(new BorderLayout(5, 5));
        this.service = service;
        setBorder(BorderFactory.createTitledBorder(title));
        setPreferredSize(new Dimension(240, 0));

        list.setCellRenderer((l, v, i, sel, foc) -> {
            JLabel lbl = new JLabel(v == null ? "" : "#" + v.getId() + "  " + v.getTitle());
            lbl.setOpaque(true);
            if (sel) { lbl.setBackground(l.getSelectionBackground()); lbl.setForeground(l.getSelectionForeground()); }
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return lbl;
        });

        JButton refresh = new JButton("⟳ Refresh");
        refresh.addActionListener(e -> refresh());

        JButton action = new JButton(actionLabel);
        action.addActionListener(e -> {
            Quiz q = list.getSelectedValue();
            if (q != null) onAction.accept(q);
            else JOptionPane.showMessageDialog(this, "Select a quiz first.");
        });

        JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 5));
        buttons.add(refresh);
        buttons.add(action);

        add(new JScrollPane(list), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    public Quiz getSelected() { return list.getSelectedValue(); }

    public void refresh() {
        AsyncTask.run(this,
                service::getQuizzes,
                this::render,
                "Refresh quizzes failed");
    }

    private void render(List<Quiz> quizzes) {
        model.clear();
        for (Quiz q : quizzes) model.addElement(q);
    }
}