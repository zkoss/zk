---
name: zk-test-guide
description: >-
    Use this skill when the user mentions "zk-test-guide", "test", "testing",
    "WebDriverTestCase", "ZATS", or needs help writing/debugging ZK test cases.
    This skill provides enhanced test guidance leveraging CaseFoundry's
    find_usage_examples tool and historical test failure patterns.
    Automatically invoked by zk-bug (Step 4) and zk-feature (Step 5) to validate
    test case quality before running.
---

# ZK Test Guide Skill

Enhanced testing guidance for ZK Framework: write correct tests, avoid common pitfalls, and leverage real usage examples from the test suite.

---

## When to use

- Writing a new `WebDriverTestCase` or ZATS test for a ZK bug fix or feature.
- A test is failing and the developer needs help diagnosing why.
- The `zk-bug` or `zk-feature` skill invokes this to validate test quality before running.
- A developer asks "how do I test this component" or "why is my test flaky".

---

## Step 1: Determine Test Type

| Scenario | Test Type | Framework | Key Dependency |
|----------|-----------|-----------|---------------|
| Component behavior testable via browser interaction | WebDriver Test | `WebDriverTestCase` | zk-webdriver (free, ZK CE) |
| Server-side logic testable without real browser | ZATS Test | `ZatsEnvironment` | zats-mimic-ext (free, ZK CE) |
| Visual/layout issue requiring human eyes | Manual Only | `.zul` file only | `##manually##` in config.properties |
| `zul.xsd` / `zk.xsd` IDE completion | Manual Only | `.zul` file only | `##manually##` in config.properties |

> Both ZATS and ZK WebDriver are **free, open-source** (ZK CE). They do NOT require a paid license.
> Maven repository (NOT Maven Central): `https://mavensync.zkoss.org/maven2`

---

## Step 2: Fetch Real Usage Examples

> **MCP availability check:** Attempt the `find_usage_examples` call below. If it fails (connection error, tool not found), CaseFoundry is unavailable — use the Manual Fallback instead.

### When MCP is available

Call CaseFoundry to find real test examples for the component(s) under test:

```
find_usage_examples(component="<component name>", zk_version="<if known>", method="<method or event>")
```

This returns actual test code snippets from the ZK test suite showing:
- How the component is instantiated and interacted with
- What assertions are typical
- What edge cases are covered

**Use these examples as reference patterns**, not as copy-paste templates. Adapt to the specific issue.

### When MCP is unavailable

Search for existing test examples locally:
1. `grep -rn "<component name>" zktest/src/main/java/ --include="*.java" | head -20` — find tests that reference the component.
2. Read 2–3 matching test files to understand the interaction patterns used.
3. Use the test templates in Step 6 below as the baseline structure.

---

## Step 3: Critical Test Rules

These rules are derived from CaseFoundry's historical test failure cases. Every test MUST comply.

### 3.1 WebDriver Lifecycle (MANDATORY ORDER)

```java
@Test
public void test() {
    connect();                    // 1. MUST be first — creates the WebDriver session
    WebDriver driver = getDriver(); // 2. Only safe AFTER connect()
    // ... test logic ...
}
```

**`connect()` MUST be called before `getDriver()` or any driver operation.**
Calling `getDriver()` before `connect()` will fail silently or throw NPE.

### 3.2 `waitResponse()` After Every UI Action

```java
click(widget);
waitResponse();    // MANDATORY — waits for AU request/response cycle to complete

type(input, "text");
waitResponse();    // MANDATORY — even after type()

selectComboitem(combo, 0);
waitResponse();    // MANDATORY — after any selection change
```

**Every action that triggers a server round-trip MUST be followed by `waitResponse()`.**
Missing `waitResponse()` is the #1 cause of flaky tests.

### Common interaction patterns

```java
// Clear an input field (set to empty)
type(input, "");
waitResponse();

// Trigger blur by clicking elsewhere
click(jq("@label"));
waitResponse();

// Check if a server-side error occurred (e.g., WrongValueException, NPE)
assertFalse(hasError(), "No server error expected");

// Get error message text when validation is expected to fail
assertTrue(hasError(), "Validation error expected");
```

- `type(target, "")` — clears the input; triggers onChange + server round-trip.
- `hasError()` — returns `true` if a ZK server error (e.g., exception) is visible on the page. Use to verify no unexpected exceptions after an action.
- `click(jq("@label"))` or `click(jq("body"))` — triggers blur on the current focused component.

### 3.3 Null-Check `jq()` Results Before Access

```java
// BAD — will NPE if element is not rendered yet
String text = jq(".z-label").text();

// GOOD — verify element exists first
JQuery label = jq(".z-label");
assertTrue(label.exists(), "Label should be rendered");
String text = label.text();
```

A common failure pattern: test assumes DOM structure exists immediately after click but the element is not yet rendered.

### 3.4 Never Override `isHeadless()`

```java
// FORBIDDEN — do not add this to any Test.java
@Override
protected boolean isHeadless() { return false; }
```

