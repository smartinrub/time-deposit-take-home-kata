package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "withdrawals")
class WithdrawalEntity {

    @Id
    private Integer id;
    private Integer timeDepositId;
    private BigDecimal amount;
    private LocalDate date;

    protected WithdrawalEntity() {
    }

    Integer getId() {
        return id;
    }

    Integer getTimeDepositId() {
        return timeDepositId;
    }

    BigDecimal getAmount() {
        return amount;
    }

    LocalDate getDate() {
        return date;
    }
}
