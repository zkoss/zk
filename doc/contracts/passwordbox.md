# Component: passwordbox
tier: T1
category: input
shared-css-file: zul/src/main/resources/web/js/zul/inp/css/input.css
siblings: [textbox, intbox, decimalbox, doublebox, longbox, textarea]
preview: ${PREVIEW_URL}/textbox.zul   (passwordbox section on textbox preview)

## References
Same as textbox. Passwordbox uses `<input type="password" class="z-passwordbox">`.

## DOM key selectors
```
input.z-passwordbox
input.z-passwordbox.z-passwordbox-invalid
```

## Expected values
Same as textbox c1–c14 with selector → `.z-passwordbox`. Plus invalid.

## States to evaluate
- [ ] default, hover, focus-visible, disabled, readonly, invalid
