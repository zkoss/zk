# The `.css.dsp` pipeline

**Source is `.css`. The shipped artifact is `.css.dsp`.** Retiring LESS retired the
*preprocessor*, not the delivery extension — `.css.dsp` is kept so ZK's existing registration and
DSP serving path work unchanged. A file with no DSP tags is a passthrough.

## ZK does not discover CSS by convention

Every component stylesheet is named individually by a `<css-uri>` entry in a ZK lang file, and
every entry is hard-wired to `.css.dsp`. The lang files are
`zul/src/main/resources/metainfo/zk/lang.xml` and, in `../zkcml`, the `lang-addon.xml` of `zkmax`
and `zkex` (`grep -c "<css-uri>"` counts the entries).

Plus one hard-wired in `zul/css/zk.wcs` (`norm.css.dsp`), and the entry point itself is fixed:
`StandardThemeProvider.DEFAULT_WCS = "~./zul/css/zk.wcs"`.

**Path resolution:** a relative `<css-uri>` resolves against the widget's JS package —
`<widget-package>` if declared, else derived from `<widget-class>` with the class segment dropped.
Full path = `js/<pkg-as-path>/<css-uri>`. An absolute `~./…` or `/…` is used as-is. Global
bundles come from ZK core convention, not from lang files.

**Consequence: adding a component CSS file is not enough.** If nothing requests it, it is dead.

## Two guards, in opposite directions — do not confuse them

`$ZK_HOME` is the directory that holds the `zk` and `zkcml` checkouts as siblings.

| Guard | Direction | Where |
|---|---|---|
| `node scripts/check-css-dsp.js --module zul --zk-home "$ZK_HOME"` | ZK registers it → the file **must exist** | `scripts/check-css-dsp.js` |
| `assertNoOrphanComponentCss()` | No `css-uri` → must **not** be emitted as an orphan; **fails the build** | `scripts/build-css.js` |

Together they make a silently-dead or silently-missing stylesheet impossible.

`check-css-dsp.js` reads the **real ZK lang files** as its source of truth —
`--zk-home` or `$ZK_HOME` names the workspace root for the zkcml sibling; with neither set, it uses `../zkcml` relative to the zk checkout it runs from. So `MISSING: 0` means *verified
against ZK's actual registrations*, not merely internally consistent. `font-awesome.css.dsp` is
deliberately out of scope: since 11.0 `zk.wcs` no longer lists it (Marble draws icons with Lucide
masks); a legacy theme jar such as iceblue adds its own copy through its `<Theme>ThemeURIHandler`.

## A source-file count above the registration count is normal

There are more source files under `js/zul/**/css` than registrations, because of deliberate bundling
declared in `build-css.js`:

- `combo.css.dsp` ← the `js/zul/inp/css` files (combobox, datebox, timebox, spinner, bandbox…)
- `footer.css.dsp` ← toolbarbutton, plus the classes `zk/flex.ts` toggles at runtime (stock ZK
  defines them in `footer.css.dsp`, so they live in the footer bundle here too). It is loaded last, so
  its rules win ties against component CSS.
- `norm.css.dsp` ← tokens + base + the components that have **no** `css-uri` at all
  (notification, toast, captcha — captcha is mold-only, so it must be bundled or it never loads;
  div, html, image, imagemap, label and span for the same reason, through `normFiles`)
- `tablet.css.dsp` ← the `zkmax/css/tablet/_*.css` files, concatenated; inserted as a disabled
  `<link>` on every request by `TabletThemeURIHandler`, and enabled by the client when `zk.mobile`
  (see `reference/verification.md`, tablet gate)

Where Marble's bundling differs from stock `lang.xml`: Marble merges datebox and timebox into
`combo.css.dsp` and toolbarbutton into `footer.css.dsp`; stock `lang.xml` uses `calendar.css.dsp`
for datebox, no `css-uri` for timebox, and `toolbar.css.dsp` for toolbarbutton. Stock ZK has no
`css-uri` for div, html, image, imagemap, label and span, so without the `norm` bundle they were
served by no stylesheet (an orphan sweep on 2026-06-30 found `.z-div` elements and labels unstyled).

`base/_reset.css` is intentionally **not** in the `norm` bundle — it ships as its own stylesheet
(`reset.css` / `reset-embed.css`) and is loaded ahead of the bundle. See
`doc/spec/reset-scoping.md` for the `browserDefault` swap.

## Fixing an orphaned component CSS

Add its source path to `normFiles` in `scripts/build-css.js` (Marble's global bundle `norm.css.dsp`;
`combo` and `footer` are the other bundles), give the component a `css-uri`, or delete the file if
it is dead. `assertNoOrphanComponentCss()` (build time) fails the build when a non-empty auto-scanned
component CSS is neither `css-uri`-backed nor in a bundle. The runtime probe is the
`orphaned-helper-css` test in `zkthemelab/src/test/playwright/screenshot.spec.ts`.

