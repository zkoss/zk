# Component: doublespinner
tier: T1
category: input
shared-css-file: zul/src/main/resources/web/js/zul/inp/css/spinner.css
siblings: [spinner]
preview: ${PREVIEW_URL}/spinner.zul   (doublespinner section if present)

## References
Same as spinner.

## DOM key selectors
```
.z-doublespinner / .z-doublespinner-input / .z-doublespinner-button
```

## Expected values
Identical to spinner check set, selectors → `.z-doublespinner-*`.

## States to evaluate
- [ ] default, hover, focus, disabled, readonly, invalid
- [ ] inplace (see `reference/inplace-state.md`)
- [ ] buttonVisible-false (up/down arrow stack hidden — see `reference/buttonVisible-attribute.md`; verify `.z-doublespinner-button.z-doublespinner-disabled` has `display: none`)
