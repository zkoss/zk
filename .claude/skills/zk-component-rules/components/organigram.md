# organigram

A container that renders a hierarchical organizational chart as a centred tree of node boxes connected by 90-degree lines (one horizontal bus across each generation, one vertical drop into each child). Built from four cooperating widgets — `<organigram>` (root container), `<orgchildren>` (a generation row), `<orgitem>` (a single branch — wraps its own `<orgnode>` plus an optional nested `<orgchildren>`), and `<orgnode>` (the visible node card with label, optional image, and an expand/collapse icon). Recursive — every `<orgitem>` may contain another `<orgchildren>` that contains more `<orgitem>`s, ad infinitum.

ZK-EE only (ships in `zkmax.jar`, package `zkmax.layout`).

## DOM structure

```
.z-organigram                            (<div role="tree" aria-orientation="horizontal">, root)
└─ .z-orgchildren                        (<div>, one generation; root carries no extra class for "topmost")
   └─ .z-orgitem                         (<div role="treeitem">, one branch)
      ├─ .z-orgnode                      (<div>, the visible node card — flex item containing label, image, icon)
      │  ├─ <img> / text                 (provided by LabelImageWidget base — label text + optional image)
      │  ├─ .z-orgnode-icon              (<i id="{uuid}-icon" aria-hidden="true">, expand/collapse glyph; z-icon-plus or z-icon-minus or neither on leaves)
      │  └─ [custom children]            (any child widgets of orgnode rendered after the icon, in source order)
      └─ .z-orgchildren                  (<div>, nested generation; present only when this item has children)
         └─ .z-orgitem                   (recursive)
            └─ …
```

Notes on the recursion:

- The root `<organigram>` has exactly one direct `.z-orgchildren` child (the topmost generation). `isTopmost()` on an `Orgchildren` returns true when its parent is `.z-organigram` rather than another `.z-orgitem`; the topmost is the only one not gated by an ancestor `.z-orgitem-close`.
- Every `<orgitem>` always renders its own `<orgnode>` first child. A second child (`<orgchildren>`) appears only when the item actually has descendants.
- The `<orgnode>` always emits the `<i class="z-orgnode-icon" aria-hidden="true">` element, but the icon glyph class (`.z-icon-plus` / `.z-icon-minus`) is added only when `this.nextSibling` is an `Orgchildren` widget (i.e. the item has descendants). Leaf nodes therefore have a `.z-orgnode-icon` element with no glyph class. Because `aria-hidden="true"` is hardcoded by the mold, the icon is always hidden from screen readers regardless of glyph state; do not attempt to expose it via CSS `content` tricks or ARIA overrides.
- The orgnode mold renders `domContent_()` (label + image from `LabelImageWidget`) then the icon `<i>`, then any custom widget children in child order. CSS selectors that assume the icon is the last child of orgnode (e.g. `.z-orgnode > i:last-child`) may break if the author places custom children inside orgnode; use `.z-orgnode > .z-orgnode-icon` instead.

## State classes

State classes are JS-toggled on the **`.z-orgitem`** root by setters in `Orgitem.ts`, and on the `.z-orgnode-icon` by `Orgnode._adjustIcon()`:

- `.z-orgitem-selected` — added by `setSelected(true)`. The organigram allows only one selected item at a time; setting a new selection clears the previous one (`Organigram.setSelectedItem` deselects the old before selecting the new).
- `.z-orgitem-disabled` — added by `setDisabled(true)`. While present, the item ignores click selection.
- `.z-orgitem-non-selectable` — added by `setSelectable(false)`. While present, the item ignores click selection but is *not* visually treated as disabled (the CSS rule for it changes the cursor only).
- `.z-orgitem-close` — added by `setOpen(false)`. While present, the item's direct `.z-orgchildren` child is collapsed (hidden) and the descendant subtree is not drawn.

Icon glyph classes on `.z-orgnode-icon` (mutually exclusive, toggled by `Orgnode._adjustIcon` and `Orgitem.setOpen`):

- `.z-icon-plus` — when the item has children and is closed.
- `.z-icon-minus` — when the item has children and is open.
- (neither) — when the item is a leaf (no `<orgchildren>` sibling alongside the orgnode).

