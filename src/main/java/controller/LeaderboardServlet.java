package controller;

import dao.ScoreDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** GET /leaderboard -> shows the top 10 scores. */
@WebServlet("/leaderboard")
public class LeaderboardServlet extends HttpServlet {

    private final ScoreDAO scoreDAO = new ScoreDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("scores", scoreDAO.findTop10());
        } catch (SQLException e) {
            log("Could not load leaderboard", e);
            request.setAttribute("error", "Could not load the leaderboard: " + e.getMessage());
        }
        request.getRequestDispatcher("/leaderboard.jsp").forward(request, response);
    }
}
