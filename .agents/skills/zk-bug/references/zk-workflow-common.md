<!-- Canonical source: zk-bug/references/zk-workflow-common.md — keep both copies in sync -->

# ZK Workflow Common Reference

Shared procedural content for `zk-bug` and `zk-feature` skills. Each skill references these sections by name.
Use `[PREFIX]` as `B` (bug) or `F` (feature) depending on the calling skill.

---

## Excluded Directories

**Never read, review, or modify files under these directories — they are generated outputs, not source code:**

- `build/` — Gradle build output
- `bin/` — Compiled class files
- `codegen/` — Auto-generated source files

When searching for code, tracing callers, or reviewing changes, always exclude these paths. Only work with files under `src/`.

---

## Version Mapping

> - `ZK 10.4.0` → version code = `104`
> - `ZK 11.0.0` → version code = `110`
> - `ZK 11.1.0` → version code = `111`

---

## Special Case: `zul.xsd` / `zk.xsd` Issues

If the issue is about `zul.xsd` or `zk.xsd`, and validation depends on IDE suggestions or code completion:

- Do **not** create `Test.java`
- Create the `.zul` file only
- Write the `.zul` content so the IDE can show the expected suggestion/completion behavior
- Validate manually
- In `config.properties`, use `##manually##` instead of `##zats##`

---

## Java Test File Key Rules

- Extend `WebDriverTestCase`
- Use `jq()` for element selection, `click()` for interactions, `waitResponse()` after actions
- Use `org.junit.jupiter.api.Assertions` for assertions

### Validate test case against scenario

Before running, verify the test case logic matches the issue description:
- The test must verify the exact behavior described in the issue.
- Assertions must reflect the expected behavior stated in the issue, not assumptions.
- If the test does not match the described scenario, revise the test first.

### Run the test

```bash
./gradlew test --tests "[PREFIX][VERSION]_ZK_[ISSUE_NUMBER]Test" -DfailIfNoTests=false
```

Must run until your own related tests are completed and all pass.
Do not proceed to next steps if any related test is failing.

**If the test fails:**
1. Re-examine the test case — ensure it correctly describes the expected scenario.
2. If the test is correct, re-examine the fix/implementation.
3. Iterate between fixing the logic and re-running the test until all tests pass.

---

## Side Effect Check

**Golden rule: Backward compatible — no side effects.**

Before and after applying changes, actively check for the following:

| Area | What to check |
|------|---------------|
| Shared state | Does the change affect any field/variable used by other methods or components? |
| Event flow | Does the change trigger, suppress, or alter any existing events unexpectedly? |
| Rendering | Does the change affect layout, style, or redraw behavior beyond the intended scope? |
| Lifecycle | Does the change interfere with component init, detach, or re-render cycles? |
| Other callers | Are there other places that call the modified method? Do they still behave correctly? |

**If a side effect is found:**
1. Narrow the scope — prefer guarding with a condition over changing shared logic broadly.
2. If shared logic must change, trace all callers and verify each one is unaffected.
3. Add a comment explaining the constraint: `// ZK-[ISSUE_NUMBER]: limited to X condition to avoid affecting Y`
4. Re-run the test. If new failures appear, treat them as regressions and fix before proceeding.

---

## Code Comment Style

Always explain *why*, not just *what*. Prefix with the issue number:
```java
// ZK-[ISSUE_NUMBER]: [explain what this does and why it's needed]
```

### Coding style reference
See `references/coding-style.md` for full Java, JavaScript, and TypeScript style rules.

---

## config.properties Format Reference

```
##zats##     → tested via WebDriverTestCase (default)
##manually## → human-only test

# Code Level:
  A = Important (default)
  B = Unknown / Uncategorized
  C = Unimportant

# User Experience Level:
  H = Hard
  M = Middle
  E = Easy (default)

Components: comma-separated ZK component tags
```

