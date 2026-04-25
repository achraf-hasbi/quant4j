# Quant4J

[![CI](https://github.com/achraf-hasbi/quant4j/actions/workflows/ci.yml/badge.svg)](https://github.com/achraf-hasbi/quant4j/actions/workflows/ci.yml)
[![Coverage](https://codecov.io/gh/achraf-hasbi/quant4j/branch/main/graph/badge.svg)](https://codecov.io/gh/achraf-hasbi/quant4j)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)

> A clean, well-tested quantitative finance library for Java 21.

Quant4J provides production-quality implementations of fixed-income mathematics —
bond pricing, duration, yield to maturity, and spot rate curve bootstrapping —
built on composable, strategy-based abstractions that are easy to extend.

---

## Features

### Fixed-Income (v0.1)

| Area | What's included |
|---|---|
| **Compounding** | Discrete (any frequency) and continuous compounding strategies |
| **Rate conversion** | Convert rates across compounding conventions |
| **Bond pricing** | YTM-based, geometric, and zero-coupon rate models |
| **Duration** | Macaulay duration, modified duration, DV01 |
| **Yield to maturity** | Bisection, Newton-Raphson, and Secant solvers |
| **Yield curve** | Spot rate curve bootstrapping from par or market-priced bonds |

---

## Requirements

- **Java 21** or later
- **Maven 3.9+** (to build from source)

---

## Installation

Quant4J is not yet published to Maven Central. Build and install locally:

```bash
git clone https://github.com/achraf-hasbi/quant4j.git
cd quant4j
mvn install -DskipTests
```

Then add to your project:

**Maven**
```xml
<dependency>
    <groupId>com.quant4j</groupId>
    <artifactId>quant4j</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

**Gradle**
```groovy
implementation 'com.quant4j:quant4j:0.1.0-SNAPSHOT'
```

---

## Quick Start

```java
import com.quant4j.rates.Frequency;
import com.quant4j.bond.Bond;
import com.quant4j.bond.pricing.YieldBondPricer;
import com.quant4j.bond.duration.YieldBondDurationCalculator;
import com.quant4j.bond.yield.BondYieldCalculator;
import com.quant4j.rates.compounding.DiscreteCompoundingStrategy;

// ── 1. Define a 5-year semi-annual coupon bond ─────────────────────────────
Bond bond = new Bond(1000.0, 0.05, 5.0, Frequency.SEMI_ANNUALLY);

        var compounding = new DiscreteCompoundingStrategy(2);

        // ── 2. Price at a 5% semi-annual yield ─────────────────────────────────────
        double price = new YieldBondPricer(0.05, compounding).price(bond);
// → 1000.00  (par, since coupon rate == yield)

        // ── 3. Compute duration and DV01 ───────────────────────────────────────────
        var calculator = new YieldBondDurationCalculator(0.05, compounding);
        double macaulay = calculator.macaulayDuration(bond, price);   // ~4.49 years
        double modified = calculator.modifiedDuration(bond, price);   // ~4.38
        double dv01 = calculator.dv01(bond, price);               // ~$0.438 per bp

        // ── 4. Recover yield from price using Newton-Raphson ──────────────────────
        BondYieldCalculator ytmCalc = new NewtonRaphsonBondYieldCalculator(compounding);
        double ytm = ytmCalc.yieldToMaturity(bond, price);                      // → 0.05
```

---

## Building from Source

```bash
# Full build: checkstyle → compile → test → Javadoc → coverage report
mvn verify

# Run tests only
mvn test

# Generate Javadoc site
mvn javadoc:javadoc

# View coverage report
open target/site/jacoco/index.html
```

---


## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.

---

## License

Copyright 2025 Achraf Hasbi

Licensed under the **Apache License, Version 2.0**. See [LICENSE](LICENSE) for the full text.
