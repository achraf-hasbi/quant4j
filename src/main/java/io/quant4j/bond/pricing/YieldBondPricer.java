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

import io.quant4j.bond.Bond;
import io.quant4j.rates.compounding.CompoundingStrategy;

import java.util.Map;
import java.util.Objects;

/**
 * Prices a bond using a single Yield to Maturity (YTM).
 *
 * <p>The yield's compounding convention is specified explicitly and may differ
 * from the bond's coupon frequency.</p>
 */
public class YieldBondPricer implements BondPricer {

    private final double yield;
    private final CompoundingStrategy yieldCompoundingStrategy;

    /**
     * Constructs a pricer with a specific yield and its compounding convention.
     *
     * @param yield                    The annual yield to maturity (decimal); must be finite.
     * @param yieldCompoundingStrategy The compounding convention for the yield; must not be {@code null}.
     * @throws IllegalArgumentException if {@code yield} is NaN or infinite
     */
    public YieldBondPricer(double yield, CompoundingStrategy yieldCompoundingStrategy) {
        Objects.requireNonNull(yieldCompoundingStrategy, "Yield compounding strategy cannot be null");
        if (!Double.isFinite(yield)) {
            throw new IllegalArgumentException("Yield must be a finite number, got: " + yield);
        }
        this.yield = yield;
        this.yieldCompoundingStrategy = yieldCompoundingStrategy;
    }

    /**
     * Prices the bond by discounting all cash flows — coupons and the final face value
     * redemption — at the single yield supplied at construction time.
     *
     * <p>Cash flows are sourced from {@link Bond#getCashflows()}, which is the canonical
     * schedule for the bond and ensures consistency with other calculators in this library.</p>
     *
     * @param bond The bond to price; must not be {@code null}.
     * @return The present value of all cash flows discounted at the YTM.
     * @throws NullPointerException if {@code bond} is null
     */
    @Override
    public double price(Bond bond) {
        Objects.requireNonNull(bond, "Bond cannot be null");

        double price = 0.0;
        for (Map.Entry<Double, Double> entry : bond.getCashflows().entrySet()) {
            price += entry.getValue() * yieldCompoundingStrategy.discountFactor(yield, entry.getKey());
        }
        return price;
    }
}