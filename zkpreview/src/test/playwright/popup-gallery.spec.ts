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

/**
 * Whether `target` computes the same value of `property` as an element styled with `declaration`, e.g.
 * `box-shadow: var(--zk-elevation-3)`. Compares against the token instead of a pasted string, so a rule that
 * moves to another elevation fails even where the pixels barely differ (a shadow on a 50 % black scrim).
 */
async function expectComputedLike(target: Locator, property: string, tokenValue: string): Promise<void> {
  const [actual, reference] = await target.evaluate((el, [prop, value]) => {
    const probe = document.createElement('div');
    probe.style.setProperty(prop, value);
    document.body.appendChild(probe);
    const expected = getComputedStyle(probe).getPropertyValue(prop);
    probe.remove();
    return [getComputedStyle(el).getPropertyValue(prop), expected];
  }, [property, tokenValue]);
  expect(reference, `the reference ${property} must not be empty`).not.toBe('none');
  expect(actual, `${property} of the element`).toBe(reference);
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

  // combobox.zul sets no emptySearchMessage and no comboitem image: both are on pv/popup-api-fixtures.zul.
  const emptySearchCombobox = (page: Page) => labelled(page, 'Empty search message', 'z-combobox');

  test('combobox.zul: type a value with no match to show the empty search message (pv/popup-api-fixtures.zul)', async ({ page }) => {
    await openPage(page, '/pv/popup-api-fixtures.zul');
    await emptySearchCombobox(page).locator('input').click();
    // Typed one key at a time: the server filters the items after each change, and a burst of keys can outrun it.
    await page.keyboard.type('Zzz', { delay: 100 });
    await expect(page.locator('.z-combobox-empty-search-message:visible')).toHaveCount(1);
    await parkCursor(page);
    await popupShot(page, '.z-combobox-popup', ['z-combobox-open', 'z-combobox-empty-search-message'], 'combobox-popup-empty-search.png');
  });

  // The message element stays in the popup and is hidden while items match: nothing is painted for it, so this is
  // asserted on the DOM and the computed style, not on a baseline that would repeat the dropdown shots.
  test('combobox.zul: type a value with matches to hide the empty search message (pv/popup-api-fixtures.zul)', async ({ page }) => {
    await openPage(page, '/pv/popup-api-fixtures.zul');
    await emptySearchCombobox(page).locator('input').click();
    await page.keyboard.type('Ap', { delay: 100 });
    await expect(page.locator('.z-comboitem:visible')).toHaveCount(2);
    const message = page.locator('.z-combobox-empty-search-message');
    await expect(message).toHaveClass(/z-combobox-empty-search-message-hidden/);
    await expect(message).toHaveCSS('display', 'none');
  });

  test('combobox.zul: open dropdown of items with an image (pv/popup-api-fixtures.zul)', async ({ page }) => {
    await openPage(page, '/pv/popup-api-fixtures.zul');
    await labelled(page, 'Comboitem with image', 'z-combobox').locator('.z-combobox-button').click();
    await parkCursor(page);
    await popupShot(page, '.z-combobox-popup', ['z-combobox-open', 'z-comboitem', 'z-comboitem-image'], 'combobox-popup-open-image.png');
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
    await popupShot(page, '.z-chosenbox-popup', ['z-chosenbox-create', 'z-chosenbox-empty-creatable'], 'chosenbox-popup-create.png');
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

  // Every menupopup renders a <div class="z-menupopup-separator"> above its list; Marble hides it on purpose
  // (menu.css: "the decorative separator at the top of every menupopup"), so it has no box to photograph.
  // The line between items is the <menuseparator> (li.z-menuseparator), which the shots above paint.
  test('menubar.zul: the decorative separator on top of a menupopup stays hidden', async ({ page }) => {
    await menu(page, 'Project').click();
    const popup = page.locator('.z-menupopup:visible');
    await expect(popup).toHaveCount(1);
    const separator = popup.locator('.z-menupopup-separator');
    await expect(separator).toHaveCount(1);
    await expect(separator).toHaveCSS('display', 'none');
    expect(await separator.boundingBox()).toBeNull();
    // The visible line of the menu is the menuseparator item.
    await expect(popup.locator('.z-menuseparator')).toHaveCount(1);
    await expect(popup.locator('.z-menuseparator')).toBeVisible();
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

// -------------------------------------------------------
// loading: the global busy mask and the busy mask of one component (Clients.showBusy)
// -------------------------------------------------------
/** The scrim of a modal window or a global busy: fixed over the whole window, painted with the scrim token. */
async function expectScrimOverWindow(page: Page): Promise<void> {
  const mask = page.locator('.z-modal-mask');
  await expect(mask).toHaveCount(1);
  const viewport = page.viewportSize()!;
  expect(await mask.boundingBox()).toEqual({ x: 0, y: 0, width: viewport.width, height: viewport.height });
  await expectComputedLike(mask, 'background-color', 'var(--zk-color-scrim)');
}

test.describe('loading', () => {
  test.beforeEach(({ page }) => openPage(page, '/loading.zul'));

  // The two "Show Busy" buttons of loading.zul: the first is the global busy, the second the busy of the demo window.
  const showBusyButton = (page: Page, n: number) => page.getByRole('button', { name: 'Show Busy', exact: true }).nth(n);

  test('loading.zul: global busy', async ({ page }) => {
    await showBusyButton(page, 0).click();
    // The page clears the global busy by itself after 14 s; the shot is taken a moment after it shows.
    await expect(page.locator('.z-loading')).toHaveCount(1);
    await parkCursor(page);
    // The scrim covers the window and is not in the shot (it would pull the whole page into the baseline).
    await expectScrimOverWindow(page);
    await expectComputedLike(page.locator('.z-loading'), 'box-shadow', 'var(--zk-elevation-3)');
    await popupShot(page, '.z-loading', ['z-modal-mask', 'z-loading', 'z-loading-indicator', 'z-loading-icon'], 'loading-popup-global-busy.png');
  });

  test('loading.zul: busy of one component', async ({ page }) => {
    await showBusyButton(page, 1).click();
    await expect(page.locator('.z-apply-loading')).toHaveCount(1);
    await parkCursor(page);
    await popupShot(page, '.z-apply-mask', ['z-apply-mask', 'z-apply-loading', 'z-apply-loading-indicator', 'z-apply-loading-icon'], 'loading-popup-component-busy.png');
  });
});

// -------------------------------------------------------
// window: a modal window (doModal), on pv/popup-api-fixtures.zul because window.zul has none
// -------------------------------------------------------
test.describe('window', () => {
  test('window.zul: open a modal window (pv/popup-api-fixtures.zul)', async ({ page }) => {
    await openPage(page, '/pv/popup-api-fixtures.zul');
    await page.getByRole('button', { name: 'Open modal window', exact: true }).click();
    await expect(page.locator('.z-window-modal')).toHaveCount(1);
    await parkCursor(page);
    // The scrim is asserted, not shot: the shot is the window plus PAD, so the page behind stays out of the baseline.
    await expectScrimOverWindow(page);
    await expectComputedLike(page.locator('.z-window-modal'), 'box-shadow', 'var(--zk-elevation-3)');
    await popupShot(page, '.z-window-modal', ['z-window-modal', 'z-modal-mask'], 'window-popup-modal.png');
  });
});

// -------------------------------------------------------
// loadingbar: the bar the Loadingbar API pins to the bottom of the window
// -------------------------------------------------------
test.describe('loadingbar', () => {
  test('loadingbar.zul: a bar at the bottom of the window (pv/popup-api-fixtures.zul)', async ({ page }) => {
    await openPage(page, '/pv/popup-api-fixtures.zul');
    await page.getByRole('button', { name: 'Start a loading bar at the bottom', exact: true }).click();
    await parkCursor(page);
    const bar = page.locator('.z-loadingbar-position-bottom:visible');
    await expect(bar, 'exactly one visible bottom bar').toHaveCount(1);
    await expectClassesInDom(page, ['z-loadingbar-position-wrapper', 'z-loadingbar-position-bottom', 'z-loadingbar', 'z-loadingbar-colorbar']);
    // The bar is fixed to the bottom edge of the window, so the capture stops at the window instead of adding PAD below it.
    const box = await settledBox(page, bar);
    const viewport = page.viewportSize()!;
    const top = Math.max(0, box.y - PAD);
    const file = 'loadingbar-popup-bottom.png';
    await expect(page).toHaveScreenshot(file, {
      clip: { x: 0, y: top, width: viewport.width, height: viewport.height - top },
      animations: 'disabled',
      maxDiffPixels: maxDiffFor(file),
    });
  });
});

// -------------------------------------------------------
// grid, listbox, tree: the loading overlay of rendering on demand (ROD)
// -------------------------------------------------------

/**
 * The loading class sits on the scrolling body only while the next rows are on their way (about 60-90 ms on a local
 * server). The AU request that fetches them (`onDataLoading` for grid and listbox, `onRender` for tree) is held until
 * `release()` is called, so the state can be read instead of raced; the action that starts it is a real mouse wheel
 * over the body. The URL is matched loosely: ZK appends `;jsessionid=...` to `/zkau`.
 */
async function holdDataLoadingRequests(page: Page): Promise<() => void> {
  let release!: () => void;
  const released = new Promise<void>(resolve => { release = resolve; });
  await page.route(/\/zkau/, async route => {
    if (/onDataLoading|onRender/.test(route.request().postData() ?? '')) await released;
    await route.continue();
  });
  return release;
}

/** Scroll the body of a ROD widget with the mouse wheel and read the overlay while the request is held. */
async function expectRodLoadingOverlay(page: Page, bodyClass: string, loadingClass: string): Promise<void> {
  await openPage(page, '/pv/popup-rod-fixtures.zul');
  const body = page.locator(`.${bodyClass}`);
  // The pads that stand in for the rows not rendered yet arrive a moment after the widget is bound.
  await expect.poll(() => body.evaluate(el => el.scrollHeight > 2 * el.clientHeight)).toBe(true);
  const release = await holdDataLoadingRequests(page);
  const box = (await body.boundingBox())!;
  await page.mouse.move(box.x + box.width / 2, box.y + box.height / 2);
  await page.mouse.wheel(0, 3000);
  await expect(body).toHaveClass(new RegExp(loadingClass));
  // 60 % white over the rows: the one declaration every rule of this overlay keeps.
  await expect(body).toHaveCSS('background-color', 'rgba(255, 255, 255, 0.6)');
  release();
  await expect(body).not.toHaveClass(new RegExp(loadingClass));
}

test.describe('grid', () => {
  test('grid.zul: scroll a ROD grid to show the loading overlay (pv/popup-rod-fixtures.zul)', async ({ page }) => {
    await expectRodLoadingOverlay(page, 'z-grid-body', 'z-grid-loading');
  });
});

test.describe('listbox', () => {
  test('listbox.zul: scroll a ROD listbox to show the loading overlay (pv/popup-rod-fixtures.zul)', async ({ page }) => {
    await expectRodLoadingOverlay(page, 'z-listbox-body', 'z-listbox-loading');
  });
});

test.describe('tree', () => {
  test('tree.zul: scroll a ROD tree to show the loading overlay (pv/popup-rod-fixtures.zul)', async ({ page }) => {
    await expectRodLoadingOverlay(page, 'z-tree-body', 'z-tree-loading');
  });
});

// -------------------------------------------------------
// coachmark-positions
// -------------------------------------------------------
// Three coachmarks opened at once stack three 50 % scrims and make the page unreadable, and the pointer
// direction class exists only on an open coachmark. So each position is opened on its own, with the
// click a user makes on the target, and closed with the close button before the next one.
test.describe('coachmark-positions', () => {
  test.beforeEach(({ page }) => openPage(page, '/coachmark-positions.zul'));

  const positions = [
    { name: 'above its target (before_center)', trigger: 'Show coachmark above', direction: 'z-coachmark-down', file: 'coachmark-positions-popup-down.png' },
    { name: 'at the end of its target (end_center)', trigger: 'Show coachmark at the end', direction: 'z-coachmark-left', file: 'coachmark-positions-popup-left.png' },
    { name: 'at the start of its target (start_center)', trigger: 'Show coachmark at the start', direction: 'z-coachmark-right', file: 'coachmark-positions-popup-right.png' },
  ];

  for (const { name, trigger, direction, file } of positions) {
    test(`coachmark-positions.zul: open the coachmark ${name}`, async ({ page }) => {
      await expect(page.locator('.z-coachmark-mask'), 'no scrim before the click').toHaveCount(0);
      await page.getByRole('button', { name: trigger, exact: true }).click();
      await parkCursor(page);
      const popup = await popupShot(page, '.z-coachmark.z-coachmark-open', ['z-coachmark-open', direction], file, [direction]);
      await expect(page.locator('.z-coachmark-mask'), 'one scrim while one coachmark is open').toHaveCount(1);
      await popup.locator('.z-coachmark-close').click();
      await expect(popup).toBeHidden();
      await expect(page.locator('.z-coachmark-mask')).toHaveCount(0);
    });
  }
});
