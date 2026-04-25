package io.quant4j.bond.pricing;

import io.quant4j.bond.Bond;

/**
 * Defines the contract for pricing bonds.
 */
public interface BondPricer {

    /**
     * Calculates the price of a bond.
     *
     * @param bond The bond to price.
     * @return The calculated price.
     * @throws NullPointerException if {@code bond} is null
     */
    double price(Bond bond);
}