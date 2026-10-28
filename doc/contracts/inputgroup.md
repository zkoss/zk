# Component: inputgroup
tier: T2
category: input
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/inputgroup.css
siblings: []
closest-sibling: textbox
decomposition: textbox (centre input) + prefix/suffix adornments (text or icon)
preview: ${PREVIEW_URL}/inputgroup.zul

## References
- DESIGN.md sections: §1, §5, §7, §11
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/inp/css/input.css`
- MUI reference for adornments: `/Users/hawk/.../static-css-output/Inputs/InputAdornment.css`
- Close to MUI InputAdornment pattern.

## DOM key selectors
```
.z-inputgroup                      ← wrapper
.z-inputgroup-content
.z-inputgroup-prefix / -suffix     ← adornments (icons/text)
```

## Expected values (T2 — DESIGN.md tokens only)
- Border / radius / focus state should match other inputs (combobox/textbox).
- Adornment text uses on-surface-variant colour.

## States to evaluate
- [ ] default, hover, focus, disabled, sibling-coherence (compare to textbox)
- [ ] **focus-no-layout-shift**: `.z-inputgroup` bbox dimensions when an inner
      input is `:focus-within` **==** dimensions at rest (±0px), for every
      variant (suffix / both-sides / input+button / vertical / multiline). The
      single-input Mechanism-B `:focus` rule (`input.css`) leaks its **padding
      compensation** (`0 calc(spacing-3 - 1px)`) onto grouped children — the
      inputgroup base rule wins on the *border* (stays 1px, equal specificity,
      loads later) but NOT the padding, so the child shrinks ~2px with no border
      growth to offset it and the shrink-to-fit group jumps. Inside a group,
      children must keep constant 1px border AND constant rest padding; the
      group's `:focus-within` outline owns the affordance (see skill
      `reference/focus-affordance-no-layout-shift.md`).
- [ ] **vertical-border**: in `orient="vertical"`, the centre `.z-textbox` must
      keep BOTH left and right borders (1px outline colour); only its
      `border-top` is collapsed against the preceding addon. A textbox with
      `border-left-width: 0` in vertical mode is a fail (horizontal collapse
      rule leaking — see skill "Border-collapse is axis-aware").

- [ ] **addon-edge (ZK-6112)**: an addon and its neighbouring input share ONE 1px edge on either side
      (leading *or* trailing addon): `right border of the first + left border of the second == 1px` in
      horizontal mode. Covers `.z-textbox`, `.z-combobox` and the number boxes (`.z-intbox`,
      `.z-longbox`, `.z-doublebox`, `.z-decimalbox`). Before the fix only a leading addon was merged, so a
      trailing addon showed a 2px seam.
- [ ] **number-boxes**: inside a group the four number boxes get the textbox treatment: 1px group-border
      colour, `flex: 1`, `min-width: 0`, `min-height: var(--zk-control-height)`, constant border/padding on
      focus. Guarded by `screenshot.spec.ts › inputgroup › addon and input share a single 1px edge`.
