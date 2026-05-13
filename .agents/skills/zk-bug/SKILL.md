---
name: zk-bug
description: >-
  Use this skill when the user mentions "zk-bug". This skill guides developers
  through the complete bug-fix workflow for the ZK Framework, including creating
  test files, writing WebDriverTestCase tests, updating release notes, and ensuring
  backward compatibility. Applicable for fixing ZK bugs, working on ZK issue tickets,
  and reproducing/verifying ZK issues.
---

# ZK Bug Fix Skill

Step-by-step guide for fixing bugs in the ZK Framework, from issue intake to release note update.

---

## Step 1: Gather Issue Information

**STOP.** Confirm BOTH of the following before proceeding:

1. **Issue number** — must follow the exact format `ZK-[NUMBER]` (e.g. `ZK-1234`).
   - If the input does NOT contain a string matching `ZK-` followed by digits → **ask for it now. Do not proceed.**

2. **Issue title** — the full title copied from JIRA.
   - If the input does NOT contain an explicit issue title separate from the issue number → **ask for it now. Do not proceed.**

Do NOT begin investigating code or applying fixes until both fields are confirmed.

Then check `zkdoc/release-note` — read the **topmost version number** to determine the current version.

> See `references/zk-workflow-common.md` → **Version Mapping** for version code examples.

---

## Step 1.5: CaseFoundry Diagnosis (Auto-invoked)

### MCP availability check

Before calling any CaseFoundry tool, verify the MCP server is connected:
1. Attempt to call `lookup_issue("ZK-[ISSUE_NUMBER]")` as the first probe.
2. **If the call succeeds** → CaseFoundry is available. Continue with the CaseFoundry workflow below.
3. **If the call fails** (connection error, tool not found, or MCP server not configured) → CaseFoundry is unavailable. Use the **Manual Fallback** below instead.

### CaseFoundry workflow (when MCP is available)

**Automatically invoke the `zk-diagnose` skill** to search CaseFoundry for historical precedents before writing any fix.

1. Call `lookup_issue("ZK-[ISSUE_NUMBER]")` to pull JIRA issue details, related commits, helpdesk tickets, and test cases.
2. **Short-circuit:** If `lookup_issue` returns comprehensive results (JIRA + related commits or helpdesk tickets), skip `diagnose_bug` — the context is already sufficient.
3. If results are partial or empty, call `diagnose_bug(query="<issue title + description>", domain="ZK", component="<if known>")`.
4. If a stack trace is available, also call `diagnose_from_stacktrace(stacktrace="<trace>")`.

### Manual Fallback (when MCP is unavailable)

Perform equivalent investigation using local tools:
1. **Git history search:** `git log --all --oneline --grep="ZK-[ISSUE_NUMBER]"` in both zk and zkcml repos to find prior commits.
2. **Code archaeology:** `git log -S "<keyword>" --oneline -- <suspected file>` to trace relevant changes.
3. **Source reading:** Read the affected component source to understand current behavior.
4. **Pattern matching:** Consult `references/zk-workflow-common.md` → **Side Effect Check** to manually check for known antipatterns.

> The manual fallback provides less historical breadth than CaseFoundry but ensures the workflow is never blocked.

### Cross-repo check

Search the companion repo for related commits on the same issue:

```bash
# If working in zk core, search zkcml:
git -C /Users/peakerlee/prj/zk-master/zkcml log --all --oneline --grep="ZK-[ISSUE_NUMBER]"

# If working in zkcml, search zk core:
git -C /Users/peakerlee/prj/zk-master/zk log --all --oneline --grep="ZK-[ISSUE_NUMBER]"
```

If companion commits exist, review them as part of the same logical change.

**Use the diagnosis results to:**
- Understand historical root causes for similar bugs.
- Identify known antipatterns in the affected component.
- Avoid reimplementing a fix that was already tried and reverted.
- Know whether the fix requires changes in both repos.

> If CaseFoundry returns no results, proceed normally. The diagnosis is advisory, not blocking.

---

## Step 2: Determine File Naming Convention

