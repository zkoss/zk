#!/usr/bin/env node
// Component x feature matrix for the zkthemelab Playwright suite.
//
// Rows are the ZK components (lang.xml in zk, lang-addon.xml in ../zkcml), columns are the Playwright
// projects (one project = one feature). A cell counts the tests of that project that exercise the
// component. A test exercises a component when
//   1. a segment of its title (a describe or the test title) starts with the component name, or the
//      title names the component's root class (`.z-<name>`) or, for an unambiguous name, the name anywhere;
//   2. or it opens a preview page of the component (`<name>.zul`, `<name>-<topic>.zul`,
//      `pv/<name>-<topic>-guard.zul`), as select-tests.mjs finds the pages a test opens;
//   3. or, in a page-scan project (a whole-page check such as a gallery screenshot), the page it opens
//      renders the component's root class `z-<name>` at rest (doc/class-index.json).
// Only the first, precise tier of select-tests' page evidence counts here; a page named somewhere else in
// the spec file proves nothing about this test.
//
// Breadth features (doc/component-feature-config.json) are expected to cover every component; one breadth
// feature may span several projects (the desktop gallery is the `gallery` scan plus screenshot.spec.ts's
// bespoke blocks). An empty breadth cell is a gap unless the config gives the reason it does not apply.
// The other projects target a few components each and are listed per component without gaps.
//
// Report only: it never fails on a gap. Run from zkthemelab/:  npm run matrix
// Output: doc/component-feature-matrix.md (generated; do not edit by hand).

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { listAllTests, loadClassIndex, loadAggregatePages, pagesOfTestFactory } from './select-tests.mjs';

const ZKTHEMELAB_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const ZK_ROOT = path.resolve(ZKTHEMELAB_DIR, '..');
const ZKCML_ROOT = path.resolve(ZK_ROOT, '../zkcml');
const CONFIG_FILE = path.join(ZKTHEMELAB_DIR, 'doc/component-feature-config.json');
const OUTPUT_FILE = path.join(ZKTHEMELAB_DIR, 'doc/component-feature-matrix.md');

const LANG_FILES = [
  ['zul', path.join(ZK_ROOT, 'zul/src/main/resources/metainfo/zk/lang.xml')],
  ...['zkex', 'zkmax', 'zuti'].map(m => [m, path.join(ZKCML_ROOT, m, 'src/main/resources/metainfo/zk/lang-addon.xml')]),
];

function loadComponents() {
  const modules = new Map(); // name -> first module that declares it
  for (const [module, file] of LANG_FILES) {
    if (!fs.existsSync(file)) {
      if (module === 'zul') throw new Error(`missing ${file}`);
      console.warn(`component-feature-matrix: ${file} not found, ${module} components left out`);
      continue;
    }
    for (const m of fs.readFileSync(file, 'utf8').matchAll(/<component-name>\s*([\w-]+)\s*<\/component-name>/g)) {
      if (!modules.has(m[1])) modules.set(m[1], module);
    }
  }
  return modules;
}

const escapeRe = s => s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

function componentsOfTitle(test, components, ambiguous) {
  const found = new Set();
  const segments = [...test.describes.filter(Boolean), test.title];
  for (const seg of segments) {
    const s = seg.toLowerCase();
    for (const c of components) {
      // Leading name: "button — focus", "slider knob — ...", "multislider", "label follows ...".
      // An ambiguous name (e.g. `a`, an English article) counts only on its own or before a separator.
      const lead = ambiguous.has(c)
        ? new RegExp(`^${escapeRe(c)}(?:$|\\s*(?:—|:|›))`)
        : new RegExp(`^${escapeRe(c)}(?![\\w-])`);
      if (lead.test(s)) found.add(c);
      if (new RegExp(`\\.z-${escapeRe(c)}(?![\\w-])`).test(s)) found.add(c);
      if (!ambiguous.has(c) && new RegExp(`(?<![\\w.-])${escapeRe(c)}(?![\\w-])`).test(s)) found.add(c);
    }
  }
  return found;
}

/** `grid-livegrouping` -> grid, `pv/bandbox-popupwidth-guard` -> bandbox, `utility/icons` -> none. */
function componentOfPage(pageKey, components, aggregatePages) {
  if (aggregatePages.has(pageKey)) return null;
  const parts = pageKey.split('/');
  if (parts.length > 1 && parts[0] !== 'pv') return null;
  const words = parts.pop().replace(/-guard$/, '').split('-');
  for (let n = words.length; n > 0; n--) {
    const name = words.slice(0, n).join('-');
    if (components.has(name)) return name;
  }
  return null;
}

