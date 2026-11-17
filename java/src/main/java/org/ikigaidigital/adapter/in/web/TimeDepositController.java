package org.ikigaidigital.adapter.in.web;

import org.ikigaidigital.adapter.in.web.api.model.TimeDepositDto;
import org.ikigaidigital.adapter.in.web.api.model.WithdrawalDto;
import org.ikigaidigital.application.TimeDepositService;
import org.ikigaidigital.application.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/time-deposits")
public class TimeDepositController {

    private final TimeDepositService timeDepositService;

    public TimeDepositController(TimeDepositService timeDepositService) {
        this.timeDepositService = timeDepositService;
    }

    @GetMapping
    public List<TimeDepositDto> getTimeDeposits() {
        return timeDepositService.listAll().stream()
                .map(TimeDepositController::toDto)
                .toList();
    }

    private static TimeDepositDto toDto(TimeDepositWithWithdrawals depositWithWithdrawals) {
        TimeDeposit deposit = depositWithWithdrawals.timeDeposit();
        return new TimeDepositDto(
                deposit.getId(),
                deposit.getPlanType(),
                BigDecimal.valueOf(deposit.getBalance()),
                deposit.getDays(),
                depositWithWithdrawals.withdrawals().stream().map(TimeDepositController::toDto).toList()
        );
    }

    private static WithdrawalDto toDto(Withdrawal withdrawal) {
        return new WithdrawalDto(withdrawal.id(), withdrawal.amount(), withdrawal.date());
    }
}
