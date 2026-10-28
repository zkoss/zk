# Component: portallayout (theme design)
tier: T1
category: layout
preview: ${PREVIEW_URL}/portallayout.zul
rules: see .claude/skills/zk-component-rules/components/portallayout.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/Portallayout.ts
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/mold/portallayout.js
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/Portalchildren.ts
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/mold/portalchildren.js
js-source-hash: 1f082acde43b9bc8c5bb497d5d53149f6caed76c43ba994c7fa85d2c4b6db3f6
closest-sibling: none — novel dashboard shell; panel sibling governs visual substance of children (see components/panel.md); the layout container itself has no ZK sibling

## References
- MUI CSS: no analog — Material-UI 7 ships no portal/dashboard-column layout; closest conceptual match is MUI `Paper` for framed columns
- DESIGN.md sections: §1 (surfaces — transparent layout shell, surface for framed columns), §4 (spacing — column gap and inner padding), §5 (corner radii — framed column uses card radius), §6 (elevation — framed column uses elevation-1 or outlined variant), §8 (state layers — drag ghost), §9 (motion — drag ghost transition)
- Iceblue baseline: doc/contracts/baselines/portallayout-iceblue.png
- HTML contract: doc/contracts/portallayout.html

## Design Contract

The portallayout shell is intentionally invisible — it is a transparent flex row of columns whose sole job is to arrange and resize panel children. No background, no border, no shadow on `.z-portallayout` itself: the panels supply all the visual weight.

