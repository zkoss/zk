#!/usr/bin/env node
/**
 * @file Builds the Marble theme CSS for one ZK module.
 *
 * Reads the module's theme CSS sources, verifies each file declares its cascade layer,
 * bundles and minifies them, and writes the .css.dsp files that ZK serves. zul also gets
 * the Lucide icon SVGs, the vendored Inter font and the utility-class manifest.
 * The build fails when a component CSS would ship with no way to be loaded, or when the
 * CSS breakpoints drift from the zkmax responsive constants.
 *
 * Source and output roots per module:
 *
 *   --module | source web root                        | output root (default)                | lang file(s)
 *   zul      | ZK_ROOT/zul/src/main/resources/web      | ZK_ROOT/zul/codegen/resources/web    | ZK_ROOT/zul/src/main/resources/metainfo/zk/lang.xml
 *   zkmax    | ZKCML_ROOT/zkmax/src/main/resources/web | ZKCML_ROOT/zkmax/codegen/resources/web | ZKCML_ROOT/zkmax/src/main/resources/metainfo/zk/lang-addon.xml
 *   zkex     | ZKCML_ROOT/zkex/src/main/resources/web  | ZKCML_ROOT/zkex/codegen/resources/web  | ZKCML_ROOT/zkex/src/main/resources/metainfo/zk/lang-addon.xml
 *   za11y    | ZKCML_ROOT/za11y/src/main/resources/web | ZKCML_ROOT/za11y/codegen/resources/web | (none — loaded by A11yThemeURIHandler via za11y's themes.properties, not a lang file)
 *
 * where ZK_ROOT = path.resolve(__dirname, '..') (this repo) and
 * ZKCML_ROOT = path.resolve(ZK_ROOT, '../zkcml') (the enterprise sibling repo).
 *
 * za11y only: the partials js/za11y/{zul,zkmax,zkex}/css/_*-a11y.css are concatenated into the one
 * za11y/css/marble.css.dsp (see buildA11ySheet).
 *
 * zul only, this also vendors the Inter web font from the @fontsource-variable/inter npm
 * package (ZK_ROOT/node_modules) into zul/font/ under the zul output root — see FONT_SOURCES.
 *
 * Usage:
 *   node scripts/build-css.js --module zul|zkmax|zkex|za11y [--out <dir>] [--dev] [--icon-catalog]
 * Exit: 2 = missing/unknown --module (usage error)
 *
 * Verification: scripts/snapshot-build-css.sh captures every output of this script (all modules,
 * minified and --dev, --icon-catalog, usage errors).
 *   - Refactoring: take a snapshot before and after; `diff -r` of the two must be empty.
 *   - Any other change: the same diff must show only the outputs you meant to change.
 *   - A new flag, module, output or side file here needs the matching change in that script.
 *
 * Reading order: command line → build() (the list of every output, in build order) → one section
 * per output, each next to its source list → shared helpers (bundling, minify, file IO).
 */

const fs = require('fs');
const path = require('path');
const { transform: lightningTransform } = require('lightningcss');
const { buildUtilityManifest } = require('./utility-manifest');
const { checkBreakpoints } = require('./check-breakpoints');
const { extractRequired } = require('./css-uri');

// ===== Command line =====

const ZK_ROOT = path.resolve(__dirname, '..');
const ZKCML_ROOT = path.resolve(ZK_ROOT, '../zkcml');

/**
 * Per-module source web root, default output root and lang file. See the table in the header comment.
 */
const MODULES = {
    zul: {
        webDir: path.join(ZK_ROOT, 'zul/src/main/resources/web'),
        outDir: path.join(ZK_ROOT, 'zul/codegen/resources/web'),
        langFile: path.join(ZK_ROOT, 'zul/src/main/resources/metainfo/zk/lang.xml'),
    },
    zkmax: {
        webDir: path.join(ZKCML_ROOT, 'zkmax/src/main/resources/web'),
        outDir: path.join(ZKCML_ROOT, 'zkmax/codegen/resources/web'),
        langFile: path.join(ZKCML_ROOT, 'zkmax/src/main/resources/metainfo/zk/lang-addon.xml'),
    },
    zkex: {
        webDir: path.join(ZKCML_ROOT, 'zkex/src/main/resources/web'),
        outDir: path.join(ZKCML_ROOT, 'zkex/codegen/resources/web'),
        langFile: path.join(ZKCML_ROOT, 'zkex/src/main/resources/metainfo/zk/lang-addon.xml'),
    },
    za11y: {
        webDir: path.join(ZKCML_ROOT, 'za11y/src/main/resources/web'),
        outDir: path.join(ZKCML_ROOT, 'za11y/codegen/resources/web'),
    },
};

function argFor(flag) {
    const i = process.argv.indexOf(flag);
    return i >= 0 && i + 1 < process.argv.length ? process.argv[i + 1] : undefined;
}

/**
 * One run builds one module, so the parsed options are module-level constants.
 */
