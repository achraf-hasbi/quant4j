package io.quant4j.rates.compounding;

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
     * Formula: $AF = e^{r \cdot t}$
     * </p>
     */
    @Override
    public double accumulationFactor(double rate, double time) {
        return Math.exp(rate * time);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Formula: $FV = P \cdot e^{r \cdot t}$
     * </p>
     */
    @Override
    public double futureValue(double principal, double rate, double time) {
        return principal * accumulationFactor(rate, time);
    }

    /**
     * {@inheritDoc}
     * <p>
     * For continuous compounding, $PV = FV * e^{-rt}$.
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
     * Formula: $r = -\frac{\ln(DF)}{t}$
     * </p>
     */
    @Override
    public double rateFromDiscountFactor(double discountFactor, double time) {
        return -Math.log(discountFactor) / time;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Under continuous compounding, modified duration equals Macaulay duration.
     * </p>
     */
    @Override
    public double adjustMacaulayToModified(double macaulay, double yield) {
        return macaulay;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Formula: $F = \frac{r_2 \cdot t_2 - r_1 \cdot t_1}{t_2 - t_1}$
     * </p>
     */
    @Override
    public double forwardRate(double r1, double t1, double r2, double t2) {
        if (t1 >= t2) {
            throw new IllegalArgumentException("t1 must be less than t2 to calculate a forward rate");
        }
        return (r2 * t2 - r1 * t1) / (t2 - t1);
    }
}