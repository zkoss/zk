# grid + listbox + tree (the row-based data trio)

Three different components for tabular data, sharing structural conventions.

## Shared structure pattern

| Component | Header | Rows container | Row | Cell |
|-----------|--------|----------------|-----|------|
| grid      | `.z-columns` > `.z-column` | `.z-rows` | `.z-row` | `.z-row` > `.z-cell` |
| listbox   | `.z-listhead` > `.z-listheader` | `.z-listbox-content` | `.z-listitem` | `.z-listcell` |
| tree      | `.z-treecols` > `.z-treecol` | `.z-treechildren` | `.z-treerow` | `.z-treecell` |

## Alternating row classes — use these, not `:nth-child`

Grid and listbox emit explicit `-odd` classes (`Grid.ts` and `Listbox.ts`, `getOddRowSclass`):

- `.z-grid-odd` (applied to alternating `.z-row`)
- `.z-listbox-odd` (applied to alternating `.z-listitem`)

**Prefer the explicit class** over `:nth-child(odd)` for grid and listbox. With virtual scrolling and dynamic row insertion/removal, `:nth-child` selectors break (the DOM position changes but the row's logical position doesn't). The framework-emitted class survives re-renders.

Tree rows are styled for alternation with `:nth-child` (Tree has no `getOddRowSclass`); check the result under virtual scrolling.

## Cell content wrapper naming

Grid's actual cell-content wrapper is on the **row**, not the cell:

- `.z-row-content` ← grid (intentional — flexbox-flat row layout)
- `.z-listcell-content` ← listbox (per-cell)
- `.z-treecell-content` ← tree (per-cell)

The grid choice is a deliberate departure from the per-cell pattern. The cell elements (`.z-cell`) exist but the content wrapper sits at the row level for grid's flat-row rendering. The grid class is `.z-row-content`, not `.z-cell-content`.

## Grid footer cell structure

Grid footer cells use `TR.z-foot > TD.z-footer > DIV.z-footer-content`, not `.z-cell` or `.z-row-content`. Cell padding belongs on `.z-footer-content`. The grid CSS sets `.z-footer` to `padding: 0`. (Source: the "Actual DOM" comment in `zul/src/main/resources/web/js/zul/grid/css/grid.css`.)

## Selection support

| Component | Row selection | Class emitted |
|-----------|---------------|---------------|
| grid      | Not supported | — |
| listbox   | Yes           | `.z-listitem-selected` |
| tree      | Yes           | `.z-treerow-selected` |

Grid is for **read-only tabular display**, so the grid widget emits no selection-related classes on rows (`grep -r "selected" zul/src/main/resources/web/js/zul/grid/` returns no match). If a use-case needs row selection on a grid-like surface, use listbox.

## Sort state classes

Header cells add sort modifiers when `sort="auto"` or `sort="ascending"/"descending"` is set. The classes follow the header's zclass: `.z-column-sort` / `.z-column-sorticon` (grid), `.z-listheader-sort` / `.z-listheader-sorticon` (listbox), `.z-treecol-sort` / `.z-treecol-sorticon` (tree). Source: `HeaderWidget.ts` and `SortWidget.ts`.

## Sizable header → resize affordance (hover-only `.z-{c}-sizing` class)

`<listhead sizable="true">` / `<columns sizable="true">` / `<treecols sizable="true">`
let the user drag a column's right edge to resize it. The mechanism is **not** a
rendered handle element:

- The drag hot-zone is the **rightmost 8px** of each header cell
  (`HeaderWidget._insizer: x >= this.$n_().offsetWidth - 8`).
- On mouse-move inside that zone ZK adds class `.z-{c}-sizing` **to the header cell
  (the TH) itself** (`doMouseMove_ → jq(n).addClass(this.$s('sizing'))`, `n = $n()`),
  and removes it on mouse-out / drag-end. Class names: `.z-column-sizing` (grid),
  `.z-listheader-sizing` (listbox), `.z-treecol-sizing` (tree).
- ZK emits **NO persistent "this head is sizable" class** — `HeadWidget.setSizable`
  only flips an internal `_sizable` flag and rerenders; nothing in the DOM marks a
  sizable head at rest. So a theme **cannot** draw a resting per-column divider scoped
  to sizable heads; the only CSS hook is the transient `.z-{c}-sizing` state.

