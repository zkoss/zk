# Component: daterangebox (theme design)
tier: T2
category: input
preview: ${PREVIEW_URL}/daterangebox.zul
rules: see .claude/skills/zk-component-rules/components/daterangebox.md
contract-approved: true
zk-version: 10.4.0-jakarta
js-source-files:
  - zkcml/zkmax/src/main/resources/web/js/zkmax/db/DaterangePopup.ts
  - zkcml/zkmax/src/main/resources/web/js/zkmax/db/Daterangebox.ts
  - zkcml/zkmax/src/main/resources/web/js/zkmax/db/mold/daterangebox.js
js-source-hash: d7a2c4c16983baa5501152e3799fb8bba4e451c3d48b070d718e2e041503d04c
closest-sibling: combo-trio (root input+button chrome) + calendar (popup panels and range-highlight cells)
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/db/css/daterangebox.css
mockup-needed: N
mockup-rationale: user explicitly opted out of the HTML mockup for this pass (scope override — see spec-author invocation 2026-07-17). No dedicated ZKCompRef_<Component>.png exists yet either (daterangebox is new in ZK 10.4.0; the only ZKDoc trace is an inline example image inside calendar.md). Until a mockup is authored, the Design Contract prose + Outcome assertions table below are the sole binding visual reference.

## References
- MUI CSS: no direct analog — MUI's date-range picker (`DateRangePicker`) ships only in the separate, paid MUI X package and is not present in `static-css-output/`. Reuse `Inputs/OutlinedInput.css` for the root chrome (same reference already used by combobox/datebox/timepicker) and the existing in-theme dropdown-popup convention (`reference/popup-tokens.md`, `reference/floating-popup-in-body.md`) for the detached popup surface.
- DESIGN.md sections: §1 (Surface Palette), §3 (Brand colors), §4 (Spacing scale), §5 (Corner radii), §6 (Elevation), §7 (Typography), §8 (State-layer overlays), §9 (Motion), §10 (Density), §11 (Border rules)
- Sibling contracts: doc/contracts/combobox.md, doc/contracts/datebox.md, doc/contracts/timepicker.md, doc/contracts/calendar.md
- Iceblue baseline: not captured (component is new in ZK 10.4.0; out of scope for this pass per explicit user instruction)
- ZKDoc: no dedicated page; referenced only as an inline example image inside `/Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/calendar.md` (`dateRangeSelector.png`)
- HTML contract: none (mockup-needed: N)

## Design Contract

The daterangebox root reads as one continuous outlined input field, matching the combo-trio family: `1px solid --zk-color-outline` border, `--zk-shape-corner-extra-small` (4px) radius, `--zk-color-surface` background, and a min-height matched to the combo-trio input row (~39–40px, DESIGN.md §10) so a row with `buttonVisible="false"` sits at the same pitch as one with the icon. Both inputs share `--zk-typescale-body-medium-size` (13px) text, centered, on a transparent background so only the root shows the border. Each input is **content-fitted** (`field-sizing: content` + a `min-width` date floor, c6a/c6b, M11) rather than left at the browser-default `size=20` (~20ch) width — otherwise the short centered date sits in the middle of a ~172px box with large symmetric gutters, making the field roughly twice as wide as datebox; with content-fitting the two dates + separator + trigger read as one compact field close to datebox's footprint (`text-align: center` is retained but becomes visually neutral once the box hugs its content). The separator glyph and the trigger icon both use `--zk-color-on-surface-variant`; the trigger brightens to `--zk-color-primary` on hover, matching the datebox/timepicker icon-button convention. On `:hover` the root border brightens to `--zk-color-on-surface` (DESIGN.md §11). The focused state (ZK's own `.z-daterangebox-focused` class — not `:focus-within`, since it must stay applied while the user interacts with the body-detached popup) keeps the border at `1px` and adds a primary inset ring (`box-shadow: inset 0 0 0 1px --zk-color-primary`) — Mechanism A from `reference/focus-affordance-no-layout-shift.md`; a `border-width: 2px` bump would force the two min-height-pinned inputs to grow the root vertically. No `::after` overlay indirection is needed here (unlike timepicker's historical case) because both inputs stay transparent-background in every state, so a direct root-level inset ring is never occluded. The invalid state (`.z-daterangebox-invalid`) tints the border `--zk-color-error`; combined with focus, the inset ring switches to error too (never a mixed primary/error look). The disabled state applies `opacity: --zk-state-disabled-opacity` (0.38) to the whole root plus `cursor: not-allowed`, consistent with timepicker's disabled treatment. Readonly is a **narrower** state than in the combo-trio siblings: because this widget's `_openPopup()` explicitly refuses to open while readonly (unlike timepicker's post-ZK-6122 "readonly stays interactive" convention), the calendar trigger icon is genuinely non-functional here — it is dimmed to `--zk-state-disabled-opacity` while the two inputs themselves stay full-opacity, transparent-background, `cursor: default` (value is legible and selectable, just not editable — the honest "read-only display" reading, distinct from the fully-dimmed `disabled` state).

