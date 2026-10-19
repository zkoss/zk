import { test, expect, Locator, Page } from '@playwright/test';

// Pixel baselines of the pop-up layers and API-created elements that a goto-and-shoot gallery
// never paints: they exist only after a user opens them. One describe per preview page, named
// like the page so select-tests.mjs maps a CSS change on the component to these tests.
// Rationale and measurements: doc/marble-popup-gallery-report.md; inventory: doc/marble-popup-inventory.md.
//
// Rules every test follows (doc/marble-popup-gallery-report.md, .claude/skills/marble-theme/reference/verification.md):
//  - open the pop-up with the gesture a user makes; a widget API only where no gesture exists, with the reason in a comment;
//  - exactly ONE visible match of the pop-up selector, never `.first()` (pages pre-render hidden panels);
//  - the classes the shot is meant to paint are asserted present before shooting;
//  - park the cursor after the trigger (cursor position is the dominant noise source), `holdCursor` only for a measured exception.

const PAD = 12;

// Absolute count of differing pixels tolerated per shot. Measured run-to-run noise: 0 differing pixels in every shot
// over at least three runs in separate Playwright processes (doc/marble-popup-gallery-report.md), so the budget is a margin of
// 10 px for a crop, 20 px for the two shots that cover a whole 1280x720 viewport. Not a ratio: a ratio of the
// viewport shots would hide a mis-painted border (DR-3 in doc/verification-harness-decisions.md).
// POPUP_MAX_DIFF overrides it for the measurement itself (`POPUP_MAX_DIFF=0` shows the raw noise).
const DEFAULT_MAX_DIFF = 10;
const MAX_DIFF: Record<string, number> = {
  'drawer-popup-open-left.png': 20,
  'toast-popup-positions.png': 20,
};
const maxDiffFor = (file: string): number =>
  process.env.POPUP_MAX_DIFF !== undefined ? Number(process.env.POPUP_MAX_DIFF) : (MAX_DIFF[file] ?? DEFAULT_MAX_DIFF);

async function openPage(page: Page, url: string): Promise<void> {
  await page.goto(url);
  await page.waitForLoadState('networkidle');
  // A cold server answers networkidle before the widgets are bound; a click or a blur then does nothing.
  await page.waitForFunction(() => {
    const zk = (window as any).zk;
    return zk && zk.booted && !zk.loading && !zk.mounting && !zk.processing;
  });
  await page.evaluate(() => document.fonts.ready.then(() => true));
}

const parkCursor = (page: Page) => page.mouse.move(0, 0);

/** Every class must exist somewhere in the DOM now: the shot is only worth taking if it can paint them. */
async function expectClassesInDom(page: Page, classes: string[]): Promise<void> {
  // Polled: a few classes (e.g. the chosenbox create icon) are added a moment after the pop-up shows.
  await expect.poll(() => page.evaluate(
    cs => cs.filter(c => !document.querySelector(`.${CSS.escape(c)}`)), classes),
    'classes absent from the DOM after opening').toEqual([]);
}

/** Box of an element once it has stopped moving or resizing: pop-ups open with a JS slide animation CSS cannot disable. */
async function settledBox(page: Page, target: Locator): Promise<{ x: number; y: number; width: number; height: number }> {
  const SAME_READS = 3;
  let previous = '';
  let same = 0;
  for (let attempt = 0; attempt < 50; attempt++) {
    const box = await target.boundingBox();
    const current = JSON.stringify(box);
    same = box && current === previous ? same + 1 : 0;
    if (box && same >= SAME_READS) return box;
    previous = current;
    await page.waitForTimeout(100);
  }
  throw new Error('settledBox: the element kept moving for 5 s');
}

type Box = { x: number; y: number; width: number; height: number };

function unionBox(a: Box, b: Box): Box {
  const x = Math.min(a.x, b.x);
  const y = Math.min(a.y, b.y);
  return { x, y, width: Math.max(a.x + a.width, b.x + b.width) - x, height: Math.max(a.y + a.height, b.y + b.height) - y };
}

