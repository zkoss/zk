#!/usr/bin/env node
//
// Ported from zkThemeTemplate/scripts/build-css.js on 2026-09-10 (Marble -> zk migration, F26).
// This file is now maintained in zk; the template original is no longer the source of truth
// for it and is not edited to reflect changes made here.
//
// One repository root per module, instead of the template's single webDir/themeDir:
//
//   --module | source web root                        | output root (default)                | lang file(s)
//   zul      | ZK_ROOT/zul/src/main/resources/web      | ZK_ROOT/zul/codegen/resources/web    | ZK_ROOT/zul/src/main/resources/metainfo/zk/lang.xml
//   zkmax    | ZKCML_ROOT/zkmax/src/main/resources/web | ZKCML_ROOT/zkmax/codegen/resources/web | ZKCML_ROOT/zkmax/src/main/resources/metainfo/zk/lang-addon.xml
//   zkex     | ZKCML_ROOT/zkex/src/main/resources/web  | ZKCML_ROOT/zkex/codegen/resources/web  | ZKCML_ROOT/zkex/src/main/resources/metainfo/zk/lang-addon.xml
//
// where ZK_ROOT = path.resolve(__dirname, '..') (this repo) and
// ZKCML_ROOT = path.resolve(ZK_ROOT, '../zkcml') (the enterprise sibling repo).
//
// zul only, this also vendors the Inter web font from the @fontsource-variable/inter npm
// package (ZK_ROOT/node_modules) into zul/font/ under the zul output root — see FONT_SOURCES.
//
// Usage:
//   node scripts/build-css.js --module zul|zkmax|zkex [--out <dir>] [--dev] [--emit-docs]
// Exit: 2 = missing/unknown --module (usage error)

const fs = require('fs');
const path = require('path');
const { transform: lightningTransform } = require('lightningcss');

function argFor(flag) {
    const i = process.argv.indexOf(flag);
    return i >= 0 && i + 1 < process.argv.length ? process.argv[i + 1] : undefined;
}

const ZK_ROOT = path.resolve(__dirname, '..');
const ZKCML_ROOT = path.resolve(ZK_ROOT, '../zkcml');

// Per-module source web root + default output root. See the table in the header comment.
const MODULES = {
    zul: {
        webDir: path.join(ZK_ROOT, 'zul/src/main/resources/web'),
        outDir: path.join(ZK_ROOT, 'zul/codegen/resources/web'),
    },
    zkmax: {
        webDir: path.join(ZKCML_ROOT, 'zkmax/src/main/resources/web'),
        outDir: path.join(ZKCML_ROOT, 'zkmax/codegen/resources/web'),
    },
    zkex: {
        webDir: path.join(ZKCML_ROOT, 'zkex/src/main/resources/web'),
        outDir: path.join(ZKCML_ROOT, 'zkex/codegen/resources/web'),
    },
};

const moduleName = argFor('--module');
if (!moduleName || !MODULES[moduleName]) {
    console.error('Usage: node scripts/build-css.js --module zul|zkmax|zkex [--out <dir>] [--dev] [--emit-docs]');
    process.exit(2);
}

const webDir = MODULES[moduleName].webDir;
const themeDir = argFor('--out') || MODULES[moduleName].outDir;

// True only for a relPath that belongs to the selected module, by the same path-prefix
// convention the template used across modules (js/zul/, js/zkmax/, js/zkex/, zul/, zkmax/, zkex/).
function belongsToModule(relPath, mod) {
    return relPath.startsWith(`js/${mod}/`) || relPath.startsWith(`${mod}/`);
}

// DSP taglib directive enabling ${c:encodeURL(...)} in a .css.dsp (the `c` prefix).
// Required by norm.css.dsp's self-hosted-font @font-face; mirrors ZK's font DSPs.
const DSP_CORE_TAGLIB = '<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>';

// Dev builds (watch / build:css:dev) stay unminified so hot-swapped CSS is
// readable in DevTools; the packaged build (build:css) is minified.
const isDev = process.argv.includes('--dev');
// Side outputs (icon-index.md, icons-lucide.zul) only run when explicitly requested — see
// generateIconIndexMd / generateIconsZul and build() stage 1b/1c below.
const emitDocs = process.argv.includes('--emit-docs');
// Minifier: Lightning CSS, not CleanCSS. CleanCSS 5.3.3 does not understand the modern
// syntax this theme is built on and fails SILENTLY — it reports the damage in
// `output.warnings`, never in `output.errors`, so an errors-only check ships broken CSS at
// exit 0. Measured on this source tree (2026-09-03):
//   - a bare `@layer a, b;` order statement EMPTIES the whole output;
//   - `@scope (...) { … }` loses the rules inside it;
//   - `::picker(select)` / `appearance: base-select` rules are dropped with
//     "Invalid property name … Ignoring." (3 live warnings before this swap).
// Lightning CSS parses all of them correctly, which removes the two workarounds this
// function used to carry. Bootstrap 6 replaced clean-css for the same reason — see
// ../zkThemeTemplate-iceblue/doc/css-preprocessor-industry-direction.md §11 D1.
//
// No `targets` is set on purpose: this theme is modern-browsers-only, so nothing should be
// downlevelled. Adding targets would start rewriting output and is a separate decision.

// DSP EL (`${c:encodeURL("~./marble/font/x.woff2")}`) is not valid CSS. CleanCSS merely
// tolerated it; Lightning CSS's parser rejects the file outright ("Unexpected end of
// input"). Mask each expression with an inert identifier before parsing and restore it
// afterwards. Two sites today, both inside url() in tokens/_fonts.css.
const DSP_EL_RE = /\$\{[^}]*\}/g;
const DSP_EL_MASK_RE = /ZKDSPEL(\d+)ZZ/g;

