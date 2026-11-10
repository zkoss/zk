# Component: checkbox (theme design)
tier: T1
category: selection
preview: ${PREVIEW_URL}/checkbox.zul
rules: see .claude/skills/zk-component-rules/components/checkbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Checkbox.css; Inputs/Switch.css (switch mold — visual benchmark is the MD3 switch, see Switch section)
- doc/spec/DESIGN.md sections: "Text Colors", "Brand & Status Colors", "Typography", "State-Layer Overlays", "Border Rules"

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-checkbox-mold` | width / height | 18–20px |
| c2 | `.z-checkbox-mold` | border | 2px solid `var(--zk-checkbox-border-color)` (unchecked) |
| c3 | `.z-checkbox-mold` | border-radius | `var(--zk-checkbox-radius)` (checkbox) or 50% (radio) |
| c4 | `.z-checkbox-on > .z-checkbox-mold` | background-color | `var(--zk-checkbox-accent)` |
| c5 | `.z-checkbox-on > .z-checkbox-mold` | content/check-mark | white check icon |
| c6 | `.z-checkbox-off:hover > .z-checkbox-mold::before` | opacity | `var(--zk-state-hover-opacity)` (state layer) |
| c7 | `.z-checkbox:has(input[type="checkbox"]:focus-visible) > .z-checkbox-mold` | outline / shadow | focus ring |
| c8 | `.z-checkbox-disabled > .z-checkbox-mold` | opacity | `var(--zk-state-disabled-opacity)` |
| c9 | `.z-checkbox-content` | font-size | `var(--zk-typescale-body-medium-size)` |
| c10 | `.z-checkbox-mold::before`, `.z-radio input[type="radio"]::before` (state-layer ring) | pointer-events | **not** `none` (inherited): the ring is the touch target and shows the pointer, so a click on it toggles the control (B50-2976934). A disabled wrapper has `pointer-events: none`, so a disabled ring stays dead; do not set `auto`, which would revive it |
| c11 | `.z-checkbox-mold::before`, `.z-radio input[type="radio"]::before` (state-layer ring) | size | 36×36px; in compact density `min(36px, var(--zk-control-height))` = 32px, so the ring stays inside its wrapper and two stacked controls never overlap (a click at the bottom edge of the upper one toggles the upper one). Tablet `_selection.css` widens the margin and inset to a 44px ring in a 48px row, still inside the row. The tablet rules apply to the checkbox and radio only, not the switch or the toggle |

### Switch mold (`mold="switch"`) — MD3 values, token-adapted

MD3 switch: 52×32 track (outer border box, 2px border, radius 16) with a thumb whose
centre is 16px from the track's outer edge (off: left, on: right); thumb 16px off / 24px on.
Switch follows MD3. Deviation: off-state thumb uses
--zk-color-on-surface-variant (not outline) so the thumb keeps >= 3:1 against the track with
Marble's light outline token.

| id | selector | property | expected |
|----|----------|----------|----------|
| sw1 | `.z-checkbox-switch > .z-checkbox-mold` (track) | width × height (border-box) | 52 × 32px |
| sw2 | track | border-radius / border | 16px / 2px solid |
| sw3 | track (off) | background-color / border-color | `var(--zk-color-surface-container-highest)` / `var(--zk-color-outline)` |
| sw4 | `.z-checkbox-switch-on > .z-checkbox-mold` | background-color / border-color | `var(--zk-color-primary)` / `var(--zk-color-primary)` (no visible ring) |
| sw5 | thumb (`::after`) | size / shape | off 16 × 16px, on 24 × 24px, border-radius 50%, no box-shadow |
| sw6 | thumb (off) | background | `var(--zk-color-on-surface-variant)` (deviation from MD3 outline, see note) |
| sw7 | thumb (on) | background / position | `var(--zk-color-on-primary)`; off `left: 6px`, on `left: calc(100% - 26px)` (padding box 48 wide ⇒ centre 16px from outer left / right edge) |
| sw8 | hover (non-disabled) | state layer (`::before`) | circle centred on the thumb, `min(40px, var(--zk-control-height))` (40px; 32px in compact) — off `left: calc(14px - size / 2)`, on `left: calc(100% - 14px - size / 2)`; opacity `--zk-state-hover-opacity`; on-surface (off) / primary (on) |
| sw9 | `.z-checkbox-switch-disabled` | opacity | `var(--zk-state-disabled-opacity)` |
| sw10 | state layer (`::before`) | pointer-events | **not** `none`, same as c10: a click on the circle toggles the switch |
| sw11 | switch (desktop, compact, tablet) | ring placement | the 40px ring (32px in compact) is centred on the thumb and inside the wrapper in every mode; on tablet the track keeps its 4px margin and the row is 48px (`min-height`), the tablet checkbox margin / inset rules do not apply to it. In the 48px tablet row the bottom 4px under the ring is not a click target |

### Toggle mold (`mold="toggle"`) — MD3 filled-toggle reading, compact

ZK renders the mold element **empty** (label is always a sibling — see skill "The mold element
is always EMPTY"), so the fill is the only on/off signal. Rule: ON = **solid primary fill** (MD3 toggle
button "selected = filled container" reading; no glyph), NO inset shadow. A check icon is not used,
because it reads as *selection confirmation*, which diverges from a toggle's press semantics. The
toggle state classes follow the mold-prefixed pattern in `doc/spec/DESIGN.md` "Checkbox — mold state
class naming" (`z-checkbox-toggle-on`). Sizing stays MUI-compact (32×32, r4).

| id | selector | property | expected |
|----|----------|----------|----------|
| tg1 | `.z-checkbox-toggle > .z-checkbox-mold` | width × height | 32 × 32px |
| tg2 | mold (off) | border / border-radius | 1px solid `var(--zk-color-outline-variant)` / 4px |
| tg3 | mold (off) | background | transparent; hover (non-disabled) = on-surface 8% state layer |
| tg4 | `.z-checkbox-toggle-on > .z-checkbox-mold` | background / border / shadow / content | solid `var(--zk-color-primary)`; border-color primary; box-shadow none (no inset); NO injected glyph (`::after` display none) |
| tg6 | on hover (non-disabled) | background | on-primary 8% state layer over primary (`color-mix(… var(--zk-color-on-primary) 8%, var(--zk-color-primary))` — filled-button convention) |
| tg7 | `.z-checkbox-toggle-disabled` | opacity | `var(--zk-state-disabled-opacity)` |

(tg5 is not used: the toggle has no check icon; id not reused.)

### Tristate mold (`mold="tristate"`) — three visually distinct states

ZK emits mold-**prefixed** state classes for this mold (`.z-checkbox-tristate-off` / `-on` /
`-indeterminate`), NOT the unprefixed default-mold classes — see the checkbox skill entry. The box
reuses the default-mold visual (18px square, `::after` glyph). Each state must be distinguishable:
checked = checkmark on a primary fill, indeterminate = dash on a primary fill.
| id | selector | property | expected |
|----|----------|----------|----------|
| tr1 | `.z-checkbox-tristate-off > .z-checkbox-mold` | background / `::after` | transparent box, outline border; `::after` display none (no glyph) |
| tr2 | `.z-checkbox-tristate-on > .z-checkbox-mold` | background / `::after` | `var(--zk-color-primary)` fill; `::after` display block = **checkmark** SVG |
| tr3 | `.z-checkbox-tristate-indeterminate > .z-checkbox-mold` | background / `::after` | `var(--zk-color-primary)` fill; `::after` display block = **dash** SVG |
| tr4 | tr2 vs tr3 | `::after` background-image | must **differ** (checkmark ≠ dash) — the two states are not interchangeable |
| tr5 | `.z-checkbox-disabled.z-checkbox-tristate-{on,indeterminate}` | opacity | `var(--zk-state-disabled-opacity)` |

## States to evaluate
- [ ] unchecked, checked, indeterminate (if supported), hover, focus-visible, disabled
- [ ] switch mold (MD3, 52×32 track): off, on, hover, focus-visible, disabled (sw1–sw9)
- [ ] toggle mold: off, on, hover, focus-visible, disabled (tg1–tg7)
- [ ] tristate mold: off, on (checkmark), indeterminate (dash), disabled (tr1–tr5)
