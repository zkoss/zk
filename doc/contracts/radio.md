# Component: radio
tier: T1
category: selection
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/checkbox.css
siblings: [checkbox]
preview: ${PREVIEW_URL}/checkbox.zul   (radiogroup section)

## References
- MUI CSS: Radio.css
- DESIGN.md sections: §2, §3, §7, §8, §11

## Shared-selector warning
Same file as checkbox. Generator must isolate radio-only selectors when fixing.

## DOM key selectors
```
.z-radio / .z-radiogroup
.z-radio-input / .z-radio-mold / .z-radio-content
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| r1 | `.z-radio-mold` | width / height | 18–20px |
| r2 | `.z-radio-mold` | border-radius | 50% |
| r3 | `.z-radio-mold` | border | 2px solid rgba(0, 0, 0, 0.6) |
| r4 | `.z-radio-input:checked + .z-radio-mold` | inner-dot visible | yes, 8–10px rgb(55,111,208) |
| r5 | `.z-radio:hover` | state-layer | rgba(0,0,0,0.08) |
| r6 | `.z-radio-input:focus-visible + .z-radio-mold` | outline / shadow | focus ring |
| r7 | `.z-radio[disabled]` | opacity | 0.38 |

## States to evaluate
- [ ] unchecked, checked, hover, focus-visible, disabled
