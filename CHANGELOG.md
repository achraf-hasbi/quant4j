# Changelog

All notable changes to this project will be documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Added
- `ARCHITECTURE.md` documenting package structure, dependency direction, design
  patterns, and extension points.
- `CHANGELOG.md` (this file).
- `distributionManagement` section in `pom.xml` for Maven Central publication
  via Sonatype OSSRH.

---

## [0.1.0] — Unreleased (in development)

### Added

#### Fixed-Income Domain
- `Bond` — immutable record representing a fixed-coupon bond (face value,
  annual coupon rate, maturity in years, coupon frequency).
- `ValidationHelper` — internal utility that enforces time-frequency coherence
  on bond construction.

#### Interest Rate Primitives
- `Frequency` — enum of standard discrete compounding frequencies: `ANNUALLY`,
  `SEMI_ANNUALLY`, `QUARTERLY`, `MONTHLY`, `DAILY`.
- `CompoundingStrategy` — interface defining discount factor, accumulation
  factor, present/future value, rate inversion, and forward-rate methods.
- `DiscreteCompoundingStrategy` — implements `CompoundingStrategy` for discrete
  (periodic) compounding with a configurable number of periods per year.
- `ContinuousCompoundingStrategy` — implements `CompoundingStrategy` for
  continuous compounding (e^rt convention).
- `RateConverter` — static utilities to convert rates between discrete
  frequencies and between discrete and continuous conventions.
- `CompoundInterestCalculator` — calculates period-by-period compound interest
  growth with optional periodic contributions at a different frequency.

#### Bond Pricing
- `BondPricer` — single-method interface: `price(Bond) → double`.
- `YieldBondPricer` — prices a bond by discounting all cash flows at a
  single yield-to-maturity under an injected compounding convention.
- `ZeroCouponBondRateBondPricer` — prices a bond by discounting each cash
  flow at its corresponding interpolated zero (spot) rate.

#### Duration and Risk Metrics
- `BondDurationCalculator` — interface exposing Macaulay duration, modified
  duration, and DV01.
- `YieldBondDurationCalculator` — implements the above metrics using a YTM
  and compounding convention; correctly adjusts modified duration for both
  discrete and continuous compounding.

#### Yield to Maturity
- `BondYieldCalculator` — single-method interface: `yield(Bond, price) → double`.
- `RootFindingBondYieldCalculator` — solves for YTM by root-finding on the
  price-minus-target function; the numerical method is injected at construction.

#### Numerical Methods
- `RootSolver` — interface: `solve(DoubleUnaryOperator) → double`.
- `BisectionSolver` — guaranteed-convergence root-finding over a bracketing
  interval; configurable bounds, tolerance, and iteration limit.
- `NewtonRaphsonSolver` — quadratically convergent root-finding using a
  symmetric finite-difference derivative approximation.
- `SecantSolver` — superlinearly convergent root-finding requiring two initial
  guesses; avoids explicit derivative computation.
- `InterpolationStrategy` — interface: `interpolate(NavigableMap, x) → double`.
- `LinearInterpolationStrategy` — linear interpolation with flat extrapolation
  at both ends of the curve.

#### Yield Curve Bootstrapping
- `BootstrappingStrategy` — interface for bootstrapping a spot-rate curve from
  par bonds or market-priced bonds.
- `SpotRateCurveBootstrappingStrategy` — iterative bootstrap sorting bonds by
  maturity, discounting intermediate cash flows using the curve built so far,
  and solving algebraically for each new zero rate. Accepts an optional seed
  curve and returns an unmodifiable `NavigableMap`.

#### Build and Quality
- Maven build with Java 21, pinned plugin versions, and `mvn verify` pipeline
  covering Checkstyle, compilation, tests, Javadoc, and JaCoCo coverage.
- GitHub Actions CI triggered on push and pull request to `main`; coverage
  uploaded to Codecov.
- Apache License 2.0.

---

[Unreleased]: https://github.com/achraf-hasbi/quant4j/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/achraf-hasbi/quant4j/releases/tag/v0.1.0
