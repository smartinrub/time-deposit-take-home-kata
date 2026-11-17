package org.ikigaidigital.domain;

/**
 * Plan-specific interest rule. Adding a plan type means adding an implementation
 * and registering it in {@link TimeDepositCalculator}.
 */
interface InterestPolicy {

    double calculateMonthlyInterest(TimeDeposit deposit);
}
