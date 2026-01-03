package com.quant4j.bond.rate;

import com.quant4j.bond.rate.compound.CompoundingFrequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RateConverterTest {

    private static final double TOLERANCE = 1.0e-9;

    @ParameterizedTest
    @EnumSource(CompoundingFrequency.class)
    @DisplayName("Test Rate Conversion: Discrete to Continuous")
    void testDiscreteToContinuous(CompoundingFrequency frequency) {
        double discreteRate = 0.10;
        int m = frequency.getPeriodsPerYear();

        double continuousRate = RateConverter.discreteToContinuous(discreteRate, frequency);

        assertEquals(m * Math.log(1 + discreteRate/m), continuousRate, TOLERANCE);
    }


    @ParameterizedTest
    @EnumSource(CompoundingFrequency.class)
    @DisplayName("Test Rate Conversion: Continuous to Discrete")
    void testContinuousToDiscrete(CompoundingFrequency frequency) {
        double continuousRate = 0.07;
        int m = frequency.getPeriodsPerYear();

        double discreteRate = RateConverter.continuousToDiscrete(continuousRate, frequency);

        assertEquals(m * (Math.exp(continuousRate / m) - 1), discreteRate, TOLERANCE);
    }

    @Test
    @DisplayName("Test Rate Conversion: Discrete to Discrete")
    void testDiscreteToDiscrete() {
        // Convert 10% Annual to Semi-Annual
        // (1 + 0.10)^1 = (1 + r/2)^2
        // 1.10 = (1 + r/2)^2
        // sqrt(1.10) = 1 + r/2
        // r/2 = sqrt(1.10) - 1
        // r = 2 * (sqrt(1.10) - 1)

        double sourceRate = 0.10;
        double targetRate = RateConverter.convertDiscreteRates(
                sourceRate,
                CompoundingFrequency.ANNUALLY,
                CompoundingFrequency.SEMI_ANNUALLY
        );

        double expected = 2.0 * (Math.sqrt(1.10) - 1.0);
        assertEquals(expected, targetRate, TOLERANCE);

        // Verify round trip
        double backToAnnual = RateConverter.convertDiscreteRates(
                targetRate,
                CompoundingFrequency.SEMI_ANNUALLY,
                CompoundingFrequency.ANNUALLY
        );
        assertEquals(sourceRate, backToAnnual, TOLERANCE);
    }

    @Test
    @DisplayName("Test Null Safety")
    void testNullSafety() {
        assertThrows(NullPointerException.class, () -> RateConverter.discreteToContinuous(0.05, null));
        assertThrows(NullPointerException.class, () -> RateConverter.continuousToDiscrete(0.05, null));
    }
}
