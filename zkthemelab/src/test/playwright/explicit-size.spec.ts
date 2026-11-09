import { test, expect } from '@playwright/test';

// Explicit size: a size the page sets on a component (an attribute, vflex) wins over the theme's default
// size or spacing. Each component keeps its own fixture and helpers inside its describe.

declare const zk: any;

// Guard for toolbarbutton.css (ZK-6112, ZK-2802): a button without an inline height grows with a large image,
// but an inline height (the height attribute, vflex) still sizes the button, as under IceBlue. A min-height
// on every button would keep these at 36px.
// Fixture: web/pv/toolbarbutton-height-guard.zul
test.describe('toolbarbutton', () => {
  const FIXTURE = '/pv/toolbarbutton-height-guard.zul';
  const IMAGE_HEIGHT = 72;
  // [widget id, what it is, expected height]
  const FIXED = [
    ['plain', 'a button without a height', 36],
    ['height24', 'height="24px"', 24],
    ['vflex', 'vflex in a 24px parent', 24],
    ['imageHeight24', 'a large image with height="24px"', 24],
  ] as const;

  const height = (page: import('@playwright/test').Page, id: string) =>
    page.evaluate((id) => zk.Widget.$('$' + id).$n().offsetHeight, id);

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
  });

  for (const [id, what, expected] of FIXED) {
    test(`toolbarbutton — ${what} is ${expected}px tall`, { tag: '@ZK-2802' }, async ({ page }) => {
      expect(await height(page, id)).toBe(expected);
    });
  }

  test('toolbarbutton — a button without a height grows taller than its large image', { tag: '@ZK-2802' }, async ({ page }) => {
    await expect.poll(() => height(page, 'image')).toBeGreaterThan(IMAGE_HEIGHT);
  });
});

// Guard for portallayout.css (ZK-6112): vflex panels are CSS flex items, which keep room for their margin, so a
// margin under the last panel of a column without a title is an empty strip at the column's bottom, as it is not
// under IceBlue. The panels are spaced by --zk-spacing-3 only between each other.
// Fixture: web/pv/portallayout-last-panel-guard.zul
test.describe('portallayout', () => {
  const FIXTURE = '/pv/portallayout-last-panel-guard.zul';

  // Vertical bounds of the elements matching a selector inside the portallayout with that widget id
  const bounds = (page: import('@playwright/test').Page, layoutId: string, selector: string) =>
    page.evaluate(({ layoutId, selector }) =>
      Array.from(zk.Widget.$('$' + layoutId).$n().querySelectorAll(selector), (el: Element) => {
        const { top, bottom } = el.getBoundingClientRect();
        return { top, bottom };
      }), { layoutId, selector });

  const spacing3 = (page: import('@playwright/test').Page) =>
    page.evaluate(() => {
      const probe = document.createElement('div');
      probe.style.width = 'var(--zk-spacing-3)';
      document.body.appendChild(probe);
      const width = probe.getBoundingClientRect().width;
      probe.remove();
      return width;
    });

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    await page.evaluate(() => document.fonts.ready);
  });

  test('portallayout — the last panel of a column without a title reaches the column bottom', { tag: '@ZK-6112' }, async ({ page }) => {
    const columns = await bounds(page, 'plain', '.z-portalchildren-content');
    const panels = await bounds(page, 'plain', '.z-portalchildren-content > .z-panel');
    // Panel 2 closes column 1 and Panel 3 closes column 2
    expect(panels[1].bottom).toBeCloseTo(columns[0].bottom, 0);
    expect(panels[2].bottom).toBeCloseTo(columns[1].bottom, 0);
  });

  test('portallayout — panels in a column without a title are spaced by --zk-spacing-3', { tag: '@ZK-6112' }, async ({ page }) => {
    const panels = await bounds(page, 'plain', '.z-portalchildren-content > .z-panel');
    expect(panels[1].top - panels[0].bottom).toBeCloseTo(await spacing3(page), 0);
  });

  test('portallayout — a column with a title keeps its spacing under the last panel', { tag: '@ZK-6112' }, async ({ page }) => {
    const [column] = await bounds(page, 'framedAuto', '.z-portalchildren-frame');
    const [panel] = await bounds(page, 'framedAuto', '.z-portalchildren-content > .z-panel');
    // the frame's border and the column's 1px clearing spacer add to the spacing
    expect(column.bottom - panel.bottom).toBeGreaterThanOrEqual(await spacing3(page));
  });
});

