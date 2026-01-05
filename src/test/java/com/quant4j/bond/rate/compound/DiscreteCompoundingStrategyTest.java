package com.quant4j.bond.rate.compound;

import com.quant4j.bond.enumeration.Frequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscreteCompoundingStrategyTest {

    private static final double TOLERANCE = 1.0e-9;

    @ParameterizedTest
    @CsvSource({
            "ANNUALLY, 0.9294286409033649",
            "SEMI_ANNUALLY, 0.928599410919749",
            "QUARTERLY, 0.9281748759929555",
            "MONTHLY, 0.9278880560989459"
    })
    @DisplayName("Test Discrete Compounding Discount Factor with known values")
    void testDiscreteDiscountFactor(Frequency frequency, double expected) {
        CompoundingStrategy strategy = frequency.getCompoundingStrategy();
        double rate = 0.05;
        double time = 1.5;

        assertEquals(expected, strategy.discountFactor(rate, time), TOLERANCE);
    }

    @ParameterizedTest
    @CsvSource({
            "ANNUALLY, 1.1025",
            "SEMI_ANNUALLY, 1.103812890625",
            "QUARTERLY, 1.104486101166677",
            "MONTHLY, 1.104941335558328"
    })
    @DisplayName("Test Discrete Compounding Accumulation Factor with known values")
    void testDiscreteAccumulationFactor(Frequency frequency, double expected) {
        CompoundingStrategy strategy = frequency.getCompoundingStrategy();
        double rate = 0.05;
        double time = 2.0;

        assertEquals(expected, strategy.accumulationFactor(rate, time), TOLERANCE);
    }

    @ParameterizedTest
    @CsvSource({
            "ANNUALLY, 107.59298304257578",
            "SEMI_ANNUALLY, 107.6890625",
            "QUARTERLY, 107.73831805458065",
            "MONTHLY, 107.77162109449165"
    })
    @DisplayName("Test Discrete Compounding Future Value with known values")
    void testSpecificDiscreteCalculation(Frequency frequency, double expected) {
        CompoundingStrategy strategy = frequency.getCompoundingStrategy();
        double principal = 100;
        double rate = 0.05;
        double time = 1.5;

        assertEquals(expected, strategy.futureValue(principal, rate, time), TOLERANCE);
    }

    @ParameterizedTest
    @CsvSource({
            "ANNUALLY, 107.59298304257578",
            "SEMI_ANNUALLY, 107.6890625",
            "QUARTERLY, 107.73831805458065",
            "MONTHLY, 107.77162109449165"
    })
    @DisplayName("Test Discrete Compounding Present Value with known values")
    void testSpecificDiscretePresentValueCalculation(Frequency frequency, double futureAmount) {
        CompoundingStrategy strategy = frequency.getCompoundingStrategy();
        double expectedPrincipal = 100;
        double rate = 0.05;
        double time = 1.5;

        assertEquals(expectedPrincipal, strategy.presentValue(futureAmount, rate, time), TOLERANCE);
    }

    @ParameterizedTest
    @CsvSource({
            "ANNUALLY, 0.9294286409033649",
            "SEMI_ANNUALLY, 0.928599410919749",
            "QUARTERLY, 0.9281748759929555",
            "MONTHLY, 0.9278880560989459"
    })
    @DisplayName("Test Rate from Discount Factor for discrete frequencies with known values")
    void testRateFromDiscountFactor(Frequency frequency, double df) {
        CompoundingStrategy strategy = frequency.getCompoundingStrategy();
        double expectedRate = 0.05;
        double time = 1.5;

        double actualRate = strategy.rateFromDiscountFactor(df, time);
        assertEquals(expectedRate, actualRate, TOLERANCE);
    }
}