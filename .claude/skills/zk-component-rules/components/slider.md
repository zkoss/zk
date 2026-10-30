# slider

A draggable handle on a track. Supports four molds: **default**, **sphere**, **scale** (all CE), and **knob** (EE — rotary SVG dial).

## State support: minimal

Slider has no `disabled`, `readonly`, `invalid` or `inplace` property (`Slider.java` and `Slider.ts` define none, and `XulElement` does not add one), so it emits no state class or attribute for them. Its only interactive states are the pseudo-classes in "State classes" below. Rangeslider and multislider differ; see "State classes".

## Molds

Four molds exist. Three are CE; one is EE:

- **default** — round flat knob on a linear track (CE)
- **sphere** — gradient sphere on a linear track (CE, `mold="sphere"`)
- **scale** — knob with a vertical scale indicator on a linear track (CE, `mold="scale"`, horizontal only)
- **knob** — rotary SVG dial; completely different DOM (EE, `mold="knob"`, registered by zkmax `lang-addon.xml`)

For the **default** mold: only the orientation class is added (no mold class).

For the **sphere** mold: `domClass_()` adds both the orientation class AND `.z-slider-sphere` to the root.

For the **scale** mold (horizontal only): `domClass_()` adds both the orientation class AND `.z-slider-scale` to the root. (Scale is blocked for vertical by a ZK `WrongValueException`.)

Source: `Slider.domClass_` in `Slider.ts`:
```
if (this.inSphereMold())
    sclsHTML += ' ' + this.$s('sphere');
else if (this.inScaleMold() && !isVertical)
    sclsHTML += ' ' + this.$s('scale');
```

For the **knob** mold: `domClass_()` returns early — **no orientation class is added** to the root. The root receives only `.z-slider` (plus any sclass). This is a critical distinction for CSS targeting.

## DOM structure — default / sphere / scale molds

```
.z-slider[.z-slider-horizontal | .z-slider-vertical][.z-slider-sphere | .z-slider-scale]
                                               (root <div>, role="slider")
└─ #uuid-inner  .z-slider-center               (track container, aria-hidden)
   ├─ #uuid-area  .z-slider-area               (filled portion; position: absolute, width/height set by JS)
   └─ #uuid-btn   .z-slider-button             (draggable thumb)
```

Mold root classes:
- Default mold — no mold class on root; only orientation class.
- Sphere mold — `.z-slider-sphere` on root in addition to orientation class.
- Scale mold — `.z-slider-scale` on root in addition to `.z-slider-horizontal` (vertical not supported for scale).

Note: the DOM structure above comes from `mold/slider.js`. The element IDs use the ZK uuid pattern; the sub-ID suffix (`-inner`, `-area`, `-btn`) is accessed via `this.$n('inner')` etc. in JS.

The `.z-slider-center` inner element's width (horizontal) or height (vertical) is set by JS via `_fixSize()` inline style. CSS must not constrain this element's dimension in a way that overrides the inline style.

## DOM structure — knob mold (EE)

```
.z-slider                                   (root <div>; NO orientation class)
├─ <svg>  .z-slider-knob-svg                (the dial; aria-hidden, cursor: pointer)
│  ├─ <path>  .z-slider-knob-inner          (background arc track; fill: none; stroke drawn by JS)
│  └─ <path>  .z-slider-knob-area           (filled arc; fill: none; stroke drawn by JS; d= attribute removed at 0%)
└─ <input type="number">  .z-slider-input   (numeric input overlay; position: absolute; top/left/width/height/font-size all set by JS inline style)
```

Source: `zkmax/slider.ts` `knob` mold function (`zk.augment(zul.inp.Slider.molds, { knob(...) }`).

## CSS-themable boundary for the knob mold

The knob mold uses SVG `<path>` elements with `stroke` (not `fill`) for the arc visuals. The stroke colors ARE CSS-controlled via:

- `.z-slider-knob-area { stroke: <color>; }` — the filled arc (progress indicator)
- `.z-slider-knob-inner { stroke: <color>; }` — the background track arc

**What is NOT CSS-themable in the knob mold:**
- Arc geometry (`d` path attribute) — computed by `_calcPath()` using `angleArc` and JS geometry; written as inline SVG attribute
- `stroke-width` on both `<path>` elements — computed from `_getStrokeWidth()` and written as inline SVG attribute by `setStrokeWidth()` / `_renderKnob()`
- The input element's `top`, `left`, `width`, `height`, `font-size` — all computed by `_getInputProperty()` and written as inline styles
- Root element width/height — set to `200px × 200px` by default if no explicit width/height is given (written by `bind_()` via `node.width(DEFAULT_SIZE)`)