This is a ZK-specific guardrail enforced across all skills. If found, it must be removed.

### 3.5 Handle ZatsException Wrapping

When testing for expected exceptions in ZATS:

```java
// BAD — ZatsException wraps the real exception
try {
    agent.click();
    Assertions.fail("Should throw");
} catch (UnsupportedOperationException e) {
    // This will never catch — ZATS wraps it
}

// GOOD — inspect the cause chain
try {
    agent.click();
    Assertions.fail("Should throw");
} catch (ZatsException e) {
    assertTrue(e.getCause() instanceof UnsupportedOperationException);
}
```

Without this, the test passes incorrectly because the wrong exception type is caught.

### 3.6 W3C WebDriver Compatibility

- Use W3C-compliant element interaction patterns, not legacy Selenium-specific APIs.
The ZK test suite has been refactored for W3C WebDriver protocol — use W3C-compliant APIs only.

### 3.7 Set `-DzkWebdriverContextPath`

WebDriver tests require this system property. Without it:
```
… zkWebdriverContextPath is not found
```

Ensure the Gradle/Maven test task sets:
```
-DzkWebdriverContextPath=/zktest
```

---

## Step 4: Test Structure Validation

When invoked from `zk-bug` or `zk-feature`, validate the test against these criteria:

### 4.1 Issue-Scenario Match

- [ ] Test reproduces the **exact scenario** described in the JIRA issue.
- [ ] Assertions check the **specific behavior** mentioned in the issue (not just "no exception").
- [ ] If the issue describes user interaction steps, the test follows the same steps.

### 4.2 Coverage Completeness

- [ ] **Happy path** is tested (the fix/feature works as intended).
- [ ] **Edge case from the issue** is tested (the specific trigger condition).
- [ ] If the bug was about a specific component state (detached, invalidated, re-rendered), the test puts the component in that state.

### 4.3 Test Stability

- [ ] `waitResponse()` follows every UI-triggering action.
- [ ] No hardcoded `Thread.sleep()` — use `waitResponse()` or explicit waits instead.
- [ ] `jq()` results are null-checked before property access.
- [ ] Test does not depend on rendering timing (use explicit waits, not DOM assumptions).

---

## Step 5: Debugging Failing Tests

If a test is failing, use this diagnostic flow:

### 5.1 Classify the Failure

| Symptom | Likely Cause | Fix |
|---------|-------------|-----|
| NPE in test code | Missing `connect()` or null `jq()` result | Add connect() first; null-check jq() |
| Assertion fails intermittently | Missing `waitResponse()` | Add waitResponse() after every UI action |
| Element not found | Wrong selector or element not yet rendered | Check selector; add explicit wait |
| ZatsException instead of expected exception | ZATS wraps exceptions | Catch ZatsException, inspect cause |
| `zkWebdriverContextPath is not found` | Missing system property | Add `-DzkWebdriverContextPath` |
| Test passes locally, fails in CI | Headless browser differences | Do NOT override `isHeadless()` |

### 5.2 Search for Historical Solutions

If CaseFoundry MCP is available:

```
diagnose_bug(query="test failure <describe symptom>", domain="ZK", source="zk_test", top_k=5)
```

If MCP is unavailable: search `git log --all --oneline --grep="<symptom keyword>"` in the test repo for prior fixes to similar test failures, and consult Step 3 rules above for common root causes.

---

## Step 6: Generate Test Skeleton

When creating a new test, use this template (adjust imports as needed):

```java
/* [PREFIX][VERSION]_ZK_[ISSUE_NUMBER]Test.java
 *
 *  Purpose: [What this test verifies]
 *  Description: [Link to JIRA issue]
 *  History: [Date], Created by [author]
 *
 * Copyright (C) [year] Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class [PREFIX][VERSION]_ZK_[ISSUE_NUMBER]Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();  // 1. Always first

        // 2. Interact with components
        JQuery target = jq("<selector>");
        assertNotNull(target, "Target element should exist");

        click(target);
        waitResponse();  // 3. Always after UI action

        // 4. Assert expected behavior
        JQuery result = jq("<result selector>");
        assertTrue(result.exists(), "<explain what should appear>");
        assertEquals("<expected>", result.text().trim());
    }
}
```

---

## Step 7: Chain Back (if invoked from zk-bug / zk-feature)

Return the validated test or diagnostic results to the calling skill.

If test validation found issues:
- List the specific violations.
- Suggest fixes for each.
- Do NOT proceed to next step until the test is correct.

---

## Guardrails

- **Do not generate tests that only check "no exception thrown".** Tests must assert specific behavioral outcomes.
- **Do not use `Thread.sleep()` in generated tests.** Always use `waitResponse()` or component-based waits.
- **Always include `connect()` as the first line** in WebDriver test methods.
- **Always include `waitResponse()` after UI actions** — err on the side of extra waits rather than missing ones.
- **Prefer real usage examples from CaseFoundry** over generic templates.
