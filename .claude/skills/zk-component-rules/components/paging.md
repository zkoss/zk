# paging

Pagination bar rendered below data components (listbox, grid, tree).

## DOM structure

```
.z-paging                          (root <nav> container)
└─ ul                              (button list — needs display: flex; list-style: none)
   ├─ .z-paging-button             (navigation button)
   │   ├─ .z-paging-previous       (← previous page — NOT .z-paging-prev)
   │   ├─ .z-paging-next           (→ next page)
   │   ├─ .z-paging-first          (« first page)
   │   └─ .z-paging-last           (» last page)
   └─ .z-paging-icon               (icon inside each button)
.z-paging-input                    (page-number input field)
.z-paging-text                     (label text: "Total:", "Page:", etc.)
```

## Critical naming trap

The previous-page button is `.z-paging-previous`, **NOT** `.z-paging-prev`. Writing `.z-paging-prev` rules in CSS will have no effect — the class never appears in the DOM.

## State classes

- `.z-paging-selected` — active page-number button
- `.z-paging-os` — OS-style layout variant (no bottom border)

## CSS file

`paging.css.dsp`

## Divider between the bar and the rows is owned by the host data-component

When a grid/listbox/tree uses `mold="paging"`, ZK wraps the bar in a per-component wrapper
named by `$s('paging-top')` / `$s('paging-bottom')` → `.z-grid-paging-top`/`-bottom`,
`.z-listbox-paging-*`, `.z-tree-paging-*`. The **host component's CSS** (grid.css / listbox.css
/ tree.css), not `paging.css`, must draw the divider that separates the bar from the data:

```css
.z-<comp>-paging-top    .z-paging { border-bottom: 1px solid var(--zk-color-outline-variant); }
.z-<comp>-paging-bottom .z-paging { border-top:    1px solid var(--zk-color-outline-variant); }
```

This is mandatory because the body's last row strips its own `border-bottom` (`:last-child`),
so without the wrapper border the bar floats against the rows with no separation. Keep the
rule in sync across all three host components — it is easy to add it to grid and forget tree.

## Note

Stub entry — full documentation pending spec-author run (Phase 3/4).
See `doc/spec-author-pipeline-plan.md (retired with the template)`.
