# Component: fileupload
tier: T3
category: media
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/button.css
siblings: [button]
preview: ${PREVIEW_URL}/fileupload.zul

## Notes
fileupload renders as a styled button — no dedicated CSS.dsp found in ZK source. Uses button.css styling.
Evaluator should verify the fileupload button matches standard button appearance.

## DOM key selectors
```
.z-fileupload           ← root (button-like element)
.z-fileupload-cnt       ← inner content
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-fileupload` | border-radius | 4px (full-round or standard) |
| c2 | `.z-fileupload` | padding | 6–10px 16–24px |
| c3 | `.z-fileupload` | font-size | 14px |
| c4 | `.z-fileupload` | background-color | rgb(55, 111, 208) or surface (depends on variant) |

## States to evaluate
- [ ] default button appearance
