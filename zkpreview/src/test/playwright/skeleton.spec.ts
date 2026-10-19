import { test, expect, type Locator, type Page } from '@playwright/test';

// Skeleton placeholder (ZK-6099, PE; skeleton.css in zkex), asserted on skeleton.zul.
// Computed styles of the ordinary-element mask, the overlay div laid over replaced and
// form-control roots, reduced motion, the --zk-skeleton-* overrides, and the runtime toggle.
// The static look of every section is in the gallery project (skeleton-0N-*.png).

// --zk-color-surface-variant, the placeholder fill when no override is set.
const FILL = 'rgb(240, 244, 250)';
const PURPLE = 'rgb(103, 80, 164)'; // #6750a4

const radiusOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).borderTopLeftRadius);
const bgOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).backgroundColor);
const animationOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).animationName);

async function open(page: Page) {
  await page.goto('/skeleton.zul', { waitUntil: 'networkidle' });
  await page.evaluate(() => document.fonts.ready.then(() => true));
}

test.describe('skeleton', () => {
  test('rect, circle and text roots take their own radius and the placeholder fill', async ({ page }) => {
    await open(page);
    const label = (shape: string) => page.locator(`.z-label.z-skeleton-active.z-skeleton-${shape}`).first();
    expect(await radiusOf(label('rect'))).toBe('4px'); // --zk-shape-corner-extra-small
    expect(await radiusOf(label('circle'))).toBe('50%');
    expect(await radiusOf(label('text'))).toBe('2px');
    for (const shape of ['rect', 'circle', 'text']) {
      expect(await bgOf(label(shape)), shape).toBe(FILL);
      expect(await animationOf(label(shape)), shape).toBe('z-skeleton-pulse');
      await expect(label(shape), shape).toHaveAttribute('aria-busy', 'true');
    }
  });

  test('an ordinary root hides its descendants and paints an opaque ::after over its own text', async ({ page }) => {
    await open(page);
    const grid = page.locator('.z-grid.z-skeleton-active').first();
    expect(await grid.locator('.z-row-content').first().evaluate((el) => getComputedStyle(el).visibility)).toBe('hidden');
    const after = await grid.evaluate((el) => {
      const a = getComputedStyle(el, '::after');
      return { content: a.content, position: a.position, bg: a.backgroundColor, w: a.width, h: a.height };
    });
    expect(after.content).toBe('""');
    expect(after.position).toBe('absolute');
    expect(after.bg).toBe(FILL);
    const box = await grid.evaluate((el) => ({ w: `${el.clientWidth}px`, h: `${el.clientHeight}px` }));
    expect({ w: after.w, h: after.h }).toEqual(box); // inset: 0 covers the whole root
  });

  test('a replaced or form-control root gets exactly one overlay div over its own box and is masked', async ({ page }) => {
    await open(page);
    for (const tag of ['IMG', 'INPUT', 'TEXTAREA', 'SELECT']) {
      const root = page.locator(`${tag.toLowerCase()}.z-skeleton-active.z-skeleton-masked`).first();
      expect(await root.evaluate((el) => getComputedStyle(el).visibility), tag).toBe('hidden');
      const overlays = root.locator('xpath=..').locator('.z-skeleton-overlay');
      await expect(overlays, tag).toHaveCount(1);
      const rootBox = await root.evaluate((el: HTMLElement) => [el.offsetLeft, el.offsetTop, el.offsetWidth, el.offsetHeight]);
      const overlayBox = await overlays.evaluate((el: HTMLElement) => [el.offsetLeft, el.offsetTop, el.offsetWidth, el.offsetHeight]);
      expect(overlayBox, tag).toEqual(rootBox);
      expect(await bgOf(overlays), tag).toBe(FILL);
      expect(await animationOf(overlays), tag).toBe('z-skeleton-pulse');
    }
  });

  test('prefers-reduced-motion stops the pulse on both the in-place mask and the overlay div', async ({ page }) => {
    await page.emulateMedia({ reducedMotion: 'no-preference' });
    await open(page);
    const ordinary = page.locator('.z-label.z-skeleton-active').first();
    const overlay = page.locator('.z-skeleton-overlay').first();
    expect(await animationOf(ordinary)).toBe('z-skeleton-pulse');
    expect(await animationOf(overlay)).toBe('z-skeleton-pulse');
    await page.emulateMedia({ reducedMotion: 'reduce' });
    expect(await animationOf(ordinary)).toBe('none');
    expect(await animationOf(overlay)).toBe('none');
  });

  test('--zk-skeleton-* overrides at :root reach the ordinary root, its ::after and the overlay div', async ({ page }) => {
    await open(page);
    const rect = page.locator('.z-label.z-skeleton-active.z-skeleton-rect').first();
    const text = page.locator('.z-label.z-skeleton-active.z-skeleton-text').first();
    const overlay = page.locator('.z-skeleton-overlay.z-skeleton-rect').first();
    await page.addStyleTag({
      content: ':root{--zk-skeleton-background:#6750a4;--zk-skeleton-radius:0px;--zk-skeleton-text-radius:9px;--zk-skeleton-duration:3s}',
    });
    expect(await bgOf(rect)).toBe(PURPLE);
    expect(await rect.evaluate((el) => getComputedStyle(el, '::after').backgroundColor)).toBe(PURPLE);
    expect(await bgOf(overlay)).toBe(PURPLE);
    expect(await radiusOf(rect)).toBe('0px');
    expect(await radiusOf(overlay)).toBe('0px');
    expect(await radiusOf(text)).toBe('9px');
    expect(await rect.evaluate((el) => getComputedStyle(el).animationDuration)).toBe('3s');
  });

  test('setSkeleton and clearing it at runtime add and remove the mask, and aria-busy goes with it', async ({ page }) => {
    await open(page);
    const target = page.locator('.z-label', { hasText: 'Toggled content' });
    await expect(target).not.toHaveClass(/z-skeleton-active/);
    await expect(target).not.toHaveAttribute('aria-busy', /.*/);

    await page.getByRole('button', { name: 'Set skeleton', exact: true }).click();
    await expect(target).toHaveClass(/z-skeleton-active z-skeleton-rect/);
    await expect(target).toHaveAttribute('aria-busy', 'true');
    expect(await bgOf(target)).toBe(FILL);

    await page.getByRole('button', { name: 'Clear skeleton', exact: true }).click();
    await expect(target).not.toHaveClass(/z-skeleton/);
    await expect(target).not.toHaveAttribute('aria-busy', /.*/);
  });
});
