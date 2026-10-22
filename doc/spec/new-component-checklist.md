# New-Component Cross-Cutting Checklist (Definition of Done)

Since the verification harness was created, Marble gained theme-wide features that live
outside any single component's MD3 appearance: brand-color override, Component Theme
Variables (CTV), forced-colors/high-contrast guards, density/compact mode, the tablet
layer. This spec is the **single canonical list** of what a component must additionally
satisfy before it is `VERIFIED` — and which harness role owns each part, preserving the
implement/verify separation (Generator implements, Evaluator measures, orchestrator does
bookkeeping; no role certifies its own work).

**Scope**: applies to every contract that carries a `## Cross-cutting features` section —
mandatory for all contracts authored or re-authored after 2026-07-22. **Legacy contracts**
(no section) are grandfathered: their rows flip to `VERIFIED` under the old rules, and
their CTV state is governed by [`../../tasks/component-theme-variables-progress.md`](../../tasks/component-theme-variables-progress.md).
When a legacy contract is re-authored for any reason (js-source drift, Gate-2 revision),
`zk-spec-author` adds the section then.

**Extending this list**: when a future theme-wide feature ships (e.g. auto-contrast text
once [`auto-contrast-text.md`](auto-contrast-text.md) is finalized), add ONE row to the
feature table below + one subsection to the contract template — the agent definitions
reference this file and need no per-feature edits.

## Feature table (obligation × role)

| Feature | Spec | Declared by (contract) | Implemented by | Verified by | Bookkeeping (orchestrator) |
|---------|------|------------------------|----------------|-------------|-----------------------------|
| MD3 appearance | `DESIGN.md` | spec-author (D/M rows) | Generator | Evaluator §3b + Gate 2 | status flip |
| Brand override | [`brand-override.md`](brand-override.md) | `brand-allowed-literals:` | Generator (token-rooted declarations only) | Evaluator `x-brand-decl` | — |
| Component Theme Variables | [`component-theme-variables.md`](component-theme-variables.md) | `ctv:` + knob table | Generator (knob hoist, consumption, demo, spec test) | Evaluator `x-ctv-root`, `x-ctv-region`, `x-ctv-suite` | tracker row + spec family table port |
| Density / compact | [`data-dense-mode.md`](data-dense-mode.md) | `density:` + `density-tokens:` | Generator (bind to ladder/alias) | Evaluator `x-density` | — |
| Forced colors (WHCM) | [`forced-colors.md`](forced-colors.md) | `fc-risk:` + `fc-guards:` | Generator (guards in central file) | Evaluator `x-fc-capture` + §3d fc review; `npm run test:forced-colors` when guards touched | — |
| Tablet / touch | [`tablet-design-overview.md`](tablet-design-overview.md) | `tablet:` | Generator (only if `needs-specific`) | tablet Playwright project (manual run for now) | — |
| Render smoke | — | — | — | `render-smoke` project | add `/<comp>.zul` to `render-smoke.spec.ts` `PAGES` |
| css.dsp wiring | [`css-dsp-file-structure.md`](css-dsp-file-structure.md) | — | Generator/theme | `npm run check:css-dsp` (mechanical) | — |

## Contract section template (authored by zk-spec-author, approved by the user)

Every new contract must contain this section — all five subsections present; `N/A`
requires a stated rationale. Field names are fixed (they are grepped by the agents).

```markdown
## Cross-cutting features

### Component Theme Variables
ctv: shipped | N/A — <rationale, e.g. "non-visual stub">
ctv-knobs: --zk-<comp>-bg, --zk-<comp>-fg, --zk-<comp>-radius, …
ctv-probe: { knob: --zk-<comp>-radius, property: border-radius, value: 2px }

### Density
density: bound | N/A — <rationale, e.g. "no intrinsic control height">
density-tokens: --zk-input-height

### Forced colors
fc-risk: none | [mask-glyph, box-shadow-focus, selection, background-affordance]
fc-guards: N/A | <selectors to guard in tokens/_forced-colors.css>

### Brand override
brand-allowed-literals: none | <literal — reason, e.g. "rgba(0,0,0,0.04) — row-hover, matches grid convention">

### Tablet
tablet: central-touch-rules | needs-specific — <what> | N/A — <non-interactive>
```

Authoring rules:
- **CTV vocabulary follows the shipped families** in `component-theme-variables.md`
  (e.g. wrapper-border inputs use `bg/fg/radius/border-color/-hover/-focus` +
  `popup-bg/-radius`). Don't invent new knob axes without checking the family tables.
