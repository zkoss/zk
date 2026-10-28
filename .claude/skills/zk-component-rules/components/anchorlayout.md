# anchorlayout

A layout container that sizes its direct `<anchorchildren>` children relative to the parent's
pixel dimensions. Each child declares an `anchor=` attribute whose value specifies its width (and
optionally height) either as a percentage string (`"50%"`) or as a delta from the parent
(`"-30"` = parent width minus 30 px). Children fill left-to-right in source order.

The stock ZK rendering is float-based: children carry `float: left`. A theme that replaces the
float model (e.g. with flex-wrap) must also handle the parent-sizing loop that `onSize()` runs —
because `onSize()` reads `jq(parentn).width()` to calculate absolute pixel widths from deltas. As
long as the parent `.z-anchorlayout` has a deterministic `offsetWidth`/`offsetHeight`, the JS
sizing engine works regardless of whether the container uses float or flex.

## DOM structure

```
.z-anchorlayout                  (<div> — root container; children follow in DOM order)
└─ .z-anchorchildren * N         (<div> — each child; JS writes inline width/height via style=)
   └─ (child widgets)
```

Note: there is **no** `.z-anchorlayout-body` wrapper element. The mold (`anchorlayout$mold$`)
renders children directly inside the root `<div>`. The existing stub entry that listed
`.z-anchorlayout-body` was incorrect — the mold source confirms no such wrapper is emitted.

## State classes

Anchorlayout and anchorchildren carry no ZK-added state classes. The component is purely
structural: no `disabled`, `readonly`, or selection concept applies to the container itself.

States rely entirely on the content widgets placed inside each `<anchorchildren>`.

## Attribute support

- `anchor="<width> [<height>]"` on `<anchorchildren>` — JS reads this in `onSize()` to compute
  inline `style.width` / `style.height`. Values:
  - Percentage (`"50%"`, `"25%"`) → written verbatim as CSS percentage string.
  - Delta from parent (`"-30"`, `"+50"`) → computed as `parentWidth + delta` px and written as an
    absolute pixel value (via `jq.px0`).
  - Combined (`"50% -200"`) → first token = width rule, second token = height rule.
  - Omitting height token → height is not touched by JS; the theme's CSS controls it.

## Composition invariants

- `onSize()` triggers on every `zWatch` resize event; it reads the parent `.z-anchorlayout`
  `offsetWidth` / `offsetHeight` to resolve delta anchors. The root must have a deterministic
  rendered size before `onSize()` fires, otherwise delta-anchored children receive 0px widths.
- Percentage anchors are written as CSS percentage strings (`style.width = "50%"`) — they are
  relative to the element's containing block, which is the `.z-anchorlayout` root. The root must
  establish a block formatting context with a known width.
- Delta anchors are written as absolute `px` values computed at resize time. Changing the root's
  width after render (e.g. via a CSS flex transition) will trigger another `onSize()` if the resize
  event fires; CSS-only width changes without a JS resize event leave delta children stale.
- The root must NOT use `display: none` during `onSize()` — JS cannot read `offsetWidth` from a
  hidden element, so all delta-anchored children would be set to `0px`.
- Children that omit an `anchor` attribute are not touched by `onSize()` — their size is controlled
  entirely by CSS.

## Sibling decomposition

No ZK sibling uses the same anchor-sizing model. This component is a novel layout primitive.

Content placed inside `<anchorchildren>` may use any other ZK widget — size and positioning of
those widgets is governed by their own skill entries.

## Contract

`layout.css.dsp` — shared with borderlayout, hlayout/vlayout, and splitter.

The canonical source file for Marble is:
`zul/src/main/resources/web/js/zul/layout/css/anchorlayout.css`

(Ignore the stray copy at `zul/src/main/resources/web/js/zul/layout/anchorlayout.css` — it is not
read by the build pipeline.)

## Edition

CE

## Notes

- The mold for both `anchorlayout` and `anchorchildren` is a minimal `<div` + `domAttrs_()` + children:
  no inner wrappers, no role attributes, no data attributes beyond the standard ZK widget ones.
- The ZK stock theme applies `overflow: hidden` and `float: left` to `.z-anchorchildren`, and
  `overflow: hidden` to `.z-anchorlayout`. A theme may replace float with flex-wrap; this changes
  the block-formatting context but preserves the sizing model as long as the root has a rendered
  width before `onSize()` fires.
- `display: flex; flex-wrap: wrap` is a valid layout replacement for the float model because ZK
  writes inline widths directly on `.z-anchorchildren` — flex item widths are respected when
  `flex-shrink: 0` is also set (preventing the flex algorithm from compressing below the JS-set
  inline width).
- `min-width: 0` on `.z-anchorchildren` prevents flex blowout when a child's content is wider
  than the JS-computed width.
- `cursor` and `pointer-events` are not constrained by this component — children inherit their
  own interaction contracts.
