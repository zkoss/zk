# Component: chosenbox (theme design)
tier: T1
category: selection
preview: ${PREVIEW_URL}/chosenbox.zul
rules: see .claude/skills/zk-component-rules/components/chosenbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: `Inputs/OutlinedInput.css`, `Navigation/MenuItem.css` (for popup option rows), `DataDisplay/Chip.css` (for selected chips)
- Skill: `zk-component-rules reference/floating-popup-in-body.md` (popup detached to `<body>` — no percentage widths)
- Skill: `components/chosenbox.md` (chip class quirk, EE-only)
- doc/spec/DESIGN.md sections: "Text Colors", "Brand & Status Colors", "Typography", "State-Layer Overlays", "Border Rules"

## Expected values

| id  | selector | property | expected |
|-----|----------|----------|----------|
| c1  | `.z-chosenbox` | border | 1px solid `var(--zk-chosenbox-border-color)` (→ `--zk-color-outline`) |
| c2  | `.z-chosenbox` | border-radius | `var(--zk-chosenbox-radius)` (→ `--zk-shape-input`) |
| c3  | `.z-chosenbox` | min-height | `var(--zk-control-height)` |
| c4  | `.z-chosenbox-item` | background-color | `var(--zk-chosenbox-item-bg)` (→ `--zk-color-surface-container-high`, neutral grey) — NOT primary; matches MUI default `MuiChip-filled` `rgba(0,0,0,0.08)` |
| c5  | `.z-chosenbox-item` | border-radius | `var(--zk-shape-corner-extra-small)` (extra-small chip) |
| c6  | `.z-chosenbox-item` | height | 28px |
| c7  | `.z-chosenbox-item-focus` | background-color | `var(--zk-chosenbox-item-focus-bg)` (→ `--zk-color-secondary-container`, LIST-ROW family per `marble-theme reference/selected-state-families.md`) — only on keyboard focus, not default |
| c7a | `.z-chosenbox-item-focus` | background-color | MUST NOT be `--zk-color-primary-container` — wrong family; the focused chip uses `--zk-chosenbox-item-focus-bg` = `--zk-color-secondary-container` |
| c8  | `.z-chosenbox-input` | font-size | `var(--zk-typescale-body-medium-size)` |
| c9  | `.z-chosenbox-focus` | border-color | `--zk-color-primary` |
| c10 | `.z-chosenbox-popup` | **rendered width** | **must equal trigger width** (ZK sets inline width = chosenbox width). CSS must NOT set `width: 100%` / `min-width: 100%` — popup parent is `<body>`. See `zk-component-rules reference/floating-popup-in-body.md`. |
| c11 | `.z-chosenbox-popup` | border-radius | `var(--zk-chosenbox-popup-radius)` |
| c12 | `.z-chosenbox-popup` | box-shadow | dropdown elevation (doc/spec/DESIGN.md "Elevation / Shadow", level 2) |
| c13 | `.z-chosenbox-option` | padding | **6px `var(--zk-spacing-4)`** (NOT `0 12px`) — MUI MenuItem pattern |
| c14 | `.z-chosenbox-option` | min-height | 36px (allow content to grow, do not lock `height`) |
| c15 | `.z-chosenbox-option:hover` | background-color | tinted overlay (~rgba(0,0,0,0.04)) |
| c16 | `.z-chosenbox-disabled` | opacity | `var(--zk-state-disabled-opacity)` |
| va1 | `.z-chosenbox` (root) | vertical-align | `middle` — Safari 18 gives an inline-flex box whose first item is an empty input the input's bottom edge as its baseline, so an empty field sat about 14px off the text line and jumped when typed into; centring the box avoids it (WebKit bug 294558, fixed in Safari 26; B50-3032885, B50-ZK-577). Side effect: text beside the field sits about 1px above the field centre |

## States to evaluate
- [ ] default (with selected chips)
- [ ] hover (wrapper + chips + options)
- [ ] focus-visible (wrapper border highlights primary)
- [ ] popup-open — **measure popup rendered width vs trigger width** (must match, not full viewport)
- [ ] option-hover (popup row tinted)
- [ ] item-focus (chip keyboard-focused → secondary-container background)
- [ ] disabled (chips show no close button, opacity reduced)
- [ ] creatable + empty-result ("create new" row visible)

## Common pitfalls
1. **Popup width** — `min-width: 100%` on `.z-chosenbox-popup` makes the popup span the viewport because the popup is a child of `<body>`. ZK already sets inline width — do not add any width hint in CSS.
2. **Chip color** — Selected chips in a multi-select autocomplete are MD3 *Input Chips* / MUI default `MuiChip-filled` — neutral tonal surface, NOT primary. Secondary-container is reserved for the keyboard-focused chip (`.z-chosenbox-item-focus`).
3. **Option vs item** — `.z-chosenbox-item` is the selected chip; `.z-chosenbox-option` is the popup row. Easy to swap.
