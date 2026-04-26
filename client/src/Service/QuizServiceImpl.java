package Service;

import DAO.*;

import Model.Quiz;
import Model.Question;
import Model.Result;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class QuizServiceImpl extends UnicastRemoteObject implements QuizService {

    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private ResultDAO resultDAO;
    private Map<Integer, List<LeaderboardListener>> listenersMap = new HashMap<>();
    private final List<QuizCatalogListener> quizCatalogListeners = new CopyOnWriteArrayList<>();
    private ParticipantDAO participantDAO = new ParticipantDAO();

    public QuizServiceImpl() throws RemoteException {
        super();
        quizDAO = new QuizDAOImpl();
        questionDAO = new QuestionDAOImpl();
        resultDAO = new ResultDAOImpl();
    }

    @Override
    public int createQuiz(Quiz quiz) throws RemoteException {
        try {
            int quizId = quizDAO.createQuiz(quiz);
            notifyQuizCatalogListeners();
            return quizId;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error creating quiz");
        }
    }

    @Override
    public void addQuestion(Question question) throws RemoteException {
        try {
            questionDAO.addQuestion(question);
            notifyQuizCatalogListeners();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error adding question");
        }
    }

    @Override
    public List<Quiz> getQuizzes() throws RemoteException {
        try {
            return quizDAO.getAllQuizzes();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error fetching quizzes");
        }
    }

    @Override
    public List<Question> getQuestions(int quizId) throws RemoteException {
        try {
            return questionDAO.getQuestionsForClient(quizId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error fetching questions");
        }
    }

    @Override
    public boolean joinQuiz(int quizId, String userId) throws RemoteException {
        try {
            boolean joined = participantDAO.joinQuiz(quizId, userId);
            if (joined) {
                System.out.println("✅ Participant " + userId + " joined quiz " + quizId);
            } else {
                System.out.println("⚠️ Participant " + userId + " already in quiz " + quizId + " (or already completed)");
            }
            return joined;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Join error");
        }
    }

    @Override
    public double submitAnswers(int quizId, String userId, Map<Integer, String> answers) throws RemoteException {
        try {
            // Check if the user has already participated
            if (resultDAO.hasParticipated(quizId, userId)) {
                throw new RemoteException("You have already submitted answers for this quiz. You cannot change your answers.");
            }
            List<Question> questions = questionDAO.getQuestionsByQuizIdWithAnswers(quizId);
            int total = questions.size();
            int correct = 0;
            for (Question q : questions) {
                String ans = answers.get(q.getId());
                if (ans != null && ans.equalsIgnoreCase(q.getCorrectAnswer())) {
                    correct++;
                }
            }
            double score = (total == 0) ? 0 : (double) correct / total * 100.0;

            Result r = new Result();
            r.setUserId(userId);
            r.setQuizId(quizId);
            r.setScore(score);
            resultDAO.saveResult(r);


            System.out.println("📩 Submission from " + userId + " for quiz " + quizId + " – Score: " + score);
            notifyListeners(quizId);

            return score;
        } catch (SQLException e) {
            e.printStackTrace();   // Print the real error on the server console
            throw new RemoteException("Grading error: " + e.getMessage()); // now includes the cause message
        }
    }

    @Override
    public List<Result> getLeaderboard(int quizId) throws RemoteException {
        try {
            return resultDAO.getLeaderboard(quizId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Leaderboard error");
        }
    }

    @Override
    public synchronized void registerListener(int quizId, LeaderboardListener listener) throws RemoteException {
        listenersMap.computeIfAbsent(quizId, k -> new CopyOnWriteArrayList<>()).add(listener);
        System.out.println("Listener added for quiz " + quizId);
    }

    @Override
    public synchronized void unregisterListener(int quizId, LeaderboardListener listener) throws RemoteException {
        List<LeaderboardListener> list = listenersMap.get(quizId);
        if (list != null) {
            list.remove(listener);
        }
    }

    @Override
    public synchronized void registerQuizCatalogListener(QuizCatalogListener listener) throws RemoteException {
        quizCatalogListeners.add(listener);
        try {
            listener.onQuizCatalogChanged(quizDAO.getAllQuizzes());
        } catch (SQLException e) {
            quizCatalogListeners.remove(listener);
            throw new RemoteException("Database error fetching quizzes", e);
        } catch (RemoteException e) {
            quizCatalogListeners.remove(listener);
            throw e;
        }
    }

    @Override
    public synchronized void unregisterQuizCatalogListener(QuizCatalogListener listener) throws RemoteException {
        quizCatalogListeners.remove(listener);
    }

    private void notifyListeners(int quizId) {
        List<LeaderboardListener> listeners = listenersMap.get(quizId);
        if (listeners == null || listeners.isEmpty()) return;

        try {
            List<Result> lb = resultDAO.getLeaderboard(quizId);
            for (LeaderboardListener l : listeners) {
                try {
                    l.updateLeaderboard(lb);
                } catch (RemoteException e) {
                    listeners.remove(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void notifyQuizCatalogListeners() {
        if (quizCatalogListeners.isEmpty()) {
            return;
        }

        List<Quiz> quizzes;
        try {
            quizzes = quizDAO.getAllQuizzes();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        for (QuizCatalogListener listener : quizCatalogListeners) {
            try {
                listener.onQuizCatalogChanged(quizzes);
            } catch (RemoteException e) {
                quizCatalogListeners.remove(listener);
            }
        }
    }
}