---
name: zk-feature
description: >-
    Use this skill when the user mentions "zk-feature". This skill guides developers
    through the complete new feature development workflow for the ZK Framework, including
    creating test files, writing WebDriverTestCase tests, updating release notes, and
    referencing ZK component documentation. Applicable for implementing ZK features,
    working on ZK feature tickets, and verifying new ZK component behaviors.
---

# ZK Feature Development Skill

Step-by-step guide for developing new features in the ZK Framework, from issue intake to release note update.

---

## Step 1: Gather Issue Information

**STOP.** Confirm BOTH of the following before proceeding:

1. **Issue number** — must match format `ZK-[NUMBER]` (e.g. `ZK-6047`).
   - If the input does NOT contain a string matching `ZK-` followed by digits → **ask for it now. Do not proceed.**

2. **Issue title** — the full title from JIRA.
   - If the input does NOT contain an explicit issue title separate from the issue number → **ask for it now. Do not proceed.**

Do NOT begin investigating code or applying fixes until both fields are confirmed.

Then check `zkdoc/release-note` — read the **topmost version number** to determine the current version.

> See `references/zk-workflow-common.md` → **Version Mapping** for version code examples.

---

## Step 1.5: CaseFoundry Research (Auto-invoked)

### MCP availability check

Before calling any CaseFoundry tool, verify the MCP server is connected:
1. Attempt to call `lookup_issue("ZK-[ISSUE_NUMBER]")` as the first probe.
2. **If the call succeeds** → CaseFoundry is available. Continue with the CaseFoundry workflow below.
3. **If the call fails** (connection error, tool not found, or MCP server not configured) → CaseFoundry is unavailable. Use the **Manual Fallback** below instead.

### CaseFoundry workflow (when MCP is available)

**Automatically invoke the `zk-diagnose` skill** to search CaseFoundry for historical context before implementing.

1. Call `lookup_issue("ZK-[ISSUE_NUMBER]")` to check if this issue has prior context, related commits, or helpdesk tickets.
2. **Short-circuit:** If `lookup_issue` returns comprehensive results, skip `search_cases`.
3. If results are partial or empty, call `search_cases(query="<feature description + component name>", domain="ZK")`.
4. Call `find_usage_examples(component="<target component>")` to get real test-based usage patterns.

### Manual Fallback (when MCP is unavailable)

Perform equivalent research using local tools:
1. **Git history search:** `git log --all --oneline --grep="ZK-[ISSUE_NUMBER]"` in both zk and zkcml repos.
2. **Existing test scan:** Search `zktest/src/test/java/` for test files referencing the target component to find usage patterns.
3. **ZK Component Reference:** Read the official ZK documentation for the component(s) involved.
4. **Source reading:** Read the component source directly to understand current behavior and extension points.

> The manual fallback provides less historical breadth than CaseFoundry but ensures the workflow is never blocked.

### Cross-repo check

Search the companion repo for related commits on the same issue:

```bash
# If working in zk core, search zkcml:
git -C /Users/peakerlee/prj/zk-master/zkcml log --all --oneline --grep="ZK-[ISSUE_NUMBER]"

# If working in zkcml, search zk core:
git -C /Users/peakerlee/prj/zk-master/zk log --all --oneline --grep="ZK-[ISSUE_NUMBER]"
```

If companion commits exist, review them to understand the full scope of this feature.

**Use the research results to:**
- Understand how similar features were implemented historically.
- Identify known pitfalls for the target component(s).
- Reference real usage patterns when designing the implementation.
- Know whether the feature requires changes in both repos.

> If CaseFoundry returns no results, proceed normally. The research is advisory, not blocking.

---

## Step 2: Determine File Naming Convention

| Part | Meaning | Example |
|------|---------|---------|
| `F` | Feature | `F` |
| `[VERSION]` | ZK version (e.g., 104 for 10.4, 111 for 11.1) | `104`, `111` |
| `ZK-[ISSUE_NUMBER]` | Issue number extracted from JIRA | `ZK-6047`, `ZK-5123` |

Full naming pattern: `F[VERSION]-ZK-[ISSUE_NUMBER]`

**Example:** For ZK 10.4.0 and issue ZK-6047 → `F104-ZK-6047`

---

## Step 3: Component Reference

Before implementing, consult the ZK Component Reference for the relevant component(s). Each component entry covers:

- A demonstration of the component
- Java API and JavaScript API links
- Employment and purpose of the component
- Example with source code
- Supported events and children
- Component use cases
- Version history

> For introductory concepts → ZK Developer's Reference: UI > Composing
> For general patterns (drag-and-drop, hflex/vflex, tooltips, context menus) → ZK Developer's Reference: UI > Patterns

### CaseFoundry Usage Examples (Auto-invoked)

Additionally, call CaseFoundry to get real test-based usage patterns:

```
find_usage_examples(component="<component name>", zk_version="<current version>", method="<method or event if applicable>")
```

This returns actual test code snippets showing how the component is used in the ZK test suite — more reliable than documentation alone.

---

## Step 4: Create the `.zul` Test File

**Path:** `zktest/src/main/webapp/test2/F[version]-ZK-[issue].zul`

Fetch the file header snippet from:
```
zk/.vscode/potix.code-snippets  →  zul snippet
```

### File Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
F[VERSION]-ZK-[ISSUE_NUMBER].zul

        Purpose:

        Description:

        History:
                [Date], Created by [author]

