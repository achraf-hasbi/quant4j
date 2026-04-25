# Quant4J — Architecture

## Purpose

Quant4J is a single-module Java 21 library for fixed-income quantitative finance.
It provides composable, strategy-based implementations of bond pricing, duration,
yield-to-maturity, and spot-rate curve bootstrapping, built on a foundation of
general-purpose numerical methods.

---

## Package Structure

```
com.quant4j
├── math/
│   ├── solver/          Root-finding algorithms (Bisection, Newton-Raphson, Secant)
│   └── interpolation/   Curve interpolation algorithms (Linear)
├── rates/
│   ├── Frequency        Enum of discrete compounding frequencies
│   ├── RateConverter    Static utilities for rate convention conversion
│   ├── compounding/     CompoundingStrategy and its implementations
│   └── interest/        CompoundInterestCalculator and result type
└── bond/
    ├── Bond             Immutable domain record for a fixed-coupon bond
    ├── ValidationHelper Internal input-validation utilities
    ├── pricing/         BondPricer interface and implementations
    ├── duration/        BondDurationCalculator interface and implementation
    ├── yield/           BondYieldCalculator interface and implementation
    └── curve/           BootstrappingStrategy interface and implementation
```

### Dependency Direction

```
math  ──►  rates  ──►  bond
```

- `math` has no dependencies on `rates` or `bond`.
- `rates` depends on `math` (solvers are used indirectly via injection).
- `bond` depends on both `math` (interpolation, solvers) and `rates` (compounding, frequency).
- No package imports anything from a higher layer (no upward dependencies).

---

## Primary Design Pattern: Strategy

Almost every algorithm is expressed as an interface with one or more interchangeable
implementations. Dependencies between components are satisfied by constructor injection,
never by hard-coded instantiation inside the consuming class.

| Interface | Implementations | Injected into |
|---|---|---|
| `CompoundingStrategy` | `DiscreteCompoundingStrategy`, `ContinuousCompoundingStrategy` | pricers, calculators, bootstrapper |
| `RootSolver` | `BisectionSolver`, `NewtonRaphsonSolver`, `SecantSolver` | `RootFindingBondYieldCalculator` |
| `InterpolationStrategy` | `LinearInterpolationStrategy` | `ZeroCouponBondRateBondPricer`, `SpotRateCurveBootstrappingStrategy` |
| `BondPricer` | `YieldBondPricer`, `ZeroCouponBondRateBondPricer` | user code |
| `BondDurationCalculator` | `YieldBondDurationCalculator` | user code |
| `BondYieldCalculator` | `RootFindingBondYieldCalculator` | user code |
| `BootstrappingStrategy` | `SpotRateCurveBootstrappingStrategy` | user code |

This means a user can supply any `RootSolver` to YTM calculation, any
`InterpolationStrategy` to curve-based pricing, and any `CompoundingStrategy`
to any calculator — without modifying library code.

---

## Domain Model

`Bond` is an immutable `record` with four fields:

| Field | Type | Meaning |
|---|---|---|
| `faceValue` | `double` | Principal repaid at maturity |
| `annualRate` | `double` | Annual coupon rate (decimal) |
| `maturityYears` | `double` | Time to maturity in years |
| `couponFrequency` | `Frequency` | How often coupons are paid |

The compact constructor validates all fields immediately and delegates to
`ValidationHelper.validateTimeCoherence` to ensure the maturity is an exact
multiple of the coupon period. This makes the bond model fail-fast and always
self-consistent.

`Bond.getCashflows()` returns the full cash-flow schedule as a
`NavigableMap<Double, Double>` (time in years → amount). This is the single
source of truth consumed by pricers, duration calculators, and the bootstrapper.

---

## Compounding Layer

`CompoundingStrategy` is the central abstraction for time-value-of-money
calculations. Every strategy exposes:

- `discountFactor(rate, time)` — present value of 1 unit at time _t_
- `accumulationFactor(rate, time)` — future value of 1 unit at time _t_
- `futureValue / presentValue` — convenience wrappers over the above
- `rateFromDiscountFactor(df, time)` — inverse: extract rate from a known DF
- `forwardRate(r1, t1, r2, t2)` — forward rate consistent with the convention

The two built-in implementations cover the standard conventions:

| Convention | Formula |
|---|---|
| Discrete (`m` periods/year) | DF = (1 + r/m)^(−m·t) |
| Continuous | DF = e^(−r·t) |

`Frequency` is an enum that pairs a period count with a pre-built
`DiscreteCompoundingStrategy`. This lets callers write
`Frequency.SEMI_ANNUALLY.getCompoundingStrategy()` as a convenience,
while still allowing custom strategies to be injected directly.

---

## Numerical Layer

### Root Solvers (`math.solver`)

All three solvers implement `RootSolver`, which has a single method:

```java
double solve(DoubleUnaryOperator f);
```

The contract is: find `x` such that `f(x) ≈ 0`, or throw `ArithmeticException`
if the solver fails to converge within the configured iteration limit.

| Solver | Requires | Convergence |
|---|---|---|
| `BisectionSolver` | Bracketing interval with sign change | Guaranteed (linear) |
| `NewtonRaphsonSolver` | Initial guess; smooth function | Quadratic (near root) |
| `SecantSolver` | Two distinct initial guesses | Superlinear |

Newton-Raphson uses a symmetric finite-difference derivative approximation
(`h = 1e-6`) so no analytic derivative is needed.

### Interpolation (`math.interpolation`)

`InterpolationStrategy` defines `interpolate(NavigableMap<Double,Double>, x)`.

`LinearInterpolationStrategy` performs linear interpolation between the two
nearest known points, and flat extrapolation outside the curve's range (left
or right). This is the standard choice for bootstrapping short yield curves.

---

## Bootstrapping Algorithm

`SpotRateCurveBootstrappingStrategy` implements the standard iterative bootstrap:

1. Sort bonds by maturity (ascending).
2. For each bond, discount all intermediate cash flows using the zero rates
   already in the curve (interpolating where necessary).
3. Solve algebraically for the zero rate at the bond's own maturity using the
   known market or par price.
4. Add the new zero rate to the working curve and continue.

An initial seed curve (e.g., short-end rates from money-market instruments) can
be provided at construction time. It is defensively copied and never mutated.
The returned map is wrapped in `Collections.unmodifiableNavigableMap`.

---

## Extension Points

To add a new compounding convention, implement `CompoundingStrategy` and inject it.

To add a new root-finding algorithm, implement `RootSolver` and pass it to
`RootFindingBondYieldCalculator`.

To add a new interpolation method (cubic spline, log-linear, etc.), implement
`InterpolationStrategy` and pass it to the pricer or bootstrapper.

To add a new bond type (floating-rate, inflation-linked), subclass or replace
`Bond` and implement a corresponding `BondPricer`.

---

## Build and Quality Gates

`mvn verify` runs the following pipeline in order:

| Phase | Plugin | What it checks |
|---|---|---|
| `validate` | maven-checkstyle-plugin | Naming, imports, line length |
| `compile` | maven-compiler-plugin | Java 21 compilation |
| `test` | maven-surefire-plugin | All JUnit 5 tests |
| `verify` | jacoco-maven-plugin | Coverage report (HTML + XML) |
| `verify` | maven-javadoc-plugin | Javadoc syntax and reference validity |

Source and Javadoc JARs are attached to every build, satisfying Maven Central's
publication requirements.
