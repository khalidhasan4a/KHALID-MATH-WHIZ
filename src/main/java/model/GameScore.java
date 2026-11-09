package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** One row of the game_scores table. */
public class GameScore {
    private static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    private int id;
    private String playerName;
    private int score;
    private String difficulty;
    private LocalDateTime playedAt;

    public GameScore() {}

    public GameScore(String playerName, int score, String difficulty, LocalDateTime playedAt) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.playedAt = playedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public LocalDateTime getPlayedAt() { return playedAt; }
    public void setPlayedAt(LocalDateTime playedAt) { this.playedAt = playedAt; }

    /** "EASY" -> "Easy" (old rows such as "NORMAL" -> "Normal"). */
    public String getDifficultyLabel() {
        if (difficulty == null || difficulty.isEmpty()) {
            return "";
        }
        Difficulty d = Difficulty.fromStored(difficulty);
        if (d != null) {
            return d.getLabel();
        }
        return difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1).toLowerCase();
    }

    /** CSS class for the coloured badge. */
    public String getDifficultyCss() {
        Difficulty d = Difficulty.fromStored(difficulty);
        return d == null ? "easy" : d.name().toLowerCase();
    }

    /** Date/time ready for display in a JSP. */
    public String getPlayedAtFormatted() {
        return playedAt == null ? "" : playedAt.format(DISPLAY);
    }
}
