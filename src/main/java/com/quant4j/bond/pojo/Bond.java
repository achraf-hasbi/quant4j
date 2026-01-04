package com.quant4j.bond.pojo;

import com.quant4j.bond.enumeration.BondType;
import com.quant4j.bond.enumeration.Frequency;

import java.util.Objects;

import static com.quant4j.bond.ValidationHelper.validateTimeCoherence;

/**
 * Represents a fixed-coupon bond.
 *
 * @param faceValue       The principal amount of the bond (e.g., 1000).
 * @param bondType        The type of the bond (e.g., COUPON_BEARING).
 * @param annualRate      The annual coupon rate (decimal, e.g., 0.05 for 5%).
 * @param maturityYears   The time to maturity in years.
 * @param couponFrequency The frequency of coupon payments (e.g., SEMI_ANNUALLY).
 */
public record Bond(double faceValue,
                   BondType bondType,
                   double annualRate,
                   double maturityYears,
                   Frequency couponFrequency) {

    public Bond {
        if (faceValue <= 0) throw new IllegalArgumentException("Face value must be positive");
        Objects.requireNonNull(bondType, "Bond type cannot be null");
        if (annualRate < 0) throw new IllegalArgumentException("Annual rate cannot be negative");
        if (maturityYears <= 0) throw new IllegalArgumentException("Maturity must be positive");
        Objects.requireNonNull(couponFrequency, "Coupon frequency cannot be null");
        validateTimeCoherence(maturityYears, couponFrequency);
    }

    /**
     * Calculates the coupon payment amount per period.
     *
     * @return the coupon amount.
     */
    public double getCouponPayment() {
        return faceValue * annualRate / couponFrequency.getPeriodsPerYear();
    }
}