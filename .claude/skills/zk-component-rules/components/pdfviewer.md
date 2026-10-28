# pdfviewer

A PDF rendering widget that wraps Mozilla PDF.js. ZK emits a root container with an internal floating toolbar and a scrollable canvas viewport. The canvas, text selection layer, and annotation layer are all rendered by PDF.js — those elements are opaque to the theme. The theme may only style the ZK-emitted wrapper (`-container`, `-page`, `-text-layer`, `-annotation-layer`, `-toolbar`, and the buttons/controls inside the toolbar).

ZK PE and EE only (ships in `zkex.jar`).

## Wrapper boundary — T3 discipline

**Themable (ZK-emitted DOM):**
- `.z-pdfviewer` — root `<div>`; everything below is part of ZK's chrome
- `.z-pdfviewer-container` — the scrollable viewport wrapper
- `.z-pdfviewer-page` — the page-sized layer that wraps canvas + text + annotation
- `.z-pdfviewer-text-layer` — transparent text overlay for selection
- `.z-pdfviewer-annotation-layer` — transparent annotation overlay for links
- `.z-pdfviewer-toolbar` — floating control strip (absolutely positioned)
- `.z-pdfviewer-toolbar-button` — each icon button inside the toolbar
- `.z-pdfviewer-toolbar-separator` — visual divider between button groups
- `.z-pdfviewer-toolbar-page` — page count label element
- `.z-pdfviewer-toolbar-page-active` — the page-number `<input>` (also carries `z-textbox`)
- `.z-pdfviewer-toolbar-zoom` — the zoom-level `<select>` (also carries `z-selectbox`)
- `.z-pdfviewer-toolbar-fullscreen` — the fullscreen toggle button

**Forbidden (PDF.js internals — do not style):**
- `<canvas>` inside `.z-pdfviewer-page` — raw pixel output; no class
- Any `.pdfViewer`, `.page`, `.textLayer`, `.annotationLayer` classes that PDF.js may inject
- `#viewerContainer`, `#viewer`, `#toolbarContainer`, `.toolbar` — PDF.js viewer.html IDs/classes; not emitted by ZK but may appear if PDF.js viewer.html is used directly

**PDF.js classes required for functional behavior (handle with care — not pure theming surfaces):**
- `.linkAnnotation` (inside `.z-pdfviewer-annotation-layer`) — PDF.js wrapper for clickable PDF link annotations. Must be `position: absolute` so its child anchor can fill the link area. Iceblue ships this rule; themes that omit it leave PDF links non-clickable. Treat as a PDF.js-version-dependent hook, NOT a stable theming surface — if PDF.js renames or restructures these classes in a future version, the rule must follow.
- `.linkAnnotation > a` — the actual clickable anchor. Must be sized to fill its parent (`top: 0; left: 0; width: 100%; height: 100%`). Same caveat: dependency on PDF.js's annotation-layer DOM shape.

## DOM structure

```
.z-pdfviewer                               (<div>, root; position: relative; flex-column)
├─ [optional child widget redraw here]     (if a ZK child widget is added, toolbar is hidden)
├─ .z-pdfviewer-container                  (<div>, scrollable viewport; overflow: auto; flex: 1)
│   └─ .z-pdfviewer-page                   (<div>, sized to canvas by JS; position: relative)
│       ├─ <canvas id="{uuid}-content">    (PDF.js canvas — opaque, do not style)
│       ├─ .z-pdfviewer-text-layer         (<div>, transparent selection overlay)
│       └─ .z-pdfviewer-annotation-layer   (<div>, transparent link/annotation overlay)
└─ .z-pdfviewer-toolbar                    (<div>, role="toolbar"; position: absolute; bottom-centered)
    ├─ .z-pdfviewer-toolbar-button         (<button> × N — rotate R/L, first, prev, next, last, zoom-out, zoom-in)
    ├─ .z-pdfviewer-toolbar-separator      (<span> × 3 — role="separator" between button groups)
    ├─ .z-pdfviewer-toolbar-page-active    (<input type="number" class="z-textbox …">)
    ├─ .z-pdfviewer-toolbar-page           (<span> " / <total>" text)
    ├─ .z-pdfviewer-toolbar-zoom           (<select class="z-selectbox …">)
    └─ .z-pdfviewer-toolbar-fullscreen     (<button> — also carries .z-pdfviewer-toolbar-button)
```

