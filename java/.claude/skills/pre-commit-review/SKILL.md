---
name: pre-commit-review
description: Review the uncommitted changes against the project rules in CLAUDE.md before committing, run the full build, and propose atomic Conventional Commit splits and messages. Use when the user asks to review, check or prepare a commit.
---

# Pre-commit review

## Steps

1. Read `CLAUDE.md` so the rules are current.
2. Collect the changes: `git status --short`, `git diff`, `git diff --cached`; read any new untracked files in full.
3. Check every change against these rules and report each violation with `file:line`:
   - **Shared class**: `TimeDeposit` constructor, getters and setters unchanged; `updateBalance(List<TimeDeposit>)` signature unchanged.
   - **Behaviour**: no test expectation changed in a `refactor`; interest boundaries and rounding untouched unless a test commit explains why.
   - **Endpoints**: exactly two REST endpoints in the whole codebase (`GET /time-deposits`, `POST /time-deposits/balances`).
   - **Architecture**: no Spring, JPA or Jackson imports in `org.ikigaidigital.domain`; adapters depend on application/domain, never the reverse.
   - **Quality**: no dead code, unused imports or dependencies, commented-out code, or speculative abstractions.
   - **Tests**: new behaviour has a test; names follow `should<Expected>When<Condition>`; assertions check outcomes, not interactions.
   - **Docs**: new assumptions justified in a short code comment where implemented; `CLAUDE.md` still matches the code.
4. Run `mvn verify` and include the test count and the coverage summary.

## Report

1. Violations (or "none"), most serious first.
2. Build result.
3. Proposed commits: for each, the files it contains and a Conventional Commit message
   (`feat:`, `fix:`, `refactor:`, `test:`, `build:`, `docs:`, `chore:`). Split refactors from features and
   tests-first changes from the code they protect.

Do not stage, commit or push. The user commits.
