package DAO;


import Database.DBConnection;
import Model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAOImpl implements QuizDAO {

    @Override
    public int createQuiz(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (title, created_by) VALUES (?, ?)";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getCreatedBy());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Quiz creation failed — no generated ID returned.");
    }

    @Override
    public List<Quiz> getAllQuizzes() throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT id, title, created_by FROM quizzes ORDER BY id";
        try (Connection conn = DBConnection.makeConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                quizzes.add(mapRow(rs));
            }
        }
        return quizzes;
    }

    @Override
    public Quiz getQuizById(int quizId) throws SQLException {
        String sql = "SELECT id, title, created_by FROM quizzes WHERE id = ?";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private Quiz mapRow(ResultSet rs) throws SQLException {
        Quiz q = new Quiz();
        q.setId(rs.getInt("id"));
        q.setTitle(rs.getString("title"));
        q.setCreatedBy(rs.getString("created_by"));
        return q;
    }
}