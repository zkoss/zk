import { test, expect } from '@playwright/test';

// Guard for zkmax nav.css (ZK-6112): in a collapsed navbar, Nav._openPopup moves the label to body
// as .z-nav-text-popup right before the .z-nav-popup list. The title takes the popup surface and
// the top corners, the list the bottom ones, so the two read as one menu.
// Fixture: web/pv/navbar-popup-title-guard.zul

const FIXTURE = '/pv/navbar-popup-title-guard.zul';

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.waitForFunction(() => {
    const zk = (window as any).zk;
    return zk && zk.booted && !zk.loading && !zk.mounting && !zk.processing;
  });
});

test('the popup title and list form one surface', async ({ page }) => {
  await page.locator('.z-navbar-collapsed .z-nav:visible').first().click();
  // No mouse move: the nav popup closes as soon as the pointer leaves the nav.
  const title = page.locator('.z-nav-text-popup');
  await expect(title).toBeVisible();
  const m = await title.evaluate((el) => {
    const t = getComputedStyle(el);
    const list = el.nextElementSibling as HTMLElement;
    const l = getComputedStyle(list);
    const probe = document.createElement('div');
    probe.style.backgroundColor = 'var(--zk-color-surface-container)';
    document.body.appendChild(probe);
    const surface = getComputedStyle(probe).backgroundColor;
    probe.remove();
    return {
      listFollows: list.classList.contains('z-nav-popup'),
      bg: t.backgroundColor, surface,
      titleTop: t.borderTopLeftRadius, titleBottom: t.borderBottomLeftRadius,
      listTop: l.borderTopLeftRadius, listBottom: l.borderBottomLeftRadius,
    };
  });
  expect(m.listFollows).toBe(true);
  expect(m.bg).toBe(m.surface);
  expect(m.titleTop).not.toBe('0px');
  expect(m.titleBottom).toBe('0px');
  expect(m.listTop).toBe('0px');
  expect(m.listBottom).toBe(m.titleTop);
});
