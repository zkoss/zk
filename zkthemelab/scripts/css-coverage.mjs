#!/usr/bin/env node
// CSS rule coverage of the zkthemelab Playwright suite: which served style rules no test ever applied?
//
// A coverage run (below) makes every test write test-results/css-coverage/<project>--<test>.json with the
// rules Chromium reported as used on every page the test opened, plus each stylesheet text once under sheets/<sha1>.css.
// This script unions those files, parses every stylesheet into style rules and writes, next to each other in the same
// folder, report.md, never-applied.json and not-parsed-by-chromium.json. A rule is never-applied when no Marble test
// page had an element that matched it.
//
// How the numbers are made (details and limits are printed at the top of report.md):
//   - While a test body runs the collector sends a page nothing but what CDP needs to follow it (agents stay enabled,
//     CSS.getStyleSheetText once per added stylesheet); there is no polling. Usage is read by CDP
//     CSS.takeCoverageDelta before every navigating Page call (goto/reload/setContent/goBack/goForward), before a page
//     or context closes and at test end; those last reads force a style update first. So every page a test reaches by
//     those calls counts. A navigation the page starts itself (a link click, location.href=, a form submit) LOSES the
//     earlier document's usage, and a page that closes itself (window.close()) loses all of it: the collector records
//     both per test (scriptNavigations, selfClosedPages) and the report flags those tests.
//   - Chromium reports used rules only, with offsets and no list of all rules, so the rule list comes from this
//     script's own parser. Each sheet is then parsed once more by headless Chromium (the same Playwright install):
//     a rule whose selector Chromium rejects (e.g. ::-ms-expand) is listed as "not parsed by Chromium", not as never
//     applied, and the selector sequence of Chromium's CSSOM must equal the parser's (rejected rules left out) or the
//     script stops with an error instead of reporting. If Chromium cannot be launched the check cannot run: the report
//     is marked UNVERIFIED, the exit code is 1 and never-applied.json and
//     not-parsed-by-chromium.json are not written (an existing copy is left as it is).
//   - The offsets are checked too: every used range Chromium reported must start at the start of a rule the parser knows
//     (a style rule, or the prelude of an @layer/@media/@supports/... block), else the script stops with an error.
//   - Every document is IceBlue or Marble by itself: it is IceBlue when it loaded a stylesheet under /_zkiju-iceblue/
//     or /iceblue/ (the theme cookie is not trusted), and its usage is filed under that theme, also when one page
//     visits both themes in turn. Marble numbers count only usage from Marble documents, so they do not depend on which
//     projects ran.
//   - The collector drops the `path` option of page/locator.screenshot, so a run never rewrites
//     doc/screenshots/*-forced-colors.png (forced-colors-gallery.spec.ts), and loses no coverage by it.
//
// Collect (from zkthemelab/, preview server up; --ignore-snapshots compares and writes no screenshot, and the separate
// --output keeps Playwright from cleaning the coverage folder, which a plain run does when it shares test-results/):
//   rm -r test-results/css-coverage   # a stale folder would be merged in
//   CSS_COVERAGE=1 PREVIEW_URL=http://127.0.0.1:8125 npx playwright test -c src/test/playwright/playwright.config.ts \
//     --tsconfig src/test/playwright/css-coverage/tsconfig.json --output test-results/css-coverage-run --ignore-snapshots
//   (CSS_COVERAGE_DIR redirects the collected JSON; the default is test-results/css-coverage)
// Merge:
//   node scripts/css-coverage.mjs [--dir test-results/css-coverage]
// The collector is src/test/playwright/css-coverage/; --tsconfig maps '@playwright/test' to its wrapper, so no spec
// changes. Without CSS_COVERAGE=1 the wrapper hands out the stock `test`.

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ZKTHEMELAB_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const GROUP_AT_RULES = new Set(['media', 'supports', 'layer', 'container', 'scope', 'document', 'starting-style']);
const CONDITION_AT_RULES = new Set(['media', 'supports', 'container']);
const MAX_SELECTOR_CHARS = 160;
// The collector names a test's file <project>--<test id>--r<retry>.json; any other JSON in the folder (the merge's own
// outputs, a classification someone saved there) is not a test.
const TEST_FILE = /^.+--.+--r\d+\.json$/;
// The spacing, display, container-query and order utilities (.z-mx-*, .z-d-*, .z-cq-*, .z-order-*) are no component.
const UTILITY_PREFIXES = /^([a-z]{1,2}|cq|order)$/;

