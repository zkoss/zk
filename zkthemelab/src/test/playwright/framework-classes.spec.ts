import { test, expect } from '@playwright/test';

// Framework drag/drop + frozen classes — see doc/contracts/framework-classes.md.
// These classes are JS-toggled hooks ZK's client engine emits and expects the theme to style;
// most appear only during an active drag, so we assert the CSS CONTRACT by injecting the exact
// runtime markup (from zk/widget.ts) and reading computed styles — deterministic, no flaky drag.
//
// Requires the preview app on ${PREVIEW_URL} (withjdk.sh 17 mvn test exec:java@preview-app).

const DND = '/dnd.zul';
const GRID = '/grid.zul'; // frozen-columns example lives here, not in dnd.zul

// Inject an element with the given className, return a computed-style prop map, then remove it.
async function injected(page, html: string, selector: string, props: string[]) {
  return page.evaluate(
    ({ html, selector, props }) => {
      const wrap = document.createElement('div');
      wrap.innerHTML = html;
      document.body.appendChild(wrap);
      const el = (selector ? wrap.querySelector(selector) : wrap.firstElementChild) as HTMLElement;
      const cs = getComputedStyle(el);
      const out: Record<string, string> = {};
      for (const p of props) out[p] = (cs as any)[p];
      wrap.remove();
      return out;
    },
    { html, selector, props }
  );
}

const GHOST = (state: string) =>
  `<div class="z-drop-ghost ${state}" style="position:absolute;top:10px;left:10px;">` +
  `<div class="z-drop-content"><span class="z-drop-icon"></span><span class="z-drop-text">Item Alpha</span></div></div>`;

test.describe('framework classes — drag/drop + frozen', () => {
  test('f1 frozen grid body (grid.zul) carries .z-word-nowrap → nowrap', async ({ page }) => {
    // Verify on the real Frozen Columns example in grid.zul. ZK's Frozen.ts adds the class on
    // load to the grid body div; with _dnd.css it must compute white-space:nowrap.
    await page.goto(GRID, { waitUntil: 'networkidle' });
    const el = page.locator('.z-word-nowrap').first();
    await expect(el).toBeAttached();
    const ws = await el.evaluate(e => getComputedStyle(e).whiteSpace);
    expect(ws).toBe('nowrap');
  });

  // Drag/ghost classes appear only during an active drag, so we assert the CSS CONTRACT by
  // injecting the exact runtime markup (from zk/widget.ts) on the dnd.zul page.
  test('d1 .z-dragged de-emphasises the source (opacity 0.4)', async ({ page }) => {
    await page.goto(DND, { waitUntil: 'networkidle' });
    const r = await injected(page, '<div class="z-dragged"></div>', '', ['opacity']);
    expect(r.opacity).toBe('0.4');
  });

  test('d2 .z-drag-over highlights the drop target', async ({ page }) => {
    await page.goto(DND, { waitUntil: 'networkidle' });
    const r = await injected(page, '<div class="z-drag-over"></div>', '', ['backgroundColor', 'outlineStyle']);
    expect(r.backgroundColor).not.toBe('rgba(0, 0, 0, 0)');
    expect(r.outlineStyle).toBe('dashed');
  });

  test('d3 .z-drag-ghost is an elevated, rounded floating clone', async ({ page }) => {
    await page.goto(DND, { waitUntil: 'networkidle' });
    const r = await injected(page, '<div class="z-drag-ghost"></div>', '', ['boxShadow', 'borderTopLeftRadius', 'pointerEvents']);
    expect(r.boxShadow).not.toBe('none');
    expect(r.borderTopLeftRadius).not.toBe('0px');
    expect(r.pointerEvents).toBe('none');
  });

  test('g1 .z-drop-ghost is a styled chip that preserves JS-set position', async ({ page }) => {
    await page.goto(DND, { waitUntil: 'networkidle' });
    const r = await injected(page, GHOST('z-drop-disallow'), '.z-drop-ghost', ['display', 'boxShadow', 'backgroundColor', 'position']);
    // position:absolute blockifies inline-flex → flex (computed). Either is the flex contract.
    expect(['flex', 'inline-flex']).toContain(r.display);
    expect(r.boxShadow).not.toBe('none');
    expect(r.backgroundColor).not.toBe('rgba(0, 0, 0, 0)');
    expect(r.position).toBe('absolute'); // inline style must not be overridden
  });

  test('g2/g3 .z-drop-content is inline-flex and .z-drop-icon is 16px', async ({ page }) => {
    await page.goto(DND, { waitUntil: 'networkidle' });
    const content = await injected(page, GHOST('z-drop-allow'), '.z-drop-content', ['display']);
    // .z-drop-content is a flex item of the ghost → inline-flex blockifies to flex (computed).
    expect(['flex', 'inline-flex']).toContain(content.display);
    const icon = await injected(page, GHOST('z-drop-allow'), '.z-drop-icon', ['width', 'height']);
    expect(icon.width).toBe('16px');
    expect(icon.height).toBe('16px');
  });

  test('g5 allow vs disallow show distinct icon glyphs', async ({ page }) => {
    await page.goto(DND, { waitUntil: 'networkidle' });
    const allow = await injected(page, GHOST('z-drop-allow'), '.z-drop-icon', ['maskImage', 'webkitMaskImage']);
    const disallow = await injected(page, GHOST('z-drop-disallow'), '.z-drop-icon', ['maskImage', 'webkitMaskImage']);
    const a = allow.maskImage !== 'none' ? allow.maskImage : allow.webkitMaskImage;
    const d = disallow.maskImage !== 'none' ? disallow.maskImage : disallow.webkitMaskImage;
    expect(a).not.toBe('none');
    expect(d).not.toBe('none');
    expect(a).not.toBe(d);
  });
});
