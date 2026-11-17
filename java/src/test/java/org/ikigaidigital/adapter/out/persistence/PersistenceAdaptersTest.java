package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.PostgresTestcontainersConfiguration;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@Import({PostgresTestcontainersConfiguration.class, TimeDepositPersistenceAdapter.class, WithdrawalPersistenceAdapter.class})
@Sql(statements = {
        "INSERT INTO time_deposits (id, plan_type, days, balance) VALUES (2, 'premium', 60, 2000.75), (1, 'basic', 45, 1234.56)",
        "INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES (11, 1, 50.50, '2026-02-10'), (10, 1, 100.25, '2026-01-15')"
})
class PersistenceAdaptersTest {

    @Autowired
    private TimeDepositPersistenceAdapter timeDepositAdapter;

    @Autowired
    private WithdrawalPersistenceAdapter withdrawalAdapter;

    @Test
    void shouldLoadAllTimeDepositsOrderedById() {
        assertThat(timeDepositAdapter.findAll())
                .extracting(TimeDeposit::getId, TimeDeposit::getPlanType, TimeDeposit::getBalance, TimeDeposit::getDays)
                .containsExactly(
                        tuple(1, "basic", 1234.56, 45),
                        tuple(2, "premium", 2000.75, 60)
                );
    }

    @Test
    void shouldLoadAllWithdrawalsOrderedById() {
        List<Withdrawal> allWithdrawals = withdrawalAdapter.findAll();

        assertThat(allWithdrawals).containsExactly(
                new Withdrawal(10, 1, new BigDecimal("100.25"), LocalDate.of(2026, 1, 15)),
                new Withdrawal(11, 1, new BigDecimal("50.50"), LocalDate.of(2026, 2, 10))
        );
    }
}
