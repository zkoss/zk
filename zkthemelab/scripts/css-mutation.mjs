#!/usr/bin/env node
// Sampled CSS mutation testing: does a test fail when a theme declaration breaks?
//
// A covered component (component-feature-matrix.mjs) only proves a test opens it. Here each
// mutation in doc/css-mutations.json replaces one declaration in the CSS the preview server sends,
// and the tests named for it must fail ("killed"). A mutation they survive marks a declaration no
// test really checks.
//
// No rebuild: a local proxy in front of the preview server rewrites every CSS response (the served
// CSS is minified, so `find` is written in that form and must occur at most once per stylesheet). Each mutation's tests first run unmutated through the proxy and must pass.
//
// Run from zkthemelab/, with the preview server up:
//   node scripts/css-mutation.mjs [--target http://127.0.0.1:8136] [--only <id>,<id>]
// Output: doc/css-mutation-report.md (generated). Exits 1 when a mutation survives or is invalid.

import fs from 'node:fs';
import http from 'node:http';
import path from 'node:path';
import zlib from 'node:zlib';
import { spawn } from 'node:child_process';
import { fileURLToPath } from 'node:url';

const ZKTHEMELAB_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const MUTATIONS_FILE = path.join(ZKTHEMELAB_DIR, 'doc/css-mutations.json');
const REPORT_FILE = path.join(ZKTHEMELAB_DIR, 'doc/css-mutation-report.md');
const PLAYWRIGHT_CONFIG = 'src/test/playwright/playwright.config.ts';

function parseArgs(argv) {
  const opts = { target: process.env.PREVIEW_URL ?? 'http://127.0.0.1:8085', only: null };
  for (let i = 0; i < argv.length; i++) {
    if (argv[i] === '--target') opts.target = argv[++i];
    else if (argv[i] === '--only') opts.only = new Set(argv[++i].split(','));
    else throw new Error(`unknown argument ${argv[i]}`);
  }
  return opts;
}

/** Reverse proxy that applies the active mutation to CSS responses and counts its hits. */
function startProxy(target) {
  const upstream = new URL(target);
  const state = { mutation: null, hits: 0, multi: false };
  const server = http.createServer((req, res) => {
    const headers = { ...req.headers, host: upstream.host };
    delete headers['accept-encoding']; // ask for a plain body; a compressed one is still decoded below
    const up = http.request({ hostname: upstream.hostname, port: upstream.port, path: req.url, method: req.method, headers }, (upRes) => {
      const type = upRes.headers['content-type'] ?? '';
      if (!state.mutation || !/css/.test(type)) {
        res.writeHead(upRes.statusCode, upRes.headers);
        upRes.pipe(res);
        return;
      }
      const chunks = [];
      upRes.on('data', c => chunks.push(c));
      upRes.on('end', () => {
        let body = Buffer.concat(chunks);
        const enc = upRes.headers['content-encoding'];
        if (enc === 'gzip') body = zlib.gunzipSync(body);
        else if (enc === 'br') body = zlib.brotliDecompressSync(body);
        else if (enc === 'deflate') body = zlib.inflateSync(body);
        let text = body.toString('utf8');
        const { find, replace } = state.mutation;
        const n = text.split(find).length - 1;
        if (n > 1) state.multi = true;
        if (n) { state.hits += n; text = text.split(find).join(replace); }
        const out = Buffer.from(text, 'utf8');
        const outHeaders = { ...upRes.headers, 'content-length': String(out.length), 'cache-control': 'no-store' };
        delete outHeaders['content-encoding'];
        delete outHeaders['transfer-encoding'];
        delete outHeaders.etag;
        res.writeHead(upRes.statusCode, outHeaders);
        res.end(out);
      });
    });
    up.on('error', (e) => { res.writeHead(502); res.end(String(e)); });
    req.pipe(up);
  });
  return new Promise(resolve => server.listen(0, '127.0.0.1', () => resolve({ server, state, url: `http://127.0.0.1:${server.address().port}` })));
}

