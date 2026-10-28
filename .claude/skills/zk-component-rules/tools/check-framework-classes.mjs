#!/usr/bin/env node
/*
 * check-framework-classes.mjs — ZK framework-class compliance checker
 *
 * Verifies that a ZK theme defines every JS-toggled CONTRACT class (from the
 * framework-classes registry) under its exact stock-ZK name. Catches the failure
 * mode behind the Marble splitlayout drag bug: a class ZK's client engine toggles
 * at runtime (e.g. `.z-flex`) was renamed / omitted, so the theme silently breaks
 * hflex/vflex, drag, frozen columns, render-defer, etc.
 *
 * This is the AUTOMATED, deterministic half of the compliance check. It reliably
 * reports MISSING and renamed-candidate classes. It CANNOT catch the semantic case
 * where a class IS defined but a component hard-codes its effect onto a JS-managed
 * container (e.g. splitlayout root `display:flex`) — that needs the agent-guided
 * semantic-review checklist in reference/framework-classes.md.
 *
 * Theme-agnostic: point --theme-css-dir at any ZK theme's COMPILED CSS output.
 *
 * Usage:
 *   node check-framework-classes.mjs --theme-css-dir <dir> [--registry <json>] [--json]
 *
 * Defaults: --registry resolves to ../reference/framework-classes.json (sibling of this script).
 * Exit code: 0 = all contract classes present; 1 = one or more MISSING; 2 = bad args / IO error.
 */
