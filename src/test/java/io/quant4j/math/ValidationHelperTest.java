package io.quant4j.math;

import io.quant4j.rates.Frequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.quant4j.math.ValidationHelper.validateTimeCoherence;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationHelperTest {

    @Test
    @DisplayName("Test Time vs Frequency Coherence")
    void testTimeCoherence() {
        // 1.5 years is NOT coherent with ANNUAL compounding (1.5 periods)
        assertThrows(IllegalArgumentException.class, () ->
                validateTimeCoherence(1.5, Frequency.ANNUALLY)
        );

        // 1.5 years is coherent with SEMI_ANNUAL compounding (3 periods)
        assertDoesNotThrow(() ->
                validateTimeCoherence(1.5, Frequency.SEMI_ANNUALLY)
        );
    }
}
