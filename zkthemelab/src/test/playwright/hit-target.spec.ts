import { test, expect, Page } from '@playwright/test';

// Interaction guard: an interactive part must be reachable where the user aims at it. The first guard is for
// knobs, the others for the buttons of read-only fields, the popup of an empty combobox and the state-layer ring
// of a checkbox, radio and switch.
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

// Guard for checkbox.css (ZK-6112, B50-2976934): the state-layer ring around the box of a Checkbox, the circle
// of a Radio and the thumb of a switch is the touch target, and it shows the pointer, so a click on it toggles the
// control; a disabled control still takes no click, because the ring inherits `pointer-events: none` from its wrapper.
// The click is made just outside the box, where only the ring is.
// Fixture: web/pv/checkbox-ring-guard.zul
test.describe('checkbox ring', () => {
  const OUTSIDE_BOX_PX = 2;
  // [widget id, widget name, what the box is]
  const CONTROLS = [
    ['checkbox', 'checkbox', '.z-checkbox-mold'],
    ['radio', 'radio', 'input'],
    ['switch', 'switch', '.z-checkbox-mold'],
  ] as const;

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto('/pv/checkbox-ring-guard.zul', { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    await page.evaluate(() => document.fonts.ready);
  });

  const pointBesideBox = async (page: Page, id: string, boxSelector: string) => {
    const box = await page.evaluate(({ id, boxSelector }) => {
      const { x, y, height } = zk.Widget.$('$' + id).$n().querySelector(boxSelector).getBoundingClientRect();
      return { x, y, height };
    }, { id, boxSelector });
    return { x: box.x - OUTSIDE_BOX_PX, y: box.y + box.height / 2 };
  };
  const clickBesideBox = async (page: Page, id: string, boxSelector: string) => {
    const { x, y } = await pointBesideBox(page, id, boxSelector);
    await page.mouse.click(x, y);
  };
  const isChecked = (page: Page, id: string) => page.evaluate((id) => zk.Widget.$('$' + id).isChecked(), id);

  for (const [id, name, boxSelector] of CONTROLS) {
    test(`${name} — a click on the ring beside the box toggles the control`, { tag: '@ZK-6112' }, async ({ page }) => {
      await clickBesideBox(page, id, boxSelector);
      await expect.poll(() => isChecked(page, id)).toBe(true);
    });

    // ZK ignores a click on a disabled control anyway, so the hit test is what tells a dead ring from a live one.
    test(`${name} — the ring of a disabled control takes no pointer event and a click on it does nothing`, { tag: '@ZK-6112' }, async ({ page }) => {
      const { x, y } = await pointBesideBox(page, id + 'Disabled', boxSelector);
      const hitsControl = await page.evaluate(({ id, x, y }) => {
        const hit = document.elementFromPoint(x, y);
        return !!hit && zk.Widget.$('$' + id).$n().contains(hit);
      }, { id: id + 'Disabled', x, y });
      expect(hitsControl).toBe(false);
      await page.mouse.click(x, y);
      await page.waitForTimeout(300);
      expect(await isChecked(page, id + 'Disabled')).toBe(false);
    });
  }
});

