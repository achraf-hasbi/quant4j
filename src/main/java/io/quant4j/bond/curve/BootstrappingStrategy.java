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
package io.quant4j.bond.curve;

import io.quant4j.math.interpolation.InterpolationStrategy;
import io.quant4j.bond.Bond;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Strategy for bootstrapping a zero-coupon (spot rate) curve from a set of coupon bonds.
 *
 * <p>Bootstrapping iterates over bonds in ascending maturity order, discounting all
 * intermediate cashflows with already-derived spot rates and solving for the unknown
 * zero rate at each bond's maturity.</p>
 */
public interface BootstrappingStrategy {

    /**
     * Bootstraps a spot rate curve assuming all bonds trade at par (price equals face value).
     *
     * @param bonds                 the benchmark bonds; must not be null or empty.
     * @param interpolationStrategy the strategy used to look up spot rates for maturities
     *                              not yet in the curve; must not be null.
     * @return a {@link NavigableMap} from maturity in years to zero (spot) rate,
     *         sorted ascending by maturity, including any initial seed rates.
     * @throws IllegalArgumentException if {@code bonds} is empty.
     * @throws NullPointerException     if {@code bonds} or {@code interpolationStrategy} is null.
     */
    NavigableMap<Double, Double> bootstrapFromParBonds(List<Bond> bonds, InterpolationStrategy interpolationStrategy);

    /**
     * Bootstraps a spot rate curve from bonds trading at given market prices.
     *
     * @param bonds                 the benchmark bonds; must not be null or empty.
     * @param marketPrices          observed market price per bond; must not be null and must
     *                              contain an entry for every bond in {@code bonds}.
     * @param interpolationStrategy the strategy used to look up spot rates for maturities
     *                              not yet in the curve; must not be null.
     * @return a {@link NavigableMap} from maturity in years to zero (spot) rate,
     *         sorted ascending by maturity, including any initial seed rates.
     * @throws IllegalArgumentException if {@code bonds} is empty or a market price is missing.
     * @throws NullPointerException     if any argument is null.
     */
    NavigableMap<Double, Double> bootstrapFromMarketPrices(List<Bond> bonds,
                                                            Map<Bond, Double> marketPrices,
                                                            InterpolationStrategy interpolationStrategy);
}
