# Component: linelayout (theme design)
tier: T2
category: layout
preview: ${PREVIEW_URL}/linelayout.zul
rules: see .claude/skills/zk-component-rules/components/linelayout.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/src/main/resources/web/js/zkmax/layout/Linelayout.ts
  - zkmax/src/main/resources/web/js/zkmax/layout/Lineitem.ts
  - zkmax/src/main/resources/web/js/zkmax/layout/mold/linelayout.js
  - zkmax/src/main/resources/web/js/zkmax/layout/mold/lineitem.js
js-source-hash: bf8218c6a29eda134ae13e682a2fbdfe86a31f5df7a316b60154b5dce994ac95
closest-sibling: none — novel tripartite-column + center-line pattern; no ZK component shares this DOM architecture
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/css/linelayout.css
mockup-needed: Y
mockup-rationale: ZKDoc images exist but the Marble timeline point uses MD3 primary-color filled circle with outline-variant line — the iceblue default is close but the point size, gap, and line color diverge from our token values enough to warrant a mockup as the visual ground truth

## References
- MUI CSS: no analog — no MUI Timeline CSS in `/Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/`; see DESIGN.md §3, §5, §6 for novel-component policy
- DESIGN.md sections: §3 (color roles — primary for point fill), §5 (spacing — cave gap/padding), §6 (shape — point border-radius full circle), §7 (motion — none; no interactive states)
- Iceblue baseline: doc/contracts/baselines/linelayout-iceblue.png
- ZKDoc canonical: /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/Linelayout-1.png
- HTML contract: doc/contracts/linelayout.html

## Design Contract

