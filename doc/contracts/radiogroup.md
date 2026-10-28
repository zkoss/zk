# Component: radiogroup
tier: T1
category: selection
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/checkbox.css
siblings: [checkbox, radio]
preview: ${PREVIEW_URL}/radiogroup.zul

## References
- MUI CSS: Radio.css, RadioGroup.css
- DESIGN.md sections: §2, §3, §7, §8, §11

## Shared-selector warning
Same file as checkbox and radio. Generator must isolate radio-only selectors when fixing.

## DOM key selectors
```
.z-radiogroup           ← root container
.z-radio                ← individual radio item
.z-radio-input          ← hidden input
.z-radio-mold           ← visual circle
.z-radio-content        ← label text
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-radio-mold` | width / height | 18–20px |
| c2 | `.z-radio-mold` | border-radius | 50% |
| c3 | `.z-radio-mold` | border | 2px solid rgba(0, 0, 0, 0.6) |
| c4 | `.z-radio-input:checked + .z-radio-mold` | inner dot | 8–10px rgb(55,111,208) |
| c5 | `.z-radio-content` | font-size | 14px |
| c6 | `.z-radio[disabled]` | opacity | 0.38 |

## States to evaluate
- [ ] unchecked, checked, disabled
