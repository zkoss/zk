import { test, expect, Page } from '@playwright/test';

// Regression for the print stylesheet added to
// src/main/resources/web/zul/css/utility/_print.css and demoed on
// src/test/resources/web/utility/print.zul:
//   (a) opt-in visibility  .z-d-print-{value}   (@media print)
//   (b) standard reset — elevation flattens to a hairline border (box-shadow:none)
//
// Print media is applied IN the spec via page.emulateMedia({ media: 'print' }),
// following the forced-colors specs' precedent (the config `use:` option does
// not affect the page fixture in this runner). getComputedStyle reflects the
// emulated media, so we read `display` / `box-shadow` directly.
//
// NOT asserted here (verified via manual Print Preview instead): un-sticking
// sticky headers and expanding scroll bodies both require a live scrolling
// mesh widget with sticky enabled — see doc/spec/print-styles.md §Limitations.

const URL = '/utility/print.zul';

async function open(page: Page) {
  await page.goto(URL, { waitUntil: 'domcontentloaded' });
  await page.locator('.z-elevation-2').first().waitFor({ state: 'attached' });
}

function display(page: Page, selector: string): Promise<string> {
  return page.locator(selector).first().evaluate(n => getComputedStyle(n).display);
}

function boxShadow(page: Page, selector: string): Promise<string> {
  return page.locator(selector).first().evaluate(n => getComputedStyle(n).boxShadow);
}

test.describe('print utilities', () => {
  // screen-only chip carries .z-d-print-none; print-only chip carries .z-d-none.z-d-print-block
  const screenOnly = '.z-d-print-none';
  const printOnly = '.z-d-none.z-d-print-block';

  test('z-d-print-* flip between screen and print media', async ({ page }) => {
    await open(page);

    // screen media: screen-only shown, print-only hidden
    await page.emulateMedia({ media: 'screen' });
    expect(await display(page, screenOnly)).not.toBe('none');
    expect(await display(page, printOnly)).toBe('none');

    // print media: screen-only hidden, print-only shown as block
    await page.emulateMedia({ media: 'print' });
    expect(await display(page, screenOnly)).toBe('none');
    expect(await display(page, printOnly)).toBe('block');
  });

  test('elevation flattens to no shadow under print', async ({ page }) => {
    await open(page);

    // on screen the elevation-2 card carries a real box-shadow…
    await page.emulateMedia({ media: 'screen' });
    expect(await boxShadow(page, '.z-elevation-2')).not.toBe('none');

    // …which flattens to none (hairline border instead) when printed.
    await page.emulateMedia({ media: 'print' });
    expect(await boxShadow(page, '.z-elevation-2')).toBe('none');
  });
});
