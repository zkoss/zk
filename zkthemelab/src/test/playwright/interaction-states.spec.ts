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

/** The pixels of the element's screenshot: the brightest one (by channel sum), leaving out a 4px frame where the rounded
 *  corners show the page, and the one 6px in from the left edge. */
async function pixelsOf(element: Locator) {
  const { PNG } = require('playwright-core/lib/utilsBundle');
  const { data, width, height } = PNG.sync.read(await element.screenshot());
  let brightest = [0, 0, 0];
  for (let y = 4; y < height - 4; y++)
    for (let x = 4; x < width - 4; x++) {
      const i = 4 * (y * width + x);
      if (data[i] + data[i + 1] + data[i + 2] > brightest[0] + brightest[1] + brightest[2]) brightest = [data[i], data[i + 1], data[i + 2]];
    }
  const at = 4 * (Math.floor(height / 2) * width + 6);
  return { brightest, background: [data[at], data[at + 1], data[at + 2]] as number[] };
}
const channelSum = (rgb: number[]) => rgb[0] + rgb[1] + rgb[2];

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
      await tabTo(page, control);
      expect(await control.evaluate(el => el.matches(':focus-visible')), 'the keyboard focus is a focus-visible one').toBe(true);
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

// Chosenbox: the remove button of a chip and the option that creates a new chip draw the hover state layer, a translucent
// --zk-color-on-surface at --zk-state-hover-opacity, over the transparent rest.
// Fixture: web/pv/popup-item-states-guard.zul
test.describe('chosenbox', () => {
  const HOVER_LAYER = 'color-mix(in srgb, var(--zk-color-on-surface) calc(var(--zk-state-hover-opacity) * 100%), transparent)';
  const TRANSPARENT = 'rgba(0, 0, 0, 0)';

  test.beforeEach(async ({ page }) => {
    await open(page, '/pv/popup-item-states-guard.zul');
    await page.mouse.move(0, 0);
  });

  test('chosenbox — hovering the remove button of a chip shades it', async ({ page }) => {
    const button = page.locator('.z-chosenbox-item .z-chosenbox-button').first();
    // za11y moves the shade from the button to a ::before circle (a 24px hit box with a 20px circle).
    const shade = () => button.evaluate(el => [getComputedStyle(el).backgroundColor, getComputedStyle(el, '::before').backgroundColor]);
    const layer = await resolve(page, 'background-color', HOVER_LAYER);
    expect(await shade(), 'no shade at rest').toEqual([TRANSPARENT, TRANSPARENT]);
    await button.hover();
    expect(await button.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await shade(), 'the hover state layer').toContain(layer);
  });

  // The za11y sheet moves the shade to a ::before circle and clears the button's own, which hides chosenbox.css from the
  // test above, so this one switches that sheet off.
  test('chosenbox — without the za11y sheet the remove button itself carries the hover state layer', async ({ page }) => {
    await page.evaluate(() => {
      for (const sheet of Array.from(document.styleSheets))
        if (sheet.href?.includes('/za11y/css/marble')) sheet.disabled = true;
    });
    const button = page.locator('.z-chosenbox-item .z-chosenbox-button').first();
    expect(await style(button, 'background-color'), 'no shade at rest').toBe(TRANSPARENT);
    await button.hover();
    expect(await button.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(button, 'background-color'), 'the hover state layer').toBe(await resolve(page, 'background-color', HOVER_LAYER));
  });

  test('chosenbox — hovering the option that creates a new item shades it', async ({ page }) => {
    await page.locator('.z-chosenbox-input').click();
    await page.keyboard.type('zzz');
    const creatable = page.locator('.z-chosenbox-empty-creatable');
    await expect(creatable).toBeVisible();
    // ZK highlights the only option when the popup opens; without that class only the pointer can shade it.
    await creatable.evaluate(el => el.classList.remove('z-chosenbox-option-hover'));
    await page.mouse.move(0, 0);
    expect(await style(creatable, 'background-color'), 'no shade before the pointer arrives').toBe(TRANSPARENT);
    await creatable.hover();
    expect(await creatable.evaluate(el => el.matches(':hover'))).toBe(true);
    expect(await style(creatable, 'background-color'), 'the hover state layer').toBe(await resolve(page, 'background-color', HOVER_LAYER));
  });
});

// A placeholder is drawn in the secondary text colour, not the browser's grey.
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
    });
  }
});

