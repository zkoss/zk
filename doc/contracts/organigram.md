# Component: organigram (theme design)
tier: T2
category: data
preview: ${PREVIEW_URL}/organigram.zul
rules: see .claude/skills/zk-component-rules/components/organigram.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/Organigram.ts
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/Orgchildren.ts
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/Orgitem.ts
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/Orgnode.ts
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/mold/organigram.js
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/mold/orgchildren.js
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/mold/orgitem.js
  - zkcml/zkmax/src/main/resources/web/js/zkmax/layout/mold/orgnode.js
js-source-hash: 3ebf24c3de417b55b1bb30c7f9fbf573a04a1ea06a62d3390edb4923339b38a2
closest-sibling: none — novel T2 (no ZK component renders centred branching trees; partial idiom borrows from panel for card chrome and from tree for the plus/minus glyph)

## References
- MUI CSS: no analog — Material-UI 7 ships no org-chart component
- DESIGN.md sections: §1 (surfaces), §2 (text colors), §5 (corner radii — `--zk-shape-card`), §6 (elevation — outlined card on white surface), §7 (typography — body 13/14 medium-weight), §8 (state-layer overlays), §9 (motion — 250ms standard), §11 (border rules — outline-variant dividers, primary focus ring)
- Iceblue baseline: `doc/contracts/baselines/organigram-iceblue.png`
- HTML contract: `doc/contracts/organigram.html`

## Design Contract

