# Class-name quirks (non-intuitive z-* class names)

ZK's class naming is mostly predictable (`.z-{component}-{part}`) but several components break the pattern. Always verify the actual class via DevTools or `doc/component-dom-structures.md (retired with the template)` before writing a selector.

## Confirmed non-intuitive class names

| Component | Intuitive guess | Actual class |
|-----------|-----------------|--------------|
| panel content area | `.z-panel-content` | `.z-panelchildren` |
| grid cell content | `.z-cell-content` / `.z-cell-cnt` | `.z-row-content` (on the row, not the cell) |
| listbox cell content | `.z-listcell-cnt` | `.z-listcell-content` |
| tree cell content | `.z-treecell-cnt` | `.z-treecell-content` |
| groupbox body | `.z-groupbox-body` | `.z-groupbox-content` |
| rating star | `.z-rating-star` | `.z-rating-icon` |
| progressmeter fill | `.z-progressmeter-bar` | `.z-progressmeter-image` |
| paging previous button | `.z-paging-prev` | `.z-paging-previous` |
| chosenbox tag | `.z-chosenbox-tag` | `.z-chosenbox-item` |
| window title | `.z-window-title` (child) | (no child element — plain text node in header) |
| separator visible bar | `.z-separator` (transparent base only) | `.z-separator-horizontal-bar` |
| menubar item | `.z-menubar > .z-menu` | `.z-menubar > ul > li > .z-menu` (intermediate `<ul><li>`) |
| hlayout/vlayout inner | (none) | `.z-hlayout-inner` / `.z-vlayout-inner` wraps the flex items |
| treenode expand icon | `.z-tree-expand` | `.z-tree-icon` (toggles open/close) |

## Alternating row classes

ZK emits explicit `-odd` classes for alternating row styles. Prefer these over `:nth-child()`:

- `.z-grid-odd` (and matching `.z-row.z-grid-odd`)
- `.z-listbox-odd` / `.z-listitem.z-listbox-odd`
- `.z-tree-odd`

Reason: `:nth-child` breaks with ZK's dynamic row insertion/removal in virtual-scroll modes; the explicit class survives re-renders.

## Frozen / sticky columns

- `.z-frozen-sticky` — sticky-positioned frozen columns. Uses `z-index: 1`. Don't override.

## Tabbox orientation

The orientation class is on the tabbox root:

- `.z-tabbox-top` (default)
- `.z-tabbox-bottom`
- `.z-tabbox-left`
- `.z-tabbox-right`
- `.z-tabbox-accordion` (treated as an orientation in ZK's API)

## Splitter

- `.z-splitter-horizontal` / `.z-splitter-vertical`
- `.z-splitter-button` — the draggable handle
- `.z-splitter-ghost` — the drag preview
- `.z-splitter-nosplitter` — disabled splitter bar (still renders, but not draggable)

## Toast positions

Nine position classes on toast root:

`.z-toast-position-{top|middle|bottom}-{left|center|right}`

## Notification severity

`.z-notification-info`, `.z-notification-warning`, `.z-notification-error` — applied to the notification root for semantic styling.

## How to discover the actual class

When in doubt:

1. Open the preview page for the component in the browser.
2. Inspect the DOM via DevTools.
3. Cross-reference with `doc/component-dom-structures.md (retired with the template)`.
4. Check ZK source at `/Users/hawk/Documents/workspace/ZK10/zk/zul/src/main/resources/web/js/zul/{module}/` — the widget's `.ts` file lists its `_class` and child class names.

Never trust the "obvious" guess. ZK has 15+ years of accumulated naming decisions and the patterns shifted across versions.
