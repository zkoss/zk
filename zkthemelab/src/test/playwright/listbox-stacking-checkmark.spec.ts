import { test, expect } from '@playwright/test';

// Guard for zkmax sel/css/listbox.css (ZK-6112): in a stacking listbox card the checkbox keeps the
// table-row geometry. Its 16px box sits on the cell's inline padding (the card keyline) and the
// text follows it after --zk-spacing-3. The checkable span used to keep the inline padding it has in
// a table row, so the box touched the text of a visible first column and was clipped in half when
// the first column was hidden from the card.
// Fixture: web/pv/listbox-stacking-checkmark-guard.zul

const FIXTURE = '/pv/listbox-stacking-checkmark-guard.zul';
const TOLERANCE_PX = 1.5;

type Geometry = { inset: number; padding: number; gap: number; spacing: number; top: number; painted: boolean };

for (const density of ['comfortable', 'compact'] as const) {
  test.describe(density, () => {
    test.beforeEach(async ({ page }) => {
      const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
      expect(resp?.status()).toBe(200);
      if (density === 'compact') {
        await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
      }
      await page.evaluate(() => document.fonts.ready);
    });

    const measure = (page: import('@playwright/test').Page, sclass: string) =>
      page.locator(`.${sclass}`).evaluate((lb): Geometry => {
        const row = lb.querySelector('tr.z-listitem') as HTMLElement;
        const icon = row.querySelector('.z-listitem-icon')!.getBoundingClientRect();
        const card = row.getBoundingClientRect();
        // The first card field that shows a value: the checkbox cell itself, or the cell after the hidden one.
        const field = [...row.children].find(td => !td.classList.contains('z-cell-cm-only')
          && getComputedStyle(td).display !== 'none') as HTMLElement;
        const walker = document.createTreeWalker(field, NodeFilter.SHOW_TEXT);
        let glyph: DOMRect | undefined;
        while (walker.nextNode()) {
          const text = walker.currentNode.textContent!;
          const i = text.search(/\S/);
          if (i < 0) continue;
          const range = document.createRange();
          range.setStart(walker.currentNode, i);
          range.setEnd(walker.currentNode, i + 1);
          glyph = range.getBoundingClientRect();
          break;
        }
        const hit = (x: number) => {
          const e = document.elementFromPoint(x, icon.top + icon.height / 2);
          return !!e && (e === row.querySelector('.z-listitem-checkable') || row.querySelector('.z-listitem-checkable')!.contains(e));
        };
        const probe = document.createElement('div');
        probe.style.width = 'var(--zk-spacing-3)';
        document.body.appendChild(probe);
        const spacing = probe.getBoundingClientRect().width;
        probe.remove();
        return {
          inset: icon.left - card.left - parseFloat(getComputedStyle(row).borderLeftWidth),
          // The checkbox's own cell keeps the density's cell padding on its start side.
          padding: parseFloat(getComputedStyle(row.querySelector('.z-listitem-checkable')!.closest('td')!).paddingLeft),
          gap: glyph!.left - icon.right,
          spacing,
          top: Math.round(icon.top - card.top),
          painted: hit(icon.left + 2) && hit(icon.right - 2),
        };
      });

    for (const sclass of ['cm-lead', 'cm-only']) {
      test(`${sclass}: the checkbox sits on the keyline, fully painted, --zk-spacing-3 from the text`, async ({ page }) => {
        const g = await measure(page, sclass);
        expect(g.painted, 'both ends of the checkbox are painted (not clipped)').toBe(true);
        expect(Math.abs(g.inset - g.padding), `checkbox inset ${g.inset}px vs cell padding ${g.padding}px`)
          .toBeLessThanOrEqual(TOLERANCE_PX);
        expect(Math.abs(g.gap - g.spacing), `checkbox-to-text gap ${g.gap}px vs --zk-spacing-3 ${g.spacing}px`)
          .toBeLessThanOrEqual(TOLERANCE_PX);
      });
    }

    test('cm-lead and cm-only put the checkbox at the same place in the card', async ({ page }) => {
      const lead = await measure(page, 'cm-lead');
      const only = await measure(page, 'cm-only');
      expect(Math.abs(lead.inset - only.inset)).toBeLessThanOrEqual(0.5);
      expect(lead.top).toBe(only.top);
    });
  });
}
