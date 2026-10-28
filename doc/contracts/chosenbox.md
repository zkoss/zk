# Component: chosenbox (theme design)
tier: T1
category: selection
preview: ${PREVIEW_URL}/chosenbox.zul
rules: see .claude/skills/zk-component-rules/components/chosenbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: `Inputs/OutlinedInput.css`, `Navigation/MenuItem.css` (for popup option rows), `DataDisplay/Chip.css` (for selected chips)
- Skill: `reference/floating-popup-in-body.md` (popup detached to `<body>` — no percentage widths)
- Skill: `components/chosenbox.md` (chip class quirk, EE-only)
- DESIGN.md sections: §2, §3, §7, §8, §11

## Expected values

| id  | selector | property | expected |
|-----|----------|----------|----------|
| c1  | `.z-chosenbox` | border | 1px solid rgba(0, 0, 0, 0.23) (outline) |
| c2  | `.z-chosenbox` | border-radius | 4px (`--zk-shape-input`) |
| c3  | `.z-chosenbox` | min-height | 36–40px |
| c4  | `.z-chosenbox-item` | background-color | `--zk-color-surface-container-high` (neutral grey, ~rgb(232,238,247)) — NOT primary; matches MUI default `MuiChip-filled` `rgba(0,0,0,0.08)` |
| c5  | `.z-chosenbox-item` | border-radius | 4px (extra-small chip) |
| c6  | `.z-chosenbox-item` | height | 28px |
| c7  | `.z-chosenbox-item-focus` | background-color | `rgb(201, 213, 234)` (≈ `--zk-color-secondary-container`, LIST-ROW family per `reference/selected-state-families.md`) — only on keyboard focus, not default |
| c7a | `.z-chosenbox-item-focus` | background-color | MUST NOT be `rgb(178, 223, 219)` (= `--zk-color-secondary-container`) — wrong family |
| c8  | `.z-chosenbox-input` | font-size | 13px |
| c9  | `.z-chosenbox-focus` | border-color | `--zk-color-primary` |
| c10 | `.z-chosenbox-popup` | **rendered width** | **must equal trigger width** (ZK sets inline width = chosenbox width). CSS must NOT set `width: 100%` / `min-width: 100%` — popup parent is `<body>`. See `reference/floating-popup-in-body.md`. |
| c11 | `.z-chosenbox-popup` | border-radius | 4px |
| c12 | `.z-chosenbox-popup` | box-shadow | dropdown elevation (DESIGN.md §6, level 2) |
| c13 | `.z-chosenbox-option` | padding | **6px 16px** (NOT `0 12px`) — MUI MenuItem pattern |
| c14 | `.z-chosenbox-option` | min-height | 36px (allow content to grow, do not lock `height`) |
| c15 | `.z-chosenbox-option:hover` | background-color | tinted overlay (~rgba(0,0,0,0.04)) |
| c16 | `.z-chosenbox-disabled` | opacity | 0.38 |

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
2. **Chip color** — Selected chips in a multi-select autocomplete are MD3 *Input Chips* / MUI default `MuiChip-filled` — neutral tonal surface, NOT primary. Primary-container is reserved for the keyboard-focused chip (`.z-chosenbox-item-focus`).
3. **Option vs item** — `.z-chosenbox-item` is the selected chip; `.z-chosenbox-option` is the popup row. Easy to swap.