function parseArgs() {
    const moduleName = argFor('--module');
    if (!moduleName || !MODULES[moduleName]) {
        console.error('Usage: node scripts/build-css.js --module zul|zkmax|zkex|za11y [--out <dir>] [--dev] [--icon-catalog]');
        process.exit(2);
    }
    return {
        moduleName,
        webDir: MODULES[moduleName].webDir,
        themeDir: argFor('--out') || MODULES[moduleName].outDir,
        // Dev builds (--dev) stay unminified so hot-swapped CSS is readable in
        // DevTools; the packaged build (Gradle compileMarbleCss) is minified.
        isDev: process.argv.includes('--dev'),
        // Side outputs (icon-index.md, icons-lucide.zul) only run when explicitly requested — see
        // writeIconCatalog. Run them with `npm run build:icon-catalog` after upgrading lucide-static
        // or editing FA_TO_LUCIDE / CUSTOM_ICONS, and commit both files.
        iconCatalog: process.argv.includes('--icon-catalog'),
    };
}

const { moduleName, webDir, themeDir, isDev, iconCatalog } = parseArgs();

// ===== Build =====

/**
 * What each module's run does, in order, after the checks every module runs. zul's global sheets
 * come before the 1:1 component sheets and its merged sheets after them; this is the order the
 * build has always logged in.
 */
const MODULE_STEPS = {
    zul: [
        buildNormSheet,
        buildResetVariants,
        copyFonts,
        writeUtilityManifest,
        writeIconCatalog,
        buildComponentSheets,
        buildComboSheet,
        buildFooterSheet,
    ],
    zkex: [
        buildComponentSheets,
    ],
    zkmax: [
        assertBreakpointsConsistent,
        buildComponentSheets,
        buildTabletSheet,
    ],
    za11y: [
        buildComponentSheets,
        buildA11ySheet,
    ],
};

function build() {
    assertNoOrphanComponentCss();
    assertNoUnlistedThemeCss();
    for (const step of MODULE_STEPS[moduleName]) step();
    console.log(`\nCSS build complete for --module ${moduleName} (${isDev ? 'dev — unminified' : 'minified'}).`);
}

// ===== Guard: orphaned component CSS =====

/**
 * The `.css.dsp` paths (relative to the web root, as ZK resolves them) that the selected module's
 * lang file makes ZK request: each component `<css-uri>` and each global `<stylesheet href>`.
 * ZK loads these itself, so a 1:1 auto-scanned dsp at one of these paths is served. za11y has no
 * lang file, so nothing of its own is exempt.
 */
function cssUriBacked() {
    const langFile = MODULES[moduleName].langFile;
    if (!langFile) return new Set();
    return new Set(extractRequired(fs.readFileSync(langFile, 'utf8'), langFile)
        .map(r => r.resolved)
        .filter(Boolean));
}

// NOTE: the orphan sweep (2026-06-30) also found two DEAD-CSS files — `box/css/space.css`
// (`.z-space`; `<space>` actually renders `.z-separator`) and `menu/css/toolbarpanel.css`
// (`.z-toolbarpanel`; Toolbar's `panel` mold renders `.z-toolbar-panel`). Their selectors
// matched nothing, so they were DELETED rather than bundled. There is intentionally no
// dead-CSS allowlist: a future no-css-uri file that matches nothing SHOULD trip the guard
// below so someone decides delete-vs-fix.

/**
 * Build-time orphan guard for the scrollbar bug class (zk-component-rules
 * reference/component-css-must-be-bundled-or-css-uri.md).
 * Every non-empty component CSS left to the 1:1 auto-scan is emitted as a standalone
 * *.css.dsp. ZK only REQUESTS that dsp if the component has a `css-uri`; otherwise the file
 * is never loaded and the component renders unstyled — silently, with no error. This asserts
 * that every auto-scanned, non-empty component CSS is either css-uri-backed or in a bundle
 * (excluded from the scan already). zk.wcs serves only the <stylesheet>, css-uri and footer entries, so
 * nothing else is loaded. A new no-css-uri component thus can't silently orphan: the build fails
 * until it is bundled, given a css-uri, or deleted if dead.
 * Only inspects the selected module's own component files (js/<module>/**).
 */
function assertNoOrphanComponentCss() {
    const requested = cssUriBacked();
    const orphans = autoScannedCssFiles().filter(relPath => {
        const content = readFile(relPath);
        if (!content || !content.trim()) return false;      // empty → not emitted
        if (requested.has(relPath + '.dsp')) return false;  // ZK requests it
        return true;
    });
    if (orphans.length) {
        throw new Error(
            'Orphaned component CSS — no css-uri and not bundled:\n' +
            orphans.map(f => '  - ' + f).join('\n') +
            '\nEach is emitted as a 1:1 *.css.dsp that ZK never requests, so the component renders ' +
            'unstyled (no error).\nFix: register a css-uri for it in lang.xml / lang-addon.xml, or ' +
            'bundle it in `normFiles` in scripts/build-css.js,\nor delete the file if it is dead.\n' +
            'See ' +
            '.claude/skills/zk-component-rules/reference/component-css-must-be-bundled-or-css-uri.md.');
    }
}

