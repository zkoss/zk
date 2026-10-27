# Component: anchornav
tier: T2
category: navigation
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/nav/css/anchornav.css
siblings: [navbar]
preview: ${PREVIEW_URL}/anchornav.zul

## References
- DESIGN.md sections: §2, §3, §7, §9

## DOM key selectors
```
.z-anchornav            ← root
.z-anchornav-item       ← individual anchor link
.z-anchornav-item-active ← currently active/highlighted item
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-anchornav-item` | font-size | 14px |
| c2 | `.z-anchornav-item` | color | rgba(0, 0, 0, 0.6) |
| c3 | `.z-anchornav-item-active` | color | rgb(55, 111, 208) |
| c4 | `.z-anchornav-item-active` | border-left or indicator | 2–4px solid rgb(55, 111, 208) |

WCAG 2.5.8 target: the `:where(a[data-anchornav-target])` rule (inline-flex, min 24×24) lives in za11y `zkmax/css/_nav-a11y.css` (zkcml repo) and is loaded only when za11y is active; without za11y the links are not enlarged.

## States to evaluate
- [ ] default, active/highlighted
