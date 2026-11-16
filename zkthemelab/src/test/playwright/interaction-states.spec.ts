import { test, expect, Locator, Page } from '@playwright/test';

// Interaction states that no other spec drives through real input: hover, keyboard focus, an open picker, a
// highlighted or selected item, a collapsed container. Every test performs the interaction and asserts the styled
// outcome as a computed value that it resolves from the token, so a rule that stops matching or loses its value fails.
// The suite's CSS coverage measurement (scripts/css-coverage.mjs) lists rules that no test ever made match; these
// tests were written for the largest of those gaps.
// Fixtures: web/pv/select-states-guard.zul, popup-item-states-guard.zul, groupbox-collapsed-guard.zul,
//           placeholder-guard.zul, daterangebox-popup-guard.zul, menupopup-submenu-guard.zul; the preview pages
//           tabbox, portallayout, biglistbox and goldenlayout.

declare const zk: any;

/** The computed value of `property` when it is set to `value` (a `var(--zk-…)` or an expression over tokens). */
async function resolve(page: Page, property: string, value: string): Promise<string> {
  return page.evaluate(([prop, val]) => {
    const probe = document.createElement('span');
    probe.style.setProperty(prop, val);
    document.body.appendChild(probe);
    const resolved = getComputedStyle(probe).getPropertyValue(prop);
    probe.remove();
    return resolved;
  }, [property, value]);
}

const style = (locator: Locator, property: string, pseudo: string | null = null) =>
  locator.evaluate((el, [prop, pseudoElement]) => getComputedStyle(el, pseudoElement).getPropertyValue(prop), [property, pseudo]);

/** Kills transitions, so a value read right after a state change is the end value, not a step of the transition. */
async function stopTransitions(page: Page): Promise<void> {
  await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important}' });
}

/** The width, style and colour of an element's outline. */
const outlineOf = (element: Locator) =>
  element.evaluate(el => {
    const computed = getComputedStyle(el);
    return [computed.outlineWidth, computed.outlineStyle, computed.outlineColor];
  });
/** The same three values of the `--zk-focus-ring` token, resolved on a probe element. */
const ringToken = (page: Page) =>
  page.evaluate(() => {
    const probe = document.createElement('span');
    probe.style.outline = 'var(--zk-focus-ring)';
    document.body.appendChild(probe);
    const computed = getComputedStyle(probe);
    const ring = [computed.outlineWidth, computed.outlineStyle, computed.outlineColor];
    probe.remove();
    return ring;
  });

/** Presses Tab until the element has the focus: the only way to focus it so that :focus-visible matches. */
async function tabTo(page: Page, target: Locator): Promise<void> {
  for (let presses = 0; presses < 40 && !(await target.evaluate(el => el === document.activeElement)); presses++)
    await page.keyboard.press('Tab');
  expect(await target.evaluate(el => el === document.activeElement), 'the keyboard reached the element').toBe(true);
}

/** Waits until the element stops moving: a popup slides open, and a pointer placed on it meanwhile ends up on another item. */
async function expectSettled(locator: Locator): Promise<void> {
  let previous = '';
  await expect.poll(async () => {
    const box = JSON.stringify(await locator.boundingBox());
    const settled = box === previous;
    previous = box;
    return settled;
  }, { intervals: [100] }).toBe(true);
}

