# Component: codeeditor (theme design)
tier: T3
category: input
shared-css-file: zul/src/main/resources/web/js/zul/code/css/codeeditor.css
siblings: []
preview: ${PREVIEW_URL}/codeeditor.zul
rules: see .claude/skills/zk-component-rules/components/codeeditor.md
contract-approved: true
zk-version: 11.0.0-jakarta.FL.20260904
js-source-files:
  - zul/code/Codeeditor.ts
  - zul/code/mold/codeeditor.js
js-source-hash: 7041c98df9ada3a34d15341994baf153faccd316620782a12b2d51ff5545fb80
closest-sibling: textbox (chrome only — border/radius/bg/fg/hover/focus color roles; see the
  skill's "Sibling decomposition"). Tier is **T3, not T1**, because the payload rendered below
  `.z-codeeditor-cave` — everything from `.cm-editor` down — is CodeMirror 6's own DOM, injected
  asynchronously at runtime and only partially reachable (a handful of named anchor classes are a
  legitimate styling surface; internal generated structure — per-token highlight spans, CodeMirror's
  own `.ͼ*` hash classes — is not). This is the same "chrome from one sibling, third-party payload
  underneath" shape as `tbeditor` (chrome from `window`/`toolbar`, Trumbowyg DOM opaque) and
  `pdfviewer` (thin chrome, PDF.js viewport opaque) — codeeditor sits at the more-reachable end of
  that T3 spectrum (closer to `goldenlayout`'s "named classes ARE styleable" posture than to
  `pdfviewer`'s "internal DOM is forbidden" posture), but the third-party-runtime-DOM structural fact
  is what forces T3 over T1 despite the chrome being a straight textbox copy.
mockup-needed: Y — no ZKDoc canonical image exists for codeeditor (confirmed: no
  `ZKCompRef_Codeeditor*.png` under `DOC/zkdoc/zk_component_ref/images/`, and no
  `zk_component_ref/codeeditor.md` entry — the component is `@since 11.0.0`, newer than the ZKDoc
  snapshot on this machine), so condition (1) of the mockup-decision rule fires automatically. The
  zkbooks componentreference demo (`DOC/zkbooks/componentreference/src/main/webapp/input/codeeditor.zul`)
  renders ZK's own unthemed stock CodeMirror look, which documents the public custom-property API,
  not a design ground truth. The HTML mockup is therefore the only visual reference for this
  contract, alongside the live Marble preview page itself (`codeeditor.zul`, already authored).
mockup-rationale: see mockup-needed above — condition (1), no ZKDoc image, forces Y.

## References
- MUI CSS: **partial analog only** —
  `/Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/Inputs/OutlinedInput.css`
  for the outlined-field chrome (border/radius/color roles) — the same file textbox's own contract
  cites. MUI ships no code-editor component of any kind (confirmed absent from
  `static-css-output/INDEX.md` — no `code`/`monaco`/`editor` entry); the chrome is a *pattern* reuse,
  not a component-for-component analog, which is part of why this is T3 rather than T1.
- DESIGN.md sections: §1 (Surface Palette — root/light-gutter fills), §2 (Text Colors — foreground
  roles), §5 (Corner Radii — `--zk-shape-corner-extra-small`, the "input" row), §7 (Typography —
  body-medium size/line-height; DESIGN.md's table has no monospace row, see the typography token
  file citation below), §8 (State-Layer Overlays — disabled-content opacity), §9 (Motion — standard
  duration/easing pair; also documents *why* a departure from a literal border-width bump is
  legitimate design freedom, see Design Contract), §10 (Density — the auxhead "faint tonal band"
  precedent the gutter reuses; also the "no intrinsic control-height" carve-out class), §11 (Border
  Rules — default/hover/focus color rows; also documents the *literal* 2px-focus-border mechanism
  this component deliberately does not use, see Design Contract).
- `tokens/_typography.css` — `--zk-typescale-mono-family` is defined here, not in a DESIGN.md §7
  table row (the table enumerates UI-text roles only; monospace code text is a novel role this
  component introduces).
- `reference/focus-affordance-no-layout-shift.md` — Mechanism A (inset box-shadow ring, border
  stays 1px) is why the focus row departs from DESIGN.md §11's literal "2px solid" prescription;
  same precedent as daterangebox/timepicker/selectbox/the wrapper-border combo-trio family, for a
  different structural reason (zero root padding, not min-height-pinned children — see Design
  Contract).
- `doc/spec/component-theme-variables.md` — the "Codeeditor — shipped" entry (already ported; this
  contract's CTV knob table below is a verbatim restatement, not a new proposal) records the
  two-family knob split and the WCAG measurement backing the literal dark-surface default.
- `doc/spec/important-inventory.md` — the "Beats a third-party stylesheet injected UNLAYERED at
  runtime" section records the live A/B evidence for all nine `!important` declarations this
  component's CSS carries (and the one that was tested and found *removable*, `.cm-scroller`
  `font-size`) — cited per-row in Expected values below rather than repeated here.
- zkbooks demo (usage + public custom-property API reference, not visual ground truth):
  `DOC/zkbooks/componentreference/src/main/webapp/input/codeeditor.zul`.
- ZKDoc canonical: none found — see `mockup-needed` above.
- Iceblue baseline: **N/A by construction, not merely "not captured."** `org.zkoss.zul.Codeeditor`
  is `@since 11.0.0`; it does not exist in any ZK 10.x jar, so no iceblue-themed rendering of this
  widget can ever exist, on any machine, at any time. `doc/contracts/baselines/codeeditor-iceblue.png`
  is therefore not applicable and should not be searched for — the skill's §4 "reconcile JS source
  against live iceblue render" step was answered instead directly against the live ZK 11 Marble
  preview (`${PREVIEW_URL}/codeeditor.zul`) plus the shipped mold source, which already agree (see
  the authoring summary's "structural surprises" — there were none).
- HTML contract: doc/contracts/codeeditor.html.

## Design Contract

Codeeditor reads as one outlined text field — the same chrome recipe as textbox, just holding a
CodeMirror viewport instead of an `<input>`: `1px solid outline` border, the input-family corner
radius (`--zk-shape-corner-extra-small`, 4px), `--zk-color-surface` background, `--zk-color-on-surface`
foreground — so a codeeditor sitting next to a textbox in a form (see the preview's "Focus order"
section) reads as a sibling control, not an imported widget. All five of these roles are exposed as
codeeditor's own knobs (`--zk-codeeditor-bg/-fg/-radius/-border-color[-hover|-focus]`) rather than
reusing textbox's `--zk-input-*` names directly, because the two widgets are independently
overridable — a page repainting one input family should not have to also repaint the code editor.

**Focus departs from DESIGN.md §11's literal mechanism, and that departure is itself the design
decision, not an oversight.** §11 prescribes "Input focus: 2px solid primary" — the literal
mechanism textbox uses (border-width bump, absorbed by padding compensation). Codeeditor's root has
**zero padding** between its border and the CodeMirror viewport (the cave fills the content box
edge-to-edge, since giving CodeMirror its own internal gutter/scroller chrome makes a second layer
of padding redundant) — so a 1px→2px border bump would shove the entire editor viewport sideways by
a pixel on every focus/blur, the exact layout-shift bug
`reference/focus-affordance-no-layout-shift.md` catalogs. The fix is Mechanism A: the border stays
`1px` in every state, and the second ring pixel is an **inset** `box-shadow` (never outset — an
outset ring would be clipped by the root's own `overflow: hidden`, needed to clip CodeMirror's
square corners to the rounded frame). This is the same mechanism the wrapper-border combo-trio
family (datebox/timebox/spinner/bandbox/timepicker) already uses, but for an unrelated structural
reason — their constraint is min-height-pinned children fighting a shrinking content box; codeeditor
has no padding to shrink from at all. CodeMirror's own default `outline: 1px dotted` on `.cm-focused`
is suppressed (`outline: none !important` — one of the nine irreducible `!important`s, since that
outline is unlayered) so only the theme's own ring reads, avoiding a double ring one pixel apart.

**The ring is drawn on an `::after` overlay, not on the root — and for this component that is
mandatory, not stylistic** (designer report 2026-09-08). An inset `box-shadow` paints on the
element's own background layer, *below* every child. Because the root has zero padding, CodeMirror's
DOM sits flush against the border, and it paints opaque: `.cm-gutters` always, and the whole
`.cm-editor` on the dark surface. With the ring on the root it was therefore painted *under* the
payload — measured `1/2/2/2` px (left/top/right/bottom) on the light surface, where the gutter ate
the left pixel, and `1/1/1/1` on the dark surface, where the ring vanished entirely and only the
bare border survived. The overlay (`position: absolute; inset: 0; pointer-events: none`, anchored by
`position: relative` on the root and rounded-clipped by its `overflow: hidden`) is a single element
above both children, so the ring is uniform **by construction**. This is the same overlay variant
timepicker uses, and it generalises to any widget whose payload is a third-party editor mounted into
a cave. Note that this defect was invisible to computed-style checking — `getComputedStyle(root)`
reported `inset 0 0 0 1px primary` in both broken states — which is why c32 is specified in painted
pixels.

**The dark surface takes its own focus colour (c31/c33).** It previously reused the light knob on the
argument that primary `#376fd0` already clears `3.45:1` against the `#1e1e1e` fill. That is true
against WCAG 1.4.11's 3:1 UI-component floor and it was still reported unreadable in design review —
MD3 does not put the light-scheme primary on a dark surface at all; the dark scheme carries primary
at **tone 80**. `--zk-codeeditor-dark-border-color-focus` derives that tone by absolute OKLCH
lightness off the same seed the palette's containers use, so a brand override
(`doc/spec/brand-override.md`) stays hue-consistent instead of pinning a literal blue. Measured
`8.45:1`.

**The gutter reuses DESIGN.md §10's auxhead "faint tonal band" convention**, not a new decision:
`--zk-codeeditor-gutter-bg` defaults to `--zk-color-surface-container-low`, the exact token §10
already establishes for "one distinguishing signal" on a structurally-adjacent-but-different region
(there, a multi-level header row; here, the line-number rail beside the content pane). The gutter
foreground (`--zk-color-on-surface-variant`) and its divider (`--zk-color-outline-variant`, a plain
§11 "dividers / row separators" row) complete a self-consistent "this is a secondary rail, not the
text you're editing" read, at the same "muted, not competing" strength every other secondary-region
convention in this theme uses.

**The dark surface (`theme="dark"`) is a deliberate, literal near-black — not `--zk-color-inverse-surface`
— and this is the one place in this component's CSS that knowingly refuses a theme token.** The ten
syntax-token colors (`--zk-codeeditor-token-*`) are VS Code Dark+ literals fixed by ZK core inside
the widget JS, not derived from Marble's palette — the surface has to stay legible *against those
specific fixed colors*, not against whatever Marble's inverse-surface token happens to resolve to
under a future palette revision or a brand override. Measured 2026-09-08 (recorded in full in
`component-theme-variables.md`'s Codeeditor entry): all twelve dark-mode foregrounds (ten tokens +
gutter + primary text) clear WCAG AA (4.5:1) against the shipped `#1e1e1e`, worst case 4.52:1 — five
of the same twelve fail against `--zk-color-inverse-surface`'s `#2d3748` (keyword/tag 4.07, comment
3.60, meta 3.39, gutter 3.25). This is the identical "contrast can't key off theme tokens here"
carve-out carousel's overlay chrome already documents, applied to a second, unrelated component for
the same underlying reason: a fixed foreground palette this component's own CSS does not control.
Within the dark surface, the root repaints (not just `.cm-editor`) because the root owns the rounded
corners — leaving it on the light fill would show four pale wedges peeking out around a dark inner
editor. The frame also switches to its own **light-alpha** border pair (c28-c30): the shared
black-alpha knobs composite onto the near-black fill, which left the hover state computing a
different value while producing no visible difference (`rgb(23,23,23)` -> `rgb(4,4,4)`, 1.14:1) —
dark mode had a hover rule but no hover affordance. The component's outline was never at risk (the
fill alone reads ~16.6:1 against the page); it was the state *change* that was invisible, which is
why an assertion that merely compares two states for inequality cannot catch it. Hover is
deliberately **not** a full mirror of the light pair, which would land near `rgb(252)` and read as
a white halo around a black box; `rgba(255,255,255,0.6)` clears the 3:1 UI-state bar at 3.17:1
without that. Focus needs no dark variant (primary blue is already 3.45:1 on the fill) but must be
**re-asserted** after the dark hover rule (c31), since the shared `:focus-within` rule sits earlier
in the file at equal specificity. The dark gutter is kept **flush** with the surface (`background-color` identical to the
editor body, `border-right-color: transparent`) rather than CodeMirror's own lighter-slab default —
distinguishing the rail from the content purely by the line-number color, a quieter separation than
the light mode's tonal-band + divider pairing, matching how a dark IDE theme conventionally reads
(gutter and body as one continuous dark canvas, numbers the only differentiator).

**Disabled** applies the theme's ordinary content-opacity convention
(`--zk-state-disabled-opacity`, 0.38) to the whole root — the same button/input convention, not a
codeeditor-specific knob — plus `pointer-events: none` on the **root**. It began scoped to
`.cm-editor` only, so the dimmed frame still matched `:hover` and answered the pointer with a
border-color change: a live affordance on a dead control (MD3 Gate 2, 2026-09-08). Inerting the
root removes that without a `:not(.z-codeeditor-disabled)` on every hover rule, which would have
raised their specificity above the focus rule. It also blocks select-and-copy, which is correct
for `disabled` and is precisely what separates it from readonly. **Readonly renders identically to enabled** — there is no `.z-codeeditor-readonly`
class and no CSS rule keys off `[readonly]` anywhere in this file. A readonly instance is fully
legible, focusable, and copy-selectable; only keystroke edits are blocked, entirely inside
CodeMirror's editable compartment. This mirrors the theme's established "readonly stays interactive,
disabled alone gets the dimmed treatment" convention (see `doc/spec/component-state-model.md` and
the timepicker/daterangebox precedent) — deliberately, not by omission.

