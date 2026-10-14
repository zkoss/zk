'use strict';

// Breakpoint consistency check — used by scripts/build-css.js (--module zkmax).
//
// The framework has one breakpoint scale (doc/breakpoint-unified-spec-proposal.md §9). Its values
// exist in two places that cannot read each other: the width conditions of `@media` / `@container`
// in CSS (a condition cannot use var()), and the `_DEFAULT_BREAKPOINTS` constant of the zkmax
// Grid and Listbox `responsive` feature. This check fails the build when they drift apart:
//   1. the Grid and Listbox constants must be equal;
//   2. every width condition in the scanned CSS must be one of those values
//      (`min-width: N`, `width >= N`), or the exact complement of one (`max-width: N-1`,
//      `width < N`).
// `xxl` is ignored on purpose: it is being removed from the JS side, so it is neither required nor
// accepted as a CSS value.

const fs = require('fs');
const path = require('path');

const SCALE = ['sm', 'md', 'lg', 'xl'];

function stripComments(css) {
    return css.replace(/\/\*[\s\S]*?\*\//g, '');
}

// Reads the `_DEFAULT_BREAKPOINTS` object literal of a TypeScript file → { sm: 576, ... }.
function readJsBreakpoints(file) {
    const src = fs.readFileSync(file, 'utf8');
    const m = /_DEFAULT_BREAKPOINTS\b[^=]*=\s*\{([^}]*)\}/.exec(src);
    if (!m) throw new Error(`breakpoint check: no _DEFAULT_BREAKPOINTS literal found in ${file}`);
    const out = {};
    for (const pair of m[1].matchAll(/(\w+)\s*:\s*(\d+)/g)) out[pair[1]] = Number(pair[2]);
    for (const name of SCALE) {
        if (!(name in out)) throw new Error(`breakpoint check: ${file} has no "${name}" breakpoint`);
    }
    return out;
}

function listCssFiles(dir) {
    const files = [];
    (function walk(d) {
        for (const e of fs.readdirSync(d, { withFileTypes: true })) {
            const full = path.join(d, e.name);
            if (e.isDirectory()) walk(full);
            else if (e.name.endsWith('.css')) files.push(full);
        }
    })(dir);
    return files;
}

// Width conditions of every @media / @container prelude: [{ kind: 'min'|'max', px, text }].
function widthConditions(css) {
    const found = [];
    for (const m of stripComments(css).matchAll(/@(?:media|container)\b([^{;]*)\{/g)) {
        const prelude = m[1];
        for (const c of prelude.matchAll(/\b(min|max)-width\s*:\s*(\d+(?:\.\d+)?)px/g)) {
            found.push({ kind: c[1], px: Number(c[2]), text: c[0] });
        }
        for (const c of prelude.matchAll(/\bwidth\s*(>=|<=|>|<)\s*(\d+(?:\.\d+)?)px/g)) {
            found.push({ kind: c[1] === '>=' || c[1] === '>' ? 'min' : 'max', px: Number(c[2]), text: c[0], strict: c[1] === '<' || c[1] === '>' });
        }
    }
    return found;
}

// jsFiles: the zkmax files that define _DEFAULT_BREAKPOINTS. cssRoots: directories scanned for CSS.
// Returns an array of error strings (empty = consistent).
function checkBreakpoints({ jsFiles, cssRoots }) {
    const errors = [];
    const defs = jsFiles.map(f => ({ file: f, bp: readJsBreakpoints(f) }));
    const ref = defs[0].bp;
    for (const d of defs.slice(1)) {
        for (const name of SCALE) {
            if (d.bp[name] !== ref[name]) {
                errors.push(`${d.file}: ${name} = ${d.bp[name]}, but ${defs[0].file} has ${ref[name]}`);
            }
        }
    }
    const mins = new Set(SCALE.map(n => ref[n]));
    const maxs = new Set(SCALE.map(n => ref[n] - 1));
    for (const root of cssRoots) {
        for (const file of listCssFiles(root)) {
            for (const c of widthConditions(fs.readFileSync(file, 'utf8'))) {
                const ok = c.kind === 'min' ? mins.has(c.px) && !c.strict
                    : c.strict ? mins.has(c.px) : maxs.has(c.px);
                if (!ok) {
                    errors.push(`${file}: "${c.text}" is not a framework breakpoint `
                        + `(allowed min-width: ${[...mins].join('/')}px, max-width: ${[...maxs].join('/')}px)`);
                }
            }
        }
    }
    return errors;
}

module.exports = { checkBreakpoints, readJsBreakpoints, widthConditions, SCALE };