States rely on the JS-added classes above; ZK does **not** add a `.z-orgitem-hover` class. Hover/focus styling must come from `:hover` / `:focus-visible` pseudo-classes on `.z-orgnode`.

## Attribute support

| Attribute | Widget | Effect on DOM / classes |
|-----------|--------|-------------------------|
| `model` | organigram | Drives data-binding; does not alter DOM structure (the same `<orgchildren>` / `<orgitem>` / `<orgnode>` tree is rendered from the TreeModel). Sets the `_model` flag, which gates whether `_toggleOpen` calls `setOpen` locally or waits for a server roundtrip. |
| `selectedItem` | organigram | Toggles `.z-orgitem-selected` on the target `<orgitem>` and removes it from the previously selected item. Only one item carries the class at any time. |
| `open` | orgitem | Toggles `.z-orgitem-close` (note: `close`, not `open`-prefixed) and swaps the `<orgnode>`'s icon between `.z-icon-plus` and `.z-icon-minus`. |
| `selected` | orgitem | Toggles `.z-orgitem-selected`. |
| `disabled` | orgitem | Toggles `.z-orgitem-disabled`. |
| `selectable` | orgitem | Toggles `.z-orgitem-non-selectable` (note: presence of the class means *not* selectable; absence is the default selectable state). |
| `label`, `image`, `iconSclass` | orgnode | Inherited from `LabelImageWidget` — render text / image / icon prefix inside the node body. They do not add state classes; they only add content. |
| `width`, `hflex` | orgitem | `Orgitem._adjustSize()` writes inline `flex` (and clears `width`) on the rendered `<div>`; affects how horizontal space is shared inside an `<orgchildren>` generation row. |

There is no `readonly`, no `inplace`, no `buttonVisible`. No mold variants — `default` mold only.

## Composition invariants