import { readFileSync, readdirSync, statSync } from 'node:fs';
import { join, dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const HERE = dirname(fileURLToPath(import.meta.url));

function parseArgs(argv) {
  const a = { registry: resolve(HERE, '../reference/framework-classes.json'), themeCssDir: null, json: false };
  for (let i = 0; i < argv.length; i++) {
    const k = argv[i];
    if (k === '--theme-css-dir') a.themeCssDir = argv[++i];
    else if (k === '--registry') a.registry = argv[++i];
    else if (k === '--json') a.json = true;
    else if (k === '--help' || k === '-h') a.help = true;
    else { console.error(`Unknown arg: ${k}`); a.bad = true; }
  }
  return a;
}

function walkCss(dir) {
  const out = [];
  for (const name of readdirSync(dir)) {
    const p = join(dir, name);
    const st = statSync(p);
    if (st.isDirectory()) out.push(...walkCss(p));
    else if (name.endsWith('.css') || name.endsWith('.css.dsp')) out.push(p);
  }
  return out;
}

// Strip /* */ comments, then collapse whitespace for whitespace-insensitive matching.
function loadCss(files) {
  let text = '';
  for (const f of files) text += '\n' + readFileSync(f, 'utf8');
  return text.replace(/\/\*[\s\S]*?\*\//g, ' ').replace(/\s+/g, ' ');
}

// Exact class-token boundary: the char after the class name must not continue the class token.
function classToken(cls) {
  // cls may be a compound selector (e.g. ".z-flex>:not(.z-flex-item)") — match it literally,
  // whitespace-insensitive. Otherwise match the bare class with a trailing boundary.
  const esc = cls.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  if (/[>:~+\s([]/.test(cls.slice(1))) {
    // compound selector: require it appears as a selector (followed by other selector chars then '{')
    return new RegExp(esc.replace(/\s+/g, '\\s*') + '[^{}]*\\{');
  }
  // simple class: ".z-flex" must be followed by a non-class char, and define a rule somewhere
  return new RegExp(esc + '(?![A-Za-z0-9_-])[^{}]*\\{');
}

// Key property of a contract css declaration (first "prop:" token).
function keyProp(css) {
  const m = /([a-z-]+)\s*:/.exec(css || '');
  return m ? m[1] : null;
}

// For a MISSING simple class, suggest renamed candidates sharing the significant stem.
function renameCandidates(cls, css) {
  const stem = cls.replace(/^\.z-/, '').split('-').filter(s => s.length > 2)[0];
  if (!stem) return [];
  const re = new RegExp('\\.z-[A-Za-z0-9_-]*' + stem + '[A-Za-z0-9_-]*', 'g');
  const found = new Set();
  let m;
  while ((m = re.exec(css)) && found.size < 8) {
    if (m[0] !== cls) found.add(m[0]);
  }
  return [...found];
}

function main() {
  const args = parseArgs(process.argv.slice(2));
  if (args.help) {
    console.log('Usage: node check-framework-classes.mjs --theme-css-dir <dir> [--registry <json>] [--json]');
    process.exit(0);
  }
  if (args.bad || !args.themeCssDir) {
    console.error('Required: --theme-css-dir <compiled css dir>. See --help.');
    process.exit(2);
  }

  let registry, files, css;
  try {
    registry = JSON.parse(readFileSync(args.registry, 'utf8'));
    files = walkCss(resolve(args.themeCssDir));
    css = loadCss(files);
  } catch (e) {
    console.error(`IO error: ${e.message}`);
    process.exit(2);
  }

  const contract = registry.filter(r => r.type === 'contract');
  const results = contract.map(r => {
    const present = classToken(r.class).test(css);
    if (!present) {
      return { class: r.class, status: 'MISSING', jsSite: r.jsSite, candidates: renameCandidates(r.class, css) };
    }
    const kp = keyProp(r.css);
    // Check the key property exists in ANY rule body for this class (best-effort conflict signal).
    // Scan all matching rules — the class can appear inside :not()/compound selectors whose body
    // legitimately lacks the key prop; we only need ONE real rule to carry it.
    let keyPropPresent = true;
    if (kp) {
      const esc = r.class.replace(/[.*+?^${}()|[\]\\]/g, '\\$&').replace(/\s+/g, '\\s*');
      const ruleRe = new RegExp(esc + '(?![A-Za-z0-9_-])[^{}]*\\{([^}]*)\\}', 'g');
      const propRe = new RegExp('(^|;|\\{|\\s)' + kp + '\\s*:');
      keyPropPresent = false;
      let m;
      while ((m = ruleRe.exec(css))) { if (propRe.test(m[1])) { keyPropPresent = true; break; } }
    }
    return { class: r.class, status: keyPropPresent ? 'PRESENT' : 'PRESENT_NO_KEYPROP', keyProp: kp, jsSite: r.jsSite };
  });

  const missing = results.filter(r => r.status === 'MISSING');
  const review = results.filter(r => r.status === 'PRESENT_NO_KEYPROP');

  if (args.json) {
    console.log(JSON.stringify({ themeCssDir: resolve(args.themeCssDir), filesScanned: files.length, results, missing: missing.length }, null, 2));
  } else {
    console.log(`Framework-class compliance — ${contract.length} contract classes, ${files.length} CSS files scanned`);
    console.log(`Theme: ${resolve(args.themeCssDir)}\n`);
    for (const r of results) {
      const tag = r.status === 'PRESENT' ? 'OK  ' : r.status === 'PRESENT_NO_KEYPROP' ? 'WARN' : 'MISS';
      let line = `[${tag}] ${r.class}`;
      if (r.status === 'PRESENT_NO_KEYPROP') line += `  (defined, but key prop "${r.keyProp}" not found — possible override/conflict; ${r.jsSite})`;
      if (r.status === 'MISSING') line += `  (toggled by ${r.jsSite})${r.candidates.length ? ' — possible renames: ' + r.candidates.join(', ') : ''}`;
      console.log(line);
    }
    console.log('');
    if (missing.length === 0 && review.length === 0) console.log('PASS — all contract classes defined.');
    else {
      if (missing.length) console.log(`FAIL — ${missing.length} contract class(es) MISSING.`);
      if (review.length) console.log(`${review.length} class(es) need semantic review (defined but key property absent).`);
    }
  }

  process.exit(missing.length ? 1 : 0);
}

main();
