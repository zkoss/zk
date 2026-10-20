import { test, expect, Locator, Page } from '@playwright/test';
import { expectSectionShots } from './section-shots';

// Absolute per-section pixel budget of the sectioned gallery shots; set from measured noise.
const SECTION_MAX_DIFF_PIXELS = 20;

type DynamicState = { name: string; action: (loc: Locator) => Promise<void> };

const hoverFocusStates: DynamicState[] = [
  { name: 'hover',  action: loc => loc.hover() },
  { name: 'focus',  action: loc => loc.focus() },
];

// Breathing room (px) added around a captured control so the hover border /
// focus ring isn't flush against the image edge and is easy to eyeball.
const PAD = 12;

// Capture `target` with a `PAD`-px margin on every side, instead of the element's
// exact bounding box. Many controls paint their hover/focus affordance (border
// colour, inset ring, state-layer glow) right at — or just outside — their edge,
// so an edge-tight element screenshot clips it. We screenshot the PAGE with a
// clip expanded around the element's box (clamped to the page top-left).
async function padShot(page: Page, target: Locator, name: string | string[]) {
  await target.scrollIntoViewIfNeeded();
  const box = await target.boundingBox();
  if (!box) throw new Error(`padShot: no bounding box for "${name}"`);
  const x = Math.max(0, box.x - PAD);
  const y = Math.max(0, box.y - PAD);
  await expect(page).toHaveScreenshot(name, {
    clip: { x, y, width: box.x + box.width + PAD - x, height: box.y + box.height + PAD - y },
    animations: 'disabled',
    // Absolute floor for anti-aliasing flicker. These crops are small (a selectbox
    // focus shot is 144x64) and high-contrast at the ring's edge, so a 1-2px AA
    // flicker that no eye can see turns the shot red run to run (chat D80; the
    // selectbox focus shot differed by 2px — doc/screenshot-tolerance-policy.md). An absolute
    // count, not maxDiffPixelRatio: 1% of ~9000px would be 92px — enough to hide a
    // whole mis-rendered ring segment. The gallery/tablet projects keep their own
    // ratio-based policy; this applies to state shots only.
    maxDiffPixels: 20,
  });
}

const buttonDynamicStates: DynamicState[] = [
  ...hoverFocusStates,
  { name: 'active', action: async loc => {
      await loc.hover();
      await loc.page().mouse.down();
    }
  },
];

// Every preview page is a single `.z-p-8` wrapper laying its demos out with
// generic utilities (the state matrix uses `.z-grid-cols-auto` + `.z-d-contents`
// rows; there are no `.pv-*` wrappers any more). So:
//  - "gallery" shot captures the whole `.z-p-8`.
//  - dynamic-state shots target a bare framework class (`.z-textbox`, `.z-row`,
//    `.z-tab`, `.z-listitem`, …) — the first instance on the page.

// -------------------------------------------------------
// Button
// -------------------------------------------------------
test.describe('button', () => {
  const DIR = 'button';
  test.beforeEach(async ({ page }) => {
    await page.goto('/button.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  const variants = [
    { label: 'default',  selector: '.z-button' },
    { label: 'outlined', selector: '.z-button-outlined' },
  ];

  for (const { label, selector } of variants) {
    for (const { name, action } of buttonDynamicStates) {
      test(`${label}-${name}`, async ({ page }) => {
        const el = page.locator(selector).first();
        await action(el);
        // padShot (element + PAD margin), not an edge-tight element shot: the
        // filled-button hover raises box-shadow: elevation-2, which paints
        // OUTSIDE the element box and an element-clipped capture would drop it.
        // Matches how every other stateful component captures its states.
        await padShot(page, el, `${DIR}-${label}-${name}.png`);
        if (name === 'active') await page.mouse.up();
      });
    }
  }

  // c15-c19: contained color-variant disabled buttons must use disabled-container bg
  // --zk-color-disabled-container = rgba(0,0,0,0.12), alpha ≈ 0.12 (variant colors are opaque)
  test('color-variant-disabled-state', async ({ page }) => {
    const containedVariants = ['secondary', 'success', 'warning', 'error', 'info'];
    for (const variant of containedVariants) {
      const alpha = await page.evaluate((cls) => {
        const el = document.querySelector(`.z-button-${cls}[disabled]`);
        if (!el) throw new Error(`No disabled .z-button-${cls} found`);
        const bg = getComputedStyle(el).backgroundColor;
        const m = bg.match(/rgba\(\s*[\d.]+\s*,\s*[\d.]+\s*,\s*[\d.]+\s*,\s*([\d.]+)\s*\)/);
        return m ? parseFloat(m[1]) : 1.0; // no alpha → opaque
      }, variant);
      expect(alpha, `${variant} disabled button bg should be semi-transparent (disabled-container), not opaque`).toBeLessThanOrEqual(0.2);
    }

    // c20-c25: outlined color-variant disabled buttons must use disabled text/border colors
    // --zk-color-disabled = rgba(0,0,0,0.38), alpha ≈ 0.38 (variant colors are opaque)
    const outlinedVariants = ['secondary', 'success', 'warning', 'error', 'info'];
    for (const variant of outlinedVariants) {
      const alpha = await page.evaluate((cls) => {
        const el = document.querySelector(`.z-button-outlined-${cls}[disabled]`);
        if (!el) throw new Error(`No disabled .z-button-outlined-${cls} found`);
        const color = getComputedStyle(el).color;
        const m = color.match(/rgba\(\s*[\d.]+\s*,\s*[\d.]+\s*,\s*[\d.]+\s*,\s*([\d.]+)\s*\)/);
        return m ? parseFloat(m[1]) : 1.0;
      }, variant);
      expect(alpha, `outlined-${variant} disabled button color should be semi-transparent (disabled), not opaque`).toBeLessThanOrEqual(0.5);
    }

    // c26-c28: text color-variant disabled buttons must use disabled text color
    const textVariants = ['secondary', 'error', 'info'];
    for (const variant of textVariants) {
      const alpha = await page.evaluate((cls) => {
        const el = document.querySelector(`.z-button-text-${cls}[disabled]`);
        if (!el) throw new Error(`No disabled .z-button-text-${cls} found`);
        const color = getComputedStyle(el).color;
        const m = color.match(/rgba\(\s*[\d.]+\s*,\s*[\d.]+\s*,\s*[\d.]+\s*,\s*([\d.]+)\s*\)/);
        return m ? parseFloat(m[1]) : 1.0;
      }, variant);
      expect(alpha, `text-${variant} disabled button color should be semi-transparent (disabled), not opaque`).toBeLessThanOrEqual(0.5);
    }
  });

  // c30: MD3 icon/image-to-label gap. ZK renders `<img class="z-button-image">`
  // (or `<i class="z-icon-*">`) + a plain SPACE text node + the label as adjacent
  // flex children of `.z-button`. With no container `gap` the space collapses and
  // the graphic glues to the label. Base `.z-button{gap:var(--zk-spacing-2)}` restores
  // the MD3 8dp gap, order-independently (also for dir="reverse"). The label is a bare
  // text node (no wrapper), so a Range gives its true glyph box.
  // See doc/skill-gaps.md 2026-07-02.
  test('graphic-and-label-have-md3-gap', async ({ page }) => {
    const gaps = await page.evaluate(() => {
      const out: { kind: string; gap: number }[] = [];
      for (const btn of [...document.querySelectorAll('.z-button')] as HTMLElement[]) {
        if (btn.querySelector('br')) continue; // vertical buttons stack (2px) — not this check
        const gfx = btn.querySelector('.z-button-image, [class*="z-icon-"]') as HTMLElement | null;
        if (!gfx) continue;
        const textNode = [...btn.childNodes].find(
          n => n.nodeType === Node.TEXT_NODE && (n.textContent || '').trim().length > 0);
        if (!textNode) continue; // icon-only / text-only — no pair to measure
        const range = document.createRange();
        range.selectNodeContents(textNode);
        const tr = range.getBoundingClientRect();
        const gr = gfx.getBoundingClientRect();
        // edge-to-edge horizontal distance, regardless of which side the graphic is on
        const gap = gr.left >= tr.right ? gr.left - tr.right   // dir="reverse": label, then graphic
                                        : tr.left - gr.right;  // normal: graphic, then label
        out.push({ kind: gfx.className, gap: Math.round(gap) });
      }
      return out;
    });
    expect(gaps.length, 'expected image/icon + label buttons on button.zul').toBeGreaterThan(0);
    for (const g of gaps) {
      // 0px = graphic glued to label (the bug); MD3 wants ~8dp.
      expect(g.gap, `graphic→label gap for "${g.kind}" is ${g.gap}px (want ~8)`).toBeGreaterThanOrEqual(6);
    }
  });
});

// -------------------------------------------------------
// Textbox
// -------------------------------------------------------
test.describe('textbox', () => {
  const DIR = 'textbox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/textbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      const el = page.locator('.z-textbox').first();
      await action(el);
      await padShot(page, el, `${DIR}-${name}.png`);
    });
  }
});

// -------------------------------------------------------
// Checkbox
// -------------------------------------------------------
test.describe('checkbox', () => {
  const DIR = 'checkbox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/checkbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      const el = page.locator('.z-checkbox').first();
      await action(el);
      await padShot(page, el, `${DIR}-${name}.png`);
    });
  }

  // mold="tristate" checked vs indeterminate must be visually distinct: checked =
  // checkmark on a filled box, indeterminate = dash on a filled box. ZK emits
  // mold-PREFIXED state classes (.z-checkbox-tristate-on / -indeterminate) that the
  // theme originally didn't style, so both rendered as an empty box. See
  // doc/skill-gaps.md 2026-07-14.
  test('tristate checked shows checkmark, indeterminate shows dash', async ({ page }) => {
    const r = await page.evaluate(() => {
      const read = (wrapperSel: string) => {
        const wrap = document.querySelector(wrapperSel) as HTMLElement | null;
        if (!wrap) return null;
        const mold = wrap.querySelector('.z-checkbox-mold') as HTMLElement;
        const cs = getComputedStyle(mold);
        const after = getComputedStyle(mold, '::after');
        return { bg: cs.backgroundColor, afterDisplay: after.display, afterImage: after.backgroundImage };
      };
      return { on: read('.z-checkbox-tristate-on'), ind: read('.z-checkbox-tristate-indeterminate') };
    });
    expect(r.on, 'tristate checked checkbox present on checkbox.zul').not.toBeNull();
    expect(r.ind, 'tristate indeterminate checkbox present on checkbox.zul').not.toBeNull();
    // Both states fill the box (primary) — a transparent box = the bug (unstyled).
    expect(r.on!.bg, 'tristate-on box must be filled').not.toBe('rgba(0, 0, 0, 0)');
    expect(r.ind!.bg, 'tristate-indeterminate box must be filled').not.toBe('rgba(0, 0, 0, 0)');
    // Both reveal their ::after glyph.
    expect(r.on!.afterDisplay).toBe('block');
    expect(r.ind!.afterDisplay).toBe('block');
    // And the two glyphs differ (checkmark vs dash) — this is what makes them distinguishable.
    expect(r.on!.afterImage, 'checkmark glyph present').not.toBe('none');
    expect(r.ind!.afterImage, 'dash glyph present').not.toBe('none');
    expect(r.on!.afterImage, 'checked and indeterminate glyphs must differ').not.toBe(r.ind!.afterImage);
  });
});

