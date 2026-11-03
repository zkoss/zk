# Component: panel (theme design)
tier: T1
category: container
preview: ${PREVIEW_URL}/panel.zul
rules: see .claude/skills/zk-component-rules/components/panel.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Card.css
- `doc/spec/DESIGN.md` sections: "Surface Palette", "Corner Radii", "Elevation / Shadow", "Typography", "Density", "Border Rules"

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-panel` | background-color | `var(--zk-panel-bg)` |
| c2 | `.z-panel` | border-radius | `var(--zk-panel-radius)` (card corner per `doc/spec/DESIGN.md` "Corner Radii") |
| c3 | `.z-panel` | box-shadow | level-1 card shadow (`doc/spec/DESIGN.md` "Elevation / Shadow") |
| c4 | `.z-panel-header` | padding | `var(--zk-header-padding-y) var(--zk-spacing-4)` |
| c5 | `.z-panel-header` | font-size | `var(--zk-typescale-title-medium-size)` |
| c6 | `.z-panel-header` | font-weight | `var(--zk-typescale-title-medium-weight)` |
| c7 | `.z-panelchildren` | padding | `var(--zk-spacing-4) var(--zk-spacing-4) var(--zk-spacing-6)` (`doc/spec/DESIGN.md` "Density", card content padding) |
| c8 | `.z-panel-drag-button` | display | `none` — must be invisible in all cases; the element is a JS drag-wire anchor only (DK parity; see skill `components/panel.md`) |
| c9 | `.z-panel-header` | box-sizing | `border-box` — min-height must INCLUDE padding, not add to it (guards the dropped-reset regression) |
| c10 | `.z-panel-header` | height | ≤ 68px — compact card header; a content-box blow-up reads ~80px. Bare title text must not wrap (`white-space: nowrap`), incl. narrow no-border panels |

## States to evaluate
- [ ] default, collapsed, with-shadow, no-border
- [ ] no-title panel (`.z-panel-noheader`): drag-button hidden (c8), body fills full height

> Not applicable: there is **no `3d` panel variant** — ZK `Panel` only registers the `default` mold in `lang.xml` (the `3d` mold belongs to Groupbox). `<panel mold="3d">` throws an HTTP 500 at compose time.
