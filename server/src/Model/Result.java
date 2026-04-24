package Model;

import java.io.Serializable;

public class Result implements Serializable {
    private int id;
    private String userId;
    private int quizId;
    private double score;

    public Result() {}

    public Result(int id, String userId, int quizId, double score) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}