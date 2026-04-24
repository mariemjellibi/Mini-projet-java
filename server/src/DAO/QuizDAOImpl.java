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
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Quiz creation failed.");
    }

    @Override
    public List<Quiz> getAllQuizzes() throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes";
        try (Connection conn = DBConnection.makeConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Quiz q = new Quiz();
                q.setId(rs.getInt("id"));
                q.setTitle(rs.getString("title"));
                q.setCreatedBy(rs.getString("created_by"));
                quizzes.add(q);
            }
        }
        return quizzes;
    }
}