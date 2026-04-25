package com.quant4j.math;

import com.quant4j.math.interpolation.LinearInterpolationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Linear Interpolation Strategy Tests")
class LinearInterpolationStrategyTest {

    private final LinearInterpolationStrategy strategy = new LinearInterpolationStrategy();
    private static final double TOLERANCE = 1.0e-9;

    @Test
    @DisplayName("Interpolate should throw NPE when data map is null")
    void testInterpolate_NullData() {
        assertThrows(NullPointerException.class, () -> strategy.interpolate(null, 1.0),
                "Should throw NullPointerException when data map is null");
    }

    @Test
    @DisplayName("Interpolate should return 0.0 when data map is empty")
    void testInterpolate_EmptyData() {
        NavigableMap<Double, Double> data = new TreeMap<>();
        double result = strategy.interpolate(data, 1.0);
        assertEquals(0.0, result, TOLERANCE, "Should return 0.0 for empty map");
    }

    @Test
    @DisplayName("Interpolate should extrapolate backward (flat) when x is before first key")
    void testInterpolate_ExtrapolateBackward() {
        NavigableMap<Double, Double> data = new TreeMap<>();
        data.put(1.0, 0.05);
        data.put(2.0, 0.06);

        double x = 0.5;
        double result = strategy.interpolate(data, x);

        assertEquals(0.05, result, TOLERANCE, "Should flat extrapolate using the first value");
    }

    @Test
    @DisplayName("Interpolate should extrapolate forward (flat) when x is after last key")
    void testInterpolate_ExtrapolateForward() {
        NavigableMap<Double, Double> data = new TreeMap<>();
        data.put(1.0, 0.05);
        data.put(2.0, 0.06);

        double x = 2.5;
        double result = strategy.interpolate(data, x);

        assertEquals(0.06, result, TOLERANCE, "Should flat extrapolate using the last value");
    }

    @Test
    @DisplayName("Interpolate should return exact value when x matches a key")
    void testInterpolate_ExactMatch() {
        NavigableMap<Double, Double> data = new TreeMap<>();
        data.put(1.0, 0.05);
        data.put(2.0, 0.06);

        double x = 1.0;
        double result = strategy.interpolate(data, x);
        assertEquals(0.05, result, TOLERANCE, "Should return exact value for existing key");

        x = 2.0;
        result = strategy.interpolate(data, x);
        assertEquals(0.06, result, TOLERANCE, "Should return exact value for existing key");
    }

    @Test
    @DisplayName("Interpolate should handle multiple points correctly")
    void testInterpolate_MultiplePoints() {
        NavigableMap<Double, Double> data = new TreeMap<>();
        data.put(1.0, 0.02);
        data.put(2.0, 0.03);
        data.put(3.0, 0.05);
        data.put(5.0, 0.06);

        // Interpolate between 1.0 and 2.0
        double x = 1.5;
        // Expected: 0.02 + (1.5 - 1.0) * (0.03 - 0.02) / (2.0 - 1.0) = 0.025
        double expected = 0.025;

        assertEquals(expected, strategy.interpolate(data, x), TOLERANCE);

        // Interpolate between 2.0 and 3.0
        x = 2.5;
        // Expected: 0.03 + (2.5 - 2.0) * (0.05 - 0.03) / (3.0 - 2.0) = 0.03 + 0.5 * 0.02 = 0.04
        expected = 0.04;
        
        assertEquals(expected, strategy.interpolate(data, x), TOLERANCE);

        // Interpolate between 3.0 and 5.0
        x = 4.0;
        // Expected: 0.05 + (4.0 - 3.0) * (0.06 - 0.05) / (5.0 - 3.0) = 0.05 + 1.0 * 0.01 / 2.0 = 0.055
        expected = 0.055;

        assertEquals(expected, strategy.interpolate(data, x), TOLERANCE);
    }
}
