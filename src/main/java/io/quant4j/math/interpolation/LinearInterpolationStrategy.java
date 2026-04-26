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
package io.quant4j.math.interpolation;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;

/**
 * Implementation of linear interpolation.
 * Falls back to flat extrapolation if the requested point is outside the range.
 */
public class LinearInterpolationStrategy implements InterpolationStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public double interpolate(NavigableMap<Double, Double> data, double x) {
        Objects.requireNonNull(data, "Data map cannot be null");
        if (data.isEmpty()) {
            return 0.0;
        }

        Map.Entry<Double, Double> floor = data.floorEntry(x);
        Map.Entry<Double, Double> ceiling = data.ceilingEntry(x);

        if (floor == null && ceiling == null) {
            return 0.0;
        }

        // x is before the first point on the curve. Flat extrapolation backward.
        if (floor == null) {
            return ceiling.getValue();
        }

        // x is after the last point. Flat extrapolation forward.
        if (ceiling == null) {
            return floor.getValue();
        }

        // Exact match
        if (floor.getKey().equals(ceiling.getKey())) {
            return floor.getValue();
        }

        // Linear Interpolation: y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
        double x1 = floor.getKey();
        double y1 = floor.getValue();
        double x2 = ceiling.getKey();
        double y2 = ceiling.getValue();

        return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
    }
}
