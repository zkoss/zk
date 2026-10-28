# Component: doublebox
tier: T1
category: input
shared-css-file: zul/src/main/resources/web/js/zul/inp/css/input.css
siblings: [textbox, intbox, decimalbox, longbox, textarea, passwordbox]
preview: ${PREVIEW_URL}/doublebox.zul

## References
Same as intbox.

## DOM key selectors
```
input.z-doublebox
input.z-doublebox.z-doublebox-invalid
```

## Expected values
Same as textbox c1–c14 with selector → `.z-doublebox`. Plus invalid:
- `.z-doublebox.z-doublebox-invalid` border-color = rgb(211, 47, 47)

## States to evaluate
- [ ] default, hover, focus-visible, disabled, readonly, invalid
