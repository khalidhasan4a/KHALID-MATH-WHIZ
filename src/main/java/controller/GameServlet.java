package controller;

import dao.ScoreDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Attempt;
import model.GameScore;
import model.GameState;
import model.MathModule;
import service.MathService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Controller for the whole game.
 *
 * GET  /game                  -> module menu
 * GET  /game?module=prime|mod|trig -> the chosen module's screen
 * GET  /game?view=result      -> final result page
 * POST /game  action=start | primeGenerate | primeAnswer | modAnswer | trigAnswer | finish
 */
@WebServlet("/game")
public class GameServlet extends HttpServlet {

    private static final String STATE = "state";
    private static final String FLASH_MESSAGE = "flashMessage";
    private static final String FLASH_OK = "flashOk";
    private static final String LAST_ATTEMPT = "lastAttempt";
    private static final int MAX_NAME_LENGTH = 100;
    private static final long NUMBER_LIMIT = 1_000_000L;

    private final ScoreDAO scoreDAO = new ScoreDAO();

    // ------------------------------------------------------------------ GET

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        GameState state = currentState(req);
        if (state == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        boolean resultView = "result".equals(req.getParameter("view"));
        if (resultView) {
            if (!state.isFinished()) {
                resp.sendRedirect(req.getContextPath() + "/game");
                return;
            }
            showFlash(req);
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
            return;
        }
        if (state.isFinished()) {
            resp.sendRedirect(req.getContextPath() + "/game?view=result");
            return;
        }

        showFlash(req);
        String module = req.getParameter("module");
        String screen = "menu";
        if ("prime".equals(module) && !state.isPrimeDone()) {
            screen = state.getPrimeNumber() != null ? "primeAnswer" : "primeInput";
        } else if ("mod".equals(module) && !state.isModDone()) {
            screen = "mod";
        } else if ("trig".equals(module) && !state.isTrigDone()) {
            screen = "trig";
        } else if (module != null && !module.isEmpty()) {
            req.setAttribute("message", "Please choose a module that is not completed yet.");
            req.setAttribute("messageOk", false);
        }
        show(req, resp, screen);
    }

    // ----------------------------------------------------------------- POST

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action") == null ? "" : req.getParameter("action");

        if ("start".equals(action)) {
            startGame(req, resp);
            return;
        }

        GameState state = currentState(req);
        if (state == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        if (state.isFinished() && !"finish".equals(action)) {
            resp.sendRedirect(req.getContextPath() + "/game?view=result");
            return;
        }

        switch (action) {
            case "primeGenerate":
                primeGenerate(req, resp, state);
                break;
            case "primeAnswer":
                primeAnswer(req, resp, state);
                break;
            case "modAnswer":
                modAnswer(req, resp, state);
                break;
            case "trigAnswer":
                trigAnswer(req, resp, state);
                break;
            case "finish":
                finishRequested(req, resp, state);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/game");
        }
    }

    // ------------------------------------------------------------- actions