Button order in the toolbar (left-to-right as emitted): rotate-right, rotate-left, separator, first, prev, next, last, page-active input, page span, separator, zoom-out, zoom-in, zoom select, separator, fullscreen.

## State classes

- Toolbar visibility is controlled by `opacity` via CSS, not by a state class:
  - Default (no hover, no focus): `opacity: 0` — toolbar is hidden.
  - `.z-pdfviewer:hover .z-pdfviewer-toolbar`: `opacity: 0.5`
  - `.z-pdfviewer:hover .z-pdfviewer-toolbar:hover`: `opacity: 1`
  - `.z-pdfviewer:focus-within .z-pdfviewer-toolbar`: `opacity: 1`
  - When `FOCUS_WITHIN_SUPPORT` is false (old browsers), ZK JS manually sets `style="opacity: 1"` via `_focusinToolbar()` / `_focusoutToolbar()`.

- `.z-pdfviewer-toolbar-button[disabled]` — toolbar navigation buttons (first, prev, next, last) gain the HTML `disabled` attribute when the active page is at its boundary. There is no ZK state class; use the `[disabled]` attribute selector.

- `.z-pdfviewer-toolbar-page-active:invalid` — the page-number `<input type="number" min="1" required>` fires the browser's native `:invalid` pseudo-class when the typed value is non-numeric, below `min`, or empty (because `required`). There is no ZK state class for this; the theme must style `:invalid` directly to communicate the error visually.

- Fullscreen state: when the widget enters fullscreen, the `:fullscreen` (and vendor-prefixed) pseudo-class fires on `.z-pdfviewer`. The fullscreen button's `<i>` content changes from `z-icon-expand` to `z-icon-compress` via the CSS rule `&:fullscreen .z-pdfviewer-toolbar-fullscreen > i::before`. No extra class is added.

- ZK adds no `.z-pdfviewer-disabled` or `.z-pdfviewer-readonly` class — those attributes are not supported by this widget.

## Attribute support

- `src="url"` — triggers JS PDF load; no DOM class change.
- `width` / `height` — applied as inline style on the root element by ZK's standard dimension handling; no class added.
- `zoom` / `zoomMode` — numeric or named zoom level; no DOM class change, JS resizes the canvas.
- `activePage` — sets which page is displayed; no class change.
- `rotation` — page rotation angle (0/90/180/270); no class change.

## Composition invariants

- **Toolbar is absolutely positioned, bottom-center.** It sits at `position: absolute; bottom: 1em; left: 50%; transform: translateX(-50%)`. The root must have `position: relative` and a defined height for this to work.
- **Root must be flex-column.** JS relies on the container filling the remaining height; `display: flex; flex-direction: column` on `.z-pdfviewer` is structural, not thematic.
- **Canvas is sized by JS at render time.** `_getPage0()` sets `pageLayer.style.height` / `pageLayer.style.width` to the PDF viewport dimensions. Theme CSS must not constrain the canvas or page layer dimensions.
- **Toolbar is hidden by a child widget override.** If an author adds a ZK child widget inside `<pdfviewer>`, `onChildAdded_` sets `toolbar.style.display = 'none'`. Theme CSS must not override that inline `display` or the custom toolbar will fight the built-in one.
- **Text layer uses `opacity: 0.2`** so selection highlighting shows through. This opacity is structural (the text spans are `color: transparent`); do not remove it in the theme.
- **Zoom select carries both `z-selectbox` and `z-pdfviewer-toolbar-zoom`.** Target the component via the pdfviewer-specific class to avoid accidentally restyling all selectboxes inside the widget.
- **Page-number `<input type="number">` must suppress native spin buttons.** The page-active input would otherwise render with Firefox/WebKit's default numeric spin arrows, which break the compact toolbar layout. The theme must declare `-moz-appearance: textfield` (or modern `appearance: textfield`) on `.z-pdfviewer-toolbar-page-active` and hide `::-webkit-inner-spin-button` / `::-webkit-outer-spin-button` (`-webkit-appearance: none`). Iceblue ships these rules; omitting them produces visible spin arrows that overflow the toolbar pill.

## Relational invariants

Theme-agnostic geometric/quantitative predicates. Verify by measurement with the stated tolerance; absolute pixel values are theme choice but the relation must hold.

