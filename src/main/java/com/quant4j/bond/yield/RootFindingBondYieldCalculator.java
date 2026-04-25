package com.quant4j.bond.yield;

import com.quant4j.bond.Bond;
import com.quant4j.math.solver.RootSolver;
import com.quant4j.rates.compounding.CompoundingStrategy;

import java.util.Map;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

/**
 * Computes the yield to maturity (YTM) of a bond by finding the yield {@code y} that
 * makes the theoretical bond price equal to the observed market price.
 *
 * <p>The root-finding problem solved is: {@code P(y) - targetPrice = 0}, where
 * {@code P(y)} is the present value of all bond cash flows discounted at {@code y}.
 * The actual search algorithm is injected via a {@link RootSolver} strategy, making
 * this class independent of any particular numerical method (bisection, Newton-Raphson,
 * secant, etc.).
 *
 * <p>Bond cash flows are captured once before the solver loop to avoid repeated
 * recomputation across iterations.
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
     */
    @Override
    public double yieldToMaturity(Bond bond, double price) {
        Objects.requireNonNull(bond, "Bond cannot be null");
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        // Capture cash flows once — avoids recomputing the bond schedule on every solver iteration.
        Map<Double, Double> cashflows = bond.getCashflows();
        DoubleUnaryOperator pricer = y -> {
            double p = 0.0;
            for (Map.Entry<Double, Double> entry : cashflows.entrySet()) {
                p += entry.getValue() * compoundingStrategy.discountFactor(y, entry.getKey());
            }
            return p - price;
        };

        return rootSolver.solve(pricer);
    }
}
