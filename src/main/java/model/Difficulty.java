package model;

import java.util.List;

/** How hard the question the player made is. Stored in the database as EASY / MEDIUM / HARD. */
public enum Difficulty {
    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3);

    private final String label;
    private final int level;

    Difficulty(String label, int level) {
        this.label = label;
        this.level = level;
    }

    public String getLabel() { return label; }

    public int getLevel() { return level; }

    /** One step harder (Hard stays Hard). */
    public Difficulty harder() {
        return this == EASY ? MEDIUM : HARD;
    }

    /** Average of several difficulties, rounded to the nearest level (1.5 rounds up). */
    public static Difficulty average(List<Difficulty> list) {
        if (list.isEmpty()) {
            return EASY;
        }
        int sum = 0;
        for (Difficulty d : list) {
            sum += d.level;
        }
        int rounded = (int) Math.floor((double) sum / list.size() + 0.5);
        return values()[Math.max(1, Math.min(3, rounded)) - 1];
    }

    /** Reads a stored value (also copes with old rows such as "NORMAL"); returns null if unknown. */
    public static Difficulty fromStored(String text) {
        if (text == null) {
            return null;
        }
        for (Difficulty d : values()) {
            if (d.name().equalsIgnoreCase(text.trim())) {
                return d;
            }
        }
        return null;
    }
}
