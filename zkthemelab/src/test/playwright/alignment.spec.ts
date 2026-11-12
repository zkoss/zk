import { test, expect, Page } from '@playwright/test';

// Alignment: text in footer and header cells lines up with the data cells of its column, and an empty field lines up
// with the text and button beside it as a filled one does.
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

// Guard for the inline-flex field widgets (ZK-6112, B50-3032885, B50-ZK-577). Two causes, both fixed by centring the
// root (vertical-align: middle):
//  - datebox, timebox, spinner, doublespinner, bandbox, inputgroup, timepicker, chosenbox: Safari 18 gives an inline-flex
//    box whose first item is an empty input the input's bottom edge as its baseline, so an empty field sat about 14px
//    off the text line and jumped when typed into (WebKit bug 294558, fixed in Safari 26). Playwright's WebKit already
//    has the fix, so their layout test cannot fail there or in Chromium; the computed vertical-align pins the
//    declaration, and the real Safari 18 numbers were measured by hand.
//  - cascader, searchbox: no input; with no value the first flex item is an empty placeholder, which has no baseline,
//    so EVERY browser aligned the box by its bottom edge. Their layout test is a real guard in Chromium.
// Fixture: web/pv/empty-field-baseline-guard.zul
declare const zk: any;

test.describe('empty field baseline', () => {
  const WIDGETS = [
    'datebox', 'timebox', 'spinner', 'doublespinner', 'bandbox', 'inputgroup', 'timepicker', 'chosenbox', 'cascader',
    'searchbox',
  ];
  const TOLERANCE_PX = 0.5;

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto('/pv/empty-field-baseline-guard.zul', { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    await page.evaluate(() => document.fonts.ready);
  });

  // The row's height, and the vertical offset of its label text and of its button from the field's centre.
  const lineGeometry = (page: Page, id: string) => page.evaluate((id) => {
    const row = zk.Widget.$('$' + id).$n();
    const [label, field] = Array.from(row.children) as HTMLElement[];
    const centre = (el: Element) => { const r = el.getBoundingClientRect(); return (r.top + r.bottom) / 2; };
    return {
      rowHeight: row.getBoundingClientRect().height,
      labelOffset: centre(label) - centre(field),
      buttonOffset: centre(row.querySelector('.z-button')) - centre(field),
    };
  }, id);

  for (const widget of WIDGETS) {
    test(`${widget} — the root is centred on the line, not aligned by its baseline`, { tag: '@ZK-6112' }, async ({ page }) => {
      const verticalAlign = await page.evaluate((widget) => {
        const root = document.querySelector('.z-' + widget) as HTMLElement;
        return getComputedStyle(root).verticalAlign;
      }, widget);
      expect(verticalAlign).toBe('middle');
    });

    test(`${widget} — an empty field lines up with the text and the button as a filled one does`, { tag: '@ZK-6112' }, async ({ page }) => {
      const empty = await lineGeometry(page, widget + 'Empty');
      const filled = await lineGeometry(page, widget + 'Filled');
      expect(empty.rowHeight).toBeCloseTo(filled.rowHeight, 0);
      expect(Math.abs(empty.labelOffset - filled.labelOffset)).toBeLessThanOrEqual(TOLERANCE_PX);
      expect(Math.abs(empty.buttonOffset - filled.buttonOffset)).toBeLessThanOrEqual(TOLERANCE_PX);
    });
  }
});

// Guard for `.z-combobox { vertical-align: middle; }` (combobox.css, ZK-6112; ztl B30-1799602, B70-ZK-2742,
// B80-ZK-3051-1). The root is an inline-block with overflow: clip. Safari 18 takes such a box's bottom edge as its
// baseline, so a baseline-aligned Combobox rose 16px above the Datebox and the Bandbox beside it, and
// zk(n).offsetHeight() (which measures a copy of the field in a line of text) gave 45px for a 40px field, opening the
// list 5px low. Chromium keeps the text baseline but still put the Combobox 1.45px high, so the layout test is a real
// guard here; the Safari numbers were measured by hand.
// Fixture: web/combobox.zul, section "In a Line of Text".
test.describe('field in a line of text', () => {
  const TOLERANCE_PX = 0.5;
  // The section ends a page that does not scroll; at the default 720px the list lacks 1px below the field and opens above it.
  test.use({ viewport: { width: 1280, height: 1000 } });

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto('/combobox.zul', { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    await page.evaluate(() => document.fonts.ready);
  });

  test('combobox — the root is centred on the line, not aligned by its baseline', { tag: '@ZK-6112' }, async ({ page }) => {
    const verticalAlign = await page.locator('#pv-combobox-inline .z-combobox').evaluate((el) => getComputedStyle(el).verticalAlign);
    expect(verticalAlign).toBe('middle');
  });

  test('combobox — shares its top edge with the datebox and the bandbox', { tag: '@ZK-6112' }, async ({ page }) => {
    const tops = await page.locator('#pv-combobox-inline').evaluate((line) =>
      ['.z-combobox', '.z-datebox', '.z-bandbox'].map((s) => line.querySelector(s)!.getBoundingClientRect().top));
    expect(Math.abs(tops[0] - tops[1]), `combobox top ${tops[0]}, datebox top ${tops[1]}`).toBeLessThanOrEqual(TOLERANCE_PX);
    expect(Math.abs(tops[2] - tops[1]), `bandbox top ${tops[2]}, datebox top ${tops[1]}`).toBeLessThanOrEqual(TOLERANCE_PX);
  });

  test('combobox — the list opens at the field\'s bottom edge', { tag: '@ZK-6112' }, async ({ page }) => {
    const combobox = page.locator('#pv-combobox-inline .z-combobox');
    expect(await combobox.evaluate((el) => zk(el).offsetHeight())).toBe(await combobox.evaluate((el) => el.offsetHeight));
    await combobox.locator('.z-combobox-button').click();
    // The list slides down with an inline margin of 0, 4px above where it ends, so poll until the slide has finished.
    await expect.poll(() => combobox.evaluate((el) => {
      const popup = zk.Widget.$(el).$n('pp') as HTMLElement;
      return popup.getBoundingClientRect().top - el.getBoundingClientRect().bottom;
    }), { timeout: 2000 }).toBeCloseTo(0, 0);
  });
});
