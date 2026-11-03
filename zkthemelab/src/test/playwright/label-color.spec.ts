import { test, expect } from '@playwright/test';

// Guard for Label colour: a label inherits its container's colour, so no theme rule may set a
// colour on .z-label. IceBlue sets none either (ZK-6112).
// Fixture: web/pv/label-color-guard.zul.

const FIXTURE = '/pv/label-color-guard.zul';
const RED = 'rgb(204, 0, 0)';
const DEFAULT_TEXT = 'rgba(0, 0, 0, 0.87)';

const colorOf = (page: import('@playwright/test').Page, selector: string) =>
  page.locator(selector).evaluate(el => getComputedStyle(el).color);

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

test('plain text follows the red container', async ({ page }) => {
  expect(await colorOf(page, '.z-label:has-text("Plain text")')).toBe(RED);
});

test('label follows the red container', async ({ page }) => {
  expect(await colorOf(page, '.z-label:has-text("Label in a red div")')).toBe(RED);
});

test('label in a container without a colour keeps the default text colour', async ({ page }) => {
  expect(await colorOf(page, '.z-label:has-text("Label in a default div")')).toBe(DEFAULT_TEXT);
});