- **Toolbar is horizontally centred inside the root.** The toolbar's visual centre-X (`toolbar.left + toolbar.width / 2`) equals the root's centre-X (`root.left + root.width / 2`) within ±2px. (Enforced by `left: 50%; transform: translateX(-50%)`; any theme that removes the transform breaks centring at arbitrary root widths.)
- **Toolbar is positioned in the bottom region of the root.** The toolbar's top edge is strictly below the midpoint of the root: `toolbar.top > root.height / 2`. (Enforced by `position: absolute; bottom: <positive value>`; the exact bottom offset is theme choice but the toolbar must be in the lower half, not the upper half.)
- **Toolbar is fully within root bounds horizontally.** `toolbar.left ≥ root.left` AND `toolbar.right ≤ root.right` (i.e. the pill-shaped strip does not overflow the root wrapper). Relevant when the toolbar is wider than half the root.
- **Toolbar buttons are square (equal width and height).** For each `.z-pdfviewer-toolbar-button`, `|button.width − button.height| ≤ 2px`. (The widget CSS enforces square buttons with equal `width` and `height`; themes may use different sizes but must not produce tall/wide non-square buttons.)
- **Toolbar icon events are suppressed.** The `<i>` child of each toolbar button has `pointer-events: none` — the click target is the `<button>` element, not the icon glyph. A theme that adds a pseudo-element with `pointer-events: auto` over the icon would break button interactivity.
- **Text-layer covers the page exactly.** `.z-pdfviewer-text-layer` has `position: absolute; left: 0; top: 0; right: 0; bottom: 0` — its bounding rect must equal that of its containing `.z-pdfviewer-page` within ±1px. A theme must not add `margin` or non-zero `inset` to the text layer.
- **Annotation links cover the full annotation area.** `.z-pdfviewer-annotation-layer .linkAnnotation > a` uses `position: absolute; top: 0; left: 0; width: 100%; height: 100%` — the anchor must fill its `.linkAnnotation` parent; themes that add `padding` to the anchor or constrain its size will reduce the clickable link area.
- **Page is horizontally centred in its container.** `.z-pdfviewer-page` has `margin: 0 auto`, so `|page.left − (container.left + (container.width − page.width) / 2)| ≤ 2px`. A theme must not set `margin-left` or `margin-right` to a non-auto value on `.z-pdfviewer-page`.
- **Container fills the root height remaining after the toolbar (when toolbar is in normal flow).** `.z-pdfviewer-container` has `height: 100%; width: 100%` inside a flex column — it should expand to fill available space. (Toolbar is `position: absolute` so it is out of normal flow; container growth is purely driven by the flex layout on root.)
- **Separator is a vertical hairline.** `.z-pdfviewer-toolbar-separator` has `width: 1px; display: inline-block; vertical-align: middle` — its rendered width must remain 1px. Themes must not set `width` to a value > 2px on the separator; its visual weight is provided by `border-left`, not by padding expansion.

## State-differs invariants

Theme-agnostic predicates: between the two named states, **at least one** of the listed properties must differ in computed style. The disjunction preserves theme freedom while keeping the state machine readable.

