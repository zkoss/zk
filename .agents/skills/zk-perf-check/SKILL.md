---
name: zk-perf-check
description: >-
  Use this skill when the user mentions "zk-perf-check", "performance", "slow",
  "freeze", "UI freeze", "lag", or reports rendering performance issues.
  This skill diagnoses ZK performance problems using a structured antipattern
  checklist. Automatically invoked by zk-bug
  and zk-feature during the fix/implement step when performance-related components
  are involved.
---

# ZK Performance Check Skill

Diagnose and prevent ZK UI performance issues using a structured antipattern checklist.

---

## When to use

- A developer reports UI freezing, slow rendering, or browser unresponsiveness.
- The `zk-bug` or `zk-feature` skill detects performance-sensitive components (Grid, Listbox, Tree, ForEach, Hlayout/Vlayout with many children).
- A code review involves rendering logic, mount/unmount cycles, or large dataset handling.
- A developer asks "why is this slow" or "how to optimize rendering".

---

## Step 1: Gather Performance Context

Collect the following (ask if not provided):

| Field | Required? | Example |
|-------|-----------|---------|
| Symptom description | Yes | "Grid freezes when scrolling 1000 rows" |
| Component(s) involved | Yes | Grid, Listbox, Tree, ForEach, etc. |
| Dataset size | Yes | Row count, column count, nesting depth |
| ZK version | Preferred | 10.4.0, 11.1.0 |
| Client MVVM or Server MVVM? | Preferred | Client / Server |
| Rendering mode (ROD enabled?) | Preferred | Yes / No / Unknown |
| Browser | If relevant | Chrome, Firefox, IE11 |

---

## Step 2: Search Historical Cases

> **MCP availability check:** Attempt the `diagnose_bug` call below. If it fails (connection error, tool not found), CaseFoundry is unavailable — skip this step entirely and proceed directly to Step 3 (Antipattern Audit). The antipattern checklist is self-contained and does not require MCP.

### When MCP is available

Call CaseFoundry to find matching precedents:

```
diagnose_bug(
  query="<symptom description>",
  domain="ZK",
  component="<component name>",
  source=null,
  severity=null,
  top_k=8
)
```

If specific code is available:

```
review_code(code="<code content>", filename="<filename>")
```

### When MCP is unavailable

Skip CaseFoundry lookup. Proceed directly to **Step 3: Antipattern Audit** — the checklist covers all known performance patterns and does not depend on MCP. In the final report, leave the "CaseFoundry Matches" table empty and note: "CaseFoundry was not consulted (MCP unavailable)."

---

## Step 3: Antipattern Audit

Systematically check each item against the code and configuration. Mark each as Pass / Fail / N/A.

### 3.1 Render-on-Demand (ROD)

- [ ] **Grid/Listbox/Tree with >50 items has ROD enabled**
  - Check: `<custom-attributes org.zkoss.zul.listbox.rod="true"/>` or library property `org.zkoss.zul.listbox.rod`
  - Without ROD, large datasets cause page timeouts due to full DOM rendering

- [ ] **Tree paging mode cleans up rendered nodes between pages**
  - Tree retains rendered nodes until detach, causing linear performance degradation across page navigations

### 3.2 Rendering Loop Efficiency

- [ ] **`breathe()` called BEFORE expensive DOM creation in mount loops**
  - UI freezes when breathe() is placed after expensive operations
  - Fix: Move breathe() check before widget instantiation logic

- [ ] **No synchronous for-loops blocking browser thread in widget init**
  - Synchronous loops in `_loadAndInit` / `_evalInit` block the browser thread
  - Fix: Replace for-loop with while-loop, remove setTimeout fallback

- [ ] **`rerender()` not called synchronously before DOM ready**
  - Fix: Defer rerender() to next tick using timeout(0)

- [ ] **No re-rendering during rendering phase**
  - Fix: Check `_rdque.includes(this)` before re-rendering

### 3.3 Data Iteration Efficiency

- [ ] **`getChildCount()` / `getItemCount()` cached in local variable before loops**
  - Repeated model queries in loop conditions cause unnecessary overhead

- [ ] **ForEach uses incremental update, not full re-render**
  - ForEach must reuse previously rendered components rather than recreating them

- [ ] **Model replacement followed by `invalidate()` for bulk updates**
  - Tree model replacement without invalidate() causes individual DOM operations instead of a single batch

### 3.4 Event Throttling

- [ ] **Scroll/resize event handlers are throttled or debounced**
  - Unthrottled scroll events cause layout thrashing, especially in older browsers

- [ ] **No high-frequency style updates in scroll handlers**
  - Fix: Decouple event frequency from DOM mutation rate

### 3.5 Layout and Flex

- [ ] **No `hflex="1"` on children inside Listcell (causes per-row sizing recalc)**
  - ROD listbox with many columns triggers excessive `isRealVisible`/`fireSized` calls

- [ ] **Hlayout/Vlayout does not contain >20 dynamic children**
  - Excessive DOM manipulation occurs with many children in RowRenderer

### 3.6 Client MVVM vs Server MVVM

- [ ] **Large datasets (>100 rows or >50 columns) use Server MVVM, not Client MVVM**
  - Client MVVM creates excessive per-cell bindings with many columns; Server MVVM defers rendering to the server, reducing client overhead

- [ ] **Deeply nested forEach loops in Client MVVM are avoided**
  - Fix: Move complex iteration logic to server-side ViewModel

### 3.7 MVVM Tree Performance

- [ ] **MVVM Tree with paging uses scoped rendering (not eager full-node render)**
  - MVVM Tree may eagerly render all sub-nodes ignoring pagination, causing severe slowdown

---

## Step 4: Produce Performance Report

Output the following structure:

```md
## Performance Audit: [Component / Issue]

### Environment
- Component(s): [names]
- Dataset size: [rows x columns]
- ZK Version: [version]
- MVVM mode: [Client / Server]

### CaseFoundry Matches
| Case | Pattern | Root Cause | Similarity |
|------|---------|-----------|-----------|
| [ref] | [pattern] | [cause] | [score] |

### Antipattern Findings
| # | Check Item | Status | Severity | Recommendation |
|---|-----------|--------|----------|---------------|
| 1 | [item] | Fail | Critical | [fix] |
| 2 | [item] | Fail | Major | [fix] |
| 3 | [item] | Pass | - | - |

### Recommended Actions (priority order)
1. [Highest impact fix]
2. [Second fix]
3. [Third fix]

### Estimated Impact
- [Which antipattern fix addresses the most cases historically]
```

---

## Step 5: Chain Back (if invoked from zk-bug / zk-feature)

Return the performance report to the calling skill. The fix/implementation step should incorporate the recommended actions.

---

## Guardrails

- **Always verify ROD status before suggesting ROD-related fixes.** Do not assume ROD is disabled.
- **Include clear explanations** for every antipattern finding so the developer understands the root cause.
- **Severity levels:** Critical = measurable freeze/timeout, Major = noticeable slowness, Minor = suboptimal but functional.
- **Do not suggest architectural rewrites** (e.g., "switch from Client MVVM to Server MVVM") unless the dataset size clearly warrants it. Provide the threshold data from historical cases.
- **If no antipatterns are found:** State this explicitly and suggest profiling approaches (Chrome DevTools, ZK AU profiling).
