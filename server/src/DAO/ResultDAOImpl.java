package DAO;

import Database.DBConnection;
import Model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAOImpl implements ResultDAO {

    @Override
    public void saveResult(Result result) throws SQLException {
        String sql = "INSERT INTO results (user_id, quiz_id, score) VALUES (?, ?, ?) " +
                "ON CONFLICT (user_id, quiz_id) DO UPDATE SET score = EXCLUDED.score";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, result.getUserId());
            stmt.setInt(2, result.getQuizId());
            stmt.setDouble(3, result.getScore());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Result> getLeaderboard(int quizId) throws SQLException {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM results WHERE quiz_id = ? ORDER BY score DESC";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Result r = new Result();
                r.setId(rs.getInt("id"));
                r.setUserId(rs.getString("user_id"));
                r.setQuizId(rs.getInt("quiz_id"));
                r.setScore(rs.getDouble("score"));
                results.add(r);
            }
        }
        return results;
    }
}