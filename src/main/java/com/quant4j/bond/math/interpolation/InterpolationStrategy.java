package com.quant4j.bond.math.interpolation;

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
