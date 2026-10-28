# biglistbox (zkmax virtual data grid)

`<biglistbox>` is a **zkmax (EE) virtual/lazy mesh** for unbounded data (millions of
rows/cols). It is NOT a Listbox variant — different widget, different DOM, its own
scrollbar. It renders only the visible `rows`×`cols` window and re-fetches from a
`MatrixModel` as you scroll. These are ZK-portable facts (true for any theme); theme
values (colors, thumb size, radius) live in `doc/spec/DESIGN.md` / `doc/contracts/biglistbox.md`.

## DOM structure

```
.z-biglistbox                              ← root (overflow:hidden, the card frame)
  .z-biglistbox-outer                      ← relative-positioned wrapper
    .z-biglistbox-head-outer               ← header viewport (overflow:hidden)
      table > thead.z-biglistbox-faker     ← zero-height row that reserves column widths
            > thead > tr > th.z-biglistbox-header[.z-biglistbox-sort][.z-biglistbox-header-leftmost]
                 > div.z-biglistbox-header-content
                     > div.z-biglistbox-sorticon > i[.z-icon-caret-up|.z-icon-caret-down]
                     > div[title]   ← the column label (sibling AFTER the sorticon)
    .z-biglistbox-body-outer               ← body viewport (overflow:hidden)
      table > thead.z-biglistbox-faker
            > tbody > tr.z-biglistbox-row[.z-biglistbox-odd][.z-biglistbox-selected]
                 > td[data-axis="c,r"]
    .z-focus-a                             ← focus trap (see data-components.md)
    .z-biglistbox-wscroll-vertical         ← custom scrollbar (see below)
    .z-biglistbox-wscroll-horizontal
    .z-biglistbox-verticalbar-*            ← frozen-column resize bar (only with frozenCols)
```

Key class facts:
- Alternating rows: `.z-biglistbox-odd` (explicit class, not `:nth-child`).
- Selected row: `.z-biglistbox-selected` on the TR.
- **There is no `.z-biglistbox-cell` class** — body cells are bare `<td data-axis="c,r">`.
  Style cells via `.z-biglistbox-row td`. (An old contract asserted `.z-biglistbox-cell`;
  it matches nothing.)
- Sort active adds `.z-biglistbox-sort` (cursor:pointer) to the TH; the caret `<i>` only
  gets a `z-icon-caret-up|down` class **when that column is the sorted one** — unsorted
  headers carry an empty `<i class="">`.

## Both tables are emitted UNCLASSED — reset `border-spacing:0`

Same class of bug as the grid/listbox/tree header rule in `data-components.md`
("The header table is emitted UNCLASSED"). Biglistbox's **head AND body** `<table>`s
have no theme class, so they fall to the UA default `border-collapse:separate;
border-spacing:2px`. That 2px leaks between every cell and every row, and most visibly
puts an ~8px gap between the header's bottom border and the first row — making **row 1
look taller than the rest** even though all rows are uniform height.

```css
.z-biglistbox table { border-collapse: separate; border-spacing: 0; }
```

ZK's own default theme does this via a `.resetTable()` mixin (`biglistbox.less` ~line 3).
This is the 4th ZK data table (after grid/listbox/tree) that needs the same reset —
always sweep all four when touching table border-spacing.

## Sort caret must be laid out inline — it must never add a header row

`.z-biglistbox-header-content` defaults to `display:block`, and `.z-biglistbox-sorticon`
is a full-width block sitting **before** the label. So when the caret glyph appears the
icon row + label row stack into **two lines**, ~doubling the header height. ZK's default
theme avoids this with `position:absolute` on the sorticon; a flex layout is cleaner and
matches MUI DataGrid (caret trails the label):

```css
.z-biglistbox-header-content { display: flex; align-items: center; gap: <token>; }
.z-biglistbox-sorticon       { order: 1; flex: 0 0 auto; display: inline-flex; }
```

