import { test, expect } from '@playwright/test';

// Guard for the z-sticky-header rules (ZK-4795, EE). Like IceBlue, Marble ships them only in zkmax:
// grid/css/grid.css, sel/css/listbox.css and sel/css/tree.css (ZK-6112). The preview app runs with
// zkmax, so the header must stay pinned to the top of its scroll container while the rows scroll.
// Fixture: web/pv/sticky-header-guard.zul

const FIXTURE = '/pv/sticky-header-guard.zul';

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

for (const comp of ['grid', 'listbox', 'tree']) {
  test(`${comp} header stays pinned while the rows scroll`, async ({ page }) => {
    const box = page.locator(`.z-div:has(> .z-${comp})`);
    const header = page.locator(`.z-${comp}.z-sticky-header .z-${comp}-header`);
    const boxTop = (await box.boundingBox())!.y;

    await box.hover();
    await page.mouse.wheel(0, 300);
    await expect.poll(() => box.evaluate((el) => el.scrollTop)).toBeGreaterThan(100);

    expect((await header.boundingBox())!.y).toBeCloseTo(boxTop, 0);
    // Opaque, so the scrolled rows do not show through the pinned header.
    const bg = await header.evaluate((el) => getComputedStyle(el).backgroundColor);
    expect(bg).not.toBe('rgba(0, 0, 0, 0)');
  });
}
