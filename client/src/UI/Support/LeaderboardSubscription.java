package UI.Support;

import Model.Result;
import Service.LeaderboardListener;
import Service.QuizService;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.function.Consumer;

public class LeaderboardSubscription {

    private final QuizService service;
    private LeaderboardListener listener;
    private int quizId = -1;

    public LeaderboardSubscription(QuizService service) {
        this.service = service;
    }

    public void watch(int newQuizId, Consumer<List<Result>> onUpdate) throws Exception {
        cancel();

        LeaderboardListener l = new LeaderboardListener() {
            @Override public void updateLeaderboard(List<Result> lb) throws RemoteException {
                SwingUtilities.invokeLater(() -> onUpdate.accept(lb));
            }
        };
        UnicastRemoteObject.exportObject(l, 0);
        service.registerListener(newQuizId, l);

        this.listener = l;
        this.quizId = newQuizId;

        onUpdate.accept(service.getLeaderboard(newQuizId));
    }

    public void cancel() {
        if (listener != null) {
            try { service.unregisterListener(quizId, listener); } catch (Exception ignored) {}
            try { UnicastRemoteObject.unexportObject(listener, true); } catch (Exception ignored) {}
            listener = null;
            quizId = -1;
        }
    }
}