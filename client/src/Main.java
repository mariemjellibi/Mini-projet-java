
import Model.Quiz;
import Model.Question;
import Model.Result;
import Service.QuizService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            QuizService service = (QuizService) registry.lookup("QuizService");
            System.out.println("✅ Connected to server.\n");

            // ========== TEACHER: Create Quiz ==========
            System.out.println("===== TEACHER: Create Quiz =====");
            Quiz quiz = new Quiz();
            quiz.setTitle("Geography Quiz");
            quiz.setCreatedBy("teacher1");
            int quizId = service.createQuiz(quiz);
            System.out.println("Created quiz ID = " + quizId);

            // ========== TEACHER: Add Questions ==========
            System.out.println("\n===== TEACHER: Add Questions =====");
            Question q1 = new Question();
            q1.setQuizId(quizId);
            q1.setQuestionText("What is the capital of France?");
            q1.setOptions(Arrays.asList("Berlin", "Madrid", "Paris", "Rome"));
            q1.setCorrectAnswer("Paris");
            service.addQuestion(q1);

            Question q2 = new Question();
            q2.setQuizId(quizId);
            q2.setQuestionText("Which planet is known as the Red Planet?");
            q2.setOptions(Arrays.asList("Earth", "Mars", "Jupiter", "Venus"));
            q2.setCorrectAnswer("Mars");
            service.addQuestion(q2);

            System.out.println("Questions added.");

            System.out.println("\n===== STUDENT 1: Join Quiz =====");
            String studentId = "student123";
            boolean joined = service.joinQuiz(quizId, studentId);
            System.out.println("Join: " + (joined ? "success" : "failed"));

            // Fetch questions to get actual IDs
            List<Question> questions = service.getQuestions(quizId);
            System.out.println("\n===== STUDENT 1: Fetch Questions =====");
            for (Question q : questions) {
                System.out.println("Q" + q.getId() + ": " + q.getQuestionText());
                System.out.println("Options: " + q.getOptions());
            }
            Map<Integer, String> answers = new HashMap<>();
            if (questions.size() >= 2) {
                answers.put(questions.get(0).getId(), "Paris");   // correct
                answers.put(questions.get(1).getId(), "Venus");   // wrong
            }

            System.out.println("\n===== STUDENT 1: Submit Answers =====");
            double score = service.submitAnswers(quizId, studentId, answers);
            System.out.println("Score = " + score + "%");

            // ========== STUDENT 2 ==========
            System.out.println("\n===== STUDENT 2: Join Quiz =====");
            String student2 = "student456";
            service.joinQuiz(quizId, student2);

            List<Question> questions2 = service.getQuestions(quizId);
            Map<Integer, String> answers2 = new HashMap<>();
            if (questions2.size() >= 2) {
                answers2.put(questions2.get(0).getId(), "Paris");   // correct
                answers2.put(questions2.get(1).getId(), "Mars");    // correct
            }

            double score2 = service.submitAnswers(quizId, student2, answers2);
            System.out.println("Student2 Score = " + score2 + "%");

            // ========== LEADERBOARD ==========
            System.out.println("\n===== UPDATED LEADERBOARD =====");
            List<Result> leaderboard = service.getLeaderboard(quizId);
            for (Result r : leaderboard) {
                System.out.println("User: " + r.getUserId() + " – Score: " + r.getScore() + "%");
            }

            System.out.println("\n✅ All tests completed successfully.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}