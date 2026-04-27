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
package io.quant4j.bond.duration;

import io.quant4j.bond.Bond;
import io.quant4j.rates.compounding.CompoundingStrategy;

import java.util.Map;
import java.util.Objects;

/**
 * Computes the Macaulay duration, modified duration and DV01 of a bond
 * using a yield and compounding convention supplied at construction time.
 *
 * <p>Macaulay duration formula:</p>
 * <pre>
 *   D_mac = (1 / P) * sum[ t * CF(t) * DF(y, t) ]
 * </pre>
 *
 * <p>The Macaulay-to-modified conversion is delegated to the injected
 * {@link CompoundingStrategy#adjustMacaulayToModified}, so each convention
 * carries its own adjustment rule.</p>
 */
public class YieldBondDurationCalculator implements BondDurationCalculator {

    private final double yield;
    private final CompoundingStrategy yieldCompoundingStrategy;

    /**
     * Constructs a duration calculator for a specific yield and compounding convention.
     *
     * @param yield                    The yield to maturity (decimal).
     * @param yieldCompoundingStrategy The compounding convention for the yield.
     */
    public YieldBondDurationCalculator(double yield, CompoundingStrategy yieldCompoundingStrategy) {
        Objects.requireNonNull(yieldCompoundingStrategy, "Compounding strategy cannot be null");
        if (!Double.isFinite(yield)) {
            throw new IllegalArgumentException("Yield must be a finite number");
        }
        this.yield = yield;
        this.yieldCompoundingStrategy = yieldCompoundingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double macaulayDuration(Bond bond, double price) {
        Objects.requireNonNull(bond, "Bond cannot be null");
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        double weightedSum = 0.0;

        for (Map.Entry<Double, Double> entry : bond.getCashflows().entrySet()) {
            double t = entry.getKey();
            double cashflow = entry.getValue();
            double pv = cashflow * yieldCompoundingStrategy.discountFactor(yield, t);
            weightedSum += t * pv;
        }

        return weightedSum / price;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double modifiedDuration(Bond bond, double price) {
        return yieldCompoundingStrategy.adjustMacaulayToModified(macaulayDuration(bond, price), yield);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double dv01(Bond bond, double price) {
        return modifiedDuration(bond, price) * price * 0.0001;
    }
}
