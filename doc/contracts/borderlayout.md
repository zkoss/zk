# Component: borderlayout (theme design)
tier: T1
category: layout
preview: ${PREVIEW_URL}/borderlayout.zul
rules: see .claude/skills/zk-component-rules/components/borderlayout.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zul/layout/Borderlayout.ts
  - zul/layout/Center.ts
  - zul/layout/East.ts
  - zul/layout/LayoutRegion.ts
  - zul/layout/North.ts
  - zul/layout/South.ts
  - zul/layout/West.ts
js-source-hash: cb10172254bb9eb2ee7fdf1cd7cd67112b6bec86ad2cd445e71107e6c564a1e3
closest-sibling: none — novel composite pattern

## References

- MUI CSS: no direct analog — closest reference is `Navigation/Drawer.css` (sidebar surface + 1px outline-variant divider pattern) and `Layout/Container.css` (surface background).
- doc/spec/DESIGN.md sections: "Surface Palette", "Brand & Status Colors", "Spacing Scale (4dp baseline)", "Motion", "Splitter Family (unified spec)"
- Iceblue baseline: doc/contracts/baselines/borderlayout-iceblue.png
- HTML contract: doc/contracts/borderlayout.html

## Design Contract

The shipped CSS is the contract for the splitter rows, including the pill design (strip `--zk-splitter-thickness`, pill `--zk-splitter-thickness` × `--zk-splitter-pill-length` in outline-variant, grips `--zk-splitter-icon-size`, no elevation). The splitter spec lives in doc/spec/DESIGN.md "Splitter Family (unified spec)".

The borderlayout uses the MD3 surface elevation system to signal region hierarchy visually. North and south regions are primary content containers and sit on `--zk-color-surface` (white) with a 1px `--zk-color-outline-variant` divider on the inside edge separating them from the center. West and east sidebar regions use the slightly-toned `--zk-color-surface-container-low` to read as secondary panels, also separated by a 1px `--zk-color-outline-variant` border on their inner edge. The center region uses `--zk-color-background` (the lightest tonal surface) to register as the main workspace. Splitter bars follow the unified **splitter family** (doc/spec/DESIGN.md "Splitter Family (unified spec)"): **`--zk-splitter-thickness`-wide `--zk-color-surface-container` tonal strips at idle** that tint with the family's primary hover mix (`color-mix(primary at hover-opacity, surface-container)`) when the cursor enters. Each splitter carries the family's **actuator pill** (`--zk-splitter-thickness` × `--zk-splitter-pill-length` for west/east, the reverse for north/south) with `--zk-shape-corner-full` radius and `--zk-color-outline-variant` fill — no border, no elevation; the spec composes the MD3 bottom-sheet drag handle with a surface-tinted divider. On hover the pill fills `--zk-color-primary` with `on-primary` icon color (still no elevation lift, and **no size growth** — it keeps its `--zk-splitter-pill-length` long-axis; MD3 signals hover via the state-layer colour, not geometry, so the strip is the actuator, not a button popping off it). The pill's grip ellipsis icons render at **`--zk-splitter-icon-size`, full opacity** so the pill reads as a draggable handle; the **collapse caret is hidden at idle and fades in on hover** to communicate "click to collapse" without crowding the idle state. The pill is centered on the bar's long axis by ZK JS (`setBtnPos_` inline margin) — CSS must not add its own centering on that axis (see the skill file). When a region is collapsed, it leaves a `--zk-color-surface-container` placeholder strip with a 1px outline-variant border; hover shifts it to `--zk-color-surface-container-high`. Region headers (when `title` is set) are 40px tall, `--zk-color-surface-container` background, `--zk-typescale-title-small-size` typography, and `--zk-color-on-surface-variant` text. All splitter color transitions use `--zk-motion-duration-standard` with `--zk-motion-easing-standard`; the CSS reads it through `--zk-splitter-transition` (`tokens/_splitter.css`).

