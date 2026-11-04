#!/usr/bin/env node
//
// check-css-dsp.js — verify every .css.dsp ZK will actually REQUEST exists in the build output.
//
// WHY: a ZK component renders unstyled — silently, no error — if the .css.dsp ZK requests for it
// is absent or sits at the wrong path. The path ZK requests is NOT chosen by the theme; it is
// decided by the ZK lang files (see doc/spec/css-dsp-file-structure.md, Path-resolution model):
//
//   per-component CSS  → lang.xml/lang-addon.xml  <css-uri> (relative)  resolved against the
//                        widget's JS package: <widget-package>, else derived from <widget-class>
//                        (drop the class segment).  Full = js/<pkg-as-path>/<css-uri>.
//   absolute css-uri   → "~./<path>" / "/<path>" used as-is.
//   global bundles     → ZK core convention: norm.css.dsp from the zk.wcs
//                        <stylesheet> list; footer.css.dsp hard-coded in WcsExtendlet.java.
//                        Required only for --module zul (the module that owns those bundles).
//
// This script reads the ZK lang file for the selected module as the source of truth, resolves
// every <css-uri> to its full path, adds the module's global bundles (if any), and asserts each
// file exists under the built output dir. It is the runtime-faithful complement to
// build-css.js's assertNoOrphanComponentCss() (which guards the INVERSE: a no-css-uri file must
// not be emitted as an orphan ZK never requests). This script enforces the inverse too, on the
// build output: a built .css.dsp that nothing requests fails the check unless it is listed in
// EXPECTED_EXTRAS.
//
// One repository root per module — see build-css.js's header comment for the full table:
//
//   --module | lang file                                                         | output root (default)
//   zul      | ZK_ROOT/zul/src/main/resources/metainfo/zk/lang.xml               | ZK_ROOT/zul/codegen/resources/web
//   zkmax    | ZKCML_ROOT/zkmax/src/main/resources/metainfo/zk/lang-addon.xml    | ZKCML_ROOT/zkmax/codegen/resources/web
//   zkex     | ZKCML_ROOT/zkex/src/main/resources/metainfo/zk/lang-addon.xml     | ZKCML_ROOT/zkex/codegen/resources/web
//
// where ZK_ROOT = path.resolve(__dirname, '..') (this repo) and
// ZKCML_ROOT = path.resolve(ZK_ROOT, '../zkcml') (the enterprise sibling repo), unless
// overridden by --zk-home <workspace-root-containing-both-zk-and-zkcml>.
//
// --module za11y is different: za11y has no lang file. A11yThemeURIHandler requests
// ~./za11y/css/<sheet>.css.dsp for the sheet that themes.properties maps the current theme to, and
// silently skips a sheet that does not exist. So the check reads themes.properties and asserts the
// Marble entry's sheet was built. Only Marble can be checked: the IceBlue sheets are produced
// outside this checkout, so they are not in the output dir.
//
//   za11y    | ZKCML_ROOT/za11y/src/main/resources/metainfo/org/zkoss/za11y/theme/themes.properties | ZKCML_ROOT/za11y/codegen/resources/web
//
// font-awesome.css.dsp is neither requested nor built: ZK 11 dropped its line from zul/css/zk.wcs
// (Marble renders icons via Lucide masks) and build-css.js emits no stub for it.
//
// Usage:
//   node scripts/check-css-dsp.js --module zul|zkmax|zkex|za11y [--theme-dir <dir>] [--zk-home <dir>]
// Exit: 0 = all present, no unexpected extras · 1 = missing file(s) or unexpected extra(s) ·
//       2 = cannot run (bad --module, or lang file not found)

const fs = require('fs');
const path = require('path');
const { extractRequired } = require('./css-uri');

function argFor(flag) {
    const i = process.argv.indexOf(flag);
    return i >= 0 && i + 1 < process.argv.length ? process.argv[i + 1] : undefined;
}

