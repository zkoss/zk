# Component: tablelayout (theme design)
tier: T1
category: layout
preview: ${PREVIEW_URL}/tablelayout.zul
rules: see .claude/skills/zk-component-rules/components/tablelayout.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/layout/Tablelayout.ts
  - zkmax/layout/Tablechildren.ts
  - zkmax/layout/mold/tablelayout.js
  - zkmax/layout/mold/tablechildren.js
js-source-hash: ebaf7326c748a0d20fe7a70ec7b48cce42a5466cf5dcd70b8aada637adb32648
closest-sibling: none — novel HTML-table layout primitive
mockup-needed: N
mockup-rationale: ZKDoc canonical image exists (ZKComRef_Tablelayout_Example.PNG); Marble applies only cell-spacing and vertical-align over a transparent table shell — no surface paint and no significant visual divergence from the ZKDoc layout structure.
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/css/tablelayout.css

## References
- MUI CSS: Layout/Grid.css — structural analog only (MUI Grid is CSS Grid not HTML table; no visual theming borrowed)
- DESIGN.md sections: §4 (Spacing Scale)
- Iceblue baseline: doc/contracts/baselines/tablelayout-iceblue.png
- ZKDoc canonical: /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/ZKComRef_Tablelayout_Example.PNG
- HTML contract: doc/contracts/tablelayout.html (not generated — mockup-needed: N)

## Design Contract

Tablelayout is a transparent layout shell whose sole role is to position child panels/widgets
in a table grid. The `.z-tablelayout` `<table>` element paints no surface, no border, and no
background — it is invisible in the final render. The only theme-relevant decision is the gutter
between cells: Marble uses `border-collapse: separate` and `border-spacing: var(--zk-spacing-2)`
(8px on both axes) to provide a consistent MD3-grid gutter between all adjacent cells in the
table. This replaces iceblue's zero-spacing (flush cells) with a compact but readable gap that
matches the 8px system step. Cells (`<td>.z-tablechildren`) are top-aligned by default
(`vertical-align: top`) to mirror the ZK stock default and match standard card-grid patterns
where cells contain panels of varying heights. No padding is added to the cell itself — content
widgets placed inside the cell carry their own padding. The `box-sizing` on the table is
`border-box`. Width and height of cells are entirely controlled by the content widgets or by
explicit `width=`/`height=` attributes on the child ZK panels — the theme does not override these.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-tablelayout` bbox.width > 0 AND contains at least one `.z-tablechildren` with bbox.width > 0 | layout is engaged — table and at least one cell have non-zero size |
| M2 | For any two horizontally adjacent `.z-tablechildren` cells (same `<tr>`, adjacent columns): gap between right edge of left cell and left edge of right cell ≥ 4px | cells have a visible gutter — not flush |
| M3 | No two `.z-tablechildren` bounding boxes overlap (intersection area = 0) | cells do not z-fight or stack on top of each other |
| M4 | `.z-tablelayout` has background = transparent OR rgba(0,0,0,0) AND box-shadow = none AND border-width = 0 | layout shell is invisible — must not paint a card frame around the entire grid |
| M5 | For two vertically adjacent `.z-tablechildren` cells (same column, consecutive rows): gap between bottom edge of upper cell and top edge of lower cell ≥ 4px | row gutter is present in both axes |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-tablelayout` | border-collapse | `separate` | required for border-spacing to have effect |
| c2 | `.z-tablelayout` | border-spacing | `var(--zk-spacing-2)` | DESIGN.md §4 — 8px grid gutter (compact step) |
| c3 | `.z-tablelayout` | background-color | `transparent` | layout shell must be invisible |
| c4 | `.z-tablelayout` | border-width | `0px` | layout shell must have no border of its own |
| c5 | `.z-tablechildren` | vertical-align | `top` | top-align cell content (ZK stock default; matches card-grid convention) |
| c6 | `.z-tablechildren` | padding | `0px` | cell padding is zero; content widgets carry their own padding |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default | `.z-tablelayout` | c1, c2, c3, c4 |
| default-cell | `.z-tablechildren` | c5, c6 |

## States to evaluate
- [x] default (layout shell — transparent, no border, border-spacing set)
- [x] default-cell (cell vertical-align, zero padding)
