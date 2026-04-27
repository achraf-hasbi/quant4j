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

import java.util.NavigableMap;

/**
 * Strategy interface for interpolating values from a dataset.
 */
public interface InterpolationStrategy {

    /**
     * Interpolates a value for a given key x based on the provided data map.
     *
     * @param data the known data points (key -> value), sorted.
     * @param x    the point to interpolate.
     * @return the interpolated value.
     */
    double interpolate(NavigableMap<Double, Double> data, double x);
}
