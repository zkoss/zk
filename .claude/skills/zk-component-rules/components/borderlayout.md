# borderlayout

A 5-region container layout (north, south, east, west, center). ZK's JavaScript layout engine computes pixel coordinates for each region and writes them as inline `style="left:…; top:…; width:…; height:…"` on each region's `-real` sub-element. The outer `.z-borderlayout` is the coordinate origin (`position: relative`).

## JS layout engine: do NOT use `display: flex` or `display: grid` on the root

All regions are positioned by the JS engine via inline absolute coordinates. If you apply `display: flex` or `display: grid` to `.z-borderlayout`, the engine's inline `left`/`top`/`width`/`height` values fight the layout model and produce visual chaos. **Never apply flex/grid to `.z-borderlayout`.**

Style each region individually (`.z-north`, `.z-west`, etc.) — background, borders, overflow, and font treatments on those elements do not interfere with the engine.

## DOM structure

Each region follows the same wrapper pattern. The outer `<div id="{uuid}">` is the ZK widget root (no meaningful class — skip it for CSS). The `-real` element is what the JS engine sizes.

```
.z-borderlayout                             (<div> — coordinate origin, position:relative)
├─ <div id="{uuid}">                        (ZK widget root — no class; transparent wrapper)
│   └─ .z-north|-real                       (<div> — JS positions this element absolutely)
│       ├─ .z-north-header                  (<div> — optional title bar, only if title attr set)
│       │   └─ <i .z-borderlayout-icon …>   (collapse icon in header, shown if collapsible=true)
│       └─ .z-north-body                    (<div> — scrollable content area = -cave in source)
├─ <div id="{uuid}">
│   └─ .z-south-real
│       ├─ .z-south-header                  (optional)
│       └─ .z-south-body
├─ <div id="{uuid}">
│   └─ .z-west-real
│       ├─ .z-west-header                   (optional)
│       └─ .z-west-body
├─ <div id="{uuid}">
│   └─ .z-east-real
│       ├─ .z-east-header                   (optional)
│       └─ .z-east-body
└─ <div id="{uuid}">
    └─ .z-center-real
        └─ .z-center-body
```

The splitter and collapsed-placeholder sub-elements are rendered **inside the same widget wrapper** as their region, after the `-real` div:

```
<div id="{uuid}">                           (same wrapper as the west region above)
    .z-west-real  (+ children above)
    .z-west-splitter                        (<div> — draggable bar; position:absolute by JS)
    │   └─ .z-west-splitter-button          (<span> — pill-shaped collapse toggle)
    │       ├─ .z-west-icon.z-icon-ellipsis-v   (<i> — grip dots, top)
    │       ├─ .z-west-icon.z-icon-caret-left   (<i> — direction caret)
    │       └─ .z-west-icon.z-icon-ellipsis-v   (<i> — grip dots, bottom)
    .z-west-collapsed                       (<div> — placeholder shown when region is closed)
        ├─ .z-borderlayout-icon             (<i> — expand icon on placeholder, open direction)
        └─ .z-west-title                    (<div> — rotated title text inside placeholder)
```

Note: `.z-center` has **no** splitter, collapsed placeholder, or header collapse button — center is never collapsible.

## Exact icon classes per region

The mold (`layoutregion.js`) selects icons by position:

| Region | Ellipsis class (grip) | Caret class (direction) |
|--------|----------------------|------------------------|
| west   | `z-icon-ellipsis-v`  | `z-icon-caret-left`    |
| east   | `z-icon-ellipsis-v`  | `z-icon-caret-right`   |
| north  | `z-icon-ellipsis-h`  | `z-icon-caret-up`      |
| south  | `z-icon-ellipsis-h`  | `z-icon-caret-down`    |

The splitter button always renders three icons in order: ellipsis (grip) → caret → ellipsis (grip).

For the collapsed placeholder's expand icon (`getIconClass_(true)` — "collapsed=true" argument):