// A number carrying 7+ significant digits is silently ROUNDED TO 6 when it sits inside a
// custom property. Real properties are parsed as typed values and keep full precision
// (`z-index:9999999` survives); a custom property is an untyped token stream, and Lightning
// CSS re-formats the numeric tokens in it through an f32 with 6 significant digits.
// Measured on 1.33.0, no warning of any kind:
//   --x:9999999   -> 10000000      --x:2147483647 -> 2147480000
//   --x:1000001   -> 1000000       --x:0.123456789 -> .123457
// This bit `--zk-index-error: 9999999` (the fatal-JS-error stacking value, deliberately
// copied from ZK core's own theme CSS — see doc/zindex-audit.md), which shipped as
// 10000000 and broke the zindex project's computed-style assertion.
// Mask those tokens with an inert identifier — legal anywhere in a custom property's token
// stream — and restore them verbatim afterwards, exactly as the DSP EL above is handled.
const CUSTOM_PROP_DECL_RE = /(--[\w-]+\s*:)([^;}]*)/g;
const NUM_TOKEN_RE = /\d*\.?\d+(?:[eE][+-]?\d+)?/g;
const LONG_NUM_MASK_RE = /ZKNUM(\d+)ZZ/g;
const MAX_SAFE_SIG_DIGITS = 6;

function maskLongNumbers(css, store) {
    return css.replace(CUSTOM_PROP_DECL_RE, (_, head, value) =>
        head + value.replace(NUM_TOKEN_RE, (num) => {
            const digits = num.replace(/\D/g, '').replace(/^0+/, '');
            if (digits.length <= MAX_SAFE_SIG_DIGITS) return num;
            return `ZKNUM${store.push(num) - 1}ZZ`;
        }));
}

function minifyCss(css) {
    if (isDev || !css) return css;
    const els = [];
    const nums = [];
    const masked = maskLongNumbers(
        css.replace(DSP_EL_RE, (m) => `ZKDSPEL${els.push(m) - 1}ZZ`),
        nums,
    );
    let code;
    try {
        const res = lightningTransform({
            filename: 'theme.css',
            code: Buffer.from(masked),
            minify: true,
        });
        // Surface warnings. CleanCSS's silent-warning behaviour is exactly what this swap
        // was meant to end, so never let one pass unprinted.
        for (const w of res.warnings || []) {
            console.warn(`  ⚠ minify warning: ${w.message || w}`);
        }
        code = res.code.toString();
    } catch (e) {
        // Never break the build / empty a file on a minifier hiccup.
        console.warn(`  ⚠ minify failed, writing raw CSS: ${e.message}`);
        return css;
    }
    return code
        .replace(LONG_NUM_MASK_RE, (_, i) => nums[Number(i)])
        .replace(DSP_EL_MASK_RE, (_, i) => els[Number(i)]);
}

// Guard: component/base CSS self-declares its cascade layer IN SOURCE (readability +
// correctness — see doc/spec/layer-architecture-review.md). The build no longer injects
// layers; it only VERIFIES the wrapper is present, so a new file that forgets it fails the
// build instead of silently shipping unlayered (which would beat utilities + user CSS).
function assertLayer(relPath, content, layer) {
    if (content.trim() && !new RegExp(`@layer\\s+${layer}\\s*\\{`).test(content)) {
        throw new Error(
            `CSS layer guard: ${relPath} must wrap its rules in "@layer ${layer} { … }". ` +
            'All component/base CSS belongs to a cascade layer.');
    }
}

// norm.css.dsp = tokens + base + global styles (loaded first by WCS). zul only.
const normFiles = [
    'zul/css/tokens/_fonts.css',
    'zul/css/tokens/_colors.css',
    'zul/css/tokens/_typography.css',
    'zul/css/tokens/_spacing.css',
    'zul/css/tokens/_elevation.css',
    'zul/css/tokens/_motion.css',
    'zul/css/tokens/_shape.css',
    'zul/css/tokens/_sizing.css',
    'zul/css/tokens/_splitter.css',
    // Stacking scale — --zk-index-* (load-bearing z-index values only). Unlayered
    // :root defs. ZK sets floating-widget z-index inline at runtime (base 1800), so
    // only non-floating elements are tokenized here. See doc/spec/zindex-scale.md.
    'zul/css/tokens/_zindex.css',
    // Component Theme Variables — per-component appearance vars (--zk-<comp>-*).
    // Unlayered :root defs (like the other token files); placed after the base
    // tokens it references. See doc/spec/component-theme-variables.md.
    'zul/css/tokens/_component-theme.css',
    // forced-colors (Windows High-Contrast) a11y guards — GAP 5. Unlayered, and
    // placed last among tokens so its `--zk-focus-ring` override wins over
    // _colors.css. See doc/spec/forced-colors.md.
    'zul/css/tokens/_forced-colors.css',
    // NOTE: base/_reset.css is intentionally NOT bundled here. It is emitted as its own
    // stylesheet (reset.css / reset-embed.css) and loaded ahead of this bundle by
    // StandardThemeProvider.getThemeURIs (zul), so the theme can swap a host-safe variant for
    // JS-Embed pages. See buildResetVariants() below and doc/spec/reset-scoping.md.
    // Utility CSS — split by sidebar category (see usecase/index.zul "Utility CSS").
    // NOTE: no default-rhythm file — widgets carry zero default margins (ZK's
    // flex sizing subtracts child margins; spacing is opt-in via _stack.css /
    // _spacing.css — see doc/spec/spacing-policy.md, gap log 2026-06-05).
    'zul/css/utility/_colors.css',
    'zul/css/utility/_elevation.css',
    'zul/css/utility/_components.css',
    'zul/css/utility/_spacing.css',
    'zul/css/utility/_layout.css',
    'zul/css/utility/_typography.css',
    'zul/css/utility/_borders.css',
    'zul/css/utility/_stack.css',
    'zul/css/utility/_print.css',
    'zul/css/base/_icons.css',
    // Notification has no css-uri mold registration in lang.xml (moldOnly, no mold element),
    // so it must be bundled here to ensure styles are always loaded.
    'js/zul/wgt/css/notification.css',
    // Toast (zkmax moldOnly) has no css-uri in lang.xml, same pattern as notification.
    'js/zul/wgt/css/toast.css',
    // Captcha has no css-uri in lang.xml (mold only), must be bundled here.
    'js/zul/wgt/css/captcha.css',
    // misc.css holds page-level styles (z-modal-mask, z-loading, z-loading-icon, tooltip…)
    // that are emitted directly by ZK core (zAu.cmd0.showBusy / zUtl.progressbox),
    // not by any widget — so it must always be in the global bundle.
    'js/zul/wgt/css/misc.css',
    // Scrollbar is drawn by the zul.Scrollbar helper (instantiated by MeshWidget /
    // LayoutRegion when org.zkoss.zul.nativebar=false), not a registered widget — so it
    // has NO lang.xml css-uri and, like notification/toast/captcha above, must be bundled
    // here or its CSS never loads (the 1:1 auto-scan emits an orphaned scrollbar.css.dsp
    // that ZK never requests). Stock ZK keeps scrollbar styling in the global norm.less too.
    'js/zul/wgt/css/scrollbar.css',
    // The widgets below are REAL widgets but have NO lang.xml css-uri (stock ZK styles
    // them in the global norm.less, not via a per-component mold css-uri), so the 1:1
    // auto-scan would emit orphaned *.css.dsp files that ZK never requests and the widget
    // renders with browser/inherited defaults. Same loading trap as scrollbar/notification
    // above. Found by the 2026-06-30 orphan sweep via runtime document.styleSheets probe
    // (gap log 2026-06-30). NOTE: select.css/cell.css/bandpopup.css are NOT here — they have
    // no css-uri either but ZK's zk.wcs DOES serve them via package aggregation (probe-
    // verified), so they live in WCS_SERVED_ALLOWLIST below instead.
    'js/zul/wgt/css/label.css',
    'js/zul/box/css/div.css',
    'js/zul/box/css/span.css',
    'js/zul/layout/css/html.css',
    'js/zul/wgt/css/image.css',
    'js/zul/wgt/css/imagemap.css',
];

