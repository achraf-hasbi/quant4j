package com.quant4j.bond.pojo;

import com.quant4j.bond.enumeration.BondType;
import com.quant4j.bond.enumeration.Frequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Zero Coupon Bond Tests")
class ZeroCouponBondTest {

    @Test
    @DisplayName("Coupon payment should be zero")
    void testGetCouponPayment_ShouldReturnZero() {
        Bond zeroBond = new Bond(1000.0, BondType.ZERO_COUPON, 0.05, 2.0, Frequency.ANNUALLY);
        assertEquals(0.0, zeroBond.getCouponPayment(), 1e-9);
    }

    @Test
    @DisplayName("Cashflows should contain only single payment at maturity")
    void testGetCashflows_ShouldReturnSinglePayment() {
        Bond zeroBond = new Bond(1000.0, BondType.ZERO_COUPON, 0.05, 2.0, Frequency.ANNUALLY);

        Map<Double, Double> cashflows = zeroBond.getCashflows();

        assertEquals(1, cashflows.size());
        assertTrue(cashflows.containsKey(2.0));
        assertEquals(1000.0, cashflows.get(2.0), 1e-9);
    }
}
