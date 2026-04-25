package UI.Panel;

import Model.Result;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaderboardPanel extends JPanel {

    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{"User", "Score (%)"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

    public LeaderboardPanel() {
        super(new BorderLayout(5, 5));
        UI.Support.AppStyle.stylePanelWithTitle(this, "Live Leaderboard");
        setPreferredSize(new Dimension(280, 0));

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        UI.Support.AppStyle.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UI.Support.AppStyle.BORDER));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void render(List<Result> lb) {
        model.setRowCount(0);
        for (Result r : lb) model.addRow(new Object[]{ r.getUserId(), r.getScore() });
    }
}