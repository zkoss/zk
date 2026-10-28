import { test, expect, Page } from '@playwright/test';

// Regression for the stacking scale added in
// src/main/resources/web/zul/css/tokens/_zindex.css (--zk-index-* tokens) and
// the .z-index-* utilities in utility/_layout.css, demoed on
// src/test/resources/web/utility/zindex.zul.
//
// The point of this scale is NOT to control ZK's floating widgets — ZK stamps
// those inline at runtime (base 1800). So we only assert the LOAD-BEARING side:
//   (a) each .z-index-* utility resolves to its token's numeric value, and
//   (b) the three hard constraints on the token ladder hold (see the spec).
//
// getComputedStyle().zIndex returns the numeric value for a positioned element
// (the demo chips all carry .z-position-relative). Token :root values are read
// straight off document.documentElement.
//
// See doc/spec/zindex-scale.md.

const URL = '/utility/zindex.zul';

async function open(page: Page) {
  await page.goto(URL, { waitUntil: 'domcontentloaded' });
  await page.locator('.z-index-nav').first().waitFor({ state: 'attached' });
}

function zIndexOf(page: Page, selector: string): Promise<string> {
  return page.locator(selector).first().evaluate(n => getComputedStyle(n).zIndex);
}

function token(page: Page, name: string): Promise<number> {
  return page.evaluate(
    n => parseInt(getComputedStyle(document.documentElement).getPropertyValue(n), 10),
    name
  );
}

test.describe('stacking scale', () => {
  test('.z-index-* utilities resolve to their token values', async ({ page }) => {
    await open(page);
    expect(await zIndexOf(page, '.z-index-nav')).toBe('1000');
    expect(await zIndexOf(page, '.z-index-float-fallback')).toBe('1800');
    expect(await zIndexOf(page, '.z-index-loading')).toBe('1450');
    expect(await zIndexOf(page, '.z-index-loadingbar')).toBe('2000');
    expect(await zIndexOf(page, '.z-index-error')).toBe('9999999');
  });

  test('token ladder honors the three hard constraints', async ({ page }) => {
    await open(page);
    const busyMask = await token(page, '--zk-index-busy-mask');
    const busyLoading = await token(page, '--zk-index-busy-loading');
    const loading = await token(page, '--zk-index-loading');
    const error = await token(page, '--zk-index-error');
    const loadingbar = await token(page, '--zk-index-loadingbar');

    // 1. busy scrim must sit below its own spinner
    expect(busyMask).toBeLessThan(busyLoading);
    // 2. global loading must sit above the modal-mask baseline (1400)
    expect(loading).toBeGreaterThan(1400);
    // 3. the fatal-error box must outrank everything else in the scale
    expect(error).toBeGreaterThan(Math.max(busyLoading, loadingbar));
  });
});
