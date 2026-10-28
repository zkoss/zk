# scrollbar

The simulated (JS-drawn) scrollbar that replaces the browser-native bar on scrollable
ZK content. It is **not a widget** — there is no `<scrollbar>` tag and no entry in
`lang.xml`. It is drawn by the helper class `zul.Scrollbar`
(`~./js/zul/Scrollbar.ts`), instantiated by **MeshWidget** (grid / listbox / tree) and
**LayoutRegion** (borderlayout regions) when native scrollbars are turned off.

## When it renders (gating)

The simulated bar is created **only when `org.zkoss.zul.nativebar="false"`** on the
component (custom-attribute or library property). MeshWidget's default is
`_nativebar = true` (`MeshWidget.ts`), i.e. the browser-native scrollbar — so a plain
grid never shows the simulated bar.

```
<grid ...>
  <custom-attributes org.zkoss.zul.nativebar="false"/>   <!-- → simulated zul.Scrollbar -->
</grid>
```

## `data-embedscrollbar` → the `embed` option

`MeshWidget.ts` / `LayoutRegion.ts` read `jq(node).data('embedscrollbar') !== false` and
pass it as the `embed` option to `new zul.Scrollbar(...)`. Authored in ZUL via the client
namespace: `xmlns:ca="client/attribute"` then `ca:data-embedscrollbar="true|false"`.

- `embed` **defaults to `true`** ("change default value to true since 7.0.2") — set
  `data-embedscrollbar="false"` to opt into pure overlay mode.
- Only meaningful when `nativebar="false"`. With the native bar it is inert.

## DOM structure (from `Scrollbar.ts` `redraw()`)

`uid` = the host widget's uuid. Built per orientation (`hor` / `ver`):

```
(when embed=true) .z-scrollbar-{horizontal|vertical}-embed   ← static placeholder rail
.z-scrollbar.z-scrollbar-{horizontal|vertical}              ← root, display:none until hover
├─ .z-scrollbar-{left|up}        ← arrow button   (i.z-icon-caret-*)
├─ .z-scrollbar-wrapper
│  ├─ .z-scrollbar-indicator     ← the draggable THUMB   (i.z-scrollbar-icon.z-icon-reorder)
│  └─ .z-scrollbar-rail          ← the track/groove (transparent, click-to-page)
└─ .z-scrollbar-{right|down}     ← arrow button   (i.z-icon-caret-*)
```

- **Thumb** = `.z-scrollbar-indicator` (NOT `-thumb`). Size/position set by JS (`syncSize`),
  min length 15px. The inner `.z-scrollbar-icon` is present but kept `display:none`.
- **Track** = `.z-scrollbar-rail`.
- **Step buttons** `-up/-down/-left/-right` (each wraps a caret/chevron `<i>`) are emitted on
  every bar. Showing vs hiding them is a **theme choice** (many MD3/modern themes hide them).
  **Critical:** `syncSize()` reads each button's `offsetWidth`/`offsetHeight` to inset the
  wrapper (`vhgh = laneH − up.offsetHeight − down.offsetHeight`, horizontal analogous). So if
  you show them, give them an explicit size AND offset the wrapper by that size in CSS
  (`.z-scrollbar-vertical .z-scrollbar-wrapper { top: <btn-height> }`); if you hide them with
  `display:none` their offset is 0 and the wrapper fills the lane.

## Overlay vs embedded — the visual mechanism

`_showScrollbar(orient, opacity)` runs on `mouseenter`/`mouseleave` of the host body:

| State | `.z-scrollbar` (bar) | `*-embed` element |
|-------|----------------------|-------------------|
| pointer over body | `display:block` (thumb shown) | `display:none` |
| pointer left body | `display:none` | `display:block` (only exists when `embed=true`) |

So **overlay** (`embed=false`): the bar floats on hover and fully disappears at rest, no
reserved space, no `*-embed` element. **Embedded** (`embed=true`): a thin static
`*-embed` rail is always visible in a reserved gutter at rest; the full bar replaces it on
hover. Consequence: the `*-embed` rail must NOT have a `:hover` rule — it is
`display:none` precisely when the pointer is over the body.

### Cross-axis positioning (embedded): avoid the rest→hover jump

The scroll-sync (`_doScroll`-style handler, `Scrollbar.ts` ~L841) writes inline
`right`/`bottom = -pos` on **both** the bar and the `*-embed` rail (vertical pair gets
`right`, horizontal pair gets `bottom`) — this keeps the simulated bar glued to the visible
edge as the cave scrolls. Two consequences for a theme:

1. **The bar's and embed's cross-axis *anchor* are JS-owned and always identical** — your CSS
   `right`/`bottom` on `.z-scrollbar-vertical`/`-horizontal` and on `*-embed` is overridden
   (it's `-pos`, i.e. `0` when not scrolled). You cannot move the anchor from CSS, and you
   don't need to — JS keeps the rest rail and the hover bar on the same line.
2. **What IS yours:** the embed's `width`/`height` (cross-axis thickness), and the bar's
   *internal* layout — rail/thumb/arrows positioned **within** the lane via their own
   `right`/`bottom` relative to it.

So the jump is never an anchor problem; it's a **thickness/inset** problem. If the lane is
wider than the track and the track sits inset within it (centred), the hover track/thumb
appear inset from the shared anchor while the thin flush embed sits *at* it → lateral jump on
mouse-over. Fix: **edge-anchor the track/thumb/arrows inside the lane** (vertical → `right:0`,
horizontal → `bottom:0`; thumb 1px in) **and size the `*-embed` rail to the hover *track's*
thickness** (not the thumb's). Then the flush rest footprint equals the flush hover-track
footprint and the bar only "refines" into thumb-in-track in place. (Marble gap log 2026-06-30;
this is ZK behaviour, not a Marble choice, so it lives here.)

## CSS LOADING TRAP (theme-portable — applies to every ZK theme)

Because the scrollbar is a **helper, not a widget**, it has **no `lang.xml` css-uri and no
WPD entry** — nothing ever requests a per-component `scrollbar.css.dsp`. Stock ZK styles it
inside the global `zul/less/norm.less` bundle for exactly this reason.

**A theme MUST load its scrollbar CSS through the global bundle** (the file served by
`zk.wcs`, e.g. `norm.css.dsp`), not as a 1:1 per-component `.dsp`. If a theme's build
auto-scans component CSS into standalone `*.css.dsp` files, the scrollbar file will be
emitted but **never loaded** — the simulated bar renders completely unstyled (radius 0,
transparent thumb, no transition) and no error is raised. Same category as the other
non-widget, mold-only emitters that must be bundled globally: `notification`, `toast`,
`captcha`, `misc`. (Marble gap log 2026-06-30.)

## Related native-bar surfaces (separate from the simulated bar)

The browser-native scrollbar (`nativebar="true"`, the default) is styled per-platform via
`::-webkit-scrollbar*` pseudo-elements — in Marble: globally in `base/_reset.css` and, for
frozen grid/listbox columns, `.z-frozen-inner::-webkit-scrollbar*` in `mesh/css/frozen.css`.
These are unrelated DOM (no `.z-scrollbar*` classes) and load through their normal paths.
