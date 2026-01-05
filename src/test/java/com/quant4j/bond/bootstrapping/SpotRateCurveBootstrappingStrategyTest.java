package com.quant4j.bond.bootstrapping;

import com.quant4j.bond.enumeration.BondType;
import com.quant4j.bond.enumeration.Frequency;
import com.quant4j.bond.math.interpolation.InterpolationStrategy;
import com.quant4j.bond.math.interpolation.LinearInterpolationStrategy;
import com.quant4j.bond.pojo.Bond;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpotRateCurveBootstrappingStrategyTest {

    private final SpotRateCurveBootstrappingStrategy strategy = new SpotRateCurveBootstrappingStrategy();
    private final InterpolationStrategy interpolationStrategy = new LinearInterpolationStrategy();

    @Test
    @DisplayName("Bootstrap single 1-year par bond should return coupon rate as zero rate")
    void testSingleBondBootstrapping() {
        // If a bond is trading at par, its yield to maturity is equal to the coupon rate.
        // For a single bullet bond at start, the zero rate is exactly the coupon rate.
        double faceValue = 1000.0;
        double annualRate = 0.05;
        double maturity = 1.0;
        Frequency frequency = Frequency.ANNUALLY;

        Bond bond = new Bond(faceValue, BondType.COUPON_BEARING, annualRate, maturity, frequency);

        Map<Double, Double> curve = strategy.bootstrapFromParBonds(List.of(bond), interpolationStrategy);

        assertNotNull(curve);
        assertEquals(1, curve.size());
        assertTrue(curve.containsKey(1.0));
        assertEquals(0.05, curve.get(1.0), 1e-9, "Zero rate for 1Y par bond should match coupon");
    }

    @Test
    @DisplayName("Bootstrap curve with two annual bonds")
    void testTwoBondBootstrapping() {
        // Bond 1: 1 Year, 5% Coupon, Par
        Bond b1 = new Bond(100.0, BondType.COUPON_BEARING, 0.05, 1.0, Frequency.ANNUALLY);
        // Bond 2: 2 Year, 6% Coupon, Par
        Bond b2 = new Bond(100.0, BondType.COUPON_BEARING, 0.06, 2.0, Frequency.ANNUALLY);

        List<Bond> bonds = List.of(b1, b2);

        Map<Double, Double> curve = strategy.bootstrapFromParBonds(bonds, interpolationStrategy);

        assertEquals(2, curve.size());

        // Check 1Y Rate
        // Price = 100, CashFlow = 105. DF = 100/105. Rate = 5%.
        double rate1 = curve.get(1.0);
        assertEquals(0.05, rate1, 1e-9);

        // Check 2Y Rate Calculation:
        // Bond 2 Cash Flows:
        // T=1: Coupon = 6.0
        // T=2: Principal + Coupon = 106.0
        //
        // PV = 6.0 * DF(1) + 106.0 * DF(2) = 100.0 (Par)
        // We know Rate(1) = 0.05
        // DF(1) = 1 / (1.05)^1 = 0.952380952
        //
        // 100 = 6 * 0.952380952 + 106 * DF(2)
        // 100 = 5.714285714 + 106 * DF(2)
        // 94.285714286 = 106 * DF(2)
        // DF(2) = 94.285714286 / 106 = 0.889487871
        //
        // Rate(2) => DF(2) = 1 / (1 + r)^2
        // (1 + r)^2 = 1 / 0.889487871 = 1.124242424
        // 1 + r = sqrt(1.124242424) = 1.060302987
        // r = 0.060302987

        double rate2 = curve.get(2.0);
        assertEquals(0.060302987, rate2, 1e-6, "Zero rate for 2Y bond should be calculated correctly");
    }

    @Test
    @DisplayName("Verify list sorting does not affect result")
    void testSorting() {
        Bond b1 = new Bond(100.0, BondType.COUPON_BEARING, 0.04, 1.0, Frequency.ANNUALLY);
        Bond b2 = new Bond(100.0, BondType.COUPON_BEARING, 0.05, 2.0, Frequency.ANNUALLY);
        Bond b3 = new Bond(100.0, BondType.COUPON_BEARING, 0.06, 3.0, Frequency.ANNUALLY);

        // Provide in reverse order
        List<Bond> bonds = List.of(b3, b2, b1);

        Map<Double, Double> curve = strategy.bootstrapFromParBonds(bonds, interpolationStrategy);

        // Keys (Maturities) should be correct
        Double[] maturities = curve.keySet().toArray(new Double[0]);
        assertArrayEquals(new Double[]{1.0, 2.0, 3.0}, maturities);
    }

    @Test
    @DisplayName("Should throw exception for empty or null list")
    void testInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () ->
                strategy.bootstrapFromParBonds(null, interpolationStrategy)
        );

        assertThrows(IllegalArgumentException.class, () ->
                strategy.bootstrapFromParBonds(Collections.emptyList(), interpolationStrategy)
        );
    }

    @Test
    @DisplayName("Bootstrap with mixed par and non-par bonds")
    void testMixedBondBootstrapping() {
        // Bond 1: 0.5 Year, Zero Coupon, 5% nominal (ignored), Price derived from 5% yield
        Bond b1 = new Bond(100.0, BondType.ZERO_COUPON, 0.05, 0.5, Frequency.SEMI_ANNUALLY);
        // Bond 2: 1.0 Year, Zero Coupon, 5% nominal (ignored), Price derived from 5% yield
        Bond b2 = new Bond(100.0, BondType.ZERO_COUPON, 0.05, 1.0, Frequency.SEMI_ANNUALLY);
        // Bond 3: 1.5 Year, 4% Coupon, Price 98.29, Semi-Annual
        Bond b3 = new Bond(100.0, BondType.COUPON_BEARING, 0.04, 1.5, Frequency.SEMI_ANNUALLY);

        List<Bond> bonds = List.of(b1, b2, b3);
        Map<Bond, Double> prices = Map.of(
                b1, b1.couponFrequency().getCompoundingStrategy().presentValue(100.0, 0.05, 0.5),
                b2, b2.couponFrequency().getCompoundingStrategy().presentValue(100.0, 0.05, 1.0),
                b3, 98.29
        );

        Map<Double, Double> curve = strategy.bootstrap(bonds, prices, interpolationStrategy);

        assertEquals(3, curve.size());

        // Check 0.5Y Rate
        // Price calculated with 5% rate.
        // Price = 100 / (1 + 0.05/2)^1 = 97.5609756
        // DF(0.5) = Price / 100 = 0.975609756
        // Rate from DF: 1 / (1 + r/2) = 0.975609756 -> r = 0.05
        assertEquals(0.05, curve.get(0.5), 1e-9, "Zero rate for 0.5Y zero-bond should be 5%");

        // Check 1.0Y Rate
        // Price calculated with 5% rate.
        // Price = 100 / (1 + 0.05/2)^2 = 95.1814396
        // DF(1.0) = Price / 100 = 0.951814396
        // Rate from DF: 1 / (1 + r/2)^2 = 0.951814396 -> r = 0.05
        assertEquals(0.05, curve.get(1.0), 1e-9, "Zero rate for 1.0Y zero-bond should be 5%");

        // Check 1.5Y Rate
        // Bond 3 Cashflows:
        // Coupon = 100 * 0.04 / 2 = 2.0
        // T=0.5: 2.0
        // T=1.0: 2.0
        // T=1.5: 102.0
        //
        // Price = 98.29
        // 98.29 = 2.0*DF(0.5) + 2.0*DF(1.0) + 102.0*DF(1.5)
        // Rate(0.5)=0.05 -> DF(0.5) = 1 / 1.025 = 0.975609756
        // Rate(1.0)=0.05 -> DF(1.0) = 1 / (1.025)^2 = 0.951814396
        //
        // 98.29 = 2.0 * 0.975609756 + 2.0 * 0.951814396 + 102.0 * DF(1.5)
        // 98.29 = 1.951219512 + 1.903628792 + 102.0 * DF(1.5)
        // 98.29 = 3.854848304 + 102.0 * DF(1.5)
        // 102.0 * DF(1.5) = 98.29 - 3.854848304 = 94.435151696
        // DF(1.5) = 94.435151696 / 102.0 = 0.925834821
        //
        // Rate(1.5) Semi-Annual Compounding:
        // DF(T) = 1 / (1 + r/2)^(2T)
        // 0.925834821 = 1 / (1 + r/2)^(3)
        // (1 + r/2)^3 = 1 / 0.925834821 = 1.080106263
        // 1 + r/2 = (1.080106263)^(1/3) = 1.02601922
        // r/2 = 0.02601922
        // r = 0.05203844

        assertEquals(0.052038441, curve.get(1.5), 1e-7, "Zero rate for 1.5Y bond should be calculated correctly");
    }
}