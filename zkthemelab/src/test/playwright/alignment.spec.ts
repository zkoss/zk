import { test, expect, Page } from '@playwright/test';

// Alignment: text in footer and header cells lines up with the data cells of its column.
// Each component keeps its own fixture and helpers inside its describe.

// Guard for grid.css `.z-groupfoot-content` (ZK-6112, zkdemo B6 Z6): ZK writes text-align on
// td.z-groupfoot-inner from the column align, exactly as on td.z-row-inner. The groupfoot text must
// therefore sit where the data row text sits; a flex .z-groupfoot-content ignores text-align.
// Fixture: web/pv/grid-groupfoot-align-guard.zul
test.describe('grid', () => {
  const FIXTURE = '/pv/grid-groupfoot-align-guard.zul';
  const TOLERANCE_PX = 1;
  const NAME = 1;
  const QUANTITY = 2;
  const STATUS = 3;

  async function open(page: Page) {
    const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    await page.evaluate(() => document.fonts.ready.then(() => true));
  }

  // Horizontal extent of the cell's rendered text, which is what the eye compares; the cell and its content div are not.
  function textExtent(page: Page, rowSelector: string, column: number): Promise<{ left: number; right: number }> {
    return page.locator(`${rowSelector} > td:nth-child(${column})`).first().evaluate((cell) => {
      const text = document.createTreeWalker(cell, NodeFilter.SHOW_TEXT, {
        acceptNode: (node) => (node.textContent?.trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP),
      }).nextNode() as Text;
      const range = document.createRange();
      range.selectNodeContents(text);
      const { left, right } = range.getBoundingClientRect();
      return { left, right };
    });
  }

  async function dataAndFoot(page: Page, column: number) {
    await open(page);
    return {
      data: await textExtent(page, '.groupfoot-guard tr.z-row', column),
      foot: await textExtent(page, '.groupfoot-guard tr.z-groupfoot', column),
    };
  }

  test.describe('grid groupfoot follows the column align', () => {
    test('a right-aligned column ends the groupfoot text at the data row text right edge', async ({ page }) => {
      const { data, foot } = await dataAndFoot(page, QUANTITY);
      expect(Math.abs(foot.right - data.right), `data right ${data.right}, groupfoot right ${foot.right}`)
        .toBeLessThanOrEqual(TOLERANCE_PX);
    });

    test('a centre-aligned column centres the groupfoot text like the data row text', async ({ page }) => {
      const { data, foot } = await dataAndFoot(page, STATUS);
      const dataCentre = (data.left + data.right) / 2;
      const footCentre = (foot.left + foot.right) / 2;
      expect(Math.abs(footCentre - dataCentre), `data centre ${dataCentre}, groupfoot centre ${footCentre}`)
        .toBeLessThanOrEqual(TOLERANCE_PX);
    });

    test('a left-aligned column starts the groupfoot text at the data row text left edge', async ({ page }) => {
      const { data, foot } = await dataAndFoot(page, NAME);
      expect(Math.abs(foot.left - data.left), `data left ${data.left}, groupfoot left ${foot.left}`)
        .toBeLessThanOrEqual(TOLERANCE_PX);
    });
  });
});

// Guard for `.z-html th:not([align]) { text-align: inherit; }` (html.css, ZK-6112): a <th> inside
// an <html> component must line up with the <td> cells under it, while an explicit align on the
// th or on its tr must still win. Without the rule the browser centres every th.
// Fixture: web/pv/html-table-guard.zul
test.describe('html table', () => {
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
});
