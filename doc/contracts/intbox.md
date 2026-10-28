# Component: intbox
tier: T1
category: input
shared-css-file: zul/src/main/resources/web/js/zul/inp/css/input.css
siblings: [textbox, decimalbox, doublebox, longbox, textarea, passwordbox]
preview: ${PREVIEW_URL}/intbox.zul

## References
- See textbox contract for full check list; intbox shares all base rules
- MUI CSS: Inputs/OutlinedInput.css
- DESIGN.md sections: §1, §2, §5, §7, §9, §10, §11

## DOM key selectors
```
input.z-intbox
input.z-intbox:hover / :focus / [readonly] / [disabled]
input.z-intbox.z-intbox-invalid       ← numeric constraint violation
```

## Expected values
Same as textbox c1–c14 with `.z-textbox` → `.z-intbox`. Plus:

| id | selector | property | expected |
|----|----------|----------|----------|
| c15 | `.z-intbox.z-intbox-invalid` | border-color | rgb(211, 47, 47) |
| c16 | `.z-intbox.z-intbox-invalid:focus` | border-width | 2px |

## States to evaluate
- [ ] default, hover, focus-visible, disabled, readonly, invalid
