# KHALID – MATH WHIZ

A web-based maths **game** built with **Java Servlets, JSP and JDBC (MySQL)**.
It is not a quiz with stored answers: the player chooses a module, sets the problem
themselves and types an answer, and the game **calculates the real result by calling Java methods**
(`MathService`) to decide whether marks are awarded. Scores are saved to MySQL and the top 10 are shown on a leaderboard.

## Project Objectives

- Write reusable Java methods for prime numbers, modulus and trigonometry.
- Apply conditional logic to score answers (10 marks total).
- Handle requests/responses with Servlets and render pages with JSP + JSTL.
- Use JDBC with `PreparedStatement` to save and retrieve scores.
- Show a sorted Top 10 leaderboard.

## Technologies Used

| Area | Technology |
|------|-----------|
| Language | Java 11+ |
| Web | Jakarta Servlet 6.0, JSP, JSTL 3.0 |
| Server | Apache Tomcat 10.1 (or Jetty 11 via Maven) |
| Database | MySQL 8 + JDBC (`mysql-connector-java`) |
| Build | Maven |
| Tests | JUnit 5 |
| IDE | IntelliJ IDEA |

## Software Requirements

- JDK 11 or newer
- IntelliJ IDEA (Community or Ultimate)
- MySQL Server + MySQL Workbench (or any client)
- Apache Tomcat **10.1** (Ultimate) – or just use the bundled Jetty plugin (Community)

## Project Structure

```
MathWhiz/
├── src/main/java/
│   ├── controller/GameServlet.java        # game flow (menu, modules, result)
│   ├── controller/LeaderboardServlet.java # top 10 page
│   ├── model/GameScore.java               # one database row
│   ├── model/GameState.java               # player's game in the session
│   ├── model/Attempt.java                 # one answered question
│   ├── model/MathModule.java              # PRIME / MOD / TRIG and their marks
│   ├── model/Difficulty.java              # EASY / MEDIUM / HARD
│   ├── service/MathService.java           # isPrime, calculateMod, calculateSin/Cos/Tan
│   ├── dao/ScoreDAO.java                  # JDBC: INSERT + top-10 SELECT
│   └── util/DBConnection.java             # JDBC connection settings
├── src/main/webapp/
│   ├── index.jsp  game.jsp  result.jsp  leaderboard.jsp
│   ├── css/style.css
│   └── WEB-INF/web.xml
├── src/test/java/service/MathServiceTest.java
├── database/math_whiz_db.sql
└── README.md
```

## Database Configuration

1. Open MySQL Workbench and run `database/math_whiz_db.sql`.
2. Open `src/main/java/util/DBConnection.java` and set your MySQL username and password:

```java
private static final String USER = "root";
private static final String PASSWORD = "yourpassword";
```

### Database creation SQL

