# Component: splitter (theme design)
tier: T2
category: layout
preview: ${PREVIEW_URL}/splitter.zul   (or any borderlayout-with-splitter page)
rules: see .claude/skills/zk-component-rules/components/splitter.md
contract-approved: true
zk-version: 10.2.1-jakarta

> Revised 2026-06-04: the splitter now follows the canonical family spec in the "Splitter Family" section of `doc/spec/DESIGN.md` — shared with borderlayout region splitters, splitlayout, and goldenlayout (colors only).

## References
- `doc/spec/DESIGN.md` sections: **"Splitter Family" (canonical spec)**, "State-Layer Overlays", "Motion", "Border Rules"
- Closest siblings: `borderlayout` region splitters and `splitlayout` splitter — identical ZK mold pattern (bar + `<span>-button` + grip/caret/grip icons) and identical `setBtnPos_` JS centering (see skill file).
- Visual goal: one resize affordance across the app — unobtrusive tonal strip at rest, primary-tinted on hover, pill actuator.

## Visual outcome

A splitter is a working resize handle, not a decorated strip: dragging the bar moves the boundary between its two sibling panes, and the bar itself tracks the new boundary. In the default mold the parent hbox/vbox is a nested-TABLE layout (`table.z-hbox/.z-vbox > tr > td-frame > table#-real > tr/td`); ZK persists a drag by writing inline px `width`/`height` on the adjacent `<td>`s, and sizes the bar itself via `width:100%` (vertical) / px height (horizontal) — all of which require the table display chain and the inline `width/height:100%` chain to stay intact. See `.claude/skills/zk-component-rules/components/box.md`.

## Outcome assertions

Outcome-level predicates that gate `VERIFIED`: failing any row blocks VERIFIED even if all declared-value rows pass. Measured on `/splitter.zul` Default Mold (hbox 300px high containing a 100%×100% vbox + side splitter; vbox contains an inner splitter).

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | **%-chain integrity**: the vbox's inner `#<uuid>-real` table bbox height equals the vbox root bbox height ± 2px, and the hbox's inner `-real` table bbox width+height match the hbox content box ± 2px | added 2026-06-06 — the mold's inline `width/height:100%` must resolve; theme CSS that changes `display` on `.z-hbox`/`.z-vbox` breaks the chain. Pre-fix FAILURE measured 2026-06-06: vbox `-real` table 68px tall inside a 300px vbox (theme `display:inline-flex` on the outer table → anonymous-table wrapper with auto size) |
| M2 | **drag follows (vertical splitter)**: after a pointer-event drag of `.z-splitter-vertical` by +80px down (dispatched mousedown → stepped mousemoves → mouseup, target away from the pill — the screenshot-tool single-gesture drag does NOT drive ZK's Draggable; same protocol as splitlayout M10/M11), the bar's bbox top moves by min(80, `_snap` clamp = adjacent-pane offsetHeight − bar) ± 2px and persists ≥ 300ms later | added 2026-06-06 — user report: vertical bar never moves. Pre-fix FAILURE measured 2026-06-06: bbox unchanged (rows squashed to content height → clamp ≈ 0, so the px heights `_doDragEndResize` writes had no room to act). Post-fix: +80px requested → +80px measured, persists |
| M3 | **drag follows (horizontal splitter)**: same protocol with +50px right on `.z-splitter-horizontal` — bbox left moves by min(50, clamp) ± 2px and persists ≥ 300ms later | added 2026-06-06 — same mechanism on the other axis (inline px `width` on adjacent tds). Post-fix: +50px requested → +35px measured = exact clamp (narrow right pane), persists |
| M4 | **cross-axis stretch propagation**: after M3's drag, the inner `.z-splitter-vertical` bar's bbox width equals its vbox root bbox width ± 2px (i.e. the bar lengthened with the widened pane) | added 2026-06-06 — user report: widening the left pane never lengthens the inner bar. The bar's `width:100%`/`_fixsz` resize chain must reach the resized td |

## Expected values (T2) — `doc/spec/DESIGN.md` "Splitter Family"
- Bar: `var(--zk-splitter-thickness)` thick, background `var(--zk-splitter-bar-bg)` at rest.
- Bar hover: `var(--zk-splitter-bar-bg-hover)`.
- Bar drag (`:active`): `var(--zk-splitter-bar-bg-active)`.
- Actuator pill (`.z-splitter-button`): `var(--zk-splitter-thickness)` cross-axis × `var(--zk-splitter-pill-length)` long-axis, `--zk-splitter-pill-radius`, `--zk-splitter-pill-fill` fill, no border, no elevation.
- Pill hover: `--zk-splitter-pill-fill-hover` fill, icons `--zk-splitter-pill-icon-hover`.
- Grip icons (`.z-splitter-icon.z-icon-ellipsis-*`): `var(--zk-splitter-icon-size)`, `--zk-splitter-pill-icon`, always visible (`opacity: 1`).
- Collapse caret (middle `.z-splitter-icon`): hidden at idle (`opacity: 0`), fades in on pill hover.
- Cursor: `col-resize` (`.z-splitter-horizontal`) / `row-resize` (`.z-splitter-vertical`).
- Non-resizable (`.z-splitter-nosplitter`): cursor `default`; `:hover` keeps the idle `--zk-splitter-bar-bg` background — no primary tint on a bar that cannot resize (`doc/spec/DESIGN.md` "Splitter Family", non-resizable row; a hover tint there is a false resize affordance, 2026-06-04).
- Transition: `var(--zk-splitter-transition)`.
- Centering: pill midpoint within ±2px of bar midpoint on the long axis. Mechanism (errata 2026-06-04, outcome unchanged): never half-mix CSS and JS centering — either the axis is fully JS-owned (`setBtnPos_` inline margin; only safe when the bar's long-axis size is CSS-fixed at bind time) or fully CSS-owned (`margin-left/top: 0 !important` to neutralize the JS inline margin + `left/top: 50% + transform`). CSS ownership is required on a flex-resolved axis — `setBtnPos_` can run while the bar offset is still 0 (see skill file timing-trap notes; both half-mixing and JS-only-on-flex-axis shipped as real splitlayout bugs).
- Ghost (`.z-splitter-ghost`): `--zk-color-primary` at 0.3 opacity (matches borderlayout c41/c42).

## States to evaluate
- [ ] default, hover, active (drag), pill hover (primary fill + caret fade-in), nosplitter (cursor default), button centering both orientations
