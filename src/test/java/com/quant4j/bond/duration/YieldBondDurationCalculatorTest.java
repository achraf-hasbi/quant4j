package com.quant4j.bond.duration;

import com.quant4j.rates.Frequency;
import com.quant4j.bond.Bond;
import com.quant4j.bond.pricing.YieldBondPricer;
import com.quant4j.rates.compounding.ContinuousCompoundingStrategy;
import com.quant4j.rates.compounding.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YieldBondDurationCalculatorTest {

    private static final double TOLERANCE = 0.001;

    private final ContinuousCompoundingStrategy continuous = new ContinuousCompoundingStrategy();
    private final DiscreteCompoundingStrategy semiAnnual = new DiscreteCompoundingStrategy(2);

    @Test
    @DisplayName("Macaulay duration of a coupon bond is less than its maturity")
    void testMacaulayDuration_CouponBondLessThanMaturity() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double price = new YieldBondPricer(0.05, semiAnnual).price(bond);
        YieldBondDurationCalculator calculator = new YieldBondDurationCalculator(0.05, semiAnnual);

        double duration = calculator.macaulayDuration(bond, price);

        assertTrue(duration < 2.0, "Coupon bond duration must be less than maturity");
        assertEquals(1.928, duration, TOLERANCE);
    }

    @Test
    @DisplayName("Macaulay duration of a coupon bond under continuous compounding")
    void testMacaulayDuration_CouponBondLessThanMaturityContinuous() {
        Bond bond = new Bond(100, 0.08, 5, Frequency.ANNUALLY);
        double price = new YieldBondPricer(0.07, continuous).price(bond);
        YieldBondDurationCalculator calculator = new YieldBondDurationCalculator(0.07, continuous);

        double duration = calculator.macaulayDuration(bond, price);

        assertEquals(4.3235, duration, 0.0001);
    }

    @Test
    @DisplayName("Modified duration equals Macaulay duration under continuous compounding")
    void testModifiedDuration_EqualsMacaulayForContinuous() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double price = new YieldBondPricer(0.05, continuous).price(bond);
        YieldBondDurationCalculator calculator = new YieldBondDurationCalculator(0.05, continuous);

        assertEquals(calculator.macaulayDuration(bond, price), calculator.modifiedDuration(bond, price), TOLERANCE);
    }

    @Test
    @DisplayName("Modified duration is less than Macaulay duration under discrete compounding")
    void testModifiedDuration_LessThanMacaulayForDiscrete() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double price = new YieldBondPricer(0.05, semiAnnual).price(bond);
        YieldBondDurationCalculator calculator = new YieldBondDurationCalculator(0.05, semiAnnual);

        double macaulay = calculator.macaulayDuration(bond, price);
        double modified = calculator.modifiedDuration(bond, price);

        assertTrue(modified < macaulay);
        assertEquals(macaulay / 1.025, modified, TOLERANCE);
    }

    @Test
    @DisplayName("Modified duration uses the yield's compounding frequency, not the bond's coupon frequency")
    void testModifiedDuration_UsesYieldCompoundingFrequency() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        DiscreteCompoundingStrategy annual = new DiscreteCompoundingStrategy(1);
        double price = new YieldBondPricer(0.05, annual).price(bond);
        YieldBondDurationCalculator calculator = new YieldBondDurationCalculator(0.05, annual);

        double macaulay = calculator.macaulayDuration(bond, price);
        double modified = calculator.modifiedDuration(bond, price);

        // m=1 (annual compounding): D_mod = D_mac / (1 + y/1) = D_mac / 1.05
        assertEquals(macaulay / 1.05, modified, TOLERANCE);
    }

    @Test
    @DisplayName("DV01 equals modified duration times price times 0.0001")
    void testDv01_ConcreteValue() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double price = new YieldBondPricer(0.05, semiAnnual).price(bond);
        YieldBondDurationCalculator calculator = new YieldBondDurationCalculator(0.05, semiAnnual);

        // macaulay ≈ 1.928, modified = 1.928 / 1.025 ≈ 1.881, DV01 = 1.881 * 1000 * 0.0001 ≈ 0.188
        assertEquals(0.188, calculator.dv01(bond, price), TOLERANCE);
    }

    @Test
    @DisplayName("Null bond should throw NullPointerException")
    void testNullBondThrows() {
        YieldBondDurationCalculator calculator = new YieldBondDurationCalculator(0.05, continuous);
        assertThrows(NullPointerException.class, () -> calculator.macaulayDuration(null, 1000));
    }

    @Test
    @DisplayName("Non-positive price should throw IllegalArgumentException")
    void testNonPositivePriceThrows() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        YieldBondDurationCalculator calculator = new YieldBondDurationCalculator(0.05, continuous);
        assertThrows(IllegalArgumentException.class, () -> calculator.macaulayDuration(bond, 0));
    }
}
