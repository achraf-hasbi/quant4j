package io.quant4j.bond.curve;

import io.quant4j.rates.Frequency;
import io.quant4j.math.interpolation.LinearInterpolationStrategy;
import io.quant4j.bond.Bond;
import io.quant4j.rates.compounding.CompoundingStrategy;
import io.quant4j.rates.compounding.ContinuousCompoundingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpotRateCurveBootstrappingStrategyTest {

    private static final double TOLERANCE = 1e-6;

    private LinearInterpolationStrategy interpolation;
    private CompoundingStrategy annualCompounding;

    @BeforeEach
    void setUp() {
        interpolation = new LinearInterpolationStrategy();
        annualCompounding = Frequency.ANNUALLY.getCompoundingStrategy();
    }

    @Test
    @DisplayName("Single par bond: bootstrapped zero rate equals coupon rate")
    void testSingleParBond_ZeroRateEqualsCouponRate() {
        Bond bond = new Bond(1000, 0.05, 1.0, Frequency.ANNUALLY);
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);

        Map<Double, Double> curve = strategy.bootstrapFromParBonds(List.of(bond), interpolation);

        assertEquals(0.05, curve.get(1.0), TOLERANCE);
    }

    @Test
    @DisplayName("Multiple par bonds: bootstrapped zero rates are correct at each maturity")
    void testMultipleParBonds_ZeroRatesAreCorrect() {
        Bond bond1 = new Bond(1000, 0.04, 1.0, Frequency.ANNUALLY);
        Bond bond2 = new Bond(1000, 0.045, 2.0, Frequency.ANNUALLY);
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);

        Map<Double, Double> curve = strategy.bootstrapFromParBonds(List.of(bond1, bond2), interpolation);

        assertEquals(2, curve.size());
        assertEquals(0.04, curve.get(1.0), TOLERANCE);
        // z(2) = sqrt(1045 * 1.04 / (1040 - 45)) - 1 = sqrt(1086.8 / 995) - 1
        assertEquals(0.045113, curve.get(2.0), 1e-7);
    }

    @Test
    @DisplayName("Bonds supplied out of maturity order are sorted and bootstrapped correctly")
    void testUnsortedBondsAreBootstrappedCorrectly() {
        Bond bond1 = new Bond(1000, 0.04, 1.0, Frequency.ANNUALLY);
        Bond bond2 = new Bond(1000, 0.045, 2.0, Frequency.ANNUALLY);
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);

        Map<Double, Double> curveOrdered = strategy.bootstrapFromParBonds(List.of(bond1, bond2), interpolation);
        Map<Double, Double> curveReversed = strategy.bootstrapFromParBonds(List.of(bond2, bond1), interpolation);

        assertEquals(curveOrdered.get(1.0), curveReversed.get(1.0), TOLERANCE);
        assertEquals(curveOrdered.get(2.0), curveReversed.get(2.0), TOLERANCE);
    }

    @Test
    @DisplayName("Bootstrapped curve contains entries from initial curve and new bonds")
    void testCurveContainsInitialAndNewEntries() {
        List<Bond> bonds = List.of(
                new Bond(100, 0.08, 1.5, Frequency.SEMI_ANNUALLY),
                new Bond(100, 0.10, 2.0, Frequency.SEMI_ANNUALLY)
        );
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(
                Map.of(0.5, 0.1238, 1.0, 0.1165)
        );
        Map<Bond, Double> marketPrices = Map.of(
                bonds.get(0), 94.84,
                bonds.get(1), 97.12
        );
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(zeroCurve, new ContinuousCompoundingStrategy());

        Map<Double, Double> curve = strategy.bootstrapFromMarketPrices(bonds, marketPrices, interpolation);

        assertEquals(4, curve.size());
        assertEquals(0.1238, curve.get(0.5), 0.0001);
        assertEquals(0.1165, curve.get(1.0), 0.0001);
        assertEquals(0.1150, curve.get(1.5), 0.0001);
        assertEquals(0.1130, curve.get(2.0), 0.0001);
    }

    @Test
    @DisplayName("Initial zero curve is seeded into the result and not mutated")
    void testInitialCurveIsSeededAndNotMutated() {
        TreeMap<Double, Double> initial = new TreeMap<>(Map.of(1.0, 0.04));
        Bond bond = new Bond(1000, 0.045, 2.0, Frequency.ANNUALLY);
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(initial, annualCompounding);

        Map<Double, Double> curve = strategy.bootstrapFromParBonds(List.of(bond), interpolation);

        assertTrue(curve.containsKey(1.0));
        assertTrue(curve.containsKey(2.0));
        assertEquals(1, initial.size());
        assertFalse(initial.containsKey(2.0));
    }

    @Test
    @DisplayName("Null initial zero curve should throw NullPointerException")
    void testNullInitialCurveThrows() {
        assertThrows(NullPointerException.class,
                () -> new SpotRateCurveBootstrappingStrategy(null, annualCompounding));
    }

    @Test
    @DisplayName("Null compounding strategy should throw NullPointerException")
    void testNullCompoundingStrategyThrows() {
        assertThrows(NullPointerException.class,
                () -> new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), null));
    }

    @Test
    @DisplayName("Null interpolation strategy should throw NullPointerException")
    void testNullInterpolationStrategyThrows() {
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);
        Bond bond = new Bond(1000, 0.05, 1.0, Frequency.ANNUALLY);

        assertThrows(NullPointerException.class,
                () -> strategy.bootstrapFromParBonds(List.of(bond), null));
    }

    @Test
    @DisplayName("Empty bond list should throw IllegalArgumentException")
    void testEmptyBondsThrows() {
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);

        assertThrows(IllegalArgumentException.class,
                () -> strategy.bootstrapFromParBonds(List.of(), interpolation));
    }

    @Test
    @DisplayName("Zero-coupon bond: single terminal cashflow, bootstrapped rate matches discount factor")
    void testZeroCouponBond_BootstrappedRateMatchesDiscountFactor() {
        // annualRate = 0 → getCashflows() produces a single entry {1.0 → faceValue}
        Bond bond = new Bond(1000, 0.0, 1.0, Frequency.ANNUALLY);
        double expectedRate = 0.05;
        double price = 1000.0 / (1.0 + expectedRate); // 952.380...
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);

        Map<Double, Double> curve = strategy.bootstrapFromMarketPrices(
                List.of(bond), Map.of(bond, price), interpolation);

        assertEquals(expectedRate, curve.get(1.0), TOLERANCE);
    }

    @Test
    @DisplayName("Duplicate maturities in bond list should throw IllegalArgumentException")
    void testDuplicateMaturitiesThrows() {
        Bond bond1 = new Bond(1000, 0.04, 1.0, Frequency.ANNUALLY);
        Bond bond2 = new Bond(500, 0.06, 1.0, Frequency.ANNUALLY);
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);

        assertThrows(IllegalArgumentException.class,
                () -> strategy.bootstrapFromParBonds(List.of(bond1, bond2), interpolation));
    }

    @Test
    @DisplayName("Missing market price for a bond should throw IllegalArgumentException")
    void testMissingMarketPriceThrows() {
        Bond bond = new Bond(1000, 0.05, 1.0, Frequency.ANNUALLY);
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);

        assertThrows(IllegalArgumentException.class,
                () -> strategy.bootstrapFromMarketPrices(List.of(bond), Map.of(), interpolation));
    }
}
