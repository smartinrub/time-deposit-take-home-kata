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
Stopping a `dev` run removes its database (`docker compose down -v`), so sample data never leaks into a later run without the profile, and each `dev` run starts from fresh sample data.

- **Reset the local database** (e.g. after editing a migration that was already applied): `docker compose down -v`

### Running the tests

```bash
mvn verify
```

Runs the unit, web, persistence and end-to-end tests. The persistence and end-to-end tests start PostgreSQL with Testcontainers, so Docker must be running. The coverage summary is printed at the end of the build, and the full report is in `target/site/jacoco/index.html`.

## Using the API (Swagger)

The contract is [`src/main/resources/static/openapi.yaml`](src/main/resources/static/openapi.yaml). With the app running:

| | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI contract | http://localhost:8080/openapi.yaml |

### From Swagger UI

1. Start the app with sample data: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
2. Open http://localhost:8080/swagger-ui/index.html. Both endpoints are listed under **TimeDeposits**.
3. **`GET /time-deposits`** → **Try it out** → **Execute**. Returns every deposit with its withdrawals (`200`).
4. **`POST /time-deposits/balances`** → **Try it out** → **Execute**. Applies one month of interest to every deposit (`204`, no body).
5. Run **`GET /time-deposits`** again to see the new balances.

### From the command line

```bash
curl http://localhost:8080/time-deposits
curl -i -X POST http://localhost:8080/time-deposits/balances
```

### What to expect with the sample data

One `POST` changes the balances as follows. Each plan has a deposit on both sides of its interest boundary:

| id | plan | days | balance before | after one POST | why |
|---|---|---|---|---|---|
| 1 | basic | 30 | 10000.00 | 10000.00 | no interest during the first 30 days |
| 2 | basic | 45 | 1234567.00 | 1235595.81 | 1% / 12 = +1028.81 |
| 3 | student | 200 | 5000.00 | 5012.50 | 3% / 12 = +12.50 |
| 4 | student | 366 | 5000.00 | 5000.00 | no interest after one year |
| 5 | premium | 45 | 20000.00 | 20000.00 | premium interest starts after 45 days |
| 6 | premium | 46 | 20000.00 | 20083.33 | 5% / 12 = +83.33 |

Example deposit from `GET /time-deposits` after one `POST`:

```json
{
  "id": 2,
  "planType": "basic",
  "balance": 1235595.81,
  "days": 45,
  "withdrawals": [
    { "id": 1, "amount": 500.00, "date": "2026-01-15" },
    { "id": 2, "amount": 250.50, "date": "2026-02-10" }
  ]
}
```

`POST` is not idempotent: every call adds another month of interest. Restart the app to get the original sample data back, because stopping a `dev` run removes its database.

## AI-assisted development

### Tools and setup

- **Claude Code** (Anthropic's CLI coding agent) with the **Claude Opus 5.5** model, run from this `java/` folder throughout the exercise.
- All configuration is in the repository, so the setup is reproducible: install Claude Code, run `claude` in `java/`, and the rules, hook and skills below load automatically. The integration tests need Docker.

| File | Purpose |
|---|---|
| [`CLAUDE.md`](CLAUDE.md) | Project rules the agent follows. |
| [`.claude/settings.json`](.claude/settings.json) | Permissions. |
| [`.claude/skills/mutation-check`](.claude/skills/mutation-check/SKILL.md) | `/mutation-check`: applies small deliberate bugs to production code one at a time and reports any the tests miss. |
| [`.claude/skills/pre-commit-review`](.claude/skills/pre-commit-review/SKILL.md) | `/pre-commit-review`: checks uncommitted changes against `CLAUDE.md`, runs `mvn verify` and proposes commit splits and messages. |

### What was AI-assisted, and why

| Area | What the AI did | Why |
|---|---|---|
| Analysis | Read the requirements, code and the placeholder test. | Fast, systematic first pass. |
| Implementation | Propose refactor, adapters, entities, tests, and Flyway/Testcontainers/OpenAPI configuration | Boilerplate and configuration are where an agent saves the most time. |
