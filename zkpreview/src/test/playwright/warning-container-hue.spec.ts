import { test, expect, Page } from '@playwright/test';

// Guard for tokens/_colors.css `--zk-color-warning-container` (ZK-6112, zkdemo B7 Z6): the container
// turns the warning seed hue 35° toward yellow so a warning notification no longer reads as the same
// peach as an error one. With the seed's own hue the two sat 13° apart.
// Fixture: web/pv/warning-container-guard.zul

const FIXTURE = '/pv/warning-container-guard.zul';
const MIN_HUE_GAP = 30;
const HUE_TURN = 35;
const TOLERANCE_DEG = 1;

// Chrome serialises a relative oklch() colour as `oklch(L C H)`.
function oklch(css: string): { l: number; c: number; h: number } {
  const m = css.match(/^oklch\(([\d.]+) ([\d.]+) ([\d.]+)/);
  expect(m, `not an oklch() colour: ${css}`).not.toBeNull();
  return { l: Number(m![1]), c: Number(m![2]), h: Number(m![3]) };
}

async function open(page: Page, css?: string) {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  if (css) await page.addStyleTag({ content: css });
  await expect(page.locator('.z-notification-warning .z-notification-content')).toBeVisible();
  await expect(page.locator('.z-notification-error .z-notification-content')).toBeVisible();
}

const bg = (page: Page, type: string) => page.locator(`.z-notification-${type} .z-notification-content`)
  .evaluate((el) => getComputedStyle(el).backgroundColor);

test('warning and error notification containers are at least 30° apart in hue', async ({ page }) => {
  await open(page);
  const warning = oklch(await bg(page, 'warning'));
  const error = oklch(await bg(page, 'error'));
  expect(warning.h - error.h, `warning h ${warning.h}, error h ${error.h}`).toBeGreaterThanOrEqual(MIN_HUE_GAP);
});

test('an overridden warning seed still drives the container, 35° further toward yellow', async ({ page }) => {
  await open(page, ':root { --zk-color-warning: #2e7d32; }');
  const seed = oklch(await page.evaluate(() => {
    const probe = document.createElement('span');
    probe.style.color = 'oklch(from #2e7d32 l c h)';
    document.body.append(probe);
    return getComputedStyle(probe).color;
  }));
  const warning = oklch(await bg(page, 'warning'));
  expect(Math.abs(warning.h - (seed.h + HUE_TURN)), `seed h ${seed.h}, container h ${warning.h}`)
    .toBeLessThanOrEqual(TOLERANCE_DEG);
});
