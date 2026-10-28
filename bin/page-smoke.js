#!/usr/bin/env node
// page-smoke.js — open every listed page in headless Chromium and report whether it composed.
//
// Usage:  NODE_PATH=zkthemelab/node_modules node bin/page-smoke.js <baseURL> <listfile> [concurrency]
//         (run from the zk root; `playwright` is resolved from zkthemelab, so `npm ci` there must have run)
//
// <baseURL> is the directory the page paths are relative to, WITH its trailing slash, e.g.
//   http://127.0.0.1:8085/zkthemelab/web/
// The paths are joined with `new URL(path, baseURL)`, never with a leading slash — a leading
// slash would drop the context path (execution plan gates/2.1.md, Planner notes).
//
// <listfile>: one page per line, `<relative path>[ strict]`; blank lines and `#` comments ignored.
//   every page  → HTTP 200 on the navigation response and, after `load`, at least one element
//                 whose class starts with `z-` (ZK composed something)
//   strict      → additionally the `.z-p-8` page wrapper is visible — the same assertion the
//                 template's own render-smoke.spec.ts makes for the pages it lists
//
// Prints one JSON object {baseURL, total, passed, failed, failures:[{path, status, reason}], seconds}
// and exits 1 if any page failed. Mirrors the template smoke on purpose: `load`, not `networkidle`
// (the SPA pages carry a live-reload <script> that keeps the network busy), and no error-dialog
// assertion — errorbox.zul and runtime-error.zul show error UI by design.
'use strict';
const fs = require('fs');
const { chromium } = require('playwright');

const [baseURL, listFile, conc] = process.argv.slice(2);
if (!baseURL || !listFile) {
  console.error('usage: node page-smoke.js <baseURL/> <listfile> [concurrency]');
  process.exit(2);
}
if (!baseURL.endsWith('/')) {
  console.error('baseURL must end with a slash so relative page paths resolve under it');
  process.exit(2);
}
const pages = fs.readFileSync(listFile, 'utf8').split('\n')
  .map((l) => l.trim()).filter((l) => l && !l.startsWith('#'))
  .map((l) => { const [p, flag] = l.split(/\s+/); return { path: p, strict: flag === 'strict' }; });
const concurrency = Math.max(1, parseInt(conc || '4', 10));

(async () => {
  const t0 = Date.now();
  const browser = await chromium.launch();
  const context = await browser.newContext({ viewport: { width: 1280, height: 720 } });
  const failures = [];
  let passed = 0;
  let next = 0;
  async function worker() {
    while (next < pages.length) {
      const { path, strict } = pages[next++];
      const url = new URL(path, baseURL).toString();
      const page = await context.newPage();
      try {
        const resp = await page.goto(url, { waitUntil: 'load', timeout: 60000 });
        const status = resp ? resp.status() : 0;
        if (status !== 200) { failures.push({ path, status, reason: `HTTP ${status}` }); continue; }
        const zCount = await page.evaluate(() => document.querySelectorAll('[class^="z-"], [class*=" z-"]').length);
        if (zCount === 0) { failures.push({ path, status, reason: 'no z-* element after load' }); continue; }
        if (strict) {
          const wrapper = page.locator('.z-p-8').first();
          const visible = await wrapper.isVisible().catch(() => false);
          if (!visible) { failures.push({ path, status, reason: '.z-p-8 page wrapper not visible' }); continue; }
        }
        passed++;
      } catch (e) {
        failures.push({ path, status: 0, reason: String(e.message || e).split('\n')[0] });
      } finally {
        await page.close();
      }
    }
  }
  await Promise.all(Array.from({ length: concurrency }, worker));
  await browser.close();
  const out = { baseURL, total: pages.length, passed, failed: failures.length, failures, seconds: Math.round((Date.now() - t0) / 1000) };
  console.log(JSON.stringify(out, null, 2));
  process.exit(failures.length ? 1 : 0);
})().catch((e) => { console.error(e); process.exit(1); });
