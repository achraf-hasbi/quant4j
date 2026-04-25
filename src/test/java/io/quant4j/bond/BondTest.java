package io.quant4j.bond;

import io.quant4j.rates.Frequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Bond Tests")
class BondTest {

    @Test
    @DisplayName("Calculate coupon payment for Semi-Annual bond")
    void testGetCouponPayment_SemiAnnual() {
        // Face Value = 1000, Rate = 5%, Semi-Annual -> 1000 * 0.05 / 2 = 25
        Bond bond = new Bond(1000.0, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        assertEquals(25.0, bond.getCouponPayment(), 1e-9);
    }

    @Test
    @DisplayName("Generate cashflows for 2-year Semi-Annual bond")
    void testGetCashflows_TwoYears_SemiAnnual() {
        // 2 years * 2 payments/year = 4 periods
        // Coupon = 25
        // Final payment = 25 + 1000 = 1025
        Bond bond = new Bond(1000.0, 0.05, 2.0, Frequency.SEMI_ANNUALLY);

        Map<Double, Double> cashflows = bond.getCashflows();

        assertEquals(4, cashflows.size());
        assertEquals(25.0, cashflows.get(0.5), 1e-9);
        assertEquals(25.0, cashflows.get(1.0), 1e-9);
        assertEquals(25.0, cashflows.get(1.5), 1e-9);
        assertEquals(1025.0, cashflows.get(2.0), 1e-9);
    }
}
