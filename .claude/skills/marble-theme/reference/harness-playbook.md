# Orchestrator Playbook

How the main Claude session (any model — Opus, Sonnet, Haiku) drives the ZK-Material verification harness. This is the only file you need open to run the loop.

Companion docs: `.claude/skills/marble-theme/reference/harness-decisions.md` (architecture decision records), `tasks/harness/work-status.md` (state, local only; the canonical status vocabulary is `.claude/skills/marble-theme/reference/harness-status.md`), `doc/spec/new-component-checklist.md` (cross-cutting Definition of Done — CTV, brand, forced-colors, density, tablet; Step 4c).

---

## Contract selector convention

Preview pages render many instances of each component in different states, laid out as one or more **state matrices** produced by `src/test/resources/web/pv/matrix.zul` (pure `z-*` utilities — the old `pv-*` classes were dissolved with `pv.css` and no longer exist in any preview page). Rendered structure per matrix:

```
div.z-d-grid.z-grid-cols-auto        ← matrix container (one per section)
├─ div.z-grid-col-full               ← section title ("States", "Multiline", …)
├─ div.z-d-contents                  ← header row: empty cell + one header cell per state column
└─ div.z-d-contents                  ← data row: label cell + one component cell per state
   …                                    (more data rows)
```

Contracts MUST anchor on **matrix title + header/label text** — robust to reordering — not bare class selectors or positional `nth-child` chains:

```js
const matrix = [...document.querySelectorAll('.z-d-grid.z-grid-cols-auto')]
  .find(m => m.querySelector('.z-grid-col-full')?.textContent.trim() === 'States');
const rows = [...matrix.querySelectorAll(':scope > .z-d-contents')];
const col  = [...rows[0].children].findIndex(c => c.textContent.trim() === 'Disabled');
const el   = rows.find(r => r.children[0].textContent.trim() === 'Text')
               .children[col].querySelector('input.z-textbox');
```

- Explicit attribute filters (e.g. `[readonly]`, `[disabled]`) remain fine when the variant isn't in a state matrix.
- The Evaluator must NEVER use bare `input.z-textbox` because the page has 20+ matches. The default-state row was originally measured against a sibling that interfered with focus measurement — anchoring fixes this.

See `doc/contracts/textbox.md` Preview Anchors section as the reference example.

---

## Pre-flight

Before the first dispatch in any session:

1. Verify preview app is reachable:
   ```bash
   curl -sI ${PREVIEW_URL}/textbox.zul | head -1
   ```
   Expect `HTTP/1.1 200`. If not, ask the user to start it:
   ```bash
   withjdk.sh 17 mvn test exec:java@preview-app
   ```

2. Verify the three agents are registered in the running session. The Agent tool's available-agent list (shown at session start) must include `zk-theme-evaluator`, `zk-theme-generator`, and `md3-design-verifier`. If not, the user must restart Claude Code — agents in `.claude/agents/` are loaded only at session boot.

3. Read `tasks/harness/work-status.md` to load current state.

---

## Main loop

**`VERIFIED` is dual-gate**: Gate 1 (`zk-theme-evaluator` — conformance to the contract) AND Gate 2 (`md3-design-verifier` — MD3/MUI design quality) must both pass. The evaluator never writes `VERIFIED`; its all-measurements-pass result is `GATE2_PENDING`, and only the orchestrator flips `GATE2_PENDING → VERIFIED` after a `GATE2: PASS`.

**Manual-verification exception (audit-trail rule)**: the orchestrator may exceptionally perform Gate-1 measurement itself (e.g. the Evaluator agent is unavailable), but the artefact obligations do NOT lapse — it must still write `tasks/harness/eval-reports/<component>.md` and capture flat `doc/screenshots/<component>-*.png` before the row may leave a Gate-1 state. A row with no eval report and no screenshots cannot reach `GATE2_PENDING` (Gate 2 would be `BLOCKED` anyway — screenshots are its only visual input) and can never be flipped to `VERIFIED`.

