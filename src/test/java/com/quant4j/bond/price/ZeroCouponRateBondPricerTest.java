package com.quant4j.bond.price;

import com.quant4j.bond.enumeration.Frequency;
import com.quant4j.bond.math.interpolation.LinearInterpolationStrategy;
import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.ContinuousCompoundingStrategy;
import com.quant4j.bond.rate.compound.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class ZeroCouponRateBondPricerTest {

    private static final double TOLERANCE = 1e-6;

    private LinearInterpolationStrategy linearInterpolation;
    private ContinuousCompoundingStrategy continuousCompounding;
    private DiscreteCompoundingStrategy semiAnnualCompounding;

    @BeforeEach
    void setUp() {
        linearInterpolation = new LinearInterpolationStrategy();
        continuousCompounding = new ContinuousCompoundingStrategy();
        semiAnnualCompounding = new DiscreteCompoundingStrategy(2);
    }

    @Test
    @DisplayName("Flat discrete zero curve at coupon rate should price bond at par")
    void testPriceAtParWithFlatDiscreteZeroCurve() {
        // Flat zero curve at 5% (semi-annual discrete)
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(Map.of(
                0.5, 0.05,
                1.0, 0.05,
                1.5, 0.05,
                2.0, 0.05
        ));

        // 2-year bond, 5% annual coupon, semi-annual payments, face value 1000
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        ZeroCouponRateBondPricer pricer = new ZeroCouponRateBondPricer(zeroCurve, linearInterpolation, semiAnnualCompounding);

        double price = pricer.price(bond);

        assertEquals(1000.0, price, TOLERANCE);
    }

    @Test
    @DisplayName("Rising zero curve")
    void testPriceBelowParWithRisingZeroCurve() {
        // Rising zero curve: rates higher than coupon => bond trades below par
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(Map.of(
                0.5, 0.04,
                1.0, 0.042,
                1.5, 0.044,
                2.0, 0.046,
                2.5, 0.048
        ));

        // 2-year bond, 5% annual coupon, face value 1000
        Bond bond = new Bond(100, 0.04, 2.5, Frequency.SEMI_ANNUALLY);
        ZeroCouponRateBondPricer pricer = new ZeroCouponRateBondPricer(zeroCurve, linearInterpolation, continuousCompounding);

        double price = pricer.price(bond);

        assertEquals(98.04, price, 0.001);
    }

    @Test
    @DisplayName("Empty zero curve should throw IllegalArgumentException")
    void testEmptyZeroCurveThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ZeroCouponRateBondPricer(new TreeMap<>(), linearInterpolation, continuousCompounding));
    }

    @Test
    @DisplayName("Null interpolation strategy should throw IllegalArgumentException")
    void testNullInterpolationStrategyThrows() {
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(Map.of(1.0, 0.05));

        assertThrows(IllegalArgumentException.class,
                () -> new ZeroCouponRateBondPricer(zeroCurve, null, continuousCompounding));
    }

    @Test
    @DisplayName("Null compounding strategy should throw IllegalArgumentException")
    void testNullCompoundingStrategyThrows() {
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(Map.of(1.0, 0.05));

        assertThrows(IllegalArgumentException.class,
                () -> new ZeroCouponRateBondPricer(zeroCurve, linearInterpolation, null));
    }
}