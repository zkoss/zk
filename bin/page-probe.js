#!/usr/bin/env node
// page-probe.js — open one URL in headless Chromium and report what the browser actually loaded.
//
// Usage:  NODE_PATH=zkthemelab/node_modules node bin/page-probe.js <url>
//         (run from the zk root; `playwright` is resolved from zkthemelab, so `npm ci` there must have run)
//
// Prints one JSON object:
//   status       HTTP status of the navigation response
//   sheets       document.styleSheets hrefs in cascade order ("(inline)" for <style> blocks)
//   zkVarDecls   number of `--zk-*` custom-property declarations across every loaded rule,
//                including rules nested in @media / @layer / @supports blocks
//   resetIndex   position of the first sheet whose href path ends in reset.css or reset-embed.css, before any
//                ;jsessionid or query (−1 if none — ZK appends ;jsessionid on a cookie-less first response)
//   wcsIndex     position of the zk.wcs sheet (−1 if none)
//
// Why in-page: the marble-theme skill's verification reference — a theme that never loaded gives a
// perfect zero diff, `zk.wcs` may be served empty on a repeated request, and body's font is Times
// even on a healthy page. Only `document.styleSheets` says whether Marble's variables reached the
// page, and only its order proves the reset precedes zk.wcs (execution plan item 2.1, E1/E3).
'use strict';
const { chromium } = require('playwright');

const url = process.argv[2];
if (!url) {
  console.error('usage: node page-probe.js <url>');
  process.exit(2);
}

(async () => {
  const browser = await chromium.launch();
  try {
    const page = await browser.newPage();
    const response = await page.goto(url, { waitUntil: 'load', timeout: 60000 });
    const inPage = await page.evaluate(() => {
      const sheets = [...document.styleSheets].map(s => s.href || '(inline)');
      let zkVarDecls = 0;
      const walk = rules => {
        for (const rule of rules) {
          if (rule.style) {
            for (let i = 0; i < rule.style.length; i++) {
              if (rule.style[i].startsWith('--zk-')) zkVarDecls++;
            }
          }
          if (rule.cssRules) walk(rule.cssRules);
        }
      };
      for (const sheet of document.styleSheets) {
        let rules;
        try { rules = sheet.cssRules; } catch (e) { continue; } // cross-origin sheet: not ours
        if (rules) walk(rules);
      }
      const resetIndex = sheets.findIndex(h => /\/reset(-embed)?\.css(;|\?|$)/.test(h));
      const wcsIndex = sheets.findIndex(h => /\/zk\.wcs(;|\?|$)/.test(h));
      return { title: document.title, sheets, zkVarDecls, resetIndex, wcsIndex };
    });
    console.log(JSON.stringify({ url, status: response ? response.status() : null, ...inPage }, null, 2));
  } finally {
    await browser.close();
  }
})().catch(err => {
  console.error(String(err && err.stack || err));
  process.exit(1);
});
