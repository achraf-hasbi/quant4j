# Contributing to Quant4J

Thank you for your interest in contributing. This document covers everything you need:
environment setup, coding conventions, testing standards, and the pull request process.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Getting the code](#getting-the-code)
3. [Building and testing](#building-and-testing)
4. [Before you write code](#before-you-write-code)
5. [Coding conventions](#coding-conventions)
6. [Writing tests](#writing-tests)
7. [Submitting a pull request](#submitting-a-pull-request)
8. [Reporting bugs](#reporting-bugs)

---

## Prerequisites

| Tool | Minimum version |
|---|---|
| JDK | 21  |

---

## Getting the code

```bash
# 1. Fork the repository on GitHub, then clone your fork
git clone https://github.com/<your-username>/quant4j.git
cd quant4j

# 2. Add the upstream remote so you can pull in future changes
git remote add upstream https://github.com/achraf-hasbi/quant4j.git
```

---

## Building and testing

```bash
# Full verification: checkstyle → compile → test → Javadoc → coverage
mvn verify

# Run only tests
mvn test

# Run a single test class
mvn test -Dtest=YieldBondDurationCalculatorTest

# Run Checkstyle in isolation
mvn checkstyle:check

# Generate Javadoc
mvn javadoc:javadoc
```

`mvn verify` must exit 0 before any pull request will be merged.

---

## Before you write code

**Read [`ARCHITECTURE.md`](ARCHITECTURE.md) first.** It defines the package structure,
permitted dependency directions, and core invariants (e.g. `Bond.getCashflows()` is the
single cash flow source). Violating these rules will block a PR regardless of whether
the tests pass.

**Check [`ROADMAP.md`](ROADMAP.md)** if you are proposing a new instrument type or module.
It shows what is planned, in what order, and what API shape is expected. Open a GitHub
discussion before starting significant new work so effort is not duplicated.

---

## Coding conventions

### Style

Style is enforced automatically by Checkstyle on every build. The full rule set is in
[`checkstyle.xml`](checkstyle.xml). Key rules:

| Rule | Detail |
|---|---|
| Indentation | 4 spaces — no tabs |
| Line length | 150 characters maximum |
| Imports | No wildcards, no unused, no duplicates |
| Naming | `UpperCamelCase` types · `UPPER_SNAKE_CASE` constants · `lowerCamelCase` everything else |
| Braces | Required on all blocks, even single-statement `if`/`else` |
| Fields | Must be `private` — expose state via methods |
| Modifiers | Canonical JLS order: `public static final` |
| Utility classes | Must declare a private constructor |

### Design

- **Immutability first** — prefer `final` fields and Java records for value objects.
- **Strategy pattern** — algorithms (compounding, interpolation, root-finding) are injected
  via constructor, never hard-coded.
- **Interface first** — every domain operation is expressed as an interface. Concrete classes
  are implementation details.
- **`Bond.getCashflows()` is the canonical schedule** — never re-derive the coupon schedule
  independently inside a pricer or calculator.
- **Compounding convention is always explicit** — never infer it from coupon frequency.
- **Fail fast** — validate inputs at method entry with `Objects.requireNonNull` for nulls and
  explicit `IllegalArgumentException` for invalid values. Never propagate invalid state.
- **No magic numbers** — extract numerical constants with meaningful names and a comment
  explaining their origin.

### Javadoc

- Every public type and public method must have a Javadoc comment.
- The interface must declare all `@throws` that implementations can throw — callers
  programming to the interface must not need to read implementation source.
- Use `{@inheritDoc}` only when the parent Javadoc is genuinely complete. Do not use it as
  a substitute for writing a meaningful description.

### Commits

- Imperative mood in the subject line: `Add bisection solver`, not `Added bisection solver`.
- Subject line under 72 characters.
- Reference the GitHub issue when one exists: `Fix YTM sign error (#42)`.

---

## Writing tests

### Structure

- Every new class must have a corresponding test class in the matching `src/test/java/...` package.
- Tests use **JUnit 5** (`@Test`, `@DisplayName`, `@ParameterizedTest`).
- Assertions use `org.junit.jupiter.api.Assertions` — no third-party assertion libraries.
- Every test method must have a `@DisplayName` that reads as a plain-English sentence
  describing the expected behaviour.

### Coverage requirements

Each test class must cover:

| Category | Requirement |
|---|---|
| Guard conditions | One dedicated test per `null` argument, non-positive value, and invalid state |
| Directional invariants | e.g. discount bond yields above coupon rate |
| Round-trip consistency | Price a bond at yield Y → recover Y from that price |
| **External benchmark** | At least one value verified against a textbook, ISDA spec, or Bloomberg |

The external benchmark requirement is **mandatory** for any new pricing or risk model.
Round-trip consistency alone only proves that two components agree with each other —
it does not prove that either is financially correct.

### Tolerances

| Test type | Tolerance |
|---|---|
| Round-trip numerical | `1e-6` |
| Textbook benchmark | `1e-3` (the published value is itself rounded) |
| Guard conditions | `assertThrows` — no tolerance needed |

### Example

```java
@Test
@DisplayName("Benchmark: 6% semi-annual, 5yr, face 1000, price 950.24 → YTM ≈ 7.00%")
void testBenchmark_KnownYtm() {
    // CFA Level 1 standard example. Derivation:
    // coupon = 30, r_period = 3.5%, 10 periods
    // PV = Σ 30/(1.036)^i + 1000/(1.036)^10 ≈ 950.24 at YTM = 7.2%
    Bond bond = new Bond(1000, 0.06, 5.0, Frequency.SEMI_ANNUALLY);
    BondYieldCalculator calc = new RootFindingBondYieldCalculator(
            new DiscreteCompoundingStrategy(2), new NewtonRaphsonSolver());

    assertEquals(0.072, calc.yieldToMaturity(bond, 950.24), 1e-3);
}
```

---

## Submitting a pull request

1. **Create a branch** from the latest `main`:
   ```bash
   git checkout -b feature/my-feature
   ```
   Branch naming: `feature/<description>` · `fix/<description>` ·
   `refactor/<description>` · `docs/<description>`

2. **Make your changes** — keep commits focused and atomic.

3. **Verify** the full build passes:
   ```bash
   mvn verify
   ```

4. **Push** to your fork and open a pull request against `main`.

5. **Fill in the PR description**:
   - What problem does it solve?
   - How was it tested? Paste the benchmark source if applicable.
   - Any trade-offs or known limitations?
   - For new instruments or modules, link to the relevant section of [`ROADMAP.md`](ROADMAP.md).

6. Address review feedback by adding new commits — avoid force-pushing so reviewers
   can see what changed between rounds.

---

## Reporting bugs

Open an issue at <https://github.com/achraf-hasbi/quant4j/issues> and include:

- A short, descriptive title.
- Steps to reproduce — a failing test or minimal code snippet is ideal.
- Expected behaviour vs. actual behaviour.
- Java version and OS.
