# signature

A canvas-based signature capture widget. ZK renders a root `<div>` containing two stacked `<canvas>` elements (background and drawing layers) and an absolutely-positioned toolbar strip with three native `<button>` controls (Undo, Save, Clear). The drawing surface is owned by the third-party `signature_pad` library and is opaque to theme CSS; only the wrapper and toolbar elements are styleable.

ZK-EE only (ships in `zkmax.jar`).

## DOM structure

```
.z-signature                         (<div>, root; position: relative; sized by width/height attrs)
├─ .z-signature-canvas               (<canvas>, background layer; position: absolute; top: 0; left: 0; width: 100%; height: 100%)
├─ .z-signature-canvas               (<canvas>, drawing layer; position: absolute; top: 0; left: 0; width: 100%; height: 100%)
└─ .z-signature-toolbar              (<div>, action strip; position: absolute; bottom + right set by theme)
   ├─ .z-signature-tool-button       (<button>, Undo; native HTML button — not a ZK toolbarbutton)
   │  ├─ .z-signature-tool-button-undo .z-signature-tool-button-icon   (<i>, icon glyph)
   │  └─ .z-signature-tool-button-label                                  (<label>, text label)
   ├─ .z-signature-tool-button       (<button>, Save)
   │  ├─ .z-signature-tool-button-save .z-signature-tool-button-icon   (<i>)
   │  └─ .z-signature-tool-button-label                                  (<label>)
   └─ .z-signature-tool-button       (<button>, Clear)
      ├─ .z-signature-tool-button-clear .z-signature-tool-button-icon  (<i>)
      └─ .z-signature-tool-button-label                                  (<label>)
```

Both `<canvas>` elements share the class `.z-signature-canvas`. The background canvas is drawn first (lower z-index); the drawing canvas sits on top. JS sizes both via `offsetWidth * devicePixelRatio` on every `onSize` event.

The toolbar visibility is toggled by adding/removing `.z-signature-toolbar-hide` on `.z-signature-toolbar`. During active pen drawing (`beginStroke` event) the toolbar is hidden; it reappears on `endStroke`. When `toolbarVisible="false"` is set, the toolbar is permanently hidden.

## State classes

- `.z-signature-toolbar-hide` — added to `.z-signature-toolbar` when the toolbar is hidden. Two triggers: (a) `toolbarVisible="false"` on the widget; (b) transiently during active pen drawing (`beginStroke` → hidden, `endStroke` → shown). Theme must set this to `display: none`.

No disabled or readonly state class is emitted on the root. When the widget is disabled, the `[disabled]` attribute is present on the root element; themes target it via `.z-signature[disabled]`.

## Attribute support

| Attribute | Effect on DOM/classes |
|-----------|-----------------------|
| `toolbarVisible` | `false` → `_toggleToolbar()` adds `.z-signature-toolbar-hide` on `.z-signature-toolbar` permanently. `true` (default) → class removed. |
| `width` / `height` | Applied as inline style on the root `<div>`. JS reads `offsetWidth` / `offsetHeight` from the root and sizes both canvas elements. Changing these after bind calls `onSize()`. |
| `undoLabel` | Text content of `.z-signature-tool-button-label` inside the Undo button (id-suffix `-undo-label`). |
| `saveLabel` | Text content of `.z-signature-tool-button-label` inside the Save button (id-suffix `-save-label`). |
| `clearLabel` | Text content of `.z-signature-tool-button-label` inside the Clear button (id-suffix `-clear-label`). |
| `penColor` | JS-only: sets `SignaturePad.penColor`. No CSS class emitted. |
| `backgroundColor` | JS-only: fills background canvas via 2D context. No CSS class emitted. |
| `penSize` | JS-only: sets `SignaturePad.maxWidth` and `dotSize`. No CSS class emitted. |

## Composition invariants

