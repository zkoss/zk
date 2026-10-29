#!/usr/bin/env node
// Builds zkthemelab/doc/class-index.json: for every top-level preview page
// (src/main/webapp/web/*.zul), and for every other .zul a spec navigates to (pv/..., utility/...),
// the sorted list of `z-*` classes present in the DOM once the page is idle. Keyed by URL path
// (`a.zul`, `pv/input-invalid-guard.zul`). select-tests.mjs reads it to map a changed CSS class to pages.
//
//   node scripts/build-class-index.mjs [--port 8086]      (JDK 17, from a worktree whose jars are built)
//
// Starts its own preview server on --port, indexes, and stops it. The server runs with
//   -Dorg.zkoss.zul.client.rod=false  so combobox/bandbox/datebox/menu/popup pop-ups are rendered at page load and
//                                     their classes are present at rest (doc/client-rod-probe.md). Never run the
//                                     Playwright tests against such a server: a Tree loses its ROD spacers.
//   -Djava.awt.headless=true          captcha.zul draws with AWT; without it the server JVM aborts (exit 134).
// Refuses a busy port, and stops only the server it started. Exits 1 (and writes nothing) if any page fails to load.
import { chromium } from '@playwright/test';
import { execFileSync, spawn } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ZKTHEMELAB_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const WEB_DIR = path.join(ZKTHEMELAB_DIR, 'src/main/webapp/web');
const SPEC_DIR = path.join(ZKTHEMELAB_DIR, 'src/test/playwright');
const OUTPUT_FILE = path.join(ZKTHEMELAB_DIR, 'doc/class-index.json');
const DEFAULT_PORT = '8086';
const SERVER_JAVA_OPTIONS = '-Dorg.zkoss.zul.client.rod=false -Djava.awt.headless=true';
const SERVER_LOG = path.join(ZKTHEMELAB_DIR, 'build/class-index-server.log');
const SERVER_START_TIMEOUT_MS = 15 * 60000; // a cold composite build takes minutes
const VIEWPORT = { width: 1280, height: 900 };
const PAGE_TIMEOUT_MS = 60000;

function parsePort(argv) {
  const i = argv.indexOf('--port');
  return i >= 0 ? argv[i + 1] : DEFAULT_PORT;
}

const sleep = ms => new Promise(r => setTimeout(r, ms));

async function status(url) {
  try { return (await fetch(url)).status; } catch { return 0; }
}

/** PID listening on the port, or null. */
function listener(port) {
  try {
    return execFileSync('lsof', ['-nP', `-iTCP:${port}`, '-sTCP:LISTEN', '-t'], { encoding: 'utf8' }).trim().split('\n')[0] || null;
  } catch { return null; }
}

function cwdOf(pid) {
  try {
    return execFileSync('lsof', ['-a', '-p', pid, '-d', 'cwd', '-Fn'], { encoding: 'utf8' }).split('\n').find(l => l.startsWith('n'))?.slice(1);
  } catch { return null; }
}

/** appRun stops on a key press on stdin, so stdin stays a pipe until stopServer(). */
async function startServer(port, baseUrl) {
  if (listener(port)) throw new Error(`port ${port} is already in use; pick another with --port`);
  fs.mkdirSync(path.dirname(SERVER_LOG), { recursive: true });
  const log = fs.openSync(SERVER_LOG, 'w');
  const child = spawn('bash', ['./gradlew', 'appRun', '--no-daemon', `-PhttpPort=${port}`, '--console=plain'], {
    cwd: ZKTHEMELAB_DIR,
    env: { ...process.env, JAVA_TOOL_OPTIONS: SERVER_JAVA_OPTIONS },
    stdio: ['pipe', log, log],
  });
  let exited = false;
  child.on('exit', () => { exited = true; });
  const deadline = Date.now() + SERVER_START_TIMEOUT_MS;
  while (await status(`${baseUrl}/smoke.zul`) !== 200) {
    if (exited) throw new Error(`preview server exited before serving; see ${SERVER_LOG}`);
    if (Date.now() > deadline) { await stopServer(child, port); throw new Error(`preview server not up after 15 min; see ${SERVER_LOG}`); }
    await sleep(2000);
  }
  // HtmlPageRenders writes z$rod:false into the page only when client ROD is off.
  const html = await (await fetch(`${baseUrl}/smoke.zul`)).text();
  if (!html.includes('z$rod:false')) {
    await stopServer(child, port);
    throw new Error('preview server is serving with client ROD on; the index would miss every pop-up class');
  }
  return child;
}

async function stopServer(child, port) {
  child.stdin.end('\n');
  for (let i = 0; i < 60 && listener(port); i++) await sleep(1000);
  const pid = listener(port);
  // gretty's Jetty JVM can outlive appRun; kill it only if it is this zkthemelab's.
  if (pid && cwdOf(pid) === fs.realpathSync(ZKTHEMELAB_DIR)) process.kill(Number(pid));
  else if (pid) console.error(`port ${port} is still held by pid ${pid}, which is not this server; left running`);
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
  return execFileSync('git', ['rev-parse', '--short=10', 'HEAD'], { cwd: ZKTHEMELAB_DIR }).toString().trim();
}

function collectZClasses() {
  const found = new Set();
  document.querySelectorAll('*').forEach(el => el.classList.forEach(c => { if (c.startsWith('z-')) found.add(c); }));
  return [...found].sort();
}

async function main() {
  const port = parsePort(process.argv.slice(2));
  const baseUrl = `http://127.0.0.1:${port}`;
  const server = await startServer(port, baseUrl);
  try {
    await indexPages(baseUrl);
  } finally {
    await stopServer(server, port);
  }
}

async function indexPages(baseUrl) {
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
    throw new Error(`class index NOT written: ${failed.length} page(s) failed\n`
      + failed.map(f => `  ${f}: ${errors[f]}`).join('\n'));
  }
  const index = { generatedAt: new Date().toISOString(), commit: currentCommit(), pages };
  fs.writeFileSync(OUTPUT_FILE, JSON.stringify(index, null, 1) + '\n');
  console.log(`wrote ${path.relative(process.cwd(), OUTPUT_FILE)}: ${Object.keys(pages).length} pages`);
}

main().catch(e => {
  console.error(e.message);
  process.exit(1);
});
