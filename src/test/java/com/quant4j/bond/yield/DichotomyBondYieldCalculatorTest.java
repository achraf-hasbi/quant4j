package com.quant4j.bond.yield;

import com.quant4j.bond.enumeration.Frequency;
import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.price.YieldBondPricer;
import com.quant4j.bond.rate.compound.ContinuousCompoundingStrategy;
import com.quant4j.bond.rate.compound.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DichotomyBondYieldCalculatorTest {

    private static final double TOLERANCE = 1e-6;

    private final DiscreteCompoundingStrategy semiAnnual = new DiscreteCompoundingStrategy(2);
    private final ContinuousCompoundingStrategy continuous = new ContinuousCompoundingStrategy();

    @Test
    @DisplayName("Yield of a par bond equals its coupon rate (semi-annual discrete)")
    void testYield_ParBondEqualsCouponRate() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double parPrice = new YieldBondPricer(0.05, semiAnnual).price(bond);

        double yield = new DichotomyBondYieldCalculator(semiAnnual).yield(bond, parPrice);

        assertEquals(0.05, yield, TOLERANCE);
    }

    @Test
    @DisplayName("Higher than par price yields a rate below coupon rate")
    void testYield_PremiumBondBelowCoupon() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double premiumPrice = 1020.0;

        double yield = new DichotomyBondYieldCalculator(semiAnnual).yield(bond, premiumPrice);

        assertTrue(yield < 0.05);
    }

    @Test
    @DisplayName("Yield recovery under continuous compounding")
    void testYield_RecoversContinuousYield() {
        Bond bond = new Bond(100, 0.08, 5, Frequency.ANNUALLY);
        double price = new YieldBondPricer(0.07, continuous).price(bond);

        double yield = new DichotomyBondYieldCalculator(continuous).yield(bond, price);

        assertEquals(0.07, yield, TOLERANCE);
    }

    @Test
    @DisplayName("Price not bracketed by search interval should throw IllegalArgumentException")
    void testUnbracketedRootThrows() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        // Very tight interval that doesn't contain the solution (par yield = 0.05)
        BondYieldCalculator calculator =
                new DichotomyBondYieldCalculator(semiAnnual, 0.10, 0.20, 1e-10, 200);

        assertThrows(IllegalArgumentException.class, () -> calculator.yield(bond, 1000.0));
    }

    @Test
    @DisplayName("Null bond should throw NullPointerException")
    void testNullBondThrows() {
        BondYieldCalculator calculator = new DichotomyBondYieldCalculator(semiAnnual);
        assertThrows(NullPointerException.class, () -> calculator.yield(null, 1000));
    }
}
