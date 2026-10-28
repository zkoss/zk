# Component: paging
tier: T1
category: data
shared-css-file: zul/src/main/resources/web/js/zul/mesh/css/paging.css
siblings: []
preview: ${PREVIEW_URL}/paging.zul   (paging widget is also embedded in grid/listbox/tree previews)

## References
- MUI CSS: Pagination.css
- DESIGN.md sections: §3, §5, §7, §11

## DOM key selectors
```
.z-paging
.z-paging-info
.z-paging-button / -current / -disabled
.z-paging-previous / -next / -first / -last
.z-paging-input                    ← jump-to-page input
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-paging-button` | min-width / height | ~32px |
| c2 | `.z-paging-button` | border-radius | 50% (circular pill per MUI Pagination) |
| c3 | `.z-paging-button:hover` | background-color | state-layer tint |
| c4 | `.z-paging-current` | background-color | primary-container tint |
| c5 | `.z-paging-current` | color | rgb(55, 111, 208) |
| c6 | `.z-paging-button[disabled]` | opacity | 0.38 |
| c7 | `.z-paging-info` | font-size | 13px |

## States to evaluate
- [ ] default, hover, current page, disabled (first/last edges), input variant
