package gui;

import Model.Result;
import Service.LeaderboardListener;
import Service.QuizService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class LeaderboardFrame extends JFrame {
    private QuizService service;
    private int quizId;
    private String userId;
    private boolean asStudent;
    private DefaultTableModel tableModel;
    private LeaderboardListener listener;
    private boolean listenerExported = false;

    public LeaderboardFrame(QuizService service, int quizId, String userId, boolean asStudent) {
        this.service = service;
        this.quizId = quizId;
        this.userId = userId;
        this.asStudent = asStudent;

        setTitle("Live Leaderboard – Quiz " + quizId);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new Object[]{"Rank", "User", "Score"}, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Create and export listener
        listener = new LeaderboardListener() {
            @Override
            public void updateLeaderboard(List<Result> leaderboard) throws RemoteException {
                // Update Swing table on EDT
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    int rank = 1;
                    for (Result r : leaderboard) {
                        tableModel.addRow(new Object[]{rank++, r.getUserId(), String.format("%.1f%%", r.getScore())});
                    }
                });
            }
        };

        try {
            UnicastRemoteObject.exportObject(listener, 0);
            listenerExported = true;
            service.registerListener(quizId, listener);
            // Fetch initial leaderboard
            List<Result> initial = service.getLeaderboard(quizId);
            listener.updateLeaderboard(initial);
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering listener: " + e.getMessage());
        }

        // Unregister when window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    service.unregisterListener(quizId, listener);
                    UnicastRemoteObject.unexportObject(listener, true);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        setVisible(true);
    }
}