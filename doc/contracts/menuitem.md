# Component: menuitem
tier: T1
category: navigation
shared-css-file: zul/src/main/resources/web/js/zul/menu/css/menu.css
siblings: [menubar, menupopup]
preview: ${PREVIEW_URL}/menubar.zul

## References
Same as menubar.

## DOM key selectors
```
.z-menuitem / -content / -text / -image
.z-menuitem-icon                      ← check icon for checkable items
.z-menuitem-checkable
.z-menuitem-selected
.z-menuitem-checked
.z-menuitem[disabled]
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-menuitem-content` | padding | 8px 16px |
| c2 | `.z-menuitem-content` | font-size | 13–14px |
| c3 | `.z-menuitem-content:hover::before` | opacity | `var(--zk-state-hover-opacity)` (0.08); the `::before` layer is on-surface and rests at opacity 0 |
| c4 | `.z-menuitem-selected .z-menuitem-content` | background-color | secondary-container (list-row family) |
| c5 | `.z-menuitem-checked .z-menuitem-icon` | visible | yes, primary colour |
| c6 | `.z-menuitem[disabled]` | opacity | 0.38 |

## States to evaluate
- [ ] default, hover, selected, checked, disabled
