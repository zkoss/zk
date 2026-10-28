# Component: toast (theme design)
tier: T1
category: feedback
preview: ${PREVIEW_URL}/notification.zul   (toast variant — confirm preview includes toast section)
rules: see .claude/skills/zk-component-rules/components/toast-notification.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Snackbar.css
- DESIGN.md sections: §1, §2, §6, §11

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-toast` | background-color | rgba(50, 50, 50, 0.95) (dark) OR surface |
| c2 | `.z-toast` | color | rgb(255, 255, 255) (when dark bg) |
| c3 | `.z-toast` | min-height | 48px |
| c4 | `.z-toast` | padding | 8px 16px |
| c5 | `.z-toast` | border-radius | 4px |
| c6 | `.z-toast` | box-shadow | level-3 |
| c7 | `.z-toast` | font-size | 13px |

## States to evaluate
- [ ] visible toast, with action button, with close button
