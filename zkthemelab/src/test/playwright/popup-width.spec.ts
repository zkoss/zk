import { test, expect } from '@playwright/test';

// Guard for the width attribute on <popup> and <menupopup>: ZK writes it to the inline width, so no
// theme rule may put a min-width on .z-popup or .z-menupopup that overrides it. IceBlue has none
// either (ZK-6112).
// Fixture: web/pv/popup-width-guard.zul.

const FIXTURE = '/pv/popup-width-guard.zul';

async function openFixture(page: import('@playwright/test').Page) {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
}

async function expectRenderedWidth(popup: import('@playwright/test').Locator, expected: number) {
  await expect(popup).toHaveCount(1);
  await expect.poll(async () => Math.round((await popup.boundingBox())!.width)).toBe(expected);
}

test('<popup width="100px"> renders 100px wide', async ({ page }) => {
  await openFixture(page);
  await page.locator('.z-button').click();
  await expectRenderedWidth(page.locator('.z-popup:visible'), 100);
});

test('<menupopup width="100px"> renders 100px wide', async ({ page }) => {
  await openFixture(page);
  await page.getByText('Right-click for menu').click({ button: 'right' });
  await expectRenderedWidth(page.locator('.z-menupopup:visible'), 100);
});
