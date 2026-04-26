package DAO;

import Database.DBConnection;
import Model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionDAOImpl implements QuestionDAO {

    @Override
    public int addQuestion(Question question) throws SQLException {
        String insertQuestion =
                "INSERT INTO questions (quiz_id, question_text, correct_answer) VALUES (?, ?, ?)";
        String insertOption =
                "INSERT INTO question_options (question_id, option_text) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.makeConnection();
            conn.setAutoCommit(false);

            int questionId;
            try (PreparedStatement stmt = conn.prepareStatement(
                    insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, question.getQuizId());
                stmt.setString(2, question.getQuestionText());
                stmt.setString(3, question.getCorrectAnswer());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to add question — no ID generated.");
                    }
                    questionId = rs.getInt(1);
                }
            }

            if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                try (PreparedStatement optStmt = conn.prepareStatement(insertOption)) {
                    for (String option : question.getOptions()) {
                        optStmt.setInt(1, questionId);
                        optStmt.setString(2, option);
                        optStmt.addBatch();
                    }
                    optStmt.executeBatch();
                }
            }

            conn.commit();
            question.setId(questionId);
            return questionId;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public List<Question> getQuestionsByQuizIdWithAnswers(int quizId) throws SQLException {
        // Single query with LEFT JOIN to fetch questions + their options in one round-trip
        String sql =
                "SELECT q.id AS q_id, q.quiz_id, q.question_text, q.correct_answer, " +
                "       o.option_text " +
                "FROM questions q " +
                "LEFT JOIN question_options o ON o.question_id = q.id " +
                "WHERE q.quiz_id = ? " +
                "ORDER BY q.id, o.id";

        Map<Integer, Question> byId = new HashMap<>();
        List<Question> ordered = new ArrayList<>();

        try (Connection conn = DBConnection.makeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int qid = rs.getInt("q_id");
                    Question q = byId.get(qid);
                    if (q == null) {
                        q = new Question();
                        q.setId(qid);
                        q.setQuizId(rs.getInt("quiz_id"));
                        q.setQuestionText(rs.getString("question_text"));
                        q.setCorrectAnswer(rs.getString("correct_answer"));
                        q.setOptions(new ArrayList<>());
                        byId.put(qid, q);
                        ordered.add(q);
                    }
                    String opt = rs.getString("option_text");
                    if (opt != null) {
                        q.getOptions().add(opt);
                    }
                }
            }
        }
        return ordered;
    }

    @Override
    public List<Question> getQuestionsForClient(int quizId) throws SQLException {
        List<Question> questions = getQuestionsByQuizIdWithAnswers(quizId);
        for (Question q : questions) {
            q.setCorrectAnswer(null); // hide correct answer from client
        }
        return questions;
    }
}