/**
 * The only visible element matching `selector`, with its classes present, captured with a PAD margin.
 * `selector` must name the pop-up root; `:visible` filters the hidden pre-rendered siblings.
 * `classes` must exist somewhere in the DOM; `inPopup` must exist inside the pop-up root, for a class that a
 * static mock on the same page already carries (a page-wide check would pass without the pop-up).
 * `alsoCapture` names a part of the pop-up that sits outside its root (the nav pop-up's label above it);
 * the capture box is the union of both.
 */
async function popupShot(page: Page, selector: string, classes: string[], file: string, inPopup: string[] = [], alsoCapture?: string): Promise<Locator> {
  const popup = page.locator(`${selector}:visible`);
  await expect(popup, `exactly one visible ${selector}`).toHaveCount(1);
  await expectClassesInDom(page, classes);
  for (const cls of inPopup) await expect(popup.locator(`.${cls}`), `.${cls} inside the pop-up`).toHaveCount(1);
  const popupBox = await settledBox(page, popup);
  const box = alsoCapture ? unionBox(popupBox, await settledBox(page, page.locator(`${alsoCapture}:visible`))) : popupBox;
  const x = Math.max(0, box.x - PAD);
  const y = Math.max(0, box.y - PAD);
  await expect(page).toHaveScreenshot(file, {
    clip: { x, y, width: box.x + box.width + PAD - x, height: box.y + box.height + PAD - y },
    animations: 'disabled',
    maxDiffPixels: maxDiffFor(file),
  });
  return popup;
}

// Enabled, editable instance that is not in a state variant: the "Default" cell of a matrix row.
const PLAIN = ':not([class*="-disabled"]):not([class*="-readonly"]):not([class*="-invalid"]):not([class*="-inplace"])';

/**
 * The widget next to a small caption label, e.g. labelled(page, 'Timezone selector', 'z-datebox'):
 * the nearest block around the FIRST occurrence of the caption that holds the widget.
 */
const labelled = (page: Page, caption: string, widgetClass: string): Locator =>
  page.locator(`span:text-is("${caption}")`).first()
    .locator(`xpath=ancestor::div[.//*[contains(concat(' ', normalize-space(@class), ' '), ' ${widgetClass} ')]][1]`)
    .locator(`.${widgetClass}`).first();

// -------------------------------------------------------
// bandbox
// -------------------------------------------------------
test.describe('bandbox', () => {
  test.beforeEach(({ page }) => openPage(page, '/bandbox.zul'));

  test('bandbox.zul: open bandpopup', async ({ page }) => {
    await page.locator(`.z-bandbox${PLAIN} .z-bandbox-button`).first().click();
    await parkCursor(page);
    await popupShot(page, '.z-bandbox-popup', ['z-bandbox-open', 'z-bandpopup'], 'bandbox-popup-open.png');
  });
});

// -------------------------------------------------------
// combobox
// -------------------------------------------------------
test.describe('combobox', () => {
  test.beforeEach(({ page }) => openPage(page, '/combobox.zul'));

  test('combobox.zul: open dropdown with a keyboard-selected item', async ({ page }) => {
    await page.locator(`.z-combobox${PLAIN} .z-combobox-button`).first().click();
    await page.keyboard.press('ArrowDown');
    await parkCursor(page);
    await popupShot(page, '.z-combobox-popup', ['z-combobox-open', 'z-comboitem', 'z-comboitem-selected'], 'combobox-popup-open-selected.png');
  });

  test('combobox.zul: open dropdown of items with a description', async ({ page }) => {
    await labelled(page, 'With description', 'z-combobox').locator('.z-combobox-button').click();
    await parkCursor(page);
    await popupShot(page, '.z-combobox-popup', ['z-combobox-open', 'z-comboitem', 'z-comboitem-inner'], 'combobox-popup-open-description.png');
  });

  test('combobox.zul: open dropdown of items with an icon', async ({ page }) => {
    await labelled(page, 'With icon', 'z-combobox').locator('.z-combobox-button').click();
    await parkCursor(page);
    await popupShot(page, '.z-combobox-popup', ['z-combobox-open', 'z-comboitem', 'z-comboitem-icon'], 'combobox-popup-open-icon.png');
  });
});

