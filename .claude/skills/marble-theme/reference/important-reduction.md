# `!important` reduction

This page was merged from the former `important-reduction` skill on 2026-09-10 and is Step 4 of maintaining Marble hygiene.

Use this page when the work is to remove, reduce, eliminate, clean up, audit, or "get rid of"
the `!important` in the theme CSS; to figure out which `!important` are actually needed vs
redundant; or when doing a ZK version upgrade and wanting to re-check `!important` workarounds.

Removing `!important` is easy; removing it *without silently changing how the theme
looks* is the hard part. The whole method exists to make every removal provable and
every survivor justified — so the count only goes down, and it stays down.

The single most important idea: **decide by measuring the running app, not by reading
code comments.** Comments that justify an `!important` ("ZK does X so we must override")
go stale across ZK versions. The flagship example below caught a comment that was two
minor versions out of date.

## Roles: Planner → Generator → Evaluator

Run one `!important` *cluster* (a file, or a category within a file) through three roles.
Clusters are independent — a failure on one never blocks the others.

- **Planner** (read-only) — inventory + triage into keep/remove hypotheses with evidence.
- **Generator** — for a remove candidate, delete the keyword (only), rebuild.
- **Evaluator** (read-only) — prove render-neutral on the live app, or reject.

The Generator/Evaluator roles map to this project's existing `zk-theme-generator` and
`zk-theme-evaluator` subagents — use them, or do the steps inline for a handful of
candidates. The Planner is you (or a `Plan`/`Explore` agent) producing the decision table.

## Non-negotiable rules

1. **Empirical proof to remove.** Remove an `!important` only when an automated
   measurement shows the rendered result is byte-identical with it gone. Anything you
   can't reproduce in a headless test (a drag-only element, an EE component with no
   preview, a state you can't drive) **stays** — documented as irreducible. This is a
   deliberately conservative bar: a false "keep" costs nothing; a false "remove" ships a
   visual regression.
2. **Delete + safe cascade wins only.** Delete the keyword; if the value would be lost,
   recover it via `@layer` priority or a *minimal* specificity bump. Never change a
   rendered value, restructure DOM/selectors, or re-introduce `!important` elsewhere.
3. **No cascade beats an inline style.** An `!important` that overrides a ZK-JS-set inline
   `style="…"` (margin, position, `display`, `height`, `width`, `padding`) is structurally
   irreducible — no layer or specificity can win against inline. Confirm what ZK actually
   writes at runtime (read the inline `style` in a probe); don't assume from the comment.
4. **Distrust version-pinned justifications.** Any comment citing a ZK version or
   describing ZK JS behavior is *unverified* until re-checked against the current runtime.
   When a rule survives, rewrite its comment to the current version + point at the guard
   test. When removed, the comment goes with it.

## Procedure

### 1 — Inventory (comment-aware)

Raw grep over-counts (it sees `!important` inside comments). Use the bundled script:

```bash
node .claude/skills/marble-theme/scripts/count-important.js
```

It prints per-file counts, a real-declaration total, and every declaration line. This
number — not a grep or a memory of past counts — is the source of truth.

### 2 — Triage each declaration

Assign a category and a hypothesis, using two oracles:

- **Default-theme comparison** — does stock ZK (iceblue) use `!important` in the analogous
  rule? Extract the version-matched theme (e.g. `temp/iceblue_c-<version>/`) and compare.
  If iceblue doesn't need it, ours probably doesn't either.
- **`@layer` cascade** — the theme layers `zk-base < zk-components < zk-utilities`. An
  `!important` that only out-ranks a *lower* layer is redundant (layer order already wins).
  But ZK *core* CSS is unlayered and beats all theme layers — an `!important` fighting a
  ZK-core rule is genuinely needed.

| cat | meaning | default hypothesis |
|-----|---------|--------------------|
| A | oracle/layer says removable | **remove** (verify) |
| B | overrides a ZK-JS inline style | keep (confirm inline at runtime) |
| C | accessibility (`prefers-reduced-motion` reset) | **keep** — must override everything |
| D | same-layer or ZK-core-unlayered conflict | keep; test if specificity alone wins |
| E | ambiguous | route to a behavioral test |

Record, per remove/test candidate, the **expected computed value** the `!important`
currently produces — that's the invariant the Evaluator asserts.

### 3 — Launch the app (the runtime oracle)

```bash
cd zkthemelab && ./gradlew appRun -PhttpPort=8085 --console=plain     # ${PREVIEW_URL}
```

The *running app* is the oracle for "what inline style does ZK actually set" and "does the
popup stay hidden" — not source-reading. (ZK source on disk may be a different minor
version than the dependency; trust the running dependency.)

