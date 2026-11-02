# Component: image
tier: T3
category: image
preview: ${PREVIEW_URL}/camera.zul
zk-version: 10.2.1-jakarta

## References
- Skill: `.claude/skills/zk-component-rules/components/image.md` (no css-uri → must bundle)
- Rule: `.claude/skills/zk-component-rules/reference/component-css-must-be-bundled-or-css-uri.md`
- MUI / Mira: MUI images use `extra-small` rounding for inline media.

`Image` renders `<span class="z-image"><img…></span>`. It has **no `lang.xml` css-uri**, so
its CSS loads only via the global `norm.css.dsp` bundle (see `.claude/skills/zk-component-rules/reference/component-css-must-be-bundled-or-css-uri.md`).

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| i0 | `.z-image` | (rule present in a LOADED stylesheet) | **own root rule served** — proves `image.css` is bundled into `norm.css.dsp` (regression guard against an orphaned standalone `.dsp`) |
| i1 | `.z-image` | border-radius | `var(--zk-shape-corner-extra-small)` |
| i2 | `.z-image` | display / max-width / vertical-align | `inline-block` / `100%` / `middle` |
| i3 | `.z-image img` | display / max-width / height | `block` / `100%` / `auto` (responsive) |

## States to evaluate
- [ ] **loading**: `.z-image` own rule comes from a served stylesheet (bundled, not an orphaned standalone `.dsp`)
- [ ] rendered image has extra-small corner rounding
- [ ] image scales down to its container (max-width 100%) without overflow