// -------------------------------------------------------
// datebox
// -------------------------------------------------------
test.describe('datebox', () => {
  test.beforeEach(({ page }) => openPage(page, '/datebox.zul'));

  test('datebox.zul: open calendar of a seeded value', async ({ page }) => {
    await labelled(page, 'Seeded value (content-fit)', 'z-datebox').locator('.z-datebox-button').click();
    await parkCursor(page);
    await popupShot(page, '.z-datebox-popup', ['z-datebox-open', 'z-datebox-popup'], 'datebox-popup-open.png');
  });

  test('datebox.zul: open calendar with the time in the popup', async ({ page }) => {
    await labelled(page, 'Time in popup (yyyy/MM/dd HH:mm)', 'z-datebox').locator('.z-datebox-button').click();
    await parkCursor(page);
    await popupShot(page, '.z-datebox-popup', ['z-datebox-open', 'z-datebox-popup'], 'datebox-popup-open-time.png');
  });

  test('datebox.zul: open calendar with the timezone selector', async ({ page }) => {
    const box = labelled(page, 'Timezone selector', 'z-datebox');
    // A typed value keeps the calendar on a fixed month; an empty box opens on the current month.
    await box.locator('input').click();
    await page.keyboard.type('Jan 15, 2025');
    await page.keyboard.press('Tab');
    // The widget takes the typed text over a moment after the blur (no request goes to the server); read it, do not guess.
    await expect.poll(() => box.evaluate(el => (window as any).zk.Widget.$(el).getValue()?.getFullYear())).toBe(2025);
    await box.locator('.z-datebox-button').click();
    await parkCursor(page);
    await popupShot(page, '.z-datebox-popup', ['z-datebox-open', 'z-datebox-popup', 'z-datebox-timezone'], 'datebox-popup-open-timezone.png');
  });
});

// -------------------------------------------------------
// daterangebox
// -------------------------------------------------------
test.describe('daterangebox', () => {
  test.beforeEach(({ page }) => openPage(page, '/daterangebox.zul'));

  const RANGE = ['z-daterangebox-popup', 'z-daterangebox-popup-panels', 'z-daterangebox-popup-footer',
    'z-daterangebox-popup-cancel', 'z-cell-range-begin', 'z-cell-range-mid', 'z-cell-range-end'];

  test('daterangebox.zul: open the seeded range', async ({ page }) => {
    await labelled(page, 'Seeded range', 'z-daterangebox').locator('.z-daterangebox-button').click();
    await parkCursor(page);
    await popupShot(page, '.z-daterangebox-popup', RANGE, 'daterangebox-popup-open.png');
  });

  test('daterangebox.zul: open with the time boxes and the clear button', async ({ page }) => {
    await labelled(page, 'Time included', 'z-daterangebox').locator('.z-daterangebox-button').click();
    await parkCursor(page);
    await popupShot(page, '.z-daterangebox-popup', [...RANGE, 'z-daterangebox-popup-times', 'z-daterangebox-popup-clear'], 'daterangebox-popup-open-time.png');
  });

  test('daterangebox.zul: open with the today link', async ({ page }) => {
    await labelled(page, 'Today link', 'z-daterangebox').locator('.z-daterangebox-button').click();
    await parkCursor(page);
    await popupShot(page, '.z-daterangebox-popup', [...RANGE, 'z-daterangebox-popup-today'], 'daterangebox-popup-open-today.png');
  });

  test('daterangebox.zul: preview the range while hovering the end day', async ({ page }) => {
    await labelled(page, '1 month panel', 'z-daterangebox').locator('.z-daterangebox-button').click();
    const popup = page.locator('.z-daterangebox-popup:visible');
    await popup.locator('td[aria-label="20 January, 2025"]').click();
    // Holding the cursor on the end day is the point of this shot: the preview range only paints while it hovers.
    await popup.locator('td[aria-label="24 January, 2025"]').hover();
    await expect(popup.locator('.z-cell-range-preview-end')).toHaveCount(1);
    await popupShot(page, '.z-daterangebox-popup', ['z-cell-range-preview-mid', 'z-cell-range-preview-end'], 'daterangebox-popup-preview.png');
  });
});