**Knob mold is analogous to a third-party library wrapper for its internal geometry.** The CSS author can only set:
- Stroke colors for the two arcs
- Background/border of the numeric input field
- Root-level box model (width, height trigger JS resize via `setWidth`/`setHeight`)

### Native spin buttons on `.z-slider-input`

The overlay is an `<input type="number">`, so browsers render their native up/down spin buttons inside it unless the theme suppresses them. Because `_getInputProperty()` sizes the input tightly from the root dimensions and digit count, the reserved spinner area pushes the numeric value visibly off-center (Chrome reserves the space even before hover). The theme should suppress the spinners (`-webkit-appearance`/`-moz-appearance: textfield`) and set `text-align: center` (see `zul/src/main/resources/web/js/zul/inp/css/slider.css`). Any theme must make this decision explicitly — leaving UA spin buttons inside the digit-count-sized box is almost certainly broken visuals. Robust modern suppression: `appearance: textfield` on the input plus `::-webkit-outer-spin-button, ::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }`.

## Orientation

`orient="horizontal"` (default) or `orient="vertical"`. Applies to default/sphere/scale molds only. The orientation class is on the root: `.z-slider-horizontal` / `.z-slider-vertical`. Vertical sliders need an explicit `height` to render.

Scale mold does not support vertical orientation (ZK throws `WrongValueException` at the server).

### Vertical coordinate semantics: min=top, max=bottom

`Slider._fixPos()` writes:

```js
this.$n_('area').style[vert ? 'height' : 'width'] = newPos;
btn.style[vert ? 'top' : 'left'] = newPos;
```

`newPos` is the px offset of the **thumb** from the top of `.z-slider-center`. ZK's vertical convention is **min at the top, max at the bottom** — bigger value → larger `top` → larger `height` for `.z-slider-area`.

Implication for CSS: `.z-slider-vertical .z-slider-area` **must anchor at `top: 0`** (`bottom: auto`). If you anchor at `bottom: 0`, the fill renders at the opposite end from the thumb — visually the area looks "detached" from the button.

## Optional value popup

During drag on the default/sphere/scale molds, ZK appends `div#zul_slidetip.z-slider-popup` to `document.body` as a floating tooltip showing the current value. It is removed on drag end.

## State classes

Slider has no disabled state. Rangeslider and multislider do: ZK adds `.z-rangeslider-disabled` (`.z-multislider-disabled` for multislider, which extends Rangeslider) on the root through `setDisabled` / `domClass_` in `Rangeslider.ts`; it does not set a `[disabled]` attribute.

No ZK-emitted state classes for interactive states. All interactive states (hover, focus, active/dragging) rely on CSS pseudo-classes applied to `.z-slider-button`.

## Composition invariants

- `.z-slider-center` width (horizontal) or height (vertical) is set inline by `_fixSize()`; CSS must not override it with a competing `width`/`height` declaration.
- `.z-slider-area` has `position: absolute` written inline in the mold HTML; CSS must preserve or extend this, not override it to `static`.
- For the knob mold: if no explicit `width`/`height` is set on the root, `bind_()` sets 200px × 200px inline; CSS dimensions may override this but will trigger a `rerender()` (full DOM rebuild) when changed via `setWidth()`/`setHeight()`.

## Class name caution

Older theme docs may reference `.z-slider-center` (track) and `.z-slider-area` (fill) OR `.z-slider-rail` and `.z-slider-runner`. The mold JS (`mold/slider.js`) uses `this.$s('center')` / `this.$s('area')` / `this.$s('button')` which resolve to `.z-slider-center`, `.z-slider-area`, `.z-slider-button`. Use these names. Do not author rules for `.z-slider-rail` or `.z-slider-runner` unless you have confirmed live DOM output with those names.

ZK emits no `.z-slider-indeterminate` class.

## Related: multislider, rangeslider

- **multislider** (EE) — multiple thumbs on one track
- **rangeslider** (PE) — two thumbs marking a range

Both reuse much of slider's CSS but add their own `-multi` / `-range` modifier classes.

### `.z-rangeslider-track` is the rail, **not** the filled portion

Element naming is misleading. From `mold/rangeslider.js`:

```
.z-rangeslider
└─ .z-rangeslider-inner            (sizing container)
   ├─ .z-rangeslider-track         ← FULL TRACK RAIL (not filled span)
   ├─ .z-rangeslider-marks
   └─ .z-sliderbuttons
      ├─ .z-sliderbuttons-area     ← FILLED portion between thumbs (JS sets left/width)
      ├─ .z-sliderbuttons-button   ← thumb 1
      └─ .z-sliderbuttons-button   ← thumb 2
```

CSS must give `.z-rangeslider-track` **measurable** width/height (typically `inset: 0` over the inner). `Rangeslider.ts` `getSliderSize_()` calls `jq($n_('track')).width()` to derive the slider's px size — if the track has 0 width, every drag computes percent = `mouseOffset / 0 = Infinity` and the thumb snaps to 100%.

