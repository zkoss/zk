#!/usr/bin/env node
/*
 * Comment-aware `!important` inventory.
 *
 * Raw grep over-counts: it sees `!important` inside `/* ... *\/` comments and in
 * historical/commented-out rules. This strips block comments first, so the count
 * reflects REAL declarations only — the authoritative number to plan against.
 *
 * Usage:  node count-important.js [cssRootDir]
 *   cssRootDir defaults to zul/src/main/resources/web (run from the repo root).
 * Output: per-file counts (desc), a total, and every real declaration line.
 */
const fs = require('fs');
const path = require('path');

const root = process.argv[2] || 'zul/src/main/resources/web';

function walk(dir, acc = []) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p, acc);
    else if (e.name.endsWith('.css')) acc.push(p);
  }
  return acc;
}

const files = walk(root).sort();
let total = 0;
const perFile = [];
const rows = [];
for (const f of files) {
  const raw = fs.readFileSync(f, 'utf8');
  // Blank out /* */ comments while preserving line numbers.
  const noComments = raw.replace(/\/\*[\s\S]*?\*\//g, m => m.replace(/[^\n]/g, ' '));
  let count = 0;
  noComments.split('\n').forEach((ln, i) => {
    const m = ln.match(/!important/g);
    if (m) { count += m.length; rows.push(`${f}:${i + 1}: ${ln.trim().slice(0, 120)}`); }
  });
  if (count > 0) { perFile.push([f.replace(root + '/', ''), count]); total += count; }
}
perFile.sort((a, b) => b[1] - a[1]);

console.log('=== PER-FILE (comment-stripped) ===');
perFile.forEach(([f, c]) => console.log(String(c).padStart(3), f));
console.log(`TOTAL !important (real declarations): ${total} across ${perFile.length} files`);
console.log('\n=== ALL DECLARATION LINES ===');
rows.forEach(r => console.log(r.replace(root + '/', '')));
