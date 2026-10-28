# Component: scrollview
tier: T2
category: layout
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/css/scrollview.css
siblings: []
preview: ${PREVIEW_URL}/scrollview.zul

## References
- DESIGN.md sections: §2 (text colors), §5 (shape), §6 (elevation), §9 (motion)
- MUI CSS: no analog — MUI has no scroll-viewport component
- JS source (the structural authority for this component):
  `../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/Scrollview.ts`,
  `../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/mold/scrollview.js`

## DOM key selectors
```
.z-scrollview                       ← root viewport (+ -vertical | -horizontal orient class)
.z-scrollview-content               ← id="{uuid}-cave"; the element _move() translates
.z-scrollview-inner                 ← id="{childUuid}-chdex"; ONE PER CHILD widget
.z-scrollview-scrollbar             ← JS-created by _addBar(); touch path only
.z-scrollview-scrollbar-indicator   ← the thumb inside the bar
.z-scrollview-load                  ← pull-past-the-edge hint; touch path only
.z-scrollview-load-{up|down|left|right}   ← the direction glyph inside it
```

Note: `.z-scrollview-inner` is the **per-child slot**, not "the scrollable content
wrapper" — the scrolled element is `.z-scrollview-content`. An earlier revision of
this contract had those two swapped.

## Design Contract

Scrollview is a viewport, not a card. It paints no surface of its own — no background,
no border, no elevation — because the children scrolled through it carry their own, and
any framing here would double-frame every card inside. Two rendering paths share one
stylesheet. On the **desktop** path `bind_()` writes an inline `overflow: auto` and the
browser scrolls natively, so the theme contributes only structure (stacking context,
transform origin, orient sizing) and nothing visible. On the **touch** path the root
stays clipped while `_move()` drives `translate3d()` on the cave, and the theme supplies
the entire scroll affordance: an overlay scrollbar — a 6px pill inset from the edge,
`scrim` fill behind a `surface`-coloured halo, fading on `motion-duration-short2`, inert
to pointer input — plus a pull-past-the-edge load hint styled as a Material
pull-to-refresh puck (`surface` circle, `elevation-2`, an `on-surface-variant` chevron
drawn from two borders, since a `url()` here could not be theme-resolved). The hint takes
no layout space at rest.

## Outcome assertions

