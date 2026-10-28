# Component CSS must be css-uri-backed OR globally bundled (never orphaned)

**Theme-portable rule.** In a ZK theme there are two ways a component's CSS reaches the
browser. If a component's CSS uses *neither*, it is **orphaned** — emitted but never loaded,
and the component renders with browser/inherited defaults, **with no error**.

## The two load paths

1. **Per-component `css-uri`** — the component has a `<css-uri>` in `lang.xml` /
   `lang-addon.xml` (often per-mold). ZK requests that exact `<comp>.css.dsp` when the
   widget is on the page. A build that emits one `.css.dsp` per source CSS (1:1 auto-scan)
   serves these correctly. ✅
2. **Global bundle** — the CSS is concatenated into a file that `zk.wcs` always serves
   (in Marble: `norm.css.dsp`, built from `normFiles` in `scripts/build-css.js`; also
   `combo`/`footer`). Required for any component with **no `css-uri`**: helpers, mold-only
   widgets, core-JS-drawn DOM, and real widgets that stock ZK styles only in `norm.less`.

## The trap

If a **no-`css-uri`** component's CSS is left to the 1:1 auto-scan, the build emits an
orphaned `<comp>.css.dsp` that **ZK never requests**. The rule is in no served stylesheet;
the widget is unstyled; nothing errors. This bit `scrollbar` (gap log 2026-06-30) and then
6 more (`div`, `label`, `span`, `html`, `image`, `imagemap`) in the follow-up sweep.

## Authoritative detection (runtime probe)

Static "has no css-uri ⇒ orphaned" **over-predicts** — ZK's `zk.wcs` serves *some*
no-css-uri files via package aggregation. The only reliable check is a runtime probe:
load a page that renders the widget and scan `document.styleSheets` for the widget's own
**root** rule (a selector matching `^\.z-<comp>(?![\w-])`, not a compound ref from another
file). Absent while the widget renders ⇒ orphaned. (See `screenshot.spec.ts` ›
`orphaned-helper-css`.) Watch for two false classes:

- **Served-anyway**: `select` (`.z-select`), `cell` (`.z-cell`), `bandpopup`
  (`.z-bandpopup`) — no css-uri, but `zk.wcs` serves their own rules. Not orphans.
- **Dead CSS**: `space.css` (`.z-space`) and `toolbarpanel.css` (`.z-toolbarpanel`) target
  selectors that never render — `<space>` renders `.z-separator`, Toolbar `panel` mold
  renders `.z-toolbar-panel`/`.z-toolbar-content`. Bundling them would load no-op rules.

## Marble enforcement

- **Fix** an orphan by adding its source path to `normFiles` (`scripts/build-css.js`).
- **`WCS_SERVED_ALLOWLIST`** in `build-css.js` lists the probe-verified served-anyway files
  (select/cell/bandpopup) so the guard doesn't false-positive on them.
- **`assertNoOrphanComponentCss()`** (build-time, in `build-css.js`) fails the build if a
  non-empty auto-scanned component CSS is neither `css-uri`-backed nor in a bundle nor in
  the allowlist — so a new no-css-uri component can't silently orphan.

Gap log: `doc/skill-gaps.md` rows `2026-06-30 scrollbar` and `2026-06-30 div…imagemap`.