**Typography** consumes `--zk-typescale-mono-family` (a font stack DESIGN.md §7's table has no row
for — code text is a role this component introduces) at the editor's default size,
`--zk-typescale-body-medium-size` (13px, the same "Input / placeholder" row §7 already assigns to
every other field's text) — deliberately reusing the UI body size rather than inventing a
code-specific scale, so a codeeditor's text sits at the same visual weight as the label above it.

## Outcome assertions

Codeeditor is architecturally simple (two server-rendered elements plus a fixed CodeMirror anchor
subtree) but carries several independent repaint surfaces that must all agree — root chrome, gutter
presence, dark-mode completeness, and a focus ring that must not shift the box — so it earns a full
outcome-row set rather than a `visual-goal: trivial` declaration. No `::before`/`::after` glyph is
mentioned anywhere in the Design Contract prose above (codeeditor draws zero icons), so the mandatory
glyph-row rule does not apply to this component — this is a deliberate absence, not an oversight.

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-codeeditor` has visible framing: `border-width ≥ 1px AND border-style ≠ none` OR `box-shadow ≠ none` OR `background ≠ transparent` | visual closure — must read as an outlined field, the same "at least one framing signal" disjunction every bordered-container contract in this theme uses |
| M2 | `.z-codeeditor-cave`'s rendered width/height equals `.z-codeeditor`'s content-box width/height (±1px on each edge) | the cave must fill the frame edge-to-edge with no visible gap — a consequence of the zero-padding composition invariant; a regression here (e.g. an accidental padding re-introduction) would either clip the editor or leave a dead margin |
| M3 | Once CodeMirror has mounted (`.cm-editor` present), `.cm-scroller`'s rendered content area covers ≥ 95% of `.z-codeeditor-cave`'s content-box area | no dead space between the outer chrome and the actual editable viewport |
| M4 | When an explicit `height` (or `vflex`) is set on the component, `.z-codeeditor-cave`'s rendered height is within ±2px of the root's content-box height | the "both cave and `.cm-editor` resolve to height:100%" composition invariant, checked as a rendered outcome rather than a computed-style read — catches the case where the invariant holds on paper (both declare `height:100%`) but an intervening ancestor breaks the percentage chain |
| M-gutter-visible | when `lineNumbers` is at its default (`true`): `.cm-gutters` has a non-zero bounding rect, its `bbox.right` is ≤ `.cm-content`'s `bbox.left` (gutter sits fully to the left, no overlap), and its `bbox.height` is within ±2px of `.cm-content`'s `bbox.height` (spans the same visible line range) | the line-number rail is a documented default-on feature; this row catches it silently collapsing to zero width/height (invisible but structurally present) as well as a numbers-column that starts short or tall relative to the code it's meant to number |
| M5 | Comparing `.z-codeeditor-dark` against an unmodified instance: **all three** of `.z-codeeditor`'s own `background-color`, `.cm-editor`'s `background-color`, and `.cm-gutters`' `background-color` differ from their light-mode values (no single one may be left un-repainted) | the "root must repaint too, not just the inner editor" design-contract claim, checked as a hard AND rather than a disjunction — a partial repaint (e.g. root forgotten) produces the literal "pale wedge around a dark editor" defect the Design Contract prose calls out by name |
| M6 | `.z-codeeditor`'s bounding-box height and width when `:focus-within` are each within ±0px of the same instance at rest, AND at least one of `border-color`/`box-shadow`/`outline` differs between the two states | the Mechanism-A "no layout shift" guarantee (zero tolerance, unlike most geometry rows — this specific class of bug is a snap-to-exact-match check elsewhere in this theme too, e.g. timepicker's M5) plus a plain state-differs check that some focus signal exists |
| M7 | `.z-codeeditor-disabled` has `opacity < 1` AND `.z-codeeditor-disabled .cm-editor` resolves `pointer-events: none` | both halves of the disabled contract (visual dimming + interaction block) must hold together — a theme that dims the chrome but forgets to block pointer events on the inner editor leaves a "looks disabled, still edits" trap |

## Expected values

> **Retired: c19, c27 (2026-09-08).** They asserted `border-left-color` on
> `.cm-cursor` / `.cm-dropCursor`, which the CE build never renders — those nodes come from
> CodeMirror's `drawSelection` extension and `_baseExtensions()` does not include it. The rules
> were dead code and are gone. The ids are **not reused**. The caret that actually paints is the
> browser's native one (`caret-color` on `.cm-content`, UA default: pure black on light, pure white
> on dark, rather than the theme foregrounds) — a knowingly accepted deviation, D7 option B. Found
> by the first real Gate-1 pass; see `tasks/harness/eval-reports/codeeditor.md`.

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-codeeditor` | border | `1px solid var(--zk-codeeditor-border-color)` → `var(--zk-color-outline)` — measured `rgba(0, 0, 0, 0.23)` | DESIGN.md §11 ("Component default (outline)") |
| c2 | `.z-codeeditor` | border-radius | `var(--zk-codeeditor-radius)` → `var(--zk-shape-input)` → `var(--zk-shape-corner-extra-small)` (4px) | DESIGN.md §5 ("Button, input, menu → 4px") — identical role to textbox's own radius row |
| c3 | `.z-codeeditor` | background-color | `var(--zk-codeeditor-bg)` → `var(--zk-color-surface)` — measured `rgb(255, 255, 255)` | DESIGN.md §1 |
| c4 | `.z-codeeditor` | color | `var(--zk-codeeditor-fg)` → `var(--zk-color-on-surface)` — measured `rgba(0, 0, 0, 0.87)` | DESIGN.md §2 |
| c5 | `.z-codeeditor` | overflow | `hidden` | structural — clips CodeMirror's square corners to the root's rounding; not a design token |
| c6 | `.z-codeeditor` | transition | `border-color, box-shadow` on `var(--zk-motion-duration-standard) var(--zk-motion-easing-standard)` | DESIGN.md §9 |
| c7 | `.z-codeeditor:hover` | border-color | `var(--zk-codeeditor-border-color-hover)` → `var(--zk-color-on-surface)` | DESIGN.md §11 ("Input hover") |
| c8 | `.z-codeeditor:focus-within` | border-color | `var(--zk-codeeditor-border-color-focus)` → `var(--zk-color-primary)` — measured `rgb(55, 111, 208)` | DESIGN.md §11 ("Input focus" — color only; see c10 for the width departure) |
| c9 | `.z-codeeditor:focus-within::after` | box-shadow | `inset 0 0 0 1px var(--zk-codeeditor-border-color-focus)` — and the ROOT's own `box-shadow` must stay `none` | `reference/focus-affordance-no-layout-shift.md` — Mechanism A, **overlay variant**. A ring on the root paints below CodeMirror's opaque children and is eaten (see c32); the overlay paints above them |
| c9b | `.z-codeeditor::after` | position / inset / pointer-events | `absolute` / `0px` on all four / `none` — present at rest with a `transparent` ring so the ring can transition | overlay must never intercept clicks or text selection in the editor |
| c10 | `.z-codeeditor:focus-within` | border-width | `1px` (must **NOT** become `2px`) | `reference/focus-affordance-no-layout-shift.md` — the deliberate departure from DESIGN.md §11's literal mechanism |
| c11 | `.z-codeeditor .cm-editor.cm-focused` | outline | `none` (`!important`) | overrides CodeMirror's own unlayered `outline: 1px dotted` default — one of the nine irreducible `!important`s, `important-inventory.md` |
| c12 | `.z-codeeditor-cave`, `.z-codeeditor .cm-editor` | height | `100%` | structural (composition invariant) — not a design token |
| c13 | `.z-codeeditor .cm-scroller` | font-family | `var(--zk-typescale-mono-family)` (`!important`) | `tokens/_typography.css` — not a DESIGN.md §7 table row; overrides CodeMirror's own unlayered `monospace` default, `important-inventory.md` |
| c14 | `.z-codeeditor .cm-scroller` | font-size | `var(--zk-typescale-body-medium-size)` (13px, **no** `!important`) | DESIGN.md §7 ("Input / placeholder") — CodeMirror's own base theme sets no competing `font-size`; the `!important` here was tested and proven removable, `important-inventory.md`'s "Removed" table |
| c15 | `.z-codeeditor .cm-scroller` | line-height | `var(--zk-typescale-body-medium-line-height)` (20px, `!important`) | DESIGN.md §7 (same row as c14) — overrides CodeMirror's unlayered `1.4` default, `important-inventory.md` |
| c16 | `.z-codeeditor .cm-gutters` | background-color | `var(--zk-codeeditor-gutter-bg)` → `var(--zk-color-surface-container-low)` — measured `rgb(247, 249, 252)` (`!important`) | DESIGN.md §10 (auxhead "faint tonal band" precedent — see Design Contract); overrides CodeMirror's unlayered default, `important-inventory.md` |
| c17 | `.z-codeeditor .cm-gutters` | color | `var(--zk-codeeditor-gutter-fg)` → `var(--zk-color-on-surface-variant)` — measured `rgba(0, 0, 0, 0.6)` (`!important`) | DESIGN.md §2 |
| c18 | `.z-codeeditor .cm-gutters` | border-right-color | `var(--zk-codeeditor-gutter-border-color)` → `var(--zk-color-outline-variant)` — measured `rgba(0, 0, 0, 0.12)` (`!important`) | DESIGN.md §11 ("Dividers / row separators") |
| c20 | `.z-codeeditor-disabled` | opacity | `var(--zk-state-disabled-opacity)` (0.38) | DESIGN.md §8 |
| c21 | `.z-codeeditor-disabled` | pointer-events | `none` (on the **root**, not just `.cm-editor`) | structural/behavioral — inerts the frame so a disabled editor stops answering `:hover` with a border change (MD3 Gate 2, 2026-09-08); also blocks select-and-copy, which is exactly what separates `disabled` from `readonly` |
| c22 | `.z-codeeditor-dark`, `.z-codeeditor-dark .cm-editor` | background-color | `var(--zk-codeeditor-background)` — literal `#1e1e1e`, measured `rgb(30, 30, 30)` | deliberate literal, NOT theme-derived — see Design Contract; recorded in `component-theme-variables.md`'s Codeeditor entry |
| c23 | `.z-codeeditor-dark .cm-editor` | color | `var(--zk-codeeditor-color)` — literal `#d4d4d4`, measured `rgb(212, 212, 212)` | same rationale as c22 |
| c24 | `.z-codeeditor-dark .cm-gutters` | background-color | `var(--zk-codeeditor-background)` (same literal as c22, `!important`) | Design Contract — gutter kept flush with the dark surface, not CodeMirror's lighter-slab default; `important-inventory.md` |
| c25 | `.z-codeeditor-dark .cm-gutters` | color | `var(--zk-codeeditor-gutter-color)` — literal `#858585`, measured `rgb(133, 133, 133)` (`!important`) | same rationale as c22; `important-inventory.md` |
| c26 | `.z-codeeditor-dark .cm-gutters` | border-right-color | `transparent` (`!important`) | Design Contract — dark gutter separates by number color alone, not a divider line; `important-inventory.md` |
| c28 | `.z-codeeditor-dark` | border-color | `var(--zk-codeeditor-dark-border-color)` → `rgba(255, 255, 255, 0.23)`, composited `rgb(82, 82, 82)` | dark surface needs a LIGHT-alpha frame: the shared black-alpha c2 collapses onto the `#1e1e1e` fill (`rgb(23,23,23)`) — see c30 |
| c29 | `.z-codeeditor-dark:hover` | border-color | `var(--zk-codeeditor-dark-border-color-hover)` → `rgba(255, 255, 255, 0.6)`, composited `rgb(165, 165, 165)` | DESIGN.md §11 ("Input hover"), dark surface |
| c30 | `.z-codeeditor-dark:hover` vs `.z-codeeditor-dark` | border-color contrast | **≥ 3:1** — measured `3.17:1` (was `1.14:1` before the fix) | WCAG 2.1 SC 1.4.11 (UI state change). The component OUTLINE was never at risk (the dark fill alone is 16.67:1 against the page); it was the state CHANGE that was invisible |
| c31 | `.z-codeeditor-dark:focus-within` | border-color | `var(--zk-codeeditor-dark-border-color-focus)` → `oklch(from var(--zk-color-primary) 0.8 c h)`, measured `#80bdff` | must be RE-ASSERTED in the dark block: the shared `:focus-within` rule sits earlier in the file at equal specificity, so without it a hovered-and-focused dark editor would keep the hover border. The dark surface takes MD3's dark-scheme primary **tone 80**, not the light-scheme primary — see c33 |
| c31b | `.z-codeeditor-dark:focus-within::after` | box-shadow | `inset 0 0 0 1px var(--zk-codeeditor-dark-border-color-focus)` | the overlay ring must switch to the dark knob too, or the 2nd pixel stays light-scheme primary |
| c32 | `.z-codeeditor:focus-within`, `.z-codeeditor-dark:focus-within` | **painted** ring thickness | `2px` on all four edges, measured in pixels at each edge midpoint — the four values must be equal | designer report 2026-09-08. Before the overlay fix: light `1/2/2/2` (the opaque `.cm-gutters` ate the left inset pixel), dark `1/1/1/1` (the opaque `.cm-editor` ate all four). **A computed-style check cannot see this** — the root reported `inset 0 0 0 1px primary` in both broken states, so this row must be verified from pixels |
| c33 | `.z-codeeditor-dark:focus-within` painted ring vs the dark fill | contrast | **≥ 4.5:1** — measured `8.45:1` (`#80bdff` on `#1e1e1e`); was `3.45:1` | MD3 puts primary at tone 80 on a dark scheme. The bar is deliberately set above WCAG 1.4.11's 3:1 UI floor: the old value cleared 3:1 arithmetically and was still reported unreadable in design review |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (light, resting) | `.z-codeeditor` | c1–c6, M1, M2, M3 |
| hover | `.z-codeeditor:hover` | c7 |
| focus-within | `.z-codeeditor:focus-within` | c8, c9, c9b, c10, c11, c32, M6 |
| disabled | `.z-codeeditor-disabled` | c20, c21, M7 |
| readonly | `.z-codeeditor` with the `readonly` attribute set | **no distinct row** — renders identically to default; see Design Contract's "Readonly renders identically to enabled" |
| gutter (lineNumbers=true, default) | `.z-codeeditor .cm-gutters` | c16, c17, c18, M-gutter-visible |
| gutter absent (lineNumbers=false) | `.z-codeeditor:not(:has(.cm-gutters))` | structural — confirms the subtree is absent, not merely hidden |
| dark surface, resting | `.z-codeeditor-dark` | c22, c23, c24, c25, c26, c28, M5 |
| dark surface, hover | `.z-codeeditor-dark:hover` | c29, c30 |
| dark surface, focus + hover | `.z-codeeditor-dark:focus-within:hover` | c31, c31b — focus must win over hover |
| dark surface, focus | `.z-codeeditor-dark:focus-within` | c31, c31b, c32, c33 |
| disabled, hover | `.z-codeeditor-disabled:hover` | c21 — border-color must NOT change from c1 |
| explicit height set | `.z-codeeditor[style*="height"] .z-codeeditor-cave` | M4 |

## Cross-cutting features

### Component Theme Variables
ctv: shipped
ctv-knobs: --zk-codeeditor-bg, --zk-codeeditor-fg, --zk-codeeditor-radius,
  --zk-codeeditor-border-color, --zk-codeeditor-border-color-hover, --zk-codeeditor-border-color-focus,
  --zk-codeeditor-dark-border-color-focus,
  --zk-codeeditor-gutter-bg, --zk-codeeditor-gutter-fg, --zk-codeeditor-gutter-border-color,
  --zk-codeeditor-background, --zk-codeeditor-color, --zk-codeeditor-gutter-color,
  --zk-codeeditor-dark-border-color, --zk-codeeditor-dark-border-color-hover
ctv-probe: { knob: --zk-codeeditor-radius, property: border-radius, value: 2px }

Fifteen knobs in **two families that must not be homogenized** — already ported into
`component-theme-variables.md`'s "Codeeditor — shipped" entry (this table is a verbatim
restatement, not a new proposal; bookkeeping item 2 of the cross-cutting checklist is already
satisfied for this component). Nine are Marble's own, following the ordinary field convention
(`-bg`/`-fg`/`-radius`/`-border-color[-hover|-focus]` plus the three gutter roles), and two more
are the dark surface's own frame (`-dark-border-color[-hover]`, c28-c30): the shared black-alpha
pair composites onto the near-black fill and left dark mode with a 1.14:1 (invisible) hover
change, so the dark frame needs light alphas. Three keep
names ZK core authored (`-background`/`-color`/`-gutter-color`) because they are documented public
API on the componentreference page — the spelling is fixed and does not become Marble's
`-bg`/`-fg` pattern. Two further ZK-core-documented names — `-active-line` and the ten
`-token-*` syntax colors — are **deliberately not declared** anywhere in Marble's token file: the
CE extension set never emits `.cm-activeLine` (so an `-active-line` default would be an orphan
token, no CSS consumer), and the ten token colors are consumed entirely inside widget JS with their
own literal fallbacks (so a CSS-file default would duplicate a value with no CSS consumer either —
see the skill's Notes section, and the checklist's orphan-token rule, which counts only component
CSS as a consumer).