// ===== Guard: theme CSS missing from every source list =====

/**
 * CSS outside js/ is never auto-scanned: it ships only through a source list (normFiles,
 * footerFiles, tabletFiles) or as RESET_SOURCE. A file added there but left out of every list
 * would build without error and never ship, so the build fails until it is listed or deleted.
 * js/ is covered by assertNoOrphanComponentCss.
 */
function assertNoUnlistedThemeCss() {
    const listed = new Set([...normFiles, ...footerFiles, ...tabletFiles, RESET_SOURCE]);
    const unlisted = listCssFiles(webDir).filter(f => !f.startsWith('js/') && !listed.has(f));
    if (unlisted.length) {
        throw new Error(
            'Theme CSS not in any source list:\n' +
            unlisted.map(f => '  - ' + f).join('\n') +
            '\nEach is never built, so its rules never ship (no error).\nFix: add it to `normFiles` or ' +
            '`footerFiles` (zul) or `tabletFiles` (zkmax) in scripts/build-css.js, or delete the file if it is dead.');
    }
}

// ===== Guard: breakpoint scale =====

/**
 * The framework breakpoint scale lives in two places that cannot read each other: the width
 * conditions of @media/@container in CSS, and the Grid/Listbox `responsive` constants in zkmax.
 * Fail the build when they differ (doc/spec/responsive-design.md). Needs the zkmax
 * JS source, so it runs only for --module zkmax; a CE-only build has nothing to compare against.
 */
function assertBreakpointsConsistent() {
    const zkmaxJs = path.join(ZKCML_ROOT, 'zkmax/src/main/resources/web/js/zkmax');
    const errors = checkBreakpoints({
        jsFiles: ['grid-responsive.ts', 'listbox-responsive.ts'].map(f => path.join(zkmaxJs, f)),
        cssRoots: [MODULES.zul.webDir, MODULES.zkmax.webDir, MODULES.zkex.webDir].filter(d => fs.existsSync(d)),
    });
    if (errors.length) {
        throw new Error('Breakpoint consistency check failed:\n' + errors.map(e => '  - ' + e).join('\n') +
            '\nThe CSS width conditions must match _DEFAULT_BREAKPOINTS in zkmax grid-responsive.ts and ' +
            'listbox-responsive.ts.\nSee doc/spec/responsive-design.md.');
    }
}

// ===== zul: norm.css.dsp =====

/**
 * norm.css.dsp = tokens + base + global styles (loaded first by WCS). zul only.
 */
const normFiles = [
    // Cascade layer order — must stay first so it precedes every layer in zk.wcs.
    'zul/css/_layer-order.css',
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
    // _spacing.css — see doc/spec/spacing-policy.md).
    'zul/css/utility/_colors.css',
    'zul/css/utility/_elevation.css',
    'zul/css/utility/_surface.css',
    'zul/css/utility/_spacing.css',
    'zul/css/utility/_layout.css',
    'zul/css/utility/_typography.css',
    'zul/css/utility/_borders.css',
    'zul/css/utility/_interactions.css',
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
    // The Fileupload.get dialog's picked-file rows (zul.fud.FileuploadDlg) have no css-uri either.
    'js/zul/fud/css/fileupload.css',
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
    // (zk-component-rules reference/component-css-must-be-bundled-or-css-uri.md).
    'js/zul/wgt/css/label.css',
    'js/zul/box/css/div.css',
    'js/zul/box/css/span.css',
    'js/zul/layout/css/html.css',
    'js/zul/wgt/css/image.css',
    'js/zul/wgt/css/imagemap.css',
    // Root of a macro component. Its widget, zk.Macro, lives in the zk module and has no css-uri,
    // so the underscore partial is bundled here like the widgets above.
    'js/zul/wgt/css/_macro.css',
];

/**
 * Tokens are unlayered :root defs and utility files self-declare @layer zk-utilities, so only
 * base and component files are checked.
 */
function normLayerFor(file) {
    if (file.startsWith('zul/css/base/')) return 'zk-base';
    if (file.startsWith('js/')) return 'zk-components';
    return null;
}

function buildNormSheet() {
    const lucideIcons = getLucideIcons();
    const sourcesCSS = concatSources(normFiles, normLayerFor);
    const iconUrls = writeIconFiles(lucideIcons);
    // Lucide icon CSS is generated, so the generator emits its own @layer zk-base block.
    writeDsp('zul/css/norm.css.dsp', sourcesCSS + '\n' + generateLucideIconsCSS(lucideIcons, iconUrls));
    console.log(`  zul/css/norm.css.dsp + zul/img/icons/ (${lucideIcons.length} Lucide icons, one .svg each)`);
}

// ===== zul: Lucide icons (part of norm.css.dsp) =====

const LUCIDE_ICONS_DIR = path.join(ZK_ROOT, 'node_modules/lucide-static/icons');

