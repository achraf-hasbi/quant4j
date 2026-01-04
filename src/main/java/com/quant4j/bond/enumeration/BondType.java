package com.quant4j.bond.enumeration;

/**
 * Distinguishes between different types of bonds for calculation purposes.
 */
public enum BondType {
    /**
     * Standard bond with periodic coupon payments.
     */
    COUPON_BEARING,
    
    /**
     * Zero-coupon bond (single payment at maturity).
     */
    ZERO_COUPON
}
