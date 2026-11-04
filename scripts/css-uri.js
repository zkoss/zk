/**
 * @file Reads which .css.dsp files a ZK lang file makes ZK request.
 *
 * Shared by build-css.js (its orphan guard exempts a component CSS that a css-uri requests) and
 * check-css-dsp.js (it asserts every requested file is built). The resolution follows the
 * Path-resolution model in doc/spec/css-dsp-file-structure.md.
 */

/**
 * Package "zul.wgt" → "js/zul/wgt".
 */
function pkgToPath(pkg) {
    return 'js/' + pkg.replace(/\./g, '/');
}

/**
 * widget-class "zul.wgt.Button" → package "zul.wgt".
 */
function derivePkgFromClass(widgetClass) {
    if (!widgetClass) return undefined;
    const dot = widgetClass.lastIndexOf('.');
    return dot > 0 ? widgetClass.slice(0, dot) : undefined;
}

/**
 * Resolves a css-uri to a path relative to the web root. "~./<path>" and "/<path>" are absolute;
 * a relative css-uri is resolved against the widget's package, and is unresolvable (undefined)
 * without one.
 */
function resolveCssUri(cssuri, pkg) {
    const u = cssuri.trim();
    if (u.startsWith('~./')) return u.slice(3); // absolute (theme-relative root)
    if (u.startsWith('/')) return u.slice(1);
    if (!pkg) return undefined;                      // relative but no package → unresolvable
    return pkgToPath(pkg) + '/' + u;
}

/**
 * Pulls every required css-uri out of one lang file, resolved to a full theme-relative path.
 * Each entry is { cssuri, pkg, comp, langFile, resolved }.
 */
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
    // after the LESS retirement (ZK-6112).
    const sheetRe = /<stylesheet\b[^>]*\bhref="([^"]+\.css\.dsp)"[^>]*\/?>/g;
    let sh;
    while ((sh = sheetRe.exec(remainder))) {
        out.push({ cssuri: sh[1].trim(), pkg: undefined, comp: '(global <stylesheet>)', langFile, resolved: resolveCssUri(sh[1], undefined) });
    }
    return out;
}

module.exports = { extractRequired };