function getLucideIcons() {
    if (!fs.existsSync(LUCIDE_ICONS_DIR)) return [];
    return fs.readdirSync(LUCIDE_ICONS_DIR)
        .filter(f => f.endsWith('.svg'))
        .sort()
        .map(f => f.replace('.svg', ''));
}

/**
 * Font Awesome name → Lucide name aliases.
 * ZK widget JS emits FA class names (e.g. z-icon-caret-down); these redirect them to
 * the Lucide icon of the target name.
 * Only names that ZK widget JS emits belong here. Preview pages do not get aliases: they use
 * names both Marble and IceBlue render (scripts/check-icon-coverage.sh enforces this).
 */
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
    'times': 'x',
    'clock-o': 'clock',
    'refresh': 'refresh-cw',
    'rotate-left': 'rotate-ccw',
    'rotate-right': 'rotate-cw',
};

/**
 * Glyphs that ZK widget JS emits by a bare FA class name for which Lucide has no
 * equivalent icon (so no FA_TO_LUCIDE alias can resolve them). Each entry is raw
 * SVG markup drawn in the Lucide idiom (24x24 viewBox, stroke-width 2, round caps)
 * so it sits visually alongside the generated Lucide glyphs.
 *   - `exclamation`: ZK `Step._adjustIconContent()` sets bare `z-icon-exclamation`
 *     on a step's error icon. Lucide only ships alert glyphs that wrap the "!" in a
 *     circle/triangle/octagon — but the stepbar marker is already a filled circle,
 *     so we need the bare "!" (mirrors the `z-icon-check` complete glyph).
 *     Strokes follow Lucide `circle-alert`'s inner "!" geometry — a vertical bar
 *     ending at the 12px box centre + a zero-length-line dot (`x2="12.01"`), round
 *     caps — but scaled to the check glyph's 6→17 vertical extent so the error and
 *     complete markers read at the same size inside the 24px circle.
 */
const CUSTOM_ICONS = {
    'exclamation': '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="6" y2="12"/><line x1="12" x2="12.01" y1="17" y2="17"/></svg>',
};

/**
 * iconUrls comes from writeIconFiles.
 */
function generateLucideIconsCSS(iconNames, iconUrls) {
    const css =
        '/* Lucide icon classes — auto-generated from lucide-static; class name = z-icon-{lucide-name} */\n' +
        iconNames.map(name => iconRule(name, iconUrls.lucide[name])).join('') +
        '/* FA → Lucide aliases: ZK widget JS emits FA class names; redirect to Lucide SVG */\n' +
        faAliasRules(iconUrls.lucide) +
        '/* Custom glyphs: bare FA class names ZK emits that Lucide has no equivalent for */\n' +
        Object.keys(CUSTOM_ICONS).map(name => iconRule(name, iconUrls.custom[name])).join('');
    // Generated, so this code emits its own layer block: these .z-icon-* rules belong in zk-base
    // alongside _icons.css (components override them).
    return `@layer zk-base {\n${css}}\n`;
}

/**
 * Writes the Lucide icons, then the custom glyphs. Returns each one's URL by name, per group.
 */
function writeIconFiles(lucideIcons) {
    const lucide = {};
    for (const name of lucideIcons) {
        const svg = fs.readFileSync(path.join(LUCIDE_ICONS_DIR, name + '.svg'), 'utf8');
        lucide[name] = writeIconFile(name, minifySvg(svg));
    }
    const custom = {};
    for (const [name, svg] of Object.entries(CUSTOM_ICONS)) {
        custom[name] = writeIconFile(name, minifySvg(svg));
    }
    return { lucide, custom };
}

/**
 * An alias whose Lucide target is missing is skipped.
 */
function faAliasRules(lucideUrl) {
    return Object.entries(FA_TO_LUCIDE)
        .filter(([, lucide]) => lucideUrl[lucide])
        .map(([fa, lucide]) => iconRule(fa, lucideUrl[lucide]))
        .join('');
}

function iconRule(name, url) {
    return `.z-icon-${name}{--_icon:${url}}\n`;
}

/**
 * Each icon is written as its own .svg file under zul/img/icons/ and referenced by a ~./ URL, so a
 * page downloads only the icons it uses. WcsExtendlet encodes every url(~./...) in the zk.wcs
 * response, so the rules stay plain CSS. Returns the icon's URL.
 */
function writeIconFile(name, svg) {
    writeRaw(`zul/img/icons/${name}.svg`, svg + '\n');
    return `url(~./zul/img/icons/${name}.svg)`;
}

function minifySvg(svg) {
    return svg
        .replace(/<!--[\s\S]*?-->/g, '')  // strip comments
        .replace(/\s+/g, ' ')             // collapse whitespace
        .replace(/ class="[^"]*"/g, '')   // strip lucide class attr (not needed for mask)
        .trim();
}

// ===== zul: reset.css + reset-embed.css =====

/**
 * The html/body page-frame block, delimited by markers in _reset.css.
 */
