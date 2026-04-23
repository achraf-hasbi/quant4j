package com.quant4j.bond.price;

import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.CompoundingStrategy;

import java.util.Objects;

/**
 * Prices a bond using a single Yield to Maturity (YTM).
 *
 * <p>The yield's compounding convention is specified explicitly and may differ
 * from the bond's coupon frequency.</p>
 */
public class YieldBondPricer implements BondPricer {

    private final double yield;
    private final CompoundingStrategy yieldCompoundingStrategy;

    /**
     * Constructs a pricer with a specific yield and its compounding convention.
     *
     * @param yield                    The annual yield to maturity (decimal).
     * @param yieldCompoundingStrategy The compounding convention for the yield.
     */
    public YieldBondPricer(double yield, CompoundingStrategy yieldCompoundingStrategy) {
        Objects.requireNonNull(yieldCompoundingStrategy, "Yield compounding strategy cannot be null");
        this.yield = yield;
        this.yieldCompoundingStrategy = yieldCompoundingStrategy;
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
        int totalPeriods = (int) Math.round(bond.maturityYears() * periodsPerYear);

        // Sum of discounted coupon payments
        for (int i = 1; i <= totalPeriods; i++) {
            double time = (double) i / periodsPerYear;
            price += couponPayment * yieldCompoundingStrategy.discountFactor(yield, time);
        }

        // Discounted Face Value (Redemption)
        price += bond.faceValue() * yieldCompoundingStrategy.discountFactor(yield, bond.maturityYears());

        return price;
    }
}