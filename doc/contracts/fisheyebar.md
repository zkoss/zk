# Component: fisheyebar
tier: T2
category: navigation
shared-css-file: ../zkcml/zkex/src/main/resources/web/js/zkex/menu/css/fisheye.css
siblings: []
preview: ${PREVIEW_URL}/fisheyebar.zul

## References
- DESIGN.md sections: §2, §3, §9

## DOM key selectors
```
.z-fisheyebar           ← root container
.z-fisheye              ← individual item
.z-fisheye-img          ← item icon/image
.z-fisheye-text         ← item label
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-fisheyebar` | background-color | var(--zk-color-surface) or transparent |
| c2 | `.z-fisheye` | width / height | set inline by the widget on mousemove (magnification); no CSS transform and no transition, so left/top and size change together |
| c3 | `.z-fisheye-text` | font-size | `var(--zk-typescale-label-small-size)` (11px), the same tooltip chip as `.z-slider-popup` |
| c4 | `.z-fisheye`, `.z-fisheye-image`, `.z-fisheye-text` | position | absolute (the widget sets inline left/top); the label is `display: none` until the widget shows it |
| c5 | `.z-fisheye-text` | background-color / color | `var(--zk-color-inverse-surface)` / `var(--zk-fisheye-fg)` (default `inverse-on-surface`) |

## States to evaluate
- [ ] default, hover magnification
