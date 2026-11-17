# Time Deposit Kata - Java Solution

## Running locally

Requirements: Java 25 and a running Docker daemon (Docker Desktop). Run the commands from this `java/` folder.

```bash
# with sample data (dev profile): one deposit per plan on each side of its interest boundaries
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# empty database (schema only)
mvn spring-boot:run
```

Spring Boot starts PostgreSQL from `compose.yaml` automatically and applies the Flyway migrations on startup.

- **IntelliJ:** the shared run configuration `TimeDepositApplication` (in `.run/`) already has the `dev` profile active.
- **Reset the local database** (e.g. after editing a migration that was already applied): `docker compose down -v`

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

7. **`TimeDeposit`'s public API stays unchanged** (no new constructor parameters, no removed or renamed members) because it is a shared class. "No breaking changes" is read as covering that API and the `updateBalance` signature, not the package. `TimeDeposit`, `TimeDepositCalculator` and the interest policies moved from `org.ikigaidigital` to `org.ikigaidigital.domain` to support the hexagonal layout. External callers, if any, only need to update one import. Java has no type aliases, so the old package name cannot be kept working alongside the new one. Withdrawals are modelled separately and combined with the deposit only to build the GET response.
8. **`balance` stays a `Double` in the domain**, as in the shared class. The database stores it as `DECIMAL`, and the persistence adapter converts between the two.

### API

9. **Exactly two endpoints:**
   - `GET /time-deposits` returns every deposit with `id`, `planType`, `balance`, `days` and `withdrawals`.
   - `POST /time-deposits/balances` applies one month of interest to every deposit. It is **not idempotent**: each call adds interest again, which matches calling `updateBalance` repeatedly. `POST` is used instead of `PUT` because the operation is not idempotent.
10. **There is no endpoint to create deposits or withdrawals** (only two endpoints are allowed). Sample data is loaded through database migrations.
11. **The update endpoint returns `204 No Content`.** Clients that want the new balances call the GET endpoint.
12. **Errors are not handled explicitly**, because the README says invalid input and exception handling are not required. Framework defaults apply.

### Persistence

13. **Table and column names are the README's names in snake_case** (`timeDeposits` → `time_deposits`, `planType` → `plan_type`, `timeDepositId` → `time_deposit_id`). Postgres folds unquoted identifiers to lowercase, so camelCase would either silently become `timedeposits` or require quoting every identifier. snake_case is the Postgres convention and Hibernate's default mapping.
14. **IDs are plain `INTEGER` primary keys without auto-generation.** No endpoint creates deposits or withdrawals, so nothing needs generated IDs.
15. **Money columns are `NUMERIC(19, 2)`** (cents). Interest is already rounded to cents and the sample balances are whole cents, so storing two decimals loses nothing. It also drops floating-point noise from the `Double` balance (e.g. `6.029999999999999` is stored as `6.03`).
16. **`plan_type` has no check constraint**, because unknown plan types are valid and earn no interest (assumption 4).
17. **Sample data is dev-only.** There is no create endpoint, so local runs need data, but production migrations (`db/migration`) contain only schema. The seed (`db/seed/R__seed_time_deposits.sql`) loads only with the `dev` profile, which must be activated explicitly (see [Running locally](#running-locally)). It covers each plan on both sides of its interest boundaries. It is a repeatable migration, so it can never clash with a future versioned schema migration, and `ON CONFLICT DO NOTHING` makes re-runs keep existing rows.
18. **The balance update runs in a single transaction**: either every deposit is updated or none is.