`.z-sliderbuttons-area` gets its color (filled fill) from CSS; its position is set by ZK JS via inline `style="left: X%; width: Y%"`.

The same convention applies to **multislider**.

### Sliderbuttons has its own css-uri

`<sliderbuttons>` is a separate widget (`zkex.slider.Sliderbuttons`, declared in zkex `lang-addon.xml`) with its own css-uri, `css/sliderbuttons.css.dsp`. That stylesheet loads for both rangeslider (zkex) and multislider (zkmax), because both render the same `.z-sliderbuttons-*` markup. A theme should therefore put the rules both sliders share (container, pressed thumb, and the tooltip hidden by default and revealed on hover) in the sliderbuttons stylesheet; IceBlue does this in `sliderbuttons.less`. Per-slider looks (thumb size and colour, area colour, tooltip position) belong in the rangeslider or multislider stylesheet, scoped under the slider root (for example `.z-rangeslider .z-sliderbuttons-button` or `.z-multislider .z-sliderbuttons-area`), because an unscoped rule in one slider's file restyles the other depending on load order. If a theme ships no file for that css-uri, there is nothing to serve, so the theme must provide the file.

### Thumb must be centered on its JS-set offset (negative margin = −½ thumb size)

For slider/rangeslider/multislider, ZK JS writes the **value position** as an inline percentage on the thumb's main axis — `btn.style.left = X%` (horizontal) or `btn.style.top = X%` (vertical) — and on the filled area (`Sliderbuttons.syncArea()`, `Rangeslider.updatePosByValues_()`, `Sliderbuttons._dragging()`). That percentage marks where the thumb's **center** belongs, not its top/left edge.

So the thumb CSS must offset itself by half its own size on whichever axis JS drives:

- **Horizontal**: `margin-left: calc(thumb-size / -2)` (axis JS sets is `left`). Cross-axis is centered with `top: 50%; margin-top: calc(thumb-size / -2)`.
- **Vertical**: `margin-top: calc(thumb-size / -2)` (axis JS sets is `top`). Cross-axis is centered with `left: 50%; margin-left: calc(thumb-size / -2)`.

Drop the negative margin on the JS-driven axis and every thumb lands half-a-thumb off the track (its edge, not its center, sits on the value point) — and the filled-area end edge, which DOES anchor on the % point, visibly detaches from the thumb. This bit multislider's vertical thumb (`margin-top: 0` → thumbs 10px below their track points); rangeslider's vertical rule (`margin-top: calc(--btn-size / -2)`) is the correct template.

### Marks: rangeslider renders a dot + label; multislider renders the label only

`Rangeslider.redrawMarks_()` appends BOTH a `.z-{c}-mark-dot` and a `.z-{c}-mark-label` to each `.z-{c}-mark`. `Multislider.redrawMarks_()` (override) appends ONLY the label — no dot. So a multislider mark has no on-track dot anchor; its label is positioned relative to a bare (2px) mark element on the track. Theme consequence: the per-orientation offset that places the mark label a consistent perpendicular distance from the track differs between the two components (the dot shifts rangeslider's anchor), even when the intended visual gap is identical — express the intended gap as one shared value and add a small per-case `calc()` correction rather than hand-tuning four independent offsets.

## Multislider: each range has its own `.z-sliderbuttons`

Each range of a multislider renders its own sibling `.z-sliderbuttons` container. When a theme stretches each container across the full rail, no z-index separates them, so DOM order decides which one is on top, so the last range's container covers the thumbs of every earlier range at their centres. For N ranges, 2(N−1) of the 2N thumbs cannot be reached by a pointer. A single-range multislider is not affected.

A theme that stretches each container over the rail must do one of the following:

- make the container `pointer-events: none` and the thumbs `pointer-events: auto`; or
- otherwise guarantee that `elementFromPoint` at each thumb's centre returns that thumb.

## Bundle

`slider.css.dsp` — one-to-one mapping. No sibling impact.

## Edition

- Molds default / sphere / scale: CE
- Mold **knob**: EE — registered by `zkmax/src/main/resources/metainfo/zk/lang-addon.xml` (`<mold-name>knob</mold-name>`) and implemented in `zkmax/slider.ts`. The registering jar decides the edition (see `reference/edition-availability.md`).

## Notes

- The knob mold extension is loaded as a ZK augment (`zk.augment`) from zkmax, not a separate widget class. The root class remains `.z-slider`.
- `setMode()` is silently ignored for the knob mold (decimal mode not supported; use `setStep()` instead).
- `setAngleArc()` / `setStrokeWidth()` / `setScaleInput()` are knob-only properties; they have no effect on the other molds.
