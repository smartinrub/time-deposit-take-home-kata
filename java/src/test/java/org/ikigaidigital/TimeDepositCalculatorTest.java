package org.ikigaidigital;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TimeDepositCalculatorTest {

    private final TimeDepositCalculator calculator = new TimeDepositCalculator();

    @ParameterizedTest(name = "{0} plan with {1} days -> {2}")
    @CsvSource({
            // no interest during the first 30 days for any plan
            "basic,   30,  1200.00",
            "student, 30,  1200.00",
            "premium, 30,  1200.00",
            // basic: 1% from day 31 with no upper limit
            "basic,   31,  1201.00",
            "basic,   400, 1201.00",
            // student: 3% from day 31 until day 365, none after 1 year
            "student, 31,  1203.00",
            "student, 365, 1203.00",
            "student, 366, 1200.00",
            // premium: 5% only from day 46, which overrides the 30-day rule
            "premium, 31,  1200.00",
            "premium, 45,  1200.00",
            "premium, 46,  1205.00",
    })
    void shouldApplyMonthlyInterestAccordingToPlanAndDays(String planType, int days, double expectedBalance) {
        TimeDeposit deposit = new TimeDeposit(1, planType, 1200.00, days);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(expectedBalance);
    }

    @ParameterizedTest(name = "plan type \"{0}\" earns no interest")
    @CsvSource({"BASIC", "Premium", "gold"})
    void shouldNotApplyInterestWhenPlanTypeIsUnknown(String planType) {
        TimeDeposit deposit = new TimeDeposit(1, planType, 1200.00, 100);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1200.00);
    }

    @Test
    void shouldRoundInterestToTwoDecimals() {
        // 1234567.00 * 0.01 / 12 = 1028.8058... -> 1028.81
        TimeDeposit deposit = new TimeDeposit(1, "basic", 1234567.00, 45);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1235595.81);
    }

    @Test
    void shouldRoundInterestHalfUpWhenExactlyHalfway() {
        // 150.00 * 0.01 / 12 = 0.125 exactly -> 0.13 (HALF_DOWN and HALF_EVEN would give 0.12)
        TimeDeposit deposit = new TimeDeposit(1, "basic", 150.00, 31);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(150.13);
    }

    @Test
    void shouldRoundInterestUsingItsExactBinaryValue() {
        // 18.00 * 0.01 / 12 is 0.015 on paper, but the double is 0.01499999...,
        // and new BigDecimal(double) keeps that exact value, so it rounds down to 0.01
        TimeDeposit deposit = new TimeDeposit(1, "basic", 18.00, 31);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(18.01);
    }

    @Test
    void shouldRoundOnlyTheInterestAndNotTheResultingBalance() {
        // interest 0.0833... -> 0.08; the balance keeps its third decimal
        TimeDeposit deposit = new TimeDeposit(1, "basic", 100.005, 31);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(100.085);
    }

    @Test
    void shouldUpdateEachDepositInTheListIndependently() {
        TimeDeposit basic = new TimeDeposit(1, "basic", 1200.00, 31);
        TimeDeposit student = new TimeDeposit(2, "student", 1200.00, 366);
        TimeDeposit premium = new TimeDeposit(3, "premium", 1200.00, 46);

        calculator.updateBalance(List.of(basic, student, premium));

        assertThat(basic.getBalance()).isEqualTo(1201.00);
        assertThat(student.getBalance()).isEqualTo(1200.00);
        assertThat(premium.getBalance()).isEqualTo(1205.00);
    }

    @Test
    void shouldApplyInterestAgainOnEachCall() {
        TimeDeposit deposit = new TimeDeposit(1, "basic", 1200.00, 31);

        calculator.updateBalance(List.of(deposit));
        calculator.updateBalance(List.of(deposit));

        // second month: 1201.00 * 0.01 / 12 = 1.0008... -> 1.00
        assertThat(deposit.getBalance()).isEqualTo(1202.00);
    }
}
