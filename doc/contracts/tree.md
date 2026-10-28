# Component: tree (theme design)
tier: T1
category: selection
preview: ${PREVIEW_URL}/tree.zul
rules: see .claude/skills/zk-component-rules/components/data-components.md
shared-css-file: zul/src/main/resources/web/js/zul/sel/css/tree.css
contract-approved: true
zk-version: 10.2.1-jakarta

## References
- MUI CSS: DataDisplay/List.css
- DESIGN.md sections: §1, §2, §7, §10, §11

## Expected values
Mirror listbox/grid metrics. Plus:
- `.z-tree-icon` size 16–18px, rotation transition on open.
- Indent per level: 20–24px.

### Selection (list-row family)
Tree row is the LIST-ROW selection family per
`reference/selected-state-families.md` — same as listbox / combobox dropdown /
menu / searchbox dropdown.

| id | selector | property | expected |
|----|----------|----------|----------|
| s1 | `.z-treerow-selected` (or `.z-treerow.z-treerow-selected`) | background-color | `rgb(201, 213, 234)` (≈ `--zk-color-secondary-container`) |
| s2 | `.z-treerow-selected .z-treecell-content` | color | `rgb(21, 30, 45)` (≈ `--zk-color-on-secondary-container`) |
| s3 | `.z-treerow-selected` | background-color | MUST NOT be `rgb(178, 223, 219)` (= `--zk-color-secondary-container`) — wrong family |

### Checkmark column (checkbox in `multiple`, radio in single selection)

