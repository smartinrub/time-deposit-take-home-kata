# Time Deposit Kata - Java Solution

## Assumptions

The assignment leaves some points open. Instead of blocking on clarification, the following assumptions were made.

### Interest calculation

1. **The current `TimeDepositCalculator.updateBalance` behaviour is the source of truth.** Where it differs from the README wording, the code wins, because the README says it "is functioning correctly":
   - "No interest for the first 30 days" means interest applies only when `days > 30` (day 30 earns nothing, day 31 earns interest).
   - Student: "no interest after 1 year" means interest applies only when `days < 366` (day 365 earns interest, day 366 does not).
   - Premium: "interest starts after 45 days" means `days > 45`. This overrides the general 30-day rule.
2. **Rates are annual and applied monthly.** Each call to `updateBalance` adds one month of interest (`balance * rate / 12`).
3. **Only the interest amount is rounded**, to 2 decimals with `HALF_UP`, before it is added to the balance. The balance itself is not re-rounded. This matches the existing code exactly, including the `new BigDecimal(double)` conversion.
4. **Plan types are case-sensitive** (`basic`, `student`, `premium`). Unknown plan types earn no interest and raise no error. This is the current behaviour, and the README says invalid input handling is not required.
5. **`days` is an input, not something the system advances.** Updating balances does not change `days`.
6. **Withdrawals do not affect interest.** The current calculator ignores them and the requirements do not define any rule linking them.

### Domain model

7. **`TimeDeposit` stays unchanged** (no new constructor parameters, no removed or renamed members) because it is a shared class. Withdrawals are modelled separately and combined with the deposit only to build the GET response.
8. **`balance` stays a `Double` in the domain**, as in the shared class. The database stores it as `DECIMAL`, and the persistence adapter converts between the two.

### API

9. **Exactly two endpoints:**
   - `GET /time-deposits` returns every deposit with `id`, `planType`, `balance`, `days` and `withdrawals`.
   - `POST /time-deposits/balances` applies one month of interest to every deposit. It is **not idempotent**: each call adds interest again, which matches calling `updateBalance` repeatedly. `POST` is used instead of `PUT` because the operation is not idempotent.
10. **There is no endpoint to create deposits or withdrawals** (only two endpoints are allowed). Sample data is loaded through database migrations.
11. **The update endpoint returns `204 No Content`.** Clients that want the new balances call the GET endpoint.
12. **Errors are not handled explicitly**, because the README says invalid input and exception handling are not required. Framework defaults apply.

### Persistence

13. **The table and column names follow the README** (`timeDeposits`, `withdrawals`, `timeDepositId`, ...), mapped to the database's naming conventions where needed.
14. **The balance update runs in a single transaction**: either every deposit is updated or none is.
