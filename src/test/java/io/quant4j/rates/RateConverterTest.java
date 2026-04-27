/*
 * Copyright 2025 Achraf Hasbi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quant4j.rates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RateConverterTest {

    private static final double TOLERANCE = 1.0e-9;

    @ParameterizedTest
    @EnumSource(Frequency.class)
    @DisplayName("Test Rate Conversion: Discrete to Continuous")
    void testDiscreteToContinuous(Frequency frequency) {
        double discreteRate = 0.10;
        int m = frequency.getPeriodsPerYear();

        double continuousRate = RateConverter.discreteToContinuous(discreteRate, frequency);

        assertEquals(m * Math.log(1 + discreteRate/m), continuousRate, TOLERANCE);
    }


    @ParameterizedTest
    @EnumSource(Frequency.class)
    @DisplayName("Test Rate Conversion: Continuous to Discrete")
    void testContinuousToDiscrete(Frequency frequency) {
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
                Frequency.ANNUALLY,
                Frequency.SEMI_ANNUALLY
        );

        double expected = 2.0 * (Math.sqrt(1.10) - 1.0);
        assertEquals(expected, targetRate, TOLERANCE);

        // Verify round trip
        double backToAnnual = RateConverter.convertDiscreteRates(
                targetRate,
                Frequency.SEMI_ANNUALLY,
                Frequency.ANNUALLY
        );
        assertEquals(sourceRate, backToAnnual, TOLERANCE);
    }

    @Test
    @DisplayName("Test Null Safety")
    void testNullSafety() {
        assertThrows(NullPointerException.class, () -> RateConverter.discreteToContinuous(0.05, null));
        assertThrows(NullPointerException.class, () -> RateConverter.continuousToDiscrete(0.05, null));
    }

    @ParameterizedTest
    @EnumSource(Frequency.class)
    @DisplayName("Same source and target frequency returns the original rate unchanged")
    void testConvertDiscreteRates_SameFrequency_ReturnsUnchanged(Frequency frequency) {
        double rate = 0.08;
        double result = RateConverter.convertDiscreteRates(rate, frequency, frequency);
        assertEquals(rate, result, TOLERANCE);
    }
}
