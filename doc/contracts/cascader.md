# Component: cascader (theme design)
tier: T1
category: selection
preview: ${PREVIEW_URL}/cascader.zul
rules: see .claude/skills/zk-component-rules/components/cascader.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/inp/Cascader.ts
  - zkmax/inp/mold/cascader.js
js-source-hash: 86b0418368142f5b5c5f813350a618ce3936743be96ee22db9ac4a0cf08f9e0f
closest-sibling: searchbox
mockup-needed: Y
mockup-rationale: ZKDoc image shows iceblue-style rounded-rectangle trigger with blue selected text; Marble uses MD3 OutlinedInput pattern with primary-colour focus ring and `--zk-color-primary` for selected item text — different focus-ring and item-selection recipe, mockup needed to anchor design intent.

## References
- MUI CSS: `Inputs/OutlinedInput.css` (trigger chrome), `Navigation/MenuItem.css` (popup item rows) — no direct "Cascader" MUI analog
- doc/spec/DESIGN.md sections: "Border Rules" (input chrome, outline/separator colours), "Brand & Status Colors" (primary colour), "Elevation / Shadow" (dropdown shadow), "State-Layer Overlays"
- Iceblue baseline: doc/contracts/baselines/cascader-iceblue.png
- HTML contract: doc/contracts/cascader.html
- ZKDoc canonical: https://github.com/zkoss/zkdoc/blob/master/zk_component_ref/images/Cascader-basic.png

## Design Contract

