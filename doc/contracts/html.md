# Component: html
tier: T3
category: html
preview: ${PREVIEW_URL}/html.zul
zk-version: 10.2.1-jakarta

## References
- Skill: `.claude/skills/zk-component-rules/components/html.md` (no css-uri → must bundle)
- Rule: `.claude/skills/zk-component-rules/reference/component-css-must-be-bundled-or-css-uri.md`
- MUI / Mira: no direct analog — the embedded-markup normalization follows the theme typescale/shape tokens.

`Html` renders raw markup inside `<div class="z-html">`. It has **no `lang.xml` css-uri**, so
its CSS (including the embedded-tag normalization) loads only via the global `norm.css.dsp`
bundle (see gap log 2026-06-30 orphan sweep).

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| h0 | `.z-html` | (rule present in a LOADED stylesheet) | **own root rule served** — proves `html.css` is bundled into `norm.css.dsp` (regression guard for the 2026-06-30 orphan gap) |
| h1 | `.z-html` | display / color | `block` / `var(--zk-color-on-surface)` |
| h2 | `.z-html h1` | font-size | `var(--zk-typescale-headline-large-size)` (heading hierarchy normalized) |
| h3 | `.z-html a` | color | `var(--zk-color-primary)` (underline on hover) |
| h4 | `.z-html code` | font-family / background-color | mono / `var(--zk-color-surface-container)` |
| h5 | `.z-html th, .z-html td` | border | `1px solid var(--zk-color-outline-variant)` |

## States to evaluate
- [ ] **loading**: `.z-html` own rule comes from a served stylesheet (bundled, not an orphaned standalone `.dsp`)
- [ ] heading hierarchy h1–h4 sized via typescale
- [ ] links use primary color + hover underline
- [ ] code/pre on a surface-container background with corner radius
- [ ] table borders collapsed via outline-variant; `th` on surface-container-low
