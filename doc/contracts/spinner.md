# Component: spinner (theme design)
tier: T1
category: input
preview: ${PREVIEW_URL}/spinner.zul
rules: see .claude/skills/zk-component-rules/components/combo-trio.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Inputs/OutlinedInput.css (number variant)
- DESIGN.md sections: §1, §2, §5, §7, §9, §10, §11

## Expected values
- Same input metrics as combobox c1–c7 with selectors → `.z-spinner-*`.
- `.z-spinner-button` should be ~24px wide column, stacked up/down arrows.

## States to evaluate
- [ ] default, hover, focus, disabled, readonly, invalid
- [ ] inplace (see `reference/inplace-state.md`)
- [ ] buttonVisible-false (up/down arrow stack hidden — see `reference/buttonVisible-attribute.md`; verify `.z-spinner-button.z-spinner-disabled` has `display: none`)
