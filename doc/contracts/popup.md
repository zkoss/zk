# Component: popup
tier: T1
category: container
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/popup.css
siblings: []
preview: ${PREVIEW_URL}/popup.zul

## References
- MUI CSS: Popover.css
- `doc/spec/DESIGN.md` sections: "Text Colors", "Brand & Status Colors", "Elevation / Shadow", "Density"

## DOM key selectors
```
.z-popup                ← root (positioned overlay; plain square box)
.z-popup-content        ← visible surface (border, radius, background, shadow)
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-popup-content` | background-color | `var(--zk-popup-bg)` |
| c2 | `.z-popup-content` | border-radius | `var(--zk-popup-radius)` |
| c3 | `.z-popup-content` | box-shadow | `var(--zk-popup-elevation)` |
| c4 | `.z-popup-content` | border | `1px solid var(--zk-popup-border-color)` |

## States to evaluate
- [ ] visible popup
