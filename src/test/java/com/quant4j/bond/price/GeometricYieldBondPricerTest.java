package com.quant4j.bond.price;

import com.quant4j.bond.enumeration.Frequency;
import com.quant4j.bond.pojo.Bond;
import com.quant4j.bond.rate.compound.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Geometric Yield Bond Pricer Tests")
class GeometricYieldBondPricerTest {

    @Test
    @DisplayName("Constructor should throw NPE when strategy is null")
    void testConstructor_NullStrategy() {
        assertThrows(NullPointerException.class, () -> new GeometricYieldBondPricer(0.05, null),
                "Should throw NullPointerException when strategy is null");
    }

    @Test
    @DisplayName("Price should throw NPE when bond is null")
    void testPrice_NullBond() {
        GeometricYieldBondPricer pricer = new GeometricYieldBondPricer(0.05, new DiscreteCompoundingStrategy(Frequency.ANNUALLY));
        assertThrows(NullPointerException.class, () -> pricer.price(null),
                "Should throw NullPointerException when bond is null");
    }

    @Test
    @DisplayName("Price calculation for Semi-Annual Bond (1.5 years)")
    void testPrice_SemiAnnualBond_OneYear() {
        double yield = 0.052;
        double faceValue = 100.0;
        double couponRate = 0.04;
        double maturityYears = 1.5;
        Frequency frequency = Frequency.SEMI_ANNUALLY;

        Bond bond = new Bond(faceValue, couponRate, maturityYears, frequency);

        GeometricYieldBondPricer pricer = new GeometricYieldBondPricer(yield, new DiscreteCompoundingStrategy(frequency));

        double price = pricer.price(bond);

        assertEquals(98.289, price, 0.001);
    }
}