/** Runs the mutation's tests through the proxy. Asynchronous: the proxy lives in this process. */
function runTests(proxyUrl, run) {
  const args = ['playwright', 'test', '-c', PLAYWRIGHT_CONFIG, '--update-snapshots=none', '--reporter=json',
    ...run.projects.map(p => `--project=${p}`), ...(run.grep ? ['-g', run.grep] : [])];
  return new Promise((resolve) => {
    const child = spawn('npx', args, { cwd: ZKTHEMELAB_DIR, env: { ...process.env, PREVIEW_URL: proxyUrl } });
    let stdout = '';
    child.stdout.on('data', d => { stdout += d; });
    child.stderr.resume();
    child.on('close', (code) => {
      let report = null;
      try { report = JSON.parse(stdout); } catch { /* no JSON: Playwright failed before running */ }
      const failed = [];
      const walk = s => {
        (s.specs ?? []).forEach(sp => sp.tests.forEach(t => {
          if (t.results.some(x => x.status !== 'passed' && x.status !== 'skipped')) failed.push(`${t.projectName} › ${sp.title}`);
        }));
        (s.suites ?? []).forEach(walk);
      };
      (report?.suites ?? []).forEach(walk);
      resolve({ ok: code === 0, expected: report?.stats?.expected ?? 0, unexpected: report?.stats?.unexpected ?? 0, failed });
    });
  });
}

async function main() {
  const opts = parseArgs(process.argv.slice(2));
  const { mutations } = JSON.parse(fs.readFileSync(MUTATIONS_FILE, 'utf8'));
  const selected = mutations.filter(m => !opts.only || opts.only.has(m.id));
  const proxy = await startProxy(opts.target);
  const results = [];
  try {
    for (const m of selected) {
      process.stdout.write(`${m.id}: control ... `);
      proxy.state.mutation = null;
      const control = await runTests(proxy.url, m.run);
      if (!control.ok || control.expected === 0) {
        console.log(`INVALID (unmutated run: ${control.expected} passed, ${control.unexpected} failed)`);
        results.push({ m, verdict: 'invalid', note: `the unmutated run did not pass (${control.expected} passed, ${control.unexpected} failed)` });
        continue;
      }
      process.stdout.write(`${control.expected} passed; mutated ... `);
      proxy.state.mutation = m;
      proxy.state.hits = 0;
      proxy.state.multi = false;
      const mutated = await runTests(proxy.url, m.run);
      const hits = proxy.state.hits;
      proxy.state.mutation = null;
      let verdict;
      let note;
      if (hits === 0) { verdict = 'invalid'; note = '`find` matched no served CSS'; }
      else if (proxy.state.multi) { verdict = 'invalid'; note = '`find` occurs more than once in one stylesheet'; }
      else if (!mutated.ok) { verdict = 'killed'; note = `${mutated.unexpected} of ${control.expected} failed: ${mutated.failed.slice(0, 3).join('; ')}${mutated.failed.length > 3 ? '; …' : ''}`; }
      else { verdict = 'survived'; note = `all ${mutated.expected} still pass`; }
      console.log(`${verdict.toUpperCase()} (${note})`);
      results.push({ m, verdict, note, tests: control.expected });
    }
  } finally {
    proxy.server.close();
  }

  const out = ['# CSS mutation report', '',
    'Generated by `zkthemelab/scripts/css-mutation.mjs` from `doc/css-mutations.json`; do not edit by hand.', '',
    '`killed` = a test failed on the mutated CSS; `survived` = every test still passed, so no test checks that declaration; `invalid` = the mutation did not apply or its tests do not pass unmutated.', '',
    '| id | area | mutation | tests run | verdict | detail |', '|---|---|---|---:|---|---|'];
  const cell = s => String(s).replace(/\|/g, '\\|');
  for (const r of results) {
    out.push(`| ${r.m.id} | ${r.m.area} | ${cell(r.m.description)} | ${r.tests ?? ''} | ${r.verdict} | ${cell(r.note)} |`);
  }
  out.push('');
  if (!opts.only) fs.writeFileSync(REPORT_FILE, out.join('\n'));
  const bad = results.filter(r => r.verdict !== 'killed').length;
  console.log(`css-mutation: ${results.length - bad}/${results.length} killed${opts.only ? ' (--only: report not written)' : ` -> ${path.relative(ZKTHEMELAB_DIR, REPORT_FILE)}`}`);
  process.exit(bad ? 1 : 0);
}

main();
