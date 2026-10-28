import { test, expect } from '@playwright/test';

// Scrollable menubar on a mobile UA (Jess review batch 16, follow-up 19). zkmax's
// touch menubar makes setScrollable() a no-op ("use native scroll"), so a horizontal
// bar narrower than its items must scroll natively. Without `overflow: auto` the items
// paint outside the bar and nothing scrolls.
//
// File name: it ends in `tablet.spec.ts` so the `tablet` project (mobile UA) runs it.
test.describe('menubar on a mobile UA', () => {
  test('a horizontal menubar narrower than its items scrolls natively', async ({ page }) => {
    await page.goto('/menubar.zul');
    await page.waitForLoadState('networkidle');
    const m = await page.evaluate(() => {
      const bar = [...document.querySelectorAll('.z-menubar-horizontal')].pop() as HTMLElement;
      const before = { sw: bar.scrollWidth, cw: bar.clientWidth, ox: getComputedStyle(bar).overflowX };
      bar.scrollLeft = 120;
      return { ...before, scrollLeft: bar.scrollLeft };
    });
    expect(m.sw, 'the items must really be wider than the bar, else the test is vacuous').toBeGreaterThan(m.cw);
    expect(['auto', 'scroll']).toContain(m.ox);
    expect(m.scrollLeft, 'scrollLeft must stick, i.e. the bar scrolls').toBeGreaterThan(0);
  });
});
