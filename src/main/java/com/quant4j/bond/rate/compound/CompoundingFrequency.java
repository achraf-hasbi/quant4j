package com.quant4j.bond.rate.compound;

/**
 * Represents the frequency of compounding for discrete interest rates.
 * Provides the number of periods per year for standard frequencies.
 */
public enum CompoundingFrequency {
    /** Once per year. */
    ANNUALLY(1),
    /** Twice per year. */
    SEMI_ANNUALLY(2),
    /** Four times per year. */
    QUARTERLY(4),
    /** Twelve times per year. */
    MONTHLY(12),
    /** 365 times per year (standard daily convention). */
    DAILY(365);

    private final int periodsPerYear;

    CompoundingFrequency(int periodsPerYear) {
        this.periodsPerYear = periodsPerYear;
    }

    /**
     * Returns the number of compounding periods per year.
     *
     * @return the integer representing frequency (e.g., 12 for MONTHLY).
     */
    public int getPeriodsPerYear() {
        return periodsPerYear;
    }
}
