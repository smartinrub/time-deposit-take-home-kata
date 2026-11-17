package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.domain.TimeDeposit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

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
        // TODO: implement persistence layer
        throw new UnsupportedOperationException("Saving time deposits is not implemented yet");
    }

    private static TimeDeposit toDomain(TimeDepositEntity entity) {
        return new TimeDeposit(
                entity.getId(),
                entity.getPlanType(),
                entity.getBalance().doubleValue(),
                entity.getDays()
        );
    }
}
