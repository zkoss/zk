# Marble — Scope & Design Decisions

Records the deliberate scope boundaries and won't-do decisions for the theme, with the durable rationale for each.

## 1. Dark mode — won't-do

Marble ships **light only**. In enterprise internal systems (ERP/CRM/HRM) dark mode is a real but minority, high-friction request: high information density reads worse on dark, status-color semantics degrade under the desaturation dark mode forces for accessibility, and bright office ambient light turns dark screens into mirrors. The ROI — re-deriving elevation, re-tuning every chart and status color, and full contrast re-verification — does not justify the spend. This is an accepted divergence from the benchmark frameworks, not a backlog item.

**Pragmatic partial path (only if ever revisited):** dashboard/monitoring modules only — never transactional grids or forms. The centralized `tokens/_colors.css` layer makes authoring a dark palette cheap; the real cost is re-verifying every component in dark.

## 2. Dedicated high-contrast theme — won't-do

A *separate* high-contrast theme is not maintained. The `@media (forced-colors: active)` override (see [forced-colors.md](forced-colors.md)) is the modern equivalent and what the industry ships. Restore accessibility affordances there, never in a parallel theme.

## 3. RTL / bidirectional support — sole remaining functional gap (backlog)

RTL is **not yet done** — the one remaining functional gap. Work required:

- Migrate the ~27 physical `left`/`right` CSS properties to logical properties (`inset-inline-*`, `margin-inline`, etc.).
- Flip directional glyphs (chevrons, arrows) under `[dir=rtl]`.
- Add an RTL Playwright project (mirrors the existing tablet-UA project pattern).

ZK itself supports `dir="rtl"`, so the gap is purely on the theme CSS side.

## 4. Theme priority = 500

Marble registers at priority **500**. Lower value = higher priority. Marble ships as the default theme of its own distribution; where it coexists with another theme, the host app sets `org.zkoss.theme.preferred=marble` (which always wins).

At **equal** priority the tie is resolved by non-deterministic `HashMap` order (a theme replaces the current only on *strictly* lower value). An adopter who needs their own theme auto-selected **without** setting `preferred` must register it at a priority `< 500`.

## 5. Deprecated font library-properties — intentionally absent

`org.zkoss.zul.theme.fontFamily*` and `org.zkoss.zul.theme.fontSize*` (deprecated since the ZK 7.0.0 LESS era) are **deliberately not supported**. Their only consumer was the LESS-era `ext.css.dsp`, which ZK 10 no longer requests, so setting them has no effect. Marble replaces them with `--zk-typescale-*` tokens consumed at `body`/`.z-page`. **Do not re-add them.**

## 6. Minor open items

- **No `tertiary` color role.** MD3 defines it; Marble has primary + secondary only. Cosmetic unless a component needs a third accent.
- **No full multi-token live theme playground.** A single-seed live color picker exists (brand showcase); a multi-token playground (edit spacing/shape/typography, export a token set) is not offered.

## 7. ZK version support — selector-safe on 10.3.0.1

The 10.2.1 → 10.3.0.1 upgrade introduces **no DOM or CSS-class breakage** for Marble's `.z-*` selectors (verified via `git diff` on the ZK source and CaseFoundry migration notes). The one structural change — the tabbox scroll-arrow (`z-tabbox-left-scroll`/`z-tabbox-right-scroll`) and toolbar sibling reorder in `tab/mold/tabbox.js` — is safe because those elements are positioned `absolute` off the `.z-tabbox` root, so Marble's CSS has no sibling-order (`+`/`~`), `nth-child`, or `order:` dependency on them.

**Reusable rationale for future upgrades** — three whole categories of ZK's stock (IceBlue) LESS deltas are structurally N/A to a pure-CSS token theme like Marble:

- **LESS → `calc()` arithmetic migration** — compiles to identical CSS values; Marble already writes final values / `var()`s.
- **IE opacity-filter removal** (`filter:alpha(opacity=…)`) — Marble is modern-browsers-only and never emitted it.
- **ZK's new CSS-variable pipeline** — Marble already ships the custom-property architecture ZK is introducing.

Triage each stock bug-fix delta individually rather than porting blindly; most are not-applicable for the above reasons.
