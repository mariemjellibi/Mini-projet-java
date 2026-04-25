package DAO;

import java.sql.SQLException;


public class ParticipantDAO {

    private final ResultDAO resultDAO = new ResultDAOImpl();
    public boolean joinQuiz(int quizId, String userId) throws SQLException {
        return !resultDAO.hasParticipated(quizId, userId);
    }
}