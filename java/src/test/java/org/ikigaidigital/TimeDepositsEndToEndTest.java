package org.ikigaidigital;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(PostgresTestcontainersConfiguration.class)
@Sql(statements = {
        "INSERT INTO time_deposits (id, plan_type, days, balance) VALUES (1, 'basic', 45, 1234.56), (2, 'premium', 60, 2000.75)",
        "INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES (10, 1, 100.25, '2026-01-15'), (11, 1, 50.50, '2026-02-10')"
})
@Sql(statements = {"DELETE FROM withdrawals", "DELETE FROM time_deposits"}, executionPhase = AFTER_TEST_METHOD)
class TimeDepositsEndToEndTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldListTimeDepositsWithTheirWithdrawals() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/time-deposits", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONAssert.assertEquals("""
                [
                  {
                    "id": 1, "planType": "basic", "balance": 1234.56, "days": 45,
                    "withdrawals": [
                      { "id": 10, "amount": 100.25, "date": "2026-01-15" },
                      { "id": 11, "amount": 50.50, "date": "2026-02-10" }
                    ]
                  },
                  { "id": 2, "planType": "premium", "balance": 2000.75, "days": 60, "withdrawals": [] }
                ]
                """, response.getBody(), JSONCompareMode.STRICT);
    }
}