- **Generation rows are horizontal flex containers.** Each `.z-orgchildren` lays its `.z-orgitem` children out horizontally; the `Orgitem` `_adjustSize()` writes inline `flex` so siblings share the row width unless `width` or `hflex` is set.
- **Node cards self-centre inside their branch.** Each `.z-orgitem` is itself a vertical flex column (orgnode on top, optional nested orgchildren below); the orgnode uses `align-self: center` so its visual centre aligns with the connector drops above and below.
- **Connectors are absolutely-positioned pseudo-elements, not real elements.** Three different pseudo-elements compose the 90-degree branching:
  - `.z-orgitem::before` draws the horizontal bus segment along the top edge of the branch (full width by default; half-width on `:first-child` aligned to the right half, half-width on `:last-child` aligned to the left half — so an only-child has no bus, a two-child generation has two half-segments meeting in the middle).
  - `.z-orgchildren:not(:only-child) > .z-orgitem::after` draws the vertical drop from the parent orgnode's bottom edge down to the child's top edge (the bus on the child's top).
  - `.z-orgnode:not(:only-child)::after` draws the short vertical drop-out from the bottom of a **parent** orgnode down to the horizontal bus of its nested generation. A parent orgnode always has an `.z-orgchildren` sibling inside its orgitem, so `:not(:only-child)` selects exactly the nodes that have children; a leaf orgnode is its orgitem's only child, so it correctly draws nothing. The drop-out needs somewhere to live: the theme must open a vertical gap between the parent node's bottom and the bus (e.g. `margin-top` on `.z-orgchildren`, mirroring the ZK default theme's `margin` on `.z-orgnode`), then position the drop-out (`top: 100%; height: <gap>`) to span it. **Do not** suppress this drop-out for multi-child generations — the bus is offset below the node by that gap, not flush against it, so without the drop-out a parent node hangs disconnected above its bus.
  These pseudo-elements rely on `.z-orgitem` and `.z-orgnode` being `position: relative`. Their `top`/`bottom`/`left: 50%` offsets MUST be preserved by any theme; a theme that removes `position: relative` from `.z-orgitem` or `.z-orgnode` will break the entire connector layout.
- **`:first-child` and `:last-child` of a multi-child `<orgchildren>` get half-width buses.** The default `width: 100%` bus is overridden by `&:first-child::before { width: 50%; left: 50%; }` and `&:last-child::before { width: 50%; }`. If exactly one item exists in the generation, the `:not(:only-child)` guard suppresses the bus entirely (a single drop from the parent meets a single child, no horizontal bus needed).
- **Closed branches hide their child generation AND their own outgoing drop.** `.z-orgitem-close > .z-orgchildren { display: none; }` collapses the subtree; `.z-orgitem-close > .z-orgnode::after { display: none; }` hides the drop emerging from the now-closed node, so the closed node doesn't appear to dangle a line into empty space.
- **Click target distinguishes icon-vs-body.** `Orgnode.doSelect_` checks `event.domTarget == this.$n('icon')`: a click on `.z-orgnode-icon` toggles open/close; a click anywhere else on the orgnode selects the parent orgitem. Both targets live inside the same `.z-orgnode`; CSS hover state should therefore apply to the whole `.z-orgnode`, not just its text region.
- **Selection is suppressed on disabled / non-selectable / already-selected items.** `Orgnode._canSelect()` returns false for any of these. The CSS may show a hover affordance on `.z-orgitem-disabled .z-orgnode`, but doing so would mislead users; suppress it.
- **`role="tree"` on the root and `role="treeitem"` on each orgitem are written by the molds.** Themes must not rely on `aria-expanded` — ZK does not emit it. The open/close marker is the icon glyph class on `.z-orgnode-icon`.

## Relational invariants

Theme-agnostic geometric/quantitative predicates. Verify by measurement with the stated tolerance; the absolute pixel value is theme choice but the relation must hold.

- **Generation siblings vertically aligned.** Within a single `.z-orgchildren`, all direct `.z-orgitem > .z-orgnode` elements share the same top edge: `|max(node.top) − min(node.top)| ≤ 2px`. (Enforced by `.z-orgchildren { display: flex }` + `align-items` defaulting to stretch/flex-start; a theme that introduces per-item vertical offsets breaks the chart's row reading.)
- **Parent node horizontally centered above its children's row.** For any `.z-orgitem` containing a non-empty `.z-orgchildren`, the parent `.z-orgnode.centerX` aligns with the midpoint of its first and last child `.z-orgnode.centerX`: `|parent.centerX − (firstChild.centerX + lastChild.centerX) / 2| ≤ 2px`. (Anchored by `.z-orgnode { align-self: center }` plus the flex column on `.z-orgitem`.)
- **Connectors are visible line segments with non-zero length.** Each of `.z-orgitem::before` (bus), `.z-orgchildren:not(:only-child) > .z-orgitem::after` (drop-down), and `.z-orgnode:not(:only-child)::after` (drop-out) — when present — must render with a non-transparent border/background color and a measurable extent ≥ 4px along its primary axis. A theme that sets the connector color equal to its container background (or removes the border declaration) makes the chart structurally unreadable.
- **Bus geometry for edge siblings.** For a multi-child `.z-orgchildren`, the `:first-child` bus must occupy only the right half of the orgitem (left edge ≥ 50% of orgitem width); the `:last-child` bus must occupy only the left half (right edge ≤ 50%). Intermediate siblings' bus spans the full orgitem width. These are width-relative invariants, not pixel counts.
- **Drops are horizontally centered on the node.** Both `.z-orgchildren:not(:only-child) > .z-orgitem::after` and `.z-orgnode:not(:only-child)::after` originate at `left: 50%` of their containing block: `|drop.centerX − container.centerX| ≤ 1px`.
- **Node card has a visible boundary against the chart canvas (disjunction).** Each `.z-orgnode` must satisfy at least one of:
  - `border-width ≥ 1px` AND computed `border-color` has α > 0, OR
  - computed `background-color` is visually distinct from the `.z-organigram` background (ΔE ≥ small perceptual threshold; in practice, any non-equal RGB), OR
  - `box-shadow` is not `none`.
  
  This lets themes use any combination of border, fill, or elevation to delineate nodes, but at least one must read.
- **Label text not clipped.** For each `.z-orgnode`, `label.scrollWidth ≤ node.clientWidth − (padding-left + padding-right)`. Truncation via `text-overflow: ellipsis` is acceptable only if the org chart container provides a tooltip or detail-on-hover affordance — bare truncation hides information.
- **Default label contrast meets WCAG AA.** In the resting (non-selected, non-disabled) state, computed `.z-orgnode { color }` against computed `.z-orgnode { background-color }` has contrast ratio ≥ 4.5:1. Disabled state is exempt (intentional dimming).
- **Icon does not occlude label.** When `.z-orgnode-icon` is visible (item has children, glyph class is `.z-icon-plus` or `.z-icon-minus`), the icon's bounding rect does not overlap the label's bounding rect within the node.

## State-differs invariants

Theme-agnostic predicates that two states must be visually distinguishable. Verify by rendering both states and comparing computed styles on the specified selector; assert that **at least one** of the listed properties differs. The disjunction is what gives themes design freedom while keeping the state machine readable.

- **`selected` vs unselected.** On `.z-orgitem-selected > .z-orgnode` compared to the same node without `.z-orgitem-selected`, at least one of: `background-color`, `border-color` (any side), `color`, `outline-color` (with non-zero outline-width), `box-shadow` — differs.
- **`disabled` vs enabled.** On `.z-orgitem-disabled > .z-orgnode` compared to the same node without `.z-orgitem-disabled`:
  - Visual: at least one of `opacity` (< 1), `color` contrast (lower against `background-color`), or `background-color` (differs) — must be true; AND
  - Cursor: computed `cursor` must equal `default` (not `pointer`).
- **`hover` vs resting** on a non-disabled `.z-orgnode`. At least one of `background-color`, `border-color`, `box-shadow`, `color` differs between `.z-orgnode:hover` and `.z-orgnode` (resting). Themes may optionally suppress hover on additional states (selected, non-selectable) but the eligible-item case must always show hover affordance.
- **`focus-visible` vs resting.** On `.z-orgnode:focus-visible`, at least one of: `outline` (width/style/color), `box-shadow`, `border-color` — differs from the resting state in a way perceivable to keyboard users (e.g. an outline of ≥ 2px or an equivalent shadow ring).
- **`closed` vs open.** For an `.z-orgitem` with descendants, when `.z-orgitem-close` is present:
  - direct `.z-orgchildren` child: `display: none`
  - own `.z-orgnode::after` (outgoing drop): `display: none` (avoids the closed node dangling a line into empty space)
  - `.z-orgnode-icon` glyph class swaps from `.z-icon-minus` (open) to `.z-icon-plus` (closed); the rendered icon character or background-image must differ — themes that map plus and minus to the same glyph break the readability of the open/close affordance.
- **`hover` suppression on `disabled`.** Computed style of `.z-orgitem-disabled > .z-orgnode:hover` equals that of `.z-orgitem-disabled > .z-orgnode` (resting) for `background-color`, `border-color`, `box-shadow`. Disabled items must not show hover-induced visual change. Themes must guard the hover selector so it does not match `.z-orgitem-disabled` items (e.g. via `:not(.z-orgitem-disabled)` on the containing orgitem).
- **Non-selectable's interaction-suppression cursor.** `.z-orgitem-non-selectable > .z-orgnode` has computed `cursor: default`. (Visual chrome may differ from a selectable node or may not — theme choice — but the cursor is a hard contract.)

## Sibling decomposition

organigram has no ZK component sibling with a comparable layout primitive (centred branching tree with pseudo-element connectors). It does share two narrow conventions with other components:

- **State-class-on-root, descendant-selects-content** — same pattern as `rating`, `stepbar`, `checkbox`. The state class lives on `.z-orgitem` (or on `.z-orgnode-icon` for the glyph); the visible chrome (`.z-orgnode`) is styled via descendant selectors like `.z-orgitem-selected > .z-orgnode`. Theme authors must NOT try to push state classes down onto `.z-orgnode` itself.
- **Open/close marker uses the shared `z-icon-plus` / `z-icon-minus` glyphs** — same convention as `tree`'s open/close affordance and `groupbox`'s collapse trigger. The glyphs come from the global icon font; the orgnode's icon node is just an `<i>` with the right class toggled by JS.

No bundling overlap with another component — organigram is the sole occupant of its `.css.dsp` (see Bundle below).

## Bundle

`organigram.css.dsp` — declared in zkmax's `lang-addon.xml` as the `css-uri` for the `organigram` widget's `default` mold. The companion widgets (`orgchildren`, `orgitem`, `orgnode`) have no `css-uri` of their own; every rule for the four-widget set rides in `organigram.css.dsp`.

Source layout convention: a single CSS file under `../zkcml/zkmax/src/main/resources/web/js/zkmax/layout/css/organigram.css` covers the four widgets.

## Edition

EE (zkmax). Loading the widget requires a valid ZK EE license; absent that, the `<organigram>` tag is not registered and falls back to a no-op.

## Notes

- The class is `.z-orgitem-close` (the *closed* state), not `.z-orgitem-open` (the *open* state). Open is the default — the absence of `-close` is what marks an item as open. Selectors that look for `.z-orgitem-open` will never match.
- The class is `.z-orgitem-non-selectable`, not `.z-orgitem-unselectable` or `.z-orgitem-not-selectable`. Hyphenated, matches the `setSelectable(false)` semantics.
- The expand/collapse `<i>` element is always emitted by the orgnode mold, even on leaf nodes — only its glyph class is conditional. CSS that selects `.z-orgnode > i` will match leaves too; either guard with `:has(+ .z-orgchildren)` on the parent orgitem, or simply rely on the absent glyph class to render nothing.
- The `<i class="z-orgnode-icon">` has `aria-hidden="true"` hardcoded by the mold. It is permanently invisible to screen readers; themes must not try to expose icon state via ARIA. Screen reader state announcement relies on the `role="tree"` / `role="treeitem"` hierarchy, not the icon.
- `_adjustSize()` clears any inline `width` and re-writes inline `flex` whenever `width` or `hflex` changes — themes that set `width` via CSS rules on `.z-orgitem` will be ignored by the JS-driven sibling distribution unless the CSS rule wins specificity over the inline style (it normally won't).
- The root `<organigram>`'s scrollability is controlled by the theme; ZK does not set `overflow` on it. A theme that displays an organigram larger than its container must add `overflow: auto` on `.z-organigram` itself.
- Touching the icon to toggle `open` fires `onOpen` to the server (`toServer: true`) before the local `setOpen` runs — in model mode, the server may reload children before the icon class swaps. The CSS must therefore not assume the `.z-icon-plus` / `.z-icon-minus` swap is synchronous with the click.

## Change log

### 2026-05-26 — js-source-hash drift recovery

**Hash before:** `6604d5a4bb62757bb1a786388da924ed80f3f8c874c7d631b3493fc59dd6b15c` (4 `.ts` files only)
**Hash after:** `be8916eebd1c58fae43660c7791b47c27f27127a97eac9fc5bb75d2fe055a05f` (4 `.ts` files, same set)

The TS source hash changed due to internal refactoring in ZK (TypeScript type annotation changes, `override` modifier additions, `/** @internal */` tag normalisation). No behavioral state-class logic changed.

**Structural facts added on re-read of mold `.js` files** (which were not included in the original hash but are the authoritative DOM-rendering source):

1. **`aria-hidden="true"` on icon `<i>`**: The `orgnode.js` mold hardcodes `aria-hidden="true"` on the `.z-orgnode-icon` element. Added to DOM structure and Notes.
2. **Orgnode custom children**: The orgnode mold renders `for (w = firstChild…) w.redraw(out)` after the icon — meaning custom widget children of an `<orgnode>` appear after the icon in DOM order. Added to DOM structure note.

**What did NOT change:** All four state classes (`.z-orgitem-selected`, `.z-orgitem-disabled`, `.z-orgitem-non-selectable`, `.z-orgitem-close`), icon glyph class toggling logic, `_adjustSize()` behavior, `isTopmost()` logic, `_canSelect()` guard, `doSelect_` icon-vs-body dispatch — all identical to previous TS read.

**Recommendation for future hash computation:** Include the four mold `.js` files in the hash alongside the `.ts` files, so structural changes to the DOM rendering are detected. Updated `js-source-files` in the contract accordingly.
