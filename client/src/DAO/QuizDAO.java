package DAO;

import Model.Quiz;
import java.sql.SQLException;
import java.util.List;

public interface QuizDAO {
    int createQuiz(Quiz quiz) throws SQLException;
    List<Quiz> getAllQuizzes() throws SQLException;
}