// combo.css.dsp = merged dropdown-type input components. zul only.
const comboFiles = [
    'js/zul/inp/css/combobox.css',
    'js/zul/inp/css/datebox.css',
    'js/zul/inp/css/timebox.css',
    'js/zul/inp/css/spinner.css',
    'js/zul/inp/css/bandbox.css',
];

// tablet.css.dsp = touch overrides, injected by ZK's TabletThemeURIHandler at
// cascade position 1 ONLY on a mobile User-Agent (EE). Split by component during
// development; concatenated into a single tablet.css.dsp at build time. zkmax only.
// _tokens.css MUST be first so its :root touch tokens cascade to the rest.
const tabletFiles = [
    'zkmax/css/tablet/_tokens.css',
    'zkmax/css/tablet/_inputs.css',
    'zkmax/css/tablet/_buttons.css',
    'zkmax/css/tablet/_selection.css',
    'zkmax/css/tablet/_slider.css',
    'zkmax/css/tablet/_mesh.css',
    'zkmax/css/tablet/_calendar.css',
    'zkmax/css/tablet/_wheel.css',
    'zkmax/css/tablet/_menu.css',
    'zkmax/css/tablet/_tabbox.css',
    'zkmax/css/tablet/_window.css',
    'zkmax/css/tablet/_scrollbar.css',
    'zkmax/css/tablet/_feedback.css',
];

// footer.css.dsp = loaded last by WCS. zul only.
const footerFiles = [
    'js/zul/wgt/css/toolbarbutton.css',
    'js/zul/wgt/css/loadingbar.css',
    'js/zul/wnd/css/messagebox.css',
    // Framework css-flex classes (z-flex/z-flex-row/z-flex-column/z-flex-item)
    // toggled at runtime by zk/flex.ts — stock ZK defines them in footer.less,
    // so they live in the footer bundle here too. See the file's header comment.
    'zul/css/base/_cssflex.css',
    // Framework drag-and-drop + frozen classes (z-dragged/z-drag-over/z-drag-ghost/
    // z-drop-ghost/-content/-icon/-text/-allow/-disallow, z-word-nowrap) toggled at
    // runtime by zk/widget.ts + zul/mesh/Frozen.ts. Stock ships no CSS for most, so the
    // theme must define them. See the file's header comment + doc/contracts/framework-classes.md.
    'zul/css/base/_dnd.css',
];

// Files merged into another CSS file (excluded from 1:1 auto-scan). zul only.
// errorbox.css is merged into input.css because lang.xml registers no css-uri for errorbox.
const extraMergedFiles = [
    'js/zul/wgt/css/errorbox.css',
];

// Files that are merged (excluded from 1:1 auto-scan) — non-empty only for zul; zkmax/zkex
// have no merge lists above, so their auto-scan runs 1:1 with nothing excluded.
const mergedFiles = moduleName === 'zul'
    ? new Set([...normFiles.filter(f => f.startsWith('js/')), ...comboFiles, ...footerFiles, ...extraMergedFiles])
    : new Set();

