# Component: slider (theme design)
tier: T1
category: input
preview: ${PREVIEW_URL}/slider.zul
rules: see .claude/skills/zk-component-rules/components/slider.md
contract-approved: true  # user-approved 2026-06-04 (D1–D5 deviations accepted; N1/N2 audit suggestions applied as c8b/c8c/c10a)
zk-version: 10.2.1-jakarta
js-source-files:
  - zk/zul/src/main/resources/web/js/zul/inp/Slider.ts
  - zk/zul/src/main/resources/web/js/zul/inp/mold/slider.js
  - zkcml/zkmax/src/main/resources/web/js/zkmax/slider.ts
js-source-hash: 06a9ba560453956c695f9db5aac48400af8cb797353c758f41fcb08e1097c40d
closest-sibling: none (linear slider is a simple track+thumb; knob mold is novel PE rotary)
mockup-needed: Y
mockup-rationale: ZKDoc has no canonical knob-mold image that shows Marble token mapping; knob arc colors diverge from ZKDoc iceblue-style defaults and need a visual target for the CSS generator.

## References
- MUI CSS: /Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/Inputs/Slider.css
- DESIGN.md sections: §2 (primary color), §3 (state layers), §9 (inputs), §11 (outlines)
- ZKDoc canonical (default/sphere/scale molds): /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/ZKComRef_Slider.png
- ZKDoc canonical (knob 360°): /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/Knob360.png
- ZKDoc canonical (knob 270°): /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/Knob270.png
- Iceblue baseline: doc/contracts/baselines/slider-iceblue.png
- HTML contract: doc/contracts/slider.html

## Design Contract

Material-styled linear sliders use primary-colored fill (`.z-slider-area`) against an outline-variant track (`.z-slider-center`). The thumb (`.z-slider-button`) is a filled primary circle; on hover and focus-visible, a semi-transparent primary state-layer ring (8px spread, 16% alpha via `color-mix`) expands outward via `box-shadow`; on active/drag the ring expands to 14px spread at the same 16% alpha. Disabled state uses `opacity: var(--zk-state-disabled-opacity)` on the root. The sphere mold (root class `.z-slider-sphere`) applies a radial-gradient on `.z-slider-button` for a 3D sphere effect; ZK confirms `.z-slider-sphere` is added to the root by `domClass_()`. The scale mold (root class `.z-slider-scale`) inherits all the above and adds tick marks via background-image (CSS-DSP restriction means no external image refs; tick styling uses a repeating-linear-gradient data pattern or is deferred). The popup tooltip (`.z-slider-popup`) uses inverse-surface background with inverse-on-surface text, 4px corner, elevation-1 shadow.

The knob mold (PE) renders a rotary SVG dial. The **background arc** (`.z-slider-knob-inner path`) uses `stroke: var(--zk-color-outline-variant)` — the inert ring. The **progress arc** (`.z-slider-knob-area path`) uses `stroke: var(--zk-color-primary)`. Both `stroke-width` and arc geometry are JS-controlled via inline SVG attributes and are NOT CSS-themable (T3 boundary for internal geometry). The numeric input overlay (`.z-slider-input`) uses surface-container background, outline border, extra-small corner radius, and primary-colored bold text — **centered**, with the native number-input spin buttons suppressed (`appearance: textfield` + `::-webkit-*-spin-button: none`, matching iceblue; MD3/MUI never show native spinner chrome). No hover state ring exists for the knob (SVG drag-based, not thumb-based).

## Outcome assertions