**Required theme rule** — the affordance is a *hover-time* one:

```css
.z-column-sizing, .z-listheader-sizing, .z-treecol-sizing {
    cursor: col-resize;                                   /* the real affordance */
    /* optionally accent the dragged edge, e.g. with an inset box-shadow */
}
```

**Trap — `.z-{c}-sizing` is the TH, not a handle.** It is tempting to write
`.z-column-sizing { position:absolute; right:0; width:4px }` as if it were a separator
child. It is the header cell itself — positioning/sizing it
collapses the whole column the instant the pointer enters the 8px zone. Style it as a
state on the TH only (`cursor`, `box-shadow`, `background`). `HeaderWidget.redraw` emits only
content/sorticon/[button], so there is no `.z-{c}-sizer` element to select.

## Narrow icon / checkbox / image column → collapse padding (and kill ellipsis)

A header/cell column with a small fixed width (typically `width="40px"`) holding only an
icon — a checkmark, a sort glyph, or an `image="…16x16.png"` — collides with the theme's
side padding: the fixed narrow width minus both side paddings leaves very little content width.
Three symptoms, one root cause:

1. **Checkbox/check icon clipped** — the 16×16 control overflows the narrow content area and
   the cell's `overflow:hidden` clips its right edge.
2. **Header row inflated by a wrapping icon column** — `.z-{c}` header cells are
   `white-space: normal` (so long *text* labels wrap onto a 2nd line). But a narrow icon
   column's content is *wider* than its content box, so `normal` wraps it too, doubling the
   content height and inflating the whole header **row**. Two
   triggers, same mechanism:
   - a header **image** (bare `<img>`, no class) hits the global `img{max-width:100%}`,
     shrinks to the narrow content width AND the squeezed content wraps;
   - a **select-all checkbox** header (`width="40px"`) — the `.z-{c}-checkable` span is
     wider than the narrow content box (16px icon + its inline padding), so it wraps
     below the (zero-width) sorticon.

   This is why "a small image / a checkbox makes the header taller than default" — it is not
   the icon's height, it is the narrow column forcing a wrap.
