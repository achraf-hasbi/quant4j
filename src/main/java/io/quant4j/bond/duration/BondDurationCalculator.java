package io.quant4j.bond.duration;

import io.quant4j.bond.Bond;

/**
 * Defines the contract for computing the duration and related risk measures of a bond.
 */
public interface BondDurationCalculator {

    /**
     * Calculates the Macaulay duration of a bond.
     *
     * <p>The Macaulay duration is the time-weighted average of the present values
     * of all cash flows, divided by the bond price.</p>
     *
     * @param bond  the bond.
     * @param price the current price of the bond.
     * @return the Macaulay duration in years.
     * @throws NullPointerException     if {@code bond} is null.
     * @throws IllegalArgumentException if {@code price} is not positive.
     */
    double macaulayDuration(Bond bond, double price);

    /**
     * Calculates the modified duration of a bond.
     *
     * <p>The modified duration measures the percentage price sensitivity
     * of the bond to a unit change in yield.</p>
     *
     * @param bond  the bond.
     * @param price the current price of the bond.
     * @return the modified duration.
     * @throws NullPointerException     if {@code bond} is null.
     * @throws IllegalArgumentException if {@code price} is not positive.
     */
    double modifiedDuration(Bond bond, double price);

    /**
     * Calculates the DV01 (Dollar Value of 1 basis point) of a bond.
     *
     * <p>DV01 is the absolute price change resulting from a 1 basis point (0.01%)
     * increase in yield.</p>
     *
     * <p>Formula: {@code DV01 = D_mod * P * 0.0001}</p>
     *
     * @param bond  the bond.
     * @param price the current price of the bond.
     * @return the DV01 in the same currency unit as the price.
     * @throws NullPointerException     if {@code bond} is null.
     * @throws IllegalArgumentException if {@code price} is not positive.
     */
    double dv01(Bond bond, double price);
}
