import { test, expect, Page } from '@playwright/test';

// Guard for grid.css `.z-groupfoot-content` (ZK-6112, zkdemo B6 Z6): ZK writes text-align on
// td.z-groupfoot-inner from the column align, exactly as on td.z-row-inner. The groupfoot text must
// therefore sit where the data row text sits; a flex .z-groupfoot-content ignores text-align.
// Fixture: web/pv/grid-groupfoot-align-guard.zul

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
