package org.ikigaidigital;

class PremiumInterestPolicy implements InterestPolicy {

    private static final double ANNUAL_RATE = 0.05;
    private static final int INTEREST_FREE_DAYS = 45;

    @Override
    public double calculateMonthlyInterest(TimeDeposit deposit) {
        if (deposit.getDays() <= INTEREST_FREE_DAYS) {
            return 0;
        }
        return deposit.getBalance() * ANNUAL_RATE / 12;
    }
}
