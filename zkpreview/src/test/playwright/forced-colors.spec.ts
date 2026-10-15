import { test, expect, devices, type Page } from '@playwright/test';

// Regression guard for the forced-colors / Windows High-Contrast a11y layer
// (GAP 5 — see doc/spec/forced-colors.md). Runs under the `forced-colors`
// project, whose `use.forcedColors: 'active'` makes Chromium emulate WHCM:
// the OS palette collapses to system colors and every box-shadow is stripped.
//
// Under emulation the system colors resolve to a fixed palette, e.g.
//   Highlight ≈ rgba(5, 0, 73, 0.8)   HighlightText = rgb(255,255,255)
//   Canvas    = rgb(255,255,255)       CanvasText    = rgb(0,0,0)
// so the assertions below check the *effect* of each guard (an outline appears,
// a selected row's fill changes, forced-color-adjust opts out) rather than
// pinning exact colors — that keeps the test robust if the emulated palette
// shifts between Chromium versions.
//
// If any of these fail after a change, the central guard in
// tokens/_forced-colors.css (bundled into norm.css.dsp) has regressed — rebuild
// with `npm run build:css` and re-check that file.
//
// Requires the preview app on ${PREVIEW_URL}
//   withjdk.sh 17 mvn test exec:java@preview-app
// Run: npm run test:forced-colors   (or: playwright test --project=forced-colors)

// Resolves a CSS system colour keyword to the computed rgb() string under the emulated palette.
async function systemColor(page: Page, keyword: string): Promise<string> {
  return page.evaluate(kw => {
    const probe = document.createElement('span');
    probe.style.color = kw;
    document.body.appendChild(probe);
    const resolved = getComputedStyle(probe).color;
    probe.remove();
    return resolved;
  }, keyword);
}

// Counts the distinct rows of a PNG that hold dark ink (r, g and b all below 128).
async function darkInkRows(page: Page, png: Buffer): Promise<number> {
  return page.evaluate(async b64 => {
    const bitmap = await createImageBitmap(await (await fetch('data:image/png;base64,' + b64)).blob());
    const canvas = document.createElement('canvas');
    canvas.width = bitmap.width;
    canvas.height = bitmap.height;
    const ctx = canvas.getContext('2d')!;
    ctx.drawImage(bitmap, 0, 0);
    const { data } = ctx.getImageData(0, 0, bitmap.width, bitmap.height);
    const rows = new Set<number>();
    for (let p = 0; p < data.length; p += 4) {
      if (data[p] < 128 && data[p + 1] < 128 && data[p + 2] < 128) rows.add(Math.floor(p / 4 / bitmap.width));
    }
    return rows.size;
  }, png.toString('base64'));
}