const PAGE_FRAME_RE = /\/\* page-frame:start[\s\S]*?page-frame:end \*\//;

/**
 * The one source both reset variants are derived from.
 */
const RESET_SOURCE = 'zul/css/base/_reset.css';

/**
 * Served separately, ahead of norm.css.dsp — see StandardThemeProvider.getThemeURIs.
 */
function buildResetVariants() {
    const resetSrc = readFile(RESET_SOURCE);
    // Global variant — frame intact, unscoped; reset rules already wrap themselves in @layer zk-base.
    writeDsp('zul/css/reset.css', resetSrc);
    // Embed variant — host-safe, scoped, no frame. Served when browserDefault=true.
    // Already minified + @scope-wrapped, so write it raw (don't re-run the minifier over @scope).
    writeRaw('zul/css/reset-embed.css', toEmbedReset(resetSrc));
    console.log('  zul/css/reset.css + zul/css/reset-embed.css');
}

/**
 * Derive the JS-Embed-safe reset from the single _reset.css source: drop the html/body
 * page-frame block (so ZK never touches the host page's frame) and confine the remaining
 * widget reset to the ZK subtree with @scope (.z-page). The reset rules already carry their
 * own `@layer zk-base { … }` block in source, so we just scope it (no @layer added here).
 *
 * Ordering: minify the inner body first, then wrap the result in @scope. Lightning CSS
 * parses @scope correctly (CleanCSS did not — it dropped the first nested rule and hoisted
 * the rest out of the block), so this order is no longer forced by the minifier. It is kept
 * because it keeps the emitted wrapper byte-identical to what shipped before the swap.
 */
function toEmbedReset(src) {
    const body = minifyCss(src.replace(PAGE_FRAME_RE, '')).trim();
    const open = isDev ? '@scope (.z-page) {\n' : '@scope (.z-page){';
    return `${open}${body}${isDev ? '\n}\n' : '}'}`;
}

// ===== zul: Inter web font =====

/**
 * Vendored web font: Inter (variable, Latin subset), copied from the @fontsource-variable/inter
 * npm package (installed under ZK_ROOT/node_modules) into the zul output root at build time.
 * The @font-face that points at these files lives in zul/css/tokens/_fonts.css. Inter is SIL
 * OFL 1.1, so the license ships alongside the binary. zul only (CE asset).
 */
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

// ===== zul: utility-classes.json =====

/**
 * The utility subset of the bundle, reused by the manifest generator so the catalogue can never
 * list a file the browser does not actually load. See doc/utility-class-discovery.md.
 */
const utilityFiles = normFiles.filter(f => f.startsWith('zul/css/utility/'));

/**
 * The machine-readable catalogue of authorable z-* classes. Written under web/ so it ships in
 * zul.jar beside the CSS it describes and is reachable from a running app at
 * /zkau/web/zul/css/utility-classes.json. Always emitted (not --icon-catalog gated): the IDE
 * plugin and AI agents read it from the jar, so a build that omitted it would ship a version with
 * no catalogue. Emitted compact — it is a machine artifact (39 KB), and readers pipe it through jq.
 */
function writeUtilityManifest() {
    const utility = buildUtilityManifest(webDir, utilityFiles,
        path.join(webDir, 'zul/css/tokens'), path.join(webDir, 'js/zul'));
    writeRaw('zul/css/utility-classes.json', JSON.stringify({
        schemaVersion: 1,
        theme: 'marble',
        module: 'zul',
        generatedBy: 'scripts/build-css.js',
        sources: utilityFiles,
        classes: utility.classes,
    }) + '\n');
    console.log(`  zul/css/utility-classes.json (${utility.classes.length} utility classes,`
        + ` ${utility.excludedComponentClasses.length} component classes excluded)`);
}

// ===== zul, --icon-catalog only: icons-lucide.zul + doc/spec/icon-index.md =====

/**
 * Side outputs, written into the repo (not the output root) and committed. Only with
 * --icon-catalog: a default run never touches zkthemelab/ or doc/.
 */
function writeIconCatalog() {
    if (!iconCatalog) return;
    const lucideIcons = getLucideIcons();
    writeRepoFile('zkthemelab/src/main/webapp/web/icons-lucide.zul', renderIconsZul(lucideIcons));
    console.log(`  zkthemelab/src/main/webapp/web/icons-lucide.zul (${lucideIcons.length} icons)`);
    writeRepoFile('doc/spec/icon-index.md', renderIconIndexMd(lucideIcons));
    console.log(`  doc/spec/icon-index.md (${lucideIcons.length} lucide + ${Object.keys(FA_TO_LUCIDE).length} aliases)`);
}

