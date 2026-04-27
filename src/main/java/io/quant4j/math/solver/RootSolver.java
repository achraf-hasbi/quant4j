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
package io.quant4j.math.solver;

import java.util.function.DoubleUnaryOperator;

/**
 * Strategy for finding a root {@code x} of a univariate real function {@code f},
 * i.e. {@code f(x) ≈ 0}.
 *
 * <p>Each implementation encapsulates its own convergence parameters (tolerance,
 * max iterations, initial guesses / bracketing interval) via its constructor.
 */
public interface RootSolver {

    /**
     * Finds {@code x} such that {@code f(x) ≈ 0}.
     *
     * @param f the function whose root is sought; must not be {@code null}
     * @return the approximate root
     * @throws ArithmeticException      if the algorithm fails to converge
     * @throws IllegalArgumentException if the bracketing interval has no sign change (bisection)
     */
    double solve(DoubleUnaryOperator f);
}