// -------------------------------------------------------
// colorbox
// -------------------------------------------------------
test.describe('colorbox', () => {
  test.beforeEach(({ page }) => openPage(page, '/colorbox.zul'));

  const enabledColorbox = (page: Page, n: number) => page.locator('.z-colorbox:not(.z-colorbox-disabled)').nth(n);

  test('colorbox.zul: open the palette', async ({ page }) => {
    await enabledColorbox(page, 0).click();
    await parkCursor(page);
    await popupShot(page, '.z-colorbox-popup', ['z-colorpalette-popup'], 'colorbox-popup-palette.png');
  });

  test('colorbox.zul: hover a swatch of the palette', async ({ page }) => {
    await enabledColorbox(page, 0).click();
    // The swatch under the cursor is the one that carries z-colorpalette-selected, so the cursor stays on it.
    await page.locator('.z-colorpalette-color:visible').nth(10).hover();
    // The hover moves focus to div.z-colorpalette, so the browser's default focus ring is in the baseline on purpose:
    // it records the current rendering; whether Marble should style it is follow-up #62 in doc/marble-theme-followups.md.
    await popupShot(page, '.z-colorbox-popup', ['z-colorpalette-popup', 'z-colorpalette-selected'], 'colorbox-popup-palette-selected.png');
  });

  test('colorbox.zul: switch the popup to the picker', async ({ page }) => {
    await enabledColorbox(page, 1).click();
    await page.locator('.z-colorbox-pickericon:visible').click();
    await parkCursor(page);
    await popupShot(page, '.z-colorbox-popup', ['z-colorpicker-popup'], 'colorbox-popup-picker.png');
  });
});

// -------------------------------------------------------
// chosenbox
// -------------------------------------------------------
test.describe('chosenbox', () => {
  test.beforeEach(({ page }) => openPage(page, '/chosenbox.zul'));

  test('chosenbox.zul: open the options and move the highlight', async ({ page }) => {
    await page.locator('.z-chosenbox:not(.z-chosenbox-disabled)').first().click();
    await page.keyboard.press('ArrowDown');
    await parkCursor(page);
    await popupShot(page, '.z-chosenbox-popup', ['z-chosenbox-option-hover'], 'chosenbox-popup-open.png');
  });

  test('chosenbox.zul: type a value with no match in a creatable chosenbox', async ({ page }) => {
    await labelled(page, 'createMessage + noResultsText', 'z-chosenbox').click();
    await page.keyboard.type('Zzz');
    await parkCursor(page);
    await popupShot(page, '.z-chosenbox-popup', ['z-chosenbox-create'], 'chosenbox-popup-create.png');
  });
});

// -------------------------------------------------------
// cascader
// -------------------------------------------------------
test.describe('cascader', () => {
  test.beforeEach(({ page }) => openPage(page, '/cascader.zul'));

  test('cascader.zul: open the panel and move the active item', async ({ page }) => {
    await page.locator('.z-cascader:not(.z-cascader-disabled)').first().click();
    await page.keyboard.press('ArrowDown');
    await parkCursor(page);
    await popupShot(page, '.z-cascader-popup', ['z-cascader-open', 'z-cascader-active'], 'cascader-popup-open-active.png');
  });
});

// -------------------------------------------------------
// searchbox
// -------------------------------------------------------
test.describe('searchbox', () => {
  test.beforeEach(({ page }) => openPage(page, '/searchbox.zul'));

  test('searchbox.zul: open the options and move the active item', async ({ page }) => {
    await page.locator('.z-searchbox:not(.z-searchbox-disabled)').first().click();
    await page.keyboard.press('ArrowDown');
    await parkCursor(page);
    await popupShot(page, '.z-searchbox-popup', ['z-searchbox-open', 'z-searchbox-active'], 'searchbox-popup-open.png');
  });
});

// -------------------------------------------------------
// timepicker
// -------------------------------------------------------
test.describe('timepicker', () => {
  test.beforeEach(({ page }) => openPage(page, '/timepicker.zul'));

  test('timepicker.zul: open the options and move the selected option', async ({ page }) => {
    await labelled(page, 'HH:mm:ss (seeded)', 'z-timepicker').locator('.z-timepicker-button').click();
    await page.keyboard.press('ArrowDown');
    await page.keyboard.press('ArrowDown');
    await page.keyboard.press('ArrowDown');
    await parkCursor(page);
    await popupShot(page, '.z-timepicker-popup', ['z-timepicker-option-selected'], 'timepicker-popup-open.png');
  });
});

