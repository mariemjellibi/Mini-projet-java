package DAO;

import java.sql.SQLException;

/**
 * In this schema there is no dedicated participants table.
 * "Joining" is tracked implicitly via the results table.
 * This class is kept as a thin façade so QuizServiceImpl doesn't need to change.
 */
public class ParticipantDAO {

    private final ResultDAO resultDAO = new ResultDAOImpl();

    /**
     * Returns true if this is the first time the user engages with the quiz
     * (no result row yet). Returns false if the user has already submitted.
     */
    public boolean joinQuiz(int quizId, String userId) throws SQLException {
        return !resultDAO.hasParticipated(quizId, userId);
    }
}