| Region | Icon when collapsed (click to expand) |
|--------|--------------------------------------|
| north  | `z-icon-angle-double-down`            |
| south  | `z-icon-angle-double-up`              |
| west   | `z-icon-angle-double-right`           |
| east   | `z-icon-angle-double-left`            |

## Class naming — region prefix replaces "borderlayout"

Each region uses its own name as the zclass prefix. There is no `.z-borderlayout-north` class. The region roots are:

- `.z-north` (not `.z-borderlayout-north`)
- `.z-south`
- `.z-west`
- `.z-east`
- `.z-center`

The only `.z-borderlayout-*` classes are:
- `.z-borderlayout` — the container root
- `.z-borderlayout-icon` — the icon elements inside every region (shared class, used by `_fixFontIcon`)

## State classes

### Region open/closed
- `_open = true` (default) — the `-real` element is shown; the `-split` bar is visible; the `-colled` element is `display:none`.
- `_open = false` — the `-real` element is hidden; `-split` has `display:none`; the `-colled` placeholder element is shown.
  - There is no ZK-added state class for collapsed vs. open — visibility is toggled via JS `display` style manipulation on the sub-elements.

### Region collapsible / closable
- When `collapsible="true"` and `closable="true"` (defaults): the splitter button is rendered without the `-disabled` modifier class.
- When `collapsible="false"` OR `closable="false"`: ZK adds `.z-{region}-splitter-button-disabled` to the splitter button `<span>`. The collapse icon inside is hidden by inline `style="display:none"`.

### Border mode
- Default: `border="normal"` — no modifier class added.
- `border="none"` → ZK adds `.z-{region}-noborder` to the `-real` element (via `domClass_`).

### Nested borderlayout
- When a borderlayout is nested inside a region, ZK adds `.z-{region}-nested` to the region's root wrapper div.

### Slide state
- When a collapsed region is "slid" (previewed without fully opening): ZK adds `.z-{region}-slide` to the outer wrapper div. This is a transient animation state.

## Attribute support

- `title="…"` → renders `.z-{region}-header` + title text + collapse icon inside the region; also populates `.z-{region}-title` inside the collapsed placeholder.
- `splittable="true"` → makes `-split` bar visible (`display:block`); without this the bar is `display:none`.
- `collapsible="true"` → enables the collapse toggle button inside the splitter button.
- `closable="true"` (default) → button is shown; `closable="false"` adds `-splitter-button-disabled` class and hides the caret icon.
- `border="none"` → adds `.z-{region}-noborder` to the `-real` element.
- `open="false"` → starts region collapsed (placeholder visible, real hidden).
- `autoscroll="true"` → JS sets `overflow:auto; position:relative` on the `-body` element.

## Positioning model

The JS engine (`Borderlayout._resize`) calculates pixel coordinates for each region in order: north → south → west → east → center. It writes `left`, `top`, `width`, `height` as inline styles directly on each `-real` element. The splitter bar (`-split`) is also absolutely positioned by JS.

Implications for CSS:
- Do NOT set `position`, `left`, `top`, `width`, or `height` on `-real` elements — JS overwrites them.
- DO set `background-color`, `border`, `overflow`, `z-index`, and font properties on the region elements.
- The splitter's `width` (west/east) or `height` (north/south) must be explicitly declared in CSS because `_getRegionSize` reads `offsetWidth`/`offsetHeight` to compute the available space for neighboring regions. If the splitter has no explicit size, the layout collapses to 0.

## Splitter button positioning

The splitter button (`-splitbtn`) is centered on the splitter bar by JS (`setBtnPos_`):
- For vertical regions (north/south): `margin-left` is set by JS to center the button horizontally on the bar.
- For lateral regions (west/east): `margin-top` is set by JS to center the button vertically on the bar.