// -------------------------------------------------------
// menubar
// -------------------------------------------------------
test.describe('menubar', () => {
  test.beforeEach(({ page }) => openPage(page, '/menubar.zul'));

  // By label text: the menu's own text changes once its pop-up moves back inside it after a click.
  const menu = (page: Page, label: string) => page.getByText(label, { exact: true }).first();

  test('menubar.zul: open a menu with images, a separator and a disabled item', async ({ page }) => {
    await menu(page, 'Project').click();
    await parkCursor(page);
    await popupShot(page, '.z-menupopup', ['z-menupopup', 'z-menupopup-content', 'z-menupopup-separator', 'z-menu-selected'], 'menubar-popup-open.png');
  });

  test('menubar.zul: open a menu with a checked menuitem', async ({ page }) => {
    await menu(page, 'View').click();
    await page.locator('.z-menuitem:visible', { hasText: 'Sort by Name' }).click(); // autocheck closes the menu
    await menu(page, 'View').click();
    await parkCursor(page);
    await popupShot(page, '.z-menupopup', ['z-menupopup', 'z-menuitem-checked'], 'menubar-popup-open-checked.png');
  });
});

// -------------------------------------------------------
// combobutton
// -------------------------------------------------------
test.describe('combobutton', () => {
  test.beforeEach(({ page }) => openPage(page, '/combobutton.zul'));

  test('combobutton.zul: open the menu from the arrow', async ({ page }) => {
    await page.locator('.z-combobutton:not(.z-combobutton-disabled):not(.z-combobutton-toolbar) .z-combobutton-button').first().click();
    await parkCursor(page);
    await popupShot(page, '.z-menupopup', ['z-combobutton-open', 'z-menupopup'], 'combobutton-popup-open.png');
  });
});

// -------------------------------------------------------
// popup
// -------------------------------------------------------
test.describe('popup', () => {
  test.beforeEach(({ page }) => openPage(page, '/popup.zul'));

  test('popup.zul: open a popup from the popup attribute', async ({ page }) => {
    await page.getByRole('button', { name: 'Show Popup', exact: true }).click();
    // No parkCursor: a popup opened by the popup attribute is positioned at the pointer and reads it late,
    // so parking raced the positioning (the shot differed by the PAD clamp at the page edge). The cursor stays on the button.
    await popupShot(page, '.z-popup', ['z-popup', 'z-popup-content'], 'popup-popup-open.png');
  });

  test('popup.zul: open the dark tooltip popup', async ({ page }) => {
    await page.getByRole('button', { name: 'Click', exact: true }).click();
    // Cursor stays on the button for the same reason as above.
    await popupShot(page, '.z-popup-tooltip', ['z-popup', 'z-popup-tooltip'], 'popup-popup-tooltip.png');
  });
});

// -------------------------------------------------------
// navbar
// -------------------------------------------------------
test.describe('navbar', () => {
  test.beforeEach(({ page }) => openPage(page, '/navbar.zul'));

  test('navbar.zul: open the popup of a nav in a collapsed navbar', async ({ page }) => {
    await page.locator('.z-navbar-collapsed .z-nav:visible').first().click();
    // No parkCursor: the nav pop-up closes as soon as the pointer leaves the nav, so the cursor stays on it.
    await popupShot(page, '.z-nav-popup', ['z-nav-popup'], 'navbar-popup-open.png', [], '.z-nav-text-popup');
  });
});

// -------------------------------------------------------
// toolbar
// -------------------------------------------------------
test.describe('toolbar', () => {
  test.beforeEach(({ page }) => openPage(page, '/toolbar.zul'));

  test('toolbar.zul: open the overflow popup', async ({ page }) => {
    await page.locator('.z-toolbar-overflowpopup-on .z-toolbar-overflowpopup-button').first().click();
    await parkCursor(page);
    await popupShot(page, '.z-toolbar-popup-open', ['z-toolbar-popup-open'], 'toolbar-popup-open.png');
  });
});

