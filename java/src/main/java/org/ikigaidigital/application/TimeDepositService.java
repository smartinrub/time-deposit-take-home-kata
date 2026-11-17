package org.ikigaidigital.application;

import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.application.port.WithdrawalRepository;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class TimeDepositService {

    private final TimeDepositRepository timeDepositRepository;
    private final WithdrawalRepository withdrawalRepository;

    public TimeDepositService(
            TimeDepositRepository timeDepositRepository,
            WithdrawalRepository withdrawalRepository
    ) {
        this.timeDepositRepository = timeDepositRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    public List<TimeDepositWithWithdrawals> listAll() {
        List<Withdrawal> allWithdrawals = withdrawalRepository.findAll();
        Map<Integer, List<Withdrawal>> withdrawalsByDepositId = allWithdrawals.stream()
                .collect(groupingBy(Withdrawal::timeDepositId));

        return timeDepositRepository.findAll().stream()
                .map(deposit -> toTimeDepositWithWithdrawals(deposit, withdrawalsByDepositId))
                .toList();
    }

    private static TimeDepositWithWithdrawals toTimeDepositWithWithdrawals(
            TimeDeposit deposit,
            Map<Integer, List<Withdrawal>> withdrawalsByDepositId
    ) {
        return new TimeDepositWithWithdrawals(
                deposit,
                withdrawalsByDepositId.getOrDefault(deposit.getId(), List.of())
        );
    }
}
