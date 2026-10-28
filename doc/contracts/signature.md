# Component: signature (theme design)
tier: T3
category: media
preview: ${PREVIEW_URL}/signature.zul
rules: see .claude/skills/zk-component-rules/components/signature.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/src/main/resources/web/js/zkmax/signature/Signature.ts
  - zkmax/src/main/resources/web/js/zkmax/signature/mold/signature.js
js-source-hash: 2cf766d6b716f41f8b1f59050191eb1dd575de3d4bde30e0dba720c2f05d941a
closest-sibling: toolbar (for the tool-button strip chrome pattern; canvas wrapper is novel)

## References
- MUI CSS: no analog — no MUI component renders a canvas-based signature capture widget
- DESIGN.md sections: §3 (color roles), §5 (spacing), §6 (shape), §7 (motion), §8 (disabled opacity), §9 (focus ring)
- Iceblue baseline: doc/contracts/baselines/signature-iceblue.png
- HTML contract: doc/contracts/signature.html

## Design Contract

The `.z-signature` root is a bordered container (1px solid `outline`, medium corner radius) with `position: relative` and a white (`surface`) background — it reads as a writing area akin to an outlined input field. The canvas layers inside are fully opaque to CSS. The toolbar strip is positioned absolute in the bottom-right corner of the root, with a comfortable inset (`spacing-2` / 8px). Each tool button is styled as a secondary tonal button: outlined border (`outline-variant`), `surface` fill at rest, with standard hover (`surface-container-high`), focus (`primary` 2px border), and active state layers. Tool buttons carry a subtle elevation (`elevation-1`) to lift them above the canvas and aid legibility. The toolbar disappears (`display: none` via `.z-signature-toolbar-hide`) transiently during pen drawing — no transition on this hide (instant is correct). When the signature widget is disabled, the root wrapper dims to `state-disabled-opacity` (0.38). Tool button labels render in label-medium typography next to a font-icon glyph.

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| r1 | `.z-signature` | position | `relative` | structural (canvas layers use absolute positioning inside) |
| r2 | `.z-signature` | border | `1px solid var(--zk-color-outline)` | DESIGN.md §9 (matches outlined input field style) |
| r3 | `.z-signature` | border-radius | `var(--zk-shape-corner-medium)` | DESIGN.md §6 |
| r4 | `.z-signature` | background-color | `var(--zk-color-surface)` | DESIGN.md §3 |
| r5 | `.z-signature` | overflow | `hidden` | structural (clip canvas border-radius) |
| r6 | `.z-signature:focus-within` | border-color | `var(--zk-color-primary)` | DESIGN.md §9 (active drawing = focus-within) |
| r7 | `.z-signature:focus-within` | border-width | `2px` | DESIGN.md §9 |
| tb1 | `.z-signature-toolbar` | position | `absolute` | structural |
| tb2 | `.z-signature-toolbar` | bottom | `var(--zk-spacing-2)` | DESIGN.md §5 |
| tb3 | `.z-signature-toolbar` | right | `var(--zk-spacing-2)` | DESIGN.md §5 |
| tb4 | `.z-signature-toolbar` | display | `flex` | layout |
| tb5 | `.z-signature-toolbar` | flex-direction | `row` | layout |
| tb6 | `.z-signature-toolbar` | gap | `var(--zk-spacing-2)` | DESIGN.md §5 (spacing between buttons) |
| tb7 | `.z-signature-toolbar` | z-index | `1` | structural (above canvas layers) |
| tb-hide | `.z-signature-toolbar-hide` | display | `none` | structural (JS toggles during drawing) |
| btn1 | `.z-signature-tool-button` | display | `inline-flex` | layout |
| btn2 | `.z-signature-tool-button` | align-items | `center` | layout |
| btn3 | `.z-signature-tool-button` | gap | `var(--zk-spacing-1)` | DESIGN.md §5 (icon ↔ label spacing) |
| btn4 | `.z-signature-tool-button` | padding | `var(--zk-spacing-1) var(--zk-spacing-3)` | DESIGN.md §5 (compact button: 4px 12px) |
| btn5 | `.z-signature-tool-button` | min-height | `32px` | DESIGN.md §5 (compact action button height) |
| btn6 | `.z-signature-tool-button` | border | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §3 (tonal outlined) |
| btn7 | `.z-signature-tool-button` | border-radius | `var(--zk-shape-corner-small)` | DESIGN.md §6 |
| btn8 | `.z-signature-tool-button` | background-color | `var(--zk-color-surface)` | DESIGN.md §3 |
| btn9 | `.z-signature-tool-button` | color | `var(--zk-color-on-surface)` | DESIGN.md §3 |
| btn10 | `.z-signature-tool-button` | font-size | `var(--zk-typescale-label-medium-size)` | DESIGN.md §4 |
| btn11 | `.z-signature-tool-button` | font-weight | `var(--zk-typescale-label-medium-weight)` | DESIGN.md §4 (pairs with btn10 label-medium typescale; resolves to 500) |
| btn12 | `.z-signature-tool-button` | box-shadow | `var(--zk-elevation-1)` | DESIGN.md §7 (lifts above canvas) |
| btn13 | `.z-signature-tool-button` | cursor | `pointer` | UX |
| btn14 | `.z-signature-tool-button` | white-space | `nowrap` | layout |
| btn15 | `.z-signature-tool-button` | transition | `background-color var(--zk-motion-duration-short2) var(--zk-motion-easing-standard), border-color var(--zk-motion-duration-short2) var(--zk-motion-easing-standard), color var(--zk-motion-duration-short2) var(--zk-motion-easing-standard)` | DESIGN.md §7 |
| btn-hover1 | `.z-signature-tool-button:hover` | background-color | `var(--zk-color-surface-container-high)` | DESIGN.md §3 (hover state layer) |
| btn-hover2 | `.z-signature-tool-button:hover` | border-color | `var(--zk-color-outline)` | DESIGN.md §3 |
| btn-focus1 | `.z-signature-tool-button:focus-visible` | outline | `2px solid var(--zk-color-primary)` | DESIGN.md §9 |
| btn-focus2 | `.z-signature-tool-button:focus-visible` | outline-offset | `2px` | DESIGN.md §9 |
| btn-active1 | `.z-signature-tool-button:active` | background-color | `var(--zk-color-surface-container)` | DESIGN.md §3 (pressed) |
| icon1 | `.z-signature-tool-button-icon` | font-size | `16px` | DESIGN.md §4 (icon inside compact button) |
| icon2 | `.z-signature-tool-button-icon` | line-height | `1` | layout |
| lbl1 | `.z-signature-tool-button-label:not(:empty)` | margin-left | `var(--zk-spacing-1)` | DESIGN.md §5 (conditional: label present) |
| dis1 | `.z-signature[disabled]` | opacity | `var(--zk-state-disabled-opacity)` | DESIGN.md §8 |
| dis2 | `.z-signature[disabled]` | pointer-events | `none` | DESIGN.md §8 |

