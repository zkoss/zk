# Component: cascader (theme design)
tier: T1
category: selection
preview: ${PREVIEW_URL}/cascader.zul
rules: see .claude/skills/zk-component-rules/components/cascader.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkcml/zkmax/src/main/resources/web/js/zkmax/inp/Cascader.ts
  - zkcml/zkmax/src/main/resources/web/js/zkmax/inp/mold/cascader.js
js-source-hash: 86b0418368142f5b5c5f813350a618ce3936743be96ee22db9ac4a0cf08f9e0f
closest-sibling: searchbox
mockup-needed: Y
mockup-rationale: ZKDoc image shows iceblue-style rounded-rectangle trigger with blue selected text; Marble uses MD3 OutlinedInput pattern with primary-colour focus ring and `--zk-color-primary` for selected item text — different focus-ring and item-selection recipe, mockup needed to anchor design intent.

## References
- MUI CSS: `Inputs/OutlinedInput.css` (trigger chrome), `Navigation/MenuItem.css` (popup item rows) — no direct "Cascader" MUI analog
- DESIGN.md sections: §2 (input chrome), §3 (primary colour), §6 (elevation/dropdown shadow), §8 (state layers), §11 (outline/separator colours)
- Iceblue baseline: doc/contracts/baselines/cascader-iceblue.png
- HTML contract: doc/contracts/cascader.html
- ZKDoc canonical: /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/Cascader-basic.png

## Design Contract

