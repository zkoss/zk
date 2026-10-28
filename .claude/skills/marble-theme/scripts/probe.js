#!/usr/bin/env node
/*
 * Computed-style + inline-style A/B probe against the running preview app.
 *
 * The engine of the empirical bar: measure a selector's computed values (and its
 * inline `style`, so you can see what ZK's JS actually wrote) BEFORE and AFTER
 * removing an `!important`. Identical output => the keyword was redundant =>
 * safe to remove. A changed value => load-bearing => keep.
 *
 * Requires @playwright/test to resolve. Run from the repo root, or export
 *   NODE_PATH=<repo>/node_modules
 * if invoking the script from elsewhere (e.g. a scratchpad copy).
 *
 * Usage:
 *   node probe.js <url> '<selector>' 'prop1,prop2,...' [waitSelector]
 * Example:
 *   node probe.js ${PREVIEW_URL}/progressmeter.zul \
 *     '.z-progressmeter-image' 'display,width,height'
 *
 * For BEHAVIORAL cases (open→dismiss a popup, collapse a panel, toggle a view,
 * mobile UA), this static probe is not enough — write a small bespoke Playwright
 * script that drives the interaction then measures. See the colorbox worked
 * example in SKILL.md for the pattern (chromium + devices['iPad …'] contexts).
 */
// zk: @playwright/test is installed under zkthemelab/ only (P2 item 2.3); let Node resolve it from there.
module.paths.push(require('path').resolve(__dirname, '../../../../zkthemelab/node_modules'));
const { chromium } = require('@playwright/test');

(async () => {
  const [, , url, selector, propsCsv, waitSel] = process.argv;
  if (!url || !selector) {
    console.error("usage: node probe.js <url> '<selector>' 'prop1,prop2' [waitSelector]");
    process.exit(2);
  }
  const props = (propsCsv || 'display').split(',');
  const b = await chromium.launch();
  const pg = await b.newPage({ viewport: { width: 1280, height: 900 } });
  await pg.goto(url, { waitUntil: 'networkidle', timeout: 30000 });
  try { await pg.waitForSelector(waitSel || selector, { timeout: 8000 }); } catch { /* keep going */ }
  const out = await pg.evaluate(({ selector, props }) => {
    return [...document.querySelectorAll(selector)].slice(0, 5).map(el => {
      const cs = getComputedStyle(el);
      const o = { inlineStyle: el.getAttribute('style') || '' };
      props.forEach(p => (o[p] = cs.getPropertyValue(p)));
      return o;
    });
  }, { selector, props });
  console.log(JSON.stringify({ url, selector, count: out.length, samples: out }, null, 2));
  await b.close();
})().catch(e => { console.error('PROBE_ERR', e.message); process.exit(1); });
