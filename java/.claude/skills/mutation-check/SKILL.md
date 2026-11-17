---
name: mutation-check
description: Check that the tests catch real defects by applying small deliberate bugs (mutations) to production code one at a time and reporting any the tests miss. Use after writing tests or refactoring, before committing.
---

# Mutation check

Coverage shows which lines ran; this shows whether the tests would notice if those lines were wrong.

## Steps

1. Pick the target classes: the production files named in the arguments, otherwise the files changed in
   `git diff --name-only HEAD -- src/main` (fall back to `src/main/java/**/domain/*.java`).
2. For each target, list 3–8 meaningful mutations. Prefer ones a real bug could look like:
   - boundary flips: `>` ↔ `>=`, `<` ↔ `<=`
   - constants: rates, day limits (e.g. `0.01` → `0.02`, `45` → `44`)
   - rounding: `HALF_UP` → `HALF_DOWN` / `HALF_EVEN`, `new BigDecimal(x)` → `BigDecimal.valueOf(x)`
   - defaults and fallbacks: a `return 0` → `return 1`, a lookup default
   - removed conditions or swapped branches
   Skip mutations that cannot change behaviour (equivalent mutants), and say why.
3. Apply each mutation alone:
   - copy the file to the scratchpad directory first
   - change it with a single targeted edit
   - run `mvn -q test` and record whether it failed (caught) or passed (survived)
   - restore the file from the copy before the next mutation
4. Finish with `git status --short` and `mvn -q test` to prove the code is back to its original state and green.

## Report

A table: file, mutation, caught / SURVIVED. For each survivor, explain which behaviour is untested and propose
the smallest test that would catch it (input values and expected result). Do not add the test unless asked.