// -------------------------------------------------------
// drawer
// -------------------------------------------------------
test.describe('drawer', () => {
  test.beforeEach(({ page }) => openPage(page, '/drawer.zul'));

  test('drawer.zul: open the left drawer', async ({ page }) => {
    await page.getByRole('button', { name: 'Open Left', exact: true }).click();
    await parkCursor(page);
    await popupShot(page, '.z-drawer-open', ['z-drawer-open'], 'drawer-popup-open-left.png');
  });
});

// -------------------------------------------------------
// slider
// -------------------------------------------------------
test.describe('slider', () => {
  test.beforeEach(({ page }) => openPage(page, '/slider.zul'));

  test('slider.zul: drag a handle and show the sliding text', async ({ page }) => {
    const handle = await page.locator('.z-slider-button').first().boundingBox();
    if (!handle) throw new Error('no slider handle');
    const x = handle.x + handle.width / 2;
    const y = handle.y + handle.height / 2;
    await page.mouse.move(x, y);
    await page.mouse.down();
    try {
      // The sliding text exists only while the handle is held; the button stays down (and the cursor
      // on the handle) for the capture, there is no widget API that shows it.
      await page.mouse.move(x + 40, y, { steps: 5 });
      await popupShot(page, '.z-slider-popup', ['z-slider-popup'], 'slider-popup-drag.png');
    } finally {
      await page.mouse.up();
    }
  });
});

// -------------------------------------------------------
// errorbox
// -------------------------------------------------------
test.describe('errorbox', () => {
  test.beforeEach(({ page }) => openPage(page, '/errorbox.zul'));

  // Type a character, delete it and click away: the constraint fails on blur and ZK opens the errorbox.
  async function failConstraint(page: Page, field: Locator): Promise<void> {
    await field.click();
    await page.keyboard.type('a');
    await page.keyboard.press('Backspace');
    await page.locator('body').click({ position: { x: 640, y: 20 } });
  }

  test('errorbox.zul: constraint error beside a field', async ({ page }) => {
    await failConstraint(page, page.locator('input[placeholder="Required field"]'));
    await parkCursor(page);
    await popupShot(page, '.z-errorbox.z-errorbox-open', ['z-errorbox-open'], 'errorbox-popup-beside.png', ['z-errorbox-left']);
  });

  test('errorbox.zul: constraint error below a full-width field', async ({ page }) => {
    await failConstraint(page, page.locator('.z-hlayout .z-textbox').last());
    await parkCursor(page);
    await popupShot(page, '.z-errorbox.z-errorbox-open', ['z-errorbox-open'], 'errorbox-popup-below.png', ['z-errorbox-up']);
  });

  // errorbox.zul has no field near the right edge of the viewport, where ZK flips the errorbox to the left of
  // the field (pointer z-errorbox-right): the field is on pv/popup-fixtures.zul.
  test('errorbox.zul: constraint error left of a field at the right edge (pv/popup-fixtures.zul)', async ({ page }) => {
    await openPage(page, '/pv/popup-fixtures.zul');
    await failConstraint(page, page.locator('.z-textbox'));
    await parkCursor(page);
    await popupShot(page, '.z-errorbox.z-errorbox-open', ['z-errorbox-open'], 'errorbox-popup-flipped.png', ['z-errorbox-right']);
  });
});

// -------------------------------------------------------
// messagebox
// -------------------------------------------------------
test.describe('messagebox', () => {
  test.beforeEach(({ page }) => openPage(page, '/messagebox.zul'));

  for (const [button, icon, file] of [
    ['Warning', 'z-messagebox-exclamation', 'messagebox-popup-warning.png'],
    ['Error', 'z-messagebox-error', 'messagebox-popup-error.png'],
    ['Question', 'z-messagebox-question', 'messagebox-popup-question.png'],
  ]) {
    test(`messagebox.zul: ${button.toLowerCase()} dialog`, async ({ page }) => {
      await page.getByRole('button', { name: button, exact: true }).click();
      await parkCursor(page);
      await popupShot(page, '.z-messagebox-window', ['z-messagebox-window', icon, 'z-messagebox-button'], file);
    });
  }
});

// -------------------------------------------------------
// toast and notification: auto-close after 3 s, so the page timers are frozen once the element shows
// -------------------------------------------------------

