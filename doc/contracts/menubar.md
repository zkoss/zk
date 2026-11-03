# Component: menubar (theme design)
tier: T1
category: navigation
preview: ${PREVIEW_URL}/menubar.zul
rules: see .claude/skills/zk-component-rules/components/menubar.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Menu.css / AppBar.css
- `doc/spec/DESIGN.md` sections: "Surface Palette", "Typography", "Border Rules"

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-menubar` | background-color | `var(--zk-color-surface-container)` (MD3 Top App Bar / Menu Bar surface; override to `transparent` at use site if nested in already-tinted region) |
| c2 | `.z-menu-content` | padding | `var(--zk-spacing-2) var(--zk-spacing-4)` |
| c3 | `.z-menu-content` | font-size | `var(--zk-typescale-title-small-size)` |
| c4 | `.z-menu:hover .z-menu-content` | background-color | state-layer tint |
| c5 | `.z-menubar .z-menu-selected > .z-menu-content::before` | opacity | `var(--zk-state-pressed-opacity)` — the open state is an on-surface state layer (`menu.css:171-174`), not a selection fill |
| c6 | `.z-menubar .z-menu-selected > .z-menu-content::before` | background-color | `var(--zk-color-on-surface)` |
| c7 | `.z-menu-selected .z-menu-content` | background-color | MUST NOT be `--zk-color-primary-container` — wrong family; open state is a state layer, not a selection fill |
| c8 | `.z-menubar-scroll .z-menubar-icon` | color | `var(--zk-color-on-surface)` — inherits the bar's text color; MUST contrast with `--zk-color-surface-container`. MUST NOT be `--zk-color-on-primary` (white → invisible on the light bar) |
| c9 | `.z-menubar-scroll` | padding-left / padding-right | `0` — the bar's default `0 var(--zk-spacing-4)` gutter must reset when scrollable, else the abs-positioned arrows are inset by that gutter and float inside the bar instead of flush at its edges |
| c10 | `.z-menubar-left` (scrollable) | left | flush at the bar's border edge (arrow `getBoundingClientRect().x` == bar `.x`), not inset by the gutter |
| c11 | `.z-menubar-scrollable:hover` | background-color | `color-mix(in srgb, var(--zk-color-on-surface) 8%, var(--zk-color-surface-container))` — state-layer over the surface bar (NOT `on-primary`) |
| c12 | `.z-menubar` | padding | `0 var(--zk-spacing-4)` — leading/trailing gutter; conformant with MUI `Toolbar` (`padding-left:16px`) for an app-bar surface. The visible space before the first item is intentional gutter, not a bug |
| c13 | `.z-menubar-scroll .z-menubar-icon` | vertical position | centered in the arrow box (icon centerY == arrow centerY, ±2px). NOTE: ZK's `_fixScrolling` sets `style.display='block'` inline on the arrow, overriding any `display:flex` — so the icon MUST be centered via abs-positioning (or line-height), NOT via the arrow's flex `align-items` |

## States to evaluate
- [ ] default, hover, open/selected, disabled
- [ ] scrollable: scroll arrows visible (contrast) + flush at bar edges (`onCreate→setScrollable(true)` demo)
