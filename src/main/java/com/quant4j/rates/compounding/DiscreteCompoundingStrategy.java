package com.quant4j.rates.compounding;

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
     * @param periodsPerYear The number of compounding periods per year; must be at least 1.
     * @throws IllegalArgumentException if {@code periodsPerYear} is less than 1.
     */
    public DiscreteCompoundingStrategy(int periodsPerYear) {
        if (periodsPerYear < 1) {
            throw new IllegalArgumentException("Periods per year must be at least 1");
        }
        this.periodsPerYear = periodsPerYear;
    }

    public int getPeriodsPerYear() {
        return periodsPerYear;
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

    /**
     * {@inheritDoc}
     * <p>
     * For discrete compounding, $PV = FV / (1 + r)^t$.
     * This is equivalent to multiplying the future amount by the discount factor.
     * </p>
     */

    @Override
    public double presentValue(double futureAmount, double rate, double time) {
        return futureAmount * discountFactor(rate, time);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Formula: $r = m \cdot ((DF)^{-\frac{1}{m \cdot t}} - 1)$
     * </p>
     */
    @Override
    public double rateFromDiscountFactor(double discountFactor, double time) {
        double exponent = -1.0 / (periodsPerYear * time);
        return periodsPerYear * (Math.pow(discountFactor, exponent) - 1);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Formula derived from equating accumulation factors:
     * $(1 + \frac{r_1}{m})^{m \cdot t_1} \cdot (1 + \frac{F}{m})^{m \cdot (t_2 - t_1)} = (1 + \frac{r_2}{m})^{m \cdot t_2}$
     * </p>
     */
    @Override
    public double forwardRate(double r1, double t1, double r2, double t2) {
        if (t1 >= t2) {
            throw new IllegalArgumentException("t1 must be less than t2 to calculate a forward rate");
        }

        double af1 = accumulationFactor(r1, t1);
        double af2 = accumulationFactor(r2, t2);

        // (1 + F/m)^(m * (t2 - t1)) = af2 / af1
        double forwardAF = af2 / af1;
        double timeDelta = t2 - t1;
        
        // We can reuse the rate deduction logic for the forward period
        // Since AF = 1/DF, we can pass (1/forwardAF) as DF
        return rateFromDiscountFactor(1.0 / forwardAF, timeDelta);
    }
}
