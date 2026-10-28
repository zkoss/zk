# Component: a
tier: T1
category: navigation
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/a.css
siblings: []
preview: ${PREVIEW_URL}/a.zul

## References
- MUI CSS: Link.css
- DESIGN.md sections: §2, §3, §7

## DOM key selectors
```
.z-a                    ← root anchor element
.z-a:hover
.z-a:visited
.z-a[disabled]
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-a` | color | rgb(55, 111, 208) (primary) |
| c2 | `.z-a` | text-decoration | underline or none |
| c3 | `.z-a:hover` | color | rgb(30, 70, 160) or brighter primary |
| c4 | `.z-a[disabled]` | opacity | 0.38 |
| c5 | `.z-a[disabled]` | pointer-events | none |

## States to evaluate
- [ ] default, hover, visited, disabled
