# Changelog

All notable changes to this project will be documented in this file.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-04-25

First stable release.

### Added

#### Bond domain
- `Bond` — immutable record representing a fixed-coupon bond (face value, annual rate, maturity, coupon frequency)

#### Bond pricing (`com.quant4j.bond.pricing`)
- `BondPricer` — interface for all bond pricing implementations
- `YieldBondPricer` — prices a bond by discounting all cash flows at a single yield to maturity
- `ZeroCouponBondRateBondPricer` — prices a bond by discounting each cash flow at its corresponding interpolated zero rate

#### Bond duration (`com.quant4j.bond.duration`)
- `BondDurationCalculator` — interface exposing Macaulay duration, modified duration, and DV01
- `YieldBondDurationCalculator` — computes duration measures from a yield and compounding convention

#### Bond yield (`com.quant4j.bond.yield`)
- `BondYieldCalculator` — interface for yield to maturity inversion
- `RootFindingBondYieldCalculator` — inverts the pricing formula via an injected `RootSolver`

#### Curve construction (`com.quant4j.bond.curve`)
- `BootstrappingStrategy` — interface for spot rate curve construction
- `SpotRateCurveBootstrappingStrategy` — bootstraps a zero-coupon curve from par bonds or market prices

#### Compounding conventions (`com.quant4j.rates.compounding`)
- `CompoundingStrategy` — interface: discount factor, accumulation factor, present/future value, forward rate, Macaulay-to-modified conversion
- `DiscreteCompoundingStrategy` — periodic compounding with configurable frequency
- `ContinuousCompoundingStrategy` — continuous compounding

#### Rates (`com.quant4j.rates`)
- `Frequency` — enum: `ANNUALLY`, `SEMI_ANNUALLY`, `QUARTERLY`, `MONTHLY`, `DAILY`
- `RateConverter` — converts rates between discrete frequencies and continuous compounding

#### Interest (`com.quant4j.rates.interest`)
- `CompoundInterestCalculator` — future value with optional periodic contributions and mixed compounding/contribution frequencies
- `CompoundInterestResult` — period-by-period breakdown of balance, deposits, and accrued interest

#### Numerical solvers (`com.quant4j.math.solver`)
- `RootSolver` — interface for univariate root-finding
- `BisectionSolver` — bracketing method; configurable interval, tolerance, and max iterations
- `NewtonRaphsonSolver` — Newton's method with numerical derivative; configurable initial guess
- `SecantSolver` — secant method; configurable initial guesses

#### Interpolation (`com.quant4j.math.interpolation`)
- `InterpolationStrategy` — interface for curve interpolation
- `LinearInterpolationStrategy` — linear interpolation with flat extrapolation at boundaries

[1.0.0]: https://github.com/achraf-hasbi/quant4j/releases/tag/v1.0.0