visual-goal: simple input — M-rows for track+thumb geometry and knob SVG visibility

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-slider-area` bbox.width > 0 AND `.z-slider-button` bbox.width ≥ 12px AND `.z-slider-button` bbox.height ≥ 12px | thumb exists and fills have measurable size — slider is functional |
| M2 | `.z-slider-button` `border-radius` computed value = `50%` OR equals half of `width` | thumb is circular per MD3 slider spec |
| M3 | `.z-slider-area` background-color ≠ `.z-slider-center` background-color | filled portion is visually distinct from the track — readable as a progress indicator |
| M4 | `.z-slider.z-slider-knob` `.z-slider-knob-svg` bbox.width ≥ 100px AND bbox.height ≥ 100px (when knob mold rendered) | SVG dial is large enough to be interactive and visible |
| M5 | `.z-slider-knob-inner` `stroke` ≠ `.z-slider-knob-area` `stroke` (on SVG path elements, when knob mold rendered) | background arc and progress arc are visually distinguished |

## Expected values

### Default / sphere / scale molds (linear slider)

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-slider-center` | background-color | `var(--zk-color-outline-variant)` | DESIGN.md §11 — track is inert outline |
| c2 | `.z-slider-area` | background-color | `var(--zk-color-primary)` | DESIGN.md §2 — filled portion uses primary |
| c3 | `.z-slider-button` | background-color | `var(--zk-color-primary)` | DESIGN.md §2 — thumb uses primary |
| c4 | `.z-slider-button` | border-radius | `50%` | MD3 slider spec — circular thumb |
| c5 | `.z-slider-button:hover` | box-shadow | `0 0 0 8px color-mix(in srgb, var(--zk-color-primary) 16%, transparent)` | MUI Slider.css line 184 — 16% alpha (not MD3 8%) at 8px spread; see Accepted deviations |
| c6 | `.z-slider-button:focus-visible` | box-shadow | `0 0 0 8px color-mix(in srgb, var(--zk-color-primary) 16%, transparent)` | MUI Slider.css line 184 — same ring for hover+focus per MUI; see Accepted deviations |
| c7 | `.z-slider[disabled]` | opacity | `var(--zk-state-disabled-opacity)` | DESIGN.md §3 — disabled uses opacity |
| c8 | `.z-slider-popup` | background-color | `var(--zk-color-inverse-surface)` | DESIGN.md §9 — tooltip uses inverse surface |
| c8a | `.z-slider-popup` | color | `var(--zk-color-inverse-on-surface)` | MUI pattern — on-inverse companion for readability |
| c8b | `.z-slider-popup` | border-radius | `var(--zk-shape-corner-extra-small)` | DESIGN.md §6 — small floating chip uses extra-small corner |
| c8c | `.z-slider-popup` | box-shadow | `var(--zk-elevation-1)` | DESIGN.md §9 — floating tooltip lifts with elevation-1 |
| c9 | `.z-slider-button:active` | box-shadow | `0 0 0 14px color-mix(in srgb, var(--zk-color-primary) 16%, transparent)` | MUI Slider.css line 193 — Mui-active uses 14px spread at 16%; ZK has no drag class so `:active` is the fallback |
| c10 | `.z-slider-sphere .z-slider-button` | background | `radial-gradient(circle at 35% 35%, var(--zk-color-primary-container), var(--zk-color-primary) 60%, var(--zk-color-on-primary-container) 100%)` | sphere mold: `.z-slider-sphere` root class confirmed in Slider.ts `domClass_()` lines 315-316 |
| c10a | `.z-slider-sphere .z-slider-button` | width / height | `20px` (same as default thumb) | iceblue baseline — sphere thumb matches default thumb size; no size override |

### Knob mold (PE) — CSS-themable properties only

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| k1 | `.z-slider-knob-inner` | stroke | `var(--zk-color-outline-variant)` | DESIGN.md §11 — background arc is inert outline |
| k2 | `.z-slider-knob-area` | stroke | `var(--zk-color-primary)` | DESIGN.md §2 — progress arc uses primary |
| k3 | `.z-slider-input` | background-color | `var(--zk-color-surface-container)` | DESIGN.md §9 — input field on surface |
| k4 | `.z-slider-input` | border | `1px solid var(--zk-color-outline)` | DESIGN.md §11 — standard input border |
| k5 | `.z-slider-input` | color | `var(--zk-color-primary)` | design decision — numeric value echoes primary |
| k6 | `.z-slider-input` | text-align | `center` | iceblue baseline + approved mockup — value centered in the JS-sized box |
| k7 | `.z-slider-input` | appearance | `textfield`; AND `::-webkit-outer-spin-button` / `::-webkit-inner-spin-button` have `-webkit-appearance: none; margin: 0` | iceblue suppresses native spinners; MD3/MUI show no native spinner chrome — the JS box is sized from digit count, so reserved spinner space de-centers the value |
| k8 | `.z-slider-input` | border-radius | `var(--zk-shape-corner-extra-small)` | approved mockup slider.html — small field corner |

### Knob mold — NOT CSS-themable (document the boundary)

