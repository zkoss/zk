# Component: decimalbox
tier: T1
category: input
shared-css-file: zul/src/main/resources/web/js/zul/inp/css/input.css
siblings: [textbox, intbox, doublebox, longbox, textarea, passwordbox]
preview: ${PREVIEW_URL}/decimalbox.zul

## References
Same as intbox; decimalbox shares all rules via grouped selectors.

## DOM key selectors
```
input.z-decimalbox
input.z-decimalbox:hover / :focus / [readonly] / [disabled]
input.z-decimalbox.z-decimalbox-invalid
```

## Expected values
Same as textbox c1–c14 with selector → `.z-decimalbox`. Plus invalid:
- `.z-decimalbox.z-decimalbox-invalid` border-color = rgb(211, 47, 47)

## States to evaluate
- [ ] default, hover, focus-visible, disabled, readonly, invalid
