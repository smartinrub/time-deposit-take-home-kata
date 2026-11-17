package org.ikigaidigital.domain;

class StudentInterestPolicy implements InterestPolicy {

    private static final double ANNUAL_RATE = 0.03;
    private static final int LAST_DAY_WITH_INTEREST = 365;

    @Override
    public double calculateMonthlyInterest(TimeDeposit deposit) {
        if (deposit.getDays() > LAST_DAY_WITH_INTEREST) {
            return 0;
        }
        return deposit.getBalance() * ANNUAL_RATE / 12;
    }
}
