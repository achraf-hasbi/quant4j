package com.quant4j.bond.interest;

import java.util.ArrayList;
import java.util.List;


public record CompoundInterestResult(List<Double> deposits,
                                     List<Double> interests,
                                     List<Double> totalDeposit,
                                     List<Double> accuredInterest,
                                     List<Double> balance) {

    public CompoundInterestResult() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}