function renderIconsZul(iconNames) {
    const entries = iconNames.map(name =>
        `            <div sclass="z-d-flex z-flex-col z-items-center z-gap-1 z-text-center"><span sclass="z-icon-${name}" style="font-size:24px; color:var(--zk-color-primary)"/><label sclass="z-text-xs z-text-secondary" style="word-break:break-all" value="${name}"/></div>`
    ).join('\n');

    return `<?page title="Lucide Icons" contentType="text/html;charset=UTF-8"?>
<zk>
<div sclass="z-p-8">
    <div sclass="z-border-bottom z-mb-8 z-pb-3"><label sclass="z-text-2xl z-fw-medium z-text-on-surface" value="Lucide Icons"/></div>

    <label sclass="z-text-sm z-text-secondary z-d-block z-mb-4" value="All ${iconNames.length} Lucide icons. Usage: iconSclass=&quot;z-icon-{name}&quot; or sclass=&quot;z-icon-{name}&quot;"/>
    <div sclass="z-d-grid z-grid-fill z-grid-fill-xs z-gap-3">
${entries}
    </div>
</div>
</zk>`;
}

function renderIconIndexMd(iconNames) {
    const aliasRows = Object.entries(FA_TO_LUCIDE)
        .map(([fa, lucide]) => `| \`z-icon-${fa}\` | \`${lucide}\` |`)
        .join('\n');
    const lucideList = iconNames.map(n => `- \`z-icon-${n}\``).join('\n');
    const customRows = Object.keys(CUSTOM_ICONS)
        .map(name => `- \`z-icon-${name}\``)
        .join('\n');
    return `# Icon Index

**Auto-generated by \`scripts/build-css.js\` — do not edit by hand.**
Regenerate with \`npm run build:icon-catalog\` after upgrading \`lucide-static\` or editing \`FA_TO_LUCIDE\` / \`CUSTOM_ICONS\`.

This is the canonical lookup for valid \`z-icon-*\` class names in this theme.
- **Theme generator** consults this file before writing any \`z-icon-*\` selector.
- **Theme evaluator** validates that every \`z-icon-*\` reference resolves here.
- **Preview ZULs** (\`zkthemelab/src/main/webapp/web/**/*.zul\`) MUST use a name that both Marble serves (a Lucide name or a ZK FA/custom alias listed below) and IceBlue renders (\`scripts/iceblue-icon-names.txt\`), so neither theme shows a blank icon. Enforced by \`scripts/check-icon-coverage.sh\`.

See \`doc/spec/icon-policy.md\` for the full policy.

## FA → Lucide aliases (${Object.keys(FA_TO_LUCIDE).length} entries, from \`scripts/build-css.js\` \`FA_TO_LUCIDE\`)

These absorb FontAwesome-style class names that ZK widget JS emits at runtime (e.g. \`z-icon-caret-down\`). A widget-DOM mockup on a preview page uses the exact name the widget emits.

| FA name | Lucide target |
|---------|---------------|
${aliasRows}

## Custom glyphs (${Object.keys(CUSTOM_ICONS).length} entries, from \`scripts/build-css.js\` \`CUSTOM_ICONS\`)

Bare FA class names ZK widget JS emits that Lucide has no equivalent for (e.g. \`z-step-error\` → \`z-icon-exclamation\`). Drawn in the Lucide idiom.

${customRows}

## Lucide names (${iconNames.length} entries, from \`node_modules/lucide-static/icons/*.svg\`)

${lucideList}
`;
}

// ===== Every module: 1:1 component sheets =====

/**
 * Every .css under js/<module>/ that is not merged into a bundle becomes its own <file>.css.dsp.
 */
function buildComponentSheets() {
    for (const relPath of autoScannedCssFiles()) {
        const content = readFile(relPath);
        if (content) {
            assertLayer(relPath, content, 'zk-components');
            writeDsp(relPath + '.dsp', content);
            console.log(`  ${relPath}.dsp`);
        }
    }
}

/**
 * The selected module's .css files under js/<module>/, minus the files merged into a bundle.
 */
function autoScannedCssFiles() {
    const jsModuleDir = path.join(webDir, `js/${moduleName}`);
    if (!fs.existsSync(jsModuleDir)) return [];
    const merged = mergedFiles();
    return listCssFiles(jsModuleDir).filter(relPath => !merged.has(relPath));
}

/**
 * Component files that a bundle below already contains. zkmax/zkex have no merge lists, so their
 * auto-scan runs 1:1 with nothing excluded.
 */
function mergedFiles() {
    if (moduleName === 'zul') {
        return new Set([...normFiles.filter(f => f.startsWith('js/')), ...comboFiles, ...footerFiles]);
    }
    if (moduleName === 'za11y') return new Set(findA11yPartials());
    return new Set();
}

// ZK-5992: vendor stylesheets the widget loads by URL at runtime (zk.loadCSS) — not theme CSS, never a .css.dsp.
const runtimeLoadedDirs = new Set(['js/zkmax/tbeditor/ext/plugins']);

/**
 * Every *.css under dir, as a path relative to the web root.
 */
function listCssFiles(dir) {
    const results = [];
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
        const fullPath = path.join(dir, entry.name);
        const relPath = path.relative(webDir, fullPath).replace(/\\/g, '/');
        if (entry.isDirectory()) {
            if (!runtimeLoadedDirs.has(relPath)) results.push(...listCssFiles(fullPath));
        } else if (entry.name.endsWith('.css')) {
            results.push(relPath);
        }
    }
    return results;
}

