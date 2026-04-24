package com.quant4j.bond.duration;

import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.CompoundingStrategy;
import com.quant4j.bond.rate.compound.DiscreteCompoundingStrategy;

import java.util.Map;
import java.util.Objects;

/**
 * Computes the Macaulay duration, modified duration and DV01 of a bond
 * using a yield and compounding convention supplied at construction time.
 *
 * <p>Macaulay duration formula:</p>
 * <pre>
 *   D_mac = (1 / P) * sum[ t * CF(t) * DF(y, t) ]
 * </pre>
 *
 * <p>Modified duration adjustment depends on the compounding convention:</p>
 * <ul>
 *   <li>Discrete (m periods/year): {@code D_mod = D_mac / (1 + y/m)}</li>
 *   <li>Continuous:                {@code D_mod = D_mac}</li>
 * </ul>
 */
public class YieldBondDurationCalculator implements BondDurationCalculator {

    private final double yield;
    private final CompoundingStrategy yieldCompoundingStrategy;

    /**
     * Constructs a duration calculator for a specific yield and compounding convention.
     *
     * @param yield                    The yield to maturity (decimal).
     * @param yieldCompoundingStrategy The compounding convention for the yield.
     */
    public YieldBondDurationCalculator(double yield, CompoundingStrategy yieldCompoundingStrategy) {
        Objects.requireNonNull(yieldCompoundingStrategy, "Compounding strategy cannot be null");
        this.yield = yield;
        this.yieldCompoundingStrategy = yieldCompoundingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double macaulayDuration(Bond bond, double price) {
        Objects.requireNonNull(bond, "Bond cannot be null");
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        double weightedSum = 0.0;

        for (Map.Entry<Double, Double> entry : bond.getCashflows().entrySet()) {
            double t = entry.getKey();
            double cashflow = entry.getValue();
            double pv = cashflow * yieldCompoundingStrategy.discountFactor(yield, t);
            weightedSum += t * pv;
        }

        return weightedSum / price;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double modifiedDuration(Bond bond, double price) {
        double macaulay = macaulayDuration(bond, price);

        if (yieldCompoundingStrategy instanceof DiscreteCompoundingStrategy discrete) {
            return macaulay / (1.0 + yield / discrete.getPeriodsPerYear());
        }

        return macaulay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double dv01(Bond bond, double price) {
        return modifiedDuration(bond, price) * price * 0.0001;
    }
}
