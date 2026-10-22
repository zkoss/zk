# Harness status vocabulary

Rules for the per-component verification harness (Evaluator, Generator, Design-Verifier, Orchestrator). The loop itself is in `harness-playbook.md`; the decisions behind it are in `harness-decisions.md`.

The state lives in `tasks/harness/work-status.md` (gitignored, local). A fresh clone has no such file and no `eval-reports/`, `gen-reports/` or `design-reviews/` beside it: create them on first use. Each state row has the columns `component | tier | shared-css-file | siblings | status | iter | failing-set`, grouped under `## Group …` headings. The sections below were the head of that file.

---


**Owner of writes:** Orchestrator ONLY (single-writer; merges the status deltas that Evaluators return in their final output — see evaluator §7 and playbook Step 3).
**Read access:** Evaluator reads for iteration history. Generator may read for context. Design-Verifier (Gate 2) never writes here.
**Column rule:** `failing-set` holds check ids only (e.g. `c2, c8, M3`) — narrative history belongs in `tasks/harness/eval-reports/<component>.md`, never in this table.

## Status legend (canonical status vocabulary — the playbook references this list)
- **PENDING** — never evaluated
- **EVALUATING** — Evaluator running (transient)
- **GATE2_PENDING** — Gate 1 (Evaluator) all checks pass; awaiting Gate 2 (md3-design-verifier) (transient)
- **VERIFIED_WITH_VISUAL_NOTES** — Gate-1 measurement passed but the Evaluator's §3d AI visual review surfaced ≥ 1 finding; awaits orchestrator triage (promote to contract row → `RE_EVAL_NEEDED`, or accept as false positive → `GATE2_PENDING`) (transient)
- **VERIFIED** — dual-gate: Gate 1 measurement pass AND Gate 2 `GATE2: PASS` (zero Critical design findings). Written by the Orchestrator only.
- **NEEDS_FIX** — at least one check failing, or Gate 2 Critical findings routed through a contract revision
- **FIXING** — Generator running (transient)
- **VERIFYING** — Evaluator running post-fix (transient)
- **RE_EVAL_NEEDED** — a sibling's Generator pass touched the shared CSS file
- **STALLED** — `newly_passing == 0` for 2 consecutive iterations while `failing-set` is non-empty (D3 signal A; a clean double-pass never stalls) (terminal — user review)
- **OSCILLATING** — `failures[n] == failures[n-2]` (D3 signal B) (terminal — user review)
- **CONSTRAINT** — manual override (intentionally won't fix; e.g., ZK platform limit) (terminal)
- **ESCALATED_TOKEN_FIX** — check fails because a `--zk-*` token diverges from DESIGN.md; logged in `tasks/harness/token-issues.md`; Generator NOT dispatched (terminal until the user fixes the token, then flip to `RE_EVAL_NEEDED`)
- **ESCALATED_LIBRARY_CONFIG** — T3 only: fix requires widget `.ts`/`.java` or library JS config, not CSS; logged in `tasks/harness/library-config-issues.md` (terminal for this harness)
- **BLOCKED: \<reason\>** — Evaluator pre-flight/artefact gate refused (`contract-approved=false`, `js-source drift`, `page-not-ready`, `missing-visual-artefact`); route per playbook Step 4 / Spec-Author phase (transient)
- **EVALUATING_BLOCKED** — preview app died mid-eval; orchestrator stops the loop and alerts the user to restart it (transient)

> **Dual-gate migration note (2026-06-04):** rows marked `VERIFIED` before this date passed Gate 1 only (the dual-gate `VERIFIED` definition did not exist yet). They are grandfathered — re-run Gate 2 on them opportunistically (flip to `GATE2_PENDING`) or when their component's design quality is questioned. Pilot component: goldenlayout.

## Tier legend
- **T1** — Mira-mapped; compare to Mira values
- **T2** — ZK-only; DESIGN.md tokens only + sibling coherence
- **T3** — Third-party wrapper; wrapper-only checks

## Serialization rule
Only one component per `shared-css-file` may be in `FIXING` at a time. The orchestrator enforces this when dispatching `/zk-theme-generator`.

---
