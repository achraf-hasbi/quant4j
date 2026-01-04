package com.quant4j.bond.rate.compound;

/**
 * Implements discrete compounding logic based on a specific frequency.
 * <p>
 * Under discrete compounding with frequency $m$, the discount factor is calculated as:
 * $DF = (1 + \frac{r}{m})^{-m \cdot t}$
 * </p>
 */
public class DiscreteCompoundingStrategy implements CompoundingStrategy {

    private final int periodsPerYear;

    /**
     * Constructs a strategy with the specified compounding frequency.
     *
     * @param periodsPerYear The number of periods per year.
     */
    public DiscreteCompoundingStrategy(int periodsPerYear) {
        this.periodsPerYear = periodsPerYear;
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
        return Math.pow(1.0 + (rate / periodsPerYear), -(periodsPerYear * time));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Formula: $AF = (1 + \frac{r}{m})^{(m \cdot t)}$
     * </p>
     */
    @Override
    public double accumulationFactor(double rate, double time) {
        return Math.pow(1.0 + (rate / periodsPerYear), periodsPerYear * time);
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
