import { test, expect } from '@playwright/test';

// Explicit size: a size the page sets on a component (an attribute, vflex) wins over the theme's default
// size. Each component keeps its own fixture and helpers inside its describe.

declare const zk: any;

// Guard for toolbarbutton.css (ZK-6112, ZK-2802): a button without an inline height grows with a large image,
// but an inline height (the height attribute, vflex) still sizes the button, as under IceBlue. A min-height
// on every button would keep these at 36px.
// Fixture: web/pv/toolbarbutton-height-guard.zul
test.describe('toolbarbutton', () => {
  const FIXTURE = '/pv/toolbarbutton-height-guard.zul';
  const IMAGE_HEIGHT = 72;
  // [widget id, what it is, expected height]
  const FIXED = [
    ['plain', 'a button without a height', 36],
    ['height24', 'height="24px"', 24],
    ['vflex', 'vflex in a 24px parent', 24],
    ['imageHeight24', 'a large image with height="24px"', 24],
  ] as const;

  const height = (page: import('@playwright/test').Page, id: string) =>
    page.evaluate((id) => zk.Widget.$('$' + id).$n().offsetHeight, id);

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
  });

  for (const [id, what, expected] of FIXED) {
    test(`toolbarbutton — ${what} is ${expected}px tall`, { tag: '@ZK-2802' }, async ({ page }) => {
      expect(await height(page, id)).toBe(expected);
    });
  }

  test('toolbarbutton — a button without a height grows taller than its large image', { tag: '@ZK-2802' }, async ({ page }) => {
    await expect.poll(() => height(page, 'image')).toBeGreaterThan(IMAGE_HEIGHT);
  });
});
