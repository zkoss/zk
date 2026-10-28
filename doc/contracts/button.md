# Component: button (theme design)
tier: T1
category: button
preview: ${PREVIEW_URL}/button.zul
rules: see .claude/skills/zk-component-rules/components/button.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: /Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/Button.css
- DESIGN.md sections: §3, §5, §7, §8, §9, §10, §11

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-button` | height | 34–36px |
| c2 | `.z-button` | padding (top/bottom) | 6px |
| c3 | `.z-button` | padding (left/right) | ≥ 12px |
| c4 | `.z-button` | font-size | 14px |
| c5 | `.z-button` | font-weight | 500 |
| c6 | `.z-button` | text-transform | uppercase OR none (per DESIGN; allow either) |
| c7 | `.z-button` | border-radius | 4px |
| c8 | `.z-button` | background-color | rgb(55, 111, 208) (primary filled) |
| c9 | `.z-button` | color | rgb(255, 255, 255) |
| c10 | `.z-button` | transition-duration | 250ms |
| c11 | `.z-button:hover` | state layer | white/on-primary overlay ~0.12 opacity — **lightens** the filled bg (not a darker tint) |
| c12 | `.z-button:focus-visible` | outline | visible (ring or state layer) |
| c13 | `.z-button[disabled]` | opacity | 0.38 |
| c14 | `.z-button` | box-shadow | elevation level 1 (resting) |
| c29 | `.z-button:hover` | box-shadow | raised to `--zk-elevation-2` (from resting) for filled/contained; flat (outlined/text/icon) & FAB variants unchanged |

| c15 | `.z-button-secondary.z-button[disabled]` | background-color | `--zk-color-disabled-container` (not the secondary color) |
| c16 | `.z-button-success.z-button[disabled]` | background-color | `--zk-color-disabled-container` (not the success color) |
| c17 | `.z-button-warning.z-button[disabled]` | background-color | `--zk-color-disabled-container` (not the warning color) |
| c18 | `.z-button-error.z-button[disabled]` | background-color | `--zk-color-disabled-container` (not the error color) |
| c19 | `.z-button-info.z-button[disabled]` | background-color | `--zk-color-disabled-container` (not the info color) |
| c20 | `.z-button-outlined-secondary.z-button[disabled]` | color | `--zk-color-disabled` (not the secondary color) |
| c21 | `.z-button-outlined-secondary.z-button[disabled]` | border-color | `--zk-color-disabled-container` (not the secondary color) |
| c22 | `.z-button-outlined-success.z-button[disabled]` | color | `--zk-color-disabled` |
| c23 | `.z-button-outlined-error.z-button[disabled]` | color | `--zk-color-disabled` |
| c24 | `.z-button-outlined-warning.z-button[disabled]` | color | `--zk-color-disabled` |
| c25 | `.z-button-outlined-info.z-button[disabled]` | color | `--zk-color-disabled` |
| c26 | `.z-button-text-secondary.z-button[disabled]` | color | `--zk-color-disabled` (not the secondary color) |
| c27 | `.z-button-text-error.z-button[disabled]` | color | `--zk-color-disabled` |
| c28 | `.z-button-text-info.z-button[disabled]` | color | `--zk-color-disabled` |

| c30 | `.z-button` (graphic + label) | gap (graphic→label) | ≈8px (`--zk-spacing-2`) between `.z-button-image`/`.z-icon-*` and the label text — order-independent, so it holds for `dir="reverse"` too; vertical (`:has(br)`) stays 2px; text-only / icon-only buttons unaffected |

## States to evaluate
- [ ] default, hover, focus-visible, active (pressed), disabled
- [ ] variants if applicable: outlined, text (sclass="z-button-outlined", "z-button-text")
- [ ] color variants in disabled state: secondary, success, warning, error, info
- [ ] graphic + label spacing: `image` + label, `iconSclass` + label, `dir="reverse"` (all ≈8px gap)
