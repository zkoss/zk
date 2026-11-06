import { test, expect, Page } from '@playwright/test';

// Colour: a component takes its container's text colour, and the notification containers keep
// distinct hues. Each component keeps its own fixture and helpers inside its describe.

// Guard for Label colour: a label inherits its container's colour, so no theme rule may set a
// colour on .z-label. IceBlue sets none either (ZK-6112).
// Fixture: web/pv/label-color-guard.zul.
test.describe('label', () => {
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
});

// Guard for tokens/_colors.css `--zk-color-warning-container` (ZK-6112, zkdemo B7 Z6): the container
// turns the warning seed hue 35° toward yellow so a warning notification no longer reads as the same
// peach as an error one. With the seed's own hue the two sat 13° apart.
// Fixture: web/pv/warning-container-guard.zul
test.describe('notification', () => {
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
});

// errorbox.css: the constraint error bubble writes its message in --zk-color-on-error-container on the
// error container. A screenshot does not catch the text colour falling back to the ordinary text
// colour (both are near-black), so the computed colour is compared with the token.
test.describe('errorbox', () => {
  test('errorbox text is drawn in --zk-color-on-error-container', async ({ page }) => {
    const resp = await page.goto('/errorbox.zul', { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    const box = page.locator('.z-errorbox').first();
    await expect(box).toBeVisible();
    const { text, token } = await box.evaluate((el) => {
      const probe = document.createElement('span');
      probe.style.color = 'var(--zk-color-on-error-container)';
      document.body.appendChild(probe);
      const resolved = getComputedStyle(probe).color;
      probe.remove();
      return { text: getComputedStyle(el).color, token: resolved };
    });
    expect(text).toBe(token);
  });
});
