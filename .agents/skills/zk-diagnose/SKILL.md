---
name: zk-diagnose
description: >-
  Use this skill when the user mentions "zk-diagnose", provides a stack trace,
  asks "why does this happen", or wants to investigate a ZK bug before fixing it.
  This skill leverages CaseFoundry's 18,000+ historical cases to diagnose bugs,
  find root causes, and surface historical precedents before any code changes.
  Automatically invoked by zk-bug (Step 1.5) and zk-feature (Step 1.5).
---

# ZK Bug Diagnosis Skill

Pre-fix investigation workflow: leverage CaseFoundry historical cases to diagnose the root cause before writing any fix.

---

## MCP Availability Check

This skill relies heavily on CaseFoundry MCP tools. Before proceeding, verify the MCP server is connected:

1. Attempt to call `lookup_issue("ZK-0000")` (or the actual issue key) as the first probe.
2. **If the call succeeds** → CaseFoundry is available. Proceed with the full CaseFoundry-powered workflow below.
3. **If the call fails** (connection error, tool not found, or MCP server not configured) → CaseFoundry is unavailable.

### Manual Fallback (when MCP is unavailable)

When CaseFoundry is not connected, this skill can still provide value through local investigation:

1. **Git history search:** `git log --all --oneline --grep="ZK-[ISSUE_NUMBER]"` in both zk and zkcml repos to find prior commits on the same issue.
2. **Git blame:** `git blame -L <range> <file>` on the suspected buggy code to understand recent changes.
3. **Stack trace analysis:** Manually parse class/method names from the trace to locate relevant source.
4. **Pattern catalog lookup:** Consult `references/zk-pattern-catalog.md` to manually match symptoms against known patterns.
5. **Source code reading:** Read the affected component to understand current behavior and infer root cause.

Produce the same diagnosis report format (Step 4) but note that CaseFoundry was not consulted and confidence levels are based on local analysis only.

> If running in manual fallback mode, skip Steps 2A–2D below and go directly to Step 3 (analysis) using locally gathered information.

---

## When to use

- A developer has a ZK-XXXX issue and wants to understand what went wrong before coding a fix.
- A stack trace (Java, JavaScript, or ZUL) is available and needs diagnosis.
- The `zk-bug` or `zk-feature` skill chain invokes this skill after gathering issue info.
- A developer asks "why does this happen" or "has this been seen before".

---

## Step 1: Gather Input

Determine what information is available:

| Available Input | Action |
|----------------|--------|
| ZK-XXXX issue key | Proceed to Step 2A |
| Stack trace (Java / JS / ZUL error) | Proceed to Step 2B |
| Bug description only (no issue key, no trace) | Proceed to Step 2C |
| Code snippet suspected of causing the issue | Proceed to Step 2D (additionally) |

---

## Step 2A: Lookup by Issue Key

Call CaseFoundry `lookup_issue` with the ZK-XXXX key:

```
lookup_issue("ZK-XXXX")
```

This returns:
- The JIRA issue details (description, priority, affected versions)
- Related GitHub commits (the actual fix code)
- Related helpdesk tickets (customer-reported symptoms)
- Related test cases (regression tests written for this issue)

**If results are comprehensive** (JIRA issue + related commits or helpdesk tickets returned): Summarize the historical context and **skip directly to Step 3** — no need to run `diagnose_bug` redundantly.
**If results are partial** (only JIRA issue, no related cases): Also run Step 2C to broaden the search.
**If no results:** Fall through to Step 2C with the issue title as query.

---

## Step 2B: Diagnose from Stack Trace

Call CaseFoundry `diagnose_from_stacktrace`:

```
diagnose_from_stacktrace(stacktrace="<paste full trace>", zk_version="<if known>")
```

Key features of this tool:
- Strips line numbers entirely — the same bug at line 247 in ZK 9.6 and line 309 in ZK 10.0 will match.
- Handles all ZK error formats: Java exceptions, JavaScript browser errors, ZUL template errors, mixed traces.

**If results are returned:** Analyze the matched cases and proceed to Step 3.
**If no results:** Extract class/method names from the trace, then use Step 2C with those as query.

---

## Step 2C: Semantic Search by Description

