package io.quant4j.rates.compounding;

import io.quant4j.rates.Frequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("Zero periodsPerYear throws IllegalArgumentException")
    void testPeriodsPerYear_Zero_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new DiscreteCompoundingStrategy(0));
    }

    @Test
    @DisplayName("Negative periodsPerYear throws IllegalArgumentException")
    void testPeriodsPerYear_Negative_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new DiscreteCompoundingStrategy(-2));
    }

    @Test
    @DisplayName("forwardRate with t1 == t2 throws IllegalArgumentException")
    void testForwardRate_EqualTimes_Throws() {
        CompoundingStrategy strategy = new DiscreteCompoundingStrategy(2);
        assertThrows(IllegalArgumentException.class,
                () -> strategy.forwardRate(0.03, 1.0, 0.05, 1.0));
    }

    @Test
    @DisplayName("forwardRate with t1 > t2 throws IllegalArgumentException")
    void testForwardRate_T1GreaterThanT2_Throws() {
        CompoundingStrategy strategy = new DiscreteCompoundingStrategy(2);
        assertThrows(IllegalArgumentException.class,
                () -> strategy.forwardRate(0.05, 2.0, 0.03, 1.0));
    }

    @Test
    @DisplayName("adjustMacaulayToModified divides Macaulay duration by (1 + yield/m)")
    void testAdjustMacaulayToModified() {
        DiscreteCompoundingStrategy semiAnnual = new DiscreteCompoundingStrategy(2);
        double macaulay = 4.5;
        double yield = 0.05;
        // D_mod = 4.5 / (1 + 0.05/2) = 4.5 / 1.025
        assertEquals(macaulay / 1.025, semiAnnual.adjustMacaulayToModified(macaulay, yield), 1e-9);
    }

    @ParameterizedTest(name = "Discrete Forward (Quarterly): r1={0}, t1={1}, r2={2}, t2={3}")
    @CsvSource({
            "0.030, 0.25, 0.032, 0.50, 0.034",
            "0.032, 0.50, 0.034, 0.75, 0.038",
            "0.034, 0.75, 0.035, 1.00, 0.038",
            "0.035, 1.00, 0.036, 1.25, 0.040",
            "0.036, 1.25, 0.037, 1.50, 0.042"
    })
    @DisplayName("Test Discrete Forward Rates (Quarterly Compounding)")
    void testDiscreteForwardRate(double r1, double t1, double r2, double t2, double expectedForward) {
        Frequency frequency = Frequency.QUARTERLY;
        CompoundingStrategy strategy = new DiscreteCompoundingStrategy(frequency.getPeriodsPerYear());
        
        double actualForward = strategy.forwardRate(r1, t1, r2, t2);
        // Using 1e-4 tolerance to verify up to 4 decimal places
        assertEquals(expectedForward, actualForward, 1.0e-4);
    }
}