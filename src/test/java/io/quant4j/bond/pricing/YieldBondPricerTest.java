package io.quant4j.bond.pricing;

import io.quant4j.bond.Bond;
import io.quant4j.rates.Frequency;
import io.quant4j.rates.compounding.ContinuousCompoundingStrategy;
import io.quant4j.rates.compounding.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("YieldBondPricer")
class YieldBondPricerTest {

    private static final double TOLERANCE = 1e-3;

    private final DiscreteCompoundingStrategy semiAnnual = new DiscreteCompoundingStrategy(2);
    private final ContinuousCompoundingStrategy continuous = new ContinuousCompoundingStrategy();

    @Test
    @DisplayName("Null bond throws NullPointerException")
    void testPrice_NullBond() {
        YieldBondPricer pricer = new YieldBondPricer(0.05, continuous);
        assertThrows(NullPointerException.class, () -> pricer.price(null));
    }

    @Test
    @DisplayName("NaN yield throws IllegalArgumentException")
    void testConstructor_NanYield() {
        assertThrows(IllegalArgumentException.class,
                () -> new YieldBondPricer(Double.NaN, semiAnnual));
    }

    @Test
    @DisplayName("Infinite yield throws IllegalArgumentException")
    void testConstructor_InfiniteYield() {
        assertThrows(IllegalArgumentException.class,
                () -> new YieldBondPricer(Double.POSITIVE_INFINITY, semiAnnual));
    }

    @Test
    @DisplayName("Null compounding strategy throws NullPointerException")
    void testConstructor_NullCompoundingStrategy() {
        assertThrows(NullPointerException.class,
                () -> new YieldBondPricer(0.05, null));
    }

    @Test
    @DisplayName("Par bond: yield equals coupon rate → price equals face value")
    void testPrice_ParBond() {
        // When YTM == coupon rate, PV of all cash flows equals face value exactly.
        Bond bond = new Bond(1000, 0.06, 2.0, Frequency.SEMI_ANNUALLY);
        YieldBondPricer pricer = new YieldBondPricer(0.06, semiAnnual);

        assertEquals(1000.0, pricer.price(bond), TOLERANCE);
    }

    @Test
    @DisplayName("Premium bond: yield below coupon rate → price above face value")
    void testPrice_PremiumBond() {
        // 6% coupon, 5% YTM (semi-annual), 2yr, face 1000.
        // Expected price ≈ 1018.81 (discounted at 2.5% per period).
        //   PV = 30/1.025 + 30/1.025² + 30/1.025³ + 1030/1.025⁴ ≈ 1018.81
        Bond bond = new Bond(1000, 0.06, 2.0, Frequency.SEMI_ANNUALLY);
        YieldBondPricer pricer = new YieldBondPricer(0.05, semiAnnual);

        double price = pricer.price(bond);

        assertTrue(price > 1000.0, "Premium bond must price above par");
        assertEquals(1018.81, price, TOLERANCE);
    }

    @Test
    @DisplayName("Discount bond: yield above coupon rate → price below face value")
    void testPrice_DiscountBond() {
        // 4% coupon, 5% YTM (semi-annual), 2yr, face 1000.
        // Expected price ≈ 981.19 (discounted at 2.5% per period).
        //   PV = 20/1.025 + 20/1.025² + 20/1.025³ + 1020/1.025⁴ ≈ 981.19
        Bond bond = new Bond(1000, 0.04, 2.0, Frequency.SEMI_ANNUALLY);
        YieldBondPricer pricer = new YieldBondPricer(0.05, semiAnnual);

        double price = pricer.price(bond);

        assertTrue(price < 1000.0, "Discount bond must price below par");
        assertEquals(981.19, price, TOLERANCE);
    }

    @Test
    @DisplayName("Zero-coupon bond (discrete): price equals discounted face value")
    void testPrice_ZeroCouponBond_Discrete() {
        // PV = 1000 / (1 + 0.05/2)^(2*1) = 1000 / 1.050625 ≈ 951.81
        Bond bond = new Bond(1000, 0.0, 1.0, Frequency.SEMI_ANNUALLY);
        YieldBondPricer pricer = new YieldBondPricer(0.05, semiAnnual);

        assertEquals(951.8143, pricer.price(bond), TOLERANCE);
    }

    @Test
    @DisplayName("Zero-coupon bond (continuous): price equals face × e^(−y×T)")
    void testPrice_ZeroCouponBond_Continuous() {
        // PV = 1000 × e^(−0.05 × 5) = 1000 × e^(−0.25) ≈ 778.80
        Bond bond = new Bond(1000, 0.0, 5.0, Frequency.ANNUALLY);
        YieldBondPricer pricer = new YieldBondPricer(0.05, continuous);

        assertEquals(778.80, pricer.price(bond), TOLERANCE);
    }
}