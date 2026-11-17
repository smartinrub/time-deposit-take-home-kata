package org.ikigaidigital.adapter.in.web;

import org.ikigaidigital.application.TimeDepositService;
import org.ikigaidigital.application.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TimeDepositController.class)
class TimeDepositControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimeDepositService service;

    @Test
    void shouldReturnTimeDepositsWithTheirWithdrawals() throws Exception {
        var withdrawal = new Withdrawal(10, 1, new BigDecimal("100.25"), LocalDate.of(2026, 1, 15));
        var basicTimeDeposit = new TimeDepositWithWithdrawals(new TimeDeposit(1, "basic", 1234.56, 45), List.of(withdrawal));
        var premiumTimeDeposit = new TimeDepositWithWithdrawals(new TimeDeposit(2, "premium", 2000.75, 60), List.of());
        given(service.listAll()).willReturn(List.of(basicTimeDeposit, premiumTimeDeposit));

        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].planType").value("basic"))
                .andExpect(jsonPath("$[0].balance").value(1234.56))
                .andExpect(jsonPath("$[0].days").value(45))
                .andExpect(jsonPath("$[0].withdrawals", hasSize(1)))
                .andExpect(jsonPath("$[0].withdrawals[0].id").value(10))
                .andExpect(jsonPath("$[0].withdrawals[0].amount").value(100.25))
                .andExpect(jsonPath("$[0].withdrawals[0].date").value("2026-01-15"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].withdrawals", empty()));
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoTimeDeposits() throws Exception {
        given(service.listAll()).willReturn(List.of());

        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void shouldUpdateAllBalancesAndRespondNoContent() throws Exception {
        mockMvc.perform(post("/time-deposits/balances"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        then(service).should().updateAllBalances();
    }
}
