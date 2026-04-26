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

import io.quant4j.rates.Frequency;
import io.quant4j.math.interpolation.LinearInterpolationStrategy;
import io.quant4j.bond.Bond;
import io.quant4j.rates.compounding.ContinuousCompoundingStrategy;
import io.quant4j.rates.compounding.DiscreteCompoundingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class ZeroCouponBondRateBondPricerTest {

    private static final double TOLERANCE = 1e-6;

    private LinearInterpolationStrategy linearInterpolation;
    private ContinuousCompoundingStrategy continuousCompounding;
    private DiscreteCompoundingStrategy semiAnnualCompounding;

    @BeforeEach
    void setUp() {
        linearInterpolation = new LinearInterpolationStrategy();
        continuousCompounding = new ContinuousCompoundingStrategy();
        semiAnnualCompounding = new DiscreteCompoundingStrategy(2);
    }

    @Test
    @DisplayName("Flat discrete zero curve at coupon rate should price bond at par")
    void testPriceAtParWithFlatDiscreteZeroCurve() {
        // Flat zero curve at 5% (semi-annual discrete)
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(Map.of(
                0.5, 0.05,
                1.0, 0.05,
                1.5, 0.05,
                2.0, 0.05
        ));

        // 2-year bond, 5% annual coupon, semi-annual payments, face value 1000
        Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
        ZeroCouponBondRateBondPricer pricer = new ZeroCouponBondRateBondPricer(zeroCurve, linearInterpolation, semiAnnualCompounding);

        double price = pricer.price(bond);

        assertEquals(1000.0, price, TOLERANCE);
    }

    @Test
    @DisplayName("Rising zero curve: rates above coupon rate → bond prices below par")
    void testPriceBelowParWithRisingZeroCurve() {
        // Zero curve (continuous rates): 0.5→4%, 1.0→4.2%, 1.5→4.4%, 2.0→4.6%, 2.5→4.8%
        // Bond: face=100, coupon=4% semi-annual, maturity=2.5yr → coupon=2.0 per period
        // All cash flow times match curve nodes exactly, so no interpolation is needed.
        //
        // Derivation (continuous discounting: DF = e^(-r*t)):
        //   t=0.5: 2.0 × e^(-0.04×0.5)  = 2.0 × 0.98020 = 1.96040
        //   t=1.0: 2.0 × e^(-0.042×1.0) = 2.0 × 0.95887 = 1.91774
        //   t=1.5: 2.0 × e^(-0.044×1.5) = 2.0 × 0.93607 = 1.87215
        //   t=2.0: 2.0 × e^(-0.046×2.0) = 2.0 × 0.91208 = 1.82416
        //   t=2.5: 102 × e^(-0.048×2.5) = 102 × 0.88692 = 90.466
        //   Total ≈ 98.04
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(Map.of(
                0.5, 0.04,
                1.0, 0.042,
                1.5, 0.044,
                2.0, 0.046,
                2.5, 0.048
        ));

        Bond bond = new Bond(100, 0.04, 2.5, Frequency.SEMI_ANNUALLY);
        ZeroCouponBondRateBondPricer pricer = new ZeroCouponBondRateBondPricer(zeroCurve, linearInterpolation, continuousCompounding);

        double price = pricer.price(bond);

        assertEquals(98.04, price, 0.001);
    }

    @Test
    @DisplayName("Null zero curve throws NullPointerException")
    void testNullZeroCurveThrows() {
        assertThrows(NullPointerException.class,
                () -> new ZeroCouponBondRateBondPricer(null, linearInterpolation, continuousCompounding));
    }

    @Test
    @DisplayName("Empty zero curve throws IllegalArgumentException")
    void testEmptyZeroCurveThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ZeroCouponBondRateBondPricer(new TreeMap<>(), linearInterpolation, continuousCompounding));
    }

    @Test
    @DisplayName("Null interpolation strategy throws NullPointerException")
    void testNullInterpolationStrategyThrows() {
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(Map.of(1.0, 0.05));

        assertThrows(NullPointerException.class,
                () -> new ZeroCouponBondRateBondPricer(zeroCurve, null, continuousCompounding));
    }

    @Test
    @DisplayName("Null compounding strategy throws NullPointerException")
    void testNullCompoundingStrategyThrows() {
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(Map.of(1.0, 0.05));

        assertThrows(NullPointerException.class,
                () -> new ZeroCouponBondRateBondPricer(zeroCurve, linearInterpolation, null));
    }

    @Test
    @DisplayName("Null bond throws NullPointerException")
    void testNullBondThrows() {
        TreeMap<Double, Double> zeroCurve = new TreeMap<>(Map.of(1.0, 0.05, 2.0, 0.055));
        ZeroCouponBondRateBondPricer pricer = new ZeroCouponBondRateBondPricer(
                zeroCurve, linearInterpolation, continuousCompounding);

        assertThrows(NullPointerException.class, () -> pricer.price(null));
    }
}