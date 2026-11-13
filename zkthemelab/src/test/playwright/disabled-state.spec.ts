import { test, expect, Page } from '@playwright/test';

// Disabled state: a disabled part that does nothing when clicked looks disabled, and no hover highlight
// suggests otherwise. Each component keeps its own fixture and helpers inside its describe.

declare const zk: any;

// Guard for tree.css on a disabled treeitem with checkmark="true" (ZK-6112, ZK-332): only the row text is
// dimmed, so the checkbox or radio, whose ::before border the text colour does not reach, must be dimmed on
// its own; clicking it does nothing. Hovering the row must not turn its border primary either.
// Fixture: web/pv/tree-disabled-checkmark-guard.zul
test.describe('treeitem', () => {
  const FIXTURE = '/pv/tree-disabled-checkmark-guard.zul';
  const DISABLED_OPACITY = 0.38;

  // The DOM id of a treeitem's row, by the item's ZUL id.
  const rowId = (page: Page, item: string): Promise<string> =>
    page.evaluate((item) => zk.Widget.$('$' + item).treerow.uuid, item);

  async function effectiveOpacity(page: Page, item: string): Promise<number> {
    const id = await rowId(page, item);
    return page.locator(`[id="${id}"] .z-treerow-checkable`).evaluate((span) => {
      let opacity = 1;
      for (let n: Element | null = span; n && !n.classList.contains('z-tree'); n = n.parentElement)
        opacity *= parseFloat(getComputedStyle(n).opacity);
      return Math.round(opacity * 100) / 100;
    });
  }

  async function borderColor(page: Page, item: string): Promise<string> {
    const id = await rowId(page, item);
    return page.locator(`[id="${id}"] .z-treerow-checkable > i`)
      .evaluate((icon) => getComputedStyle(icon, '::before').borderTopColor);
  }

  async function hover(page: Page, item: string) {
    await page.locator(`[id="${await rowId(page, item)}"] .z-treecell-text`).hover();
  }

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
  });

  test('treeitem — a disabled item dims its checkbox and radio, an enabled one does not', { tag: '@ZK-332' }, async ({ page }) => {
    expect(await effectiveOpacity(page, 'multiOn')).toBe(1);
    expect(await effectiveOpacity(page, 'multiOff')).toBe(DISABLED_OPACITY);
    expect(await effectiveOpacity(page, 'multiOffSelected')).toBe(DISABLED_OPACITY);
    expect(await effectiveOpacity(page, 'singleOn')).toBe(1);
    expect(await effectiveOpacity(page, 'singleOff')).toBe(DISABLED_OPACITY);
  });

  for (const [tree, kind] of [['multi', 'checkbox'], ['single', 'radio']] as const) {
    test(`treeitem — hovering a disabled row keeps its ${kind} border`, { tag: '@ZK-332' }, async ({ page }) => {
      const atRest = await borderColor(page, `${tree}Off`);
      await hover(page, `${tree}On`);
      await expect.poll(() => borderColor(page, `${tree}On`), 'an enabled row highlights its box on hover')
        .not.toBe(atRest);
      await hover(page, `${tree}Off`);
      await expect.poll(() => borderColor(page, `${tree}Off`)).toBe(atRest);
    });
  }
});

// Guard for the disabled field family: a disabled field swaps its outline for outline-variant and its
// fill for surface-container-low, so one disabled field does not read as an enabled one beside the
// textbox. The opacity is left alone; every field dims by the same token.
// Fixture: web/pv/disabled-fields-guard.zul
test.describe('disabled fields', () => {
  const FIXTURE = '/pv/disabled-fields-guard.zul';
  const FIELDS = ['intbox', 'combobox', 'bandbox', 'datebox', 'timebox', 'spinner', 'doublespinner', 'timepicker',
    'daterangebox', 'chosenbox', 'searchbox', 'selectbox', 'cascader', 'colorbox'];

  type Surface = { border: string; background: string };

  // The root's own border and fill, by the field's ZUL id (a textbox or intbox is its own root).
  const surfaceOf = (page: Page, id: string): Promise<Surface> =>
    page.evaluate((id) => {
      const style = getComputedStyle(zk.Widget.$('$' + id).$n());
      return { border: style.borderTopColor, background: style.backgroundColor };
    }, id);

  // A token's computed colour, resolved through a probe element.
  const tokenColor = (page: Page, token: string): Promise<string> =>
    page.evaluate((token) => {
      const probe = document.body.appendChild(document.createElement('span'));
      probe.style.color = `var(${token})`;
      const color = getComputedStyle(probe).color;
      probe.remove();
      return color;
    }, token);

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
  });

  test('textbox — a disabled root uses outline-variant and surface-container-low', async ({ page }) => {
    expect(await surfaceOf(page, 'textbox')).toEqual({
      border: await tokenColor(page, '--zk-color-outline-variant'),
      background: await tokenColor(page, '--zk-color-surface-container-low'),
    });
  });

  for (const field of FIELDS) {
    test(`${field} — a disabled root draws the same border and fill as a disabled textbox`, async ({ page }) => {
      expect(await surfaceOf(page, field)).toEqual(await surfaceOf(page, 'textbox'));
    });
  }
});
