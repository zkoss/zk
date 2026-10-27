import { test, expect } from '@playwright/test';

// Mobile frozen columns (Jess review batch 15, follow-up 14). On a mobile UA ZK adds
// `.z-frozen-sticky` plus an inline `left` to frozen cells and relies on
// `position: sticky`. Without that rule the cells keep `position: relative` and the
// inline `left` shifts them (Col B's header landed 200px to the right of its column).
//
// File name: it ends in `tablet.spec.ts` so the `tablet` project (mobile UA) runs it.
test.describe('frozen columns on a mobile UA', () => {
  test('frozen header cells are sticky and sit on their column', async ({ page }) => {
    await page.goto('/grid.zul');
    await page.waitForLoadState('networkidle');
    const m = await page.evaluate(() => {
      const g = [...document.querySelectorAll('.z-grid')].find((x) => x.querySelector('.z-frozen')) as HTMLElement;
      const ths = [...g.querySelectorAll('th.z-column')] as HTMLElement[];
      const tds = [...g.querySelectorAll('.z-grid-body tr:first-child td')] as HTMLElement[];
      const sticky = ths.filter((t) => t.classList.contains('z-frozen-sticky'));
      return {
        stickyCount: sticky.length,
        positions: sticky.map((t) => getComputedStyle(t).position),
        headerLeft: ths.slice(0, 3).map((t) => Math.round(t.getBoundingClientRect().left)),
        bodyLeft: tds.slice(0, 3).map((t) => Math.round(t.getBoundingClientRect().left)),
      };
    });
    expect(m.stickyCount, 'ZK must mark frozen header cells sticky on a mobile UA, else the test is vacuous').toBeGreaterThan(0);
    for (const p of m.positions) expect(p).toBe('sticky');
    expect(m.headerLeft, 'header cells must line up with the body cells').toEqual(m.bodyLeft);
  });

  test('frozen footer cells are sticky and stay under their column after a scroll', async ({ page }) => {
    await page.goto('/tree.zul');
    await page.waitForLoadState('networkidle');
    const m = await page.evaluate(() => {
      const t = [...document.querySelectorAll('.z-tree')].find((x) => x.querySelector('.z-frozen') && x.querySelector('.z-treefooter')) as HTMLElement;
      const inner = t.querySelector('.z-frozen-inner') as HTMLElement;
      inner.scrollLeft = 200;
      inner.dispatchEvent(new Event('scroll'));
      return new Promise<{ sticky: number; positions: string[]; headerLeft: number[]; footerLeft: number[] }>((resolve) => setTimeout(() => {
        const foot = [...t.querySelectorAll('.z-treefooter')] as HTMLElement[];
        const ths = [...t.querySelectorAll('th.z-treecol')] as HTMLElement[];
        const sticky = foot.filter((f) => f.classList.contains('z-frozen-sticky'));
        resolve({
          sticky: sticky.length,
          positions: sticky.map((f) => getComputedStyle(f).position),
          headerLeft: ths.slice(0, 2).map((x) => Math.round(x.getBoundingClientRect().left)),
          footerLeft: foot.slice(0, 2).map((x) => Math.round(x.getBoundingClientRect().left)),
        });
      }, 500));
    });
    expect(m.sticky, 'ZK must mark frozen footer cells sticky, else the test is vacuous').toBeGreaterThan(0);
    for (const p of m.positions) expect(p).toBe('sticky');
    expect(m.footerLeft, 'frozen footer cells must stay under the frozen header cells after a scroll').toEqual(m.headerLeft);
  });
});
