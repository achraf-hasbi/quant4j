package com.quant4j.bond.rate;

import com.quant4j.bond.enumeration.CompoundingFrequency;

import java.util.Objects;

/**
 * Utility class for converting interest rates between different compounding conventions.
 */
public class RateConverter {

    private RateConverter() {
    }

    /**
     * Converts a discrete rate to a continuously compounded rate.
     * <p>
     * Formula: $R_c = m \cdot \ln(1 + \frac{R_d}{m})$
     * </p>
     *
     * @param discreteRate the discrete interest rate (decimal)
     * @param frequency    the frequency of the discrete rate
     * @return the equivalent continuous rate
     */
    public static double discreteToContinuous(double discreteRate, CompoundingFrequency frequency) {
        Objects.requireNonNull(frequency, "Frequency cannot be null");
        int m = frequency.getPeriodsPerYear();
        return m * Math.log(1.0 + (discreteRate / m));
    }

    /**
     * Converts a continuously compounded rate to a discrete rate.
     * <p>
     * Formula: $R_d = m \cdot (e^{\frac{R_c}{m}} - 1)$
     * </p>
     *
     * @param continuousRate the continuous interest rate (decimal)
     * @param frequency      the target frequency for the discrete rate
     * @return the equivalent discrete rate
     */
    public static double continuousToDiscrete(double continuousRate, CompoundingFrequency frequency) {
        Objects.requireNonNull(frequency, "Frequency cannot be null");
        int m = frequency.getPeriodsPerYear();
        return m * (Math.exp(continuousRate / m) - 1.0);
    }

    /**
     * Converts a discrete rate from one frequency to another.
     * <p>
     * This is done by equating the discount factors (or future value factors).
     * $(1 + \frac{r_1}{m_1})^{m_1} = (1 + \frac{r_2}{m_2})^{m_2}$
     * </p>
     *
     * @param sourceRate      the source discrete rate
     * @param sourceFrequency the source frequency
     * @param targetFrequency the target frequency
     * @return the equivalent rate at the target frequency
     */
    public static double convertDiscreteRates(double sourceRate,
                                              CompoundingFrequency sourceFrequency,
                                              CompoundingFrequency targetFrequency) {
        Objects.requireNonNull(sourceFrequency, "Source frequency cannot be null");
        Objects.requireNonNull(targetFrequency, "Target frequency cannot be null");

        if (sourceFrequency == targetFrequency) {
            return sourceRate;
        }

        int m1 = sourceFrequency.getPeriodsPerYear();
        int m2 = targetFrequency.getPeriodsPerYear();

        // (1 + r1/m1)^(m1/m2) = 1 + r2/m2
        // r2 = m2 * [ (1 + r1/m1)^(m1/m2) - 1 ]
        
        double base = 1.0 + (sourceRate / m1);
        double exponent = (double) m1 / m2;
        
        return m2 * (Math.pow(base, exponent) - 1.0);
    }
}
