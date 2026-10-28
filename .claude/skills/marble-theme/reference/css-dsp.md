# The `.css.dsp` pipeline

**Source is `.css`. The shipped artifact is `.css.dsp`.** Retiring LESS retired the
*preprocessor*, not the delivery extension — `.css.dsp` is kept so ZK's existing registration and
DSP serving path work unchanged. A file with no DSP tags is a passthrough.

## ZK does not discover CSS by convention

Every component stylesheet is named individually by a `<css-uri>` entry in a ZK lang file. There
are **78** such entries, 100% hard-wired to `.css.dsp`, with zero exceptions:

| Lang file | refs | unique |
|---|---|---|
| `zk/zul/src/main/resources/metainfo/zk/lang.xml` | 86 | 40 |
| `zkcml/zkmax/src/main/resources/metainfo/zk/lang-addon.xml` | 35 | 31 |
| `zkcml/zkex/src/main/resources/metainfo/zk/lang-addon.xml` | 8 | 7 |

Plus one hard-wired in `zul/css/zk.wcs` (`norm.css.dsp`), and the entry point itself is fixed:
`StandardThemeProvider.DEFAULT_WCS = "~./zul/css/zk.wcs"`.

**Path resolution:** a relative `<css-uri>` resolves against the widget's JS package —
`<widget-package>` if declared, else derived from `<widget-class>` with the class segment dropped.
Full path = `js/<pkg-as-path>/<css-uri>`. An absolute `~./…` or `/…` is used as-is. Global
bundles come from ZK core convention, not from lang files.

**Consequence: adding a component CSS file is not enough.** If nothing requests it, it is dead.

## Two guards, in opposite directions — do not confuse them

| Guard | Direction | Where |
|---|---|---|
| `node scripts/check-css-dsp.js --module zul --zk-home /Users/hawk/Documents/workspace/ZK10` | ZK registers it → the file **must exist** | `scripts/check-css-dsp.js` |
| `assertNoOrphanComponentCss()` | No `css-uri` → must **not** be emitted as an orphan; **fails the build** | `build-css.js:596–620` |

Together they make a silently-dead or silently-missing stylesheet impossible.

`check-css-dsp.js` reads the **real ZK lang files** as its source of truth —
`ZK_HOME` defaults to `/Users/hawk/Documents/workspace/ZK10`. So `MISSING: 0` means *verified
against ZK's actual registrations*, not merely internally consistent. `font-awesome.css.dsp` is
deliberately out of scope: `MarbleThemeProvider.beforeWidgetCSS()` returns null for it because
Marble draws icons with Lucide masks, so ZK never requests it.

## A source-file count above the registration count is normal

62 source files under `js/zul/**/css` map to 40 registrations, because of deliberate bundling
declared in `build-css.js`:

- `combo.css.dsp` ← the 7 `js/zul/inp/css` files (combobox, datebox, timebox, spinner, bandbox…)
- `footer.css.dsp` ← toolbarbutton, plus the classes `zk/flex.ts` toggles at runtime (stock ZK
  defines them in `footer.less`, so they live in the footer bundle here too)
- `norm.css.dsp` ← tokens + base + the components that have **no** `css-uri` at all
  (notification, toast, captcha — captcha is mold-only, so it must be bundled or it never loads)
- `tablet.css.dsp` ← the 13 `zkmax/css/tablet/_*.css` files, concatenated; injected by ZK's
  `TabletThemeURIHandler` on a mobile UA only

`base/_reset.css` is intentionally **not** in the `norm` bundle — it ships as its own stylesheet
(`reset.css` / `reset-embed.css`) and is loaded ahead of the bundle. See
`doc/spec/reset-scoping.md` for the `browserDefault` swap.

## Two traps that cost real debugging time

**1. DSP EL needs its taglib directive, or the rule is silently dropped.**
`${c:encodeURL(...)}` requires `<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>`
at the very top of the `.css.dsp`. Without it the DSP parser throws
`ELException: Function 'c:encodeURL' not found` and **drops the affected rule** — the rest of the
file still serves, so it presents as a CSS bug, not a parse error. This bit the self-hosted Inter
`@font-face`. `build-css.js` defines `DSP_CORE_TAGLIB` and prepends it *after* minification,
because the minifier chokes on the non-CSS `<%@ … %>`.

**2. A `.css.dsp` is not directly fetchable.**
`GET /zkau/web/marble/zul/css/norm.css.dsp` returns **404**. It is only served inside the `zk.wcs`
aggregate. To verify served output: load a page, extract the `…/zul/css/zk.wcs` href, fetch that.

Related: a relative `url()` inside `norm.css.dsp` is wrong, because the file is served *inside*
`zk.wcs` — a relative path resolves against the WCS URL, not the asset's location. Use
`c:encodeURL("~./marble/font/…")`, which yields a context- and version-aware absolute URL.

## Cascade layers are assigned at build time

Order is `zk-base < zk-components < zk-utilities`. `assertLayer()` in `build-css.js` verifies each
file lands in its expected layer and fails the build otherwise. **Reset rules must be in
`zk-base`.** Component files self-wrap in `zk-components`.

## `_`-prefixed files are partials

They are concatenated into a bundle and never shipped alone. Two consequences:

- A file meant to ship individually **must not** start with `_`.
- **Never point zklessc or gulp at Marble's source tree.** Both treat `_`-prefixed files as LESS
  partials and skip them, and Marble's tokens, utilities and tablet files are all `_`-prefixed.
  The failure would be silent.

## Upstream codegen is the structural ground truth for library-wrapping components

For any component that wraps a JS library — goldenlayout, cropper/Jcrop, tbeditor, pdfviewer,
signature — **the library's own stylesheet is never loaded in ZK's integration**. ZK's upstream
codegen `.css.dsp` (e.g.
`zkcml/zkmax/codegen/resources/web/js/zkmax/goldenlayout/css/goldenlayout.css.dsp`) reproduces the
library's structural CSS verbatim at the top of the file. Diff against it and classify every
upstream rule:

- **structural-required** — geometry, position, z-index, display toggles, cursor, user-select →
  reproduce verbatim
- **decorative** — colours, radii, shadows → the theme's choice

"Library-owned DOM" means don't *re-DOM* it, not don't *style* it. Cropper shipped with all 8
Jcrop resize handles invisible, and goldenlayout with maximize broken, from exactly this omission.

Watch for body-level nodes (`lm_transition_indicator`, dragProxy) that scoped selectors miss, and
for specificity traps where a structural rule like `.lm_maximised{position:absolute}` is outranked
by the theme's own `.lm_item.lm_stack{position:relative}`.

## Version drift (template-only)

zk has no theme-jar version coordination to drift — Marble ships inside `zul` and `zkmax`/`zkex`,
not as a separately versioned theme jar. The template's four-location (`pom.xml` / `config.xml` /
`package.json` / `Version.java`) drift check does not apply here.