// --- Orphan-CSS guard data (see assertNoOrphanComponentCss + doc/skill-gaps.md 2026-06-30) ---
//
// `.css.dsp` basenames that ZK requests via a `<css-uri>` in lang.xml / lang-addon.xml. ZK
// auto-loads these when the widget is on the page, so a 1:1 auto-scanned dsp serves correctly.
// One combined set across zul/zkmax/zkex — the orphan guard only ever inspects the selected
// module's own scanned files (see assertNoOrphanComponentCss), so entries for other modules
// are simply never matched against.
// REGENERATE on a ZK upgrade with (zul + zkmax + zkex lang files):
//   grep -rhoE "css-uri>[^<]+" <ZK>/zk/zul/.../lang.xml <ZK>/zkcml/zk{max,ex}/.../lang-addon.xml \
//     | sed -E 's#.*/##' | sort -u
const CSS_URI_BACKED = new Set([
    // zul (CE)
    'a.css.dsp', 'absolutelayout.css.dsp', 'anchorlayout.css.dsp', 'auxhead.css.dsp',
    'avatar.css.dsp', 'avatargroup.css.dsp', 'badge.css.dsp',
    'borderlayout.css.dsp', 'box.css.dsp', 'breadcrumb.css.dsp', 'button.css.dsp', 'calendar.css.dsp',
    'caption.css.dsp', 'carousel.css.dsp', 'checkbox.css.dsp', 'chip.css.dsp',
    'codeeditor.css.dsp', 'combo.css.dsp', 'combobutton.css.dsp',
    'confirmpopup.css.dsp',
    'frozen.css.dsp', 'grid.css.dsp', 'groupbox.css.dsp', 'input.css.dsp', 'inputgroup.css.dsp',
    'layout.css.dsp', 'listbox.css.dsp', 'menu.css.dsp', 'paging.css.dsp', 'panel.css.dsp',
    'popup.css.dsp', 'progressmeter.css.dsp', 'rating.css.dsp', 'selectbox.css.dsp',
    'separator.css.dsp', 'slider.css.dsp', 'tabbox.css.dsp', 'toolbar.css.dsp', 'tree.css.dsp',
    'window.css.dsp',
    // zkmax (PE/EE)
    'daterangebox.css.dsp', 'anchornav.css.dsp', 'barcodescanner.css.dsp', 'biglistbox.css.dsp',
    'camera.css.dsp', 'cardlayout.css.dsp', 'cascader.css.dsp', 'chosenbox.css.dsp',
    'coachmark.css.dsp', 'cropper.css.dsp', 'drawer.css.dsp', 'dropupload.css.dsp',
    'goldenlayout.css.dsp', 'linelayout.css.dsp', 'multislider.css.dsp', 'nav.css.dsp',
    'organigram.css.dsp', 'portallayout.css.dsp', 'rowlayout.css.dsp', 'scrollview.css.dsp',
    'searchbox.css.dsp', 'signature.css.dsp', 'splitlayout.css.dsp', 'stepbar.css.dsp',
    'tablelayout.css.dsp', 'tbeditor.css.dsp', 'timepicker.css.dsp', 'video.css.dsp',
    // zkex (EE)
    'colorbox.css.dsp', 'columnlayout.css.dsp', 'fisheye.css.dsp', 'pdfviewer.css.dsp',
    'rangeslider.css.dsp', 'sliderbuttons.css.dsp',
    // skeleton.css.dsp is requested by a GLOBAL <stylesheet href> in zkex's lang-addon.xml (ZK-6099), not
    // by a css-uri; the LESS retirement dropped its source unnoticed (ZK-6112, F62) — hence this entry.
    'skeleton.css.dsp',
]);

// No `css-uri`, NOT in any bundle list — but a runtime `document.styleSheets` probe (2026-06-30)
// confirmed ZK's zk.wcs DOES serve their own `.z-*` rule (package aggregation). NOT orphans, so
// the guard must not flag them. Re-verify with a probe before adding here. All zul paths — like
// CSS_URI_BACKED, kept as one set; harmless for zkmax/zkex since their scanned relPaths never match.
const WCS_SERVED_ALLOWLIST = new Set([
    'js/zul/sel/css/select.css',     // .z-select — 24 own rules served (probe 2026-06-30)
    'js/zul/wgt/css/cell.css',       // .z-cell
    'js/zul/wnd/css/bandpopup.css',  // .z-bandpopup
]);
// NOTE: the orphan sweep (2026-06-30) also found two DEAD-CSS files — `box/css/space.css`
// (`.z-space`; `<space>` actually renders `.z-separator`) and `menu/css/toolbarpanel.css`
// (`.z-toolbarpanel`; Toolbar's `panel` mold renders `.z-toolbar-panel`). Their selectors
// matched nothing, so they were DELETED rather than bundled. There is intentionally no
// dead-CSS allowlist: a future no-css-uri file that matches nothing SHOULD trip the guard
// below so someone decides delete-vs-fix.

// Empty stubs for unimplemented zkex/zkmax components.
// These prevent FileNotFoundException errors at runtime. Filtered to the selected module
// by path prefix at the point of use (see build() stage 0a).
const stubPaths = [
    // NOTE: confirmpopup, breadcrumb, and carousel graduated out of this stub
    // list — they now ship real Marble CSS at js/zul/wgt/css/confirmpopup.css /
    // breadcrumb.css / carousel.css (auto-scanned 1:1; css-uri-backed below).
    // zkex
    'js/zkex/grid/css/grid.css.dsp',
    'js/zkex/inp/css/colorbox.css.dsp',
    'js/zkex/layout/css/columnlayout.css.dsp',
    // sliderbuttons widget is nested in rangeslider/multislider; its styles live in
    // rangeslider.css / multislider.css, so this stub just prevents a 404 from ZK's
    // per-widget CSS lookup.
    'js/zkex/slider/css/sliderbuttons.css.dsp',
    // zkmax
    // NOTE: scrollview graduated out of this stub list — it now ships real Marble
    // CSS at js/zkmax/layout/css/scrollview.css (auto-scanned 1:1; css-uri-backed).
    // NOTE: zkmax/css/tablet.css.dsp is NOT stubbed — it is built for real from
    // the web/zkmax/css/tablet/_*.css partials (see tabletFiles + build stage 5).
    'js/zkmax/inp/css/cascader.css.dsp',
    'js/zkmax/inp/css/chosenbox.css.dsp',
    'js/zkmax/inp/css/searchbox.css.dsp',
    'js/zkmax/layout/css/cardlayout.css.dsp',
    'js/zkmax/layout/css/goldenlayout.css.dsp',
    'js/zkmax/layout/css/linelayout.css.dsp',
    'js/zkmax/layout/css/portallayout.css.dsp',
    'js/zkmax/layout/css/rowlayout.css.dsp',
    'js/zkmax/layout/css/splitlayout.css.dsp',
    'js/zkmax/layout/css/tablelayout.css.dsp',
    'js/zkmax/med/css/video.css.dsp',
    'js/zkmax/nav/css/anchornav.css.dsp',
    'js/zkmax/nav/css/nav.css.dsp',
    'js/zkmax/sel/css/listbox.css.dsp',
    'js/zkmax/sel/css/tree.css.dsp',

    'js/zkmax/big/css/biglistbox.css.dsp',
    'js/zkmax/grid/css/grid.css.dsp',
    'js/zkmax/slider/css/multislider.css.dsp',
];

