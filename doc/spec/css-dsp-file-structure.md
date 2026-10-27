# CSS DSP File Structure

This document defines all required `*.css.dsp` files for the **marble** theme, based on the Ice Blue Compact theme (`iceblue_c-10.3.0.1`) as the reference product.

> **Note (list is indicative, not the runtime contract):** the file list below was authored from the *source CSS tree* and can drift from what ZK actually requests at runtime. The authoritative, runtime-faithful coverage check is `scripts/check-css-dsp.js` (`npm run check:css-dsp`) — see the Path-resolution model below.

All files reside under:
```
zul/src/main/resources/web/
```

---

## 0. Path-resolution model — what decides each `.css.dsp` path

The path ZK *requests* a `.css.dsp` from is **not** decided by the theme's Java. It is decided in layers (verified against ZK 10.x source):

- **(a) Per-component path = lang-addon `<css-uri>` (relative) + `<widget-package>`.**
  `<css-uri>` is relative (e.g. `css/tbeditor.css.dsp`) and resolves against the widget's JS package directory declared by `<widget-package>` (e.g. `zkmax.tbeditor` → `~./js/zkmax/tbeditor/`). Full path = `~./js/` + package-as-path + `/` + css-uri.
  **Global bundles are decided by ZK core, not lang-addon:** `norm.css.dsp` + `font-awesome.css.dsp` are declared as `<stylesheet>` in the `zk.wcs` XML; `footer.css.dsp` is a hard-coded literal in `WcsExtendlet.java` (`"~./zul/css/footer.css.dsp"`). `WcsExtendlet.service()` assembles the single `zk.wcs` response as norm → every language-level `getCSSURIs()` → footer.
  The theme's own Java only **rewrites the `~./` prefix to `~./marble/`** via `MarbleThemeProvider.beforeWidgetCSS()` → `ServletFns.resolveThemeURL()`; and `build-css.js` **mirrors** the source tree to `zul/codegen/resources/web/<same-relative-path>` (it invents no paths).
- **(b) select / cell / bandpopup** are served via `zk.wcs` package **aggregation** (they are language-level css-uris). A standalone `.dsp` for these is optional / belt-and-suspenders — the rule loads either way.
- **(c) tbeditor / cropper / signature** live paths follow `<widget-package>`:
  `zkmax/{tbeditor,cropper,signature}/`. IceBlue's `inp/med/wgt` copies are stale legacy paths from older ZK versions — Marble ships only the live path (cleaner, not a gap).
- **(d) `daterangebox`** (zkmax) registers an absolute css-uri (`~./js/zkmax/db/css/daterangebox.css.dsp`). As of the **ZK 10.4.0** upgrade it ships live, so Marble now themes it (`../zkcml/zkmax/src/main/resources/web/js/zkmax/db/css/daterangebox.css`, auto-scanned 1:1) and it is **enforced** by `check-css-dsp.js` — the former `FORWARD_VERSION_SKIP` entry was removed. `confirmpopup` (CE, `js/zul/wgt/css/confirmpopup.css.dsp`) likewise graduated from an empty `build-css.js` stub to real theme CSS in the same upgrade.
- **(e) `scripts/check-css-dsp.js`** (`npm run check:css-dsp`) is the runtime-faithful coverage checker: it reads the ZK lang files for every `<css-uri>`, resolves each to a full path, and asserts it exists under `zul/codegen/resources/web/`. It is the **inverse** of build-css.js's `assertNoOrphanComponentCss()` — that guards *no-css-uri files emitted as orphans*; this guards *css-uri files ZK requests but the theme doesn't ship*.

---

## 1. ZK Core Components (`zul`)

### Top-level CSS
- `zul/css/norm.css.dsp`
- `zul/css/footer.css.dsp`
- `zul/font/font-awesome.css.dsp`

### Box / Layout
- `js/zul/box/css/box.css.dsp`
- `js/zul/box/css/layout.css.dsp`
- `js/zul/box/css/div.css.dsp`
- `js/zul/box/css/space.css.dsp`
- `js/zul/box/css/span.css.dsp`

### Layout
- `js/zul/layout/css/absolutelayout.css.dsp`
- `js/zul/layout/css/anchorlayout.css.dsp`
- `js/zul/layout/css/borderlayout.css.dsp`
- `js/zul/layout/css/html.css.dsp`
- `js/zul/layout/css/layout.css.dsp`

### Code
- `js/zul/code/css/codeeditor.css.dsp` — new in ZK 11.0.0 (CodeMirror 6 source editor, CE)

### Date / Calendar
- `js/zul/db/css/calendar.css.dsp`

### Grid
- `js/zul/grid/css/grid.css.dsp`

### Input
- `js/zul/inp/css/input.css.dsp`
- `js/zul/inp/css/combo.css.dsp`
- `js/zul/inp/css/slider.css.dsp`

### Menu
- `js/zul/menu/css/menu.css.dsp`
- `js/zul/menu/css/toolbarpanel.css.dsp`