### Density
density: N/A — no intrinsic control-height. Unlike textbox (bound to `--zk-control-height` at
rest), codeeditor is normally given an explicit `width`/`height` (or `vflex`) per instance by the
page author — a multi-line code surface has no single natural "row height" the way a one-line
field does, so there is nothing in the `_sizing.css` control-height ladder for it to bind to, and
binding it anyway would fight the author's own explicit sizing on every instance. The chrome
(border/radius/padding-equivalent-via-zero-padding) does not scale with density either way.

### Forced colors
fc-risk: [box-shadow-focus]
fc-guards: needed — add `.z-codeeditor:focus-within` to the existing block "(1b) Text-input focus"
  selector list in `tokens/_forced-colors.css` (`outline: 2px solid Highlight; outline-offset: -1px`),
  alongside `.z-datebox`/`.z-timebox`/`.z-spinner`/`.z-doublespinner`/`.z-bandbox`/`.z-combobox`/
  `.z-timepicker`/`.z-searchbox-search`/`.z-selectbox`.

Codeeditor's focus ring is carried **only** by an inset `box-shadow` (c9) with the border staying
`1px` in every state (c10) — exactly the pattern block (1b) exists to fix: under
`forced-colors: active`, `box-shadow` is stripped entirely and the border color force-maps to the
same system color whether focused or not, so **no visible focus change** survives. This is the
identical failure mode already fixed for every other Mechanism-A composite input; codeeditor is
simply missing from that selector list today because it postdates the guard's last audit pass
(`@since 11.0.0`, the guard was authored 2026-07-14). No other risk applies: the root already
carries a permanent `border` (not a background-only shape), so `background-affordance` does not
apply to the chrome; there is no selected/checked state anywhere in this widget, so `selection`
does not apply; there is no masked icon anywhere in this component, so `mask-glyph` does not apply.
The gutter's light/dark fill and divider losing their distinct color under forced-colors is a real
but purely cosmetic loss (the line numbers themselves, `CanvasText`-on-`Canvas`, stay legible
either way) — the same class of "known minor limitation" the spec already accepts for the baked-gray
dropdown chevron, not a new guard.

