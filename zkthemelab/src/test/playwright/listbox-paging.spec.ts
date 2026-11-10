import { test, expect, type Locator, type Page } from '@playwright/test';

// Guard for the paging bar of a Listbox in mold="paging" (listbox.css, ZK-6112), checked against the Grid.
// Paging.onSize shows First/Last only when the bar's own content is more than 50px wider than its items,
// and hides the detail when it overlaps them (ZK-3784). paging.css takes the detail out of the flow, so a
// bar that is only as wide as its content never qualifies: the bar must fill the Listbox's width.
// The divider is drawn on the bar, not on the -paging-top / -paging-bottom wrapper, so the bar sits at the
// same place as the Grid's, a hidden bar (a single page) leaves no band, and a top bar has its divider too.
// Fixture: web/pv/listbox-paging-guard.zul.

const FIXTURE = '/pv/listbox-paging-guard.zul';
type Comp = 'listbox' | 'grid';
type Size = 'wide' | 'narrow' | 'single' | 'top';

async function open(page: Page) {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
}

function bar(page: Page, comp: Comp, size: Size): Locator {
  return page.locator(`.z-${comp}.lpg-${size} .z-paging`).first();
}

// The bar's box relative to its widget, and the divider it draws.
async function barBox(page: Page, comp: Comp, size: Size) {
  return bar(page, comp, size).evaluate((el) => {
    const host = el.closest('.z-listbox, .z-grid')!.getBoundingClientRect();
    const r = el.getBoundingClientRect();
    const cs = getComputedStyle(el);
    return {
      x: r.x - host.x, w: r.width, h: r.height,
      borderTop: `${cs.borderTopWidth} ${cs.borderTopStyle} ${cs.borderTopColor}`,
      borderBottom: `${cs.borderBottomWidth} ${cs.borderBottomStyle} ${cs.borderBottomColor}`,
      wrapperH: el.parentElement!.getBoundingClientRect().height,
    };
  });
}

for (const comp of ['listbox', 'grid'] as const) {
  test(`${comp} 400px: the paging bar fills the widget and shows First, Last and the detail`, async ({ page }) => {
    await open(page);
    const b = bar(page, comp, 'wide');
    await expect(b.locator('.z-paging-first')).toBeVisible();
    await expect(b.locator('.z-paging-last')).toBeVisible();
    await expect(b.locator('.z-paging-info')).toBeVisible();
    const { barW, hostW } = await b.evaluate((el) => ({
      barW: el.getBoundingClientRect().width,
      hostW: el.closest('.z-listbox, .z-grid')!.clientWidth,
    }));
    expect(Math.abs(barW - hostW), `bar ${barW}px, widget content ${hostW}px`).toBeLessThanOrEqual(1);
  });

  test(`${comp} 400px: Last goes to the last page`, async ({ page }) => {
    await open(page);
    const b = bar(page, comp, 'wide');
    await b.locator('.z-paging-last').click();
    await expect(b.locator('.z-paging-last')).toBeDisabled();
    await expect(b.locator('.z-paging-next')).toBeDisabled();
  });

  test(`${comp} 160px: First and Last stay hidden (ZK-3784)`, async ({ page }) => {
    await open(page);
    const b = bar(page, comp, 'narrow');
    await expect(b.locator('.z-paging-next')).toBeVisible();
    await expect(b.locator('.z-paging-first')).toBeHidden();
    await expect(b.locator('.z-paging-last')).toBeHidden();
  });

  test(`${comp} single page: the hidden bar leaves no band`, async ({ page }) => {
    await open(page);
    const b = bar(page, comp, 'single');
    await expect(b).toBeHidden();
    const wrapperH = await b.evaluate((el) => el.parentElement!.getBoundingClientRect().height);
    expect(wrapperH).toBe(0);
  });
}

test('the Listbox bottom bar sits where the Grid bottom bar does, with the same divider', async ({ page }) => {
  await open(page);
  const lb = await barBox(page, 'listbox', 'wide');
  const g = await barBox(page, 'grid', 'wide');
  expect(lb.x).toBe(g.x);
  expect(lb.h).toBe(g.h);
  expect(lb.wrapperH).toBe(g.wrapperH);
  expect(lb.borderTop).toBe(g.borderTop);
  expect(lb.borderTop).toMatch(/^1px solid /);
});

// The Listbox has no paging control rules of its own: First, the page input, the total and Last sit at the
// Grid's x and width with the Grid's font size, and each glyph is painted once, by the shared 16px ::before.
test('the Listbox paging controls match the Grid\'s', async ({ page }) => {
  await open(page);
  const controls = (comp: Comp) => bar(page, comp, 'wide').evaluate((el) => {
    const x0 = el.getBoundingClientRect().x;
    const box = (sel: string) => {
      const e = el.querySelector(sel)!;
      const r = e.getBoundingClientRect();
      return { x: Math.round(r.x - x0), w: Math.round(r.width), fontSize: getComputedStyle(e).fontSize };
    };
    const icon = el.querySelector('.z-paging-next .z-paging-icon')!;
    return {
      first: box('.z-paging-first'), input: box('.z-paging-input'), text: box('.z-paging-text'), last: box('.z-paging-last'),
      iconW: Math.round(icon.getBoundingClientRect().width), iconMask: getComputedStyle(icon).maskImage,
    };
  });
  const lb = await controls('listbox');
  expect(lb).toEqual(await controls('grid'));
  expect(lb.iconW).toBe(16);
  expect(lb.iconMask).toBe('none');
});

test('the Listbox top bar draws the divider under it, as the Grid top bar does', async ({ page }) => {
  await open(page);
  const lb = await barBox(page, 'listbox', 'top');
  const g = await barBox(page, 'grid', 'top');
  expect(lb.borderBottom).toBe(g.borderBottom);
  expect(lb.borderBottom).toMatch(/^1px solid /);
  expect(lb.h).toBe(g.h);
});