function minifySvg(svg) {
    return svg
        .replace(/<!--[\s\S]*?-->/g, '')  // strip comments
        .replace(/\s+/g, ' ')             // collapse whitespace
        .replace(/ class="[^"]*"/g, '')   // strip lucide class attr (not needed for mask)
        .trim();
}

function encodeSvgForCss(svg) {
    return svg
        .replace(/%/g, '%25')
        .replace(/</g, '%3C')
        .replace(/>/g, '%3E')
        .replace(/"/g, '%22');
}

function getLucideIcons() {
    const iconsDir = path.join(ZK_ROOT, 'node_modules/lucide-static/icons');
    if (!fs.existsSync(iconsDir)) return [];
    return fs.readdirSync(iconsDir)
        .filter(f => f.endsWith('.svg'))
        .sort()
        .map(f => f.replace('.svg', ''));
}

// Font Awesome name → Lucide name aliases.
// ZK widget JS emits FA class names (e.g. z-icon-caret-down); these redirect them to
// the correct Lucide SVG that is already generated above.
const FA_TO_LUCIDE = {
    // ZK built-in widget icons
    'caret-down': 'chevron-down',
    'caret-left': 'chevron-left',
    'caret-right': 'chevron-right',
    'caret-up': 'chevron-up',
    'angle-left': 'chevron-left',
    'angle-right': 'chevron-right',
    'angle-double-down': 'chevrons-down',
    'angle-double-left': 'chevrons-left',
    'angle-double-right': 'chevrons-right',
    'angle-double-up': 'chevrons-up',
    'angle-down': 'chevron-down',
    'angle-up': 'chevron-up',
    'compress': 'minimize-2',
    'ellipsis-h': 'ellipsis',
    'ellipsis-v': 'ellipsis-vertical',
    'exclamation-circle': 'circle-alert',
    'exclamation-triangle': 'triangle-alert',
    'info-circle': 'info',
    'reorder': 'grip-vertical',
    'stack': 'layers',
    'times-circle': 'circle-x',
    // Common FA icons used in preview pages
    'gear': 'settings',
    'volume-up': 'volume-2',
    'clock-o': 'clock',
    'edit': 'pencil',
    'envelope': 'mail',
    'file-o': 'file',
    'file-text-o': 'file-text',
    'file-pdf-o': 'file-type',
    'folder-open-o': 'folder-open',
    'help-circle': 'circle-help',
    'keyboard-o': 'keyboard',
    'power-off': 'power',
    'print': 'printer',
    'question': 'circle-help',
    'question-circle': 'circle-help',
    'refresh': 'refresh-cw',
    'rotate-left': 'rotate-ccw',
    'rotate-right': 'rotate-cw',
    'share': 'share-2',
    'sign-out': 'log-out',
    'tachometer': 'gauge',
    'th': 'layout-grid',
    'th-list': 'layout-list',
    'cube': 'box',
    'bar-chart': 'bar-chart-2',
    'android': 'smartphone',
    'dashboard': 'layout-dashboard',
    'times': 'x',
    'cogs': 'settings',
};

// Glyphs that ZK widget JS emits by a bare FA class name for which Lucide has no
// equivalent icon (so no FA_TO_LUCIDE alias can resolve them). Each entry is raw
// SVG markup drawn in the Lucide idiom (24x24 viewBox, stroke-width 2, round caps)
// so it sits visually alongside the generated Lucide glyphs.
//   - `exclamation`: ZK `Step._adjustIconContent()` sets bare `z-icon-exclamation`
//     on a step's error icon. Lucide only ships alert glyphs that wrap the "!" in a
//     circle/triangle/octagon — but the stepbar marker is already a filled circle,
//     so we need the bare "!" (mirrors the `z-icon-check` complete glyph).
//     Strokes follow Lucide `circle-alert`'s inner "!" geometry — a vertical bar
//     ending at the 12px box centre + a zero-length-line dot (`x2="12.01"`), round
//     caps — but scaled to the check glyph's 6→17 vertical extent so the error and
//     complete markers read at the same size inside the 24px circle.
const CUSTOM_ICONS = {
    'exclamation': '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="6" y2="12"/><line x1="12" x2="12.01" y1="17" y2="17"/></svg>',
};

function generateLucideIconsCSS(iconNames) {
    const iconsDir = path.join(ZK_ROOT, 'node_modules/lucide-static/icons');
    const iconMap = {};
    let css = '/* Lucide icon classes — auto-generated from lucide-static; class name = z-icon-{lucide-name} */\n';
    for (const name of iconNames) {
        const svg = fs.readFileSync(path.join(iconsDir, name + '.svg'), 'utf8');
        const encoded = encodeSvgForCss(minifySvg(svg));
        iconMap[name] = encoded;
        css += `.z-icon-${name}{--_icon:url("data:image/svg+xml,${encoded}")}\n`;
    }
    css += '/* FA → Lucide aliases: ZK widget JS emits FA class names; redirect to Lucide SVG */\n';
    for (const [fa, lucide] of Object.entries(FA_TO_LUCIDE)) {
        if (iconMap[lucide]) {
            css += `.z-icon-${fa}{--_icon:url("data:image/svg+xml,${iconMap[lucide]}")}\n`;
        }
    }
    css += '/* Custom glyphs: bare FA class names ZK emits that Lucide has no equivalent for */\n';
    for (const [name, svg] of Object.entries(CUSTOM_ICONS)) {
        css += `.z-icon-${name}{--_icon:url("data:image/svg+xml,${encodeSvgForCss(minifySvg(svg))}")}\n`;
    }
    // Generated, so this code emits its own layer block: these .z-icon-* rules belong in zk-base
    // alongside _icons.css (components override them).
    return `@layer zk-base {\n${css}}\n`;
}

// Side output — only written with --emit-docs (see build() stage 1c). Never touches doc/
// on a default run; the directory is created here because it does not exist by default.
function generateIconIndexMd(iconNames) {
    const destPath = path.join(ZK_ROOT, 'doc/spec/icon-index.md');
    const aliasRows = Object.entries(FA_TO_LUCIDE)
        .map(([fa, lucide]) => `| \`z-icon-${fa}\` | \`${lucide}\` |`)
        .join('\n');
    const lucideList = iconNames.map(n => `- \`z-icon-${n}\``).join('\n');
    const customRows = Object.keys(CUSTOM_ICONS)
        .map(name => `- \`z-icon-${name}\``)
        .join('\n');
    const md = `# Icon Index

**Auto-generated by \`scripts/build-css.js --emit-docs\` — do not edit by hand.**

This is the canonical lookup for valid \`z-icon-*\` class names in this theme.
- **Theme generator** consults this file before writing any \`z-icon-*\` selector.
- **Theme evaluator** validates that every \`z-icon-*\` reference resolves here.
- **Preview ZULs** MUST use a name that resolves to a served class — a Lucide name OR a ZK FA/custom alias listed below; invented/misspelled names are rejected. Lucide names are preferred for new content.

## FA → Lucide aliases (${Object.keys(FA_TO_LUCIDE).length} entries, from \`scripts/build-css.js\` \`FA_TO_LUCIDE\`)

These absorb FontAwesome-style class names that ZK widget JS emits at runtime (e.g. \`z-icon-caret-down\`). They are valid in authored ZULs too (they resolve to a served class) — but for *new* content prefer the Lucide name directly; reach for an FA alias when faithfully reproducing what ZK emits (e.g. a widget-DOM mockup).

| FA name | Lucide target |
|---------|---------------|
${aliasRows}

## Custom glyphs (${Object.keys(CUSTOM_ICONS).length} entries, from \`scripts/build-css.js\` \`CUSTOM_ICONS\`)

Bare FA class names ZK widget JS emits that Lucide has no equivalent for (e.g. \`z-step-error\` → \`z-icon-exclamation\`). Drawn in the Lucide idiom.

${customRows}

## Lucide names (${iconNames.length} entries, from \`node_modules/lucide-static/icons/*.svg\`)

${lucideList}
`;
    fs.mkdirSync(path.dirname(destPath), { recursive: true });
    fs.writeFileSync(destPath, md, 'utf8');
}

// Side output — only written with --emit-docs (see build() stage 1b). Never touches
// zkpreview/ on a default run; the directory is created here because it does not exist by default.
function generateIconsZul(iconNames) {
    const destPath = path.join(ZK_ROOT, 'zkpreview/src/main/webapp/icons-lucide.zul');
    const entries = iconNames.map(name =>
        `            <div sclass="z-d-flex z-flex-col z-align-center z-gap-1 z-text-center"><span sclass="z-icon-${name}" style="font-size:24px; color:var(--zk-color-primary)"/><label sclass="z-text-xs z-text-secondary" style="word-break:break-all" value="${name}"/></div>`
    ).join('\n');

    const zul = `<?page title="Lucide Icons" contentType="text/html;charset=UTF-8"?>
<zk>
<div sclass="z-p-8">
    <div sclass="z-border-bottom z-text-xl z-fw-medium z-text-on-surface z-mb-8 z-pb-3">Lucide Icons</div>

    <label sclass="z-text-sm z-text-secondary z-d-block z-mb-4" value="All ${iconNames.length} Lucide icons. Usage: iconSclass=&quot;z-icon-{name}&quot; or sclass=&quot;z-icon-{name}&quot;"/>
    <div sclass="z-d-grid z-grid-fill z-grid-fill-xs z-gap-3">
${entries}
    </div>
</div>
</zk>`;

    fs.mkdirSync(path.dirname(destPath), { recursive: true });
    fs.writeFileSync(destPath, zul, 'utf8');
}

function readFile(relativePath) {
    const fullPath = path.join(webDir, relativePath);
    if (fs.existsSync(fullPath)) {
        return fs.readFileSync(fullPath, 'utf8');
    }
    return '';
}

function writeDsp(relativePath, content) {
    const fullPath = path.join(themeDir, relativePath);
    fs.mkdirSync(path.dirname(fullPath), { recursive: true });
    fs.writeFileSync(fullPath, minifyCss(content));
}

// Write content verbatim (no minify pass). Used for CSS the build has already minified and
// then wrapped in an at-rule, or that carries a non-CSS DSP directive — see toEmbedReset
// and the norm.css.dsp taglib prepend.
function writeRaw(relativePath, content) {
    const fullPath = path.join(themeDir, relativePath);
    fs.mkdirSync(path.dirname(fullPath), { recursive: true });
    fs.writeFileSync(fullPath, content);
}

function scanCssFiles(dir, base) {
    const results = [];
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
        const fullPath = path.join(dir, entry.name);
        const relPath = path.relative(base, fullPath).replace(/\\/g, '/');
        if (entry.isDirectory()) {
            results.push(...scanCssFiles(fullPath, base));
        } else if (entry.name.endsWith('.css') && !mergedFiles.has(relPath)) {
            results.push(relPath);
        }
    }
    return results;
}

