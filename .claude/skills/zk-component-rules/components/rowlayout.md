# rowlayout / rowchildren

`<rowlayout>` divides its parent container's horizontal width into N proportional columns
(default: 12) separated by gutters. Each `<rowchildren>` child occupies an integral number
of those columns (`colspan`) and may skip a number of columns ahead of itself (`offset`).
Stacking multiple `<rowlayout>` elements inside the same parent creates a multi-row grid.

The layout engine is purely JS-driven: on every `onSize` event `Rowlayout` computes
percentage widths for each child and writes them as inline `style.width` and `style.margin-left`.
The CSS layer provides only the float-based clearfix on the container and the `display: block;
float: left; box-sizing: border-box` on each column child.

## DOM structure

```
.z-rowlayout                         (<div> — root container, width: 100%; clearfix)
└─ .z-rowchildren.colspanN [.offsetN] * N  (<div> — one per column child)
   └─ (child widgets)
```

- Root is a plain `<div>` rendered by `rowlayout$mold$`.
- Each child is a plain `<div>` rendered by `rowchildren$mold$`.
- `colspanN` (e.g. `colspan4`) is always present; N = value of `colspan` attribute (default 1).
- `offsetN` (e.g. `offset2`) is present only when `offset > 0`; omitted entirely when `offset == 0`.
- No inner wrapper elements inside `.z-rowchildren` — child widgets render directly inside.
- No ARIA roles are emitted by the mold.

## State classes

No ZK-added state classes exist on `.z-rowlayout` or `.z-rowchildren`. The component is a
purely structural layout container. There is no `disabled`, `readonly`, `selected`, or similar
concept at the container level.

States rely entirely on the content widgets placed inside each `<rowchildren>`.

The `colspan` and `offset` class names (`colspan4`, `offset2`) are dynamic — they are updated
by `setColspan()` / `setOffset()` via `className` string replacement at runtime. The CSS selector
`.z-rowchildren[class*="colspan"]` is the canonical stock selector for targeting column cells.

## Attribute support

- `ncols="N"` on `<rowlayout>` — number of equal-width logical columns (default 12). Triggers
  JS recalculation of all child widths.
- `spacing="ratio"` on `<rowlayout>` — gutter-to-column-width ratio (default `20/60 ≈ 0.333`).
  Accepted as a number (`0.5`), fraction string (`"1/3"`), or percentage string (`"33%"`).
- `colspan="N"` on `<rowchildren>` — columns to span (default 1). JS writes inline `style.width`
  as a percentage of the parent's width.
- `offset="N"` on `<rowchildren>` — columns to skip before this child (default 0). JS writes
  inline `style.margin-left` as a percentage.

No `disabled`, `readonly`, or `inplace` attributes exist on these components.

## Column arithmetic

`Rowlayout._syncSizingParam()` computes:

```
rowWidth = ncols + (ncols - 1) * spacing
pColWidth = 1 / rowWidth
pSpacing  = spacing / rowWidth
```

For each child, `_fixChild()` then writes:

```
width       = percent( (pColWidth + pSpacing) * colspan - pSpacing )
margin-left = percent( pSpacing )               for non-first siblings (default gutter)
            = percent( (pColWidth+pSpacing)*offset + pSpacing - pSpacing )
                                                 for a child with offset > 0
            = percent( 0 )                       for the first child with no offset
```

All values are written as inline `style` by jQuery `.css()` — not as CSS classes. The CSS
file has no `width` declarations; inline styles are the sole sizing mechanism.

## Composition invariants

- JS writes inline `width` (percentage) and `margin-left` (percentage) on each `.z-rowchildren`
  at bind time and on every `onSize` event. Theme CSS must never override these inline widths
  — `!important` width rules on `.z-rowchildren` will break the grid.
- The root `.z-rowlayout` must remain `width: 100%` so the JS-computed child percentages
  resolve against the parent's full width.
- `box-sizing: border-box` is required on `.z-rowchildren`: JS-computed widths assume border
  and padding are included in the percentage — without `border-box` any theme-added padding
  will overflow the grid.
- The clearfix (`:before/:after { display: table; content: ""; clear: both }`) on `.z-rowlayout`
  is required to collapse the float-based layout. Without it the root collapses to zero height.
- At viewport `max-width: 767px`, the stock less converts to a single-column stacked layout
  (`float: none`). This is a structural breakpoint, not a visual decision.
- Children that use `hflex` / `vflex` receive a `fireSized` call from `_fixChild` after
  their inline widths are set, so they can re-measure.

## Sibling decomposition

- Closest sibling: `columnlayout` — same composition pattern (EE layout container + N div
  children, JS writes inline widths). No shared CSS file.
- Content placed inside `<rowchildren>` may use any ZK widget; their styling is governed by
  their own skill entries.

## Contract

Own CSS file: `../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/css/rowlayout.css`
Delivered as: `zkmax/layout/css/rowlayout.css.dsp` (registered via `zkmax/lang-addon.xml`
`<css-uri>css/rowlayout.css.dsp</css-uri>`).

## Edition

EE (zkmax.jar — `org.zkoss.zkmax.zul.Rowlayout`). ZKDoc badge says "PE" but the class
resides in `zkmax`; treat as EE.

## Notes

- `domClass_()` on `Rowchildren` appends ` colspan{N}` unconditionally and ` offset{N}` only
  when `offset > 0`. At runtime, `setColspan()` uses a regex replace (`/colspan\d+/`) and
  `setOffset()` uses `/offset\d+/` on `className` directly — no class-list API.
- The canonical CSS selector `.z-rowchildren[class*="colspan"]` targets only cells that carry
  a colspan class (which is always the case for valid children). A bare `.z-rowchildren`
  without `[class*="colspan"]` would not match at render time because the class is injected
  by `domClass_()` before the mold renders — the attribute selector form is the established
  pattern in the ZK stock theme and is safer than the class-only form.
- No popup detachment, no ARIA roles, no inline event handlers are emitted by the mold.
- There is no responsive breakpoint in the JS — the `max-width: 767px` stacking is purely
  CSS-driven (stock theme only; theme may keep, remove, or replace this breakpoint).
