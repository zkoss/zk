# Component: bandbox (theme design)
tier: T2
category: input
preview: ${PREVIEW_URL}/bandbox.zul
rules: see .claude/skills/zk-component-rules/components/bandbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- doc/spec/DESIGN.md sections: "Surface Palette", "Text Colors", "Corner Radii", "Typography", "Motion", "Density", "Border Rules"
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/inp/css/combobox.css`
- Popup container CSS: `zul/src/main/resources/web/js/zul/inp/css/bandbox.css`
- No direct MUI mapping — the Evaluator compares input metrics against combobox.

## Expected values (T2 — DESIGN.md tokens only)
- Input metrics match combobox (since visually a styled combobox).
- Popup uses elevation level 2 (doc/spec/DESIGN.md "Elevation / Shadow").

### Open-state (icon-click) affordance — Mechanism A
ZK adds `z-bandbox-open` to the wrapper when the band button is clicked. The open-state ring MUST
match the input-click `:focus-within` ring — an **inset** box-shadow, not an outset one (an outset
ring reads as a different halo and is clipped by ancestor `overflow: hidden`). See
`marble-theme reference/focus-affordance-no-layout-shift.md`.

| id | selector | property | expected |
|----|----------|----------|----------|
| op1 | `.z-bandbox.z-bandbox-open` | box-shadow | `inset 0 0 0 1px var(--zk-bandbox-border-color-focus)` (inset — same ring as `:focus-within`, NOT outset) |
| va1 | `.z-bandbox` (root) | vertical-align | `middle` — Safari 18 gives an inline-flex box whose first item is an empty input the input's bottom edge as its baseline, so an empty field sat about 14px off the text line and jumped when typed into; centring the box avoids it (WebKit bug 294558, fixed in Safari 26; B50-3032885, B50-ZK-577). Side effect: text beside the field sits about 1px above the field centre |

## States to evaluate
- [ ] default, hover, focus, disabled, readonly, invalid, open
- [ ] inplace (see `zk-component-rules reference/inplace-state.md`)
- [ ] buttonVisible-false (band button hidden — see `zk-component-rules reference/buttonVisible-attribute.md`; verify `.z-bandbox-button.z-bandbox-disabled` has `display: none`)