// Build-time orphan guard (gap log 2026-06-30, sweep of the scrollbar bug class).
// Every non-empty component CSS left to the 1:1 auto-scan is emitted as a standalone
// *.css.dsp. ZK only REQUESTS that dsp if the component has a `css-uri`; otherwise the file
// is never loaded and the component renders unstyled — silently, with no error. This asserts
// that every auto-scanned, non-empty component CSS is either css-uri-backed, in a bundle
// (excluded from the scan already), probe-verified served via zk.wcs, or known dead CSS.
// A new no-css-uri component thus can't silently orphan: the build fails until it is bundled.
// Only inspects the selected module's own component files (js/<module>/**).
function assertNoOrphanComponentCss() {
    const scanned = [];
    const full = path.join(webDir, `js/${moduleName}`);
    if (fs.existsSync(full)) scanned.push(...scanCssFiles(full, webDir));
    const orphans = scanned.filter(relPath => {
        const content = readFile(relPath);
        if (!content || !content.trim()) return false;          // empty → not emitted
        if (CSS_URI_BACKED.has(path.basename(relPath) + '.dsp')) return false; // ZK requests it
        if (WCS_SERVED_ALLOWLIST.has(relPath)) return false;    // served via zk.wcs (probe-verified)
        return true;
    });
    if (orphans.length) {
        throw new Error(
            'Orphaned component CSS — no css-uri, not bundled, not WCS-served:\n' +
            orphans.map(f => '  - ' + f).join('\n') +
            '\nEach is emitted as a 1:1 *.css.dsp that ZK never requests, so the component renders ' +
            'unstyled (no error).\nFix: add it to `normFiles` in scripts/build-css.js. OR, if a runtime ' +
            'document.styleSheets probe\nconfirms zk.wcs already serves its own `.z-*` rule, add it to ' +
            '`WCS_SERVED_ALLOWLIST`.');
    }
}

