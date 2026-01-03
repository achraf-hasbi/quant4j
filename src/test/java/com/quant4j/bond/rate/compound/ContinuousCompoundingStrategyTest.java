package com.quant4j.bond.rate.compound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContinuousCompoundingStrategyTest {

    private static final double TOLERANCE = 1.0e-9;

    @Test
    @DisplayName("Test Continuous Compounding Discount Factor")
    void testContinuousDiscountFactor() {
        CompoundingStrategy strategy = new ContinuousCompoundingStrategy();
        double rate = 0.05;
        double time = 2.0;

        double expected = Math.exp(-rate*time);
        assertEquals(expected, strategy.discountFactor(rate, time), TOLERANCE);
    }

    @Test
    @DisplayName("Test Continuous Compounding Future Value")
    void testContinuousFutureValue() {
        CompoundingStrategy strategy = new ContinuousCompoundingStrategy();
        double principal = 1000.0;
        double rate = 0.10;
        double time = 1.5;

        double expected = principal * Math.exp(rate * time);
        assertEquals(expected, strategy.futureValue(principal, rate, time), TOLERANCE);
    }
}