## Tokens and re-implemented stock selectors

Tokens live in `zul/src/main/resources/web/zul/css/tokens/_*.css` and bundle into `norm.css.dsp`.
Component CSS references tokens via `var(--zk-*)` and never hard-codes values. If a token's resolved
value is wrong, fix the token file, not every component that uses it. When re-implementing a stock
selector in Marble, replace the hex / pixel literals with `--zk-*` tokens.

## Framework contract classes

Marble defines the framework contract classes in `zul/src/main/resources/web/zul/css/base/_cssflex.css`
(the `.z-flex*` block, bundled into `footer.css.dsp` through `footerFiles` in `scripts/build-css.js`;
it is byte-equivalent to stock and must not be modified) and in `base/_dnd.css` (drag classes).
Marble styles `.z-dragged` with `opacity: .4`; stock IceBlue used a colour and a background. To see
the current result run `.claude/skills/zk-component-rules/tools/check-framework-classes.mjs` against `zul/codegen/resources/web`.
Marble once renamed `.z-flex` to `.z-d-flex` and `.z-flex-column` to `.z-flex-col` in its
utility-class pass, and the css-flex engine was inert theme-wide until the framework block was
restored (2026-06-05). Marble's stack utility is `.z-vstack`; Marble shipped a Bootstrap-style
default-rhythm margin rule and removed it because ZK subtracts widget margins in `applyCSSFlex` (see
`doc/spec/spacing-policy.md`).

## A trap that cost real debugging time

**A `.css.dsp` is not directly fetchable.**
A request for `/zkres/web/<version>/zul/css/norm.css.dsp` returns **404** (Marble is a core theme, so the URL has no theme segment; see `reference/verification.md`). It is only served inside the `zk.wcs`
aggregate. To verify served output: load a page, extract the `…/zul/css/zk.wcs` href, fetch that.

A relative `url()` inside `norm.css.dsp` is wrong, because the file is served *inside*
`zk.wcs` — a relative path resolves against the WCS URL, not the asset's location. Write
`url("~./zul/font/…")` (as `tokens/_fonts.css` does); `WcsExtendlet` rewrites it to a context- and
version-aware absolute URL in the `zk.wcs` response.

## Cascade layers are declared in the source files

Order is `zk-base < zk-components < zk-utilities`. Each file wraps itself in its layer, and
`assertLayer()` in `build-css.js` fails the build if the wrapper is missing or wrong. **Reset
rules must be in `zk-base`.** Component files self-wrap in `zk-components`.

## `_`-prefixed files are partials

They are concatenated into a bundle and never shipped alone. Two consequences:

- A file meant to ship individually **must not** start with `_`.
- **Never point zklessc or gulp at Marble's source tree.** Both treat `_`-prefixed files as LESS
  partials and skip them, and Marble's tokens, utilities and tablet files are all `_`-prefixed.
  The failure would be silent.

## The library's own stylesheet is the structural ground truth for library-wrapping components

For any component that wraps a JS library — goldenlayout, cropper/Jcrop, tbeditor, pdfviewer,
signature — **the library's own stylesheet is never loaded in ZK's integration**. Diff Marble's CSS
against the stylesheet the library ships (its original source CSS, not the generated `codegen`
output, which is built from Marble's own CSS) and classify every library rule:

- **structural-required** — geometry, position, z-index, display toggles, cursor, user-select →
  reproduce verbatim
- **decorative** — colours, radii, shadows → the theme's choice

"Library-owned DOM" means don't *re-DOM* it, not don't *style* it. Cropper shipped with all 8
Jcrop resize handles invisible, and goldenlayout with maximize broken, from exactly this omission.

Watch for body-level nodes (`lm_transition_indicator`, dragProxy) that scoped selectors miss, and
for specificity traps where a structural rule like `.lm_maximised{position:absolute}` is outranked
by the theme's own `.lm_item.lm_stack{position:relative}`.

## Minifier: Lightning CSS (background for anyone reintroducing CleanCSS)

`scripts/build-css.js` minifies with Lightning CSS. The comment above its minify step gives the reason CleanCSS was replaced. CleanCSS 5.3.3 does not understand the modern syntax this theme uses and fails silently: it reports the damage in `output.warnings`, never in `output.errors`, so an errors-only check ships broken CSS at exit 0. The comment records these measurements from 2026-09-03:

- a bare `@layer a, b;` order statement empties the whole output;
- `@scope (...) { … }` loses the rules inside it;
- `::picker(select)` and `appearance: base-select` rules are dropped with "Invalid property name … Ignoring." warnings.

Lightning CSS parses all three correctly, so the two workarounds the old minify step carried were removed with the swap. If CleanCSS is ever reintroduced, re-test these three cases and read the warnings output, not only the errors.
