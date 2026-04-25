# Java RMI Quiz System

This project is a real-time, multi-user quiz system built with Java RMI and Swing. It features a Teacher (server) and Student (client) UI, a PostgreSQL backend, and real-time leaderboard updates.

---

## Project Structure

- **client/**: Student UI, client-side logic, and test clients
- **server/**: Teacher UI, server-side logic, and RMI server
- **lib/**: External libraries (e.g., JDBC drivers)

---

## Class & Method Documentation

### Common Model Classes

- **Model/Quiz.java**
  - `Quiz`: Represents a quiz (id, title, createdBy)
    - `getId() / setId(int)`: Get/set quiz ID
    - `getTitle() / setTitle(String)`: Get/set quiz title
    - `getCreatedBy() / setCreatedBy(String)`: Get/set creator

- **Model/Question.java**
  - `Question`: Represents a quiz question
    - `getId() / setId(int)`: Get/set question ID
    - `getQuizId() / setQuizId(int)`: Get/set parent quiz
    - `getQuestionText() / setQuestionText(String)`: Get/set text
    - `getOptions() / setOptions(List<String>)`: Get/set answer options
    - `getCorrectAnswer() / setCorrectAnswer(String)`: Get/set correct answer

- **Model/Result.java**
  - `Result`: Represents a user's quiz result
    - `getId() / setId(int)`: Get/set result ID
    - `getUserId() / setUserId(String)`: Get/set user
    - `getQuizId() / setQuizId(int)`: Get/set quiz
    - `getScore() / setScore(double)`: Get/set score (%)

---

### DAO (Data Access) Classes

- **DAO/QuizDAO.java**
  - Interface for quiz DB operations
    - `createQuiz(Quiz)`: Insert new quiz
    - `getAllQuizzes()`: List all quizzes

- **DAO/QuizDAOImpl.java**
  - Implements QuizDAO using JDBC

- **DAO/QuestionDAO.java**
  - Interface for question DB operations
    - `addQuestion(Question)`: Insert question
    - `getQuestionsByQuizIdWithAnswers(int)`: List questions (with answers)
    - `getQuestionsForClient(int)`: List questions (no answers)

- **DAO/QuestionDAOImpl.java**
  - Implements QuestionDAO using JDBC

- **DAO/ResultDAO.java**
  - Interface for result DB operations
    - `saveResult(Result)`: Insert result
    - `getLeaderboard(int)`: List results for quiz, sorted by score

- **DAO/ResultDAOImpl.java**
  - Implements ResultDAO using JDBC

- **DAO/ParticipantDAO.java**
  - Manages quiz participation
    - `joinQuiz(int, String)`: Add user to quiz
    - `markCompleted(int, String)`: Mark quiz as completed for user

---

### Database

- **Database/Config.java**: DB connection settings
- **Database/DBConnection.java**: Creates JDBC connections
  - `makeConnection()`: Returns a new DB connection

---

### Service Layer (RMI)

- **Service/QuizService.java**
  - RMI interface for all quiz operations
    - `createQuiz(Quiz)`: Create quiz
    - `addQuestion(Question)`: Add question
    - `getQuizzes()`: List quizzes
    - `getQuestions(int)`: List questions for quiz
    - `joinQuiz(int, String)`: Student joins quiz
    - `submitAnswers(int, String, Map<Integer, String>)`: Submit answers, returns score
    - `getLeaderboard(int)`: Get leaderboard for quiz
    - `registerListener(int, LeaderboardListener)`: Register for real-time leaderboard
    - `unregisterListener(int, LeaderboardListener)`: Unregister listener

- **Service/QuizServiceImpl.java**
  - Implements QuizService, handles all business logic and RMI callbacks

- **Service/LeaderboardListener.java**
  - RMI callback interface for leaderboard updates
    - `updateLeaderboard(List<Result>)`: Called when leaderboard changes

- **Service/QuizCatalogListener.java**
  - RMI callback for quiz catalog updates
    - `updateQuizCatalog(List<Quiz>)`: Called when quiz list changes

---

### UI Layer

#### Client (Student)
- **UI/StudentUI.java**: Main student window
- **UI/Panel/QuizListPanel.java**: Shows available quizzes
- **UI/Panel/QuestionsPanel.java**: Shows questions, handles answer/submit
- **UI/Panel/LeaderboardPanel.java**: Shows live leaderboard
- **UI/Support/AppStyle.java**: Centralized UI theming
- **UI/Support/AsyncTask.java**: Utility for background tasks
- **UI/Support/LeaderboardSubscription.java**: Handles leaderboard updates
- **UI/Support/QuizCatalogSubscription.java**: Handles quiz list updates

#### Server (Teacher)
- **UI/TeacherUI.java**: Main teacher window
- **UI/Panel/QuizListPanel.java**: Shows all quizzes
- **UI/Panel/CreateQuizPanel.java**: Create new quiz
- **UI/Panel/AddQuestionPanel.java**: Add questions to quiz
- **UI/Panel/LeaderboardPanel.java**: Shows leaderboard
- **UI/Support/AppStyle.java**: Centralized UI theming
- **UI/Support/AsyncTask.java**: Utility for background tasks
- **UI/Support/LeaderboardSubscription.java**: Handles leaderboard updates
- **UI/Support/QuizCatalogSubscription.java**: Handles quiz list updates

---

### Main Entry Points

- **server/src/Main.java**: Starts the RMI server
- **server/src/TeacherMain.java**: Starts the Teacher UI
- **client/src/Main.java**: Test client for quiz operations
- **client/src/TeacherTestClient.java**: Test client for teacher operations

---

## How Each Method Works

- **DAO methods**: Use JDBC to query/update the PostgreSQL database.
- **Service methods**: Expose all quiz operations over RMI, call DAO methods, and manage real-time listeners.
- **UI methods**: Build and update Swing components, handle user actions, and call service methods.

---

## Real-Time Features

- **LeaderboardListener**: Clients register for leaderboard updates; server pushes changes in real time.
- **QuizCatalogListener**: Clients register for quiz list updates; server pushes changes in real time.

---

## Theming

- All UI panels use `AppStyle` for consistent colors, fonts, and borders.

---

## Notes

- All RMI methods throw `RemoteException`.
- All DB methods throw `SQLException`.
- See each class for further inline documentation.
