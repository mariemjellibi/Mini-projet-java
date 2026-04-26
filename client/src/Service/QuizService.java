package Service;

import Model.Question;
import Model.Quiz;
import Model.Result;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface QuizService extends Remote {
    int createQuiz(Quiz quiz) throws RemoteException;
    void addQuestion(Question question) throws RemoteException;

    List<Quiz> getQuizzes() throws RemoteException;
    List<Question> getQuestions(int quizId) throws RemoteException;


    boolean joinQuiz(int quizId, String userId) throws RemoteException;

    double submitAnswers(int quizId, String userId, Map<Integer, String> answers) throws RemoteException;

    List<Result> getLeaderboard(int quizId) throws RemoteException;


    void registerListener(int quizId, LeaderboardListener listener) throws RemoteException;
    void unregisterListener(int quizId, LeaderboardListener listener) throws RemoteException;

    void registerQuizCatalogListener(QuizCatalogListener listener) throws RemoteException;
    void unregisterQuizCatalogListener(QuizCatalogListener listener) throws RemoteException;
}