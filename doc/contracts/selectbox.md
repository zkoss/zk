# Component: selectbox (theme design)
tier: T1
category: selection
preview: ${PREVIEW_URL}/selectbox.zul
rules: see .claude/skills/zk-component-rules/components/selectbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Select.css
- DESIGN.md sections: §1, §2, §5, §7, §10, §11

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-selectbox` | height | 39–40px |
| c2 | `.z-selectbox` | padding-right | ≥ 24px (space for dropdown icon) |
| c3 | `.z-selectbox` | border | 1px solid rgba(0, 0, 0, 0.23) |
| c4 | `.z-selectbox` | border-radius | 4px |
| c5 | `.z-selectbox` | font-size | 13px |
| c6 | `.z-selectbox` | background-image | SVG dropdown chevron (data: URL) |
| c7 | `.z-selectbox:hover` | border-color | rgba(0, 0, 0, 0.87) |
| c8 | `.z-selectbox:focus` | border-color | rgb(55, 111, 208) |
| c9 | `.z-selectbox:focus` | border-width | 2px |
| c10 | `.z-selectbox[disabled]` | opacity | 0.38 |
| c11 | `.z-selectbox option:checked` | background-color | rgb(<secondary-container-rgb>) (list-row family) |
| c12 | `.z-selectbox option:checked` | color | rgb(<on-secondary-container-rgb>) |

## States to evaluate
- [ ] default, hover, focus-visible, disabled
- [ ] option:checked must use the list-row family (secondary-container), NOT primary-container. See `reference/selected-state-families.md`.