- **Canvas fills the root.** Both `<canvas>` elements are `position: absolute; top: 0; left: 0; width: 100%; height: 100%`. The root `<div>` must be `position: relative` for this to work. Theme must not change `position` on `.z-signature` to anything other than `relative`.
- **Canvas dimensions are JS-set.** On `onSize` (resize or explicit `setWidth`/`setHeight`), JS computes `canvas.width = canvas.offsetWidth * devicePixelRatio`. If the theme changes the canvas element's `width` or `height` CSS properties, JS will override them on the next resize — style canvas geometry via the root, not the canvas itself.
- **Toolbar is absolutely positioned inside the root.** The toolbar strip sits in the bottom-right corner of the root. Exact offset is a theme choice (set via CSS `bottom` and `right`). The toolbar must remain inside the root's stacking context; `z-index` on `.z-signature-toolbar` must be high enough to sit above both canvas layers.
- **Tool buttons are native `<button>` elements.** They do NOT descend from `zul.Widget`; no ZK state classes are toggled on them. All styling (hover, focus, active, disabled) uses CSS pseudo-classes only.
- **Label is present even when empty.** The `<label>` element is always rendered; it uses `:not(:empty)` to conditionally apply left margin. Theme `:empty` selectors on `.z-signature-tool-button-label` will work correctly.

## Relational invariants

Theme-agnostic geometric/quantitative predicates. Verify by measurement with the stated tolerance; the absolute pixel value is theme choice, but the relation must hold.

- **Toolbar is contained within the root stacking context.** `.z-signature-toolbar` (position: absolute) must remain inside the bounds of `.z-signature` (position: relative). The toolbar's `bottom` and `right` offsets must be ≥ 0 so it does not overflow the root. A theme that sets negative offsets or changes `.z-signature` to `position: static` will cause the toolbar to escape the widget boundary. (iceblue enforces via `position:relative` on root + `position:absolute` on toolbar — any theme must do the equivalent.)
- **Canvas layers fill the root exactly.** Both `.z-signature-canvas` elements must have computed `width` and `height` equal to the root's `clientWidth` / `clientHeight` respectively: `|canvas.width − root.clientWidth| ≤ 1px` and `|canvas.height − root.clientHeight| ≤ 1px`. These elements are `width:100%; height:100%; position:absolute; top:0; left:0` — a theme that adds `margin` or `padding` to `.z-signature-canvas`, or that sets `box-sizing` differently, will cause geometry drift and JS pixel mismatch on `onSize`. (iceblue enforces via the `width:100%;height:100%` declaration on `.z-signature-canvas`.)
- **Toolbar z-order is above both canvas layers.** The computed paint order of `.z-signature-toolbar` must be above both `.z-signature-canvas` elements (the toolbar must be clickable and not occluded). In practice: `.z-signature-toolbar` must have a `z-index` value that renders it on top of the absolute-positioned canvas siblings within the root stacking context. A theme that removes or lowers the toolbar's z-index to ≤ 0 makes tool buttons unreachable on browsers that honor stacking-context order strictly.
- **Tool buttons are horizontally separated.** Adjacent `.z-signature-tool-button` siblings within the toolbar must have a visible gap ≥ 2px between their bounding rects. (iceblue achieves this via `margin-right` on each button. Themes may use `gap` on a flex toolbar or `margin` on buttons — method is free, gap must be non-zero.)
- **Icon glyph does not vertically protrude above the button.** `.z-signature-tool-button-icon` must have `vertical-align` set to `bottom`, `middle`, or equivalent so the icon glyph aligns with or within the button's text baseline region. The icon's top edge must be ≥ the button's padding-top inner edge: icon must not cause the button to grow beyond its min-height by being baseline-misaligned. (iceblue uses `vertical-align:bottom` on the icon element.)
- **Label margin is non-negative when label is present.** For `.z-signature-tool-button-label:not(:empty)`, the computed `margin-left` must be ≥ 0px. A negative margin would collapse the icon-to-label visual gap and cause overlap. The exact gap size is theme choice. (iceblue enforces a fixed `margin-left` on `:not(:empty)` — any theme must do the equivalent to avoid icon/label overlap.)

## State-differs invariants

Theme-agnostic predicates that two states must be visually distinguishable. Verify by rendering both states and comparing computed styles on the specified selector; assert that **at least one** of the listed properties differs. The disjunction gives themes design freedom while keeping the state machine readable.

