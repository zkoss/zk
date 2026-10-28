# Component: splitlayout (theme design)
tier: T1
category: layout
preview: ${PREVIEW_URL}/splitlayout.zul
rules: see .claude/skills/zk-component-rules/components/splitlayout.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/src/main/resources/web/js/zkmax/layout/mold/splitlayout.js
  - zkmax/src/main/resources/web/js/zkmax/layout/Splitlayout.ts
js-source-hash: 91be9da33e6066ac2976cf134c507f8b4a49799a49ca3385eefe5ac15dce81da
closest-sibling: splitter (for the splitter bar sub-element only)

## References
- MUI CSS: no analog — MUI has no split-pane layout component (closest is a third-party resizable panel library; no file in static-css-output/)
- DESIGN.md sections: §1 (surfaces), §4 (spacing), §8 (state layers), §9 (motion), §11 (border rules), §14 (splitter family — canonical bar + actuator-pill spec)
- Iceblue baseline: doc/contracts/baselines/splitlayout-iceblue.png
- HTML contract: doc/contracts/splitlayout.html

## Design Contract

> Revised 2026-06-04 per user splitter-family unification ruling (gap log `doc/skill-gaps.md`): the splitter now follows the canonical family spec in DESIGN.md §14 — including the actuator pill that borderlayout pioneered — and two new outcome rows (M8/M9) assert button centering, closing the double-centering bug (Marble CSS centered via `top/left:50% + transform` on top of ZK's `setBtnPos_` inline-margin centering, pushing the button to the end of the bar; see the skill file's "Splitter button positioning" section).

