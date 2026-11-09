package service;

import model.Difficulty;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * All the maths of the game. Every answer is calculated here by real Java code -
 * nothing is stored or hard-coded.
 */
public final class MathService {

    /**
     * Trigonometry answers do not have to be exact. An answer is accepted when it is within
     * ABS_TOLERANCE of the real value, or within REL_TOLERANCE (3%) of it for big values such as tan 89 deg.
     * Change these two numbers to make the check stricter or more relaxed.
     */
    public static final double ABS_TOLERANCE = 0.05;
    public static final double REL_TOLERANCE = 0.03;

    private static final double EPSILON = 1e-9;

    private MathService() {}

    /** Returns 1 if the number is prime, otherwise 0. Numbers below 2 are not prime. */
    public static int isPrime(int number) {
        if (number < 2) {
            return 0;
        }
        if (number < 4) {
            return 1;               // 2 and 3
        }
        if (number % 2 == 0) {
            return 0;
        }
        for (long i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return 0;
            }
        }
        return 1;
    }

    /** Returns the remainder of a / b using %. The divisor must not be zero. */
    public static int calculateMod(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Divisor must not be zero.");
        }
        return a % b;
    }

    public static double calculateSin(double degree) {
        return Math.sin(Math.toRadians(degree));
    }

    public static double calculateCos(double degree) {
        return Math.cos(Math.toRadians(degree));
    }

    /** tan is undefined at 90, 270, ... degrees (cos = 0). */
    public static double calculateTan(double degree) {
        if (isTanUndefined(degree)) {
            throw new ArithmeticException(
                    "tan(" + plain(degree) + "°) is undefined. Please choose a different angle.");
        }
        return Math.tan(Math.toRadians(degree));
    }

    public static boolean isTanUndefined(double degree) {
        double reduced = ((degree % 180) + 180) % 180;
        return Math.abs(reduced - 90) < EPSILON;
    }

    /** Calculates sin, cos or tan of an angle given in degrees. */
    public static double calculateTrig(String ratio, double degree) {
        switch (ratio == null ? "" : ratio.toLowerCase()) {
            case "sin":
                return calculateSin(degree);
            case "cos":
                return calculateCos(degree);
            case "tan":
                return calculateTan(degree);
            default:
                throw new IllegalArgumentException("Ratio must be sin, cos or tan.");
        }
    }

    /** How far from the real value an answer may be and still count as correct. */
    public static double allowedError(double expected) {
        return Math.max(ABS_TOLERANCE, REL_TOLERANCE * Math.abs(expected));
    }

    /** True when the player's approximate answer is close enough to the real value. */
    public static boolean isClose(double expected, double actual) {
        return Math.abs(expected - actual) <= allowedError(expected) + 1e-12;
    }

    // ------------------------------------------------------------ difficulty
    // The player makes the question, so the difficulty is worked out from what they typed in.

    /** Prime: the smaller the number the player has to test, the easier it is. */
    public static Difficulty primeDifficulty(int number) {
        long n = Math.abs((long) number);
        if (n <= 50) {
            return Difficulty.EASY;
        }
        if (n <= 500) {
            return Difficulty.MEDIUM;
        }
        return Difficulty.HARD;
    }

    /** Modulus: judged by the biggest number; a negative number makes it one level harder. */
    public static Difficulty modDifficulty(int a, int b) {
        long biggest = Math.max(Math.abs((long) a), Math.abs((long) b));
        Difficulty d = biggest <= 50 ? Difficulty.EASY
                : biggest <= 500 ? Difficulty.MEDIUM : Difficulty.HARD;
        if (a < 0 || b < 0) {
            d = d.harder();
        }
        return d;
    }

    /**
     * Trigonometry:
     *  EASY   - 0, 30, 45, 60 or 90 degrees
     *  MEDIUM - the same special angles but in another quadrant / negative / above 360 (e.g. 120, 210, -30)
     *  HARD   - any other angle (e.g. 37, 75, 12.5)
     */
    public static Difficulty trigDifficulty(double degree) {
        if (Math.abs(degree - Math.rint(degree)) > EPSILON) {
            return Difficulty.HARD;                       // not a whole number of degrees
        }
        long d = Math.round(degree);
        long reduced = ((d % 360) + 360) % 360;
        long reference;                                   // angle measured from the x-axis
        if (reduced <= 90) {
            reference = reduced;
        } else if (reduced <= 180) {
            reference = 180 - reduced;
        } else if (reduced <= 270) {
            reference = reduced - 180;
        } else {
            reference = 360 - reduced;
        }
        boolean special = reference == 0 || reference == 30 || reference == 45
                || reference == 60 || reference == 90;
        if (!special) {
            return Difficulty.HARD;
        }
        return (d >= 0 && d <= 90) ? Difficulty.EASY : Difficulty.MEDIUM;
    }

    // ---------------------------------------------------------- explanations
    // Shown to the player after answering, so they can learn the right answer.

    /** Smallest divisor above 1 of a number that is 2 or more (the number itself if it is prime). */
    public static long smallestDivisor(long number) {
        if (number % 2 == 0) {
            return 2;
        }
        for (long i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return i;
            }
        }
        return number;
    }

    public static String explainPrime(int number) {
        if (number < 2) {
            return number + " is less than 2, and prime numbers start from 2, so it is NOT prime.";
        }
        if (isPrime(number) == 1) {
            return number + " can only be divided by 1 and itself, so it IS prime.";
        }
        long d = smallestDivisor(number);
        return number + " is divisible by " + d + " (" + number + " = " + d + " x " + (number / d)
                + "), so it is NOT prime.";
    }

    public static String explainMod(int a, int b) {
        int q = a / b;
        int r = calculateMod(a, b);
        String text = bracket(a) + " = " + bracket(b) + " x " + bracket(q) + " + " + bracket(r)
                + ", so the remainder of " + a + " % " + b + " is " + r + ".";
        if (r < 0) {
            text += " (Java gives the remainder the same sign as the first number.)";
        }
        return text;
    }

    public static String explainTrig(String ratio, double degree, double expected) {
        double allowed = allowedError(expected);
        return ratio + "(" + plain(degree) + "\u00B0) = " + fmt(expected)
                + ". Any answer between " + fmt(expected - allowed) + " and " + fmt(expected + allowed)
                + " is accepted.";
    }

    /** 4 decimal places for showing a trig value. */
    public static String fmt(double value) {
        return String.format(Locale.ROOT, "%.4f", value);
    }

    private static String bracket(int n) {
        return n < 0 ? "(" + n + ")" : String.valueOf(n);
    }

    /** 30.0 -> "30", 0.5 -> "0.5" (for showing numbers to the player). */
    public static String plain(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