The cascader trigger renders as an MD3 outlined input field — 40px tall, `--zk-cascader-border-color` border (1px) at rest. On focus/open the border colour becomes `--zk-cascader-border-color-focus` and a `box-shadow: 0 0 0 1px` ring in the same colour sits outside the border (an outset spread, not an inset ring); the 1px border plus the ring reads as 2px. The trigger body shows a slash-joined path label in `--zk-cascader-fg`, or a muted placeholder in `--zk-color-on-surface-variant` when no item is selected. The trailing icon is a caret-down (dropdown closed, no selection), caret-right (dropdown open, no selection), or times (clear, when selection present); all icon states use `--zk-color-on-surface-variant`. On hover the border colour becomes `--zk-cascader-border-color-hover`. Disabled state applies `--zk-state-disabled-opacity` to the entire trigger. The dropdown popup surfaces with `--zk-elevation-dropdown` shadow and a `--zk-color-outline-variant` border; cave columns are separated by `1px solid --zk-color-outline-variant` dividers except the first. Each item row is 36px tall; hover and keyboard-active rows get a neutral state-layer tint (`rgba(0,0,0,0.04)` — matches MUI MenuItem hover). Selected items (path ancestors + current leaf) render their text in `--zk-cascader-accent`. Non-leaf rows show a caret-right expand icon **anchored at the row's trailing edge** (MD3 trailing-icon slot — fixed position regardless of label length, like MUI nested-menu arrows), never inline after the text. The trigger's `border-color` and the item's `background-color` transition at `--zk-motion-duration-standard` / `--zk-motion-easing-standard`; focus/open sets `transition: none` on the trigger.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-cascader` bbox height ≥ 32px AND (border-width ≥ 1px OR box-shadow ≠ none) | trigger is a legible field — visually closed |
| M2 | `.z-cascader-focus` or `.z-cascader-open` state: border-color ≠ resting border-color OR box-shadow ≠ none | focus/open state must be visibly distinct from resting |
| M3 | `.z-cascader-disabled` opacity ≤ 0.5 (reads as inactive) | disabled trigger is recognisably suppressed |
| M4 | When popup is open: `.z-cascader-popup` bbox.width ≥ `.z-cascader` bbox.width − 2 | popup never narrower than its trigger |
| M5 | `.z-cascader-item.z-cascader-selected` color ≠ `.z-cascader-item` (non-selected) color | selected path items are visually distinguished from unselected siblings |
| M6 | `.z-cascader-cave` + `.z-cascader-cave` siblings are horizontally adjacent: max horizontal gap ≤ 4px | columns dock side-by-side — no collapsed second panel or phantom gap |
| M7 | All `.z-cascader-item .z-cascader-icon` within one cave share equal bbox.right (±1px), independent of each item's label length | expand carets occupy a fixed trailing slot (MD3 menus) — a caret that floats after the text drifts with label width |
| M8 | Pre-selected at **initial render, before any user interaction**: `.z-cascader-label` shows its full text (`scrollWidth ≤ clientWidth`, no ellipsis) AND `.z-cascader-placeholder` occupies zero width (bbox.width = 0 or display none). Must be measured on a freshly loaded page — the bug self-heals after one interactive selection. | ZK's initial render leaves BOTH label and placeholder visible when a selection is pre-set (`mold/cascader.js` emits empty `style` on both; `Cascader.java` never renders `placeholderVisible`; `bind_` only patches the empty-selection branch — see skill). A theme granting the placeholder horizontal space (`flex: 1`) squeezes the label into ellipsis while the same selection made interactively renders full-width |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-cascader` | border-color | `var(--zk-cascader-border-color)` | doc/spec/DESIGN.md "Border Rules" — MD3 outlined input at rest |
| c2 | `.z-cascader` | border-radius | `var(--zk-cascader-radius)` (→ `--zk-shape-input`) | doc/spec/DESIGN.md "Corner Radii" — extra-small shape for inputs |
| c3 | `.z-cascader` | height | `40px` | doc/spec/DESIGN.md "Density" — MD3 comfortable density for input fields |
| c4 | `.z-cascader` | background-color | `var(--zk-cascader-bg)` | doc/spec/DESIGN.md "Surface Palette" |
| c5 | `.z-cascader:hover` | border-color | `var(--zk-cascader-border-color-hover)` | doc/spec/DESIGN.md "Border Rules" — hover intensifies outline |
| c6 | `.z-cascader-focus` | border-color | `var(--zk-cascader-border-color-focus)` | doc/spec/DESIGN.md "Border Rules" — primary on focus |
| c7 | `.z-cascader-focus` | box-shadow | `0 0 0 1px var(--zk-cascader-border-color-focus)` | doc/spec/DESIGN.md "Border Rules" — 2px visual ring effect |
| c8 | `.z-cascader-open` | border-color | `var(--zk-cascader-border-color-focus)` | same as focus — open ≡ focused |
| c8b | `.z-cascader-open` | box-shadow | `0 0 0 1px var(--zk-cascader-border-color-focus)` | same as focus (c7) — open ≡ focused, ring included |
| c9 | `.z-cascader-label` | color | `var(--zk-cascader-fg)` | doc/spec/DESIGN.md "Text Colors" |
| c10 | `.z-cascader-label` | font-size | `var(--zk-typescale-label-large-size)` | doc/spec/DESIGN.md "Typography" — label-large for input value text |
| c11 | `.z-cascader-placeholder` | color | `var(--zk-color-on-surface-variant)` | doc/spec/DESIGN.md "Text Colors" — muted placeholder |
| c12 | `.z-cascader-icon` (root trigger icon) | color | `var(--zk-color-on-surface-variant)` | doc/spec/DESIGN.md "Text Colors" — secondary icon colour |
| c13 | `.z-cascader-disabled` | opacity | `var(--zk-state-disabled-opacity)` | doc/spec/DESIGN.md "State-Layer Overlays" — MD3 disabled state |
| c14 | `.z-cascader-popup` | box-shadow | `var(--zk-elevation-dropdown)` | doc/spec/DESIGN.md "Elevation / Shadow" — elevation-2 for dropdowns. The shadow rule is on `.z-cascader-shadow`, which sits on the same node as `.z-cascader-popup` (`Cascader.ts:646`) |
| c15 | `.z-cascader-popup` | border | `1px solid var(--zk-color-outline-variant)` | doc/spec/DESIGN.md "Border Rules" |
| c16 | `.z-cascader-popup` | border-radius | `var(--zk-cascader-popup-radius)` (→ `--zk-shape-menu`) | doc/spec/DESIGN.md "Corner Radii" — extra-small menu radius |
| c17 | `.z-cascader-cave` | border-left | `1px solid var(--zk-color-outline-variant)` | doc/spec/DESIGN.md "Border Rules" — column separator |
| c18 | `.z-cascader-cave:first-child` | border-left | `none` | structural rule — no left border on first column |
| c19 | `.z-cascader-item` | height | `36px` | doc/spec/DESIGN.md "Density" — MD3 menu item comfortable density |
| c20 | `.z-cascader-item` | font-size | `var(--zk-typescale-body-medium-size)` | doc/spec/DESIGN.md "Typography" |
| c21 | `.z-cascader-item` | color | `var(--zk-cascader-fg)` | doc/spec/DESIGN.md "Text Colors" |
| c22 | `.z-cascader-item:hover` | background-color | `rgba(0, 0, 0, 0.04)` | doc/spec/DESIGN.md "State-Layer Overlays" — solid-colour row hover tint (matches MUI MenuItem) |
| c23 | `.z-cascader-item.z-cascader-active` | background-color | `rgba(0, 0, 0, 0.04)` | same tint as hover — keyboard active = hover visual |
| c24 | `.z-cascader-item.z-cascader-selected` | color | `var(--zk-cascader-accent)` | doc/spec/DESIGN.md "Brand & Status Colors" — selected path shown in primary |
| c25 | `.z-cascader-item .z-cascader-icon` (expand icon) | color | `var(--zk-color-on-surface-variant)` | doc/spec/DESIGN.md "Text Colors" |
| c26 | `.z-cascader-item .z-cascader-icon` (expand icon) | margin-left | `auto` (declared; computed resolves to used px on flex items — verify via stylesheet rule or M7) | mockup cascader.html item-icon block — trailing-anchor in the item's flex row (item text is a bare text node; the icon must carry the push, see skill) |
| c27 | `.z-cascader:has(.z-cascader-label:not(:empty)) .z-cascader-placeholder` | display | `none` — suppress the stale placeholder div ZK leaves visible at initial render when a selection is pre-set (skill — initial-render display gap). Stylesheet-level only by design: interactive flows write inline `display` styles, which win the cascade and keep full control | guards M8 |
| va1 | `.z-cascader` (root) | vertical-align | `middle` — with no value and no placeholder text the first flex item is an empty placeholder, which has no baseline, so every browser aligned the box by that item's bottom edge and the empty field sat off the text line; centring the box avoids it (B50-3032885, B50-ZK-577). Side effect: text beside the field sits about 1px above the field centre | `doc/spec/DESIGN.md` form-row alignment |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (no selection) | `.z-cascader` | c1, c2, c3, c4 (label hidden, placeholder visible) |
| hover | `.z-cascader:hover` | c5 |
| focus-visible / open | `.z-cascader-focus`, `.z-cascader-open` | c6, c7, c8, c8b |
| has-selection | `.z-cascader` (label visible) | c9, c10 |
| pre-selected (initial render, no interaction) | `.z-cascader` with model-level pre-selection | c27, M8 |
| placeholder | `.z-cascader` (no selection) | c11 |
| disabled | `.z-cascader.z-cascader-disabled` | c13 |
| popup-default | `.z-cascader-popup` | c14, c15, c16 |
| cave-column | `.z-cascader-cave` | c17, c18 |
| item-default | `.z-cascader-item` | c19, c20, c21 |
| item-hover | `.z-cascader-item:hover` | c22 |
| item-active (keyboard) | `.z-cascader-item.z-cascader-active` | c23 |
| item-selected | `.z-cascader-item.z-cascader-selected` | c24 |
| item-expand-icon | `.z-cascader-item .z-cascader-icon` | c25, c26 |

## States to evaluate
- [ ] default (no selection, placeholder visible)
- [ ] has-selection (label shows slash-joined path)
- [ ] pre-selected at initial render (full label text, no ellipsis, placeholder takes no space — M8; measure BEFORE any interaction)
- [ ] hover (trigger border intensifies)
- [ ] focus-visible (primary ring)
- [ ] open (popup visible, caves side by side)
- [ ] item-hover
- [ ] item-active (keyboard navigation)
- [ ] item-selected (path ancestors highlighted)
- [ ] disabled
