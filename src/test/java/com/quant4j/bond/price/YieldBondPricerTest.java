package com.quant4j.bond.price;

import com.quant4j.bond.enumeration.Frequency;
import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YieldBondPricerTest {

    @Test
    void testConstructor_NullStrategy() {
        assertThrows(NullPointerException.class, () -> new YieldBondPricer(0.05, null),
                "Should throw NullPointerException when strategy is null");
    }

    @Test
    void testPrice_NullBond() {
        YieldBondPricer pricer = new YieldBondPricer(0.05, new DiscreteCompoundingStrategy(Frequency.ANNUALLY));
        assertThrows(NullPointerException.class, () -> pricer.price(null),
                "Should throw NullPointerException when bond is null");
    }

    @Test
    void testPrice_SemiAnnualBond_OneYear() {
        // Arrange
        double yield = 0.052;
        double faceValue = 100.0;
        double couponRate = 0.04;
        double maturityYears = 1.5;
        Frequency frequency = Frequency.SEMI_ANNUALLY; // 2 periods per year

        Bond bond = new Bond(faceValue, couponRate, maturityYears, frequency);

        YieldBondPricer pricer = new YieldBondPricer(yield, new DiscreteCompoundingStrategy(frequency));

        double price = pricer.price(bond);

        assertEquals(98.289, price, 0.001);
    }
}