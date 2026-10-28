# Component: label
tier: T3
category: label
preview: ${PREVIEW_URL}/label.zul
zk-version: 10.2.1-jakarta

## References
- Skill: `.claude/skills/zk-component-rules/components/label.md` (no css-uri → must bundle)
- Rule: `.claude/skills/zk-component-rules/reference/component-css-must-be-bundled-or-css-uri.md`
- MUI / Mira: MUI `Typography` (body text) — family/size/weight follow the `body-medium` typescale.

`Label` renders `<span class="z-label">`. It has **no `lang.xml` css-uri**, so its CSS is
loaded only via the global `norm.css.dsp` bundle (see gap log 2026-06-30 orphan sweep).

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| l0 | `.z-label` | (rule present in a LOADED stylesheet) | **own root rule served** — proves `label.css` is bundled into `norm.css.dsp` (regression guard for the 2026-06-30 orphan gap) |
| l1 | `.z-label` | color | `var(--zk-color-on-surface)` |
| l2 | `.z-label` | font-family | `var(--zk-typescale-font-family)` |
| l3 | `.z-label` | font-size / line-height | `body-medium` typescale tokens |
| l4 | `.z-label` | word-break | `break-word` |
| l5 | `.z-label` | (no `.z-label-pre` rule) | Label never adds that class; `pre="true"` is handled in the content by `zUtl.encodeXML` (`&nbsp;` / `<br>`), so it preserves whitespace only — it does not select a monospace font |
| l6 | `.z-label` | (no `.z-label-multiline` rule) | Label never adds that class; `multiline="true"` is handled in the content by `zUtl.encodeXML` (`<br>`), no class involved |

## States to evaluate
- [ ] **loading**: `.z-label` own rule comes from a served stylesheet (bundled, not an orphaned standalone `.dsp`)
- [ ] default inline label inherits surface text color on light surfaces
- [ ] `pre="true"` → whitespace preserved (NOT monospace — same as IceBlue)
- [ ] `multiline="true"` → line breaks preserved (mold-generated `<br>`)