The button itself uses `position:absolute` relative to the splitter bar. CSS should set `width`/`height` and styling but leave `top`/`left` unset (JS manages centering via margin injection).

This is a **family-wide ZK pattern**: the identical `setBtnPos_` inline-margin centering exists in `zul.box.Splitter` (`Splitter.ts`) and zkmax Splitlayout (`Splitlayout.ts`). Never **half-mix** CSS and JS centering on the same axis in any of the three — either leave the axis fully to JS, or take CSS ownership by neutralizing the inline margin (`margin-left/top: 0 !important`) and centering via `left/top: 50% + transform`. CSS ownership is required when the bar's long-axis size is flex-resolved: `setBtnPos_` can run while the offset is still 0 and writes margin 0 permanently (verified in splitlayout 2026-06-04) — see `components/splitter.md` / `components/splitlayout.md` for the full timing-trap notes.

## Z-index map

ZK's JS engine assigns z-indices:

| Region  | z-index |
|---------|---------|
| north   | 16      |
| south   | 14      |
| west    | 12      |
| east    | 10      |
| center  | 8       |
| splitter (theme) | 20 (must be > region z-index) |

Do not override these values — the JS engine relies on them for hit-testing.

## Title rotation rule (west/east collapsed placeholder)

When a west or east region is collapsed, the region's title appears inside the `.z-west-collapsed` / `.z-east-collapsed` placeholder. The JS engine calculates the width of the title element (`-title`) at resize time, so it can fit within the collapsed strip. CSS may apply `transform: rotate(90deg)` to the title but must use `white-space: nowrap` and avoid width constraints that would defeat JS sizing.

## Composition invariants

- Splitter bar thickness (west/east: `width`; north/south: `height`) MUST be declared in CSS. The JS engine reads `offsetWidth`/`offsetHeight` of the split element to subtract from the available region space. If zero, neighboring regions bleed into each other.
- Splitter button's `position: absolute` must be preserved — JS injects `margin-left` or `margin-top` to center it on the bar.
- The `.z-splitter-ghost` element created during drag (`_ghosting` static method) is prepended to `document.body` with a hard-coded inline background color set by ZK's JS. Theme CSS on `.z-splitter-ghost` can override `background-color` and `opacity` since CSS specificity beats inline styles only when `!important` is used — or the theme can rely on the fact that the ghost is short-lived and the JS-set color is acceptable. Best practice: declare `.z-splitter-ghost { background-color: … !important; opacity: … !important; }` if the theme wants full control.
- Center region: never renders `-split`, `-colled`, or a collapse icon. Applying splitter CSS to `.z-center-splitter` is harmless (that element does not exist) but wastes specificity.

## Sibling decomposition

This component is a novel composite — no single ZK sibling replicates the 5-region JS-positioned pattern. Sub-element references:
- Drag-bar vocabulary: see `components/splitter.md` for ghost element and cursor conventions.
- Region header vocabulary: see `components/panel.md` for the header-height/typography pattern.

## Contract

`layout.css.dsp` (covers borderlayout + hlayout/vlayout + anchorlayout).

## Edition

CE

## Notes

- The outer `<div id="{uuid}">` wrappers that ZK emits around each region have no CSS class. CSS selects the `-real` element or its children, not this wrapper.
- The mold renders the splitter and collapsed elements as siblings of `-real` inside the same wrapper — they are not children of `-real`.
- `_fixFontIcon()` reruns CSS on `.z-borderlayout-icon` after every open/close animation to resolve icon font glyph rendering. This is a ZK internal — do not suppress it.
- `animationDisabled` on the parent borderlayout skips all slide animations; the CSS transitions on splitter hover are unaffected.
- `autoscroll="true"` causes JS to set `overflow:auto; position:relative` directly on the `-body` element's inline style, overriding any CSS `overflow` rule on `-body`. If you need `overflow:hidden` on the body, it cannot be enforced via CSS alone when autoscroll is active.
