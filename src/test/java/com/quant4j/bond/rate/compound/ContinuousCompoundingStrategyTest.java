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

        // exp(-0.05 * 2.0) = 0.904837418036
        double expected = 0.904837418036;
        assertEquals(expected, strategy.discountFactor(rate, time), TOLERANCE);
    }

    @Test
    @DisplayName("Test Continuous Compounding Accumulation Factor")
    void testContinuousAccumulationFactor() {
        CompoundingStrategy strategy = new ContinuousCompoundingStrategy();
        double rate = 0.08;
        double time = 3.0;

        // exp(0.08 * 3.0) = 1.271249150321
        double expected = 1.271249150321;
        assertEquals(expected, strategy.accumulationFactor(rate, time), TOLERANCE);
    }

    @Test
    @DisplayName("Test Continuous Compounding Future Value")
    void testContinuousFutureValue() {
        CompoundingStrategy strategy = new ContinuousCompoundingStrategy();
        double principal = 1000.0;
        double rate = 0.10;
        double time = 1.5;

        // 1000 * exp(0.10 * 1.5) = 1000 * 1.161834242728
        double expected = 1161.834242728;
        assertEquals(expected, strategy.futureValue(principal, rate, time), TOLERANCE);
    }

    @Test
    @DisplayName("Test Rate from Discount Factor (Continuous)")
    void testRateFromDiscountFactor() {
        CompoundingStrategy strategy = new ContinuousCompoundingStrategy();
        double time = 2.0;
        // DF for rate 0.05 and time 2.0 is approx 0.904837418036
        double df = 0.904837418036;
        double expectedRate = 0.05;

        double actualRate = strategy.rateFromDiscountFactor(df, time);
        assertEquals(expectedRate, actualRate, TOLERANCE);
    }
}