test.describe('forced-colors (Windows High-Contrast) a11y guards', () => {
  // Emulate WHCM at runtime. This CDP-backed call (Emulation.setEmulatedMedia)
  // is the reliable path — the context-option form (use.forcedColors) does not
  // take effect for the page fixture in this runner setup.
  test.beforeEach(async ({ page }) => {
    await page.emulateMedia({ forcedColors: 'active' });
  });

  test('forced-colors is actually emulated', async ({ page }) => {
    await page.goto('/button.zul', { waitUntil: 'domcontentloaded' });
    const matches = await page.evaluate(() => matchMedia('(forced-colors: active)').matches);
    expect(matches, 'forced-colors must be active').toBe(true);
  });

  test('checkbox checked/indeterminate opt out of the OS palette (glyph stays legible)', async ({ page }) => {
    await page.goto('/checkbox.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-checkbox-mold', { timeout: 10000 });
    if (await page.locator('.z-checkbox-on > .z-checkbox-mold').count() === 0) {
      await page.locator('.z-checkbox').first().click();
    }
    const fca = await page.locator('.z-checkbox-on > .z-checkbox-mold').first()
      .evaluate(el => getComputedStyle(el).forcedColorAdjust);
    // Without the guard this reverts to the default 'auto' (box → Canvas, white check invisible).
    expect(fca).toBe('none');
  });

  test('text-input focus draws a real outline (box-shadow focus ring is stripped)', async ({ page }) => {
    await page.goto('/datebox.zul', { waitUntil: 'domcontentloaded' });
    const input = page.locator('.z-datebox-input').first();
    await input.waitFor({ state: 'visible', timeout: 10000 });
    await input.focus();
    // ZK wires the widget client-side, so the guard's outline may not be applied on the
    // very first frame after focus (the original single-shot getComputedStyle raced this,
    // reading outline-style:none on a slow/cold render). Web-first assertions auto-retry
    // until the forced-colors outline lands — the box-shadow focus ring is stripped in WHCM,
    // so a real 2px solid outline on the wrapper is the only surviving focus affordance.
    const box = page.locator('.z-datebox').first();
    await expect(box).toHaveCSS('outline-style', 'solid');
    await expect(box).toHaveCSS('outline-width', '2px');
  });

  test('selected list row uses the system selection colors (tint would otherwise vanish)', async ({ page }) => {
    await page.goto('/listbox.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-listitem', { timeout: 10000 });
    await page.locator('.z-listitem').first().click();
    await page.waitForTimeout(200);
    const sel = await page.locator('.z-listitem.z-selected, .z-listitem.z-listitem-selected').first()
      .evaluate(el => ({ bg: getComputedStyle(el).backgroundColor, color: getComputedStyle(el).color }));
    const unsel = await page.locator('.z-listitem:not(.z-selected):not(.z-listitem-selected)').first()
      .evaluate(el => getComputedStyle(el).backgroundColor).catch(() => null);
    // Selected fill must be a real (opaque-ish) color and must differ from an unselected row.
    expect(sel.bg).not.toBe('rgba(0, 0, 0, 0)');
    if (unsel) expect(sel.bg, 'selected row fill must differ from unselected').not.toBe(unsel);
    // Text must not collapse to the same value as the fill.
    expect(sel.color).not.toBe(sel.bg);
  });

  test('elevation-only surfaces gain a border (box-shadow is stripped)', async ({ page }) => {
    await page.goto('/window.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-window', { timeout: 10000 });
    const { style, width } = await page.locator('.z-window').first().evaluate(el => {
      const cs = getComputedStyle(el);
      return { style: cs.borderTopStyle, width: cs.borderTopWidth };
    });
    expect(style).toBe('solid');
    expect(width).toBe('1px');
  });

  test('buttons are delineated by a border', async ({ page }) => {
    await page.goto('/button.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-button', { timeout: 10000 });
    // A borderless button draws its border on an inset ::after (follow-up 31); an outlined one keeps its own.
    const styles = await page.locator('.z-button').first().evaluate(el =>
      [getComputedStyle(el).borderTopStyle, getComputedStyle(el, '::after').borderTopStyle]);
    expect(styles).toContain('solid');
  });

  // --- tail coverage (the 27 audited components); a representative case per bucket ---

  test('elevation surface (groupbox) gains a border', async ({ page }) => {
    await page.goto('/groupbox.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-groupbox', { timeout: 10000 });
    const { style, width } = await page.locator('.z-groupbox').first().evaluate(el => {
      const cs = getComputedStyle(el);
      return { style: cs.borderTopStyle, width: cs.borderTopWidth };
    });
    expect(style).toBe('solid');
    expect(width).toBe('1px');
  });

  test('paging current page uses the system selection color', async ({ page }) => {
    await page.goto('/paging.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-paging-selected', { timeout: 10000 });
    const { bg, color } = await page.locator('.z-paging-selected').first().evaluate(el => {
      const cs = getComputedStyle(el);
      return { bg: cs.backgroundColor, color: cs.color };
    });
    expect(bg).not.toBe('rgba(0, 0, 0, 0)');
    expect(color).not.toBe(bg);
  });

  test('calendar selected day keeps a visible filled disc (fill on ::before)', async ({ page }) => {
    await page.goto('/calendar.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-calendar-cell.z-calendar-selected', { timeout: 10000 });
    const discBg = await page.locator('.z-calendar-cell.z-calendar-selected').first()
      .evaluate(el => getComputedStyle(el, '::before').backgroundColor);
    // The disc must be a real fill (Highlight), not the stripped-transparent default.
    expect(discBg).not.toBe('rgba(0, 0, 0, 0)');
  });

  // --- masked-icon fill (reported: spinner arrows vanish under WHCM) ---
  // Lucide/SVG glyphs are drawn as `background-color: currentColor` clipped by a
  // mask. forced-colors force-maps background-color to Canvas (the page bg), not
  // the foreground, so the icon becomes an invisible same-as-background shape.
  // The guard re-points the fill at CanvasText. Assert the fill matches the
  // foreground (CanvasText, read off body text) rather than the Canvas bg.

  test('spinner up/down arrow icons stay visible (fill is foreground, not page bg)', async ({ page }) => {
    await page.goto('/spinner.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-spinner-icon', { timeout: 10000 });
    const { iconFill, canvasText, canvas } = await page.evaluate(() => {
      const icon = getComputedStyle(document.querySelector('.z-icon-angle-up')!, '::before').backgroundColor;
      const canvasText = getComputedStyle(document.body).color;       // CanvasText
      const canvas = getComputedStyle(document.body).backgroundColor; // Canvas
      return { iconFill: icon, canvasText, canvas };
    });
    // The arrow must be painted with the foreground system color, not the bg.
    expect(iconFill).toBe(canvasText);
    expect(iconFill).not.toBe(canvas);
  });

  test('combobox caret icon stays visible (unlayered fill beats the component rule)', async ({ page }) => {
    // combobox re-declares its caret fill at higher specificity in
    // @layer zk-components; the unlayered guard must still win.
    await page.goto('/combobox.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-combobox', { timeout: 10000 });
    const { iconFill, canvasText } = await page.evaluate(() => ({
      iconFill: getComputedStyle(document.querySelector('.z-combobox-button .z-icon-caret-down')!, '::before').backgroundColor,
      canvasText: getComputedStyle(document.body).color,
    }));
    expect(iconFill).toBe(canvasText);
  });

  test('selected list check keeps its baked box (icon-fill guard must not clobber it)', async ({ page }) => {
    // The selected-row check is a baked-SVG box (opted out via forced-color-adjust),
    // NOT a mask-currentColor glyph — the icon-fill guard excludes check/radio so it
    // stays a filled box, not a flat CanvasText square.
    await page.goto('/listbox.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-listitem', { timeout: 10000 });
    await page.locator('.z-listitem').first().click();
    await page.waitForTimeout(200);
    const check = await page.locator('.z-listitem.z-selected .z-icon-check, .z-listitem.z-listitem-selected .z-icon-check')
      .first().evaluate(el => {
        const cs = getComputedStyle(el, '::before');
        return { fca: cs.forcedColorAdjust, bgImg: cs.backgroundImage };
      }).catch(() => null);
    // If the checkmark column is present, it must remain a baked box (has a bg image
    // and is opted out of the OS palette), not a flat filled square.
    if (check) {
      expect(check.fca).toBe('none');
      expect(check.bgImg).not.toBe('none');
    }
  });

  test('accordion selected tab uses the system selection color', async ({ page }) => {
    await page.goto('/tabbox.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-tabbox-accordion .z-tab.z-tab-selected', { timeout: 10000 });
    const { bg, color } = await page.locator('.z-tabbox-accordion .z-tab.z-tab-selected').first().evaluate(el => {
      const cs = getComputedStyle(el);
      return { bg: cs.backgroundColor, color: cs.color };
    });
    expect(bg).not.toBe('rgba(0, 0, 0, 0)');
    expect(color).not.toBe(bg);
  });

  test('selected accordion tab chevron is painted HighlightText (readable on the Highlight fill)', async ({ page }) => {
    // The chevron is a mask painted by background-color; CanvasText on the Highlight fill is ~1.9:1.
    await page.goto('/tabbox.zul', { waitUntil: 'domcontentloaded' });
    const content = page.locator('.z-tabbox-accordion .z-tab.z-tab-selected .z-tab-content').first();
    await content.waitFor({ state: 'visible', timeout: 10000 });
    const { chevron, highlightText } = await content.evaluate(el => {
      const probe = document.createElement('span');
      probe.style.color = 'HighlightText';
      document.body.appendChild(probe);
      const highlightText = getComputedStyle(probe).color;
      probe.remove();
      return { chevron: getComputedStyle(el, '::after').backgroundColor, highlightText };
    });
    expect(chevron).toBe(highlightText);
  });

  test('horizontal separator bars paint a line (a gradient background is dropped under forced colors)', async ({ page }) => {
    await page.goto('/separator.zul', { waitUntil: 'domcontentloaded' });
    const bars = page.locator('.z-separator-horizontal-bar');
    await bars.first().waitFor({ state: 'attached', timeout: 10000 });
    await page.evaluate(() => document.fonts.ready);
    const count = await bars.count();
    expect(count, 'the page shows default and large-spacing bars').toBeGreaterThanOrEqual(2);
    for (let i = 0; i < count; i++) {
      // Read the PNG, not the computed style: the line is ink that must survive the palette swap.
      const png = (await bars.nth(i).screenshot()).toString('base64');
      const inkRows = await page.evaluate(async (b64) => {
        const bitmap = await createImageBitmap(await (await fetch('data:image/png;base64,' + b64)).blob());
        const canvas = document.createElement('canvas');
        canvas.width = bitmap.width;
        canvas.height = bitmap.height;
        const ctx = canvas.getContext('2d')!;
        ctx.drawImage(bitmap, 0, 0);
        const { data } = ctx.getImageData(0, 0, bitmap.width, bitmap.height);
        const rows = new Set<number>();
        for (let p = 0; p < data.length; p += 4) {
          if (data[p] < 128 && data[p + 1] < 128 && data[p + 2] < 128) rows.add(Math.floor(p / 4 / bitmap.width));
        }
        return rows.size;
      }, png);
      expect(inkRows, `bar #${i} must paint exactly one dark 1px row`).toBe(1);
    }
  });

  test('datebox popup gains a border (box-shadow elevation is stripped)', async ({ page }) => {
    await page.goto('/datebox.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-datebox-button', { timeout: 10000 });
    // Open a datebox popup (the "Time in popup" variant, index 3, whose popup is
    // larger than the calendar so the missing frame is the visible defect).
    await page.locator('.z-datebox-button').nth(3).click();
    const popup = page.locator('.z-datebox-popup.z-datebox-open').first();
    await popup.waitFor({ state: 'visible', timeout: 5000 });
    // In WHCM the popup's only boundary is a box-shadow elevation, which is stripped.
    // A real border must replace it so the floating surface stays delineated.
    await expect(popup).toHaveCSS('border-top-style', 'solid');
    await expect(popup).toHaveCSS('border-top-width', '1px');
  });

  test('confirmpopup gains a border and keeps its arrow (elevation + CSS-triangle beak)', async ({ page }) => {
    await page.goto('/confirmpopup.zul', { waitUntil: 'domcontentloaded' });
    const box = page.locator('.z-confirmpopup').first();
    await box.waitFor({ state: 'visible', timeout: 10000 });
    // Same (1a) gap as the datebox popup: shadow-only boundary, stripped in WHCM.
    await expect(box).toHaveCSS('border-top-style', 'solid');
    await expect(box).toHaveCSS('border-top-width', '1px');
    // The arrow is a CSS triangle (transparent sides + one filled); without
    // forced-color-adjust:none the transparent sides map to CanvasText and it
    // collapses into an opaque square. forced-color-adjust inherits to ::before/::after.
    const arrow = page.locator('.z-confirmpopup-arrow').first();
    await expect(arrow).toHaveCSS('forced-color-adjust', 'none');
  });

  test('focus ring on a SELECTED navbar item stays visible (Highlight-on-Highlight)', async ({ page }) => {
    await page.goto('/navbar.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-navbar-vertical', { timeout: 10000 });
    const navbar = page.locator('.z-navbar-vertical').first();
    await navbar.locator('.z-nav > .z-nav-content').filter({ hasText: 'Get Started' }).first().click();
    const stepOne = navbar.locator('.z-navitem-content').filter({ hasText: 'Step One' }).first();
    await stepOne.waitFor({ state: 'visible', timeout: 5000 });
    await stepOne.click();       // selects it → Highlight fill
    await page.keyboard.press(' '); // promotes focus to :focus-visible

    // In WHCM a selected item is filled with Highlight AND --zk-focus-ring is
    // Highlight, so without the (2a focus) guard the ring paints
    // Highlight-on-Highlight and vanishes. String inequality is not enough — the
    // two only differed in alpha (0.8 vs 0.78) — so compare the RGB triplets.
    const m = await page.evaluate(() => {
      const el = document.activeElement as HTMLElement;
      const cs = getComputedStyle(el);
      const rgb = (s: string) => (s.match(/[\d.]+/g) ?? []).slice(0, 3).map(Number);
      return { cls: el.className, style: cs.outlineStyle, width: cs.outlineWidth,
               ring: rgb(cs.outlineColor), fill: rgb(cs.backgroundColor) };
    });
    expect(m.cls, 'focus must be on the selected navitem link').toContain('z-navitem-content');
    expect(m.style, 'the ring must survive as a real outline').toBe('solid');
    expect(m.width).toBe('2px');
    const delta = m.ring.reduce((a, c, i) => a + Math.abs(c - (m.fill[i] ?? 0)), 0);
    expect(
      delta,
      `ring ${JSON.stringify(m.ring)} vs selection fill ${JSON.stringify(m.fill)} — ` +
        'the focus ring is invisible on the selected item',
    ).toBeGreaterThan(200);   // measured: 78 without the guard, 687 with it
  });

  test('datebox timezone selector keeps its top gap from the calendar (spacing is a margin, mode-independent)', async ({ page }) => {
    await page.goto('/datebox.zul', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('.z-datebox-button', { timeout: 10000 });
    // Open the "Timezone selector" variant (index 4): calendar + native <select>.
    await page.locator('.z-datebox-button').nth(4).click();
    const tz = page.locator('.z-datebox-popup.z-datebox-open .z-datebox-timezone').first();
    await tz.waitFor({ state: 'visible', timeout: 5000 });
    // The gap between the calendar grid and the timezone <select> is a plain layout
    // margin. WHCM strips box-shadow / remaps colors but never touches margins, so the
    // separation must be byte-identical to normal mode (12px, matching the timebox).
    await expect(tz).toHaveCSS('margin-top', '12px');
  });
  // Follow-up 31: the forced-colors button border must not change the button's box.
  const IPAD_USER_AGENT =
    'Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1';

  async function expectButtonBoxesUnchangedByForcedColors(page: Page, density: 'default' | 'compact') {
    await page.goto('/button.zul', { waitUntil: 'domcontentloaded' });
    await page.locator('.z-button').first().waitFor({ state: 'attached', timeout: 10000 });
    await page.evaluate(() => document.fonts.ready);
    if (density === 'compact') await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
    const sizes = () => page.evaluate(() => [...document.querySelectorAll('.z-button')].map(b => {
      const { width, height } = b.getBoundingClientRect();
      return `${b.className}: ${width.toFixed(2)}x${height.toFixed(2)}`;
    }));
    await page.emulateMedia({ forcedColors: 'none' });
    const normal = await sizes();
    await page.emulateMedia({ forcedColors: 'active' });
    const forced = await sizes();
    expect(normal.length, 'the button page shows many variants').toBeGreaterThanOrEqual(20);
    expect(forced).toEqual(normal);
    // The border is the affordance: every button keeps a 1px one, either its own or the inset ::after.
    const unbordered = await page.evaluate(() => [...document.querySelectorAll('.z-button')].filter(b => {
      const own = parseFloat(getComputedStyle(b).borderTopWidth);
      const inset = parseFloat(getComputedStyle(b, '::after').borderTopWidth);
      return own < 1 && !(inset >= 1);
    }).map(b => b.className));
    expect(unbordered, 'buttons without a visible border').toEqual([]);
  }

  test('button box is the same size as in normal mode (default density)', async ({ page }) => {
    await expectButtonBoxesUnchangedByForcedColors(page, 'default');
  });

  test('button box is the same size as in normal mode (compact density)', async ({ page }) => {
    await expectButtonBoxesUnchangedByForcedColors(page, 'compact');
  });

  test('button box is the same size as in normal mode (tablet)', async ({ browser }) => {
    const context = await browser.newContext({
      ...devices['Desktop Chrome'],
      viewport: { width: 834, height: 1112 },
      userAgent: IPAD_USER_AGENT,
      hasTouch: true,
      isMobile: true,
    });
    const page = await context.newPage();
    try {
      await expectButtonBoxesUnchangedByForcedColors(page, 'default');
      const tabletSheet = await page.evaluate(() =>
        [...document.styleSheets].some(sheet => sheet.href?.includes('zkmax/css/tablet.css')));
      expect(tabletSheet, 'the tablet stylesheet must be active, or this runs the desktop CSS').toBe(true);
    } finally {
      await context.close();
    }
  });

  // Follow-up 44a: listbox paging-bottom paints its glyph mask on the icon element itself.
  test('listbox paging-bottom icons are painted CanvasText (the mask sits on the element, not ::before)', async ({ page }) => {
    await page.goto('/listbox-header.zul', { waitUntil: 'domcontentloaded' });
    const icons = page.locator('.z-listbox-paging-bottom .z-paging-button:not([disabled]) .z-paging-icon');
    await icons.first().waitFor({ state: 'attached', timeout: 10000 });
    const canvasText = await systemColor(page, 'CanvasText');
    const fills = await icons.evaluateAll(els => els.map(el => getComputedStyle(el).backgroundColor));
    expect(fills.length, 'the listbox shows enabled paging icons').toBeGreaterThanOrEqual(1);
    expect(fills.every(fill => fill === canvasText), `icon fills ${JSON.stringify(fills)} must all be ${canvasText}`).toBe(true);
  });

  // Follow-up 44b: a disabled paging button must not look like an enabled one.
  // The glyph is the icon's ::before (default mold) or the icon element itself (listbox paging-bottom).
  // The OS mold draws its glyph on the button's ::after but never renders a disabled button.
  const pagingGlyphFills = (page: Page, selector: string) => page.locator(selector).evaluateAll(buttons =>
    buttons.map(button => {
      const icon = button.querySelector('[class*="z-icon-"]')!;
      return getComputedStyle(icon, button.closest('.z-listbox-paging-bottom') ? null : '::before').backgroundColor;
    }));

  test('disabled paging glyphs are GrayText, enabled ones CanvasText (default mold, listbox)', async ({ page }) => {
    const grayText = await systemColor(page, 'GrayText');
    const canvasText = await systemColor(page, 'CanvasText');
    expect(grayText, 'the palette must tell the two apart').not.toBe(canvasText);

    await page.goto('/paging.zul', { waitUntil: 'domcontentloaded' });
    await page.locator('.z-paging-button[disabled]').first().waitFor({ state: 'attached', timeout: 10000 });
    const disabled = await pagingGlyphFills(page, '.z-paging:not(.z-paging-os) .z-paging-button[disabled]');
    const enabled = await pagingGlyphFills(page, '.z-paging:not(.z-paging-os) .z-paging-button:not([disabled]):not(.z-paging-selected)');
    expect(disabled.length, 'the page shows disabled paging buttons').toBeGreaterThanOrEqual(1);
    expect(enabled.length, 'the page shows enabled paging buttons').toBeGreaterThanOrEqual(1);
    expect(disabled.every(c => c === grayText), `disabled glyphs ${JSON.stringify(disabled)} must be ${grayText}`).toBe(true);
    expect(enabled.every(c => c === canvasText), `enabled glyphs ${JSON.stringify(enabled)} must be ${canvasText}`).toBe(true);

    await page.goto('/listbox-header.zul', { waitUntil: 'domcontentloaded' });
    await page.locator('.z-listbox-paging-bottom .z-paging-button[disabled]').first().waitFor({ state: 'attached', timeout: 10000 });
    const listboxDisabled = await pagingGlyphFills(page, '.z-listbox-paging-bottom .z-paging-button[disabled]');
    expect(listboxDisabled.every(c => c === grayText), `listbox disabled glyphs ${JSON.stringify(listboxDisabled)} must be ${grayText}`).toBe(true);
  });

  // Follow-up 44c: a caption inside the selected accordion tab sets its own text colour.
  test('caption text in a selected accordion tab is HighlightText', async ({ page }) => {
    await page.goto('/tabbox-misc.zul', { waitUntil: 'domcontentloaded' });
    const content = page.locator('.z-tabbox-accordion .z-tab.z-tab-selected .z-caption-content');
    await content.first().waitFor({ state: 'attached', timeout: 10000 });
    const highlightText = await systemColor(page, 'HighlightText');
    const colors = await page.locator(
      '.z-tabbox-accordion .z-tab.z-tab-selected .z-caption-content, .z-tabbox-accordion .z-tab.z-tab-selected .z-caption-label',
    ).evaluateAll(els => els.map(el => getComputedStyle(el).color));
    expect(colors.length).toBeGreaterThanOrEqual(1);
    expect(colors.every(c => c === highlightText), `caption colours ${JSON.stringify(colors)} must be ${highlightText}`).toBe(true);
  });

  // Follow-up 44d: an invalid daterangebox shows the same double border as the other inputs.
  test('invalid daterangebox shows the double border', async ({ page }) => {
    await page.goto('/pv/daterangebox-content.zul', { waitUntil: 'domcontentloaded' });
    const invalid = page.locator('.z-daterangebox.z-daterangebox-invalid');
    await expect(invalid).toHaveCount(2);   // the class arrives through an AuInvoke after mount (ZK-6133 workaround)
    const borders = await invalid.evaluateAll(els => els.map(el => {
      const cs = getComputedStyle(el);
      return `${cs.borderTopStyle} ${cs.borderTopWidth}`;
    }));
    expect(borders).toEqual(['double 3px', 'double 3px']);
  });

  // Follow-up 44e: tbeditor SVG glyphs carry opacity 0.6 on a solid fill; the fill already maps to
  // CanvasText, so the opacity alone turns the icons grey.
  test('tbeditor toolbar glyphs are fully opaque', async ({ page }) => {
    await page.goto('/tbeditor.zul', { waitUntil: 'domcontentloaded' });
    const icons = page.locator('.z-tbeditor-button-pane svg');
    await icons.first().waitFor({ state: 'attached', timeout: 10000 });
    const opacities = await icons.evaluateAll(els => els.map(el => getComputedStyle(el).opacity));
    expect(opacities.length).toBeGreaterThanOrEqual(5);
    expect([...new Set(opacities)], 'every toolbar glyph must be fully opaque').toEqual(['1']);
  });

  // Follow-up 44f: the combobox splits one outline between input and button; the double border
  // must not close the seam, or the button draws as a second double-bordered box.
  test('invalid combobox is one double-bordered box (the input/button seam stays open)', async ({ page }) => {
    await page.goto('/pv/combobox-content.zul', { waitUntil: 'domcontentloaded' });
    const combo = page.locator('.z-combobox.z-combobox-invalid:has(.z-combobox-input:not(.z-combobox-input-full)) ').first();
    await combo.waitFor({ state: 'visible', timeout: 10000 });
    const seam = await combo.evaluate(el => {
      const input = getComputedStyle(el.querySelector('.z-combobox-input')!);
      const button = getComputedStyle(el.querySelector('.z-combobox-button')!);
      return {
        inputLeft: input.borderLeftStyle, inputRight: input.borderRightStyle,
        buttonLeft: button.borderLeftStyle, buttonRight: button.borderRightStyle,
        inputTop: input.borderTopStyle, buttonTop: button.borderTopStyle,
      };
    });
    expect(seam).toEqual({
      inputLeft: 'double', inputRight: 'none', buttonLeft: 'none', buttonRight: 'double',
      inputTop: 'double', buttonTop: 'double',
    });
  });

  // Follow-up 45 F2: a no-spacing bar is a 1px box; the line must paint inside it, not in the row below.
  test('a no-spacing horizontal separator bar paints its line inside its own 1px box', async ({ page }) => {
    await page.goto('/separator.zul', { waitUntil: 'domcontentloaded' });
    const source = page.locator('.z-separator-horizontal-bar').first();
    await source.waitFor({ state: 'attached', timeout: 10000 });
    await page.evaluate(() => document.fonts.ready);
    // The page's bars all carry a spacing; clone one and drop the inline height to get the default bar.
    await source.evaluate(el => {
      const bar = el.cloneNode(true) as HTMLElement;
      bar.removeAttribute('id');
      bar.removeAttribute('style');
      bar.setAttribute('data-probe', 'no-spacing-bar');
      el.parentElement!.appendChild(bar);
    });
    const bar = page.locator('[data-probe="no-spacing-bar"]');
    expect((await bar.boundingBox())!.height, 'the default bar is a 1px box').toBe(1);
    const inkRows = await darkInkRows(page, await bar.screenshot());
    expect(inkRows, 'the 1px box must hold exactly one dark row').toBe(1);
  });
});
