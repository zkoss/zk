#!/usr/bin/env node
/*
 * check-default-display.js — mechanical detector for `display` declarations that
 * merely RESTATE the browser-default display of the element a ZK widget renders.
 *
 * Example: `.z-span { display: inline }` is a no-op — ZK's span widget renders a
 * native <span>, which is already `display: inline`, so the rule does nothing.
 *
 * This is check 5 (§G) of the marble-theme skill's CSS audit (reference/css-audit.md). It is ADVISORY and
 * READ-ONLY: it never edits CSS and always exits 0. It lists CANDIDATES; a human
 * (Step 2 of the skill) decides what is safe to delete.
 *
 * How it decides "what element does `.z-<name>` render":
 *   ZK widget mold files are the source of truth — `function <comp>$mold$(out)`
 *   emits the root tag as its first `out.push('<TAG' ...)`. This resolves non-1:1
 *   cases automatically (label -> <span>, image -> <img>, toolbarbutton -> <a>).
 *   Molds are globbed recursively under --zk-source (`**\/mold\/<name>.js`; e.g.
 *   .z-span's mold is wgt/mold/span.js even though its CSS lives in box/css/). If
 *   the ZK source is absent or a mold has no `out.push('<...')` (e.g. div.js), a
 *   small built-in fallback map covers the high-value cases.
 *
 * Classification per bare `.z-<name> { display: <v> }`:
 *   - inline value on an inline-default element   -> SAFE no-op   (§G1, removable)
 *   - block/inline-block restating the default    -> VERIFY first (§G2, may be a
 *       defensive anchor vs ZK runtime .z-flex toggling / @layer cascade)
 *   - replaced/form element (img/input/button/…)  -> SKIPPED (UA default varies)
 *   - declared != default                         -> real override (counted only)
 *   - tag not resolvable                          -> UNKNOWN (manual)
 *
 * Only BARE single-class selectors (`.z-<name>`, incl. members of a comma group)
 * are examined; descendant/compound/pseudo selectors (e.g. `.z-image img`,
 * `.z-a:hover`) are ignored — the root's default is the only thing we can reason
 * about mechanically.
 *
 * Usage:
 *   node check-default-display.js [options]
 *     --component-root <dir>  theme component CSS root  (default: <web>/js/zul)
 *     --zk-source <dir>       ZK js/zul source root     (default: the ZK10 path)
 *     --out <file>            write report here          (default: stdout)
 */
'use strict';
const fs = require('fs');
const path = require('path');

const SCRIPT_DIR = __dirname;
// .claude/skills/marble-theme/scripts -> project root is four levels up.
const PROJECT_ROOT = path.resolve(SCRIPT_DIR, '../../../..');

let COMPONENT_ROOT = path.join(PROJECT_ROOT, 'zul/src/main/resources/web/js/zul');
let ZK_SOURCE = '/Users/hawk/Documents/workspace/ZK10/zk/zul/src/main/resources/web/js/zul';
let OUT = '';

const argv = process.argv.slice(2);
for (let i = 0; i < argv.length; i++) {
  const a = argv[i];
  if (a === '--component-root') COMPONENT_ROOT = argv[++i];
  else if (a === '--zk-source') ZK_SOURCE = argv[++i];
  else if (a === '--out') OUT = argv[++i];
  else if (a === '-h' || a === '--help') {
    const hdr = fs.readFileSync(__filename, 'utf8').split('\n').slice(1, 47)
      .map(l => l.replace(/^ ?\*\/?/, '')).join('\n');
    process.stdout.write(hdr + '\n');
    process.exit(0);
  }
}

// ── Element default-display tables ───────────────────────────────────────────
// tag -> browser-default `display`. A `display` decl equal to this is redundant.
const TAG_DEFAULT = {};
// Non-replaced inline elements: `display: inline` on these is a true no-op.
for (const t of [
  'span', 'a', 'label', 'em', 'strong', 'i', 'b', 'u', 's', 'small', 'big',
  'sub', 'sup', 'abbr', 'cite', 'code', 'kbd', 'samp', 'var', 'mark', 'q',
  'time', 'ins', 'del', 'dfn', 'bdi', 'bdo', 'wbr', 'tt', 'output', 'data',
]) TAG_DEFAULT[t] = 'inline';
// Block elements.
for (const t of [
  'div', 'p', 'section', 'article', 'header', 'footer', 'nav', 'aside', 'main',
  'figure', 'figcaption', 'blockquote', 'pre', 'hr', 'address', 'fieldset',
  'form', 'hgroup', 'dl', 'dt', 'dd', 'ul', 'ol',
  'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
]) TAG_DEFAULT[t] = 'block';
// List item and the table display family (so `.z-cell` on a <td> is judged right).
TAG_DEFAULT.li = 'list-item';
TAG_DEFAULT.table = 'table';
TAG_DEFAULT.td = TAG_DEFAULT.th = 'table-cell';
TAG_DEFAULT.tr = 'table-row';
TAG_DEFAULT.thead = 'table-header-group';
TAG_DEFAULT.tbody = 'table-row-group';
TAG_DEFAULT.tfoot = 'table-footer-group';
TAG_DEFAULT.col = 'table-column';
TAG_DEFAULT.colgroup = 'table-column-group';
TAG_DEFAULT.caption = 'table-caption';

