import { test, expect, Locator } from '@playwright/test';

// A tab label that does not fit a fixed narrow tab must be truncated with an ellipsis
// (Jess review batch 15, follow-up 9). Without `min-width: 0` on .z-tab-content and
// .z-tab-text the label keeps its full width and li.z-tab clips a middle slice.
//
// File name: it ends in `screenshot.spec.ts` on purpose so the `chromium` project
// (whose regex is not anchored) picks it up; this spec takes no screenshots.
test.describe('tab label truncation', () => {
  test('a long label in a narrow tab shrinks to the tab and keeps its ellipsis', async ({ page }) => {
    await page.goto('/tabbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    const m = await page.evaluate(() => {
      const tab = document.querySelector('.z-tabbox-top > .z-tabs li.z-tab') as HTMLElement;
      tab.style.width = '60px';
      tab.style.maxWidth = '60px';
      const content = tab.querySelector('.z-tab-content') as HTMLElement;
      const text = tab.querySelector('.z-tab-text') as HTMLElement;
      text.textContent = 'A very long tab label that cannot fit';
      return {
        tabW: tab.getBoundingClientRect().width,
        contentW: content.getBoundingClientRect().width,
        textW: text.getBoundingClientRect().width,
        textScrollW: text.scrollWidth,
        ellipsis: getComputedStyle(text).textOverflow,
        display: getComputedStyle(text).display,
      };
    });
    expect(m.textScrollW, 'the label must really be wider than the tab, else the test is vacuous').toBeGreaterThan(m.tabW);
    expect(m.contentW, '.z-tab-content must not exceed the tab').toBeLessThanOrEqual(m.tabW);
    expect(m.textW, '.z-tab-text must be narrower than its full text so the ellipsis shows').toBeLessThan(m.textScrollW);
    expect(m.ellipsis).toBe('ellipsis');
    // text-overflow does not apply to a flex container: the label must be a block-level box
    // for the ellipsis to be painted (a flex label is hard-clipped, e.g. "A ve").
    expect(m.display, 'a text-only label must not be a flex container, or no ellipsis is drawn').toBe('block');
  });
});

// A component inside a tab caption must receive the click (follow-up 49b). .z-tab-content and
// .z-tab-text set `pointer-events: none` so a click on the label lands on the tab, and the
// property is inherited: the combobox in the caption was never the hit-test target and its
// input and button were dead. The click still reaches the Tab widget by event bubbling.
// The page's second accordion tab holds a textbox and a combobox wider than its 450px tabbox
// (the combobox button is clipped by the tab), so only its input is checked.
test.describe('component in a tab caption', () => {
  const hitsPart = (combobox: Locator, part: string) =>
    combobox.locator(part).evaluate((el) => {
      const r = el.getBoundingClientRect();
      const hit = document.elementFromPoint(r.x + r.width / 2, r.y + r.height / 2);
      return !!hit && el.contains(hit);
    });

  test('the combobox in an accordion tab caption takes the click and opens its popup', async ({ page }) => {
    await page.goto('/tabbox-misc.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    const comboboxes = page.locator('.z-tabbox-accordion .z-tab .z-combobox');
    for (const part of ['.z-combobox-input', '.z-combobox-button']) {
      expect(await hitsPart(comboboxes.nth(0), part), `selected tab: ${part} must be the hit-test target`).toBe(true);
    }
    expect(await hitsPart(comboboxes.nth(1), '.z-combobox-input'), 'unselected tab: input must be the hit-test target').toBe(true);

    await comboboxes.nth(0).locator('.z-combobox-button').click({ timeout: 3000 });
    await expect(page.locator('.z-combobox-popup:visible')).toHaveCount(1);
  });
});
