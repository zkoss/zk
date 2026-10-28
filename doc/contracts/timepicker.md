# Component: timepicker (theme design)
tier: T1
category: input
preview: ${PREVIEW_URL}/timepicker.zul
rules: see .claude/skills/zk-component-rules/components/timepicker.md
contract-approved: true  # user-approved 2026-06-08 (GATE2 contract-audit PASS critical=0; 3 suggested findings forwarded to Generator as impl notes). 2026-06-08 popup realigned to combobox dropdown per user request (position/z-index/margin/shape-menu/elevation-dropdown/no-border) — fixed bottom-left mispositioning. 2026-06-08 focus ring changed from border-width:2px to inset box-shadow (c5/c5b/c5c, M5 — no layout shift) + root overflow:hidden (c18, M6 — clip always-readonly opaque input to rounded corners), per two user-reported bugs. 2026-06-09 focus ring moved from a root inset shadow to an always-present `::after` overlay (c5 re-pointed to `::after`, new c5d root box-shadow:none) — the opaque always-readonly input occluded the root inset ring on the input side, making the ring look thicker around the transparent button (user-reported). 2026-07-03 readonly de-emphasis removed (user-authorized) — `.z-timepicker-readonly` now reads as an ACTIVE outlined field (c15/c15b: transparent input + `cursor:pointer`), matching the readonly combobox/datebox/bandbox, after **ZK-6122** established the constructor no longer forces readonly (the "always-readonly" premise behind c15/c18/c5/c5d/M6 is obsolete). The input is now transparent in every state, so `overflow:hidden` + the `::after` overlay now serve only the focus ring.
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/src/main/resources/web/js/zkmax/inp/Timepicker.ts  (relative to ZK10/zkcml/)
  - zkmax/src/main/resources/web/js/zkmax/inp/mold/timepicker.js  (delegates to ComboWidget.$redraw)
js-source-hash: 64eba4112b076c7eea3def04a9630731319d58f297d41ab5157b9890b2e56cce
closest-sibling: timebox (via combo-trio pattern)
mockup-needed: N
mockup-rationale: ZKDoc canonical image ZKCompRef_Timepicker.png exists; Marble's token-swap of the input chrome matches the ZKDoc default appearance — no structural redesign.

## References
- MUI CSS: Inputs/OutlinedInput.css (input chrome); Inputs/Select.css (popup list)
- DESIGN.md sections: §7 (Input typography — 13px body-medium), §5 (Shape — 4px corner-extra-small for inputs), §11 (Borders — 1px outline), §9 (State layers — disabled opacity 0.38)
- Iceblue baseline: doc/contracts/baselines/timepicker-iceblue.png (captured 2026-06-08)
- ZKDoc canonical: /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/ZKCompRef_Timepicker.png
- HTML contract: doc/contracts/timepicker.html (mockup-needed: N — skip)

## Design Contract

