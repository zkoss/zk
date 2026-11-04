# CSS DSP File Structure

This document explains how the **marble** theme's `*.css.dsp` files are named and found, based on the Ice Blue Compact theme (`iceblue_c-10.3.0.1`) as the reference product.

> **Note (file list):** the list of `.css.dsp` files is in the sources, because a copy in this doc drifts from what ZK actually requests at runtime. The authoritative, runtime-faithful coverage check is `scripts/check-css-dsp.js` (`node scripts/check-css-dsp.js --module zul`) — see the Path-resolution model below. The files themselves are listed by `lang.xml` and `lang-addon.xml` (`<css-uri>`), and built by `scripts/build-css.js`.

All files reside under:
```
zul/src/main/resources/web/
```

---

## 0. Path-resolution model — what decides each `.css.dsp` path

The path ZK *requests* a `.css.dsp` from is **not** decided by the theme's Java. It is decided in layers (verified against ZK 10.x source):

- **(a) Per-component path = lang-addon `<css-uri>` (relative) + `<widget-package>`.**
  `<css-uri>` is relative (e.g. `css/tbeditor.css.dsp`) and resolves against the widget's JS package directory declared by `<widget-package>` (e.g. `zkmax.tbeditor` → `~./js/zkmax/tbeditor/`). Full path = `~./js/` + package-as-path + `/` + css-uri.
  **Global bundles are decided by ZK core, not lang-addon:** `norm.css.dsp` is declared as `<stylesheet>` in the `zk.wcs` XML; `footer.css.dsp` is a hard-coded literal in `WcsExtendlet.java` (`"~./zul/css/footer.css.dsp"`). `WcsExtendlet.service()` assembles the single `zk.wcs` response as norm → every language-level `getCSSURIs()` → footer. Marble ships no Font Awesome stylesheet (see `StandardThemeProvider.java`).
  The theme's own Java only **rewrites the `~./` prefix to `~./marble/`** via `StandardThemeProvider.beforeWidgetCSS()` → `ServletFns.resolveThemeURL()`; and `build-css.js` **mirrors** the source tree to `zul/codegen/resources/web/<same-relative-path>` (it invents no paths).
- **(b) select / cell / bandpopup** have no `css-uri` and no CSS file of their own. Their `.z-select`, `.z-cell` and `.z-bandpopup` rules come from `listbox.css`, `grid.css` / `frozen.css` and `bandbox.css`. An earlier note said these files were served by `zk.wcs` package aggregation; that was a false positive of a selector-only probe, and no such mechanism exists.
- **(c) tbeditor / cropper / signature** live paths follow `<widget-package>`:
  `zkmax/{tbeditor,cropper,signature}/`. IceBlue's `inp/med/wgt` copies are stale legacy paths from older ZK versions — Marble ships only the live path (cleaner, not a gap).
- **(d) `daterangebox`** (zkmax) registers an absolute css-uri (`~./js/zkmax/db/css/daterangebox.css.dsp`). As of the **ZK 10.4.0** upgrade it ships live, so Marble now themes it (`../zkcml/zkmax/src/main/resources/web/js/zkmax/db/css/daterangebox.css`, auto-scanned 1:1) and it is **enforced** by `check-css-dsp.js` — the former `FORWARD_VERSION_SKIP` entry was removed. `confirmpopup` (CE, `js/zul/wgt/css/confirmpopup.css.dsp`) likewise graduated from an empty `build-css.js` stub to real theme CSS in the same upgrade.
- **(e) `scripts/check-css-dsp.js`** (`node scripts/check-css-dsp.js --module zul`) is the runtime-faithful coverage checker: it reads the ZK lang files for every `<css-uri>`, resolves each to a full path, and asserts it exists under `zul/codegen/resources/web/`. It is the **inverse** of build-css.js's `assertNoOrphanComponentCss()` — that guards *no-css-uri files emitted as orphans*; this guards *css-uri files ZK requests but the theme doesn't ship*.
- **(f) Tablet bundle** `zkmax/css/tablet.css.dsp` is a **built bundle**: touch-optimized overrides injected by ZK's `TabletThemeURIHandler` at cascade position 1, **only on a mobile User-Agent** (EE). `scripts/build-css.js` (`buildTabletSheet()`, `tabletFiles`) concatenates it from per-component partials under `../zkcml/zkmax/src/main/resources/web/zkmax/css/tablet/`, kept split during development; `_tokens.css` (the tablet-scoped `--zk-touch-*` tokens) MUST be first. Regression-guarded by the Playwright `tablet` project (`zkthemelab/src/test/playwright/tablet.spec.ts`).

