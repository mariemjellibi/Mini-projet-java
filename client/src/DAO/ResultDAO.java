package DAO;

import Model.Result;
import java.sql.SQLException;
import java.util.List;

public interface ResultDAO {

    void saveResult(Result result) throws SQLException;
    List<Result> getLeaderboard(int quizId) throws SQLException;
    boolean hasParticipated(int quizId, String userId) throws SQLException;
}