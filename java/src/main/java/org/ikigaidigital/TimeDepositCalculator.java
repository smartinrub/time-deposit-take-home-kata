package org.ikigaidigital;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class TimeDepositCalculator {

    private static final int INTEREST_FREE_DAYS = 30;
    private static final InterestPolicy NO_INTEREST = deposit -> 0;

    private final Map<String, InterestPolicy> policiesByPlanType = Map.of(
            "basic", new BasicInterestPolicy(),
            "student", new StudentInterestPolicy(),
            "premium", new PremiumInterestPolicy()
    );

    public void updateBalance(List<TimeDeposit> deposits) {
        for (TimeDeposit deposit : deposits) {
            double interest = 0;

            if (deposit.getDays() > INTEREST_FREE_DAYS) {
                InterestPolicy policy = policiesByPlanType.getOrDefault(deposit.getPlanType(), NO_INTEREST);
                interest = policy.calculateMonthlyInterest(deposit);
            }

            double roundedInterest = new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP).doubleValue();
            deposit.setBalance(deposit.getBalance() + roundedInterest);
        }
    }
}
