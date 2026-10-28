'use strict';

// Utility-class manifest generator.
//
// Produces the canonical, machine-readable catalogue of Marble's authorable `z-*` utility
// classes from the utility CSS sources, so the IDE plugin, AI agents and the docs all read one
// generated list instead of a hand-maintained copy that rots at every release.
// See doc/utility-class-discovery.md for the design and the rejected alternatives.
//
// Two independent extractors run over the same input and must agree:
//   1. a brace-matching text scan, which yields the declarations and the at-rule prelude
//      verbatim (an AST walk would re-serialise them, losing the authored spelling);
//   2. a Lightning CSS AST walk, used purely to cross-check the set of class names.
// They disagree only if the text scan is wrong, which is the point — a regex over line starts
// silently misses the 57 responsive/print variants nested in @media and @container.

const fs = require('fs');
const path = require('path');
const { transform } = require('lightningcss');

// Comments are stripped before scanning: _stack.css documents its own classes in prose
// (`<div sclass="z-vstack">`), which a naive scan would read as CSS.
function stripComments(css) {
    return css.replace(/\/\*[\s\S]*?\*\//g, '');
}

// Split a selector list / declaration list on top-level separators only, so a comma inside
// :is(…) or a semicolon inside a var() fallback does not split the token.
function splitTopLevel(text, sep) {
    const parts = [];
    let depth = 0, buf = '';
    for (const ch of text) {
        if (ch === '(' || ch === '[') depth++;
        else if (ch === ')' || ch === ']') depth--;
        if (ch === sep && depth === 0) { parts.push(buf); buf = ''; } else buf += ch;
    }
    parts.push(buf);
    return parts.map(s => s.trim()).filter(Boolean);
}

// Walk the stylesheet, returning one record per style rule with the at-rule preludes it is
// nested inside. At-rules are pushed on a stack; style-rule bodies are consumed whole so their
// braces never reach the stack.
function scanBlocks(css) {
    const blocks = [];
    const stack = [];
    let buf = '';
    for (let i = 0; i < css.length; i++) {
        const ch = css[i];
        if (ch === '{') {
            const prelude = buf.trim();
            buf = '';
            if (prelude.startsWith('@')) {
                stack.push(prelude);
                continue;
            }
            let depth = 1, j = i + 1, body = '';
            for (; j < css.length; j++) {
                if (css[j] === '{') depth++;
                else if (css[j] === '}' && --depth === 0) break;
                body += css[j];
            }
            blocks.push({ prelude, body, atRules: [...stack] });
            i = j;
        } else if (ch === '}') {
            stack.pop();
            buf = '';
        } else if (ch === ';') {
            buf = '';   // a statement at-rule, e.g. `@layer a, b;`
        } else {
            buf += ch;
        }
    }
    return blocks;
}

// The authorable class of a selector, or undefined. A selector qualifies when its first
// compound is exactly one class: `.z-clearfix::after` and `.z-vstack > * + *` do, while
// `.z-grid.z-sticky-header .z-grid-header` does not — there the class is a modifier on a
// component, documented with that component. `[class*="z-elevation-"]` and `:root` yield none.
function subjectClass(selector) {
    const m = /^\.(z-[A-Za-z0-9_-]+)/.exec(selector);
    if (!m) return undefined;
    return selector[m[0].length] === '.' ? undefined : m[1];
}

// Same question asked of the Lightning CSS AST, for the cross-check.
function subjectClassFromAst(selector) {
    const lead = [];
    for (const part of selector) {
        if (part.type === 'combinator') break;
        lead.push(part);
    }
    const classes = lead.filter(p => p.type === 'class');
    return classes.length === 1 ? classes[0].name : undefined;
}

function astClassNames(filename, code) {
    const names = new Set();
    transform({
        filename, code, minify: false,
        visitor: {
            Rule: {
                style(rule) {
                    for (const sel of rule.value.selectors) {
                        const name = subjectClassFromAst(sel);
                        if (name && name.startsWith('z-')) names.add(name);
                    }
                    return undefined;
                },
            },
        },
    });
    return names;
}

// Every class name the component stylesheets define. The utility files are not purely
// authorable: _print.css also resets component chrome under @media print (`.z-panel`,
// `.z-mask`, `.z-listbox-body`, …). Those rules apply on their own and are not something an
// author writes in `sclass`, so they must not reach the catalogue. Asking the component CSS
// which names it owns settles it without a hand-maintained exclusion list.
function collectComponentClasses(componentDir) {
    const names = new Set();
    const walk = (dir) => {
        for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
            const full = path.join(dir, entry.name);
            if (entry.isDirectory()) walk(full);
            else if (entry.name.endsWith('.css')) {
                const css = stripComments(fs.readFileSync(full, 'utf8'));
                for (const block of scanBlocks(css))
                    for (const m of block.prelude.matchAll(/\.(z-[A-Za-z0-9_-]+)/g)) names.add(m[1]);
            }
        }
    };
    if (fs.existsSync(componentDir)) walk(componentDir);
    return names;
}

// Every `--zk-*` custom property the theme defines. A var() reference that resolves here is a
// theme token; one that does not is an input the page author is expected to supply, such as
// `--zk-cols` on z-grid-cols-auto.
function collectTokenNames(tokensDir) {
    const names = new Set();
    for (const file of fs.readdirSync(tokensDir).sort()) {
        if (!file.endsWith('.css')) continue;
        const css = stripComments(fs.readFileSync(path.join(tokensDir, file), 'utf8'));
        for (const m of css.matchAll(/(--zk-[A-Za-z0-9_-]+)\s*:/g)) names.add(m[1]);
    }
    return names;
}