```sql
CREATE DATABASE IF NOT EXISTS math_whiz_db;
USE math_whiz_db;

CREATE TABLE IF NOT EXISTS game_scores (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(100) NOT NULL,
    score       INT          NOT NULL,
    difficulty  VARCHAR(20)  NOT NULL DEFAULT 'EASY',
    played_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Leaderboard query used by the app:

```sql
SELECT * FROM game_scores ORDER BY score DESC, played_at ASC LIMIT 10;
```

## Steps to Run the Application

1. Open the `MathWhiz` folder in IntelliJ (**File → Open**) and let Maven import the project.
2. Run the SQL script and update `DBConnection.java` (see above).
3. Run it with **one** of these:

   **A. Tomcat 10.1 (IntelliJ Ultimate)** – *Run → Edit Configurations → + → Tomcat Server → Local*, choose your Tomcat 10 folder,
   *Deployment → + → Artifact → MathWhiz:war exploded*, set **Application context** to `/`, then press Run.

   **B. Jetty (any IntelliJ edition)** – Maven tool window → *Plugins → jetty → jetty:run*
   (or `mvn jetty:run` in a terminal).

4. Open <http://localhost:8080/>

> Tomcat 10 uses `jakarta.servlet.*`. This project is already written for it. For Tomcat 9 you would need the older `javax.servlet.*` packages.

## Game Rules and Scoring

The player enters a name, then picks a module, plays it, and returns to the menu. Each module can be played once.
When all three are done (or the player presses **Finish & Save Score**) the result is saved with name, score, difficulty (`EASY` / `MEDIUM` / `HARD`) and date/time.

| Module | How it is played | Marks |
|--------|------------------|-------|
| Prime Number | Player enters a number; the game picks a random value (1–20) and randomly **adds it or subtracts it** (one operation per question). The player answers **1** (prime) or **0** (not prime). | 3 |
| Modulus | Player enters two integers (divisor ≠ 0) and types the remainder of `a % b`. | 3 |
| Trigonometry | Player picks sin / cos / tan, enters a degree and types the value. An approximate answer is accepted (±0.05, or 3% for big values like tan 89°); a big difference is wrong. | 4 |
| **Total** | | **10** |

### Difficulty (Easy / Medium / Hard)

The player makes the question, so the game works out the difficulty from what the player typed:

| Module | Easy | Medium | Hard |
|--------|------|--------|------|
| Prime (the new number after +/- random) | up to 50 | 51 – 500 | above 500 |
| Modulus (biggest of a, b) | up to 50 | 51 – 500 | above 500 (a negative number = one level harder) |
| Trigonometry (angle) | 0, 30, 45, 60, 90 | the same special angles in other quadrants (120, 210, -30, 390 ...) | any other angle (37, 75, 12.5 ...) |

Each answered question is labelled with its level. The overall difficulty saved to the database and shown on the result page and
leaderboard is the average of the levels of the questions played (all Easy -> Easy, all Medium -> Medium, all Hard -> Hard).

### Correct answer after every question

After the player submits an answer, a card shows the question, the player's answer, the **correct answer** (calculated by Java),
the marks and a short "Why?" explanation. The result page lists the correct answer and explanation for every question.

Java methods in `MathService`:

```java
public static int    isPrime(int number)          // 1 = prime, 0 = not prime (numbers < 2 are not prime)
public static int    calculateMod(int a, int b)   // a % b, divisor 0 -> error
public static double calculateSin(double degree)  // degrees -> radians -> Math.sin
public static double calculateCos(double degree)
public static double calculateTan(double degree)  // undefined at 90°, 270° ... -> error
public static boolean isClose(double expected, double actual) // approximate check (ABS_TOLERANCE / REL_TOLERANCE)
public static Difficulty primeDifficulty(int n) / modDifficulty(int a, int b) / trigDifficulty(double degree)
```

## Testing

Run `mvn test`. The JUnit tests cover the assignment's cases:

| Test case | Expected |
|-----------|----------|
| Prime input 7 / 8 / 1 | 1 / 0 / 0 |
| 17 % 5 | 2 |
| sin 30° | 0.5 |
| cos 60° | 0.5 |
| tan 45° | 1.0 |
| Divisor 0 | Validation error |
| Difficulty of a question | Easy / Medium / Hard |
| Approximate trig answer (0.48 for sin 30°) | Correct; 0.6 is wrong |
| All answers correct | 10 marks |
| Wrong answers | No marks for those questions |

## Application Screenshots

### Welcome Page
The Welcome Page is the main entry point of the Math Whiz application, providing a simple and attractive interface for users.
It introduces the game and allows users to begin their mathematical quiz journey.

<img width="991" height="907" alt="image" src="https://github.com/user-attachments/assets/71695f83-77ae-4f79-a7cf-8b387623f721" />


### Game Page
The Game Page provides an interactive environment where users can solve mathematical questions.
It allows players to test their mathematical skills and improve their problem-solving abilities.

<img width="1040" height="880" alt="image" src="https://github.com/user-attachments/assets/f4379e64-56bf-49d8-950c-c0ad5342d455" />


### Result Page
The Result Page displays the player's performance after completing a game session.
It provides feedback on the game outcome and helps users evaluate their performance.

<img width="1047" height="887" alt="image" src="https://github.com/user-attachments/assets/2bd64811-0f76-4bf8-bb14-45bb097c4858" />

This screen presents additional details about the player's quiz results and overall performance.
It helps users understand their achievements after completing the mathematical challenges.

<img width="1012" height="897" alt="image" src="https://github.com/user-attachments/assets/ccffa905-6666-43fb-8f62-19f8ce4edd62" />

This view further demonstrates the result interface with performance-related information.
It gives users a clear overview of their progress in the game.

<img width="937" height="906" alt="image" src="https://github.com/user-attachments/assets/3347267a-1a28-44b2-a729-839fe3f8ecbe" />


### Leaderboard
The Leaderboard displays player rankings based on their game performance.
It encourages healthy competition by allowing users to compare their scores and achievements.

<img width="953" height="872" alt="image" src="https://github.com/user-attachments/assets/9b58f136-3d83-4a4a-aa2b-9a82913c010f" />


## GitHub Project Directory Link
The complete source code and project structure are available in the following GitHub repository directory:

https://github.com/khalidhasan4a/KHALID-MATH-WHIZ/tree/main/src/main

## Author

Khalid