Call CaseFoundry `diagnose_bug`:

```
diagnose_bug(query="<symptom description>", domain="ZK", component="<if known>", top_k=8)
```

Tips for effective queries:
- Include the **symptom** (what the user sees), not just the suspected cause.
- Include the **component name** if known (Grid, Listbox, Tree, Combobox, etc.).
- Include the **ZK version** if known via the `zk_version` parameter.

---

## Step 2D: Review Code for Known Pitfalls

If a code snippet is available (the suspected buggy code), also call:

```
review_code(code="<code content>", filename="<filename if known>")
```

This proactively flags components with known historical issues, returning:
- Severity and root cause from historical cases
- Recommended fix patterns

---

## Step 3: Analyze and Cross-Reference

After gathering CaseFoundry results, perform this analysis:

### 3.1 Pattern Identification

Map the returned cases to CaseFoundry's known pattern categories:

| Pattern | Key Indicators |
|---------|---------------|
| `NPE_NULL_CHECK` | NullPointerException, missing null guard, detached component access |
| `UI_FREEZE` | Browser unresponsive, rendering timeout, >2.5s mount |
| `COMPONENT_LIFECYCLE` | Detach/unbind ordering, rerender during render, destroy callback missing |
| `DATA_BINDING_MISMATCH` | @init vs @load confusion, NotifyChange not triggering, type mismatch |
| `EVENT_LISTENER_GHOST` | Memory leak, listener not removed, listen/unlisten asymmetry |
| `RESOURCE_CLEANUP` | Unclosed stream/connection, executor not shut down, missing destroy() |
| `CONFIG_MISMATCH` | Wrong property name, typo in config, version string inconsistency |
| `RACE_CONDITION` | Intermittent failure, concurrent modification, ordering dependency |

### 3.2 Confidence Assessment

Rate each hypothesis based on:
- **High confidence** — multiple cases match with similarity > 0.75 and consistent root cause
- **Medium confidence** — 1-2 cases match with similarity 0.60-0.75
- **Low confidence** — weak matches only, or conflicting root causes across cases

> See `references/zk-pattern-catalog.md` for the full pattern catalog with representative case IDs.

---

## Step 4: Produce Diagnosis Report

Output the following structure:

```md
## Diagnosis: ZK-[ISSUE_NUMBER]

### Most Likely Root Cause
- **Pattern:** [pattern name]
- **Cause:** [one-line technical cause]
- **Confidence:** [High/Medium/Low]
- **Supporting cases:** [case IDs, e.g. GH-c3afa3c1, JR-ZK-2837]

### Historical Precedents
| Case | Pattern | Fix Applied | Similarity |
|------|---------|-------------|-----------|
| [case_ref] | [pattern] | [one-line fix] | [sim score] |
| ... | ... | ... | ... |

### Alternative Hypotheses
- [Alternative cause 1] (confidence: [level], ref: [case_id])
- [Alternative cause 2] (confidence: [level], ref: [case_id])

### Recommended Investigation Steps
1. [First thing to check]
2. [Second thing to check]
3. [Third thing to check]

### Code Review Warnings
[Output from review_code, if Step 2D was executed]
```

---

## Step 5: Chain to Next Skill (if applicable)

If this skill was invoked as part of `zk-bug` or `zk-feature`:
- Return the diagnosis report to the calling skill.
- The calling skill will use this information to guide the fix/implementation.

If invoked standalone:
- Ask the developer if they want to proceed with the `zk-bug` workflow to fix this issue.

---

## Guardrails

- **Do not claim certainty without evidence.** If CaseFoundry returns low-confidence matches, say so explicitly.
- **Do not fabricate case IDs.** Only reference case IDs that were actually returned by CaseFoundry tools.
- **Always show similarity scores.** Let the developer judge relevance.
- **Prefer root-cause explanation over fix prescription.** The diagnosis skill explains *why*; the bug/feature skill handles *how to fix*.
- **If CaseFoundry is unavailable:** State this in the report header. Use local tools (git log, git blame, source reading) and consult `references/zk-pattern-catalog.md` for pattern matching. Mark all confidence levels as "Local analysis only".
- **If no cases match:** State this clearly and suggest manual investigation approaches (git blame, component source reading).
