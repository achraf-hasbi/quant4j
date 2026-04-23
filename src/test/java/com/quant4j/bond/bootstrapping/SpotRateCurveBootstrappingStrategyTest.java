package com.quant4j.bond.bootstrapping;

import com.quant4j.bond.enumeration.Frequency;
import com.quant4j.bond.math.interpolation.LinearInterpolationStrategy;
import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.ContinuousCompoundingStrategy;
import com.quant4j.bond.rate.compound.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class SpotRateCurveBootstrappingStrategyTest {

    private static final double TOLERANCE = 1e-6;

    private LinearInterpolationStrategy interpolation;
    private DiscreteCompoundingStrategy annualCompounding;

    @BeforeEach
    void setUp() {
        interpolation = new LinearInterpolationStrategy();
        annualCompounding = new DiscreteCompoundingStrategy(1);
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

        Map<Double, Double> curve = strategy.bootstrap(bonds, marketPrices, interpolation);

        // 2 initial + 2 bootstrapped
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

        // Original initial curve must not be mutated
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
    @DisplayName("Empty bond list should throw IllegalArgumentException")
    void testEmptyBondsThrows() {
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);

        assertThrows(IllegalArgumentException.class,
                () -> strategy.bootstrapFromParBonds(List.of(), interpolation));
    }

    @Test
    @DisplayName("Missing market price for a bond should throw IllegalArgumentException")
    void testMissingMarketPriceThrows() {
        Bond bond = new Bond(1000, 0.05, 1.0, Frequency.ANNUALLY);
        SpotRateCurveBootstrappingStrategy strategy =
                new SpotRateCurveBootstrappingStrategy(new TreeMap<>(), annualCompounding);

        assertThrows(IllegalArgumentException.class,
                () -> strategy.bootstrap(List.of(bond), Map.of(), interpolation));
    }
}