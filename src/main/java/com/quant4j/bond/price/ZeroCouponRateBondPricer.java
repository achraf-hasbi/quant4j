package com.quant4j.bond.price;

import com.quant4j.bond.math.interpolation.InterpolationStrategy;
import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.CompoundingStrategy;

import java.util.Map;
import java.util.TreeMap;

/**
 * Prices a bond by discounting each cash flow using its corresponding
 * zero coupon rate, interpolated from a provided zero rate curve.
 *
 * <p>The zero rate curve maps time (in years) to zero coupon rates.
 * Each cash flow is discounted individually at the rate obtained by
 * interpolating the curve at the cash flow's time.</p>
 */
public class ZeroCouponRateBondPricer implements BondPricer {

    private final TreeMap<Double, Double> zeroCurve;
    private final InterpolationStrategy interpolationStrategy;
    private final CompoundingStrategy compoundingStrategy;

    /**
     * Constructs a pricer with a zero coupon rate curve.
     *
     * @param zeroCurve             The zero coupon rate curve (Time in years -> Zero Rate).
     * @param interpolationStrategy The strategy used to interpolate rates from the curve.
     * @param compoundingStrategy   The compounding convention used to compute discount factors.
     */
    public ZeroCouponRateBondPricer(Map<Double, Double> zeroCurve,
                                    InterpolationStrategy interpolationStrategy,
                                    CompoundingStrategy compoundingStrategy) {
        if (zeroCurve == null || zeroCurve.isEmpty()) {
            throw new IllegalArgumentException("Zero curve cannot be null or empty");
        }
        if (interpolationStrategy == null) {
            throw new IllegalArgumentException("Interpolation strategy cannot be null");
        }
        if (compoundingStrategy == null) {
            throw new IllegalArgumentException("Compounding strategy cannot be null");
        }
        this.zeroCurve = new TreeMap<>(zeroCurve);
        this.interpolationStrategy = interpolationStrategy;
        this.compoundingStrategy = compoundingStrategy;
    }

    /**
     * Prices the bond by summing the present value of each cash flow,
     * discounted at the zero coupon rate interpolated from the curve.
     *
     * {@inheritDoc}
     */
    @Override
    public double price(Bond bond) {
        if (bond == null) {
            throw new IllegalArgumentException("Bond cannot be null");
        }

        Map<Double, Double> cashflows = bond.getCashflows();
        double price = 0.0;

        for (Map.Entry<Double, Double> entry : cashflows.entrySet()) {
            double t = entry.getKey();
            double cashflow = entry.getValue();

            double zeroRate = interpolationStrategy.interpolate(zeroCurve, t);
            double discountFactor = compoundingStrategy.discountFactor(zeroRate, t);

            price += cashflow * discountFactor;
        }

        return price;
    }
}