## Expected values

| id  | selector                                          | property          | expected (token preferred)                                                              | source            |
|-----|---------------------------------------------------|-------------------|-----------------------------------------------------------------------------------------|-------------------|
| c1  | `.z-borderlayout`                                 | background-color  | transparent                                                                             | doc/spec/DESIGN.md "Surface Palette" |
| c2  | `.z-borderlayout`                                 | position          | relative                                                                                | structural        |
| c3  | `.z-north`                                        | background-color  | `var(--zk-color-surface)`                                                               | doc/spec/DESIGN.md "Surface Palette" |
| c4  | `.z-north`                                        | border-bottom     | 1px solid `var(--zk-color-outline-variant)`                                             | MUI Drawer analog |
| c5  | `.z-south`                                        | background-color  | `var(--zk-color-surface)`                                                               | doc/spec/DESIGN.md "Surface Palette" |
| c6  | `.z-south`                                        | border-top        | 1px solid `var(--zk-color-outline-variant)`                                             | MUI Drawer analog |
| c7  | `.z-west`                                         | background-color  | `var(--zk-color-surface-container-low)`                                                 | doc/spec/DESIGN.md "Surface Palette" |
| c8  | `.z-west`                                         | border-right      | 1px solid `var(--zk-color-outline-variant)`                                             | MUI Drawer analog |
| c9  | `.z-east`                                         | background-color  | `var(--zk-color-surface-container-low)`                                                 | doc/spec/DESIGN.md "Surface Palette" |
| c10 | `.z-east`                                         | border-left       | 1px solid `var(--zk-color-outline-variant)`                                             | MUI Drawer analog |
| c11 | `.z-center`                                       | background-color  | `var(--zk-color-background)`                                                            | doc/spec/DESIGN.md "Surface Palette" |
| c12 | `.z-west-splitter, .z-east-splitter`              | width             | `var(--zk-splitter-thickness)`                                                          | doc/spec/DESIGN.md "Splitter Family (unified spec)" — family bar |
| c13 | `.z-north-splitter, .z-south-splitter`            | height            | `var(--zk-splitter-thickness)`                                                          | doc/spec/DESIGN.md "Splitter Family (unified spec)" — family bar |
| c14 | `.z-west-splitter, .z-east-splitter, .z-north-splitter, .z-south-splitter` | background-color | `var(--zk-color-surface-container)`                      | doc/spec/DESIGN.md "Splitter Family (unified spec)" — family idle bar |
| c15 | (retired — splitter bar is a tonal strip at idle; no opacity)   | —                 | —                                                                                       | —                 |
| c16 | (retired — no opacity transition)                 | —                 | —                                                                                       | —                 |
| c17 | `*-splitter:hover`                                | background-color  | `color-mix(in srgb, var(--zk-color-primary) calc(var(--zk-state-hover-opacity) * 100%), var(--zk-color-surface-container))` | doc/spec/DESIGN.md "Splitter Family (unified spec)" — family hover tint |
| c18 | `*-splitter`                                      | transition        | background-color `var(--zk-motion-duration-standard)` `var(--zk-motion-easing-standard)` (CSS reads `--zk-splitter-transition`) | doc/spec/DESIGN.md "Motion" |
| c19 | `.z-west-splitter-button, .z-east-splitter-button` | width            | `var(--zk-splitter-thickness)`                                                          | doc/spec/DESIGN.md "Splitter Family (unified spec)" — pill |
| c20 | `.z-west-splitter-button, .z-east-splitter-button` | height           | `var(--zk-splitter-pill-length)`                                                        | doc/spec/DESIGN.md "Splitter Family (unified spec)" — pill |
| c21 | `.z-north-splitter-button, .z-south-splitter-button` | width          | `var(--zk-splitter-pill-length)`                                                        | doc/spec/DESIGN.md "Splitter Family (unified spec)" — pill |
| c22 | `.z-north-splitter-button, .z-south-splitter-button` | height         | `var(--zk-splitter-thickness)`                                                          | doc/spec/DESIGN.md "Splitter Family (unified spec)" — pill |
| c23 | `*-splitter-button`                               | background-color  | `var(--zk-color-outline-variant)`                                                       | doc/spec/DESIGN.md "Splitter Family (unified spec)" — pill idle fill |
| c24 | `*-splitter-button`                               | border            | none                                                                                    | doc/spec/DESIGN.md "Splitter Family (unified spec)" |
| c25 | `*-splitter-button`                               | border-radius     | `var(--zk-shape-corner-full)`                                                           | doc/spec/DESIGN.md "Splitter Family (unified spec)" |
| c26 | `*-splitter-button`                               | box-shadow        | none                                                                                    | doc/spec/DESIGN.md "Splitter Family (unified spec)" — no elevation on the pill |
| c27 | `*-splitter-button`                               | color             | `var(--zk-color-on-surface-variant)`                                                    | doc/spec/DESIGN.md "Splitter Family (unified spec)" |
| c28 | `*-splitter-button:hover`                         | background-color  | `var(--zk-color-primary)`                                                               | doc/spec/DESIGN.md "Splitter Family (unified spec)" |
| c29 | `*-splitter-button:hover`                         | color             | `var(--zk-color-on-primary)`                                                            | doc/spec/DESIGN.md "Splitter Family (unified spec)" |
| c29b| (retired — no elevation lift on hover; the strip is the actuator, not a button popping off it) | — | —                                                       | —                 |
| c29c| `.z-west-splitter:hover .z-west-splitter-button`  | height            | `var(--zk-splitter-pill-length)`                                                        | pill does NOT grow on hover; MD3 signals hover via colour, not geometry (caret already has its space at idle) |
| c29d| `.z-north-splitter:hover .z-north-splitter-button`| width             | `var(--zk-splitter-pill-length)`                                                        | pill does NOT grow on hover; matches splitter/splitlayout |
| c30 | `.z-west-icon.z-icon-ellipsis-v, .z-east-icon.z-icon-ellipsis-v, .z-north-icon.z-icon-ellipsis-h, .z-south-icon.z-icon-ellipsis-h` | opacity | 1 (grip dots must be fully visible — they signal both drag-handle and resize affordance) | doc/spec/DESIGN.md "Splitter Family (unified spec)" |
| c30b| `.z-west-icon, .z-east-icon, .z-north-icon, .z-south-icon` | font-size | `var(--zk-splitter-icon-size)` (grip glyphs)                                            | doc/spec/DESIGN.md "Splitter Family (unified spec)" |
| c30c| `.z-west-icon[class*="z-icon-caret"], .z-east-icon[class*="z-icon-caret"], .z-north-icon[class*="z-icon-caret"], .z-south-icon[class*="z-icon-caret"]` | opacity | 0 at rest; 1 on `*-splitter-button:hover` | doc/spec/DESIGN.md "Splitter Family (unified spec)" — caret hidden at idle, fades in on hover |
| c31 | `*-splitter-button-disabled .z-icon-caret-*`      | display           | none                                                                                    | structural        |
| c32 | `.z-north-collapsed, .z-south-collapsed, .z-west-collapsed, .z-east-collapsed` | background-color | `var(--zk-color-surface-container)`                   | doc/spec/DESIGN.md "Surface Palette" |
| c33 | `*-collapsed`                                     | border            | 1px solid `var(--zk-color-outline-variant)`                                             | doc/spec/DESIGN.md "Border Rules" |
| c34 | `*-collapsed:hover`                               | background-color  | `var(--zk-color-surface-container-high)`                                                | doc/spec/DESIGN.md "Surface Palette" |
| c35 | `.z-north-header, .z-south-header, .z-west-header, .z-east-header, .z-center-header` | height | 40px                                                      | doc/spec/DESIGN.md "Spacing Scale (4dp baseline)" |
| c36 | `*-header`                                        | background-color  | `var(--zk-color-surface-container)`                                                     | doc/spec/DESIGN.md "Surface Palette" |
| c37 | `*-header`                                        | border-bottom     | 1px solid `var(--zk-color-outline-variant)`                                             | doc/spec/DESIGN.md "Border Rules" |
| c38 | `*-header`                                        | font-size         | `var(--zk-typescale-title-small-size)`                                                  | doc/spec/DESIGN.md "Typography" |
| c39 | `*-header`                                        | font-weight       | `var(--zk-typescale-title-small-weight)`                                                | doc/spec/DESIGN.md "Typography" |
| c40 | `*-header`                                        | color             | `var(--zk-color-on-surface-variant)`                                                    | doc/spec/DESIGN.md "Text Colors" |
| c41 | `.z-splitter-ghost`                               | background-color  | `var(--zk-color-primary)`                                                               | doc/spec/DESIGN.md "Brand & Status Colors" |
| c42 | `.z-splitter-ghost`                               | opacity           | 0.3                                                                                     | `zul/src/main/resources/web/js/zul/layout/css/borderlayout.css` |
| c43 | `.z-west-splitter, .z-east-splitter`              | cursor            | `col-resize`                                                                            | doc/spec/DESIGN.md "Splitter Family (unified spec)" — family cursor |
| c44 | `.z-north-splitter, .z-south-splitter`            | cursor            | `row-resize`                                                                            | doc/spec/DESIGN.md "Splitter Family (unified spec)" — family cursor |

