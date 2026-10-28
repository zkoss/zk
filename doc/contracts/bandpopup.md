# Component: bandpopup
tier: T2
category: container
shared-css-file: zul/src/main/resources/web/js/zul/wnd/css/bandpopup.css
siblings: []
closest-sibling: menupopup
decomposition: card surface (background + radius + elevation) — same as menu popup container, no inner padding
preview: ${PREVIEW_URL}/bandbox.zul   (open bandbox popup)

## References
- DESIGN.md sections: §5, §6
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/menu/css/menu.css` (the `.z-menupopup` block) — reuse box-shadow, border-radius, background
- Closest MUI: Popover surface.

## DOM key selectors
```
.z-bandpopup
.z-bandpopup-content
```

## Expected values (T2)
- Background: rgb(255, 255, 255).
- Border-radius: 4–6px.
- Box-shadow: level-2 dropdown shadow.
- Padding: 0 (content provides its own padding).

## States to evaluate
- [ ] hidden, visible
