package com.quant4j.math.solver;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

/**
 * Root-finding via the bisection (binary search) method.
 *
 * <p>The algorithm repeatedly halves the bracketing interval {@code [a, b]} until the
 * residual {@code |f(mid)|} or the interval width falls below {@code tolerance}.
 * A sign change must exist in the interval: {@code f(a) * f(b) < 0}.
 */
public class BisectionSolver implements RootSolver {

    private static final int DEFAULT_MAX_ITERATIONS = 200;
    private static final double DEFAULT_TOLERANCE = 1.0e-10;
    private static final double DEFAULT_LOWER_BOUND = -0.99;
    private static final double DEFAULT_UPPER_BOUND = 1.0;

    private final double lowerBound;
    private final double upperBound;
    private final double tolerance;
    private final int maxIterations;

    /** Creates a solver with default bounds {@code [-0.99, 1.0]} and tolerance {@code 1e-10}. */
    public BisectionSolver() {
        this(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a solver with custom bracketing interval and convergence parameters.
     *
     * @param lowerBound    left endpoint; must be strictly less than {@code upperBound}
     * @param upperBound    right endpoint
     * @param tolerance     convergence threshold on residual and interval half-width
     * @param maxIterations maximum bisection steps before throwing
     */
    public BisectionSolver(double lowerBound, double upperBound, double tolerance, int maxIterations) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    @Override
    public double solve(DoubleUnaryOperator f) {
        Objects.requireNonNull(f, "Function cannot be null");

        double a = lowerBound;
        double b = upperBound;
        double fa = f.applyAsDouble(a);
        double fb = f.applyAsDouble(b);

        if (fa * fb > 0) {
            throw new IllegalArgumentException(
                    "Root not bracketed in [" + a + ", " + b + "]. Consider widening the interval.");
        }

        for (int i = 0; i < maxIterations; i++) {
            double mid = 0.5 * (a + b);
            double fm = f.applyAsDouble(mid);

            if (Math.abs(fm) < tolerance || 0.5 * (b - a) < tolerance) {
                return mid;
            }

            if (fa * fm < 0) {
                b = mid;
            } else {
                a = mid;
                fa = fm;
            }
        }
        throw new ArithmeticException("Bisection did not converge within " + maxIterations + " iterations");
    }
}