function parseArgs(argv) {
  const opts = { dir: path.join(ZKTHEMELAB_DIR, 'test-results/css-coverage') };
  for (let i = 0; i < argv.length; i++) {
    if (argv[i] === '--dir') opts.dir = path.resolve(argv[++i]);
    else throw new Error(`unknown argument ${argv[i]}`);
  }
  return opts;
}

// ---------------------------------------------------------------- CSS parsing

/** Index just past the comment or string starting at `i`, or `i` when none starts there. */
function skipCommentOrString(text, i) {
  const ch = text[i];
  if (ch === '/' && text[i + 1] === '*') {
    const end = text.indexOf('*/', i + 2);
    return end < 0 ? text.length : end + 2;
  }
  if (ch !== '"' && ch !== "'") return i;
  for (let j = i + 1; j < text.length; j++) {
    if (text[j] === '\\') j++;
    else if (text[j] === ch) return j + 1;
  }
  return text.length;
}

/** Index of the first character in [from, to) that is neither whitespace nor part of a comment, or `to`. */
function skipSpaceAndComments(text, from, to) {
  let i = from;
  while (i < to) {
    if (/\s/.test(text[i])) i++;
    else if (text[i] === '/' && text[i + 1] === '*') i = skipCommentOrString(text, i);
    else break;
  }
  return Math.min(i, to);
}

/** `text` without its comments; strings are kept as they are. */
function stripComments(text) {
  let out = '';
  for (let i = 0; i < text.length;) {
    const next = skipCommentOrString(text, i);
    if (next !== i && text[i] === '/') i = next;
    else if (next !== i) { out += text.slice(i, next); i = next; }
    else out += text[i++];
  }
  return out;
}

/** Index of the `}` that closes the `{` at `open`. */
function findClosingBrace(text, open) {
  let depth = 0;
  for (let i = open; i < text.length; i++) {
    const next = skipCommentOrString(text, i);
    if (next !== i) { i = next - 1; continue; }
    if (text[i] === '{') depth++;
    else if (text[i] === '}' && --depth === 0) return i;
  }
  return text.length - 1;
}

/**
 * Splits a stylesheet into leaf style rules and the other at-rule blocks (@font-face, @keyframes, ...).
 * Grouping at-rules (@media, @supports, @layer, @container, ...) are entered; the conditions of @media, @supports and
 * @container are kept on each rule. A style rule is a leaf: CSS nesting inside it would count as part of it.
 * Returns { rules: [{start, end, selector, conditions, nested}], atRules: [{start, end, name, prelude}],
 * groupStarts: [offset of each grouping at-rule's prelude, after its @-keyword] }.
 */
export function parseStylesheet(text) {
  const rules = [];
  const atRules = [];
  const groupStarts = [];
  const stack = [];
  let preludeStart = 0;
  let parens = 0;
  for (let i = 0; i < text.length; i++) {
    const next = skipCommentOrString(text, i);
    if (next !== i) { i = next - 1; continue; }
    const ch = text[i];
    if (ch === '(') parens++;
    else if (ch === ')') parens = Math.max(0, parens - 1);
    else if (parens > 0) continue;
    else if (ch === ';') preludeStart = i + 1;
    else if (ch === '}') { stack.pop(); preludeStart = i + 1; }
    else if (ch === '{') {
      // Chromium's startOffset of a rule is its selector or @-keyword, after any comment in front of it.
      const start = skipSpaceAndComments(text, preludeStart, i);
      const prelude = stripComments(text.slice(start, i)).trim();
      const atName = prelude.startsWith('@') ? /^@([\w-]+)/.exec(prelude)?.[1].toLowerCase() : null;
      if (atName && GROUP_AT_RULES.has(atName)) {
        stack.push({ name: atName, prelude });
        groupStarts.push(skipSpaceAndComments(text, start + 1 + atName.length, i)); // Chromium starts a group at its prelude
        preludeStart = i + 1;
        continue;
      }
      const end = findClosingBrace(text, i);
      if (atName) atRules.push({ start, end, name: atName, prelude });
      else {
        const body = text.slice(i + 1, end);
        rules.push({
          start, end, selector: prelude, nested: body.includes('{'),
          conditions: stack.filter((g) => CONDITION_AT_RULES.has(g.name)).map((g) => g.prelude),
        });
      }
      i = end;
      preludeStart = end + 1;
    }
  }
  return { rules, atRules, groupStarts };
}

