import { test, expect, Page } from '@playwright/test';

// Regression for the responsive display utilities added to
// src/main/resources/web/zul/css/utility/_layout.css and demoed on
// src/test/resources/web/utility/responsive.zul:
//   (a) viewport visibility  .z-d-{bp}-{value}   (mobile-first / min-width @media)
//   (b) container queries     .z-container + .z-cq-{bp}-{value}   (@container)
// Bootstrap breakpoints: sm 576, md 768, lg 992, xl 1200.
//
// The breakpoint-reference chips (scenario 1.4) share their class combos with
// the teaching scenarios above them (e.g. the 1.1 sidebar is also
// .z-d-none.z-d-md-block), so those chips are located by their exact text —
// unique on the page. The layout-switch container (.z-d-block.z-d-md-flex) and
// the container-query demo selectors remain singular / fixed-count.

const URL = '/utility/responsive.zul';

// Scenario 1.4 chip labels — exact rendered text, one element each.
const CHIP = {
  ltSm: 'Visible < sm (<576)',
  geSm: 'Visible ≥ sm (576)',
  geMd: 'Visible ≥ md (768)',
  geLg: 'Visible ≥ lg (992)',
  geXl: 'Visible ≥ xl (1200)',
};

async function open(page: Page) {
  await page.goto(URL, { waitUntil: 'domcontentloaded' });
  // ZK renders the div tree into the initial response; wait for the query
  // containers to exist before measuring.
  await page.locator('.z-container').first().waitFor({ state: 'attached' });
}

async function display(page: Page, selector: string, nth = 0): Promise<string> {
  return page.locator(selector).nth(nth)
    .evaluate(n => getComputedStyle(n).display);
}

// -------------------------------------------------------
// (a) Viewport visibility — display flips as the window crosses each breakpoint
// -------------------------------------------------------
test.describe('viewport-visibility', () => {
  test('chips appear/disappear at their breakpoint', async ({ page }) => {
    await open(page);
    const chip = (t: string) => page.getByText(t, { exact: true });

    // Below sm (< 576): only the "< sm" chip shows; every min-width chip is hidden.
    await page.setViewportSize({ width: 500, height: 900 });
    await expect(chip(CHIP.ltSm)).toBeVisible();
    await expect(chip(CHIP.geSm)).toBeHidden();
    await expect(chip(CHIP.geMd)).toBeHidden();
    await expect(chip(CHIP.geLg)).toBeHidden();
    await expect(chip(CHIP.geXl)).toBeHidden();

    // md range (>= 768, < 992): sm + md show; < sm hides; lg/xl still hidden.
    await page.setViewportSize({ width: 850, height: 900 });
    await expect(chip(CHIP.ltSm)).toBeHidden();
    await expect(chip(CHIP.geSm)).toBeVisible();
    await expect(chip(CHIP.geMd)).toBeVisible();
    await expect(chip(CHIP.geLg)).toBeHidden();
    await expect(chip(CHIP.geXl)).toBeHidden();

    // >= lg (992): lg shows too; xl (1200) still hidden.
    await page.setViewportSize({ width: 1100, height: 900 });
    await expect(chip(CHIP.geLg)).toBeVisible();
    await expect(chip(CHIP.geXl)).toBeHidden();

    // >= xl (1200): xl shows.
    await page.setViewportSize({ width: 1300, height: 900 });
    await expect(chip(CHIP.geXl)).toBeVisible();
  });

  test('layout switch: block below md, flex from md up', async ({ page }) => {
    await open(page);
    const sel = '.z-d-block.z-d-md-flex';

    await page.setViewportSize({ width: 700, height: 900 });   // < md
    expect(await display(page, sel)).toBe('block');

    await page.setViewportSize({ width: 1000, height: 900 });  // >= md
    expect(await display(page, sel)).toBe('flex');
  });
});

// -------------------------------------------------------
// (b) Container queries — each card reacts to ITS container width, not the viewport
// -------------------------------------------------------
test.describe('container-queries', () => {
  test('identical markup adapts per container width', async ({ page }) => {
    await open(page);
    // Wide viewport so no container is size-constrained; container queries
    // read each .z-container's own fixed inline width (300 / 700 / 950px).
    await page.setViewportSize({ width: 1400, height: 1000 });

    const body = '.z-cq-md-flex';                 // body: z-d-block z-cq-md-flex
    const details = '.z-cq-none.z-cq-sm-block';   // details chip

    // 300px container (< sm, < md): stacked (block), details hidden.
    expect(await display(page, body, 0)).toBe('block');
    await expect(page.locator(details).nth(0)).toBeHidden();

    // 700px container (>= sm, < md): stacked (block), details visible.
    expect(await display(page, body, 1)).toBe('block');
    await expect(page.locator(details).nth(1)).toBeVisible();

    // 950px container (>= md): row (flex), details visible.
    expect(await display(page, body, 2)).toBe('flex');
    await expect(page.locator(details).nth(2)).toBeVisible();
  });

  test('.z-container establishes an inline-size query container', async ({ page }) => {
    await open(page);
    expect(await display(page, '.z-container')).not.toBe('');
    const ct = await page.locator('.z-container').first()
      .evaluate(n => getComputedStyle(n).containerType);
    expect(ct).toBe('inline-size');
  });
});
