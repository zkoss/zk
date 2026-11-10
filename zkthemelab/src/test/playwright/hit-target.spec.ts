import { test, expect, Page } from '@playwright/test';

// Interaction guard: an interactive part must be reachable where the user aims at it. The first guard is for
// knobs, the others for the buttons of read-only fields and the popup of an empty combobox.
//
// A knob must be reachable AT ITS OWN CENTRE.
//
// Why this assertion and not `.hover()`: a knob covered by a sibling overlay
// still *renders* correctly and still reacts near its edges, so every visual and
// computed-style check passes. Playwright's hover does catch it, but reports it
// as "element intercepts pointer events / retrying" and then times out — a
// message that names the wrong culprit and hides how many knobs are affected.
// `elementFromPoint(centre) === knob` states the actual requirement: the point a
// user aims at belongs to the thing they are aiming for.
//
// The defect this was written for (multislider, 2026-09-08): every
// <sliderbuttons> child renders its OWN `.z-sliderbuttons` div, and the theme
// stretches each one across the full rail. With no z-index anywhere they resolve
// by DOM order, so the LAST range's container paints over every earlier range's
// knobs and swallows the pointer at their centres — 2(N-1) of 2N knobs dead for
// N ranges, while a single-range slider (the only shape previously exercised)
// stays healthy. Fixed by lifting the knob in multislider.css.
//
// Scanning every knob on the page is load-bearing, not thoroughness theatre: the
// topmost range is always reachable, so any spot check that happens to pick it —
// `.first()` included, depending on markup order — reports a false green.

interface Variant {
  name: string;
  url: string;
  widget: string;   // one slider instance
  knob: string;     // interactive knob within it
}

const VARIANTS: Variant[] = [
  { name: 'multislider', url: '/multislider.zul', widget: '.z-multislider', knob: '.z-sliderbuttons-button' },
  { name: 'rangeslider', url: '/rangeslider.zul', widget: '.z-rangeslider', knob: '.z-sliderbuttons-button' },
  { name: 'slider',      url: '/slider.zul',      widget: '.z-slider',      knob: '.z-slider-button' },
];

/** Every knob that does not own its own centre point, as readable labels. */
async function unreachableKnobs(page: Page, v: Variant): Promise<string[]> {
  return page.evaluate(({ widget, knob }) => {
    const out: string[] = [];
    document.querySelectorAll(widget).forEach((slider, si) => {
      slider.querySelectorAll(knob).forEach((el, ki) => {
        // Skip knobs that are not a pointer target in the first place: a
        // disabled widget sets pointer-events:none (inherited), and a knob in a
        // collapsed/hidden slider has no box to aim at.
        if (getComputedStyle(el).pointerEvents === 'none') return;
        // elementFromPoint takes VIEWPORT coordinates and returns null outside
        // them, so a knob below the fold must be scrolled in before it can be
        // hit-tested — otherwise every off-screen slider reports a phantom
        // failure. Re-read the rect afterwards: scrolling moved it.
        el.scrollIntoView({ block: 'center' });
        const r = el.getBoundingClientRect();
        if (!r.width || !r.height) return;
        const hit = document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2);
        if (hit === el) return;
        const blocker = hit ? `${hit.id || '(no id)'}.${(hit.className || '').trim()}` : 'nothing';
        out.push(`slider[${si}] knob[${ki}] at ${(el as HTMLElement).style.left || (el as HTMLElement).style.top || '?'} blocked by ${blocker}`);
      });
    });
    return out;
  }, v);
}

for (const v of VARIANTS) {
  test(`${v.name}: every knob owns its own centre`, async ({ page }) => {
    await page.goto(v.url, { waitUntil: 'domcontentloaded' });
    await page.locator(v.knob).first().waitFor({ state: 'visible' });
    await page.evaluate(() => document.fonts.ready);

    expect(await unreachableKnobs(page, v)).toEqual([]);
  });
}

