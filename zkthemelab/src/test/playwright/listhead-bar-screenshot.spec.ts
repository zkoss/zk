import { test, expect, Page } from '@playwright/test';

// Scrollbar-column header cell (`th.z-listhead-bar`, Jess review batch 14 S4).
//
// ZK adds one extra header cell above a listbox's vertical scrollbar. It must be
// painted like every other header cell, not as a tinted bar. The cell has width 0
// unless a classic (non-overlay) scrollbar takes space, and headless Chromium hides
// scrollbars by default — so this spec launches with scrollbars shown, and asserts
// the column's width > 0 first so it can never pass vacuously.
//
// File name: it ends in `screenshot.spec.ts` on purpose. playwright.config.ts maps
// only /screenshot\.spec\.ts/ to the `chromium` project, and that regex is not
// anchored; this spec takes no screenshots.
test.use({ launchOptions: { ignoreDefaultArgs: ['--hide-scrollbars'] } });

test.describe('listhead scrollbar column', () => {
  // Compare the bar cell with the first ordinary header cell of the same header row.
  async function barVsHeader(page: Page, listbox: ReturnType<Page['locator']>) {
    return listbox.evaluate((lb) => {
      const bar = lb.querySelector('th.z-listhead-bar') as HTMLElement;
      const sibling = lb.querySelector('th.z-listheader') as HTMLElement;
      return {
        barWidth: bar.getBoundingClientRect().width,
        barBg: getComputedStyle(bar).backgroundColor,
        siblingBg: getComputedStyle(sibling).backgroundColor,
      };
    });
  }

  test('listbox-header.zul scrolling listbox: bar cell matches sibling header', async ({ page }) => {
    await page.goto('/listbox-header.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    // Pick the first listbox whose body really shows a vertical scrollbar.
    const idx = await page.evaluate(() =>
      Array.from(document.querySelectorAll('.z-listbox')).findIndex((lb) => {
        const body = lb.querySelector('.z-listbox-body') as HTMLElement | null;
        return !!body && body.offsetWidth > body.clientWidth;
      }));
    expect(idx, 'a listbox with a vertical scrollbar must exist on listbox-header.zul').toBeGreaterThanOrEqual(0);
    const m = await barVsHeader(page, page.locator('.z-listbox').nth(idx));
    expect(m.barWidth, 'scrollbar column must exist (width > 0), else the test is vacuous').toBeGreaterThan(0);
    expect(m.barBg, 'th.z-listhead-bar background must equal a sibling header cell').toBe(m.siblingBg);
  });

  test('bandbox.zul bandpopup listbox: bar cell matches sibling header', async ({ page }) => {
    await page.goto('/bandbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    const example = page.locator('div:has(> .z-label:text-is("Listbox in bandpopup"))');
    await example.locator('.z-bandbox-button').click();
    const listbox = page.locator('.z-bandpopup .z-listbox:visible');
    await expect(listbox).toBeVisible();
    // ZK sizes the bar cell after the popup is shown and the body is synced.
    await expect.poll(async () => (await barVsHeader(page, listbox)).barWidth, { timeout: 5000 }).toBeGreaterThan(0);
    const m = await barVsHeader(page, listbox);
    expect(m.barWidth, 'scrollbar column must exist (width > 0), else the test is vacuous').toBeGreaterThan(0);
    expect(m.barBg, 'th.z-listhead-bar background must equal a sibling header cell').toBe(m.siblingBg);
  });
});
