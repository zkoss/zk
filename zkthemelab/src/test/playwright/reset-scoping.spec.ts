import { test, expect } from '@playwright/test';

// Verifies the `org.zkoss.zul.theme.browserDefault` reset-scoping mechanism — the DSP-free
// replacement for ZK's legacy norm.css.dsp `<c:if>` (see doc/spec/reset-scoping.md).
//
// The theme serves its reset as its OWN stylesheet, swapped in Java by MarbleThemeProvider:
//   browserDefault=false (default) → reset.css       : global reset, <html>/<body> frame
//                                                       intact, NOT scoped
//   browserDefault=true            → reset-embed.css  : same widget reset confined to
//                                                       @scope(.z-page), <html>/<body> frame
//                                                       dropped (so a JS-Embed host page is
//                                                       never restyled)
// MarbleThemeProvider inserts the chosen file immediately BEFORE the ~./zul/css/zk.wcs widget
// bundle so the reset keeps its historical "loads first" cascade position.
//
// The preview app runs in the DEFAULT (false) mode, so we verify:
//   1. default wiring picks the GLOBAL variant and loads it ahead of zk.wcs (false branch, live);
//   2. BOTH built variants are correct — the embed variant is exactly what the provider swaps
//      in when the flag is true (its content is the other half of the property's contract).
//
// Requires the preview app on ${PREVIEW_URL}
//   withjdk.sh 17 mvn test exec:java@preview-app

const PAGE = '/button.zul';

// Resolved (absolute) href of the linked global reset.css — `/reset.css` literal does not
// match `/reset-embed.css`, so this only ever picks the global variant.
async function resetHref(page): Promise<string> {
  return page.$$eval('link[rel="stylesheet"]', (els: HTMLLinkElement[]) => {
    const m = els.map(e => e.href).find(h => /\/reset\.css(?:[;?]|$)/.test(h));
    return m || '';
  });
}

