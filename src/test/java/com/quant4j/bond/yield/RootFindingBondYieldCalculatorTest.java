package com.quant4j.bond.yield;

import com.quant4j.bond.Bond;
import com.quant4j.bond.pricing.YieldBondPricer;
import com.quant4j.math.solver.BisectionSolver;
import com.quant4j.math.solver.NewtonRaphsonSolver;
import com.quant4j.math.solver.SecantSolver;
import com.quant4j.rates.Frequency;
import com.quant4j.rates.compounding.ContinuousCompoundingStrategy;
import com.quant4j.rates.compounding.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RootFindingBondYieldCalculatorTest {

    private static final double TOLERANCE = 1.0e-6;

    private final DiscreteCompoundingStrategy semiAnnual = new DiscreteCompoundingStrategy(2);
    private final ContinuousCompoundingStrategy continuous = new ContinuousCompoundingStrategy();

    // ── Bisection ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Bisection: yield of a par bond equals its coupon rate")
    void testBisection_ParBondEqualsCouponRate() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double parPrice = new YieldBondPricer(0.05, semiAnnual).price(bond);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(semiAnnual, new BisectionSolver());

        assertEquals(0.05, calculator.yield(bond, parPrice), TOLERANCE);
    }

    @Test
    @DisplayName("Bisection: premium bond yields below coupon rate")
    void testBisection_PremiumBond_YieldBelowCoupon() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(semiAnnual, new BisectionSolver());

        double yield = calculator.yield(bond, 1020.0);

        assertTrue(yield < 0.05);
    }

    @Test
    @DisplayName("Bisection: yield recovery under continuous compounding")
    void testBisection_RecoversContinuousYield() {
        Bond bond = new Bond(100, 0.08, 5, Frequency.ANNUALLY);
        double price = new YieldBondPricer(0.07, continuous).price(bond);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(continuous, new BisectionSolver());

        assertEquals(0.07, calculator.yield(bond, price), TOLERANCE);
    }

    // ── Newton-Raphson ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Newton-Raphson: yield of a par bond equals its coupon rate")
    void testNewton_ParBondEqualsCouponRate() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double parPrice = new YieldBondPricer(0.05, semiAnnual).price(bond);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(semiAnnual, new NewtonRaphsonSolver());

        assertEquals(0.05, calculator.yield(bond, parPrice), TOLERANCE);
    }

    @Test
    @DisplayName("Newton-Raphson: discount bond yields above coupon rate")
    void testNewton_DiscountBond_YieldAboveCoupon() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(semiAnnual, new NewtonRaphsonSolver());

        double yield = calculator.yield(bond, 980.0);

        assertTrue(yield > 0.05);
    }

    @Test
    @DisplayName("Newton-Raphson: yield recovery under continuous compounding")
    void testNewton_RecoversContinuousYield() {
        Bond bond = new Bond(100, 0.08, 5, Frequency.ANNUALLY);
        double price = new YieldBondPricer(0.07, continuous).price(bond);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(continuous, new NewtonRaphsonSolver());

        assertEquals(0.07, calculator.yield(bond, price), TOLERANCE);
    }

    // ── Secant ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Secant: yield of a par bond equals its coupon rate")
    void testSecant_ParBondEqualsCouponRate() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        double parPrice = new YieldBondPricer(0.05, semiAnnual).price(bond);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(semiAnnual, new SecantSolver());

        assertEquals(0.05, calculator.yield(bond, parPrice), TOLERANCE);
    }

    @Test
    @DisplayName("Secant: discount bond yields above coupon rate")
    void testSecant_DiscountBond_YieldAboveCoupon() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(semiAnnual, new SecantSolver());

        double yield = calculator.yield(bond, 980.0);

        assertTrue(yield > 0.05);
    }

    @Test
    @DisplayName("Secant: yield recovery under continuous compounding")
    void testSecant_RecoversContinuousYield() {
        Bond bond = new Bond(100, 0.08, 5, Frequency.ANNUALLY);
        double price = new YieldBondPricer(0.07, continuous).price(bond);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(continuous, new SecantSolver());

        assertEquals(0.07, calculator.yield(bond, price), TOLERANCE);
    }

    // ── Guard conditions ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Null bond throws NullPointerException")
    void testNullBondThrows() {
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(semiAnnual, new NewtonRaphsonSolver());

        assertThrows(NullPointerException.class, () -> calculator.yield(null, 1000));
    }

    @Test
    @DisplayName("Non-positive price throws IllegalArgumentException")
    void testNonPositivePriceThrows() {
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        BondYieldCalculator calculator = new RootFindingBondYieldCalculator(semiAnnual, new NewtonRaphsonSolver());

        assertThrows(IllegalArgumentException.class, () -> calculator.yield(bond, 0));
        assertThrows(IllegalArgumentException.class, () -> calculator.yield(bond, -100));
    }

    @Test
    @DisplayName("Null compounding strategy throws NullPointerException")
    void testNullCompoundingStrategyThrows() {
        assertThrows(NullPointerException.class,
                () -> new RootFindingBondYieldCalculator(null, new NewtonRaphsonSolver()));
    }

    @Test
    @DisplayName("Null root solver throws NullPointerException")
    void testNullRootSolverThrows() {
        assertThrows(NullPointerException.class,
                () -> new RootFindingBondYieldCalculator(semiAnnual, null));
    }
}
