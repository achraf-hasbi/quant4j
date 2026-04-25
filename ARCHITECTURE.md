# Architecture — Quant4J

## Package structure

```
io.quant4j
├── math
│   ├── solver          — root-finding algorithms (Bisection, Newton-Raphson, Secant)
│   ├── interpolation   — curve interpolation strategies (Linear)
│   └── ValidationHelper— shared parameter validation utilities
├── rates
│   ├── compounding     — CompoundingStrategy (Discrete, Continuous)
│   ├── interest        — CompoundInterestCalculator
│   ├── Frequency       — enum: periods per year + bound compounding strategy
│   └── RateConverter   — stateless rate conversion utilities
└── bond
    ├── Bond            — immutable record: the canonical bond model
    ├── pricing         — BondPricer (YTM-based, zero-curve-based)
    ├── duration        — BondDurationCalculator (Macaulay, modified, DV01)
    ├── yield           — BondYieldCalculator (root-finding YTM inversion)
    └── curve           — BootstrappingStrategy (spot rate curve construction)
```

## Dependency rules

```
bond.*   →  rates.*, math.*   ✓
rates.*  →  math.*            ✓
math.*   →  (nothing)         ✓

bond sub-packages (pricing, duration, yield, curve) must not import each other  ✗
rates.* / math.*  must not import bond.*                                        ✗
```

## Core invariants

- **`Bond.getCashflows()` is the single schedule source.** All pricers and calculators iterate
  this map. Never re-derive the coupon schedule independently.
- **Compounding convention is always explicit.** It is never inferred from coupon frequency.
- **All classes are immutable after construction.** Fields are final; no setters.
- **Program to interfaces.** `BondPricer`, `BondDurationCalculator`, `BondYieldCalculator`,
  `CompoundingStrategy`, `RootSolver`, `InterpolationStrategy` are the public API.

## Extension points

| Goal | Action |
|---|---|
| New compounding convention | Implement `CompoundingStrategy` |
| New numerical solver | Implement `RootSolver` |
| New interpolation method | Implement `InterpolationStrategy` |
| New pricing model | Implement `BondPricer` |
| New instrument | New domain record + parallel sub-package |
