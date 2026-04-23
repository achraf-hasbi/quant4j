package com.quant4j.bond.duration;

import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.CompoundingStrategy;

/**
 * Defines the contract for computing the duration of a bond.
 */
public interface BondDurationCalculator {

    /**
     * Calculates the Macaulay duration of a bond.
     *
     * <p>The Macaulay duration is the time-weighted average of the present values
     * of all cash flows, divided by the bond price.</p>
     *
     * @param bond                     The bond to compute the duration for.
     * @param yield                    The yield to maturity (decimal).
     * @param yieldCompoundingStrategy The compounding convention for the yield.
     * @param price                    The current price of the bond.
     * @return The Macaulay duration in years.
     */
    double macaulayDuration(Bond bond, double yield, CompoundingStrategy yieldCompoundingStrategy, double price);

    /**
     * Calculates the modified duration of a bond.
     *
     * <p>The modified duration measures the percentage price sensitivity
     * of the bond to a unit change in yield.</p>
     *
     * @param bond                     The bond to compute the duration for.
     * @param yield                    The yield to maturity (decimal).
     * @param yieldCompoundingStrategy The compounding convention for the yield.
     * @param price                    The current price of the bond.
     * @return The modified duration.
     */
    double modifiedDuration(Bond bond, double yield, CompoundingStrategy yieldCompoundingStrategy, double price);
}
