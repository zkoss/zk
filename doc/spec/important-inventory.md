# `!important` inventory & irreducibility record

Durable record of every `!important` in the theme CSS source: what it does, whether it was
removed or kept, and the **evidence** for that verdict. Purpose: stop re-investigation — a
survivor here has already been empirically tested against the current ZK runtime.

**Method** (Planner → Generator → Evaluator):
each candidate's `!important` was deleted, the CSS rebuilt (`npm run build:css`), and the computed
value / behavior re-measured on the running **ZK 10.3.0.1-jakarta** preview app. Removed only when
proven **byte-identical** (empirical-proof bar); otherwise kept, with the failing evidence recorded.
No cascade beats an inline style, so an `!important` that overrides a ZK-JS-set inline `style` is
structurally irreducible.

Count: **37 → 33** (comment-stripped; source of truth `scratchpad/count-important.js`). Date: 2026-07.

**Recount 2026-09-08: 45.** The 2026-07 sweep left 33; since then `grid.css`, `avatargroup.css`
and `carousel.css` each grew one that was never added here (36), and the ZK 11 `codeeditor`
component adds 9 (below). All three of the previously-undocumented ones have now been through the
removal bar too — see "Backfill" below. Nothing was removable; the count stands at 45.

**Recount 2026-09-30: 49 — 45 documented, 4 not yet tested.** The four are
`zkex/wgt/css/skeleton.css`, restored as a Marble CSS source after the 2026-09-08 sweep
(`ZK-6112: restore the skeleton stylesheet as a Marble CSS source in zkex`): `.z-skeleton-active *`
and `.z-skeleton-masked` (`visibility: hidden`), and the `::after` / `::before` `content` pair that
builds the overlay. They have **not** been through the removal bar, so they are listed here as
outstanding rather than as survivors. Everything below still stands.

## Removed (5) — proven render-neutral, guarded by tests

| file:line | declaration | why it was redundant | guard test |
|-----------|-------------|----------------------|------------|
| `zul/code/css/codeeditor.css` | `.cm-scroller { font-size }` | CodeMirror's base theme sets `font-family` and `line-height` on `.cm-scroller` but **no** `font-size`, so nothing competes. Computed `font-size` = `13px` with and without. Measured on ZK 11.0.0.FL.20260904, 2026-09-08. | live CSSOM A/B (below) |
| `wgt/css/progressmeter.css` | `display: block` | ZK sets inline `width`+`overflow` on `.z-progressmeter-image`, never `display`; our layered rule wins alone. Computed `display` = `block` unchanged. | `screenshot.spec.ts › important-removal-guards › progressmeter …` |
| `wnd/css/messagebox.css` | `.z-messagebox-buttons { display: flex }` | `.z-hlayout` sets no `display`; nothing competes. Row geometry identical (1- & 3-button). | `… › messagebox button row …` |
| `wnd/css/messagebox.css` | `.z-messagebox-buttons > * { margin-left: 0 }` | `.z-hlayout-inner` has no competing margin rule; `margin-left` stays `0px`. | `… › messagebox button row …` |
| `menu/css/menu.css` | `.z-menupopup-content > li.z-menuseparator { margin: … 0 }` | The high-specificity LI-scoped selector (0,2,1) wins over framework `ul>li{margin:0}` by specificity alone on 10.3; margin stays `4px`. | `… › menupopup separator …` |

## Kept — irreducible, with evidence

**Overrides a ZK-JS-set inline `style` (no cascade can beat inline):**
- `zkex/inp/css/colorbox.css` `display:none` — **empirically confirmed load-bearing on 10.3.0.1**: after outside-tap dismiss the reattached popup keeps inline `display:block`; removing `!important` makes it compute `display:block`/visible. Guarded by `tablet.spec.ts › tablet-colorbox-dismiss`. (ZK 10.4 source resets display on close → re-check on upgrade.) Comment was stale ("10.2.1") — corrected.
- `zkmax/tbeditor/css/tbeditor.css` ×4 — the hidden raw textarea carries inline `height:0px`; `height:1px !important` overrides it, and `opacity:0` hides the overlay. Coupled force-hide over an inline style.
- `zkmax/layout/css/splitlayout.css` ×2 (`margin`) — JS `setBtnPos_` writes inline margin.
- `zul/layout/css/borderlayout.css` (`width:auto`) — JS sets inline width on the rotated E/W title.
- `zul/wnd/css/panel.css`, `zul/wgt/css/groupbox.css` (`height:auto`) — collapse beats inline `height="Xpx"` from the ZUL attr.
- `zul/wgt/css/misc.css` ×2 (`top/left:0`) — `zk/utl.ts progressbox()` writes inline `top:Ypx` on `.z-modal-mask`.
- `zul/wgt/css/separator.css` (`width:1px`), `zul/inp/css/input.css` + `zul/wgt/css/errorbox.css` (`padding`) — override ZK inline `width`/one-sided `padding`.
- `zkmax/css/tablet/_wheel.css` ×4, `_inputs.css` ×4 — pin the mobile picker over ZK JS inline `top` (`_fixedVParent`); tablet layer is unlayered by design.
- `zkmax/cropper/css/cropper.css` ×2, `zkmax/big/css/biglistbox.css` — cropper crosshair / hidden measurement span (inline-driven); not reproducible in a headless preview → kept under the conservative bar.

