# Component: barcode
tier: T3
category: media
shared-css-file: ../zkcml/zkex/src/main/resources/web/js/zkex/barcode/css/barcode.css
siblings: []
preview: ${PREVIEW_URL}/barcode.zul

## Notes
No barcode.css.dsp found in ZK source. Barcode renders as SVG/canvas inside a wrapper div.
Evaluator should check if a CSS file is served; if not, flag as T2 (needs own file at presumed path).

## DOM key selectors
```
.z-barcode              ← root wrapper
.z-barcode canvas, .z-barcode svg  ← rendered barcode
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-barcode` | display | inline-block or block |
| c2 | `.z-barcode` | background-color | rgb(255, 255, 255) |

## States to evaluate
- [ ] default barcode rendered