A vertical-by-default timeline layout presenting items as a column of points connected by a single continuous vertical line. The connector line is `outline-variant` — a subtle neutral stroke that recedes behind the content. Each point circle is filled with `primary` and bordered with `primary` (border matches fill, creating a solid disc); the inner area is `on-primary` for any icon content. The point size is visually equivalent to a small avatar (24px). The cave padding provides symmetric gutter between the center column and the content areas on either side. The first-area column (left/top) and last-area column (right/bottom) receive no background, border, or elevation — they are transparent layout containers. Content placed inside lineitems is not styled by linelayout; it inherits the page surface. The `pointVisible=false` state makes the point invisible while preserving spacing continuity. No interactive states (hover, focus, disabled) — this is a pure display layout component. Horizontal orientation follows the same token choices with swapped axes.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-linelayout` root bbox area > 0 AND contains at least one `.z-lineitem-point` with non-zero bbox | layout is engaged and at least one point is visible |
| M2 | `.z-linelayout-line` has `background-color ≠ transparent` AND `background-color ≠ rgba(0,0,0,0)` AND `width ≥ 1px` (vertical: `height ≥ 80%` of `.z-linelayout-cave` height; horizontal: `width ≥ 80%` of cave width) | connector line is visible and spans the timeline |
| M3 | `.z-lineitem-point` computed `width` equals computed `height` within ±1px AND `border-radius ≥ min(width,height)/2 - 1px` | each point is a circle, not an ellipse or rectangle |
| M4 | `.z-lineitem-point` `background-color ≠ transparent` AND `background-color ≠ rgba(0,0,0,0)` | points are visually filled (not ghost circles that blend into the page) |
| M5 | All `.z-lineitem-point` elements have their `bbox.top` within the vertical range of `.z-linelayout-cave` bbox (i.e., no point escapes the cave's bounds) | points are contained within the cave column and do not overflow |
| M6 | `.z-linelayout-line` `z-index` (or stacking order) is below `.z-lineitem-point` — verified by confirming point bbox overlaps line bbox AND point is rendered above the line visually (background-color of point is opaque and covers the line at the overlap) | the continuous line passes behind the points, not over them |
| M7 | For an N-lineitem vertical timeline, each content slot's bbox vertical centre matches its corresponding `.z-lineitem-point` bbox vertical centre within ±4px, AND the content slots occupy ≥2 distinct vertical positions (not all collapsed to one) | content must line up with its point along the timeline axis; a single shared main-axis position means the content column is flowing on the wrong axis (`flex-direction:row` instead of `column`) |
| M8 | For a point with an icon (`pointIconSclass`) or image (`pointImageSrc`), the `.z-lineitem-point-inner` bbox centre matches its enclosing `.z-lineitem-point` bbox centre within ±1px on both axes, AND the inner is a flex centring container (`display:flex`, `align-items:center`, `justify-content:center`) so the icon glyph (`::before`) sits at the circle centre, AND `background-position` is centred so the image is not anchored top-left | icon and image content must be both vertically and horizontally centred in the point circle; `width/height:inherit` overflow or a top-left `background-position` pushes content off-centre |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| ll1 | `.z-linelayout` | height | `100%` | structural — ZK always sets 100% height on root; theme must preserve |
| ll2 | `.z-linelayout` | width | `100%` | structural — ZK always sets 100% width on root |
| ll3 | `.z-linelayout` | background | `transparent` | DESIGN.md §3 — layout container adds no surface |
| ll4 | `.z-linelayout-vertical` | display | `flex` | structural |
| ll5 | `.z-linelayout-vertical` | flex-direction | `row` | structural — three columns side by side |
| ll6 | `.z-linelayout-horizontal` | display | `flex` | structural |
| ll7 | `.z-linelayout-horizontal` | flex-direction | `column` | structural — three rows stacked |
| ll8 | `.z-linelayout-first` | flex | `1 0 0` | structural — equal flex share (overridable by firstScale) |
| ll8a | `.z-linelayout-vertical .z-linelayout-first` | justify-content | `space-around` | structural — aligns first-area content slots with their cave points; without this, items stack to one end |
| ll8b | `.z-linelayout-horizontal .z-linelayout-first` | justify-content | `space-around` | structural — horizontal analog of ll8a |
| ll8c | `.z-linelayout-vertical .z-linelayout-first` | flex-direction | `column` | structural — content slots must stack along the timeline axis so each aligns with its cave point; without it the slots flow `row` and collapse to a single main-axis position |
| ll8d | `.z-linelayout-horizontal .z-linelayout-first` | flex-direction | `row` | structural — horizontal analog of ll8c |
| ll8e | `.z-linelayout-first` | align-items | `flex-end` | structural — pulls first-area content toward the cave (right in vertical, bottom in horizontal) so it sits adjacent to the connector line/points |
| ll9 | `.z-linelayout-first` | display | `flex` | structural |
| ll10 | `.z-linelayout-first` | overflow | `hidden` | structural — content must not bleed into cave |
| ll11 | `.z-linelayout-last` | flex | `1 0 0` | structural — equal flex share (overridable by lastScale) |
| ll11a | `.z-linelayout-vertical .z-linelayout-last` | justify-content | `space-around` | structural — aligns last-area content slots with their cave points |
| ll11b | `.z-linelayout-horizontal .z-linelayout-last` | justify-content | `space-around` | structural — horizontal analog of ll11a |
| ll11c | `.z-linelayout-vertical .z-linelayout-last` | flex-direction | `column` | structural — content slots stack along the timeline axis; mirrors ll8c |
| ll11d | `.z-linelayout-horizontal .z-linelayout-last` | flex-direction | `row` | structural — horizontal analog of ll11c |
| ll11e | `.z-linelayout-last` | align-items | `flex-start` | structural — pulls last-area content toward the cave (left in vertical, top in horizontal) so it sits adjacent to the points |
| ll12 | `.z-linelayout-last` | display | `flex` | structural |
| ll13 | `.z-linelayout-last` | overflow | `hidden` | structural |
| ll14 | `.z-linelayout-cave` | display | `flex` | structural |
| ll14a | `.z-linelayout-vertical .z-linelayout-cave` | justify-content | `space-around` | structural — distributes point circles evenly so cave aligns with first/last columns |
| ll14b | `.z-linelayout-horizontal .z-linelayout-cave` | justify-content | `space-around` | structural — horizontal analog of ll14a |
| ll14c | `.z-linelayout-cave` | align-items | `flex-end` | structural — stock ZK sets this; the point fills the cave content box so it has no visible effect, kept for faithful reproduction |
| ll15 | `.z-linelayout-cave` | position | `relative` | structural — line is absolutely positioned inside cave |
| ll16 | `.z-linelayout-vertical .z-linelayout-cave` | flex-direction | `column` | structural |
| ll16a | `.z-linelayout-horizontal .z-linelayout-cave` | flex-direction | `row` | structural — point circles lay out side-by-side in horizontal orientation |
| ll17 | `.z-linelayout-vertical .z-linelayout-cave` | padding | `0 var(--zk-spacing-3)` | DESIGN.md §5 — cave gutter between line and content edges; token: spacing-3 (12px) |
| ll18 | `.z-linelayout-horizontal .z-linelayout-cave` | padding | `var(--zk-spacing-3) 0` | DESIGN.md §5 |
| ll19 | `.z-linelayout-line` | position | `absolute` | structural — line must be absolutely positioned |
| ll20 | `.z-linelayout-line` | z-index | `99` | structural — ZK sets this; must not be removed |
| ll21 | `.z-linelayout-line` | background-color | `var(--zk-color-outline-variant)` | DESIGN.md §3 — subtle neutral stroke |
| ll22 | `.z-linelayout-vertical .z-linelayout-line` | width | `2px` | DESIGN.md §6 — 2× MD3 divider weight (MD3 divider is 1dp); timeline connector needs more visual weight to read as a structural rail |
| ll23 | `.z-linelayout-vertical .z-linelayout-line` | height | `100%` | structural — spans full cave |
| ll24 | `.z-linelayout-vertical .z-linelayout-line` | left | `calc(50% - 1px)` | structural — centered in cave |
| ll25 | `.z-linelayout-horizontal .z-linelayout-line` | height | `2px` | DESIGN.md §6 — 2× MD3 divider weight; same rationale as ll22 (horizontal axis) |
| ll26 | `.z-linelayout-horizontal .z-linelayout-line` | width | `100%` | structural |
| ll27 | `.z-linelayout-horizontal .z-linelayout-line` | top | `calc(50% - 1px)` | structural |
| li1 | `.z-lineitem` | flex | `1 0 0` | structural |
| li2 | `.z-lineitem` | display | `flex` | structural |
| li3 | `.z-lineitem` | overflow | `hidden` | structural |
| li4 | `.z-linelayout-vertical .z-lineitem` | flex-direction | `column` | structural |
| li5 | `.z-lineitem > *` | flex-shrink | `0` | structural — children must not shrink inside lineitem slots |
| li6 | `.z-lineitem > *` | margin | `auto` | structural — auto margin centers children in the slot |
| li7 | `.z-lineitem-stretch` | width | `100%` | structural |
| li8 | `.z-lineitem-stretch` | height | `100%` | structural |
| pt1 | `.z-lineitem-point` | width | `24px` | DESIGN.md §6 — small-avatar size; matches MD3 icon button size |
| pt2 | `.z-lineitem-point` | height | `24px` | DESIGN.md §6 |
| pt3 | `.z-lineitem-point` | border-radius | `var(--zk-shape-corner-full)` | DESIGN.md §6 — full circle |
| pt4 | `.z-lineitem-point` | background | `var(--zk-color-primary)` | DESIGN.md §3 — primary fill |
| pt5 | `.z-lineitem-point` | border | `2px solid var(--zk-color-primary)` | DESIGN.md §3 — border matches fill for solid disc look |
| pt6 | `.z-lineitem-point` | overflow | `hidden` | structural — image and icon must clip to circle boundary |
| pt6a | `.z-lineitem-point` | position | `relative` | structural — required for z-index: 100 (pt7) to establish a stacking context above the line; z-index has no effect on non-positioned elements |
| pt6b | `.z-lineitem-point` | flex-shrink | `0` | structural — prevents the 24×24px circle from compressing inside a narrow cave |
| pt7 | `.z-lineitem-point` | z-index | `100` | structural — above the line |
| pt8 | `.z-lineitem-point` | color | `var(--zk-color-on-primary)` | DESIGN.md §3 — icon color inside primary circle |
| pt9 | `.z-lineitem-point-hidden` | visibility | `hidden` | structural — point-invisible state preserves space |
| pt10 | `.z-lineitem-point-inner` | height | `100%` | structural — inner fills the point's bordered interior exactly (24px − 2×2px border = 20px); `inherit` produced a 24px box that overflows the 20px interior and offsets content +2px/+2px from the point centre |
| pt11 | `.z-lineitem-point-inner` | width | `100%` | structural — same as pt10 (horizontal axis) |
| pt12 | `.z-lineitem-point-inner` | background-size | `cover` | structural — image fills the circle |
| pt12a | `.z-lineitem-point-inner` | background-position | `center` (computed `50% 50%`) | structural — `pointImageSrc` sets `background-image` with NO position; the default `0% 0%` anchors the image top-left, so the theme must centre it |
| pt13 | `.z-lineitem-point-inner` | display | `flex` | structural — centre icon glyph (`::before`) and image content both axes; replaces text-align/line-height baseline centring which was off by the inherit overflow |
| pt13a | `.z-lineitem-point-inner` | align-items | `center` | structural — vertical centring of the icon glyph / content |
| pt13c | `.z-lineitem-point-inner` | justify-content | `center` | structural — horizontal centring of the icon glyph / content |
| pt13b | `.z-lineitem-point-inner` | font-size | `12px` | DESIGN.md §6 — icon glyph fits within the 20px interior height without overflow |
| pt14 | `.z-lineitem-point-image` | display | `inline-flex` | structural |
| pt15 | `.z-lineitem-point-image` | vertical-align | `baseline` | structural |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (vertical) | `.z-linelayout.z-linelayout-vertical` | ll4, ll5, ll8a, ll8c, ll8e, ll11a, ll11c, ll11e, ll14, ll14a, ll14c, ll15, ll16, ll17 |
| default (horizontal) | `.z-linelayout.z-linelayout-horizontal` | ll6, ll7, ll8b, ll8d, ll8e, ll11b, ll11d, ll11e, ll14, ll14b, ll14c, ll15, ll16a, ll18 |
| line | `.z-linelayout-line` | ll20, ll21, ll22, ll23 |
| point (visible) | `.z-lineitem-point` | pt1–pt8, pt6a, pt6b |
| point (hidden) | `.z-lineitem-point.z-lineitem-point-hidden` | pt9 |
| point (icon) | `.z-lineitem-point .z-lineitem-point-inner.<icon-class>` | pt8, pt10, pt11, pt13, pt13a, pt13c, pt13b, M8 |
| point (image) | `.z-lineitem-point .z-lineitem-point-inner` (with background-image) | pt10, pt11, pt12, pt12a, M8 |

## States to evaluate
- [x] default (vertical orient — most common)
- [x] default (horizontal orient)
- [x] point visible (default)
- [x] point hidden (pointVisible=false)
- [x] point with image (pointImageSrc set) — image centred in circle (M8, pt12a)
- [x] point with icon (pointIconSclass set) — icon glyph centred in circle (M8, pt13/pt13a/pt13c)
- [ ] content in first area only (default — last area is default slot)
- [ ] content in last area only (opposite=true)
- [ ] model-driven (template rendering)
