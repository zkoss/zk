# Component: camera
tier: T2
category: media
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/med/css/camera.css
siblings: []
preview: ${PREVIEW_URL}/camera.zul

## References
- DESIGN.md sections: §2, §3

## DOM key selectors
```
.z-camera               ← root container
.z-camera-video         ← live preview video element
.z-camera-toolbar       ← capture controls toolbar
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-camera` | border | 1px solid rgba(0, 0, 0, 0.12) |
| c2 | `.z-camera` | border-radius | 4–8px |
| c3 | `.z-camera-toolbar` | background-color | var(--zk-color-surface) |

## States to evaluate
- [ ] default camera view
