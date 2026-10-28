# goldenlayout

A dockable multi-pane layout container powered by GoldenLayoutJS (v2.x). ZK renders a thin wrapper `<div class="z-goldenlayout">` and delegates all internal DOM — tabs, headers, splitters, pane content areas — to GoldenLayoutJS. ZK child components (`<goldenpanel>`) are registered as GoldenLayout "components" and their `$n()` nodes are injected into GoldenLayout's `.lm_content` divs at runtime.

Two ZK widget classes exist: `GoldenLayout` (the outer container, zclass `z-goldenlayout`) and `GoldenPanel` (each draggable pane, zclass `z-goldenpanel`). The GoldenPanel DOM node is injected by JS *inside* the GoldenLayoutJS-owned `.lm_content` element.

## DOM structure

```
div.z-goldenlayout                    (root, position:relative, JS writes absolute children inside)
└─ [GoldenLayoutJS-injected DOM]
   ├─ .lm_root                        (GoldenLayout root item)
   │  ├─ .lm_row  OR  .lm_column
   │  │  ├─ .lm_stack                 (one per pane group)
   │  │  │  ├─ .lm_header             (tab strip + controls bar)
   │  │  │  │  ├─ .lm_tabs            (positioned container for tab items)
   │  │  │  │  │  └─ .lm_tab          (one per tab; .lm_active on selected)
   │  │  │  │  │     ├─ .lm_title     (tab label text)
   │  │  │  │  │     └─ .lm_close_tab (×  icon when closable=true)
   │  │  │  │  └─ .lm_controls        (far-right icon buttons)
   │  │  │  │     ├─ li.lm_maximise   (↗ maximize icon)
   │  │  │  │     └─ li.lm_close      (× close-stack icon)
   │  │  │  └─ .lm_items              (content area container)
   │  │  │     └─ .lm_item.lm_component.lm_content
   │  │  │        └─ div.z-goldenpanel (ZK widget node inserted here)
   │  │  └─ .lm_splitter              (resize handle between stacks)
   │  │     ├─ .lm_horizontal         (vertical divider; cursor: ew-resize)
   │  │     └─ .lm_vertical           (horizontal divider; cursor: ns-resize)
```

GoldenLayout uses absolute/float positioning internally — the JS engine sets `top`, `left`, `width`, `height` inline on `.lm_stack`, `.lm_items`, etc. Do not set `position`, `top`, `left`, `width`, `height` via CSS on `.lm_*` elements; only color, background, border, and typographic properties are safe to override.

A hidden `<div class="z-goldenlayout"><div id="uuid-styleIndicator" class="lm_header"></div></div>` is temporarily injected by the mold to let `bind_` read the CSS `min-height` of `.lm_header`. It is removed after `bind_` runs and is never visible.

During drag, a `.z-goldenlayout-dragProxy` div is appended to the document root (detached from the `.z-goldenlayout` node). It contains a mirror of the dragged tab's `.lm_header .lm_tabs`.

## State classes

### On `.lm_tab` (per-tab states)
- `.lm_active` — added by GoldenLayoutJS when this tab is the selected/active pane. ZK fires `onActive` on the corresponding `<goldenpanel>` when this changes.
- Default (no extra class) — tab is visible but not selected.

### On `.lm_stack` (per-stack states)
- `.lm_maximised` — added when the stack is expanded to fill the full layout. ZK fires `onMaximize` on each contained panel.
- `.lm_left`, `.lm_right`, `.lm_bottom` — GoldenLayoutJS adds these when the tab strip is oriented on that side (vertical tab layout). Default (top) has no orientation class.
- `.lm_docked` — GoldenLayoutJS docked/pinned stack variant (rare).

### On the `.lm_splitter` (divider states)
- `.lm_horizontal` — the splitter is a vertical bar separating left/right panes.
- `.lm_vertical` — the splitter is a horizontal bar separating top/bottom panes.
- `.lm_dragging` — added to the splitter (and to `<body>`) while the user is dragging the resize handle.

### On `.z-goldenpanel`
- No ZK-added state classes. The panel content is a plain div. GoldenLayout adds no class to the panel node itself.

### On `.z-goldenlayout` (outer wrapper)
- No ZK-added state classes beyond the root zclass. `disabled`/`readonly` are not supported by GoldenLayout.

## Attribute support

