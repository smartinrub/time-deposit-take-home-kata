package org.ikigaidigital.application.port;

import org.ikigaidigital.domain.TimeDeposit;

import java.util.List;

public interface TimeDepositRepository {

    List<TimeDeposit> findAll();
}
