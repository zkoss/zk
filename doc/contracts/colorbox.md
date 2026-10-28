# Component: colorbox (theme design)
tier: T2
category: input
preview: ${PREVIEW_URL}/colorbox.zul
rules: see .claude/skills/zk-component-rules/components/colorbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- Skill: `components/colorbox.md` (DOM structure, sprite-locked picker geometry)
- Skill: `reference/floating-popup-in-body.md` (popup detached to `<body>`)
- ZK source (LESS) for original geometry: `/Users/hawk/Documents/workspace/ZK10/zkcml/zkex/src/main/resources/web/js/zkex/inp/less/colorbox.less`
- DESIGN.md sections: §2, §3, §11

## Expected values — closed state

| id  | selector | property | expected |
|-----|----------|----------|----------|
| c1  | `.z-colorbox` | display | inline-flex |
| c2  | `.z-colorbox` | width | 32–40px (square-ish frame) |
| c3  | `.z-colorbox` | height | 32–36px |
| c4  | `.z-colorbox` | border | 1px solid `--zk-color-outline` |
| c5  | `.z-colorbox` | border-radius | `--zk-shape-input` (4px) |
| c6  | `.z-colorbox` | padding | 2–4px (inset around swatch + button) |
| c7  | `.z-colorbox:hover` | border-color | `--zk-color-on-surface` |
| c8  | `.z-colorbox-current` | flex / size | must fill remaining width — `flex: 1` (or `width: 100%; height: 100%` with absolute positioning). MUST NOT be inline-block with no size or it collapses |
| c9  | `.z-colorbox-current` | background-color | matches inline `style="background-color"` set by ZK (verify by reading inline style and comparing) |
| c10 | `.z-colorbox-current` | border-radius | 2px (subtle inset to differentiate from outer frame) |
| c11 | `.z-colorbox-button` | width | 14–18px |
| c12 | `.z-colorbox-button` | display | flex (center the icon) |
| c13 | `.z-colorbox-icon` | width / height | rendered chevron — must have computed `mask-image: url(...)` (NOT `none`). MUI / our theme uses chevron-down shape, NOT a filled caret triangle |
| c14 | `.z-colorbox-disabled` | opacity | 0.38 |

## Expected values — popup chrome (open state)

| id  | selector | property | expected |
|-----|----------|----------|----------|
| p1  | `.z-colorbox-popup` | **rendered width** | **matches ZK's inline width** (do NOT set `width`/`min-width: 100%`) |
| p2  | `.z-colorbox-popup` | border | 1px solid `--zk-color-outline-variant` |
| p3  | `.z-colorbox-popup` | border-radius | `--zk-shape-menu` |
| p4  | `.z-colorbox-popup` | box-shadow | `--zk-elevation-dropdown` |
| p5  | `.z-colorbox-popup` | background-color | `--zk-color-surface` |
| p6  | `.z-colorbox-paletteicon` | hover bg | tinted overlay |
| p7  | `.z-colorbox-pickericon` | hover bg | tinted overlay |
| p8  | `.z-colorpalette-color` | size | 12–16px (must remain compatible with original layout) |

**Sprite-locked picker internals**: `.z-colorpicker-gradient`, `.z-colorpicker-bar`, `.z-colorpicker-circle`, `.z-colorpicker-arrows`, `.z-colorpicker-rgb`, `.z-colorpicker-hsv`, `.z-colorpicker-hex` MUST keep the ZK-source geometry (sizes + absolute positions). Do not redesign — see `components/colorbox.md`.

## Expected values — menu-content mold (`<menu content="#color=…">`)

ZK can embed a colorbox in a `<menu>` (see `components/colorbox.md` → "Menu-content mold"). The mirror classes `.z-menu-popup` / `.z-menu-paletteicon` / `.z-menu-pickericon` / `.z-menu-image.z-colorbox-color` MUST be styled in lockstep with the standalone `.z-colorbox-*` chrome — otherwise the popup ships frameless. The shared `.z-colorpicker` / `.z-colorpalette` body already carries over (no zclass prefix).

| id  | selector | property | expected |
|-----|----------|----------|----------|
| m1  | `.z-menu-popup` | border / border-radius / box-shadow / background / padding | identical to `.z-colorbox-popup` (1px `--zk-color-outline-variant`, `--zk-shape-menu`, `--zk-elevation-dropdown`, `--zk-color-surface`, 8px) — must NOT be the browser-default frameless block |
| m2  | `.z-menu-paletteicon`, `.z-menu-pickericon` | size / radius / color / hover | identical to `.z-colorbox-paletteicon`/`-pickericon` (28×28, small-corner radius, on-surface-variant, 8% hover overlay) |
| m3  | `.z-menu-paletteicon::before`, `.z-menu-pickericon::before` | mask-image | grid / palette lucide glyph present (NOT `none`) — same as colorbox tab icons |
| m4  | `.z-colorpalette-popup .z-menu-paletteicon`, `.z-colorpicker-popup .z-menu-pickericon` | selected fill | `--zk-color-primary-container` bg + `--zk-color-on-primary-container` color |
| m5  | `.z-menu-image.z-colorbox-color` | border / border-radius / display | 1px `--zk-color-outline-variant`, 2px radius; `background-color` from ZK inline style. **Visible only when the colour menu is non-topmost** (nested in a `<menupopup>`) — ZK inline-hides the chip on a topmost menubar menu (`Menu.ts isTopmost()`); do not force it with `!important`. The theme's hide-blank-placeholder rule (`.z-menu-image[src*="R0lGODlhAQABAIAA"]`) MUST exclude `.z-colorbox-color` or the chip is hidden even when non-topmost |

**Verify the menu mold with a nested colour menu** (chip only renders here):
```xml
<menubar><menu label="Format"><menupopup>
    <menu label="Text Colour" content="#color=#184dc6"/>
    <menu label="Fill Colour" content="#color=#990000"/>
</menupopup></menu></menubar>
```

## States to evaluate
- [ ] default (closed, no color set) — swatch renders as black or theme default, chevron visible
- [ ] default (with color="#184dc6") — swatch shows the picked color
- [ ] hover — wrapper border darkens
- [ ] disabled — opacity reduced
- [ ] open (popup visible) — measure popup rendered width vs ZK inline width (must match, not full viewport)
- [ ] popup chrome — border, shadow, radius match MD3 surface tokens

## Common pitfalls
1. **Swatch collapse** — `.z-colorbox-current` is `<i>` (inline default). Without explicit width/height/flex, it collapses to near-zero and the user can't see the color. **Always** size it.
2. **Caret icon missing** — `z-icon-caret-down` has no global definition in this theme. If left unstyled, the `<i>` renders nothing. Set `--_icon` locally on `.z-colorbox-icon`.
3. **Popup width** — same trap as chosenbox; do not use percentage widths on `.z-colorbox-popup` (it's detached to body).
4. **Picker sprite geometry** — picker internals' sizes/positions are JS-coordinate-coupled. Restyle chrome only.
