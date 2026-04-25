package UI.Support;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class AppStyle {

    private AppStyle() {}

    public static final Color APP_BACKGROUND = new Color(244, 247, 252);
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color BORDER = new Color(208, 220, 235);
    public static final Color ACCENT = new Color(20, 115, 190);
    public static final Color ACCENT_DARK = new Color(12, 83, 143);
    public static final Color TEXT_PRIMARY = new Color(35, 43, 52);
    public static final Color TEXT_MUTED = new Color(96, 106, 118);
    public static final Color LIST_SELECTION = new Color(225, 238, 252);

    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 13);

    public static void stylePanelWithTitle(JComponent panel, String title) {
        panel.setOpaque(true);
        panel.setBackground(PANEL_BACKGROUND);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ),
                title
        );
        border.setTitleColor(ACCENT_DARK);
        border.setTitleFont(FONT_TITLE);
        panel.setBorder(border);
    }

    public static void styleButton(AbstractButton button) {
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(FONT_BODY.deriveFont(Font.BOLD));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_DARK),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    public static void styleList(JList<?> list) {
        list.setFont(FONT_BODY);
        list.setBackground(PANEL_BACKGROUND);
        list.setForeground(TEXT_PRIMARY);
        list.setSelectionBackground(LIST_SELECTION);
        list.setSelectionForeground(TEXT_PRIMARY);
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(24);
        table.setGridColor(BORDER);
        table.setShowGrid(true);
        table.setSelectionBackground(LIST_SELECTION);
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY.deriveFont(Font.BOLD));
        header.setBackground(new Color(230, 240, 252));
        header.setForeground(ACCENT_DARK);
        header.setReorderingAllowed(false);
    }
}