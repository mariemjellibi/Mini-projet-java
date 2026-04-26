package UI.Panel;

import Model.Quiz;
import Service.QuizService;
import UI.Support.AsyncTask;
import UI.Support.AppStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class QuizListPanel extends JPanel {

    private final QuizService service;
    private final DefaultListModel<Quiz> model = new DefaultListModel<>();
    private final JList<Quiz> list = new JList<>(model);

    public QuizListPanel(QuizService service, String title, Consumer<Quiz> onAction) {
        super(new BorderLayout(5, 5));
        this.service = service;
        AppStyle.stylePanelWithTitle(this, title);
        setPreferredSize(new Dimension(240, 0));

        AppStyle.styleList(list);
        list.setCellRenderer((l, v, i, sel, foc) -> {
            JLabel lbl = new JLabel(v == null ? "" : "#" + v.getId() + "  " + v.getTitle());
            lbl.setOpaque(true);
            lbl.setFont(AppStyle.FONT_BODY);
            lbl.setForeground(AppStyle.TEXT_PRIMARY);
            if (sel) {
                lbl.setBackground(AppStyle.LIST_SELECTION);
                lbl.setForeground(AppStyle.TEXT_PRIMARY);
            } else {
                lbl.setBackground(AppStyle.PANEL_BACKGROUND);
            }
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return lbl;
        });
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    Quiz q = list.getSelectedValue();
                    if (q != null) {
                        onAction.accept(q);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppStyle.BORDER));
        add(scrollPane, BorderLayout.CENTER);
        JLabel hint = new JLabel("Double-click a quiz to join", SwingConstants.CENTER);
        hint.setFont(AppStyle.FONT_BODY.deriveFont(12f));
        hint.setForeground(AppStyle.TEXT_MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        add(hint, BorderLayout.SOUTH);
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