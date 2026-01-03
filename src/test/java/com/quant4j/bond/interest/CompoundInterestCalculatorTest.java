package com.quant4j.bond.interest;

import com.quant4j.bond.rate.compound.CompoundingFrequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompoundInterestCalculatorTest {

    private final CompoundInterestCalculator calculator = new CompoundInterestCalculator();
    private static final double TOLERANCE = 1.0e-2; // Penny tolerance

    private double getLast(List<Double> list) {
        if (list.isEmpty()) return 0.0;
        return list.get(list.size() - 1);
    }

    @Test
    @DisplayName("Simple Annual Compounding without Contributions")
    void testSimpleGrowth() {
        // 1000 principal, 10% rate, 2 years, Annual compound
        // Year 1: 1000 * 1.10 = 1100
        // Year 2: 1100 * 1.10 = 1210
        CompoundInterestResult result = calculator.calculate(
                1000,
                0,
                CompoundingFrequency.ANNUALLY,
                2,
                0.1,
                CompoundingFrequency.ANNUALLY
        );

        assertEquals(1210.0, getLast(result.balance()), TOLERANCE);
        assertEquals(1000.0, getLast(result.totalDeposit()), TOLERANCE); // Only initial
        assertEquals(210.0, getLast(result.accuredInterest()), TOLERANCE);
    }

    @Test
    @DisplayName("Monthly Contributions matching Monthly Compounding")
    void testMonthlyMatching() {
        // 0 principal, 100 monthly, 1 year (12 periods), 6% annual (0.5% monthly)
        // Rate per period = 0.06 / 12 = 0.005
        // FV = PMT * [ (1+r)^n - 1 ] / r
        // FV = 100 * [ (1.005)^12 - 1 ] / 0.005
        
        CompoundInterestResult result = calculator.calculate(
                0,
                100,
                CompoundingFrequency.MONTHLY,
                1,
                0.06,
                CompoundingFrequency.MONTHLY
        );
        
        double r = 0.005;
        double expectedFV = 100 * (Math.pow(1 + r, 12) - 1) / r;
        
        assertEquals(expectedFV, getLast(result.balance()), TOLERANCE);
        assertEquals(1200.0, getLast(result.totalDeposit()), TOLERANCE);
        assertEquals(33.56, getLast(result.accuredInterest()), TOLERANCE);
        assertEquals(12, result.balance().size() - 1); // Initial state + 12 periods
    }

    @Test
    @DisplayName("Aggregation: Monthly Contributions, Annual Compounding")
    void testAggregationLogic() {
        // 0 principal, 100 monthly, 1 year, 10% annual compounding
        // In our logic: Since compounding is ANNUALLY (1 period), and contribution is MONTHLY (12 periods)
        // We aggregate 12 contributions into the single compounding period.
        // contributionThisPeriod = 100 * 12 = 1200.
        // Interest is calculated on start balance (0).
        // End balance = 0 + 0 + 1200 = 1200.
        
        CompoundInterestResult result = calculator.calculate(
                1500,
                100,
                CompoundingFrequency.MONTHLY,
                2,
                0.10,
                CompoundingFrequency.ANNUALLY
        );

        assertEquals(4335, getLast(result.balance()), TOLERANCE);
        assertEquals(3900, getLast(result.totalDeposit()), TOLERANCE);
        assertEquals(285.0, getLast(result.interests()), TOLERANCE);
        assertEquals(435.0, getLast(result.accuredInterest()), TOLERANCE);
        assertEquals(2, result.balance().size() - 1); // Initial + 1 period
    }
    
    @Test
    @DisplayName("Distribution: Annual Contribution, Monthly Compounding")
    void testDistributionLogic() {
        // 0 principal, 1200 Annual contribution, 1 year, 0% interest, Monthly Compounding
        // Compounding periods = 12. Contribution periods = 1.
        // interval = 12 / 1 = 12.
        // Contribution happens only at period 12.
        
        CompoundInterestResult result = calculator.calculate(
                0,
                1200,
                CompoundingFrequency.ANNUALLY,
                1,
                0.0,
                CompoundingFrequency.MONTHLY
        );
        
        // Month 1-11: balance 0.
        // Month 12: Add 1200.
        
        assertEquals(0.0, result.balance().get(1), TOLERANCE);
        assertEquals(0.0, result.balance().get(11), TOLERANCE); 
        assertEquals(1200.0, getLast(result.balance()), TOLERANCE);
    }

    @Test
    @DisplayName("Zero Interest Rate")
    void testZeroInterest() {
        // 1000 initial, 100 monthly, 1 year, 0% rate, monthly compounding
        CompoundInterestResult result = calculator.calculate(
                1000,
                100,
                CompoundingFrequency.MONTHLY,
                1,
                0.0,
                CompoundingFrequency.MONTHLY
        );
        
        // 1000 + 12*100 = 2200
        assertEquals(2200.0, getLast(result.balance()), TOLERANCE);
        assertEquals(2200.0, getLast(result.totalDeposit()), TOLERANCE);
        assertEquals(0.0, getLast(result.accuredInterest()), TOLERANCE);
    }

    @Test
    @DisplayName("Validation")
    void testValidation() {
        assertThrows(NullPointerException.class, () -> 
            calculator.calculate(100, 10, null, 1, 0.05, CompoundingFrequency.ANNUALLY));
        assertThrows(IllegalArgumentException.class, () -> 
            calculator.calculate(100, 10, CompoundingFrequency.MONTHLY, -1, 0.05, CompoundingFrequency.ANNUALLY));
    }
}
