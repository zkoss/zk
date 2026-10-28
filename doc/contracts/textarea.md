# Component: textarea
tier: T1
category: input
shared-css-file: zul/src/main/resources/web/js/zul/inp/css/input.css
siblings: [textbox, intbox, decimalbox, doublebox, longbox, passwordbox]
preview: ${PREVIEW_URL}/textbox.zul   (textarea section on the textbox preview)

## References
Same as textbox; textarea is the multi-line variant of textbox (uses `<textarea class="z-textbox">`).

## DOM key selectors
```
textarea.z-textbox                  ← multi-line root
textarea.z-textbox:hover / :focus / [readonly] / [disabled]
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| t1 | `textarea.z-textbox` | min-height | ≥ 64px (must be taller than single-line) |
| t2 | `textarea.z-textbox` | padding | not 0 (uses spacing-2 / spacing-3) |
| t3 | `textarea.z-textbox` | resize | vertical |
| t4 | `textarea.z-textbox` | font-family | starts-with "Inter" |
| t5 | `textarea.z-textbox` | font-size | 13px |
| t6 | `textarea.z-textbox` | border | 1px solid rgba(0, 0, 0, 0.23) |
| t7 | `textarea.z-textbox:focus` | border-color | rgb(55, 111, 208) |

## States to evaluate
- [ ] default, hover, focus-visible, disabled, readonly
