import { test, expect, Locator, Page } from '@playwright/test';

// Guard for the invalid state of a real combobox and bandbox (combobox.css, bandbox.css). ZK puts
// z-combobox-invalid / z-bandbox-invalid on the <input>, not on the wrapper that a preview page's
// sclass reaches, so the constraint failure is driven the way a user does it: type, delete, blur.
// The same inputs inside an inputgroup, whose `border` shorthand repainted the error border (follow-up 55), and as inplace
// inputs, which are transparent at rest and on hover and show the error on focus (follow-up 56).
// Fixture: web/pv/input-invalid-guard.zul

const FIXTURE = '/pv/input-invalid-guard.zul';
const TRANSPARENT = 'rgba(0, 0, 0, 0)';

async function failConstraint(page: Page, widget: 'combobox' | 'bandbox'): Promise<void> {
  const input = page.locator(`.iig-single .z-${widget}-input`);
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
  // Kill transitions: the border colour transitions from its rest value, so a focus or hover rule that
  // paints the wrong colour would still read as the rest (error) colour on the first poll.
  await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important}' });
});

test('an invalid combobox draws the error border on its input and button at rest, hover and focus', async ({ page }) => {
  await failConstraint(page, 'combobox');
  const error = await errorColor(page);
  const expectBorders = async (state: string) => {
    await expect.poll(() => topBorderColor(page, '.iig-single .z-combobox-input'), `input at ${state}`).toBe(error);
    await expect.poll(() => topBorderColor(page, '.iig-single .z-combobox-button'), `button at ${state}`).toBe(error);
  };

  await page.mouse.move(0, 0);
  await expectBorders('rest');
  await page.locator('.iig-single .z-combobox').hover();
  await expectBorders('hover');
  await page.locator('.iig-single .z-combobox-input').focus();
  await expectBorders('focus');
});

test('an invalid bandbox draws the error border on its wrapper at rest, hover and focus', async ({ page }) => {
  await failConstraint(page, 'bandbox');
  const error = await errorColor(page);
  const expectBorder = (state: string) => expect.poll(() => topBorderColor(page, '.iig-single .z-bandbox'), `wrapper at ${state}`).toBe(error);

  await page.mouse.move(0, 0);
  await expectBorder('rest');
  await page.locator('.iig-single .z-bandbox').hover();
  await expectBorder('hover');
  await page.locator('.iig-single .z-bandbox-input').focus();
  await expectBorder('focus');
});

test('a combobox and bandbox that have not failed their constraint keep the neutral border', async ({ page }) => {
  const error = await errorColor(page);
  expect(await topBorderColor(page, '.iig-single .z-combobox-input')).not.toBe(error);
  expect(await topBorderColor(page, '.iig-single .z-bandbox')).not.toBe(error);
});

// Types a character and deletes it, then blurs, so ZK itself marks the input invalid.
async function failConstraintOf(page: Page, input: Locator): Promise<void> {
  await input.click();
  await page.keyboard.type('1');
  await page.keyboard.press('Backspace');
  await page.locator('body').click({ position: { x: 600, y: 700 } });
  await expect(input).toHaveClass(/z-\w+-invalid/);
}

async function resolvedColor(page: Page, value: string): Promise<string> {
  return page.evaluate((color) => {
    const probe = document.createElement('span');
    probe.style.color = color;
    document.body.appendChild(probe);
    const resolved = getComputedStyle(probe).color;
    probe.remove();
    return resolved;
  }, value);
}

const INPUTGROUP_WIDGETS = ['textbox', 'intbox', 'longbox', 'doublebox', 'decimalbox', 'combobox'];
const groupInput = (page: Page, widget: string) =>
  page.locator(widget === 'combobox' ? '.iig-group-combobox .z-combobox-input' : `.iig-group-${widget} input.z-${widget}`);

for (const widget of INPUTGROUP_WIDGETS) {
  test(`an invalid ${widget} in an inputgroup draws the error border at rest, hover and focus`, async ({ page }) => {
    const input = groupInput(page, widget);
    await failConstraintOf(page, input);
    const error = await errorColor(page);
    const expectBorder = async (state: string) =>
      expect.poll(() => input.evaluate(el => getComputedStyle(el).borderTopColor), `${widget} at ${state}`).toBe(error);

    await page.mouse.move(0, 0);
    await expectBorder('rest');
    await input.hover();
    expect(await input.evaluate(el => el.matches(':hover')), 'the pointer is over the input').toBe(true);
    await expectBorder('hover');
    await input.focus();
    await expectBorder('focus');
  });

  test(`a ${widget} in an inputgroup that has not failed its constraint keeps the group border`, async ({ page }) => {
    const groupBorder = await resolvedColor(page, 'var(--zk-inputgroup-border-color)');
    await page.mouse.move(0, 0);
    expect(await groupInput(page, widget).evaluate(el => getComputedStyle(el).borderTopColor)).toBe(groupBorder);
  });
}

// The border sits on the wrapper of a bandbox and on the input of a textbox and a combobox.
const INPLACE_BORDERS: Record<string, string> = {
  textbox: '.iig-inplace-textbox',
  combobox: '.iig-inplace-combobox .z-combobox-input',
  bandbox: '.iig-inplace-bandbox',
};
const INPLACE_INPUTS: Record<string, string> = {
  textbox: '.iig-inplace-textbox',
  combobox: '.iig-inplace-combobox .z-combobox-input',
  bandbox: '.iig-inplace-bandbox .z-bandbox-input',
};

for (const widget of Object.keys(INPLACE_BORDERS)) {
  test(`an invalid inplace ${widget} is transparent at rest and on hover and draws the error border on focus`, async ({ page }) => {
    const input = page.locator(INPLACE_INPUTS[widget]);
    const border = page.locator(INPLACE_BORDERS[widget]);
    await failConstraintOf(page, input);
    const error = await errorColor(page);
    const expectBorder = async (state: string, color: string) =>
      expect.poll(() => border.evaluate(el => getComputedStyle(el).borderTopColor), `${widget} at ${state}`).toBe(color);

    await page.mouse.move(0, 0);
    await expectBorder('rest', TRANSPARENT);
    await border.hover();
    expect(await border.evaluate(el => el.matches(':hover')), 'the pointer is over the border box').toBe(true);
    await expectBorder('hover', TRANSPARENT);
    await input.focus();
    await expectBorder('focus', error);
  });
}
