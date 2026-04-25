package com.quant4j.math.solver;

import java.util.function.DoubleUnaryOperator;

/**
 * Strategy for finding a root {@code x} of a univariate real function {@code f},
 * i.e. {@code f(x) ≈ 0}.
 *
 * <p>Each implementation encapsulates its own convergence parameters (tolerance,
 * max iterations, initial guesses / bracketing interval) via its constructor.
 */
public interface RootSolver {

    /**
     * Finds {@code x} such that {@code f(x) ≈ 0}.
     *
     * @param f the function whose root is sought; must not be {@code null}
     * @return the approximate root
     * @throws ArithmeticException      if the algorithm fails to converge
     * @throws IllegalArgumentException if the bracketing interval has no sign change (bisection)
     */
    double solve(DoubleUnaryOperator f);
}
