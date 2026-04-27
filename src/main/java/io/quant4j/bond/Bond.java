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
package io.quant4j.bond;

import io.quant4j.rates.Frequency;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static io.quant4j.math.ValidationHelper.validateTimeCoherence;

/**
 * Represents a fixed-coupon bond.
 *
 * @param faceValue       The principal amount of the bond (e.g., 1000).
 * @param annualRate      The annual coupon rate (decimal, e.g., 0.05 for 5%).
 * @param maturityYears   The time to maturity in years.
 * @param couponFrequency The frequency of coupon payments (e.g., SEMI_ANNUALLY).
 */
public record Bond(double faceValue,
                   double annualRate,
                   double maturityYears,
                   Frequency couponFrequency) {

    public Bond {
        if (faceValue <= 0) {
            throw new IllegalArgumentException("Face value must be positive");
        }
        if (annualRate < 0) {
            throw new IllegalArgumentException("Annual rate cannot be negative");
        }
        if (maturityYears <= 0) {
            throw new IllegalArgumentException("Maturity must be positive");
        }
        Objects.requireNonNull(couponFrequency, "Coupon frequency cannot be null");
        validateTimeCoherence(maturityYears, couponFrequency);
    }

    /**
     * Calculates the coupon payment amount per period.
     *
     * @return the coupon amount.
     */
    public double getCouponPayment() {
        return faceValue * annualRate / couponFrequency.getPeriodsPerYear();
    }

    /**
     * Generates the cash flows for the bond.
     *
     * @return a map where the key is the time in years and the value is the cash flow amount.
     */
    public Map<Double, Double> getCashflows() {
        Map<Double, Double> cashflows = new LinkedHashMap<>();

        double couponPayment = getCouponPayment();
        int periodsPerYear = couponFrequency.getPeriodsPerYear();
        int totalPeriods = (int) Math.round(maturityYears * periodsPerYear);
        double timeStep = 1.0 / periodsPerYear;

        for (int i = 1; i <= totalPeriods; i++) {
            double time = i * timeStep;
            double amount = couponPayment;

            // Add face value to the last payment
            if (i == totalPeriods) {
                amount += faceValue;
            }

            cashflows.put(time, amount);
        }

        return cashflows;
    }
}