Repeat until every row is `VERIFIED`, `STALLED`, `OSCILLATING`, `CONSTRAINT`, `ESCALATED_TOKEN_FIX`, or `ESCALATED_LIBRARY_CONFIG` (the canonical status vocabulary lives in `.claude/skills/marble-theme/reference/harness-status.md`):

### Step 1 — Pick the next work batch

Compute two batches from `tasks/harness/work-status.md`:

**Evaluator batch (parallel, up to 4):**
- All rows in `RE_EVAL_NEEDED` status (highest priority — these block siblings)
- Then rows in `PENDING` or `NEEDS_FIX` that don't have an open eval report
- Order: highest-impact component groups first (blockers before dependents)
- Cap at 4 per batch (Chrome handles 4 tabs comfortably; more risks slowdown)

**Generator batch (serial, 1 at a time):**
- All rows in `NEEDS_FIX` status WITH an Action-required section that is not flagged `TOKEN_FIX_REQUIRED`
- Pick exactly 1 per outer loop iteration
- Build serialization: `npm run build:css` writes to `target/classes/web/marble/` non-atomically; concurrent builds race. Even though different Generators edit different CSS source files, their builds would clobber each other.

**Design-review batch (parallel, read-only):**
- All rows in `GATE2_PENDING` status → dispatch `md3-design-verifier` per component (Step 4b)
- These are read-only (CSS + pre-captured screenshots, no Chrome) and can run in the same message as the Evaluator batch — but NOT for a component whose `shared-css-file` matches a currently-running Generator's target

### Step 2 — Dispatch the Evaluator batch (parallel)

Issue **one message with multiple Agent tool calls** (this is what makes them parallel):

```
Agent(
  description="Evaluate textbox",
  subagent_type="zk-theme-evaluator",
  prompt="Component: textbox\n\nFollow your agent definition. Use a fresh Chrome tab."
)
Agent(
  description="Evaluate button",
  subagent_type="zk-theme-evaluator",
  prompt="Component: button\n\nFollow your agent definition. Use a fresh Chrome tab."
)
Agent(
  description="Evaluate grid",
  subagent_type="zk-theme-evaluator",
  prompt="Component: grid\n\nFollow your agent definition. Use a fresh Chrome tab."
)
Agent(
  description="Evaluate checkbox",
  subagent_type="zk-theme-evaluator",
  prompt="Component: checkbox\n\nFollow your agent definition. Use a fresh Chrome tab."
)
```

Wait for all four to return.

### Step 3 — Merge Evaluator results (orchestrator is the SOLE writer of work-status.md)

Each Evaluator returns a machine-readable **status delta block** in its final output (its agent definition §7); it does NOT write `tasks/harness/work-status.md` itself. This eliminates the parallel-write race by design. The orchestrator merges:

- After all Evaluators return, apply each delta to `tasks/harness/work-status.md`: update the row's `status` / `iter` / `failing-set` columns and append the delta's history line to the **Failing-set history** section.
- The `failing-set` column holds **check ids only** (e.g. `c2, c8, M3`). Narrative history, root-cause notes, and fix chronicles belong in `tasks/harness/eval-reports/<component>.md` — never in the status table (paragraph-length cells made the table unreadable and expensive to reconcile — the 2026-07-22 process audit found the column had degenerated into changelogs).
- If a delta looks inconsistent, read the corresponding `tasks/harness/eval-reports/<component>.md` (single-writer per file) — that is the authoritative source of truth.

### Step 4 — Triage results

For each Evaluator return:

