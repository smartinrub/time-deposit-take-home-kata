# CLAUDE.md

Project rules for AI-assisted work on the Time Deposit refactoring kata (Java solution).
The assignment is in `../README.md`. Assumptions are justified in short code comments where they are implemented.

## Hard constraints (never break these)

- `TimeDeposit` is shared: do not change its constructor, getters or setters without explicit approval. It lives in `org.ikigaidigital.domain`; its public API must stay unchanged.
- Do not change the signature of `TimeDepositCalculator.updateBalance(List<TimeDeposit>)`.
- `updateBalance` behaviour must stay exactly as it is, including the boundaries (`days > 30`, student `days < 366`, premium `days > 45`) and rounding (only the interest is rounded, 2 decimals, `HALF_UP`, via `new BigDecimal(double)`).
- Build **exactly two** REST endpoints: `GET /time-deposits` and `POST /time-deposits/balances`. No others.
- No invalid-input or exception handling unless asked; the assignment excludes it.

## Architecture (hexagonal)

- `org.ikigaidigital.domain`: plain Java only, with no Spring, JPA or Jackson annotations. Holds the models, interest policies and calculator.
- `org.ikigaidigital.application`: use cases and ports (interfaces). Depends only on the domain.
- `org.ikigaidigital.adapter.in.web`: `@RestController`s whose mappings match the OpenAPI contract exactly, returning the DTOs generated from it (contract-first).
- `org.ikigaidigital.adapter.out.persistence`: JPA entities, Spring Data repositories and the port implementation. Maps entities to domain objects and back.
- Dependencies point inwards: adapters → application → domain, never the other way.
- Don't add abstractions without a clear current need. No speculative interfaces, factories or layers.

## Stack

- Java 25 (LTS), Spring Boot 4, Maven.
- PostgreSQL, Spring Data JPA, Flyway migrations (schema in `db/migration`; dev-only sample data in `db/seed`, loaded by the `dev` profile).
- OpenAPI contract in `src/main/resources/static/openapi.yaml` is the source of truth. `openapi-generator-maven-plugin` generates only the `*Dto` models into `org.ikigaidigital.adapter.in.web.api.model` (never edit generated code; change the contract). Paths and status codes are written by hand in the controllers, so the web tests must check them against the contract. Swagger UI (springdoc) serves the contract file as-is.
- Tests: JUnit 5, AssertJ, Testcontainers (PostgreSQL) for integration tests.

## Workflow

- Work in small steps. Run `mvn verify` after each change; the build must be green before every commit.
- Write tests first: characterization tests before refactoring, and a failing test before each new behaviour.
- Test behaviour and outcomes, not interactions. Avoid mocks unless a port boundary needs one. Test boundaries explicitly (e.g. 30/31, 45/46, 365/366 days).
- Use descriptive test names in the style camelCase `should<Expected>When<Condition>` (e.g. `shouldNotApplyInterestWhenPlanTypeIsUnknown`).
- Refactor commits must not change test expectations. Keep refactors and features in separate commits.
- Use atomic Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`, `build:`, `docs:`, `chore:`). Each commit compiles and passes tests on its own.
- Only commit when the user asks, and never push without being asked.
- For an ambiguous requirement, make a reasonable assumption and justify it in a short code comment where it is implemented. Don't stop to ask.

## Harness (`.claude/`)

- `settings.json`: allows `mvn` and read-only `git`, denies `git push`. A PostToolUse hook runs `mvn -q test` after every edit to a `.java` file or `pom.xml` and reports failures back immediately.
- `/mutation-check`: applies small deliberate bugs to production code to confirm the tests catch them. Run it after adding tests or refactoring.
- `/pre-commit-review`: checks uncommitted changes against these rules, runs `mvn verify` and proposes commit splits and messages.

## Code style

- Match the surrounding code. Prefer clarity over cleverness.
- Remove dead code, unused imports and unused dependencies as you go.
- Comments explain *why*, not *what*.
- Every line must be explainable. Don't leave generated code you don't understand.

## Commands

- Build and test everything: `mvn verify`
- Unit tests only: `mvn test`
- Run the app: `mvn spring-boot:run` (needs a running PostgreSQL; see `README.md`)
