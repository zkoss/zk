# Component: tabpanel
tier: T1
category: navigation
shared-css-file: zul/src/main/resources/web/js/zul/tab/css/tabbox.css
siblings: [tabbox, tab]
preview: ${PREVIEW_URL}/tabbox.zul

## References
See tabbox contract.

## DOM key selectors
```
.z-tabpanels
.z-tabpanel
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| p1 | `.z-tabpanel` | padding | 16px (matches content area) |
| p2 | `.z-tabpanel` | background-color | transparent OR surface |

## States to evaluate
- [ ] active panel visible, inactive hidden
