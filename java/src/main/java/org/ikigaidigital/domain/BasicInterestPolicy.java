package org.ikigaidigital.domain;

class BasicInterestPolicy implements InterestPolicy {

    private static final double ANNUAL_RATE = 0.01;

    @Override
    public double calculateMonthlyInterest(TimeDeposit deposit) {
        return deposit.getBalance() * ANNUAL_RATE / 12;
    }
}
