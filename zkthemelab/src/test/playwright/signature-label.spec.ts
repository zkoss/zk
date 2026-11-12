import { test, expect, Locator } from '@playwright/test';

// Guard for zkmax signature.css (ZK-6112, zkdemo B13 Z4): undoLabel, saveLabel and clearLabel show
// their text after the icon; a button without a label stays icon-only.
// Fixture: web/pv/signature-label-guard.zul

const FIXTURE = '/pv/signature-label-guard.zul';
const MIN_ICON_GAP_PX = 4;

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

for (const [kind, text] of [['undo', 'Undo'], ['save', 'Save'], ['clear', 'Clear']] as const) {
  test(`${kind}Label shows after the icon`, async ({ page }) => {
    const button = page.locator('.labelled .z-signature-tool-button').filter({ hasText: text });
    const label = button.locator('.z-signature-tool-button-label');
    await expect(label).toBeVisible();
    const icon = (await button.locator('.z-signature-tool-button-icon').boundingBox())!;
    const labelBox = (await label.boundingBox())!;
    expect(labelBox.x - (icon.x + icon.width)).toBeGreaterThanOrEqual(MIN_ICON_GAP_PX);
  });
}

test('a button without a label stays icon-only', async ({ page }) => {
  const buttons = page.locator('.plain .z-signature-tool-button');
  await expect(buttons).toHaveCount(3);
  for (const button of await buttons.all()) {
    const offset = await button.evaluate((el) => {
      const b = el.getBoundingClientRect();
      const i = el.querySelector('.z-signature-tool-button-icon')!.getBoundingClientRect();
      return (i.left + i.width / 2) - (b.left + b.width / 2);
    });
    // No space reserved for an empty label: the icon stays centred in the button.
    expect(Math.abs(offset)).toBeLessThanOrEqual(0.5);
  }
});

// An icon-only button names itself in a tooltip: the ::after shows the aria-label za11y sets,
// on hover and on keyboard focus. A labelled button already shows its name, so no tooltip.
test('an icon-only button shows its aria-label in a tooltip on hover and focus', async ({ page }) => {
  const tooltipOf = (button: Locator) => button.evaluate((el) => getComputedStyle(el, '::after').content);
  const button = page.locator('.plain .z-signature-tool-button').first();
  const name = await button.getAttribute('aria-label');
  expect(name).toBeTruthy();
  expect(await tooltipOf(button)).toBe('none');
  await button.hover();
  expect(await tooltipOf(button)).toBe(`"${name}"`);
  await page.mouse.move(0, 0);
  expect(await tooltipOf(button)).toBe('none');
  // Tab to the button so it matches :focus-visible, as a keyboard user reaches it.
  for (let i = 0; i < 20 && !(await button.evaluate((el) => el === document.activeElement)); i++) {
    await page.keyboard.press('Tab');
  }
  expect(await button.evaluate((el) => el.matches(':focus-visible'))).toBe(true);
  expect(await tooltipOf(button)).toBe(`"${name}"`);
});

test('a labelled button shows no tooltip', async ({ page }) => {
  const button = page.locator('.labelled .z-signature-tool-button').first();
  await button.hover();
  expect(await button.evaluate((el) => getComputedStyle(el, '::after').content)).toBe('none');
});
