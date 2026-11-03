# Component: dropupload
tier: T2
category: media
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/wgt/css/dropupload.css
siblings: []
preview: ${PREVIEW_URL}/dropupload.zul

## References
- `doc/spec/DESIGN.md` sections: "Text Colors", "Brand & Status Colors", "Border Rules"

## DOM key selectors
```
.z-dropupload           ← root drop zone (only theme-stylable surface)
```

> The `.z-dropupload` root is the only stylable element (`Dropupload.ts`/`.java`). Drag
> feedback is the native cursor `dataTransfer.dropEffect='copy'`; the `detection` mode
> (`none`/`browser`/`self`/`<id>`) toggles content/component **visibility**, not
> container styling — the box looks identical in every mode. See
> `.claude/skills/zk-component-rules/components/dropupload.md`.

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-dropupload` | border | 2px dashed `var(--zk-dropupload-border-color)` |
| c2 | `.z-dropupload` | border-radius | `var(--zk-dropupload-radius)` |
| c3 | `.z-dropupload` | background-color | `var(--zk-dropupload-bg)` |

## States to evaluate
- [ ] default drop zone
