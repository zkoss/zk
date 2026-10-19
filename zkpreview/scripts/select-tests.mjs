#!/usr/bin/env node
// Decides which zkpreview Playwright tests a set of changed files needs: a full run, a partial
// run (with ready-to-run commands) or nothing. Rules: doc/marble-selective-test-rules.md §3-§4.
// Usage and accepted path forms: scripts/README-select-tests.md.
import { execFileSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import postcss from 'postcss';

const ZKPREVIEW_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const ZK_ROOT = path.resolve(ZKPREVIEW_DIR, '..');
const ZKCML_ROOT = path.resolve(ZK_ROOT, '../zkcml');
const REPO_ROOTS = { zk: ZK_ROOT, zkcml: ZKCML_ROOT };
const WEBAPP_DIR = path.join(ZKPREVIEW_DIR, 'src/main/webapp');
const SPEC_DIR = path.join(ZKPREVIEW_DIR, 'src/test/playwright');
const CLASS_INDEX_FILE = path.join(ZKPREVIEW_DIR, 'doc/class-index.json');
const AGGREGATE_PAGES_FILE = path.join(path.dirname(fileURLToPath(import.meta.url)), 'aggregate-pages.json');
const PLAYWRIGHT_CONFIG = 'src/test/playwright/playwright.config.ts';
const RUN_PREFIX = `npx playwright test -c ${PLAYWRIGHT_CONFIG}`;

// Rules §3 (D10): selecting more than this share of all tests means a partial run no longer saves enough time.
const FULL_RUN_TEST_FRACTION = 2 / 3;

// Spec files whose tests must not share a worker's test order or state: a partial run that includes one of
// them gets no `--fully-parallel`. Audit result 2026-10-18 (README-select-tests.md, doc/marble-test-phase1-selector.md 11.2):
// none. font-size-equivalence.spec.ts and focus-ring-scan.spec.ts are serial / read-modify-write only while
// UPDATE_FONT_BASELINE=1 / FOCUS_SCAN_UPDATE=1 is set, and the commands printed here never set them.
const UNSAFE_PARALLEL_SPECS = new Map(); // spec file -> why it cannot run fully parallel

// ---------------------------------------------------------------------------------------------
// Path classification (rules §3). Each rule maps a repo-relative path to a handling kind.
// First match wins; a path that matches nothing is "unknown" and forces a full run.
// ---------------------------------------------------------------------------------------------

const COMPONENT_CSS = /^(?:zul|zkmax|zkex)\/src\/main\/resources\/web\/js\/(?:zul|zkmax|zkex)\/[^/]+\/css\/([^/]+)\.css$/;
const TABLET_CSS = /^zkmax\/src\/main\/resources\/web\/zkmax\/css\/tablet\/([^/]+)\.css$/;
const WIDGET_SOURCE = /^(?:zul|zkmax|zkex)\/src\/main\/resources\/web\/js\/(?:zul|zkmax|zkex)\/[^/]+\/([A-Za-z0-9_]+)\.(?:ts|js)$/;
const SHARED_CSS_FILES = new Set(['misc']); // rules §4: shared by many components, treated like base
const PREVIEW_ZUL = /^zkpreview\/src\/main\/webapp\/(.+)\.zul$/;
const SPEC_FILE = /^zkpreview\/src\/test\/playwright\/([^/]+\.spec\.ts)$/;
const BASELINE_PNG = /^zkpreview\/doc\/screenshots\/(.+)\.png$/;
const REVIEW_ARTIFACT_PNG = /-forced-colors\.png$/;
const SPEC_DATA_FILE = /^zkpreview\/doc\/([^/]+\.json)$/;

const FULL_RUN_TRIGGERS = [
  [/^zul\/src\/main\/resources\/web\/zul\/css\/(?:tokens|base|utility)\//, 'token/base/utility CSS affects every page'],
  [/(^|\/)_layer-order\.css$/, 'cascade layer order affects every page'],
  [/\.wcs$/, 'a .wcs bundle changes which CSS loads and in what order'],
  [/(^|\/)lang(?:-addon)?\.xml$/, 'lang.xml / lang-addon.xml changes which CSS loads and in what order'],
  [/^(?:gulpfile\.js|webpack\.config\.js|scripts\/(?:build-css|check-css-dsp|utility-manifest)\.js)$/, 'CSS build script: output may change as a whole'],
  [/(^|\/)(?:build|settings)\.gradle(?:\.kts)?$|(^|\/)gradle\.properties$/, 'gradle build: CSS output may change as a whole'],
  [/\.(?:woff2?|ttf|otf|eot)$/, 'font file affects text measurement on every page'],
  [/^zkpreview\/src\/test\/playwright\/playwright\.config\.ts$/, 'Playwright config changes the baseline rendering environment'],
  [/(^|\/)package(?:-lock)?\.json$/, 'package.json / lockfile may change the Playwright or Chromium version'],
];

const NO_TEST_PATHS = [
  [/^doc\//, 'documentation'],
  [/\.md$/, 'markdown document'],
  [/^\.claude\//, 'Claude configuration'],
  [/^(?:tasks|logs)\//, 'working notes / logs'],
  [/(^|\/)node_modules(\/|$)/, 'installed dependencies'],
  [/^zkpreview\/scripts\//, 'zkpreview tooling scripts (do not affect rendered output)'],
  [/^zkpreview\/doc\/class-index\.json$/, 'class index (selector data)'],
  [REVIEW_ARTIFACT_PNG, 'forced-colors review artifact (written by a spec, not compared)'],
];

// ---------------------------------------------------------------------------------------------
// Argument parsing and changed-file discovery
// ---------------------------------------------------------------------------------------------

function parseArgs(argv) {
  const opts = { json: false, base: null, zkcmlBase: null, only: false, threshold: FULL_RUN_TEST_FRACTION, paths: [] };
  for (let i = 0; i < argv.length; i++) {
    const arg = argv[i];
    if (arg === '--json') opts.json = true;
    else if (arg === '--base') opts.base = argv[++i];
    else if (arg === '--zkcml-base') opts.zkcmlBase = argv[++i];
    else if (arg === '--only') opts.only = true;
    else if (arg === '--threshold') opts.threshold = Number(argv[++i]);
    else if (arg.startsWith('--')) fail(`unknown option ${arg}`);
    else opts.paths.push(arg);
  }
  if (!(opts.threshold > 0 && opts.threshold <= 1)) fail('--threshold is a fraction of all tests, greater than 0 and at most 1 (e.g. 0.5)');
  if (!opts.base && !opts.zkcmlBase && opts.paths.length === 0) fail('give changed paths, or --base <rev>');
  if (opts.only && !opts.base && !opts.zkcmlBase) fail('--only needs --base or --zkcml-base');
  return opts;
}

function fail(message) {
  console.error(`select-tests: ${message}`);
  process.exit(2);
}

function git(repo, args) {
  return execFileSync('git', ['-C', REPO_ROOTS[repo], ...args], { encoding: 'utf8', maxBuffer: 64 * 1024 * 1024 });
}

/** Paths changed since `rev`, including uncommitted and untracked files. */
function changedSince(repo, rev) {
  const tracked = git(repo, ['diff', '--name-only', '--no-renames', rev]);
  const untracked = git(repo, ['ls-files', '--others', '--exclude-standard']);
  return [...new Set((tracked + untracked).split('\n').filter(Boolean))];
}

/** Accepts zk-relative, `zkcml:`-prefixed, or absolute paths; returns { repo, rel }. */
function resolveInputPath(input) {
  if (input.startsWith('zkcml:')) return { repo: 'zkcml', rel: input.slice('zkcml:'.length) };
  if (input.startsWith('zk:')) return { repo: 'zk', rel: input.slice('zk:'.length) };
  if (path.isAbsolute(input)) {
    for (const repo of ['zkcml', 'zk']) {
      const rel = path.relative(REPO_ROOTS[repo], input);
      if (!rel.startsWith('..')) return { repo, rel };
    }
    fail(`absolute path outside the zk and zkcml worktrees: ${input}`);
  }
  return { repo: 'zk', rel: input };
}

function collectChanges(opts) {
  const given = opts.paths.map(resolveInputPath);
  // --only: the given paths filter the diff against the base instead of being added as bare paths.
  const changes = opts.only ? [] : given.map(c => ({ ...c, base: null }));
  if (opts.base) changes.push(...changedSince('zk', opts.base).map(rel => ({ repo: 'zk', rel, base: opts.base })));
  if (opts.zkcmlBase) changes.push(...changedSince('zkcml', opts.zkcmlBase).map(rel => ({ repo: 'zkcml', rel, base: opts.zkcmlBase })));
  const keep = new Set(given.map(c => `${c.repo}:${c.rel}`));
  const seen = new Set();
  return changes.filter(c => (!opts.only || keep.has(`${c.repo}:${c.rel}`))
    && !seen.has(`${c.repo}:${c.rel}`) && seen.add(`${c.repo}:${c.rel}`));
}

// ---------------------------------------------------------------------------------------------
// Class index (index B) and CSS parsing (index A)
// ---------------------------------------------------------------------------------------------

/**
 * Page keys are URL paths without `.zul` (`button`, `pv/input-invalid-guard`). Aggregate pages are left out of
 * the class -> pages map, so a class found only on an aggregate page falls through to the name-prefix and
 * own-component steps instead of selecting the aggregate page's tests.
 */
function loadClassIndex(aggregatePages = new Set()) {
  if (!fs.existsSync(CLASS_INDEX_FILE)) fail(`missing ${CLASS_INDEX_FILE}; run scripts/build-class-index.mjs`);
  const { pages } = JSON.parse(fs.readFileSync(CLASS_INDEX_FILE, 'utf8'));
  const pagesByClass = new Map();
  for (const [file, classes] of Object.entries(pages)) {
    const key = file.replace(/\.zul$/, '');
    if (aggregatePages.has(key)) continue;
    for (const c of classes) {
      if (!pagesByClass.has(c)) pagesByClass.set(c, new Set());
      pagesByClass.get(c).add(key);
    }
  }
  return { pageKeys: new Set(Object.keys(pages).map(f => f.replace(/\.zul$/, ''))), pagesByClass };
}

/** Pages that combine many components (aggregate-pages.json, D12): only their own .zul selects their tests. */
function loadAggregatePages() {
  return new Set(Object.keys(JSON.parse(fs.readFileSync(AGGREGATE_PAGES_FILE, 'utf8'))));
}

/**
 * Every `.z-*` class in a piece of CSS (a whole stylesheet or just some selectors), plus attribute-substring
 * selectors such as `[class*="z-icon-"]`.
 */
function parseCssClasses(cssText) {
  const text = cssText.replace(/\/\*[\s\S]*?\*\//g, '');
  const classes = new Set();
  const attributePatterns = [];
  for (const m of text.matchAll(/\[\s*class\s*([*^]?)=\s*["']?\s*(z-[^"'\]\s]{3,})/g)) {
    attributePatterns.push({ operator: m[1], value: m[2] });
  }
  const withoutStringsAndUrls = text.replace(/url\([^)]*\)/g, '').replace(/"[^"]*"|'[^']*'/g, '');
  for (const m of withoutStringsAndUrls.matchAll(/\.(z-(?:[A-Za-z0-9_-]|\\.)+)/g)) {
    classes.add(m[1].replace(/\\(.)/g, '$1'));
  }
  return { classes, attributePatterns };
}

// Changed-rule scanning (rules §3, D10). A changed line is attributed to the rule that contains it;
// an at-rule header or a body that is not a list of rules cannot be attributed, so the caller falls back.
class CannotAttribute extends Error {}

const RULE_BLOCK_AT_RULES = /^(?:media|layer|supports|container|scope)$/i;

/** Selectors of the rules (and the rules they are nested in) that contain a changed line. */
function selectorsOfChangedLines(cssText, changedLines) {
  let root;
  try { root = postcss.parse(cssText); } catch (e) { throw new CannotAttribute(`CSS does not parse (${e.reason ?? e.message})`); }
  const textLines = cssText.split('\n');
  const selectors = new Set();
  const covers = (node, line) => line >= node.source.start.line && line <= node.source.end.line;

  const attribute = (container, line, enclosingSelectors) => {
    const child = (container.nodes ?? []).find(n => covers(n, line));
    if (container.type === 'root') {
      if (!child) return; // blank line between top-level rules
    } else if (container.type === 'atrule') {
      if (!child) throw new CannotAttribute(`line ${line} is inside @${container.name}, outside any rule (header or closing line)`);
      if (!RULE_BLOCK_AT_RULES.test(container.name) && child.type !== 'comment') {
        throw new CannotAttribute(`line ${line} is inside @${container.name}, which is not a block of rules`);
      }
    } else if (!child || child.type === 'decl' || child.type === 'comment') {
      enclosingSelectors.forEach(s => selectors.add(s)); // declaration, comment or selector/closing line of this rule
      return;
    }
    if (child.type === 'rule') attribute(child, line, [...enclosingSelectors, child.selector]);
    else if (child.type === 'atrule') attribute(child, line, enclosingSelectors);
    else if (child.type === 'decl') { // a declaration directly inside a nested @media/@layer block
      if (!enclosingSelectors.length) throw new CannotAttribute(`line ${line} is a declaration outside any rule`);
      enclosingSelectors.forEach(s => selectors.add(s));
    }
  };

  for (const line of changedLines) {
    if (line < 1 || line > textLines.length || textLines[line - 1].trim() === '') continue;
    attribute(root, line, []);
  }
  return selectors;
}

/** Changed line numbers of the old and the new version of a file, from `git diff -U0 <base>`. */
function changedLinesSince(repo, base, rel) {
  const diff = git(repo, ['diff', '-U0', '--no-renames', base, '--', rel]);
  const oldLines = new Set();
  const newLines = new Set();
  for (const m of diff.matchAll(/^@@ -(\d+)(?:,(\d+))? \+(\d+)(?:,(\d+))? @@/gm)) {
    const [oldStart, oldCount, newStart, newCount] = [m[1], m[2], m[3], m[4]].map(v => (v === undefined ? 1 : Number(v)));
    for (let i = 0; i < oldCount; i++) oldLines.add(oldStart + i);
    for (let i = 0; i < newCount; i++) newLines.add(newStart + i);
  }
  return { oldLines, newLines };
}

function mergeParsed(texts) {
  const merged = { classes: new Set(), attributePatterns: [] };
  for (const text of texts) {
    const parsed = parseCssClasses(text);
    parsed.classes.forEach(c => merged.classes.add(c));
    merged.attributePatterns.push(...parsed.attributePatterns);
  }
  return merged;
}

/** Classes of the rules a diff touches, in the old and the new version, so removed and edited rules both count. */
function parseChangedRules(change, oldText, newText) {
  const { oldLines, newLines } = changedLinesSince(change.repo, change.base, change.rel);
  const selectors = new Set([...selectorsOfChangedLines(oldText, oldLines), ...selectorsOfChangedLines(newText, newLines)]);
  return { ...mergeParsed([...selectors]), selectorCount: selectors.size, parseMode: `changed rules (${selectors.size} selector(s))` };
}

/** Classes to resolve for a changed CSS file, and how they were found (`parseMode`); null when unreadable. */
function readCssClasses(change) {
  const file = path.join(REPO_ROOTS[change.repo], change.rel);
  const newText = fs.existsSync(file) ? fs.readFileSync(file, 'utf8') : null;
  let oldText = null;
  if (change.base) { // a rule deleted by the change is gone from the working copy but still affects pages
    try { oldText = git(change.repo, ['show', `${change.base}:${change.rel}`]); } catch { /* new file */ }
  }
  const texts = [newText, oldText].filter(t => t !== null);
  if (texts.length === 0) return null;

  const wholeFile = why => ({ ...mergeParsed(texts), parseMode: `whole file (${why})` });
  if (!change.base) return wholeFile('no diff available, bare path');
  if (newText === null || oldText === null) return wholeFile(newText === null ? 'file deleted' : 'new file');
  try {
    return parseChangedRules(change, oldText, newText);
  } catch (e) {
    if (e instanceof CannotAttribute) return wholeFile(e.message);
    throw e;
  }
}

// ---------------------------------------------------------------------------------------------
// Class -> pages (rules §4 resolution order)
// ---------------------------------------------------------------------------------------------

function classResolver({ pageKeys, pagesByClass }) {
  const pagesWithClass = c => pagesByClass.get(c) ?? new Set();

  /** `z-datebox-popup` -> pages of the longest present prefix (`z-datebox`). */
  function pagesByNamePrefix(c) {
    const segments = c.split('-');
    for (let n = segments.length - 1; n >= 2; n--) {
      const prefix = segments.slice(0, n).join('-');
      if (pagesByClass.has(prefix)) return { pages: pagesByClass.get(prefix), via: prefix };
    }
    return null;
  }

  /** The component a CSS file is named after: `combobox.css` -> pages with `z-combobox`, or combobox.zul. */
  function pagesOfFileComponent(component) {
    const pages = new Set(pagesWithClass(`z-${component}`));
    if (pageKeys.has(component)) pages.add(component);
    return pages;
  }

  /**
   * Pages of the class's owner component, wherever the class itself is not drawn at rest (a pop-up class of a
   * combobox is only on screen while its pop-up is open): the longest present name prefix, else the root class of
   * the component the CSS file is named after.
   */
  function ownerPages(c, component) {
    const prefixed = pagesByNamePrefix(c);
    if (prefixed) return prefixed.pages;
    return component ? pagesWithClass(`z-${component}`) : new Set();
  }

  /** Returns { pages, trace } where trace says how each class was resolved. */
  function resolve(classes, attributePatterns, component) {
    const pages = new Set();
    const unresolved = [];
    const byPrefix = [];
    const byComponent = [];
    for (const c of classes) {
      ownerPages(c, component).forEach(p => pages.add(p));
      const atRest = pagesWithClass(c);
      if (atRest.size) { atRest.forEach(p => pages.add(p)); continue; }
      const prefixed = pagesByNamePrefix(c);
      if (prefixed) { prefixed.pages.forEach(p => pages.add(p)); byPrefix.push(`${c} -> ${prefixed.via}`); continue; }
      const owner = component ? pagesOfFileComponent(component) : new Set();
      if (owner.size) { owner.forEach(p => pages.add(p)); byComponent.push(c); continue; }
      unresolved.push(c);
    }
    for (const { operator, value } of attributePatterns) {
      for (const [c, cPages] of pagesByClass) {
        const hit = operator === '^' ? c.startsWith(value) : c.includes(value);
        if (hit) cPages.forEach(p => pages.add(p));
      }
    }
    return { pages, unresolved, byPrefix, byComponent };
  }

  return { resolve, pagesOfFileComponent };
}

// ---------------------------------------------------------------------------------------------
// Test inventory: Playwright's own --list is the source of truth for projects, files and titles
// ---------------------------------------------------------------------------------------------

function listAllTests() {
  const json = execFileSync('npx', ['playwright', 'test', '-c', PLAYWRIGHT_CONFIG, '--list', '--reporter=json'],
    { cwd: ZKPREVIEW_DIR, encoding: 'utf8', maxBuffer: 256 * 1024 * 1024, stdio: ['ignore', 'pipe', 'pipe'] });
  const tests = [];
  // `ancestors` are the enclosing describes, outermost first; their `firstChildLine` is filled in during the walk.
  const visit = (suite, ancestors) => {
    for (const spec of suite.specs ?? []) {
      noteChildLine(ancestors, spec.line);
      for (const t of spec.tests) {
        const describes = ancestors.map(a => a.title);
        tests.push({
          project: t.projectName, file: spec.file, line: spec.line, describes, ancestors, title: spec.title,
          // The exact string Playwright's -g matches against: project, file, describes, title, tags.
          grepTitle: [t.projectName, spec.file, ...describes.filter(Boolean), spec.title, ...(spec.tags ?? [])].join(' '),
        });
      }
    }
    for (const child of suite.suites ?? []) {
      noteChildLine(ancestors, child.line);
      visit(child, [...ancestors, { title: child.title, line: child.line, firstChildLine: Infinity }]);
    }
  };
  JSON.parse(json).suites.forEach(fileSuite => visit(fileSuite, [])); // a file suite is the root, not a describe
  tests.forEach((t, id) => { t.id = id; });
  return tests;
}

function noteChildLine(ancestors, line) {
  ancestors.forEach(a => { a.firstChildLine = Math.min(a.firstChildLine, line); });
}

// ---------------------------------------------------------------------------------------------
// Page -> tests. KEEP ALL OF THE "WHICH TEST OPENS WHICH PAGE" KNOWLEDGE IN THIS SECTION.
// Sectioned gallery baselines (`<page>-<NN>-<slug>.png`, session zk-e3) keep the test titles, so only
// `galleryPageOfBaseline` and `isGalleryTest` know about them; if the titles change, adjust those and `titleNamesPage`.
// ---------------------------------------------------------------------------------------------

const ZUL_REFERENCE = /((?:[A-Za-z0-9_-]+\/)*[A-Za-z0-9_-]+)\.zul(?![A-Za-z0-9_])(?!\.[A-Za-z])/g; // not `org.zkoss.zul.theme`
const DATA_ROW = /name:\s*['"]([^'"]+)['"].*?url:\s*['"]\/?((?:[A-Za-z0-9_-]+\/)*[A-Za-z0-9_-]+)\.zul/;

function sourceRefs(text) {
  return new Set([...text.matchAll(ZUL_REFERENCE)].map(m => m[1]));
}

// What may follow the page name in a gallery baseline: `gallery`, a sectioned `<NN>-<slug>`,
// or a slug-less `<name>-<NN>` such as `window-overlapped-00`.
const GALLERY_BASELINE_TAIL = /^(?:gallery|\d{2}-.+|.+-\d{2})$/;

/**
 * The page a gallery baseline belongs to: `<page>-gallery.png`, the sectioned `<page>-<NN>-<slug>.png`
 * (e.g. `tree-03-tree-live-data`) or `<page>-<name>-<NN>.png` (`window-overlapped-00`).
 * The longest page name that fits wins (`tree-header-02-x` is tree-header, not tree). Null for any other
 * baseline name (`button-hover`, `bandbox-tablet`, ...).
 */
function galleryPageOfBaseline(name, pageKeys) {
  const pagesByLength = [...pageKeys].sort((a, b) => b.length - a.length);
  return pagesByLength.find(page => name.startsWith(`${page}-`) && GALLERY_BASELINE_TAIL.test(name.slice(page.length + 1))) ?? null;
}

const VARIANT_BASELINE_TAIL = /^(compact|brand)-\d{2}-.+$/;

/**
 * The page and variant of a variant gallery baseline `<page>-compact-<NN>-<slug>.png` / `<page>-brand-<NN>-<slug>.png`
 * (compact-density.spec.ts, brand-override.spec.ts: describe `<variant>-gallery`, test `<page>`). The longest page name wins.
 */
function variantGalleryOfBaseline(name, pageKeys) {
  const pagesByLength = [...pageKeys].sort((a, b) => b.length - a.length);
  for (const page of pagesByLength) {
    if (!name.startsWith(`${page}-`)) continue;
    const m = VARIANT_BASELINE_TAIL.exec(name.slice(page.length + 1));
    if (m) return { page, variant: m[1] };
  }
  return null;
}

/** The tests that compare gallery baselines: gallery-scan.spec.ts (project `gallery`) and `test('gallery')` in screenshot.spec.ts. */
function isGalleryTest(test) {
  return test.project === 'gallery' || (test.project === 'chromium' && test.title === 'gallery'); // tablet galleries write `<page>-tablet.png`
}

/** Scan-driven specs name the test after the page; others put `x.zul` in the title or `x:` in front of it. */
function titleNamesPage(test, pageKey) {
  const name = pageKey.split('/').pop();
  const mentioned = [...test.title.matchAll(ZUL_REFERENCE)].map(m => m[1]);        // 'a.zul', 'renders /x.zul'
  return mentioned.includes(pageKey)
    || test.title === pageKey || test.title === name                                // gallery / focus-scan titles
    || test.describes[0] === name                                                  // screenshot.spec.ts `describe('button')`
    || test.title.startsWith(`${name}: `);                                          // hit-target.spec.ts
}

/**
 * COMPUTED-URL SPECS (component-theme-variables.spec.ts). Its describes open the page in `beforeEach` from the test title:
 * `page.goto(`/${PAGES[comp] ?? comp}.zul`)`, where `comp` is the text before ' — ' and `PAGES`, `PAGES3` are
 * `Record<string, string>` maps for the components whose page has another name. No `.zul` literal exists to scan,
 * so the page is rebuilt here: parse the maps, apply them to the title. Nothing else in the selector knows this spec.
 */
const PAGE_MAP_DECLARATION = /const PAGES\d*: Record<string, string> = (\{[^}]*\});/g;
const COMPUTED_URL_USE = /\bPAGES\d*\[/;
const TITLE_SEPARATOR = ' — ';

function computedUrlPage(test, specLines, pageKeys) {
  const setUp = test.ancestors.map(a => specLines.slice(a.line - 1, a.firstChildLine - 1).join('\n')).join('\n');
  if (!COMPUTED_URL_USE.test(setUp) || !test.title.includes(TITLE_SEPARATOR)) return [];
  const pageNames = Object.assign({}, ...[...specLines.join('\n').matchAll(PAGE_MAP_DECLARATION)].map(m => JSON.parse(m[1])));
  const component = test.title.split(TITLE_SEPARATOR)[0];
  const page = pageNames[component] ?? component;
  return pageKeys.has(page) ? [page] : [];
}

/**
 * Pages a test opens, from the most to the least specific evidence, stopping at the first that finds any:
 *   1. its title, a data-table row (`{ name: 'tablet-x', url: '/x.zul' }`) that generates it, or the page
 *      mentioned in its own source lines plus its enclosing describes' set-up lines (beforeEach);
 *   2. the whole top-level describe it sits in (a helper may navigate for it);
 *   3. every page its spec file mentions anywhere.
 * Each step is a superset of the one before, so a test is never attributed to fewer pages than it opens.
 */
function pagesOfTestFactory(tests, pageKeys) {
  const sources = new Map();
  const linesOf = file => {
    if (!sources.has(file)) sources.set(file, fs.readFileSync(path.join(SPEC_DIR, file), 'utf8').split('\n'));
    return sources.get(file);
  };
  // A comment that names a page ("toast.zul and notification.zul buttons ...") is no navigation; the one after a
  // describe would otherwise count for the last test above it.
  const withoutComments = text => text.replace(/\/\*[\s\S]*?\*\//g, '').replace(/^\s*\/\/.*$/gm, '');
  const refsInRange = (file, fromLine, toLineExclusive) =>
    [...sourceRefs(withoutComments(linesOf(file).slice(fromLine - 1, Math.min(toLineExclusive - 1, linesOf(file).length)).join('\n')))]
      .filter(key => pageKeys.has(key));
  const boundaries = new Map(); // file -> sorted start lines of every test and describe
  for (const t of tests) {
    if (!boundaries.has(t.file)) boundaries.set(t.file, new Set());
    boundaries.get(t.file).add(t.line);
    t.ancestors.forEach(a => boundaries.get(t.file).add(a.line));
  }
  const topLevelStarts = new Map(); // file -> start lines of top-level describes and bare tests
  for (const t of tests) {
    if (!topLevelStarts.has(t.file)) topLevelStarts.set(t.file, new Set());
    topLevelStarts.get(t.file).add((t.ancestors[0] ?? t).line);
  }
  const nextAfter = (lines, line) => [...lines].filter(l => l > line).sort((a, b) => a - b)[0] ?? Infinity;
  const nextBoundary = (file, line) => nextAfter(boundaries.get(file), line);
  const rows = new Map();
  const rowsOf = file => {
    if (!rows.has(file)) {
      const map = new Map();
      linesOf(file).forEach(l => { const m = l.match(DATA_ROW); if (m) map.set(m[1], m[2]); });
      rows.set(file, map);
    }
    return rows.get(file);
  };

  return function pagesOfTest(test) {
    const pages = new Set();
    for (const key of pageKeys) if (titleNamesPage(test, key)) pages.add(key);
    computedUrlPage(test, linesOf(test.file), pageKeys).forEach(p => pages.add(p));
    const row = rowsOf(test.file).get(test.describes[0]) ?? rowsOf(test.file).get(test.title);
    if (row) pages.add(row);
    refsInRange(test.file, test.line, nextBoundary(test.file, test.line)).forEach(p => pages.add(p));
    test.ancestors.forEach(a => refsInRange(test.file, a.line, a.firstChildLine).forEach(p => pages.add(p)));
    if (pages.size) return { pages, tier: 1 };

    const outer = test.ancestors[0];
    if (outer) {
      refsInRange(test.file, outer.line, nextAfter(topLevelStarts.get(test.file), outer.line)).forEach(p => pages.add(p));
      if (pages.size) return { pages, tier: 2 };
    }
    refsInRange(test.file, 1, Infinity).forEach(p => pages.add(p));
    return { pages, tier: 3 };
  };
}

/** THE page -> test-filter function. Returns, for a page key, the ids of the tests that open it. */
function buildTestsForPage(tests, pageKeys) {
  const pagesOfTest = pagesOfTestFactory(tests, pageKeys);
  const byPage = new Map();
  const tierCounts = { 1: 0, 2: 0, 3: 0 };
  for (const test of tests) {
    const { pages, tier } = pagesOfTest(test);
    tierCounts[tier]++;
    for (const p of pages) {
      if (!byPage.has(p)) byPage.set(p, new Set());
      byPage.get(p).add(test.id);
    }
  }
  const testsForPage = pageKey => byPage.get(pageKey) ?? new Set();
  testsForPage.galleryOnly = pageKey => new Set([...testsForPage(pageKey)].filter(id => isGalleryTest(tests[id])));
  testsForPage.variantGallery = (pageKey, variant) => new Set([...testsForPage(pageKey)]
    .filter(id => tests[id].project === variant && tests[id].describes[0] === `${variant}-gallery`));
  testsForPage.tierCounts = tierCounts;
  return testsForPage;
}

// ---------------------------------------------------------------------------------------------
// Preview .zul changes and pv/ templates
// ---------------------------------------------------------------------------------------------

function allZulKeys() {
  const keys = [];
  const walk = (dir, prefix) => {
    for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
      if (e.isDirectory() && (dir !== WEBAPP_DIR || e.name === 'web')) walk(path.join(dir, e.name), `${prefix}${e.name}/`);
      else if (e.name.endsWith('.zul')) keys.push({ key: zulKeyOf(`${prefix}${e.name.replace(/\.zul$/, '')}`), file: path.join(dir, e.name) });
    }
  };
  walk(WEBAPP_DIR, '');
  return keys;
}

/** Page key as the specs spell it: `web/pv/matrix.zul` -> `pv/matrix`; `web/button.zul` -> `button`. */
function zulKeyOf(relUnderWebapp) {
  return relUnderWebapp.replace(/^web\//, '');
}

/**
 * The changed .zul plus, for a pv/ template, every page that applies it directly or through another
 * template. Plain pages are not followed: others only link to them (preview.zul lists every page).
 */
function zulWithDependants(changedKey, zuls) {
  const affected = new Set([changedKey]);
  if (!changedKey.startsWith('pv/')) return affected;
  const queue = [changedKey];
  while (queue.length) {
    const key = queue.shift();
    for (const z of zuls) {
      if (affected.has(z.key)) continue;
      if (fs.readFileSync(z.file, 'utf8').includes(`${key}.zul`)) { affected.add(z.key); queue.push(z.key); }
    }
  }
  return affected;
}

// ---------------------------------------------------------------------------------------------
// Resolution of each changed file into: full-run reasons, pages, tests, uncovered, no-test notes
// ---------------------------------------------------------------------------------------------

function createSelection(aggregatePages) {
  return {
    fullReasons: [], reasons: [], pages: new Set(), testIds: new Set(), uncovered: [], namedWithoutTest: [], untestedPages: new Set(),
    aggregatePages, skippedAggregates: new Set(),
  };
}

const isTemplate = key => key.startsWith('pv/');

/**
 * Adds pages and the tests that open them. An aggregate page is added only when the change names it. A pv/ page
 * counts as a page when a test opens it; a template no test opens is silently ignored. Any other page no test
 * opens is reported as uncovered when the change names it, and silently left out when it is only reached
 * through a class (the preview.zul listing carries nearly every class and is never tested).
 */
function addPagesAndTests(sel, pages, testsForPage, namedPages = []) {
  for (const p of pages) {
    if (sel.aggregatePages.has(p) && !namedPages.includes(p)) { sel.skippedAggregates.add(p); continue; }
    const ids = testsForPage(p);
    ids.forEach(id => sel.testIds.add(id));
    if (ids.size) sel.pages.add(p);
    else if (isTemplate(p)) continue;
    else if (namedPages.includes(p)) { sel.pages.add(p); sel.namedWithoutTest.push(`page ${p}: no test opens it`); }
    else sel.untestedPages.add(p);
  }
}

function resolveChange(change, ctx, sel) {
  const where = change.repo === 'zk' ? change.rel : `zkcml:${change.rel}`;
  const { rel } = change;

  for (const [pattern, why] of FULL_RUN_TRIGGERS) {
    if (pattern.test(rel)) { sel.fullReasons.push(`${where}: ${why}`); return; }
  }
  for (const [pattern, why] of NO_TEST_PATHS) {
    if (pattern.test(rel)) { sel.reasons.push(`${where}: no tests needed (${why})`); return; }
  }

  const css = rel.match(COMPONENT_CSS) ?? rel.match(TABLET_CSS);
  if (css) return resolveComponentCss(change, where, css[1].replace(/^_/, ''), ctx, sel);

  const widget = rel.match(WIDGET_SOURCE);
  if (widget) return resolveWidget(where, widget[1], ctx, sel);

  if (change.repo === 'zk') {
    const spec = rel.match(SPEC_FILE);
    if (spec) return resolveSpec(where, spec[1], ctx, sel);
    const specData = rel.match(SPEC_DATA_FILE);
    if (specData) return resolveSpecData(where, specData[1], ctx, sel);
    const zul = rel.match(PREVIEW_ZUL);
    if (zul) return resolvePreviewZul(where, zulKeyOf(zul[1]), ctx, sel);
    const png = rel.match(BASELINE_PNG);
    if (png) return resolveBaselinePng(where, png[1], ctx, sel);
  }
  sel.fullReasons.push(`${where}: unrecognised path type, cannot decide what it affects`);
}

/** A component's own preview page (`combobox.zul` for combobox.css or Combobox.ts) is always selected, whatever the rule. */
function addOwnPage(pages, component, ctx) {
  const hasOwnPage = ctx.zuls.some(z => z.key === component);
  if (hasOwnPage) pages.add(component);
  return hasOwnPage ? component : null;
}

function resolveComponentCss(change, where, component, ctx, sel) {
  if (SHARED_CSS_FILES.has(component)) {
    sel.fullReasons.push(`${where}: shared CSS file (${component}.css) treated like base`);
    return;
  }
  const parsed = readCssClasses(change);
  if (!parsed) { sel.fullReasons.push(`${where}: cannot read the file to parse its classes`); return; }
  if (parsed.selectorCount === 0) { // only blank lines or comments changed
    sel.reasons.push(`${where}: ${parsed.parseMode}: no rule changed, rendered CSS is unaffected`);
    return;
  }
  if (parsed.classes.size === 0 && parsed.attributePatterns.length === 0) {
    sel.fullReasons.push(`${where}: no .z-* class selectors in ${parsed.parseMode} (custom properties or element rules), cannot tell which pages it affects`);
    return;
  }
  const { pages, unresolved, byPrefix, byComponent } = ctx.classes.resolve(parsed.classes, parsed.attributePatterns, component);
  const ownPage = addOwnPage(pages, component, ctx);
  const notes = [`parsed ${parsed.parseMode}`, `${parsed.classes.size} classes -> ${pages.size} pages`];
  if (ownPage) notes.push(`own page ${ownPage}.zul always selected`);
  if (byPrefix.length) notes.push(`${byPrefix.length} resolved by name prefix (${byPrefix.slice(0, 3).join(', ')}${byPrefix.length > 3 ? ', ...' : ''})`);
  if (byComponent.length) notes.push(`${byComponent.length} resolved by the file's own component z-${component}`);
  sel.reasons.push(`${where}: ${notes.join('; ')}`);
  for (const c of unresolved) sel.uncovered.push(`class .${c} in ${where}: not in any page at rest, no owner prefix, no preview page for z-${component}`);
  addPagesAndTests(sel, pages, ctx.testsForPage);
}

function resolveWidget(where, name, ctx, sel) {
  const rootClass = `z-${name.toLowerCase()}`;
  const { pages, unresolved } = ctx.classes.resolve([rootClass], [], null);
  const ownPage = addOwnPage(pages, name.toLowerCase(), ctx);
  sel.reasons.push(`${where}: widget root class .${rootClass} -> ${pages.size} pages${ownPage ? `, own page ${ownPage}.zul always selected` : ''}`);
  unresolved.forEach(c => sel.uncovered.push(`widget ${where}: root class .${c} is on no preview page`));
  addPagesAndTests(sel, pages, ctx.testsForPage);
}

function addSpecFile(sel, ctx, file) {
  ctx.tests.filter(t => t.file === file).forEach(t => sel.testIds.add(t.id));
}

function resolveSpec(where, file, ctx, sel) {
  if (!ctx.tests.some(t => t.file === file)) { sel.reasons.push(`${where}: no tests collected from this spec (not matched by any project)`); return; }
  sel.reasons.push(`${where}: spec file -> run only that spec`);
  addSpecFile(sel, ctx, file);
}

function resolveSpecData(where, jsonName, ctx, sel) {
  const readers = [...new Set(ctx.tests.map(t => t.file))]
    .filter(file => fs.readFileSync(path.join(SPEC_DIR, file), 'utf8').includes(jsonName));
  if (readers.length === 0) { sel.fullReasons.push(`${where}: JSON file read by no spec, cannot decide what it affects`); return; }
  sel.reasons.push(`${where}: baseline data read by ${readers.join(', ')}`);
  readers.forEach(file => addSpecFile(sel, ctx, file));
}

function resolvePreviewZul(where, key, ctx, sel) {
  const affected = zulWithDependants(key, ctx.zuls);
  const dependants = [...affected].filter(k => k !== key);
  sel.reasons.push(`${where}: preview page ${key}${dependants.length ? `, applied by ${dependants.join(', ')}` : ''}`);
  addPagesAndTests(sel, affected, ctx.testsForPage, [key]);
}

function resolveBaselinePng(where, name, ctx, sel) {
  const variantGallery = variantGalleryOfBaseline(name, ctx.classes.pageKeys);
  if (variantGallery) {
    const { page, variant } = variantGallery;
    sel.reasons.push(`${where}: ${variant} gallery baseline image of page ${page}`);
    addPagesAndTests(sel, [page], pageKey => ctx.testsForPage.variantGallery(pageKey, variant), [page]);
    return;
  }
  const galleryPage = galleryPageOfBaseline(name, ctx.classes.pageKeys);
  if (galleryPage) {
    sel.reasons.push(`${where}: gallery baseline image of page ${galleryPage}`);
    addPagesAndTests(sel, [galleryPage], ctx.galleryTestsForPage, [galleryPage]);
    return;
  }
  const owner = [...ctx.classes.pageKeys].filter(p => name.startsWith(`${p}-`)).sort((a, b) => b.length - a.length)[0];
  if (!owner) { sel.fullReasons.push(`${where}: baseline image of no known page`); return; }
  sel.reasons.push(`${where}: baseline image of page ${owner}`);
  addPagesAndTests(sel, [owner], ctx.testsForPage, [owner]);
}

// ---------------------------------------------------------------------------------------------
// Command: every selected test in ONE Playwright invocation
// ---------------------------------------------------------------------------------------------

const escapeRegExp = s => s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
const shellQuote = s => `'${s.replace(/'/g, `'\\''`)}'`;

/**
 * One -g regex: `^(?:alternative|alternative|...)$` over the string Playwright matches, `<project> <file> <describe...> <title>`.
 * A whole project, or a whole file of a project, is written as `<prefix> .*`; otherwise the titles of one describe
 * are merged into `<prefix> (?:a|b|c)`. The project is part of every alternative, so `forced-colors` never matches
 * `forced-colors-gallery`.
 */
function grepPatternFor(selected, allTests) {
  const alternatives = [];
  for (const project of new Set(selected.map(t => t.project))) {
    const chosenInProject = selected.filter(t => t.project === project);
    if (chosenInProject.length === allTests.filter(t => t.project === project).length) {
      alternatives.push(`${escapeRegExp(project)} .*`);
      continue;
    }
    for (const file of new Set(chosenInProject.map(t => t.file))) {
      const chosenInFile = chosenInProject.filter(t => t.file === file);
      if (chosenInFile.length === allTests.filter(t => t.project === project && t.file === file).length) {
        alternatives.push(`${escapeRegExp(project)} ${escapeRegExp(file)} .*`);
        continue;
      }
      const tailsByDescribes = new Map();
      for (const t of chosenInFile) {
        const prefix = [project, file, ...t.describes].join(' ');
        if (!tailsByDescribes.has(prefix)) tailsByDescribes.set(prefix, []);
        tailsByDescribes.get(prefix).push(t.grepTitle.slice(prefix.length + 1));
      }
      for (const [prefix, tails] of tailsByDescribes) {
        const uniqueTails = [...new Set(tails)].map(escapeRegExp);
        alternatives.push(`${escapeRegExp(prefix)} ${uniqueTails.length === 1 ? uniqueTails[0] : `(?:${uniqueTails.join('|')})`}`);
      }
    }
  }
  return `^(?:${alternatives.join('|')})$`;
}

/** The command plus the reason `--fully-parallel` was or was not added. */
function buildCommand(selectedIds, tests) {
  const selected = tests.filter(t => selectedIds.has(t.id));
  const projects = [...new Set(selected.map(t => t.project))];
  const pattern = grepPatternFor(selected, tests);
  assertPatternIsExact(pattern, projects, selected, tests);

  const unsafeSpecs = [...new Set(selected.map(t => t.file))].filter(file => UNSAFE_PARALLEL_SPECS.has(file));
  const parallelReason = unsafeSpecs.length
    ? `--fully-parallel not added: ${unsafeSpecs.map(file => `${file} (${UNSAFE_PARALLEL_SPECS.get(file)})`).join('; ')}`
    : `--fully-parallel added: none of the ${new Set(selected.map(t => t.file)).size} selected spec file(s) is in UNSAFE_PARALLEL_SPECS`;
  const command = [RUN_PREFIX, ...(unsafeSpecs.length ? [] : ['--fully-parallel']), ...projects.map(p => `--project=${p}`), `-g ${shellQuote(pattern)}`].join(' ');
  return { command, parallelReason };
}

/** Guards against a -g pattern that Playwright would resolve to more (or fewer) tests than intended within the given projects. */
function assertPatternIsExact(pattern, projects, intended, tests) {
  const regex = new RegExp(pattern, 'i');
  const intendedIds = new Set(intended.map(t => t.id));
  const matchedIds = new Set(tests.filter(t => projects.includes(t.project) && regex.test(t.grepTitle)).map(t => t.id));
  const wrong = [...matchedIds].filter(id => !intendedIds.has(id)).length + [...intendedIds].filter(id => !matchedIds.has(id)).length;
  if (wrong) throw new Error(`-g pattern is not exact (${wrong} mismatches; intended ${intendedIds.size}, matched ${matchedIds.size})`);
}

// ---------------------------------------------------------------------------------------------
// Result and output
// ---------------------------------------------------------------------------------------------

// A partial run is worth it up to this many tests; rounded up so that 2/3 of 1,345 is 897 (rules §3).
const maxPartialTests = (total, fraction) => Math.ceil(total * fraction);

function decide(sel, tests, threshold) {
  const base = {
    reasons: sel.reasons, pages: [...sel.pages].sort(), uncovered: [...sel.uncovered, ...sel.namedWithoutTest],
    selectedTests: sel.testIds.size, totalTests: tests.length,
  };
  if (sel.untestedPages.size) base.reasons = [...base.reasons, `pages no test opens, left out: ${[...sel.untestedPages].sort().join(', ')}`];
  if (sel.skippedAggregates.size) {
    base.reasons = [...base.reasons, `aggregate pages left out (only their own .zul selects them, aggregate-pages.json): ${[...sel.skippedAggregates].sort().join(', ')}`];
  }
  const fullReasons = [...sel.fullReasons];
  if (sel.uncovered.length) fullReasons.push(`${sel.uncovered.length} class(es) could not be mapped to any page (see uncovered)`);
  if (sel.testIds.size > maxPartialTests(tests.length, threshold)) {
    fullReasons.push(`${sel.testIds.size} of ${tests.length} tests selected, more than ${maxPartialTests(tests.length, threshold)} (threshold ${threshold})`);
  }
  if (fullReasons.length) {
    return { mode: 'full', ...base, selectedTests: tests.length, reasons: [...fullReasons, ...sel.reasons], commands: [RUN_PREFIX], testCounts: countByProject(tests.map(t => t.id), tests) };
  }
  if (sel.testIds.size === 0) return { mode: 'none', ...base, commands: [], testCounts: {} };
  const { command, parallelReason } = buildCommand(sel.testIds, tests);
  return { mode: 'partial', ...base, reasons: [...base.reasons, parallelReason], commands: [command], testCounts: countByProject(sel.testIds, tests) };
}

function countByProject(ids, tests) {
  const counts = {};
  for (const id of ids) counts[tests[id].project] = (counts[tests[id].project] ?? 0) + 1;
  return counts;
}

function printSummary(result) {
  console.log(`mode: ${result.mode} (selected ${result.selectedTests} of ${result.totalTests} tests, ${result.pages.length} pages)`);
  console.log('reasons:');
  result.reasons.forEach(r => console.log(`  - ${r}`));
  if (result.pages.length) console.log(`pages (${result.pages.length}): ${result.pages.join(', ')}`);
  if (result.uncovered.length) {
    console.log('uncovered:');
    result.uncovered.forEach(u => console.log(`  - ${u}`));
  }
  if (result.mode !== 'none') {
    console.log('run from zkpreview/ (set PREVIEW_URL for a port other than 8085):');
    result.commands.forEach(c => console.log(`  ${c}`));
  }
}

function main() {
  const opts = parseArgs(process.argv.slice(2));
  const aggregatePages = loadAggregatePages();
  const classIndex = loadClassIndex(aggregatePages);
  const tests = listAllTests();
  const zuls = allZulKeys();
  const ctx = {
    tests, zuls,
    classes: { ...classResolver(classIndex), pageKeys: classIndex.pageKeys },
    testsForPage: null, galleryTestsForPage: null,
  };
  ctx.testsForPage = buildTestsForPage(tests, new Set([...classIndex.pageKeys, ...zuls.map(z => z.key)]));
  ctx.galleryTestsForPage = ctx.testsForPage.galleryOnly;
  const selection = createSelection(aggregatePages);
  collectChanges(opts).forEach(change => resolveChange(change, ctx, selection));
  const result = decide(selection, tests, opts.threshold);
  if (opts.json) {
    const { testCounts, ...rest } = result;
    console.log(JSON.stringify({ ...rest, tests: testCounts }, null, 2));
  } else {
    printSummary(result);
  }
}

if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) main();

export { buildTestsForPage, listAllTests, loadClassIndex, allZulKeys, classResolver, parseCssClasses };
