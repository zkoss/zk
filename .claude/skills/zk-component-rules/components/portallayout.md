# portallayout

A multi-column dashboard shell that lays out one or more `Portalchildren` columns side-by-side (vertical orient, the default) or stacked (horizontal orient). Each column holds one or more `Panel` children. The layout provides drag-and-drop reordering of panels across columns via ZK's `Draggable` mechanism. The portallayout itself has no visible chrome — all visual substance comes from the `Panel` children it hosts.

## DOM structure

```
.z-portallayout[.z-portallayout-vertical|.z-portallayout-horizontal]   (<div> — root)
├─ .z-portalchildren[.z-portalchildren-frame]                           (<div> — one per column)
│   ├─ .z-portalchildren-title                                          (<div> — column title; display:none unless title attr set)
│   │   ├─ .z-portalchildren-counter[.z-portalchildren-counter-on]     (<span> — panel count badge; always in DOM)
│   │   └─ [plain text node — the title string]
│   └─ .z-portalchildren-content                                        (<div id="{uuid}-cave" role="list"> — panel container)
│       ├─ .z-panel …                                                   (Panel child 1; see components/panel.md for full sub-tree)
│       ├─ .z-panel …                                                   (Panel child 2)
│       └─ <div style="height:1px;position:relative;width:1px;"></div>  (structural spacer — always present; do not style)
└─ <div class="z-clear"></div>                                          (float-clear sentinel after all columns)
```

Notes:
- The orient class (`.z-portallayout-vertical` or `.z-portallayout-horizontal`) is always appended via `domClass_()` — it is never absent.
- Default orient is `"vertical"`, producing `.z-portallayout-vertical`.
- `.z-portalchildren-title` is always in the DOM. It becomes `display: block` only when the `title` attribute is set on `Portalchildren` (the `z-portalchildren-frame` class is also added to the column root at that point).
- `.z-portalchildren-counter` (the panel-count badge) is always rendered inside `.z-portalchildren-title`; visibility is controlled by `.z-portalchildren-counter-on` toggled by `counterVisible` attribute.
- `.z-portalchildren-content` carries `id="{uuid}-cave"` and `role="list"`.
- The trailing 1px spacer `<div>` inside `.z-portalchildren-content` is emitted by the mold unconditionally; do not target it.

## Drag-and-drop DOM

During a drag, ZK manipulates the DOM as follows (CSS themes must not interfere with these classes):

- `.z-panel-move-ghost` — a full-size ghost `<div id="zk_ddghost">` prepended to `<body>` while dragging. Contains a cloned panel header plus a `.z-panel-move-block` content area.
- `.z-panel-move-block` — a placeholder `<div>` inserted into the target column's `.z-portalchildren-content` to show drop position. In vertical orient: `height: 10px; width: auto`. In horizontal orient: `height: 100%; width: 10px`.
- `.z-panel-header-move` — added to `.z-panel-header` on the draggable panel's cap element to signal that the cursor should be `move`.
- The dragged panel itself is hidden via `jq(cmp).hide()` during the drag and restored on drop.

## State classes

### On `.z-portallayout` (root)
- `.z-portallayout-vertical` — always present when `orient="vertical"` (default); emitted by `domClass_()` unconditionally.
- `.z-portallayout-horizontal` — present when `orient="horizontal"`.

### On `.z-portalchildren` (column)
- `.z-portalchildren-frame` — added when the column has a non-empty `title` attribute; triggers column chrome (border, radius, background, padding).

### On `.z-portalchildren-counter` (badge)
- `.z-portalchildren-counter-on` — added when `counterVisible="true"` (the default); removed when `counterVisible="false"`. Default state is `display: none`; with `-on` it becomes `display: inline`.

States rely exclusively on the classes above — no pseudo-class-only state changes exist on the layout shell itself. Panel child states (hover, focus, collapsed, maximized, disabled, etc.) are governed by `components/panel.md`.

## Attribute support

- `orient="vertical"` (default) → `.z-portallayout-vertical` on root
- `orient="horizontal"` → `.z-portallayout-horizontal` on root
- `maximizedMode="column"` (default) / `"whole"` → controls which panels hide when one is maximized; no CSS class emitted
- Per-column `width="N%"` or `"Npx"` → applied as inline `style.width` (percent) or `style.height` (horizontal) by JS at render time; not as a class
- Per-column `title="…"` → `.z-portalchildren-frame` added on column root; title text placed in `.z-portalchildren-title`
- Per-column `counterVisible="false"` → removes `.z-portalchildren-counter-on`

## Composition invariants

