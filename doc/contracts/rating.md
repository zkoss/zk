# Component: rating (theme design)
tier: T1
category: input
preview: ${PREVIEW_URL}/rating.zul
rules: see .claude/skills/zk-component-rules/components/rating.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Rating.css
- DESIGN.md sections: §3, §12

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-rating-icon` | width / height | 20–24px |
| c2 | `.z-rating-icon` | color | rgba(0, 0, 0, 0.38) (empty) |
| c3 | `.z-rating-icon-checked` | color | rgb(237, 108, 2) (warning amber) or primary, per DESIGN.md §3 |
| c4 | `.z-rating-icon:hover` | color | hover tint of c3 |
| c5 | `.z-rating` | gap | ≥ 2px between stars |

## States to evaluate
- [ ] default (empty), hover, checked (filled), readonly, disabled

## iconSclass picks the glyph (ZK-6112)
The icon element is `<i class="z-rating-icon z-icon-… ">`. Marble draws only the **default** `z-icon-star` itself
(SVG mask on the element, `::before` off). Any other `iconSclass` keeps the theme's icon (`[class*=" z-icon-"]::before`
mask, Lucide), so it shows its own glyph. Selected / hover on a custom icon change **colour only**
(`--zk-rating-accent`); there is no outline→filled swap because icon sets have no common filled/outline naming.

| id | selector | property | expected |
|----|----------|----------|----------|
| i1 | `.z-rating-icon.z-icon-star` | mask-image | SVG star mask; `::before` `content: none` |
| i2 | `.z-rating-icon:not(.z-icon-star)` | mask-image / `::before` | element has **no** mask; `::before` shows the icon (`content` ≠ `none`) |
| i3 | `.z-rating-icon.z-rating-selected` | color | `--zk-rating-accent`, for every icon |

Guarded by `screenshot.spec.ts › rating › iconSclass picks the glyph; the star mask is only the default`.
In forced-colors the star fill is re-pointed to `CanvasText` only for `.z-icon-star` (`_forced-colors.css` 2d);
a custom icon is handled by the generic `z-icon-*::before` rule.