Outcome-level predicates that gate `VERIFIED`. Rows M3–M6 require a **mobile UA** — on a
desktop UA the elements they name are never created, and those rows are inapplicable
rather than failing.

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-scrollview` root bbox height > 0 AND `scrollHeight − clientHeight ≥ 1` AND computed `overflow` ∈ {`auto`, `hidden`, `scroll`} | the viewport is a real scroll container holding more than it shows — the premise every other row depends on |
| M2 | `.z-scrollview` computed `background-color` has alpha 0 AND all four `border-*-width` are 0 AND `box-shadow` is `none` | scrollview is a viewport, not a card. Deliberate inversion of the template's default "visible framing" M1: framing here would double-frame every card scrolled through it |
| M3 `M-scrollbar-visible` | mobile UA, after `_refresh()`: `.z-scrollview-scrollbar` bbox width ≥ 4px AND height > 0 AND `bbox.right ≤ root.bbox.right` AND `bbox.top ≥ root.bbox.top` AND `bbox.bottom ≤ root.bbox.bottom` | on the touch path the root is `overflow:hidden`, so this overlay bar is the **only** scroll affordance; absent or out of frame means no position feedback at all |
| M4 | `.z-scrollview-scrollbar-indicator` bbox height ≥ 8px AND ≤ track height − 1px, AND (thumb ÷ track) is within ±0.05 of (track ÷ cave `offsetHeight`), AND indicator `background-color` alpha > 0 | the thumb must read as a *proportional* position indicator — a full-length or hairline thumb conveys nothing about where you are |
| M5 | `document.elementFromPoint()` at the indicator's bbox centre returns an element that is neither `.z-scrollview-scrollbar` nor `.z-scrollview-scrollbar-indicator` | the bar is a read-out, not a drag handle. The root owns the whole touch stream; a bar that swallows touches kills scrolling exactly where the thumb rests |
| M6 `M-load-hint-at-rest` | before any overscroll, `.z-scrollview-load` has a zero-area bounding rect | the mold emits the hint *ahead of* the cave inside the root — if it takes layout space at rest it pushes all content down by its own height |
| M7 | horizontal orient with ≥2 children: the `.z-scrollview-inner` slots occupy ≥2 distinct `bbox.left` values AND all share `bbox.top` within ±2px | horizontal scrolling is meaningless if the slots stack vertically; asserts the outcome of the inline-block rule without naming the recipe |
| M8 `M-touch-drag-scrolls` | mobile UA: after a 200px upward touch drag across the root's centre, the cave's computed transform translates along the scroll axis by ≥ 50px AND the widget's `_pos` holds finite numbers | the touch path's entire purpose. `doTouchStart_` seeds `_pos` by parsing the cave's **computed** transform matrix; a cave with transform `none` parses to `NaN` and every later `_move()` writes an invalid `translate3d`, so the bar shows but the content never moves — M1–M6 all stay green while scrolling is dead (found 2026-09-12) |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-scrollview` | overflow | `auto` or `hidden` | structural — `hidden` from this stylesheet; `bind_()` overrides it inline with `auto` on desktop |
| c2 | `.z-scrollview` | background-color | `transparent` | DESIGN.md §1 — viewport adds no surface |
| c3 | `.z-scrollview` | position | `relative` | structural — containing block for the absolutely-positioned bar and load hint |
| c4 | `.z-scrollview-content` | transform-origin | `0 0` | structural — `_move()` writes px `translate3d()`; a centred origin would halve every scroll offset |
| c5 | `.z-scrollview-inner` | position | `relative` | structural — children position within their own slot |
| c6 | `.z-scrollview-horizontal .z-scrollview-content` | white-space | `nowrap` | structural — keeps the inline-block slots on one line. Scoped to the cave, **not** the root as stock ZK does, so it never suppresses wrapping in the children's own text |
| c7 | `.z-scrollview-horizontal .z-scrollview-inner` | display | `inline-block` | structural — slots run along one line |
| c8 | `.z-scrollview-horizontal .z-scrollview-inner` | white-space | `normal` | structural — undoes c6 for the child's own content |
| c9 | `.z-scrollview-scrollbar` | pointer-events | `none` | structural — see M5 |
| c10 | `.z-scrollview-scrollbar` | z-index | `100` | structural — local to the root's stacking context; under the load hint (999). Scrollview is not a ZK floating widget, so no `--zk-index-*` role applies |
| c11 | `.z-scrollview-vertical .z-scrollview-scrollbar` | width | `6px` | DESIGN.md §6 — Material overlay-thumb weight |
| c12 | `.z-scrollview-scrollbar` | transition | `opacity var(--zk-motion-duration-short2) var(--zk-motion-easing-standard)` | DESIGN.md §9 — `_barPos()`/`_resetPos()` toggle inline opacity; fade rather than snap |
| c13 | `.z-scrollview-scrollbar-indicator` | background | `var(--zk-color-scrim)` | DESIGN.md §3 |
| c14 | `.z-scrollview-scrollbar-indicator` | border | `1px solid var(--zk-color-surface)` | DESIGN.md §1 — halo keeping the pill legible over dark content |
| c15 | `.z-scrollview-scrollbar-indicator` | border-radius | `var(--zk-shape-corner-full)` | DESIGN.md §5 — pill |
| c16 | `.z-scrollview-scrollbar-indicator` | background-clip | `padding-box` | structural — keeps the halo a separate ring instead of tinting the scrim |
| c17 | `.z-scrollview-load` | display | `none` | structural — see M6 |
| c18 | `.z-scrollview-load` | z-index | `999` | structural — above the overlay bar (c10) |
| c19 | `.z-scrollview-load-{dir}` | background | `var(--zk-color-surface)` | DESIGN.md §1 — pull-to-refresh puck |
| c20 | `.z-scrollview-load-{dir}` | box-shadow | `var(--zk-elevation-2)` | DESIGN.md §6 |
| c21 | `.z-scrollview-load-{dir}` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §2 — chevron ink |
| c22 | `.z-scrollview-load-{dir}` | width / height | `32px` | structural — `_loadBoundary()` centres the hint by reading `offsetWidth`/`offsetHeight`, so the explicit size is load-bearing |
| c23 | `.z-scrollview-content` | transform | `translate3d(0, 0, 0)` (computed `matrix(1, 0, 0, 1, 0, 0)`) | structural — **load-bearing, not a no-op**: `doTouchStart_` (Scrollview.ts:598) reads the computed matrix to seed `_pos`; `none` parses to `NaN` and disables touch scrolling entirely. See M8 |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (vertical, desktop) | `.z-scrollview.z-scrollview-vertical` | c1, c2, c3, c4, c5 |
| default (horizontal) | `.z-scrollview.z-scrollview-horizontal` | c6, c7, c8, M7 |
| touch — bar at rest | `.z-scrollview-scrollbar` | c9, c10, c11, c12, M3, M5 |
| touch — thumb | `.z-scrollview-scrollbar-indicator` | c13, c14, c15, c16, M4 |
| touch — load hint at rest | `.z-scrollview-load` | c17, c18, M6 |
| touch — drag in flight | `.z-scrollview-content` | c23, M8 |
| touch — load hint shown | `.z-scrollview-load-{dir}` | c19, c20, c21, c22 |

## States to evaluate
- [x] default with content (vertical, desktop) — `gallery` project, `scrollview-gallery.png`
- [x] touch path, overlay scrollbar at rest — `tablet` project, `scrollview-tablet.png`
- [x] touch path, drag scrolls the content — `tablet` project, `M-touch-drag-scrolls` (CDP touch sequence; Playwright's touchscreen API has no drag)
- [ ] horizontal orient — M7, c6–c8 are **authored but not yet evaluated**: the preview
      page carries only a vertical scrollview, so exercising them needs a horizontal
      instance added to `zkthemelab/src/main/webapp/web/scrollview.zul` (which re-cuts
      `scrollview-gallery.png`)
- [ ] overscroll — load hint shown (c19–c22): needs an `onScroll` listener on the
      component plus a touch drag past the boundary; `_loadBoundary()` only runs when
      `isListen('onScroll')` is true
