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
package io.quant4j.rates.compounding;

/**
 * Defines the contract for interest rate compounding calculations.
 * Implementations handle specific compounding conventions (e.g., Discrete, Continuous).
 */
public interface CompoundingStrategy {

    /**
     * Calculates the discount factor for a given interest rate and time period.
     * <p>
     * The discount factor $DF(r, t)$ represents the present value of 1 unit of currency
     * received at time $t$.
     * </p>
     *
     * @param rate the annual interest rate (as a decimal, e.g., 0.05 for 5%)
     * @param time the time in years
     * @return the discount factor
     */
    double discountFactor(double rate, double time);

    /**
     * Calculates the accumulation factor (Future Value Interest Factor).
     * <p>
     * Represents the future value of 1 unit of currency invested today
     * at rate $r$ for time $t$.
     * </p>
     *
     * @param rate the annual interest rate (as a decimal)
     * @param time the time in years
     * @return the accumulation factor
     */
    double accumulationFactor(double rate, double time);

    /**
     * Calculates the future value of a principal amount.
     *
     * @param principal the initial amount
     * @param rate      the annual interest rate (as a decimal)
     * @param time      the time in years
     * @return the future value
     */
    double futureValue(double principal, double rate, double time);

    /**
     * Calculates the present value of a future amount.
     *
     * @param futureAmount the amount to be received in the future
     * @param rate         the annual interest rate (as a decimal)
     * @param time         the time in years
     * @return the present value
     */
    double presentValue(double futureAmount, double rate, double time);

    /**
     * Deduce the rate from a given discount factor and time.
     *
     * @param discountFactor the discount factor
     * @param time           the time in years
     * @return the deduced annual interest rate
     */
    double rateFromDiscountFactor(double discountFactor, double time);

    /**
     * Calculates the forward rate between two time periods.
     *
     * @param r1 Rate for period 1
     * @param t1 Time to maturity 1 (in years)
     * @param r2 Rate for period 2
     * @param t2 Time to maturity 2 (in years)
     * @return the forward rate applicable between t1 and t2
     * @throws IllegalArgumentException if t1 >= t2
     */
    double forwardRate(double r1, double t1, double r2, double t2);

    /**
     * Converts a Macaulay duration to a modified duration under this compounding convention.
     *
     * <ul>
     *   <li>Discrete (m periods/year): {@code D_mod = D_mac / (1 + y/m)}</li>
     *   <li>Continuous:               {@code D_mod = D_mac}</li>
     * </ul>
     *
     * @param macaulay the Macaulay duration in years
     * @param yield    the annual yield to maturity (decimal)
     * @return the modified duration
     */
    double adjustMacaulayToModified(double macaulay, double yield);
}