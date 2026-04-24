package DAO;

import Database.DBConnection;
import Model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionDAOImpl implements QuestionDAO {

    @Override
    public int addQuestion(Question question) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, question_text, options, correct_answer) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getQuestionText());
            Array optionsArray = conn.createArrayOf("text", question.getOptions().toArray());
            stmt.setArray(3, optionsArray);
            stmt.setString(4, question.getCorrectAnswer());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to add question.");
    }

    @Override
    public List<Question> getQuestionsByQuizIdWithAnswers(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";
        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setQuizId(rs.getInt("quiz_id"));
                q.setQuestionText(rs.getString("question_text"));
                Array sqlArray = rs.getArray("options");
                if (sqlArray != null) {
                    String[] opts = (String[]) sqlArray.getArray();
                    q.setOptions(Arrays.asList(opts));
                }
                q.setCorrectAnswer(rs.getString("correct_answer"));
                questions.add(q);
            }
        }
        return questions;
    }

    @Override
    public List<Question> getQuestionsForClient(int quizId) throws SQLException {
        List<Question> questions = getQuestionsByQuizIdWithAnswers(quizId);
        for (Question q : questions) {
            q.setCorrectAnswer(null);
        }
        return questions;
    }
}