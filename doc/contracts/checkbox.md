# Component: checkbox (theme design)
tier: T1
category: selection
preview: ${PREVIEW_URL}/checkbox.zul
rules: see .claude/skills/zk-component-rules/components/checkbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Checkbox.css; Inputs/Switch.css (switch mold — visual benchmark is the MD3 switch, see Switch section)
- DESIGN.md sections: §2, §3, §7, §8, §11

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-checkbox-mold` | width / height | 18–20px |
| c2 | `.z-checkbox-mold` | border | 2px solid rgba(0, 0, 0, 0.6) (unchecked) |
| c3 | `.z-checkbox-mold` | border-radius | 2px (checkbox) or 50% (radio) |
| c4 | `.z-checkbox-input:checked + .z-checkbox-mold` | background-color | rgb(55, 111, 208) |
| c5 | `.z-checkbox-input:checked + .z-checkbox-mold` | content/check-mark | white check icon |
| c6 | `.z-checkbox:hover .z-checkbox-mold` | background-color | state-layer overlay |
| c7 | `.z-checkbox-input:focus-visible + .z-checkbox-mold` | outline / shadow | focus ring |
| c8 | `.z-checkbox[disabled]` | opacity | 0.38 |
| c9 | `.z-checkbox-content` | font-size | 13–14px |

### Switch mold (`mold="switch"`) — MD3 values, token-adapted

MD3 switch: 52×32 track (outer border box, 2px border, radius 16) with a thumb whose
centre is 16px from the track's outer edge (off: left, on: right); thumb 16px off / 24px on.
Retired by D62-A (2026-10-09, #10): Switch now follows MD3. Deviation: off-state thumb uses
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
| sw8 | hover (non-disabled) | state layer (`::before`) | 40px circle centred on the thumb (off `left: -6px`, on `left: calc(100% - 34px)`); opacity `--zk-state-hover-opacity`; on-surface (off) / primary (on) |
| sw9 | `.z-checkbox-switch-disabled` | opacity | 0.38 |

### Toggle mold (`mold="toggle"`) — MD3 filled-toggle reading, compact

ZK renders the mold element **empty** (label is always a sibling — see skill "The mold element
is always EMPTY"), so the fill is the only on/off signal. Per user decision (2026-06-04): a
check icon reads as *selection confirmation*, which diverges from a toggle's press semantics —
so ON = **solid primary fill** (MD3 toggle button "selected = filled container" reading; no
glyph), NO inset shadow. Sizing stays MUI-compact (32×32, r4).

| id | selector | property | expected |
|----|----------|----------|----------|
| tg1 | `.z-checkbox-toggle > .z-checkbox-mold` | width × height | 32 × 32px |
| tg2 | mold (off) | border / border-radius | 1px solid `--zk-color-outline-variant` rgba(0, 0, 0, 0.12) / 4px |
| tg3 | mold (off) | background | transparent; hover (non-disabled) = on-surface 8% state layer |
| tg4 | `.z-checkbox-toggle-on > .z-checkbox-mold` | background / border / shadow / content | solid `var(--zk-color-primary)` rgb(55, 111, 208); border-color primary; box-shadow none (no inset); NO injected glyph (`::after` display none) |
| tg6 | on hover (non-disabled) | background | on-primary 8% state layer over primary (`color-mix(… var(--zk-color-on-primary) 8%, var(--zk-color-primary))` — filled-button convention) |
| tg7 | `.z-checkbox-toggle-disabled` | opacity | 0.38 |

(tg5 retired 2026-06-04 — check icon removed by user decision; id not reused.)

### Tristate mold (`mold="tristate"`) — three visually distinct states

ZK emits mold-**prefixed** state classes for this mold (`.z-checkbox-tristate-off` / `-on` /
`-indeterminate`), NOT the unprefixed default-mold classes — see the checkbox skill entry. The box
reuses the default-mold visual (18px square, `::after` glyph). Each state must be distinguishable:
checked = checkmark on a primary fill, indeterminate = dash on a primary fill. (Gap 2026-07-14.)

| id | selector | property | expected |
|----|----------|----------|----------|
| tr1 | `.z-checkbox-tristate-off > .z-checkbox-mold` | background / `::after` | transparent box, outline border; `::after` display none (no glyph) |
| tr2 | `.z-checkbox-tristate-on > .z-checkbox-mold` | background / `::after` | `var(--zk-color-primary)` fill; `::after` display block = **checkmark** SVG |
| tr3 | `.z-checkbox-tristate-indeterminate > .z-checkbox-mold` | background / `::after` | `var(--zk-color-primary)` fill; `::after` display block = **dash** SVG |
| tr4 | tr2 vs tr3 | `::after` background-image | must **differ** (checkmark ≠ dash) — the two states are not interchangeable |
| tr5 | `.z-checkbox-disabled.z-checkbox-tristate-{on,indeterminate}` | opacity | 0.38 |

## States to evaluate
- [ ] unchecked, checked, indeterminate (if supported), hover, focus-visible, disabled
- [ ] switch mold (MD3, 52×32 track): off, on, hover, focus-visible, disabled (sw1–sw9)
- [ ] toggle mold: off, on, hover, focus-visible, disabled (tg1–tg7)
- [ ] tristate mold: off, on (checkmark), indeterminate (dash), disabled (tr1–tr5)