/**
 * Build the manifest.
 *
 * Each entry carries `name`, `category` and `css`; `media`, `selector`, `inputs` and
 * `description` appear only where they say something a consumer cannot derive.
 *
 * @param webDir - module web root, e.g. zul/src/main/resources/web
 * @param relPaths - utility CSS files, relative to webDir, in bundle order
 * @param tokensDir - directory holding the token definitions
 * @param componentDir - directory holding the component CSS, e.g. webDir/js/zul
 */
function buildUtilityManifest(webDir, relPaths, tokensDir, componentDir) {
    const tokenNames = collectTokenNames(tokensDir);
    const componentClasses = collectComponentClasses(componentDir);
    const byName = new Map();
    const skipped = [];

    for (const relPath of relPaths) {
        const raw = fs.readFileSync(path.join(webDir, relPath), 'utf8');
        const category = path.basename(relPath).replace(/^_/, '').replace(/\.css$/, '');
        const fromText = new Set();

        for (const block of scanBlocks(stripComments(raw))) {
            const declarations = splitTopLevel(block.body, ';').map(decl => {
                const at = decl.indexOf(':');
                if (at < 0) throw new Error(`${relPath}: declaration without ':' — ${decl}`);
                return { property: decl.slice(0, at).trim(), value: decl.slice(at + 1).trim() };
            });
            const atRules = block.atRules.filter(a => !a.startsWith('@layer'));

            for (const selector of splitTopLevel(block.prelude, ',')) {
                const name = subjectClass(selector);
                if (!name) continue;
                fromText.add(name);
                if (componentClasses.has(name)) {
                    if (!skipped.includes(name)) skipped.push(name);
                    continue;
                }

                let entry = byName.get(name);
                if (!entry) {
                    entry = { name, category, rules: [], inputs: [], sets: [] };
                    byName.set(name, entry);
                }
                entry.rules.push({
                    selector,
                    // Verbatim prelude, e.g. "@media (min-width: 600px)", or '' when the rule
                    // applies unconditionally. Nothing in the utility CSS nests two deep.
                    condition: atRules[0] || '',
                    declarations,
                });
                for (const decl of declarations) {
                    // A var() the theme defines is a token and tells the author nothing; one it
                    // does not define is a value the author has to supply, which is worth saying.
                    for (const m of decl.value.matchAll(/var\(\s*(--[A-Za-z0-9_-]+)/g))
                        if (!tokenNames.has(m[1]) && !entry.inputs.includes(m[1]))
                            entry.inputs.push(m[1]);
                    if (decl.property.startsWith('--') && !entry.sets.includes(decl.property))
                        entry.sets.push(decl.property);
                }
            }
        }

        // Cross-check: the AST must see exactly the classes the text scan saw.
        const fromAst = astClassNames(relPath, Buffer.from(raw));
        const missing = [...fromAst].filter(n => !fromText.has(n));
        const extra = [...fromText].filter(n => !fromAst.has(n));
        if (missing.length || extra.length) {
            throw new Error(
                `Utility manifest: text scan and CSS parser disagree on ${relPath}.\n` +
                `  only in parser: ${missing.join(', ') || '—'}\n` +
                `  only in scan:   ${extra.join(', ') || '—'}`);
        }
    }

    // Second pass for the component classes the first pass cannot see. _print.css resets EE
    // component chrome too (`.z-drawer` is the zkmax Drawer), and those components live in a
    // different repository that a CE build must not depend on. What distinguishes them is that a
    // reset only ever appears under @media print, whereas a genuine print utility says so in its
    // name. Anything else print-only is chrome, not something an author writes in `sclass`.
    for (const [name, entry] of byName) {
        if (name.startsWith('z-d-print-')) continue;
        if (!entry.rules.every(r => /^@media\s+print\b/.test(r.condition))) continue;
        byName.delete(name);
        if (!skipped.includes(name)) skipped.push(name);
    }

    return {
        classes: [...byName.values()].map(flatten).sort((a, b) => a.name.localeCompare(b.name)),
        // Reported so a new collision shows up in the build log rather than silently shrinking
        // the catalogue.
        excludedComponentClasses: skipped.sort(),
    };
}

// Collapse an accumulated entry to what a consumer needs: name, category and the CSS as text.
// `media`, `selector` and `inputs` appear only where they say something — 57 of 425 entries.
// Everything else was derivable and has been dropped: the source file is the category, and the
// tokens a rule reads are visible in the CSS.
function flatten(entry) {
    // A class is usually authored as one rule, but a few are split across a shared block and a
    // specific one (.z-h1 twice, .z-hstack twice). Those merge: same selector, same condition,
    // no information in the split. Only z-paper carries a genuinely conditional second rule — a
    // print override — and it is dropped, because an author completing `sclass` is choosing the
    // class, not reading its print behaviour (that is doc/spec/print-styles.md).
    const unconditional = entry.rules.filter(r => !r.condition);
    const rules = unconditional.length ? unconditional : entry.rules;
    const out = {
        name: entry.name,
        category: entry.category,
        css: rules
            .flatMap(r => r.declarations)
            .map(d => `${d.property}: ${d.value}`)
            .join('; '),
    };
    // Only for a class that exists *nowhere else* — the responsive and print variants.
    if (!unconditional.length)
        out.media = rules[0].condition.replace(/^@(?:media|container)\s*/, '');
    // Only when the class is not the whole selector: `.z-vstack > * + *`, `.z-clearfix::after`.
    if (rules[0].selector !== `.${entry.name}`) out.selector = rules[0].selector;
    // A custom property a rule both sets and reads (z-grid-fill-xs sets --zk-grid-min, the base
    // z-grid-fill reads it) is plumbing, not something the author supplies.
    const inputs = entry.inputs.filter(v => !entry.sets.includes(v));
    if (inputs.length) out.inputs = inputs;
    return out;
}

module.exports = { buildUtilityManifest };
