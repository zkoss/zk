import { test, expect, Page } from '@playwright/test';

// Guard for `.z-html th:not([align]) { text-align: inherit; }` (html.css, ZK-6112): a <th> inside
// an <html> component must line up with the <td> cells under it, while an explicit align on the
// th or on its tr must still win. Without the rule the browser centres every th.
// Fixture: web/pv/html-table-guard.zul

const FIXTURE = '/pv/html-table-guard.zul';
const RIGHT_ALIGNED = ['right', '-webkit-right'];

async function open(page: Page) {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.evaluate(() => document.fonts.ready.then(() => true));
}

function textAlign(page: Page, selector: string): Promise<string> {
  return page.locator(selector).first().evaluate((el) => getComputedStyle(el).textAlign);
}

// Left edge of the cell's rendered text, which is what the eye compares; the cell box is not.
function textLeft(page: Page, selector: string): Promise<number> {
  return page.locator(selector).first().evaluate((cell) => {
    const range = document.createRange();
    range.selectNodeContents(cell);
    return range.getBoundingClientRect().left;
  });
}

test.describe('html component table header alignment', () => {
  test('a plain th text lines up with the td text in the same column', async ({ page }) => {
    await open(page);
    for (let column = 1; column <= 2; column++) {
      const th = await textLeft(page, `#plain tr:nth-child(1) th:nth-child(${column})`);
      const td = await textLeft(page, `#plain tr:nth-child(2) td:nth-child(${column})`);
      expect(Math.abs(th - td), `column ${column}: th text x ${th}, td text x ${td}`).toBeLessThanOrEqual(1);
    }
  });

  test('th align="right" is still honoured', async ({ page }) => {
    await open(page);
    expect(RIGHT_ALIGNED).toContain(await textAlign(page, '#th-align th[align="right"]'));
  });

  test('a th under tr align="right" computes right', async ({ page }) => {
    await open(page);
    expect(RIGHT_ALIGNED).toContain(await textAlign(page, '#tr-align th'));
  });
});