`checkmark="true"` renders a selection control in the first cell. Same fork as listbox:
`multiple` → `z-icon-check` (checkbox), single → `z-icon-radio` (radio). Tree's class
prefix is **`z-treerow`** (Treeitem zclass resolves to the treerow's). Both controls
must be real Material controls — NOT the generic flat mask glyph (no border,
`background-color: rgba(0,0,0,.87)` on `::before`). See `data-components.md` →
"Checkmark column renders a checkbox … OR a radio".

**Preview anchor:** the tree page's "Tree with Checkmark" section (multiple), the
single-selection checkmark case, and the tristate (partial) case.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| cm1 | multi → real checkbox box | `.z-treerow-checkbox .z-treerow-icon.z-icon-check::before` | border-width / border-radius | border ≈ `1.5px` solid `outline`; `border-radius: 2px`; `mask-image: none` | computedStyle |
| cm2 | multi selected → filled box | `.z-treerow-selected .z-treerow-icon.z-icon-check::before` | background-color | `rgb(55, 111, 208)` (= `--zk-color-primary`) with a checkmark `background-image` | computedStyle |
| cm3 | single → real radio circle | `.z-treerow-radio .z-treerow-icon.z-icon-radio::before` | border-radius | `50%` (circle), `mask-image: none`, NOT `rgba(0,0,0,.87)` flat glyph | computedStyle |
| cm4 | single selected → filled dot | `.z-treerow-radio.z-treerow-selected .z-treerow-icon.z-icon-radio::before` (or `::after`) | — | border-color/inner-dot = `--zk-color-primary` | computedStyle |
| cm5 | header select-all is a checkbox box | `.z-treecol-checkable .z-treecol-icon.z-icon-check::before` | border-radius | `2px` box (multiple only); `mask-image: none` | computedStyle |
| cm6 | header box aligns with row boxes | `.z-treecol-checkable .z-treecol-icon` vs `.z-treerow-checkable .z-treerow-icon` | getBoundingClientRect centerX | header `centerX === row centerX` (±1px). Offset source is `.z-treecol-content` flex `gap:4px` acting on the zero-width sorticon — `display:none` the sorticon, not `margin-left:0` | getBoundingClientRect |
| cm7 | tristate row → indeterminate (minus) box | `.z-treerow-partial .z-treerow-icon.z-icon-minus::before` | background-color / background-image | filled `rgb(55, 111, 208)` (= `--zk-color-primary`) box with a white **minus** `background-image`; `mask-image: none`. Requires a `TristateModel` (ZK ships none — see skill) | computedStyle |
| cm8 | header select-all → indeterminate when only some rows selected | `.z-treecol-icon.z-icon-minus::before` | background-color | same filled primary minus box as cm7 (ZK switches the header icon class to `z-icon-minus`, not a `z-treecol-partial` class) | computedStyle |
| cm9 | centred checkmark column → both boxes on the column centre | `.z-treecol[style*="text-align:center"] .z-treecol-icon` vs `.z-treerow-icon` in the same column, vs the `th` | getBoundingClientRect centerX | header `centerX`, row `centerX` and column `centerX` within ±1px at 24, 40, 48 and 160px, default, compact and tablet (`.mhf-tree-box-column-center-*`, `mesh-header-footer.spec.ts`). A left-aligned column keeps both boxes on one keyline (`.mhf-tree-box-column-left-*`). Offset sources before follow-up 54: the 20px `.z-tree-spacer` (+4px gap) after the leaf box was centred with it (row box 12px left of centre at 160px); the header's 16px/4px padding and the box margin (header box right of centre); on a tablet the 24px `.z-treerow-checkable` span put the 16px icon 4px right of the header box. The centring rules cover select-all-only columns: a centred column with a label (`.mhf-tree-label-*`, follow-up 59) keeps its row padding (16px / 12px compact / 24px tablet before the box), its indent (24px per level), and a block header with the ellipsis, the box inside the column and a 4px gap to the label; in a tree without a container row its header unit is centred on the column, left of where the 16px/4px padding put it: by default 4px (short label in 160px) and 12px (long label overflowing 100px), compact 2px and 8px, tablet 6px and 16px; where the label overflows, the header box no longer sits on the row-box keyline (12px, 8px and 20px off in default, compact and tablet), accepted because it occurs only when the label overflows a narrow column. `.mhf-tree-label-100` and `.mhf-tree-label-160` have nested rows, so in them (a tree with a container row) the header is the pre-follow-up-54 one: its box 16px / 12px / 20px from the edge of the overflowing 100px column, which is 0px / 0px / 4px off the row-box keyline in default, compact and tablet. All centring rules, header and rows, apply to a tree without a container row only (`.z-tree:not(:has(> .z-tree-body > table > tbody > tr > td > .z-treecell-content > .z-treerow-checkable + .z-tree-icon))`, NS30, follow-up 59): a tree with a container row, open or closed, is judged on its own rows (the check is anchored to the rows of the tree itself and the rules reach its own header and rows only, so an outer flat tree whose cell holds a nested checkmark Tree with a container row keeps its centring, and the inner tree keeps its own keyline; NS31) and renders header and rows as before follow-up 54, so a level-0 leaf box sits on the keyline of its container siblings (a centred leaf sat 12px right of them, one nesting step the wrong way, and a container that lost its last child jumped 12px) and the header box sits right of the column centre again (4px default, 2px compact, 6px tablet, `.mhf-tree-mixed-box`). Limitation: with the paging mold or opt-in ROD the CSS sees the rendered rows only, so a page that holds leaves only is centred and a page that holds a container row is not, and an ROD tree jumps 12px when a container row loads (`.mhf-tree-outer-box`, `.mhf-tree-inner-box`, `.mhf-tree-label-flat-*` guard the anchored check and the flat labelled header: unit centred on the 160px column, starting at the edge of the overflowing 100px one, 4px gap, ellipsis). In a tree without a container row the row rules centre a level-0 leaf box-only cell only, that is a cell whose content is the box followed by one spacer and nothing else (`.z-treecell-content:has(> .z-treerow-checkable + .z-tree-spacer:last-child)`); indentation carries the hierarchy, so a container row, a child row and a cell with a label or a component keep the pre-follow-up-54 geometry (24px of indent per level, the box moving 12px per level in a centred cell; `.mhf-tree-nested-*`) | getBoundingClientRect |

## States to evaluate
- [ ] default, hover, selected, focus, expanded, collapsed, disabled, frozen columns
- [ ] checkmark column: multiple (checkbox) + single (radio); header select-all box
- [ ] tristate: row indeterminate (minus) box + header indeterminate box (needs a `TristateModel`)

## Frozen columns (shared CSS: mesh/css/frozen.css)

ZK `<frozen columns="N"/>` — same bleed-through mechanism as grid and listbox.

**Preview anchor:** the tree page's "Frozen Columns" section (if present). If the page
has no frozen demo, mark all f-checks `SKIPPED` — do not FAIL.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| f1 | frozen header cell opaque | `.z-treecol.z-frozen-col` | `backgroundColor` | ≠ `rgba(0, 0, 0, 0)` | computedStyle |
| f2 | frozen body cell opaque (pre-scroll) | `.z-tree:has(.z-tree-frozen) .z-treerow .z-treecell` | `backgroundColor` | ≠ `rgba(0, 0, 0, 0)` | computedStyle |
| f3 | no bleed-through after scroll | scroll `.z-tree-body` to `scrollLeft=350`, wait 300ms | visual | no text from a non-frozen column visible inside frozen column area | scroll-trigger screenshot |

**scroll-trigger procedure for f3:**
```js
const tb = document.querySelector('.z-tree:has(.z-tree-frozen) .z-tree-body');
if (tb) tb.scrollLeft = 350;
```
Wait 300 ms, then screenshot. FAIL if any text from a non-frozen column overlaps the frozen column area.

## Sticky header (`z-sticky-header`)

ZK-4795 (since 9.6.0): `sclass="z-sticky-header"` pins the column header to the top
of the scroll ancestor (page or an `overflow:auto` wrapper) as rows scroll under it.
This is a **bare opt-in class** — ZK's `zul/less` ships NO rule; the implementation
lives in the `zkmax` addon, so the theme MUST supply it. See
`data-components.md` → "Sticky header (`z-sticky-header`)".

**Preview anchor:** the tree page's "Sticky Header (z-sticky-header)" section
(`tree-header.zul`), a `<tree sclass="z-sticky-header">` inside a `height:200px;
overflow-y:auto` scroller.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| sh1 | header pins on scroll | `.z-tree.z-sticky-header .z-tree-header` | `position` | `sticky` (with `top: 0px`) | computedStyle |
| sh2 | header opaque (no bleed-through) | `.z-tree.z-sticky-header .z-tree-header` | `background-color` | ≠ `rgba(0, 0, 0, 0)` — rows must not show through the pinned header | computedStyle |
| sh3 | root un-clips header | `.z-tree.z-sticky-header` | `overflow` | `visible` (else the header is clipped and cannot escape to stick) | computedStyle |

## Paging divider (`mold="paging"`)

Tree wraps the paging bar in `.z-tree-paging-top` / `.z-tree-paging-bottom`
(mold `$s('paging-top/bottom')`, the same scheme as grid's `.z-grid-paging-*`). The host
component owns the divider between the bar and the rows — the last `.z-treerow` strips its
own `border-bottom` (`:last-child`), so without a wrapper border the bar floats against the
rows with no separation. See `components/paging.md`.

**Preview anchor:** the tree page's "Paging with Tree" section (`tree.zul`), a
`<tree mold="paging">` with a radiogroup toggling `top` / `bottom` / `both`.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| pg1 | top bar divides from header below | `.z-tree-paging-top .z-paging` | `border-bottom` | `1px solid` `--zk-color-outline-variant` | computedStyle |
| pg2 | bottom bar divides from rows above | `.z-tree-paging-bottom .z-paging` | `border-top` | `1px solid` `--zk-color-outline-variant` | computedStyle |

## Auxhead divider (shared CSS: mesh/css/auxhead.css)

The header table is `border-collapse: separate` (so frozen-col TH `box-shadow` /
`border-inline-end` paint). In `separate` mode browsers do NOT paint borders on a TR — so
the auxhead row divider MUST live on the `.z-auxheader` **TH cell** `border-bottom`, not on
`.z-auxhead` (the TR). Without this, two adjacent `<auxhead>` rows merge into one tonal band
(no painted line between them). See `data-components.md` → "Header-row dividers go on the TH".

**Preview anchor:** the "Auxhead + Treecols Combinations" section (`tree-header.zul`),
specifically the 2nd / 3rd trees which stack two `<auxhead>` rows adjacently.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| ax1 | auxhead row divider paints (on the TH) | `.z-auxheader` | `border-bottom` | `1px solid` `--zk-color-outline-variant` (so adjacent aux rows separate under `border-collapse: separate`) | computedStyle |
| hs1 | header table has no inter-cell gap | `.z-tree-header table` | `border-spacing` | `0px` (UA default 2px would leak white gaps between cells/rows under separate border-collapse) | computedStyle |

## Outer frame (container)

Default = standalone **outlined** card: border, NO shadow (never both). No-border variant
(`z-tree-noborder`) and panel/groupbox ancestry strip the border. ZK emits no border attribute
for tree (unlike window's `z-window-noborder`) → the variant is a theme sclass mirroring that
naming. See `doc/spec/DESIGN.md §11`.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| fr1 | default outlined | `.z-tree` | `border` | `1px solid` `--zk-color-outline-variant` | computedStyle |
| fr2 | never border + shadow | `.z-tree` | `box-shadow` | `none` | computedStyle |
| fr3 | noborder variant strips frame | `.z-tree.z-tree-noborder` | `border` | `none` (for nesting in a bounded parent) | computedStyle |
| fr4 | auto-flat inside panel/groupbox | `.z-panel-body .z-tree`, `.z-groupbox .z-tree` | `border` | `none` | computedStyle |
