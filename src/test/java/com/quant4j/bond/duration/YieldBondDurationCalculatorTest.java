package com.quant4j.bond.duration;

import com.quant4j.bond.enumeration.Frequency;
import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.price.YieldBondPricer;
import com.quant4j.bond.rate.compound.ContinuousCompoundingStrategy;
import com.quant4j.bond.rate.compound.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YieldBondDurationCalculatorTest {

    private static final double TOLERANCE = 0.001;

    private YieldBondDurationCalculator calculator;
    private ContinuousCompoundingStrategy continuous;
    private DiscreteCompoundingStrategy semiAnnual;

    @BeforeEach
    void setUp() {
        calculator = new YieldBondDurationCalculator();
        continuous = new ContinuousCompoundingStrategy();
        semiAnnual = new DiscreteCompoundingStrategy(2);
    }

    @Test
    @DisplayName("Macaulay duration of a coupon bond is less than its maturity")
    void testMacaulayDuration_CouponBondLessThanMaturity() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        YieldBondPricer pricer = new YieldBondPricer(0.05, semiAnnual);
        double price = pricer.price(bond);

        double duration = calculator.macaulayDuration(bond, 0.05, semiAnnual, price);

        assertTrue(duration < 2.0, "Coupon bond duration must be less than maturity");
        assertEquals(1.928, duration, TOLERANCE);
    }

    @Test
    @DisplayName("Macaulay duration of a coupon bond is less than its maturity continuous compounding")
    void testMacaulayDuration_CouponBondLessThanMaturityContinuous() {
        Bond bond = new Bond(100, 0.08, 5, Frequency.ANNUALLY);
        YieldBondPricer pricer = new YieldBondPricer(0.07, continuous);
        double price = pricer.price(bond);

        double duration = calculator.macaulayDuration(bond, 0.07, continuous, price);

        assertEquals(4.3235, duration, 0.0001);
    }

    @Test
    @DisplayName("Modified duration equals Macaulay duration under continuous compounding")
    void testModifiedDuration_EqualsMacaulayForContinuous() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        YieldBondPricer pricer = new YieldBondPricer(0.05, continuous);
        double price = pricer.price(bond);

        double macaulay = calculator.macaulayDuration(bond, 0.05, continuous, price);
        double modified = calculator.modifiedDuration(bond, 0.05, continuous, price);

        assertEquals(macaulay, modified, TOLERANCE);
    }

    @Test
    @DisplayName("Modified duration is less than Macaulay duration under discrete compounding")
    void testModifiedDuration_LessThanMacaulayForDiscrete() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        YieldBondPricer pricer = new YieldBondPricer(0.05, semiAnnual);
        double price = pricer.price(bond);

        double macaulay = calculator.macaulayDuration(bond, 0.05, semiAnnual, price);
        double modified = calculator.modifiedDuration(bond, 0.05, semiAnnual, price);

        assertTrue(modified < macaulay, "Modified duration must be less than Macaulay under discrete compounding");
        // D_mod = D_mac / (1 + 0.05/2) = D_mac / 1.025
        assertEquals(macaulay / 1.025, modified, TOLERANCE);
    }

    @Test
    @DisplayName("Null bond should throw NullPointerException")
    void testNullBondThrows() {
        assertThrows(NullPointerException.class,
                () -> calculator.macaulayDuration(null, 0.05, continuous, 1000));
    }

    @Test
    @DisplayName("Non-positive price should throw IllegalArgumentException")
    void testNonPositivePriceThrows() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        assertThrows(IllegalArgumentException.class,
                () -> calculator.macaulayDuration(bond, 0.05, continuous, 0));
    }
}
