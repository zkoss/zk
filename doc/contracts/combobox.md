# Component: combobox (theme design)
tier: T1
category: input
preview: ${PREVIEW_URL}/combobox.zul
rules: see .claude/skills/zk-component-rules/components/combobox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- doc/spec/DESIGN.md sections: "Surface Palette", "Text Colors", "Corner Radii", "Typography", "Motion", "Density", "Border Rules"

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-combobox-input` | height | `var(--zk-control-height)` (min-height) |
| c2 | `.z-combobox-input` | font-size | `var(--zk-typescale-body-medium-size)` |
| c3 | `.z-combobox-input` | border | 1px solid `var(--zk-combobox-border-color)` |
| c4 | `.z-combobox-input` | border-radius | `var(--zk-combobox-radius)` |
| c5 | `.z-combobox-input:hover` | border-color | `var(--zk-combobox-border-color-hover)` |
| c6 | `.z-combobox-input:focus` | border-color | `var(--zk-combobox-border-color-focus)` |
| c7 | `.z-combobox-input:focus` | border-width | 2px |
| c7a | `.z-combobox:focus-within .z-combobox-input` | content edge (`border-left + padding-left`) | **== rest (±0px)** — `padding-left` compensates the 2px left border (`border-right:none`); text must NOT shift right. |
| c7b | `.z-combobox-input` / `.z-combobox-button` | transition-property | must **NOT** include `border-width` — snaps in step with padding comp so nothing drifts |
| c8 | `.z-combobox-button` | width | `var(--zk-combobox-button-width)` (small icon area) |
| c9 | `.z-combobox-popup` | box-shadow | dropdown shadow (doc/spec/DESIGN.md "Elevation / Shadow", level 2) |
| c10 | `.z-combobox-popup` | border-radius | `var(--zk-combobox-popup-radius)` |
| c11 | `.z-comboitem` | padding | `var(--zk-spacing-2) var(--zk-spacing-4)` (vertical 8px) |
| c12 | `.z-comboitem:hover` | background-color | tinted (`--zk-combobox-item-hover-bg`) |
| c13 | `.z-comboitem-selected` | background-color | `var(--zk-combobox-selected-bg)` (→ `--zk-color-secondary-container`) — LIST-ROW family, see `marble-theme reference/selected-state-families.md` |
| c14 | `.z-comboitem-selected` | color | `var(--zk-combobox-selected-fg)` (→ `--zk-color-on-secondary-container`) |
| c15 | `.z-comboitem-selected` | background-color | MUST NOT be `--zk-color-primary-container` — wrong family; `--zk-combobox-selected-bg` = `--zk-color-secondary-container` |
| c16 | `.z-comboitem-icon` (item with `iconSclass`) | margin-right | `var(--zk-spacing-3)` — leading icon must be separated from the label; glyph-to-text gap ≥ 8px, NOT 0 |
| c17 | `.z-comboitem-image:empty` (plain item's backward-compat span) | margin-right | 0px — the always-emitted empty image span must NOT phantom-indent the label (no container `gap`); see skill `components/combobox.md` |
| c18 | `.z-combobox-popup` (no items) | min-height | `var(--zk-spacing-4)` — an open popup without items stays a visible, clickable surface (a click on it closes the list, `ComboWidget.doClick_`); a popup with items is taller, so unchanged (B30-1879511) |

## States to evaluate
- [ ] default
- [ ] hover (input + popup items)
- [ ] focus-visible (input)
- [ ] disabled
- [ ] readonly
- [ ] invalid
- [ ] open (popup visible)
- [ ] item-selected
- [ ] inplace (border/background collapse when blurred — see `zk-component-rules reference/inplace-state.md`)
- [ ] buttonVisible-false — combobox uses split-border pattern (see `zk-component-rules reference/buttonVisible-attribute.md`). Two sub-checks both required:
  - (a) `.z-combobox-button.z-combobox-disabled` has `display: none`
  - (b) `.z-combobox-input.z-combobox-input-full` has 4-side border (`border-right` is NOT `none`) and uniform `border-radius` (no flat-right rectangle)
