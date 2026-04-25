package com.quant4j.math.solver;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

/**
 * Root-finding via the secant method.
 *
 * <p>The secant method approximates the derivative using the slope of the chord through the
 * two most recent iterates, avoiding the need for an explicit derivative.
 * Two distinct initial guesses are required.
 */
public class SecantSolver implements RootSolver {

    private static final int DEFAULT_MAX_ITERATIONS = 100;
    private static final double DEFAULT_TOLERANCE = 1.0e-10;
    private static final double DEFAULT_FIRST_GUESS = 0.01;
    private static final double DEFAULT_SECOND_GUESS = 0.10;

    private final double firstGuess;
    private final double secondGuess;
    private final double tolerance;
    private final int maxIterations;

    /** Creates a solver with initial guesses {@code 0.01}, {@code 0.10} and tolerance {@code 1e-10}. */
    public SecantSolver() {
        this(DEFAULT_FIRST_GUESS, DEFAULT_SECOND_GUESS, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a solver with custom initial guesses and convergence parameters.
     *
     * @param firstGuess    first initial guess; must differ from {@code secondGuess}
     * @param secondGuess   second initial guess
     * @param tolerance     convergence threshold on the residual
     * @param maxIterations maximum iterations before throwing
     */
    public SecantSolver(double firstGuess, double secondGuess, double tolerance, int maxIterations) {
        if (firstGuess == secondGuess) {
            throw new IllegalArgumentException("First and second guesses must be different");
        }
        if (tolerance <= 0) {
            throw new IllegalArgumentException("Tolerance must be positive");
        }
        if (maxIterations < 1) {
            throw new IllegalArgumentException("Max iterations must be at least 1");
        }
        this.firstGuess = firstGuess;
        this.secondGuess = secondGuess;
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    @Override
    public double solve(DoubleUnaryOperator f) {
        Objects.requireNonNull(f, "Function cannot be null");

        double y0 = firstGuess;
        double y1 = secondGuess;
        double f0 = f.applyAsDouble(y0);
        double f1 = f.applyAsDouble(y1);

        for (int i = 0; i < maxIterations; i++) {
            if (Math.abs(f1) < tolerance) {
                return y1;
            }
            double denom = f1 - f0;
            if (denom == 0.0) {
                throw new ArithmeticException("Zero denominator encountered in secant method");
            }
            double y2 = y1 - f1 * (y1 - y0) / denom;
            y0 = y1;
            f0 = f1;
            y1 = y2;
            f1 = f.applyAsDouble(y1);
        }
        throw new ArithmeticException("Secant method did not converge within " + maxIterations + " iterations");
    }
}