### Mesh (shared grid/listbox infrastructure)
- `js/zul/mesh/css/auxhead.css.dsp`
- `js/zul/mesh/css/frozen.css.dsp`
- `js/zul/mesh/css/paging.css.dsp`

### Selection
- `js/zul/sel/css/listbox.css.dsp`
- `js/zul/sel/css/tree.css.dsp`
- `js/zul/sel/css/select.css.dsp`

### Tab
- `js/zul/tab/css/tabbox.css.dsp`

### Widget
- `js/zul/wgt/css/a.css.dsp`
- `js/zul/wgt/css/button.css.dsp`
- `js/zul/wgt/css/caption.css.dsp`
- `js/zul/wgt/css/checkbox.css.dsp`
- `js/zul/wgt/css/combobutton.css.dsp`
- `js/zul/wgt/css/confirmpopup.css.dsp` — new in ZK 10.4.0 (severity popover)
- `js/zul/wgt/css/groupbox.css.dsp`
- `js/zul/wgt/css/inputgroup.css.dsp`
- `js/zul/wgt/css/popup.css.dsp`
- `js/zul/wgt/css/progressmeter.css.dsp`
- `js/zul/wgt/css/rating.css.dsp`
- `js/zul/wgt/css/selectbox.css.dsp`
- `js/zul/wgt/css/separator.css.dsp`
- `js/zul/wgt/css/toolbar.css.dsp`
- `js/zul/wgt/css/cell.css.dsp`
- `js/zul/wgt/css/image.css.dsp`
- `js/zul/wgt/css/imagemap.css.dsp`
- `js/zul/wgt/css/label.css.dsp`
- `js/zul/wgt/css/misc.css.dsp`

### Window
- `js/zul/wnd/css/window.css.dsp`
- `js/zul/wnd/css/panel.css.dsp`
- `js/zul/wnd/css/bandpopup.css.dsp`

---

## 2. ZK Enterprise Components (`zkex`)

- `js/zkex/grid/css/grid.css.dsp`
- `js/zkex/inp/css/colorbox.css.dsp`
- `js/zkex/layout/css/columnlayout.css.dsp`
- `js/zkex/menu/css/fisheye.css.dsp`
- `js/zkex/pdfviewer/css/pdfviewer.css.dsp`
- `js/zkex/slider/css/rangeslider.css.dsp`
- `js/zkex/slider/css/sliderbuttons.css.dsp`

---

## 3. ZK Max Components (`zkmax`)

### Tablet
- `zkmax/css/tablet.css.dsp` — **built bundle** (no longer a stub). Touch-optimized
  overrides injected by ZK's `TabletThemeURIHandler` at cascade position 1, **only
  on a mobile User-Agent** (EE). Concatenated by `scripts/build-css.js` (build stage 5,
  `tabletFiles`) from per-component source partials, kept split during development:
  ```
  ../zkcml/zkmax/src/main/resources/web/zkmax/css/tablet/
    _tokens.css      # tablet-scoped --zk-touch-* tokens (MUST be first)
    _inputs.css      # textbox + combo bundle
    _buttons.css     # button / toolbarbutton / combobutton
    _selection.css   # checkbox / radio / switch
    _mesh.css        # listbox / grid / tree rows + headers + paging
    _calendar.css    # calendar / datebox popup day cells
    _window.css      # window / panel headers + affordances
    _scrollbar.css   # biglistbox WScroll touch width
  ```
  Regression-guarded by the Playwright `tablet` project (`zkthemelab/src/test/playwright/tablet.spec.ts`).

### Date
- `js/zkmax/db/css/daterangebox.css.dsp` — new in ZK 10.4.0 (date-range picker)

### Input
- `js/zkmax/inp/css/cascader.css.dsp`
- `js/zkmax/inp/css/chosenbox.css.dsp`
- `js/zkmax/inp/css/searchbox.css.dsp`
- `js/zkmax/inp/css/timepicker.css.dsp`

### Layout
- `js/zkmax/layout/css/cardlayout.css.dsp`
- `js/zkmax/layout/css/goldenlayout.css.dsp`
- `js/zkmax/layout/css/linelayout.css.dsp`
- `js/zkmax/layout/css/organigram.css.dsp`
- `js/zkmax/layout/css/portallayout.css.dsp`
- `js/zkmax/layout/css/rowlayout.css.dsp`
- `js/zkmax/layout/css/scrollview.css.dsp`
- `js/zkmax/layout/css/splitlayout.css.dsp`
- `js/zkmax/layout/css/tablelayout.css.dsp`

### Media
- `js/zkmax/med/css/camera.css.dsp`
- `js/zkmax/med/css/video.css.dsp`

### Navigation
- `js/zkmax/nav/css/anchornav.css.dsp`
- `js/zkmax/nav/css/coachmark.css.dsp`
- `js/zkmax/nav/css/nav.css.dsp`