3. **Stray ellipsis "…" beside a checkbox** — the checkable body cell's `.z-listcell-content`
   is `white-space:nowrap; overflow:hidden; text-overflow:ellipsis`. The checkable span
   (16px icon + its own inline padding) is wider than the content box, so the browser
   paints a leftover ellipsis to the right of the box. (The header select-all box shows no
   ellipsis because `.z-listheader` is `white-space:normal`; a labelled checkmark cell in an
   auto-width column shows none because it doesn't overflow.)

**Fix — collapse the side padding on BOTH the header cell and the cell content, and clip
(don't ellipsis) the checkable cell:**

```css
/* Header side — checkable OR image/icon column. nowrap is REQUIRED: the cell defaults
   to white-space:normal for text labels, which wraps the over-wide icon content and
   inflates the row. padding-collapse alone does NOT fix the checkbox header (its span
   is wider than the content box even at 4px padding). */
.z-listheader:has(.z-listheader-checkable),
.z-listheader:has(img) {
    padding-left: <small padding>;
    padding-right: <small padding>;
    white-space: nowrap;
}
/* Cell side — mirror header padding; padding lives on .z-listcell-content, NOT the td. */
.z-listcell:has(.z-listitem-checkable) > .z-listcell-content {
    padding-left: <small padding>;
    padding-right: <small padding>;
    text-overflow: clip;   /* holds a control, not clipping text → no stray "…" */
}
```

Same pattern for `.z-column` (grid) and `.z-treecol` (tree). **Trap:** fixing only the
header (or only the cell) leaves header/row icons misaligned — always do both, and verify
`header.icon.right === cell.icon.right`. Tree's `.z-treecell-content` is `display:flex`, so
its ellipsis lives on the child `.z-treecell-text` and the stray-ellipsis symptom (3) is
listbox-specific; symptoms (1) and (2) apply to all three.

## Focus and disabled state classes (listbox + tree)

| Component | Focus class | Disabled class |
|-----------|-------------|----------------|
| listbox | `.z-listitem-focus` | `.z-listitem-disabled` |
| tree | `.z-treerow-focus` | `.z-treerow-disabled` |

Grid rows have no explicit focus/disabled classes (grid is read-only display — see Selection support section).

## Header action button (`menupopup` trigger)

All three data widgets render a small caret-down button at the right edge of each header cell when `menupopup` is set. **Same pattern, three class names** (header zclass + `-button` / `-hover`): `.z-column-button` / `.z-column-hover` (grid), `.z-listheader-button` / `.z-listheader-hover` (listbox), `.z-treecol-button` / `.z-treecol-hover` (tree). Source: `HeaderWidget.ts`.

DOM (grid example — the other two are structurally identical):

```
TH.z-column > DIV.z-column-content > DIV.z-column-button > I.z-icon-caret-down
```

Key facts:

- **Only rendered when `menupopup` is set.** No `menupopup` → no `*-button` element exists. Don't write rules that assume it's always there.
- ZK ships it `display: none` by default and toggles `display: block` via the **`.*-hover` class** (added/removed by the header widget's mouseenter/mouseleave). For modern themes prefer the CSS `:hover` state on the header cell — but keep the `.*-hover` class as a fallback selector for legacy JS paths.
- It's `position: absolute` at the right edge of the header cell — the header cell itself must be `position: relative`.
- Pattern: progressive disclosure on hover — invisible until the header cell is hovered, then fades in.
- **The button is nested inside `.z-{c}-content`, NOT a direct child of the header cell.** When using `:has()` to detect a button slot (e.g. reserving right-padding so wrapped header text never collides), use descendant `:has(.z-{c}-button)` — NOT `:has(> .z-{c}-button)`. The direct-child variant never matches.

## Long header text + hover-revealed button → reserve a slot

When a header has an action button slot (`menupopup` set), wrapping long labels with `white-space: normal` (instead of `nowrap + ellipsis`) is the only way to avoid the button overlapping the text on hover. Combine with a right-padding that reserves the button's width:

```css
.z-listheader { white-space: normal; overflow-wrap: anywhere; }
.z-listheader:has(.z-listheader-button) {
    /* right-edge gap + button width + text gap */
    padding-right: calc(<edge gap> + <button width> + <text gap>);
}
```

Same pattern applies to `.z-column` (grid) and `.z-treecol` (tree).

## Checkmark column renders a checkbox (multiple) OR a radio (single)

`checkmark="true"` on a **listbox** or **tree** inserts a selection control as the
first element of each first-visible cell. **The control's shape depends on the
selection mode**, and ZK signals it via the *icon class*, not the span class:

| Mode | `<i>` icon class | Render as |
|------|------------------|-----------|
| `multiple="true"` | `…-icon z-icon-check` | **checkbox** (square box, checkmark when selected) |
| single (default)  | `…-icon z-icon-radio` | **radio** (circle, filled dot when selected) |

Source: `Listcell._colHtmlPre` / `Treecell._colHtmlPre` —
`multi ? 'z-icon-check' : 'z-icon-radio'`. A theme that styles only `z-icon-check`
ships a broken single-selection checkmark (the radio falls through to the generic
flat mask glyph). **Always style both `z-icon-check` and `z-icon-radio`.**

### Class map (the span, the icon, the header select-all)

The checkmark span wraps an `<i>` icon. Class prefixes differ per widget — and tree's
prefix is `z-treerow`, **not** `z-treeitem` (Treeitem's zclass resolves to the
treerow's, since the treeitem element is never rendered). The span carries `-checkable` plus `-checkbox` (multiple) or `-radio` (single), and `-disabled` when unselectable; the icon is `-icon` plus `z-icon-check` / `z-icon-radio`; the header select-all span is `z-listheader-checkable` / `z-treecol-checkable` (+`-checked`) with `-icon z-icon-check`. For the exact classes read `Listcell.ts` (`_colHtmlPre`), `Treecell.ts` and `Treecol.ts`.

The header "select all" control **only exists in `multiple` mode** (`_hasCheckbox`
requires `_multiple`) and is **always a checkbox** (`z-icon-check`) — never a radio.

**Override the generic mask glyph.** `z-icon-check`/`z-icon-radio` arrive pre-wired
to the theme's generic icon system (`mask-image` + `background-color`). To draw a real
CSS-drawn box/circle, kill the mask on `::before` and rebuild:
`-webkit-mask-image: none !important; mask-image: none !important;` then add
`border` + `border-radius` (small radius for the checkbox box, `50%` for the radio circle).

**Trap — dead `.z-{row}.z-{row}-checkable` selector.** `z-treerow-checkable` /
`z-listitem-checkable` live on the **inner span**, never on the TR. A compound
selector like `.z-treerow.z-treerow-checkable` (both classes on one element) matches
**nothing**. Scope row-level checkable rules through the span:
`.z-treerow:has(.z-treerow-checkable) .z-treecell:first-child …`, or target the span
directly.

**Trap — header checkbox misaligned with row checkboxes.** The header content
wrapper always contains an (often zero-width) `.z-{c}-sorticon` element *before* the
checkable span, so a theme that lays the sorticon out in flow can push the header control
off the body-row controls (the column's vertical line breaks). Check how the theme
positions the sorticon (in flow, flex item or absolute) before assuming the offset's source.

Verify alignment with `header.icon.centerX === row.icon.centerX`.

**Tristate / indeterminate (partial) checkmark — tree only.** A parent whose children
are *partly* selected renders an indeterminate (minus) box. This is **gated on the model
implementing `org.zkoss.zul.ext.TristateModel`** — `Tree.java` casts `(TristateModel) _model`
and reads `getPartials()`; with a plain `DefaultTreeModel` the partial codepath never
runs and **no** partial DOM is emitted. ZK ships **no concrete `TristateModel`** — a demo
must declare one (e.g. `class X extends DefaultTreeModel implements TristateModel { Set getPartials() … }`).
When active, ZK adds `z-treerow-partial` to the row's **TR** and switches the icon class
to **`z-icon-minus`**; the header select-all icon also becomes `z-icon-minus` when only
some rows are selected (the zkmax `tristate-tree.ts` `setHeadercmIcon` adds `z-treecol-partial` to the header `-cm` span and swaps the icon class to `z-icon-minus`). The tristate tree is a zkmax (EE) feature.
`z-icon-minus` arrives as the same flat mask glyph as check/radio — override and rebuild
as a box with a minus glyph. Scope through the icon, not the dead compound
`.z-treerow.z-treerow-partial`:
`.z-treerow-partial .z-treerow-icon.z-icon-minus::before, .z-treecol-icon.z-icon-minus::before { … }`.
Two gotchas when building the demo model: (1) call `model.setMultiple(true)` or every
`addToSelection` but the last is dropped (single-select default), so fully-selected
parents never get `z-treerow-selected`; (2) partial nodes in `getPartials()` must be
rendered (a visible row) for `getChildByNode` to map them.

**`TristateModel` is PASSIVE — ZK does NOT cascade.** `Tree.java` only *reads*
`getPartials()` to render and re-reads it on `SELECTION_CHANGED`/`TRISTATE_CHANGED`; it
**never computes** a parent's state from its children. There is zero cascade logic and
zero concrete impl anywhere in ZK — child↔parent propagation is by design 100% the
application's job. So a static `partials` set (set once, no listeners) renders correctly
but is **inert under interaction**: deselecting a child leaves the parent's box frozen.
This is **not a ZK bug and not a CSS gap** — it is unimplemented app logic. To make
tristate interactive, the model/page must, on every selection change:
(1) recompute each parent bottom-up — *all* children selected → SELECTED, *some*-or-any-child-partial
→ PARTIAL, *none* → UNSELECTED; (2) write that back to the model's selection + `partials`
sets, **removing partial parents from selection** (else they render checked and the minus
loses); (3) call the **public** `AbstractTreeModel.fireEvent(TreeDataEvent.TRISTATE_CHANGED, path, 0, 0)`
to force ZK to re-read `getPartials()` and repaint. For a parent click, down-propagate the
parent's new state to its subtree first (`addToSelection`/`removeFromSelection` on each
descendant), then recompute. An `onSelect` handler whose recompute runs *after* the
`getReference()` parent-branch covers both directions; recompute alone (no reference
needed) handles every child→parent case. Derive the initial parent states by running the
same recompute at page load rather than hand-setting `partials` — the demo then starts in
a state interaction can actually reproduce. See `tree.zul` tristate block for the pattern.

## Focus-trap div (`.z-focus-a`)

SelectWidget (Listbox, Tree) renders a hidden focus-trap div at the end of the body (Grid is not a SelectWidget and emits none; biglistbox, calendar and menupopup also emit a `.z-focus-a`):

```html
<div ...-a class="z-focus-a" style="top:0px;left:0px" tabindex="0"></div>
```

ZK calls `focusEl.focus()` after row clicks to keep keyboard navigation working. The inline `top`/`left` are JS-driven (`SelectWidget._focusAnchor`) and ONLY work if `.z-focus-a` finds a positioned ancestor — otherwise `position: absolute` resolves against `<body>` and `focus()` scrolls the entire page to (0, 0).

**Required theme rules** (apply to listbox and tree):

```css
.z-listbox-body, .z-tree-body {
    position: relative;     /* anchor .z-focus-a inside the body */
}
.z-listbox .z-focus-a,
.z-tree    .z-focus-a {
    position: absolute;
    width: 0;
    height: 0;
    overflow: hidden;
    opacity: 0;
    pointer-events: none;
}
```

If you skip the `position: relative` on the body element, you get the classic *click-listitem-and-page-jumps-to-top* bug — and it happens silently in production whenever the listbox is scrolled below the fold.

## Auxhead / auxheader (multi-level header rows)

ZK-only multi-row header (no native HTML equivalent). DOM:

```
TR.z-auxhead > TH.z-auxheader (with colspan="N")
TR.z-auxhead > TH.z-auxhead-bar   (scrollbar spacer when sizing)
```

Key facts:

- `<auxhead>` can be placed **above or below** `<columns>` — both positions are valid.
- `colspan` is the only mechanism for grouping; cells under a multi-cell auxheader still emit independently.
- `.z-auxheader.z-frozen-col` is applied to the auxheader cell that aligns with a frozen pane.
- Themes that hide the regular column-header background need to give the auxhead row some visual marker (tonal band, stronger divider, or typographic shift) or the multi-level structure won't read. Aim for hierarchy: auxhead-row > column-header > body-row.
- **The auxhead row divider must go on the `.z-auxheader` TH, never on `.z-auxhead` (TR)** — the header table is `border-collapse: separate`, where TR borders do not paint. Otherwise two stacked `<auxhead>` rows merge into one band. See "Header-row dividers go on the TH" below.
- **The trailing `.z-auxhead-bar` filler is the row's real `:last-child` — never the visible-last `.z-auxheader`.** ZK appends a zero-width `TH.z-auxhead-bar` (scrollbar/sizing spacer) to **every** auxhead row, even when nothing is being resized. So an edge-stripping rule keyed on `.z-auxheader:last-child` silently misses the rightmost visible header. If auxheaders carry a `border-inline-end` (a vertical divider between groups), that border stays on the visible-last cell and **doubles with the container's own frame border** — two parallel 1px lines on the right edge only (the left edge is clean because auxheaders have no border-left). In `border-collapse: separate` header tables (listbox/tree, and grid's `*-headtbl`) the two lines don't merge, so the doubling is visible. Strip the edge border with `.z-auxheader:has(+ .z-auxhead-bar)` (the cell immediately before the filler) **in addition to** `:last-child`:

  ```css
  .z-auxheader { border-inline-end: 1px solid <divider color>; }
  .z-auxheader:last-child,
  .z-auxheader:has(+ .z-auxhead-bar) { border-inline-end: none; }  /* don't double the frame */
  ```

  Grid often *looks* fine without this only because its auto-width lands the last-auxheader border coincident with the grid frame — same latent trap, just hidden by pixel alignment. Fix it in the shared `mesh/css/auxhead.css` so all three (grid/listbox/tree) are covered at once.

## Frozen columns

ZK `<frozen columns="N">` mechanism (`zul/src/main/resources/web/js/zul/mesh/Frozen.ts`; ZK-6065 added smooth frozen scrolling and `rightColumns`). Which way the first N columns stay in place depends on the client:

- **Desktop:** `_syncFrozenCellsPosition` sets an inline `transform: translate3d(<scrollLeft>, 0, 0)` and `z-index: 1` on the frozen cells so they follow the horizontal scroll.
- **Mobile (`zk.mobile`):** the frozen cells get `.z-frozen-sticky` and an inline `left` (`_adjustStickyFrozenCols`), i.e. CSS `position: sticky`; a theme rule on `.z-frozen-sticky` is live there.
- **Smooth mode (the default):** `_syncColumnBorders` adds `.z-frozen-col` to the frozen header cells so the theme can draw the pane boundary.
- **`rightColumns`:** the last N columns get `.z-frozen-right-col` and an inline `right` (native scrollbar only).
- The mold writes a `div#-frozen` with the widget's `frozen` zclass (`z-grid-frozen`, `z-listbox-frozen`, `z-tree-frozen`) around the tray below.
- A separate **scroll tray** DOM block is rendered (below the body):

  ```
  .z-frozen
    > .z-frozen-body
    > .z-frozen-inner   (overflow-x: scroll, this is where the scrollbar lives)
    > .z-frozen-right
    > .z-clear
  ```

- On desktop ZK JS listens to `scrollLeft` on `.z-frozen-inner` and moves the frozen cells as described above. The body itself stays `overflow-x: hidden`.

Theme implications:

- Style `.z-frozen` / `.z-frozen-inner` to expose a thin scrollbar (default WebKit scrollbar otherwise dominates).
- Mark the pane boundary with `border-inline-end` and a small box-shadow on `.z-frozen-col:last-of-type` so the divider is visible during horizontal scroll. Do not add `position: sticky` to the frozen cells on desktop — it conflicts with the `translate3d` driver; the sticky path is ZK's own and runs on mobile only.

**Side-by-side layout — float, not flex.** The stock theme's frozen layout requires:

```css
.z-frozen-body  { float: left;  overflow: hidden }
.z-frozen-inner { overflow-x: scroll; overflow-y: hidden }
.z-frozen-right { float: right; overflow: hidden }
.z-clear        { clear: both }    /* the trailing .z-clear div ends the float row */
```

Without `float: left` on `.z-frozen-body`, `.z-frozen-inner` stacks **below** body instead of next to it — and the horizontal scrollbar can no longer be seen above the data rows. The theme must match this layout exactly; you can't substitute flex/grid because ZK's JS reads the resulting offsets to position the frozen cells. The `.z-clear` div terminates the float row — keep its rule (`clear: both`).

**Tray height pitfall — macOS overlay scrollbars collapse to ~6px.** ZK calls `jq.scrollbarWidth()` to size the tray; on Macs with overlay scrollbars active it reads ≈6 (vs 15 on Windows / older browsers). Inline `height: 6px` on `.z-frozen`, `.z-frozen-body`, `.z-frozen-inner` is too thin to render a visible WebKit scrollbar. Force a `min-height: 12px` on all three — CSS `min-height` always wins against inline `height`, so the value is forced regardless of what JS measured.

**Visual indicator on `.z-frozen-body`.** That spacer aligns under the frozen columns; users need a visual cue that the columns above are frozen. Apply a faint background plus the same `border-inline-end + box-shadow` boundary you used on `.z-frozen-col`. That makes the frozen pane edge continue past the data rows.

## Sticky header (`z-sticky-header`)

ZK-4795 (since 9.6.0) gives **grid, listbox, and tree** an opt-in sticky column
header: add `sclass="z-sticky-header"` and the header pins to the top of its scroll
ancestor (the page, or any ancestor with `overflow:auto`) while rows scroll under it.

**It is a bare opt-in class — the `zul` module ships NO CSS for it.** The only
stock implementation lives in the `zkmax` addon's IceBlue grid, listbox and tree styles,
which a custom theme does not inherit. So
**any theme must supply the rule itself**, or the class is inert. The canonical
mechanism (per component, `<comp>` ∈ grid/listbox/tree):

```css
.z-<comp>.z-sticky-header            { overflow: visible; }   /* un-clip the header */
.z-<comp>.z-sticky-header .z-<comp>-header {
    position: sticky;
    top: 0;
    z-index: 1;            /* above the scrolling rows */
}
```

Two things to get right that ZK's own LESS does NOT handle:

- **The root must be `overflow: visible`.** Data components default to `overflow:hidden`
  (to clip body + honour the card radius). A `position:sticky` header cannot escape an
  `overflow:hidden` ancestor, so the sticky never engages. Overriding to `visible` is
  required and means the card's rounded corners no longer clip — an accepted trade-off
  for sticky mode (ZK does the same).
- **The header must get an OPAQUE background.** A transparent sticky header lets the
  scrolling rows show THROUGH it, so a theme whose header has no fill must give it the
  surface color. The stock zkmax styles omit this and inherit whatever the theme set, so
  the opaque background is theme-mandatory, not optional. (The existing header cell
  `border-bottom` already provides the divider against the scrolling content.)

Sticky engages relative to the **nearest scrolling ancestor**: if the component has no
fixed height and the page scrolls, it pins to the viewport; if it sits inside an
`overflow:auto` wrapper (as the preview pages do — `height:200px; overflow-y:auto`), it
pins to that wrapper's top. The component must NOT impose its own fixed body height in
this mode, or the body scrolls internally and the outer scroll never reaches the header.
Verify: scroll the container, then `getComputedStyle(header).position === 'sticky'` and
`header.getBoundingClientRect().top === container.getBoundingClientRect().top`.

## Tree-specific

- **Indent per level**: `.z-tree-spacer` width controls indentation (typically 20px per level).
- **Expand icon**: `.z-tree-icon`. Toggles rotation (0deg → 90deg) on open/close via CSS transform.
- **Connector lines**: `.z-tree-line` between rows (optional, off by default).

## Tree expand-icon rotation

ZK sets no `[open]` attribute. `Treeitem.ts` and `Treecell.ts` swap the `<i>`'s classes between `z-icon-caret-right` / `z-icon-caret-down` and `z-tree-close` / `z-tree-open` on open/close. `.z-tree-icon` is the outer `<span id="-open">`, and `.z-tree-spacer` is `span.z-tree-line.z-tree-spacer`. A rotation animation must therefore use these classes as its hook, for example:

```css
.z-tree-icon { transition: transform <duration> <easing>; }
```

(Exact transition value is a theme decision; the mechanism is ZK's.)

## Group-row `<td colspan="N">` — never set `display: flex/grid` on the cell

ZK's `<listgroup>` (and analogous group rows in grid/tree) renders one wide `<td>` with `colspan` covering every column:

```
TR.z-listgroup > TD.z-listgroup-inner[colspan="5"] > DIV.z-listcell-content > …
```

If theme CSS sets `display: flex` (or `grid`) directly on that `<td>`, the cell stops being a `display: table-cell` — HTML table layout then ignores its `colspan` and the cell collapses to a single-column width. Visible symptom: the group title is the same width as the first regular column, not the full row.

```css
/* Correct: keep the td as table-cell so colspan works; flex the inner DIV. */
td.z-listgroup-inner { display: table-cell; }
.z-listgroup-inner > .z-listcell-content {
    display: flex;
    align-items: center;
    gap: <gap>;
}
```

The same trap applies to any `<td>` that uses `colspan` (e.g. `.z-grid-foot` / `.z-foot` footer cells, frozen-row spanning, multi-level header cells emitted by auxhead).

## Listgroup / Listgroupfoot — borders on the ROW, not the cell

Group header (`.z-listgroup`) and group subtotal (`.z-listgroupfoot`) rows are TR elements with TDs inside. ZK renders cell content as:

```
TR.z-listgroup > TD.z-listcell > DIV.z-listcell-cnt > DIV.z-listcell-content
```

If you write `.z-listgroup .z-listcell-content { border-top; border-bottom }`, the border lands on the innermost DIV — every cell shows its own top+bottom lines, giving you 3 stacked lines across the row (since td/cnt/content all rendered separately). The intent is **one** divider per row, so put the border on the TR itself:

```css
.z-listgroup    { border-bottom: 1px <divider color> }   /* divider after group header */
.z-listgroupfoot { border-top:    1px <divider color> }  /* divider before subtotals */
```

Cell-level rules can still set `background-color: transparent` so the row band shows through. Tree's `.z-treegroup` follows the same pattern.

## Header-row dividers go on the TH — the header table is `border-collapse: separate`

The rule above ("borders on the ROW, not the cell") is true **only for the body table**,
which is `border-collapse: collapse` — there a TR border paints. The **header** table is the
opposite: grid/listbox/tree render the header `<table>` as `border-collapse: separate` — the
listbox/tree roots set it explicitly (so frozen-column TH `box-shadow` / `border-inline-end`
survive; collapse clips them), and grid's auxhead `*-headtbl` table is unclassed so it falls
to the browser default, which is also `separate`. **In `separate` mode browsers do NOT paint
borders on a `<tr>` / `<tbody>` — only on `table`, `th`, `td`.**

So any divider that needs to appear **between header rows** — most importantly the
`<auxhead>` row divider, and any multi-level header — MUST be set on the **TH cell**
(`.z-auxheader`, `.z-column` / `.z-listheader` / `.z-treecol`), never on the TR:

```css
/* WRONG — TR border is silently dropped in the separate-collapse header table */
.z-auxhead { border-bottom: 1px solid <divider color>; }

/* RIGHT — TH border paints in both collapse AND separate */
.z-auxheader { border-bottom: 1px solid <divider color>; }
```

Symptom when you get it wrong: a single `<auxhead>` still looks bordered (the adjacent
`.z-column`/`.z-treecol` TH supplies its own divider), but **two adjacent `<auxhead>` rows
merge into one tonal band** — there is no painted line between them, because the only thing
that would draw it is the dropped TR border (this is true of grid too — its `*-headtbl` is
`separate` as well). Grid merely *looked* acceptable because its previews show a single
`<auxhead>` whose weight reads against the column-header row, not two stacked aux rows that
need a line drawn between them. Put the divider on the TH and it is uniform across all three.

Keep the row-level `background-color` on the TR (the band) — only the *border* must move to
the cell. (Cell bg stays `transparent` so the row band shows through, per the Auxhead rule.)

### The header table is emitted UNCLASSED — reset `border-spacing` on the `table`, not a class

In ZK 10 the header table for all three components is rendered **unclassed**, as
`<table id="…-headtbl">` (see `grid.js` / the sel molds). There is **no `.z-grid-header-inner`
class** (older docs/comments claim one — it is stale). A theme rule keyed on
`.z-grid-header-inner` is therefore a **dead selector**, and the real `-headtbl` table falls
back to the **UA default `border-spacing: 2px`**. In `separate` mode (which the header table
is — see above) that 2px gap shows the table's background **between every cell and every row**
— e.g. a white line between two stacked `<auxhead>` rows, or between auxheader cells.

Reset it the way ZK's stock theme does — target the descendant `table`, never a class:

```css
.z-<comp>-header table {        /* grid / listbox / tree — matches the unclassed -headtbl */
    border-collapse: separate;  /* keep separate so frozen-col TH box-shadow paints */
    border-spacing: 0;          /* ← the UA default is 2px; without this you get white gaps */
    table-layout: fixed;
}
```

The stock IceBlue theme does the same with a `table { border-spacing: 0; th,td { padding: 0 } }`
reset mixin for grid, tree and listbox. Mirror that. The trap is
keying the reset on a class the runtime doesn't emit — then it silently no-ops and the UA
default leaks through only in the `separate` header table (the collapsed body table hides it,
because `collapse` ignores `border-spacing`).

**4th data table — `biglistbox`.** The zkmax virtual grid has the *same* unclassed-table
trap on **both** its head and body tables (`.z-biglistbox table { border-spacing:0 }`), and
its body shows the leak too (it is `separate`, unlike the collapsed grid/listbox/tree body).
When you touch border-spacing on any data table, sweep all four. See `components/biglistbox.md`.

## The outer frame is theme-driven, not ZK-driven

Unlike `window` (which has `setBorder`/`getBorder` → `border="none"` → `.z-window-noborder`),
`Grid`, `Listbox` and `Tree` have no `setBorder`/`getBorder`/`noborder` in their Java source, so
ZK emits **no class that signals "standalone vs embedded"** for these components. Consequences for any theme:

- Whether the component shows an outer frame is a **pure theme decision on the root class**
  (`.z-grid` / `.z-listbox` / `.z-tree`) — there is no ZK toggle to hook.
- A "strip the frame when embedded" affordance must be a **theme-defined sclass** the author
  applies (e.g. `z-grid-noborder` — mirror ZK's own `z-window-noborder` / `z-panel-noborder`
  naming for discoverability), and/or an **ancestor-context selector** (`.z-panel-body .z-grid`,
  `.z-groupbox .z-grid`) — ZK won't do it automatically.
- A theme-defined class such as `.z-grid-standalone` only does anything if a ZUL actually
  writes `sclass="z-grid-standalone"`; ZK never applies it.

## Bundles

- `grid.css.dsp` (zul/grid)
- `listbox.css.dsp` (zul/sel); zkmax has an override version
- `tree.css.dsp` (zul/sel); zkmax has an override version

The zkmax override files only apply when EE is loaded. See `reference/css-file-bundling.md`.
