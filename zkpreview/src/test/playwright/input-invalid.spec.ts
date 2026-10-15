import { test, expect, Page } from '@playwright/test';

// Guard for the invalid state of a real combobox and bandbox (combobox.css, bandbox.css). ZK puts
// z-combobox-invalid / z-bandbox-invalid on the <input>, not on the wrapper that a preview page's
// sclass reaches, so the constraint failure is driven the way a user does it: type, delete, blur.
// Fixture: web/pv/input-invalid-guard.zul

const FIXTURE = '/pv/input-invalid-guard.zul';

async function failConstraint(page: Page, widget: 'combobox' | 'bandbox'): Promise<void> {
  const input = page.locator(`.z-${widget}-input`);
  await input.click();
  await page.keyboard.type('a');
  await page.keyboard.press('Backspace');
  await page.locator('body').click({ position: { x: 600, y: 400 } });
  await expect(input).toHaveClass(new RegExp(`z-${widget}-invalid`));
}

async function errorColor(page: Page): Promise<string> {
  return page.evaluate(() => {
    const probe = document.createElement('span');
    probe.style.color = 'var(--zk-color-error)';
    document.body.appendChild(probe);
    const resolved = getComputedStyle(probe).color;
    probe.remove();
    return resolved;
  });
}

const topBorderColor = (page: Page, selector: string) =>
  page.locator(selector).evaluate(el => getComputedStyle(el).borderTopColor);

test.beforeEach(async ({ page }) => {
  await page.goto(FIXTURE, { waitUntil: 'networkidle' });
});

test('an invalid combobox draws the error border on its input and button at rest, hover and focus', async ({ page }) => {
  await failConstraint(page, 'combobox');
  const error = await errorColor(page);
  // The border colour transitions, so the computed value is polled until it settles.
  const expectBorders = async (state: string) => {
    await expect.poll(() => topBorderColor(page, '.z-combobox-input'), `input at ${state}`).toBe(error);
    await expect.poll(() => topBorderColor(page, '.z-combobox-button'), `button at ${state}`).toBe(error);
  };

  await page.mouse.move(0, 0);
  await expectBorders('rest');
  await page.locator('.z-combobox').hover();
  await expectBorders('hover');
  await page.locator('.z-combobox-input').focus();
  await expectBorders('focus');
});

test('an invalid bandbox draws the error border on its wrapper at rest, hover and focus', async ({ page }) => {
  await failConstraint(page, 'bandbox');
  const error = await errorColor(page);
  const expectBorder = (state: string) => expect.poll(() => topBorderColor(page, '.z-bandbox'), `wrapper at ${state}`).toBe(error);

  await page.mouse.move(0, 0);
  await expectBorder('rest');
  await page.locator('.z-bandbox').hover();
  await expectBorder('hover');
  await page.locator('.z-bandbox-input').focus();
  await expectBorder('focus');
});

test('a combobox and bandbox that have not failed their constraint keep the neutral border', async ({ page }) => {
  const error = await errorColor(page);
  expect(await topBorderColor(page, '.z-combobox-input')).not.toBe(error);
  expect(await topBorderColor(page, '.z-bandbox')).not.toBe(error);
});
