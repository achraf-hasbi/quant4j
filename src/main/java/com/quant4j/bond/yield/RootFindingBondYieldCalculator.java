package com.quant4j.bond.yield;

import com.quant4j.bond.Bond;
import com.quant4j.bond.pricing.YieldBondPricer;
import com.quant4j.math.solver.RootSolver;
import com.quant4j.rates.compounding.CompoundingStrategy;

import java.util.Objects;

/**
 * Computes the yield to maturity (YTM) of a bond by finding the yield {@code y} that
 * makes the theoretical bond price equal to the observed market price.
 *
 * <p>The root-finding problem solved is: {@code P(y) - targetPrice = 0}, where
 * {@code P(y)} is the price computed by {@link YieldBondPricer}.
 * The actual search algorithm is injected via a {@link RootSolver} strategy, making
 * this class independent of any particular numerical method (bisection, Newton-Raphson,
 * secant, etc.).
 */
public class RootFindingBondYieldCalculator implements BondYieldCalculator {

    private final CompoundingStrategy compoundingStrategy;
    private final RootSolver rootSolver;

    /**
     * Constructs a yield calculator with the given compounding convention and root-finding strategy.
     *
     * @param compoundingStrategy the compounding convention used to price the bond at each trial yield;
     *                            must not be {@code null}
     * @param rootSolver          the numerical solver used to locate the yield; must not be {@code null}
     */
    public RootFindingBondYieldCalculator(CompoundingStrategy compoundingStrategy, RootSolver rootSolver) {
        Objects.requireNonNull(compoundingStrategy, "Compounding strategy cannot be null");
        Objects.requireNonNull(rootSolver, "Root solver cannot be null");
        this.compoundingStrategy = compoundingStrategy;
        this.rootSolver = rootSolver;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if {@code price} is not positive
     * @throws NullPointerException     if {@code bond} is null
     * @throws ArithmeticException      if the solver fails to converge
     */
    @Override
    public double yield(Bond bond, double price) {
        Objects.requireNonNull(bond, "Bond cannot be null");
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        return rootSolver.solve(y -> new YieldBondPricer(y, compoundingStrategy).price(bond) - price);
    }
}
