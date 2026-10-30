import { test, expect } from '@playwright/test';

// Guard for goldenlayout's stylesheet (ZK-6112). Its css-uri `css/goldenlayout.css.dsp` resolves
// against the widget package zkmax.goldenlayout (moved out of zkmax.layout in ZK-4966), so the CSS
// comes from js/zkmax/goldenlayout/css/goldenlayout.css; the old js/zkmax/layout/css stub was dropped
// from scripts/build-css.js. The page must load without a failed request and with that CSS applied.

test('goldenlayout loads its CSS without a failed request', async ({ page }) => {
  const failed: string[] = [];
  page.on('response', (r) => { if (r.status() >= 400) failed.push(`${r.status()} ${r.url()}`); });
  const resp = await page.goto('/goldenlayout.zul', { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  expect(failed).toEqual([]);
  // --_gl-icon-x is declared only in goldenlayout.css.
  const icon = await page.locator('.z-goldenlayout').first()
    .evaluate((el) => getComputedStyle(el).getPropertyValue('--_gl-icon-x').trim());
  expect(icon).not.toBe('');
});
