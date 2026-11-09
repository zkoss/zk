# Component: anchorlayout (theme design)
tier: T1
category: layout
preview: ${PREVIEW_URL}/anchorlayout.zul
rules: see .claude/skills/zk-component-rules/components/anchorlayout.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zul/layout/Anchorlayout.ts
  - zul/layout/Anchorchildren.ts
  - zul/layout/mold/anchorlayout.js
  - zul/layout/mold/anchorchildren.js
js-source-hash: 7eca7284bfb4dc16c1b8573f06a80c75b5c156407ced18278340aa13b9bb40f7
closest-sibling: none — novel anchor-sizing pattern
mockup-needed: N
mockup-rationale: ZKDoc canonical image exists; Marble applies only a float arrangement over a transparent container, as IceBlue does — no surface paint, no significant visual divergence from the ZKDoc layout diagram

## References
- MUI CSS: no analog — Material-UI ships no anchor-sized layout; the arrangement is IceBlue's float model, no visual theming borrowed
- Iceblue baseline: doc/contracts/baselines/anchorlayout-iceblue.png
- ZKDoc canonical: https://github.com/zkoss/zkdoc/blob/master/zk_component_ref/images/ZKComRef_Anchorlayout_Example.png
- Divergence from baseline: iceblue renders anchor-children **flush** (float model, no gutter); Marble renders them flush too (no `gap`): anchor widths are inline percentages, so any gap makes 50%+50% / 33%x3 overflow and wrap. Children whose percentages sum above 100% wrap, since a float that does not fit beside the others moves down.

## Design Contract

Anchorlayout is a transparent layout shell — it paints no surface, no border, and no shadow.
Children carry inline percentage widths and heights set by the JS engine, so they sit flush side by
side exactly as in iceblue: 50%+50% share one row and 33%x3 share one row. Marble keeps iceblue's
float model: the root is `display: flow-root` to contain the floats, and every
child is `float: left`. `flow-root` is used instead of iceblue's `overflow: hidden` so that the focus ring and shadow of a child at the layout's edge are not clipped; a page that wants clipping sets `overflow` on the component itself. A float goes to the highest place it fits, so a child that fits beside a
taller sibling sits there (anchors `50% 100%`, `50% 50%`, `50% 50%`: the third child goes under the
second, beside the first). A wrapping flex row would start a new line under the tallest item and
push that child out of the layout box (ZK-6112). Marble deliberately sets **no `gap`** — a gap would
push percentage-sized siblings past 100% and wrap them. The root carries `width: 100%` and
`box-sizing: border-box` so that percentage-anchored children resolve correctly against the parent
container width; children carry `box-sizing: border-box` for a sizing model consistent with the root
container.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-anchorlayout` bbox.width > 0 AND `.z-anchorchildren` count ≥ 1 with bbox.width > 0 | layout is engaged — root and at least one child have non-zero widths |
| M2 | For any two horizontally adjacent `.z-anchorchildren` nodes (bbox.top overlap > 50%): `.z-anchorchildren` with anchor percentages summing ≤ 100% share the same `bbox.top` (no wrap) | children are not pushed onto a new row |
| M3 | No two `.z-anchorchildren` nodes have overlapping bounding boxes (intersection area = 0) | children do not z-fight or overlap |
| M4 | `.z-anchorlayout` has background = transparent OR rgba(0,0,0,0) AND box-shadow = none AND border-width = 0 | layout shell is invisible — it must not paint a card frame |
| M5 | A `.z-anchorchildren` with anchor="50%" has bbox.width ≈ 0.5 × `.z-anchorlayout` bbox.width (±2px tolerance — no gap, so the width is exact) | percentage anchor sizing is correctly resolved by the JS engine |
| M6 | With anchors `50% 100%`, `50% 50%`, `50% 50%`: the third child's `bbox.left` ≈ 0.5 × `.z-anchorlayout` bbox.width and its `bbox.top` ≈ the second child's `bbox.bottom`; `.z-anchorlayout` scrollHeight ≤ clientHeight | a child that fits beside a taller sibling sits beside it and stays inside the layout box |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c3 | `.z-anchorlayout` | gap | `normal` (0) — none declared | inline % widths; any gap overflows and wraps the row |
| c4 | `.z-anchorlayout` | width | `100%` | required for % anchors to resolve against parent |
| c5 | `.z-anchorlayout` | box-sizing | `border-box` | required so 100% width includes any padding |
| c8 | `.z-anchorchildren` | box-sizing | `border-box` | consistent sizing model for child containers |
| c10 | `.z-anchorlayout` | display | `flow-root` | float model as in iceblue; contains the floats; no flex line to push a child below the tallest sibling |
| c11 | `.z-anchorlayout` | overflow | `visible` (none declared) | a child's focus ring and shadow at the layout's edge are not clipped; a page's own `overflow` wins |
| c12 | `.z-anchorchildren` | float | `left` | a child that fits beside a taller sibling sits beside it, as in iceblue |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default | `.z-anchorlayout` | c3–c5, c10, c11 |
| child-default | `.z-anchorchildren` | c8, c12 |

## States to evaluate
- [x] default (layout container — no interactive states)
- [x] child-default (anchorchildren — sizing model)