| Result | Action |
|--------|--------|
| `GATE2_PENDING` | Gate 1 passed. Dispatch Gate 2 (`md3-design-verifier`) — see Step 4b. Do NOT write `VERIFIED` yet. |
| `VERIFIED_WITH_VISUAL_NOTES` | Measurement passed (`failing_set == []`) but §3d AI visual review surfaced ≥ 1 finding. Open the eval report's `## AI visual findings` table and inspect each row. For each HIGH/MEDIUM finding: decide one of (a) **Promote** — add a new outcome row or D-tier row to the contract that catches this visually, flip status to `RE_EVAL_NEEDED` (the new row exercises the next eval), and dispatch Generator if appropriate. (b) **Accept** — finding is a false positive (e.g. ZKDoc image style choice that Marble intentionally deviates from); flip status to `GATE2_PENDING` (Gate 2 still required) and log the dismissal in eval-report under `## Findings accepted as false positive` for audit. The orchestrator never auto-promotes; this row requires explicit triage. LOW findings can be accepted in batch with a comment. |
| `NEEDS_FIX` with no `TOKEN_FIX_REQUIRED` in Action-required | Queue for Generator (Step 5). |
| `NEEDS_FIX` with `TOKEN_FIX_REQUIRED` | Append the component + check ids to `tasks/harness/token-issues.md`. Flip status to `ESCALATED_TOKEN_FIX` in work-status.md. Generator is NOT dispatched. |
| `NEEDS_FIX` with `LIBRARY_CONFIG_REQUIRED` (T3 only) | Append the component + check ids + reason to `tasks/harness/library-config-issues.md`. Flip status to `ESCALATED_LIBRARY_CONFIG`. Generator is NOT dispatched (fix needs widget `.ts`/`.java` work, not CSS). |
| `BLOCKED: missing-visual-artefact` | §3a post-condition failed — Evaluator could not capture any screenshot. Check that the preview app is reachable, the component's preview ZUL exists, and `gif_creator` is functional. Retry the Evaluator after fixing. This is rare but blocks ALL downstream eval steps including the AI visual review. |
| `STALLED` / `OSCILLATING` | Append the component to `tasks/harness/escalation.md` with the failure history. Stop loop iteration for this component; ask user before retrying. |
| `EVALUATING_BLOCKED` | Preview app died mid-run. Stop the loop, alert user to restart. |

### Step 4b — Gate 2: MD3 design review (dual-gate VERIFIED)

For every component in `GATE2_PENDING`, dispatch the design reviewer (parallel-safe; may share a message with Evaluator dispatches):

```
Agent(
  description="Design-review textbox",
  subagent_type="md3-design-verifier",
  prompt="Component: textbox\nMode: loop-gate\n\nFollow your agent definition. Screenshots are pre-captured as flat doc/screenshots/textbox-*.png."
)
```

The reviewer reads the contract, component CSS, and the evaluator's screenshots; it writes `tasks/harness/design-reviews/<component>.md` and returns a terminal line. Triage:

| Gate 2 result | Action |
|---------------|--------|
| `GATE2: PASS` | Run Step 4c (cross-cutting Definition of Done). Only after its items are confirmed, flip status to `VERIFIED`. |
| `GATE2: FAIL (critical=N)` | **Contract revision path.** Read the report's `## Findings` table. For each Critical row: (a) if it maps to a contract row (`suspected-row` set), revise that row's expected value; (b) if `suspected-row` is blank, add a new D-tier or M-row; (c) if the user rules the deviation intentional, add it to the contract's `## Accepted MD3 deviations` section instead (user-only decision — the orchestrator never adds entries autonomously). Contract changes of design substance require user re-approval (`contract-approved` gate). Then flip status to `NEEDS_FIX` with the findings as the Action-required input → Generator → `RE_EVAL_NEEDED` → next round runs **both gates** again. |
| `GATE2: BLOCKED (missing screenshots)` | The evaluator's artefacts are missing. Re-dispatch the Evaluator (its §3a captures them), then retry Gate 2. |

Rules:
- **Skip rule**: never dispatch Gate 2 for a component whose Gate 1 failed this round (the CSS is about to change anyway).
- **Convergence**: Gate 2 blocks only on Critical findings; Suggested findings never block. Deviations listed in the contract's `## Accepted MD3 deviations` are skipped by the reviewer — this is what guarantees the loop converges despite intentional MD3 deviations (Marble policy: MD3 token naming, MUI v7 visual values).
- **Gate-2 iteration cap**: track a per-component `gate2-iter` count (increment on every `GATE2: FAIL`). After **2 consecutive** Gate-2 FAILs for the same component, do NOT start another revision round — stop and escalate to the user (the D3 detectors watch only Gate-1 `failing_set`, which is empty in this loop, so nothing else trips). The usual resolution is a user-added `## Accepted MD3 deviations` entry or a user-directed contract redesign.
- The reviewer is read-only: it never edits CSS, contracts, or `work-status.md`. All flips and revisions are orchestrator/user actions.