function main() {
  const config = JSON.parse(fs.readFileSync(CONFIG_FILE, 'utf8'));
  const modules = loadComponents();
  // Widgets ZK themes that have no component tag (messagebox, notification, the loading indicator, ...).
  for (const w of config.clientWidgets ?? []) if (!modules.has(w)) modules.set(w, 'client');
  const components = new Set(modules.keys());
  const ambiguous = new Set(config.ambiguousNames);
  const aliases = config.titleAliases ?? {};
  const aggregatePages = loadAggregatePages();
  const { pageKeys, pagesByClass } = loadClassIndex(aggregatePages);
  const breadthDefs = config.breadthFeatures; // [{ name, projects, pageScan }]
  const pageScanProjects = new Set(breadthDefs.filter(f => f.pageScan).flatMap(f => f.projects));
  const rootsOnPage = new Map(); // page -> components whose root class it renders at rest
  for (const c of components) {
    for (const p of pagesByClass.get(`z-${c}`) ?? []) {
      if (!rootsOnPage.has(p)) rootsOnPage.set(p, new Set());
      rootsOnPage.get(p).add(c);
    }
  }
  const tests = listAllTests();
  const pagesOfTest = pagesOfTestFactory(tests, pageKeys);

  const cells = new Map();            // component -> project -> count
  const unmatched = new Map();        // project -> titles
  const add = (c, p) => {
    if (!cells.has(c)) cells.set(c, new Map());
    cells.get(c).set(p, (cells.get(c).get(p) ?? 0) + 1);
  };
  for (const t of tests) {
    const found = componentsOfTitle(t, components, ambiguous);
    const full = [...t.describes, t.title].join(' › ');
    for (const [needle, comp] of Object.entries(aliases)) if (full.includes(needle)) found.add(comp);
    (config.projectComponents?.[t.project] ?? []).forEach(c => found.add(c));
    const { pages, tier } = pagesOfTest(t);
    if (tier === 1) {
      for (const p of pages) {
        const c = componentOfPage(p, components, aggregatePages);
        if (c) found.add(c);
        if (pageScanProjects.has(t.project)) (rootsOnPage.get(p) ?? []).forEach(r => found.add(r));
      }
    }
    if (!found.size) {
      if (!unmatched.has(t.project)) unmatched.set(t.project, []);
      unmatched.get(t.project).push(full);
    }
    for (const c of found) add(c, t.project);
  }

  const projects = [...new Set(tests.map(t => t.project))];
  const unknown = breadthDefs.flatMap(f => f.projects).filter(p => !projects.includes(p));
  if (unknown.length) throw new Error(`component-feature-config.json names unknown projects: ${unknown.join(', ')}`);
  const breadth = breadthDefs.map(f => f.name);
  const projectsOf = new Map(breadthDefs.map(f => [f.name, f.projects]));
  const breadthProjects = new Set(breadthDefs.flatMap(f => f.projects));
  const targeted = projects.filter(p => !breadthProjects.has(p)).sort();
  const countIn = (row, f) => projectsOf.get(f).reduce((n, p) => n + (row.get(p) ?? 0), 0);
  const notApplicable = new Map(); // `${component}|${feature}` -> reason
  for (const e of config.notApplicable ?? []) {
    for (const c of e.components) for (const f of e.features) notApplicable.set(`${c}|${f}`, e.reason);
  }

  const tested = [...components].filter(c => cells.has(c)).sort();
  const untested = [...components].filter(c => !cells.has(c)).sort();
  const gaps = [];
  const out = [];
  out.push('# Component × feature matrix');
  out.push('');
  out.push('Generated by `zkthemelab/scripts/component-feature-matrix.mjs` (`npm run matrix`); do not edit by hand.');
  out.push('Breadth features, ambiguous names, title aliases and not-applicable cells: `doc/component-feature-config.json`.');
  out.push('');
  out.push(`Tests: ${tests.length}. Components: ${components.size} (${tested.length} with at least one test).`);
  out.push('');
  out.push('Cell: number of tests; `n/a` = not applicable (reason in the config); `—` = gap.');
  out.push('');
  out.push(breadthDefs.map(f => `- **${f.name}**: ${f.projects.join(' + ')}${f.pageScan ? ' (page scan: every component the opened page renders)' : ''}`).join('\n'));
  out.push('');
  out.push(`| component | module | ${breadth.join(' | ')} | other projects |`);
  out.push(`|---|---|${breadth.map(() => '---:').join('|')}|---|`);
  for (const c of tested) {
    const row = cells.get(c);
    const breadthCells = breadth.map(f => {
      if (countIn(row, f)) return String(countIn(row, f));
      if (notApplicable.has(`${c}|${f}`)) return 'n/a';
      gaps.push([c, f]);
      return '—';
    });
    const other = targeted.filter(p => row.has(p)).map(p => `${p} ${row.get(p)}`).join(', ');
    out.push(`| ${c} | ${modules.get(c)} | ${breadthCells.join(' | ')} | ${other} |`);
  }
  out.push('');
  out.push(`## Gaps (${gaps.length})`);
  out.push('');
  for (const f of breadth) {
    const list = gaps.filter(g => g[1] === f).map(g => g[0]);
    if (list.length) out.push(`- **${f}** (${list.length}): ${list.join(', ')}`);
  }
  out.push('');
  out.push(`## Components with no test (${untested.length})`);
  out.push('');
  out.push(untested.map(c => `${c} (${modules.get(c)})`).join(', '));
  out.push('');
  const unmatchedCount = [...unmatched.values()].reduce((n, l) => n + l.length, 0);
  out.push(`## Tests matched to no component (${unmatchedCount})`);
  out.push('');
  out.push('Page-level or cross-component tests, or a title that needs an alias in the config.');
  out.push('');
  for (const p of [...unmatched.keys()].sort()) {
    out.push(`- **${p}** (${unmatched.get(p).length})`);
    for (const title of unmatched.get(p)) out.push(`  - ${title}`);
  }
  out.push('');
  fs.writeFileSync(OUTPUT_FILE, out.join('\n'));
  console.log(`component-feature-matrix: ${tested.length}/${components.size} components tested, ${gaps.length} gaps, ${unmatchedCount} unmatched tests -> ${path.relative(ZKTHEMELAB_DIR, OUTPUT_FILE)}`);
}

main();
