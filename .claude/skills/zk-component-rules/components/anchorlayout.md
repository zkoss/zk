# anchorlayout

A layout container that sizes its direct `<anchorchildren>` children relative to the parent's
pixel dimensions. Each child declares an `anchor=` attribute whose value specifies its width (and
optionally height) either as a percentage string (`"50%"`) or as a delta from the parent
(`"-30"` = parent width minus 30 px). Children fill left-to-right in source order.

The stock ZK rendering is float-based: children carry `float: left`. `onSize()` reads
`jq(parentn).width()` to calculate absolute pixel widths from deltas. As long as the parent
`.z-anchorlayout` has a deterministic `offsetWidth`/`offsetHeight`, the JS sizing engine works
whatever layout model the container uses.

## DOM structure

```
.z-anchorlayout                  (<div> — root container; children follow in DOM order)
└─ .z-anchorchildren * N         (<div> — each child; JS writes inline width/height via style=)
   └─ (child widgets)
```

The mold (`anchorlayout$mold$`) renders children directly inside the root `<div>`, so
`.z-anchorchildren` is a direct child of `.z-anchorlayout`.

## State classes

Anchorlayout and anchorchildren are purely structural. State (disabled, readonly, selection)
belongs to the content widgets placed inside each `<anchorchildren>`.

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
  width after render will trigger another `onSize()` if the resize
  event fires; CSS-only width changes without a JS resize event leave delta children stale.
- The root must NOT use `display: none` during `onSize()` — JS cannot read `offsetWidth` from a
  hidden element, so all delta-anchored children would be set to `0px`.
- Children that omit an `anchor` attribute are not touched by `onSize()` — their size is controlled
  entirely by CSS.

## Sibling decomposition

Content placed inside `<anchorchildren>` may use any other ZK widget — size and positioning of
those widgets is governed by their own skill entries.

## Contract

`anchorlayout.css.dsp` (`lang.xml` `css-uri`). The canonical source file is:
`zul/src/main/resources/web/js/zul/layout/css/anchorlayout.css`

## Edition

CE

## Notes

- The mold for both `anchorlayout` and `anchorchildren` is a minimal `<div` + `domAttrs_()` + children:
  no inner wrappers, no role attributes, no data attributes beyond the standard ZK widget ones.
- The ZK stock theme applies `overflow: hidden` and `float: left` to `.z-anchorchildren`, and
  `overflow: hidden` to `.z-anchorlayout`.
- `cursor` and `pointer-events` are not constrained by this component — children inherit their
  own interaction contracts.