These properties are controlled by JS inline styles/SVG attributes and cannot be overridden by CSS:

| property / attribute | set by | notes |
|---------------------|--------|-------|
| `stroke-width` on `.z-slider-knob-inner` / `.z-slider-knob-area` | `_getStrokeWidth()` → inline SVG attribute | defaults to 10px; controlled by `strokeWidth` ZK property |
| `d` attribute on both `<path>` elements | `_calcPath()` → inline SVG attribute | arc geometry; controlled by `angleArc`, `curpos`, `minpos`, `maxpos` |
| `.z-slider-input` `top`, `left`, `width`, `height`, `font-size` | `_getInputProperty()` → inline CSS | all computed from root dimensions and digit count |
| root width/height (default 200×200px) | `bind_()` → `node.width(DEFAULT_SIZE)` | can be overridden by explicit ZUL `width=`/`height=`; changing via CSS triggers `rerender()` |

## State matrix

### Linear molds (default / sphere / scale)

| state | selector | properties to check |
|-------|----------|---------------------|
| default | `.z-slider` | c1, c2, c3, c4 |
| thumb hover | `.z-slider-button:hover` | c5 |
| thumb focus | `.z-slider-button:focus-visible` | c6 |
| thumb active/drag | `.z-slider-button:active` | c9 |
| disabled | `.z-slider[disabled]` | c7 |
| popup/dragging | `.z-slider-popup` | c8, c8a, c8b, c8c |
| sphere mold | `.z-slider-sphere .z-slider-button` | c10, c10a |

### Knob mold (PE)

| state | selector | properties to check |
|-------|----------|---------------------|
| default | `.z-slider` (knob mold) | k1, k2, k3, k4, k5, k6, k7, k8, M4, M5 |

## States to evaluate
- [ ] default (horizontal)
- [ ] default (vertical)
- [ ] sphere mold (`.z-slider-sphere` root class)
- [ ] scale mold (`.z-slider-scale` root class)
- [ ] hover (thumb) — c5 ring at 8px/16%
- [ ] focus-visible (thumb) — c6 ring at 8px/16%
- [ ] active/drag (thumb) — c9 ring at 14px/16%
- [ ] disabled
- [ ] dragging (popup visible) — c8 + c8a + c8b + c8c
- [ ] knob mold default (PE — skip if not PE+ build)

## Accepted MD3 deviations

The following are intentional divergences from raw MD3 spec. They are documented here so Gate 2 evaluators do not flag them on every iteration. **Approved by the user at the contract-approval gate, 2026-06-04.**

| # | topic | MD3 spec | MUI visual target | Marble choice | rationale |
|---|-------|----------|-------------------|---------------|-----------|
| D1 | Thumb ring opacity (hover) | 8% (`--zk-state-hover-opacity`) | 16% (MUI Slider.css line 184: `rgba(25,118,210,0.16)`) | **16%** via `color-mix` | MUI diverges from MD3 here specifically because 8% is too faint at the 8px spread radius against a white background. Marble follows MUI visual target per project policy. |
| D2 | Thumb ring opacity (focus-visible) | 12% (`--zk-state-focus-opacity`) | 16% same selector as hover (MUI Slider.css line 184) | **16%** via `color-mix` | MUI uses same 16% for focus as hover — simpler ring UX. Marble follows MUI. |
| D3 | Track (rail) background mechanism | M3: use `--zk-color-outline-variant` directly | MUI uses `.MuiSlider-rail { opacity: 0.38 }` over the primary container (Slider.css line 98) | **`var(--zk-color-outline-variant)`** directly (rgba 0.12 tone) | `outline-variant` is semantically correct for an inert track; avoids a relative opacity + extra element dependency. Visual result is similar lightness. |
| D4 | Popup (valueLabel) background color | M3: inverse-surface | MUI uses `#757575` (grey, Slider.css lines 365–382) | **`var(--zk-color-inverse-surface)`** (#2d3748, dark blue-grey) | Inverse-surface provides better on-brand contrast; the MUI grey is a Material v1/v2 legacy holdover not used in MD3. |
| D5 | Root padding | — | MUI root padding 13px | **12px** (`--zk-spacing-3`) | 1px sub-pixel UX difference; 12px stays on the 4dp spacing grid. Accepted at the 2026-06-04 approval gate (audit finding F9) — not contracted, do not re-flag. |