// Bare `@layer <names>;` order statement (same shape minifyCss guards against).
const LAYER_STMT_RE = /@layer\s+[\w-]+(?:\s*,\s*[\w-]+)*\s*;/;
// The html/body page-frame block, delimited by markers in _reset.css.
const PAGE_FRAME_RE = /\/\* page-frame:start[\s\S]*?page-frame:end \*\//;

// Derive the JS-Embed-safe reset from the single _reset.css source: drop the html/body
// page-frame block (so ZK never touches the host page's frame) and confine the remaining
// widget reset to the ZK subtree with @scope (.z-page). The bare @layer order statement is
// lifted above @scope so it still declares layer order first; the reset rules already carry
// their own `@layer zk-base { … }` block in source, so we just scope it (no @layer added here).
//
// Ordering: minify the inner body first, then wrap the result in @scope. Lightning CSS
// parses @scope correctly (CleanCSS did not — it dropped the first nested rule and hoisted
// the rest out of the block), so this order is no longer forced by the minifier. It is kept
// because it keeps the emitted wrapper byte-identical to what shipped before the swap.
//
// The order statement is read from SOURCE and removed before minifying — never fished back
// out of the minified output. A minifier may legally rewrite a bare `@layer` statement:
// Lightning CSS emits the layer *blocks* in declared order and leaves the names that are
// still empty behind as a trailing placeholder statement, so the first statement in its
// output is `@layer zk-components,zk-utilities;`. Lifting THAT above @scope would leave
// zk-base to be created last (inside @scope) and invert the whole cascade — the reset would
// outrank every component and utility rule. Only the browserDefault=true path is served
// this file, so the inversion is invisible in the default build.
function toEmbedReset(src) {
    const noFrame = src.replace(PAGE_FRAME_RE, '');
    const layerStmt = (noFrame.match(LAYER_STMT_RE) || [''])[0];
    // Body = "@layer zk-base{…}" only; with no bare statement in the input the minifier has
    // no layer names to reorder or re-emit.
    const body = minifyCss(noFrame.replace(LAYER_STMT_RE, '')).trim();
    const open = isDev ? `${layerStmt}\n@scope (.z-page) {\n` : `${layerStmt}@scope (.z-page){`;
    return `${open}${body}${isDev ? '\n}\n' : '}'}`;
}

function buildResetVariants() {
    const resetSrc = readFile('zul/css/base/_reset.css');
    // Global variant — frame intact, unscoped; reset rules already wrap themselves in @layer zk-base.
    writeDsp('zul/css/reset.css', resetSrc);
    // Embed variant — host-safe, scoped, no frame. Served when browserDefault=true.
    // Already minified + @scope-wrapped, so write it raw (don't re-run the minifier over @scope).
    writeRaw('zul/css/reset-embed.css', toEmbedReset(resetSrc));
    console.log('  zul/css/reset.css + zul/css/reset-embed.css');
}

// Vendored web font: Inter (variable, Latin subset), copied from the @fontsource-variable/inter
// npm package (installed under ZK_ROOT/node_modules) into the zul output root at build time.
// The @font-face that points at these files lives in zul/css/tokens/_fonts.css. Inter is SIL
// OFL 1.1, so the license ships alongside the binary. zul only (CE asset).
const FONT_SOURCES = [
    {
        from: path.join(ZK_ROOT, 'node_modules/@fontsource-variable/inter/files/inter-latin-wght-normal.woff2'),
        to: 'zul/font/inter-latin-variable.woff2',
    },
    {
        from: path.join(ZK_ROOT, 'node_modules/@fontsource-variable/inter/files/inter-latin-ext-wght-normal.woff2'),
        to: 'zul/font/inter-latin-ext-variable.woff2',
    },
    {
        from: path.join(ZK_ROOT, 'node_modules/@fontsource-variable/inter/LICENSE'),
        to: 'zul/font/inter-LICENSE.txt',
    },
];