The **plain column** (no `title` on `Portalchildren`) renders as a transparent wrapper with no additional chrome — 100% width inside its column, panels stacked with `--zk-spacing-3` gap between them (supplied by portallayout's own scoped rule `.z-portalchildren-content > .z-panel { margin-bottom: var(--zk-spacing-3) }` — widgets carry no theme-default margins since the 2026-06-05 rhythm removal, so the component declares its stacking gap itself). Adjacent columns are separated by a matching `--zk-spacing-3` **horizontal gutter** so the dashboard reads as a true multi-column grid rather than a single merged block. ZK provides no built-in gutter (columns are bare floats whose widths are set as JS inline styles), so the theme supplies it via an adjacent-sibling `padding-left` on every column except the first; `box-sizing: border-box` keeps the JS-set inline width intact and leaves the layout's outer left/right edges flush. Margin / negative-margin cannot be used here — the inline percentage widths plus the root's `overflow: hidden` would either overflow the row or clip a compensating negative margin.

The **framed column** (`title` set on `Portalchildren`, triggering `.z-portalchildren-frame`) introduces a card-style container: `1px solid var(--zk-color-outline-variant)` border, `var(--zk-shape-card)` radius (6px), `var(--zk-color-surface)` background, and `var(--zk-spacing-3)` padding (except padding-bottom which is 0 to avoid double-spacing with the last panel). A column title displays in body-medium size (`var(--zk-typescale-body-medium-size)`) at `500` weight (theme medium, per `.z-fw-medium`) and `var(--zk-color-on-surface-variant)` color. The panel-count badge (`.z-portalchildren-counter-on`) renders as a small pill — `var(--zk-color-primary-container)` background, `var(--zk-color-primary)` text, `var(--zk-shape-corner-full)` radius — inline beside the title text, mirroring the chip pattern used across the theme.

The **drag ghost** (`.z-panel-move-ghost`) receives a semi-transparent surface overlay — `var(--zk-color-surface)` background at `var(--zk-state-dragged-opacity)` — to indicate motion without fully hiding the drop target beneath. The drop placeholder (`.z-panel-move-block`) uses `var(--zk-color-primary-container)` as a 10px-tall highlight strip so the insertion point reads as an active slot.

In **horizontal orient** (`orient="horizontal"` → `.z-portallayout-horizontal`) the layout becomes row-based: each `Portalchildren` is a full-height row that stacks vertically, and the panels inside a row float left so they sit side-by-side. ZK supplies no built-in float for this — the theme must add `float: left` to `.z-portallayout-horizontal .z-portalchildren-content > .z-panel` (and `.z-panel-move-block`), mirroring the vertical-orient column float. Row height comes from the column's JS-set inline size (the `height` attribute on `Portalchildren`), with `height: 100%` as the CSS fallback.

The **inter-panel gutter must be consistent across orients** — both axes use the same `var(--zk-spacing-3)` (12px) spacing the vertical orient uses (column `padding-left` for the side gutter, panel `margin-bottom` for stacking). Horizontal needs two rules: an **inter-row gutter** (`padding-top: var(--zk-spacing-3)` on `.z-portalchildren + .z-portalchildren`, which is transparent and box-sizing `border-box` so the gutter sits inside the JS-set inline row height — the exact vertical-axis mirror of the vertical inter-column `padding-left`), and a **side-by-side panel gutter** (`margin-left: var(--zk-spacing-3)` on `.z-panel + .z-panel`). The side gutter must be `margin` (not `padding`) because the panel's card chrome — border and background — lives on `.z-panel` itself with no transparent wrapper, so padding would open space *inside* the card instead of *between* cards. Because floated panels with widths summing to 100% plus a margin would overflow and wrap, the side-by-side panels must budget for the gutter in their authored widths (e.g. `width="calc(50% - 6px)"` for a two-up row). This is the structural counterpart of the vertical orient, where ZK already computes column widths to pixels and so leaves room for the gutter automatically; in horizontal orient ZK leaves panel widths at the authored value, so the width budget is the page author's responsibility.

**No transition** on the layout shell itself — column width changes are JS-driven and cannot meaningfully animate without layout-thrash. The panel children animate according to their own contract.

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| root-1 | `.z-portallayout` | overflow | `hidden` | structural (float containment) |
| root-2 | `.z-portallayout` | background | `transparent` | DESIGN.md §1 (shell has no surface) |
| col-1 | `.z-portallayout-vertical > .z-portalchildren` | height | `100%` | structural |
| col-2 | `.z-portallayout-vertical > .z-portalchildren` | float | `left` | structural |
| col-3 | `.z-portalchildren` | overflow | `hidden` | structural |
| col-4 | `.z-portalchildren-content` | overflow | `hidden` | structural |
| col-5 | `.z-portalchildren-content` | width | `100%` | structural |
| col-6 | `.z-portalchildren-content` | height | `100%` | structural |
| col-7 | `.z-portallayout-vertical > .z-portalchildren + .z-portalchildren` | padding-left | `var(--zk-spacing-3)` | DESIGN.md §4 (inter-column gutter) |
| col-h1 | `.z-portallayout-horizontal > .z-portalchildren` | height | `100%` | structural (row fills shell height) |
| col-h2 | `.z-portallayout-horizontal .z-portalchildren-content > .z-panel` | float | `left` | structural (row-based orient — panels sit side-by-side) |
| col-h3 | `.z-portallayout-horizontal > .z-portalchildren + .z-portalchildren` | padding-top | `var(--zk-spacing-3)` | DESIGN.md §4 (inter-row gutter — consistency with vertical inter-column gutter) |
| col-h4 | `.z-portallayout-horizontal .z-portalchildren-content > .z-panel + .z-panel` | margin-left | `var(--zk-spacing-3)` | DESIGN.md §4 (side-by-side panel gutter — consistency with vertical) |
| title-1 | `.z-portalchildren-title` | display | `none` | structural (hidden until frame variant) |
| title-2 | `.z-portalchildren-frame > .z-portalchildren-title` | display | `block` | structural |
| title-3 | `.z-portalchildren-frame > .z-portalchildren-title` | font-size | `var(--zk-typescale-body-medium-size)` | DESIGN.md §7 |
| title-4 | `.z-portalchildren-frame > .z-portalchildren-title` | font-weight | `500` (theme medium weight, per `.z-fw-medium`) | DESIGN.md §7 |
| title-5 | `.z-portalchildren-frame > .z-portalchildren-title` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §2 |
| title-6 | `.z-portalchildren-frame > .z-portalchildren-title` | padding-bottom | `var(--zk-spacing-3)` | DESIGN.md §4 |
| frame-1 | `.z-portalchildren-frame` | border | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §11 (outlined card) |
| frame-2 | `.z-portalchildren-frame` | border-radius | `var(--zk-shape-card)` | DESIGN.md §5 |
| frame-3 | `.z-portalchildren-frame` | background | `var(--zk-color-surface)` | DESIGN.md §1 |
| frame-4 | `.z-portalchildren-frame` | padding | `var(--zk-spacing-3)` | DESIGN.md §4 |
| frame-5 | `.z-portalchildren-frame` | padding-bottom | `0` | structural (last-panel spacing provided by panel margin) |
| counter-1 | `.z-portalchildren-counter` | display | `none` | structural (default hidden) |
| counter-2 | `.z-portalchildren-counter-on` | display | `inline` | structural |
| counter-3 | `.z-portalchildren-counter-on` | background | `var(--zk-color-primary-container)` | DESIGN.md §3 |
| counter-4 | `.z-portalchildren-counter-on` | color | `var(--zk-color-primary)` | DESIGN.md §3 |
| counter-5 | `.z-portalchildren-counter-on` | border-radius | `var(--zk-shape-corner-full)` | DESIGN.md §5 (pill badge) |
| counter-6 | `.z-portalchildren-counter-on` | font-size | `var(--zk-typescale-body-small-size)` | DESIGN.md §7 |
| counter-7 | `.z-portalchildren-counter-on` | font-weight | `600` (theme semibold weight, per `.z-fw-semibold`) | DESIGN.md §7 (badge weight) |
| ghost-1 | `.z-panel-move-ghost` | background | `var(--zk-color-surface)` | DESIGN.md §1 |
| ghost-2 | `.z-panel-move-ghost` | opacity | `var(--zk-state-dragged-opacity)` | DESIGN.md §8 (dragged state layer) |
| ghost-3 | `.z-panel-move-ghost` | border | `1px solid var(--zk-color-outline)` | DESIGN.md §11 |
| ghost-4 | `.z-panel-move-ghost` | border-radius | `var(--zk-shape-card)` | DESIGN.md §5 |
| block-1 | `.z-panel-move-block` | background | `var(--zk-color-primary-container)` | DESIGN.md §3 (active drop zone signal) |
| block-2 | `.z-panel-move-block` | border-radius | `var(--zk-shape-corner-extra-small)` | DESIGN.md §5 |
| drag-1 | `.z-panel-header-move` | cursor | `move` | interaction contract |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default shell | `.z-portallayout` | root-1, root-2 |
| vertical column | `.z-portallayout-vertical > .z-portalchildren` | col-1, col-2, col-3 |
| inter-column gutter | `.z-portallayout-vertical > .z-portalchildren + .z-portalchildren` | col-7 |
| horizontal orient | `.z-portallayout-horizontal > .z-portalchildren` / `… .z-portalchildren-content > .z-panel` | col-h1, col-h2 |
| horizontal gutters | `.z-portallayout-horizontal > .z-portalchildren + .z-portalchildren` / `… .z-portalchildren-content > .z-panel + .z-panel` | col-h3, col-h4 |
| plain column content | `.z-portalchildren-content` | col-4, col-5, col-6 |
| column title hidden | `.z-portalchildren-title` | title-1 |
| framed column | `.z-portalchildren-frame` | frame-1, frame-2, frame-3, frame-4, frame-5 |
| framed title visible | `.z-portalchildren-frame > .z-portalchildren-title` | title-2, title-3, title-4, title-5, title-6 |
| counter hidden | `.z-portalchildren-counter` | counter-1 |
| counter visible | `.z-portalchildren-counter-on` | counter-2, counter-3, counter-4, counter-5, counter-6, counter-7 |
| drag ghost | `.z-panel-move-ghost` | ghost-1, ghost-2, ghost-3, ghost-4 |
| drop placeholder | `.z-panel-move-block` | block-1, block-2 |
| draggable header | `.z-panel-header-move` | drag-1 |

## States to evaluate
- [ ] default (plain portallayout shell — transparent, no chrome)
- [ ] vertical-orient (`.z-portallayout-vertical` — default)
- [x] horizontal-orient (`.z-portallayout-horizontal` — rows stack; panels float left, verified col-h1/col-h2)
- [ ] plain-column (no title — transparent column wrapper)
- [ ] framed-column (title set — `.z-portalchildren-frame` chrome)
- [ ] counter-visible (`.z-portalchildren-counter-on`)
- [ ] counter-hidden (counterVisible="false")
- [ ] drag-active (ghost + placeholder visible)
