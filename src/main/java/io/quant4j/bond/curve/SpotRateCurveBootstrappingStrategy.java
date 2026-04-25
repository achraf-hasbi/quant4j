package io.quant4j.bond.curve;

import io.quant4j.bond.Bond;
import io.quant4j.math.interpolation.InterpolationStrategy;
import io.quant4j.rates.compounding.CompoundingStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Bootstraps a spot rate (zero-coupon yield) curve from a set of coupon bonds.
 *
 * <p>An initial spot rate curve is provided at construction time and defensively copied —
 * neither it nor any result map is ever mutated. Each call to
 * {@link #bootstrapFromMarketPrices} or {@link #bootstrapFromParBonds} starts from a fresh
 * copy of the initial curve and appends the newly derived zero rates.</p>
 */
public class SpotRateCurveBootstrappingStrategy implements BootstrappingStrategy {

    private static final double TIME_EPSILON = 1e-9;

    private final TreeMap<Double, Double> initialZeroCurve;
    private final CompoundingStrategy compoundingStrategy;

    /**
     * Constructs the strategy with an initial spot rate curve and a compounding convention.
     *
     * @param initialZeroCurve    known spot rates to seed the bootstrap (maturity → rate);
     *                            may be empty but must not be null. Defensively copied.
     *                            Any {@link java.util.Map} implementation is accepted.
     * @param compoundingStrategy the compounding convention used to compute discount factors
     *                            and invert them back to zero rates; must not be null.
     * @throws NullPointerException if either argument is null.
     */
    public SpotRateCurveBootstrappingStrategy(Map<Double, Double> initialZeroCurve,
                                              CompoundingStrategy compoundingStrategy) {
        Objects.requireNonNull(initialZeroCurve, "Initial zero curve cannot be null");
        Objects.requireNonNull(compoundingStrategy, "Compounding strategy cannot be null");
        this.initialZeroCurve = new TreeMap<>(initialZeroCurve);
        this.compoundingStrategy = compoundingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableMap<Double, Double> bootstrapFromParBonds(List<Bond> bonds,
                                                               InterpolationStrategy interpolationStrategy) {
        Map<Bond, Double> parPrices = bonds.stream()
                .collect(Collectors.toMap(Function.identity(), Bond::faceValue));

        return bootstrapFromMarketPrices(bonds, parPrices, interpolationStrategy);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Bonds are processed in ascending maturity order regardless of input order.
     * Intermediate cashflows are discounted using the zero curve built so far, with
     * the interpolation strategy filling in rates for maturities not yet in the curve.</p>
     *
     * <p>Note: {@code marketPrices} uses {@link Bond} as a map key. {@code Bond} is a record
     * whose equality is field-based — two {@code Bond} instances with identical parameters
     * are the same key. Ensure each bond in {@code bonds} is structurally distinct.</p>
     */
    @Override
    public NavigableMap<Double, Double> bootstrapFromMarketPrices(List<Bond> bonds,
                                                                    Map<Bond, Double> marketPrices,
                                                                    InterpolationStrategy interpolationStrategy) {
        Objects.requireNonNull(bonds, "Bond list cannot be null");
        Objects.requireNonNull(marketPrices, "Market prices cannot be null");
        Objects.requireNonNull(interpolationStrategy, "Interpolation strategy cannot be null");
        if (bonds.isEmpty()) {
            throw new IllegalArgumentException("Bond list cannot be empty");
        }

        long distinctMaturities = bonds.stream().mapToDouble(Bond::maturityYears).distinct().count();
        if (distinctMaturities != bonds.size()) {
            throw new IllegalArgumentException("Bond list contains bonds with duplicate maturities");
        }

        TreeMap<Double, Double> zeroCurve = new TreeMap<>(initialZeroCurve);

        List<Bond> sortedBonds = new ArrayList<>(bonds);
        sortedBonds.sort(Comparator.comparingDouble(Bond::maturityYears));

        for (Bond bond : sortedBonds) {
            if (!marketPrices.containsKey(bond)) {
                throw new IllegalArgumentException("Market price missing for bond with maturity " + bond.maturityYears());
            }
            double price = marketPrices.get(bond);
            double maturity = bond.maturityYears();

            double sumPvIntermediateCashflows = 0.0;
            double terminalCashflow = 0.0;

            for (Map.Entry<Double, Double> entry : bond.getCashflows().entrySet()) {
                double t = entry.getKey();
                double cashflow = entry.getValue();

                if (Math.abs(t - maturity) < TIME_EPSILON) {
                    terminalCashflow = cashflow;
                } else {
                    double spotRate = interpolationStrategy.interpolate(zeroCurve, t);
                    double discountFactor = compoundingStrategy.discountFactor(spotRate, t);
                    sumPvIntermediateCashflows += cashflow * discountFactor;
                }
            }

            if (terminalCashflow == 0.0) {
                throw new IllegalStateException(
                        "No terminal cashflow found at maturity " + maturity);
            }

            double dfAtMaturity = (price - sumPvIntermediateCashflows) / terminalCashflow;
            double zeroRate = compoundingStrategy.rateFromDiscountFactor(dfAtMaturity, maturity);

            zeroCurve.put(maturity, zeroRate);
        }

        return Collections.unmodifiableNavigableMap(zeroCurve);
    }
}
