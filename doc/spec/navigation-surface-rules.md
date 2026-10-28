# Navigation surface rules (MD3 tonal elevation)

MD3 separates **content** from **navigation chrome** using tonal surface
steps, not box-shadows. Components that carry application data sit on
`surface`; navigation chrome (drawers, app bars, region headers) sits on
deeper tonal steps (`surface-container-low`, `surface-container`, …).

The visual outcome is subtle on a white background (all surface-container*
tokens are in the 98–94% lightness band), which is correct per the spec but
sometimes prompts the question "is this a bug?". This file is the
authoritative answer: **no, it's intentional**.

## Why tonal, not shadow?
1. **Cleaner visuals.** No drop-shadow seams; chrome feels integrated.
2. **Dark mode parity.** Box-shadows almost disappear on dark backgrounds;
   tonal steps still read clearly.
3. **Large-area friendly.** A 300px-tall sidebar with a heavy shadow feels
   oppressive; tonal tint scales gracefully.

## Surface-tier mapping (this theme)

| MD3 tier | Token | Used by | File reference |
|----------|-------|---------|----------------|
| Page background | `--zk-color-background` | `body`, page root | `tokens/_colors.css`, `marble.css` |
| Content surface | `--zk-color-surface` | `z-window`, `z-panel`, `z-grid`, `z-listbox` background, `z-north`, `z-south` (BorderLayout) | many — content widgets |
| Chrome tier 1 | `--zk-color-surface-container-low` | `.z-navbar` (Navigation Drawer / Rail), `.z-west`, `.z-east` (BorderLayout sidebars) | `js/zkmax/nav/css/nav.css:44`, `js/zul/layout/css/borderlayout.css:65,82` |
| Chrome tier 2 | `--zk-color-surface-container` | `.z-menubar` (Top App Bar), `.z-*-header` (BorderLayout region headers), `.z-*-collapsed` (BorderLayout collapse strips) | `js/zul/menu/css/menu.css:60`, `js/zul/layout/css/borderlayout.css:259,283` |
| Chrome tier 3 | `--zk-color-surface-container-high` | Hovered chrome (e.g. collapsed strip on hover) | `js/zul/layout/css/borderlayout.css:270` |
| Chrome tier 4 (max) | `--zk-color-surface-container-highest` | Filled-tonal icon buttons (splitter button), elevated chips | `js/zul/layout/css/borderlayout.css:166,188` |

## Rules
1. **Content widgets MUST use `surface`** (or `background` for the page itself).
   Never `surface-container*`.
2. **Navigation chrome MUST use a `surface-container*` step.** Which step
   depends on the chrome's role:
   - Sidebars / drawers → `surface-container-low` (tier 1)
   - Top app bars / region headers → `surface-container` (tier 2)
   - Hovered chrome or collapse strips that should feel "ready to interact"
     → `surface-container-high` (tier 3)
   - Filled-tonal buttons sitting on chrome → `surface-container-highest`
     (tier 4)
3. **Do not "unify" chrome with content.** If two surfaces look "too close"
   to each other, the fix is to deepen the chrome's tonal step in
   `tokens/_colors.css`, NOT to repaint the chrome with `surface`.
4. **No box-shadow on chrome.** Use tonal step + (optionally) a 1px
   `outline-variant` divider at the edge if you need a hairline separator.

## Forbidden patterns
- ❌ `.z-navbar { background-color: var(--zk-color-surface); }` — flattens
   chrome to content.
- ❌ `.z-menubar { box-shadow: var(--zk-elevation-2); }` — MD3 chrome uses
   tonal elevation, not box-shadow elevation.
- ❌ Hardcoded hex / rgba on any chrome surface — must be a token.

## When the difference looks too subtle
This is a tonal-palette gap, not a widget bug. The fix is in
`tokens/_colors.css`: deepen the surface-container ladder so the steps are
perceptually distinct on white. The widget CSS should not be touched.

## Shared sidebar / nav inclusion — use `<apply>`, not `<include>`

The shared sidebar / navigation surface MUST be injected via
`<apply templateURI="_sidebar.zul"/>`, **not** `<include src="_sidebar.zul"/>`.
`<apply>` evaluates the template in the current page's component scope, which is
required for the active-state sclass and any per-page bindings on the nav to
resolve. `<include>` creates a separate IdSpace that breaks that ID / binding
scoping, so active-state coordination fails.

## Cross-reference
- `doc/spec/window-design-rules.md` — `border` attribute MUST NOT drive shadow on
  Window; same principle (tonal, not shadow) for elevation distinctions.
- `doc/skill-gaps.md` 2026-05-29 row "navbar / menubar (tonal-surface
  rationale)" — gap log entry.
