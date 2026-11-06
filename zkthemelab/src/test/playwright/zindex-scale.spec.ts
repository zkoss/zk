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

// Guard for _log.css, the zk.log box that zk.ts doLog() creates (ZK-6112). doLog() sets no inline
// z-index, so the stylesheet's --zk-index-log is what stacks the box. Its close button carries
// .z-button, so the button's font-size rule must stay in zk-components: in zk-base it loses to
// .z-button and the button grows from body-small to the button's own size.
// Fixture: web/pv/zk-log-guard.zul

const FIXTURE = '/pv/zk-log-guard.zul';

test('the zk.log box is out of flow, stacked by --zk-index-log, with a body-small close button', async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  const box = page.locator('div.z-log');
  await expect(box).toBeVisible();
  const got = await box.evaluate((el) => {
    const root = getComputedStyle(document.documentElement);
    const probe = document.createElement('div');
    probe.style.fontSize = root.getPropertyValue('--zk-typescale-body-small-size');
    document.body.appendChild(probe);
    const bodySmall = getComputedStyle(probe).fontSize;
    probe.remove();
    return {
      position: getComputedStyle(el).position,
      zIndex: getComputedStyle(el).zIndex,
      logToken: root.getPropertyValue('--zk-index-log').trim(),
      buttonFont: getComputedStyle(el.querySelector('button')!).fontSize,
      bodySmall,
    };
  });
  expect(got.position).toBe('absolute');
  expect(got.logToken).not.toBe('');
  expect(got.zIndex).toBe(got.logToken);
  expect(got.buttonFont).toBe(got.bodySmall);
});
