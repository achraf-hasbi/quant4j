package com.quant4j.rates.compounding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
    @DisplayName("Test Continuous Compounding Present Value")
    void testContinuousPresentValue() {
        // From testContinuousFutureValue: 1000 * exp(0.10 * 1.5) = 1161.834242728
        CompoundingStrategy strategy = new ContinuousCompoundingStrategy();
        double futureAmount = 1161.834242728;
        double rate = 0.10;
        double time = 1.5;
        double expectedPrincipal = 1000.0;

        assertEquals(expectedPrincipal, strategy.presentValue(futureAmount, rate, time), TOLERANCE);
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

    @Test
    @DisplayName("adjustMacaulayToModified returns Macaulay unchanged under continuous compounding")
    void testAdjustMacaulayToModified() {
        ContinuousCompoundingStrategy strategy = new ContinuousCompoundingStrategy();
        double macaulay = 4.3235;
        assertEquals(macaulay, strategy.adjustMacaulayToModified(macaulay, 0.07), 1e-9);
    }

    @ParameterizedTest(name = "Continuous Forward: r1={0}, t1={1}, r2={2}, t2={3}")
    @CsvSource({
            "0.030, 0.25, 0.032, 0.50, 0.034",
            "0.032, 0.50, 0.034, 0.75, 0.038",
            "0.034, 0.75, 0.035, 1.00, 0.038",
            "0.035, 1.00, 0.036, 1.25, 0.040",
            "0.036, 1.25, 0.037, 1.50, 0.042"
    })
    @DisplayName("Test Continuous Forward Rates Calculation")
    void testContinuousForwardRate(double r1, double t1, double r2, double t2, double expectedForward) {
        CompoundingStrategy strategy = new ContinuousCompoundingStrategy();
        double actualForward = strategy.forwardRate(r1, t1, r2, t2);
        assertEquals(expectedForward, actualForward, TOLERANCE);
    }
}
