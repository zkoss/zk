# Component: errorbox
tier: T2
category: feedback
shared-css-file: zul/src/main/resources/web/js/zul/inp/css/input.css
siblings: []
closest-sibling: notification
decomposition: tooltip surface (small floating callout with pointer caret) + error variant of notification (error-container bg + on-error-container text)
preview: trigger validation in any input preview that uses Constraint (e.g., ${PREVIEW_URL}/textbox.zul)

## References
- `doc/spec/DESIGN.md` sections: "Brand & Status Colors", "Elevation / Shadow", "Typography"
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/wgt/css/notification.css` (specifically the `-error` variant) — reuse the error-container colour + radius + shadow
- For the pointer/caret triangle: use a `::before` or `::after` pseudo-element with `border` trick; no MD3 spec for this — keep minimal.
- Closest MUI: FormHelperText (semantics) + Tooltip arrow (caret).

## DOM key selectors
```
.z-errorbox
.z-errorbox-pointer                   ← arrow caret
.z-errorbox-content                   ← message wrapper
.z-errorbox-close
```

## Expected values (T2)
- `.z-errorbox-content` background: `var(--zk-color-error-container)`.
- Color: `var(--zk-color-on-error-container)`.
- Padding: `8px 28px 8px 34px` — left room for the icon, right room for the close button.
- Border-radius: `var(--zk-shape-corner-extra-small)`.
- Font-size: body-small (`var(--zk-typescale-body-small-size)`).
- Box-shadow: level-2 (`var(--zk-elevation-2)`).

## States to evaluate
- [ ] visible on validation failure, with pointer caret, dismissible
- [ ] **position-invariant layout**: the icon→text gap (~12px) and close→content-edge gap (~4px) are identical whether the box lands to the right of, left of, above, or below the field — only the pointer direction differs. ZK's `Errorbox._fixarrow` writes a per-direction inline padding onto the outer `.z-errorbox`; the theme MUST override it with **symmetric** padding (`.z-errorbox { --zk-errorbox-beak: 8px; padding: var(--zk-errorbox-beak) !important }`) and re-add the beak to the icon/close offsets, or the absolutely-positioned icon/close drift 8px between positions. Do **not** zero the padding — it is load-bearing for the beak. Guard: `screenshot.spec.ts › errorbox-position-invariance`.
- [ ] **beak sits flush OUTSIDE the content**: the pointer triangle's base is flush against the content edge it points at, with the triangle entirely outside the content (beak intrusion ≤ 1px). Zeroing `.z-errorbox` padding pulls the beak ~8px INSIDE the content (arrow appears inside the box) — wrong. Guard: same test (`beakIntrusion ≤ 1`).
- [ ] **complete structure**: every errorbox carries the pointer beak, the warning icon, AND the close button — there is no no-arrow or no-close variant (the State Gallery shows the single canonical structure).