// Replaced / form-associated elements: UA default display varies and these are
// almost always styled deliberately — never treated as a redundancy finding.
const REPLACED = new Set([
  'img', 'input', 'button', 'select', 'textarea', 'meter', 'progress',
  'iframe', 'embed', 'object', 'video', 'audio', 'canvas', 'svg', 'picture',
]);

// Only these values CAN be some element's default — everything else (flex, grid,
// inline-flex, none, contents, …) is never a restatement, so it is skipped
// outright rather than reported as "unknown".
const CANDIDATE_VALUES = new Set([
  'inline', 'block', 'inline-block', 'list-item', 'table', 'inline-table',
  'table-cell', 'table-row', 'table-column', 'table-column-group',
  'table-row-group', 'table-header-group', 'table-footer-group', 'table-caption',
]);

// Fallback name -> tag for when ZK source is absent or a mold declares no tag.
const FALLBACK = {
  span: 'span', a: 'a', label: 'span', image: 'img', div: 'div',
  separator: 'div', groupbox: 'div', toolbarbutton: 'a', button: 'button',
  html: 'div', vbox: 'div', hbox: 'div',
};

// ── Comment stripping ────────────────────────────────────────────────────────
function blankBlockComments(s) {
  // Blank /* */ comments, preserving newlines so line numbers stay accurate.
  return s.replace(/\/\*[\s\S]*?\*\//g, m => m.replace(/[^\n]/g, ' '));
}

// ── ZK mold index (name -> mold file path) ───────────────────────────────────
function buildMoldIndex(root) {
  const idx = {};
  if (!root || !fs.existsSync(root)) return idx;
  const stack = [root];
  while (stack.length) {
    const dir = stack.pop();
    let entries;
    try { entries = fs.readdirSync(dir, { withFileTypes: true }); } catch { continue; }
    for (const e of entries) {
      const p = path.join(dir, e.name);
      if (e.isDirectory()) stack.push(p);
      else if (e.isFile() && e.name.endsWith('.js') &&
               p.replace(/\\/g, '/').includes('/mold/')) {
        const base = e.name.slice(0, -3);
        if (!(base in idx)) idx[base] = p; // first wins
      }
    }
  }
  return idx;
}

function tagFromMold(file) {
  let src;
  try { src = fs.readFileSync(file, 'utf8'); } catch { return null; }
  src = src.replace(/\/\*[\s\S]*?\*\//g, ' ').replace(/\/\/[^\n]*/g, ' ');
  const m = src.match(/out\.push\(\s*['"]<([a-zA-Z][a-zA-Z0-9]*)/);
  return m ? m[1].toLowerCase() : null;
}

// ── CSS walk ─────────────────────────────────────────────────────────────────
function walkCss(dir, acc = []) {
  let entries;
  try { entries = fs.readdirSync(dir, { withFileTypes: true }); } catch { return acc; }
  for (const e of entries) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walkCss(p, acc);
    else if (e.name.endsWith('.css')) acc.push(p);
  }
  return acc;
}

// ── Extract bare `.z-<name> { display: <v> }` declarations ───────────────────
const BARE_CLASS = /^\.z-[a-z0-9-]+$/;

function lineOf(s, idx) {
  let n = 1;
  for (let i = 0; i < idx && i < s.length; i++) if (s[i] === '\n') n++;
  return n;
}

function nearestSelector(stack) {
  for (let k = stack.length - 1; k >= 0; k--) {
    const p = stack[k];
    if (p && !p.startsWith('@')) return p;
  }
  return null;
}

function findDisplayDecls(cssRaw) {
  const css = blankBlockComments(cssRaw);
  const out = [];
  const stack = [];
  let buf = '';
  let tokenStart = -1;
  for (let i = 0; i < css.length; i++) {
    const ch = css[i];
    if (ch === '{') { stack.push(buf.trim()); buf = ''; tokenStart = -1; }
    else if (ch === '}') { stack.pop(); buf = ''; tokenStart = -1; }
    else if (ch === ';') {
      const decl = buf.trim();
      const dm = decl.match(/^display\s*:\s*(.+)$/i);
      if (dm) {
        const sel = nearestSelector(stack);
        if (sel) {
          const value = dm[1].replace(/!important/i, '').trim().toLowerCase().split(/\s+/)[0];
          if (!CANDIDATE_VALUES.has(value)) { buf = ''; tokenStart = -1; continue; }
          const line = lineOf(css, tokenStart >= 0 ? tokenStart : i);
          // A selector may be a comma group; evaluate each bare-class member.
          for (const part of sel.split(',').map(s => s.trim())) {
            if (BARE_CLASS.test(part)) {
              out.push({ name: part.slice(3), value, line }); // strip ".z-"
            }
          }
        }
      }
      buf = ''; tokenStart = -1;
    } else {
      if (tokenStart < 0 && !/\s/.test(ch)) tokenStart = i;
      buf += ch;
    }
  }
  return out;
}

// ── Classify ─────────────────────────────────────────────────────────────────
function classify(rec, moldIndex) {
  const { name, value } = rec;
  let tag = null, src = null;
  if (moldIndex[name]) { tag = tagFromMold(moldIndex[name]); if (tag) src = 'mold'; }
  if (!tag && FALLBACK[name]) { tag = FALLBACK[name]; src = 'fallback'; }
  if (!tag) return { ...rec, tag: null, src: null, category: 'unknown', def: null };

  if (REPLACED.has(tag)) return { ...rec, tag, src, category: 'skipped', def: null };
  const def = TAG_DEFAULT[tag];
  if (!def) return { ...rec, tag, src, category: 'unknown', def: null };

  if (value !== def) return { ...rec, tag, src, category: 'override', def };
  // value restates the default: inline is a safe no-op; anything else -> verify.
  return { ...rec, tag, src, category: def === 'inline' ? 'safe' : 'verify', def };
}

// ── Run ──────────────────────────────────────────────────────────────────────
const moldIndex = buildMoldIndex(ZK_SOURCE);
const moldCount = Object.keys(moldIndex).length;

const files = walkCss(COMPONENT_ROOT).sort();
const findings = [];
for (const f of files) {
  const raw = fs.readFileSync(f, 'utf8');
  for (const rec of findDisplayDecls(raw)) {
    findings.push(classify({ ...rec, file: f }, moldIndex));
  }
}

const rel = p => p.replace(PROJECT_ROOT + '/', '');
const byCat = c => findings.filter(x => x.category === c)
  .sort((a, b) => (a.file + a.line).localeCompare(b.file + b.line));

const safe = byCat('safe');
const verify = byCat('verify');
const skipped = byCat('skipped');
const unknown = byCat('unknown');
const overrideCount = findings.filter(x => x.category === 'override').length;

const L = [];
const emit = s => L.push(s);

emit('## G. Default-value redundancy (declarations restating the element browser default)');
emit('');
if (moldCount > 0) {
  emit(`> Root tags resolved from ${moldCount} ZK mold file(s) under \`${ZK_SOURCE}\`` +
       ' (fallback map used where a mold declares no tag).');
} else {
  emit(`> ⚠️ ZK source not found at \`${ZK_SOURCE}\` — resolved from the built-in fallback map only.` +
       ' Pass \`--zk-source\` for full coverage.');
}
emit('');
emit(`Scanned bare \`.z-<name>\` \`display\` declarations under \`${rel(COMPONENT_ROOT)}\`. ` +
     `Summary: **${safe.length} safe no-op**, **${verify.length} verify**, ` +
     `${skipped.length} skipped (replaced/form), ${overrideCount} real override, ${unknown.length} unknown.`);
emit('');

emit('### G1. Safe no-ops — `inline` restated on an inline-default element (removable)');
emit('');
if (safe.length) {
  for (const x of safe) emit(`- \`${rel(x.file)}:${x.line}\`  \`.z-${x.name} { display: ${x.value} }\` → <${x.tag}> is inline by default`);
} else emit('_None._');
emit('');

emit('### G2. Restates default — VERIFY before removing (`block`/`inline-block` on a same-default element)');
emit('');
emit('> May be a defensive anchor vs ZK runtime `.z-flex` class toggling or `@layer` cascade.');
emit('> Prove render-neutral on the live app before deleting (see reference/important-reduction.md and .claude/skills/marble-theme/scripts/probe.js).');
emit('');
if (verify.length) {
  for (const x of verify) emit(`- \`${rel(x.file)}:${x.line}\`  \`.z-${x.name} { display: ${x.value} }\` → <${x.tag}> default is \`${x.def}\``);
} else emit('_None._');
emit('');

emit('### G3. Skipped — replaced/form element (UA default varies; not a finding)');
emit('');
emit(skipped.length
  ? `${skipped.length} declaration(s) on replaced/form roots (img/input/button/…). Not flagged.`
  : '_None._');
emit('');

emit('### G4. Unknown root tag (manual — mold + fallback could not resolve)');
emit('');
if (unknown.length) {
  for (const x of unknown) emit(`- \`${rel(x.file)}:${x.line}\`  \`.z-${x.name} { display: ${x.value} }\`${x.tag ? ` (tag <${x.tag}>, no default rule)` : ''}`);
} else emit('_None._');
emit('');

const report = L.join('\n') + '\n';
if (OUT) fs.writeFileSync(OUT, report); else process.stdout.write(report);
process.exit(0);
