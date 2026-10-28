# Component: popup
tier: T1
category: container
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/popup.css
siblings: []
preview: ${PREVIEW_URL}/popup.zul

## References
- MUI CSS: Popover.css
- DESIGN.md sections: §2, §3, §6, §10

## DOM key selectors
```
.z-popup                ← root (positioned overlay)
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-popup` | background-color | rgb(255, 255, 255) (surface) |
| c2 | `.z-popup` | border-radius | 4px or var(--zk-shape-corner-small) |
| c3 | `.z-popup` | box-shadow | elevation-2 shadow |
| c4 | `.z-popup` | border | none or 1px solid rgba(0,0,0,0.08) |

## States to evaluate
- [ ] visible popup
