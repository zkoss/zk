import { test, expect } from '@playwright/test';

// Guard for _log.css, the zk.log box that zk.ts doLog() creates (ZK-6112). doLog() sets no inline
// z-index, so the stylesheet's --zk-index-log is what stacks the box. Its close button carries
// .z-button, so the button's font-size rule must stay in zk-components: in zk-base it loses to
// .z-button and the button grows from body-small to the button's own size.
// Fixture: web/pv/zk-log-guard.zul

const FIXTURE = '/pv/zk-log-guard.zul';

test('the zk.log box is out of flow, stacked by --zk-index-log, with a body-small close button', async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  const box = page.locator('div.z-log');
  await expect(box).toBeVisible();
  const got = await box.evaluate((el) => {
    const root = getComputedStyle(document.documentElement);
    const probe = document.createElement('div');
    probe.style.fontSize = root.getPropertyValue('--zk-typescale-body-small-size');
    document.body.appendChild(probe);
    const bodySmall = getComputedStyle(probe).fontSize;
    probe.remove();
    return {
      position: getComputedStyle(el).position,
      zIndex: getComputedStyle(el).zIndex,
      logToken: root.getPropertyValue('--zk-index-log').trim(),
      buttonFont: getComputedStyle(el.querySelector('button')!).fontSize,
      bodySmall,
    };
  });
  expect(got.position).toBe('absolute');
  expect(got.logToken).not.toBe('');
  expect(got.zIndex).toBe(got.logToken);
  expect(got.buttonFont).toBe(got.bodySmall);
});
