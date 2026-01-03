package com.quant4j.bond;

import com.quant4j.bond.rate.compound.CompoundingFrequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.quant4j.bond.ValidationHelper.validateTimeCoherence;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationHelperTest {

    @Test
    @DisplayName("Test Time vs Frequency Coherence")
    void testTimeCoherence() {
        // 1.5 years is NOT coherent with ANNUAL compounding (1.5 periods)
        assertThrows(IllegalArgumentException.class, () ->
                validateTimeCoherence(1.5, CompoundingFrequency.ANNUALLY)
        );

        // 1.5 years is coherent with SEMI_ANNUAL compounding (3 periods)
        assertDoesNotThrow(() ->
                validateTimeCoherence(1.5, CompoundingFrequency.SEMI_ANNUALLY)
        );
    }
}
