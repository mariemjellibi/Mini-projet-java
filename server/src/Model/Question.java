package Model;


import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private int id;
    private int quizId;
    private String questionText;
    private List<String> options;       // sent to client
    private String correctAnswer;       // not sent to client

    public Question() {}

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}