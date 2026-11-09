package service;

import model.Difficulty;
import model.MathModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathServiceTest {

    // ---- Prime
    @Test void primeSeven()        { assertEquals(1, MathService.isPrime(7)); }
    @Test void primeEight()        { assertEquals(0, MathService.isPrime(8)); }
    @Test void primeOne()          { assertEquals(0, MathService.isPrime(1)); }
    @Test void primeZeroNegative() {
        assertEquals(0, MathService.isPrime(0));
        assertEquals(0, MathService.isPrime(-7));
    }
    @Test void primeSmallAndLarge() {
        assertEquals(1, MathService.isPrime(2));
        assertEquals(1, MathService.isPrime(3));
        assertEquals(0, MathService.isPrime(9));
        assertEquals(1, MathService.isPrime(97));
        assertEquals(1, MathService.isPrime(2147483647));   // largest int prime
    }

    // ---- Modulus
    @Test void modSeventeenFive()  { assertEquals(2, MathService.calculateMod(17, 5)); }
    @Test void modNegatives() {
        assertEquals(-2, MathService.calculateMod(-17, 5));
        assertEquals(2, MathService.calculateMod(17, -5));
        assertEquals(0, MathService.calculateMod(0, 5));
    }
    @Test void modZeroDivisor() {
        assertThrows(IllegalArgumentException.class, () -> MathService.calculateMod(10, 0));
    }

    // ---- Trigonometry
    @Test void sin30()  { assertEquals(0.5, MathService.calculateSin(30), 1e-9); }
    @Test void cos60()  { assertEquals(0.5, MathService.calculateCos(60), 1e-9); }
    @Test void tan45()  { assertEquals(1.0, MathService.calculateTan(45), 1e-9); }
    @Test void tanUndefined() {
        assertThrows(ArithmeticException.class, () -> MathService.calculateTan(90));
        assertThrows(ArithmeticException.class, () -> MathService.calculateTan(270));
        assertThrows(ArithmeticException.class, () -> MathService.calculateTan(-90));
    }
    @Test void trigDispatcher() {
        assertEquals(0.5, MathService.calculateTrig("SIN", 30), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> MathService.calculateTrig("sec", 30));
    }

    // ---- Tolerance (approximate answers are accepted, big differences are not)
    @Test void toleranceCheck() {
        assertTrue(MathService.isClose(0.8660254, 0.87));
        assertTrue(MathService.isClose(0.5, 0.5));
        assertTrue(MathService.isClose(0.5, 0.48));      // a little less
        assertTrue(MathService.isClose(0.5, 0.53));      // a little more
        assertFalse(MathService.isClose(0.5, 0.6));      // too far
        assertFalse(MathService.isClose(0.5, 1.0));
    }
    @Test void toleranceForBigValues() {
        double tan89 = MathService.calculateTan(89);     // 57.29
        assertTrue(MathService.isClose(tan89, 57));
        assertTrue(MathService.isClose(tan89, 58));
        assertFalse(MathService.isClose(tan89, 50));
    }

    // ---- Difficulty (decided by the question the player makes)
    @Test void primeDifficulty() {
        assertEquals(Difficulty.EASY, MathService.primeDifficulty(13));
        assertEquals(Difficulty.EASY, MathService.primeDifficulty(50));
        assertEquals(Difficulty.MEDIUM, MathService.primeDifficulty(51));
        assertEquals(Difficulty.MEDIUM, MathService.primeDifficulty(500));
        assertEquals(Difficulty.HARD, MathService.primeDifficulty(501));
        assertEquals(Difficulty.HARD, MathService.primeDifficulty(99991));
    }
    @Test void modDifficulty() {
        assertEquals(Difficulty.EASY, MathService.modDifficulty(17, 5));
        assertEquals(Difficulty.MEDIUM, MathService.modDifficulty(345, 17));
        assertEquals(Difficulty.HARD, MathService.modDifficulty(123456, 789));
        assertEquals(Difficulty.MEDIUM, MathService.modDifficulty(-17, 5));   // negative: one level harder
        assertEquals(Difficulty.HARD, MathService.modDifficulty(-345, 17));
    }
    @Test void trigDifficulty() {
        for (double d : new double[]{0, 30, 45, 60, 90}) {
            assertEquals(Difficulty.EASY, MathService.trigDifficulty(d), "angle " + d);
        }
        for (double d : new double[]{120, 135, 210, 300, 330, -30, 390, 180, 360}) {
            assertEquals(Difficulty.MEDIUM, MathService.trigDifficulty(d), "angle " + d);
        }
        for (double d : new double[]{37, 75, 12.5, 100, 89}) {
            assertEquals(Difficulty.HARD, MathService.trigDifficulty(d), "angle " + d);
        }
    }
    @Test void averageDifficulty() {
        assertEquals(Difficulty.EASY, Difficulty.average(java.util.List.of(Difficulty.EASY, Difficulty.EASY)));
        assertEquals(Difficulty.MEDIUM, Difficulty.average(
                java.util.List.of(Difficulty.MEDIUM, Difficulty.MEDIUM, Difficulty.MEDIUM)));
        assertEquals(Difficulty.HARD, Difficulty.average(
                java.util.List.of(Difficulty.HARD, Difficulty.HARD, Difficulty.MEDIUM)));
        assertEquals(Difficulty.MEDIUM, Difficulty.average(
                java.util.List.of(Difficulty.EASY, Difficulty.HARD)));         // 2.0
    }

    // ---- Explanations shown after the answer
    @Test void explanations() {
        assertTrue(MathService.explainPrime(12).contains("divisible by 2"));
        assertTrue(MathService.explainPrime(91).contains("7 x 13"));
        assertTrue(MathService.explainPrime(13).contains("IS prime"));
        assertTrue(MathService.explainPrime(1).contains("NOT prime"));
        assertTrue(MathService.explainMod(17, 5).contains("17 = 5 x 3 + 2"));
        assertTrue(MathService.explainMod(-17, 5).contains("(-17) = 5 x (-3) + (-2)"));
        assertTrue(MathService.explainTrig("sin", 30, 0.5).contains("0.5000"));
    }

    // ---- Marks
    @Test void marksAddUpToTen() { assertEquals(10, MathModule.totalMarks()); }

    @Test void plainFormatting() {
        assertEquals("30", MathService.plain(30.0));
        assertEquals("0.5", MathService.plain(0.5));
        assertEquals("0", MathService.plain(0.0));
    }
}
