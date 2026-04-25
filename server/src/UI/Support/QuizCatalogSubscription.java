package UI.Support;

import Model.Quiz;
import Service.QuizCatalogListener;
import Service.QuizService;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.function.Consumer;

public class QuizCatalogSubscription {

    private final QuizService service;
    private QuizCatalogListener listener;

    public QuizCatalogSubscription(QuizService service) {
        this.service = service;
    }

    public void watch(Consumer<List<Quiz>> onChanged) throws Exception {
        cancel();

        QuizCatalogListener l = new QuizCatalogListener() {
            @Override
            public void onQuizCatalogChanged(List<Quiz> quizzes) throws RemoteException {
                SwingUtilities.invokeLater(() -> onChanged.accept(quizzes));
            }
        };

        UnicastRemoteObject.exportObject(l, 0);
        service.registerQuizCatalogListener(l);
        this.listener = l;
    }

    public void cancel() {
        if (listener != null) {
            try { service.unregisterQuizCatalogListener(listener); } catch (Exception ignored) {}
            try { UnicastRemoteObject.unexportObject(listener, true); } catch (Exception ignored) {}
            listener = null;
        }
    }
}