- **Toolbar hidden vs widget-hover-revealed.** Between `opacity` on `.z-pdfviewer-toolbar` at rest and `.z-pdfviewer:hover .z-pdfviewer-toolbar`, `opacity` must differ (resting is 0, hover-revealed is > 0). This is not a disjunction — opacity IS the mechanism; a theme that keeps both at 0 makes the toolbar permanently invisible. A theme that keeps both at 1 destroys the progressive-reveal UX.
- **Widget-hover (semi-visible) vs toolbar-hover (fully visible).** Between `.z-pdfviewer:hover .z-pdfviewer-toolbar` and `.z-pdfviewer:hover .z-pdfviewer-toolbar:hover`, `opacity` must increase: `toolbar-hover.opacity > widget-hover.opacity`. Themes may choose any two distinct values (e.g. 0.7 / 1.0, or 0.5 / 1.0) but the progression must be strictly monotone.
- **Toolbar semi-visible vs focus-within-revealed.** `.z-pdfviewer:focus-within .z-pdfviewer-toolbar` must be fully readable: `opacity` must equal 1 (or the maximum the theme uses for the fully-visible state). Focus-within must not leave the toolbar at the semi-visible level — a keyboard user entering the toolbar must see it at full opacity.
- **Button default vs button hover.** On `.z-pdfviewer-toolbar-button` vs `.z-pdfviewer-toolbar-button:hover`, at least one of: `background-color`, `color`, `box-shadow` — must differ. (Themes may use any of these as the hover signal; background-only, color-only, and combined approaches all satisfy.)
- **Button default vs button active (pressed).** On `.z-pdfviewer-toolbar-button` vs `.z-pdfviewer-toolbar-button:active`, at least one of: `background-color`, `color`, `box-shadow` — must differ, AND the active state must be visually distinct from the hover state (i.e. `background-color` OR `color` at `:active` ≠ at `:hover`). The hover-vs-active distinction matters for press affordance.
- **Button enabled vs button disabled.** Between `.z-pdfviewer-toolbar-button` and `.z-pdfviewer-toolbar-button[disabled]`, at least one of: `color`, `opacity`, `background-color` — must differ (visually reduced). AND: `cursor` at the disabled button must be `default` (not `pointer`). (Color-only dimming, opacity-only dimming, and combined approaches all satisfy.)
- **Fullscreen vs non-fullscreen.** Between `.z-pdfviewer` and `.z-pdfviewer:fullscreen`, `width` and `height` must both be `100%` (or `100vw` / `100vh`) in fullscreen — the root must expand to fill the screen. In non-fullscreen, the dimensions are theme/author-controlled. The CSS must NOT restrict the fullscreen override with a lower-specificity `max-width` or `max-height`.
- **Fullscreen icon swap.** Between `.z-pdfviewer-toolbar-fullscreen > i::before { content }` in non-fullscreen and `.z-pdfviewer:fullscreen .z-pdfviewer-toolbar-fullscreen > i::before { content }`, the `content` value must differ (expand glyph ≠ compress glyph). Themes that omit the fullscreen rule leave the icon stuck on "expand" while the viewer is already fullscreen.
- **Page-active input valid vs invalid.** Between `.z-pdfviewer-toolbar-page-active` and `.z-pdfviewer-toolbar-page-active:invalid`, at least one of: `border-color`, `outline-color`, `box-shadow` — must differ to signal invalid input. (Native browser `:invalid` styling may satisfy this on some browsers; the theme should not suppress it with a blanket `border: none` or `outline: none` on inputs without providing an equivalent.)

## Sibling decomposition

- **Root chrome (border, radius, shadow, overflow):** mirrors `window` — a thin border + radius around an opaque payload. See `components/window.md`.
- **Floating toolbar (background, border, radius, button row):** the toolbar is a self-contained floating strip. Its button styling is analogous to `toolbar` → `toolbarbutton` pattern (icon buttons with hover/active state layers). See `components/toolbar.md` for structural parallels.
- **Page-number input:** carries `z-textbox` — see the textbox CSS for baseline input styling; the pdfviewer-specific class can override sizing.
- **Zoom select:** carries `z-selectbox` — see selectbox CSS for baseline; pdfviewer-specific class can override `min-width`.

## Bundle

`pdfviewer.css.dsp` — declared in zkex's `lang-addon.xml`. Not shared with other components.

## Edition

PE / EE (zkex). Requires a valid ZK PE or EE license.

## Notes

- The `FOCUS_WITHIN_SUPPORT` flag determines whether ZK uses CSS `:focus-within` or JS event listeners to show the toolbar. Modern browsers all support `:focus-within`; the JS fallback path (inline `opacity` style) will override the CSS rule on those browsers that don't. Theme CSS should always include the `:focus-within` rule as the primary mechanism.
- The fullscreen `::before` content swap (expand → compress icon) is implemented as a CSS rule in the LESS source (`.z-pdfviewer:fullscreen .z-pdfviewer-toolbar-fullscreen > i::before { content: '\f066' }`). The theme must replicate this rule to keep the icon correct in fullscreen mode.
- The mold file (`pdfviewer.js`) emits the toolbar buttons in a fixed order; the separator `<span>` elements use `role="separator"` and `aria-orientation="vertical"`. Do not hide or remove them — they are part of the accessibility tree.
- The page-active `<input>` has `type="number"` with `min="1"` and `required`. It will fire the browser's native `:invalid` pseudo-class if the user types an out-of-range value. The theme should style this state.
