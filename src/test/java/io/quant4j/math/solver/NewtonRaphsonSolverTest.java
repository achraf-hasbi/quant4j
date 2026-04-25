package io.quant4j.math.solver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NewtonRaphsonSolverTest {

    private static final double TOLERANCE = 1.0e-8;

    @Test
    @DisplayName("Finds the positive root of x^2 - 4 = 0 (x = 2) from guess 3.0")
    void testSolve_QuadraticRoot_FindsTwo() {
        RootSolver solver = new NewtonRaphsonSolver(3.0, 1.0e-10, 100);

        double root = solver.solve(x -> x * x - 4.0);

        assertEquals(2.0, root, TOLERANCE);
    }

    @Test
    @DisplayName("Finds the real root of x^3 - x - 2 = 0 (x ≈ 1.5214) from guess 1.0")
    void testSolve_CubicRoot() {
        RootSolver solver = new NewtonRaphsonSolver(1.0, 1.0e-10, 100);

        double root = solver.solve(x -> x * x * x - x - 2.0);

        assertEquals(1.5214, root, 1.0e-4);
    }

    @Test
    @DisplayName("Finds the root of a linear function x - 7 = 0 (x = 7)")
    void testSolve_LinearFunction() {
        RootSolver solver = new NewtonRaphsonSolver(0.0, 1.0e-10, 100);

        double root = solver.solve(x -> x - 7.0);

        assertEquals(7.0, root, TOLERANCE);
    }

    @Test
    @DisplayName("Default constructor uses initial guess 0.05 and converges for a simple function")
    void testSolve_DefaultConstructor() {
        RootSolver solver = new NewtonRaphsonSolver();

        double root = solver.solve(x -> x - 0.05);

        assertEquals(0.05, root, TOLERANCE);
    }

    @Test
    @DisplayName("Null function throws NullPointerException")
    void testNullFunctionThrows() {
        RootSolver solver = new NewtonRaphsonSolver();

        assertThrows(NullPointerException.class, () -> solver.solve(null));
    }

    @Test
    @DisplayName("Constant function (zero derivative) throws ArithmeticException")
    void testZeroDerivativeThrows() {
        // f(x) = 5 has f'(x) = 0 everywhere
        RootSolver solver = new NewtonRaphsonSolver(1.0, 1.0e-10, 100);

        assertThrows(ArithmeticException.class, () -> solver.solve(x -> 5.0));
    }
}
