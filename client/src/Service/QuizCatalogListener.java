package Service;

import Model.Quiz;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface QuizCatalogListener extends Remote {
    void onQuizCatalogChanged(List<Quiz> quizzes) throws RemoteException;
}