- **`density: bound` obligates the Expected-values table**: every height/padding row for
  the control must cite the ladder/alias token from `tokens/_sizing.css` (e.g.
  `var(--zk-input-height)`), never a raw px literal. If no alias fits, the contract names
  the new alias to add (Generator adds it to `_sizing.css`).
- **fc-risk triage**: `mask-glyph` = icon drawn via `mask-image`/`background` (WHCM erases
  backgrounds → glyph vanishes); `box-shadow-focus` = focus affordance carried only by
  box-shadow (WHCM strips shadows); `selection` = selected state carried only by
  background tint; `background-affordance` = any state readable only from a fill.
  Each named risk needs a guard in `tokens/_forced-colors.css` (or a rationale why the
  central rules already cover it).

## The `x-*` check-id vocabulary (Evaluator §3e)

These ids count toward `failing-set` exactly like `c*`/`M*` rows; all are
**component-rooted** (routed to the Generator, never `TOKEN_FIX_REQUIRED`).

| id | procedure (summary) |
|----|---------------------|
| `x-ctv-root` | Set `ctv-probe.knob` to `ctv-probe.value` via `document.documentElement.style.setProperty(…)` → the anchor's `ctv-probe.property` computes to the value; `removeProperty` → default restored |
| `x-ctv-region` | Set the same knob inline on a container holding one instance → that instance changes, an instance outside does not; then clean up |
| `x-ctv-suite` | `npx playwright test --config zkpreview/src/test/playwright/playwright.config.ts --project=component-theme-variables -g "<component>"` is green; **0 matched tests = FAIL** (CTV-9 test missing; it runs on the component's own preview page) |
| `x-density` | For each `density-tokens` entry: shrink it by 8px at `documentElement` → the control's rendered height shrinks accordingly (±2px); clean up |
| `x-fc-capture` | `npx playwright test --config zkpreview/src/test/playwright/playwright.config.ts --project=forced-colors-gallery -g "<component>"` produces `doc/screenshots/<comp>-forced-colors.png` (>0 bytes); the snapshot joins the §3d review inputs, reviewed against the `fc-risk` list |
| `x-brand-decl` | Grep `shared-css-file` for color literals (`#hex`, `rgb/rgba/hsl/oklch(` without `var(--zk-`): every hit must be whitelisted in `brand-allowed-literals`; plus every Expected-values row flagged `token-rooted? yes` must be *declared* via its token (computed-value equality is not enough) |

When `ctv: N/A` / `density: N/A` / `fc-risk: none`, the matching probes are `SKIPPED
(contract N/A)` — a recorded decision, not a gap. When the contract has **no**
`## Cross-cutting features` section at all (legacy), all `x-*` rows are `SKIPPED (legacy
contract)` and the eval report notes it.

## Orchestrator bookkeeping gate (before the `VERIFIED` flip)

For a contract carrying the section, `GATE2: PASS` alone no longer flips `VERIFIED`.
The orchestrator (sole writer of status/tracker docs) first confirms:

1. **CTV tracker row** exists in `tasks/component-theme-variables-progress.md`
   (a `ctv: N/A` decision is recorded as ➖ with the rationale — the tracker's
   completeness audit must not go stale).
2. **CTV spec family table** — when `ctv: shipped`, the contract's knob table is ported
   into `component-theme-variables.md` (CTV-8) and the Shipped list updated.
3. **Render smoke** — `zkpreview/src/test/playwright/render-smoke.spec.ts` `PAGES` contains
   `/<comp>.zul` (mechanical one-liner; the orchestrator adds it if missing).
4. **Forced-colors snapshot** — `doc/screenshots/<comp>-forced-colors.png` exists
   (produced by `x-fc-capture`).

## Known limits / follow-ups (not yet implemented)

Work steps for these items live in [`../../tasks/marble-theme-followups.md`](../../tasks/marble-theme-followups.md).

- No automated **brand-flip** regression project (override `--zk-color-primary` at
  `:root`, assert key surfaces recolor) — `x-brand-decl` guards the declaration level only.
- No automated **compact-preset** project — `x-density` probes seeds per component only.
- `render-smoke.spec.ts` `PAGES` is still a hardcoded list (gallery-scan auto-discovers);
  converting it to auto-discovery would delete bookkeeping item 3.
- Forced-colors judgment beyond the mechanical suite is §3d AI review (advisory) — a HIGH
  fc finding routes through `VERIFIED_WITH_VISUAL_NOTES` triage, not a hard gate.