const ZK_ROOT = path.resolve(__dirname, '..');
// --zk-home (or $ZK_HOME) points at the workspace root that holds zk/ and zkcml/ as siblings —
// the same role ZK_HOME played before the port. ZK_ROOT itself always comes from this script's
// own location (it only ever runs from inside this exact zk checkout); the override only
// relocates where the zkcml sibling is found.
const zkHomeOverride = argFor('--zk-home') || process.env.ZK_HOME;
const ZKCML_ROOT = zkHomeOverride ? path.join(zkHomeOverride, 'zkcml') : path.resolve(ZK_ROOT, '../zkcml');

// Per-module lang file + default output root.
const MODULES = {
    zul: {
        langFiles: [path.join(ZK_ROOT, 'zul/src/main/resources/metainfo/zk/lang.xml')],
        outDir: path.join(ZK_ROOT, 'zul/codegen/resources/web'),
        // Global bundles ZK requests outside lang-addon (zk.wcs <stylesheet> + WcsExtendlet
        // literal) — owned by zul. font-awesome.css.dsp omitted on purpose (dropped from
        // zk.wcs in ZK 11).
        globalBundles: ['zul/css/norm.css.dsp', 'zul/css/footer.css.dsp'],
    },
    zkmax: {
        langFiles: [path.join(ZKCML_ROOT, 'zkmax/src/main/resources/metainfo/zk/lang-addon.xml')],
        outDir: path.join(ZKCML_ROOT, 'zkmax/codegen/resources/web'),
        globalBundles: [],
    },
    zkex: {
        langFiles: [path.join(ZKCML_ROOT, 'zkex/src/main/resources/metainfo/zk/lang-addon.xml')],
        outDir: path.join(ZKCML_ROOT, 'zkex/codegen/resources/web'),
        globalBundles: [],
    },
    za11y: {
        langFiles: [],
        outDir: path.join(ZKCML_ROOT, 'za11y/codegen/resources/web'),
        globalBundles: [],
        themesFile: path.join(ZKCML_ROOT, 'za11y/src/main/resources/metainfo/org/zkoss/za11y/theme/themes.properties'),
    },
};

// The theme name Themes.getCurrentTheme() returns for Marble (StandardTheme.DEFAULT_NAME).
const MARBLE_THEME = 'marble';

const moduleName = argFor('--module');
if (!moduleName || !MODULES[moduleName]) {
    console.error('Usage: node scripts/check-css-dsp.js --module zul|zkmax|zkex|za11y [--theme-dir <dir>] [--zk-home <dir>]');
    process.exit(2);
}

const LANG_FILES = MODULES[moduleName].langFiles;
const THEME_DIR = argFor('--theme-dir') || MODULES[moduleName].outDir;
const GLOBAL_BUNDLES = MODULES[moduleName].globalBundles;

// Only css-uris under these prefixes are served from this module's own output. A css-uri
// outside them belongs to a different module (or stock ZK), so this module is not required to
// ship it. The selected module's own lang file only ever declares its own components, so this
// is really just a sanity boundary.
const THEMED_PREFIXES = [`js/${moduleName}/`, `${moduleName}/`];

// Components that exist in the ZK source on disk but belong to a NEWER ZK version than this
// theme currently targets. They are intentionally out of scope for now and will be styled when
// the theme moves to that ZK version — so the check skips them rather than failing. They are
// still LISTED in the report (never silently dropped). Re-evaluate on upgrade.
const FORWARD_VERSION_SKIP = new Map([]);

// Built .css.dsp files that no css-uri / global bundle requests but that are legitimate. Any other
// unrequested dsp is dead CSS (ZK never loads it) and fails the check.
const EXPECTED_EXTRAS = new Map([
    // TabletThemeURIHandler (zkmax) adds this as its own stylesheet via ThemeURIModifier, not via a css-uri.
    ['zkmax/css/tablet.css.dsp', 'added by TabletThemeURIHandler, not requested by a css-uri'],
]);

function isThemed(p) {
    return THEMED_PREFIXES.some(pre => p.startsWith(pre));
}

