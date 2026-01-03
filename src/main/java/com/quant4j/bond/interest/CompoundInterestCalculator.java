package com.quant4j.bond.interest;

import com.quant4j.bond.rate.compound.CompoundingFrequency;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Calculator for determining the growth of an investment using compound interest.
 * <p>
 * Supports:
 * <ul>
 *     <li>Initial Investment growth</li>
 *     <li>Periodic contributions (Ordinary Annuity)</li>
 *     <li>Different frequencies for compounding and contributions</li>
 * </ul>
 */
public class CompoundInterestCalculator {

    /**
     * Calculates the future value and total contributions.
     *
     * @param initialInvestment     The starting principal amount.
     * @param periodicContribution  The amount added at each contribution interval.
     * @param contributionFrequency The frequency of contributions (e.g., MONTHLY).
     * @param timeInYears           The duration of the investment in years.
     * @param annualRate            The annual interest rate (as a decimal, e.g., 0.05 for 5%).
     * @param compoundingFrequency  The frequency of interest compounding (e.g., ANNUALLY).
     * @return A {@link CompoundInterestResult} containing the calculation details.
     */
    public CompoundInterestResult calculate(double initialInvestment,
                                            double periodicContribution,
                                            CompoundingFrequency contributionFrequency,
                                            double timeInYears,
                                            double annualRate,
                                            CompoundingFrequency compoundingFrequency) {
        Objects.requireNonNull(contributionFrequency, "Contribution frequency cannot be null");
        Objects.requireNonNull(compoundingFrequency, "Compounding frequency cannot be null");
        if (timeInYears < 0) {
            throw new IllegalArgumentException("Time cannot be negative");
        }

        validateTimeCoherence(timeInYears, compoundingFrequency);

        CompoundInterestResult result = new CompoundInterestResult();

        int compoundingPeriodsPerYear = compoundingFrequency.getPeriodsPerYear();
        int contributionPeriodsPerYear = contributionFrequency.getPeriodsPerYear();

        // Total number of compounding periods
        int totalPeriods = (int) Math.round(timeInYears * compoundingPeriodsPerYear);

        // Rate per compounding period
        double ratePerPeriod = annualRate / compoundingPeriodsPerYear;

        double currentBalance = initialInvestment;
        double totalPrincipal = initialInvestment;
        double totalAccruedInterest = 0.0;

        result.deposits().add(initialInvestment);
        result.interests().add(0.0);
        result.totalDeposit().add(initialInvestment);
        result.accuredInterest().add(totalAccruedInterest);
        result.balance().add(initialInvestment);

        for (int period = 1; period <= totalPeriods; period++) {
            // 1. Calculate Interest for this compounding period
            double interestThisPeriod = currentBalance * ratePerPeriod;

            // 2. Determine Contribution for this period
            double contributionThisPeriod = 0.0;

            if (contributionPeriodsPerYear == compoundingPeriodsPerYear) {
                // Frequencies match
                contributionThisPeriod = periodicContribution;
            } else if (contributionPeriodsPerYear > compoundingPeriodsPerYear) {
                // More contributions than compounding periods (e.g. Monthly Contrib, Annual Compounding)
                // We aggregate contributions that happened during this compounding period.
                double contributionsPerCompoundingPeriod = (double) contributionPeriodsPerYear / compoundingPeriodsPerYear;
                contributionThisPeriod = periodicContribution * contributionsPerCompoundingPeriod;
            } else {
                // Fewer contributions than compounding periods (e.g. Annual Contrib, Monthly Compounding)
                // Add contribution only on specific periods
                int interval = compoundingPeriodsPerYear / contributionPeriodsPerYear;
                if (period % interval == 0) {
                    contributionThisPeriod = periodicContribution;
                }
            }

            // 3. Update State
            // Interest is added first (compounded), then contribution is added (Ordinary Annuity style, end of period)
            // Note: Standard definition usually adds interest based on start balance.
            currentBalance += interestThisPeriod + contributionThisPeriod;
            totalPrincipal += contributionThisPeriod;
            totalAccruedInterest += interestThisPeriod;

            // 4. Populate Result
            result.deposits().add(contributionThisPeriod);
            result.interests().add(interestThisPeriod);
            result.totalDeposit().add(totalPrincipal);
            result.accuredInterest().add(totalAccruedInterest);
            result.balance().add(currentBalance);
        }

        return result;
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
    private void validateTimeCoherence(double timeInYears, CompoundingFrequency frequency) {
        BigDecimal time = BigDecimal.valueOf(timeInYears);
        BigDecimal periods = BigDecimal.valueOf(frequency.getPeriodsPerYear());
        BigDecimal totalPeriods = time.multiply(periods);

        if (totalPeriods.stripTrailingZeros().scale() <= 0) {
            throw new IllegalArgumentException(
                    "Time duration must be a multiple of the compounding frequency period. " +
                            "Duration: " + timeInYears + " years, Frequency: " + frequency
            );
        }
    }

    /**
     * Helper method to accept time in months.
     *
     * @param timeInMonths Time duration in months.
     * @return The calculated result.
     * @see #calculate(double, double, CompoundingFrequency, double, double, CompoundingFrequency)
     */
    public CompoundInterestResult calculateForMonths(double initialInvestment,
                                                     double periodicContribution,
                                                     CompoundingFrequency contributionFrequency,
                                                     int timeInMonths,
                                                     double annualRate,
                                                     CompoundingFrequency compoundingFrequency) {
        return calculate(initialInvestment, periodicContribution, contributionFrequency, timeInMonths / 12.0, annualRate, compoundingFrequency);
    }
}