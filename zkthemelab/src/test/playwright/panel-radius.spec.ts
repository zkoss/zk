import { test, expect } from '@playwright/test';

// Guard for zul panel.css (ZK-6112): every Panel uses --zk-panel-radius. ZK adds
// .z-panel-noframe for border="none" (the default) and border="normal", and a rule on that class
// used to reset the radius to 0, so only border="rounded" was rounded.
// Fixture: web/pv/panel-radius-guard.zul

const FIXTURE = '/pv/panel-radius-guard.zul';

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

for (const sclass of ['border-default', 'border-normal', 'border-rounded']) {
  test(`${sclass}: the panel is rounded with --zk-panel-radius`, async ({ page }) => {
    const panel = page.locator(`.${sclass}`);
    if (sclass !== 'border-rounded') await expect(panel).toHaveClass(/\bz-panel-noframe\b/);
    const { radius, token } = await panel.evaluate((el) => ({
      radius: getComputedStyle(el).borderTopLeftRadius,
      token: getComputedStyle(el).getPropertyValue('--zk-panel-radius').trim(),
    }));
    expect(radius).not.toBe('0px');
    expect(radius).toBe(token);
  });
}