// ---------------------------------------------------------------- reading the collected data

/** The theme a collected sheet was seen in; data from an older collector has none and was Marble or IceBlue by project. */
const themeOf = (sheet, test) => sheet.theme ?? (test.project === 'iceblue' ? 'iceblue' : 'marble');

/** Stylesheet contents keyed "<theme>:<hash>": one content seen by both themes is two entries, never mixed. */
function readCollected(dir) {
  const sheets = new Map(); // key -> { hash, theme, urls, projects, tests, usedRanges }
  const tests = [];
  let inlineSheets = 0;
  const files = fs.readdirSync(dir).filter((f) => TEST_FILE.test(f)).sort();
  for (const file of files) {
    const test = JSON.parse(fs.readFileSync(path.join(dir, file), 'utf8'));
    tests.push({
      project: test.project, title: test.title, file: test.file, status: test.status, pages: test.pages,
      scriptNavigations: test.scriptNavigations ?? 0, selfClosedPages: test.selfClosedPages ?? 0,
    });
    inlineSheets += test.inlineSheets;
    for (const sheet of test.sheets) {
      const theme = themeOf(sheet, test);
      const key = `${theme}:${sheet.hash}`;
      let entry = sheets.get(key);
      if (!entry) {
        entry = { hash: sheet.hash, theme, urls: new Set(), projects: new Set(), tests: new Set(), usedRanges: new Map() };
        sheets.set(key, entry);
      }
      entry.urls.add(sheet.url);
      entry.projects.add(test.project);
      entry.tests.add(file);
      for (let i = 0; i < sheet.used.length; i += 2) entry.usedRanges.set(sheet.used[i], sheet.used[i + 1]);
    }
  }
  return { sheets, tests, inlineSheets };
}

const readSheetText = (hash, dir) => fs.readFileSync(path.join(dir, 'sheets', `${hash}.css`), 'utf8');

/** The rules of one stylesheet content (theme-independent): every rule parsed, none yet flagged used. */
function parseSheet(hash, dir) {
  const text = readSheetText(hash, dir);
  const { rules, atRules, groupStarts } = parseStylesheet(text);
  const atRuleCounts = new Map();
  for (const a of atRules) atRuleCounts.set(a.name, (atRuleCounts.get(a.name) ?? 0) + 1);
  return { text, rules, atRules, groupStarts, atRuleCounts, bytes: text.length, nestedRules: rules.filter((r) => r.nested).length };
}

/**
 * Asks headless Chromium which parsed rules it drops and what it makes of the whole sheet. Chromium reports only used
 * rules, never the list of all rules, so the parser's list is cross-checked twice: a selector that `insertRule` rejects
 * is a rule Chromium never had, and the selectors a real CSSOM walk of the sheet yields must equal, in order, the
 * parser's selectors that survived. Sets `rule.notParsed` and `sheet.chromium = { styleRules, selectorMismatch }` on
 * every sheet; returns false when no browser started.
 */
