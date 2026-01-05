package com.quant4j.bond.bootstrapping;

import com.quant4j.bond.math.interpolation.InterpolationStrategy;
import com.quant4j.bond.pojo.Bond;

import java.util.*;
import java.util.stream.Collectors;

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

        Map<Bond, Double> parPrices = bonds.stream()
                .collect(Collectors.toMap(bond -> bond, Bond::faceValue));

        return bootstrap(bonds, parPrices, interpolationStrategy);
    }

    @Override
    public Map<Double, Double> bootstrap(List<Bond> bonds, Map<Bond, Double> marketPrices, InterpolationStrategy interpolationStrategy) {
        if (bonds == null || marketPrices == null) {
            throw new IllegalArgumentException("Bonds list and market prices map cannot be null");
        }
        if (bonds.isEmpty()) {
            throw new IllegalArgumentException("Bond list cannot be empty");
        }

        // Sort bonds by maturity
        List<Bond> sortedBonds = new ArrayList<>(bonds);
        sortedBonds.sort(Comparator.comparingDouble(Bond::maturityYears));

        // Map to store Time -> Zero Rate
        TreeMap<Double, Double> zeroCurve = new TreeMap<>();

        for (Bond bond : sortedBonds) {
            if (!marketPrices.containsKey(bond)) {
                throw new IllegalArgumentException("Market price missing for bond with maturity " + bond.maturityYears());
            }
            double price = marketPrices.get(bond);

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
                double rate = interpolationStrategy.interpolate(zeroCurve, t);
                double df = bond.couponFrequency().getCompoundingStrategy().discountFactor(rate, t);
                sumPvKnownCoupons += couponPayment * df;
            }

            // Equation: Price = sumPvKnownCoupons + (FaceValue + Coupon) * DF(T)
            // DF(T) = (Price - sumPvKnownCoupons) / (FaceValue + Coupon)
            double finalCashFlow = faceValue + couponPayment;
            double dfAtMaturity = (price - sumPvKnownCoupons) / finalCashFlow;

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