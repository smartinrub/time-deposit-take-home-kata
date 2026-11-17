package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.domain.TimeDeposit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
class TimeDepositPersistenceAdapter implements TimeDepositRepository {

    private final TimeDepositJpaRepository jpaRepository;

    TimeDepositPersistenceAdapter(TimeDepositJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TimeDeposit> findAll() {
        return jpaRepository.findAll(Sort.by("id")).stream()
                .map(TimeDepositPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<TimeDeposit> deposits) {
        jpaRepository.saveAll(deposits.stream()
                .map(TimeDepositPersistenceAdapter::toEntity)
                .toList());
    }

    private static TimeDeposit toDomain(TimeDepositEntity entity) {
        return new TimeDeposit(
                entity.getId(),
                entity.getPlanType(),
                entity.getBalance().doubleValue(),
                entity.getDays()
        );
    }

    private static TimeDepositEntity toEntity(TimeDeposit deposit) {
        return new TimeDepositEntity(
                deposit.getId(),
                deposit.getPlanType(),
                deposit.getDays(),
                // stored in cents (NUMERIC(19, 2)); rounded here explicitly rather than silently by the database
                BigDecimal.valueOf(deposit.getBalance()).setScale(2, RoundingMode.HALF_UP)
        );
    }
}
