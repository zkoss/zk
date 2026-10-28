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
| c1 | `.z-menupopup` | box-shadow | level-2 dropdown (DESIGN.md §6) |
| c2 | `.z-menupopup` | border-radius | 4px |
| c3 | `.z-menupopup` | background-color | rgb(255, 255, 255) |
| c4 | `.z-menupopup` | padding | 4–8px vertical |
| c5 | `.z-menuseparator` | border-top | 1px solid rgba(0, 0, 0, 0.12) |
| c6 | `.z-menuseparator` | margin | 4px 0 |

## States to evaluate
- [ ] closed, open, with items, with separators
