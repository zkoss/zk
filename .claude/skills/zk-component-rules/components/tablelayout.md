# tablelayout

A layout container that positions child `<tablechildren>` widgets in an HTML table grid. The
developer specifies the number of columns via `columns=`; ZK's mold engine auto-wraps children
into `<tr>` rows when the column count is exhausted. Each child widget (`Tablechildren`) renders
as a native `<td>` element and supports `colspan=` / `rowspan=` for spanning cells, enabling
complex grid-style layouts within a ZK page.

The component itself renders as a `<table>` element — not a `<div>`. This means `border-collapse`
and table-layout properties apply, and `display: flex` must never be set on the root. ZK's
`getFlexContainer_()` returns `undefined` for both Tablelayout and Tablechildren, meaning ZK's
flex sizing engine is disabled for this component; child sizing is controlled by CSS table layout
or explicit width/height attributes on children.

## DOM structure

```
.z-tablelayout                   (<table> — root; one per tablelayout widget)
└─ <tr> * N                      (auto-generated rows — no ZK class; managed by mold logic)
   └─ .z-tablechildren * M       (<td> — one per tablechildren widget; colspan/rowspan are
                                   native HTML attributes, not classes)
      └─ (child widgets)
```

Note: `<tr>` elements carry NO ZK class. They are bare HTML emitted by the mold (`tablelayout.js`)
and are not independently styleable via ZK state classes. The CSS selector must be
`.z-tablelayout > tr > .z-tablechildren` (or `.z-tablelayout tr .z-tablechildren` for
colspan/rowspan-generated rows at deeper nesting).

`colspan` and `rowspan` appear as native HTML attributes on the `<td>` element, not as CSS
classes. The mold only emits them when their values differ from 1.

`deferRedrawHTML_` in `Tablechildren.ts` emits `<td … class="z-renderdefer">` as a placeholder
during deferred rendering — this class is transient and must not be styled permanently.

## State classes

Tablelayout and Tablechildren carry no ZK-added state classes. The component is purely structural:
no `disabled`, `readonly`, `selected`, or `open/closed` concept applies to the layout container
or its cells.

States rely entirely on the content widgets placed inside each `<tablechildren>` cell.

## Attribute support

- `columns="<n>"` on `<tablelayout>` — integer; controls the number of `<td>` cells per `<tr>`.
  Changing this at runtime causes a full `rerender()`.
- `colspan="<n>"` on `<tablechildren>` — maps directly to the native `<td colspan="n">` attribute.
  Default is 1 (attribute omitted from emitted HTML).
- `rowspan="<n>"` on `<tablechildren>` — maps directly to the native `<td rowspan="n">` attribute.
  Default is 1 (attribute omitted from emitted HTML).
- Adding or removing a child widget causes a full `rerender()` on the parent Tablelayout.

## Composition invariants

- The root element is a `<table>` — `display` must not be overridden to `flex`, `grid`, or `block`.
  The JS-generated `<tr>` / `<td>` structure depends on native table layout.
- `getFlexContainer_()` returns `undefined` for both Tablelayout and Tablechildren — ZK's flex
  sizing engine does not manage this component. Children are NOT auto-sized by ZK.
- Cells fill columns left-to-right in DOM order. When `colspan` or `rowspan` are in use, the mold
  tracks span positions internally and defers `<tr>` boundaries accordingly. The CSS grid model
  does not apply here — only native HTML table spanning.
- Each `<tablechildren>` (`<td>`) may contain any number of child ZK widgets. The `<td>` is the
  direct containing block for those children; the theme's `vertical-align` and `padding` on
  `.z-tablechildren` affect child positioning within the cell.
- `z-renderdefer` on a `<td>` is a transient class emitted during deferred rendering; it is
  replaced when the deferred content is bound. Themes must not rely on its presence.

## Sibling decomposition

No ZK sibling uses the HTML-table rendering model for layout. This component is a novel layout
primitive with no shared CSS ancestry. Content placed inside `<tablechildren>` may be any ZK
widget — their styling is governed by their own skill entries.

## Contract

`../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/css/tablelayout.css` (new file; standalone, not merged
into another `.css.dsp`).

The ZK default theme CSS for this component contains exactly one rule: `vertical-align: top` on `.z-tablechildren`.

## Edition

EE (requires `zkmax.jar`; class is `zkmax.layout.Tablelayout` / `zkmax.layout.Tablechildren`)

## Notes

- Because the root is a native `<table>`, `border-spacing` is the correct property for adding
  gutters between cells, not `gap` (which applies to flex/grid only). Alternatively,
  `border-collapse: separate` + `border-spacing` achieves cell gutters.
- `border-collapse: collapse` removes all space between cells and is the HTML default for many
  browsers; the theme must explicitly set `border-collapse: separate` if gutters are desired.
- The `<tr>` wrappers are auto-generated by the mold and carry no ZK class. Selectors targeting
  table rows must use the native `<tr>` element selector within the `.z-tablelayout` scope.
- Since `getFlexContainer_()` returns `undefined`, setting `hflex="1"` or `vflex="1"` on
  `<tablechildren>` children will NOT work — do not use ZK flex attributes inside tablelayout cells.
- The ZK stock default CSS applies only `vertical-align: top` to `.z-tablechildren`. There is no
  border, padding, or background on either the table root or the cells in the stock ZK theme.
