package io.quant4j.bond.yield;

import io.quant4j.bond.Bond;

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
     * @throws NullPointerException     if {@code bond} is null
     * @throws IllegalArgumentException if {@code price} is not positive, or if the solver
     *                                  cannot bracket the root within its configured interval
     * @throws ArithmeticException      if the numerical solver fails to converge
     */
    double yieldToMaturity(Bond bond, double price);
}