An org chart is fundamentally a *map of belonging* — every node says "I report to this one, these report to me". In MD3 vocabulary that demands two readable layers: the **node** as a discrete, named atom and the **connector** as a quiet, neutral relation. We render the orgnode as an **outlined card** (1px outline-variant border, surface fill, card-radius 6px, no resting shadow) rather than a filled tile, because a filled tile competes with the connector linework and an elevated card detaches the node from its connectors visually. Padding is medium-tight (`--zk-spacing-2` block, `--zk-spacing-3` inline) — denser than a content card because an org chart's value is in seeing many nodes at once, not in each node's individual weight. Typography is body-medium regular on `--zk-color-on-surface`; the label is the content, no headline emphasis needed. The **expand/collapse icon** sits flush in the node's bottom-right corner (the legacy iceblue placement, which preserves a clean text-leading edge and lets the icon read as a control, not a decoration); when the icon is the click target it picks up an `on-surface-variant` color to read as quieter than the label. **Connectors** are a single CSS pixel of `--zk-color-outline-variant` — the same neutral grey ZK uses for table dividers — drawn as absolutely-positioned pseudo-element segments (horizontal bus along each generation's top edge, vertical drops between parent and child). Lines remain neutral even when nodes are selected; a *selected node* (filled `--zk-color-primary-container` with primary border) is enough indication of the active path without lighting up the connector chain (a "lit path" idiom belongs to wizards and stepbars, not org charts where the relationship structure is itself the message). **Hover** raises a state-layer overlay (`currentColor` at `--zk-state-hover-opacity`) on the orgnode and shifts its border to `--zk-color-on-surface` (the same hover affordance our inputs use). **Focus-visible** draws a 2px `--zk-color-primary` ring inside the card border to keep keyboard navigation legible against the small node size. **Disabled** dims the entire orgnode at `--zk-state-disabled-opacity` and removes the hover affordance; **non-selectable** changes only the cursor (no visual demotion — it's a click-routing decision, not a relevance one). **Motion** runs at 250ms standard easing on background, border, and box-shadow; the open/close subtree change is instantaneous because the connector geometry depends on layout, and animating layout would distort the lines.

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| root-1 | `.z-organigram` | display | `block` | structural |
| root-2 | `.z-organigram` | overflow | `auto` | DESIGN.md §10 (charts often exceed container width) |
| root-3 | `.z-organigram` | font-family | `var(--zk-typescale-font-family)` | DESIGN.md §7 |
| root-4 | `.z-organigram` | padding | `var(--zk-spacing-2)` | DESIGN.md §4 (small breathing room around the chart) |
| gen-1 | `.z-orgchildren` | display | `flex` | structural (horizontal generation row) |
| gen-2 | `.z-orgchildren` | flex-direction | `row` | layout |
| gen-3 | `.z-orgchildren` | align-items | `flex-start` | layout (siblings hang from the top of the generation, even-height) |
| item-1 | `.z-orgitem` | flex | `auto` | layout (siblings share row width by default) |
| item-2 | `.z-orgitem` | display | `flex` | structural |
| item-3 | `.z-orgitem` | flex-direction | `column` | layout (node on top, nested generation below) |
| item-4 | `.z-orgitem` | position | `relative` | structural (anchors `::before` bus segment) |
| item-5 | `.z-orgitem` | padding-top | `var(--zk-spacing-3)` | DESIGN.md §4 (room for the bus segment + vertical drop above the node) |
| bus-1 | `.z-orgchildren:not(:only-child) > .z-orgitem::before` | content | `''` | structural |
| bus-2 | `.z-orgchildren:not(:only-child) > .z-orgitem::before` | position | `absolute` | structural |
| bus-3 | `.z-orgchildren:not(:only-child) > .z-orgitem::before` | top | `0` | layout |
| bus-4 | `.z-orgchildren:not(:only-child) > .z-orgitem::before` | left | `0` | layout |
| bus-5 | `.z-orgchildren:not(:only-child) > .z-orgitem::before` | width | `100%` | layout (full-width default; first/last children override) |
| bus-6 | `.z-orgchildren:not(:only-child) > .z-orgitem::before` | height | `1px` | DESIGN.md §11 (subtle row separator weight) |
| bus-7 | `.z-orgchildren:not(:only-child) > .z-orgitem::before` | border-top | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §11 (outline-variant for dividers) |
| bus-8 | `.z-orgchildren:not(:only-child) > .z-orgitem:first-child::before` | width | `50%` | layout (kill bus left of leftmost child) |
| bus-9 | `.z-orgchildren:not(:only-child) > .z-orgitem:first-child::before` | left | `50%` | layout |
| bus-10 | `.z-orgchildren:not(:only-child) > .z-orgitem:last-child::before` | width | `50%` | layout (kill bus right of rightmost child) |
| drop-down-1 | `.z-orgchildren:not(:only-child) > .z-orgitem::after` | content | `''` | structural (vertical drop from parent into this child's top) |
| drop-down-2 | `.z-orgchildren:not(:only-child) > .z-orgitem::after` | position | `absolute` | structural |
| drop-down-3 | `.z-orgchildren:not(:only-child) > .z-orgitem::after` | top | `0` | layout |
| drop-down-4 | `.z-orgchildren:not(:only-child) > .z-orgitem::after` | left | `50%` | layout |
| drop-down-5 | `.z-orgchildren:not(:only-child) > .z-orgitem::after` | height | `var(--zk-spacing-3)` | DESIGN.md §4 (drop length matches item padding-top) |
| drop-down-6 | `.z-orgchildren:not(:only-child) > .z-orgitem::after` | border-left | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §11 |
| drop-out-1 | `.z-orgnode::after` | content | `''` | structural (vertical drop from this node into its own nested generation) |
| drop-out-2 | `.z-orgnode::after` | position | `absolute` | structural |
| drop-out-3 | `.z-orgnode::after` | bottom | `calc(-1 * var(--zk-spacing-3))` | layout (extends below the node by item padding-top of the next generation) |
| drop-out-4 | `.z-orgnode::after` | left | `50%` | layout |
| drop-out-5 | `.z-orgnode::after` | height | `var(--zk-spacing-3)` | DESIGN.md §4 |
| drop-out-6 | `.z-orgnode::after` | border-left | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §11 |
| node-1 | `.z-orgnode` | align-self | `center` | structural (centre node above its branch) |
| node-2 | `.z-orgnode` | position | `relative` | structural |
| node-3 | `.z-orgnode` | display | `inline-flex` | layout |
| node-4 | `.z-orgnode` | align-items | `center` | layout |
| node-5 | `.z-orgnode` | gap | `var(--zk-spacing-2)` | DESIGN.md §4 (label ↔ icon spacing) |
| node-6 | `.z-orgnode` | min-width | `64px` | DESIGN.md §10 (smallest legible org card; mirrors iceblue) |
| node-7 | `.z-orgnode` | padding | `var(--zk-spacing-2) var(--zk-spacing-3)` | DESIGN.md §10 (denser than content card) |
| node-8 | `.z-orgnode` | margin | `0 var(--zk-spacing-2)` | DESIGN.md §4 (horizontal breathing room between sibling cards) |
| node-9 | `.z-orgnode` | background-color | `var(--zk-color-surface)` | DESIGN.md §1 |
| node-10 | `.z-orgnode` | border | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §11 (outlined card variant) |
| node-11 | `.z-orgnode` | border-radius | `var(--zk-shape-card)` | DESIGN.md §5 (6px card radius) |
| node-12 | `.z-orgnode` | color | `var(--zk-color-on-surface)` | DESIGN.md §2 |
| node-13 | `.z-orgnode` | font-size | `var(--zk-typescale-body-medium-size)` | DESIGN.md §7 |
| node-14 | `.z-orgnode` | line-height | `var(--zk-typescale-body-medium-line-height)` | DESIGN.md §7 |
| node-15 | `.z-orgnode` | cursor | `pointer` | DESIGN.md §9 (entire card is the selection target) |
| node-16 | `.z-orgnode` | box-shadow | `none` | DESIGN.md §6 (outlined card has no resting shadow) |
| node-17 | `.z-orgnode` | transition | `background-color var(--zk-motion-duration-short3) var(--zk-motion-easing-legacy), border-color var(--zk-motion-duration-short3) var(--zk-motion-easing-legacy), box-shadow var(--zk-motion-duration-short3) var(--zk-motion-easing-legacy)` | DESIGN.md §9 |
| hover-1 | `.z-orgitem:not(.z-orgitem-disabled):not(.z-orgitem-selected) > .z-orgnode:hover` | background-color | `rgba(0, 0, 0, 0.04)` | DESIGN.md §8 (solid surface hover for cards) |
| hover-2 | `.z-orgitem:not(.z-orgitem-disabled):not(.z-orgitem-selected) > .z-orgnode:hover` | border-color | `var(--zk-color-on-surface)` | DESIGN.md §11 (input/card hover border) |
| focus-1 | `.z-orgnode:focus-visible` | outline | `2px solid var(--zk-color-primary)` | DESIGN.md §11 (focus ring) |
| focus-2 | `.z-orgnode:focus-visible` | outline-offset | `-2px` | layout (ring inside the card border) |
| selected-1 | `.z-orgitem-selected > .z-orgnode` | background-color | `var(--zk-color-primary-container)` | DESIGN.md §3 |
| selected-2 | `.z-orgitem-selected > .z-orgnode` | border-color | `var(--zk-color-primary)` | DESIGN.md §3 |
| selected-3 | `.z-orgitem-selected > .z-orgnode` | color | `var(--zk-color-on-primary-container)` | DESIGN.md §3 |
| disabled-1 | `.z-orgitem-disabled > .z-orgnode` | opacity | `var(--zk-state-disabled-opacity)` | DESIGN.md §8 |
| disabled-2 | `.z-orgitem-disabled > .z-orgnode` | cursor | `default` | DESIGN.md §9 |
| disabled-3 | `.z-orgitem-disabled > .z-orgnode` | background-color | `var(--zk-color-surface)` | DESIGN.md §1 (no hover layer when disabled) |
| nonsel-1 | `.z-orgitem-non-selectable > .z-orgnode` | cursor | `default` | DESIGN.md §9 (clickable region is inert; visual chrome unchanged) |
| close-1 | `.z-orgitem-close > .z-orgchildren` | display | `none` | structural (collapse subtree) |
| close-2 | `.z-orgitem-close > .z-orgnode::after` | display | `none` | structural (no drop into hidden generation) |
| icon-1 | `.z-orgnode-icon` | position | `absolute` | structural (corner-pinned) |
| icon-2 | `.z-orgnode-icon` | right | `var(--zk-spacing-1)` | DESIGN.md §4 |
| icon-3 | `.z-orgnode-icon` | bottom | `var(--zk-spacing-1)` | DESIGN.md §4 |
| icon-4 | `.z-orgnode-icon` | width | `12px` | DESIGN.md §12 (small inline icon) |
| icon-5 | `.z-orgnode-icon` | height | `12px` | DESIGN.md §12 |
| icon-6 | `.z-orgnode-icon` | font-size | `12px` | DESIGN.md §12 |
| icon-7 | `.z-orgnode-icon` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §2 (muted control) |
| icon-8 | `.z-orgnode-icon` | cursor | `pointer` | DESIGN.md §9 |
| icon-9 | `.z-orgnode-icon` | display | `inline-flex` | layout |
| icon-10 | `.z-orgnode-icon` | align-items | `center` | layout |
| icon-11 | `.z-orgnode-icon` | justify-content | `center` | layout |
| icon-12 | `.z-orgnode > i.z-orgnode-icon:not(.z-icon-plus):not(.z-icon-minus)` | display | `none` | structural (hide the icon node on leaf items where no glyph class is present) |
| icon-13 | `.z-orgitem-selected > .z-orgnode > .z-orgnode-icon` | color | `var(--zk-color-on-primary-container)` | DESIGN.md §2 (icon picks up selected text color) |

## State matrix

| state | selector | property ids |
|-------|----------|--------------|
| organigram-root | `.z-organigram` | root-1, root-2, root-3, root-4 |
| generation-row | `.z-orgchildren` | gen-1, gen-2, gen-3 |
| item-frame (any) | `.z-orgitem` | item-1, item-2, item-3, item-4, item-5 |
| bus (default) | `.z-orgchildren:not(:only-child) > .z-orgitem::before` | bus-1, bus-2, bus-3, bus-4, bus-5, bus-6, bus-7 |
| bus (first child of multi-child generation) | `.z-orgchildren:not(:only-child) > .z-orgitem:first-child::before` | bus-8, bus-9 |
| bus (last child of multi-child generation) | `.z-orgchildren:not(:only-child) > .z-orgitem:last-child::before` | bus-10 |
| drop into child | `.z-orgchildren:not(:only-child) > .z-orgitem::after` | drop-down-1..6 |
| drop out of node | `.z-orgnode::after` | drop-out-1..6 |
| node default | `.z-orgnode` | node-1 … node-17 |
| node hover (eligible) | `.z-orgitem:not(.z-orgitem-disabled):not(.z-orgitem-selected) > .z-orgnode:hover` | hover-1, hover-2 |
| node focus-visible | `.z-orgnode:focus-visible` | focus-1, focus-2 |
| selected | `.z-orgitem-selected > .z-orgnode` | selected-1, selected-2, selected-3 |
| disabled | `.z-orgitem-disabled > .z-orgnode` | disabled-1, disabled-2, disabled-3 |
| non-selectable | `.z-orgitem-non-selectable > .z-orgnode` | nonsel-1 |
| closed branch | `.z-orgitem-close > .z-orgchildren`, `.z-orgitem-close > .z-orgnode::after` | close-1, close-2 |
| icon base | `.z-orgnode-icon` | icon-1 … icon-11 |
| icon (leaf — no glyph class) | `.z-orgnode > i.z-orgnode-icon:not(.z-icon-plus):not(.z-icon-minus)` | icon-12 |
| icon (selected item) | `.z-orgitem-selected > .z-orgnode > .z-orgnode-icon` | icon-13 |

## States to evaluate
- [ ] default (root item with two children — verify bus, drops, node card chrome)
- [ ] hover on a selectable, non-selected, non-disabled orgnode
- [ ] focus-visible (tab to an orgnode; primary ring inside card border)
- [ ] selected (orgitem with `.z-orgitem-selected` — primary-container fill, primary border, icon picks up on-primary-container)
- [ ] disabled (orgitem with `.z-orgitem-disabled` — node dims, no hover affordance, cursor default)
- [ ] non-selectable (orgitem with `.z-orgitem-non-selectable` — cursor default, no visual demotion)
- [ ] closed (orgitem with `.z-orgitem-close` — subtree hidden, outgoing drop hidden, icon shows `.z-icon-plus`)
- [ ] open (default — icon shows `.z-icon-minus`)
- [ ] leaf node (no `<orgchildren>` sibling — `.z-orgnode-icon` element exists but has no glyph class and is hidden)
- [ ] single-child generation (`.z-orgchildren` with one `.z-orgitem` — bus suppressed by `:not(:only-child)` guard)
- [ ] first-child of multi-child generation (bus is half-width, aligned to right half)
- [ ] last-child of multi-child generation (bus is half-width, aligned to left half)
- [ ] selected + disabled simultaneously (selected wins for chrome; disabled-opacity still applies — verify both classes coexist sanely)
