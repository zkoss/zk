# Component: progressmeter (theme design)
tier: T1
category: feedback
preview: ${PREVIEW_URL}/progressmeter.zul
rules: see .claude/skills/zk-component-rules/components/progressmeter.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: LinearProgress.css
- DESIGN.md sections: §3, §5

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-progressmeter` | height | 4–8px |
| c2 | `.z-progressmeter` | background-color | rgba(0, 0, 0, ~0.12) (track) |
| c3 | `.z-progressmeter` | border-radius | 4px (rounded ends) |
| c4 | `.z-progressmeter-image` | background-color | rgb(55, 111, 208) (primary) |
| c5 | `.z-progressmeter-image` | border-radius | inherit |
| c6 | `.z-progressmeter-image` | transition | width 250ms |

## States to evaluate
- [ ] 0%, 50%, 100%, indeterminate (if supported)