// -------------------------------------------------------
// Input focus — no layout shift (regression guard, doc/skill-gaps.md 2026-07-14)
// -------------------------------------------------------
// Focusing an input must NOT move its text and must NOT transition `border-width`.
// Transitioning border-width (1px→2px) without co-animating the compensating padding
// makes the content edge drift mid-animation = the ~1px jitter the user reported.
// The steady-state content edge is measured with transitions DISABLED (per the
// reference doc — otherwise we read mid-animation values, not the focus target).
test.describe('input focus (no layout shift)', () => {
  async function measure(page: Page, sel: string) {
    return await page.evaluate((sel) => {
      const el = document.querySelector(sel) as HTMLElement | null;
      if (!el) return null;
      const origTransition = getComputedStyle(el).transitionProperty;
      const edge = () => {
        const cs = getComputedStyle(el);
        return parseFloat(cs.borderLeftWidth) + parseFloat(cs.paddingLeft);
      };
      el.style.transition = 'none';      // measure settled geometry, not mid-transition
      void el.offsetWidth;
      const rest = edge();
      el.focus();
      void el.offsetWidth;
      const focused = edge();
      el.blur();
      return { origTransition, rest, focused };
    }, sel);
  }

  test('textbox family: no border-width transition, content edge stable', async ({ page }) => {
    await page.goto('/textbox.zul');
    await page.waitForLoadState('networkidle');
    const m = await measure(page, '.z-textbox');
    expect(m, '.z-textbox present').not.toBeNull();
    // The actual jitter cause: border-width is animated but its padding compensator is not.
    expect(m!.origTransition, 'textbox must not transition border-width').not.toContain('border-width');
    expect(m!.focused, 'settled content edge must equal rest').toBeCloseTo(m!.rest, 1);
  });

  test('combobox input: no border-width transition, content edge stable', async ({ page }) => {
    await page.goto('/combobox.zul');
    await page.waitForLoadState('networkidle');
    // Focusing the input triggers :focus-within on the wrapper → border-width:2px on the input.
    const m = await measure(page, '.z-combobox-input');
    expect(m, '.z-combobox-input present').not.toBeNull();
    expect(m!.origTransition, 'combobox input must not transition border-width').not.toContain('border-width');
    // Before the fix the input had border-width:2px on focus with NO padding comp → +1px shift.
    expect(m!.focused, 'settled content edge must equal rest').toBeCloseTo(m!.rest, 1);
  });

  // The timezone <select> lives inside the datebox popup (awkward to instantiate),
  // so assert against the compiled CSS text instead. ZK injects component CSS lazily
  // via the AU engine, so we fetch combo.css.dsp directly rather than scanning
  // document.styleSheets. A native <select> is intrinsically sized → must use
  // Mechanism A (border stays 1px, ring via inset box-shadow); a border-width
  // transition would both grow the box and shift its text.
  test('datebox timezone <select>: Mechanism A ring, no border-width transition', async ({ page }) => {
    await page.goto('/datebox.zul');
    await page.waitForLoadState('networkidle');
    const css = await page.evaluate(async () => {
      const link = [...document.querySelectorAll('link[rel="stylesheet"]')]
        .map(l => (l as HTMLLinkElement).href).find(h => h.includes('/zul/css/reset.css'));
      if (!link) return null;
      const prefix = link.slice(0, link.indexOf('/zul/css/reset.css'));
      const res = await fetch(prefix + '/js/zul/inp/css/combo.css.dsp');
      return res.ok ? await res.text() : null;
    });
    expect(css, 'combo.css.dsp fetched').not.toBeNull();
    // Strip block comments first: the dev preview serves UNMINIFIED CSS whose
    // explanatory comments mention "border-width" in prose ("Never transition
    // border-width"), which would false-match the substring guard below. CI serves
    // minified (comment-free) CSS; stripping here makes the test pass on both.
    const clean = css!.replace(/\/\*[\s\S]*?\*\//g, '');
    // Match the STANDALONE `.z-datebox-timezone > select` rule (not the popup-scoped
    // `.z-datebox-popup .z-datebox-timezone …` override): the negative lookbehind
    // rejects a selector where `.z-datebox-timezone` is preceded by a descendant
    // combinator (space) or another selector token. Works on minified OR pretty CSS.
    const base = clean.match(/(?<![\w.\-# ])\.z-datebox-timezone\s*>\s*select\s*\{([^}]*)\}/);
    expect(base, 'timezone <select> base rule found').not.toBeNull();
    expect(base![1], 'base rule must not transition border-width').not.toContain('border-width');
    const focus = clean.match(/(?<![\w.\-# ])\.z-datebox-timezone\s*>\s*select:focus[^{]*\{([^}]*)\}/);
    expect(focus, 'timezone <select> :focus rule found').not.toBeNull();
    expect(focus![1], 'focus must draw an inset box-shadow ring (Mechanism A)').toContain('inset');
  });

  // The main-wrapper OPEN state (icon click). ZK adds `z-datebox-open` to the
  // wrapper <span> when the calendar icon is clicked. That emphasis state must reuse
  // the same affordance as :focus-within (1px border + inset ring) — NOT
  // border-width:2px. A 2px border on the min-height-pinned composite grows the whole
  // field (~1px/side, measured 40→42px) and reads thicker than the input-click focus,
  // so the two focus styles diverge. See doc/skill-gaps.md 2026-07-14.
  test('datebox open state (icon click): no border-width growth, no layout shift', async ({ page }) => {
    await page.goto('/datebox.zul');
    await page.waitForLoadState('networkidle');
    // Kill transitions on the wrapper so we read settled geometry, not mid-animation.
    await page.addStyleTag({ content: '.z-datebox { transition: none !important; }' });
    const box = page.locator('.z-datebox').first();
    const btn = page.locator('.z-datebox-button').first();
    const rest = await box.evaluate((el) => {
      const r = el.getBoundingClientRect();
      return { border: getComputedStyle(el).borderTopWidth, w: r.width, h: r.height };
    });
    await btn.click();
    await page.waitForSelector('.z-datebox.z-datebox-open', { timeout: 5000 });
    const open = await box.evaluate((el) => {
      const r = el.getBoundingClientRect();
      return { border: getComputedStyle(el).borderTopWidth, w: r.width, h: r.height };
    });
    // RED before fix: border grows 1px→2px and the field grows ~2px in height.
    expect(open.border, 'open-state border must stay 1px (not 2px)').toBe(rest.border);
    expect(Math.abs(open.h - rest.h), 'open-state height must not grow').toBeLessThan(0.6);
    expect(Math.abs(open.w - rest.w), 'open-state width must not grow').toBeLessThan(0.6);
  });

  // Deterministic compiled-CSS guard for the open-state rules (robust against
  // popup-open flakiness). datebox & bandbox are both bundled into combo.css.dsp.
  // The icon-open emphasis must reuse the :focus-within mechanism: datebox must NOT
  // bump border-width, and both must draw an INSET ring (not datebox's old 2px border
  // nor bandbox's old outset ring).
  test('datebox & bandbox open-state: inset ring, no border-width bump', async ({ page }) => {
    await page.goto('/datebox.zul');
    await page.waitForLoadState('networkidle');
    const css = await page.evaluate(async () => {
      const link = [...document.querySelectorAll('link[rel="stylesheet"]')]
        .map(l => (l as HTMLLinkElement).href).find(h => h.includes('/zul/css/reset.css'));
      if (!link) return null;
      const prefix = link.slice(0, link.indexOf('/zul/css/reset.css'));
      const res = await fetch(prefix + '/js/zul/inp/css/combo.css.dsp');
      return res.ok ? await res.text() : null;
    });
    expect(css, 'combo.css.dsp fetched').not.toBeNull();
    const db = css!.match(/\.z-datebox\.z-datebox-open\s*\{([^}]*)\}/);
    expect(db, 'datebox open rule found').not.toBeNull();
    expect(db![1], 'datebox open must NOT bump border-width').not.toContain('border-width');
    expect(db![1], 'datebox open must draw an inset ring (Mechanism A)').toContain('inset');
    const bb = css!.match(/\.z-bandbox\.z-bandbox-open\s*\{([^}]*)\}/);
    expect(bb, 'bandbox open rule found').not.toBeNull();
    expect(bb![1], 'bandbox open must draw an inset ring (not an outset ring)').toContain('inset');
  });
});

// -------------------------------------------------------
// Combobox
// -------------------------------------------------------
test.describe('combobox', () => {
  const DIR = 'combobox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/combobox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      // Action on the inner input; capture the whole .z-combobox so the shot
      // includes the dropdown button — both input and button take the focus/hover
      // border. (padShot adds breathing room around the ring.)
      await action(page.locator('.z-combobox-input').first());
      await padShot(page, page.locator('.z-combobox').first(), `${DIR}-${name}.png`);
    });
  }

  // Open dropdown with one keyboard-selected item. Batch 2 moved the selected item to
  // --zk-color-secondary-container; the closed-state shots above never reach it.
  test('dropdown', async ({ page }) => {
    await page.locator('.z-combobox-button').first().click();
    await page.keyboard.press('ArrowDown');
    await page.keyboard.press('ArrowDown');
    await page.mouse.move(0, 0); // keep the pointer off the popup so :hover cannot tint an item
    const popup = page.locator('.z-combobox-popup.z-combobox-open');
    await expect(popup).toBeVisible();
    await expect(popup.locator('.z-comboitem-selected')).toHaveCount(1);
    await padShot(page, popup, `${DIR}-dropdown.png`);
  });

  // Comboitem with iconSclass: the leading icon must be separated from the
  // label, not glued to it. ZK renders `.z-comboitem-icon` + `.z-comboitem-text`
  // as adjacent flex children with no built-in gap. See doc/skill-gaps.md 2026-06-15.
  test('comboitem icon is separated from its label', async ({ page }) => {
    const gap = await page.evaluate(() => {
      const cbs = [...document.querySelectorAll('.z-combobox')];
      let item = null;
      for (const el of cbs) {
        const w = (window as any).zk.Widget.$(el);
        for (let c = w.firstChild; c; c = c.nextSibling) {
          if (c._iconSclass) { item = c; break; }
        }
        if (item) { w.open(); break; }
      }
      if (!item) return -1;
      const li = item.$n();
      const iconGlyph = li.querySelector('.z-comboitem-icon > *') || li.querySelector('.z-comboitem-icon');
      const text = li.querySelector('.z-comboitem-text');
      return text.getBoundingClientRect().left - iconGlyph.getBoundingClientRect().right;
    });
    // 0px = glyph touching the label (the bug); expect a real MD-scale gap.
    expect(gap).toBeGreaterThanOrEqual(8);
  });
});

// -------------------------------------------------------
// Listbox
// -------------------------------------------------------
// The preview page lays its variants (default, checkmark, no-border…) out with
// generic utilities — there is no longer a `.pv-variant-*` wrapper to scope to.
// Gallery shoots the whole `.z-p-8` page (every variant in one shot, like the
// other gallery pages); hover targets the first bare `.z-listitem`.
test.describe('listbox', () => {
  const DIR = 'listbox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/listbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  test('hover', async ({ page }) => {
    const el = page.locator('.z-listitem').first();
    await el.hover();
    await expect(el).toHaveScreenshot(`${DIR}-hover.png`);
  });
});

// -------------------------------------------------------
// Grid
// -------------------------------------------------------
test.describe('grid', () => {
  const DIR = 'grid';
  test.beforeEach(async ({ page }) => {
    await page.goto('/grid.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  test('hover', async ({ page }) => {
    const el = page.locator('.z-row').first();
    await el.hover();
    await expect(el).toHaveScreenshot(`${DIR}-hover.png`);
  });
});

// -------------------------------------------------------
// Datebox
// -------------------------------------------------------
test.describe('datebox', () => {
  const DIR = 'datebox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/datebox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      // Action on the inner input, capture the wrapper — the border/ring lives
      // on .z-datebox, not the transparent .z-datebox-input. (See bandbox note.)
      await action(page.locator('.z-datebox-input').first());
      await padShot(page, page.locator('.z-datebox').first(), `${DIR}-${name}.png`);
    });
  }

  // ZK-6112 (Contract dw1/dw2/M1): the input width follows the cols API. ZK renders
  // cols as the input's `size` (Datebox default 11); `field-sizing: content` made the
  // browser ignore it, so cols had no effect. A larger cols must render wider and a
  // smaller cols narrower, and field-sizing must stay at its default (`fixed`).
  test('input width follows cols (no field-sizing: content)', async ({ page }) => {
    const info = await page.evaluate(() => {
      const input = ([...document.querySelectorAll('.z-datebox-input')] as HTMLInputElement[])
        .find(i => (i as HTMLElement).offsetParent !== null)!;
      const wgt = (window as any).zk.Widget.$(input);
      const widthAt = (cols: number) => {
        wgt.setCols(cols);
        return Math.round(input.getBoundingClientRect().width);
      };
      return {
        w8: widthAt(8), w30: widthAt(30),
        fieldSizing: getComputedStyle(input).getPropertyValue('field-sizing'),
      };
    });
    expect(info.fieldSizing, 'field-sizing: content must not be applied').not.toBe('content');
    expect(info.w30, `cols=30 (${info.w30}px) must be wider than cols=8 (${info.w8}px)`).toBeGreaterThan(info.w8);
  });
});

// -------------------------------------------------------
// DateRangeBox
// -------------------------------------------------------
test.describe('daterangebox', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/daterangebox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  // gap 2026-07-20 (Contract M11 / c6a-c6b): the two inputs ship with no
  // size/width (ZK Daterangebox.ts redraw() writes only class/aria/placeholder/
  // autocomplete). Left unbounded each renders at the UA-default size=20 (~20ch,
  // ~172px measured) and, with text-align:center, a short date sits centered with
  // large symmetric gutters — making the field ~2x datebox width. field-sizing:
  // content (daterangebox.css .z-daterangebox-input) must make each input hug its
  // date content instead. RED before that fix (172px), GREEN after (~100px).
  test('input hugs its date content (not the UA ~20ch default)', async ({ page }) => {
    const begin = page.locator('.z-daterangebox-input.z-daterangebox-begin').first();
    await expect(begin).toBeVisible();
    // Sanity: the preview's first daterangebox is SEEDED, so we measure the
    // content-fit case (a rendered date), not an empty box.
    const value = await begin.inputValue();
    expect(value.length, 'preview first daterangebox must be seeded').toBeGreaterThan(0);
    const box = await begin.boundingBox();
    const width = box!.width;
    expect(width, `seeded input must hug its date content (< 130px), was ${width}px`).toBeLessThan(130);
  });

  // gap 2026-07-20 (invalid-state coverage): daterangebox has a real invalid state
  // (`z-daterangebox-invalid` on the ROOT, since it's a XulElement composite, not an
  // InputElement). ZK-6133 (https://zkoss.atlassian.net/browse/ZK-6133) drops the user
  // `sclass`, so it CANNOT be forced with `sclass="z-daterangebox-invalid"` the way other
  // inputs are. The preview (pv/daterangebox-content.zul) forces the GENUINE state on load
  // via the framework's own AuInvoke — Clients.response(new AuInvoke(self, "_setInvalid",
  // true)) — the same call Daterangebox.java fires on a validation failure. This guards
  // that ZK-6133 workaround: the two Invalid-column cells must carry the class AND paint
  // the error border (--zk-color-error), not the default outline.
  test('invalid state paints the error border (ZK-6133 workaround)', async ({ page }) => {
    const invalid = page.locator('.z-daterangebox.z-daterangebox-invalid');
    // Deterministic wait: the class arrives via an AuInvoke applied after mount, so
    // toHaveCount auto-retries until it lands (no fixed timeout). The preview forces
    // exactly the two Invalid-column cells.
    await expect(invalid).toHaveCount(2);

    // Resolve --zk-color-error at runtime instead of hardcoding rgb(211,47,47), so the
    // guard survives a token-value change and still asserts "invalid uses the error color".
    const errorColor = await page.evaluate(() => {
      const probe = document.createElement('span');
      probe.style.color = 'var(--zk-color-error)';
      document.body.appendChild(probe);
      const c = getComputedStyle(probe).color;
      probe.remove();
      return c;
    });

    const borderColor = await invalid.first().evaluate(el => getComputedStyle(el).borderTopColor);
    expect(borderColor, `invalid border must be --zk-color-error (${errorColor}), was ${borderColor}`).toBe(errorColor);
  });
});

// -------------------------------------------------------
// Timebox
// -------------------------------------------------------
test.describe('timebox', () => {
  const DIR = 'timebox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/timebox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      // Action on the inner input, capture the wrapper — the border/ring lives
      // on .z-timebox, not the transparent .z-timebox-input. (See bandbox note.)
      await action(page.locator('.z-timebox-input').first());
      await padShot(page, page.locator('.z-timebox').first(), `${DIR}-${name}.png`);
    });
  }

  // ZK-6112 (Contract tw1/tw2/M1): same as datebox — the width follows cols
  // (rendered as `size`; Timebox derives its default from the formatted value).
  test('input width follows cols (no field-sizing: content)', async ({ page }) => {
    const info = await page.evaluate(() => {
      const input = ([...document.querySelectorAll('.z-timebox-input')] as HTMLInputElement[])
        .find(i => (i as HTMLElement).offsetParent !== null)!;
      const wgt = (window as any).zk.Widget.$(input);
      const widthAt = (cols: number) => {
        wgt.setCols(cols);
        return Math.round(input.getBoundingClientRect().width);
      };
      return {
        w8: widthAt(8), w30: widthAt(30),
        fieldSizing: getComputedStyle(input).getPropertyValue('field-sizing'),
      };
    });
    expect(info.fieldSizing, 'field-sizing: content must not be applied').not.toBe('content');
    expect(info.w30, `cols=30 (${info.w30}px) must be wider than cols=8 (${info.w8}px)`).toBeGreaterThan(info.w8);
  });
});

// -------------------------------------------------------
// TimePicker
// -------------------------------------------------------
test.describe('timepicker', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/timepicker.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  // gap 2026-07-20 (similar-case sweep of the datebox/timebox content-fit fix):
  // timepicker is a fixed-format, readonly time field but ZK hardcodes the input at
  // size="5" (fits HH:mm only). Under the old `flex:1;min-width:0` with no field-sizing,
  // an HH:mm:ss value ("10:30:00") CLIPS (scrollWidth > clientWidth) whenever the root
  // isn't given an explicit width. pv/timepicker-content.zul renders its timepickers at
  // content width (no forced width="160px"), so every seeded HH:mm:ss cell is a live clip
  // target. Fix mirrors timebox: field-sizing:content lets the input hug/grow to its time.
  // Readonly (value picked from popup, not typed) → no per-keystroke jitter. RED before
  // the fix (seeded cells clipped), GREEN after. Order-independent: no seeded input may
  // clip, and all must report field-sizing:content.
  test('input hugs its time content (field-sizing, no clip)', async ({ page }) => {
    const info = await page.evaluate(() => {
      const seeded = ([...document.querySelectorAll('.z-timepicker-input')] as HTMLInputElement[])
        .filter(i => i.value && (i as HTMLElement).offsetParent !== null);
      return {
        count: seeded.length,
        allContentSized: seeded.every(i => getComputedStyle(i).getPropertyValue('field-sizing') === 'content'),
        clippedValues: seeded.filter(i => i.scrollWidth > i.clientWidth + 1).map(i => i.value),
      };
    });
    expect(info.count, 'at least one seeded timepicker must be present').toBeGreaterThan(0);
    expect(info.allContentSized, 'field-sizing: content must be applied to timepicker inputs').toBe(true);
    expect(info.clippedValues, `timepicker times must not be clipped, clipped: ${JSON.stringify(info.clippedValues)}`).toEqual([]);
  });
});

