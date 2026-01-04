package com.quant4j.bond.bootstrapping;

import com.quant4j.bond.math.interpolation.InterpolationStrategy;
import com.quant4j.bond.pojo.Bond;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Logic to bootstrap a spot rate (i.e zero-coupon yield) curve from a set of par bonds using a specific interpolation strategy.
 */
public class SpotRateCurveBootstrappingStrategy implements BootstrappingStrategy {
    
    /**
     * Calculates the zero-coupon yield curve using the bootstrap method.
     *
     * @param bonds the list of benchmark bonds (par bonds).
     * @return a Map representing the curve: Time (Years) -> Zero Rate.
     */
    @Override
    public Map<Double, Double> bootstrapFromParBonds(List<Bond> bonds, InterpolationStrategy interpolationStrategy) {
        if (bonds == null || bonds.isEmpty()) {
            throw new IllegalArgumentException("Bond list cannot be null or empty");
        }

        // 1. Sort bonds by maturity
        List<Bond> sortedBonds = new ArrayList<>(bonds);
        sortedBonds.sort(Comparator.comparingDouble(Bond::maturityYears));

        // Map to store Time -> Zero Rate
        // TreeMap ensures keys are sorted, useful for interpolation lookups
        TreeMap<Double, Double> zeroCurve = new TreeMap<>();

        for (Bond bond : sortedBonds) {
            double maturity = bond.maturityYears();
            double couponPayment = bond.getCouponPayment();
            double faceValue = bond.faceValue();
            int frequency = bond.couponFrequency().getPeriodsPerYear();

            // Calculate cash flow times
            List<Double> cashFlowTimes = generateCashFlowTimes(maturity, frequency);

            double sumPvKnownCoupons = 0.0;

            // Iterate over all coupons except the final principal+coupon at maturity
            for (int i = 0; i < cashFlowTimes.size() - 1; i++) {
                double t = cashFlowTimes.get(i);
                // Use the strategy to get the rate
                double rate = interpolationStrategy.interpolate(zeroCurve, t);
                double df = bond.couponFrequency().getCompoundingStrategy().discountFactor(rate, t);
                sumPvKnownCoupons += couponPayment * df;
            }

            // Equation: FaceValue = sumPvKnownCoupons + (FaceValue + Coupon) * DF(T)
            // DF(T) = (FaceValue - sumPvKnownCoupons) / (FaceValue + Coupon)
            double finalCashFlow = faceValue + couponPayment;
            double dfAtMaturity = (faceValue - sumPvKnownCoupons) / finalCashFlow;

            double zeroRate = bond.couponFrequency().getCompoundingStrategy().rateFromDiscountFactor(dfAtMaturity, maturity);

            zeroCurve.put(maturity, zeroRate);
        }

        return zeroCurve;
    }

    private List<Double> generateCashFlowTimes(double maturity, int frequency) {
        List<Double> times = new ArrayList<>();
        double dt = 1.0 / frequency;
        // Simple generation: T, T-dt, T-2dt ... until > 0
        // Then sort them back to ascending order
        for (double t = maturity; t > 1e-9; t -= dt) {
            times.add(t);
        }
        times.sort(Double::compare);
        return times;
    }
}