async function markRulesChromiumDrops(sheetsByHash) {
  let browser;
  try {
    const { chromium } = await import('playwright');
    browser = await chromium.launch();
  } catch (error) {
    console.warn(`Chromium check skipped: ${error.message.split('\n')[0]}`);
    return false;
  }
  const page = await browser.newPage();
  for (const sheet of sheetsByHash.values()) {
    const { styleRules, normalized, cssomSelectors } = await page.evaluate(({ text, selectors }) => {
      const parsed = new CSSStyleSheet();
      parsed.replaceSync(text);
      let styleRules = 0;
      const count = (list) => {
        for (const rule of list) {
          if (rule instanceof CSSStyleRule) styleRules++;
          if (rule.cssRules) count(rule.cssRules);
        }
      };
      count(parsed.cssRules);
      // The leaf style rules in source order, not entering style rules (nested rules belong to their parent).
      const cssomSelectors = [];
      const walk = (list) => {
        for (const rule of list) {
          if (rule instanceof CSSStyleRule) cssomSelectors.push(rule.selectorText);
          else if (rule.cssRules && !(rule instanceof CSSKeyframesRule)) walk(rule.cssRules);
        }
      };
      walk(parsed.cssRules);
      // Each parsed selector as Chromium serialises it, or null when Chromium rejects it.
      const probe = new CSSStyleSheet();
      const normalized = selectors.map((selector) => {
        try {
          probe.insertRule(`${selector}{}`, 0);
          const serialised = probe.cssRules[0].selectorText;
          probe.deleteRule(0);
          return serialised;
        } catch {
          return null;
        }
      });
      return { styleRules, normalized, cssomSelectors };
    }, { text: sheet.text, selectors: sheet.rules.map((r) => r.selector) });
    sheet.rules.forEach((rule, index) => { rule.notParsed = normalized[index] === null; });
    sheet.chromium = { styleRules, selectorMismatch: firstSelectorMismatch(normalized.filter((n) => n !== null), cssomSelectors) };
  }
  await browser.close();
  return true;
}

/** Describes where two selector sequences first differ, or returns null when they are identical. */
function firstSelectorMismatch(parserSelectors, cssomSelectors) {
  const common = Math.min(parserSelectors.length, cssomSelectors.length);
  for (let i = 0; i < common; i++) {
    if (parserSelectors[i] !== cssomSelectors[i]) {
      return `rule ${i}: parser has \`${clip(parserSelectors[i])}\`, Chromium has \`${clip(cssomSelectors[i])}\``;
    }
  }
  if (parserSelectors.length === cssomSelectors.length) return null;
  return `the parser has ${parserSelectors.length} accepted rules, Chromium ${cssomSelectors.length} (the lists agree up to rule ${common})`;
}

/** Stops the run when the parser's rule list for any sheet disagrees with Chromium's, since the report would be wrong. */
function assertParserAgreesWithChromium(sheetsByHash) {
  const disagreements = [...sheetsByHash].filter(([, sheet]) => sheet.chromium.selectorMismatch);
  if (disagreements.length === 0) return;
  const lines = disagreements.map(([hash, sheet]) => `  sheet ${hash.slice(0, 8)}: ${sheet.chromium.selectorMismatch}`);
  throw new Error(`The rule list of ${disagreements.length} stylesheet(s) disagrees with what Chromium parses; the parser `
    + `(parseStylesheet) has a gap this sheet hits, so no report is written:\n${lines.join('\n')}`);
}

/** Stops the run when Chromium reports a used range that starts where the parser knows no rule or group prelude. */
function assertOffsetsAgree(collectedSheets, sheetsByHash) {
  const problems = [];
  for (const entry of collectedSheets.values()) {
    const sheet = sheetsByHash.get(entry.hash);
    const known = new Set([...sheet.rules.map((r) => r.start), ...sheet.groupStarts]);
    const stray = [...entry.usedRanges.keys()].filter((start) => !known.has(start));
    if (stray.length > 0) problems.push(`  ${entry.theme} sheet ${entry.hash.slice(0, 8)}: ${stray.length} used range(s) start at no known rule, first at offset ${stray[0]}`);
  }
  if (problems.length === 0) return;
  throw new Error(`Chromium's used ranges disagree with the parser's rule offsets, so no report is written:\n${problems.join('\n')}`);
}

