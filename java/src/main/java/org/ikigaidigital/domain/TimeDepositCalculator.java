package org.ikigaidigital.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class TimeDepositCalculator {

    private static final int INTEREST_FREE_DAYS = 30;
    // plan types are case-sensitive: unknown ones (e.g. "BASIC", "gold") earn no interest, as in the original code
    private static final InterestPolicy NO_INTEREST = deposit -> 0;

    private final Map<String, InterestPolicy> policiesByPlanType = Map.of(
            "basic", new BasicInterestPolicy(),
            "student", new StudentInterestPolicy(),
            "premium", new PremiumInterestPolicy()
    );

    public void updateBalance(List<TimeDeposit> deposits) {
        for (TimeDeposit deposit : deposits) {
            double interest = roundToCents(calculateMonthlyInterest(deposit));
            deposit.setBalance(deposit.getBalance() + interest);
        }
    }

    private double calculateMonthlyInterest(TimeDeposit deposit) {
        if (deposit.getDays() <= INTEREST_FREE_DAYS) {
            return 0;
        }
        InterestPolicy policy = policiesByPlanType.getOrDefault(deposit.getPlanType(), NO_INTEREST);
        return policy.calculateMonthlyInterest(deposit);
    }

    private double roundToCents(double amount) {
        // new BigDecimal(double), not BigDecimal.valueOf: preserves existing rounding results
        return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
