# Component: navbar
tier: T1
category: navigation
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/nav/css/nav.css
siblings: [anchornav]
preview: ${PREVIEW_URL}/navbar.zul

## References
- MUI CSS: Drawer.css, List.css
- DESIGN.md sections: §2, §3, §7, §9

## DOM key selectors
```
.z-navbar               ← root
.z-navbar-vertical      ← vertical orientation variant
.z-nav                  ← nav group
.z-nav-header           ← group header/label
.z-navitem              ← individual item
.z-navitem-selected     ← active/selected item
.z-navseparator         ← divider
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c0 | `.z-navbar` | background-color | `var(--zk-color-surface-container-low)` (MD3 Navigation Drawer surface; override to `transparent` at use site if nested in already-tinted region) |
| c1 | `.z-navitem` | padding | 8–12px 16px |
| c2 | `.z-navitem` | font-size | 14px |
| c3 | `.z-navitem-selected` | background-color | `color-mix(in srgb, var(--zk-color-primary) 12%, transparent)` → rgba(55, 111, 208, 0.12) |
| c4 | `.z-navitem-selected` | color | `var(--zk-color-primary)` → rgb(55, 111, 208) |
| c5 | `.z-navitem:hover` | background-color | rgba(0, 0, 0, 0.08) |
| c6 | `.z-nav-header` | font-size | 12px |
| c7 | `.z-navbar-vertical .z-navitem-selected` | active marker | The rounded tonal **container alone** (c3 tint fill + c4 primary text + weight 600), MD3 Navigation Drawer / MUI ListItemButton. **No left-edge accent** — neither a `border-left` nor an `::after`/`::before` bar strip. (Stacking a classic-sidebar bar on the rounded MD3 pill is redundant + clashes at the corners; design review 2026-07-07.) |
| c8 | `.z-navitem-content:focus-visible` | outline | `var(--zk-focus-ring)` → `2px solid rgb(55, 111, 208)`. The theme ring, not the browser's `outline-style: auto` default — a navbar with no `:focus-visible` rule silently ships the UA ring (1px, off-palette). |
| c9 | `.z-navitem-content:focus-visible` | outline-offset | `-2px` — **inset**, the theme's full-bleed-row focus mechanism (same as `.z-treerow`). A positive offset is a defect here, not a taste call: the ring is drawn outside a box that has no room, and is cut. See the skill's `reference/focus-ring-clipping.md`. |
| c10 | `.z-nav-content:focus-visible` | outline + outline-offset | Identical to c8/c9. The group header is a focus target in its own right, so it must not fall back to the UA ring. |
| c11 | `.z-navitem-selected > .z-navitem-content:focus-visible` @ `forced-colors: active` | outline-color | `HighlightText`. The selection fill and `--zk-focus-ring` both remap to `Highlight`, so the inherited ring would paint Highlight-on-Highlight and disappear. Guard lives in `tokens/_forced-colors.css` block (2a focus); see `doc/spec/forced-colors.md`. |

## States to evaluate
- [ ] default, selected/active, hover, focus-visible (c8-c10), selected + focus-visible under forced-colors (c11)
