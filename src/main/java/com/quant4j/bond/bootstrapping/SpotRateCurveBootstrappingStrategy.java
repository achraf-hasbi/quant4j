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

            Map<Double, Double> cashflows = bond.getCashflows();

            double sumPvKnownCashflows = 0.0;
            double finalCashFlow = 0.0;

            for (Map.Entry<Double, Double> entry : cashflows.entrySet()) {
                double t = entry.getKey();
                double amount = entry.getValue();

                if (Math.abs(t - maturity) < 1e-9) {
                    // This is the payment at maturity (Coupon + FaceValue)
                    finalCashFlow = amount;
                } else {
                    // Intermediate cash flow, discount it using known curve
                    double rate = interpolationStrategy.interpolate(zeroCurve, t);
                    double df = bond.couponFrequency().getCompoundingStrategy().discountFactor(rate, t);
                    sumPvKnownCashflows += amount * df;
                }
            }

            // Equation: Price = sumPvKnownCashflows + finalCashFlow * DF(T)
            // DF(T) = (Price - sumPvKnownCashflows) / finalCashFlow
            double dfAtMaturity = (price - sumPvKnownCashflows) / finalCashFlow;

            double zeroRate = bond.couponFrequency().getCompoundingStrategy().rateFromDiscountFactor(dfAtMaturity, maturity);

            zeroCurve.put(maturity, zeroRate);
        }

        return zeroCurve;
    }
}