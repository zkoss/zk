import { test, expect } from '@playwright/test';

// Guard for messagebox.css `.z-messagebox .z-label` (ZK-6112, zkdemo B7 Z1): the message label is
// multiline, so ZK renders "Line 1<br/>\nLine 2<br/>\nLine 3". With white-space: pre-wrap the "\n"
// breaks too and every line break doubles. Three lines must be three line-heights tall.
// Fixture: web/pv/messagebox-multiline-guard.zul

const FIXTURE = '/pv/messagebox-multiline-guard.zul';
const LINES = 3;
const TOLERANCE_PX = 1;

test('a three-line message is three line-heights tall', async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  const label = page.locator('.z-messagebox .z-label');
  await expect(label).toBeVisible();
  const { height, lineHeight } = await label.evaluate((el) => ({
    height: el.getBoundingClientRect().height,
    lineHeight: parseFloat(getComputedStyle(el).lineHeight),
  }));
  expect(Math.abs(height - LINES * lineHeight), `label height ${height}, line-height ${lineHeight}`)
    .toBeLessThanOrEqual(TOLERANCE_PX);
});
