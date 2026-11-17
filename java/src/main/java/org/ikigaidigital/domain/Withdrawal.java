package org.ikigaidigital.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

// BigDecimal for money (TimeDeposit.balance stays Double only for compatibility)
public record Withdrawal(int id, int timeDepositId, BigDecimal amount, LocalDate date) {
}
