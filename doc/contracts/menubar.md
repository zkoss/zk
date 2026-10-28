# Component: menubar (theme design)
tier: T1
category: navigation
preview: ${PREVIEW_URL}/menubar.zul
rules: see .claude/skills/zk-component-rules/components/menubar.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Menu.css / AppBar.css
- DESIGN.md sections: §1, §7, §11

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-menubar` | background-color | `var(--zk-color-surface-container)` (MD3 Top App Bar / Menu Bar surface; override to `transparent` at use site if nested in already-tinted region) |
| c2 | `.z-menu-content` | padding | 8px 16px |
| c3 | `.z-menu-content` | font-size | 14px |
| c4 | `.z-menu:hover .z-menu-content` | background-color | state-layer tint |
| c5 | `.z-menu-selected .z-menu-content` | background-color | `rgb(201, 213, 234)` (≈ `--zk-color-secondary-container`) — LIST-ROW family, see `reference/selected-state-families.md` |
| c6 | `.z-menu-selected .z-menu-content` | color | `rgb(21, 30, 45)` (≈ `--zk-color-on-secondary-container`) |
| c7 | `.z-menu-selected .z-menu-content` | background-color | MUST NOT be `rgb(178, 223, 219)` (= `--zk-color-secondary-container`) — wrong family |
| c8 | `.z-menubar-scroll .z-menubar-icon` | color | `var(--zk-color-on-surface)` — inherits the bar's text color; MUST contrast with `--zk-color-surface-container`. MUST NOT be `--zk-color-on-primary` (white → invisible on the light bar) |
| c9 | `.z-menubar-scroll` | padding-left / padding-right | `0` — the bar's default `0 16px` gutter must reset when scrollable, else the abs-positioned arrows are inset 16px and float inside the bar instead of flush at its edges |
| c10 | `.z-menubar-left` (scrollable) | left | flush at the bar's border edge (arrow `getBoundingClientRect().x` == bar `.x`), not inset by the gutter |
| c11 | `.z-menubar-scrollable:hover` | background-color | `color-mix(--zk-color-on-surface 8%, transparent)` — state-layer over the surface bar (NOT `on-primary`) |
| c12 | `.z-menubar` | padding | `0 16px` — leading/trailing gutter; conformant with MUI `Toolbar` (`padding-left:16px`) for an app-bar surface. The visible space before the first item is intentional gutter, not a bug |
| c13 | `.z-menubar-scroll .z-menubar-icon` | vertical position | centered in the arrow box (icon centerY == arrow centerY, ±2px). NOTE: ZK's `_fixScrolling` sets `style.display='block'` inline on the arrow, overriding any `display:flex` — so the icon MUST be centered via abs-positioning (or line-height), NOT via the arrow's flex `align-items` |

## States to evaluate
- [ ] default, hover, open/selected, disabled
- [ ] scrollable: scroll arrows visible (contrast) + flush at bar edges (`onCreate→setScrollable(true)` demo)
