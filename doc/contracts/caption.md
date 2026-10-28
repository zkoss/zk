# Component: caption
tier: T1
category: container
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/caption.css
siblings: []
preview: ${PREVIEW_URL}/caption.zul

## References
- DESIGN.md sections: §2, §3, §7

## DOM key selectors
```
.z-caption              ← root
.z-caption-content      ← inner wrapper
.z-caption-text         ← text label
.z-caption-image        ← optional icon/image
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-caption-text` | font-size | 14px or var(--zk-typescale-body-medium-size) |
| c2 | `.z-caption-text` | color | var(--zk-color-on-surface) |
| c3 | `.z-caption-image` | width | 16–20px |

## States to evaluate
- [ ] with image+label, label only, image only
