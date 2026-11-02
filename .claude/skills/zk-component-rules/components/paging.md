# paging

Pagination bar rendered below data components (listbox, grid, tree).

## DOM structure

Default mold (source: `zul/mesh/mold/paging.js`):

```
nav.z-paging
└─ ul                                   (button list — needs display: flex; list-style: none)
   ├─ li > button.z-paging-button.z-paging-first      > i.z-paging-icon
   ├─ li > button.z-paging-button.z-paging-previous   > i.z-paging-icon   (NOT .z-paging-prev)
   ├─ li > input.z-paging-input + span.z-paging-text  (page-number field, " / <count>" text)
   ├─ li > button.z-paging-button.z-paging-next       > i.z-paging-icon
   └─ li > button.z-paging-button.z-paging-last       > i.z-paging-icon
```

`.z-paging-button` and the position class (`-first`, `-previous`, `-next`, `-last`) are on the same `<button>`, not parent and child. The `<i.z-paging-icon>` is inside the button.

The `os` mold renders `nav.z-paging.z-paging-os > ul > li > a.z-paging-button`; `.z-paging-selected` is used only there, on the active page-number link.

## Critical naming trap

The previous-page button is `.z-paging-previous`, **NOT** `.z-paging-prev`. Writing `.z-paging-prev` rules in CSS will have no effect — the class never appears in the DOM.

## CSS file

`paging.css.dsp`

## Divider between the bar and the rows is owned by the host data-component

When a grid/listbox/tree uses `mold="paging"`, ZK wraps the bar in a per-component wrapper
named by `$s('paging-top')` / `$s('paging-bottom')` → `.z-grid-paging-top`/`-bottom`,
`.z-listbox-paging-*`, `.z-tree-paging-*`. The **host component's CSS** (grid.css / listbox.css
/ tree.css), not `paging.css`, must draw the divider that separates the bar from the data.
Keep the rule in sync across all three host components — it is easy to add it to grid and forget tree.

To find the current rules:

```
grep -n "paging-top\|paging-bottom" zul/src/main/resources/web/js/zul/*/css/*.css
```