// -------------------------------------------------------
// Spinner
// -------------------------------------------------------
test.describe('spinner', () => {
  const DIR = 'spinner';
  test.beforeEach(async ({ page }) => {
    await page.goto('/spinner.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      // Action on the inner input, capture the wrapper — the border/ring lives
      // on .z-spinner, not the transparent .z-spinner-input. (See bandbox note.)
      await action(page.locator('.z-spinner-input').first());
      await padShot(page, page.locator('.z-spinner').first(), `${DIR}-${name}.png`);
    });
  }
});

// -------------------------------------------------------
// Bandbox
// -------------------------------------------------------
test.describe('bandbox', () => {
  const DIR = 'bandbox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/bandbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      // Focus/hover the inner <input> (the focusable node), but capture the
      // bordered WRAPPER: the hover border-color and the :focus-within ring are
      // painted on .z-bandbox, while .z-bandbox-input is transparent/borderless.
      // Capturing the input would clip away the very effect under test.
      await action(page.locator('.z-bandbox-input').first());
      await padShot(page, page.locator('.z-bandbox').first(), `${DIR}-${name}.png`);
    });
  }
});

// -------------------------------------------------------
// Selectbox
// -------------------------------------------------------
test.describe('selectbox', () => {
  const DIR = 'selectbox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/selectbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      const el = page.locator('.z-selectbox').first();
      await action(el);
      await padShot(page, el, `${DIR}-${name}.png`);
    });
  }
});

// -------------------------------------------------------
// Tabbox
// -------------------------------------------------------
test.describe('tabbox', () => {
  const DIR = 'tabbox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/tabbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  test('hover', async ({ page }) => {
    const el = page.locator('.z-tab').first();
    await el.hover();
    await expect(el).toHaveScreenshot(`${DIR}-hover.png`);
  });
});

