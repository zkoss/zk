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
   (for example `norm.css.dsp`, `combo.css.dsp`, `footer.css.dsp`). Required for any component with **no `css-uri`**: helpers, mold-only
   widgets, core-JS-drawn DOM, and real widgets that stock ZK styles only in `norm.css.dsp`.

## The trap

If a **no-`css-uri`** component's CSS is left to the 1:1 auto-scan, the build emits an
orphaned `<comp>.css.dsp` that **ZK never requests**. The rule is in no served stylesheet;
the widget is unstyled; nothing errors. This bit `scrollbar`, `div`, `label`, `span`, `html`, `image` and `imagemap`.

## Authoritative detection (runtime probe)

Static "has no css-uri ⇒ orphaned" **over-predicts** — ZK's `zk.wcs` serves *some*
no-css-uri files via package aggregation. The only reliable check is a runtime probe:
load a page that renders the widget and scan `document.styleSheets` for the widget's own
**root** rule (a selector matching `^\.z-<comp>(?![\w-])`, not a compound ref from another
file). Absent while the widget renders ⇒ orphaned. Watch for two false classes:

- **Served-anyway**: `select` (`.z-select`), `cell` (`.z-cell`), `bandpopup`
  (`.z-bandpopup`) — no css-uri, but `zk.wcs` serves their own rules. Not orphans.
- **Dead CSS**: a CSS file whose root selector never renders (for example a rule for
  `.z-space` when `<space>` renders `.z-separator`) is not an orphan; bundling it would only load
  no-op rules.

## Fixing an orphan

Add the component CSS to a global bundle, or give the component a `css-uri`. The build step that guards against orphans is a theme build concern; see the theme's build documentation.
