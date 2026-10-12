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
mockup-rationale: ZKDoc canonical image exists; Marble applies only flex-wrap over a transparent container — no surface paint, no significant visual divergence from the ZKDoc layout diagram

## References
- MUI CSS: Layout/Stack.css — flex-wrap row layout is the closest structural analog; no visual theming borrowed
- Iceblue baseline: doc/contracts/baselines/anchorlayout-iceblue.png
- ZKDoc canonical: /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/ZKComRef_Anchorlayout_Example.png
- Divergence from baseline: iceblue renders anchor-children **flush** (float model, no gutter); Marble also renders them flush (no `gap`): anchor widths are inline percentages, so any gap makes 50%+50% / 33%x3 overflow and wrap (zkdemo B6 Z1). Flex-wrap is kept only as a safety net for children whose percentages sum above 100%.

## Design Contract

Anchorlayout is a transparent layout shell — it paints no surface, no border, and no shadow.
Children carry inline percentage widths set by the JS engine, so they sit flush side by side
exactly as in iceblue: 50%+50% share one row and 33%x3 share one row. Marble overrides iceblue's
float-based model with `display: flex; flex-wrap: wrap` and deliberately sets **no `gap`** —
a gap would push percentage-sized siblings past 100% and wrap them (zkdemo B6 Z1). The root
carries `width: 100%` and `box-sizing: border-box` so that percentage-anchored children resolve
correctly against the parent container width. Children carry `flex-shrink: 0` to prevent the flex
algorithm from compressing below the JS-set inline widths, `min-width: 0` to prevent flex blowout
from wide content, and `box-sizing: border-box` for a sizing model consistent with the root
container.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-anchorlayout` bbox.width > 0 AND `.z-anchorchildren` count ≥ 1 with bbox.width > 0 | layout is engaged — root and at least one child have non-zero widths |
| M2 | For any two horizontally adjacent `.z-anchorchildren` nodes (bbox.top overlap > 50%): `.z-anchorchildren` with anchor percentages summing ≤ 100% share the same `bbox.top` (no wrap) | children are not pushed onto a new row |
| M3 | No two `.z-anchorchildren` nodes have overlapping bounding boxes (intersection area = 0) | children do not z-fight or overlap |
| M4 | `.z-anchorlayout` has background = transparent OR rgba(0,0,0,0) AND box-shadow = none AND border-width = 0 | layout shell is invisible — it must not paint a card frame |
| M5 | A `.z-anchorchildren` with anchor="50%" has bbox.width ≈ 0.5 × `.z-anchorlayout` bbox.width (±2px tolerance — no gap, so the width is exact) | percentage anchor sizing is correctly resolved by the JS engine |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-anchorlayout` | display | `flex` | Marble flex override (replaces iceblue float) |
| c2 | `.z-anchorlayout` | flex-wrap | `wrap` | Marble layout policy — wrapping anchor layout |
| c3 | `.z-anchorlayout` | gap | `normal` (0) — none declared | inline % widths; any gap overflows and wraps the row |
| c4 | `.z-anchorlayout` | width | `100%` | required for % anchors to resolve against parent |
| c5 | `.z-anchorlayout` | box-sizing | `border-box` | required so 100% width includes any padding |
| c6 | `.z-anchorlayout` | align-items | `flex-start` | children should not stretch to the tallest sibling |
| c7 | `.z-anchorchildren` | flex-shrink | `0` | prevents flex from compressing below JS-set inline widths |
| c8 | `.z-anchorchildren` | box-sizing | `border-box` | consistent sizing model for child containers |
| c9 | `.z-anchorchildren` | min-width | `0` | prevents flex blowout from wide content |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default | `.z-anchorlayout` | c1–c6 |
| child-default | `.z-anchorchildren` | c7–c9 |

## States to evaluate
- [x] default (layout container — no interactive states)
- [x] child-default (anchorchildren — sizing model)