The cascader trigger renders as an MD3 outlined input field — 40px tall, `--zk-color-outline` border at rest, border thickens to a 2px primary-colour ring on focus/open (via `border-color: primary + box-shadow: 0 0 0 1px primary`). The trigger body shows a slash-joined path label in `--zk-color-on-surface`, or a muted placeholder in `--zk-color-on-surface-variant` when no item is selected. The trailing icon is a caret-down (dropdown closed, no selection), caret-right (dropdown open, no selection), or times (clear, when selection present); all icon states use `--zk-color-on-surface-variant`. On hover the border becomes `--zk-color-on-surface`. Disabled state applies `--zk-state-disabled-opacity` (0.38) to the entire trigger. The dropdown popup surfaces with `--zk-elevation-dropdown` shadow and a `--zk-color-outline-variant` border; cave columns are separated by `1px solid --zk-color-outline-variant` dividers except the first. Each item row is 36px tall; hover and keyboard-active rows get a neutral state-layer tint (`rgba(0,0,0,0.04)` — matches MUI MenuItem hover). Selected items (path ancestors + current leaf) render their text in `--zk-color-primary`. Non-leaf rows show a caret-right expand icon **anchored at the row's trailing edge** (MD3 trailing-icon slot — fixed position regardless of label length, like MUI nested-menu arrows), never inline after the text. Transitions on `border-color` and `background-color` use `--zk-motion-duration-short3` / `--zk-motion-easing-standard`.

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
| M8 | Pre-selected at **initial render, before any user interaction**: `.z-cascader-label` shows its full text (`scrollWidth ≤ clientWidth`, no ellipsis) AND `.z-cascader-placeholder` occupies zero width (bbox.width = 0 or display none). Must be measured on a freshly loaded page — the bug self-heals after one interactive selection. | added 2026-06-07, failing-first — ZK's initial render leaves BOTH label and placeholder visible when a selection is pre-set (`mold/cascader.js` emits empty `style` on both; `Cascader.java` never renders `placeholderVisible`; `bind_` only patches the empty-selection branch — see skill). A theme granting the placeholder horizontal space (`flex: 1`) squeezes the label into ellipsis while the same selection made interactively renders full-width |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-cascader` | border-color | `var(--zk-color-outline)` | DESIGN.md §2 — MD3 outlined input at rest |
| c2 | `.z-cascader` | border-radius | `var(--zk-shape-input)` (= 4px) | DESIGN.md §2 — extra-small shape for inputs |
| c3 | `.z-cascader` | height | `40px` | DESIGN.md §2 — MD3 comfortable density for input fields |
| c4 | `.z-cascader` | background-color | `var(--zk-color-surface)` | DESIGN.md §2 |
| c5 | `.z-cascader:hover` | border-color | `var(--zk-color-on-surface)` | DESIGN.md §7 — hover intensifies outline |
| c6 | `.z-cascader-focus` | border-color | `var(--zk-color-primary)` | DESIGN.md §2 — primary on focus |
| c7 | `.z-cascader-focus` | box-shadow | `0 0 0 1px var(--zk-color-primary)` | DESIGN.md §2 — 2px visual ring effect |
| c8 | `.z-cascader-open` | border-color | `var(--zk-color-primary)` | same as focus — open ≡ focused |
| c8b | `.z-cascader-open` | box-shadow | `0 0 0 1px var(--zk-color-primary)` | same as focus (c7) — open ≡ focused, ring included |
| c9 | `.z-cascader-label` | color | `var(--zk-color-on-surface)` | DESIGN.md §3 |
| c10 | `.z-cascader-label` | font-size | `var(--zk-typescale-label-large-size)` (= 14px) | DESIGN.md §2 — label-large for input value text |
| c11 | `.z-cascader-placeholder` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §2 — muted placeholder |
| c12 | `.z-cascader-icon` (root trigger icon) | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §11 — secondary icon colour |
| c13 | `.z-cascader-disabled` | opacity | `var(--zk-state-disabled-opacity)` (= 0.38) | DESIGN.md §8 — MD3 disabled state |
| c14 | `.z-cascader-popup` | box-shadow | `var(--zk-elevation-dropdown)` | DESIGN.md §6 — elevation-2 for dropdowns |
| c15 | `.z-cascader-popup` | border | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §11 |
| c16 | `.z-cascader-popup` | border-radius | `var(--zk-shape-menu)` (= 4px) | DESIGN.md §2 — extra-small menu radius |
| c17 | `.z-cascader-cave` | border-left | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §11 — column separator |
| c18 | `.z-cascader-cave:first-child` | border-left | `none` | structural rule — no left border on first column |
| c19 | `.z-cascader-item` | height | `36px` | DESIGN.md §2 — MD3 menu item comfortable density |
| c20 | `.z-cascader-item` | font-size | `var(--zk-typescale-body-medium-size)` (= 13px) | DESIGN.md §2 |
| c21 | `.z-cascader-item` | color | `var(--zk-color-on-surface)` | DESIGN.md §3 |
| c22 | `.z-cascader-item:hover` | background-color | `rgba(0, 0, 0, 0.04)` | DESIGN.md §8 — solid-colour row hover tint (matches MUI MenuItem) |
| c23 | `.z-cascader-item.z-cascader-active` | background-color | `rgba(0, 0, 0, 0.04)` | same tint as hover — keyboard active = hover visual |
| c24 | `.z-cascader-item.z-cascader-selected` | color | `var(--zk-color-primary)` | DESIGN.md §3 — selected path shown in primary |
| c25 | `.z-cascader-item .z-cascader-icon` (expand icon) | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §11 |
| c26 | `.z-cascader-item .z-cascader-icon` (expand icon) | margin-left | `auto` (declared; computed resolves to used px on flex items — verify via stylesheet rule or M7) | mockup cascader.html item-icon block — trailing-anchor in the item's flex row (item text is a bare text node; the icon must carry the push, see skill) |
| c27 | `.z-cascader:has(.z-cascader-label:not(:empty)) .z-cascader-placeholder` | display | `none` — suppress the stale placeholder div ZK leaves visible at initial render when a selection is pre-set (skill — initial-render display gap). Stylesheet-level only by design: interactive flows write inline `display` styles, which win the cascade and keep full control | added 2026-06-07, failing-first (M8) |

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