### 4 — Empirical A/B per candidate

For each remove candidate:

1. **Measure HEAD** (keyword present) with the probe:
   ```bash
   node .claude/skills/marble-theme/scripts/probe.js \
     ${PREVIEW_URL}/<page>.zul '<selector>' 'display,width,…'
   ```
   Note the inline `style` in the output — if ZK set the property inline, it's category B → keep.
2. **Generator**: delete just the `!important` (keep the declaration), `node scripts/build-css.js --module zul`.
3. **Re-measure.** Identical computed values → removable. Any change → load-bearing → revert.
4. For **behavioral** cases (popup open/dismiss, collapse, view toggle, mobile UA), the
   static probe isn't enough — write a small bespoke Playwright script that drives the
   interaction then measures (see the colorbox example). Test the **mobile UA** too (ZK's
   touch code paths differ) via a `devices['iPad …']` context.

> **Don't gate on screenshot baselines.** Committed `toHaveScreenshot` baselines drift and
> are often stale. The trustworthy gate is computed-style / behavioral A/B on the live app.

### 5 — Guard every accepted removal

Add a computed-style (or behavioral) Playwright assertion that the ex-`!important` value
still holds — a permanent CI guard so a future `@layer`/ZK change that lets a lower rule win
fails loudly. Desktop guards → `screenshot.spec.ts`; mobile/touch → `tablet.spec.ts`.
Proof-of-guard: confirm a *naive* removal (without the cascade backup) would FAIL the
assertion — a test that's green both ways guards nothing. (Often a guard already exists —
check before writing a new one.)

### 6 — Gates, record, commit

- Gates: `npm run lint:css` (no *new* issues), `node scripts/check-css-dsp.js --module zul --zk-home /Users/hawk/Documents/workspace/ZK10`
  (exit 0), `bash .claude/skills/marble-theme/scripts/audit-css.sh --out <file>` (advisory).
- Record every verdict + evidence + guard-test name in `doc/spec/important-inventory.md`
  (the durable anti-re-investigation record) and append a row to `doc/skill-gaps.md`.
- Fix any stale version-pinned comment you touched; correct `CLAUDE.md`'s ZK version if drifted.
- Stage only the paths you changed (never `git add -A` here) and commit.

## Worked example — colorbox (why empirical beats source-reading)

`colorbox.css` had `.z-colorbox > .z-colorbox-popup { display: none !important }`, justified
by a comment citing **ZK 10.2.1**. Two signals *suggested removable*: (a) stock iceblue hides
the same popup with plain `display:none` (no `!important`); (b) the current ZK **source**
(10.4-SNAPSHOT) shows `closePopup()` now sets inline `display:none` before `undoVParent()`.

But the shipped dependency was **10.3.0.1**. A behavioral probe on the **mobile UA** — open
the colorbox, tap away to dismiss, read the reattached popup — showed it *keeps* inline
`display:block` after dismiss. Removing the `!important` made it compute `display:block` /
visible = the bug returns. So on 10.3 the rule is **load-bearing** → KEEP. The comment was
stale (10.2.1) and was rewritten to the version-independent mechanism + a pointer to the
guarding `tablet.spec.ts › tablet-colorbox-dismiss` test.

Lesson: the user's "this looks like a misjudgment" hunch was half-right — the *comment* was
wrong, but the *rule* was needed. Only measuring the running dependency settled it; both the
comment and the newer source would have misled a remove.

Contrast — the clean removals in the same pass (progressmeter `display:block`, messagebox
`display:flex` + child `margin-left`, menu separator `margin`): the probe showed ZK sets no
competing inline style and the layered/high-specificity rule already wins, so computed values
were identical without the keyword. Removed, each with a computed-style guard.

## Bundled scripts

Both scripts live in this skill's `scripts/` directory. Run them from the zk repo root;
`count-important.js` scans `zul/src/main/resources/web` unless a root is given, and EE CSS lives in
`../zkcml` instead.

- `.claude/skills/marble-theme/scripts/count-important.js` — comment-aware inventory (step 1).
- `.claude/skills/marble-theme/scripts/probe.js` — computed-style + inline-style A/B probe (step 4). Run from the repo
  root, or `export NODE_PATH=<repo>/node_modules` if run from elsewhere.

## When to stop

Under the conservative bar, most survivors are genuinely irreducible (inline overrides,
a11y, or unreproducible states) — so a small removal count is a correct outcome, not a
shortfall. Stop when every remaining `!important` has an evidence-backed row in
`doc/spec/important-inventory.md`. On a ZK upgrade, re-run and re-test the inline-override
group first — that's where a version bump most often changes the answer.
