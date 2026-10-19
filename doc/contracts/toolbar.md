# Component: toolbar (theme design)
tier: T1
category: navigation
preview: ${PREVIEW_URL}/toolbar.zul
rules: see .claude/skills/zk-component-rules/components/toolbar.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Toolbar.css / AppBar.css
- DESIGN.md sections: §1, §7, §10, §11

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-toolbar` | min-height | ~48px |
| c2 | `.z-toolbar` | padding | 0 8px (or 0 16px) |
| c3 | `.z-toolbar` | background-color | transparent OR surface |
| c4 | `.z-toolbar` | border-bottom | optional 1px solid outline-variant |
| c5 | `.z-toolbar` | gap | 4–8px between children |

## States to evaluate
- [ ] horizontal, vertical, containing buttons/toolbarbutton/separator
- [ ] `<separator bar="true"/>` between items (horizontal or vertical `orient`) is a divider: 1px x 24px in a horizontal toolbar, 24px x 1px in a vertical one, `--zk-color-outline-variant` (`CanvasText` under forced colours), items adjacent; `<separator/>` without a bar stays a flex spacer (follow-up 45 F1, D21 = B)