test.describe('reset scoping — org.zkoss.zul.theme.browserDefault', () => {
  test('default mode links the global reset (reset.css) ahead of the zk.wcs bundle', async ({ page }) => {
    await page.goto(PAGE, { waitUntil: 'domcontentloaded' });
    const hrefs = await page.$$eval('link[rel="stylesheet"]', (els: HTMLLinkElement[]) =>
      els.map(e => e.getAttribute('href') || '')
    );
    const resetIdx = hrefs.findIndex(h => /\/reset\.css(?:[;?]|$)/.test(h));
    const wcsIdx = hrefs.findIndex(h => /zk\.wcs/.test(h));

    expect(resetIdx, 'reset.css stylesheet link is present').toBeGreaterThanOrEqual(0);
    expect(wcsIdx, 'zk.wcs bundle link is present').toBeGreaterThanOrEqual(0);
    expect(resetIdx, 'reset.css must load before the zk.wcs widget bundle').toBeLessThan(wcsIdx);
    expect(
      hrefs.some(h => /reset-embed\.css/.test(h)),
      'reset-embed.css must NOT be linked when browserDefault=false'
    ).toBe(false);
  });

  test('global variant (reset.css) is unscoped and keeps the html/body frame', async ({ page, request }) => {
    await page.goto(PAGE, { waitUntil: 'domcontentloaded' });
    const href = await resetHref(page);
    expect(href, 'resolved reset.css URL').toBeTruthy();

    // Whitespace-tolerant: the running preview app serves the unminified dev build, the
    // packaged jar serves the minified build — both must satisfy these assertions.
    const css = await (await request.get(href)).text();
    expect(css).toMatch(/box-sizing:\s*border-box/);
    expect(css, 'page-frame <html> reset present').toMatch(/html\s*\{/);
    expect(css, 'page-frame <body> reset present').toMatch(/body\s*\{/);
    // No @scope BLOCK (the dev build keeps comments, and _reset.css mentions "@scope" in
    // prose — so match the at-rule's `( … ) {` block form, not the bare word).
    expect(css, 'global variant is NOT scoped').not.toMatch(/@scope\s*\([^)]*\)\s*\{/);
  });

  test('root keeps the browser-default font-size (the 14px override was removed)', async ({ page }) => {
    // _reset.css no longer sets `html { font-size }`, so the root stays at the browser default
    // (16px) — the rem base the theme's rem values are authored against.
    await page.goto(PAGE, { waitUntil: 'domcontentloaded' });
    expect(await page.evaluate(() => getComputedStyle(document.documentElement).fontSize)).toBe('16px');
  });

  test('extracted reset still takes effect in default mode (body margin 0, widgets border-box)', async ({ page }) => {
    // Cascade-parity guard: the reset used to live inside norm.css.dsp; now it is a separate
    // sheet loaded ahead of the bundle. Its rules must still win — body frame + box-sizing.
    await page.goto(PAGE, { waitUntil: 'networkidle' });
    expect(await page.evaluate(() => getComputedStyle(document.body).margin)).toBe('0px');
    const boxSizing = await page.locator('.z-button').first().evaluate(el => getComputedStyle(el).boxSizing);
    expect(boxSizing).toBe('border-box');
  });

  test('embed variant (reset-embed.css) is scoped to .z-page and drops the html/body frame', async ({ page, request }) => {
    await page.goto(PAGE, { waitUntil: 'domcontentloaded' });
    const href = await resetHref(page);
    expect(href, 'resolved reset.css URL').toBeTruthy();

    // Same folder, just the embed sibling (preserves any ;jsessionid suffix).
    const embedHref = href.replace('/reset.css', '/reset-embed.css');
    const res = await request.get(embedHref);
    expect(res.status(), 'reset-embed.css is served').toBe(200);

    // Whitespace-tolerant (dev build is unminified, packaged build is minified).
    const css = await res.text();
    expect(css, 'widget reset confined to the ZK subtree').toMatch(/@scope\s*\(\s*\.z-page\s*\)/);
    expect(css, 'box-sizing reset retained inside the scope').toMatch(/box-sizing:\s*border-box/);
    // The html{…}/body{…} blocks were the ONLY html/body rules, so the embed variant must
    // contain neither — proving the host page's frame is never touched.
    expect(css, 'no <html> frame reset leaks to the host').not.toMatch(/html\s*\{/);
    expect(css, 'no <body> frame reset leaks to the host').not.toMatch(/body\s*\{/);
  });
});

// The cascade layer order (zk-base < zk-components < zk-utilities) is declared by
// zul/css/_layer-order.css, the first file of the zk.wcs bundle, not in reset.css. reset.css only
// carries zk-base, the lowest layer, so loading it first cannot change the order — and a page
// without it (a custom ThemeProvider that does not insert it, or a failed request) still gets
// utilities above components.
test.describe('cascade layer order — declared by zk.wcs, not reset.css', () => {
  async function wcsHref(page): Promise<string> {
    return page.$$eval('link[rel="stylesheet"]', (els: HTMLLinkElement[]) =>
      els.map(e => e.href).find(h => /zk\.wcs/.test(h)) || '');
  }

  // The minifier rewrites the order statement into layer blocks emitted in that order, so check
  // the order the layer names first appear in — that is what the browser uses — not the text.
  test('zk.wcs creates the layers in order: base, components, utilities', async ({ page, request }) => {
    await page.goto(PAGE, { waitUntil: 'domcontentloaded' });
    const css = (await (await request.get(await wcsHref(page))).text()).replace(/\/\*[\s\S]*?\*\//g, '');
    const seen: string[] = [];
    for (const m of css.matchAll(/@layer\s+([\w\s,-]+?)\s*[{;]/g)) {
      for (const name of m[1].split(',').map(s => s.trim())) {
        if (!seen.includes(name)) seen.push(name);
      }
    }
    expect(seen).toEqual(['zk-base', 'zk-components', 'zk-utilities']);
  });

  test('reset.css carries no order statement of its own', async ({ page, request }) => {
    await page.goto(PAGE, { waitUntil: 'domcontentloaded' });
    const href = await resetHref(page);
    const css = (await (await request.get(href)).text()).replace(/\/\*[\s\S]*?\*\//g, '');
    expect(css, 'only the zk-base block').not.toMatch(/@layer\s+[\w-]+(?:\s*,\s*[\w-]+)*\s*;/);
  });

  test('utilities still beat components when reset.css never loads', async ({ page }) => {
    await page.route(/\/zul\/css\/reset\.css/, route => route.abort());
    await page.goto(PAGE, { waitUntil: 'networkidle' });
    // .z-button (zk-components) sets padding; .z-p-0 (zk-utilities) has the same specificity,
    // so only the layer order decides which one applies.
    const padding = await page.locator('.z-button').first().evaluate(el => {
      el.classList.add('z-p-0');
      return getComputedStyle(el).paddingLeft;
    });
    expect(padding, '.z-p-0 must override the button padding').toBe('0px');
  });
});
