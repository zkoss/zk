# Component: messagebox
tier: T1
category: feedback
shared-css-file: zul/src/main/resources/web/js/zul/wnd/css/messagebox.css
siblings: []
preview: ${PREVIEW_URL}/messagebox.zul

## References
- MUI CSS: Dialog.css / Alert.css
- `doc/spec/DESIGN.md` sections: "Brand & Status Colors", "Elevation / Shadow", "Density", "Border Rules"

## DOM key selectors
```
.z-messagebox-window.z-window         ← root (background, radius, shadow)
.z-messagebox                         ← message text div
.z-messagebox-icon + -information / -exclamation / -error / -question
.z-messagebox-buttons
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-messagebox-window` | background-color | `var(--zk-messagebox-bg)` (surface) |
| c2 | `.z-messagebox-window` | border-radius | `var(--zk-messagebox-radius)` (dialog shape) |
| c3 | `.z-messagebox-window` | box-shadow | `var(--zk-messagebox-elevation)` (dialog elevation) |
| c4 | `.z-messagebox-icon` | size | 28px |
| c5 | `.z-messagebox-information` | color | `var(--zk-color-primary)` |
| c6 | `.z-messagebox-exclamation` | color | `var(--zk-color-warning)` |
| c7 | `.z-messagebox-error` | color | `var(--zk-color-error)` |
| c8 | `.z-messagebox-buttons` | display | flex, justify-content: flex-end |

## States to evaluate
- [ ] information, exclamation, error, question variants
