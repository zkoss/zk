import { test, expect, Locator, Page } from '@playwright/test';

// Guard for the invalid state of a real combobox and bandbox (combobox.css, bandbox.css). ZK puts
// z-combobox-invalid / z-bandbox-invalid on the <input>, so the constraint failure is driven the way a user does it:
// type, delete, blur.
// The same inputs inside an inputgroup, whose `border` shorthand repainted the error border, and as inplace
// inputs, which are transparent at rest and on hover and show the error on focus.
// Fixture: web/pv/input-invalid-guard.zul

declare const zk: any;

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

test('an invalid combobox draws the error border on its root at rest, hover and focus', async ({ page }) => {
  await failConstraint(page, 'combobox');
  const error = await errorColor(page);
  const expectBorders = (state: string) =>
    expect.poll(() => topBorderColor(page, '.iig-single .z-combobox'), `root at ${state}`).toBe(error);

  await page.mouse.move(0, 0);
  await expectBorders('rest');
  await page.locator('.iig-single .z-combobox').hover();
  await expectBorders('hover');
  await page.locator('.iig-single .z-combobox-input').focus();
  await expectBorders('focus');
});

// An open combobox that has no focus (opened by the API, not by a click) is matched by `.z-combobox-open`, which would
// paint the primary border over the error border on hover if the invalid hover rule did not outrank it: the invalid
// hover rule wins on specificity (it names the invalid class and the :hover), not on source order.
test('an invalid combobox that is open without focus keeps the error border on hover', async ({ page }) => {
  await failConstraint(page, 'combobox');
  const error = await errorColor(page);
  const combobox = page.locator('.iig-single .z-combobox');

  await page.mouse.move(0, 0);
  await combobox.evaluate(el => zk.Widget.$(el).open());
  await expect(combobox).toHaveClass(/z-combobox-open/);
  expect(await combobox.evaluate(el => el.matches(':focus-within')), 'the open combobox has no focus').toBe(false);
  await combobox.hover({ position: { x: 4, y: 4 }, force: true });
  expect(await combobox.evaluate(el => el.matches(':hover')), 'the pointer is over the combobox').toBe(true);
  await expect.poll(() => topBorderColor(page, '.iig-single .z-combobox'), 'root').toBe(error);
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

test('an invalid textbox draws the error border at rest, hover and focus', async ({ page }) => {
  const input = page.locator('.iig-plain .z-textbox');
  await input.click();
  await page.keyboard.type('a');
  await page.keyboard.press('Backspace');
  await page.locator('body').click({ position: { x: 600, y: 400 } });
  await expect(input).toHaveClass(/z-textbox-invalid/);
  const error = await errorColor(page);
  const expectBorder = (state: string) => expect.poll(() => topBorderColor(page, '.iig-plain .z-textbox'), `textbox at ${state}`).toBe(error);

  await page.mouse.move(0, 0);
  await expectBorder('rest');
  await input.hover();
  await expectBorder('hover');
  await input.focus();
  await expectBorder('focus');
});

test('a combobox and bandbox that have not failed their constraint keep the neutral border', async ({ page }) => {
  const error = await errorColor(page);
  expect(await topBorderColor(page, '.iig-single .z-combobox')).not.toBe(error);
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
// The combobox draws its border on its root, the other widgets on the input itself.
const groupBorderBox = (page: Page, widget: string) =>
  widget === 'combobox' ? page.locator('.iig-group-combobox .z-combobox') : groupInput(page, widget);

for (const widget of INPUTGROUP_WIDGETS) {
  test(`an invalid ${widget} in an inputgroup draws the error border at rest, hover and focus`, async ({ page }) => {
    const input = groupInput(page, widget);
    const borderBox = groupBorderBox(page, widget);
    await failConstraintOf(page, input);
    const error = await errorColor(page);
    const expectBorder = async (state: string) =>
      expect.poll(() => borderBox.evaluate(el => getComputedStyle(el).borderTopColor), `${widget} at ${state}`).toBe(error);

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
    expect(await groupBorderBox(page, widget).evaluate(el => getComputedStyle(el).borderTopColor)).toBe(groupBorder);
  });
}

// A focused combobox in an inputgroup keeps the border width and colour a focused textbox in the group has, the group
// outline owning the focus; an invalid combobox keeps the error colour. Its border is on the root, and neither the input
// nor the button has one.
const borderOf = (locator: Locator) =>
  locator.evaluate(el => {
    const style = getComputedStyle(el);
    return { width: style.borderTopWidth, color: style.borderTopColor };
  });

for (const state of ['valid', 'invalid']) {
  test(`a focused ${state} combobox in an inputgroup draws the border of its root as a focused textbox does`, async ({ page }) => {
    const input = groupInput(page, 'combobox');
    const root = groupBorderBox(page, 'combobox');
    if (state === 'invalid') await failConstraintOf(page, input);
    await page.mouse.move(0, 0);
    await groupInput(page, 'textbox').focus();
    const reference = await borderOf(groupInput(page, 'textbox'));
    await input.focus();
    const expectedColor = state === 'invalid' ? await errorColor(page) : reference.color;

    expect(reference.width, 'the textbox in the group keeps its 1px border on focus').toBe('1px');
    await expect.poll(() => borderOf(root), 'root border').toEqual({ width: reference.width, color: expectedColor });
    expect(await page.locator('.iig-group-combobox .z-combobox').evaluate(el => getComputedStyle(el).boxShadow), 'no inset ring: the group outline owns the focus').toBe('none');
  });
}

// The border sits on the wrapper of a bandbox and a combobox and on the input of a textbox.
const INPLACE_BORDERS: Record<string, string> = {
  textbox: '.iig-inplace-textbox',
  combobox: '.iig-inplace-combobox',
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

// A read-only field keeps the 3:1 outline at rest and on hover, but its read-only rules must not hide the focus or
// error border: they win over those rules on order or specificity, so a border-color in them kept the field grey.
// Read-only blocks typing, so the error is set the way Clients.wrongValue does it, through setErrorMessage.
// The composite fields draw the border on the wrapper; the text fields are the <input> itself.
const READONLY_FIELDS = [
  { widget: 'timebox', id: 'roTime', composite: true },
  { widget: 'spinner', id: 'roSpinner', composite: true },
  { widget: 'doublespinner', id: 'roDoublespinner', composite: true },
  { widget: 'textbox', id: 'roText', composite: false },
  { widget: 'intbox', id: 'roInt', composite: false },
];

for (const { widget, id, composite } of READONLY_FIELDS) {
  test(`a read-only ${widget} draws the primary border on focus and the error border when invalid`, { tag: '@ZK-6112' }, async ({ page }) => {
    const box = `.iig-readonly .z-${widget}`;
    const input = page.locator(composite ? `${box} .z-${widget}-input` : box);
    const resolve = (token: string) => page.evaluate(token => {
      const probe = document.createElement('span');
      probe.style.color = `var(${token})`;
      document.body.appendChild(probe);
      const resolved = getComputedStyle(probe).color;
      probe.remove();
      return resolved;
    }, token);
    const outline = await resolve('--zk-color-outline');

    await page.mouse.move(0, 0);
    expect(await topBorderColor(page, box), 'rest').toBe(outline);
    await page.locator(box).hover();
    expect(await topBorderColor(page, box), 'hover').toBe(outline);
    await input.focus();
    expect(await topBorderColor(page, box), 'focus').toBe(await resolve('--zk-color-primary'));
    await input.blur();

    await page.evaluate(id => zk.Widget.$(`$${id}`).setErrorMessage('Wrong'), id);
    await expect(input).toHaveClass(new RegExp(`z-${widget}-invalid`));
    await page.mouse.move(0, 0);
    const error = await errorColor(page);
    expect(await topBorderColor(page, box), 'invalid at rest').toBe(error);
    await page.locator(box).hover();
    expect(await topBorderColor(page, box), 'invalid on hover').toBe(error);
  });
}

// An intbox outside an inputgroup: input.css's `.z-intbox:hover` and `.z-intbox:focus` rules must not repaint the error
// border of a failed constraint, and the focused field keeps the 2px border of the focus rule. (The textbox and the grouped
// widgets are covered by tests earlier in this file.)
test('intbox — an invalid intbox draws the error border at rest, hover and focus', { tag: '@ZK-6112' }, async ({ page }) => {
  const input = page.locator('.iig-plain input.z-intbox');
  await failConstraintOf(page, input);
  const error = await errorColor(page);
  const expectBorder = (state: string) =>
    expect.poll(() => topBorderColor(page, '.iig-plain input.z-intbox'), `intbox at ${state}`).toBe(error);

  await page.mouse.move(0, 0);
  await expectBorder('rest');
  await input.hover();
  expect(await input.evaluate(el => el.matches(':hover')), 'the pointer is over the input').toBe(true);
  await expectBorder('hover');
  await input.focus();
  await expectBorder('focus');
  expect(await input.evaluate(el => getComputedStyle(el).borderTopWidth), 'focus border width').toBe('2px');
});

// A field that failed its constraint keeps the error border when the pointer is over it and when it has focus; the
// focused field also draws the second ring pixel as an inset shadow in the error colour. The composite widgets carry
// the border on the wrapper while ZK puts the invalid class on the <input> inside, so the wrapper's `:has()` rules
// must reach it. The fields fail the way a user fails them; a timebox cannot be typed empty or wrong, so its button
// fails it through Clients.wrongValue. A daterangebox takes the invalid class on its root.
// Fixture: web/pv/input-invalid-focus-guard.zul
test.describe('invalid together with focus or hover', () => {
  const FOCUS_FIXTURE = '/pv/input-invalid-focus-guard.zul';
  const COMPOSITE_WIDGETS = ['datebox', 'timebox', 'spinner', 'doublespinner', 'timepicker', 'daterangebox'];

  async function failField(page: Page, widget: string): Promise<void> {
    const input = page.locator(`.iif-${widget} input`).first();
    if (widget === 'timebox') {
      await page.locator('.iif-fail-timebox').click();
    } else {
      await input.click();
      await page.keyboard.type(widget === 'daterangebox' ? 'abc' : '1');
      if (widget !== 'daterangebox') await page.keyboard.press('Backspace');
      await page.locator('body').click({ position: { x: 900, y: 700 } });
    }
    const invalidRoot = widget === 'daterangebox' ? page.locator(`.z-${widget}.iif-${widget}`) : input;
    await expect(invalidRoot).toHaveClass(new RegExp(`z-${widget}-invalid`));
  }

  test.beforeEach(async ({ page }) => {
    await page.goto(FOCUS_FIXTURE, { waitUntil: 'networkidle' });
    await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important}' });
  });

  for (const widget of COMPOSITE_WIDGETS) {
    test(`${widget} — a field that failed its constraint draws the error border on hover and focus`, async ({ page }) => {
      await failField(page, widget);
      const root = page.locator(`.z-${widget}.iif-${widget}`);
      const error = await errorColor(page);
      const inset = (el: Element, pseudo: string | null) => getComputedStyle(el, pseudo).boxShadow;
      // The timepicker draws its second ring pixel on ::after, the other composites on the wrapper.
      const ringPseudo = widget === 'timepicker' ? '::after' : null;

      await page.mouse.move(0, 0);
      await root.hover();
      expect(await root.evaluate(el => el.matches(':hover')), 'the pointer is over the field').toBe(true);
      await expect.poll(() => topBorderColor(page, `.z-${widget}.iif-${widget}`), `${widget} on hover`).toBe(error);

      await page.mouse.move(0, 0);
      await page.locator(`.iif-${widget} input`).first().focus();
      expect(await root.evaluate(el => el.matches(':focus-within')), 'the focus is inside the field').toBe(true);
      await expect.poll(() => topBorderColor(page, `.z-${widget}.iif-${widget}`), `${widget} on focus`).toBe(error);
      await expect.poll(() => root.evaluate(inset, ringPseudo), `${widget} focus ring`).toContain(error);
    });
  }

  // Opened by the API, the datebox has no focus, so `.z-datebox-open` (primary border) competes with the invalid hover
  // rule; the error border must win, by specificity (the invalid hover rule names one more class or pseudo-class).
  test('datebox — a field that failed its constraint and is open without focus keeps the error border on hover', async ({ page }) => {
    await failField(page, 'datebox');
    const root = page.locator('.z-datebox.iif-datebox');
    const error = await errorColor(page);

    await page.mouse.move(0, 0);
    await root.evaluate(el => zk.Widget.$(el).setOpen(true));
    await expect(root).toHaveClass(/z-datebox-open/);
    expect(await root.evaluate(el => el.matches(':focus-within')), 'the open datebox has no focus').toBe(false);
    await root.hover({ position: { x: 4, y: 4 }, force: true });
    expect(await root.evaluate(el => el.matches(':hover')), 'the pointer is over the field').toBe(true);
    await expect.poll(() => topBorderColor(page, '.z-datebox.iif-datebox'), 'datebox on hover').toBe(error);
  });

  // Only the border carries the invalid state; the typed value keeps the text colour of a valid field. The field holds a
  // valid value when the error is set (setErrorMessage), so there is text to colour. The error box is hidden so that it
  // cannot cover the input.
  for (const widget of ['datebox', 'spinner', 'doublespinner']) {
    test(`${widget} — a field that failed its constraint keeps the default text colour`, async ({ page }) => {
      await page.addStyleTag({ content: '.z-errorbox{visibility:hidden}' });
      const root = page.locator(`.z-${widget}.iif-${widget}`);
      const input = root.locator('input');
      const textColor = () => input.evaluate(el => getComputedStyle(el).color);

      await input.fill(widget === 'datebox' ? '2026-01-15' : '3');
      await page.locator('body').click({ position: { x: 900, y: 700 } });
      const validColor = await textColor();
      await root.evaluate(el => zk.Widget.$(el).setErrorMessage('Wrong'));
      await expect(input).toHaveClass(new RegExp(`z-${widget}-invalid`));

      expect(await textColor(), `${widget} text colour when invalid`).toBe(validColor);
      expect(validColor, 'the valid text colour is not the error colour').not.toBe(await errorColor(page));
    });
  }

  // `.z-timepicker-inplace:hover` would turn the border transparent on hover if the invalid hover rule did not outrank
  // it: that rule wins on specificity (it also names the invalid class), and it comes earlier in the file. The error box
  // is hidden so that it cannot sit under the pointer.
  test('timepicker — an inplace field that failed its constraint keeps the error border on hover', async ({ page }) => {
    await page.addStyleTag({ content: '.z-errorbox{visibility:hidden}' });
    const root = page.locator('.z-timepicker.iif-timepicker-inplace');
    const error = await errorColor(page);

    await root.evaluate(el => zk.Widget.$(el).setErrorMessage('Wrong'));
    await expect(root.locator('input')).toHaveClass(/z-timepicker-invalid/);
    await page.mouse.move(0, 0);
    await expect.poll(() => topBorderColor(page, '.z-timepicker.iif-timepicker-inplace'), 'timepicker at rest').toBe(error);
    await root.hover({ position: { x: 4, y: 4 }, force: true });
    expect(await root.evaluate(el => el.matches(':hover')), 'the pointer is over the field').toBe(true);
    await expect.poll(() => topBorderColor(page, '.z-timepicker.iif-timepicker-inplace'), 'timepicker on hover').toBe(error);
  });

  test('textbox — a textarea that failed its constraint draws the error border on focus and keeps its vertical padding', async ({ page }) => {
    const textarea = page.locator('textarea.iif-textarea');
    await failConstraintOf(page, textarea);
    const error = await errorColor(page);
    const verticalPadding = await page.evaluate(() => {
      const probe = document.createElement('span');
      probe.style.paddingTop = 'calc(var(--zk-spacing-2) - 1px)';
      document.body.appendChild(probe);
      const resolved = getComputedStyle(probe).paddingTop;
      probe.remove();
      return resolved;
    });

    await textarea.focus();
    await expect.poll(() => topBorderColor(page, 'textarea.iif-textarea'), 'textarea on focus').toBe(error);
    expect(await textarea.evaluate(el => getComputedStyle(el).paddingTop), 'the focused textarea keeps its padding').toBe(verticalPadding);
  });
});
