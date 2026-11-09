package model;

/** The three playable modules and the marks each one is worth (total = 10). */
public enum MathModule {
    PRIME("Prime Number", 3),
    MOD("Modulus", 3),
    TRIG("Trigonometry", 4);

    private final String label;
    private final int marks;

    MathModule(String label, int marks) {
        this.label = label;
        this.marks = marks;
    }

    public String getLabel() { return label; }

    public int getMarks() { return marks; }

    public static int totalMarks() {
        int total = 0;
        for (MathModule m : values()) {
            total += m.marks;
        }
        return total;
    }
}