### Brand override
brand-allowed-literals: `#1e1e1e` (`--zk-codeeditor-background`), `#d4d4d4` (`--zk-codeeditor-color`),
  `#858585` (`--zk-codeeditor-gutter-color`) — reason: these three knob defaults intentionally do
  not derive from `--zk-color-inverse-surface` (or any brand-adjacent token) because they exist to
  stay legible against a **fixed**, non-brand-derived ten-color VS Code Dark+ syntax palette that
  ZK core itself bakes into the widget JS — see Design Contract for the measured WCAG evidence
  (5 of 12 foregrounds fail AA against the inverse-surface alternative). A brand/region override can
  still replace all three outright via the knobs — this is a non-token-rooted **default**, not an
  un-overridable hardcoded value, the same status carousel's `--zk-carousel-arrow-bg-hover` literal
  has.

Every other design-bearing value in the Expected-values table above is either a `var(--zk-*)`
token or an explicitly-literal non-color/non-spacing keyword (`hidden`, `100%`, `none`,
`transparent`).

### Tablet
tablet: N/A — codeeditor has no discrete small tap-target sub-controls of any kind (no
icon/button/stepper anywhere in its DOM — contrast breadcrumb's ellipsis button or carousel's
arrow/indicator pair, which is exactly what makes those `needs-specific`). The entire surface is
one large content-editable region whose box size is set explicitly by the page author per instance
(every state-matrix instance in the preview is ≥110px tall × ≥260px wide, and the component has no
intrinsic-height default to shrink below in the first place — see Density above), well clear of any
`--zk-touch-target-min` concern. CodeMirror's own touch-typing/selection/scroll interaction is a JS
concern internal to the library and works identically at any box size — unaffected by the tablet
CSS bundle either way, the same "unaffected either way" reasoning carousel's own JS-driven swipe
gesture already documents for a different component.

