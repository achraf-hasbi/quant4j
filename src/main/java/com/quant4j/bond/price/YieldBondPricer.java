package com.quant4j.bond.price;

import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.CompoundingStrategy;

import java.util.Objects;

/**
 * Prices a bond using a single Yield to Maturity (YTM).
 */
public class YieldBondPricer implements BondPricer {

    private final double yield;

    /**
     * Constructs a pricer with a specific yield.
     *
     * @param yield                    The annual yield to maturity (decimal).
     */
    public YieldBondPricer(double yield) {
        this.yield = yield;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double price(Bond bond) {
        Objects.requireNonNull(bond, "Bond cannot be null");

        double price = 0.0;
        double couponPayment = bond.getCouponPayment();
        int periodsPerYear = bond.couponFrequency().getPeriodsPerYear();

        // Total number of coupon periods
        // Assumes integer number of periods for simplicity
        int totalPeriods = (int) Math.round(bond.maturityYears() * periodsPerYear);

        // Sum of discounted coupon payments
        for (int i = 1; i <= totalPeriods; i++) {
            double time = (double) i / periodsPerYear;
            price += couponPayment * bond.couponFrequency().getCompoundingStrategy().discountFactor(yield, time);
        }

        // Discounted Face Value (Redemption)
        price += bond.faceValue() * bond.couponFrequency().getCompoundingStrategy().discountFactor(yield, bond.maturityYears());

        return price;
    }
}