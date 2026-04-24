package com.quant4j.bond.yield;

import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.price.YieldBondPricer;
import com.quant4j.bond.rate.compound.CompoundingStrategy;

import java.util.Objects;

/**
 * Computes the yield to maturity using the secant method.
 *
 * <p>Iteration: {@code y_{n+1} = y_n - f(y_n) * (y_n - y_{n-1}) / (f(y_n) - f(y_{n-1}))}</p>
 */
public class SecantBondYieldCalculator implements BondYieldCalculator {

    private static final int DEFAULT_MAX_ITERATIONS = 100;
    private static final double DEFAULT_TOLERANCE = 1.0e-10;
    private static final double DEFAULT_FIRST_GUESS = 0.01;
    private static final double DEFAULT_SECOND_GUESS = 0.10;

    private final CompoundingStrategy compoundingStrategy;
    private final double firstGuess;
    private final double secondGuess;
    private final double tolerance;
    private final int maxIterations;

    public SecantBondYieldCalculator(CompoundingStrategy compoundingStrategy) {
        this(compoundingStrategy, DEFAULT_FIRST_GUESS, DEFAULT_SECOND_GUESS, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS);
    }

    public SecantBondYieldCalculator(CompoundingStrategy compoundingStrategy,
                                     double firstGuess,
                                     double secondGuess,
                                     double tolerance,
                                     int maxIterations) {
        Objects.requireNonNull(compoundingStrategy, "Compounding strategy cannot be null");
        if (firstGuess == secondGuess) {
            throw new IllegalArgumentException("First and second guess must be different");
        }
        this.compoundingStrategy = compoundingStrategy;
        this.firstGuess = firstGuess;
        this.secondGuess = secondGuess;
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    @Override
    public double yield(Bond bond, double price) {
        Objects.requireNonNull(bond, "Bond cannot be null");
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        double y0 = firstGuess;
        double y1 = secondGuess;
        double f0 = new YieldBondPricer(y0, compoundingStrategy).price(bond) - price;
        double f1 = new YieldBondPricer(y1, compoundingStrategy).price(bond) - price;

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
            f1 = new YieldBondPricer(y1, compoundingStrategy).price(bond) - price;
        }
        throw new ArithmeticException("Secant method did not converge within " + maxIterations + " iterations");
    }
}
