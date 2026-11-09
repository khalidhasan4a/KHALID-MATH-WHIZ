package dao;

import model.GameScore;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** All SQL for the game_scores table. */
public class ScoreDAO {

    private static final String INSERT_SQL =
            "INSERT INTO game_scores (player_name, score, difficulty, played_at) VALUES (?, ?, ?, ?)";

    private static final String TOP10_SQL =
            "SELECT id, player_name, score, difficulty, played_at FROM game_scores "
                    + "ORDER BY score DESC, played_at ASC LIMIT 10";

    public void save(GameScore score) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {
            ps.setString(1, score.getPlayerName());
            ps.setInt(2, score.getScore());
            ps.setString(3, score.getDifficulty());
            ps.setTimestamp(4, Timestamp.valueOf(score.getPlayedAt()));
            ps.executeUpdate();
        }
    }

    public List<GameScore> findTop10() throws SQLException {
        List<GameScore> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(TOP10_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GameScore s = new GameScore();
                s.setId(rs.getInt("id"));
                s.setPlayerName(rs.getString("player_name"));
                s.setScore(rs.getInt("score"));
                s.setDifficulty(rs.getString("difficulty"));
                Timestamp ts = rs.getTimestamp("played_at");
                s.setPlayedAt(ts == null ? null : ts.toLocalDateTime());
                list.add(s);
            }
        }
        return list;
    }
}