| Part | Meaning | Example |
|------|---------|---------|
| `B` | Bug | `B` |
| `[VERSION]` | ZK version (e.g., 104 for 10.4, 111 for 11.1) | `104`, `111` |
| `ZK-[ISSUE_NUMBER]` | Issue number extracted from JIRA | `ZK-1234`, `ZK-4321` |

Full naming pattern: `B[VERSION]-ZK-[ISSUE_NUMBER]`

**Example:** For ZK 10.4.0 and issue ZK-1234 → `B104-ZK-1234`

---

## Step 3: Create the `.zul` Test File

**Path:** `zktest/src/main/webapp/test2/B[version]-ZK-[issue].zul`

Fetch the file header snippet from:
```
zk/.vscode/potix.code-snippets  →  zul snippet
```

### File Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
B[VERSION]-ZK-[ISSUE_NUMBER].zul

        Purpose:

        Description:

        History:
                [Date], Created by [author]

Copyright (C) [year] Potix Corporation. All Rights Reserved.
-->
<zk>
    <label multiline="true">
        [Describe how to reproduce the bug and verify the fix here]

        Steps to reproduce:
        1. ...
        2. ...

        To verify the fix:
        - Expected: ...
    </label>

    <!-- Place the fixed component(s) below -->
    <div viewModel="@id('vm') @init('org.zkoss.zktest.test2.B[VERSION]_ZK_[ISSUE_NUMBER]VM')">
        ...
    </div>
