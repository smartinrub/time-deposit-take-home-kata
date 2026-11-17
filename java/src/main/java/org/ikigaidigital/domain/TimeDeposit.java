package org.ikigaidigital.domain;

public class TimeDeposit {
    // Integer as required. int overflows after ~2.1 billion rows, and sequential ids are guessable once exposed
    // through the API. A UUID avoids both (and can be generated outside the database), at the cost of larger indexes.
    private int id;
    private String planType;
    private Double balance; // kept as Double for compatibility with the shared API; BigDecimal would be the right type for money
    private int days;

    public TimeDeposit(int id, String planType, Double balance, int days) {
        this.id = id;
        this.planType = planType;
        this.balance = balance;
        this.days = days;
    }

    public int getId() { return id; }

    public String getPlanType() {
        return planType;
    }

    public Double getBalance() {
        return balance;
    }

    public int getDays() {
        return days;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