// Guard for timebox.css and spinner.css (ZK-6112): the up and down buttons of a read-only Timebox, Spinner and
// Doublespinner change the value in ZK, only typing is blocked (Timebox javadoc: "the value of a read-only time
// box can be changed by clicking the up or down button"), so the buttons must take the pointer and not look
// disabled. A real click is used because pointer-events: none only shows up there.
// Fixture: web/pv/readonly-spin-buttons-guard.zul
declare const zk: any;
declare const jq: any;

for (const name of ['timebox', 'spinner', 'doublespinner']) {
  test.describe(`${name} read-only buttons`, () => {
    test.beforeEach(async ({ page }) => {
      const resp = await page.goto('/pv/readonly-spin-buttons-guard.zul', { waitUntil: 'networkidle' });
      expect(resp?.status()).toBe(200);
      await page.evaluate(() => document.fonts.ready);
    });

    test(`${name} — a click on the up button of a read-only field changes the value`, { tag: '@ZK-6112' }, async ({ page }) => {
      const value = () => page.evaluate((id) => zk.Widget.$('$' + id).getInputNode().value, name);
      const before = await value();
      const box = await page.evaluate((id) => {
        const { x, y, width, height } = zk.Widget.$('$' + id).$n().querySelector('.z-' + id + '-button').getBoundingClientRect();
        return { x, y, width, height };
      }, name);
      // the upper half of the button is the up button
      await page.mouse.click(box.x + box.width / 2, box.y + box.height / 4);
      await expect.poll(value).not.toBe(before);
    });

    test(`${name} — the buttons of a read-only field are not dimmed`, { tag: '@ZK-6112' }, async ({ page }) => {
      const opacity = await page.evaluate((id) => {
        const button = zk.Widget.$('$' + id).$n().querySelector('.z-' + id + '-button');
        return getComputedStyle(button).opacity;
      }, name);
      expect(opacity).toBe('1');
    });
  });
}

// Guard for combobox.css (ZK-6112, B30-1879511): an open Combobox without items shows its popup as a surface under
// the field, and a click on it closes the list and keeps the focus in the field (ComboWidget.doClick_). Without a
// minimum height the popup is 0px tall and the click lands on the page, which blurs the field.
// Fixture: web/pv/empty-combobox-popup-guard.zul
test.describe('combobox', () => {
  const MIN_POPUP_HEIGHT_PX = 16; // --zk-spacing-4

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto('/pv/empty-combobox-popup-guard.zul', { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    await page.evaluate(() => document.fonts.ready);
    const button = await page.evaluate(() => {
      const { x, y, width, height } = zk.Widget.$('$combobox').$n().querySelector('.z-combobox-button').getBoundingClientRect();
      return { x, y, width, height };
    });
    await page.mouse.click(button.x + button.width / 2, button.y + button.height / 2);
    await page.waitForFunction(() => {
      const combobox = zk.Widget.$('$combobox');
      return combobox.isOpen() && !jq(combobox.getPopupNode_()).is(':animated');
    });
  });

  const popupBox = (page: Page) => page.evaluate(() => {
    const { x, y, width, height } = zk.Widget.$('$combobox').getPopupNode_().getBoundingClientRect();
    return { x, y, width, height };
  });

  test('combobox — an open popup without items is a visible surface', { tag: '@ZK-6112' }, async ({ page }) => {
    expect((await popupBox(page)).height).toBeGreaterThanOrEqual(MIN_POPUP_HEIGHT_PX);
  });

  test('combobox — a click on the popup without items closes the list and keeps the focus in the field', { tag: '@ZK-6112' }, async ({ page }) => {
    const box = await popupBox(page);
    await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2);
    await expect.poll(() => page.evaluate(() => zk.Widget.$('$combobox').isOpen())).toBe(false);
    expect(await page.evaluate(() => document.activeElement === zk.Widget.$('$combobox').getInputNode())).toBe(true);
  });
});

