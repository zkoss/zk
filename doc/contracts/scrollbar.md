# Component: scrollbar
tier: T2
category: scrollbar
preview: ${PREVIEW_URL}/scrollbar.zul
zk-version: 10.2.1-jakarta

## References
- DESIGN.md sections: §5 (Corner Radii), §9 (Motion), §15 (Scrollbar)
- Skill: `.claude/skills/zk-component-rules/components/scrollbar.md` (DOM tree, gating, must-bundle loading rule)
- MUI / Mira: **none** — both ship native browser scrollbars only; the native `::-webkit-scrollbar` bar in `base/_reset.css` is the de-facto MD3 baseline.

This contract covers the **simulated** scrollbar (`zul.Scrollbar`, rendered only when
`org.zkoss.zul.nativebar="false"`). The native `::-webkit-scrollbar` bar is covered by the
reset/frozen CSS, not here.

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| s0 | `.z-scrollbar-indicator` | (rule present in a LOADED stylesheet) | **border-radius ≠ 0px** — proves `scrollbar.css` is bundled into `norm.css.dsp` and served (regression guard for the 2026-06-30 orphan gap) |
| s1 | `.z-scrollbar-indicator` | border-radius | pill — `var(--zk-shape-corner-full)` = 9999px (NOT 4px) |
| s2 | `.z-scrollbar-indicator` | background-color | `--zk-color-outline` @ idle opacity 0.6 (bolder draggable thumb) |
| s3 | `.z-scrollbar-indicator` | opacity | 0.6 |
| s4 | `.z-scrollbar-indicator` | transition-timing-function | `cubic-bezier(0.4, 0, 0.2, 1)` (`--zk-motion-easing-standard`, NOT `ease`) |
| s5 | `.z-scrollbar-indicator:hover` | background-color | `--zk-color-on-surface-variant` |
| s6 | `.z-scrollbar-indicator:hover` | opacity | 0.85 |
| s7 | `.z-scrollbar-rail` | background | `--zk-color-surface-container` (a **visible** faint track channel — NOT transparent; distinguishes the simulated bar from the track-less native overlay) |
| s8 | `.z-scrollbar-rail` | border-radius | pill — `var(--zk-shape-corner-full)` |
| s9 | `.z-scrollbar-vertical` | width | 12px (lane); track `.z-scrollbar-rail` width 8px **right-anchored flush** (`right:0`); `.z-scrollbar-indicator` width 6px **`right:1px`** (1px inset → centred in the track) |
| s10 | `.z-scrollbar-horizontal` | height | 12px (lane); track height 8px **bottom-anchored flush** (`bottom:0`); `.z-scrollbar-indicator` height 6px **`bottom:1px`** |
| s11 | `.z-scrollbar-vertical-embed`, `.z-scrollbar-horizontal-embed` | background-color / border-radius / size | `--zk-color-outline-variant` / pill / **8px cross-axis thickness (= the hover *track* width, NOT the 6px thumb)**; **no `:hover` rule**. JS pins it flush (`right:0` / `bottom:0` inline), so an 8px rail gives the rest state the SAME flush footprint as the hover track → it refines into thumb-in-track on hover with no movement. |
| s12 | `.z-scrollbar-up`, `.z-scrollbar-down`, `.z-scrollbar-left`, `.z-scrollbar-right` | display | **NOT none** — step buttons are shown, flat/neutral caret `i` ~11px, faint hover state layer; appear only on hover. Cross-axis 8px **and edge-anchored** (vertical up/down `right:0`; horizontal left/right `bottom:0`) so the caret centres on the thumb's line; along-axis kept 12px (`syncSize()` reads it to inset the wrapper). |
| s13 | `.z-scrollbar-vertical .z-scrollbar-wrapper` | top | 12px (inset by the up-button along-axis height so `syncSize()` math aligns); horizontal wrapper `left` 12px |
| s14 | rest `*-embed` rail vs hover `.z-scrollbar-indicator` | cross-axis centre (embedded grid) | **equal within ±1px on BOTH axes** — the rest rail must preview the hover thumb at the same line, so the bar does not jump laterally on mouse-over (MD3 continuity; measured: both at ~4px from the edge). |

## States to evaluate
- [ ] gating: `nativebar="false"` renders the simulated `.z-scrollbar`; `nativebar="true"` renders the native bar (no `.z-scrollbar*` DOM)
- [ ] mode: overlay (`data-embedscrollbar="false"` — no `*-embed`, hover-only float) vs embedded (`data-embedscrollbar="true"` — static `*-embed` rail at rest)
- [ ] **no lateral jump rest→hover** (embedded): the `*-embed` rail and the hover bar share one cross-axis line; mouse-over only fades in the track + arrows, the footprint does not shift (s14)
- [ ] orientation: vertical + horizontal (both lanes present when content overflows both axes)
- [ ] thumb states: idle, hover (darkens to on-surface-variant), drag/active
- [ ] step buttons shown in every mode/orientation; caret icon renders; hover state layer
- [ ] visible track channel present behind the thumb (distinct from native)
- [ ] **loading**: the `.z-scrollbar-indicator` rule must come from a served stylesheet (bundled, not an orphaned standalone `.dsp`)