Verify the header height is **identical before and after sorting** (it is the only state
that injects the glyph, so a static screenshot won't catch a regression here).

## Biglistbox draws its OWN scrollbar (`zul.WScroll`) — there is no native scrollbar

This is the biggest theme trap. The root and viewports are `overflow:hidden`; scrolling
is driven by a custom widget. **A theme that styles nothing here ships an invisible,
zero-size scrollbar — overflowed rows/cols become unreachable.** ZK's default theme sizes
it (`@biglistboxSize:18px`, `@biglistboxWscrollDragSize:116px`) and paints arrow-button
glyphs from the `ZK85Icons` font (which custom themes do not inherit).

**Two positioning requirements ZK's less provides but a custom theme must re-supply — get
either wrong and the bar looks missing or the thumb jumps off-track:**

1. **`.z-biglistbox` AND `.z-biglistbox-outer` must be `position:relative`.** The wscroll
   bars are `position:absolute` children of `-outer`. With no positioned ancestor they
   anchor to the **viewport** — the bar renders at the top-right corner of the *page*,
   500+px away from the grid, and looks entirely missing on the component. (Marble shipped
   neither rule and the scrollbar was off-component.)
2. **Every wscroll part you keep must declare its own `position:absolute`.** ZK sets each
   part's `top`/`left` via *inline style* and expects the theme's less to have made it
   absolute. The killer case is the **end-stop `-endbar` (eend)**: WScroll clamps the thumb
   on wheel/drag with `end = eend.offsetTop − thumbHeight`. If eend is `position:static`,
   ZK's inline `top` is ignored, `offsetTop` reads 0, and the thumb is clamped to a
   **negative** top (jumps above the track) on the first scroll. **Do NOT `display:none`
   the endbar either** — `zk(eend).isVisible()` still returns true (it ignores your
   stylesheet), but `display:none` also zeroes `offsetTop` → same negative clamp. Hide it
   with `position:absolute; visibility:hidden` (keeps the box + correct offsetTop).

WScroll element roles (`zul/WScroll.ts`), per bar (`-vertical` / `-horizontal`):

| element | class | role |
|---------|-------|------|
| `node`       | `.z-biglistbox-wscroll-vertical`            | the track container |
| `edrag`      | `.z-biglistbox-wscroll-drag` (`node.firstChild`) | **the persistent, fixed-size thumb** (its 5 button children are absolutely positioned inside it) |
| `edragBody`  | `.z-biglistbox-wscroll-body` (`edrag.childNodes[2]`) | the **only** element that initiates a drag (`ignoredrag` rejects all others) |
| `epos`       | `.z-biglistbox-wscroll-pos` (`edrag.nextSibling`) | transient drag-*preview*; ZK keeps it `display:none` except mid-drag |
| `eend`       | `.z-biglistbox-wscroll-endbar` (`node.lastChild`) | end marker |
| buttons | `-home`,`-up`,`-down`,`-end` | arrow-step buttons; safe to `display:none` (absolutely positioned, don't affect `edrag.offsetHeight`) |

**Construction-time `_gap` coupling (the subtle part).** WScroll computes once, in `bind_`:
`_gap = edrag.offsetHeight − epos.offsetHeight` (heights for vertical, widths for
horizontal). The effective thumb size used by every scroll calc is
`edrag.offsetHeight − _gap`. Two consequences:

1. If `epos` has **zero** box at construction, `_gap = edrag.offsetHeight`, so the
   effective thumb size becomes 0 and the thumb mispositions (slides off the track).
   **Therefore `epos` must NOT be `display:none`** — give it a real box and hide it with
   `visibility:hidden` / `opacity:0` instead (these preserve `offsetHeight`). Override
   ZK's inline `display:none` with `display:block !important`.
2. Make `-drag` and `-pos` the **same fixed size** ⇒ `_gap = 0` ⇒ the thumb is a clean
   fixed pill that positions correctly.

**Scale cap — inherent, not a bug.** WScroll caps `_scale` at 1 when content is modest
(`viewportSize − thumb > totalSteps`). For large data (biglistbox's purpose) `_scale < 1`
and the thumb traverses the whole track proportionally; for small datasets `_scale = 1`,
so the fixed thumb only moves `(steps × 1)` px and sits in the upper region of a tall
track — at max scroll the thumb does **not** reach the track bottom. This is **not tunable
from CSS** (it's JS) and is **identical in ZK's default theme** — empirically confirmed:
simulating the default's 116px thumb (`@biglistboxWscrollDragSize`) on a 100-row model,
`scale=1`, thumb at max scroll tops out at 127/500 (vs ~243/500 with the fatter thumb) —
same compression, just less obvious with a bigger thumb. Don't chase it; verify large-data
travel (HugeRow/HugeColumn) instead. A true small-data proportional thumb needs a JS shim
over WScroll (rejected — keeps the theme CSS-only; the user accepted the default behavior).

A minimal MD3 thin-overlay implementation: `position:relative` root + outer; the track
divs (`-vertical`/`-horizontal`) are `visibility:hidden` (geometry stays CSS-only and JS
never touches it; hit-testing passes through, no pixels drawn) while the `-drag` thumb is
`visibility:visible` — a rounded 8px pill in `--zk-color-outline-variant` with **no
groove** (`::before { content:none }`), matching the document scrollbar's resting look;
do not shift the track down to clear the header, because WScroll positions the thumb
relative to the track top (it would double-offset); arrow buttons `display:none`;
`-endbar` `position:absolute; visibility:hidden` (kept boxed for the clamp math); `-pos`
boxed-but-invisible and the same size as `-drag`. See `doc/contracts/biglistbox.md` for
the theme-specific values.

**Verification gotcha — the horizontal bar hides below the page fold.** The vertical bar
runs the right edge *from the top*, so it shows as soon as the component's top is on screen.
The horizontal bar lives **only on the bottom edge**, so a `height:500px` biglistbox sitting
~600px down a page taller than the viewport puts its bottom edge — and the whole horizontal
bar — *below the fold*. "I see no horizontal scrollbar" is almost always this, not a CSS
bug: `scrollIntoView({block:'end'})` the component before judging it missing. Both axes were
verified working (horizontal shift-wheel scrolls `_currentX` 0→86/100 with the thumb
`offsetLeft` tracking 0→max, in-bounds and monotonic). Note the `scale=1` compression reads
**worse horizontally**: the horizontal track is far wider (~1832px) than the vertical track
is tall (~500px), so an identical step-count travel parks the horizontal thumb in the
far-left ~5% corner where it looks static — still ZK-inherent (axis-agnostic `WScroll.js`),
not tunable from CSS.

## Demo / preview note (not a ZK fact, but a recurring trap)

Wiring a biglistbox control's `onSelect`/property change in an **inline `zscript`** that
reflectively walks a collection (`event.selectedObjects.iterator().next()`) throws under
JDK 17: *"module java.base does not opens java.util to unnamed module"* (BeanShell runs in
the unnamed module). Put such handlers in a **compiled composer** (`@Listen`) instead —
compiled bytecode has no reflective-access restriction. (This is a page-authoring rule, not
a theme/skill gap; recorded here so the next biglistbox demo doesn't repeat it.)

## Bundle

`biglistbox.css.dsp` (standalone 1:1, `js/zkmax/big/css/`). zkmax/EE only.
