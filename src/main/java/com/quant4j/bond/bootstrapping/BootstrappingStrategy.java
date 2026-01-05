package com.quant4j.bond.bootstrapping;

import com.quant4j.bond.math.interpolation.InterpolationStrategy;
import com.quant4j.bond.pojo.Bond;

import java.util.List;
import java.util.Map;

/**
 * Interface for strategies that build a yield curve from a list of bonds.
 */
public interface BootstrappingStrategy {

    /**
     * Calculates the yield curve.
     * The input bonds are trading at Par (Price = Face Value).
     * Uses the configured interpolation strategy for rates between maturities.
     *
     * @param bonds the list of benchmark bonds.
     * @param interpolationStrategy the interpolation strategy to use.
     * @return a Map representing the curve: Time (Years) -> Rate.
     */
    Map<Double, Double> bootstrapFromParBonds(List<Bond> bonds, InterpolationStrategy interpolationStrategy);

    /**
     * Calculates the yield curve using market prices for the input bonds.
     * The bonds are not necessarily trading at Par.
     *
     * @param bonds the list of benchmark bonds.
     * @param marketPrices a map of bond to its market price.
     * @param interpolationStrategy the interpolation strategy to use.
     * @return a Map representing the curve: Time (Years) -> Rate.
     */
    Map<Double, Double> bootstrap(List<Bond> bonds, Map<Bond, Double> marketPrices, InterpolationStrategy interpolationStrategy);
}
