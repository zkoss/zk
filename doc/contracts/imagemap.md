# Component: imagemap
tier: T1
category: layout
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/imagemap.css
siblings: []
preview: ${PREVIEW_URL}/imagemap.zul

## References
- DESIGN.md sections: §2
- Skill: `.claude/skills/zk-component-rules/components/imagemap.md` (no css-uri → must bundle)
- Rule: `.claude/skills/zk-component-rules/reference/component-css-must-be-bundled-or-css-uri.md`

`Imagemap` has **no `lang.xml` css-uri**, so `imagemap.css` loads only via the global
`norm.css.dsp` bundle (gap log 2026-06-30 orphan sweep — it was previously orphaned).

## DOM key selectors
```
.z-imagemap             ← root wrapper
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c0 | `.z-imagemap` | (rule present in a LOADED stylesheet) | **own root rule served** — proves `imagemap.css` is bundled into `norm.css.dsp` (regression guard for the 2026-06-30 orphan gap) |
| c1 | `.z-imagemap` | display | inline-block or block |
| c2 | `.z-imagemap` | border | none (no extra border) |
| c3 | `.z-imagemap` | position | relative (positioning context the clickable area overlays require) |

## States to evaluate
- [ ] default
- [ ] **loading**: `.z-imagemap` own rule comes from a served stylesheet (bundled, not an orphaned standalone `.dsp`)
