import { test, expect } from '@playwright/test';

// Guard for Bandbox popupWidth: ComboWidget._fixsz() writes popupWidth (or the percentage of the
// input width) to the popup's inline width, so no theme rule may put a min-width on
// .z-bandbox-popup that overrides it. IceBlue has none either (ZK-6112).
// Fixture: web/pv/bandbox-popupwidth-guard.zul.

const FIXTURE = '/pv/bandbox-popupwidth-guard.zul';

// [popupWidth, expected width]: the input is 150px wide, so 120% is 180px.
const CASES = [['200px', 200], ['120%', 180]] as const;

CASES.forEach(([popupWidth, expected], i) => {
  test(`popupWidth="${popupWidth}" renders the popup ${expected}px wide`, async ({ page }) => {
    const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    await page.locator('.z-bandbox').nth(i).locator('.z-bandbox-button').click();
    const popup = page.locator('.z-bandbox-popup:visible');
    await expect(popup).toHaveCount(1);
    await expect.poll(async () => Math.round((await popup.boundingBox())!.width)).toBe(expected);
  });
});
