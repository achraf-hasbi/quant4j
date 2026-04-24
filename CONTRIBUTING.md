# Contributing to Quant4J

Thank you for your interest in contributing. This document covers everything
you need to get started: environment setup, coding conventions, testing
requirements, and the pull request process.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Getting the code](#getting-the-code)
3. [Building and testing](#building-and-testing)
4. [Coding conventions](#coding-conventions)
5. [Writing tests](#writing-tests)
6. [Submitting a pull request](#submitting-a-pull-request)
7. [Reporting bugs](#reporting-bugs)

---

## Prerequisites

| Tool | Minimum version |
|---|---|
| JDK | 21 (Temurin recommended) |
| Maven | 3.9 |
| Git | 2.x |

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

# Run only tests (skip other phases)
mvn test

# Run a single test class
mvn test -Dtest=YieldBondDurationCalculatorTest

# Generate Javadoc
mvn javadoc:javadoc

# Open coverage report (macOS / Linux)
open target/site/jacoco/index.html
```

The build must pass completely (`mvn verify` exits 0) before a pull request
will be merged.

---

## Coding conventions

### Style

All main-source Java files are checked with **Checkstyle** using the rules in
[`checkstyle.xml`](checkstyle.xml). The build fails on any violation.

Run the check in isolation:

```bash
mvn checkstyle:check
```

Key rules enforced:

- **No tab characters** — 4-space indentation only.
- **Line length** — 120 characters maximum.
- **No wildcard imports** — always import specific types.
- **No unused or redundant imports**.
- **Standard Java naming** — `UpperCamelCase` for types, `UPPER_SNAKE_CASE`
  for constants, `lowerCamelCase` for everything else.

### Design principles

- **Immutability first** — prefer `final` fields and Java records for value objects.
- **Strategy pattern** — algorithms (compounding, interpolation, root-finding)
  must be injectable, not hard-coded.
- **No magic numbers** — extract domain constants with meaningful names.
- **Fail fast** — validate inputs at method entry with `Objects.requireNonNull`
  and explicit `IllegalArgumentException`; do not propagate invalid state.
- **Javadoc on all public APIs** — every public type and public method must have
  a Javadoc comment. Use `{@inheritDoc}` only where the parent doc is genuinely
  sufficient.

### Commits

- Use the imperative mood in the subject line: `Add bisection solver`, not
  `Added bisection solver`.
- Keep the subject under 72 characters.
- Reference the GitHub issue if one exists: `Fix YTM sign error (#42)`.

---

## Writing tests

- Every new class must have a corresponding test class in the matching
  `src/test/java/...` package.
- Tests use **JUnit 5** (`@Test`, `@DisplayName`, `@ParameterizedTest`).
- Assertions use `org.junit.jupiter.api.Assertions.*` — no third-party
  assertion libraries are required.
- Each test method must have a `@DisplayName` that reads as a plain-English
  sentence describing the expected behaviour.
- Numerical assertions must use a meaningful tolerance, not `0.0`.
- Guard conditions (`null`, negative price, empty list) must each have their
  own dedicated test method.

Example structure:

```java
@Test
@DisplayName("Macaulay duration of a coupon bond is strictly less than its maturity")
void testMacaulayDuration_CouponBondLessThanMaturity() {
    Bond bond = new Bond(1000, 0.05, 2.0, Frequency.SEMI_ANNUALLY);
    double price = new YieldBondPricer(0.05, semiAnnual).price(bond);

    double duration = calculator.macaulayDuration(bond, price);

    assertTrue(duration < 2.0);
    assertEquals(1.928, duration, 0.001);
}
```

---

## Submitting a pull request

1. **Create a branch** from the latest `main`:
   ```bash
   git checkout -b feature/my-feature
   ```
   Branch naming: `feature/<description>`, `fix/<description>`,
   `refactor/<description>`, `docs/<description>`.

2. **Make your changes**, keeping commits focused and atomic.

3. **Verify** the full build passes:
   ```bash
   mvn verify
   ```

4. **Push** to your fork and open a pull request against `main`.

5. **Fill in the PR description**: what problem does it solve, how was it
   tested, are there any trade-offs or known limitations?

6. Address any review feedback. Prefer adding new commits over force-pushing
   so reviewers can see what changed.

---

## Reporting bugs

Open an issue at <https://github.com/achraf-hasbi/quant4j/issues> and include:

- A short, descriptive title.
- Steps to reproduce (preferably a failing test or minimal code snippet).
- Expected behaviour vs. actual behaviour.
- Java version and OS.
