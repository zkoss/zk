import { test, expect } from '@playwright/test';

// Guard for the paging preview page (paging.zul): every default-mold paging shows First / Last, and
// the detailed one shows its detail. Paging.onSize hides First / Last, and the detail when it
// overlaps the controls, on a paging no wider than its controls plus 50px; the page gives these
// pagings a width so they show what their captions describe. The tablet buttons are larger, so the
// detailed paging is checked under the tablet UA as well.

const PAGE = '/paging.zul';
const DEFAULT_MOLD = '.z-paging:not(.z-paging-os)';
const DEFAULT_MOLD_COUNT = 5;

async function checkPagings(page: import('@playwright/test').Page) {
  const resp = await page.goto(PAGE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  const pagings = page.locator(DEFAULT_MOLD);
  await expect(pagings).toHaveCount(DEFAULT_MOLD_COUNT);
  for (let i = 0; i < DEFAULT_MOLD_COUNT; i++) {
    const paging = pagings.nth(i);
    await expect(paging.locator('.z-paging-first'), `paging ${i} First`).toBeVisible();
    await expect(paging.locator('.z-paging-last'), `paging ${i} Last`).toBeVisible();
  }
  // The detailed paging (the first one) shows its detail beside the controls, not over them.
  const detailed = pagings.first();
  const info = detailed.locator('.z-paging-info');
  await expect(info).toBeVisible();
  const [controls, detail] = await Promise.all([
    detailed.locator('> ul').boundingBox(),
    info.boundingBox(),
  ]);
  expect(detail!.x, 'detail starts after the controls').toBeGreaterThanOrEqual(controls!.x + controls!.width);
}

test('default-mold pagings show First / Last and the detailed one its detail', async ({ page }) => {
  await checkPagings(page);
});

// Tagged @tablet: the `paging-preview` project skips it, `paging-preview-tablet` runs only it.
test('default-mold pagings show First / Last and the detailed one its detail on a tablet @tablet', async ({ page }) => {
  await checkPagings(page);
});
