# Component: loadingbar
tier: T2
category: feedback
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/loadingbar.css
siblings: []
closest-sibling: progressmeter
decomposition: LinearProgress (indeterminate variant) — a 2–4px sliding/gradient bar pinned to top of viewport
preview: ${PREVIEW_URL}/loadingbar.zul   (trigger long-running request from any page, e.g., paging change)

## References
- `doc/spec/DESIGN.md` sections: "Brand & Status Colors", "Motion"
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/wgt/css/progressmeter.css` — reuse track + filled-portion colours, then add the indeterminate animation
- MD3 spec for indeterminate linear progress: https://m3.material.io/components/progress-indicators/specs (only consult if other items don't answer the animation curve)
- Closest MUI: LinearProgress with `variant="indeterminate"`.

## DOM key selectors
```
div.z-loadingbar                                  ← bar root (Loadingbar.ts redraw); + .z-loadingbar-indeterminate when indeterminate
├─ span#<uuid>-img.z-loadingbar-image.z-loadingbar-colorbar
│                                                 ← determinate fill: inline width in % (set by _fixImgWidth);
│                                                   sliding part when indeterminate
└─ span.z-loadingbar-effectbar.z-loadingbar-colorbar
                                                  ← animated part for the indeterminate animation

Position wrapper (created by Loadingbar.getPositionWrapper_, not by redraw):
div#_z_loadingbarpositionwrapper.z-loadingbar-position-wrapper   ← appended to document.body
└─ div.z-loadingbar-position.z-loadingbar-position-<top|bottom>   ← one per pos; the .z-loadingbar root sits inside
   └─ div.z-loadingbar                                            ← (the tree above)
```
Note: `.z-loadingbar-position-top|bottom` is on the parent `.z-loadingbar-position` div, not on the `.z-loadingbar` root.

## Expected values (T2)
- Position fixed/absolute, top of viewport.
- Height 3px, full-width.
- Determinate fill: `var(--zk-color-primary)` on a `var(--zk-color-primary-container)` track.
- Indeterminate: two sliding parts, both `var(--zk-color-primary)`. `.z-loadingbar-image` runs `z-loadingbar-ani1` and `.z-loadingbar-effectbar` runs `z-loadingbar-ani2`; each is `3s` and `infinite` (`ease-in` and `ease` timing).

## States to evaluate
- [ ] visible loading, hidden when complete
