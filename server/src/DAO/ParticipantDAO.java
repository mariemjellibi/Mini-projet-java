package DAO;

import Database.DBConnection;
import java.sql.*;

public class ParticipantDAO {
    public boolean joinQuiz(int quizId, String userId) throws SQLException {
        String sql = "INSERT INTO quiz_participants (user_id, quiz_id, status) VALUES (?, ?, 'in_progress') " +
                "ON CONFLICT (user_id, quiz_id) DO NOTHING";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setInt(2, quizId);
            return stmt.executeUpdate() > 0;
        }
    }

    public void markCompleted(int quizId, String userId) throws SQLException {
        String sql = "UPDATE quiz_participants SET status = 'completed', end_time = CURRENT_TIMESTAMP " +
                "WHERE quiz_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }
}