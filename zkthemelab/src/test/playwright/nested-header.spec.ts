import { test, expect } from '@playwright/test';

// Guard for zul window.css and groupbox.css: the "no header" state of an outer container must not
// reach a titled container nested inside it. ZK adds z-window-noheader / z-groupbox-notitle only
// when the container renders no header, so a descendant rule on that class can only ever hit a
// nested container.
// Fixture: web/pv/nested-header-guard.zul

const FIXTURE = '/pv/nested-header-guard.zul';

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

test('window — a titled window nested in a header-less window keeps its title bar', async ({ page }) => {
  const inner = page.locator('.z-window-noheader .z-window .z-window-header');
  await expect(inner).toHaveCount(1);
  expect(await inner.evaluate((el) => getComputedStyle(el).display)).not.toBe('none');
});

test('groupbox — a captioned groupbox nested in a title-less groupbox keeps its header', async ({ page }) => {
  const inner = page.locator('.z-groupbox-notitle .z-groupbox .z-groupbox-header');
  await expect(inner).toHaveCount(1);
  expect(await inner.evaluate((el) => getComputedStyle(el).display)).not.toBe('none');
});

test('groupbox — a captioned groupbox nested in a title-less groupbox keeps its square top corners', async ({ page }) => {
  const content = page.locator('.z-groupbox-notitle .z-groupbox .z-groupbox-content');
  await expect(content).toHaveCount(1);
  expect(await content.evaluate((el) => getComputedStyle(el).borderTopLeftRadius)).toBe('0px');
});
