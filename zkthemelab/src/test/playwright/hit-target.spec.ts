import { test, expect, Page } from '@playwright/test';

// Interaction guard: an interactive knob must be reachable AT ITS OWN CENTRE.
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
//
// See tasks/multislider-knob-hit-target-analysis.md.

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
