package io.quant4j.math;

import io.quant4j.rates.Frequency;

import java.math.BigDecimal;

/**
 * Internal utility for validating bond and rate parameters.
 *
 * <p>All methods are static; this class is not instantiable.</p>
 */
public class ValidationHelper {

    private ValidationHelper() {
    }

    /**
     * Validates that the total duration aligns with the compounding frequency.
     * <p>
     * Ensures that the total number of periods (time in years * frequency) is an integer.
     * This prevents ambiguity in discrete calculations involving partial periods.
     * </p>
     *
     * @param timeInYears the duration in years
     * @param frequency   the compounding frequency
     * @throws IllegalArgumentException if the duration results in fractional periods
     */
    public static void validateTimeCoherence(double timeInYears, Frequency frequency) {
        BigDecimal time = BigDecimal.valueOf(timeInYears);
        BigDecimal periods = BigDecimal.valueOf(frequency.getPeriodsPerYear());
        BigDecimal totalPeriods = time.multiply(periods);

        if (totalPeriods.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException(
                    "Time duration must be a multiple of the compounding frequency period. " +
                            "Duration: " + timeInYears + " years, Frequency: " + frequency
            );
        }
    }
}
