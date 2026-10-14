# Component: grid (theme design)
tier: T1
category: data
preview: ${PREVIEW_URL}/grid.zul
rules: see .claude/skills/zk-component-rules/components/data-components.md
shared-css-file: zul/src/main/resources/web/js/zul/grid/css/grid.css
contract-approved: true
zk-version: 10.2.1-jakarta

## References
- MUI CSS: DataDisplay/Table.css
- DESIGN.md sections: §1, §2, §7, §10, §11

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-row` | height (rendered) | ~52px |
| c2 | `.z-cell` | padding | 16px (all sides per DESIGN.md §10) |
| c3 | `.z-cell` | font-size | 13px |
| c4 | `.z-column` | padding | 16px |
| c5 | `.z-column` | background-color | transparent OR very subtle tint |
| c6 | `.z-column` | font-weight | 500 |
| c7 | `.z-column` | border-bottom | 1px solid rgba(0, 0, 0, 0.12) |
| c8 | `.z-row` | border-bottom | 1px solid rgba(0, 0, 0, 0.12) |
| c9 | `.z-row-hover` or `.z-row:hover` | background-color | rgba(0, 0, 0, ~0.04) |
| c10 | `.z-grid-odd` | background-color | tinted alternate (or not used) |
| c11 | `.z-grid` | box-shadow | level-1 card shadow (if standalone) |
| c12 | `.z-grid` | border-radius | 6px (card-like) |

## States to evaluate
- [ ] default rows, header, hover, selected, striped (odd), sortable, card variant, frozen columns

## Frozen columns (shared CSS: mesh/css/frozen.css)

ZK `<frozen columns="N"/>` — ZK JS applies `transform: translate3d(scrollLeft,0,0); z-index:1`
inline on the first N cells. Cells paint above neighbours but transparent backgrounds
reveal scrolled-away content. The fix: explicit opaque backgrounds on all frozen cells.

**Preview anchor:** the grid page's "Frozen Columns" section (has `<frozen columns="2"/>`).

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| f1 | frozen header cell opaque | `.z-column.z-frozen-col` | `backgroundColor` | ≠ `rgba(0, 0, 0, 0)` | computedStyle |
| f2 | frozen body cell opaque (pre-scroll) | `.z-grid:has(.z-grid-frozen) .z-row .z-cell` | `backgroundColor` | ≠ `rgba(0, 0, 0, 0)` | computedStyle |
| f3 | z-grid-odd frozen body cell opaque | `.z-grid-odd.z-grid:has(.z-grid-frozen) .z-row .z-cell` (skip if absent) | `backgroundColor` | ≠ `rgba(0, 0, 0, 0)` | computedStyle |
| f4 | no bleed-through after scroll | scroll `.z-grid-body` to `scrollLeft=350`, wait 300ms | visual | no text from Col C visible inside frozen Col A/B area | scroll-trigger screenshot |

**scroll-trigger procedure for f4:**
```js
const gb = document.querySelector('.z-grid:has(.z-grid-frozen) .z-grid-body');
if (gb) gb.scrollLeft = 350;
```
Wait 300 ms, then screenshot. FAIL if any text from a non-frozen column overlaps the frozen column area.

## Sticky header (`z-sticky-header`)

ZK-4795 (since 9.6.0): `sclass="z-sticky-header"` pins the column header to the top
of the scroll ancestor (page or an `overflow:auto` wrapper) as rows scroll under it.
This is a **bare opt-in class** — ZK's `zul/less` ships NO rule; the implementation
lives in the `zkmax` addon, so the theme MUST supply it. See
`data-components.md` → "Sticky header (`z-sticky-header`)".

**Preview anchor:** the grid page's "Sticky Header (z-sticky-header)" section
(`grid-header.zul`), a `<grid sclass="z-sticky-header">` inside a `height:200px;
overflow-y:auto` scroller.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| sh1 | header pins on scroll | `.z-grid.z-sticky-header .z-grid-header` | `position` | `sticky` (with `top: 0px`) | computedStyle |
| sh2 | header opaque (no bleed-through) | `.z-grid.z-sticky-header .z-grid-header` | `background-color` | ≠ `rgba(0, 0, 0, 0)` — rows must not show through the pinned header | computedStyle |
| sh3 | root un-clips header | `.z-grid.z-sticky-header` | `overflow` | `visible` (else the header is clipped and cannot escape to stick) | computedStyle |

## Header table spacing (border-spacing)

The header table is emitted UNCLASSED as `<table id="…-headtbl">` (ZK 10) — there is **no
`.z-grid-header-inner` class**. A reset keyed on that stale class is a dead selector, and the
real table falls back to the UA default `border-spacing: 2px`, which in `separate` mode leaks
a white gap between auxhead cells/rows. Reset on the descendant `table` instead, mirroring
tree/listbox and the ZK default less. See `data-components.md` → "The header table is emitted
UNCLASSED".

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| hs1 | header table has no inter-cell gap | `.z-grid-header table` | `border-spacing` | `0px` (UA default 2px would leak white gaps between auxhead cells/rows under separate border-collapse) | computedStyle |

## Image-only header column with the column-menu button

A `<columns menupopup="…">` header that holds only an image needs a column of at least **58px on desktop and 78px
on a tablet** for the 16px image to render at full size clear of the menu button; narrower columns are not
supported and Marble does not engineer around them. The reasoning and measurements are in
`listbox.md` → "Minimum width of an image-only header column with the menu button" (same CSS rules in `grid.css`).
Guard: `mesh-header-footer.spec.ts`, `.mhf-grid-icon-menu`.

## Outer frame (container)

Default = standalone **outlined** card: border, NO shadow (never both). No-border variant
(`z-grid-noborder`) and panel/groupbox ancestry strip the border. ZK emits no border attribute
for grid (unlike window's `z-window-noborder`) → the variant is a theme sclass mirroring that
naming. See `doc/spec/DESIGN.md §11`.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| fr1 | default outlined | `.z-grid` | `border` | `1px solid` `--zk-color-outline-variant` | computedStyle |
| fr2 | never border + shadow | `.z-grid` | `box-shadow` | `none` | computedStyle |
| fr3 | noborder variant strips frame | `.z-grid.z-grid-noborder` | `border` | `none` (for nesting in a bounded parent) | computedStyle |
| fr4 | auto-flat inside panel/groupbox | `.z-panel-body .z-grid`, `.z-groupbox .z-grid` | `border` | `none` | computedStyle |
