package model;

import java.io.Serializable;

/** One answered question: what was asked, what the player said, and what Java calculated. */
public class Attempt implements Serializable {
    private static final long serialVersionUID = 2L;

    private final MathModule module;
    private final Difficulty difficulty;
    private final String question;
    private final String yourAnswer;
    private final String correctAnswer;
    private final String explanation;
    private final boolean correct;
    private final int marks;

    public Attempt(MathModule module, Difficulty difficulty, String question, String yourAnswer,
                   String correctAnswer, String explanation, boolean correct) {
        this.module = module;
        this.difficulty = difficulty;
        this.question = question;
        this.yourAnswer = yourAnswer;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.correct = correct;
        this.marks = correct ? module.getMarks() : 0;
    }

    public MathModule getModule() { return module; }
    public String getModuleLabel() { return module.getLabel(); }
    public Difficulty getDifficulty() { return difficulty; }
    public String getDifficultyLabel() { return difficulty.getLabel(); }
    public String getDifficultyCss() { return difficulty.name().toLowerCase(); }
    public String getQuestion() { return question; }
    public String getYourAnswer() { return yourAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }
    public boolean isCorrect() { return correct; }
    public int getMarks() { return marks; }
    public int getMaxMarks() { return module.getMarks(); }
}
