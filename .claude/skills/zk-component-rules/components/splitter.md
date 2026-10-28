# splitter

A draggable divider between layout regions (typically inside `hbox`/`vbox` or `borderlayout`).

## Delivery: merged into `box.css.dsp`

Despite the source file `js/zul/box/css/splitter.css` existing, the build merges its content into `box.css.dsp`. There is no standalone `splitter.css.dsp` served to the browser. Edits to splitter styles end up in `box.css`.

See `reference/css-file-bundling.md` for the full list of delivery quirks.

## DOM structure

```
.z-splitter[.z-splitter-{horizontal|vertical}][.z-splitter-nosplitter]
├─ .z-splitter-button            (the draggable handle visible to the user)
│  ├─ <i class="z-splitter-icon z-icon-ellipsis-{v|h}">   (grip 1)
│  ├─ <i class="z-splitter-icon">                          (collapse caret)
│  └─ <i class="z-splitter-icon z-icon-ellipsis-{v|h}">   (grip 2)
└─ .z-splitter-ghost             (drag preview, shown during drag operation)
```

(mold: `zul/box/mold/splitter.js` — same button + grip/caret/grip pattern as borderlayout's `layoutregion.js` and splitlayout's `splitlayout.js`)

## Button positioning — JS centers via inline margin

ZK centers `.z-splitter-button` on the bar's long axis by JS (`zul/box/Splitter.ts` `setBtnPos_`): inline `margin-left` (horizontal splitter) / `margin-top` (vertical splitter) computed from `(bar offset − btn offset)/2`. **Never half-mix CSS and JS centering on the same axis** — inline margin stacks on top of CSS positioning and double-centers the button toward the end of the bar. An axis must be owned entirely by one side: either no CSS centering at all (JS-owned), or neutralize the inline margin with `margin-left/top: 0 !important` (stylesheet `!important` beats inline non-important styles) and center via `left/top: 50% + transform` (CSS-owned).

**Timing trap**: `setBtnPos_` may run before the bar's flex-resolved dimension is computed (offset reads 0 → margin written as 0) and not re-run until the next resize — verified live in splitlayout 2026-06-04. Prefer **CSS ownership of the long axis** when the bar's long-axis size comes from flex/stretch; JS-owned centering is only safe when the relevant bar dimension is fixed by CSS at bind time. Same pattern in splitlayout and borderlayout — see `components/splitlayout.md` / `components/borderlayout.md`.

## Drag persistence — px written onto the parent box's `<td>`s

The default-mold parent hbox/vbox is a nested TABLE (see `components/box.md`). Splitter drag is implemented entirely against that table model (`zul/box/Splitter.ts`):

- `_doDragEndResize` persists the drag by writing inline px `width` (hbox) / `height` (vbox, on `run.prev/next.cells[0]`) onto the **adjacent chdex `<td>`s** — then re-reads `clientWidth/Height` and corrects, so the table must actually respond to td sizes.
- `_snap` clamps the drag delta to the adjacent panes' `offsetWidth/Height` — if the panes are squashed (broken `width/height:100%` chain), the clamp is ≈ 0 and the drag is visually dead even though the JS "works".
- `_fixsz` sizes the bar itself: vertical splitter → `width: 100%` (must resolve against its td); horizontal splitter → px height from the parent's `clientHeight`.
- Sibling navigation (`_prev`/`_next` = `.prev().prev()`) assumes the always-rendered `-chdex2` separator between children.

**Consequence for themes**: any rule that breaks table layout in the box (changing `display` on `.z-hbox`/`.z-vbox`, making chdex tds non-`table-cell`) kills splitter drag — shipped as a real Marble bug 2026-06-06 (`display:inline-flex` on the outer table → both default-mold drags dead on `/splitter.zul`).

## Cursor mode

- `.z-splitter-horizontal` → cursor `col-resize` (drag left/right)
- `.z-splitter-vertical` → cursor `row-resize` (drag up/down)

(The naming is from the perspective of the parent container's orientation, so "horizontal splitter inside a horizontal layout" lets you drag left/right.)

## Disabled

`.z-splitter-nosplitter` — the splitter renders but is not draggable. Useful as a static visual divider.

## Ghost during drag

`.z-splitter-ghost` is shown only during the drag operation. Style it with a low-opacity highlight to indicate the drag target.
