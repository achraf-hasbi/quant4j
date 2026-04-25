package io.quant4j.rates;

import io.quant4j.rates.compounding.CompoundingStrategy;
import io.quant4j.rates.compounding.DiscreteCompoundingStrategy;

/**
 * Represents the frequency of compounding for discrete interest rates.
 * Provides the number of periods per year for standard frequencies.
 */
public enum Frequency {
    /** Once per year. */
    ANNUALLY(1, new DiscreteCompoundingStrategy(1)),
    /** Twice per year. */
    SEMI_ANNUALLY(2, new DiscreteCompoundingStrategy(2)),
    /** Four times per year. */
    QUARTERLY(4, new DiscreteCompoundingStrategy(4)),
    /** Twelve times per year. */
    MONTHLY(12, new DiscreteCompoundingStrategy(12)),
    /** 365 times per year (standard daily convention). */
    DAILY(365, new DiscreteCompoundingStrategy(365));

    private final int periodsPerYear;
    private final CompoundingStrategy compoundingStrategy;

    Frequency(int periodsPerYear, CompoundingStrategy compoundingStrategy) {
        this.periodsPerYear = periodsPerYear;
        this.compoundingStrategy = compoundingStrategy;
    }

    /**
     * Returns the number of compounding periods per year.
     *
     * @return the integer representing frequency (e.g., 12 for MONTHLY).
     */
    public int getPeriodsPerYear() {
        return this.periodsPerYear;
    }

    /**
     * Returns the compounding strategy for this frequency.
     *
     * @return the compounding strategy (e.g., DiscreteCompoundingStrategy or ContinuousCompoundingStrategy).
     */
    public CompoundingStrategy getCompoundingStrategy() {
        return this.compoundingStrategy;
    }
}