// Guard for checkbox.css in compact density (ZK-6112, B50-2976934): the ring is as tall as the control height, so it
// stays inside its wrapper. A default-size ring in a 32px wrapper would overlap the control above or below, and a
// click at the bottom edge of the upper control would toggle the lower one.
// Fixture: web/pv/compact-ring-guard.zul
test.describe('checkbox ring in compact density', () => {
  const ABOVE_BOTTOM_PX = 1;
  // [widget name, upper id, lower id, what the box is]
  const PAIRS = [
    ['checkbox', 'checkbox1', 'checkbox2', '.z-checkbox-mold'],
    ['radio', 'radio1', 'radio2', 'input'],
    ['switch', 'switch1', 'switch2', '.z-checkbox-mold'],
  ] as const;

  test.beforeEach(async ({ page }) => {
    const resp = await page.goto('/pv/compact-ring-guard.zul', { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    await page.evaluate(() => document.fonts.ready);
    await page.evaluate(() => { document.documentElement.dataset.density = 'compact'; });
  });

  for (const [name, upper, lower, boxSelector] of PAIRS) {
    test(`${name} — a click at the bottom edge of the upper control toggles only the upper one`, { tag: '@ZK-6112' }, async ({ page }) => {
      const point = await page.evaluate(({ upper, boxSelector }) => {
        const wrapper = zk.Widget.$('$' + upper).$n();
        const box = wrapper.querySelector(boxSelector).getBoundingClientRect();
        return { x: box.x + box.width / 2, y: wrapper.getBoundingClientRect().bottom };
      }, { upper, boxSelector });
      await page.mouse.click(point.x, point.y - ABOVE_BOTTOM_PX);
      const checked = (id: string) => page.evaluate((id) => zk.Widget.$('$' + id).isChecked(), id);
      await expect.poll(() => checked(upper)).toBe(true);
      expect(await checked(lower)).toBe(false);
    });
  }
});

// A combobox, bandbox, datebox, timebox, spinner, daterangebox or timepicker narrower than its input plus its
// button keeps the given width, and the input gives way instead of the button: the button stays whole inside
// the box and owns its centre. The datebox and timebox inputs used to keep an em floor, so at
// 70–100px the button ran past the box and the next field took the click (B50-3101558); the daterangebox's
// button did the same below about 220px, and the timepicker clipped its button below about 100px.
// Fixture: web/pv/input-narrow-guard.zul
test('datebox — a narrow combobox, bandbox, datebox, timebox, spinner, daterangebox and timepicker keep their button inside the box', async ({ page }) => {
  const resp = await page.goto('/pv/input-narrow-guard.zul', { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.evaluate(() => document.fonts.ready);

  const problems = await page.evaluate(() => {
    const out: string[] = [];
    const boxes = document.querySelectorAll<HTMLElement>('.ing-fit > :is(.z-combobox, .z-bandbox, .z-datebox, .z-timebox, .z-spinner, .z-daterangebox, .z-timepicker)');
    if (boxes.length !== 14) out.push(`found ${boxes.length} fields, not 14`);
    boxes.forEach((box, i) => {
      const name = box.className.split(' ')[0].slice(2);
      const button = box.querySelector(`.z-${name}-button`)!;
      const b = box.getBoundingClientRect();
      const r = button.getBoundingClientRect();
      if (Math.round(b.width) !== parseInt(box.style.width)) out.push(`${name}[${i}] is ${b.width}px, not ${box.style.width}`);
      if (r.left < b.left - 0.5 || r.right > b.right + 0.5) out.push(`${name}[${i}] button ${r.left}–${r.right} runs past the box ${b.left}–${b.right}`);
      const hit = document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2);
      if (!hit || !button.contains(hit)) out.push(`${name}[${i}] button centre hits ${hit ? hit.className : 'nothing'}`);
    });
    return out;
  });
  expect(problems).toEqual([]);
});

// B50-3101558: five fields at 35px, narrower than their input's padding plus their button. The box keeps the
// given width and clips its parts, so the part of the button that shows belongs to the button (the datebox's and
// timebox's inputs used to fill the box and push the button out) and the left edge of the next field belongs to
// that field (the spinner's arrows used to sit on it). Parts that run under the next field are not detectable here.
// The textbox at the end only gives the spinner a next field.
// Fixture: web/pv/input-narrow-guard.zul
test('datebox — a 35px combobox, bandbox, datebox, timebox and spinner show their button and leave the next field alone', async ({ page }) => {
  const resp = await page.goto('/pv/input-narrow-guard.zul', { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.evaluate(() => document.fonts.ready);

  const problems = await page.evaluate(() => {
    const out: string[] = [];
    const fields = [...document.querySelectorAll<HTMLElement>('.ing-35 > [class^="z-"]')];
    if (fields.length !== 6) out.push(`found ${fields.length} fields, not 6`);
    fields.slice(0, -1).forEach((box, i) => {
      const name = box.className.split(' ')[0].slice(2);
      const next = fields[i + 1];
      const b = box.getBoundingClientRect();
      const n = next.getBoundingClientRect();
      const y = b.top + b.height / 2;
      if (Math.round(b.width) !== 35) out.push(`${name} is ${b.width}px, not 35px`);
      const atNext = document.elementFromPoint(n.left + 2, y);
      if (!atNext || !next.contains(atNext)) out.push(`${name} covers the left edge of the next field: ${atNext ? atNext.className : 'nothing'}`);
      const button = box.querySelector(`.z-${name}-button`)!;
      const r = button.getBoundingClientRect();
      const x = Math.min(r.right, b.right) - 2;
      const atButton = document.elementFromPoint(x, y);
      if (x <= r.left || !atButton || !button.contains(atButton)) out.push(`${name} shows no button at its right edge: ${atButton ? atButton.className : 'nothing'}`);
    });
    return out;
  });
  expect(problems).toEqual([]);
});

// A field's min-width floor sizes it only while the page leaves its width open: a selectbox, slider or
// multislider given 80px is 80px wide, not its 120px floor, and the floored fields without a width keep it.
// Fixture: web/pv/input-narrow-guard.zul
test('datebox — a set width wins over a field\'s min-width floor, which still sizes a field without one', async ({ page }) => {
  const resp = await page.goto('/pv/input-narrow-guard.zul', { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.evaluate(() => document.fonts.ready);

  const sized = await page.locator('.ing-width > *').evaluateAll(els =>
    els.map(el => `${el.className.split(' ')[0]} ${Math.round(el.getBoundingClientRect().width)}`));
  expect(sized).toEqual(['z-selectbox 80', 'z-slider 80', 'z-multislider 80']);

  // The daterangebox and timepicker floors sit on their inputs (6.5em and 5em); the others on the root (120px).
  const floors = await page.locator('.ing-floor > *').evaluateAll(els => els.map(el => {
    const input = el.querySelector<HTMLElement>('.z-daterangebox-input, .z-timepicker-input');
    const floored = input ?? el;
    const ems = parseFloat(getComputedStyle(floored).minWidth) / parseFloat(getComputedStyle(floored).fontSize);
    return `${el.className.split(' ')[0]} ${input ? `${ems}em` : getComputedStyle(el).minWidth}`;
  }));
  expect(floors).toEqual(['z-daterangebox 6.5em', 'z-timepicker 5em', 'z-selectbox 120px', 'z-slider 120px', 'z-multislider 120px']);
});
