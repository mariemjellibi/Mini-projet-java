package DAO;

import Database.DBConnection;
import Model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAOImpl implements ResultDAO {

    @Override
    public void saveResult(Result result) throws SQLException {

        String sql =
                "INSERT INTO results (user_id, quiz_id, score) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE score = VALUES(score)";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, result.getUserId());
            stmt.setInt(2, result.getQuizId());
            stmt.setDouble(3, result.getScore());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    result.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Result> getLeaderboard(int quizId) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql =
                "SELECT id, user_id, quiz_id, score " +
                        "FROM results WHERE quiz_id = ? " +
                        "ORDER BY score DESC, id ASC";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Result r = new Result();
                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getString("user_id"));
                    r.setQuizId(rs.getInt("quiz_id"));
                    r.setScore(rs.getDouble("score"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    @Override
    public boolean hasParticipated(int quizId, String userId) throws SQLException {
        String sql = "SELECT 1 FROM results WHERE quiz_id = ? AND user_id = ? LIMIT 1";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            stmt.setString(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}