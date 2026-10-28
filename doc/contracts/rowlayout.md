# Component: rowlayout (theme design)
tier: T1
category: layout
preview: ${PREVIEW_URL}/rowlayout.zul
rules: see .claude/skills/zk-component-rules/components/rowlayout.md
contract-approved: true
zk-version: 10.2.1-jakarta
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/css/rowlayout.css
js-source-files:
  - zkmax/src/main/resources/web/js/zkmax/layout/Rowlayout.ts
  - zkmax/src/main/resources/web/js/zkmax/layout/Rowchildren.ts
  - zkmax/src/main/resources/web/js/zkmax/layout/mold/rowlayout.js
  - zkmax/src/main/resources/web/js/zkmax/layout/mold/rowchildren.js
js-source-hash: c76cc794679e2e209e45cf59587099efebb0a91f14db9b742204ed149700c0d0
closest-sibling: columnlayout
mockup-needed: N
mockup-rationale: ZKDoc canonical image ZKComRef_Rowlayout.PNG exists and the Marble contract is a minimal token-mapping of the stock layout (no color/surface/shape divergence from ZKDoc default).

## References
- MUI CSS: Layout/Grid.css — `.MuiGrid-root { min-width: 0; box-sizing: border-box; }` — confirms column cells need `box-sizing: border-box`; no additional MUI guidance (MUI Grid uses CSS Grid, not float columns)
- DESIGN.md sections: §4 (spacing scale for gutter sizing)
- Iceblue baseline: doc/contracts/baselines/rowlayout-iceblue.png
- ZKDoc canonical: /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/ZKComRef_Rowlayout.PNG
- HTML contract: doc/contracts/rowlayout.html (not generated — mockup-needed: N)

## Design Contract

`rowlayout` is a purely structural 12-column-grid layout primitive. The container (`.z-rowlayout`) carries no visible surface: no background fill, no border, no shadow. Column cells (`.z-rowchildren`) are similarly transparent containers — their visible content and framing comes entirely from the child widgets placed inside them. The only theme-owned structural concerns are:

1. **Float clearfix** — `.z-rowlayout` must collapse its floated children via a clearfix; without it the container renders at zero height.
2. **Float model** — `.z-rowchildren[class*="colspan"]` must be `display: block; float: left; box-sizing: border-box` so JS-computed percentage widths and margin-left gutters work correctly.
3. **Responsive stacking** — at `max-width: 767px` the float collapses to `float: none` so cells stack single-column. No other breakpoint is defined.
4. **No decoration** — no background-color, border, border-radius, shadow, or padding is applied to `.z-rowlayout` or `.z-rowchildren`. Any visual decoration is the responsibility of child widgets.

The Marble theme reproduces the stock ZK layout rules verbatim and adds nothing. This is intentional: a grid primitive should be visually invisible; content widgets carry their own surface styling.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-rowlayout` root bbox height > 0 (container does not collapse) | float clearfix working — without it the floated children escape and root height = 0 |
| M2 | For a 3-column equal-span demo (`colspan="4"` × 3 inside `ncols="12"`): each `.z-rowchildren` bbox width is within ±2px of `(rootWidth - 2 × gutterWidth) / 3` | JS column arithmetic produces equal widths; gutter math correct |
| M3 | Adjacent `.z-rowchildren` siblings share the same `bbox.top` value within ±2px (they sit on the same horizontal row, not wrapped to the next line) | cells are floated left, not block-stacked |
| M4 | No `.z-rowchildren` bbox overlaps an adjacent sibling's bbox by > 1px on the horizontal axis | gutters are respected — no cell collision |
| M5 | `.z-rowlayout` root bbox width ≥ 90% of its parent container's content-box width | `width: 100%` respected — layout fills available width |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-rowlayout` | width | `100%` | structural — fills parent |
| c2 | `.z-rowlayout` | background-color | transparent (no rule needed — inherits) | layout primitive; no surface |
| c3 | `.z-rowlayout` | border | none | layout primitive; no decoration |
| c4 | `.z-rowlayout::before, .z-rowlayout::after` | display | `table` | structural — clearfix |
| c5 | `.z-rowlayout::before, .z-rowlayout::after` | content | `""` | structural — clearfix |
| c6 | `.z-rowlayout::after` | clear | `both` | structural — clearfix collapses floats |
| c7 | `.z-rowchildren[class*="colspan"]` | display | `block` | structural — float child |
| c8 | `.z-rowchildren[class*="colspan"]` | float | `left` | structural — horizontal flow |
| c9 | `.z-rowchildren[class*="colspan"]` | box-sizing | `border-box` | structural — JS widths include border+padding |
| c10 | `.z-rowchildren[class*="colspan"]` | background-color | transparent (no rule needed) | column cell is transparent; child widget provides fill |
| c11 | `.z-rowchildren[class*="colspan"]` | border | none | column cell has no decoration |
| c12 | `.z-rowlayout` | padding | `0` | defensive — any padding breaks JS-computed percentage widths (border-box absorbs it) |
| c13 | `.z-rowchildren[class*="colspan"]` | padding | `0` | defensive — column cell padding would corrupt the column arithmetic; child widgets carry their own padding |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (container) | `.z-rowlayout` | c1, c2, c3, c4, c5, c6, c12 |
| default (column cell) | `.z-rowchildren[class*="colspan"]` | c7, c8, c9, c10, c11, c13 |
| responsive (narrow viewport) | `.z-rowchildren[class*="colspan"]` at `max-width: 767px` | float must be `none` |

## States to evaluate
- [ ] default — container collapses float children correctly (M1)
- [ ] default — 3 equal columns: widths match, same horizontal baseline (M2, M3)
- [ ] default — mixed colspan (8+4, 6+6): no overlap, gutters respected (M4)
- [ ] default — root fills parent width (M5)
- [ ] responsive — at 767px cells stack single-column (float: none)