### Selection
- `js/zkmax/sel/css/listbox.css.dsp`
- `js/zkmax/sel/css/tree.css.dsp`

### Widgets
- `js/zkmax/wgt/css/drawer.css.dsp`
- `js/zkmax/wgt/css/dropupload.css.dsp`
- `js/zkmax/wgt/css/stepbar.css.dsp`

### Other
- `js/zkmax/barscanner/css/barcodescanner.css.dsp`
- `js/zkmax/big/css/biglistbox.css.dsp`
- `js/zkmax/cropper/css/cropper.css.dsp`
- `js/zkmax/goldenlayout/css/goldenlayout.css.dsp`
- `js/zkmax/grid/css/grid.css.dsp`
- `js/zkmax/signature/css/signature.css.dsp`
- `js/zkmax/slider/css/multislider.css.dsp`
- `js/zkmax/tbeditor/css/tbeditor.css.dsp`

---

## 4. Authoring model: plain CSS (decided 2026-09-03)

Source: the evaluation this section is taken from (Option 1, decisions D1–D6) is kept locally under `tasks/` as `tasks/zk11-less-dsp-deprecation-evaluation-已加進SPEC.md`. It is gitignored and is not part of the repository.

- **Decision:** Marble's stylesheets are plain `.css`. No LESS and no DSP preprocessing is needed to build them.
- **Evidence (zk):** zk tracks 0 `.less` files. `build.gradle` (line 192) states LESS was retired in ZK 11 (ZK-6112). `scripts/build-css.js` is the only CSS pipeline.
- **Still in zkcml (not settled):** the zkcml repo still has 66 `.less` files, and `zkcml/zkthemebuilder/pom.xml` (line 187) still runs a `zklessc` step.
- **Open:** D8 (does IceBlue ship pure CSS in 11.0, or stay on LESS?) and D9 (does the 11.0 scope fit the end-of-October date?). Both are **open**; this spec does not decide them.

## 5. Asset URLs in plain CSS

- **Since ZK 11.0.0,** a plain stylesheet may write `url(~./...)` with no DSP tag.
- **Mechanism:** `WcsExtendlet` (`zk/src/main/java/org/zkoss/zk/ui/http/WcsExtendlet.java`, lines ~157–160, method `encodeClassWebURLs`) encodes every such URL when it serves the stylesheet. The context path and build prefix are included.
- **Coverage:** `zktest` `B110_ZK_6112Test`.
- **DSP files:** a `.css.dsp` file still uses `${c:encodeURL(...)}` for the same purpose.

### 5.1 Design tokens are declared on `:root`, not on every element or `*` (D1)

Source: the conclusion this section is taken from (D1, Option A) is kept locally under `tasks/`. It is gitignored and is not part of the repository.

- **Rule:** All Marble `--zk-*` tokens are declared on `:root` (including the `:root[data-brand="…"]` variants), not repeated on each element or on `*`.
- **Implementation:** The token files under `zul/src/main/resources/web/zul/css/tokens/` declare their tokens on `:root`, for example `_colors.css:14` and `_zindex.css:24`.
- **Measured basis (synthetic page, not a ZK page):** Marble has 592 unique `--zk-*` names (652 declarations). On a synthetic page with 10,000 `div` elements (Chrome 154.0.8037.92, median values), with 0 / 600 / 3,000 / 10,000 `:root` declarations, the time to recompute after changing a referenced variable was 55.4 / 55.0 / 55.4 / 58.0 ms. After changing an unreferenced variable it was 13.6 / 13.7 / 14.3 / 15.6 ms. The number of `:root` declarations had no measurable effect; the cost depends on the scope where the variable is used.
- **Why not per-element:** Declaring the tokens repeatedly on each element once caused a memory regression in Chromium 127 (fixed in Chromium 132).
- **Note:** Not measured on Firefox and Safari. The CSS byte size is the actual cost of the tokens.

### 5.2 Icons, component images and fonts are served by URL, not as data URIs (D5)

Source: the conclusion this section is taken from (D5) is kept locally under `tasks/`. It is gitignored and is not part of the repository.

- **Rule:** The icon set, component images and fonts are served by URL (`url(~./zul/img/...)`), not as data URIs, and no `<function>` extension is added to `zk.wcs`.
- **Implementation:** `writeIconFile` in `scripts/build-css.js` writes each icon to `zul/img/icons/<name>.svg` and returns `url(~./zul/img/icons/<name>.svg)`. `zul/src/main/resources/web/zul/css/base/_icons.css` applies `mask-image` with `var(--_icon)`. Component images, for example in `base/_dnd.css`, use `url(~./zul/img/marble/<name>.svg)`. The `@font-face` convention for fonts and its mapping in doc/spec/DESIGN.md: TBD.
- **Measured at the change (from the message of commit 606a0c25ba):** zk.wcs went from 1,371,519 B to 537,589 B. The commit message notes that it no longer contains any data URI.