// ===== zul: combo.css.dsp =====

/**
 * combo.css.dsp = merged dropdown-type input components. zul only.
 */
const comboFiles = [
    'js/zul/inp/css/combobox.css',
    'js/zul/inp/css/datebox.css',
    'js/zul/inp/css/timebox.css',
    'js/zul/inp/css/spinner.css',
    'js/zul/inp/css/bandbox.css',
];

function buildComboSheet() {
    writeSheet('js/zul/inp/css/combo.css.dsp', concatSources(comboFiles, () => 'zk-components'));
}

// ===== zul: footer.css.dsp =====

/**
 * footer.css.dsp = loaded last by WCS. zul only.
 */
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
    // zk.log box created by zk/zk.ts doLog() (IceBlue norm.less div.z-log). See the file's header comment.
    'js/zul/wgt/css/_log.css',
];

/**
 * Component CSS self-declares zk-components in source; the framework runtime classes _cssflex/_dnd
 * (z-flex/z-dragged… toggled by ZK JS) stay UNLAYERED so they keep beating layered component CSS.
 */
function buildFooterSheet() {
    const css = concatSources(footerFiles, f => (f.startsWith('js/') ? 'zk-components' : null));
    writeSheet('zul/css/footer.css.dsp', css);
}

// ===== zkmax: tablet.css.dsp =====

/**
 * tablet.css.dsp = touch overrides, injected by ZK's TabletThemeURIHandler at
 * cascade position 1 ONLY on a mobile User-Agent (EE). Split by component during
 * development; concatenated into a single tablet.css.dsp at build time. zkmax only.
 * _tokens.css MUST be first so its :root touch tokens cascade to the rest.
 */
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

function buildTabletSheet() {
    writeSheet('zkmax/css/tablet.css.dsp', concatSources(tabletFiles));
}

// ===== za11y: marble.css.dsp =====

/**
 * marble.css.dsp = za11y's accessibility rules, one sheet for Marble. za11y only.
 * Partials are discovered, not listed, so adding one needs no build change; the order is fixed
 * (zul, zkmax, zkex, then by name) to keep the output reproducible.
 */
const A11Y_PARTIAL_PACKAGES = ['zul', 'zkmax', 'zkex'];
const A11Y_PARTIAL_RE = /^_.+-a11y\.css$/;

function findA11yPartials() {
    const files = [];
    for (const pkg of A11Y_PARTIAL_PACKAGES) {
        const cssDir = `js/za11y/${pkg}/css`;
        const dir = path.join(webDir, cssDir);
        if (!fs.existsSync(dir)) continue;
        files.push(...fs.readdirSync(dir).filter(name => A11Y_PARTIAL_RE.test(name)).sort()
            .map(name => `${cssDir}/${name}`));
    }
    return files;
}

/**
 * Every partial self-wraps in zk-components. With no partial yet it still emits the empty layer,
 * so the sheet always exists for the themes.properties entry.
 */
function buildA11ySheet() {
    const a11yFiles = findA11yPartials();
    const a11yCSS = concatSources(a11yFiles, () => 'zk-components');
    writeDsp('za11y/css/marble.css.dsp', a11yCSS.trim() ? a11yCSS : '@layer zk-components {}');
    console.log(`  za11y/css/marble.css.dsp (${a11yFiles.length} partials)`);
}

// ===== Bundling helpers =====

/**
 * Joins the source files with '\n'. layerFor(file) names the cascade layer the file must declare,
 * or null for a file that is not checked.
 */
function concatSources(files, layerFor = () => null) {
    return files.map(file => {
        const css = readFile(file);
        const layer = layerFor(file);
        if (layer) assertLayer(file, css, layer);
        return css;
    }).join('\n');
}

/**
 * Unlike writeDsp: skips an empty bundle and logs the path it wrote.
 */
function writeSheet(relPath, css) {
    if (!css.trim()) return;
    writeDsp(relPath, css);
    console.log(`  ${relPath}`);
}

/**
 * Guard: component/base CSS self-declares its cascade layer IN SOURCE (readability +
 * correctness — see doc/spec/layer-architecture-review.md). The build no longer injects
 * layers; it only VERIFIES the wrapper is present, so a new file that forgets it fails the
 * build instead of silently shipping unlayered (which would beat utilities + user CSS).
 */
function assertLayer(relPath, content, layer) {
    if (content.trim() && !new RegExp(`@layer\\s+${layer}\\s*\\{`).test(content)) {
        throw new Error(
            `CSS layer guard: ${relPath} must wrap its rules in "@layer ${layer} { … }". ` +
            'All component/base CSS belongs to a cascade layer.');
    }
}

// ===== Minify =====

