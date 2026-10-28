# Component: combobutton (theme design)
tier: T2
category: button
preview: ${PREVIEW_URL}/combobutton.zul
rules: see .claude/skills/zk-component-rules/components/combobutton.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- DESIGN.md sections: §5, §7, §10, §11
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/wgt/css/button.css`
- Dropdown content styling: `zul/src/main/resources/web/js/zul/menu/css/menu.css`
- MUI nearest: ButtonGroup + SplitButton pattern.

## Expected values (T2 — DESIGN.md tokens; coherent with button)
- Height matches `.z-button` (~35px).
- Divider between main and dropdown segments (1px solid rgba(0,0,0,0.12)).
- Hover and focus mirror `.z-button`.

## States to evaluate
- [ ] default, hover, focus, disabled, open
