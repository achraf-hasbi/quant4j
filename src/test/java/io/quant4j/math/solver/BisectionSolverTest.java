package io.quant4j.math.solver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BisectionSolverTest {

    private static final double TOLERANCE = 1.0e-8;

    @Test
    @DisplayName("Finds the positive root of x^2 - 4 = 0 (x = 2)")
    void testSolve_QuadraticRoot_FindsTwo() {
        RootSolver solver = new BisectionSolver(0.0, 5.0, 1.0e-10, 200);

        double root = solver.solve(x -> x * x - 4.0);

        assertEquals(2.0, root, TOLERANCE);
    }

    @Test
    @DisplayName("Finds a negative root of x + 3 = 0 (x = -3)")
    void testSolve_LinearFunction_FindsNegativeRoot() {
        RootSolver solver = new BisectionSolver(-5.0, 0.0, 1.0e-10, 200);

        double root = solver.solve(x -> x + 3.0);

        assertEquals(-3.0, root, TOLERANCE);
    }

    @Test
    @DisplayName("Finds the real root of x^3 - x - 2 = 0 (x ≈ 1.5214)")
    void testSolve_CubicRoot() {
        RootSolver solver = new BisectionSolver(1.0, 2.0, 1.0e-10, 200);

        double root = solver.solve(x -> x * x * x - x - 2.0);

        assertEquals(1.5214, root, 1.0e-4);
    }

    @Test
    @DisplayName("Default bounds [-0.99, 1.0] can resolve a root at x = 0.5")
    void testSolve_DefaultBounds_FindsRootInDefaultInterval() {
        RootSolver solver = new BisectionSolver();

        double root = solver.solve(x -> x - 0.5);

        assertEquals(0.5, root, TOLERANCE);
    }

    @Test
    @DisplayName("Null function throws NullPointerException")
    void testNullFunctionThrows() {
        RootSolver solver = new BisectionSolver();

        assertThrows(NullPointerException.class, () -> solver.solve(null));
    }

    @Test
    @DisplayName("Root not bracketed throws IllegalArgumentException")
    void testUnbracketedRootThrows() {
        // f(x) = x^2 + 1 is always positive — no root in any real interval
        RootSolver solver = new BisectionSolver(0.0, 5.0, 1.0e-10, 200);

        assertThrows(IllegalArgumentException.class, () -> solver.solve(x -> x * x + 1.0));
    }

    @Test
    @DisplayName("Lower bound >= upper bound throws IllegalArgumentException")
    void testInvalidBoundsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new BisectionSolver(5.0, 1.0, 1.0e-10, 200));
        assertThrows(IllegalArgumentException.class,
                () -> new BisectionSolver(3.0, 3.0, 1.0e-10, 200));
    }

    @Test
    @DisplayName("Zero tolerance throws IllegalArgumentException")
    void testZeroToleranceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new BisectionSolver(0.0, 5.0, 0.0, 200));
    }

    @Test
    @DisplayName("Negative tolerance throws IllegalArgumentException")
    void testNegativeToleranceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new BisectionSolver(0.0, 5.0, -1.0e-10, 200));
    }

    @Test
    @DisplayName("Zero maxIterations throws IllegalArgumentException")
    void testZeroMaxIterationsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new BisectionSolver(0.0, 5.0, 1.0e-10, 0));
    }

    @Test
    @DisplayName("Negative maxIterations throws IllegalArgumentException")
    void testNegativeMaxIterationsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new BisectionSolver(0.0, 5.0, 1.0e-10, -1));
    }
}
