# Component: loadingbar
tier: T2
category: feedback
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/loadingbar.css
siblings: []
closest-sibling: progressmeter
decomposition: LinearProgress (indeterminate variant) — a 2–4px sliding/gradient bar pinned to top of viewport
preview: trigger long-running request from any page (e.g., paging change)

## References
- DESIGN.md sections: §3, §9
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/wgt/css/progressmeter.css` — reuse track + filled-portion colours, then add the indeterminate animation
- MD3 spec for indeterminate linear progress: https://m3.material.io/components/progress-indicators/specs (only consult if other items don't answer the animation curve)
- Closest MUI: LinearProgress with `variant="indeterminate"`.

## DOM key selectors
```
.z-loading                            ← absolute-positioned overlay bar
.z-loading-icon                       ← spinner glyph
.z-loading-message                    ← text label (optional)
```

## Expected values (T2)
- Position fixed/absolute, top of viewport.
- Height 2–4px, full-width.
- Animated gradient or sliding fill in primary colour.
- Animation duration: 1–2s loop.

## States to evaluate
- [ ] visible loading, hidden when complete
