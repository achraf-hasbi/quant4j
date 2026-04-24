package com.quant4j.bond.yield;

import com.quant4j.bond.enumeration.Frequency;
import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.price.YieldBondPricer;
import com.quant4j.bond.rate.compound.ContinuousCompoundingStrategy;
import com.quant4j.bond.rate.compound.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecantBondYieldCalculatorTest {

    private static final double TOLERANCE = 1e-6;

    private final DiscreteCompoundingStrategy semiAnnual = new DiscreteCompoundingStrategy(2);
    private final ContinuousCompoundingStrategy continuous = new ContinuousCompoundingStrategy();

    @Test
    @DisplayName("Yield of a par bond equals its coupon rate (semi-annual discrete)")
    void testYield_ParBondEqualsCouponRate() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double parPrice = new YieldBondPricer(0.05, semiAnnual).price(bond);

        double yield = new SecantBondYieldCalculator(semiAnnual).yield(bond, parPrice);

        assertEquals(0.05, yield, TOLERANCE);
    }

    @Test
    @DisplayName("Discount bond yields above coupon rate")
    void testYield_DiscountBondAboveCoupon() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double discountPrice = 980.0;

        double yield = new SecantBondYieldCalculator(semiAnnual).yield(bond, discountPrice);

        assertTrue(yield > 0.05);
    }

    @Test
    @DisplayName("Yield recovery under continuous compounding")
    void testYield_RecoversContinuousYield() {
        Bond bond = new Bond(100, 0.08, 5, Frequency.ANNUALLY);
        double price = new YieldBondPricer(0.07, continuous).price(bond);

        double yield = new SecantBondYieldCalculator(continuous).yield(bond, price);

        assertEquals(0.07, yield, TOLERANCE);
    }

    @Test
    @DisplayName("Identical initial guesses should throw IllegalArgumentException")
    void testIdenticalGuessesThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new SecantBondYieldCalculator(semiAnnual, 0.05, 0.05, 1e-10, 100));
    }

    @Test
    @DisplayName("Null bond should throw NullPointerException")
    void testNullBondThrows() {
        BondYieldCalculator calculator = new SecantBondYieldCalculator(semiAnnual);
        assertThrows(NullPointerException.class, () -> calculator.yield(null, 1000));
    }
}