// -------------------------------------------------------
// Tree
// -------------------------------------------------------
test.describe('tree', () => {
  const DIR = 'tree';
  test.beforeEach(async ({ page }) => {
    await page.goto('/tree.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  test('hover', async ({ page }) => {
    const el = page.locator('.z-treerow').first();
    await el.hover();
    await expect(el).toHaveScreenshot(`${DIR}-hover.png`);
  });
});

// -------------------------------------------------------
// Window
// -------------------------------------------------------
test.describe('window', () => {
  const DIR = 'window';
  test.beforeEach(async ({ page }) => {
    await page.goto('/window.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
    // Overlapped windows positioned against the viewport (`right, bottom`) land below every section
    // once the viewport is as tall as the page (section-shots.ts), so shoot each one on its own.
    const overlapped = page.locator('.z-window-overlapped');
    const count = await overlapped.count();
    expect(count, 'window.zul has no overlapped window').toBeGreaterThan(0);
    for (let i = 0; i < count; i++) {
      await expect(overlapped.nth(i)).toHaveScreenshot(`${DIR}-overlapped-${String(i).padStart(2, '0')}.png`, {
        maxDiffPixels: SECTION_MAX_DIFF_PIXELS,
      });
    }
  });
});

// -------------------------------------------------------
// Panel
// -------------------------------------------------------
test.describe('panel', () => {
  const DIR = 'panel';
  test.beforeEach(async ({ page }) => {
    await page.goto('/panel.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });
});

// -------------------------------------------------------
// Navbar — selected item is a rounded tonal container, NO left accent
// -------------------------------------------------------
// The selected navitem's active marker is the rounded tonal CONTAINER alone
// (12% primary tint fill + primary text + weight 600) — MD3 Navigation Drawer /
// MUI ListItemButton. There is NO left-edge accent: an earlier iteration added a
// left bar (first a radius-clipped `border-left` arc, then a straight `::after`
// strip), but stacking a classic-sidebar bar on the MD3 pill is redundant and
// clashes at the corners, so the bar was dropped (design review 2026-07-07). This
// guards against either accent re-appearing. NB: the `.z-listitem` "blue left
// line" is a *focus* indicator (`box-shadow: inset 3px 0 0`), a different
// component/state — not this. Gallery breadth is owned by gallery-scan.spec.ts
// (the static gallery has no selected item). See doc/skill-gaps.md 2026-07-07 and
// doc/contracts/navbar.md c7.
test.describe('navbar', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/navbar.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('selected item is a rounded tonal container with no left accent', async ({ page }) => {
    // Select the first item in the expanded vertical navbar via a real click.
    await page.locator('.z-navbar-vertical .z-navitem-content').first().click();
    const selected = page.locator('.z-navbar-vertical .z-navitem-selected > .z-navitem-content').first();
    await selected.waitFor({ state: 'visible' });

    const m = await selected.evaluate((el) => {
      // Handles rgb/rgba AND the slash-alpha form Chrome uses for color-mix()
      // results (e.g. `oklab(L a b / 0.12)`, `color(srgb r g b / 0.12)`).
      const alphaOf = (s: string) => {
        if (!s || s === 'transparent') return 0;
        const slash = s.match(/\/\s*([\d.]+)\s*\)/);
        if (slash) return parseFloat(slash[1]);
        const mm = s.match(/rgba?\(([^)]+)\)/);
        if (!mm) return 1; // opaque named/hex-resolved colour
        const p = mm[1].split(',').map((x) => x.trim());
        return p.length === 4 ? parseFloat(p[3]) : 1;
      };
      const cs = getComputedStyle(el);
      const after = getComputedStyle(el, '::after');
      return {
        borderTopLeftRadius: parseFloat(cs.borderTopLeftRadius) || 0,
        bgAlpha: alphaOf(cs.backgroundColor),
        borderLeftWidth: parseFloat(cs.borderLeftWidth) || 0,
        borderLeftAlpha: alphaOf(cs.borderLeftColor),
        afterContent: after.content,
        afterWidth: parseFloat(after.width) || 0,
      };
    });

    // Active marker = the rounded tonal container: a rounded corner + a visible
    // tint fill. Both must be present.
    expect(m.borderTopLeftRadius, 'selected item must be a rounded container').toBeGreaterThan(0);
    expect(m.bgAlpha, 'selected item must carry the tonal tint fill').toBeGreaterThan(0);
    // NO left accent of any kind. (a) not a coloured border-left…
    const borderAccent = m.borderLeftWidth > 0 && m.borderLeftAlpha > 0.1;
    expect(
      borderAccent,
      `selected item must have no border-left accent: width=${m.borderLeftWidth}px alpha=${m.borderLeftAlpha}`,
    ).toBe(false);
    // …(b) nor an ::after bar strip.
    const afterAccent = m.afterContent !== 'none' && m.afterWidth >= 2;
    expect(
      afterAccent,
      `selected item must have no ::after accent bar: content=${m.afterContent} width=${m.afterWidth}px`,
    ).toBe(false);
  });

  // Keyboard focus on a nav/navitem link must be the THEME ring drawn INSIDE the
  // item's box. Two failure modes this guards, both observed 2026-09-04:
  //   1. No `:focus-visible` rule at all → the browser's own ring (Chrome:
  //      `outline-style: auto`, 1px, rgb(0,95,204), offset +1px) stands in for it,
  //      so the navbar's focus affordance is off-palette and off-spec.
  //   2. An OUTSET ring (positive offset) on a full-bleed item is clipped: the
  //      submenu cave `.z-nav > ul` is exactly as wide as the item it holds and
  //      carries `overflow: hidden` — and jQuery's slideUp/slideDown (Nav.ts)
  //      re-applies `overflow: hidden` inline while the group animates, so no
  //      theme CSS can opt out of the clip. Hence `outline-offset <= 0`.
  // Repro is the designer's: expand "Get Started" → click "Step One" → press Space
  // (the mouse click focuses the <a>; the keypress promotes it to :focus-visible).
  // See doc/skill-gaps.md 2026-09-04 and doc/contracts/navbar.md c8–c10.
  test('submenu item keyboard focus ring is the theme inset ring and is not clipped', async ({ page }) => {
    const navbar = page.locator('.z-navbar-vertical').first();
    await navbar
      .locator('.z-nav > .z-nav-content')
      .filter({ hasText: 'Get Started' })
      .first()
      .click();

    const stepOne = navbar.locator('.z-navitem-content').filter({ hasText: 'Step One' }).first();
    await stepOne.waitFor({ state: 'visible' });
    await stepOne.click();
    await page.keyboard.press(' ');

    const m = await page.evaluate(() => {
      const el = document.activeElement as HTMLElement | null;
      if (!el) return null;
      const cs = getComputedStyle(el);
      const width = parseFloat(cs.outlineWidth) || 0;
      const offset = parseFloat(cs.outlineOffset) || 0;
      const r = el.getBoundingClientRect();
      // Outer edge of the painted ring. A negative offset pulls it inward.
      const ring = {
        top: r.top - offset - width,
        left: r.left - offset - width,
        right: r.right + offset + width,
        bottom: r.bottom + offset + width,
      };
      // Nearest ancestor whose overflow actually cuts that ring.
      let clipper: { cls: string; overflow: string; short: number } | null = null;
      for (let p = el.parentElement; p && p !== document.documentElement; p = p.parentElement) {
        const pcs = getComputedStyle(p);
        if (pcs.overflowX === 'visible' && pcs.overflowY === 'visible') continue;
        const pr = p.getBoundingClientRect();
        const short = Math.max(
          pr.left - ring.left,
          ring.right - pr.right,
          pr.top - ring.top,
          ring.bottom - pr.bottom,
        );
        if (short > 0.5) {
          // ZK's submenu cave <ul> carries no class, so name it by tag +
          // owning .z-nav to keep the failure message actionable.
          const own = typeof p.className === 'string' ? p.className : '';
          const host = p.parentElement && typeof p.parentElement.className === 'string'
            ? p.parentElement.className
            : '';
          clipper = {
            cls: own ? `${p.tagName}.${own}` : `${p.tagName} (unclassed, inside .${host})`,
            overflow: `${pcs.overflowX}/${pcs.overflowY}`,
            short: Math.round(short * 100) / 100,
          };
          break;
        }
      }
      return {
        cls: el.className,
        focusVisible: el.matches(':focus-visible'),
        outlineStyle: cs.outlineStyle,
        width,
        offset,
        color: cs.outlineColor,
        clipper,
      };
    });

    expect(m, 'a navitem link must hold focus after the click').not.toBeNull();
    expect(m!.cls, 'focus must land on the navitem link').toContain('z-navitem-content');
    expect(m!.focusVisible, 'Space after the click must promote focus to :focus-visible').toBe(true);
    // The theme ring, not the browser's `outline-style: auto` default.
    expect.soft(m!.outlineStyle, `expected the theme ring, got outline-style: ${m!.outlineStyle}`).toBe('solid');
    expect.soft(m!.width, 'ring is 2px (--zk-focus-ring)').toBe(2);
    expect.soft(m!.color, 'ring is --zk-color-primary').toBe('rgb(55, 111, 208)');
    // Inset, so the cave's overflow:hidden can never cut it.
    expect.soft(m!.offset, `ring must be inset; outline-offset was ${m!.offset}px`).toBeLessThanOrEqual(0);
    expect(
      m!.clipper,
      `focus ring is clipped by ${m!.clipper?.cls} (overflow ${m!.clipper?.overflow}) — ` +
        `ring overshoots its clip rect by ${m!.clipper?.short}px`,
    ).toBeNull();
  });
});

// -------------------------------------------------------
// Coachmark — neutral surface (regression guard, doc/skill-gaps.md 2026-07-15)
// -------------------------------------------------------
// The coachmark pop-up is an MD3 rich tooltip: a neutral surface-container card
// so nested action widgets render with their STANDARD filled style (no per-widget
// colour inversion). A primary-filled card forces every nested filled control to be
// hand-inverted, which does not scale. This guard asserts (a) the card is a light
// neutral surface — NOT the primary brand fill — and (b) a nested .z-button keeps the
// standard primary fill rather than the old white inverse.
test.describe('coachmark', () => {
  const PRIMARY = 'rgb(55, 111, 208)';
  test.beforeEach(async ({ page }) => {
    await page.goto('/coachmark.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    // The first coachmark is visible by default → it opens on load.
    await page.locator('.z-coachmark.z-coachmark-open .z-coachmark-content').first().waitFor({ state: 'visible' });
  });

  test('card is a neutral surface and nested button keeps the standard fill', async ({ page }) => {
    const r = await page.evaluate(() => {
      const content = document.querySelector('.z-coachmark.z-coachmark-open .z-coachmark-content') as HTMLElement | null;
      if (!content) return null;
      const btn = content.querySelector('.z-button') as HTMLElement | null;
      const rgb = (s: string) => (s.match(/\d+/g) || []).slice(0, 3).map(Number);
      return {
        cardBg: getComputedStyle(content).backgroundColor,
        cardRgb: rgb(getComputedStyle(content).backgroundColor),
        btnBg: btn ? getComputedStyle(btn).backgroundColor : null,
      };
    });
    expect(r, 'open coachmark card present on coachmark.zul').not.toBeNull();
    // (a) The card must NOT be the primary brand fill…
    expect(r!.cardBg, 'coachmark card must not be the primary brand fill').not.toBe(PRIMARY);
    // …and must be a light neutral surface (all channels high).
    expect(
      r!.cardRgb.every((c) => c >= 200),
      `coachmark card must be a light neutral surface, got ${r!.cardBg}`,
    ).toBe(true);
    // (b) A nested filled button keeps its STANDARD primary fill (no white inversion).
    expect(r!.btnBg, 'nested button present in coachmark').not.toBeNull();
    expect(r!.btnBg, 'nested button must keep the standard primary fill, not the white inverse').toBe(PRIMARY);
  });

  // Designer review (doc/skill-gaps.md 2026-07-15): the top-right close must be a real
  // icon-button affordance (circular hit target + hover state layer), pinned at a SYMMETRIC
  // corner inset. The old build shipped a bare 16px glyph at an asymmetric inset (4px top /
  // 8px right) because the pointer-side padding bump (+16px) was 4px short of the 20px ZK
  // actually injects. This guard fails on that build and passes on the fixed one.
  test('close button is a circular icon-button target with symmetric corner inset', async ({ page }) => {
    const r = await page.evaluate(() => {
      const root = document.querySelector('.z-coachmark.z-coachmark-open') as HTMLElement | null;
      if (!root) return null;
      const content = root.querySelector('.z-coachmark-content') as HTMLElement;
      const close = root.querySelector('.z-coachmark-close') as HTMLElement | null;
      if (!close) return null;
      const cs = getComputedStyle(close);
      const cr = close.getBoundingClientRect();
      const cc = content.getBoundingClientRect();
      const num = (s: string) => parseFloat(s) || 0;
      return {
        w: cr.width, h: cr.height,
        radius: num(cs.borderRadius),
        topInset: cr.top - cc.top,          // px inside the content box, top edge
        rightInset: cc.right - cr.right,    // px inside the content box, right edge
      };
    });
    expect(r, 'open coachmark with close button present').not.toBeNull();
    // (a) a real hit target, not a bare glyph
    expect(r!.w, `close width must be a real target (got ${r!.w})`).toBeGreaterThanOrEqual(24);
    expect(r!.h, `close height must be a real target (got ${r!.h})`).toBeGreaterThanOrEqual(24);
    // (b) circular state layer (border-radius ≥ half the box)
    expect(r!.radius, `close must be circular (radius ${r!.radius} vs w ${r!.w})`).toBeGreaterThanOrEqual(r!.w / 2);
    // (c) symmetric corner inset — top inset must equal right inset (both ≈ 8px)
    expect(
      Math.abs(r!.topInset - r!.rightInset),
      `close corner inset must be symmetric (top ${r!.topInset} vs right ${r!.rightInset})`,
    ).toBeLessThanOrEqual(1.5);
  });

  // The close must give hover feedback (a state layer), like every other icon-button in the theme.
  // The layer is a ::before overlay that fades in on hover (MD3 canonical state layer).
  test('close button shows a hover state layer', async ({ page }) => {
    const close = page.locator('.z-coachmark.z-coachmark-open .z-coachmark-close').first();
    const before = await close.evaluate((el) => Number(getComputedStyle(el, '::before').opacity));
    expect(before, 'state layer is invisible at rest').toBe(0);
    await close.hover();
    // the ::before overlay fades in over ~250ms — poll until it has ramped up
    await expect
      .poll(() => close.evaluate((el) => Number(getComputedStyle(el, '::before').opacity)),
        { message: 'state layer must fade in on hover' })
      .toBeGreaterThan(0);
  });
});

// -------------------------------------------------------
// Tablet-isolation guard
// The tablet bundle must stay tablet-only: on a desktop UA, ZK must NOT inject
// zkmax/css/tablet.css, so touch overrides can never leak into desktop.
// -------------------------------------------------------
test.describe('tablet-isolation', () => {
  test('tablet.css is absent on a desktop UA', async ({ page }) => {
    await page.goto('/button.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    const hrefs = await page.evaluate(() =>
      [...document.styleSheets].map(s => s.href).filter(Boolean) as string[]);
    expect(hrefs.some(h => h.includes('zkmax/css/tablet.css'))).toBe(false);
  });
});

// -------------------------------------------------------
// Viewport-fill guard — the `100vh` footgun
// The base reset must use `body { height: 100% }`, NOT `100vh`. `100vh` is
// blind to scrollbars: when any wide content triggers a horizontal scrollbar,
// a `100vh` body exceeds the now-shorter visible area and spawns a SPURIOUS
// vertical scrollbar on a page whose content fits. We force the horizontal
// scrollbar with an injected off-document-flow wide spacer (the preview pages
// are now responsive and no longer overflow), so this guards the reset rule
// itself rather than any page's layout.
// See `.claude/skills/zk-component-rules/reference/viewport-height-fill.md`.
// -------------------------------------------------------
test.describe('viewport-fill', () => {
  test('horizontal overflow must not spawn a spurious vertical scrollbar', async ({ page }) => {
    await page.setViewportSize({ width: 414, height: 835 });
    await page.goto('/colorbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    const m = await page.evaluate(() => {
      // Force a horizontal scrollbar without adding vertical content height:
      // an absolutely-positioned 1px-tall spacer wider than the viewport.
      const spacer = document.createElement('div');
      spacer.style.cssText = 'position:absolute;top:0;left:0;width:2000px;height:1px;pointer-events:none;';
      document.body.appendChild(spacer);

      const d = document.documentElement;
      const pageEl = document.querySelector('.z-page') as HTMLElement;
      return {
        // .z-page itself is viewport-tall (inline height:100% against body's height:100%),
        // so measure the real content through its children.
        contentBottom: Math.round(Math.max(...[...pageEl.children].map(c => c.getBoundingClientRect().bottom))),
        innerH: window.innerHeight,
        hScroll: d.scrollWidth > d.clientWidth,
        vScroll: d.scrollHeight > d.clientHeight,
      };
    });
    // Precondition: the page content genuinely fits within the viewport...
    expect(m.contentBottom).toBeLessThan(m.innerH);
    // ...and the fixed-width preview matrix does overflow horizontally — the trigger.
    expect(m.hScroll).toBe(true);
    // The guard: despite the horizontal scrollbar, no vertical scrollbar appears.
    expect(m.vScroll).toBe(false);
  });

  // A floating popup must dismiss on an outside click ANYWHERE in the viewport,
  // including the empty area below the page content. ZK core only fires the
  // float-up / auto-close when the click's clientY <= document.body.clientHeight
  // (mount.ts `_docMouseDown`, a "not a scrollbar click" guard). So `body` MUST
  // fill the viewport height; if it collapses to content height, clicks below
  // the content are silently swallowed and open popups never close. This guards
  // the regression where `body { height: 100% }` failed to resolve (its
  // containing block <html> had no definite height) and body shrank to content.
  test('colorbox popup dismisses on a click in the empty area below content', async ({ page }) => {
    await page.setViewportSize({ width: 1280, height: 900 });
    await page.goto('/colorbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));

    const geo = await page.evaluate(() => {
      const cb = document.querySelector('.z-colorbox') as HTMLElement;
      const w = (window as any).zk.Widget.$(cb);
      w.openPopup();
      const pp = w.$n('pp') as HTMLElement;
      const r = pp.getBoundingClientRect();
      return {
        open: getComputedStyle(pp).display !== 'none',
        popupBottom: Math.round(r.bottom),
        bodyClientHeight: document.body.clientHeight,
        innerH: window.innerHeight,
      };
    });
    // Precondition: the popup is open, and there is empty viewport below it.
    expect(geo.open).toBe(true);
    const clickY = geo.popupBottom + 120;
    expect(clickY).toBeLessThan(geo.innerH);

    // Click in the empty area below the content, well clear of the popup (x=1100).
    await page.mouse.click(1100, clickY);
    await page.waitForTimeout(300); // ZK's float-up filter is ~120ms

    const closed = await page.evaluate(() => {
      const cb = document.querySelector('.z-colorbox') as HTMLElement;
      const w = (window as any).zk.Widget.$(cb);
      return getComputedStyle(w.$n('pp') as HTMLElement).display === 'none';
    });
    expect(closed).toBe(true);
  });
});

// -------------------------------------------------------
// State-matrix columns must FILL their container, not cap at a fixed px.
// The old `pv-cols` grid capped its data columns at ~160px, leaving a large
// empty gutter on a wide desktop. The matrix is now the generic
// `.z-grid-cols-auto` utility (`auto repeat(var(--zk-cols), minmax(0,1fr))`),
// with each variant row a `.z-d-contents` (display:contents) whose children are
// the grid cells directly. This asserts the rightmost data cell reaches the
// container's content-right edge (i.e. the columns fill).
// -------------------------------------------------------
test.describe('pv-cols-fill', () => {
  test('state matrix columns fill the container width on desktop', async ({ page }) => {
    await page.setViewportSize({ width: 1280, height: 900 });
    await page.goto('/button.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));

    const gap = await page.evaluate(() => {
      // Data rows are `.z-d-contents` (display:contents) so their children ARE
      // the grid cells; the title row spans full width, so pick a multi-cell row.
      const row = [...document.querySelectorAll('.z-grid-cols-auto > .z-d-contents')]
        .find(r => r.children.length > 1) as HTMLElement | undefined;
      if (!row) throw new Error('no multi-cell .z-d-contents row under a .z-grid-cols-auto');
      const container = row.closest('.z-grid-cols-auto') as HTMLElement;
      const cells = [...row.children] as HTMLElement[];
      const last = cells[cells.length - 1];
      const cs = getComputedStyle(container);
      // a display:contents row has no box of its own — measure against the
      // container's content-right edge (rect minus right padding + border).
      const contentRight = container.getBoundingClientRect().right
        - parseFloat(cs.paddingRight) - parseFloat(cs.borderRightWidth);
      return Math.round(contentRight - last.getBoundingClientRect().right);
    });
    // With fill, the last cell ends at the container's content edge (sub-pixel slack).
    // With the old capped 160px tracks this gutter was several hundred px.
    expect(gap, `right gutter is ${gap}px — columns are not filling`).toBeLessThanOrEqual(8);
  });
});

// -------------------------------------------------------
// Container header height — the box-sizing-reset regression guard.
// The universal reset `*{box-sizing:border-box}` lives in `_reset.css` directly
// after the `@layer …;` order statement. CleanCSS 5.3.3 (build-css.js minify)
// dropped a bare `@layer a,b;` statement TOGETHER with the rule right after it,
// so the packaged build shipped without the reset. Headers then computed as
// content-box, making `min-height` ADD to padding instead of including it:
// window 56+32→88px, panel 48+32→80px, groupbox 48+24→73px. This guards both
// the precise cause (box-sizing must be border-box) and the visible symptom
// (header height must stay within the intended band, not the content-box blow-up).
// See doc/skill-gaps.md.
// -------------------------------------------------------
test.describe('container-header-height', () => {
  const cases = [
    { name: 'window',   url: '/window.zul',   selector: '.z-window-header',   maxHeight: 72 },
    { name: 'panel',    url: '/panel.zul',     selector: '.z-panel-header',    maxHeight: 68 },
    { name: 'groupbox', url: '/groupbox.zul',  selector: '.z-groupbox-header', maxHeight: 56 },
  ];

  for (const { name, url, selector, maxHeight } of cases) {
    test(`${name} header uses border-box and stays compact`, async ({ page }) => {
      await page.goto(url);
      await page.waitForLoadState('networkidle');
      await page.evaluate(() => document.fonts.ready.then(() => true));

      const m = await page.evaluate((sel) => {
        const headers = [...document.querySelectorAll(sel)] as HTMLElement[];
        return headers.map(h => {
          // A title that wraps (a narrow panel: the icons keep their room and the title gives
          // way) legitimately adds a line, so count the title's lines and allow one
          // line-height per extra line. The content-box blow-up adds padding, not lines.
          const text = document.createTreeWalker(h, NodeFilter.SHOW_TEXT, {
            acceptNode: n => (n.textContent || '').trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_REJECT,
          }).nextNode();
          const range = document.createRange();
          if (text) range.selectNodeContents(text);
          const lineTops = new Set([...(text ? range.getClientRects() : [])].map(r => Math.round(r.top)));
          return {
            boxSizing: getComputedStyle(h).boxSizing,
            height: Math.round(h.getBoundingClientRect().height),
            extraLines: Math.max(0, lineTops.size - 1),
            lineHeight: parseFloat(getComputedStyle(h).lineHeight) || 0,
          };
        });
      }, selector);

      expect(m.length, `no ${selector} found on ${url}`).toBeGreaterThan(0);
      for (const h of m) {
        // Precise cause: the reset must reach the header.
        expect(h.boxSizing, `${name} header box-sizing`).toBe('border-box');
        // Visible symptom: height must stay within the intended band.
        const allowed = maxHeight + h.extraLines * h.lineHeight;
        expect(h.height, `${name} header height is ${h.height}px (${h.extraLines} extra title lines)`).toBeLessThanOrEqual(allowed);
      }
    });
  }
});

// -------------------------------------------------------
// Errorbox — layout must be position-invariant
// -------------------------------------------------------
// Errorbox._fixarrow() (ZK) writes padding to ONLY the beak-facing side of
// .z-errorbox per pointer direction (padding-left when the beak points left,
// padding-top when up, etc.). The content is a table-cell so that one-sided
// padding shifts it, but the icon and close button are absolutely positioned
// against .z-errorbox and stay put. The theme overrides it with SYMMETRIC
// padding (`.z-errorbox { padding: var(--zk-errorbox-beak) !important }`, 8px)
// and adds the same beak back to the icon/close offsets, so:
//   (a) the icon→text and close→edge gaps stay constant in every direction, and
//   (b) the beak (pointer -4px + 12px triangle) lands flush at the content edge
//       — OUTSIDE the content, never intruding into it.
// Two failure modes this guards: the original one-sided drift (8px), and a
// `padding:0` over-correction that pulls the beak INSIDE the content.
test.describe('errorbox-position-invariance', () => {
  test('icon/close gaps are constant and the beak sits flush outside the content', async ({ page }) => {
    await page.setViewportSize({ width: 1280, height: 800 });
    await page.goto('/errorbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));

    const boxes = await page.evaluate(async () => {
      const win = window as any;
      // Drive validation through the widget API — synthetic typing+Tab is
      // unreliable in automation (see skills/.../components/errorbox.md).
      [...document.querySelectorAll('input.z-textbox, input.z-intbox')].forEach(inp => {
        const w = win.zk.Widget.$(inp);
        if (w && w.setErrorMessage) w.setErrorMessage('Value is required');
      });
      await new Promise(r => setTimeout(r, 500)); // Errorbox.show() defers open() by 50ms + reposition

      return [...document.querySelectorAll('.z-errorbox')]
        // real widget boxes carry pointer + icon + close; the static State
        // Gallery examples have only a subset, so require all three.
        .filter(eb => eb.querySelector('.z-errorbox-pointer')
                   && eb.querySelector('.z-errorbox-icon')
                   && eb.querySelector('.z-errorbox-close'))
        .map(eb => {
          const content = eb.querySelector('.z-errorbox-content') as HTMLElement;
          const icon = eb.querySelector('.z-errorbox-icon') as HTMLElement;
          const close = eb.querySelector('.z-errorbox-close') as HTMLElement;
          const pointer = eb.querySelector('.z-errorbox-pointer') as HTMLElement;
          const cr = content.getBoundingClientRect();
          const ir = icon.getBoundingClientRect();
          const clr = close.getBoundingClientRect();
          const pr = pointer.getBoundingClientRect();
          const dir = (pointer.className.match(/z-errorbox-(up|down|left|right)/) || [, 'none'])[1];
          // How far the beak's far edge crosses past the content edge it points
          // at, INTO the content. ~0 = flush outside; >0 = beak sits inside (bug).
          const intrusion =
            dir === 'left'  ? pr.right - cr.left :
            dir === 'right' ? cr.right - pr.left :
            dir === 'up'    ? pr.bottom - cr.top :
            dir === 'down'  ? cr.bottom - pr.top : 0;
          return {
            dir,
            iconLeftFromContent: Math.round(ir.left - cr.left),
            closeRightFromContent: Math.round(cr.right - clr.right),
            beakIntrusion: Math.round(intrusion),
          };
        });
    });

    expect(boxes.length, 'no live errorboxes were triggered').toBeGreaterThanOrEqual(2);

    for (const b of boxes) {
      // Icon sits 12px inside the content text edge, close 4px from the content
      // right edge — in EVERY pointer direction. (Buggy one-sided build: 4px.)
      expect(b.iconLeftFromContent,
        `icon→text gap is ${b.iconLeftFromContent}px for a "${b.dir}" pointer (expected ~12): ${JSON.stringify(boxes)}`)
        .toBeGreaterThanOrEqual(10);
      expect(b.iconLeftFromContent).toBeLessThanOrEqual(14);
      expect(b.closeRightFromContent,
        `close→edge gap is ${b.closeRightFromContent}px for a "${b.dir}" pointer (expected ~4): ${JSON.stringify(boxes)}`)
        .toBeGreaterThanOrEqual(2);
      expect(b.closeRightFromContent).toBeLessThanOrEqual(6);
      // Beak must sit flush OUTSIDE the content, not inside it. (padding:0
      // over-correction build: intrusion = 8px.)
      expect(b.beakIntrusion,
        `beak intrudes ${b.beakIntrusion}px into the content for a "${b.dir}" pointer (must be ≤1): ${JSON.stringify(boxes)}`)
        .toBeLessThanOrEqual(1);
    }

    // Invariance: the gaps must not drift between directions.
    const spread = (xs: number[]) => Math.max(...xs) - Math.min(...xs);
    expect(spread(boxes.map(b => b.iconLeftFromContent)),
      `icon→text gap drifts across pointer directions: ${JSON.stringify(boxes)}`).toBeLessThanOrEqual(2);
    expect(spread(boxes.map(b => b.closeRightFromContent)),
      `close→edge gap drifts across pointer directions: ${JSON.stringify(boxes)}`).toBeLessThanOrEqual(2);
  });
});

// -------------------------------------------------------
// Notification
// `.z-notification` is a bare layout shell — the visual card (bg, shadow,
// radius, padding) lives only on `.z-notification-content`. A dead duplicate
// rule in misc.css once painted the shell with inverse-surface, producing a
// dark frame around the card and pushing the icon onto the accent stripe.
// See doc/skill-gaps.md 2026-06-18.
// -------------------------------------------------------
test.describe('notification', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/notification.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('shell-is-bare-and-no-stripe', async ({ page }) => {
    const m = await page.evaluate(() => {
      const shell = document.querySelector('.z-notification-info') as HTMLElement;
      const content = document.querySelector('.z-notification-info .z-notification-content') as HTMLElement;
      const icon = document.querySelector('.z-notification-info .z-notification-icon') as HTMLElement;
      const sc = getComputedStyle(shell);
      const stripe = getComputedStyle(content, '::before');
      const cr = content.getBoundingClientRect(), ir = icon.getBoundingClientRect();
      return {
        shellBg: sc.backgroundColor,
        shellPaddingLeft: sc.paddingLeft,
        stripeWidth: parseFloat(stripe.width) || 0,
        iconLeftFromContent: Math.round(ir.x - cr.x),
      };
    });
    // shell must be transparent — the dark inverse-surface frame is the bug
    expect(m.shellBg, 'shell .z-notification must be transparent (no dark frame)').toBe('rgba(0, 0, 0, 0)');
    // no padding on the shell — padding would shift the absolute icon off its inset
    expect(parseFloat(m.shellPaddingLeft), 'shell .z-notification must have no padding-left').toBe(0);
    // the 4px accent stripe was removed from the card; it must stay removed
    expect(m.stripeWidth, 'content ::before accent stripe must stay removed (width 0)').toBe(0);
    // icon keeps its inset from the content's left edge
    expect(m.iconLeftFromContent, 'icon must sit at least 8px in from the content left edge').toBeGreaterThanOrEqual(8);
  });

  // A notification floats over arbitrary page content (e.g. top_left lands on the
  // page header). A translucent fill (rgba alpha < 1) lets that content bleed
  // through, making the header look like it sits ON TOP of the notification.
  // Every variant's card background must be fully opaque.
  test('variant-backgrounds-are-opaque', async ({ page }) => {
    const alphas = await page.evaluate(() => {
      const types = ['info', 'warning', 'error'];
      const alphaOf = (s: string) => {
        const m = s.match(/rgba?\(([^)]+)\)/);
        if (!m) return 1;
        const parts = m[1].split(',').map((x) => x.trim());
        return parts.length === 4 ? parseFloat(parts[3]) : 1;
      };
      return types.map((t) => {
        const c = document.querySelector(`.z-notification-${t} .z-notification-content`) as HTMLElement;
        return { type: t, alpha: alphaOf(getComputedStyle(c).backgroundColor) };
      });
    });
    for (const { type, alpha } of alphas) {
      expect(alpha, `.z-notification-${type} content background must be opaque (alpha=1), got ${alpha}`).toBe(1);
    }
  });

  // `.z-notification-content` has min-height:48px — taller than a single 13px/1.5
  // line (~19.5px) plus its 12px top+bottom padding (~43.5px). Block layout flows
  // the text from the top, leaving the ~4.5px slack at the bottom, so a single
  // line reads as top-aligned rather than vertically centred against the icon
  // (which IS centred via top:50%). The text's optical centre must match the
  // card's centre. See doc/skill-gaps.md 2026-06-18.
  test('single-line-content-is-vertically-centered', async ({ page }) => {
    const m = await page.evaluate(() => {
      const content = document.querySelector('.z-notification-info .z-notification-content') as HTMLElement;
      const cr = content.getBoundingClientRect();
      // measure the actual rendered text glyph box, not the padding box
      const range = document.createRange();
      range.selectNodeContents(content);
      const tr = range.getBoundingClientRect();
      return { contentCenter: cr.y + cr.height / 2, textCenter: tr.y + tr.height / 2 };
    });
    const offset = m.textCenter - m.contentCenter;
    expect(Math.abs(offset), `single-line text off-centre by ${offset.toFixed(2)}px (positive = too low, negative = too high)`).toBeLessThanOrEqual(1);
  });
});

// -------------------------------------------------------
// Toast
// -------------------------------------------------------
// The gallery screenshot covers the three TYPE variants (info/warning/error),
// whose `.z-toast-content` carries a light token tint. The typeless BASE
// `.z-toast-content` deliberately has NO background/color: the Java API always
// defaults a null type to "info", so every server-driven toast is typed. The
// old dark-scrim base fill was dead style and was removed — this guards against
// its silent re-introduction (an injected typeless probe must be transparent).
test.describe('toast', () => {
  const DIR = 'toast';
  test.beforeEach(async ({ page }) => {
    await page.goto('/toast.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('gallery', async ({ page }) => {
    await expectSectionShots(page, DIR, SECTION_MAX_DIFF_PIXELS);
  });

  test('base-content-has-no-dark-scrim-fill', async ({ page }) => {
    const bg = await page.evaluate(() => {
      const d = document.createElement('div');
      d.className = 'z-toast-content';
      document.body.appendChild(d);
      const v = getComputedStyle(d).backgroundColor;
      d.remove();
      return v;
    });
    expect(bg, 'typeless .z-toast-content base must have no fill (dark scrim removed)').toBe('rgba(0, 0, 0, 0)');
  });
});

// -------------------------------------------------------
// Tooltip (dark popup)
// -------------------------------------------------------
// `.z-popup-tooltip` is the dark MUI-style tooltip (rgba(97,97,97,0.92) + white).
// It only renders when a <popup> carries sclass="z-popup-tooltip" — exercised on
// the /popup.zul "Dark tooltip" section, NOT via plain `tooltip=` (which yields a
// white popup). The popup is detached to <body> and animated, so a screenshot is
// flaky; a computed-style assertion is the robust regression guard for the dark fill.
// (Note: `.z-tooltip` in misc.css shares the same value but has no ZUL usage —
// unreachable; tracked as a dead-code item, not covered here.)
test.describe('tooltip', () => {
  test('dark-popup-fill-and-text', async ({ page }) => {
    await page.goto('/popup.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(2500);
    await page.locator('button:has-text("Click")').first().click();
    const tip = page.locator('.z-popup-tooltip.z-popup-open').first();
    await tip.waitFor({ state: 'visible' });
    const m = await tip.evaluate(el => {
      const s = getComputedStyle(el);
      return { bg: s.backgroundColor, color: s.color, fontSize: s.fontSize };
    });
    expect(m.bg, '.z-popup-tooltip background must be the dark tooltip fill').toBe('rgba(97, 97, 97, 0.92)');
    expect(m.color, '.z-popup-tooltip text must be white').toBe('rgb(255, 255, 255)');
    // 0.6875rem against the browser-default 16px root (the `html{font-size:14px}` override was
    // removed). Guards the intended /16 size.
    expect(m.fontSize, '.z-popup-tooltip text is 0.6875rem = 11px at the 16px root').toBe('11px');
  });
});

// -------------------------------------------------------
// Linelayout
// -------------------------------------------------------
// Each lineitem's content (a button) is moved out of the cave into the
// .z-linelayout-last column at bind_(). For the timeline to read correctly the
// content slots must (a) line up along the timeline axis with their cave points
// and (b) sit adjacent to the connector line. Both depend on the first/last
// CONTENT columns carrying the per-orientation flex-direction (column in vertical)
// and align-items (flex-start on last). The Marble rewrite set flex-direction only
// on the cave, so the content column defaulted to `row` and all buttons collapsed
// to one vertical position. See doc/skill-gaps.md 2026-06-22 and contract M7.
test.describe('linelayout', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/linelayout.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    await page.waitForSelector('.z-linelayout-last .z-button, .z-linelayout-last button');
  });

  test('content-aligns-with-points', async ({ page }) => {
    const m = await page.evaluate(() => {
      const cy = (el: Element) => { const r = el.getBoundingClientRect(); return r.y + r.height / 2; };
      // Scope to the first linelayout (the model-driven vertical timeline: 4 items,
      // each a single button in the default last area). The page now hosts several
      // linelayouts demonstrating other attributes; document-wide selectors would
      // mix their points/buttons together.
      const root = document.querySelector('.z-linelayout')!;
      const points = [...root.querySelectorAll('.z-linelayout-cave .z-lineitem-point')].map(cy);
      const lastRight = root.querySelector('.z-linelayout-last')!.getBoundingClientRect().right;
      const btns = [...root.querySelectorAll('.z-linelayout-last .z-button, .z-linelayout-last button')]
        .map((el) => { const r = el.getBoundingClientRect(); return { cy: r.y + r.height / 2, left: r.x, right: r.right }; });
      const cave = root.querySelector('.z-linelayout-cave')!.getBoundingClientRect();
      return { points, btns, caveRight: cave.x + cave.width, lastRight };
    });

    expect(m.btns.length, 'expected 4 timeline buttons').toBe(4);
    expect(m.points.length, 'expected 4 cave points').toBe(4);

    // (a) buttons must occupy distinct vertical positions — not all collapsed to one
    const distinctY = new Set(m.btns.map((b) => Math.round(b.cy))).size;
    expect(distinctY, 'buttons must spread vertically along the timeline, not collapse to one row').toBeGreaterThanOrEqual(m.btns.length);

    // (b) each button's vertical centre must match its corresponding point's (±4px)
    m.btns.forEach((b, i) => {
      const d = b.cy - m.points[i];
      expect(Math.abs(d), `button[${i}] cy=${b.cy.toFixed(0)} vs point cy=${m.points[i].toFixed(0)} (off ${d.toFixed(1)}px)`).toBeLessThanOrEqual(4);
    });

    // (c) each button must hug the cave (adjacent to the connector line), not float mid-column
    m.btns.forEach((b, i) => {
      const gap = b.left - m.caveRight;
      expect(gap, `button[${i}] must sit adjacent to the cave (left edge ${gap.toFixed(0)}px from cave, expected ≤16)`).toBeLessThanOrEqual(16);
    });

    // (d) no button may be clipped by the column's overflow:hidden — the timeline
    // must be wide enough that each button renders in full (see skill-gaps 2026-06-22).
    m.btns.forEach((b, i) => {
      const overflow = b.right - m.lastRight;
      expect(overflow, `button[${i}] is clipped: right edge overflows the last column by ${overflow.toFixed(0)}px`).toBeLessThanOrEqual(1);
    });
  });

  // Icon (pointIconSclass) and image (pointImageSrc) content must be both
  // vertically and horizontally centred in the point circle. The Marble rewrite
  // gave .z-lineitem-point-inner `width/height:inherit` (→ a 24px box overflowing
  // the 20px bordered interior, offsetting content +2/+2px from the point centre)
  // and left `background-position` at the default `0% 0%` (image anchored
  // top-left). See doc/skill-gaps.md 2026-06-22 and contract M8/pt10-pt13c.
  test('point-content-centered', async ({ page }) => {
    const m = await page.evaluate(() => {
      const box = (el: Element) => {
        const r = el.getBoundingClientRect();
        return { cx: r.x + r.width / 2, cy: r.y + r.height / 2 };
      };
      const measure = (el: Element) => {
        const point = el.closest('.z-lineitem-point')!;
        const ic = box(el), pc = box(point);
        const cs = getComputedStyle(el);
        return {
          dx: ic.cx - pc.cx, dy: ic.cy - pc.cy,
          display: cs.display, alignItems: cs.alignItems, justifyContent: cs.justifyContent,
          bgPos: cs.backgroundPosition,
        };
      };
      const inners = [...document.querySelectorAll('.z-lineitem-point-inner')];
      const iconInner = inners.find((el) => /\bz-icon-/.test(el.className));
      const imageInner = inners.find((el) => /url\(.*(earth|\.png|\.jpg|\.gif|\.svg)/i.test(getComputedStyle(el).backgroundImage)
        && !el.className.includes('z-icon-')
        && !/data:image\/gif/.test(getComputedStyle(el).backgroundImage));
      return {
        icon: iconInner ? measure(iconInner) : null,
        image: imageInner ? measure(imageInner) : null,
      };
    });

    // ── Icon point ──────────────────────────────────────────────────────
    expect(m.icon, 'expected an icon point (pointIconSclass) on the page').not.toBeNull();
    // inner holder centred within the circle (no inherit-overflow offset)
    expect(Math.abs(m.icon!.dx), `icon inner off-centre horizontally by ${m.icon!.dx.toFixed(2)}px`).toBeLessThanOrEqual(1);
    expect(Math.abs(m.icon!.dy), `icon inner off-centre vertically by ${m.icon!.dy.toFixed(2)}px`).toBeLessThanOrEqual(1);
    // flex centring guarantees the 12×12 ::before glyph sits at the circle centre
    expect(m.icon!.display, 'icon inner must flex-centre its glyph').toBe('flex');
    expect(m.icon!.alignItems).toBe('center');
    expect(m.icon!.justifyContent).toBe('center');

    // ── Image point ─────────────────────────────────────────────────────
    expect(m.image, 'expected an image point (pointImageSrc) on the page').not.toBeNull();
    expect(Math.abs(m.image!.dx), `image inner off-centre horizontally by ${m.image!.dx.toFixed(2)}px`).toBeLessThanOrEqual(1);
    expect(Math.abs(m.image!.dy), `image inner off-centre vertically by ${m.image!.dy.toFixed(2)}px`).toBeLessThanOrEqual(1);
    // image must be centred in the circle, not anchored top-left (0% 0%)
    expect(m.image!.bgPos, `image must be centred in the circle (background-position=${m.image!.bgPos})`).toMatch(/^(center|50%)/);
  });
});

// The splitter family (DESIGN.md §14) presents ONE resize affordance: an 8px
// surface-container tonal bar + actuator pill. The bar's tonal fill IS the
// divider — no member draws a border on the bar. splitlayout used to be the
// lone outlier with a 1px outline-variant bar border (contract c2), which
// stacked into a double-line between its already-bordered panes. This test
// pins every family bar to border-width 0. See doc/skill-gaps.md 2026-06-23.
test.describe('splitter-family', () => {
  test('bars-are-borderless-across-the-family', async ({ page }) => {
    const maxBorder = async (url: string, selector: string) => {
      await page.goto(url);
      await page.waitForLoadState('networkidle');
      await page.evaluate(() => document.fonts.ready.then(() => true));
      await page.waitForSelector(selector);
      return page.evaluate((sel) => {
        const els = [...document.querySelectorAll(sel)];
        const widths = els.map((el) => {
          const cs = getComputedStyle(el);
          return Math.max(
            parseFloat(cs.borderTopWidth) || 0,
            parseFloat(cs.borderRightWidth) || 0,
            parseFloat(cs.borderBottomWidth) || 0,
            parseFloat(cs.borderLeftWidth) || 0,
          );
        });
        return { count: els.length, max: widths.length ? Math.max(...widths) : -1 };
      }, selector);
    };

    const splitlayout = await maxBorder('/splitlayout.zul', '.z-splitlayout-splitter');
    expect(splitlayout.count, 'expected splitlayout splitter bars on the page').toBeGreaterThan(0);
    expect(splitlayout.max, `splitlayout splitter bar must be borderless, max border-width=${splitlayout.max}px`).toBe(0);

    const splitter = await maxBorder('/splitter.zul', '.z-splitter');
    expect(splitter.count, 'expected splitter bars on the page').toBeGreaterThan(0);
    expect(splitter.max, `splitter bar must be borderless, max border-width=${splitter.max}px`).toBe(0);
  });

  // MD3 communicates hover via the state layer (colour), not geometry — no family
  // member resizes its pill on hover. borderlayout used to grow its pill 28px→44px
  // (the lone outlier; the caret already has its space at idle via opacity:0, so
  // the growth was decorative). The pill long-axis must stay 28px on hover, like
  // splitter/splitlayout. See doc/skill-gaps.md 2026-06-23.
  test('pill-does-not-resize-on-hover', async ({ page }) => {
    await page.goto('/borderlayout.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    await page.waitForSelector('.z-west-splitter-button');
    const longAxis = (sel: string) =>
      page.evaluate((s) => {
        const el = document.querySelector(s) as HTMLElement;
        return el ? el.getBoundingClientRect().height : -1;
      }, sel);

    const idle = await longAxis('.z-west-splitter-button');
    expect(idle, 'west pill idle long-axis should be the family 28px').toBeCloseTo(28, 0);

    await page.hover('.z-west-splitter');
    await page.waitForTimeout(400); // allow the (former) growth transition to settle
    const hovered = await longAxis('.z-west-splitter-button');
    expect(hovered, `west pill must not grow on hover (idle=${idle}px, hover=${hovered}px)`).toBeCloseTo(28, 0);
  });
});

// -------------------------------------------------------
// Stepbar (skill-gaps 2026-06-23/24: error glyph, vertical, wrapped-label)
// -------------------------------------------------------
test.describe('stepbar', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/stepbar.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  // error-4: ZK emits bare `z-icon-exclamation` for error steps; the theme's
  // icon build must resolve a mask-image for it, else the glyph is invisible.
  test('error-icon-glyph-renders', async ({ page }) => {
    const mask = await page.evaluate(() => {
      const icon = document.querySelector('.z-step-error .z-step-content > .z-step-icon') as HTMLElement;
      if (!icon) return { found: false } as any;
      const bs = getComputedStyle(icon, '::before');
      return {
        found: true,
        classes: icon.className,
        maskImage: bs.maskImage,
        webkitMask: (bs as any).webkitMaskImage,
        iconVar: getComputedStyle(icon).getPropertyValue('--_icon').trim(),
      };
    });
    expect(mask.found, 'error step must exist in the preview').toBe(true);
    const resolved = (mask.maskImage && mask.maskImage !== 'none') ||
                     (mask.webkitMask && mask.webkitMask !== 'none');
    expect(resolved, `error icon (${mask.classes}) must resolve a mask-image glyph; got mask-image:${mask.maskImage}, --_icon:"${mask.iconVar}"`).toBe(true);
  });

  // vert-2/vert-10: vertical step is a column; connector is a vertical stripe
  // whose horizontal centre aligns with the icon centre (±2px).
  test('vertical-connector-centered-under-icon', async ({ page }) => {
    const m = await page.evaluate(() => {
      const bar = document.querySelector('.z-stepbar-vertical');
      if (!bar) return { found: false } as any;
      const step = bar.querySelectorAll('.z-step')[1] as HTMLElement; // non-first → has connector
      const before = getComputedStyle(step, '::before');
      const icon = step.querySelector('.z-step-content > .z-step-icon') as HTMLElement;
      const sr = step.getBoundingClientRect();
      const ir = icon.getBoundingClientRect();
      const w = parseFloat(before.width);
      const h = parseFloat(before.height === 'auto' ? before.minHeight : before.height);
      const ml = parseFloat(before.marginLeft);
      return {
        found: true,
        stepFlexDir: getComputedStyle(step).flexDirection,
        beforeW: w, beforeH: h,
        connectorCenterX: sr.left + ml + w / 2,
        iconCenterX: ir.left + ir.width / 2,
      };
    });
    expect(m.found, 'vertical stepbar must exist in the preview').toBe(true);
    expect(m.stepFlexDir, 'vertical step must be a column so the connector stacks above content').toBe('column');
    expect(m.beforeH, `connector must be a vertical stripe (taller than wide): w=${m.beforeW} h=${m.beforeH}`).toBeGreaterThan(m.beforeW);
    expect(Math.abs(m.connectorCenterX - m.iconCenterX), `connector center-x (${m.connectorCenterX}) must align with icon center-x (${m.iconCenterX})`).toBeLessThanOrEqual(2);
  });

  // wrap: wrapped-label stacks the title below the icon (content = column) and
  // draws the connector on .z-step-content::before (not the inline .z-step::before).
  test('wrapped-label-stacks-title-below-icon', async ({ page }) => {
    const m = await page.evaluate(() => {
      const bar = document.querySelector('.z-stepbar-wrapped-label');
      if (!bar) return { found: false } as any;
      const step = bar.querySelectorAll('.z-step')[1] as HTMLElement;
      const content = step.querySelector('.z-step-content') as HTMLElement;
      const icon = step.querySelector('.z-step-content > .z-step-icon') as HTMLElement;
      const title = step.querySelector('.z-step-content > .z-step-title') as HTMLElement;
      return {
        found: true,
        contentFlexDir: getComputedStyle(content).flexDirection,
        iconBottom: icon.getBoundingClientRect().bottom,
        titleTop: title.getBoundingClientRect().top,
        contentBeforeContent: getComputedStyle(content, '::before').content,
      };
    });
    expect(m.found, 'wrapped-label stepbar must exist in the preview').toBe(true);
    expect(m.contentFlexDir, 'wrapped-label content must be a column (icon above title)').toBe('column');
    expect(m.titleTop, `title (top=${m.titleTop}) must sit below the icon (bottom=${m.iconBottom})`).toBeGreaterThanOrEqual(m.iconBottom - 1);
    expect(m.contentBeforeContent !== 'none', `connector must be drawn on .z-step-content::before; content:${m.contentBeforeContent}`).toBe(true);
  });

  // wrap-1c: a multi-line title must NOT shift its icon/connector vertically.
  // All step icons (hence all connector segments) must share one center-Y, so the
  // connector line stays unbroken regardless of how many lines each label wraps to.
  test('wrapped-label-icons-share-one-baseline', async ({ page }) => {
    const ys = await page.evaluate(() => {
      const bar = document.querySelector('.z-stepbar-wrapped-label');
      if (!bar) return null as any;
      return [...bar.querySelectorAll('.z-step .z-step-icon')].map((icon) => {
        const r = (icon as HTMLElement).getBoundingClientRect();
        return Math.round(r.top + r.height / 2);
      });
    });
    expect(ys, 'wrapped-label stepbar must exist').not.toBeNull();
    const min = Math.min(...ys), max = Math.max(...ys);
    expect(max - min, `all step icons must share one center-Y so connectors stay aligned; got ${JSON.stringify(ys)}`).toBeLessThanOrEqual(2);
  });
});

// -------------------------------------------------------
// Dropupload
// -------------------------------------------------------
test.describe('dropupload', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/dropupload.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  // The ZK Dropupload widget never emits `.z-dropupload-active` or
  // `.z-dropupload-disabled` (verified against Dropupload.ts/.java — drag feedback
  // is the native cursor `dataTransfer.dropEffect='copy'`, and there is no setDisabled).
  // The theme must not ship CSS rules for those phantom states. This guards the
  // dead-CSS regression: it fails while the rules exist and passes once they are gone.
  test('no-phantom-state-rules', async ({ page }) => {
    const phantom = await page.evaluate(() => {
      const hits: string[] = [];
      for (const sheet of Array.from(document.styleSheets)) {
        let rules: CSSRuleList;
        try { rules = sheet.cssRules; } catch { continue; } // cross-origin: skip
        for (const rule of Array.from(rules)) {
          const sel = (rule as CSSStyleRule).selectorText;
          if (sel && /\.z-dropupload-(active|disabled)\b/.test(sel)) hits.push(sel);
        }
      }
      return hits;
    });
    expect(phantom, `theme ships no CSS for ZK-nonexistent dropupload states; found: ${JSON.stringify(phantom)}`).toEqual([]);
  });
});

// -------------------------------------------------------
// Inputgroup — focusing an inner input must not change its size
// -------------------------------------------------------
test.describe('inputgroup', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/inputgroup.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  // ZK-6112: a grouped input and its addon share ONE 1px edge, whichever side the
  // addon is on. Before the fix only a leading addon was merged, so a trailing addon
  // showed a 2px seam (input border-right + addon border-left); the number boxes had
  // no inputgroup rules at all. Checks every horizontal text addon <-> input pair.
  test('addon and input share a single 1px edge (prefix and suffix, number boxes)', async ({ page }) => {
    const info = await page.evaluate(() => {
      const inputSel = '.z-textbox, .z-intbox, .z-longbox, .z-doublebox, .z-decimalbox, .z-combobox';
      const px = (s: string) => parseFloat(s) || 0;
      const seams: string[] = [];
      let pairs = 0;
      let numberBoxes = 0;
      document.querySelectorAll('.z-inputgroup:not(.z-inputgroup-vertical)').forEach(g => {
        const kids = [...g.children] as HTMLElement[];
        kids.forEach((k, i) => {
          if (k.matches('.z-intbox, .z-longbox, .z-doublebox, .z-decimalbox')) {
            numberBoxes++;
            const cs = getComputedStyle(k);
            if (px(cs.borderTopWidth) !== 1 || cs.flexGrow !== '1' || cs.minWidth !== '0px')
              seams.push(`number box not styled like textbox: ${k.className}`);
          }
          const next = kids[i + 1];
          if (!next) return;
          const addonFirst = k.matches('.z-inputgroup-text') && next.matches(inputSel);
          const inputFirst = k.matches(inputSel) && next.matches('.z-inputgroup-text');
          if (!addonFirst && !inputFirst) return;
          pairs++;
          const shared = px(getComputedStyle(k).borderRightWidth) + px(getComputedStyle(next).borderLeftWidth);
          if (shared !== 1) seams.push(`${k.className} | ${next.className}: shared edge ${shared}px`);
        });
      });
      return { pairs, numberBoxes, seams };
    });
    expect(info.pairs, 'preview must contain addon/input pairs').toBeGreaterThan(3);
    expect(info.numberBoxes, 'preview must contain number boxes in an inputgroup').toBeGreaterThan(0);
    expect(info.seams, info.seams.join('; ')).toEqual([]);
  });

  // The single-input focus rule in input.css bumps a textbox's border 1px→2px
  // (Mechanism B). Inside a group that thickening leaks onto the child while its
  // addon/button neighbours stay 1px, so the focused segment puffs proud of them
  // and the group "looks bigger". The fix keeps grouped children
  // at a constant 1px border in every state — the group's :focus-within outline
  // owns the affordance. Guard: a focused grouped input's border-width == its rest
  // border-width (and the group's bbox is unchanged), per
  // reference/focus-affordance-no-layout-shift.md.
  test('no-layout-shift-on-focus', async ({ page }) => {
    const groups = await page.evaluate(() => {
      // Border-width animates over the standard transition; disable transitions
      // so the focused geometry/border settles to its target value immediately.
      const style = document.createElement('style');
      style.textContent = '*{transition:none !important; animation:none !important}';
      document.head.appendChild(style);

      const round = (n: number) => Math.round(n * 100) / 100;
      const px = (s: string) => parseFloat(s) || 0;
      const out: any[] = [];
      document.querySelectorAll('.z-inputgroup').forEach((g, i) => {
        const input = g.querySelector('.z-textbox') as HTMLElement | null;
        if (!input) return; // group has no textbox (skip)
        (document.activeElement as HTMLElement | null)?.blur();
        const gRest = g.getBoundingClientRect();
        const csRest = getComputedStyle(input);
        const restBW = [csRest.borderTopWidth, csRest.borderRightWidth,
                        csRest.borderBottomWidth, csRest.borderLeftWidth].map(px);
        input.focus();
        void (g as HTMLElement).offsetWidth; // force reflow
        const gFoc = g.getBoundingClientRect();
        const csFoc = getComputedStyle(input);
        const focBW = [csFoc.borderTopWidth, csFoc.borderRightWidth,
                       csFoc.borderBottomWidth, csFoc.borderLeftWidth].map(px);
        input.blur();
        out.push({
          i,
          groupDW: round(gFoc.width - gRest.width),
          groupDH: round(gFoc.height - gRest.height),
          restBW, focBW,
          maxBWGrowth: round(Math.max(...focBW.map((w, k) => w - restBW[k]))),
        });
      });
      return out;
    });

    expect(groups.length, 'expected at least one .z-inputgroup with a textbox').toBeGreaterThan(0);
    for (const g of groups) {
      expect(Math.abs(g.groupDW),
        `inputgroup[${g.i}] width changed on focus by ${g.groupDW}px (rest BW ${JSON.stringify(g.restBW)} → focus BW ${JSON.stringify(g.focBW)})`).toBeLessThanOrEqual(0.5);
      expect(Math.abs(g.groupDH),
        `inputgroup[${g.i}] height changed on focus by ${g.groupDH}px`).toBeLessThanOrEqual(0.5);
      // The real defect: the grouped input's border thickens on focus.
      expect(g.maxBWGrowth,
        `inputgroup[${g.i}] inner input border grew on focus: rest ${JSON.stringify(g.restBW)} → focus ${JSON.stringify(g.focBW)} (the focused segment puffs past its 1px neighbours)`).toBeLessThanOrEqual(0);
    }
  });
});

// =======================================================
// State-depth blocks (doc/playwright-visual-test-guide.md §4)
// Distinct interactive controls whose hover/focus state layer is worth a baseline
// beyond the breadth gallery shot in gallery-scan.spec.ts.
// =======================================================

// -------------------------------------------------------
// Radiogroup — hover/focus state layer on a radio
// -------------------------------------------------------
test.describe('radiogroup', () => {
  const DIR = 'radiogroup';
  test.beforeEach(async ({ page }) => {
    await page.goto('/radiogroup.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      const el = page.locator('.z-radio').first();
      await action(el);
      await padShot(page, el, `${DIR}-${name}.png`);
    });
  }
});

// -------------------------------------------------------
// Rating — hovering a star highlights the run up to the cursor
// -------------------------------------------------------
test.describe('rating', () => {
  const DIR = 'rating';
  test.beforeEach(async ({ page }) => {
    await page.goto('/rating.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  // ZK-6112: iconSclass picks the glyph. Marble draws the default z-icon-star itself
  // (SVG mask, ::before off); any other iconSclass must keep its icon-font glyph, so no
  // mask and a visible ::before. Before the fix every icon was masked as a star.
  test('iconSclass picks the glyph; the star mask is only the default', async ({ page }) => {
    const info = await page.evaluate(() => {
      const probe = (el: Element | null) => el && ({
        mask: getComputedStyle(el).maskImage,
        before: getComputedStyle(el, '::before').content,
      });
      return {
        star: probe(document.querySelector('.z-rating-icon.z-icon-star')),
        custom: probe(document.querySelector('.z-rating-icon.z-icon-bolt')),
      };
    });
    expect(info.star, 'default star icon must be present').toBeTruthy();
    expect(info.custom, 'custom iconSclass icon must be present').toBeTruthy();
    expect(info.star!.mask, 'default star keeps the SVG mask').not.toBe('none');
    expect(info.star!.before, 'default star hides the font glyph').toBe('none');
    expect(info.custom!.mask, 'custom icon must not be star-masked').toBe('none');
    expect(info.custom!.before, 'custom icon must show its font glyph').not.toBe('none');
  });

  test('hover', async ({ page }) => {
    // hover the 3rd star of the first rating; capture the whole control so the
    // highlight spread (stars 1–3 filled) is visible.
    await page.locator('.z-rating-icon').nth(2).hover();
    await padShot(page, page.locator('.z-rating').first(), `${DIR}-hover.png`);
  });
});

// -------------------------------------------------------
// Slider — hover/focus state layer on the knob
// -------------------------------------------------------
test.describe('slider', () => {
  const DIR = 'slider';
  test.beforeEach(async ({ page }) => {
    await page.goto('/slider.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      await action(page.locator('.z-slider-button').first());
      // capture the whole slider so the knob's state layer is in context
      await padShot(page, page.locator('.z-slider').first(), `${DIR}-${name}.png`);
    });
  }
});

// -------------------------------------------------------
// Colorbox — hover/focus state on the swatch button
// -------------------------------------------------------
test.describe('colorbox', () => {
  const DIR = 'colorbox';
  test.beforeEach(async ({ page }) => {
    await page.goto('/colorbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  for (const { name, action } of hoverFocusStates) {
    test(name, async ({ page }) => {
      const el = page.locator('.z-colorbox').first();
      await action(el);
      await padShot(page, el, `${DIR}-${name}.png`);
    });
  }
});

// =======================================================
// Codeeditor
// =======================================================
// The focus ring is measured in PAINTED PIXELS, not computed style, and that is the
// whole point of these two guards. Codeeditor's payload below `.z-codeeditor-cave` is
// CodeMirror's own DOM, it paints opaque backgrounds (the gutter always; the entire
// editor on the dark surface), and the root has zero padding — so a ring drawn as an
// inset box-shadow on the ROOT is painted under those children and is partly or wholly
// invisible. `getComputedStyle(root).boxShadow` still reports `inset 0 0 0 1px primary`
// in exactly that broken state, so only pixels can tell the two apart.
// See reference/focus-affordance-no-layout-shift.md ("an opaque child background
// occludes a root inset ring") — the remedy is the ::after overlay, as on timepicker.
test.describe('codeeditor', () => {
  type RingScan = {
    sides: { top: number; right: number; bottom: number; left: number };
    ring: [number, number, number];
    fill: [number, number, number];
  };

  // Screenshot the element, then decode it back inside the page on a canvas (no image
  // library needed) and walk inward from the midpoint of each edge counting how many
  // consecutive pixels still match the ring colour. That run-length IS the visible ring
  // thickness on that side. Midpoints only — the corners are rounded.
  async function scanRing(page: Page, sel: string): Promise<RingScan> {
    const loc = page.locator(sel).first();
    await loc.scrollIntoViewIfNeeded();
    const png = (await loc.screenshot()).toString('base64');
    return await page.evaluate(async (b64) => {
      const img = new Image();
      await new Promise((res, rej) => { img.onload = res; img.onerror = rej; img.src = 'data:image/png;base64,' + b64; });
      const cv = document.createElement('canvas');
      cv.width = img.naturalWidth; cv.height = img.naturalHeight;
      const ctx = cv.getContext('2d', { willReadFrequently: true })!;
      ctx.drawImage(img, 0, 0);
      const W = cv.width, H = cv.height;
      const at = (x: number, y: number): [number, number, number] => {
        const d = ctx.getImageData(x, y, 1, 1).data;
        return [d[0], d[1], d[2]];
      };
      const dist = (a: number[], b: number[]) =>
        Math.max(Math.abs(a[0] - b[0]), Math.abs(a[1] - b[1]), Math.abs(a[2] - b[2]));
      const mx = Math.floor(W / 2), my = Math.floor(H / 2);
      const ring = at(mx, 0);                       // outermost pixel on a straight edge
      // Modal interior colour, sampled off a grid so a glyph or a syntax-coloured
      // token can't be mistaken for the surface fill.
      const tally = new Map<string, { n: number; c: [number, number, number] }>();
      for (let x = 8; x < W - 8; x += 7) {
        for (let y = 8; y < H - 8; y += 7) {
          const c = at(x, y), k = c.join(',');
          const e = tally.get(k) ?? { n: 0, c };
          e.n++; tally.set(k, e);
        }
      }
      const fill = [...tally.values()].sort((a, b) => b.n - a.n)[0].c;
      const run = (x0: number, y0: number, dx: number, dy: number) => {
        let n = 0;
        for (let i = 0; i < 10; i++) {
          if (dist(at(x0 + dx * i, y0 + dy * i), ring) > 40) break;
          n++;
        }
        return n;
      };
      return {
        sides: {
          top: run(mx, 0, 0, 1),
          bottom: run(mx, H - 1, 0, -1),
          left: run(0, my, 1, 0),
          right: run(W - 1, my, -1, 0),
        },
        ring, fill,
      };
    }, png);
  }

  const contrast = (a: number[], b: number[]) => {
    const lum = (c: number[]) => {
      const f = (v: number) => { v /= 255; return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4); };
      return 0.2126 * f(c[0]) + 0.7152 * f(c[1]) + 0.0722 * f(c[2]);
    };
    const [hi, lo] = [lum(a), lum(b)].sort((m, n) => n - m);
    return (hi + 0.05) / (lo + 0.05);
  };

  async function openFocused(page: Page, sel: string) {
    await page.goto('/codeeditor.zul');
    await page.waitForSelector('.z-codeeditor .cm-editor', { timeout: 15000 });
    await page.evaluate(() => document.fonts.ready);
    // The root's transition does not reach ::after, so kill both with a stylesheet.
    await page.addStyleTag({
      content: '*,*::before,*::after{transition:none!important;animation:none!important}',
    });
    await page.evaluate((s) => {
      const root = document.querySelector(s)!;
      (root.querySelector('.cm-content') as HTMLElement).focus();
    }, sel);
  }

  // RED before the fix: the gutter paints over the inset pixel on the left, so the ring
  // measures 1px there against 2px on the other three sides — the reported asymmetry.
  test('focus ring is uniform on all four sides (gutter must not eat it)', async ({ page }) => {
    const sel = '.z-codeeditor:not(.z-codeeditor-dark):not(.z-codeeditor-disabled)';
    await openFocused(page, sel);
    const s = await scanRing(page, sel);
    const { top, right, bottom, left } = s.sides;
    expect(top, 'ring must be 2px (1px border + 1px overlay)').toBe(2);
    expect(
      [right, bottom, left],
      `all four sides must match the top edge — measured ${JSON.stringify(s.sides)}`,
    ).toEqual([top, top, top]);
  });

  // RED before the fix twice over: the dark editor paints an opaque background on
  // .cm-editor, so ALL FOUR sides are occluded and the ring collapses to the bare 1px
  // border; and that border is --zk-color-primary #376fd0, only 3.45:1 on the #1e1e1e
  // fill. MD3 puts a lighter primary tone on a dark surface.
  test('dark surface focus ring is uniform and legible against the fill', async ({ page }) => {
    const sel = '.z-codeeditor-dark';
    await openFocused(page, sel);
    const s = await scanRing(page, sel);
    const { top, right, bottom, left } = s.sides;
    expect(top, 'dark ring must be 2px (1px border + 1px overlay)').toBe(2);
    expect(
      [right, bottom, left],
      `all four sides must match the top edge — measured ${JSON.stringify(s.sides)}`,
    ).toEqual([top, top, top]);
    const ratio = contrast(s.ring, s.fill);
    expect(
      ratio,
      `ring rgb(${s.ring}) on fill rgb(${s.fill}) = ${ratio.toFixed(2)}:1 — a dark-surface ` +
      `ring must clear 4.5:1, well above the 3:1 UI-component floor`,
    ).toBeGreaterThanOrEqual(4.5);
  });
});

// =======================================================
// `!important` removal guards
// These assert the computed value that a now-deleted `!important` used to force
// is still produced by the plain cascade on ZK 10.3.0.1. Each was proven
// render-neutral before the keyword was removed (see doc/spec/important-inventory.md
// + doc/important-decisions.md). If a future ZK/@layer change lets a lower rule
// win again, these fail — the signal to restore the override.
// =======================================================
test.describe('important-removal-guards', () => {
  test('progressmeter fill bar stays display:block without !important', async ({ page }) => {
    await page.goto('/progressmeter.zul');
    await page.waitForLoadState('networkidle');
    const display = await page.evaluate(() => {
      const el = document.querySelector('.z-progressmeter-image');
      if (!el) throw new Error('no .z-progressmeter-image');
      return getComputedStyle(el).display;
    });
    expect(display).toBe('block');
  });

  test('messagebox button row is flex with zeroed child margin without !important', async ({ page }) => {
    await page.goto('/messagebox.zul');
    await page.waitForLoadState('networkidle');
    await page.click('button:has-text("YES / NO / CANCEL")');
    await page.waitForSelector('.z-messagebox-buttons');
    const r = await page.evaluate(() => {
      const box = document.querySelector('.z-messagebox-buttons');
      const kid = box.firstElementChild;
      return { display: getComputedStyle(box).display, ml: getComputedStyle(kid).marginLeft };
    });
    expect(r.display).toBe('flex');
    expect(r.ml).toBe('0px');
  });

  test('menupopup separator keeps its 4px vertical margin without !important', async ({ page }) => {
    await page.goto('/menubar.zul');
    await page.waitForLoadState('networkidle');
    await page.click('.z-menu:has-text("Project")');
    await page.waitForTimeout(300);
    const m = await page.evaluate(() => {
      const sep = document.querySelector('.z-menupopup-content > li.z-menuseparator');
      if (!sep) throw new Error('no menu separator');
      const cs = getComputedStyle(sep);
      return { top: cs.marginTop, bottom: cs.marginBottom };
    });
    expect(m.top).toBe('4px');
    expect(m.bottom).toBe('4px');
  });
});

// Follow-up 45 F1 (D21 = B): a bar separator between toolbar items is a 1px x 24px divider in the toolbar's own axis,
// whichever orient it has, and the items after it stay next to it; a separator without a bar stays a flex spacer.
// Fixture: web/pv/separator-toolbar-guard.zul
test.describe('separator in a toolbar (follow-up 45 F1)', () => {
  const FIXTURE = '/pv/separator-toolbar-guard.zul';
  const DIVIDER_LONG_PX = 24;
  const TOOLBAR_GAP_PX = 4.5;
  const DIVIDER_GAP_MAX_PX = 2 * TOOLBAR_GAP_PX;

  async function measure(page: Page, wrapper: string) {
    return page.evaluate((cls) => {
      const root = document.querySelector('.' + cls) as HTMLElement;
      const box = (el: Element) => el.getBoundingClientRect();
      const sep = root.querySelector('.z-separator') as HTMLElement;
      const [first, last] = Array.from(root.querySelectorAll('.z-toolbarbutton'));
      const toolbar = root.querySelector('.z-toolbar') as HTMLElement;
      return {
        width: box(sep).width, height: box(sep).height,
        background: getComputedStyle(sep).backgroundColor,
        centreX: box(sep).left + box(sep).width / 2, centreY: box(sep).top + box(sep).height / 2,
        toolbarCentreX: box(toolbar).left + box(toolbar).width / 2, toolbarContentCentreY: box(first).top + box(first).height / 2,
        gapAfterX: box(last).left - box(sep).right, gapAfterY: box(last).top - box(sep).bottom,
        gapBeforeX: box(sep).left - box(first).right,
      };
    }, wrapper);
  }

  async function open(page: Page) {
    await page.goto(FIXTURE, { waitUntil: 'domcontentloaded' });
    await page.locator('.gs-plain').waitFor();
    await page.evaluate(() => document.fonts.ready);
  }

  for (const wrapper of ['gs-horizontal-bar', 'gs-vertical-bar']) {
    test(`${wrapper}: a bar in a horizontal toolbar is a 1px x ${DIVIDER_LONG_PX}px divider between adjacent items`, async ({ page }) => {
      await open(page);
      const m = await measure(page, wrapper);
      expect({ width: m.width, height: m.height }).toEqual({ width: 1, height: DIVIDER_LONG_PX });
      expect(m.background, 'the divider is painted').not.toBe('rgba(0, 0, 0, 0)');
      expect(Math.abs(m.centreY - m.toolbarContentCentreY), 'centred on the items').toBeLessThanOrEqual(1);
      expect(m.gapAfterX, 'the next item stays next to the divider').toBeLessThanOrEqual(DIVIDER_GAP_MAX_PX);
      expect(m.gapBeforeX, 'the previous item stays next to the divider').toBeLessThanOrEqual(DIVIDER_GAP_MAX_PX);
    });
  }

  for (const wrapper of ['gs-toolbar-vertical', 'gs-vertical-toolbar-vertical-bar']) {
    test(`${wrapper}: a bar in a vertical toolbar is a ${DIVIDER_LONG_PX}px x 1px divider between adjacent items`, async ({ page }) => {
      await open(page);
      const m = await measure(page, wrapper);
      expect({ width: m.width, height: m.height }).toEqual({ width: DIVIDER_LONG_PX, height: 1 });
      expect(Math.abs(m.centreX - m.toolbarCentreX), 'centred across the toolbar').toBeLessThanOrEqual(1);
      expect(m.gapAfterY, 'the next item stays next to the divider').toBeLessThanOrEqual(DIVIDER_GAP_MAX_PX);
    });
  }

  // ZK writes `spacing` inline (height on a horizontal bar, width on a vertical-orient bar); the divider keeps its 24px.
  for (const wrapper of ['gs-spacing-horizontal', 'gs-spacing-vertical-orient']) {
    test(`${wrapper}: a bar with spacing="40px" in a horizontal toolbar is still a 1px x ${DIVIDER_LONG_PX}px divider`, async ({ page }) => {
      await open(page);
      const m = await measure(page, wrapper);
      expect({ width: m.width, height: m.height }).toEqual({ width: 1, height: DIVIDER_LONG_PX });
    });
  }

  for (const wrapper of ['gs-spacing-toolbar-vertical', 'gs-spacing-vertical-toolbar-vertical-bar']) {
    test(`${wrapper}: a bar with spacing="40px" in a vertical toolbar is still a ${DIVIDER_LONG_PX}px x 1px divider`, async ({ page }) => {
      await open(page);
      const m = await measure(page, wrapper);
      expect({ width: m.width, height: m.height }).toEqual({ width: DIVIDER_LONG_PX, height: 1 });
    });
  }

  test('gs-plain: a separator without a bar is still a spacer that pushes the next item to the end', async ({ page }) => {
    await open(page);
    const m = await measure(page, 'gs-plain');
    expect(m.width, 'the spacer takes the free width').toBeGreaterThan(100);
    expect(m.gapAfterX, 'the next item is one flex gap from the spacer, at the far end').toBeLessThanOrEqual(TOOLBAR_GAP_PX);
  });
});

// =======================================================
// Remaining input / form-control state coverage
// Goal: EVERY ZK input component carries a hover + focus baseline, not just the
// representative few. These follow one of two structural patterns:
//   - self-styled:    border/ring on the element itself  → action == capture
//   - wrapper-styled:  border/ring on the wrapper, focusable node inside →
//                      action targets the focusable node, capture the wrapper
// `focus` = the node to hover/focus; `shot` = the box to screenshot (padded).
// All baselines land at doc/screenshots/<comp>/<state>.png.
// =======================================================
const FORM_CONTROL_STATES: { comp: string; focus: string; shot: string }[] = [
  // Numeric textbox variants — share input.css with textbox (border on the element).
  { comp: 'intbox',        focus: '.z-intbox',               shot: '.z-intbox' },
  { comp: 'longbox',       focus: '.z-longbox',              shot: '.z-longbox' },
  { comp: 'doublebox',     focus: '.z-doublebox',            shot: '.z-doublebox' },
  { comp: 'decimalbox',    focus: '.z-decimalbox',           shot: '.z-decimalbox' },
  // Wrapper-styled: ring on the wrapper, focusable <input> inside.
  { comp: 'doublespinner', focus: '.z-doublespinner-input',  shot: '.z-doublespinner' },
  { comp: 'chosenbox',     focus: '.z-chosenbox-input',      shot: '.z-chosenbox' },
  // Self-focusable wrappers — the root carries tabindex; ZK toggles .z-*-focus
  // on focus and the theme draws the ring off that class (verified live: a
  // programmatic focus does fire it). hover uses a plain :hover border change.
  { comp: 'searchbox',     focus: '.z-searchbox',            shot: '.z-searchbox' },
  { comp: 'cascader',      focus: '.z-cascader',             shot: '.z-cascader' },
  // Combobutton: :focus-within / :hover drive a state-layer tint on the content.
  { comp: 'combobutton',   focus: '.z-combobutton',          shot: '.z-combobutton' },
  // Slider variants — state layer on the knob, capture the whole control.
  { comp: 'multislider',   focus: '.z-sliderbuttons-button', shot: '.z-multislider' },
  { comp: 'rangeslider',   focus: '.z-sliderbuttons-button', shot: '.z-rangeslider' },
];

for (const { comp, focus, shot } of FORM_CONTROL_STATES) {
  test.describe(comp, () => {
    test.beforeEach(async ({ page }) => {
      await page.goto(`/${comp}.zul`);
      await page.waitForLoadState('networkidle');
      await page.evaluate(() => document.fonts.ready.then(() => true));
    });
    for (const { name, action } of hoverFocusStates) {
      test(name, async ({ page }) => {
        await action(page.locator(focus).first());
        await padShot(page, page.locator(shot).first(), `${comp}-${name}.png`);
      });
    }
  });
}

// -------------------------------------------------------
// Dropdown popups with a highlighted item (searchbox, chosenbox)
// Batch 2 changed the list-row selected colour; the popups were never captured open.
// Searchbox: the second default searchbox has a pre-selected item, which carries
// `.z-searchbox-selected` (secondary-container). Chosenbox dropdown: ArrowDown moves
// `.z-chosenbox-option-hover`, whose fill is hardcoded (follow-up item 10 in
// doc/marble-theme-followups.md), so that shot does NOT cover batch 2's colour. The
// chosenbox chip shot below covers batch 2's `--zk-chosenbox-item-focus-bg`
// (`.z-chosenbox-item-focus`).
// -------------------------------------------------------
test.describe('searchbox dropdown', () => {
  test('dropdown', async ({ page }) => {
    await page.goto('/searchbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    await page.locator('.z-searchbox:not(.z-searchbox-disabled)').nth(1).click();
    await page.mouse.move(0, 0);
    const popup = page.locator('.z-searchbox-popup:visible');
    await expect(popup).toHaveCount(1);
    await expect(popup.locator('.z-searchbox-selected')).toHaveCount(1);
    await padShot(page, popup, 'searchbox-dropdown.png');
  });
});

test.describe('chosenbox dropdown', () => {
  test('dropdown', async ({ page }) => {
    await page.goto('/chosenbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    await page.locator('.z-chosenbox').first().click();
    await page.keyboard.press('ArrowDown');
    await page.mouse.move(0, 0);
    const popup = page.locator('.z-chosenbox-popup:visible');
    await expect(popup).toHaveCount(1);
    await expect(popup.locator('.z-chosenbox-option-hover')).toHaveCount(1);
    await padShot(page, popup, 'chosenbox-dropdown.png');
  });

  // Clicking a selected chip gives it `.z-chosenbox-item-focus` (batch 2's
  // `--zk-chosenbox-item-focus-bg`).
  test('chip focus', async ({ page }) => {
    await page.goto('/chosenbox.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    const box = page.locator('.z-chosenbox:has(.z-chosenbox-item)').first();
    await box.locator('.z-chosenbox-item').first().click();
    await page.mouse.move(0, 0);
    await expect(page.locator('.z-chosenbox-item-focus')).toHaveCount(1);
    await padShot(page, box, 'chosenbox-chip-focus.png');
  });
});

// -------------------------------------------------------
// Scrollbar (custom / simulated overlay + embedded)
// MD3 gap (doc/skill-gaps.md 2026-06-30): the simulated bar's thumb/rail/embed
// used a hardcoded `border-radius: 4px` and the `ease` timing keyword instead of
// the theme's pill radius (--zk-shape-corner-full) + standard easing
// (--zk-motion-easing-standard = cubic-bezier(0.4, 0, 0.2, 1)). Thumb COLOURS are
// a deliberate "bolder draggable thumb" choice and are intentionally NOT asserted.
// The State Gallery at the top of scrollbar.zul always renders a real
// `.z-scrollbar-indicator`, so this is deterministic regardless of ZK's JS init.
// -------------------------------------------------------
test.describe('scrollbar', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/scrollbar.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
  });

  test('simulated-thumb-md3-radius-and-easing', async ({ page }) => {
    const probe = await page.evaluate(() => {
      const el = document.querySelector('.z-scrollbar-indicator');
      if (!el) throw new Error('No .z-scrollbar-indicator found on /scrollbar.zul');
      const cs = getComputedStyle(el);
      return { radius: cs.borderTopLeftRadius, timing: cs.transitionTimingFunction };
    });
    // Pill radius (--zk-shape-corner-full), not the old hardcoded 4px.
    expect(parseFloat(probe.radius), `thumb border-radius should be a pill, got "${probe.radius}"`).toBeGreaterThan(100);
    // MD3 standard easing curve, not the `ease` keyword.
    expect(probe.timing, `thumb transition easing should be the MD3 standard curve, got "${probe.timing}"`).toContain('cubic-bezier(0.4, 0, 0.2, 1)');
  });

  // MD3 gap (doc/skill-gaps.md 2026-06-30): in EMBEDDED mode (data-embedscrollbar=true)
  // the rest `*-embed` rail and the hover `.z-scrollbar` bar disagreed on their cross-axis
  // offset, so the bar jumped ~5px laterally on mouse-over (rest center 3px vs hover 8px
  // from the edge) and read as "two different scrollbars". MD3 continuity-of-motion: the
  // rest rail must preview the hover thumb at the SAME cross-axis line — hover only grows
  // (track + arrows fade in), it must not move. ZK's scroll-sync pins both the bar and the
  // *-embed rail to the same cross-axis anchor inline, so the fix is thickness/inset (size
  // the embed to the track width + edge-anchor the track) — see the scrollbar skill. The
  // embedded grid is the 2nd .z-grid on the page (overlay / embedded / native).
  test('embed-rail-and-hover-thumb-share-cross-axis-no-jump', async ({ page }) => {
    const grid = page.locator('.z-grid').nth(1);
    await grid.scrollIntoViewIfNeeded();
    await grid.locator('.z-scrollbar-vertical-embed').waitFor({ state: 'attached' });

    // REST: cross-axis CENTER of each embed rail, as a distance from the grid body's edge.
    await page.mouse.move(0, 0);
    const rest = await grid.evaluate((g) => {
      const body = g.querySelector('.z-grid-body') || g;
      const br = body.getBoundingClientRect();
      const ve = g.querySelector('.z-scrollbar-vertical-embed') as HTMLElement | null;
      const he = g.querySelector('.z-scrollbar-horizontal-embed') as HTMLElement | null;
      const vr = ve && ve.offsetWidth ? ve.getBoundingClientRect() : null;
      const hr = he && he.offsetHeight ? he.getBoundingClientRect() : null;
      return {
        vCenterFromRight: vr ? br.right - (vr.left + vr.right) / 2 : null,
        hCenterFromBottom: hr ? br.bottom - (hr.top + hr.bottom) / 2 : null,
      };
    });

    // HOVER: same measurement for the live thumb (the bar replaces the embed rail).
    await grid.locator('.z-grid-body').hover();
    await page.waitForTimeout(300);
    const hover = await grid.evaluate((g) => {
      const body = g.querySelector('.z-grid-body') || g;
      const br = body.getBoundingClientRect();
      const vt = g.querySelector('.z-scrollbar-vertical .z-scrollbar-indicator') as HTMLElement | null;
      const ht = g.querySelector('.z-scrollbar-horizontal .z-scrollbar-indicator') as HTMLElement | null;
      const vr = vt ? vt.getBoundingClientRect() : null;
      const hr = ht ? ht.getBoundingClientRect() : null;
      return {
        vCenterFromRight: vr ? br.right - (vr.left + vr.right) / 2 : null,
        hCenterFromBottom: hr ? br.bottom - (hr.top + hr.bottom) / 2 : null,
      };
    });

    expect(rest.vCenterFromRight, 'rest vertical embed rail must be present').not.toBeNull();
    expect(hover.vCenterFromRight, 'hover vertical thumb must be present').not.toBeNull();
    expect(rest.hCenterFromBottom, 'rest horizontal embed rail must be present').not.toBeNull();
    expect(hover.hCenterFromBottom, 'hover horizontal thumb must be present').not.toBeNull();

    // Rest rail and hover thumb must sit on the same cross-axis line — no lateral jump.
    expect(
      Math.abs((rest.vCenterFromRight as number) - (hover.vCenterFromRight as number)),
      `vertical: rest embed center ${rest.vCenterFromRight}px vs hover thumb ${hover.vCenterFromRight}px from right edge`,
    ).toBeLessThanOrEqual(1);
    expect(
      Math.abs((rest.hCenterFromBottom as number) - (hover.hCenterFromBottom as number)),
      `horizontal: rest embed center ${rest.hCenterFromBottom}px vs hover thumb ${hover.hCenterFromBottom}px from bottom edge`,
    ).toBeLessThanOrEqual(1);
  });
});

// Orphaned helper/mold-only component CSS — these widgets have NO lang.xml css-uri,
// so ZK never requests their per-component .css.dsp; their CSS must be in the global
// norm.css.dsp bundle or it is silently dropped and the widget renders unstyled (same
// bug class as the 2026-06-30 scrollbar orphan; sweep gap log 2026-06-30). Each guard
// loads a page that renders the widget and asserts the widget's own `.z-<comp>` root
// rule is present in a *served* stylesheet — fails red when the file is orphaned,
// passes once it is bundled. Page-agnostic after the fix (norm loads on every page),
// but we probe a page that actually renders the widget to keep the guard meaningful.
test.describe('orphaned-helper-css', () => {
  // [component class (without leading dot), preview page that renders it]
  const ORPHAN_GUARDS: [string, string][] = [
    ['z-label',    '/label.zul'],
    ['z-div',      '/label.zul'],
    ['z-span',     '/progressmeter.zul'],
    ['z-html',     '/html.zul'],
    ['z-image',    '/camera.zul'],
    ['z-imagemap', '/imagemap.zul'],
  ];

  for (const [cls, url] of ORPHAN_GUARDS) {
    test(`${cls.replace('z-', '')}-css-is-served`, async ({ page }) => {
      await page.goto(url);
      await page.waitForLoadState('networkidle');
      await page.evaluate(() => document.fonts.ready.then(() => true));
      const result = await page.evaluate((name) => {
        const domCount = document.querySelectorAll('.' + name).length;
        const re = new RegExp('^\\.' + name + '(?![\\w-])');
        let ownRuleServed = false;
        for (const ss of document.styleSheets) {
          let rules: CSSRuleList;
          try { rules = ss.cssRules; } catch (e) { continue; }
          const scan = (list: CSSRuleList): boolean => {
            for (const r of Array.from(list)) {
              const grouping = r as CSSGroupingRule;
              if (grouping.cssRules && scan(grouping.cssRules)) return true;
              const styleRule = r as CSSStyleRule;
              if (styleRule.selectorText) {
                for (const sel of styleRule.selectorText.split(',')) {
                  if (re.test(sel.trim())) return true;
                }
              }
            }
            return false;
          };
          try { if (scan(rules)) { ownRuleServed = true; break; } } catch (e) { /* ignore */ }
        }
        return { domCount, ownRuleServed };
      }, cls);

      expect(result.domCount, `${url} should render at least one .${cls} element`).toBeGreaterThan(0);
      expect(
        result.ownRuleServed,
        `.${cls} own root rule must be present in a served stylesheet — it is orphaned ` +
        `(emitted as a 1:1 .css.dsp ZK never requests). Add the source CSS to normFiles in scripts/build-css.js.`,
      ).toBe(true);
    });
  }
});
