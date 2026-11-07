import { test, expect } from '@playwright/test';

// Guard for zul a.css (ZK-6112): the icon/label gap of an A is a flex gap, so it only appears
// between an icon and a label. An icon-only A is as wide as its icon (a margin on the icon left
// 8px of trailing space), and dir="reverse" still separates the label from the icon.
// Fixture: web/pv/a-icon-only-guard.zul

const FIXTURE = '/pv/a-icon-only-guard.zul';
const MIN_ICON_GAP_PX = 4;

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

test('an icon-only A is as wide as its icon', async ({ page }) => {
  const link = page.locator('.icon-only');
  const linkBox = (await link.boundingBox())!;
  const iconBox = (await link.locator('[class*="z-icon-"]').boundingBox())!;
  expect(Math.abs(linkBox.width - iconBox.width)).toBeLessThanOrEqual(0.5);
});

for (const [sclass, iconFirst] of [['labelled', true], ['reversed', false]] as const) {
  test(`${sclass}: the label keeps its gap from the icon`, async ({ page }) => {
    const gap = await page.locator(`.${sclass}`).evaluate((a, iconFirst) => {
      const icon = a.querySelector('[class*="z-icon-"]')!.getBoundingClientRect();
      const range = document.createRange();
      range.selectNodeContents(a);
      range.setStartAfter(a.querySelector('[class*="z-icon-"]')!);
      if (!iconFirst) {
        range.setStart(a, 0);
        range.setEndBefore(a.querySelector('[class*="z-icon-"]')!);
      }
      const label = range.getBoundingClientRect();
      return iconFirst ? label.left - icon.right : icon.left - label.right;
    }, iconFirst);
    expect(gap).toBeGreaterThanOrEqual(MIN_ICON_GAP_PX);
  });
}