/**
 * Minifier: Lightning CSS, not CleanCSS. CleanCSS 5.3.3 does not understand the modern
 * syntax this theme is built on and fails SILENTLY — it reports the damage in
 * `output.warnings`, never in `output.errors`, so an errors-only check ships broken CSS at
 * exit 0. Measured on this source tree (2026-09-03):
 *   - a bare `@layer a, b;` order statement EMPTIES the whole output;
 *   - `@scope (...) { … }` loses the rules inside it;
 *   - `::picker(select)` / `appearance: base-select` rules are dropped with
 *     "Invalid property name … Ignoring." (3 live warnings before this swap).
 * Lightning CSS parses all of them correctly, which removes the two workarounds this
 * function used to carry. Bootstrap 6 replaced clean-css for the same reason.
 *
 * No `targets` is set on purpose: this theme is modern-browsers-only, so nothing should be
 * downlevelled. Adding targets would start rewriting output and is a separate decision.
 */
function minifyCss(css) {
    if (isDev || !css) return css;
    const nums = [];
    const minified = lightningMinify(maskLongNumbers(css, nums));
    // Never break the build / empty a file on a minifier hiccup.
    if (minified === null) return css;
    return restoreLongNumbers(minified, nums);
}

/**
 * Returns null when Lightning CSS throws.
 */
function lightningMinify(css) {
    try {
        const res = lightningTransform({
            filename: 'theme.css',
            code: Buffer.from(css),
            minify: true,
        });
        // Surface warnings. CleanCSS's silent-warning behaviour is exactly what this swap
        // was meant to end, so never let one pass unprinted.
        for (const w of res.warnings || []) {
            console.warn(`  ⚠ minify warning: ${w.message || w}`);
        }
        return res.code.toString();
    } catch (e) {
        console.warn(`  ⚠ minify failed, writing raw CSS: ${e.message}`);
        return null;
    }
}

/**
 * A number carrying 7+ significant digits is silently ROUNDED TO 6 when it sits inside a
 * custom property. Real properties are parsed as typed values and keep full precision
 * (`z-index:9999999` survives); a custom property is an untyped token stream, and Lightning
 * CSS re-formats the numeric tokens in it through an f32 with 6 significant digits.
 * Measured on 1.33.0, no warning of any kind:
 *   --x:9999999   -> 10000000      --x:2147483647 -> 2147480000
 *   --x:1000001   -> 1000000       --x:0.123456789 -> .123457
 * This bit `--zk-index-error: 9999999` (the fatal-JS-error stacking value, deliberately
 * copied from ZK core's own theme CSS — see doc/spec/zindex-scale.md), which shipped as
 * 10000000 and broke the zindex project's computed-style assertion.
 * Mask those tokens with an inert identifier — legal anywhere in a custom property's token
 * stream — and restore them verbatim afterwards.
 */
const CUSTOM_PROP_DECL_RE = /(--[\w-]+\s*:)([^;}]*)/g;
const NUM_TOKEN_RE = /\d*\.?\d+(?:[eE][+-]?\d+)?/g;
const LONG_NUM_MASK_RE = /ZKNUM(\d+)ZZ/g;
const MAX_SAFE_SIG_DIGITS = 6;

/**
 * Pushes each masked number onto store; its index is the mask's number.
 */
function maskLongNumbers(css, store) {
    return css.replace(CUSTOM_PROP_DECL_RE, (_, head, value) =>
        head + value.replace(NUM_TOKEN_RE, (num) => {
            const digits = num.replace(/\D/g, '').replace(/^0+/, '');
            if (digits.length <= MAX_SAFE_SIG_DIGITS) return num;
            return `ZKNUM${store.push(num) - 1}ZZ`;
        }));
}

function restoreLongNumbers(css, store) {
    return css.replace(LONG_NUM_MASK_RE, (_, i) => store[Number(i)]);
}

// ===== File IO =====

/**
 * Missing source files read as '' (the bundles tolerate an absent partial).
 */
function readFile(relativePath) {
    const fullPath = path.join(webDir, relativePath);
    if (fs.existsSync(fullPath)) {
        return fs.readFileSync(fullPath, 'utf8');
    }
    return '';
}

/**
 * Writes CSS minified (unminified with --dev) under the output root.
 */
function writeDsp(relativePath, content) {
    writeRaw(relativePath, minifyCss(content));
}

/**
 * Writes content verbatim under the output root. Used for non-CSS outputs and for CSS the build
 * has already minified and then wrapped in an at-rule (toEmbedReset).
 */
function writeRaw(relativePath, content) {
    const fullPath = path.join(themeDir, relativePath);
    fs.mkdirSync(path.dirname(fullPath), { recursive: true });
    fs.writeFileSync(fullPath, content);
}

/**
 * Writes a committed side output under ZK_ROOT (not the output root).
 */
function writeRepoFile(relativePath, content) {
    const destPath = path.join(ZK_ROOT, relativePath);
    fs.mkdirSync(path.dirname(destPath), { recursive: true });
    fs.writeFileSync(destPath, content, 'utf8');
}

// Last statement on purpose: the source lists above are consts that build() reads.
build();
