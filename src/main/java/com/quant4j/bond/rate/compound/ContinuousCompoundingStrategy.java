package com.quant4j.bond.rate.compound;

/**
 * Implements continuous compounding logic.
 * <p>
 * Under continuous compounding, the discount factor is calculated as $e^{-r \cdot t}$.
 * </p>
 */
public class ContinuousCompoundingStrategy implements CompoundingStrategy {

    /**
     * {@inheritDoc}
     * <p>
     * Formula: $DF = e^{-r \cdot t}$
     * </p>
     */
    @Override
    public double discountFactor(double rate, double time) {
        return Math.exp(-rate * time);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Formula: $FV = P \cdot e^{r \cdot t}$
     * </p>
     */
    @Override
    public double futureValue(double principal, double rate, double time) {
        return principal * Math.exp(rate * time);
    }
}