## States to evaluate
- [ ] default (light, resting)
- [ ] hover
- [ ] focus-within (no layout shift; ring visible)
- [ ] disabled (dimmed root + inert editor)
- [ ] readonly (renders identically to default — confirm no accidental divergence)
- [ ] gutter present (lineNumbers=true, default)
- [ ] gutter absent (lineNumbers=false — subtree missing, not hidden)
- [ ] disabled, hover (border-color must NOT move off c1 — the root is inert)
- [ ] dark surface, resting (full repaint: root + editor + gutter + light-alpha frame)
- [ ] dark surface, hover (border change must clear 3:1 against the resting border)
- [ ] dark surface, focus + hover (focus must win — the dark hover rule sits later at equal specificity)
- [ ] explicit height set (cave/editor fill it, no collapse)
- [ ] caret color (light and dark)

## T3 wrapper boundary

codeeditor is T3: CodeMirror 6 owns and writes the `.cm-*` DOM tree below `.z-codeeditor-cave`. ZK
only renders `.z-codeeditor` (the root `<div>`) and `.z-codeeditor-cave` (the empty mount point).
Unlike goldenlayout's GoldenLayoutJS, CodeMirror's own base theme is applied via an *unlayered*
runtime `<style>` StyleModule tag rather than an imported stylesheet the theme replaces wholesale —
so the handful of anchor classes below are styled by **beating** that injected stylesheet
(`!important`, recorded per-row in Expected values and in `important-inventory.md`), not by writing
into a vacuum the way goldenlayout's `lm_*` selectors are.