- **`tool-button :hover` vs resting.** On `.z-signature-tool-button:hover` compared to `.z-signature-tool-button` (resting), at least one of: `background-color`, `border-color` (any side), `color` — differs. (iceblue changes all three; Marble may use any subset. Either satisfies.)
- **`tool-button :focus` vs resting.** On `.z-signature-tool-button:focus-visible` compared to `.z-signature-tool-button` (resting), at least one of: `outline` (with non-zero outline-width), `box-shadow`, `border-color`, `background-color` — differs in a way perceivable to a keyboard user (e.g. an outline ring ≥ 2px or an equivalent shadow). (iceblue targets `:focus` broadly; any theme should implement `:focus-visible` for keyboard-only affordance.)
- **`tool-button :active` vs resting.** On `.z-signature-tool-button:active` compared to `.z-signature-tool-button` (resting), at least one of: `background-color`, `border-color`, `color` — differs. The pressed state must be visually distinct from both resting and hover. (iceblue changes all three on `:active`.)
- **`toolbar-hide` vs toolbar visible.** When `.z-signature-toolbar-hide` is present on `.z-signature-toolbar`, the toolbar must not be visible or interact-able: computed `display` must equal `none` OR computed `visibility` must equal `hidden` OR computed `opacity` must equal `0` AND `pointer-events` must equal `none`. The simplest and iceblue-mandated form is `display:none`. Themes must not weaken this to a mere opacity reduction that leaves the toolbar in the event-hit path during drawing.
- **`root :focus-within` vs root resting (active drawing signal).** On `.z-signature:focus-within` compared to `.z-signature` (resting), at least one of: `border-color`, `border-width`, `outline`, `box-shadow` — differs, signalling that the canvas is in active use. (iceblue does not define a `:focus-within` selector — this is a gap in the baseline. Any theme must supply this state to meet modern accessibility expectations; the specific property is theme choice.)
- **Disabled vs enabled root.** On `.z-signature[disabled]` compared to `.z-signature` (not disabled), at least one of: `opacity` (< 1), `pointer-events` (`none`), `filter` — differs, signalling that the whole widget is inactive. Additionally, the tool buttons inside a disabled root must not respond to hover/focus/active state changes (suppressed via `pointer-events:none` on the root or equivalent). (iceblue does not define a `.z-signature[disabled]` selector — this is a gap in the baseline. Any theme must supply this to avoid misleading users that a disabled widget is interactive.)

## Sibling decomposition

- **Wrapper chrome** (border, radius, relative positioning): novel — no ZK sibling renders a canvas wrapper with this exact structure.
- **Toolbar strip** (absolute positioning, button row): mirrors toolbar chrome pattern — see `components/toolbar.md` for the `padding on content div, not root` rule. Here the toolbar strip is a `<div>`, not the ZK `<toolbar>` widget, but the layout principle (buttons in a horizontal row with spacing) applies.
- **Tool buttons** (hover / focus / active states): these are native `<button>` elements styled to look like ZK's secondary outlined button. See `components/button.md` for the outlined-button state pattern.

## Bundle

`css/signature.css.dsp` — declared as the `css-uri` for the `signature` widget's `default` mold in zkmax's `lang-addon.xml`. No other component shares this file.

## Edition

EE (zkmax). Requires a valid ZK EE license. The widget class is `zkmax.signature.Signature`.

## Notes

- The two `<canvas>` elements share the class `.z-signature-canvas`. There is no way to distinguish them by class alone. If a theme needs to target only one, use `:first-of-type` / `:last-of-type` within `.z-signature` — but in practice the canvas is off-limits (`forbidden-selectors`) and this distinction is moot.
- `penColor`, `backgroundColor`, and `penSize` are JS-constructor options passed to `SignaturePad`. They cannot be driven by CSS variables without modifying `Signature.ts`. Treat them as `ESCALATED_LIBRARY_CONFIG`.
- The toolbar hides transiently during pen drawing. This is a JS behavior the theme cannot and should not override. The `.z-signature-toolbar-hide { display: none }` rule must be present so the transition works correctly.
- `toolbarVisible` setter calls `_toggleToolbar()` which uses jQuery's `toggleClass`. There is no server-side sclass involved.
- **The tool-button icon `<i>` carries NO glyph by itself.** Its classes are `z-signature-tool-button-{undo,save,clear}` (plus `z-signature-tool-button-icon`) — these are NOT `z-icon-*` classes, so a mask-based theme's generic `[class^="z-icon-"]::before` machinery does not apply and the icon renders blank until the theme supplies it. The default ZK theme draws them via FontAwesome glyphs (`\f112` undo, `\f0c7` save, `\f00d` clear) on `…-{undo,save,clear}:before`. A mask-based theme must instead set `--_icon` (a Lucide SVG data-URI) on each of the three classes and add a `.z-signature-tool-button-icon::before { mask: var(--_icon) … }` rule. Sensible Lucide equivalents: `undo-2`, `save`, `eraser`. For icon-only buttons, also hide `.z-signature-tool-button-label` (`display:none`).