function copyFonts() {
    let copied = 0;
    for (const { from, to } of FONT_SOURCES) {
        if (!fs.existsSync(from)) {
            console.warn(`  ⚠ font asset missing (did you run npm install?): ${from}`);
            continue;
        }
        const dest = path.join(themeDir, to);
        fs.mkdirSync(path.dirname(dest), { recursive: true });
        fs.copyFileSync(from, dest);
        copied++;
    }
    if (copied) console.log(`  zul/font/ — Inter latin + latin-ext variable woff2 (+ OFL license) [${copied} files]`);
}

function build() {
    // 0. Fail fast if any no-css-uri component CSS would be emitted as an orphaned 1:1 dsp.
    assertNoOrphanComponentCss();

    // 0a. Write empty stubs for unimplemented components belonging to this module.
    const moduleStubs = stubPaths.filter(p => belongsToModule(p, moduleName));
    for (const stubPath of moduleStubs) {
        writeDsp(stubPath, '');
    }
    if (moduleStubs.length) console.log(`  ${moduleStubs.length} empty stubs (${moduleName})`);

    if (moduleName === 'zul') {
        // 1. Build norm.css.dsp (tokens + base + global + generated Lucide icons)
        const lucideIcons = getLucideIcons();
        let normCSS = '';
        for (const file of normFiles) {
            const css = readFile(file);
            // Each file self-declares its layer in source; verify the ones that must be layered.
            // (tokens are unlayered :root defs; utility files self-declare @layer zk-utilities.)
            if (file.startsWith('zul/css/base/')) assertLayer(file, css, 'zk-base');
            else if (file.startsWith('js/')) assertLayer(file, css, 'zk-components');
            normCSS += css + '\n';
        }
        // Lucide icon CSS is generated, so the generator emits its own @layer zk-base block.
        normCSS += generateLucideIconsCSS(lucideIcons);
        // norm.css.dsp uses ${c:encodeURL(...)} in _fonts.css's @font-face (self-hosted
        // Inter). The DSP `c` taglib must be declared at the top of the file or the parser
        // throws "Function 'c:encodeURL' not found" and drops the rule — same directive ZK's
        // own font-awesome.css.dsp carries. Prepend it AFTER minify so the minifier never sees
        // the non-CSS <%@ ... %> directive. Written raw for the same reason.
        writeRaw('zul/css/norm.css.dsp', DSP_CORE_TAGLIB + minifyCss(normCSS));
        console.log(`  zul/css/norm.css.dsp (${lucideIcons.length} Lucide icons)`);

        // 1a. Build the two reset variants (served separately, ahead of norm — see getThemeURIs)
        buildResetVariants();

        // Copy vendored Inter font files (zul only — CE asset, see FONT_SOURCES above)
        copyFonts();

        // 1b/1c. Side outputs — icons-lucide.zul + doc/spec/icon-index.md — only with --emit-docs.
        if (emitDocs) {
            generateIconsZul(lucideIcons);
            console.log(`  zkpreview/src/main/webapp/icons-lucide.zul (${lucideIcons.length} icons)`);
            generateIconIndexMd(lucideIcons);
            console.log(`  doc/spec/icon-index.md (${lucideIcons.length} lucide + ${Object.keys(FA_TO_LUCIDE).length} aliases)`);
        }
    }

    // 2. Auto-scan js/<module>/**/css/*.css → 1:1 *.css.dsp (overrides stubs)
    const jsModuleDir = path.join(webDir, `js/${moduleName}`);
    if (fs.existsSync(jsModuleDir)) {
        const cssFiles = scanCssFiles(jsModuleDir, webDir);
        for (const relPath of cssFiles) {
            const content = readFile(relPath);
            if (content) {
                assertLayer(relPath, content, 'zk-components');
                writeDsp(relPath + '.dsp', content);
                console.log(`  ${relPath}.dsp`);
            }
        }
    }

    if (moduleName === 'zul') {
        // 3. Build combo.css.dsp (merged dropdown inputs — each source file self-wraps in zk-components)
        comboFiles.forEach(f => assertLayer(f, readFile(f), 'zk-components'));
        const comboCSS = comboFiles.map(f => readFile(f)).join('\n');
        if (comboCSS.trim()) {
            writeDsp('js/zul/inp/css/combo.css.dsp', comboCSS);
            console.log('  js/zul/inp/css/combo.css.dsp');
        }

        // 4. Build footer.css.dsp (loaded last). Component CSS self-declares zk-components in source;
        //    the framework runtime classes _cssflex/_dnd (z-flex/z-dragged… toggled by ZK JS) stay
        //    UNLAYERED so they keep beating layered component CSS exactly as they do today.
        footerFiles.filter(f => f.startsWith('js/')).forEach(f => assertLayer(f, readFile(f), 'zk-components'));
        const footerCSS = footerFiles.map(f => readFile(f)).join('\n');
        if (footerCSS.trim()) {
            writeDsp('zul/css/footer.css.dsp', footerCSS);
            console.log('  zul/css/footer.css.dsp');
        }
    }

    if (moduleName === 'zkmax') {
        // 5. Build tablet.css.dsp (touch overrides — single file from partials)
        const tabletCSS = tabletFiles.map(f => readFile(f)).join('\n');
        if (tabletCSS.trim()) {
            writeDsp('zkmax/css/tablet.css.dsp', tabletCSS);
            console.log('  zkmax/css/tablet.css.dsp');
        }
    }

    console.log(`\nCSS build complete for --module ${moduleName} (${isDev ? 'dev — unminified' : 'minified'}).`);
}

build();
