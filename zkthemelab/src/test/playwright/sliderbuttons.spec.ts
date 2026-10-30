import { test, expect, type Page } from '@playwright/test';

// Guard for the shared Sliderbuttons sub-widget (zkex), rendered by rangeslider (zkex) and multislider
// (zkmax). Like IceBlue, its base rules (container, tooltip, pressed thumb) live in
// zkex/slider/css/sliderbuttons.css, loaded through the sliderbuttons css-uri; rangeslider.css and
// multislider.css only style their own thumbs and areas, scoped under their root (ZK-6112).
// Fixture: web/pv/sliderbuttons-guard.zul (both sliders); /multislider.zul has multisliders only.

const FIXTURE = '/pv/sliderbuttons-guard.zul';
const RED = 'rgb(255, 0, 0)';
const BLUE = 'rgb(0, 0, 255)';

async function open(page: Page, url: string) {
  const resp = await page.goto(url, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
}

for (const [comp, url] of [['rangeslider', FIXTURE], ['multislider', FIXTURE], ['multislider', '/multislider.zul']]) {
  test(`${comp} on ${url}: tooltip hidden at rest, shown on hover, thumb grows while pressed`, async ({ page }) => {
    await open(page, url);
    const thumb = page.locator(`.z-${comp} .z-sliderbuttons-button`).first();
    const tooltip = page.locator(`.z-${comp} .z-sliderbuttons-tooltip`).first();
    await expect(tooltip).toBeHidden();

    await thumb.hover();
    await expect(tooltip).toBeVisible();

    await page.mouse.down();
    await expect.poll(() => thumb.evaluate((el) => getComputedStyle(el).transform)).not.toBe('none');
    await page.mouse.up();
  });
}

test('each slider keeps its own accent on the shared thumb and area', async ({ page }) => {
  await open(page, FIXTURE);
  await page.addStyleTag({ content: `:root{--zk-rangeslider-accent:${RED};--zk-multislider-accent:${BLUE}}` });
  const bg = (sel: string) => page.locator(sel).first().evaluate((el) => getComputedStyle(el).backgroundColor);
  expect(await bg('.z-rangeslider .z-sliderbuttons-button')).toBe(RED);
  expect(await bg('.z-rangeslider .z-sliderbuttons-area')).toBe(RED);
  expect(await bg('.z-multislider .z-sliderbuttons-button')).toBe(BLUE);
  expect(await bg('.z-multislider .z-sliderbuttons-area')).toBe(BLUE);
});