The detached popup follows the canonical dropdown-popup recipe already established for combobox/datebox/timepicker (`reference/popup-tokens.md`, `reference/floating-popup-in-body.md`): `position: absolute`, `--zk-color-surface` background, `--zk-elevation-dropdown` shadow, `--zk-shape-menu` (4px) radius, no border (the shadow reads as the edge), `z-index: 1700` (aligned with the datebox/calendar family, one tier above the plain combobox/bandbox dropdown's 1600, since this popup itself hosts Calendar sub-widgets). The panels grid and the times grid both use `gap: --zk-spacing-3` (12px) between columns, consuming the widget-set `--panels` custom property for column count (see `components/daterangebox.md` — this mechanism is structural, not themeable). The footer is a `1px solid --zk-color-outline-variant` top divider with `--zk-spacing-3` (12px) padding on all sides; Clear and Cancel are visually **outlined** buttons (transparent background, `1px solid --zk-color-outline`, `--zk-color-on-surface` text, `::before` state-layer overlay at `--zk-color-primary` tint on hover/focus/press — mirroring `.z-button-outlined`'s recipe even though these are plain `<button>` elements, not ZK `<button>` widgets) while Today is visually **filled-primary** (`--zk-color-primary` background, `--zk-color-on-primary` text, `::before` state-layer overlay at `--zk-color-on-primary` tint) — Today is the single-click shortcut CTA and should read as the primary action the same way a MD3 filled button signals "the recommended next step," while Clear/Cancel are secondary/tertiary actions. All three footer buttons share the input family's typography (`--zk-typescale-body-medium-size`) and `--zk-shape-corner-extra-small` (4px) radius for visual family consistency with the root input. Range-highlight cells inside the popup's Calendar panels (`.z-cell-range-begin` / `-end` / `-mid` / `-preview-mid` / `-preview-end` — see `components/calendar.md`) render as one visually continuous tinted band: begin/end cells use `--zk-color-primary` fill with `--zk-color-on-primary` text (matching the plain `.z-calendar-selected` treatment elsewhere in the theme), mid cells use a lighter `--zk-color-primary-container` fill with square (non-rounded) inner edges so adjacent mid cells visually fuse into one band, and the two preview variants repeat the same treatment at reduced opacity (`--zk-state-hover-opacity`-scaled) so a not-yet-committed hover preview reads as tentative rather than committed.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-daterangebox` has visible framing: `border-width ≥ 1px AND border-color ≠ transparent` | root must read as one continuous outlined field, not two floating inputs |
| M2 | `.z-daterangebox` bbox height ≥ 28px | minimum usable touch/click target for the composite row |
| M3 | both `.z-daterangebox-input` bboxes AND `.z-daterangebox-separator` bbox AND (if present) `.z-daterangebox-button` bbox are fully contained within `.z-daterangebox`'s bbox (±1px) | no child overflows the outlined root — reads as one field, not clipped or spilling content |
| M4 | `.z-daterangebox.z-daterangebox-disabled` has `opacity < 0.5` OR its input children have text `color` with effective alpha < 0.5 | disabled must be visually distinguishable from default at a glance |
| M5 | `.z-daterangebox` bbox height when `.z-daterangebox-focused` is applied == bbox height at rest (±0px) | the focus ring is an inset box-shadow, not a border-width increase — no layout jitter when the user tabs into either input |
| M6 | inside an open popup, every `.z-calendar` panel under `.z-daterangebox-popup-panels` has its bbox.top within ±2px of every sibling panel's bbox.top | panels sit in one row (desktop breakpoint), not wrapped or staggered |
| M7 | inside an open popup's footer, the present buttons among `.z-daterangebox-popup-clear` / `-today` / `-cancel` do not overlap each other's bbox by > 1px on either axis, AND `.z-daterangebox-popup-cancel`'s bbox.right is within 4px of the footer's own bbox.right (rightmost slot) | the 1–3 slot footer row docks left-to-right without collision, with Cancel always anchored to the trailing edge |
| M8 | when `.z-daterangebox-popup-today` is present alongside `.z-daterangebox-popup-clear` and/or `-cancel`, at least one of `{background-color, border-color}` differs between Today and the other footer button(s) | Today (the one-click shortcut) must read as visually distinct from the secondary Clear/Cancel actions |
| M9 | with a committed range spanning ≥ 2 visible day cells in the same panel, every `.z-calendar-cell.z-cell-range-mid` between the begin and end cell has a bbox directly adjacent (touching, 0px gap ±1px) to its neighboring range cell, AND the begin/end cells' fill color differs from the mid cells' fill color | the highlighted range must read as one continuous band, with the two endpoints visually distinguished from the interior — not a row of disconnected dots |
| M10 | `.z-daterangebox-popup` bbox is fully within the viewport bbox (0 ≤ top, left AND right ≤ innerWidth AND bottom ≤ innerHeight), each ±2px | the JS position-clamp (see `components/daterangebox.md`) must not be defeated by a CSS width/height that overflows the clamped box |
| M11 | with a seeded value, each `.z-daterangebox-input` bbox width < 130px (content-fitted — NOT the UA-default `size=20` ≈ 172px measured) | the two inputs carry no `size`/`width` (see `components/daterangebox.md`); left unbounded they render at ~20ch and, with centered text, a short date sits in the middle with large symmetric gutters, making the field ~2× datebox width. Each input must hug its date content instead. (gap 2026-07-20) |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-daterangebox` | border | `1px solid var(--zk-color-outline)` | DESIGN.md §11 — component default outline |
| c2 | `.z-daterangebox` | border-radius | `var(--zk-shape-corner-extra-small)` | DESIGN.md §5 — inputs use 4px |
| c3 | `.z-daterangebox` | background-color | `var(--zk-color-surface)` | DESIGN.md §1 — input background |
| c4 | `.z-daterangebox` | min-height | 39–40px | DESIGN.md §10 — matches combo-trio input row height (see combobox.md c1) |
| c5 | `.z-daterangebox-input` | font-size | `var(--zk-typescale-body-medium-size)` (13px) | DESIGN.md §7 — input/placeholder text |
| c6 | `.z-daterangebox-input` | background-color | `transparent` | root supplies the only visible surface; avoids double-fill and keeps the focus ring (c12) unoccluded |
| c6a | `.z-daterangebox-input` | field-sizing | `content` | the input hugs its date text instead of the UA-default `size=20` (~20ch/172px) intrinsic width — kills the large symmetric gutters (M11). Degrades gracefully: browsers without `field-sizing` fall back to the prior ~20ch look (no regression) |
| c6b | `.z-daterangebox-input` | flex + min-width | `flex: 0 1 auto` AND `min-width` sized to fit the default date (`~6.5em`) | `flex: 0 1 auto` stops the input growing past its content if the root is ever stretched; the `min-width` floor keeps an empty input a sensible click target and holds a normal-length date without per-keystroke resize jitter |
| c7 | `.z-daterangebox-separator` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §2 — secondary/muted text |
| c8 | `.z-daterangebox-button` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §2 — icon at rest matches muted text |
| c9 | `.z-daterangebox-button:hover` | color | `var(--zk-color-primary)` | matches datebox/timepicker trigger-icon hover convention |
| c10 | `.z-daterangebox:hover` | border-color | `var(--zk-color-on-surface)` | DESIGN.md §11 — input hover border |
| c11 | `.z-daterangebox.z-daterangebox-focused` | border-color | `var(--zk-color-primary)` | DESIGN.md §11 — focus tint |
| c12 | `.z-daterangebox.z-daterangebox-focused` | box-shadow | `inset 0 0 0 1px var(--zk-color-primary)` | `reference/focus-affordance-no-layout-shift.md` — Mechanism A ring |
| c13 | `.z-daterangebox.z-daterangebox-focused` | border-width | `1px` (must NOT become 2px) | same reference — the layout-shift trap |
| c14 | `.z-daterangebox.z-daterangebox-invalid` | border-color | `var(--zk-color-error)` | DESIGN.md §3 — error/invalid uses error color |
| c15 | `.z-daterangebox.z-daterangebox-invalid.z-daterangebox-focused` | box-shadow | `inset 0 0 0 1px var(--zk-color-error)` | invalid + focus combine to the error-colored ring, never a mixed primary/error look |
| c16 | `.z-daterangebox.z-daterangebox-disabled` | opacity | `var(--zk-state-disabled-opacity)` (0.38) | DESIGN.md §8 — disabled content opacity |
| c17 | `.z-daterangebox.z-daterangebox-disabled` | cursor | `not-allowed` | standard disabled affordance |
| c18 | `.z-daterangebox.z-daterangebox-readonly .z-daterangebox-input` | cursor | `default` | readonly is legible/selectable but not editable — not the `pointer` affordance used by timepicker's readonly (that widget still opens its popup readonly; this one does not — see Design Contract) |
| c19 | `.z-daterangebox.z-daterangebox-readonly .z-daterangebox-input` | background-color | `transparent` | readonly must NOT read as fully disabled (DESIGN.md §8/§3 disabled-vs-readonly distinction) |
| c20 | `.z-daterangebox.z-daterangebox-readonly .z-daterangebox-button` | opacity | `var(--zk-state-disabled-opacity)` (0.38) | the trigger icon genuinely does nothing while readonly (popup refuses to open) — dim only the non-functional icon, not the whole field |
| c21 | `.z-daterangebox-popup` | position | `absolute` | `reference/floating-popup-in-body.md` — ZK sets inline left/top only; position must be supplied |
| c22 | `.z-daterangebox-popup` | z-index | `1700` | aligns with the datebox/calendar popup family (one tier above the plain 1600 dropdown, since this popup hosts Calendar sub-widgets) |
| c23 | `.z-daterangebox-popup` | background-color | `var(--zk-color-surface)` | `reference/popup-tokens.md` — all floating popups share this surface token |
| c24 | `.z-daterangebox-popup` | box-shadow | `var(--zk-elevation-dropdown)` | DESIGN.md §6 — level 2, shared dropdown elevation |
| c25 | `.z-daterangebox-popup` | border-radius | `var(--zk-shape-menu)` | DESIGN.md §5 — shared menu/dropdown radius (4px) |
| c26 | `.z-daterangebox-popup` | border | `none` | elevation shadow defines the edge, matching timepicker/combobox popups |
| c27 | `.z-daterangebox-popup-panels`, `.z-daterangebox-popup-times` | gap | `var(--zk-spacing-3)` (12px) | DESIGN.md §4 — spacing scale |
| c28 | `.z-daterangebox-popup-footer` | border-top | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §11 — divider token |
| c29 | `.z-daterangebox-popup-footer` | padding | `var(--zk-spacing-3)` (12px) | DESIGN.md §4 |
| c30 | `.z-daterangebox-popup-clear`, `.z-daterangebox-popup-cancel` | background-color | `transparent` | outlined-button treatment — secondary actions |
| c31 | `.z-daterangebox-popup-clear`, `.z-daterangebox-popup-cancel` | border | `1px solid var(--zk-color-outline)` | outlined-button treatment |
| c32 | `.z-daterangebox-popup-clear`, `.z-daterangebox-popup-cancel` | color | `var(--zk-color-on-surface)` | outlined-button treatment |
| c33 | `.z-daterangebox-popup-today` | background-color | `var(--zk-color-primary)` | Today is the primary/recommended shortcut action — filled treatment |
| c34 | `.z-daterangebox-popup-today` | color | `var(--zk-color-on-primary)` | pairs with c33 |
| c35 | `.z-daterangebox-popup-clear`, `-today`, `-cancel` | border-radius | `var(--zk-shape-corner-extra-small)` | DESIGN.md §5 — matches root input radius for family consistency |
| c36 | `.z-daterangebox-popup-clear`, `-today`, `-cancel` | font-size | `var(--zk-typescale-body-medium-size)` | DESIGN.md §7 |
| c37 | `.z-calendar-cell.z-cell-range-begin`, `.z-cell-range-end` | background-color | `var(--zk-color-primary)` | endpoint cells match the theme's existing `.z-calendar-selected` treatment |
| c38 | `.z-calendar-cell.z-cell-range-begin`, `.z-cell-range-end` | color | `var(--zk-color-on-primary)` | pairs with c37 |
| c39 | `.z-calendar-cell.z-cell-range-mid` | background-color | `var(--zk-color-primary-container)` | interior band — lighter than the endpoints so begin/end still read as distinct |
| c40 | `.z-calendar-cell.z-cell-range-preview-mid`, `.z-cell-range-preview-end` | opacity (of the range fill) | scaled by `var(--zk-state-hover-opacity)` relative to the committed equivalent | a hover preview must read as tentative, not committed |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default | `.z-daterangebox` | c1, c2, c3, c4 (M1, M2, M3) |
| hover | `.z-daterangebox:hover` | c10 |
| focused | `.z-daterangebox.z-daterangebox-focused` | c11, c12, c13 (M5) |
| invalid | `.z-daterangebox.z-daterangebox-invalid` | c14 |
| invalid + focused | `.z-daterangebox.z-daterangebox-invalid.z-daterangebox-focused` | c15 |
| disabled | `.z-daterangebox.z-daterangebox-disabled` | c16, c17 (M4) |
| readonly | `.z-daterangebox.z-daterangebox-readonly` | c18, c19, c20 |
| trigger default | `.z-daterangebox-button` | c8 |
| trigger hover | `.z-daterangebox-button:hover` | c9 |
| popup default | `.z-daterangebox-popup` | c21–c26 |
| popup panels/times | `.z-daterangebox-popup-panels`, `.z-daterangebox-popup-times` | c27 (M6) |
| popup footer | `.z-daterangebox-popup-footer` | c28, c29 (M7) |
| footer clear/cancel | `.z-daterangebox-popup-clear`, `.z-daterangebox-popup-cancel` | c30, c31, c32, c35, c36 |
| footer today | `.z-daterangebox-popup-today` | c33, c34, c35, c36 (M8) |
| range begin/end | `.z-cell-range-begin`, `.z-cell-range-end` | c37, c38 (M9) |
| range mid | `.z-cell-range-mid` | c39 (M9) |
| range preview | `.z-cell-range-preview-mid`, `.z-cell-range-preview-end` | c40 |

