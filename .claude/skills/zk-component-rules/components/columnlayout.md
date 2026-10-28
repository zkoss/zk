# columnlayout / columnchildren

`<columnlayout>` arranges its `<columnchildren>` columns side-by-side; each column
holds a vertical stack of components. ZK PE/EE component (zkex bundle).

## DOM structure

```
.z-columnlayout                              (column container)
└─ .z-columnchildren * N                     (one per column, ZK sets inline width)
   ├─ .z-columnchildren-content (cave)       (children render in here)
   │   └─ ...child widgets...
   └─ <div style="height:1px;width:1px">     (1×1 spacer; mold artefact)
```

See `zkcml/zkex/src/main/resources/web/js/zkex/layout/mold/columnchildren.js`
and `Columnchildren.ts` for the rendering and inline-width logic.

## Inline-block rendering — do NOT use flex

`<columnchildren width="33%">` writes `style="width:33%"` on `.z-columnchildren`
directly. The widget is designed for `display: inline-block` flow:

- Parent `.z-columnlayout` keeps `white-space: nowrap` so columns don't wrap.
- `.z-columnchildren` is `display: inline-block; vertical-align: top` and
  takes whatever width ZK has put on it (px, %, or auto from `hflex`).
- `.z-columnchildren-content` fills the cave (`width:100%; height:100%`).

A `display: flex` parent with `flex: 1` on the column would override the
user-set width attribute and collapse all columns equally — symptom: a
declared `33%/33%/33%` ZUL renders as one wide stack or three equal columns
regardless of the value. Use inline-block, not flex.

`min-width` on `.z-columnchildren` also breaks layout: ZK lets the user pick
any width including very small percentages, and the spec is "honour the
declared width exactly". Leave min-width unset.

## hflex

`hflex="1"` on `<columnchildren>` makes ZK compute a pixel width at layout
time and write it inline. No CSS rule needed — same inline-block path.

## CSS file

`zul/src/main/resources/web/js/zul/layout/css/layout.css` → bundled to
`layout.css.dsp` (note: the EE `columnlayout.css.dsp` slot in iceblue exists
for module-isolation, but in this theme the rules live in `zul/layout/css/layout.css`).
