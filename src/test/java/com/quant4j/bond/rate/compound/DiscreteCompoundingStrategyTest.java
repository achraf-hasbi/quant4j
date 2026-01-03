package com.quant4j.bond.rate.compound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DiscreteCompoundingStrategyTest {

    private static final double TOLERANCE = 1.0e-9;

    @ParameterizedTest
    @EnumSource(CompoundingFrequency.class)
    @DisplayName("Test Discrete Compounding Discount Factor Logic for all frequencies")
    void testDiscreteDiscountFactor(CompoundingFrequency frequency) {
        CompoundingStrategy strategy = new DiscreteCompoundingStrategy(frequency);
        double rate = 0.05;
        double time = 1.5;
        int m = frequency.getPeriodsPerYear();

        double expected = Math.pow(1.0 + (rate / m), -m * time);
        assertEquals(expected, strategy.discountFactor(rate, time), TOLERANCE);
    }

    @ParameterizedTest
    @EnumSource(CompoundingFrequency.class)
    @DisplayName("Test Discrete Compounding Future Value Logic for all frequencies")
    void testSpecificDiscreteCalculation(CompoundingFrequency frequency) {
        CompoundingStrategy strategy = new DiscreteCompoundingStrategy(frequency);
        double principal = 100;
        double rate = 0.05;
        double time = 1.5;
        int m = frequency.getPeriodsPerYear();

        double expectedFV = principal * Math.pow(1.0 + (rate / m), m * time);

        assertEquals(expectedFV, strategy.futureValue(principal, rate, time), TOLERANCE);
    }

    @Test
    @DisplayName("Test Null Safety")
    void testNullSafety() {
        assertThrows(NullPointerException.class, () -> new DiscreteCompoundingStrategy(null));
    }
}
