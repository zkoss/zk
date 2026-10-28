# Contract — Framework drag/drop + frozen classes

Outcome contract for the JS-toggled global classes the Marble theme must style. These have **no
MD3/Mira analog** and **no stock ZK CSS** (except `.z-word-nowrap`/`.z-dragged`, which stock
defines in `norm.less`) — they are runtime hooks ZK's client engine emits and expects the theme to
style. Spec layer: `.claude/skills/zk-component-rules/reference/framework-classes.md`. Preview:
`dnd.zul`. Tests: `src/test/playwright/framework-classes.spec.ts`.

DOM facts verified from ZK 10.2.1 `zk/widget.ts`:
- Frozen grid/listbox body div ← `.z-word-nowrap` (Frozen.ts bind_, on load).
- Drag source root ← `.z-dragged` (cloneDrag_ 4361, drag-only).
- Drop-target root ← `.z-drag-over` (dropEffect_ 4311, drag-only).
- Non-TR drag → clone ghost `#zk_ddghost.z-drag-ghost` appended to body.
- TR drag (listitem/row) → message ghost `#zk_ddghost.z-drop-ghost.z-drop-disallow` →
  `.z-drop-content` → `.z-drop-icon` (`#zk_ddghost-img`) + `.z-drop-text`. Over a valid target the
  root toggles to `.z-drop-allow` and the icon swaps `z-icon-ban` → `z-icon-plus-circle`.

## Expected values

| # | Selector | Expected (MD3 tokens) | Test assertion |
|---|----------|------------------------|----------------|
| f1 | `.z-word-nowrap` (grid body div, frozen) | `white-space: nowrap` | live frozen grid: body div computed `white-space === 'nowrap'` |
| d1 | `.z-dragged` (source) | `opacity: 0.4` | inject class → `opacity === '0.4'` |
| d2 | `.z-drag-over` (drop target) | `background-color: var(--zk-color-primary-container)`; `outline: 2px dashed var(--zk-color-primary)`; `outline-offset: -2px` | inject class → background-color non-transparent + `outline-style === 'dashed'` |
| d3 | `.z-drag-ghost` (clone ghost) | `opacity: .85`; `box-shadow: var(--zk-elevation-4)`; `border-radius: var(--zk-shape-corner-small)`; `overflow: hidden`; `pointer-events: none`; `list-style: none` | inject → `box-shadow !== 'none'` + `border-radius !== '0px'` |
| g1 | `.z-drop-ghost` (message ghost root) | `display: inline-flex`; `align-items: center`; `max-width: 320px`; `padding: var(--zk-spacing-1) var(--zk-spacing-3)`; `border-radius: var(--zk-shape-corner-small)`; `background-color: var(--zk-color-inverse-surface)`; `color: var(--zk-color-inverse-on-surface)`; `box-shadow: var(--zk-elevation-3)`; `font-size: var(--zk-typescale-body-small-size)`; `white-space: nowrap`; `pointer-events: none`; `z-index: 90000`. **Must NOT override JS-set `position:absolute; top; left`.** | inject ghost markup → `display === 'inline-flex'` + `box-shadow !== 'none'` + background non-transparent + `position === 'absolute'` (inline preserved) |
| g2 | `.z-drop-content` | `display: inline-flex`; `align-items: center`; `gap: var(--zk-spacing-2)` | inject → `display === 'inline-flex'` |
| g3 | `.z-drop-icon` | `display: inline-flex`; `flex: none`; `width: 16px`; `height: 16px`; `background-color: currentColor`; mask center/contain no-repeat | inject → `width === '16px'` |
| g4 | `.z-drop-text` | `overflow: hidden`; `text-overflow: ellipsis` | (no strict assertion) |
| g5 | `.z-drop-ghost.z-drop-allow .z-drop-icon` vs `.z-drop-disallow .z-drop-icon` | allow → plus-circle mask; disallow → ban mask (distinct glyphs) | inject both → `maskImage` differs and neither is `'none'` |

## Notes
- `.z-drop-ghost` keeps the JS-set inline `position:absolute; top; left` (cursor tracking). The rule
  must not set `position`/`top`/`left` — only appearance.
- `.z-drop-icon` is a mask-filled glyph (Marble pattern, like `.z-messagebox-icon`); `z-icon-ban` /
  `z-icon-plus-circle` (stock font-icon names) are unused — the allow/disallow glyph is supplied by
  the theme via `mask-image` on the state selectors.
- All colours via `var(--zk-color-*)`; no hardcoded hex.
