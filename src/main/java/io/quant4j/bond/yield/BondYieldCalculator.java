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
package io.quant4j.bond.yield;

import io.quant4j.bond.Bond;

/**
 * Defines the contract for computing the yield to maturity of a bond given its price.
 */
public interface BondYieldCalculator {

    /**
     * Calculates the yield to maturity of a bond that produces the given price,
     * under the compounding convention supplied to the implementation.
     *
     * @param bond  The bond.
     * @param price The observed (market) price of the bond.
     * @return The annual yield to maturity (decimal).
     * @throws NullPointerException     if {@code bond} is null
     * @throws IllegalArgumentException if {@code price} is not positive, or if the solver
     *                                  cannot bracket the root within its configured interval
     * @throws ArithmeticException      if the numerical solver fails to converge
     */
    double yieldToMaturity(Bond bond, double price);
}
