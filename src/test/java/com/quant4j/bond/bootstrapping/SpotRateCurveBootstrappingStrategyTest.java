package com.quant4j.bond.bootstrapping;

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

        Bond bond = new Bond(faceValue, annualRate, maturity, frequency);

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
        Bond b1 = new Bond(100.0, 0.05, 1.0, Frequency.ANNUALLY);
        // Bond 2: 2 Year, 6% Coupon, Par
        Bond b2 = new Bond(100.0, 0.06, 2.0, Frequency.ANNUALLY);

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
        Bond b1 = new Bond(100.0, 0.04, 1.0, Frequency.ANNUALLY);
        Bond b2 = new Bond(100.0, 0.05, 2.0, Frequency.ANNUALLY);
        Bond b3 = new Bond(100.0, 0.06, 3.0, Frequency.ANNUALLY);

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
}
