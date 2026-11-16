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
| c3 | `.z-combobox` | border | 1px solid `var(--zk-combobox-border-color)` (the root draws the whole outline; the input and the button draw no border) |
| c4 | `.z-combobox` | border-radius | `var(--zk-combobox-radius)` |
| c5 | `.z-combobox:hover` | border-color | `var(--zk-combobox-border-color-hover)` |
| c6 | `.z-combobox:focus-within` | border-color | `var(--zk-combobox-border-color-focus)` |
| c7 | `.z-combobox:focus-within` | border-width | 1px (unchanged); the focus ring is an inset `box-shadow` in `var(--zk-combobox-border-color-focus)` |
| c7a | `.z-combobox:focus-within .z-combobox-input` | border-left-width | **== rest (±0px)** — the input has no border, so focus changes no edge; text must NOT shift. |
| c7b | `.z-combobox` | transition-property | must **NOT** include `border-width` — the root transitions `border-color` and `box-shadow` only |
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
- [ ] buttonVisible-false — the combobox root draws the border (Pattern R, see `zk-component-rules reference/buttonVisible-attribute.md`), so hiding the button leaves the root border whole. Two sub-checks both required:
  - (a) `.z-combobox-button.z-combobox-disabled` has `display: none`
  - (b) `.z-combobox` keeps its 1px border on all four sides and a uniform `border-radius` (no flat-right rectangle)