## States to evaluate
- [ ] default
- [ ] hover (root border)
- [ ] focused (`.z-daterangebox-focused` — tab or click into either input)
- [ ] invalid (type unparseable text into either input)
- [ ] invalid + focused (combined ring)
- [ ] disabled
- [ ] readonly (confirm popup does NOT open — differs from timepicker's readonly)
- [ ] buttonVisible-false at initial render (button element absent from DOM entirely)
- [ ] buttonVisible-false toggled at runtime (button element present but inline `display:none` — no class involved; see `components/daterangebox.md`)
- [ ] popup open — default (numberOfMonths=2)
- [ ] popup open — numberOfMonths=1 and numberOfMonths=3+ (panel grid column count tracks `--panels`)
- [ ] popup open — showTime=true (times row visible, 2× `.z-timebox`)
- [ ] popup open — allowEmpty="none" (Clear button absent from footer)
- [ ] popup open — showTodayLink=true (Today button present, filled-primary)
- [ ] range highlight — committed begin+end spanning several days in one panel (continuous mid band)
- [ ] range highlight — hover preview with only begin committed and hoverPreview=true (preview-mid/preview-end, reduced-opacity band)
- [ ] narrow viewport (<576px) — full-screen modal popup layout (panels stack to one column, footer pinned to bottom)