/** Coverage of one stylesheet content as seen by one theme: which of its rules the theme's pages applied. */
function analyse(sheet, entry) {
  const ruleStarts = new Set(sheet.rules.map((r) => r.start));
  const atRuleOf = (offset) => sheet.atRules.find((a) => offset >= a.start && offset < a.end);
  const unmatched = { insideAtRule: new Map(), other: 0 };
  for (const start of entry.usedRanges.keys()) {
    if (ruleStarts.has(start)) continue;
    const at = atRuleOf(start);
    if (at) unmatched.insideAtRule.set(at.name, (unmatched.insideAtRule.get(at.name) ?? 0) + 1);
    else unmatched.other++;
  }
  const rules = sheet.rules.map((rule) => ({ ...rule, used: entry.usedRanges.has(rule.start) }));
  return { ...sheet, rules, unmatched, notParsed: rules.filter((r) => r.notParsed), parsed: rules.filter((r) => !r.notParsed) };
}

// ---------------------------------------------------------------- report

const pct = (part, whole) => (whole === 0 ? '-' : `${((100 * part) / whole).toFixed(1)}%`);
const shortUrl = (url) => url.replace(/^https?:\/\/[^/]+/, '').replace(/\/zkres\/web\/[0-9a-f]+\//, '/…/');
const escapeCell = (s) => s.replace(/\|/g, '\\|').replace(/`/g, "'");
const clip = (s) => (s.length > MAX_SELECTOR_CHARS ? `${s.slice(0, MAX_SELECTOR_CHARS)}…` : s);

/** Heuristic component of a rule: the first `.z-<word>` class of its selector, up to the first dash. */
function componentOf(selector) {
  const match = /\.z-([a-z0-9]+)/.exec(selector);
  if (!match) return '(no .z- class)';
  return UTILITY_PREFIXES.test(match[1]) ? '(utility classes)' : match[1];
}

function describeRule(rule) {
  const condition = rule.conditions.length ? `  — in ${rule.conditions.join(' and ')}` : '';
  return `- \`${clip(escapeCell(rule.selector))}\`${condition}`;
}

function sheetLabel(entry) {
  return `${[...entry.urls].map(shortUrl).join(', ')} [${entry.hash.slice(0, 8)}]`;
}

/** One machine-readable record per rule, for classifying the never-applied list and the not-parsed list. */
function ruleRecord(row, rule) {
  return {
    sheet: [...row.entry.urls][0],
    sheetHash: row.entry.hash,
    selector: rule.selector,
    conditions: rule.conditions,
    component: componentOf(rule.selector),
    offset: rule.start,
    projectsLoadedIn: [...row.entry.projects].sort(),
  };
}

function buildReport({ sheets, tests, inlineSheets }, sheetsByHash, dir, chromiumChecked) {
  const entries = [...sheets.values()];
  const isEmpty = (entry) => sheetsByHash.get(entry.hash).bytes === 0; // a page requested as a stylesheet (reset-scoping.spec.ts)
  const marble = entries.filter((e) => e.theme === 'marble' && !isEmpty(e));
  const iceblue = entries.filter((e) => e.theme === 'iceblue');
  const projects = [...new Set(tests.map((t) => t.project))].sort();
  const statusCounts = tests.reduce((acc, t) => ({ ...acc, [t.status]: (acc[t.status] ?? 0) + 1 }), {});

  const rows = marble.map((entry) => {
    const analysis = analyse(sheetsByHash.get(entry.hash), entry);
    const applied = analysis.parsed.filter((r) => r.used).length;
    return { entry, analysis, rules: analysis.rules, total: analysis.parsed.length, applied, never: analysis.parsed.length - applied };
  }).sort((a, b) => b.never - a.never);
  const sum = (key) => rows.reduce((n, r) => n + r[key], 0);
  const notParsedTotal = rows.reduce((n, r) => n + r.analysis.notParsed.length, 0);

  const out = [];
  out.push('# CSS rule coverage of the zkthemelab Playwright suite', '');
  out.push(`Generated by scripts/css-coverage.mjs from ${tests.length} test files in \`${path.relative(ZKTHEMELAB_DIR, dir)}\`.`);
  out.push(`Projects: ${projects.join(', ')}.`);
  out.push(`Test status as recorded: ${Object.entries(statusCounts).map(([k, v]) => `${k} ${v}`).join(', ')}.`, '');
  if (!chromiumChecked) {
    out.push('> **UNVERIFIED.** The headless-Chromium check did not run, so rules the browser drops are counted as never applied and the parser\'s rule list was not compared with Chromium\'s. Do not use these numbers; fix the browser launch and run again.', '');
  }

  out.push('## Limits and treatment', '');
  out.push('- **Applied is not asserted.** A rule counts as applied when it matched any element on a page a test opened. It says nothing about whether a test checks the result; that is mutation testing\'s question (scripts/css-mutation.mjs).');
  out.push('- **Chromium only.** The data is Chromium\'s CSS rule usage (CDP `CSS.takeCoverageDelta`); Firefox and WebKit are not measured.');
  out.push('- **Pages reached through a Playwright navigation call count; two cases are under-counted.** While a test runs the collector sends a page only what CDP needs to follow it (agents stay enabled, one stylesheet-text read per added stylesheet) and does not poll (it changed rendering). Usage is read before each `goto`/`reload`/`setContent`/`goBack`/`goForward`, and before a page or context closes and at test end, the last two after forcing a style update. (1) A navigation the page starts itself (`location.href=`, a link click, a form submit) loses the usage of the earlier document; the collector detects it and the tests are flagged under "Tests that may be under-counted". (2) A page that closes itself (`window.close()`) cannot be read; its tests are listed there too. A rule that first matches in the last frame before a `goto` is also missed, since no style update is forced there.');
  out.push('- **Custom-property rules are applied at first match.** A rule that only declares `--zk-*` properties (the `:root` blocks of tokens/) counts as applied as soon as it matches, so this level cannot tell which token is never read.');
  out.push('- **Rules are the unit.** One rule is one style rule with its whole selector list; it is applied when any selector of the list matched. A rule with CSS nesting inside would count as one rule.');
  out.push(chromiumChecked
    ? `- **Rules Chromium drops are not counted.** Chromium reports used rules only, so the list of rules comes from this script's parser and each sheet is parsed again by headless Chromium. ${notParsedTotal} parsed rules have a selector Chromium rejects (for example \`::-ms-expand\`); they are listed in their own section and in not-parsed-by-chromium.json, and are in none of the counts below.`
    : '- **Rules Chromium drops are NOT excluded in this run** (the Chromium cross-check could not start), so some never-applied rules may be rules the browser never parsed.');
  out.push(`- **IceBlue is kept apart, per document.** A document is IceBlue when it loaded a stylesheet under \`/_zkiju-iceblue/\` or \`/iceblue/\`; the theme cookie is not trusted and no project name is used. A page that visits both themes files each document's usage under its own theme. The Marble numbers count usage from Marble documents only. A content that IceBlue documents loaded is listed at the end with its rule counts and not analysed; one that both themes loaded appears in both places.`);
  out.push('- **Keyed by URL, theme and content hash.** The same URL with different content (tablet User-Agent, IceBlue) is a separate row; `;jsessionid=` is dropped from URLs.');
  out.push(`- **Inline \`<style>\` blocks have no URL and are not measured** (${inlineSheets} seen across all test pages).`);
  const atRuleTotals = new Map();
  const unmatchedInside = new Map();
  let unmatchedOther = 0;
  for (const row of rows) {
    for (const [k, v] of row.analysis.atRuleCounts) atRuleTotals.set(k, (atRuleTotals.get(k) ?? 0) + v);
    for (const [k, v] of row.analysis.unmatched.insideAtRule) unmatchedInside.set(k, (unmatchedInside.get(k) ?? 0) + v);
    unmatchedOther += row.analysis.unmatched.other;
  }
  const font = atRuleTotals.get('font-face') ?? 0;
  const keyframes = atRuleTotals.get('keyframes') ?? 0;
  out.push(`- **@font-face and @keyframes are not counted.** Chromium reports no usage for them: ${font} @font-face and ${keyframes} @keyframes blocks are in the Marble sheets, and ${[...unmatchedInside].reduce((n, [, v]) => n + v, 0)} used ranges fall inside any such block. Whether a font or animation loaded is not covered. Other at-rule blocks seen and not counted: ${[...atRuleTotals].filter(([k]) => k !== 'font-face' && k !== 'keyframes').map(([k, v]) => `@${k} ${v}`).join(', ') || 'none'}.`);
  out.push(`- **Rules inside @media, @supports, @container and @layer are counted like any other rule.** Chromium reports them individually; the report shows the @media/@supports/@container condition next to each never-applied rule. A rule in a media query is applied only if a test ran at a viewport or media state that satisfies it. ${unmatchedOther} used ranges matched no leaf style rule (group rules such as the @layer block itself, which Chromium also reports).`, '');

  out.push('## Summary', '');
  out.push('Beside the theme sheets (zk.wcs, marble.css.dsp, reset.css, tablet.css.dsp, skeleton.css.dsp) the table lists CSS the preview app itself serves to some test pages (e.g. usecase.css); empty contents are left out.', '');
  out.push(`${rows.length} Marble stylesheet contents, ${sum('total')} rules, ${sum('applied')} applied (${pct(sum('applied'), sum('total'))}), **${sum('never')} never applied**, ${notParsedTotal} not parsed by Chromium (not counted).`, '');
  out.push('| Stylesheet | Projects | Rules | Applied | % | Never applied | Not parsed by Chromium |', '|---|---|---:|---:|---:|---:|---:|');
  for (const r of rows) {
    out.push(`| ${escapeCell(sheetLabel(r.entry))} | ${r.entry.projects.size} | ${r.total} | ${r.applied} | ${pct(r.applied, r.total)} | ${r.never} | ${r.analysis.notParsed.length} |`);
  }
  out.push('');

  out.push('## Never-applied rules by component (heuristic)', '');
  out.push('The component is the first `.z-<word>` class in the selector, cut at the first dash, so `.z-list-item` is `list`. It groups the one big bundle (zk.wcs) into something readable; it is not a mapping to source files, which the minified bundle does not allow cheaply.', '');
  const byComponent = new Map();
  for (const row of rows) {
    for (const rule of row.analysis.parsed) {
      const key = componentOf(rule.selector);
      const c = byComponent.get(key) ?? { total: 0, applied: 0 };
      c.total++;
      if (rule.used) c.applied++;
      byComponent.set(key, c);
    }
  }
  const components = [...byComponent].map(([name, c]) => ({ name, ...c, never: c.total - c.applied })).sort((a, b) => b.never - a.never);
  out.push('| Component | Rules | Applied | % | Never applied |', '|---|---:|---:|---:|---:|');
  for (const c of components.filter((x) => x.never > 0)) {
    out.push(`| ${escapeCell(c.name)} | ${c.total} | ${c.applied} | ${pct(c.applied, c.total)} | ${c.never} |`);
  }
  out.push('');

  for (const row of rows) {
    out.push(`## ${sheetLabel(row.entry)}`, '');
    out.push(`${row.total} rules, ${row.applied} applied (${pct(row.applied, row.total)}), ${row.never} never applied. Loaded in projects: ${[...row.entry.projects].sort().join(', ')}.`, '');
    if (row.analysis.chromium) {
      out.push(`Chromium CSSOM style rules: ${row.analysis.chromium.styleRules}; parsed here: ${row.analysis.rules.length}, of which ${row.analysis.notParsed.length} not parsed by Chromium; the selector sequence of the CSSOM and of the parser (rejected rules left out) is identical${row.analysis.rules.length - row.analysis.notParsed.length === row.analysis.chromium.styleRules ? '' : ` (the CSSOM total differs by ${row.analysis.chromium.styleRules - (row.analysis.rules.length - row.analysis.notParsed.length)} because ${row.analysis.nestedRules} rules contain nested rules, counted by the CSSOM walk only)`}.`, '');
    }
    const never = row.analysis.parsed.filter((r) => !r.used);
    if (never.length === 0) continue;
    out.push(`Never-applied rules (${never.length}), in source order:`, '');
    for (const rule of never) out.push(describeRule(rule));
    out.push('');
  }

  out.push('## Not parsed by Chromium', '');
  const notParsedRecords = rows.flatMap((row) => row.analysis.notParsed.map((rule) => ({ row, rule })));
  if (!chromiumChecked) out.push('NOT CHECKED: the headless-Chromium check did not run in this report.');
  else if (notParsedRecords.length === 0) out.push('None.');
  for (const { row, rule } of notParsedRecords) out.push(`${describeRule(rule)}  — ${shortUrl([...row.entry.urls][0])}`);
  out.push('');

  out.push('## IceBlue stylesheets (not analysed)', '');
  if (iceblue.length === 0) out.push('None.');
  for (const entry of iceblue) {
    const alsoMarble = sheets.has(`marble:${entry.hash}`) ? ' — also loaded by Marble pages' : '';
    const sheet = sheetsByHash.get(entry.hash);
    const notParsed = sheet.rules.filter((r) => r.notParsed).length;
    out.push(`- ${sheetLabel(entry)} — ${sheet.rules.length - notParsed} rules${notParsed ? ` (${notParsed} more not parsed by Chromium, not counted)` : ''}${alsoMarble}`);
  }
  out.push('');

  out.push('## Tests that may be under-counted', '');
  out.push('Tests with a navigation the page started itself after a page with stylesheets (the earlier page\'s last usage is lost), or with a page that closed itself (its usage is lost). Their results are in the numbers above; rules only those pages matched may show as never applied.', '');
  const underCounted = tests.filter((t) => t.scriptNavigations > 0 || t.selfClosedPages > 0);
  if (underCounted.length === 0) out.push('None.');
  for (const t of underCounted) {
    const causes = [t.scriptNavigations ? `${t.scriptNavigations} script-started navigation(s)` : '', t.selfClosedPages ? `${t.selfClosedPages} self-closed page(s)` : ''].filter(Boolean).join(', ');
    out.push(`- [${t.project}] ${t.file}: ${escapeCell(t.title)} — ${causes}`);
  }
  out.push('');

  const neverRecords = rows.flatMap((row) => row.analysis.parsed.filter((r) => !r.used).map((rule) => ruleRecord(row, rule)));
  const notParsed = notParsedRecords.map(({ row, rule }) => ruleRecord(row, rule));
  return { text: out.join('\n'), rows, neverRecords, notParsed, notParsedTotal, underCounted: underCounted.length };
}

async function main() {
  const { dir } = parseArgs(process.argv.slice(2));
  const collected = readCollected(dir);
  const sheetsByHash = new Map();
  for (const entry of collected.sheets.values()) {
    if (!sheetsByHash.has(entry.hash)) sheetsByHash.set(entry.hash, parseSheet(entry.hash, dir));
  }
  const chromiumChecked = await markRulesChromiumDrops(sheetsByHash);
  if (chromiumChecked) assertParserAgreesWithChromium(sheetsByHash);
  assertOffsetsAgree(collected.sheets, sheetsByHash);
  const { text, rows, neverRecords, notParsed, notParsedTotal, underCounted } = buildReport(collected, sheetsByHash, dir, chromiumChecked);
  fs.writeFileSync(path.join(dir, 'report.md'), text);
  if (chromiumChecked) { // an unverified run leaves no JSON a consumer could mistake for a result
    fs.writeFileSync(path.join(dir, 'never-applied.json'), `${JSON.stringify(neverRecords, null, 1)}\n`);
    fs.writeFileSync(path.join(dir, 'not-parsed-by-chromium.json'), `${JSON.stringify(notParsed, null, 1)}\n`);
  }
  const total = rows.reduce((n, r) => n + r.total, 0);
  const applied = rows.reduce((n, r) => n + r.applied, 0);
  const never = rows.reduce((n, r) => n + r.never, 0);
  const notParsedText = chromiumChecked ? `${notParsedTotal} not parsed by Chromium` : 'Chromium check DID NOT RUN (numbers UNVERIFIED)';
  console.log(`${collected.tests.length} tests, ${rows.length} Marble stylesheets, ${total} rules, ${applied} applied, ${never} never applied, ${notParsedText}, ${underCounted} tests possibly under-counted -> ${path.join(dir, 'report.md')}`);
  if (!chromiumChecked) process.exitCode = 1;
}

if (process.argv[1] === fileURLToPath(import.meta.url)) await main();
