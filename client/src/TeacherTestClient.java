
import Model.Quiz;
import Model.Question;
import Service.QuizService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class TeacherTestClient {
    public static void main(String[] args) {
        try {
            // Connect to server
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            QuizService service = (QuizService) registry.lookup("QuizService");
            System.out.println("✅ Teacher client connected.\n");

            // --- 1. Create a quiz ---
            Quiz quiz = new Quiz();
            quiz.setTitle("Java Basics");
            quiz.setCreatedBy("teacher1");
            int quizId = service.createQuiz(quiz);
            System.out.println("Created quiz with ID: " + quizId);

            // --- 2. Add questions ---
            // Question 1
            Question q1 = new Question();
            q1.setQuizId(quizId);
            q1.setQuestionText("What is the default value of a boolean variable in Java?");
            q1.setOptions(Arrays.asList("true", "false", "0", "null"));
            q1.setCorrectAnswer("false");
            service.addQuestion(q1);
            System.out.println("Added Q1: " + q1.getQuestionText());

            // Question 2
            Question q2 = new Question();
            q2.setQuizId(quizId);
            q2.setQuestionText("Which keyword is used to inherit a class?");
            q2.setOptions(Arrays.asList("extends", "implements", "inherit", "super"));
            q2.setCorrectAnswer("extends");
            service.addQuestion(q2);
            System.out.println("Added Q2: " + q2.getQuestionText());

            // Question 3
            Question q3 = new Question();
            q3.setQuizId(quizId);
            q3.setQuestionText("What does JVM stand for?");
            q3.setOptions(Arrays.asList(
                    "Java Virtual Machine",
                    "Java Variable Manager",
                    "Java Visual Module",
                    "Just Virtual Machine"
            ));
            q3.setCorrectAnswer("Java Virtual Machine");
            service.addQuestion(q3);
            System.out.println("Added Q3: " + q3.getQuestionText());

            // --- 3. Verify: fetch all quizzes ---
            System.out.println("\n--- All Quizzes ---");
            List<Quiz> quizzes = service.getQuizzes();
            for (Quiz q : quizzes) {
                System.out.println("ID: " + q.getId() + ", Title: " + q.getTitle() + ", Created by: " + q.getCreatedBy());
            }

            // --- 4. Verify: fetch questions for our quiz (teacher view – no answers shown) ---
            System.out.println("\n--- Questions for quiz " + quizId + " ---");
            List<Question> questions = service.getQuestions(quizId);
            for (Question q : questions) {
                System.out.println("Q" + q.getId() + ": " + q.getQuestionText());
                System.out.println("Options: " + q.getOptions());
                // correct answer is null, but that's fine – it's hidden from client
            }

            System.out.println("\n✅ Teacher test completed successfully.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}