async function open(page: Page, url: string): Promise<void> {
  const resp = await page.goto(url, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await stopTransitions(page);
}

/** Open-picker assertions shared by every native select: the popover, its hovered, selected and pressed options. */
async function expectStyledPicker(page: Page, control: Locator): Promise<void> {
  const options = control.locator('option');
  const selected = control.locator('option:checked');
  const selectedIndex = await control.evaluate(el => (el as HTMLSelectElement).selectedIndex);
  const other = options.nth(selectedIndex === 0 ? 1 : 0);
  const onSurface = 'var(--zk-color-on-surface)';

  expect(await style(control, 'transform', '::picker-icon'), 'the chevron turns upside down').not.toBe('none');
  expect(await style(control, 'background-color', '::picker(select)')).toBe(await resolve(page, 'background-color', 'var(--zk-color-surface)'));
  expect(await style(control, 'box-shadow', '::picker(select)')).toBe(await resolve(page, 'box-shadow', 'var(--zk-elevation-3)'));
  expect(await style(selected, 'background-color'), 'the selected option').toBe(await resolve(page, 'background-color', 'var(--zk-color-secondary-container)'));
  expect(await style(selected, 'color')).toBe(await resolve(page, 'color', 'var(--zk-color-on-secondary-container)'));
  expect(await style(selected, 'opacity', '::checkmark'), 'the selected option shows its checkmark').toBe('1');
  expect(await style(other, 'opacity', '::checkmark'), 'another option hides it').toBe('0');

  await other.hover();
  expect(await other.evaluate(el => el.matches(':hover'))).toBe(true);
  expect(await style(other, 'background-color'), 'the hovered option').toBe(await resolve(page, 'background-color', `color-mix(in srgb, ${onSurface} 8%, transparent)`));

  await selected.hover();
  expect(await style(selected, 'background-color'), 'the selected option on hover')
    .toBe(await resolve(page, 'background-color', 'color-mix(in srgb, var(--zk-color-on-secondary-container) 8%, var(--zk-color-secondary-container))'));

  await other.hover();
  await page.mouse.down();
  expect(await style(other, 'background-color'), 'the pressed option').toBe(await resolve(page, 'background-color', `color-mix(in srgb, ${onSurface} 12%, transparent)`));
  await page.mouse.up();
}

// Native selects: .z-select (listbox mold="select"), .z-selectbox and the time zone select of a datebox popup. Chromium
// draws their picker as a styled in-page popover (appearance: base-select), so its options can be hovered and read.
// Fixture: web/pv/select-states-guard.zul
test.describe('native select', () => {
  const FIXTURE = '/pv/select-states-guard.zul';

  interface SelectKind {
    widget: string;
    select: string;
    disabledSelect: string;
    hoverBorder: string;
  }
  const KINDS: SelectKind[] = [
    { widget: 'select', select: 'select.z-select:not([disabled])', disabledSelect: 'select.z-select[disabled]', hoverBorder: '--zk-selectbox-border-color-hover' },
    { widget: 'selectbox', select: 'select.z-selectbox:not([disabled])', disabledSelect: 'select.z-selectbox[disabled]', hoverBorder: '--zk-color-on-surface' },
  ];

  test.beforeEach(async ({ page }) => {
    await open(page, FIXTURE);
  });

  for (const { widget, select, disabledSelect, hoverBorder } of KINDS) {
    test(`${widget} — hover turns the border to the hover colour`, async ({ page }) => {
      const control = page.locator(select).first();
      await page.mouse.move(0, 0);
      const atRest = await style(control, 'border-top-color');
      await control.hover();
      expect(await control.evaluate(el => el.matches(':hover'))).toBe(true);
      expect(await style(control, 'border-top-color')).toBe(await resolve(page, 'color', `var(${hoverBorder})`));
      expect(await style(control, 'border-top-color')).not.toBe(atRest);
    });

    test(`${widget} — keyboard focus draws the primary border and a second ring pixel inside it`, async ({ page }) => {
      const control = page.locator(select).first();
      await page.mouse.move(0, 0);
      const atRest = await style(control, 'box-shadow');
      await control.focus();
      const primary = await resolve(page, 'color', 'var(--zk-color-primary)');
      expect(await style(control, 'border-top-color')).toBe(primary);
      expect(await style(control, 'border-top-width'), 'the border stays 1px, the ring grows inward').toBe('1px');
      expect(await style(control, 'box-shadow')).toContain(primary);
      expect(await style(control, 'box-shadow')).not.toBe(atRest);
    });

    test(`${widget} — a disabled select is dimmed, shows the not-allowed cursor and ignores the pointer`, async ({ page }) => {
      const control = page.locator(disabledSelect);
      const enabled = page.locator(select).first();
      expect(await style(control, 'opacity')).toBe(await resolve(page, 'opacity', 'var(--zk-state-disabled-opacity)'));
      expect(await style(control, 'cursor')).toBe('not-allowed');
      expect(await style(control, 'pointer-events')).toBe('none');
      expect(await style(control, 'background-color')).toBe(await resolve(page, 'background-color', 'var(--zk-color-surface-container-low)'));
      expect(await style(enabled, 'opacity')).toBe('1');
    });

    test(`${widget} — the open picker lists the options, highlights the hovered one and marks the selected one`, async ({ page }) => {
      const control = page.locator(select).first();
      await control.click();
      await expect.poll(() => control.evaluate(el => el.matches(':open')), 'the picker is open').toBe(true);
      await expectStyledPicker(page, control);
    });
  }
});

// The time zone select and the time field inside a datebox popup: the popup draws them borderless, and hover, focus and an
// open picker bring the borders and option states back.
test.describe('datebox popup', () => {
  async function openPopup(page: Page) {
    await open(page, '/pv/select-states-guard.zul');
    await page.evaluate(() => zk.Widget.$('$zoned').setOpen(true));
    const timezone = page.locator('.z-datebox-popup .z-datebox-timezone > select');
    await expect(timezone).toBeVisible();
    return timezone;
  }

  test('datebox — the time zone select is borderless at rest, hover and focus bring the borders back', async ({ page }) => {
    const timezone = await openPopup(page);
    await page.mouse.move(0, 0);
    expect(await style(timezone, 'border-top-color'), 'at rest').toBe('rgba(0, 0, 0, 0)');
    await timezone.hover();
    expect(await style(timezone, 'border-top-color'), 'on hover').toBe(await resolve(page, 'color', 'var(--zk-color-on-surface)'));
    await page.mouse.move(0, 0);
    await timezone.focus();
    const primary = await resolve(page, 'color', 'var(--zk-color-primary)');
    expect(await style(timezone, 'border-top-color'), 'on focus').toBe(primary);
    expect(await style(timezone, 'box-shadow')).toContain(primary);
  });

  test('datebox — the open time zone picker highlights the hovered option and marks the selected one', async ({ page }) => {
    const timezone = await openPopup(page);
    await timezone.click();
    await expect.poll(() => timezone.evaluate(el => el.matches(':open')), 'the picker is open').toBe(true);
    await expectStyledPicker(page, timezone);
  });

  test('datebox — the time field in the popup is borderless at rest and draws the primary border on focus', async ({ page }) => {
    await openPopup(page);
    const input = page.locator('.z-datebox-popup .z-timebox .z-timebox-input');
    await page.mouse.move(0, 0);
    expect(await style(input, 'border-top-color'), 'at rest').toBe('rgba(0, 0, 0, 0)');
    await input.focus();
    expect(await style(input, 'border-top-color'), 'on focus').toBe(await resolve(page, 'color', 'var(--zk-color-primary)'));
  });
});

// Popup items of a combobox: the pointer hovers an item, the arrow keys highlight one (ZK marks it z-comboitem-selected,
// the same class a chosen item carries), and a description line under the label follows the highlight.
// Fixture: web/pv/popup-item-states-guard.zul
test.describe('combobox popup', () => {
  test.beforeEach(async ({ page }) => {
    await open(page, '/pv/popup-item-states-guard.zul');
    await page.locator('.z-combobox-button').click();
    await expect(page.locator('.z-combobox-popup')).toBeVisible();
    await expectSettled(page.locator('.z-comboitem').last());
    await page.locator('.z-combobox-input').focus();
    await page.mouse.move(0, 0);
  });

  test('comboitem — the hovered item gets the hover background, the others stay transparent', async ({ page }) => {
    const items = page.locator('.z-comboitem');
    await items.nth(2).hover();
    expect(await items.nth(2).evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(items.nth(2), 'background-color')).toBe(await resolve(page, 'background-color', 'var(--zk-combobox-item-hover-bg)'));
    expect(await style(items.nth(1), 'background-color')).toBe('rgba(0, 0, 0, 0)');
  });

  test('comboitem — the arrow keys move the highlight, which takes the selected background, colour and description colour', async ({ page }) => {
    const items = page.locator('.z-comboitem');
    const description = (index: number) => items.nth(index).locator('.z-comboitem-inner');
    const restingDescription = await style(description(1), 'color');

    await page.keyboard.press('ArrowDown');
    await expect(items.first()).toHaveClass(/z-comboitem-selected/);
    expect(await style(items.first(), 'background-color')).toBe(await resolve(page, 'background-color', 'var(--zk-combobox-selected-bg)'));
    expect(await style(items.first(), 'color')).toBe(await resolve(page, 'color', 'var(--zk-combobox-selected-fg)'));
    expect(await style(description(0), 'color'), 'the description is one step lighter than the label')
      .toBe(await resolve(page, 'color', 'color-mix(in oklch, var(--zk-combobox-selected-fg) 85%, transparent)'));
    expect(await style(description(1), 'color'), 'an item that is not highlighted keeps its description colour').toBe(restingDescription);

    await page.keyboard.press('ArrowDown');
    await expect(items.nth(1)).toHaveClass(/z-comboitem-selected/);
    await expect(items.first()).not.toHaveClass(/z-comboitem-selected/);
    expect(await style(items.first(), 'background-color'), 'the item the highlight left').toBe('rgba(0, 0, 0, 0)');
    expect(await style(items.nth(1), 'background-color')).toBe(await resolve(page, 'background-color', 'var(--zk-combobox-selected-bg)'));
  });
});

// Chosenbox: the remove button of a chip and the option that creates a new chip draw a hover shade. Both shades are
// literal translucent blacks in the CSS, so the assertion is that the hover paints a visible shade over the transparent rest.
// Fixture: web/pv/popup-item-states-guard.zul
test.describe('chosenbox', () => {
  const isVisibleShade = (color: string) => color !== 'rgba(0, 0, 0, 0)' && !/ 0\)$/.test(color);

  test.beforeEach(async ({ page }) => {
    await open(page, '/pv/popup-item-states-guard.zul');
    await page.mouse.move(0, 0);
  });

  test('chosenbox — hovering the remove button of a chip shades it', async ({ page }) => {
    const button = page.locator('.z-chosenbox-item .z-chosenbox-button').first();
    // za11y moves the shade from the button to a ::before circle (a 24px hit box with a 20px circle).
    const shade = () => button.evaluate(el => [getComputedStyle(el).backgroundColor, getComputedStyle(el, '::before').backgroundColor]);
    expect((await shade()).some(isVisibleShade), 'no shade at rest').toBe(false);
    await button.hover();
    expect(await button.evaluate(el => el.matches(':hover'))).toBe(true);
    expect((await shade()).some(isVisibleShade), 'a shade on hover').toBe(true);
  });

  test('chosenbox — hovering the option that creates a new item shades it', async ({ page }) => {
    await page.locator('.z-chosenbox-input').click();
    await page.keyboard.type('zzz');
    const creatable = page.locator('.z-chosenbox-empty-creatable');
    await expect(creatable).toBeVisible();
    // ZK highlights the only option when the popup opens; without that class only the pointer can shade it.
    await creatable.evaluate(el => el.classList.remove('z-chosenbox-option-hover'));
    await page.mouse.move(0, 0);
    expect(isVisibleShade(await style(creatable, 'background-color')), 'no shade before the pointer arrives').toBe(false);
    await creatable.hover();
    expect(await creatable.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(isVisibleShade(await style(creatable, 'background-color')), 'a shade on hover').toBe(true);
  });
});

// A placeholder is drawn in the secondary text colour at full opacity, not the browser's faded grey.
// Fixture: web/pv/placeholder-guard.zul (the chosenbox is left out: ZK never renders a placeholder on its input)
test.describe('placeholder', () => {
  const FIELDS = [
    { widget: 'datebox', input: '.ph-datebox .z-datebox-input' },
    { widget: 'timebox', input: '.ph-timebox .z-timebox-input' },
    { widget: 'bandbox', input: '.ph-bandbox .z-bandbox-input' },
    { widget: 'timepicker', input: '.ph-timepicker .z-timepicker-input' },
  ];

  for (const { widget, input } of FIELDS) {
    test(`${widget} — the placeholder is drawn in the secondary text colour`, async ({ page }) => {
      await open(page, '/pv/placeholder-guard.zul');
      const field = page.locator(input);
      expect(await field.evaluate(el => (el as HTMLInputElement).placeholder), 'the field has a placeholder').not.toBe('');
      expect(await style(field, 'color', '::placeholder')).toBe(await resolve(page, 'color', 'var(--zk-color-on-surface-variant)'));
      expect(await style(field, 'opacity', '::placeholder')).toBe('1');
    });
  }
});

// A collapsed groupbox, in both molds and with a caption or a plain title. The default mold wears z-groupbox-collapsed, which
// ignores an explicit height and rounds the header on all corners because the header is the whole frame; a 3d mold groupbox
// has no collapsed class, ZK only hides its content. The content of a groupbox that starts closed is never rendered, so
// the rule that hides it is reached by closing an open one.
// Fixture: web/pv/groupbox-collapsed-guard.zul
test.describe('groupbox', () => {
  test.beforeEach(async ({ page }) => {
    await open(page, '/pv/groupbox-collapsed-guard.zul');
  });

  const height = (locator: Locator) => locator.evaluate(el => el.getBoundingClientRect().height);

  for (const kind of ['caption', 'title']) {
    test(`groupbox — a closed default mold groupbox with a ${kind} shrinks to its rounded header despite an explicit height`, async ({ page }) => {
      const box = page.locator(`.gc-default-${kind}`);
      const header = box.locator('.z-groupbox-header');
      const radius = await resolve(page, 'border-bottom-left-radius', 'var(--zk-groupbox-radius)');
      await expect(box).toHaveClass(/z-groupbox-collapsed/);
      expect(await style(box, 'height'), 'the inline height of 200px is overridden').not.toBe('200px');
      expect(await height(box)).toBeLessThan(100);
      expect(await style(header, 'border-bottom-left-radius')).toBe(radius);
      expect(await style(header, 'border-bottom-right-radius')).toBe(radius);
      expect(await style(header, 'border-bottom-width'), 'the header draws the line the root border would').toBe('2px');
    });

    test(`groupbox — a closed 3d mold groupbox with a ${kind} renders no content and only its header`, async ({ page }) => {
      const box = page.locator(`.gc-3d-${kind}`);
      await expect(box).not.toHaveClass(/z-groupbox-collapsed/);
      await expect(box.locator('.z-groupbox-content')).toHaveCount(0);
      expect(await height(box), 'the box is as tall as its header').toBe(await height(box.locator('.z-groupbox-header')));
      expect(await style(box.locator('.z-groupbox-header'), 'border-bottom-left-radius'), '3d keeps a square header bottom').toBe('0px');
    });
  }

  for (const [mold, collapsedClass] of [['default', true], ['3d', false]] as const) {
    test(`groupbox — closing an open ${mold} mold groupbox hides its content${collapsedClass ? ' and rounds the header' : ''}`, async ({ page }) => {
      const box = page.locator(`.gc-toggle-${mold}`);
      const header = box.locator('.z-groupbox-header');
      const content = box.locator('.z-groupbox-content');
      const radius = await resolve(page, 'border-bottom-left-radius', 'var(--zk-groupbox-radius)');
      expect(await style(content, 'display')).toBe('block');
      expect(await style(header, 'border-bottom-left-radius'), 'an open header has a square bottom').toBe('0px');

      await header.click();
      await expect(content).toBeHidden();
      expect(await style(content, 'display')).toBe('none');
      if (collapsedClass) {
        await expect(box).toHaveClass(/z-groupbox-collapsed/);
        expect(await style(header, 'border-bottom-left-radius')).toBe(radius);
      } else {
        await expect(box).not.toHaveClass(/z-groupbox-collapsed/);
      }

      await header.click();
      await expect(content).toBeVisible();
      await expect(box).not.toHaveClass(/z-groupbox-collapsed/);
      expect(await style(header, 'border-bottom-left-radius')).toBe('0px');
    });
  }

  test('groupbox — a font icon in the caption is a 16px secondary-colour circle that shades on hover', async ({ page }) => {
    const icon = page.locator('.gc-open-icon .z-groupbox-header .z-caption i[class*="z-icon-"]');
    await page.mouse.move(0, 0);
    expect(await style(icon, 'font-size')).toBe('16px');
    expect(await style(icon, 'color')).toBe(await resolve(page, 'color', 'var(--zk-color-on-surface-variant)'));
    expect(await style(icon, 'padding-top')).toBe(await resolve(page, 'padding-top', 'var(--zk-spacing-1)'));
    expect(await style(icon, 'background-color')).toBe('rgba(0, 0, 0, 0)');
    await icon.hover();
    expect(await icon.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(icon, 'background-color')).toBe(await resolve(page, 'background-color', 'var(--zk-groupbox-icon-hover-bg)'));
  });
});

// The footer buttons of a daterangebox popup (Clear, Today, Cancel) shade their state layer on hover, focus and press, and
// draw the focus ring only for keyboard focus; a hovered day inside the committed range deepens its band; on a narrow
// viewport the popup fills the screen.
// Fixture: web/pv/daterangebox-popup-guard.zul
test.describe('daterangebox popup', () => {
  const BUTTONS = ['clear', 'today', 'cancel'];

  async function openPopup(page: Page): Promise<void> {
    await open(page, '/pv/daterangebox-popup-guard.zul');
    await page.locator('.z-daterangebox-button').click();
    await expect(page.locator('.z-daterangebox-popup')).toBeVisible();
    await expectSettled(page.locator('.z-daterangebox-popup-footer'));
    await page.mouse.move(0, 0);
  }

  const stateLayer = (button: Locator) => style(button, 'opacity', '::before');

  for (const name of BUTTONS) {
    test(`daterangebox — the ${name} button shades on hover and press, and keyboard focus adds the ring and the focus shade`, async ({ page }) => {
      await openPopup(page);
      const button = page.locator(`.z-daterangebox-popup-${name}`);
      expect(await stateLayer(button), 'at rest').toBe('0');

      await button.hover();
      expect(await button.evaluate(el => el.matches(':hover'))).toBe(true);
      expect(await stateLayer(button), 'hover').toBe(await resolve(page, 'opacity', 'var(--zk-state-hover-opacity)'));

      await page.mouse.down();
      expect(await stateLayer(button), 'pressed').toBe(await resolve(page, 'opacity', 'var(--zk-state-pressed-opacity)'));
      await page.mouse.move(0, 0);
      await page.mouse.up();

      await openPopup(page);
      await tabTo(page, button);
      expect(await button.evaluate(el => el.matches(':focus-visible'))).toBe(true);
      expect(await stateLayer(button), 'focus').toBe(await resolve(page, 'opacity', 'var(--zk-state-focus-opacity)'));
      expect(await outlineOf(button), 'the ring is --zk-focus-ring').toEqual(await ringToken(page));
      expect(await style(button, 'outline-offset')).toBe('2px');
    });
  }

  test('daterangebox — a hovered day inside the committed range deepens the band', async ({ page }) => {
    await openPopup(page);
    const day = page.locator('.z-daterangebox-popup .z-calendar-cell.z-cell-range-mid').first();
    const band = await style(day, 'background-color', '::before');
    await day.hover();
    expect(await day.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(day, 'background-color', '::before'))
      .toBe(await resolve(page, 'background-color', 'color-mix(in srgb, var(--zk-color-primary-container) 85%, var(--zk-color-on-primary-container))'));
    expect(band, 'the resting band is the container colour').toBe(await resolve(page, 'background-color', 'var(--zk-color-primary-container)'));
  });

  test('daterangebox — on a narrow viewport the popup spans the viewport in one column with the footer at the bottom', async ({ page }) => {
    await page.setViewportSize({ width: 500, height: 800 });
    await openPopup(page);
    const popup = page.locator('.z-daterangebox-popup');
    expect(await popup.evaluate(el => [el.getBoundingClientRect().width, el.getBoundingClientRect().height])).toEqual([500, 800]);
    expect(await style(page.locator('.z-daterangebox-popup-panels'), 'grid-template-columns').then(columns => columns.split(' ').length), 'one column').toBe(1);
    const footerBottom = await page.locator('.z-daterangebox-popup-footer').evaluate(el => el.getBoundingClientRect().bottom);
    expect(808 - footerBottom, 'the footer sits at the bottom edge of the popup, inside its padding').toBeLessThan(40);
  });

  // The media rule says position: fixed; inset: 0, but the widget writes `position: absolute; top: 8px; left: 8px` into the
  // popup's style attribute, which wins: the full-viewport popup sits 8px off the origin and overflows by 8px.
  test.fixme('daterangebox — on a narrow viewport the popup sits at the viewport origin', async ({ page }) => {
    await page.setViewportSize({ width: 500, height: 800 });
    await openPopup(page);
    const popup = page.locator('.z-daterangebox-popup');
    expect(await style(popup, 'position')).toBe('fixed');
    expect(await popup.evaluate(el => [el.getBoundingClientRect().left, el.getBoundingClientRect().top])).toEqual([0, 0]);
  });
});

// Keyboard focus (:focus-visible) draws the state layer that a pointer-focused or hovered control does not.
// A tab is a roving-tabindex stop: Tab reaches the selected tab, and only the keyboard satisfies :focus-visible.
test.describe('keyboard focus', () => {
  test('tab — the keyboard focus shades the selected tab with the focus state layer', async ({ page }) => {
    await open(page, '/tabbox.zul');
    const tab = page.locator('.z-tab.z-tab-selected').first();
    const layer = () => style(tab, 'opacity', '::before');
    await page.mouse.move(0, 0);
    expect(await layer(), 'at rest').toBe('0');
    await tabTo(page, tab);
    expect(await tab.evaluate(el => el.matches(':focus-visible'))).toBe(true);
    expect(await layer(), 'keyboard focus').toBe(await resolve(page, 'opacity', 'var(--zk-state-focus-opacity)'));
  });

  // The popup that moves a portallayout panel without dragging (za11y, WCAG 2.5.7): Space on a focused panel opens it and
  // focuses its first column; the item with the keyboard focus gets the focus state layer and Marble's ring.
  test('portallayout — the keyboard-focused item of the move popup draws the focus state layer and the focus ring', async ({ page }) => {
    await open(page, '/portallayout.zul');
    const panel = page.locator('.z-portallayout').first().locator('.z-panel').first();
    await tabTo(page, panel);
    await page.keyboard.press(' ');
    const item = page.locator('.z-portallayout-popup-nav').first();
    await expect(item).toBeVisible();
    await expect.poll(() => item.evaluate(el => el === document.activeElement), 'the popup focuses its first item').toBe(true);
    expect(await item.evaluate(el => el.matches(':focus-visible'))).toBe(true);
    expect(await style(item, 'opacity', '::before')).toBe(await resolve(page, 'opacity', 'var(--zk-state-focus-opacity)'));
    expect(await outlineOf(item), 'the ring is --zk-focus-ring').toEqual(await ringToken(page));
    expect(await style(item, 'outline-offset')).toBe('-2px');

    await page.keyboard.press('ArrowDown');
    expect(await style(item, 'opacity', '::before'), 'the item the focus left').toBe('0');
    expect(await style(item, 'outline-style')).toBe('none');
  });
});

// Biglistbox (EE): the pointer hovers a row, a click selects it, the arrow keys move the selection, and the pointer hovers
// the sortable header and the scroll parts. Rows are the list-row family of Listbox; with za11y active the scroll
// buttons, the grip and the frozen-column tick draw their hover and pressed state as a flat tint over the part.
// Fixture: the preview page biglistbox.zul
test.describe('biglistbox', () => {
  test.beforeEach(async ({ page }) => {
    await open(page, '/biglistbox.zul');
  });

  test('biglistbox — a hovered row takes the hover background, a clicked row the selected background and text colour', async ({ page }) => {
    const rows = page.locator('.z-biglistbox').first().locator('.z-biglistbox-row');
    const row = rows.nth(1);
    const cell = row.locator('td').first();
    await page.mouse.move(0, 0);
    const restingColor = await style(cell, 'color');
    expect(await style(row, 'background-color'), 'at rest').toBe('rgba(0, 0, 0, 0)');

    await row.hover();
    expect(await row.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(row, 'background-color'), 'hover').toBe(await resolve(page, 'background-color', 'var(--zk-biglistbox-row-hover-bg)'));

    await cell.click();
    await expect(row).toHaveClass(/z-biglistbox-selected/);
    expect(await style(row, 'background-color'), 'selected and hovered')
      .toBe(await resolve(page, 'background-color', 'color-mix(in srgb, var(--zk-biglistbox-row-selected-fg) 8%, var(--zk-biglistbox-row-selected-bg))'));
    expect(await style(cell, 'color')).toBe(await resolve(page, 'color', 'var(--zk-biglistbox-row-selected-fg)'));
    expect(await style(cell, 'color'), 'the selection recolours the text').not.toBe(restingColor);

    await page.mouse.move(0, 0);
    expect(await style(row, 'background-color'), 'selected, pointer away').toBe(await resolve(page, 'background-color', 'var(--zk-biglistbox-row-selected-bg)'));
  });

  test('biglistbox — the arrow keys move the selection and the selected row wears the focus ring only for the keyboard', async ({ page }) => {
    const rows = page.locator('.z-biglistbox').first().locator('.z-biglistbox-row');
    await rows.nth(1).locator('td').first().click();
    await page.mouse.move(0, 0);
    await expect(rows.nth(1)).toHaveClass(/z-biglistbox-selected/);
    expect(await style(rows.nth(1), 'outline-style'), 'a pointer selection draws no ring').toBe('none');

    await page.keyboard.press('ArrowDown');
    await expect(rows.nth(2)).toHaveClass(/z-biglistbox-selected/);
    await expect(rows.nth(1)).not.toHaveClass(/z-biglistbox-selected/);
    expect(await outlineOf(rows.nth(2)), 'the keyboard selection draws --zk-focus-ring').toEqual(await ringToken(page));
    expect(await style(rows.nth(2), 'outline-offset')).toBe('-2px');
    expect(await style(rows.nth(1), 'outline-style')).toBe('none');
  });

  test('biglistbox — a sortable header tints on hover', async ({ page }) => {
    const header = page.locator('.z-biglistbox-sort').first();
    await page.mouse.move(0, 0);
    expect(await style(header, 'background-color'), 'at rest').toBe('rgba(0, 0, 0, 0)');
    await header.hover();
    expect(await header.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(header, 'background-color')).toBe(await resolve(page, 'background-color', 'color-mix(in srgb, var(--zk-color-on-surface) 4%, transparent)'));
  });

  // za11y paints the same surface colour on the grip at rest and on hover, so the grip does not move; the buttons and the
  // tick put a tint over the part instead.
  test('biglistbox — the scroll grip keeps its surface colour on hover', async ({ page }) => {
    const grip = page.locator('.z-biglistbox-wscroll-vertical > .z-biglistbox-wscroll-drag').first();
    await grip.hover();
    expect(await grip.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(grip, 'background-color')).toBe(await resolve(page, 'background-color', 'var(--zk-color-surface)'));
  });

  // The grip rests at the head, so down and end can scroll and home and up are dimmed: they take no tint. A press on end
  // jumps to the tail, where end itself is dimmed, so only down is held pressed.
  for (const part of ['down', 'end']) {
    test(`biglistbox — the ${part} scroll button tints on hover${part === 'down' ? ' and more when pressed' : ''}`, async ({ page }) => {
      const button = page.locator('.z-biglistbox-wscroll-vertical').first().locator(`.z-biglistbox-wscroll-${part}`);
      await page.mouse.move(0, 0);
      expect(await style(button, 'background-image'), 'at rest').toBe('none');
      await button.hover();
      expect(await button.evaluate(el => el.matches(':hover'))).toBe(true);
      const hover = await style(button, 'background-image');
      expect(hover).toContain('linear-gradient');
      if (part !== 'down') return;
      await page.mouse.down();
      const pressed = await style(button, 'background-image');
      await page.mouse.up();
      expect(pressed).toContain('linear-gradient');
      expect(pressed, 'the pressed tint is stronger than the hover tint').not.toBe(hover);
    });
  }

  for (const part of ['home', 'up']) {
    test(`biglistbox — the ${part} scroll button is dimmed at the head and takes no tint on hover`, async ({ page }) => {
      const button = page.locator('.z-biglistbox-wscroll-vertical').first().locator(`.z-biglistbox-wscroll-${part}`);
      await button.hover();
      expect(await button.evaluate(el => el.matches(':hover'))).toBe(true);
      expect(await style(button, 'opacity')).toBe(await resolve(page, 'opacity', 'var(--zk-state-disabled-opacity)'));
      expect(await style(button, 'background-image')).toBe('none');
    });
  }

  test('biglistbox — the frozen-column tick tints on hover and more when pressed', async ({ page }) => {
    const tick = page.locator('.z-biglistbox-verticalbar-tick').first();
    await tick.scrollIntoViewIfNeeded();
    await page.mouse.move(0, 0);
    expect(await style(tick, 'background-image'), 'at rest').toBe('none');
    await tick.hover();
    expect(await tick.evaluate(el => el.matches(':hover'))).toBe(true);
    const hover = await style(tick, 'background-image');
    expect(hover).toContain('linear-gradient');
    await page.mouse.down();
    const pressed = await style(tick, 'background-image');
    await page.mouse.up();
    expect(pressed).toContain('linear-gradient');
    expect(pressed, 'the pressed tint is stronger than the hover tint').not.toBe(hover);
  });
});

// Goldenlayout (EE): the pointer hovers a tab, a tab's close button, a stack's header controls and a splitter; a click on
// the maximise control lets one stack fill the layout; pressing a splitter marks it as being dragged.
// Fixture: the preview page goldenlayout.zul (the first layout has stacks and splitters, the second closable tabs)
test.describe('goldenlayout', () => {
  test.beforeEach(async ({ page }) => {
    await open(page, '/goldenlayout.zul');
    await page.mouse.move(0, 0);
  });

  test('goldenlayout — a hovered tab takes the hover text colour and the hover state layer', async ({ page }) => {
    const layout = page.locator('.z-goldenlayout').first();
    const tab = layout.locator('.lm_tab:not(.lm_active)').first();
    const colorAtRest = await style(tab, 'color');
    expect(await style(tab, 'opacity', '::before'), 'at rest').toBe('0');
    await tab.hover();
    expect(await tab.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(tab, 'color')).toBe(await resolve(page, 'color', 'var(--zk-goldenlayout-fg-hover)'));
    expect(await style(tab, 'color')).not.toBe(colorAtRest);
    expect(await style(tab, 'opacity', '::before')).toBe(await resolve(page, 'opacity', 'var(--zk-state-hover-opacity)'));
  });

  test('goldenlayout — a hovered close button of a tab is shaded', async ({ page }) => {
    const close = page.locator('.z-goldenlayout').nth(1).locator('.lm_close_tab').first();
    expect(await style(close, 'background-color'), 'at rest').toBe('rgba(0, 0, 0, 0)');
    await close.hover();
    expect(await close.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(close, 'background-color')).toBe(await resolve(page, 'background-color', 'color-mix(in srgb, var(--zk-color-on-surface) 12%, transparent)'));
    expect(await style(close, 'color')).toBe(await resolve(page, 'color', 'var(--zk-goldenlayout-fg-hover)'));
  });

  test('goldenlayout — a hovered header control takes the accent colour and a shade', async ({ page }) => {
    const control = page.locator('.z-goldenlayout').first().locator('.lm_controls > li.lm_maximise').first();
    expect(await style(control, 'background-color'), 'at rest').toBe('rgba(0, 0, 0, 0)');
    await control.hover();
    expect(await control.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(control, 'color')).toBe(await resolve(page, 'color', 'var(--zk-goldenlayout-accent)'));
    expect(await style(control, 'background-color')).toBe(await resolve(page, 'background-color', 'color-mix(in srgb, var(--zk-color-on-surface) 8%, transparent)'));
  });

  test('goldenlayout — a hovered splitter shades, fills its pill with the hover colours and darkens while dragged', async ({ page }) => {
    const splitter = page.locator('.z-goldenlayout').first().locator('.lm_splitter').first();
    expect(await style(splitter, 'background-color'), 'at rest').toBe('rgba(0, 0, 0, 0)');
    await splitter.hover();
    expect(await splitter.evaluate(el => el.matches(':hover'))).toBe(true);
    const hover = await resolve(page, 'background-color', 'color-mix(in srgb, var(--zk-color-primary) calc(var(--zk-state-hover-opacity) * 100%), transparent)');
    expect(await style(splitter, 'background-color'), 'hover').toBe(hover);
    expect(await style(splitter, 'background-color', '::before'), 'pill').toBe(await resolve(page, 'background-color', 'var(--zk-splitter-pill-fill-hover)'));
    expect(await style(splitter, 'background-color', '::after'), 'grips').toBe(await resolve(page, 'background-color', 'var(--zk-splitter-pill-icon-hover)'));

    const box = (await splitter.boundingBox())!;
    await page.mouse.down();
    await page.mouse.move(box.x + box.width / 2 + 12, box.y + box.height / 2 + 12, { steps: 4 });
    await expect(splitter).toHaveClass(/lm_dragging/);
    expect(await style(splitter, 'background-color'), 'while dragged')
      .toBe(await resolve(page, 'background-color', 'color-mix(in srgb, var(--zk-color-primary) calc(var(--zk-state-pressed-opacity) * 100%), transparent)'));
    await page.mouse.up();
  });

  test('goldenlayout — the maximise control lets one stack fill the layout and swaps its icon for the restore one', async ({ page }) => {
    const layout = page.locator('.z-goldenlayout').first();
    const stack = layout.locator('.lm_item.lm_stack').first();
    const maximise = stack.locator('.lm_controls .lm_maximise');
    const iconAtRest = await style(maximise, '-webkit-mask-image', '::after');
    expect(await style(stack, 'position')).toBe('relative');

    await maximise.click();
    await expect(stack).toHaveClass(/lm_maximised/);
    expect(await style(stack, 'position')).toBe('absolute');
    expect(await style(stack, 'z-index')).toBe('40');
    expect(await stack.evaluate(el => el.getBoundingClientRect().width)).toBeGreaterThan(await layout.evaluate(el => el.getBoundingClientRect().width) * 0.95);
    expect(await style(maximise, '-webkit-mask-image', '::after'), 'the restore glyph').not.toBe(iconAtRest);
  });
});

// A submenu inside a menupopup is a row like a menuitem with a caret on its trailing edge: it shades its state layer on
// hover, dims when disabled, and sizes its icon and caret.
// Fixture: web/pv/menupopup-submenu-guard.zul
test.describe('menupopup', () => {
  test.beforeEach(async ({ page }) => {
    await open(page, '/pv/menupopup-submenu-guard.zul');
    await page.locator('.z-button').click();
    await expect(page.locator('.z-menupopup').first()).toBeVisible();
    await expectSettled(page.locator('.z-menupopup .z-menu').first());
    await page.mouse.move(0, 0);
  });

  const submenu = (page: Page) => page.locator('.z-menupopup > .z-menupopup-content > .z-menu').first();
  const layer = (menu: Locator) => style(menu.locator('.z-menu-content'), 'opacity', '::before');

  test('menu — a submenu row is a flex row at the menuitem height with an 18px leading icon and a trailing 12px caret', async ({ page }) => {
    const menu = submenu(page);
    const content = menu.locator('.z-menu-content');
    expect(await style(menu, 'display')).toBe('block');
    expect(await style(content, 'display')).toBe('flex');
    expect(Number.parseFloat(await style(content, 'min-height'))).toBe(Number.parseFloat(await resolve(page, 'min-height', 'var(--zk-menuitem-height)')));
    expect(await style(content.locator('i.z-icon-cog'), 'font-size')).toBe('18px');
    const caret = content.locator('.z-menu-icon');
    expect(await style(caret, 'font-size')).toBe('12px');
    expect(Number.parseFloat(await style(caret, 'margin-left')), 'the caret is pushed to the trailing edge').toBeGreaterThan(0);
  });

  test('menu — a hovered submenu row takes the hover state layer and opens its popup', async ({ page }) => {
    const menu = submenu(page);
    expect(await layer(menu), 'at rest').toBe('0');
    await menu.hover();
    await expect(menu).toHaveClass(/z-menu-hover/);
    expect(await layer(menu), 'hover').toBe(await resolve(page, 'opacity', 'var(--zk-state-hover-opacity)'));

    await menu.locator('.z-menu-content').click();
    await expect(page.locator('.z-menupopup:visible')).toHaveCount(2);
    expect(await layer(menu), 'the row keeps the hover layer while its popup is open').toBe(await resolve(page, 'opacity', 'var(--zk-state-hover-opacity)'));
  });

  test('menu — a disabled submenu row is dimmed, shows the not-allowed cursor and ignores the pointer', async ({ page }) => {
    const content = page.locator('.z-menupopup .z-menu-content[disabled]');
    expect(await style(content, 'opacity')).toBe(await resolve(page, 'opacity', 'var(--zk-state-disabled-opacity)'));
    expect(await style(content, 'cursor')).toBe('not-allowed');
    expect(await style(content, 'pointer-events')).toBe('none');
  });
});