- `hflex` / `vflex` on `<goldenlayout>` — standard ZK flex; the outer wrapper expands to fill its parent.
- `orient="vertical"` on `<goldenlayout>` — changes the root layout direction; no CSS class emitted, JS-only behaviour.
- `areas` attribute on `<goldenlayout>` — grid string defining how panels are laid out; JS-only, no CSS effect.
- `area=` on `<goldenpanel>` — places the panel in the named cell; JS-only.
- `title=` on `<goldenpanel>` — text shown in `.lm_title` inside the tab.
- `draggable="false"` on `<goldenpanel>` — prevents JS drag listener from being attached; no CSS class emitted.
- `droppable="false"` on `<goldenpanel>` — disables drop targets for this stack; no CSS class emitted.
- `closable="false"` on `<goldenpanel>` — hides the `.lm_close_tab` icon for this tab; no CSS class emitted.

## Composition invariants

- Header height is read from the computed `min-height` of `.lm_header` at `bind_` time and passed into `config.dimensions.headerHeight`. The CSS `min-height` on `.lm_header` is therefore load-bearing: change it and the entire layout recalculates.
- **Header-height sync rule (any theme — found as a shipped 1px clip 2026-06-05):** the *rendered* `.lm_header` height MUST equal that `min-height` exactly. GoldenLayout sizes `.lm_items`/`.lm_content` with inline heights computed as `stack − headerHeight`; if the real header is taller (tab block-padding pushing content past `min-height − border`, or a content-box border adding to the box), flexbox shrinks `.lm_items` below GL's inline child heights and `.lm_items { overflow: hidden }` clips the panel's bottom edge (border included) by exactly the delta. Safe pattern: `box-sizing: border-box` on `.lm_header`, let tabs stretch into the strip (`.lm_tabs { align-self: stretch }`, `.lm_tab { padding-block: 0 }`) — never let tab content drive the header height.
- Splitter width (horizontal) and height (vertical) are controlled by `config.dimensions.borderWidth` (set to `8` in `bind_`). The CSS width/height of `.lm_splitter` does not need to be set — GoldenLayout sets it inline.
- **GoldenLayout's bundled stylesheets (goldenlayout-base.css + a light/dark theme) are never loaded in ZK's integration.** ZK's upstream codegen `goldenlayout.css.dsp` (at `zkcml/zkmax/codegen/resources/web/js/zkmax/goldenlayout/css/`) reproduces the ENTIRE goldenlayout-base.css verbatim at the top of the file — every theme must carry the structural subset or library features visibly break (found as two shipped bugs 2026-06-05; same pattern as cropper/Jcrop). The must-have rules:
  - `.lm_maximised { position: absolute; top: 0; left: 0; z-index: 40 }` — without it, the maximised stack stays a flex/float item at partial width with siblings beside it. **Specificity trap:** if the theme also sets `position: relative` on `.lm_item.lm_stack` (0,3,0 with wrapper scope), the maximised rule needs ≥ that specificity (e.g. `.z-goldenlayout .lm_item.lm_stack.lm_maximised`).
  - `.lm_maximise_placeholder { display: none }`
  - `.lm_dropTargetIndicator { display: none; position: absolute; z-index: 20 }` — otherwise it renders as a permanently visible in-flow element at rest (GL toggles display inline only during drag). In ZK the node carries both `lm_dropTargetIndicator` and `z-goldenlayout-dropTargetIndicator` classes.
  - `.lm_transition_indicator { display: none; ... }` — the node is appended to `<body>`, OUTSIDE `.z-goldenlayout`; the selector must be unscoped.
  - `.lm_dragProxy { position: absolute; top: 0; left: 0; z-index: 30 }` (ZK class: `z-goldenlayout-dragProxy`, also at `<body>`).
  - `.lm_dragging, .lm_dragging * { cursor: move !important; user-select: none }` — GL adds `lm_dragging` to `<body>` during drags; without `user-select: none`, dragging selects page text.
  - `.lm_splitter { z-index: 2 }` plus `col-resize`/`row-resize` cursors on `.lm_horizontal`/`.lm_vertical` (the library never provides cursors in ZK).
  - State-dependent icons the base/theme CSS normally draws: `.lm_maximised … .lm_maximise::after` flips to a restore glyph; `.lm_controls .lm_tabdropdown::before` needs an overflow-trigger glyph (hidden inline by GL until tabs overflow).
