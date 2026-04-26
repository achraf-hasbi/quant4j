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

/**
 * Defines the contract for pricing bonds.
 */
public interface BondPricer {

    /**
     * Calculates the price of a bond.
     *
     * @param bond The bond to price.
     * @return The calculated price.
     * @throws NullPointerException if {@code bond} is null
     */
    double price(Bond bond);
}