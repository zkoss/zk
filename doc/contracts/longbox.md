# Component: longbox
tier: T1
category: input
shared-css-file: zul/src/main/resources/web/js/zul/inp/css/input.css
siblings: [textbox, intbox, decimalbox, doublebox, textarea, passwordbox]
preview: ${PREVIEW_URL}/longbox.zul

## References
Same as intbox.

## DOM key selectors
```
input.z-longbox
input.z-longbox.z-longbox-invalid
```

## Expected values
Same as textbox c1–c14 with selector → `.z-longbox`. Plus invalid border.

## States to evaluate
- [ ] default, hover, focus-visible, disabled, readonly, invalid