    private void startGame(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("playerName");
        name = name == null ? "" : name.trim();
        if (name.isEmpty()) {
            req.setAttribute("error", "Please enter your name to start.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            req.setAttribute("error", "Name must be at most " + MAX_NAME_LENGTH + " characters.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }
        HttpSession session = req.getSession(true);
        session.removeAttribute(FLASH_MESSAGE);
        session.removeAttribute(FLASH_OK);
        session.removeAttribute(LAST_ATTEMPT);
        session.setAttribute(STATE, new GameState(name));
        resp.sendRedirect(req.getContextPath() + "/game");
    }

    /** Prime step 1: player types a number, the game randomly adds or subtracts a random value. */
    private void primeGenerate(HttpServletRequest req, HttpServletResponse resp, GameState state)
            throws ServletException, IOException {
        if (state.isPrimeDone()) {
            flash(req, false, "The Prime module is already completed.");
            resp.sendRedirect(req.getContextPath() + "/game");
            return;
        }
        if (state.getPrimeNumber() != null) {            // question already generated
            resp.sendRedirect(req.getContextPath() + "/game?module=prime");
            return;
        }
        Integer base = parseInt(req.getParameter("number"));
        if (base == null || Math.abs((long) base) > NUMBER_LIMIT) {
            showError(req, resp, "primeInput",
                    "Enter a whole number between -1,000,000 and 1,000,000.");
            return;
        }
        // The game picks a random value (1..20) and randomly ADDS it to or SUBTRACTS it from the
        // player's number - one operation per question. When subtracting, the random value is kept
        // small enough that the new number does not drop below 2.
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int maxSubtract = (int) Math.min(20L, (long) base - 2);
        boolean subtract = maxSubtract >= 1 && random.nextBoolean();
        int extra = subtract ? random.nextInt(1, maxSubtract + 1) : random.nextInt(1, 21);
        state.startPrime(base, extra, subtract);
        resp.sendRedirect(req.getContextPath() + "/game?module=prime");
    }

    /** Prime step 2: player answers 1 (prime) or 0 (not prime); Java decides. */
    private void primeAnswer(HttpServletRequest req, HttpServletResponse resp, GameState state)
            throws ServletException, IOException {
        if (state.isPrimeDone() || state.getPrimeNumber() == null) {
            resp.sendRedirect(req.getContextPath() + "/game?module=prime");
            return;
        }
        String choice = req.getParameter("choice");
        if (!"1".equals(choice) && !"0".equals(choice)) {
            showError(req, resp, "primeAnswer", "Choose 1 (Prime) or 0 (Not Prime).");
            return;
        }
        int number = state.getPrimeNumber();
        int given = Integer.parseInt(choice);
        int expected = MathService.isPrime(number);          // <-- calculated by Java

        String question = "Is " + number + " prime? (" + state.getPrimeBase() + " entered "
                + state.getPrimeOperator() + " " + state.getPrimeExtra() + " random)";
        Attempt attempt = new Attempt(MathModule.PRIME, MathService.primeDifficulty(number), question,
                primeLabel(given), primeLabel(expected), MathService.explainPrime(number), given == expected);
        state.clearPrime();
        complete(req, resp, state, attempt);
    }

    /** Mod: player gives two integers and the remainder. */
    private void modAnswer(HttpServletRequest req, HttpServletResponse resp, GameState state)
            throws ServletException, IOException {
        if (state.isModDone()) {
            flash(req, false, "The Modulus module is already completed.");
            resp.sendRedirect(req.getContextPath() + "/game");
            return;
        }
        Integer a = parseInt(req.getParameter("a"));
        Integer b = parseInt(req.getParameter("b"));
        Integer answer = parseInt(req.getParameter("answer"));
        if (a == null || b == null || answer == null) {
            showError(req, resp, "mod", "Enter whole numbers (integers only) in all three boxes.");
            return;
        }
        if (b == 0) {
            showError(req, resp, "mod", "Validation error: the divisor (second number) must not be zero.");
            return;
        }
        int expected = MathService.calculateMod(a, b);       // <-- calculated by Java
        Attempt attempt = new Attempt(MathModule.MOD, MathService.modDifficulty(a, b), a + " % " + b,
                String.valueOf(answer), String.valueOf(expected),
                MathService.explainMod(a, b), answer == expected);
        complete(req, resp, state, attempt);
    }

    /** Trig: player picks sin/cos/tan, gives a degree and types the value. */
    private void trigAnswer(HttpServletRequest req, HttpServletResponse resp, GameState state)
            throws ServletException, IOException {
        if (state.isTrigDone()) {
            flash(req, false, "The Trigonometry module is already completed.");
            resp.sendRedirect(req.getContextPath() + "/game");
            return;
        }
        String ratio = req.getParameter("ratio") == null ? "" : req.getParameter("ratio").toLowerCase(Locale.ROOT);
        if (!ratio.equals("sin") && !ratio.equals("cos") && !ratio.equals("tan")) {
            showError(req, resp, "trig", "Select sin, cos or tan.");
            return;
        }
        Double degree = parseDouble(req.getParameter("degree"));
        Double answer = parseDouble(req.getParameter("answer"));
        if (degree == null || answer == null) {
            showError(req, resp, "trig", "Enter valid numbers for the degree and your answer.");
            return;
        }
        double expected;
        try {
            expected = MathService.calculateTrig(ratio, degree);  // <-- calculated by Java
        } catch (ArithmeticException | IllegalArgumentException e) {
            showError(req, resp, "trig", e.getMessage());
            return;
        }
        Attempt attempt = new Attempt(MathModule.TRIG, MathService.trigDifficulty(degree),
                ratio + "(" + MathService.plain(degree) + "°)",
                MathService.plain(answer),
                MathService.fmt(expected),
                MathService.explainTrig(ratio, degree, expected),
                MathService.isClose(expected, answer));
        complete(req, resp, state, attempt);
    }

    /** "Finish & Save Score" button (also used to retry a failed save). */
    private void finishRequested(HttpServletRequest req, HttpServletResponse resp, GameState state)
            throws IOException {
        if (state.getAttemptCount() == 0) {
            flash(req, false, "Play at least one module before finishing.");
            resp.sendRedirect(req.getContextPath() + "/game");
            return;
        }
        finishGame(state);
        resp.sendRedirect(req.getContextPath() + "/game?view=result");
    }

    // ------------------------------------------------------------- helpers

    private void complete(HttpServletRequest req, HttpServletResponse resp,
                          GameState state, Attempt attempt) throws IOException {
        state.record(attempt);
        String message = attempt.isCorrect()
                ? "Correct! You earned " + attempt.getMarks() + " marks."
                : "Not correct. The right answer was " + attempt.getCorrectAnswer() + ". 0 marks.";
        flash(req, attempt.isCorrect(), message);
        req.getSession().setAttribute(LAST_ATTEMPT, attempt);   // shown once as the "correct answer" card

        if (state.isAllDone()) {
            finishGame(state);
            resp.sendRedirect(req.getContextPath() + "/game?view=result");
        } else {
            resp.sendRedirect(req.getContextPath() + "/game");
        }
    }

    /** Saves name, score, difficulty and date/time to the database. */
    private void finishGame(GameState state) {
        state.setFinished(true);
        if (state.isSaved()) {
            return;
        }
        try {
            scoreDAO.save(new GameScore(state.getPlayerName(), state.getScore(),
                    state.getDifficulty().name(), LocalDateTime.now()));
            state.setSaved(true);
            state.setSaveError(null);
        } catch (SQLException e) {
            log("Could not save score", e);
            state.setSaveError(e.getMessage());
        }
    }

    private GameState currentState(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (GameState) session.getAttribute(STATE);
    }

    private void flash(HttpServletRequest req, boolean ok, String message) {
        HttpSession session = req.getSession();
        session.setAttribute(FLASH_MESSAGE, message);
        session.setAttribute(FLASH_OK, ok);
    }

    private void showFlash(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        Object last = session.getAttribute(LAST_ATTEMPT);
        if (last != null) {
            req.setAttribute("lastAttempt", last);
            session.removeAttribute(LAST_ATTEMPT);
        }
        if (session.getAttribute(FLASH_MESSAGE) == null) {
            return;
        }
        req.setAttribute("message", session.getAttribute(FLASH_MESSAGE));
        req.setAttribute("messageOk", session.getAttribute(FLASH_OK));
        session.removeAttribute(FLASH_MESSAGE);
        session.removeAttribute(FLASH_OK);
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, String screen)
            throws ServletException, IOException {
        req.setAttribute("screen", screen);
        req.setAttribute("primeMarks", MathModule.PRIME.getMarks());
        req.setAttribute("modMarks", MathModule.MOD.getMarks());
        req.setAttribute("trigMarks", MathModule.TRIG.getMarks());
        req.setAttribute("totalMarks", MathModule.totalMarks());
        req.getRequestDispatcher("/game.jsp").forward(req, resp);
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String screen, String message)
            throws ServletException, IOException {
        req.setAttribute("message", message);
        req.setAttribute("messageOk", false);
        show(req, resp, screen);
    }

    private static String primeLabel(int value) {
        return value == 1 ? "1 (Prime)" : "0 (Not Prime)";
    }

    private static Integer parseInt(String text) {
        if (text == null) {
            return null;
        }
        try {
            return Integer.valueOf(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double parseDouble(String text) {
        if (text == null) {
            return null;
        }
        try {
            double value = Double.parseDouble(text.trim());
            return Double.isFinite(value) ? value : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