</zk>
```

**Key rules:**
- `<label multiline="true">` must describe how to reproduce the bug and verify the fix
- Place the repro/fixed component **below** the label

> See `references/zk-workflow-common.md` → **Special Case: `zul.xsd` / `zk.xsd` Issues** if applicable.

---

## Step 4: Create the Java Test File

Skip this step for `zul.xsd` / `zk.xsd` manual-only issues.

**Path:** `zktest/src/test/java/org/zkoss/zktest/zats/test2/B[version]_ZK_[issue]Test.java`

Fetch the file header snippet from:
```
zk/.vscode/potix.code-snippets  →  java snippet
```

### File Structure

```java
/* B[VERSION]_ZK_[ISSUE_NUMBER]Test.java

        Purpose:

        Description:

        History:
                [Date], Created by [author]

Copyright (C) [year] Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B[VERSION]_ZK_[ISSUE_NUMBER]Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        // interact with page using jq(), click(), waitResponse()
        // assert expected state using assertTrue / assertFalse
    }
}
```

> See `references/zk-workflow-common.md` → **Java Test File Key Rules** for test rules, validation, and run instructions (use prefix `B`).

### Test Quality Gate (Auto-invoked)

**Automatically invoke the `zk-test-guide` skill** to validate the test before running:
- Verify `connect()` is called before any `getDriver()` usage.
- Verify `waitResponse()` follows every UI-triggering action.
- Verify `jq()` results are null-checked before property access.
- Verify assertions match the exact scenario in the JIRA issue.
- Call `find_usage_examples(component="<component>")` to reference real test patterns.

---

## Step 5: Fix the Component Logic

> See `references/zk-workflow-common.md` → **Side Effect Check** for the full checklist and remediation steps.

> See `references/zk-workflow-common.md` → **Code Comment Style** for comment conventions.

### Automatic Audit (Auto-invoked)

After writing the fix, run CaseFoundry `review_code` on the changed production files **if MCP is available**:

```
review_code(code="<changed file content>", filename="<filename>")
```

**If MCP is unavailable:** Manually review the changed code against `references/zk-workflow-common.md` → **Side Effect Check** and the pattern-specific checks in the `zk-commit` skill's `references/review-criteria.md` (sections 4.1–4.7).

Then, **conditionally** invoke specialized audits only when the trigger criteria match:

1. **`zk-perf-check`** — invoke ONLY when the changed code matches at least one of:
    - Modifies widget JavaScript files (`*.js` under `zk/src/main/resources/web/js/` or equivalent)
    - Changes rendering/mount/unmount logic (`rerender`, `redraw`, `bind_`, `unbind_`, `onSize`, `breathe`)
    - Touches large-data component classes (`Grid`, `Listbox`, `Tree`, `ForEach`, `Paging`)
    - Modifies layout components (`Hlayout`, `Vlayout`, `Borderlayout`, `LayoutRegion`)
    - Adds or modifies scroll/resize event handlers

2. **`zk-security-review`** — invoke ONLY when the changed code matches at least one of:
    - Writes to HTTP response (`setHeader`, `addHeader`, `getWriter`, `getOutputStream`)
    - Generates JavaScript dynamically (string concatenation into `<script>` or JS contexts)
    - Renders user-provided content into HTML/ZUL (`setContent`, `innerHTML`, `Encode.*`)
    - Handles file upload (`Fileupload`, `AuUploader`, `multipart`)
    - Processes URL parameters for rendering (`request.getParameter` used in output)
    - Touches servlet classes (`DHtmlResourceServlet`, `DHtmlUpdateServlet`, `ClassWebResource`)

**Skip audits that don't match.** For example, a null-check fix in a constraint class (like `SimpleNumberInputConstraint`) does not need perf-check or security-review.

**If any audit finds Critical or Major issues, resolve them before proceeding to Step 6.**

---

## Step 6: Update Release Notes

**File:** `zkdoc/release-note`

Find the **Bugs** section of the current fix version and append at the end:

```
    ZK-[ISSUE_NUMBER]: [Issue title from JIRA]
```

Format: 2-space indent + issue number + colon + title (copy exactly from issue title)

---

## Step 7: Update config.properties

**File:** `zktest/src/main/webapp/test2/config.properties`

Find the section matching the current ZK version, e.g. `## B[VERSION]`. If not present, create it.

Append a new line at the end of the section:

```
##zats##B[VERSION]-ZK-[ISSUE_NUMBER].zul=[IMPORTANCE],[DIFFICULTY],[RELATED_COMPONENTS]
```

For `zul.xsd` / `zk.xsd` manual-only issues, use:

```
##manually##B[VERSION]-ZK-[ISSUE_NUMBER].zul=[IMPORTANCE],[DIFFICULTY],[RELATED_COMPONENTS]
```

> See `references/zk-workflow-common.md` → **config.properties Format Reference** for field definitions.

---

## Step 8: Generate Root Cause Summary

After all previous steps are complete and tests pass, use the `rootcause` skill to produce a concise post-fix summary (root cause, solution, and optional follow-ups).

After the root cause summary is generated, **feed it back to CaseFoundry** (if MCP is available):

```
record_feedback(tool_name="diagnose_bug", score=1, comment="ZK-[ISSUE_NUMBER]: <one-line root cause>")
```

This closes the knowledge loop — future developers diagnosing similar issues will benefit from this fix.

**If MCP is unavailable:** Skip the feedback step. The root cause summary in the commit message and rootcause output still serves as documentation.

---

## Checklist

- [ ] Issue number and title confirmed
- [ ] Current ZK version identified from `zkdoc/release-note`
- [ ] CaseFoundry diagnosis completed (Step 1.5: `lookup_issue` + `diagnose_bug`)
- [ ] `.zul` file created with correct naming and structure
- [ ] Java test file created, and all own related tests are completed and passing; or manual-only `zul.xsd` / `zk.xsd` flow used
- [ ] Test validated by `zk-test-guide` (connect() order, waitResponse(), null-checks, scenario match)
- [ ] Test.java does **not** override `isHeadless()` (forbidden), if `Test.java` exists
- [ ] Component fix is backward compatible with no side effects
- [ ] `zk-perf-check` passed (if performance-sensitive components are involved)
- [ ] `zk-security-review` passed (if security-sensitive code paths are involved)
- [ ] `review_code` passed on changed production files
- [ ] Code comments explain the *why*
- [ ] `zkdoc/release-note` updated
- [ ] `config.properties` updated with `##zats##` or `##manually##` as appropriate
- [ ] Root cause summary generated via `rootcause` skill
- [ ] Root cause fed back to CaseFoundry via `record_feedback`