The splitlayout splitter follows the unified **splitter family** spec (DESIGN.md §14) so the app has a single resize affordance. The bar is 8px thick, `--zk-color-surface-container` at rest; on hover it tints with `--zk-color-primary` at hover opacity; while dragging (`:active`) it deepens to the pressed-opacity tint. The bar carries the family's **actuator pill** (`.z-splitlayout-splitter-button`): an 8×28px `--zk-shape-corner-full` pill filled `--zk-color-outline-variant` (no border, no elevation) holding the grip/caret/grip icons — grip dots at 8px always visible (`on-surface-variant`), the collapse caret hidden at rest and fading in on hover. Hovering the pill itself fills it `--zk-color-primary` with `on-primary` icons. **The pill is centered on the bar's long axis; M8/M9 assert the outcome.** Mechanism (errata 2026-06-04, outcomes unchanged): never half-mix CSS and JS centering on one axis — ZK's `setBtnPos_` inline-margin centering is only reliable when the bar's long-axis size is CSS-fixed at bind time; on the flex-resolved axis (`orient="vertical"` bar width) it runs while the offset is still 0 and writes margin 0, so theme CSS must take full ownership there (`margin-left/top: 0 !important` to neutralize the JS margin, plus `left/top: 50% + transform` — see the skill file's timing-trap notes). When `resizable="false"` (`.z-splitlayout-splitter-nosplitter`), the bar drops its hover state with `cursor: default`. Transitions on `background-color` and `color` run at `--zk-motion-duration-short3` (250ms) with standard easing. Cave areas have no decoration — they are transparent containers.

A horizontal splitlayout shows two side-by-side caves separated by a thin vertical bar; a vertical splitlayout shows two top-bottom caves with a thin horizontal bar between them. With `hflex="1"` on the root, both panes split the available width equally by default; the splitter bar sits between them as an 8px-thick separator. Nested splitlayouts compose recursively — the inner splitlayout occupies the cave of its parent and lays out its own two children with its own splitter bar.

## Outcome assertions

Outcome-level predicates that gate `VERIFIED`: failing any row blocks VERIFIED even if all D-tier rows below pass. Predicates are deliberately disjunctive / tolerance-based — they assert *outcome* (e.g. size-within-N%, docked-side-by-side), not *recipe*. Row IDs use the `M` prefix (originally "macro-scale outcome"; retained as a stable identifier across all eval reports). The evaluator measures these from bounding-box geometry; see the `### 3b-outcome` step in `.claude/agents/zk-theme-evaluator.md` for the enforcement protocol.

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-splitlayout` root with `hflex="1"` has bbox width ≥ 90% of its parent's content-box width | hflex respected — layout fills available width |
| M2 | for every `.z-splitlayout`, the sum of its three immediate children's bbox sizes on the main axis (orient="horizontal" → width; orient="vertical" → height) equals the root's main-axis size ± 2px | caves + splitter consume the full layout axis without gap or overflow |
| M3 | for every `.z-splitlayout`, the two caves have main-axis sizes within 5% of each other by default (no inline width/height set on the caves by the page, and children carry equal flex weights — a page using e.g. `vflex="2"`/`vflex="1"` legitimately splits 2:1 and is exempt) | default 50/50 split when user hasn't dragged — note (2026-06-05): the 50/50 comes from ZK's runtime `z-flex-item` classes (when children use hflex/vflex) or JS-computed inline sizes, NOT from theme CSS forcing `flex-basis: 0` on the caves; theme CSS that hard-codes the split breaks M10/M11 |
| M4 | `.z-splitlayout-splitter` is visible (bbox > 0) AND positioned between the two caves on the layout axis (cave-1.right ≤ splitter.left ≤ cave-2.left + 1px for horizontal, analogous for vertical) | splitter visually separates panes, not floating off-position |
| M5 | nested `.z-splitlayout` inside a parent cave has its bbox contained within the cave's bbox (≤ 1px outside on any side) | nested splitlayouts honor the parent's cave boundaries — no overflow into siblings |
| M6 | no `.z-splitlayout-cave-*` element's bbox overlaps another cave's bbox by > 1px on both axes (across all nested splitlayouts in the page) | panes dock side-by-side / top-and-bottom — never stack at z-index |
| M7 | every `.z-splitlayout-splitter-vertical` has `width: auto` cross-axis behavior (its bbox.width equals the parent splitlayout's bbox.width ± 2px) AND every `.z-splitlayout-splitter-horizontal` has `height` matching its parent splitlayout's bbox.height ± 2px | splitter bar stretches across the cross-axis cleanly — no gaps next to it |
| M8 | for every `.z-splitlayout-splitter-horizontal` (vertical bar), the vertical **midpoint** of its `.z-splitlayout-splitter-button` bbox is within ±2px of the vertical midpoint of the splitter's bbox | button visually centered on the bar — added 2026-06-04 after the double-centering bug (ZK `setBtnPos_` writes inline `margin-top`; theme CSS must not add centering on the same axis) |
| M9 | for every `.z-splitlayout-splitter-vertical` (horizontal bar), the horizontal **midpoint** of its `.z-splitlayout-splitter-button` bbox is within ±2px of the horizontal midpoint of the splitter's bbox | same as M8 for the other orientation (ZK writes inline `margin-left`) |
| M10 | **drag persistence (horizontal orient)**: after ZK's drag-end resize path runs with a +100px delta on a horizontal splitlayout (real pointer drag of `.z-splitlayout-splitter-draggable`, or equivalently invoking the widget's `_doDragEndResize` — the same code path), the first cave's bbox width has changed by ~100px (±2px), **the new width persists** when re-measured ≥ 300ms later, AND the caves remain docked left-beside-right (cave-1.right ≤ cave-2.left + splitter width + 2px, tops aligned ± 2px) | added 2026-06-05 — drag must stick: ZK persists drag by writing inline px `width` on the caves after disabling child flex (`clearCSSFlex`); theme CSS must not override inline sizes (`flex-basis: 0` on caves) nor hard-code `display: flex`/`flex-direction` on the root (which survives ZK's `z-flex` class removal). Pre-fix FAILURE measured 2026-06-05: JS writes `width: 1013px` inline, bbox stays 913px — delta 0 |
| M11 | **drag persistence (vertical orient)**: same as M10 with a +100px delta on a vertical splitlayout — the first cave's bbox **height** changes by ~100px (±2px), persists ≥ 300ms later, AND the caves remain docked top-above-bottom (cave-1.bottom ≤ cave-2.top + splitter height + 2px, lefts aligned ± 2px) | added 2026-06-05 — same mechanism as M10 on the other axis (inline `height` on caves). Pre-fix FAILURE measured 2026-06-05: the height delta sticks but ZK's drag-end removal of `z-flex-column` activates Marble's fallback `.z-splitlayout:not(.z-flex-column) { flex-direction: row }`, flipping the vertical layout to a ROW — cave-top and cave-bottom land side-by-side (layout collapses) |
| M12 | **pane fill**: for every cave whose child widget uses `hflex`/`vflex`, the child's bbox equals the cave's bbox within ±1px on **both** axes — both orientations, including nested splitlayouts | added 2026-06-05 — the 8px bar IS the entire pane separation (DESIGN.md §14): panes sit flush against the splitter; no whitespace between pane child and splitter/cave edge is permitted. Pre-fix FAILURE measured 2026-06-05: 4/10 caves on splitlayout.zul had a 12px deficit on both axes (all horizontal-orient; children incl. a nested splitlayout) — the theme's default `margin-block-end: 12px` rhythm rule on container widgets was subtracted by ZK's css-flex sizing (`calc(100% - marginHeight)` in row mode, both axes; `zk/flex.ts` ~600–624). Root fix: the default rhythm rule was removed theme-wide (user ruling 2026-06-05; see `doc/spec/spacing-policy.md`); this row guards against any reintroduction of default margins on flex-capable widgets |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-splitlayout-splitter` | background-color | `var(--zk-color-surface-container)` | DESIGN.md §14 — splitter-family idle bar |
| c2 | `.z-splitlayout-splitter` | border-width | `0px` | DESIGN.md §14 — splitter-family bar is borderless; the `surface-container` fill is the divider (unified 2026-06-23, was a 1px outline-variant outlier) |
| c3 | `.z-splitlayout-splitter` | cursor | `default` | DESIGN.md §8 — no cursor affordance on bare bar |
| c4 | `.z-splitlayout-splitter-vertical` | height | `var(--zk-spacing-2)` (8px) | DESIGN.md §4 — 2-unit splitter height matches splitter contract |
| c5 | `.z-splitlayout-splitter-horizontal` | width | `var(--zk-spacing-2)` (8px) | DESIGN.md §4 — 2-unit splitter width matches splitter contract |
| c6 | `.z-splitlayout-splitter:hover` | background-color | `color-mix(in srgb, var(--zk-color-primary) calc(var(--zk-state-hover-opacity) * 100%), var(--zk-color-surface-container))` | DESIGN.md §14 — splitter-family hover tint |
| c6b | `.z-splitlayout-splitter:active` | background-color | `color-mix(in srgb, var(--zk-color-primary) calc(var(--zk-state-pressed-opacity) * 100%), var(--zk-color-surface-container))` | added 2026-06-04 — DESIGN.md §14 splitter-family drag tint |
| c7 | `.z-splitlayout-splitter-draggable.z-splitlayout-splitter-horizontal` | cursor | `col-resize` | structural — horizontal orient drag cursor |
| c8 | `.z-splitlayout-splitter-draggable.z-splitlayout-splitter-vertical` | cursor | `row-resize` | structural — vertical orient drag cursor |
| c9 | `.z-splitlayout-splitter-button` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §14 — pill icons muted at rest |
| c10 | `.z-splitlayout-splitter-button:hover` | color | `var(--zk-color-on-primary)` | revised 2026-06-04 — DESIGN.md §14: pill hover fills primary, icons flip to on-primary (was bar-hover → primary icon) |
| c11 | `.z-splitlayout-splitter-button` | transition | `color var(--zk-motion-duration-short3)` | DESIGN.md §9 — standard motion on button color |
| c16 | `.z-splitlayout-splitter-button` | background-color | `var(--zk-color-outline-variant)` | added 2026-06-04 — DESIGN.md §14 actuator pill idle fill |
| c17 | `.z-splitlayout-splitter-button` | border-radius | `var(--zk-shape-corner-full)` | added 2026-06-04 — DESIGN.md §14 pill shape |
| c18 | `.z-splitlayout-splitter-button` | box-shadow | none | added 2026-06-04 — DESIGN.md §14: no elevation on the pill |
| c19 | `.z-splitlayout-splitter-horizontal .z-splitlayout-splitter-button` | width | 8px | added 2026-06-04 — DESIGN.md §14: pill cross-axis = bar thickness |
| c20 | `.z-splitlayout-splitter-horizontal .z-splitlayout-splitter-button` | height | 28px | added 2026-06-04 — DESIGN.md §14: pill long-axis |
| c21 | `.z-splitlayout-splitter-vertical .z-splitlayout-splitter-button` | width | 28px | added 2026-06-04 — DESIGN.md §14: pill long-axis |
| c22 | `.z-splitlayout-splitter-vertical .z-splitlayout-splitter-button` | height | 8px | added 2026-06-04 — DESIGN.md §14: pill cross-axis = bar thickness |
| c23 | `.z-splitlayout-splitter-button:hover` | background-color | `var(--zk-color-primary)` | added 2026-06-04 — DESIGN.md §14 pill hover fill |
| c24 | `.z-splitlayout-splitter-icon.z-icon-ellipsis-h, .z-splitlayout-splitter-icon.z-icon-ellipsis-v` | font-size | 8px | added 2026-06-04 — DESIGN.md §14 grip glyph size |
| c25 | `.z-splitlayout-splitter-icon.z-icon-ellipsis-h, .z-splitlayout-splitter-icon.z-icon-ellipsis-v` | opacity | 1 | added 2026-06-04 — DESIGN.md §14: grips always visible (drag affordance) |
| c26 | `.z-splitlayout-splitter-icon:not(.z-icon-ellipsis-h):not(.z-icon-ellipsis-v)` (collapse caret) | opacity | 0 at rest; 1 on `.z-splitlayout-splitter-button:hover` | added 2026-06-04 — DESIGN.md §14: caret hidden at idle, fades in on hover |
| c12 | `.z-splitlayout-splitter` | transition | `background-color var(--zk-motion-duration-short3)` | DESIGN.md §9 — standard motion on bar bg |
| c13 | `.z-splitlayout-splitter-nosplitter` | cursor | `default` | structural — no-drag state |
| c27 | `.z-splitlayout-splitter-nosplitter:hover` | background-color | `var(--zk-color-surface-container)` (idle — no primary tint) | DESIGN.md §14 non-resizable row; encodes the approved prose "drops its hover state" (Gate-2 finding 2026-06-04 — false resize affordance) |
| c14 | `.z-splitlayout-splitter-button-disabled` | border-width | `0` | structural — disabled button has no border |
| c15 | `.z-splitlayout-cave-top, .z-splitlayout-cave-bottom, .z-splitlayout-cave-left, .z-splitlayout-cave-right` | background-color | transparent (no rule needed — inherits) | cave areas are transparent containers, no fill |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (vertical, resizable, no collapse) | `.z-splitlayout-splitter.z-splitlayout-splitter-vertical.z-splitlayout-splitter-draggable` | c1, c2, c4, c8 |
| default (horizontal, resizable, no collapse) | `.z-splitlayout-splitter.z-splitlayout-splitter-horizontal.z-splitlayout-splitter-draggable` | c1, c2, c5, c7 |
| hover — bar | `.z-splitlayout-splitter:hover` | c6 |
| drag — bar | `.z-splitlayout-splitter:active` | c6b |
| pill default | `.z-splitlayout-splitter-button` | c9, c11, c16, c17, c18, c19, c20, c21, c22, c24, c25, c26 |
| pill hover | `.z-splitlayout-splitter-button:hover` | c10, c23, c26 |
| collapse-button visible | `.z-splitlayout-splitter-button:not(.z-splitlayout-splitter-button-disabled)` | c9, c11 |
| collapse-button disabled (no-collapse) | `.z-splitlayout-splitter-button.z-splitlayout-splitter-button-disabled` | c14 |
| not-resizable | `.z-splitlayout-splitter.z-splitlayout-splitter-nosplitter` | c3, c13 |
| not-resizable hover | `.z-splitlayout-splitter-nosplitter:hover` | c27 |

## States to evaluate
- [ ] default — vertical orient, resizable=true, collapse=none
- [ ] default — horizontal orient, resizable=true, collapse=none
- [ ] hover — splitter bar hover tint visible
- [ ] hover — pill fills primary, icons flip to on-primary, caret fades in
- [ ] button centering — pill midpoint matches bar midpoint in both orients (M8/M9)
- [ ] collapse=before — caret icon visible in button; click collapses first pane
- [ ] collapse=after — caret icon visible; click collapses second pane
- [ ] resizable=false — cursor reverts to default; drag handle icon hidden
- [ ] nested splitlayout — inner horizontal inside outer vertical (as in preview page)
- [ ] drag persistence — pane sizes stick after drag in both orients (M10/M11)
- [ ] pane fill — flexed children fill their caves flush, no gap before the splitter (M12)
