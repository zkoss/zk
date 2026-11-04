import { test, expect, type Page } from '@playwright/test';

// The knob mold has no thumb, so dropping the root's outline for the thumb ring once left a
// focused knob with no ring at all (PR zkoss/zk#3687 review). The ring must also stay concentric
// with the arc: the arc is drawn from the SVG's corner, so a mis-sized SVG puts the ring off-centre.
// Fixture: web/pv/slider-knob-focus-guard.zul.

const FIXTURE = '/pv/slider-knob-focus-guard.zul';
const MAX_CENTRE_OFFSET_PX = 1;

type Centre = { x: number; y: number };

const KNOB = '#knob';
const HORIZONTAL = '#horizontal';
const VERTICAL = '#vertical';

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.evaluate(() => document.fonts.ready);
  // The ring grows with a transition on some themes; measure the settled state.
  await page.addStyleTag({ content: '*, *::before, *::after { transition: none !important; }' });
});

// ZK gives each widget a generated uuid; the root is the element holding the slider widget, so
// resolve the fixture's component id through the widget API.
async function rootOf(page: Page, componentId: string): Promise<string> {
  return page.evaluate(id => zk.Widget.$('$' + id.slice(1)).uuid, componentId);
}

async function focusByKeyboard(page: Page, componentId: string) {
  const uuid = await rootOf(page, componentId);
  const root = page.locator(`[id="${uuid}"]`);
  await root.focus();
  await page.keyboard.press('Shift+Tab'); // re-enter by keyboard so :focus-visible matches
  await page.keyboard.press('Tab');
  await expect(root).toBeFocused();
  return root;
}

const centreOf = (r: { x: number; y: number; width: number; height: number }): Centre =>
  ({ x: r.x + r.width / 2, y: r.y + r.height / 2 });

test('slider knob — keyboard focus draws a ring concentric with the arc', async ({ page }) => {
  const root = await focusByKeyboard(page, KNOB);
  const svg = root.locator('svg.z-slider-knob-svg');
  // The inner path is the full background circle of the arc, so its box centre is the arc centre.
  const arcBox = (await root.locator('path.z-slider-knob-inner').boundingBox())!;
  const ringBox = (await svg.boundingBox())!;
  const outline = await svg.evaluate(el => {
    const cs = getComputedStyle(el);
    return { style: cs.outlineStyle, width: parseFloat(cs.outlineWidth), radius: cs.borderTopLeftRadius };
  });

  expect(outline.style).toBe('solid');
  expect(outline.width).toBeGreaterThan(0);
  expect(outline.radius).not.toBe('0px');
  const arc = centreOf(arcBox), ring = centreOf(ringBox);
  expect(Math.abs(arc.x - ring.x)).toBeLessThanOrEqual(MAX_CENTRE_OFFSET_PX);
  expect(Math.abs(arc.y - ring.y)).toBeLessThanOrEqual(MAX_CENTRE_OFFSET_PX);
  expect(Math.abs(ringBox.width - ringBox.height)).toBeLessThanOrEqual(MAX_CENTRE_OFFSET_PX);
});

// The knob JS places the input and maps a click around a centre measured from the root's top edge,
// so any top padding on the root put both above the arc (PR zkoss/zk#3687 review).
test('slider knob — the input and the click centre sit on the arc centre', async ({ page }) => {
  const uuid = await rootOf(page, KNOB);
  const root = page.locator(`[id="${uuid}"]`);
  const arcBox = (await root.locator('path.z-slider-knob-inner').boundingBox())!;
  const inputBox = (await root.locator('.z-slider-input').boundingBox())!;
  const arc = centreOf(arcBox), input = centreOf(inputBox);
  expect(Math.abs(arc.x - input.x)).toBeLessThanOrEqual(MAX_CENTRE_OFFSET_PX);
  expect(Math.abs(arc.y - input.y)).toBeLessThanOrEqual(MAX_CENTRE_OFFSET_PX);

  // The arc is a full circle from 0 at six o'clock, so its three o'clock point is 75. Playwright's
  // box pads the path by its stroke; getBoundingClientRect() gives the bare circle, whose right edge
  // is the middle of the stroke.
  const rightEdge = await root.locator('path.z-slider-knob-inner').evaluate(el => el.getBoundingClientRect().right);
  await page.mouse.click(rightEdge, arc.y);
  await expect.poll(() => page.evaluate(id => zk.Widget.$('$' + id.slice(1)).getCurpos(), KNOB))
    .toBeCloseTo(75, 0);
});

test('slider knob — the root draws no rectangular outline of its own', async ({ page }) => {
  const root = await focusByKeyboard(page, KNOB);
  expect(await root.evaluate(el => getComputedStyle(el).outlineStyle)).toBe('none');
});

for (const [name, selector] of [['horizontal', HORIZONTAL], ['vertical', VERTICAL]]) {
  test(`slider ${name} — the ring is on the thumb, not on the root`, async ({ page }) => {
    const root = await focusByKeyboard(page, selector);
    expect(await root.evaluate(el => getComputedStyle(el).outlineStyle)).toBe('none');
    expect(await root.locator('.z-slider-button').evaluate(el => getComputedStyle(el).outlineStyle)).toBe('solid');
  });
}

test('slider knob — the ring survives forced-colors as a Highlight outline', async ({ page }) => {
  await page.emulateMedia({ forcedColors: 'active' });
  const root = await focusByKeyboard(page, KNOB);
  const ring = await root.locator('svg.z-slider-knob-svg').evaluate(el => {
    const cs = getComputedStyle(el);
    const probe = document.createElement('span');
    probe.style.color = 'Highlight';
    document.body.appendChild(probe);
    const highlight = getComputedStyle(probe).color;
    probe.remove();
    return { style: cs.outlineStyle, width: parseFloat(cs.outlineWidth), color: cs.outlineColor, highlight };
  });
  expect(ring.style).toBe('solid');
  expect(ring.width).toBe(2);
  expect(ring.color).toBe(ring.highlight);
});
