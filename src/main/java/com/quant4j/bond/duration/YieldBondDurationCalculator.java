package com.quant4j.bond.duration;

import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.CompoundingStrategy;
import com.quant4j.bond.rate.compound.ContinuousCompoundingStrategy;

import java.util.Map;
import java.util.Objects;

/**
 * Computes the Macaulay and modified duration of a bond using its yield
 * and a specified compounding convention.
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

    /**
     * {@inheritDoc}
     */
    @Override
    public double macaulayDuration(Bond bond, double yield, CompoundingStrategy yieldCompoundingStrategy, double price) {
        Objects.requireNonNull(bond, "Bond cannot be null");
        Objects.requireNonNull(yieldCompoundingStrategy, "Compounding strategy cannot be null");
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
    public double modifiedDuration(Bond bond, double yield, CompoundingStrategy yieldCompoundingStrategy, double price) {
        double macaulay = macaulayDuration(bond, yield, yieldCompoundingStrategy, price);

        // Continuous compounding: modified duration equals Macaulay duration
        if (yieldCompoundingStrategy instanceof ContinuousCompoundingStrategy) {
            return macaulay;
        }

        // Discrete compounding: D_mod = D_mac / (1 + y / m)
        int periodsPerYear = bond.couponFrequency().getPeriodsPerYear();
        return macaulay / (1.0 + yield / periodsPerYear);
    }
}
