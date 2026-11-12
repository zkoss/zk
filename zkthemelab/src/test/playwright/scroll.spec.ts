import { test, expect, type Page } from '@playwright/test';

// Scrolling: content that overflows a widget can be scrolled into view.
//
// Guard for `.z-listbox-emptybody td` and `.z-grid-emptybody td { padding: 0 0 1px; }` (listbox.css, grid.css,
// ZK-6112; ztl B70-ZK-2290). With padding: 0 the empty body's table was 0px tall. Without a height the body then had
// nothing to scroll in any browser; with a height Chromium and Firefox still counted the wide table in the body's
// scroll width, but Safari 18 left it out. Either way the columns past the right edge could not be reached, and the
// header, which follows the body's scroll, could not show them. The 1px row (as under IceBlue) keeps the table in the
// scroll width. Chromium fails the no-height case without the fix; the with-height case was checked in Safari by hand.
// Fixtures: web/listbox.zul and web/grid.zul, section "Empty, Columns Wider Than the ...".

type Comp = 'listbox' | 'grid';

async function open(page: Page, comp: Comp) {
  const resp = await page.goto(`/${comp}.zul`, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
}

// Scroll the body as far right as it goes, then read how far the body and the header moved.
function scrollRight(page: Page, comp: Comp, cls: string) {
  return page.locator(`.${cls}`).evaluate((root, comp) => {
    const body = root.querySelector(`.z-${comp}-body`) as HTMLElement;
    const header = root.querySelector(`.z-${comp}-header`) as HTMLElement;
    body.scrollLeft = 1000;
    body.dispatchEvent(new Event('scroll'));
    return { scrollWidth: body.scrollWidth, clientWidth: body.clientWidth, body: body.scrollLeft, header: header.scrollLeft };
  }, comp);
}

for (const comp of ['listbox', 'grid'] as Comp[]) {
  test.describe('empty mesh with columns wider than it', () => {
    for (const [variant, suffix] of [['without a height', ''], ['with a height', '-height']]) {
      test(`${comp} — ${variant}, the empty body scrolls sideways and the header follows`, { tag: '@ZK-6112' }, async ({ page }) => {
        await open(page, comp);
        const cls = `pv-${comp}-empty-wide${suffix}`;
        const s = await scrollRight(page, comp, cls);
        expect(s.scrollWidth, `scrollWidth ${s.scrollWidth}, clientWidth ${s.clientWidth}`).toBeGreaterThan(s.clientWidth);
        expect(s.body).toBe(s.scrollWidth - s.clientWidth);
        await expect.poll(() => page.locator(`.${cls} .z-${comp}-header`).evaluate((el) => el.scrollLeft)).toBe(s.body);
      });
    }
  });
}
