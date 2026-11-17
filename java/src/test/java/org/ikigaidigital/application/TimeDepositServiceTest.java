package org.ikigaidigital.application;

import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.application.port.WithdrawalRepository;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TimeDepositServiceTest {

    @Mock
    private TimeDepositRepository timeDepositRepository;

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @InjectMocks
    private TimeDepositService service;

    @Test
    void shouldReturnEachDepositWithOnlyItsOwnWithdrawals() {
        var basic = new TimeDeposit(1, "basic", 1000.00, 45);
        var premium = new TimeDeposit(2, "premium", 2000.00, 60);
        var first = new Withdrawal(10, 1, new BigDecimal("100.00"), LocalDate.of(2026, 1, 15));
        var second = new Withdrawal(11, 1, new BigDecimal("50.50"), LocalDate.of(2026, 2, 10));
        given(timeDepositRepository.findAll()).willReturn(List.of(basic, premium));
        given(withdrawalRepository.findAll()).willReturn(List.of(first, second));

        assertThat(service.listAll()).containsExactly(
                new TimeDepositWithWithdrawals(basic, List.of(first, second)),
                new TimeDepositWithWithdrawals(premium, List.of()));
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoDeposits() {
        given(timeDepositRepository.findAll()).willReturn(List.of());
        given(withdrawalRepository.findAll()).willReturn(List.of());

        assertThat(service.listAll()).isEmpty();
    }
}