- In vertical orient, each `.z-portalchildren` floats left and stretches full height of the portallayout (`height: 100%`). The portallayout root must carry `overflow: hidden` to contain the floats.
- In horizontal orient, `.z-portalchildren-content > .z-panel` elements float left. The column itself is also `height: 100%`.
- `.z-portalchildren-content` must be `overflow: hidden` and sized `100% × 100%` to contain its panel children correctly within the column bounds.
- Column widths are set as inline styles by JS (percentage → computed pixel) via `render()`. CSS must not override these inline widths or layout will collapse.
- **No built-in inter-column gutter.** ZK renders adjacent `.z-portalchildren` columns flush against each other — the columns are bare floats and the layout provides no horizontal spacing between them. Any theme that wants a gutter must add it itself, and the only safe vehicle is an adjacent-sibling `padding-left` on the column (`.z-portalchildren + .z-portalchildren`) combined with `box-sizing: border-box`: padding lives inside the JS-set inline width, so the columns stay side-by-side, the row never overflows, and the layout's outer edges stay flush. `margin-left`/`margin-right` and negative-margin gutter tricks are **unusable** — the inline percentage widths sum to 100% and the root's mandatory `overflow: hidden` would overflow the row (positive margin) or clip the compensating negative margin.
- **No built-in gutter in horizontal orient either**, and the spacing mechanism is *not symmetric* with vertical. Two facts drive this:
  1. **Row stacking (vertical axis):** rows are transparent `.z-portalchildren` wrappers, so an adjacent-sibling `padding-top` (with `box-sizing: border-box`) sits inside the JS-set inline row `height` — the exact vertical-axis mirror of the inter-column `padding-left`. This is the safe vehicle for the inter-row gutter.
  2. **Side-by-side panels (horizontal axis):** the floated units here are the `.z-panel` cards *themselves*, with no transparent per-panel wrapper. The card chrome (border + background) is on `.z-panel`, so `padding` would open space *inside* the card, not between cards — the side gutter **must be `margin-left`** on `.z-panel + .z-panel`. Crucially, **ZK does not compute panel widths to pixels in horizontal orient** (it manages `style.height`, leaving `style.width` at the authored value — verified: a `width="50%"` panel keeps the literal `width: 50%` inline). So unlike vertical — where JS-computed pixel column widths already leave room for the gutter — horizontal side-by-side panels whose authored widths sum to 100% plus a margin will **overflow and wrap** (clipped by the row's `overflow: hidden`). The width budget for the gutter is therefore the **page author's responsibility** (e.g. `width="calc(50% - 6px)"` per panel for a two-up row with one `--zk-spacing-3` margin between them). A theme cannot reclaim this room via CSS because the author's inline width wins.
- The structural 1px spacer `<div>` at the end of `.z-portalchildren-content` ensures float-based clearance inside columns; `overflow: hidden` on the container is the float-containment strategy — do not add `clear: both` rules inside `.z-portalchildren-content`.
- `.z-panel-move-block` must render in the same flow as panels in the cave (for vertical: `width: auto; height: 10px`; for horizontal: `width: 10px; height: 100%`). The ghost node is position-absolute on `<body>`.
- Position sizing: portallayout root `overflow: hidden` is a hard requirement implied by the float strategy. Removing it causes float overflow.

## Sibling decomposition

- Column frame chrome (border, radius, background, internal padding): the `.z-portalchildren-frame` variant introduces card-like chrome on the column — mirrors `panel` card chrome pattern; see `components/panel.md`.
- Panel content rendering inside columns: fully delegated to `panel` — see `components/panel.md`.
- Drag ghost styling (`.z-panel-move-ghost`, `.z-panel-header-move`): these classes are shared with `panel`'s own drag machinery (Window/Panel use the same `Panel._startmove` / `Panel._endMove` helpers).

## Contract

`portallayout.css.dsp` — dedicated file; not bundled with other components.
Expected output path: `../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/css/portallayout.css`

## Edition

EE (zkmax.jar — `zkmax.layout.Portallayout`, `zkmax.layout.Portalchildren`)

## Notes

- The `.z-portallayout-popup` / `.z-portallayout-popup-content` / `.z-portallayout-popup-nav` / `.z-portallayout-popup-open` classes appear in the LESS source but are not referenced in any current `Portallayout.ts` or mold JS. They are legacy CSS artifacts — do not build interactive logic around them. If a future ZK version reactivates a column-picker popup feature, those selectors will need proper backing.
- The 1px spacer `<div>` inside each `.z-portalchildren-content` is a naked HTML element with no class; never target it with sibling combinators like `.z-panel + div`.
- `draggable="false"` on an individual panel prevents `_initDrag` from being called; the panel header will not receive `.z-panel-header-move` and the drag handle is effectively inactive — no class is added to indicate this at the portallayout level.
- Panel ROD (render-on-demand) is disabled for portal children (`child.z$rod0 = false`), so all panels render immediately on page load.
- The `onPortalDrop` event is fired when a drag changes a panel's column; `onPortalMove` is the legacy event name (referenced in class docs but `_endMove` fires `onPortalDrop`). CSS is not affected by these events.
