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