Copyright (C) [year] Potix Corporation. All Rights Reserved.
-->
<zk>
    <label multiline="true">
        [Describe how to use the new feature and verify its behavior here]

        Steps to verify:
        1. ...
        2. ...

        Expected result:
        - ...
    </label>

    <!-- Place the new feature component(s) below -->
    <div viewModel="@id('vm') @init('org.zkoss.zktest.test2.F[VERSION]_ZK_[ISSUE_NUMBER]VM')">
        ...
    </div>
</zk>
```

**Key rules:**
- `<label multiline="true">` must describe how to use and verify the new feature
- Place the feature component **below** the label

> See `references/zk-workflow-common.md` → **Special Case: `zul.xsd` / `zk.xsd` Issues** if applicable.

---

## Step 5: Create the Java Test File

Skip this step for `zul.xsd` / `zk.xsd` manual-only issues.

**Path:** `zktest/src/test/java/org/zkoss/zktest/zats/test2/F[version]_ZK_[issue]Test.java`

Fetch the file header snippet from:
```
zk/.vscode/potix.code-snippets  →  java snippet
```

### File Structure

```java
/* F[VERSION]_ZK_[ISSUE_NUMBER]Test.java

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

public class F[VERSION]_ZK_[ISSUE_NUMBER]Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        // interact with page using jq(), click(), waitResponse()
        // assert expected state using assertTrue / assertFalse
    }
}
```

> See `references/zk-workflow-common.md` → **Java Test File Key Rules** for test rules, validation, and run instructions (use prefix `F`).

### Test Quality Gate (Auto-invoked)

**Automatically invoke the `zk-test-guide` skill** to validate the test before running:
- Verify `connect()` is called before any `getDriver()` usage.
- Verify `waitResponse()` follows every UI-triggering action.
- Verify `jq()` results are null-checked before property access.
- Verify assertions match the exact scenario in the JIRA issue.
- Call `find_usage_examples(component="<component>")` to reference real test patterns.

---

## Step 6: Implement the Feature

> See `references/zk-workflow-common.md` → **Side Effect Check** for the full checklist and remediation steps.

> See `references/zk-workflow-common.md` → **Code Comment Style** for comment conventions.

### Automatic Audit (Auto-invoked)

After writing the implementation, run CaseFoundry `review_code` on the changed production files **if MCP is available**:

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

**Skip audits that don't match.** For example, a simple API addition to a ViewModel does not need perf-check or security-review.

**If any audit finds Critical or Major issues, resolve them before proceeding to Step 7.**

---

## Step 7: Update Release Notes

**File:** `zkdoc/release-note`

Find the **Features** section (may be labeled "Improvements" in some versions) of the current version and append at the end:

```
    ZK-[ISSUE_NUMBER]: [Issue title from JIRA]
```

Format: 2-space indent + issue number + colon + title (copy exactly from issue title)

---

## Step 8: Update config.properties

**File:** `zktest/src/main/webapp/test2/config.properties`

Find the section matching the current ZK version, e.g. `## F[VERSION]`. If not present, create it.

Append a new line at the end of the section:

```
##zats##F[VERSION]-ZK-[ISSUE_NUMBER].zul=[IMPORTANCE],[DIFFICULTY],[RELATED_COMPONENTS]
```

For `zul.xsd` / `zk.xsd` manual-only issues, use:

```
##manually##F[VERSION]-ZK-[ISSUE_NUMBER].zul=[IMPORTANCE],[DIFFICULTY],[RELATED_COMPONENTS]
```

> See `references/zk-workflow-common.md` → **config.properties Format Reference** for field definitions.

---

## Step 9: Additional Reminders

### 3rd Party Licenses
If this feature adds, updates, or removes any **dependency, plugin, or library**, please remind the developer after completion:

Please update the "3rd Party Licenses" document:
https://docs.google.com/spreadsheets/d/1q308FDE1q3HIqWUuwH7InAAFme6ou34_2xYY0vAz5WA/edit?usp=sharing

### zul.xsd Update
If this feature involves changes related to `zk.xsd`, please remind the developer after the feature is merged:

After this feature is merged, please follow the Steps to update zul.xsd:
http://potix.gitpage.potix.com:3001/zk-devnote/ZK/update-zul.xsd-sop.html

---

## Checklist

- [ ] Issue number and title confirmed
- [ ] Current ZK version identified from `zkdoc/release-note`
- [ ] CaseFoundry research completed (Step 1.5: `lookup_issue` + `search_cases` + `find_usage_examples`)
- [ ] Relevant component(s) referenced in ZK Component Reference + CaseFoundry usage examples
- [ ] `.zul` file created with correct naming (`F` prefix) and structure
- [ ] Java test file created, and all own related tests are completed and passing; or manual-only `zul.xsd` / `zk.xsd` flow used
- [ ] Test validated by `zk-test-guide` (connect() order, waitResponse(), null-checks, scenario match)
- [ ] Test.java does **not** override `isHeadless()` (forbidden), if `Test.java` exists
- [ ] Feature implementation is backward compatible with no side effects
- [ ] `zk-perf-check` passed (if performance-sensitive components are involved)
- [ ] `zk-security-review` passed (if security-sensitive code paths are involved)
- [ ] `review_code` passed on changed production files
- [ ] Code comments explain the *why*
- [ ] `zkdoc/release-note` updated (Features/Improvements section)
- [ ] `config.properties` updated with `##zats##` or `##manually##` as appropriate
- [ ] (If applicable) 3rd Party Licenses document updated
- [ ] (If applicable) `zul.xsd` update reminder sent after merge
- [ ] Completion summary shown to developer