/**
 * toast.zul and notification.zul buttons dismiss after 3000 ms (Toast.ts / Notification.ts call setTimeout(close, dur)),
 * so a shot taken late, or nine toasts shown one after another, would find them gone. Timers of exactly that delay are
 * dropped before the page loads; the entrance animation and every other timer run unchanged.
 */
async function keepAutoCloseFromFiring(page: Page): Promise<void> {
  const AUTO_CLOSE_MS = 3000;
  await page.addInitScript(ms => {
    const realSetTimeout = window.setTimeout.bind(window);
    window.setTimeout = ((handler: TimerHandler, delay?: number, ...args: unknown[]) =>
      delay === ms ? 0 : realSetTimeout(handler, delay, ...args)) as typeof window.setTimeout;
  }, AUTO_CLOSE_MS);
}

test.describe('toast', () => {
  test.beforeEach(async ({ page }) => {
    await keepAutoCloseFromFiring(page);
    await openPage(page, '/toast.zul');
  });

  for (const [type, file] of [['Info', 'toast-popup-info.png'], ['Warning', 'toast-popup-warning.png'], ['Error', 'toast-popup-error.png']]) {
    test(`toast.zul: ${type.toLowerCase()} toast`, async ({ page }) => {
      await page.getByRole('button', { name: type, exact: true }).click();
      await expect(page.locator('.z-toast-open')).toHaveCount(1);
      await parkCursor(page);
      await popupShot(page, '.z-toast-open', ['z-toast-open', 'z-toast-position-wrapper', 'z-toast-position-top-right'], file);
    });
  }

  test('toast.zul: nine positions at once', async ({ page }) => {
    const positions = ['top-left', 'top-center', 'top-right', 'middle-left', 'middle-center', 'middle-right', 'bottom-left', 'bottom-center', 'bottom-right'];
    for (const position of positions) {
      await page.getByRole('button', { name: position.replace('-', ' ').replace(/\b\w/g, c => c.toUpperCase()), exact: true }).click();
      await expect(page.locator(`.z-toast-position-${position} .z-toast-open`)).toHaveCount(1);
    }
    // The last clicked button keeps its focus ring, and clicking the bottom row can scroll the page;
    // both would end up in the full-window shot, so clear the focus and return to the top first.
    await page.evaluate(() => { (document.activeElement as HTMLElement | null)?.blur(); window.scrollTo(0, 0); });
    await parkCursor(page);
    await expectClassesInDom(page, positions.map(p => `z-toast-position-${p}`));
    await expect(page).toHaveScreenshot('toast-popup-positions.png', { animations: 'disabled', maxDiffPixels: maxDiffFor('toast-popup-positions.png') });
  });
});

test.describe('notification', () => {
  test.beforeEach(({ page }) => keepAutoCloseFromFiring(page));

  for (const [type, file] of [['Info', 'notification-popup-info.png'], ['Warning', 'notification-popup-warning.png'], ['Error', 'notification-popup-error.png']]) {
    test(`notification.zul: ${type.toLowerCase()} notification`, async ({ page }) => {
      await openPage(page, '/notification.zul');
      await page.getByRole('button', { name: type, exact: true }).click();
      await expect(page.locator('.z-notification-open')).toHaveCount(1);
      await parkCursor(page);
      await popupShot(page, '.z-notification-open', ['z-notification-open', 'z-notification-close'], file);
    });
  }

  // notification.zul passes ref = null from every button, so no pointer is drawn there;
  // the anchored variants are triggered on pv/popup-fixtures.zul.
  for (const [button, pointer] of [['Below the anchor', 'up'], ['Above the anchor', 'down'], ['Right of the anchor', 'left'], ['Left of the anchor', 'right']]) {
    test(`notification.zul: pointer ${pointer} to an anchor (pv/popup-fixtures.zul)`, async ({ page }) => {
      await openPage(page, '/pv/popup-fixtures.zul');
      await page.getByRole('button', { name: button, exact: true }).click();
      await expect(page.locator('.z-notification-open')).toHaveCount(1);
      await parkCursor(page);
      await popupShot(page, '.z-notification-open', ['z-notification-open', 'z-notification-pointer'], `notification-popup-pointer-${pointer}.png`, [`z-notification-${pointer}`]);
    });
  }
});
