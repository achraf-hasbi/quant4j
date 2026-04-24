package com.quant4j.bond.duration;

import com.quant4j.bond.pojo.Bond;

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
     * @param bond  The bond to compute the duration for.
     * @param price The current price of the bond.
     * @return The Macaulay duration in years.
     */
    double macaulayDuration(Bond bond, double price);

    /**
     * Calculates the modified duration of a bond.
     *
     * <p>The modified duration measures the percentage price sensitivity
     * of the bond to a unit change in yield.</p>
     *
     * @param bond  The bond to compute the duration for.
     * @param price The current price of the bond.
     * @return The modified duration.
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
     * @param bond  The bond to compute the DV01 for.
     * @param price The current price of the bond.
     * @return The DV01 in the same currency unit as the price.
     */
    double dv01(Bond bond, double price);
}
