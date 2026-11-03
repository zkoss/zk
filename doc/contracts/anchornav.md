# Component: anchornav
tier: T2
category: navigation
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/nav/css/anchornav.css
siblings: [navbar]
preview: ${PREVIEW_URL}/anchornav.zul

## References
- doc/spec/DESIGN.md sections: "Text Colors", "Brand & Status Colors", "Typography", "Motion"

## DOM key selectors
```
.z-anchornav                     ← root
.z-anchornav .z-a                ← a link (also button.z-button as a link)
.z-anchornav-link-active         ← the link whose target is current; added by the widget after a click
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-anchornav a.z-a` | font-size | `var(--zk-typescale-label-large-size)` |
| c2 | `.z-anchornav a.z-a` | color | `var(--zk-anchornav-fg)` |
| c3 | `.z-anchornav a.z-anchornav-link-active` | color | `var(--zk-anchornav-accent)` |
| c4 | `.z-anchornav a.z-anchornav-link-active` | font-weight | 600 |
| c5 | `.z-anchornav button.z-anchornav-link-active` | outline | 2px solid `var(--zk-anchornav-accent)`, unless keyboard-focused |

WCAG 2.5.8 target: the `:where(a[data-anchornav-target])` rule (inline-flex, min 24×24) lives in za11y `zkmax/css/_nav-a11y.css` (zkcml repo) and is loaded only when za11y is active; without za11y the links are not enlarged.

## States to evaluate
- [ ] default, active/highlighted (a link and button link)
