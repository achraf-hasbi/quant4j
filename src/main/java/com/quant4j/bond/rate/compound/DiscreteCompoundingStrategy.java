package com.quant4j.bond.rate.compound;

import com.quant4j.bond.enumeration.CompoundingFrequency;

import java.util.Objects;

/**
 * Implements discrete compounding logic based on a specific frequency.
 * <p>
 * Under discrete compounding with frequency $m$, the discount factor is calculated as:
 * $DF = (1 + \frac{r}{m})^{-m \cdot t}$
 * </p>
 */
public class DiscreteCompoundingStrategy implements CompoundingStrategy {

    private final CompoundingFrequency frequency;

    /**
     * Constructs a strategy with the specified compounding frequency.
     *
     * @param frequency the compounding frequency (cannot be null)
     */
    public DiscreteCompoundingStrategy(CompoundingFrequency frequency) {
        this.frequency = Objects.requireNonNull(frequency, "Compounding frequency cannot be null");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Formula: $DF = (1 + \frac{r}{m})^{-(m \cdot t)}$
     * where $m$ is the number of periods per year.
     * </p>
     */
    @Override
    public double discountFactor(double rate, double time) {
        int m = frequency.getPeriodsPerYear();
        return Math.pow(1.0 + (rate / m), -(m * time));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Formula: $AF = (1 + \frac{r}{m})^{(m \cdot t)}$
     * </p>
     */
    @Override
    public double accumulationFactor(double rate, double time) {
        int m = frequency.getPeriodsPerYear();
        return Math.pow(1.0 + (rate / m), m * time);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Formula: $FV = P \cdot (1 + \frac{r}{m})^{(m \cdot t)}$
     * </p>
     */
    @Override
    public double futureValue(double principal, double rate, double time) {
        return principal * accumulationFactor(rate, time);
    }
}
