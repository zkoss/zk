# Component: toolbarbutton
tier: T2
category: button
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/toolbarbutton.css
siblings: []
closest-sibling: button
decomposition: flat button (no resting elevation, transparent background, state-layer on hover) — the icon-button variant of button in toolbar context
preview: ${PREVIEW_URL}/toolbar.zul   (toolbarbuttons live inside toolbar)

## References
- DESIGN.md sections: §3, §7, §11
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/wgt/css/button.css` (then strip elevation/background; keep state-layer overlay)
- Nearest MUI: IconButton / Toolbar's children.

## DOM key selectors
```
.z-toolbarbutton
.z-toolbarbutton-content
.z-toolbarbutton-checked              ← toggled state
.z-toolbarbutton:hover / :focus / :active / [disabled]
```

## Expected values (T2)
- Flat (no resting elevation), transparent background.
- Hover: state-layer overlay rgba(0,0,0,0.08).
- Checked: primary container tint.
- Focus-visible: 2px ring matching primary.

## States to evaluate
- [ ] default, hover, focus-visible, active, checked, disabled