**Backfill 2026-09-08 — the three that post-dated the 2026-07 sweep, now tested:**
- `zkmax/grid/css/grid.css` (`.z-grid--stacking > .z-grid-body/-footer > table { width: 100% }`) —
  **irreducible, inline-override class.** `zul/mesh/MeshWidget.ts:789` writes
  `this.ebodytbl.style.width = innerWidth` — a real inline width on the body table — so no cascade
  can reach it. Not separately A/B-tested: `sizedByContent` appears on no preview page, so the
  failing case is not reproducible here; the source citation is the evidence. Re-check if ZK ever
  moves that sizing off an inline style.
- `zul/wgt/css/carousel.css` (`.z-carousel-effect-fade > .z-carousel-track { transform: none }`) —
  **irreducible, inline-override class; reproduced live** on ZK 11.0.0.FL.20260904, 2026-09-08.
  Navigate a slide-mode carousel (`setActiveIndex(1)` → ZK writes inline
  `transform: translateX(-200%)`), then `setEffect('fade')`. ZK stops writing the track transform
  in fade mode but does **not** clear the leftover, so the inline value survives the mode switch.
  Measured: computed `transform` is `none` with `!important` and `matrix(1, 0, 0, 1, -1200, 0)`
  without — i.e. the fade would render a slid-off, blank frame. ZK stock's own `carousel.less`
  uses `!important` here for the same reason.
- `zul/wgt/css/avatargroup.css` (`.z-avatargroup [data-ag-hidden] { display: none }`) — **kept as a
  DEFENSIVE guard, and it is the one survivor not proven against real ZK behaviour.** Measured live
  2026-09-08: `Avatargroup._applyOverflow` only calls `setAttribute('data-ag-hidden')` and writes no
  inline `display`, and against that real behaviour the declaration is byte-identical with and
  without `!important` (computed `display: none` either way — the `(0,2,0)` selector already beats
  `.z-avatar`'s own `display`). It is load-bearing only in the case its comment actually claims: an
  **app author's** inline `display` on an avatar (measured `none` with, `flex` without). That case
  is not hypothetical enough to drop — `avatar.zul` itself shows authors putting inline `style` on
  avatars, and an escaped 6th avatar would visibly break the "+N" overflow contract — so it is kept
  on the same "cannot disprove" footing as the `toolbar.css` entry below rather than on proof.

**Accessibility (must override everything):**
- `zul/css/tokens/_motion.css` ×4 — `prefers-reduced-motion` universal reset (WCAG 2.3.3); `1ms` (not `0`) is deliberate.

**Beats a third-party stylesheet injected UNLAYERED at runtime (no layer can win):**
- `zul/code/css/codeeditor.css` ×9 — CodeMirror 6 injects its base theme through a StyleModule
  `<style>` in `<head>`. Those rules are **unlayered**, and an unlayered author declaration
  outranks every cascade layer, so nothing in `@layer zk-components` can beat them on
  specificity — `!important` is the only lever. Each was A/B-tested live on the running
  ZK 11.0.0.FL.20260904 preview (drop the priority via CSSOM, re-read the computed value,
  restore), 2026-09-08:

  | declaration | with | without | CodeMirror's competing rule |
  |---|---|---|---|
  | `.cm-scroller { font-family }` | SFMono-Regular… | `monospace` | `.ͼ1 .cm-scroller` |
  | `.cm-scroller { line-height }` | `20px` | `18.2px` | `.ͼ1 .cm-scroller` (1.4) |
  | `.cm-editor.cm-focused { outline }` | `none` | `dotted 1px rgb(33,33,33)` | `.ͼ1.cm-focused` |
  | `.cm-gutters { background-color }` | `#f7f9fc` | `rgb(245,245,245)` | `.ͼ2 .cm-gutters` |
  | `.cm-gutters { color }` | `rgba(0,0,0,.6)` | `rgb(108,108,108)` | `.ͼ2 .cm-gutters` |
  | `.cm-gutters { border-right-color }` | `rgba(0,0,0,.12)` | `rgb(221,221,221)` | `.ͼ2 .cm-gutters` |
  | `-dark .cm-gutters { background-color }` | `#1e1e1e` | `#f7f9fc` | `.ͼ3 .cm-gutters` |
  | `-dark .cm-gutters { color }` | `rgb(133,133,133)` | `rgba(0,0,0,.6)` | `.ͼ3 .cm-gutters` |
  | `-dark .cm-gutters { border-right-color }` | `transparent` | `rgba(0,0,0,.12)` | `.ͼ3 .cm-gutters` |

  This is a **new irreducibility class** for this theme: every earlier survivor beats an inline
  style ZK's own JS wrote, or a same-layer rule. Re-test if ZK ever wraps the widget's injected
  CSS in a layer, or exposes the editor's chrome through `EditorView.theme`.

**Same-layer / cross-framework, legitimately needs `!important`:**
- `zkmax/goldenlayout/css/goldenlayout.css` (`cursor:move`) — `.lm_dragging *` must beat every descendant's own cursor during drag; iceblue uses `!important` here too.
- `zul/wgt/css/toolbar.css` (`width:auto`) — separator-as-flex-spacer must beat a `spacing`-attr inline width; the spaced case isn't in the preview → kept (can't disprove).
- `zul/box/css/box.css` (`.z-splitter-ghost` background) — drag-only element, not reproducible headless → kept.

> When re-running: the survivors above were verified on 10.3.0.1. On a ZK upgrade, re-test the
> inline-override group first (colorbox especially — 10.4 source changes the close path).
