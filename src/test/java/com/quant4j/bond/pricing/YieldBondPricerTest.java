package com.quant4j.bond.pricing;

import com.quant4j.rates.Frequency;
import com.quant4j.bond.Bond;
import com.quant4j.rates.compounding.ContinuousCompoundingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Yield Bond Pricer Tests")
class YieldBondPricerTest {

    @Test
    @DisplayName("Price should throw NPE when bond is null")
    void testPrice_NullBond() {
        YieldBondPricer pricer = new YieldBondPricer(0.05, new ContinuousCompoundingStrategy());
        assertThrows(NullPointerException.class, () -> pricer.price(null),
                "Should throw NullPointerException when bond is null");
    }

    @Test
    @DisplayName("Price calculation for Semi-Annual Bond (1.5 years)")
    void testPrice_SemiAnnualBond_OneYear() {
        double yield = 0.052;
        double faceValue = 100.0;
        double annualRate = 0.04;
        double maturityYears = 1.5;
        Frequency frequency = Frequency.SEMI_ANNUALLY;

        Bond bond = new Bond(faceValue, annualRate, maturityYears, frequency);

        YieldBondPricer pricer = new YieldBondPricer(yield, bond.couponFrequency().getCompoundingStrategy());

        double price = pricer.price(bond);

        assertEquals(98.289, price, 0.001);
    }
}