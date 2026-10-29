import { test, expect, Page, Locator } from '@playwright/test';

// The utilities added in the utility-class gap pass (P1, the Bootstrap / Lumo subset),
// each checked ON A ZK WIDGET in the preview pages, not on a bare element. Two layers per class:
//   1. the computed value is what the class declares (so a component-layer rule did not win), and
//   2. the effect a developer wants actually happens — a click passes through, a row reorders,
//      a skip link appears on focus — because a correct computed value can still do nothing.
// Widgets are located by a `pv-*` marker class added next to the utility in sclass.

async function open(page: Page, url: string) {
  const resp = await page.goto(url, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
}

function css(loc: Locator, prop: string): Promise<string> {
  return loc.first().evaluate((n, p) => getComputedStyle(n).getPropertyValue(p), prop);
}

// What `value` (usually a var(--zk-*)) computes to for `prop`, via a throwaway element.
function resolve(page: Page, prop: string, value: string): Promise<string> {
  return page.evaluate(([p, v]) => {
    const d = document.createElement('div');
    d.style.setProperty(p, v);
    document.body.appendChild(d);
    const r = getComputedStyle(d).getPropertyValue(p);
    d.remove();
    return r;
  }, [prop, value]);
}

async function box(loc: Locator) {
  const b = await loc.first().boundingBox();
  expect(b).not.toBeNull();
  return b!;
}

test.describe('opacity', () => {
  test('scale and widgets', async ({ page }) => {
    await open(page, '/utility/opacity.zul');
    for (const [cls, v] of [['0', '0'], ['25', '0.25'], ['50', '0.5'], ['75', '0.75'], ['100', '1']]) {
      expect(await css(page.locator(`.z-button.pv-opacity-${cls}`), 'opacity')).toBe(v);
    }
    expect(await css(page.locator('img.pv-opacity-image'), 'opacity')).toBe('0.5');
    expect(await css(page.locator('input.z-textbox.pv-opacity-textbox'), 'opacity')).toBe('0.75');
    expect(await css(page.locator('.z-label.pv-opacity-label'), 'opacity')).toBe('0.5');
    expect(await css(page.locator('.z-groupbox.pv-opacity-groupbox'), 'opacity')).toBe('0.5');
  });
});

test.describe('visibility', () => {
  test('.z-invisible keeps the space, .z-d-none does not', async ({ page }) => {
    await open(page, '/utility/visibility.zul');
    const hidden = page.locator('.z-button.pv-invisible');
    expect(await css(hidden, 'visibility')).toBe('hidden');
    await expect(hidden).toBeHidden();
    expect((await box(hidden)).width).toBeGreaterThan(0);
    const keptX = (await box(page.locator('.pv-after-invisible'))).x;
    const movedX = (await box(page.locator('.pv-after-dnone'))).x;
    expect(keptX).toBeGreaterThan(movedX + 20);
  });

  test('.z-visible child of an .z-invisible parent shows', async ({ page }) => {
    await open(page, '/utility/visibility.zul');
    await expect(page.locator('.pv-invisible-parent .z-textbox')).toBeHidden();
    await expect(page.locator('.z-button.pv-visible-child')).toBeVisible();
  });
});

test.describe('interactions', () => {
  async function clickCentre(page: Page, loc: Locator) {
    const b = await box(loc);
    await page.mouse.click(b.x + b.width / 2, b.y + b.height / 2);
  }
  // Each widget's server-side listener shows a notification naming it, so "the click had an
  // effect" means the round trip to the server happened, not just a DOM event. A new ZK
  // notification closes the previous one, so the DOM at the end cannot prove a notification
  // never appeared; record every one that is ever added instead.
  async function recordNotifications(page: Page) {
    await page.evaluate(() => {
      const seen: string[] = (window as any).pvNotifications = [];
      new MutationObserver(records => records.forEach(r => r.addedNodes.forEach(n => {
        if (n instanceof HTMLElement && n.classList.contains('z-notification')) seen.push(n.textContent ?? '');
      }))).observe(document.body, { childList: true, subtree: true });
    });
  }
  const seenNotifications = (page: Page): Promise<string[]> => page.evaluate(() => (window as any).pvNotifications);
  const notification = (page: Page, text: string) =>
    page.locator('.z-notification', { hasText: text });

  test('.z-pointer-none lets the click through; .z-pointer-auto opts back in', async ({ page }) => {
    await open(page, '/utility/interactions.zul');
    await recordNotifications(page);
    await clickCentre(page, page.locator('.pv-click-normal'));
    await expect(notification(page, 'Clicked: no class')).toBeVisible();

    const none = page.locator('.z-button.pv-click-none');
    expect(await css(none, 'pointer-events')).toBe('none');
    await clickCentre(page, none);
    await clickCentre(page, page.locator('.pv-check-none'));
    expect(await page.locator('.pv-check-none input').isChecked()).toBe(false);

    // The positive case below is sent after the two blocked clicks; once its notification is
    // back, any notification the blocked clicks had triggered would have arrived too.
    await clickCentre(page, page.locator('.z-button.pv-click-auto'));
    await expect(notification(page, 'Clicked: z-pointer-auto')).toBeVisible();
    const seen = await seenNotifications(page);
    expect(seen.some(s => s.includes('Clicked: no class'))).toBe(true);
    expect(seen.filter(s => s.includes('z-pointer-none'))).toEqual([]);
  });

  // Select by dragging the real mouse across `loc`'s text: a programmatic Range ignores the limits
  // a user hits (text inside a native <button> cannot be drag-selected even with user-select: auto).
  async function dragSelect(page: Page, loc: Locator, rightToLeft = false): Promise<string> {
    await page.evaluate(() => getSelection()!.removeAllRanges());
    const r = await loc.first().evaluate(n => {
      const range = document.createRange();
      range.selectNodeContents(n);
      const b = range.getBoundingClientRect();
      return { x1: b.left + 1, x2: b.right - 1, y: b.top + b.height / 2 };
    });
    const [from, to] = rightToLeft ? [r.x2, r.x1] : [r.x1, r.x2];
    await page.mouse.move(from, r.y);
    await page.mouse.down();
    await page.mouse.move(to, r.y, { steps: 10 });
    await page.mouse.up();
    return page.evaluate(() => getSelection()!.toString());
  }

  test('.z-user-select-all / none', async ({ page }) => {
    await open(page, '/utility/interactions.zul');
    const all = page.locator('.z-label.pv-select-all');
    expect(await css(all, 'user-select')).toBe('all');
    await clickCentre(page, all);
    expect(await page.evaluate(() => getSelection()!.toString())).toBe('ORD-2026-000417');

    const none = page.locator('.z-label.pv-select-none');
    expect(await css(none, 'user-select')).toBe('none');
    // Start on the selectable part: a drag that starts on user-select: none text selects nothing.
    const copied = await dragSelect(page, page.locator('.pv-select-none-host > div').first(), true);
    expect(copied).toContain('copy only this part');
    expect(copied).not.toContain('Step 3:');
  });

  test('.z-user-select-auto', async ({ page }) => {
    await open(page, '/utility/interactions.zul');
    // A Grid column header is user-select: none by default.
    expect(await dragSelect(page, page.locator('.z-column.pv-select-default .z-column-content'))).toBe('');
    const auto = page.locator('.z-column.pv-select-auto');
    expect(await css(auto, 'user-select')).toBe('auto');
    expect(await dragSelect(page, auto.locator('.z-column-content'))).toBe('Copyable header');
  });
});

test.describe('layout', () => {
  test('align-self on Button and Textbox', async ({ page }) => {
    await open(page, '/utility/layout.zul');
    const row = await page.locator('.pv-self-row').first().evaluate(n => {
      const r = n.getBoundingClientRect(), s = getComputedStyle(n);
      return { top: r.top + parseFloat(s.paddingTop) + parseFloat(s.borderTopWidth),
        bottom: r.bottom - parseFloat(s.paddingBottom) - parseFloat(s.borderBottomWidth) };
    });
    const end = await box(page.locator('.pv-self-end'));
    expect(Math.abs(end.y + end.height - row.bottom)).toBeLessThan(1.5);
    const center = await box(page.locator('.pv-self-center'));
    expect(Math.abs(center.y + center.height / 2 - (row.top + row.bottom) / 2)).toBeLessThan(1.5);
    const stretch = await box(page.locator('input.pv-self-stretch'));
    expect(Math.abs(stretch.height - (row.bottom - row.top))).toBeLessThan(1.5);
  });

  test('align-content-between pushes wrapped rows apart', async ({ page }) => {
    await open(page, '/utility/layout.zul');
    const c = page.locator('.pv-align-content');
    expect(await css(c, 'align-content')).toBe('space-between');
    const r = await c.first().evaluate(n => {
      const kids = Array.from(n.children) as HTMLElement[];
      const cb = n.getBoundingClientRect(), s = getComputedStyle(n);
      return { firstTop: kids[0].getBoundingClientRect().top - cb.top - parseFloat(s.paddingTop) - parseFloat(s.borderTopWidth),
        lastGap: cb.bottom - parseFloat(s.paddingBottom) - parseFloat(s.borderBottomWidth) - kids[kids.length - 1].getBoundingClientRect().bottom };
    });
    expect(Math.abs(r.firstTop)).toBeLessThan(1.5);
    expect(Math.abs(r.lastGap)).toBeLessThan(1.5);
  });

  test('order renders C B A', async ({ page }) => {
    await open(page, '/utility/layout.zul');
    const a = (await box(page.locator('.pv-order-a'))).x;
    const b = (await box(page.locator('.pv-order-b'))).x;
    const c = (await box(page.locator('.pv-order-c'))).x;
    expect(c).toBeLessThan(b);
    expect(b).toBeLessThan(a);
  });

  test('max-width / max-height cap an Image', async ({ page }) => {
    await open(page, '/utility/layout.zul');
    const wBox = await page.locator('.pv-maxw-box').first().evaluate(n => n.clientWidth);
    const w = (await box(page.locator('img.pv-maxw-image'))).width;
    expect(w).toBeLessThan(400);
    expect(w).toBeLessThanOrEqual(wBox);
    const hBox = await page.locator('.pv-maxh-box').first().evaluate(n => n.clientHeight);
    const h = (await box(page.locator('img.pv-maxh-image'))).height;
    expect(h).toBeLessThan(300);
    expect(h).toBeLessThanOrEqual(hBox);
  });

  test('viewport sizing', async ({ page }) => {
    await open(page, '/utility/layout.zul');
    const vp = await page.evaluate(() => ({ w: innerWidth, h: innerHeight }));
    const v = await box(page.locator('.pv-viewport'));
    expect(Math.round(v.width)).toBe(vp.w);
    expect(Math.round(v.height)).toBe(vp.h);
    const m = await page.locator('.pv-min-viewport').first().evaluate(n => ({ w: n.offsetWidth, h: n.offsetHeight }));
    expect(m.w).toBe(vp.w);
    expect(m.h).toBe(vp.h);
  });

  test('visually-hidden-focusable on A and Button appears on focus', async ({ page }) => {
    await open(page, '/utility/layout.zul');
    for (const sel of ['a.pv-skip-link', '.z-button.pv-skip-button']) {
      const loc = page.locator(sel);
      expect((await box(loc)).width).toBeLessThanOrEqual(1);
      await loc.first().focus();
      expect((await box(loc)).width).toBeGreaterThan(20);
      await loc.first().blur();
      expect((await box(loc)).width).toBeLessThanOrEqual(1);
    }
  });
});

test.describe('typography', () => {
  test('text decoration and monospace', async ({ page }) => {
    await open(page, '/utility/typography.zul');
    expect(await css(page.locator('.z-label.pv-underline'), 'text-decoration-line')).toBe('underline');
    expect(await css(page.locator('.z-label.pv-line-through'), 'text-decoration-line')).toBe('line-through');
    // A Marble link is underlined only on hover; the class has to beat that state rule.
    const link = page.locator('a.pv-decoration-none');
    await link.hover();
    expect(await css(link, 'text-decoration-line')).toBe('none');
    const mono = await resolve(page, 'font-family', 'var(--zk-typescale-mono-family)');
    expect(await css(page.locator('.z-label.pv-mono-label'), 'font-family')).toBe(mono);
    expect(await css(page.locator('input.pv-mono-textbox'), 'font-family')).toBe(mono);
  });

  test('wrap, break and white-space', async ({ page }) => {
    await open(page, '/utility/typography.zul');
    const wrap = page.locator('.z-button.pv-button-wrap');
    expect(await css(wrap, 'white-space')).toBe('normal');
    expect((await box(wrap)).height).toBeGreaterThan((await box(page.locator('.pv-button-nowrap'))).height + 8);

    const fits = await page.locator('.pv-break-box').first().evaluate(n => n.scrollWidth <= n.clientWidth);
    expect(fits).toBe(true);

    const lineHeight = (sel: string) => page.locator(sel).first()
      .evaluate(n => n.getBoundingClientRect().height / parseFloat(getComputedStyle(n).lineHeight));
    expect(await css(page.locator('.pv-pre-line'), 'white-space')).toBe('pre-line');
    expect(await lineHeight('.pv-pre-line')).toBeGreaterThan(2.5);
    expect(await css(page.locator('.pv-pre-wrap'), 'white-space')).toBe('pre-wrap');
    expect(await lineHeight('.pv-pre-wrap')).toBeGreaterThan(1.5);
    expect(await css(page.locator('.pv-pre'), 'white-space')).toBe('pre');
    expect(await lineHeight('.pv-pre')).toBeGreaterThan(1.5);
  });
});

test.describe('borders', () => {
  test('colour, style and width on Div, Textbox and Button', async ({ page }) => {
    await open(page, '/utility/borders.zul');
    const color = (t: string) => resolve(page, 'color', `var(${t})`);
    expect(await css(page.locator('.pv-border-primary'), 'border-top-color')).toBe(await color('--zk-color-primary'));
    expect(await css(page.locator('.pv-border-outline'), 'border-top-color')).toBe(await color('--zk-color-outline'));
    expect(await css(page.locator('input.pv-border-textbox'), 'border-top-color')).toBe(await color('--zk-color-error'));

    const dashed = page.locator('.pv-border-dashed');
    expect(await css(dashed, 'border-top-style')).toBe('dashed');
    expect(await css(dashed, 'border-top-width')).toBe('2px');
    expect(await css(dashed, 'border-top-color')).toBe(await color('--zk-color-primary'));
    expect(await css(page.locator('.pv-border-dotted'), 'border-top-style')).toBe('dotted');

    const accent = page.locator('.pv-border-accent');
    expect(await css(accent, 'border-inline-start-width')).toBe('2px');
    expect(await css(accent, 'border-inline-start-color')).toBe(await color('--zk-color-primary'));
    expect(await css(accent, 'border-top-style')).toBe('none');

    const button = page.locator('.z-button.pv-border-button');
    expect(await css(button, 'border-top-color')).toBe(await color('--zk-color-error'));
    expect(await css(button, 'border-top-width')).toBe('2px');
  });
});

test.describe('colors', () => {
  test('surface container steps and on-container text', async ({ page }) => {
    await open(page, '/utility/colors.zul');
    const bg = (t: string) => resolve(page, 'background-color', `var(${t})`);
    expect(await css(page.locator('.pv-bg-lowest'), 'background-color')).toBe(await bg('--zk-color-surface-container-lowest'));
    expect(await css(page.locator('.pv-bg-high'), 'background-color')).toBe(await bg('--zk-color-surface-container-high'));
    expect(await css(page.locator('.pv-bg-highest'), 'background-color')).toBe(await bg('--zk-color-surface-container-highest'));
    expect(await css(page.locator('.z-vlayout.pv-bg-vlayout'), 'background-color')).toBe(await bg('--zk-color-surface-container-high'));

    const fg = (t: string) => resolve(page, 'color', `var(${t})`);
    for (const role of ['primary', 'secondary', 'error', 'success', 'warning']) {
      expect(await css(page.locator(`.z-label.pv-on-${role}-container`), 'color'))
        .toBe(await fg(`--zk-color-on-${role}-container`));
    }
    // Why the class exists: the container's colour does not reach a Label.
    expect(await css(page.locator('.z-label.pv-container-plain'), 'color'))
      .not.toBe(await fg('--zk-color-on-primary-container'));
  });

  test('inverse surface pairs with inverse text', async ({ page }) => {
    await open(page, '/utility/colors.zul');
    const surface = page.locator('.pv-bg-inverse');
    expect(await css(surface, 'background-color')).toBe(await resolve(page, 'background-color', 'var(--zk-color-inverse-surface)'));
    expect(await css(surface, 'color')).toBe(await resolve(page, 'color', 'var(--zk-color-inverse-on-surface)'));
    // The demo's point: the inverse text reads on the dark surface (WCAG AA, 4.5:1).
    const ratio = await page.evaluate(() => {
      const rgba = (el: Element, p: string) => getComputedStyle(el).getPropertyValue(p).match(/[\d.]+/g)!.map(Number);
      // The background the text is painted on: the nearest ancestor with an opaque one.
      const paintedBg = (el: Element | null): number[] => {
        for (; el; el = el.parentElement) {
          const c = rgba(el, 'background-color');
          if (c.length < 4 || c[3] === 1) return c;
        }
        return [255, 255, 255];
      };
      const lum = ([r, g, b]: number[]) => {
        const c = [r, g, b].map(v => { v /= 255; return v <= 0.03928 ? v / 12.92 : ((v + 0.055) / 1.055) ** 2.4; });
        return 0.2126 * c[0] + 0.7152 * c[1] + 0.0722 * c[2];
      };
      const text = document.querySelector('.z-label.pv-text-inverse')!;
      const fg = lum(rgba(text, 'color'));
      const bg = lum(paintedBg(text));
      return (Math.max(fg, bg) + 0.05) / (Math.min(fg, bg) + 0.05);
    });
    expect(ratio).toBeGreaterThanOrEqual(4.5);
  });
});

test.describe('elevation', () => {
  test('levels 4 and 5 on Div, Window and Button', async ({ page }) => {
    await open(page, '/utility/elevation.zul');
    const shadow = (t: string) => resolve(page, 'box-shadow', `var(${t})`);
    const e4 = await shadow('--zk-elevation-4');
    const e5 = await shadow('--zk-elevation-5');
    expect(e4).not.toBe(e5);
    expect(await css(page.locator('.pv-elevation-4'), 'box-shadow')).toBe(e4);
    expect(await css(page.locator('.pv-elevation-5'), 'box-shadow')).toBe(e5);
    expect(await css(page.locator('.z-window.pv-elevation-window'), 'box-shadow')).toBe(e5);
    expect(await css(page.locator('.z-button.pv-elevation-button'), 'box-shadow')).toBe(e4);
  });
});
