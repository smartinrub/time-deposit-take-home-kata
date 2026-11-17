package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "time_deposits")
class TimeDepositEntity {

    @Id
    private Integer id;
    private String planType;
    private Integer days;
    private BigDecimal balance;

    protected TimeDepositEntity() {
    }

    Integer getId() {
        return id;
    }

    String getPlanType() {
        return planType;
    }

    Integer getDays() {
        return days;
    }

    BigDecimal getBalance() {
        return balance;
    }
}