// A collapsed groupbox, in both molds and with a caption or a plain title. The default mold wears z-groupbox-collapsed, which
// ignores an explicit height and rounds the header on all corners because the header is the whole frame; a 3d mold groupbox
// has no collapsed class. In both molds ZK hides the content with an inline display:none, so the CSS controls the root
// and the header only.
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
      // Only the 3d rules give the header the container surface and the narrower inner padding.
      const header = box.locator('.z-groupbox-header');
      expect(await style(header, 'background-color'), '3d header surface').toBe(await resolve(page, 'background-color', 'var(--zk-color-surface-container-low)'));
      expect(await style(header, 'padding-left'), '3d header padding').toBe(await resolve(page, 'padding-left', 'var(--zk-spacing-3)'));
      expect(await style(header, 'border-bottom-left-radius'), '3d keeps a square header bottom').toBe('0px');
    });
  }

  for (const [mold, collapsedClass] of [['default', true], ['3d', false]] as const) {
    test(`groupbox — closing an open ${mold} mold groupbox hides its content${collapsedClass ? ', shrinks the box to its rounded header' : ''}`, async ({ page }) => {
      const box = page.locator(`.gc-toggle-${mold}`);
      const header = box.locator('.z-groupbox-header');
      const content = box.locator('.z-groupbox-content');
      const radius = await resolve(page, 'border-bottom-left-radius', 'var(--zk-groupbox-radius)');
      expect(await style(header, 'border-bottom-left-radius'), 'an open header has a square bottom').toBe('0px');
      expect(await height(box), 'an open box is taller than its header').toBeGreaterThan(await height(header));

      await header.click();
      await expect(content).toBeHidden();
      if (collapsedClass) {
        await expect(box).toHaveClass(/z-groupbox-collapsed/);
        expect(await height(box), 'the box is as tall as its header, despite the inline height').toBe(await height(header));
        expect(await style(header, 'border-bottom-left-radius')).toBe(radius);
        expect(await style(header, 'border-bottom-width'), 'the header draws the line the root border would').toBe('2px');
      } else {
        await expect(box).not.toHaveClass(/z-groupbox-collapsed/);
        expect(await style(header, 'border-bottom-left-radius'), '3d keeps a square header bottom').toBe('0px');
      }

      await header.click();
      await expect(content).toBeVisible();
      await expect(box).not.toHaveClass(/z-groupbox-collapsed/);
      expect(await style(header, 'border-bottom-left-radius')).toBe('0px');
      expect(await style(header, 'border-bottom-width')).toBe('1px');
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
    expect(800 - footerBottom, 'the footer sits at the bottom edge of the popup, inside its padding').toBeLessThan(40);
  });

  // The media rule says position: fixed; inset: 0, but the widget writes `position: absolute; top: 8px; left: 8px` into the
  // popup's style attribute; only `!important` on the two properties beats it.
  test('daterangebox — on a narrow viewport the popup sits at the viewport origin', async ({ page }) => {
    await page.setViewportSize({ width: 500, height: 800 });
    await openPopup(page);
    const popup = page.locator('.z-daterangebox-popup');
    expect(await style(popup, 'position')).toBe('fixed');
    expect(await popup.evaluate(el => [el.getBoundingClientRect().left, el.getBoundingClientRect().top])).toEqual([0, 0]);
    expect(await page.evaluate(() => [document.documentElement.scrollWidth, document.documentElement.scrollHeight]), 'the page does not overflow').toEqual([500, 800]);
  });

  // calendar.css caps a calendar at 268px; in the single-column popup that left the calendar hugging the left edge.
  test('daterangebox — on a narrow viewport the calendar fills the popup between the panel paddings', async ({ page }) => {
    await page.setViewportSize({ width: 500, height: 800 });
    await openPopup(page);
    const { left, right, padding } = await page.evaluate(() => {
      const popup = document.querySelector('.z-daterangebox-popup')!.getBoundingClientRect();
      const calendar = document.querySelector('.z-daterangebox-popup .z-calendar')!.getBoundingClientRect();
      const panelsPadding = parseFloat(getComputedStyle(document.querySelector('.z-daterangebox-popup-panels')!).paddingLeft);
      return { left: calendar.left - popup.left, right: popup.right - calendar.right, padding: panelsPadding };
    });
    expect(left, 'the left gap is the panel padding').toBe(padding);
    expect(right, 'the right gap is the panel padding').toBe(padding);
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
    const rectAtRest = await stack.evaluate(el => el.getBoundingClientRect().toJSON());

    await maximise.click();
    await expect(stack).toHaveClass(/lm_maximised/);
    expect(await style(stack, 'position')).toBe('absolute');
    expect(await style(stack, 'z-index')).toBe('40');
    expect(await stack.evaluate(el => el.getBoundingClientRect().width)).toBeGreaterThan(await layout.evaluate(el => el.getBoundingClientRect().width) * 0.95);
    expect(await style(maximise, '-webkit-mask-image', '::after'), 'the restore glyph').not.toBe(iconAtRest);
    // The library keeps the stack's slot with an empty placeholder; it must take no space beside the siblings.
    expect(await style(layout.locator('.lm_maximise_place'), 'display'), 'the placeholder').toBe('none');

    await maximise.click();
    await expect(stack).not.toHaveClass(/lm_maximised/);
    await expect(layout.locator('.lm_maximise_place')).toHaveCount(0);
    expect(await stack.evaluate(el => el.getBoundingClientRect().toJSON()), 'restored to its rect').toEqual(rectAtRest);
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

// A filled button's state layer is stronger than the global tokens (hover 0.16, pressed 0.24) and darkens a coloured
// fill, while the dark and light fills move away from their own colour. The outlined and text variants keep the token
// strength and their own overlay colour; a disabled button has no layer.
// Fixture: the preview page button
test.describe('button state layer', () => {
  const BLACK = 'rgb(0, 0, 0)';
  const FILLED = [
    { variant: 'primary', sclass: '', overlay: BLACK },
    { variant: 'secondary', sclass: '.z-button-secondary', overlay: BLACK },
    { variant: 'success', sclass: '.z-button-success', overlay: BLACK },
    { variant: 'warning', sclass: '.z-button-warning', overlay: BLACK },
    { variant: 'error', sclass: '.z-button-error', overlay: BLACK },
    { variant: 'info', sclass: '.z-button-info', overlay: BLACK },
    { variant: 'dark', sclass: '.z-button-dark', overlay: 'rgb(255, 255, 255)' },
    { variant: 'light', sclass: '.z-button-light', overlay: 'rgb(51, 51, 51)' },
  ];
  const button = (page: Page, sclass: string, state = ':not([disabled])') => page.locator(`.z-button${sclass}${state}`).first();

  test.beforeEach(async ({ page }) => {
    await open(page, '/button.zul');
    await page.mouse.move(0, 0);
  });

  for (const { variant, sclass, overlay } of FILLED) {
    test(`${variant} fill — hover layer is 0.16, pressed layer is 0.24, in the variant's overlay colour`, { tag: '@ZK-6112' }, async ({ page }) => {
      const target = button(page, sclass);
      await target.hover();
      expect(await target.evaluate(el => el.matches(':hover'))).toBe(true);
      expect(await style(target, 'opacity', '::before'), 'hover').toBe('0.16');
      expect(await style(target, 'background-color', '::before'), 'overlay colour').toBe(overlay);

      await page.mouse.down();
      expect(await style(target, 'opacity', '::before'), 'pressed while hovering').toBe('0.24');
      expect(await style(target, 'background-color', '::before'), 'overlay colour').toBe(overlay);
      await page.mouse.up();
    });
  }

  for (const { variant, sclass, token } of [
    { variant: 'outlined', sclass: '.z-button-outlined', token: 'var(--zk-color-primary)' },
    { variant: 'text', sclass: '.z-button-text', token: 'var(--zk-color-primary)' },
  ]) {
    test(`${variant} button — keeps the token hover and pressed layer in its own colour`, { tag: '@ZK-6112' }, async ({ page }) => {
      const target = button(page, sclass);
      await target.hover();
      expect(await target.evaluate(el => el.matches(':hover'))).toBe(true);
      expect(await style(target, 'opacity', '::before'), 'hover').toBe(await resolve(page, 'opacity', 'var(--zk-button-hover-opacity)'));
      expect(await style(target, 'background-color', '::before'), 'overlay colour').toBe(await resolve(page, 'background-color', token));

      await page.mouse.down();
      expect(await style(target, 'opacity', '::before'), 'pressed').toBe(await resolve(page, 'opacity', 'var(--zk-button-active-opacity)'));
      expect(await style(target, 'background-color', '::before'), 'overlay colour').toBe(await resolve(page, 'background-color', token));
      await page.mouse.up();
    });
  }

  for (const { variant, sclass } of FILLED.filter(({ variant }) => ['primary', 'dark', 'light'].includes(variant))) {
    test(`${variant} fill — keyboard focus layer is 0.12 and hovering the focused button raises it to 0.16`, { tag: '@ZK-6112' }, async ({ page }) => {
      const target = button(page, sclass);
      await tabTo(page, target);
      expect(await target.evaluate(el => el.matches(':focus-visible'))).toBe(true);
      expect(await style(target, 'opacity', '::before'), 'focus').toBe('0.12');
      await target.hover();
      expect(await style(target, 'opacity', '::before'), 'focus and hover').toBe('0.16');
    });
  }

  for (const { variant, sclass } of [...FILLED, { variant: 'outlined', sclass: '.z-button-outlined' }, { variant: 'text', sclass: '.z-button-text' }, { variant: 'icon', sclass: '.z-button-icon' }]) {
    test(`${variant} button — the state layer sits under the content`, { tag: '@ZK-6112' }, async ({ page }) => {
      const target = button(page, sclass);
      expect(await style(target, 'isolation'), 'the button is a stacking context').toBe('isolate');
      expect(await style(target, 'z-index', '::before'), 'the layer is behind the label').toBe('-1');
    });
  }

  test('filled primary button — a pressed button keeps its label pure white', { tag: '@ZK-6112' }, async ({ page }) => {
    const target = button(page, '');
    const resting = await pixelsOf(target);
    await target.hover();
    await page.mouse.down();
    expect(await style(target, 'opacity', '::before'), 'pressed').toBe('0.24');
    const pressed = await pixelsOf(target);
    expect(pressed.brightest, 'the label stays white').toEqual([255, 255, 255]);
    expect(channelSum(pressed.background), 'the layer darkens the fill').toBeLessThan(channelSum(resting.background));
    await page.mouse.up();
  });

  test('disabled filled button — has no state layer and ignores the pointer', { tag: '@ZK-6112' }, async ({ page }) => {
    const target = button(page, '', '[disabled]');
    expect(await style(target, 'pointer-events')).toBe('none');
    expect(await style(target, 'display', '::before'), 'the layer is not rendered').toBe('none');
  });
});

// The filled Combobutton follows the filled Button: a darkening layer at 0.16 on hover and 0.24 pressed, under the label
// and the caret on both halves. The flat toolbar mold keeps its own overlay colour and the token strengths.
// Fixture: the preview page combobutton
test.describe('combobutton state layer', () => {
  const darken = (page: Page) => resolve(page, 'background-color', 'var(--zk-state-darken-color)');

  test.beforeEach(async ({ page }) => {
    await open(page, '/combobutton.zul');
    await page.mouse.move(0, 0);
  });

  test('combobutton — the label half darkens at 0.16 on hover and 0.24 pressed, in --zk-state-darken-color', { tag: '@ZK-6112' }, async ({ page }) => {
    const content = page.locator('.z-combobutton:not(.z-combobutton-toolbar):not([disabled]) .z-combobutton-content').first();
    const resting = await pixelsOf(content);
    await content.hover({ position: { x: 10, y: 10 } });
    expect(await style(content, 'opacity', '::before'), 'hover').toBe('0.16');
    expect(await style(content, 'background-color', '::before')).toBe(await darken(page));
    await page.mouse.down();
    expect(await style(content, 'opacity', '::before'), 'pressed').toBe('0.24');
    const pressed = await pixelsOf(content);
    expect(pressed.brightest, 'the label stays white').toEqual([255, 255, 255]);
    expect(channelSum(pressed.background), 'the layer darkens the fill').toBeLessThan(channelSum(resting.background));
    await page.mouse.up();
  });

  test('combobutton — the arrow half darkens at 0.16 on hover and 0.24 pressed, in --zk-state-darken-color', { tag: '@ZK-6112' }, async ({ page }) => {
    const arrow = page.locator('.z-combobutton:not(.z-combobutton-toolbar):not([disabled]) .z-combobutton-button').first();
    await arrow.hover();
    expect(await style(arrow, 'opacity', '::before'), 'hover').toBe('0.16');
    expect(await style(arrow, 'background-color', '::before')).toBe(await darken(page));
    await page.mouse.down();
    expect(await style(arrow, 'opacity', '::before'), 'pressed').toBe('0.24');
    // The 14px caret is thin and antialiased, so its brightest pixel is near white, not pure white; a layer over it would cap it at 194.
    expect(Math.min(...(await pixelsOf(arrow)).brightest), 'the caret stays white').toBeGreaterThanOrEqual(240);
    await page.mouse.up();
  });

  test('combobutton — both state layers sit under their content', { tag: '@ZK-6112' }, async ({ page }) => {
    const root = page.locator('.z-combobutton:not([disabled])');
    for (const part of ['.z-combobutton-content', '.z-combobutton-button']) {
      const target = root.first().locator(part);
      expect(await style(target, 'z-index', '::before'), `${part} layer`).toBe('-1');
    }
    expect(await style(root.first().locator('.z-combobutton-content'), 'isolation'), 'the label half is a stacking context').toBe('isolate');
    expect(await style(root.first().locator('.z-combobutton-button'), 'z-index'), 'the arrow half is a stacking context').toBe('1');
  });

  const filledRoot = (page: Page) => page.locator('.z-combobutton:not(.z-combobutton-toolbar):not([disabled])').first();

  test('combobutton — keyboard-focused: the label layer is 0.12, hovering it raises it to 0.16 and pressing to 0.24', { tag: '@ZK-6112' }, async ({ page }) => {
    const root = filledRoot(page);
    const content = root.locator('.z-combobutton-content');
    await tabTo(page, root);
    expect(await root.evaluate(el => el.matches(':focus-within')), 'the combobutton has the focus').toBe(true);
    expect(await style(content, 'opacity', '::before'), 'focus').toBe('0.12');
    await content.hover({ position: { x: 10, y: 10 } });
    expect(await style(content, 'opacity', '::before'), 'focus and hover').toBe('0.16');
    await page.mouse.down();
    expect(await style(content, 'opacity', '::before'), 'focus and pressed').toBe('0.24');
    await page.mouse.up();
  });

  test('combobutton — open: both halves carry 0.24 in --zk-state-darken-color, show one colour, and hovering either half changes nothing', { tag: '@ZK-6112' }, async ({ page }) => {
    const root = filledRoot(page);
    const halves = { label: root.locator('.z-combobutton-content'), arrow: root.locator('.z-combobutton-button') };
    await halves.arrow.click();
    await expect(root).toHaveClass(/z-combobutton-open/);
    await page.mouse.move(0, 0);
    const expectOneColour = async (when: string) => {
      for (const [name, half] of Object.entries(halves)) {
        expect(await style(half, 'opacity', '::before'), `${when}: ${name} layer`).toBe('0.24');
        expect(await style(half, 'background-color', '::before'), `${when}: ${name} overlay colour`).toBe(await darken(page));
      }
      const label = (await pixelsOf(halves.label)).background;
      const arrow = (await pixelsOf(halves.arrow)).background;
      expect(Math.abs(channelSum(label) - channelSum(arrow)), `${when}: the halves show the same colour (${label} / ${arrow})`).toBeLessThanOrEqual(3);
    };
    await expectOneColour('open');
    await halves.label.hover({ position: { x: 10, y: 10 } });
    await expectOneColour('open, label hovered');
    await halves.arrow.hover();
    await expectOneColour('open, arrow hovered');
  });

  test('combobutton toolbar mold — stays flat: its own overlay colour and the token strengths', { tag: '@ZK-6112' }, async ({ page }) => {
    const content = page.locator('.z-combobutton-toolbar:not([disabled]) .z-combobutton-content').first();
    await content.hover({ position: { x: 10, y: 10 } });
    expect(await style(content, 'opacity', '::before'), 'hover').toBe(await resolve(page, 'opacity', 'var(--zk-combobutton-hover-opacity)'));
    expect(await style(content, 'background-color', '::before')).toBe(await resolve(page, 'background-color', 'var(--zk-color-on-surface-variant)'));
    await page.mouse.down();
    expect(await style(content, 'opacity', '::before'), 'pressed').toBe(await resolve(page, 'opacity', 'var(--zk-combobutton-active-opacity)'));
    await page.mouse.up();
  });

  test('combobutton toolbar mold — open: neither half gets the filled open 0.24', { tag: '@ZK-6112' }, async ({ page }) => {
    await stopTransitions(page);
    const root = page.locator('.z-combobutton-toolbar:not([disabled])').first();
    await root.locator('.z-combobutton-button').click();
    await expect(root).toHaveClass(/z-combobutton-open/);
    await page.mouse.move(0, 0);
    // The label half keeps the toolbar open layer (--zk-combobutton-active-opacity); the arrow half has none.
    // Neither is the filled open 0.24.
    expect(await style(root.locator('.z-combobutton-content'), 'opacity', '::before'), 'label layer').toBe(await resolve(page, 'opacity', 'var(--zk-combobutton-active-opacity)'));
    expect(await style(root.locator('.z-combobutton-button'), 'opacity', '::before'), 'arrow layer').toBe('0');
  });
});
