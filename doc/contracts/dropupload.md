# Component: dropupload
tier: T2
category: media
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/wgt/css/dropupload.css
siblings: []
preview: ${PREVIEW_URL}/dropupload.zul

## References
- DESIGN.md sections: §2, §3, §11

## DOM key selectors
```
.z-dropupload           ← root drop zone (only theme-stylable surface)
```

> ZK `Dropupload` emits **no** drag-over or disabled state class (verified in
> `Dropupload.ts`/`.java`). Drag feedback is the native cursor
> `dataTransfer.dropEffect='copy'`; the `detection` mode (`none`/`browser`/`self`/`<id>`)
> toggles content/component **visibility**, not container styling — the box looks
> identical in every mode. There is no `setDisabled`. See
> `.claude/skills/zk-component-rules/components/dropupload.md`.

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-dropupload` | border | 2px dashed rgba(0, 0, 0, 0.23) |
| c2 | `.z-dropupload` | border-radius | 4–8px |
| c3 | `.z-dropupload` | background-color | transparent or rgba(55,111,208,0.04) |

## States to evaluate
- [ ] default drop zone