The timepicker input chrome follows the Marble outlined-input pattern: `1px solid --zk-color-outline` border on the root span, `--zk-shape-corner-extra-small` (4px) radius, `--zk-typescale-body-medium-size` (13px) font for both the input text and the popup option list. The clock-icon button shares the right border radius of the root span and uses `--zk-color-on-surface-variant` for the icon color. On hover the border brightens to `--zk-color-on-surface`; on focus-within the border-color becomes `--zk-color-primary` and the second pixel of the focus ring is drawn by an always-present `::after` **overlay** (`inset 0 0 0 1px --zk-color-primary`, transparent at rest). The ring lives on the overlay (`pointer-events:none`, clipped to the rounded shape by the root's `overflow:hidden`) so it paints above both children and reads uniformly across the field. The border-WIDTH stays `1px` so the field does not resize on focus (a `border-width:2px` increase would shrink the border-box content area and the min-height-pinned input/button would force the root 2px taller; see `reference/focus-affordance-no-layout-shift.md`). The root sets `overflow: hidden` to clip the focus overlay (and any child background) to the rounded corners. The disabled state applies `opacity: var(--zk-state-disabled-opacity)` (0.38) to the root via the `.z-timepicker-disabled` class. The readonly state reads as an **active** outlined field (transparent input, `cursor: pointer`) — not disabled — because a readonly timepicker is still interactive (the popup opens); the greyed treatment is reserved for `disabled`, matching the readonly combobox/datebox/bandbox (see `doc/spec/component-state-model.md` and ZK-6122). The detached dropdown popup is aligned with the canonical combobox dropdown: `position: absolute` with `z-index: 1600` (ZK detaches the popup to `<body>` and sets inline `left`/`top`, which require a positioned box), a `4px` top-margin gap below the trigger, `--zk-elevation-dropdown` box-shadow, `--zk-shape-menu` radius, `--zk-color-surface` background, and no border (the elevation shadow defines the edge). Each popup option is `--zk-typescale-body-medium-size` with `--zk-color-on-surface` color; the selected option uses `--zk-color-primary` color; hover on an option uses `--zk-color-surface-container` background. Inplace mode shows a transparent-border, transparent-background input with the button hidden. Invalid state adds `1px solid --zk-color-error` to the root.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-timepicker` has `border-width ≥ 1px` AND `border-color ≠ transparent` | root must read as an outlined input field |
| M2 | `.z-timepicker` bbox height ≥ 28px | minimum touch target; input must not collapse |
| M3 | `.z-timepicker-input` bbox.right ≤ `.z-timepicker` bbox.right AND `.z-timepicker-button` bbox.right ≤ `.z-timepicker` bbox.right ± 2px | input + button fit inside root; no overflow |
| M4 | `.z-timepicker.z-timepicker-disabled` has `opacity < 0.5` OR children have `color` with `alpha < 0.5` | disabled state is visually apparent |
| M5 | `.z-timepicker` bbox height when `:focus-within` == bbox height at rest (±0px) | focus affordance must not resize the field — the blue ring is an inset box-shadow, not a border-width increase |
| M6 | `.z-timepicker-input` bbox does not extend past the root's rounded corner on the left edge (root `overflow: hidden`) | children (focus-ring overlay + any child bg) are clipped to the root border-radius — no square corner bleed |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-timepicker` | border | `1px solid var(--zk-color-outline)` | DESIGN.md §11 — component default (outline) |
| c2 | `.z-timepicker` | border-radius | `var(--zk-shape-corner-extra-small)` | DESIGN.md §5 — inputs use 4px |
| c3 | `.z-timepicker-input` | font-size | `var(--zk-typescale-body-medium-size)` (resolves to 13px) | DESIGN.md §7 — Input/placeholder 13px; overrides bundle default 14px |
| c4 | `.z-timepicker-disabled` | opacity | `var(--zk-state-disabled-opacity)` (resolves to 0.38) | DESIGN.md §9 — disabled content opacity |
| c5 | `.z-timepicker:focus-within::after` | box-shadow | `inset 0 0 0 1px var(--zk-color-primary)` | reference/focus-affordance-no-layout-shift.md — the focus ring is drawn on an always-present `::after` OVERLAY (transparent at rest), painted above both children (`pointer-events:none`, clipped to the rounded shape by `overflow:hidden`) so it reads uniformly. Border stays 1px → no layout shift. |
| c5b | `.z-timepicker:focus-within` | border-color | `var(--zk-color-primary)` | DESIGN.md §11 — focus tint; border-WIDTH stays 1px (the `::after` overlay supplies the visual 2nd px) |
| c5c | `.z-timepicker:focus-within` | border-width | `1px` | reference/focus-affordance-no-layout-shift.md — MUST NOT increase to 2px (that is the layout-shift bug) |
| c5d | `.z-timepicker:focus-within` | box-shadow | `none` | reference/focus-affordance-no-layout-shift.md — the ring lives on the `::after` overlay (c5), not a root inset shadow; the root's own box-shadow stays `none` |
| c6 | `.z-timepicker:hover` | border-color | `var(--zk-color-on-surface)` | DESIGN.md §11 — Input hover border |
| c7 | `.z-timepicker-popup` | box-shadow | `var(--zk-elevation-dropdown)` | DESIGN.md §6 — shared dropdown elevation token (= elevation-2); aligns with combobox/bandbox |
| c8 | `.z-timepicker-popup` | border-radius | `var(--zk-shape-menu)` | DESIGN.md §5 — shared menu/dropdown radius token (= 4px); aligns with combobox/bandbox |
| c9 | `.z-timepicker-popup` | background-color | `var(--zk-color-surface)` | popup-tokens.md — all floating popups share `--zk-color-surface` |
| c16 | `.z-timepicker-popup` | position | `absolute` | floating-popup-in-body.md — ZK detaches popup to `<body>` and sets inline `left`/`top`; without `position: absolute` those offsets are inert and the popup falls to bottom-left of body flow |
| c17 | `.z-timepicker-popup` | z-index | `1600` | align with combobox/bandbox dropdown stacking (datebox calendar uses 1700) |
| c10 | `.z-timepicker-option` | font-size | `var(--zk-typescale-body-medium-size)` | DESIGN.md §7 — option list text at input density |
| c11 | `.z-timepicker-option-selected` | color | `var(--zk-color-primary)` | DESIGN.md §3 — selected state uses primary |
| c12 | `.z-timepicker-inplace .z-timepicker-input` | background-color | `transparent` | DESIGN.md — inplace hides chrome; transparent bg |
| c13 | `.z-timepicker-inplace .z-timepicker-button` | visibility | `hidden` | DESIGN.md — inplace hides the button |
| c14 | `.z-timepicker:has(.z-timepicker-invalid)` | border-color | `var(--zk-color-error)` | DESIGN.md §3 — error/invalid uses error color. ZK puts `.z-timepicker-invalid` on the INPUT child; `:has()` tints the root border. Rule carries `transition:none` (the base border-color transition restarts every recalc while `:has()` matches, stranding the error color at the transition start). |
| c15 | `.z-timepicker-readonly .z-timepicker-input` | cursor | `pointer` | readonly is interactive (the popup opens) → active affordance, not disabled; only `cursor:pointer` signals the popup. See doc/spec/component-state-model.md + ZK-6122. |
| c15b | `.z-timepicker-readonly .z-timepicker-input` | background-color | `transparent` (rgba(0,0,0,0)) — MUST NOT be `surface-container` | readonly reads as an ACTIVE outlined field like the editable timepicker and readonly combobox/datebox/bandbox; the grey/dimmed treatment is reserved for `disabled`. Input is made non-typeable by ZK's `readonly` attribute, not by styling. |
| c18 | `.z-timepicker` | overflow | `hidden` | components/timepicker.md — clips the `::after` focus-ring overlay (and any child background) to the root's `border-radius` (4px) so the rounded corners read correctly. (Historically also clipped the opaque readonly input; the input is transparent now — see c15b.) |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default | `.z-timepicker` | c1, c2, c18 |
| hover | `.z-timepicker:hover` | c6 |
| focus-within | `.z-timepicker:focus-within` (ring on `::after`) | c5, c5b, c5c, c5d (M5) |
| disabled | `.z-timepicker.z-timepicker-disabled` | c4 (M4) |
| readonly | `.z-timepicker.z-timepicker-readonly .z-timepicker-input` | c15, c15b |
| inplace-blurred | `.z-timepicker.z-timepicker-inplace .z-timepicker-input` | c12 |
| inplace-button | `.z-timepicker.z-timepicker-inplace .z-timepicker-button` | c13 |
| invalid | `.z-timepicker:has(.z-timepicker-invalid)` (class is on the input child) | c14 |
| popup-default | `.z-timepicker-popup` | c7, c8, c9, c16, c17 |
| popup-option | `.z-timepicker-option` | c10 |
| popup-option-selected | `.z-timepicker-option.z-timepicker-option-selected` | c11 |

## States to evaluate
- [ ] default
- [ ] hover
- [ ] focus-within (use `:focus-within` — focus target is the inner input)
- [ ] disabled
- [ ] readonly
- [ ] inplace (blurred and focused sub-states)
- [ ] invalid (constraint violated)
- [ ] popup open — option list, selected option highlight
<!-- buttonVisible-false intentionally NOT evaluated: Timepicker extends DateTimeFormatInputElement
     (not ComboElement), so it has no setButtonVisible — the .z-timepicker-button.z-timepicker-disabled
     state is structurally unreachable. Setting buttonVisible="false" in ZUL throws a 500. The matching
     CSS rule in timepicker.css is dead code (flagged for removal). -->
