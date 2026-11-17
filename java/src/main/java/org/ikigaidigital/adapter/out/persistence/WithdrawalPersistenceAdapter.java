package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.application.port.WithdrawalRepository;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class WithdrawalPersistenceAdapter implements WithdrawalRepository {

    private final WithdrawalJpaRepository jpaRepository;

    WithdrawalPersistenceAdapter(WithdrawalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Withdrawal> findAll() {
        return jpaRepository.findAll(Sort.by("id")).stream()
                .map(WithdrawalPersistenceAdapter::toDomain)
                .toList();
    }

    private static Withdrawal toDomain(WithdrawalEntity entity) {
        return new Withdrawal(entity.getId(), entity.getTimeDepositId(), entity.getAmount(), entity.getDate());
    }
}