// za11y: the sheet themes.properties maps Marble to must exist in the build output.
function checkA11yMarbleSheet() {
    const { themesFile } = MODULES.za11y;
    if (!fs.existsSync(themesFile)) {
        console.error('✖ Cannot run: themes.properties not found: ' + themesFile);
        process.exit(2);
    }
    if (!fs.existsSync(THEME_DIR)) {
        console.error('✖ Theme build dir not found: ' + THEME_DIR + '\n  Run `node scripts/build-css.js --module za11y` first.');
        process.exit(2);
    }
    const entry = fs.readFileSync(themesFile, 'utf8').split(/\r?\n/)
        .map(line => line.split('='))
        .find(([key]) => key.trim() === MARBLE_THEME);
    if (!entry) {
        console.error(`✖ ${themesFile} has no "${MARBLE_THEME}" entry, so za11y adds no sheet for Marble.`);
        process.exit(1);
    }
    const sheet = `za11y/css/${entry[1].trim()}.css.dsp`;
    if (!fs.existsSync(path.join(THEME_DIR, sheet))) {
        console.error(`✖ MISSING — themes.properties maps "${MARBLE_THEME}" to ${sheet} but the build does not contain it:`);
        console.error('    ' + path.join(THEME_DIR, sheet));
        console.error('  Fix: run `node scripts/build-css.js --module za11y`.');
        process.exit(1);
    }
    console.log(`\n✓ za11y sheet for "${MARBLE_THEME}" is present: ${sheet}\n`);
}

