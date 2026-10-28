# Component: barcodescanner
tier: T2
category: media
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/barscanner/css/barcodescanner.css
siblings: []
preview: ${PREVIEW_URL}/barcodescanner.zul

## References
- DESIGN.md sections: §2, §3

## DOM key selectors
```
.z-barcodescanner       ← root container
.z-barcodescanner-video ← camera preview area
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-barcodescanner` | border | 1px solid rgba(0, 0, 0, 0.12) |
| c2 | `.z-barcodescanner` | border-radius | 4–8px |

## States to evaluate
- [ ] default scanner view