---

## 4. Authoring model: plain CSS (decided 2026-09-03)

- **Decision:** Marble's stylesheets are plain `.css`. No LESS and no DSP preprocessing is needed to build them.
- **Evidence (zk):** zk tracks 0 `.less` files. `build.gradle` (line 192) states LESS was retired in ZK 11 (ZK-6112). `scripts/build-css.js` is the only CSS pipeline.
- **Still in zkcml (not settled):** the zkcml repo still has `.less` files, and `zkcml/zkthemebuilder/pom.xml` (line 187) still runs a `zklessc` step.
- **Open:** whether IceBlue ships pure CSS in 11.0 or stays on LESS. This is **open**; this spec does not decide it.

## 5. Asset URLs in plain CSS

- **Since ZK 11.0.0,** a plain stylesheet may write `url(~./...)` with no DSP tag.
- **Mechanism:** `WcsExtendlet` (`zk/src/main/java/org/zkoss/zk/ui/http/WcsExtendlet.java`, lines ~157–160, method `encodeClassWebURLs`) encodes every such URL when it serves the stylesheet. The context path and build prefix are included.
- **Coverage:** `zktest` `B110_ZK_6112Test`.
- **DSP files:** a `.css.dsp` file still uses `${c:encodeURL(...)}` for the same purpose.

### 5.1 Design tokens are declared on `:root`, not on every element or `*`

- **Rule:** All Marble `--zk-*` tokens are declared on `:root` (including the `:root[data-brand="…"]` variants), not repeated on each element or on `*`.
- **Implementation:** The token files under `zul/src/main/resources/web/zul/css/tokens/` declare their tokens on `:root`, for example `_colors.css:14` and `_zindex.css:24`.
- **Measured basis (synthetic page, not a ZK page):** On a synthetic page with 10,000 `div` elements (Chrome 154.0.8037.92, median values), with 0 / 600 / 3,000 / 10,000 `:root` declarations, the time to recompute after changing a referenced variable was 55.4 / 55.0 / 55.4 / 58.0 ms. After changing an unreferenced variable it was 13.6 / 13.7 / 14.3 / 15.6 ms. The number of `:root` declarations had no measurable effect; the cost depends on the scope where the variable is used.
- **Why not per-element:** Declaring the tokens repeatedly on each element once caused a memory regression in Chromium 127 (fixed in Chromium 132).
- **Note:** Not measured on Firefox and Safari. The CSS byte size is the actual cost of the tokens.

### 5.2 Icons, component images and fonts are served by URL, not as data URIs

- **Rule:** The icon set, component images and fonts are served by URL (`url(~./zul/img/...)`), not as data URIs, and no `<function>` extension is added to `zk.wcs`.
- **Measured basis:** with URLs the `zk.wcs` aggregate went from 1,371,519 B to 537,589 B (commit `606a0c25ba`, from its commit message; not re-measured).
- **Implementation:** `writeIconFile` in `scripts/build-css.js` writes each icon to `zul/img/icons/<name>.svg` and returns `url(~./zul/img/icons/<name>.svg)`. `zul/src/main/resources/web/zul/css/base/_icons.css` applies `mask-image` with `var(--_icon)`. Component images, for example in `base/_dnd.css`, use `url(~./zul/img/marble/<name>.svg)`. The `@font-face` convention for fonts is in DESIGN.md (§7, Font loading).
