package com.quant4j.bond.pricing;

import com.quant4j.bond.Bond;
import java.util.Objects;

/**
 * Prices a bond using a single Yield to Maturity (YTM) and the closed-form geometric series formula.
 * This is an optimized alternative to looping through every coupon period.
 */
public class GeometricYieldBondPricer implements BondPricer {

    private final double yield;

    /**
     * Constructs a pricer with a specific yield.
     *
     * @param yield                    The annual yield to maturity (decimal).
     */
    public GeometricYieldBondPricer(double yield) {
        this.yield = yield;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double price(Bond bond) {
        Objects.requireNonNull(bond, "Bond cannot be null");

        double couponPayment = bond.getCouponPayment();
        int periodsPerYear = bond.couponFrequency().getPeriodsPerYear();
        int totalPeriods = (int) Math.round(bond.maturityYears() * periodsPerYear);

        // Calculate the discount factor for a single period (the common ratio 'r')
        double periodTime = 1.0 / periodsPerYear;
        double r = bond.couponFrequency().getCompoundingStrategy().discountFactor(yield, periodTime);

        // Calculate Present Value of Coupons (Geometric Series)
        // Series: C*r^1 + C*r^2 + ... + C*r^n
        double pvCoupons;

        // Handle edge case where discount factor is close to 1 (e.g., 0% yield) to avoid division by zero
        if (Math.abs(r - 1.0) < 1.0E-12) {
            pvCoupons = couponPayment * totalPeriods;
        } else {
            // Formula: C * r * (1 - r^n) / (1 - r)
            // We use Math.pow(r, totalPeriods) to ensure consistency with the geometric sequence assumption
            double rToN = Math.pow(r, totalPeriods);
            pvCoupons = couponPayment * r * (1 - rToN) / (1 - r);
        }

        // Calculate Present Value of Redemption (Face Value)
        double pvRedemption = bond.faceValue() * bond.couponFrequency().getCompoundingStrategy().discountFactor(yield, bond.maturityYears());

        return pvCoupons + pvRedemption;
    }
}
