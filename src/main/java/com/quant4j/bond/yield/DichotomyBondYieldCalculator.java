package com.quant4j.bond.yield;

import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.price.YieldBondPricer;
import com.quant4j.bond.rate.compound.CompoundingStrategy;

import java.util.Objects;

/**
 * Computes the yield to maturity using the bisection (dichotomy) method.
 *
 * <p>Relies on the fact that price is a monotonically decreasing function of yield,
 * and therefore {@code f(y) = P(y) - targetPrice} changes sign exactly once.</p>
 */
public class DichotomyBondYieldCalculator implements BondYieldCalculator {

    private static final int DEFAULT_MAX_ITERATIONS = 200;
    private static final double DEFAULT_TOLERANCE = 1.0e-10;
    private static final double DEFAULT_LOWER_BOUND = -0.99;
    private static final double DEFAULT_UPPER_BOUND = 1.0;

    private final CompoundingStrategy compoundingStrategy;
    private final double lowerBound;
    private final double upperBound;
    private final double tolerance;
    private final int maxIterations;

    public DichotomyBondYieldCalculator(CompoundingStrategy compoundingStrategy) {
        this(compoundingStrategy, DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS);
    }

    public DichotomyBondYieldCalculator(CompoundingStrategy compoundingStrategy,
                                        double lowerBound,
                                        double upperBound,
                                        double tolerance,
                                        int maxIterations) {
        Objects.requireNonNull(compoundingStrategy, "Compounding strategy cannot be null");
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        this.compoundingStrategy = compoundingStrategy;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    @Override
    public double yield(Bond bond, double price) {
        Objects.requireNonNull(bond, "Bond cannot be null");
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        double a = lowerBound;
        double b = upperBound;
        double fa = new YieldBondPricer(a, compoundingStrategy).price(bond) - price;
        double fb = new YieldBondPricer(b, compoundingStrategy).price(bond) - price;

        if (fa * fb > 0) {
            throw new IllegalArgumentException(
                    "Root not bracketed in [" + a + ", " + b + "]. Consider widening the interval.");
        }

        for (int i = 0; i < maxIterations; i++) {
            double mid = 0.5 * (a + b);
            double fm = new YieldBondPricer(mid, compoundingStrategy).price(bond) - price;

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
