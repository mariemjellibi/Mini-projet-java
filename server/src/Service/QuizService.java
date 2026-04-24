package Service;

import Model.Quiz;


import Model.Result;
import Model.Question;



import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Remote interface for the Quiz System.
 * All methods must throw RemoteException.
 */
public interface QuizService extends Remote {
    int createQuiz(Quiz quiz) throws RemoteException;
    void addQuestion(Question question) throws RemoteException;

    List<Quiz> getQuizzes() throws RemoteException;
    List<Question> getQuestions(int quizId) throws RemoteException;

    // New: student joins the quiz (records participation, returns true if allowed)
    boolean joinQuiz(int quizId, String userId) throws RemoteException;

    double submitAnswers(int quizId, String userId, Map<Integer, String> answers) throws RemoteException;

    List<Result> getLeaderboard(int quizId) throws RemoteException;

    // Real-time listener management
    void registerListener(int quizId, LeaderboardListener listener) throws RemoteException;
    void unregisterListener(int quizId, LeaderboardListener listener) throws RemoteException;
}