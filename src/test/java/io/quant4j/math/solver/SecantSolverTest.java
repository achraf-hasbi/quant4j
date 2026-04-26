/*
 * Copyright 2025 Achraf Hasbi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quant4j.math.solver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecantSolverTest {

    private static final double TOLERANCE = 1.0e-8;

    @Test
    @DisplayName("Finds the positive root of x^2 - 4 = 0 (x = 2) with guesses 1.0 and 3.0")
    void testSolve_QuadraticRoot_FindsTwo() {
        RootSolver solver = new SecantSolver(1.0, 3.0, 1.0e-10, 100);

        double root = solver.solve(x -> x * x - 4.0);

        assertEquals(2.0, root, TOLERANCE);
    }

    @Test
    @DisplayName("Finds the real root of x^3 - x - 2 = 0 (x ≈ 1.5214)")
    void testSolve_CubicRoot() {
        RootSolver solver = new SecantSolver(1.0, 2.0, 1.0e-10, 100);

        double root = solver.solve(x -> x * x * x - x - 2.0);

        assertEquals(1.5214, root, 1.0e-4);
    }

    @Test
    @DisplayName("Finds the root of a linear function x - 7 = 0 (x = 7)")
    void testSolve_LinearFunction() {
        RootSolver solver = new SecantSolver(0.0, 1.0, 1.0e-10, 100);

        double root = solver.solve(x -> x - 7.0);

        assertEquals(7.0, root, TOLERANCE);
    }

    @Test
    @DisplayName("Default constructor uses guesses 0.01 / 0.10 and converges for a simple function")
    void testSolve_DefaultConstructor() {
        RootSolver solver = new SecantSolver();

        double root = solver.solve(x -> x - 0.05);

        assertEquals(0.05, root, TOLERANCE);
    }

    @Test
    @DisplayName("Null function throws NullPointerException")
    void testNullFunctionThrows() {
        RootSolver solver = new SecantSolver();

        assertThrows(NullPointerException.class, () -> solver.solve(null));
    }

    @Test
    @DisplayName("Identical initial guesses throw IllegalArgumentException")
    void testIdenticalGuessesThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new SecantSolver(0.05, 0.05, 1.0e-10, 100));
    }

    @Test
    @DisplayName("Function with equal values at both guesses throws ArithmeticException")
    void testZeroDenominatorThrows() {
        // f(x) = 1 has f(y0) == f(y1), denom = f1 - f0 = 0
        RootSolver solver = new SecantSolver(0.0, 1.0, 1.0e-10, 100);

        assertThrows(ArithmeticException.class, () -> solver.solve(x -> 1.0));
    }

    @Test
    @DisplayName("Zero tolerance throws IllegalArgumentException")
    void testZeroToleranceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new SecantSolver(0.01, 0.10, 0.0, 100));
    }

    @Test
    @DisplayName("Negative tolerance throws IllegalArgumentException")
    void testNegativeToleranceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new SecantSolver(0.01, 0.10, -1.0e-10, 100));
    }

    @Test
    @DisplayName("Zero maxIterations throws IllegalArgumentException")
    void testZeroMaxIterationsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new SecantSolver(0.01, 0.10, 1.0e-10, 0));
    }

    @Test
    @DisplayName("Negative maxIterations throws IllegalArgumentException")
    void testNegativeMaxIterationsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new SecantSolver(0.01, 0.10, 1.0e-10, -1));
    }
}
