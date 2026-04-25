package com.quant4j.bond.yield;

import com.quant4j.bond.Bond;

/**
 * Defines the contract for computing the yield to maturity of a bond given its price.
 */
public interface BondYieldCalculator {

    /**
     * Calculates the yield to maturity of a bond that produces the given price,
     * under the compounding convention supplied to the implementation.
     *
     * @param bond  The bond.
     * @param price The observed (market) price of the bond.
     * @return The annual yield to maturity (decimal).
     */
    double yield(Bond bond, double price);
}
