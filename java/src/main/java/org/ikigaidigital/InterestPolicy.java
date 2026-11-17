package org.ikigaidigital;

/**
 * Plan-specific interest rule. Adding a plan type means adding an implementation
 * and registering it in {@link TimeDepositCalculator}.
 */
interface InterestPolicy {

    double calculateMonthlyInterest(TimeDeposit deposit);
}
