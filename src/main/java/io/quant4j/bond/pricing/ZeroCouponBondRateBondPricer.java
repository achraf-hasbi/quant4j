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
package io.quant4j.bond.pricing;

import io.quant4j.math.interpolation.InterpolationStrategy;
import io.quant4j.bond.Bond;
import io.quant4j.rates.compounding.CompoundingStrategy;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Prices a bond by discounting each cash flow using its corresponding
 * zero coupon rate, interpolated from a provided zero rate curve.
 *
 * <p>The zero rate curve maps time (in years) to zero coupon rates.
 * Each cash flow is discounted individually at the rate obtained by
 * interpolating the curve at the cash flow's time.</p>
 *
 */
public class ZeroCouponBondRateBondPricer implements BondPricer {

    private final TreeMap<Double, Double> zeroCurve;
    private final InterpolationStrategy interpolationStrategy;
    private final CompoundingStrategy compoundingStrategy;

    /**
     * Constructs a pricer with a zero coupon rate curve.
     *
     * @param zeroCurve             The zero coupon rate curve (Time in years -&gt; Zero Rate);
     *                              must not be {@code null} or empty.
     * @param interpolationStrategy The strategy used to interpolate rates from the curve;
     *                              must not be {@code null}.
     * @param compoundingStrategy   The compounding convention used to compute discount factors;
     *                              must not be {@code null}.
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if {@code zeroCurve} is empty
     */
    public ZeroCouponBondRateBondPricer(Map<Double, Double> zeroCurve,
                                        InterpolationStrategy interpolationStrategy,
                                        CompoundingStrategy compoundingStrategy) {
        Objects.requireNonNull(zeroCurve, "Zero curve cannot be null");
        Objects.requireNonNull(interpolationStrategy, "Interpolation strategy cannot be null");
        Objects.requireNonNull(compoundingStrategy, "Compounding strategy cannot be null");
        if (zeroCurve.isEmpty()) {
            throw new IllegalArgumentException("Zero curve cannot be empty");
        }
        this.zeroCurve = new TreeMap<>(zeroCurve);
        this.interpolationStrategy = interpolationStrategy;
        this.compoundingStrategy = compoundingStrategy;
    }

    /**
     * Prices the bond by summing the present value of each cash flow,
     * discounted at the zero coupon rate interpolated from the zero curve.
     *
     * @param bond The bond to price; must not be {@code null}.
     * @return The sum of discounted cash flows using per-tenor zero rates.
     * @throws NullPointerException if {@code bond} is null
     */
    @Override
    public double price(Bond bond) {
        Objects.requireNonNull(bond, "Bond cannot be null");

        Map<Double, Double> cashflows = bond.getCashflows();
        double price = 0.0;

        for (Map.Entry<Double, Double> entry : cashflows.entrySet()) {
            double t = entry.getKey();
            double cashflow = entry.getValue();

            double zeroRate = interpolationStrategy.interpolate(zeroCurve, t);
            double discountFactor = compoundingStrategy.discountFactor(zeroRate, t);

            price += cashflow * discountFactor;
        }

        return price;
    }
}
