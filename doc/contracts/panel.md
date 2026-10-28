# Component: panel (theme design)
tier: T1
category: container
preview: ${PREVIEW_URL}/panel.zul
rules: see .claude/skills/zk-component-rules/components/panel.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Card.css
- DESIGN.md sections: §1, §5, §6, §7, §10, §11

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-panel` | background-color | rgb(255, 255, 255) |
| c2 | `.z-panel` | border-radius | 6px (card corner per DESIGN.md §5) |
| c3 | `.z-panel` | box-shadow | level-1 card shadow (DESIGN.md §6) |
| c4 | `.z-panel-header` | padding | 16px |
| c5 | `.z-panel-header` | font-size | 16px |
| c6 | `.z-panel-header` | font-weight | 500 |
| c7 | `.z-panelchildren` | padding | 16px 16px 24px (DESIGN.md §10 card content padding) |
| c8 | `.z-panel-drag-button` | display | `none` — must be invisible in all cases; the element is a JS drag-wire anchor only (DK parity; see skill `components/panel.md`) |
| c9 | `.z-panel-header` | box-sizing | `border-box` — min-height must INCLUDE padding, not add to it (guards the dropped-reset regression; see doc/skill-gaps.md 2026-06-16) |
| c10 | `.z-panel-header` | height | ≤ 68px — compact card header; a content-box blow-up reads ~80px. Bare title text must not wrap (`white-space: nowrap`), incl. narrow no-border panels |

## States to evaluate
- [ ] default, collapsed, with-shadow, no-border
- [ ] no-title panel (`.z-panel-noheader`): drag-button hidden (c8), body fills full height

> Not applicable: there is **no `3d` panel variant** — ZK `Panel` only registers the `default` mold in `lang.xml` (the `3d` mold belongs to Groupbox). `<panel mold="3d">` throws an HTTP 500 at compose time.
