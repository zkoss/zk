import { test, expect, Page } from '@playwright/test';

// Guard for zkmax chosenbox.css (ZK-6112): an idle inplace Chosenbox shows no field outline or
// fill, like the textbox and combobox inplace rules; clicking it brings the field back.
// Fixture: web/pv/chosenbox-inplace-guard.zul

const FIXTURE = '/pv/chosenbox-inplace-guard.zul';
const TRANSPARENT = 'rgba(0, 0, 0, 0)';

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

const fieldOf = (page: Page) => page.locator('.inplace').evaluate((el) => {
  const s = getComputedStyle(el);
  return { border: s.borderTopColor, bg: s.backgroundColor, shadow: s.boxShadow };
});

test('idle: no outline or fill, also on hover', async ({ page }) => {
  await expect(page.locator('.inplace > .z-chosenbox-inplace')).toBeVisible();
  expect(await fieldOf(page)).toEqual({ border: TRANSPARENT, bg: TRANSPARENT, shadow: 'none' });
  await page.locator('.inplace').hover();
  expect(await fieldOf(page)).toEqual({ border: TRANSPARENT, bg: TRANSPARENT, shadow: 'none' });
});

test('editing: the field outline comes back', async ({ page }) => {
  await page.locator('.inplace').click();
  await expect(page.locator('.inplace > .z-chosenbox-inplace')).toBeHidden();
  expect((await fieldOf(page)).border).not.toBe(TRANSPARENT);
});