- `.lm_splitter` hosts no button element and no `setBtnPos_`-style JS centering (unlike the three ZK-mold splitters) — both `::before` and `::after` pseudo-elements are free for themes to draw a handle/pill, and pure-CSS centering is safe on both axes.
- The `.z-goldenpanel` node is not a direct child of `.z-goldenlayout` in the live DOM; it is nested inside `.lm_content > .lm_item`. CSS selectors that assume `.z-goldenlayout > .z-goldenpanel` will not match.
- `.z-goldenlayout-dragProxy` appears at document root during drag, not scoped inside `.z-goldenlayout`. Selectors like `.z-goldenlayout-dragProxy .lm_tab` will match it independently.
- The `.lm_tab::after` pseudo-element slot is used for the active-tab underline indicator. The pseudo-element is generated inside `.lm_active` tabs via `.lm_tab.lm_active::after` — it has `display: block` and a fixed height; themes set the background-color and margin.

## Sibling decomposition

- Tab chrome (tab strip, active indicator, header background): similar in intent to `tabbox` (see `components/tabbox.md`), but implemented via `lm_*` classes — the structural parallel informs the design pattern.
- Splitter resize bar: parallels `splitter` (see `components/splitter.md`) in function; same hover/drag visual expectations.
- Pane content area (`.z-goldenpanel`): analogous to a plain surface container — no ZK component equivalent.

## Contract

`goldenlayout.css.dsp` — shipped as `../zkcml/zkmax/src/main/resources/web/js/zkmax/goldenlayout/css/goldenlayout.css`, which ZK loads as `goldenlayout.css.dsp`.

## Edition

EE (zkmax)

## Notes

- **theme-bridge approach**: GoldenLayoutJS ships its own base CSS bundled in `goldenlayout.js`. ZK themes do NOT import a GoldenLayout light/dark theme file — they override the `lm_*` selectors directly under `.z-goldenlayout` scope. Write all `lm_*` style rules scoped under `.z-goldenlayout .lm_*` in `goldenlayout.css`. GoldenLayout 2.x exposes no CSS custom properties, so there is no variable bridge.
- **The `forbidden-selectors: .lm_*` rule in the previous contract was wrong.** The `lm_*` namespace is library-internal DOM but it is fully CSS-reachable and must be styled. Scoping under `.z-goldenlayout` prevents bleed.
- **`box-sizing: content-box !important`**: GoldenLayoutJS's structural CSS sets `box-sizing: content-box !important` on all `[class^=lm_]` descendants of `.lm_header`. Any border or padding added to `.lm_header` child elements must account for this.
- **Icon rendering**: ZK's shipped LESS uses FontAwesome glyph codes (`\f00d` for ×, `\f065` for expand, `\f066` for restore) via `::after` pseudo-elements on `.lm_controls li` and `.lm_close_tab::before`. Any theme must supply icon content for `.lm_close::after`, `.lm_maximise::after`, and `.lm_close_tab::before` — either FontAwesome codes or alternative `content:` values.
- **`.lm_close_tab` carries a JS-written inline `display: inline-block`** — ZK's bundled GL fork (`zkmax .../goldenlayout/ext/goldenlayout.js`, Tab constructor: `this.closeElement[ isClosable ? 'show' : 'hide' ]().css('display', 'inline-block')`) chains an unconditional inline display write that upstream golden-layout 1.x does NOT have. Consequences for any theme: (1) a stylesheet `display` declaration on `.lm_close_tab` is dead CSS (inline style wins) unless marked `!important`; (2) flex/grid centering *on the element itself* never engages — center the icon pseudo display-independently instead (`position: relative` on the element + `position: absolute; inset: 0; margin: auto` on the pseudo); (3) because the element is a flex item of `.lm_tab` in flex-based headers, the inline `inline-block` blockifies to `block` — computed `display: block` is expected, not a bug.
- **Dropdown**: when more tabs exist than fit in the header, a `.lm_tabdropdown` control shows a dropdown arrow. The overflow list is rendered as a `<ul>` given the class `z-goldenlayout-dropdown` and positioned absolutely in the viewport. This is a ZK-assigned class (not an `lm_` class), so it is fully under theme control.
- **Drop-target indicator**: `.z-goldenlayout-dropTargetIndicator` (again a ZK-assigned class, not `lm_*`) wraps a `.lm_inner` div. The outer element receives a dashed border; the inner receives a translucent fill. Both are theme-owned.
