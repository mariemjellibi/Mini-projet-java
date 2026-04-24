package DAO;

import Model.Question;
import java.sql.SQLException;
import java.util.List;

public interface QuestionDAO {
    int addQuestion(Question question) throws SQLException;
    List<Question> getQuestionsByQuizIdWithAnswers(int quizId) throws SQLException;
    List<Question> getQuestionsForClient(int quizId) throws SQLException;
}