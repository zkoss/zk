#!/usr/bin/env node
// Builds zkpreview/doc/class-index.json: for every top-level preview page
// (src/main/webapp/web/*.zul), and for every other .zul a spec navigates to (pv/..., utility/...),
// the sorted list of `z-*` classes present in the DOM once the page is idle. Keyed by URL path
// (`a.zul`, `pv/input-invalid-guard.zul`). select-tests.mjs reads it to map a changed CSS class to pages.
//
//   node scripts/build-class-index.mjs [--url http://127.0.0.1:8086]
//
// Needs the preview app running at --url. Exits 1 (and writes nothing) if any page fails to load.
import { chromium } from '@playwright/test';
import { execFileSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ZKPREVIEW_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const WEB_DIR = path.join(ZKPREVIEW_DIR, 'src/main/webapp/web');
const SPEC_DIR = path.join(ZKPREVIEW_DIR, 'src/test/playwright');
const OUTPUT_FILE = path.join(ZKPREVIEW_DIR, 'doc/class-index.json');
const DEFAULT_URL = 'http://127.0.0.1:8086';
const VIEWPORT = { width: 1280, height: 900 };
const PAGE_TIMEOUT_MS = 60000;

function parseUrl(argv) {
  const i = argv.indexOf('--url');
  return i >= 0 ? argv[i + 1] : DEFAULT_URL;
}

function topLevelPages() {
  return fs.readdirSync(WEB_DIR)
    .filter(f => f.endsWith('.zul') && fs.statSync(path.join(WEB_DIR, f)).isFile());
}

// A .zul URL a spec spells as a whole string literal: '/pv/input-invalid-guard.zul', `/utility/print.zul?x=1`.
// Template URLs (`/${comp}.zul`) are the top-level pages already, and a path in a comment or title is no literal.
const SPEC_ZUL_LITERAL = /['"`]\/((?:[A-Za-z0-9_-]+\/)*[A-Za-z0-9_-]+\.zul)(?:\?[^'"`]*)?['"`]/g;

/** Every existing page under web/ that some spec navigates to, as a URL path without the leading slash. */
function specNavigatedPages() {
  const found = new Set();
  for (const file of fs.readdirSync(SPEC_DIR).filter(f => f.endsWith('.spec.ts'))) {
    const source = fs.readFileSync(path.join(SPEC_DIR, file), 'utf8');
    for (const m of source.matchAll(SPEC_ZUL_LITERAL)) {
      if (fs.existsSync(path.join(WEB_DIR, m[1]))) found.add(m[1]);
    }
  }
  return [...found];
}

function pagesToIndex() {
  return [...new Set([...topLevelPages(), ...specNavigatedPages()])].sort();
}

function currentCommit() {
  return execFileSync('git', ['rev-parse', '--short=10', 'HEAD'], { cwd: ZKPREVIEW_DIR }).toString().trim();
}

function collectZClasses() {
  const found = new Set();
  document.querySelectorAll('*').forEach(el => el.classList.forEach(c => { if (c.startsWith('z-')) found.add(c); }));
  return [...found].sort();
}

async function main() {
  const baseUrl = parseUrl(process.argv.slice(2));
  const browser = await chromium.launch();
  const context = await browser.newContext({ viewport: VIEWPORT });
  const pages = {};
  const errors = {};
  for (const file of pagesToIndex()) {
    const page = await context.newPage();
    try {
      const response = await page.goto(`${baseUrl}/${file}`, { waitUntil: 'networkidle', timeout: PAGE_TIMEOUT_MS });
      if (!response || response.status() !== 200) throw new Error(`HTTP ${response && response.status()}`);
      pages[file] = await page.evaluate(collectZClasses);
    } catch (e) {
      errors[file] = String(e).slice(0, 300);
    }
    await page.close();
  }
  await browser.close();

  const failed = Object.keys(errors);
  if (failed.length) {
    console.error(`class index NOT written: ${failed.length} page(s) failed`);
    for (const f of failed) console.error(`  ${f}: ${errors[f]}`);
    process.exit(1);
  }
  const index = { generatedAt: new Date().toISOString(), commit: currentCommit(), pages };
  fs.writeFileSync(OUTPUT_FILE, JSON.stringify(index, null, 1) + '\n');
  console.log(`wrote ${path.relative(process.cwd(), OUTPUT_FILE)}: ${Object.keys(pages).length} pages`);
}

main();
