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

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

/**
 * Root-finding via Newton-Raphson iteration.
 *
 * <p>The derivative is approximated numerically using a symmetric finite difference:
 * {@code f'(x) ≈ (f(x+h) - f(x-h)) / (2h)} with {@code h = 1e-6}.
 * Starting from an initial guess, the algorithm iterates
 * {@code x ← x - f(x) / f'(x)} until the residual falls below {@code tolerance}.
 */
public class NewtonRaphsonSolver implements RootSolver {

    private static final int DEFAULT_MAX_ITERATIONS = 100;
    private static final double DEFAULT_TOLERANCE = 1.0e-10;
    private static final double DEFAULT_INITIAL_GUESS = 0.05;
    private static final double FINITE_DIFF_STEP = 1.0e-6;
    private static final double EPSILON = 1e-15;

    private final double initialGuess;
    private final double tolerance;
    private final int maxIterations;

    /** Creates a solver with initial guess {@code 0.05} and tolerance {@code 1e-10}. */
    public NewtonRaphsonSolver() {
        this(DEFAULT_INITIAL_GUESS, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a solver with custom convergence parameters.
     *
     * @param initialGuess  starting point for iteration
     * @param tolerance     convergence threshold on the residual
     * @param maxIterations maximum iterations before throwing
     */
    public NewtonRaphsonSolver(double initialGuess, double tolerance, int maxIterations) {
        if (tolerance <= 0) {
            throw new IllegalArgumentException("Tolerance must be positive");
        }
        if (maxIterations < 1) {
            throw new IllegalArgumentException("Max iterations must be at least 1");
        }
        this.initialGuess = initialGuess;
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    @Override
    public double solve(DoubleUnaryOperator f) {
        Objects.requireNonNull(f, "Function cannot be null");

        double x = initialGuess;

        for (int i = 0; i < maxIterations; i++) {
            double fx = f.applyAsDouble(x);
            if (Math.abs(fx) < tolerance) {
                return x;
            }

            double fPrime = (f.applyAsDouble(x + FINITE_DIFF_STEP) - f.applyAsDouble(x - FINITE_DIFF_STEP))
                    / (2.0 * FINITE_DIFF_STEP);

            if (Math.abs(fPrime) < EPSILON) {
                throw new ArithmeticException("Zero derivative encountered in Newton-Raphson");
            }

            x = x - fx / fPrime;
        }
        throw new ArithmeticException("Newton-Raphson did not converge within " + maxIterations + " iterations");
    }
}