## State matrix

| state                    | selector                                                      | properties to check |
|--------------------------|---------------------------------------------------------------|---------------------|
| default — container      | `.z-borderlayout`                                             | c1, c2              |
| default — north/south    | `.z-north`, `.z-south`                                        | c3, c4, c5, c6      |
| default — west/east      | `.z-west`, `.z-east`                                          | c7, c8, c9, c10     |
| default — center         | `.z-center`                                                   | c11                 |
| default — splitter bar   | `.z-west-splitter`                                            | c12, c14, c18       |
| hover splitter bar       | `.z-west-splitter:hover`                                      | c17                 |
| default — splitter button (west/east) | `.z-west-splitter-button`                        | c19, c20, c23, c24, c25, c26, c27 |
| default — splitter button (north/south) | `.z-north-splitter-button`                     | c21, c22, c23, c24, c25, c26, c27 |
| hover splitter button    | `.z-west-splitter-button:hover`                               | c28, c29            |
| grip dots visible        | `.z-west-icon.z-icon-ellipsis-v`                              | c30, c30b           |
| caret fade-in            | `.z-west-icon.z-icon-caret-left` (idle vs button hover)       | c30c                |
| disabled caret (closable=false) | `.z-west-splitter-button-disabled .z-icon-caret-left`  | c31                 |
| collapsed placeholder    | `.z-west-collapsed`                                           | c32, c33            |
| collapsed placeholder hover | `.z-west-collapsed:hover`                                  | c34                 |
| region header (with title) | `.z-west-header`                                            | c35, c36, c37, c38, c39, c40 |
| drag ghost               | `.z-splitter-ghost`                                           | c41, c42            |
| noborder modifier        | `.z-north-noborder`                                           | border-bottom: none |

## States to evaluate
- [ ] default — all 5 regions visible, no splitters shown (splittable=false)
- [ ] splitter bar default (splittable=true, splitter bar rendered)
- [ ] splitter bar hover
- [ ] splitter button default (collapsible=true)
- [ ] splitter button hover
- [ ] disabled caret (closable=false — button renders but caret icon hidden)
- [ ] collapsed placeholder (open=false — region closed)
- [ ] collapsed placeholder hover
- [ ] region header (title attribute set)
- [ ] noborder modifier (border="none")
