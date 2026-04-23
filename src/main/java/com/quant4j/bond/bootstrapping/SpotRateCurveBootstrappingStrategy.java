package com.quant4j.bond.bootstrapping;

import com.quant4j.bond.math.interpolation.InterpolationStrategy;
import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.CompoundingStrategy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Logic to bootstrap a spot rate (i.e. zero-coupon yield) curve from a set of bonds.
 *
 * <p>An initial spot rate curve is provided at construction time and is never mutated.
 * Each call to {@link #bootstrap} or {@link #bootstrapFromParBonds} starts from a copy
 * of that initial curve and appends the newly derived zero rates.</p>
 */
public class SpotRateCurveBootstrappingStrategy implements BootstrappingStrategy {

    private final TreeMap<Double, Double> initialZeroCurve;
    private final CompoundingStrategy compoundingStrategy;

    /**
     * Constructs the strategy with an initial spot rate curve and a compounding convention.
     *
     * @param initialZeroCurve   Known spot rates to seed the bootstrap with (maturity -> rate).
     *                           May be empty if no prior rates are known.
     * @param compoundingStrategy The compounding convention used to compute discount factors
     *                            and derive zero rates during bootstrapping.
     */
    public SpotRateCurveBootstrappingStrategy(TreeMap<Double, Double> initialZeroCurve,
                                              CompoundingStrategy compoundingStrategy) {
        Objects.requireNonNull(initialZeroCurve, "Initial zero curve cannot be null");
        Objects.requireNonNull(compoundingStrategy, "Compounding strategy cannot be null");
        this.initialZeroCurve = initialZeroCurve;
        this.compoundingStrategy = compoundingStrategy;
    }

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     *
     * <p>Starts from a copy of the initial zero curve provided at construction and
     * appends the bootstrapped zero rates. The initial curve is never mutated.</p>
     */
    @Override
    public Map<Double, Double> bootstrap(List<Bond> bonds, Map<Bond, Double> marketPrices, InterpolationStrategy interpolationStrategy) {
        if (bonds == null || marketPrices == null) {
            throw new IllegalArgumentException("Bonds list and market prices map cannot be null");
        }
        if (bonds.isEmpty()) {
            throw new IllegalArgumentException("Bond list cannot be empty");
        }

        // Start from a copy of the initial curve — never mutate the original
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(initialZeroCurve);

        List<Bond> sortedBonds = new ArrayList<>(bonds);
        sortedBonds.sort(Comparator.comparingDouble(Bond::maturityYears));

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
                    finalCashFlow = amount;
                } else {
                    double rate = interpolationStrategy.interpolate(zeroCurve, t);
                    double df = compoundingStrategy.discountFactor(rate, t);
                    sumPvKnownCashflows += amount * df;
                }
            }

            double dfAtMaturity = (price - sumPvKnownCashflows) / finalCashFlow;
            double zeroRate = compoundingStrategy.rateFromDiscountFactor(dfAtMaturity, maturity);

            zeroCurve.put(maturity, zeroRate);
        }

        return zeroCurve;
    }
}