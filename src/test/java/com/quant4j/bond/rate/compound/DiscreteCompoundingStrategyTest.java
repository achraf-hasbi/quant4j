package com.quant4j.bond.rate.compound;

import com.quant4j.bond.enumeration.Frequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DiscreteCompoundingStrategyTest {

    private static final double TOLERANCE = 1.0e-9;

    @ParameterizedTest
    @EnumSource(value = Frequency.class, names = "CONTINUOUS", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Test Discrete Compounding Discount Factor Logic for all frequencies")
    void testDiscreteDiscountFactor(Frequency frequency) {
        CompoundingStrategy strategy = frequency.getCompoundingStrategy();
        double rate = 0.05;
        double time = 1.5;
        int m = frequency.getPeriodsPerYear();

        double expected = Math.pow(1.0 + (rate / m), -m * time);
        assertEquals(expected, strategy.discountFactor(rate, time), TOLERANCE);
    }

    @ParameterizedTest
    @EnumSource(value = Frequency.class, names = "CONTINUOUS", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Test Discrete Compounding Accumulation Factor Logic for all frequencies")
    void testDiscreteAccumulationFactor(Frequency frequency) {
        CompoundingStrategy strategy = frequency.getCompoundingStrategy();
        double rate = 0.05;
        double time = 2.0;
        int m = frequency.getPeriodsPerYear();

        double expected = Math.pow(1.0 + (rate / m), m * time);
        assertEquals(expected, strategy.accumulationFactor(rate, time), TOLERANCE);
    }

    @ParameterizedTest
    @EnumSource(value = Frequency.class, names = "CONTINUOUS", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Test Discrete Compounding Future Value Logic for all frequencies")
    void testSpecificDiscreteCalculation(Frequency frequency) {
        CompoundingStrategy strategy = frequency.getCompoundingStrategy();
        double principal = 100;
        double rate = 0.05;
        double time = 1.5;
        int m = frequency.getPeriodsPerYear();

        double expectedFV = principal * Math.pow(1.0 + (rate / m), m * time);

        assertEquals(expectedFV, strategy.futureValue(principal, rate, time), TOLERANCE);
    }
}