### Step 4c — Cross-cutting Definition of Done (before the `VERIFIED` flip)

`VERIFIED` certifies more than MD3 appearance. For any component whose contract carries a `## Cross-cutting features` section (mandatory for contracts authored/re-authored after 2026-07-22 — see `doc/spec/new-component-checklist.md`), confirm before flipping:

1. **Gate 1 covered the `x-*` rows** — the eval report's `## Cross-cutting checks` table exists and every row is PASS or `SKIPPED (contract N/A)`. (`x-*` FAILs would have forced `NEEDS_FIX` already; this is a bookkeeping re-check, not a re-measurement.)
2. **CTV tracker row** — `tasks/component-theme-variables-progress.md` has a row for the component (➖ with rationale when `ctv: N/A`). The orchestrator writes it — same single-writer rule as `work-status.md`.
3. **CTV spec family table** — when `ctv: shipped`, port the contract's knob table into `doc/spec/component-theme-variables.md` (CTV-8) and add the component to its Shipped list (mechanical copy of user-approved content).
4. **Render smoke** — `src/test/playwright/render-smoke.spec.ts` `PAGES` contains `/<component>.zul`; add the one-liner if missing.
5. **Forced-colors snapshot** — `doc/screenshots/<component>-forced-colors.png` exists (produced by the evaluator's `x-fc-capture`).

**Legacy contracts** (no `## Cross-cutting features` section — everything authored before 2026-07-22) flip on `GATE2: PASS` as before; their `x-*` rows read `SKIPPED (legacy contract)` and their CTV state is governed by the progress tracker. When such a contract is re-authored for any reason (js-source drift, Gate-2 revision), spec-author adds the section and the component joins this gate.

### Step 5 — Dispatch one Generator (serial)

Pick the highest-priority `NEEDS_FIX` component (per group order). Verify no other component with the same `shared-css-file` is currently in `FIXING` (read work-status.md). Then:

```
Agent(
  description="Generate fix for textbox",
  subagent_type="zk-theme-generator",
  prompt="Component: textbox\n\nFollow your agent definition: read eval report, print contract, edit input.css, run npm run build:css, write gen report."
)
```

Wait for it to return.

### Step 6 — Handle Generator result

Read `tasks/harness/gen-reports/<component>.md`:

- **`build: PASS`** with `Sibling impact: RE_EVAL_NEEDED for: ...`:
  - For each sibling listed, edit `tasks/harness/work-status.md` to flip that sibling's status to `RE_EVAL_NEEDED`.
  - Also flip the just-generated component to `RE_EVAL_NEEDED`.
- **`build: PASS` with no siblings**: flip just the component to `RE_EVAL_NEEDED`.
- **`build: FAIL`**: stop the loop, report the build error to user, do NOT flip status.

### Step 7 — Loop

Go to Step 1.

---

## Parallel execution rules — summary

| Phase | Concurrency | Reason |
|-------|-------------|--------|
| Evaluator | up to 4 parallel | Each uses its own Chrome tab; eval reports are one-file-per-component (no write conflict). Evaluators return status deltas; only the orchestrator writes `work-status.md` (no status-file race). |
| Design reviewer (Gate 2) | freely parallel | Read-only: CSS + pre-captured screenshots, no Chrome. Design reports are one-file-per-component. Only constraint: not while a Generator targets the same `shared-css-file` (avoids reviewing mid-edit CSS). |
| Generator | strictly 1 at a time | `npm run build:css` writes `target/` non-atomically; concurrent builds race. |
| Mixed Eval+Gen | OK in same outer iteration | Evaluator reads CSS files (no edits); Generator edits and rebuilds. They don't conflict if they target different components — but for clarity, run Eval batch, then Gen, then re-Eval. |

**File-lock invariant.** The orchestrator enforces the lock by **not dispatching** a second Generator while the first is still running, and by **not dispatching** an Evaluator on a component whose `shared-css-file` matches a currently-running Generator's target (avoids reading mid-edit CSS).

In practice this is easy: the orchestrator runs synchronously between dispatches. Each Agent tool call blocks until the agent returns. So serializing Generators is just "don't issue a second Agent call until the first returns."

---

## Stop conditions

The orchestrator stops the loop and reports to the user when:

1. All rows are in a terminal state (`VERIFIED`, `STALLED`, `OSCILLATING`, `CONSTRAINT`, `ESCALATED_TOKEN_FIX`, `ESCALATED_LIBRARY_CONFIG`).
2. `tasks/harness/escalation.md` has new entries since the last user check-in.
3. A Generator build fails.
4. Preview app dies (any `EVALUATING_BLOCKED` return).
5. The user types `/stop` or interrupts.

---

## Reading the status file at a glance

```bash
# Count rows by status
grep -E '^\| [a-z]' tasks/harness/work-status.md | awk -F'|' '{print $6}' | sort | uniq -c
```

```bash
# List next eligible components for Evaluator batch
# (note: " PENDING " with surrounding spaces so GATE2_PENDING does NOT match)
awk -F'|' '$6 ~ /( PENDING |NEEDS_FIX|RE_EVAL_NEEDED)/ {print $2 $6}' tasks/harness/work-status.md
```

```bash
# List components awaiting Gate 2 design review
awk -F'|' '$6 ~ /GATE2_PENDING/ {print $2}' tasks/harness/work-status.md
```

---

## Token escalation (D6)

When the Evaluator detects a check failing because a `--zk-*` token value diverges from DESIGN.md (e.g. `--zk-typescale-body-medium-size` is 14px but DESIGN.md §7 says 13px), the Evaluator marks the check `FAIL` and adds `TOKEN_FIX_REQUIRED: <token-name>` to the Action-required section.

The orchestrator:
1. Does NOT dispatch the Generator for that component.
2. Appends a row to `tasks/harness/token-issues.md`:
   ```
   | token | expected | actual | first-detected-in | components-blocked |
   |-------|----------|--------|--------------------|---------------------|
   | --zk-typescale-body-medium-size | 13px | 14px | textbox c2 | textbox, intbox, … |
   ```
3. Sets the component status to `ESCALATED_TOKEN_FIX`.
4. Reports the token issue to the user; token fixes are a separate manual review (token changes affect the whole theme).

After the user fixes the token, they run `npm run build:css` and re-dispatch the Evaluator on all `ESCALATED_TOKEN_FIX` components (flip their status to `RE_EVAL_NEEDED` first).

---

## Example session walk-through

```
[orchestrator] reads work-status.md
[orchestrator] picks Evaluator batch: textbox, button, grid, checkbox  (Group A+E+C+B first row each)
[orchestrator] dispatches 4 Agent() calls in one message
[evaluator-1]  measures textbox, writes report, returns delta status=NEEDS_FIX, action requires TOKEN_FIX for c2,c8
[evaluator-2]  measures button, writes report, returns delta status=NEEDS_FIX, action lists 3 component-level fixes
[evaluator-3]  measures grid, writes report, returns delta status=GATE2_PENDING
[evaluator-4]  measures checkbox, writes report, returns delta status=NEEDS_FIX, 2 component-level fixes
[orchestrator] merges the 4 deltas into work-status.md, reads the reports for triage
[orchestrator] textbox → ESCALATED_TOKEN_FIX (append to token-issues.md)
[orchestrator] dispatches md3-design-verifier for grid (Gate 2)
[design-rev-1] reads grid contract + CSS + screenshots, writes design-reviews/grid.md, returns GATE2: PASS
[orchestrator] grid → VERIFIED (both gates passed)
[orchestrator] queues button + checkbox for Generator (different shared-css-files, but Generator is serial anyway)
[orchestrator] dispatches Generator for button
[generator-1]  reads eval-reports/button.md, prints contract, edits button.css, builds, writes receipt
[orchestrator] flips button status to RE_EVAL_NEEDED
[orchestrator] dispatches Generator for checkbox
[generator-2]  edits checkbox.css (siblings: [radio]), builds, writes receipt with "RE_EVAL_NEEDED for: radio"
[orchestrator] flips checkbox AND radio to RE_EVAL_NEEDED
[orchestrator] loop → picks next Evaluator batch including button, checkbox, radio + 1 more fresh component
[…]
```

---

## Spec-Author phase (pre-loop)

Before the ralph-loop can run on a component, that component must have a user-approved theme contract. The spec-author phase produces it.

### When to run `zk-spec-author <component>`

Trigger spec-author when either condition holds for the component's contract:

- The contract's frontmatter is missing a `rules:` line (no link to a skill component file).
- The contract's frontmatter has `contract-approved: false` (or the line is missing entirely).

The orchestrator should NOT dispatch the evaluator on such a component — the evaluator's §0a gate will refuse with `BLOCKED: contract-approved=false`. Route to spec-author instead.

### Pre-approval MD3 audit (Gate 2, contract-audit mode)

After `zk-spec-author <component>` produces (or updates) the artifacts but BEFORE the user approval gate, dispatch:

```
Agent(
  description="Contract-audit <component>",
  subagent_type="md3-design-verifier",
  prompt="Component: <component>\nMode: contract-audit\n\nAudit the proposed contract + HTML mockup. No Marble CSS or screenshots exist yet."
)
```

This catches MD3/MUI violations and **prose↔table contradictions** in the proposed contract before any CSS is written (garbage in → garbage out — the goldenlayout hybrid-design failure was exactly an unaudited contract). Critical findings go back to `zk-spec-author` for contract revision; repeat until `GATE2: PASS`. Only then proceed to the user approval gate — the user reviews an MD3-vetted contract.

### Approval gate

After `zk-spec-author <component>` produces (or updates) the artifacts:

1. `.claude/skills/zk-component-rules/components/<component>.md` (structural facts only — DOM tree, state classes, composition invariants).
2. `doc/contracts/<component>.md` (theme contract — tokens, references, expected values, State matrix, **and the mandatory `## Cross-cutting features` section**: CTV knobs, density binding, forced-colors risk triage, brand literals, tablet triage — the user approves these decisions together with the visual design; see `doc/spec/new-component-checklist.md`).
3. `doc/contracts/<component>.html` (static mockup rendered with `--zk-*` tokens).
4. `doc/contracts/baselines/<component>-iceblue.png` (iceblue reference screenshot).

…the **user** reviews items 1 and 3 side-by-side against item 4. Only the user may flip `contract-approved: true` in the contract's frontmatter. The orchestrator never flips this flag autonomously.

Once approved, the orchestrator re-enters the main loop normally; the evaluator's §0a gate now passes and measurement proceeds.

### js-source-hash drift recovery

If the evaluator returns status `BLOCKED: js-source drift — re-run zk-spec-author <component>`, the underlying ZK JS source has changed since the contract was authored. The recovery flow:

1. Re-run `zk-spec-author <component>`. The agent re-reads the JS source, re-derives the structural section, refreshes the contract's `js-source-hash:` field, and (if structural facts changed) flips `contract-approved:` back to `false`.
2. If `contract-approved:` was flipped to `false`, repeat the approval gate above. If spec-author determined the structural facts were unchanged, it may keep `contract-approved: true` after refreshing only the hash — but the user should still spot-check.
3. The orchestrator resumes the main loop; the evaluator's §0a and §0b gates both pass and measurement proceeds.

---

## When the orchestrator should ask the user

- Token escalation accumulates ≥ 3 components blocked on the same token (probably a real spec issue).
- A component hits the Gate-2 iteration cap (2 consecutive `GATE2: FAIL` rounds — see Step 4b Rules).
- Stalled or oscillating component (D3 terminal states).
- Preview app stops responding.
- More than 5 consecutive components verified — good time to checkpoint progress with the user.
