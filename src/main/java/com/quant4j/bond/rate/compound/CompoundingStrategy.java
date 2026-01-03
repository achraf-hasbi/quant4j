package com.quant4j.bond.rate.compound;

/**
 * Defines the contract for interest rate compounding calculations.
 * Implementations handle specific compounding conventions (e.g., Discrete, Continuous).
 */
public interface CompoundingStrategy {

    /**
     * Calculates the discount factor for a given interest rate and time period.
     * <p>
     * The discount factor $DF(r, t)$ represents the present value of 1 unit of currency
     * received at time $t$.
     * </p>
     *
     * @param rate the annual interest rate (as a decimal, e.g., 0.05 for 5%)
     * @param time the time in years
     * @return the discount factor
     */
    double discountFactor(double rate, double time);

    /**
     * Calculates the future value of a principal amount.
     *
     * @param principal the initial amount
     * @param rate      the annual interest rate (as a decimal)
     * @param time      the time in years
     * @return the future value
     */
    double futureValue(double principal, double rate, double time);
}
