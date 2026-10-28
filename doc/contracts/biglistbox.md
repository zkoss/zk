# Component: biglistbox
tier: T2
category: data
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/big/css/biglistbox.css
siblings: []
preview: ${PREVIEW_URL}/biglistbox.zul

## References
- MUI CSS: DataDisplay/Table.css (closest analog)
- Skill: `.claude/skills/zk-component-rules/components/biglistbox.md` (DOM, WScroll, traps)
- DESIGN.md sections: §2, §3, §4, §9

## DOM key selectors
```
.z-biglistbox                       ← root (the card frame, overflow:hidden)
.z-biglistbox-outer                 ← positioned wrapper
.z-biglistbox-head-outer / -body-outer
.z-biglistbox-header                ← column header TH
.z-biglistbox-header-content        ← header layout box (sorticon + label)
.z-biglistbox-sorticon > i          ← sort caret (.z-icon-caret-up|down only when sorted)
.z-biglistbox-row                   ← data row TR  (+ .z-biglistbox-odd / -selected)
.z-biglistbox-row td                ← data cell  (NO .z-biglistbox-cell class exists)
.z-biglistbox-wscroll-vertical|-horizontal   ← ZK's own scrollbar (not native)
.z-biglistbox-wscroll-drag          ← the persistent thumb
.z-biglistbox-wscroll-pos           ← transient drag-preview (must keep a box; see skill)
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-biglistbox` | border | 1px solid rgba(0, 0, 0, 0.12) |
| c2 | `.z-biglistbox-header` | background-color | transparent or surface |
| c3 | `.z-biglistbox-header` | border-bottom | 1px solid rgba(0, 0, 0, 0.12) |
| c4 | `.z-biglistbox-row td` | padding | 8px 16px |
| c5 | `.z-biglistbox-row:hover` | background-color | rgba(0, 0, 0, 0.04) |
| bs1 | `.z-biglistbox table` | border-spacing | 0px (head & body — UA default is 2px; gap above row 1) |
| h1 | `.z-biglistbox-header-content` | display | flex (caret laid out inline; header stays single-line when sorted) |
| h2 | `.z-biglistbox-header` | offsetHeight | unchanged after sorting (≈37px before AND after — caret must not add a row) |
| sc1 | `.z-biglistbox-wscroll-vertical` (overflowing model) | visibility / width | `visibility:hidden` (track draws no pixels, hit-test passes through); width stays 14px (D39-A: the vertical track does not cover the header; an axis that fits draws nothing, ZK hides its bar) |
| sc2 | `.z-biglistbox-wscroll-drag` (overflowing model) | visibility / background-color / size | `visible`; `var(--zk-color-outline-variant)` (hover `--zk-color-on-surface-variant`, same as the frozen-column and `zul.Scrollbar` thumbs), 8px pill, fixed length > 0, no groove (D40-A: same resting look as the document scrollbar); vertical `left:6px`, horizontal `top:4px` |
| sc3 | `.z-biglistbox` / `.z-biglistbox-outer` | position | relative (else the absolute scrollbar anchors to the viewport, off-component → looks missing) |
| sc4 | `.z-biglistbox-wscroll-endbar` | position | absolute (NOT display:none) — else wheel/drag clamp the thumb to a negative top |
| sc6 | `.z-biglistbox-wscroll-vertical::before` / `-horizontal::before` | content | `none` — no track groove (the former always-visible slot is retired) |
| sc5 | thumb offset (after wheel to far end) | within track | **both axes**: 0 ≤ thumbTop ≤ trackHeight (vertical) AND 0 ≤ thumbLeft ≤ trackWidth (horizontal), monotonic with scroll (no off-track jump). Horizontal: verify `_currentX` advances and thumb `offsetLeft` tracks it to `endbarLeft − thumbWidth`. |

## States to evaluate
- [ ] default with rows, header visible
- [ ] sorted column — header height unchanged, caret inline (h1/h2)
- [ ] overflowing model (e.g. MultipleRow / MultipleColumn) — vertical & horizontal scrollbars visible and draggable (sc1/sc2). NOTE: the horizontal bar sits on the bottom edge only; on a 500px component below the page fold it is off-screen — `scrollIntoView({block:'end'})` before judging "missing".
- [ ] large model (HugeRow/HugeColumn) — thumb traverses the full track proportionally (scale<1)

## Notes
- Biglistbox draws its **own** scrollbar (`zul.WScroll`); there is no native overflow
  element. Measuring scroll requires the wscroll DOM, not `overflow`/`scrollHeight`.
- For modest datasets ZK caps the scroll `scale` at 1, so the fixed-size thumb sits in the
  upper region of the track — inherent WScroll behavior, not a theme defect (see skill).
- The frozen-column tick (`.z-biglistbox-verticalbar-tick`, 10×14) is component CSS in this file, not in za11y. za11y's `big-a11y.ts` reads its `offsetWidth` to set the horizontal bar start.