## State matrix

| state | selector | property ids |
|-------|----------|--------------|
| root default | `.z-signature` | r1, r2, r3, r4, r5 |
| root focus-within | `.z-signature:focus-within` | r6, r7 |
| toolbar default | `.z-signature-toolbar` | tb1, tb2, tb3, tb4, tb5, tb6, tb7 |
| toolbar hidden | `.z-signature-toolbar-hide` | tb-hide |
| tool-button default | `.z-signature-tool-button` | btn1–btn15 |
| tool-button hover | `.z-signature-tool-button:hover` | btn-hover1, btn-hover2 |
| tool-button focus | `.z-signature-tool-button:focus-visible` | btn-focus1, btn-focus2 |
| tool-button active | `.z-signature-tool-button:active` | btn-active1 |
| icon | `.z-signature-tool-button-icon` | icon1, icon2 |
| label (when present) | `.z-signature-tool-button-label:not(:empty)` | lbl1 |

## States to evaluate
- [ ] default (empty canvas, wrapper bordered, toolbar visible bottom-right)
- [ ] focus-within (user clicked/tabbed into canvas — wrapper shows primary 2px border)
- [ ] toolbar-hide (`.z-signature-toolbar-hide` present — toolbar not visible)
- [ ] tool-button default
- [ ] tool-button hover
- [ ] tool-button focus-visible
- [ ] tool-button active

> Not evaluatable: the **disabled** state is **unreachable from ZUL** — `Signature` (zkmax) extends `XulElement` and defines no `setDisabled`, so `disabled="true"` throws an HTTP 500 at compose time. The `dis1`/`dis2` CSS rows below are retained for reference only (would apply if the `[disabled]` DOM state were ever produced by a client API).
- [ ] label-present vs label-empty (check `:not(:empty)` margin behavior)

## T3 wrapper boundary

wrapper-selectors:
  - .z-signature
  - .z-signature-toolbar
  - .z-signature-tool-button
  - .z-signature-tool-button-icon
  - .z-signature-tool-button-label
  - .z-signature-toolbar-hide

forbidden-selectors:
  - canvas                       # belt-and-suspenders

theme-bridge:
  # signature_pad reads penColor and backgroundColor from JS constructor options, NOT CSS.
  # These cannot be driven by CSS custom properties without modifying Signature.ts.
  # Escalate to ESCALATED_LIBRARY_CONFIG if pen/background color must change.
  available: false
