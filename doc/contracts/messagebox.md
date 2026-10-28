# Component: messagebox
tier: T1
category: feedback
shared-css-file: zul/src/main/resources/web/js/zul/wnd/css/messagebox.css
siblings: []
preview: ${PREVIEW_URL}/messagebox.zul

## References
- MUI CSS: Dialog.css / Alert.css
- DESIGN.md sections: §3, §6, §10, §11

## DOM key selectors
```
.z-messagebox / -info / -warning / -error / -question
.z-messagebox-icon
.z-messagebox-content
.z-messagebox-buttons
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-messagebox` | background-color | rgb(255, 255, 255) |
| c2 | `.z-messagebox` | border-radius | 4px |
| c3 | `.z-messagebox` | box-shadow | level-3 modal shadow |
| c4 | `.z-messagebox-icon` | size | 24–32px |
| c5 | `.z-messagebox-info` `.z-messagebox-icon` | color | rgb(2, 136, 209) (info blue) |
| c6 | `.z-messagebox-warning` `.z-messagebox-icon` | color | rgb(237, 108, 2) |
| c7 | `.z-messagebox-error` `.z-messagebox-icon` | color | rgb(211, 47, 47) |
| c8 | `.z-messagebox-buttons` | display | flex, justify-content: flex-end |

## States to evaluate
- [ ] info, warning, error, question variants
