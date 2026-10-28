# Component: drawer
tier: T2
category: container
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/wgt/css/drawer.css
siblings: []
preview: ${PREVIEW_URL}/drawer.zul

## References
- MUI CSS: Drawer.css
- DESIGN.md sections: §2, §3, §6, §10

## DOM key selectors
```
.z-drawer                  ← root overlay container (position:fixed, full-viewport)
.z-drawer-mask             ← backdrop scrim (always rendered)
.z-drawer-mask-enabled     ← modifier added only when mask=true (default)
.z-drawer-real             ← the visible sliding panel (animate THIS)
.z-drawer-header           ← title band (display:none when title is empty)
.z-drawer-close            ← close icon button, sibling of header (only shown when closable=true)
.z-drawer-container        ← scroll wrapper
.z-drawer-cave             ← content padding box
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-drawer-real` | background-color | rgb(255, 255, 255) |
| c2 | `.z-drawer-real` | box-shadow | elevation-3+ shadow |
| c3 | `.z-drawer-open .z-drawer-mask.z-drawer-mask-enabled` | background-color | rgba(0, 0, 0, 0.32) (scrim) |
| c4 | `.z-drawer-header` | padding | 16–24px |
| c5 | `.z-drawer-header` | font-size | 16px or title-medium |
| c6 | `.z-drawer-open .z-drawer-mask:not(.z-drawer-mask-enabled)` | opacity | 0 (no scrim when mask=false) |
| c7 | `.z-drawer-close` | position / dimensions | absolute, 32×32, border-radius corner-full |
| c8 | `.z-drawer-close` | color | on-surface-variant |
| c9 | `.z-drawer-close > i.z-icon-times` | font (glyph) | visible × glyph, ~16px |

## Outcome assertions
| id | outcome |
|----|---------|
| M1 | On a `closable=true` drawer, `.z-drawer-close` sits at the top-trailing corner of `.z-drawer-real`: its bbox.right is within ~20px of the panel's right edge and its bbox.top is within the header band (≤ header height). |

## States to evaluate
- [ ] visible drawer with content (default, mask + no close button)
- [ ] mask="false" — backdrop scrim must NOT be visible (c6)
- [ ] closable="true" — close × styled at top-trailing corner (c7–c9, M1)
