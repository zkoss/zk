#!/usr/bin/env node
//
// Ported from zkThemeTemplate/scripts/check-css-dsp.js on 2026-09-10 (Marble -> zk migration, F26).
// This file is now maintained in zk; the template original is no longer the source of truth
// for it and is not edited to reflect changes made here.
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
// not be emitted as an orphan ZK never requests).
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
// font-awesome.css.dsp is neither requested nor built: ZK 11 dropped its line from zul/css/zk.wcs
// (Marble renders icons via Lucide masks) and build-css.js emits no stub for it (item 1.10).
//
// Usage:
//   node scripts/check-css-dsp.js --module zul|zkmax|zkex [--theme-dir <dir>] [--zk-home <dir>]
// Exit: 0 = all present · 1 = missing file(s) · 2 = cannot run (bad --module, or lang file not found)

const fs = require('fs');
const path = require('path');

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
};

const moduleName = argFor('--module');
if (!moduleName || !MODULES[moduleName]) {
    console.error('Usage: node scripts/check-css-dsp.js --module zul|zkmax|zkex [--theme-dir <dir>] [--zk-home <dir>]');
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

function pkgToPath(pkg) {
    return 'js/' + pkg.replace(/\./g, '/');
}

// widget-class "zul.wgt.Button" → package "zul.wgt"
function derivePkgFromClass(widgetClass) {
    if (!widgetClass) return undefined;
    const dot = widgetClass.lastIndexOf('.');
    return dot > 0 ? widgetClass.slice(0, dot) : undefined;
}

function resolveCssUri(cssuri, pkg) {
    const u = cssuri.trim();
    if (u.startsWith('~./')) return u.slice(3); // absolute (theme-relative root)
    if (u.startsWith('/')) return u.slice(1);
    if (!pkg) return undefined;                      // relative but no package → unresolvable
    return pkgToPath(pkg) + '/' + u;
}

// Pull every required css-uri out of one lang file, resolved to a full theme-relative path.
function extractRequired(xml, langFile) {
    const out = [];
    const compRe = /<component\b[\s\S]*?<\/component>/g;
    const tag = (block, name) => {
        const m = block.match(new RegExp(`<${name}>\\s*([^<\\s][^<]*?)\\s*</${name}>`));
        return m ? m[1].trim() : undefined;
    };
    let m;
    while ((m = compRe.exec(xml))) {
        const block = m[0];
        const pkg = tag(block, 'widget-package') || derivePkgFromClass(tag(block, 'widget-class'));
        const comp = tag(block, 'component-name') || '?';
        const uriRe = /<css-uri>\s*([^<]+?)\s*<\/css-uri>/g;
        let u;
        while ((u = uriRe.exec(block))) {
            out.push({ cssuri: u[1].trim(), pkg, comp, langFile, resolved: resolveCssUri(u[1], pkg) });
        }
    }
    // css-uri declared at language level (outside any component) — usually absolute.
    const remainder = xml.replace(compRe, '');
    const langUriRe = /<css-uri>\s*([^<]+?)\s*<\/css-uri>/g;
    let lu;
    while ((lu = langUriRe.exec(remainder))) {
        out.push({ cssuri: lu[1].trim(), pkg: undefined, comp: '(language-level)', langFile, resolved: resolveCssUri(lu[1], undefined) });
    }
    // Global <stylesheet href="~./…css.dsp"> declared at language level (outside any component):
    // ZK links it on every page, so it must exist too — zkex's skeleton.css.dsp dangled this way
    // after the LESS retirement (ZK-6112, F62).
    const sheetRe = /<stylesheet\b[^>]*\bhref="([^"]+\.css\.dsp)"[^>]*\/?>/g;
    let sh;
    while ((sh = sheetRe.exec(remainder))) {
        out.push({ cssuri: sh[1].trim(), pkg: undefined, comp: '(global <stylesheet>)', langFile, resolved: resolveCssUri(sh[1], undefined) });
    }
    return out;
}

function isThemed(p) {
    return THEMED_PREFIXES.some(pre => p.startsWith(pre));
}

function main() {
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

    // 4. (info) build dsp not requested by any css-uri/bundle — not a failure (see parity doc §2).
    const built = listDsp(THEME_DIR);
    const extra = built.filter(p => !required.has(p));

    // ---- report ----
    console.log(`\nCSS.DSP coverage check  (--module ${moduleName}, ZK_ROOT=${ZK_ROOT})`);
    console.log(`  required by ZK : ${required.size}  (lang css-uri + ${GLOBAL_BUNDLES.length} global bundles)`);
    console.log(`  present (real) : ${present.length}`);
    console.log(`  present (stub) : ${emptyStub.length}`);
    console.log(`  MISSING        : ${missing.length}`);
    console.log(`  extra in build : ${extra.length}  (built but no css-uri requests them — informational)`);
    if (forward.size) console.log(`  forward-skip   : ${forward.size}  (newer-ZK-version components, out of scope for current target)`);
    if (unresolved.length) console.log(`  unresolved     : ${unresolved.length}  (relative css-uri, no derivable package)`);

    if (emptyStub.length) {
        console.log('\n  empty stubs (file exists → no 404; component uses inherited styles):');
        emptyStub.forEach(p => console.log('    · ' + p));
    }
    if (extra.length) {
        console.log('\n  extra (informational — built but no css-uri requests them; expected cases:');
        console.log('    zk.wcs-aggregated select/cell/bandpopup · TabletThemeURIHandler-injected tablet.css.dsp ·');
        console.log('    dual package paths · empty placeholders. A truly unexpected entry here may be dead CSS):');
        extra.forEach(p => console.log('    + ' + p));
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
    console.log('\n✓ All css-uri ZK requests are present in the build.\n');
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