```yaml
wrapper-selectors:
  - .z-codeeditor
  - .z-codeeditor-cave
  - .z-codeeditor-dark        # modifier, not a distinct element
  - .z-codeeditor-disabled    # modifier, not a distinct element
styled-internal-selectors:
  # CodeMirror-injected but a legitimate, intended styling surface under .z-codeeditor scope:
  - .z-codeeditor .cm-editor
  - .z-codeeditor .cm-editor.cm-focused
  - .z-codeeditor .cm-scroller
  - .z-codeeditor .cm-gutters
  - .z-codeeditor .cm-cursor
  - .z-codeeditor .cm-dropCursor
forbidden-selectors:
  # Generated per-token highlight spans carry INLINE style from HighlightStyle — there is no
  # stable class to select, and none should be invented:
  - ".z-codeeditor .cm-content span[style]"
  # Never emitted by the CE extension set (_baseExtensions has no highlightActiveLine/drawSelection)
  # — authoring a rule here is dead CSS today and risks becoming a false "it's themed" assumption
  # if an EE build later enables the extension without a matching CSS review:
  - ".z-codeeditor .cm-activeLine"
  - ".z-codeeditor .cm-activeLineGutter"
  - ".z-codeeditor .cm-selectionLayer"
  - ".z-codeeditor .cm-selectionBackground"
  # CodeMirror's own StyleModule-generated hash classes (implementation detail, not a public
  # styling surface — content differs per CodeMirror build/version):
  - ".z-codeeditor [class^='ͼ']"
theme-bridge:
  strategy: token-passthrough   # ZK core itself bridges: its widget JS reads var(--zk-codeeditor-token-*, <fallback>)
                                # strings and hands the resolved value to CodeMirror's own
                                # HighlightStyle/EditorView.theme JS APIs — CodeMirror itself has no
                                # native CSS custom-property theming hook.
  available: true               # true via ZK-core's JS-level bridge; CodeMirror's OWN mechanism has none
  variables:
    # ZK-core-authored, consumed only inside widget JS — never inside this component's own CSS file:
    - --zk-codeeditor-token-keyword
    - --zk-codeeditor-token-string
    - --zk-codeeditor-token-comment
    - --zk-codeeditor-token-number
    - --zk-codeeditor-token-function
    - --zk-codeeditor-token-type
    - --zk-codeeditor-token-variable
    - --zk-codeeditor-token-tag
    - --zk-codeeditor-token-attribute
    - --zk-codeeditor-token-meta
    - --zk-codeeditor-active-line   # documented, but never emitted by the CE extension set — see forbidden-selectors
```
