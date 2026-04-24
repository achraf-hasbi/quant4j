package com.quant4j.bond.yield;

import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.price.YieldBondPricer;
import com.quant4j.bond.rate.compound.CompoundingStrategy;

import java.util.Objects;

/**
 * Computes the yield to maturity using the Newton-Raphson method.
 *
 * <p>Iteration: {@code y_{n+1} = y_n - (P(y_n) - targetPrice) / P'(y_n)}</p>
 */
public class NewtonRaphsonBondYieldCalculator implements BondYieldCalculator {

    private static final int DEFAULT_MAX_ITERATIONS = 100;
    private static final double DEFAULT_TOLERANCE = 1.0e-10;
    private static final double DEFAULT_INITIAL_GUESS = 0.05;

    private final CompoundingStrategy compoundingStrategy;
    private final double initialGuess;
    private final double tolerance;
    private final int maxIterations;

    public NewtonRaphsonBondYieldCalculator(CompoundingStrategy compoundingStrategy) {
        this(compoundingStrategy, DEFAULT_INITIAL_GUESS, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS);
    }

    public NewtonRaphsonBondYieldCalculator(CompoundingStrategy compoundingStrategy,
                                            double initialGuess,
                                            double tolerance,
                                            int maxIterations) {
        Objects.requireNonNull(compoundingStrategy, "Compounding strategy cannot be null");
        this.compoundingStrategy = compoundingStrategy;
        this.initialGuess = initialGuess;
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    @Override
    public double yield(Bond bond, double price) {
        Objects.requireNonNull(bond, "Bond cannot be null");
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        double yield = initialGuess;
        for (int i = 0; i < maxIterations; i++) {
            double f = new YieldBondPricer(yield, compoundingStrategy).price(bond) - price;
            if (Math.abs(f) < tolerance) {
                return yield;
            }
            double h = 1.0e-6;
            double pUp = new YieldBondPricer(yield + h, compoundingStrategy).price(bond);
            double pDn = new YieldBondPricer(yield - h, compoundingStrategy).price(bond);
            double fPrime = (pUp - pDn) / (2.0 * h);

            if (fPrime == 0.0) {
                throw new ArithmeticException("Zero derivative encountered in Newton-Raphson");
            }
            yield = yield - f / fPrime;
        }
        throw new ArithmeticException("Newton-Raphson did not converge within " + maxIterations + " iterations");
    }
}
