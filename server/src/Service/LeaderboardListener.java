package Service;


import Model.Result;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LeaderboardListener extends Remote {
    void updateLeaderboard(List<Result> leaderboard) throws RemoteException;
}