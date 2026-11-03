# Component: menupopup
tier: T1
category: navigation
shared-css-file: zul/src/main/resources/web/js/zul/menu/css/menu.css
siblings: [menubar, menuitem]
preview: ${PREVIEW_URL}/menubar.zul   (open a top-level menu)

## References
Same as menubar.

## DOM key selectors
```
.z-menupopup
.z-menupopup-content                  ← <ul>
.z-menuseparator                      ← <li> separator
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-menupopup` | box-shadow | `var(--zk-elevation-dropdown)` (`doc/spec/DESIGN.md` "Elevation / Shadow") |
| c2 | `.z-menupopup` | border-radius | `var(--zk-menupopup-radius)` |
| c3 | `.z-menupopup` | background-color | `var(--zk-menupopup-bg)` |
| c4 | `.z-menupopup` | padding | 4–8px vertical |
| c5 | `.z-menuseparator` | border-top | 1px solid `var(--zk-color-outline-variant)` |
| c6 | `.z-menuseparator` | margin | `var(--zk-spacing-1) 0` |

## States to evaluate
- [ ] closed, open, with items, with separators
