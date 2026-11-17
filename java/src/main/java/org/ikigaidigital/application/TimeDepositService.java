package org.ikigaidigital.application;

import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.application.port.WithdrawalRepository;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositCalculator;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class TimeDepositService {

    // stateless domain logic: created here rather than injected
    private final TimeDepositCalculator calculator = new TimeDepositCalculator();
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

    // One transaction: every balance is updated or none is.
    // Assuming requests do not need to be idempotent
    @Transactional
    public void updateAllBalances() {
        List<TimeDeposit> deposits = timeDepositRepository.findAll();
        calculator.updateBalance(deposits);
        timeDepositRepository.saveAll(deposits);
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