function main() {
    if (moduleName === 'za11y') {
        checkA11yMarbleSheet();
        return;
    }
    // 0. Verify the module's lang file is reachable; without it we cannot know what ZK requests.
    const missingLang = LANG_FILES.filter(f => !fs.existsSync(f));
    if (missingLang.length) {
        console.error(`✖ Cannot run: lang file(s) for --module ${moduleName} not found:`);
        missingLang.forEach(f => console.error('    missing: ' + f));
        console.error('  Point at the right checkout with:  node scripts/check-css-dsp.js --module ' + moduleName + ' --zk-home <workspace-root>');
        process.exit(2);
    }
    if (!fs.existsSync(THEME_DIR)) {
        console.error('✖ Theme build dir not found: ' + THEME_DIR + '\n  Run `node scripts/build-css.js --module ' + moduleName + '` first.');
        process.exit(2);
    }

    // 1. Collect every required css-uri across the module's lang file(s).
    const raw = [];
    for (const f of LANG_FILES) raw.push(...extractRequired(fs.readFileSync(f, 'utf8'), f));

    // 2. Resolve + dedup to the set of theme-relative paths the module must ship.
    const required = new Map(); // path → {sources:Set, langFiles:Set}
    const unresolved = [];      // relative css-uri with no derivable package
    const skipped = [];         // resolved but not under a themed prefix
    const forward = new Map();  // resolved but a newer-ZK-version component (out of scope now)
    for (const r of raw) {
        if (!r.resolved) { unresolved.push(r); continue; }
        if (FORWARD_VERSION_SKIP.has(r.resolved)) { forward.set(r.resolved, FORWARD_VERSION_SKIP.get(r.resolved)); continue; }
        if (!isThemed(r.resolved)) { skipped.push(r.resolved); continue; }
        if (!required.has(r.resolved)) required.set(r.resolved, new Set());
        required.get(r.resolved).add(r.comp);
    }
    for (const b of GLOBAL_BUNDLES) {
        if (!required.has(b)) required.set(b, new Set(['(global bundle)']));
    }

    // 3. Check existence in the build output.
    const present = [], emptyStub = [], missing = [];
    for (const [p, comps] of [...required].sort()) {
        const full = path.join(THEME_DIR, p);
        if (!fs.existsSync(full)) {
            missing.push({ p, comps: [...comps] });
        } else {
            const body = fs.readFileSync(full, 'utf8').replace(/<%@[^%]*%>/g, '');
            // "empty" = no CSS rule (only an empty @layer / comments / DSP directive)
            (/\{[^}]*[a-z-]+\s*:/.test(body) ? present : emptyStub).push(p);
        }
    }

    // 4. Built dsp that no css-uri/bundle requests: dead CSS unless it is a listed expected extra.
    const built = listDsp(THEME_DIR);
    const extra = built.filter(p => !required.has(p));
    const unexpectedExtra = extra.filter(p => !EXPECTED_EXTRAS.has(p));

    // ---- report ----
    console.log(`\nCSS.DSP coverage check  (--module ${moduleName}, ZK_ROOT=${ZK_ROOT})`);
    console.log(`  required by ZK : ${required.size}  (lang css-uri + ${GLOBAL_BUNDLES.length} global bundles)`);
    console.log(`  present (real) : ${present.length}`);
    console.log(`  present (stub) : ${emptyStub.length}`);
    console.log(`  MISSING        : ${missing.length}`);
    console.log(`  extra in build : ${extra.length}  (built but no css-uri requests them; ${unexpectedExtra.length} unexpected)`);
    if (forward.size) console.log(`  forward-skip   : ${forward.size}  (newer-ZK-version components, out of scope for current target)`);
    if (unresolved.length) console.log(`  unresolved     : ${unresolved.length}  (relative css-uri, no derivable package)`);

    if (emptyStub.length) {
        console.log('\n  empty stubs (file exists → no 404; component uses inherited styles):');
        emptyStub.forEach(p => console.log('    · ' + p));
    }
    if (extra.length) {
        console.log('\n  extra (built but no css-uri requests them; expected ones are listed in EXPECTED_EXTRAS):');
        extra.forEach(p => console.log('    + ' + p + (EXPECTED_EXTRAS.has(p) ? '   (expected: ' + EXPECTED_EXTRAS.get(p) + ')' : '')));
    }
    if (forward.size) {
        console.log('\n  ↪ skipped — components from a newer ZK version than the current target (handle on upgrade):');
        for (const [p, why] of forward) console.log(`    ~ ${p}   (${why})`);
    }
    if (unresolved.length) {
        console.log('\n  ⚠ unresolved css-uri (no widget-package and underivable widget-class):');
        unresolved.forEach(r => console.log(`    ? ${r.cssuri}  (${r.comp} in ${path.basename(r.langFile)})`));
    }
    if (missing.length) {
        console.log('\n✖ MISSING — ZK requests these but the theme does not ship them (component renders unstyled):');
        missing.forEach(({ p, comps }) => console.log(`    - ${p}   ← ${comps.join(', ')}`));
        console.log('\n  Fix: add a source CSS (or stub) under the module\'s web root at <path-without-.dsp>,');
        console.log('  or bundle it in scripts/build-css.js, then re-run `node scripts/build-css.js --module ' + moduleName + '`.');
        process.exit(1);
    }
    if (unexpectedExtra.length) {
        console.log('\n✖ UNEXPECTED EXTRA — built but ZK never requests these (dead CSS, the component is not styled by them):');
        unexpectedExtra.forEach(p => console.log('    + ' + p));
        console.log('\n  Fix: register a css-uri for it, bundle it in scripts/build-css.js, or delete the source CSS');
        console.log('  (and the stale build output). If ZK requests it by other means, add it to EXPECTED_EXTRAS with the reason.');
        process.exit(1);
    }
    console.log('\n✓ All css-uri ZK requests are present in the build, and every built file is requested.\n');
}

function listDsp(dir) {
    const out = [];
    (function walk(d) {
        for (const e of fs.readdirSync(d, { withFileTypes: true })) {
            const fp = path.join(d, e.name);
            if (e.isDirectory()) walk(fp);
            else if (e.name.endsWith('.css.dsp')) out.push(path.relative(dir, fp).replace(/\\/g, '/'));
        }
    })(dir);
    return out;
}

main();
