# Roadmap — Quant4J

The goal is for Quant4J to be the standard open-source quantitative finance library for Java —
covering fixed income, rates, credit, equity derivatives, and risk analytics with a clean,
consistent, and well-tested API throughout.

Versions marked **Stable** are released. Everything else is planned and subject to change.

---

## v1.0.0 — Fixed Income Core · Stable

Foundation layer: bonds, compounding, curve construction, and numerical solvers.

- Fixed-coupon bond model (`Bond`)
- YTM pricing, zero-curve pricing
- Macaulay duration, modified duration, DV01
- YTM inversion via Bisection, Newton-Raphson, Secant
- Spot rate curve bootstrapping (par bonds + market prices)
- Compounding conventions: Discrete, Continuous
- Rate conversion utilities
- Linear interpolation
- Compound interest calculator

---

## v1.1.0 — Fixed Income: Market Conventions

Make the bond model production-ready with real-world settlement conventions.

- **Day-count conventions**: Actual/Actual, Actual/360, Actual/365, 30/360
- **Business day calendars**: TARGET, US, UK
- **Dirty vs clean price**: accrued interest calculation
- **Settlement lag**: T+1, T+2
- **Yield conventions**: street convention, ISMA, money-market basis
- Additional interpolation: log-linear, cubic spline, monotone convex
- `BondSchedule`: explicit coupon date generation from effective/maturity dates

---

## v1.2.0 — Money Market Instruments

Short-end instruments that feed the front of the yield curve.

- **Treasury bills**: discount yield, bond-equivalent yield
- **Certificates of deposit (CD)**
- **Commercial paper**
- **Repurchase agreements (Repo)**: term repo, open repo, haircut
- **Forward Rate Agreements (FRA)**: pricing, fixing, settlement
- **OIS rates**: overnight index swap rate handling
- Unified `MoneyMarketInstrument` interface

---

## v2.0.0 — Interest Rate Derivatives

Vanilla rates derivatives with multi-curve support.

- **Multi-curve framework**: separate discounting curve (OIS) from projection curves (LIBOR/SOFR)
- **Interest Rate Swaps (IRS)**:
  - Fixed vs floating (vanilla)
  - Floating vs floating (basis swap)
  - Overnight index swaps (OIS)
- **Swap pricing**: par rate, PV01, annuity factor
- **Swap curve construction**: iterative bootstrapping from deposit + FRA + swap quotes
- **IBOR transition**: SOFR, SONIA, ESTR compounded-in-arrears conventions
- `SwapLeg`, `FixedLeg`, `FloatingLeg` domain model

---

## v2.1.0 — Interest Rate Options

- **Caps and Floors**: caplets/floorlets, Black model, Bachelier model
- **Swaptions**: European (Black, normal vol), Bermudan (LSM approximation)
- **Volatility surfaces**: flat vol, vol cube (strike × tenor × expiry)
- Greeks: delta, vega, theta for cap/floor/swaption
- `InterestRateOption` interface

---

## v3.0.0 — Credit

Credit risk instruments and default modelling.

- **Credit Default Swaps (CDS)**:
  - Single-name CDS pricing (ISDA standard model)
  - Par spread, upfront fee, risky annuity
- **Credit curves**: hazard rate bootstrapping from CDS spreads
- **Survival probability**: flat hazard rate, piecewise constant
- **Recovery rate conventions**: fixed recovery, stochastic recovery
- **Bond credit spread**: Z-spread, asset swap spread, OAS
- `CreditCurve`, `SurvivalProbabilityCurve`

---

## v3.1.0 — Equity Derivatives

Options and structured equity products.

- **Equity options**: European and American (put/call)
- **Pricing models**:
  - Black-Scholes-Merton
  - Binomial tree (Cox-Ross-Rubinstein)
  - Monte Carlo (log-normal)
- **Greeks**: delta, gamma, vega, theta, rho, vanna, volga
- **Implied volatility**: inversion from market price (Newton-Raphson)
- **Volatility surface**: strike × expiry grid, smile interpolation
- **Exotic options**: barrier (knock-in/knock-out), Asian (arithmetic/geometric), digital
- `EquityOption`, `OptionPricer`, `GreeksCalculator` interfaces

---

## v3.2.0 — Futures

Exchange-traded derivatives.

- **Interest rate futures**: Eurodollar, SOFR, treasury bond futures
- **Equity index futures**: fair value, dividend adjustment, cost of carry
- **Commodity futures**: convenience yield, storage cost
- **Futures vs forward adjustment**: convexity correction for rate futures
- `FuturesContract`, `FuturesPricer`

---

## v4.0.0 — Structured Products & Hybrids

- **Asset-backed securities (ABS)**: prepayment modelling (PSA, CPR)
- **Mortgage-backed securities (MBS)**: pass-through, CMO tranches
- **Collateralised Debt Obligations (CDO)**: Gaussian copula, tranche pricing
- **Convertible bonds**: equity-credit hybrid, binomial tree pricing
- **Callable / putable bonds**: option-adjusted spread (OAS), effective duration

---

## v5.0.0 — Risk Analytics Engine

Portfolio-level risk aggregation across all instrument types.

- **Greeks aggregation**: delta ladder, gamma matrix, vega surface by underlying
- **PV01 / DV01 ladder**: bucketed sensitivity per tenor
- **Value at Risk (VaR)**: historical simulation, parametric (delta-normal), Monte Carlo
- **Expected Shortfall (CVaR)**
- **Scenario analysis**: parallel shift, twist, butterfly, credit spread widening
- **Stress testing**: 2008 crisis, COVID shock, rate spike
- **XVA**: CVA (credit valuation adjustment), DVA, FVA (funding)
- `RiskEngine`, `Portfolio`, `Scenario` interfaces

---

## Cross-cutting concerns (all versions)

These improve incrementally across every release and are not tied to a single milestone.

| Concern | Plan |
|---|---|
| **Calendars** | Expand from v1.1.0; full FpML calendar set by v2.0.0 |
| **Currency** | ISO 4217 `Currency` type introduced in v1.1.0; FX rates and multi-currency PV from v2.0.0 |
| **ArchUnit tests** | Dependency rules codified in tests from v1.1.0 |
| **Benchmarks** | JMH micro-benchmarks for hot paths (pricing loops) from v2.0.0 |
| **FpML / ISDA CDM** | Trade representation aligned with FpML schemas from v2.0.0 |
| **Serialisation** | JSON-serialisable domain objects (Jackson-compatible) from v2.0.0 |
| **Documentation** | Worked examples per instrument type alongside each release |

---

## Design principles that apply to every new module

1. **Interface first** — every pricing, risk, or model operation is expressed as an interface.
2. **Immutable domain objects** — all instruments are records or final classes.
3. **Explicit conventions** — day counts, compounding, and calendars are always supplied by the caller, never inferred.
4. **No external runtime dependencies** — the core library stays dependency-free; optional integrations (Jackson, etc.) are separate modules.
5. **Benchmark against published values** — every new instrument type ships with at least one test against a textbook or ISDA/Bloomberg reference value.
