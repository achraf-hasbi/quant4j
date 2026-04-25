# Quant4J

[![Maven Central](https://img.shields.io/maven-central/v/com.quant4j/quant4j)](https://central.sonatype.com/artifact/com.quant4j/quant4j)
[![Coverage](https://codecov.io/gh/achraf-hasbi/quant4j/branch/main/graph/badge.svg)](https://codecov.io/gh/achraf-hasbi/quant4j)
[![Java](https://img.shields.io/badge/Java-21-blue)](https://adoptium.net)
[![License](https://img.shields.io/badge/License-Apache%202.0-green)](LICENSE)
[![Build](https://github.com/achraf-hasbi/quant4j/actions/workflows/ci.yml/badge.svg)](https://github.com/achraf-hasbi/quant4j/actions)

Quant4J is a Java library for quantitative finance — covering
fixed income, derivatives, options, futures, swaps, credit, and risk analytics.
The v1.0.0 release establishes the fixed income foundation: bond pricing, duration,
yield inversion, and curve construction. Each subsequent release extends the library
with new instrument types, all sharing the same consistent, interface-driven API.

---

## Features — v1.0.0

- **Bond pricing** — YTM-based and zero-curve-based discounting
- **Duration & risk** — Macaulay duration, modified duration, DV01
- **Yield to maturity** — root-finding inversion via Bisection, Newton-Raphson, or Secant
- **Curve construction** — spot rate bootstrapping from par bonds or market prices
- **Compounding conventions** — Discrete (any frequency) and Continuous
- **Rate conversion** — between discrete frequencies and continuous compounding
- **Compound interest** — future value with optional periodic contributions
- **Numerical solvers** — pluggable `RootSolver` strategy
- **Interpolation** — pluggable `InterpolationStrategy` (linear with flat extrapolation)
- Zero external runtime dependencies
- Java 21, Apache License 2.0

---

## Installation

**Maven**
```xml
<dependency>
    <groupId>com.quant4j</groupId>
    <artifactId>quant4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle**
```groovy
implementation 'com.quant4j:quant4j:1.0.0'
```

---

## Quick start

### 1. Price a bond at a given yield

```java
import com.quant4j.bond.Bond;
import com.quant4j.bond.pricing.YieldBondPricer;
import com.quant4j.rates.Frequency;
import com.quant4j.rates.compounding.DiscreteCompoundingStrategy;

// 6% annual coupon, semi-annual payments, 5 years, face value 1000
Bond bond = new Bond(1000, 0.06, 5.0, Frequency.SEMI_ANNUALLY);

// Price at a 7% YTM using semi-annual discrete compounding
double price = new YieldBondPricer(0.07, new DiscreteCompoundingStrategy(2)).price(bond);
// → 958.42
```

### 2. Compute yield to maturity from a market price

```java
import com.quant4j.bond.yield.BondYieldCalculator;
import com.quant4j.bond.yield.RootFindingBondYieldCalculator;
import com.quant4j.math.solver.NewtonRaphsonSolver;

BondYieldCalculator calculator = new RootFindingBondYieldCalculator(
        new DiscreteCompoundingStrategy(2),
        new NewtonRaphsonSolver());

double ytm = calculator.yield(bond, 950.24);
// → 0.0700 (7.00%)
```

### 3. Compute duration and DV01

```java
import com.quant4j.bond.duration.BondDurationCalculator;
import com.quant4j.bond.duration.YieldBondDurationCalculator;

BondDurationCalculator duration = new YieldBondDurationCalculator(
        0.07, new DiscreteCompoundingStrategy(2));

double macaulay = duration.macaulayDuration(bond, price); // years
double modified = duration.modifiedDuration(bond, price); // % per unit yield
double dv01     = duration.dv01(bond, price);             // $ per basis point
```

### 4. Price using a full zero rate curve

```java
import com.quant4j.bond.pricing.ZeroCouponBondRateBondPricer;
import com.quant4j.math.interpolation.LinearInterpolationStrategy;
import com.quant4j.rates.compounding.ContinuousCompoundingStrategy;
import java.util.Map;

Map<Double, Double> zeroCurve = Map.of(
        0.5, 0.050,
        1.0, 0.055,
        2.0, 0.060,
        5.0, 0.065
);

double price = new ZeroCouponBondRateBondPricer(
        zeroCurve,
        new LinearInterpolationStrategy(),
        new ContinuousCompoundingStrategy()).price(bond);
```

### 5. Bootstrap a spot rate curve from par bonds

```java
import com.quant4j.bond.curve.SpotRateCurveBootstrappingStrategy;
import java.util.List;
import java.util.NavigableMap;

SpotRateCurveBootstrappingStrategy bootstrapper = new SpotRateCurveBootstrappingStrategy(
        Map.of(0.5, 0.04),                  // seed: 6-month spot rate known
        new ContinuousCompoundingStrategy());

List<Bond> benchmarkBonds = List.of(
        new Bond(1000, 0.04, 1.0, Frequency.SEMI_ANNUALLY),
        new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY),
        new Bond(1000, 0.055, 3.0, Frequency.SEMI_ANNUALLY)
);

NavigableMap<Double, Double> spotCurve = bootstrapper.bootstrapFromParBonds(
        benchmarkBonds, new LinearInterpolationStrategy());
// → {0.5=0.04, 1.0=0.0408, 2.0=0.0502, 3.0=0.0551}
```

### 6. Convert between compounding conventions

```java
import com.quant4j.rates.RateConverter;

// Semi-annual discrete 5% → continuous equivalent
double rc = RateConverter.discreteToContinuous(0.05, Frequency.SEMI_ANNUALLY);
// → 0.04879 (4.879%)

// Continuous 4.879% → quarterly discrete equivalent
double rq = RateConverter.continuousToDiscrete(rc, Frequency.QUARTERLY);
// → 0.04969 (4.969%)
```

---

## Design

Quant4J is built around three principles:

**Interface first** — every operation (`BondPricer`, `BondYieldCalculator`,
`BondDurationCalculator`, `CompoundingStrategy`, `RootSolver`, `InterpolationStrategy`)
is expressed as an interface. Swap any implementation without touching the rest of your code.

**Explicit conventions** — compounding convention, coupon frequency, and interpolation
method are always supplied by the caller. The library never infers them.

**Immutable domain objects** — `Bond` is a Java record. All calculators hold only final
fields. Every instance is safe for concurrent use without synchronisation.

See [`ARCHITECTURE.md`](ARCHITECTURE.md) for the full structural rules and dependency map.

---

## Roadmap

v1.0.0 covers fixed income foundations. Planned milestones:

| Version | Scope |
|---|---|
| v1.1.0 | Day-count conventions, business calendars, dirty/clean price |
| v1.2.0 | Money market: T-bills, FRAs, OIS, Repo |
| v2.0.0 | Interest rate swaps, multi-curve framework |
| v2.1.0 | Caps, floors, swaptions |
| v3.0.0 | Credit default swaps, credit curves |
| v3.1.0 | Equity options, Black-Scholes, Greeks |
| v5.0.0 | Risk engine: VaR, Greeks aggregation, scenario analysis |

Full details in [`ROADMAP.md`](ROADMAP.md).

---

## Contributing

Contributions are welcome. Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) before
opening a pull request — it covers environment setup, coding conventions, the test
requirements (including the mandatory external benchmark rule), and the PR process.

---

## License

Copyright 2025-2026 Achraf Hasbi.
Distributed under the [Apache License, Version 2.0](LICENSE).
