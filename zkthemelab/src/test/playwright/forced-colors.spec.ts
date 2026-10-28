import { test, expect, devices, type Locator, type Page } from '@playwright/test';

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
    // The groupbox frame is drawn by the header (not the root), which keeps its border
    // under forced colors; the root must not add a second layer.
    const { style, width } = await page.locator('.z-groupbox-header').first().evaluate(el => {
      const cs = getComputedStyle(el);
      return { style: cs.borderTopStyle, width: cs.borderTopWidth };
    });
    expect(style).toBe('solid');
    expect(width).toBe('1px');
    const rootBorderWidths = await page.locator('.z-groupbox').first().evaluate(el => {
      const cs = getComputedStyle(el);
      return [cs.borderTopWidth, cs.borderRightWidth, cs.borderBottomWidth, cs.borderLeftWidth];
    });
    expect(rootBorderWidths).toEqual(['0px', '0px', '0px', '0px']);
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

  // Follow-up 53: without the (2b) line for a selected group, the group checkbox is a white box with a white tick.
  test('a selected Listbox group checkbox keeps its baked box (forced-color-adjust: none)', async ({ page }) => {
    await page.goto('/pv/mesh-header-footer-guard.zul', { waitUntil: 'domcontentloaded' });
    const group = page.locator('.mhf-listbox-group-select tr.z-listgroup:not(.z-listgroup-disabled)').first();
    await group.locator('span.z-listgroup-checkable').click();
    await expect(group).toHaveClass(/z-listgroup-selected/);
    const check = await group.locator('i.z-listgroup-icon.z-icon-check').evaluate(el => {
      const cs = getComputedStyle(el, '::before');
      return { fca: cs.forcedColorAdjust, bgImg: cs.backgroundImage };
    });
    expect(check.fca).toBe('none');
    expect(check.bgImg).toContain('check-white');
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
    // A bar between toolbar items is a 24px divider, covered by its own guard below.
    const bars = page.locator('.z-separator-horizontal-bar:not(.z-toolbar-content > .z-separator-horizontal-bar)');
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

  test('disabled buttons keep a GrayText border (own border or inset ::after)', async ({ page }) => {
    await page.goto('/button.zul', { waitUntil: 'domcontentloaded' });
    const disabled = page.locator('.z-button[disabled]');
    await disabled.first().waitFor({ state: 'attached', timeout: 10000 });
    const grayText = await systemColor(page, 'GrayText');
    const borders = await disabled.evaluateAll(els => els.map(el => {
      const own = getComputedStyle(el);
      return parseFloat(own.borderTopWidth) >= 1 ? own.borderTopColor : getComputedStyle(el, '::after').borderTopColor;
    }));
    expect(borders.length).toBeGreaterThanOrEqual(5);
    expect([...new Set(borders)]).toEqual([grayText]);
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

  // Follow-up 48: the text of a disabled paging resolved `--zk-color-disabled` to CanvasText, as an enabled one is. One test
  // per mold, so that a failure in the default mold does not stop the OS mold from being checked.
  test('disabled default-mold paging total is GrayText, enabled total CanvasText', async ({ page }) => {
    const grayText = await systemColor(page, 'GrayText');
    const canvasText = await systemColor(page, 'CanvasText');
    expect(grayText, 'the palette must tell the two apart').not.toBe(canvasText);

    await page.goto('/paging.zul', { waitUntil: 'domcontentloaded' });
    await page.locator('.pv-paging-disabled-os').waitFor({ state: 'attached', timeout: 10000 });
    const colours = (selector: string) => page.locator(selector).evaluateAll(els => els.map(el => getComputedStyle(el).color));
    const disabledTotal = await colours('.pv-paging-disabled-default .z-paging-text-disabled');
    const enabledTotal = await colours('.z-paging:not(.pv-paging-disabled-default) .z-paging-text');
    expect(disabledTotal.length, 'the page shows the disabled total').toBeGreaterThanOrEqual(1);
    expect(enabledTotal.length, 'the page shows an enabled total').toBeGreaterThanOrEqual(1);
    expect(disabledTotal.every(c => c === grayText), `disabled totals ${JSON.stringify(disabledTotal)} must be ${grayText}`).toBe(true);
    expect(enabledTotal.every(c => c === canvasText), `enabled totals ${JSON.stringify(enabledTotal)} must be ${canvasText}`).toBe(true);
  });

  // The OS mold disables its anchors: the page numbers read `color`, the « » chevrons are masked glyphs on the anchor's
  // ::after and read `background-color` (follow-up 48 and its chevron addendum, NS28).
  test('disabled OS-mold paging page numbers and chevrons are GrayText, enabled ones CanvasText', async ({ page }) => {
    const grayText = await systemColor(page, 'GrayText');
    const canvasText = await systemColor(page, 'CanvasText');
    expect(grayText, 'the palette must tell the two apart').not.toBe(canvasText);

    await page.goto('/paging.zul', { waitUntil: 'domcontentloaded' });
    await page.locator('.pv-paging-disabled-os').waitFor({ state: 'attached', timeout: 10000 });
    const colours = (selector: string) => page.locator(selector).evaluateAll(els => els.map(el => getComputedStyle(el).color));
    const fills = (selector: string) => page.locator(selector)
      .evaluateAll(els => els.map(el => getComputedStyle(el, '::after').backgroundColor));
    const found = {
      disabledPages: await colours('.pv-paging-disabled-os .z-paging-button[disabled]:not(.z-paging-selected)'),
      enabledPages: await colours('.z-paging-os:not(.pv-paging-disabled-os) .z-paging-button:not(.z-paging-selected)'),
      disabledChevrons: await fills('.pv-paging-disabled-os .z-paging-noborder[disabled]'),
      enabledChevrons: await fills('.z-paging-os:not(.pv-paging-disabled-os) .z-paging-noborder'),
    };
    for (const [name, values] of Object.entries(found)) {
      expect(values.length, `the page shows ${name}`).toBeGreaterThanOrEqual(1);
    }
    expect(found.disabledPages.every(c => c === grayText), `disabled OS page numbers ${JSON.stringify(found.disabledPages)} must be ${grayText}`).toBe(true);
    expect(found.enabledPages.every(c => c === canvasText), `enabled OS page numbers ${JSON.stringify(found.enabledPages)} must be ${canvasText}`).toBe(true);
    expect(found.disabledChevrons.every(c => c === grayText), `disabled OS chevrons ${JSON.stringify(found.disabledChevrons)} must be ${grayText}`).toBe(true);
    expect(found.enabledChevrons.every(c => c === canvasText), `enabled OS chevrons ${JSON.stringify(found.enabledChevrons)} must be ${canvasText}`).toBe(true);
  });

  // Follow-up 44c: a caption inside the selected accordion tab sets its own text colour.
  test('caption text in a selected accordion tab is HighlightText', async ({ page }) => {
    await page.goto('/tabbox-misc.zul', { waitUntil: 'domcontentloaded' });
    // Child chain: a descendant selector would also match a selected tab of a strip tabbox nested in a panel.
    const content = page.locator('.z-tabbox-accordion > .z-tabpanels > .z-tabpanel > .z-tab.z-tab-selected .z-caption-content');
    await content.first().waitFor({ state: 'attached', timeout: 10000 });
    const highlightText = await systemColor(page, 'HighlightText');
    const colors = await page.locator(
      '.z-tabbox-accordion > .z-tabpanels > .z-tabpanel > .z-tab.z-tab-selected .z-caption-content, .z-tabbox-accordion > .z-tabpanels > .z-tabpanel > .z-tab.z-tab-selected .z-caption-label',
    ).evaluateAll(els => els.map(el => getComputedStyle(el).color));
    expect(colors.length).toBeGreaterThanOrEqual(1);
    expect(colors.every(c => c === highlightText), `caption colours ${JSON.stringify(colors)} must be ${highlightText}`).toBe(true);
  });

  // The × of a closable accordion header is a ::before mask painted by background-color; (2d) paints it CanvasText,
  // which is ~1.9:1 on the Highlight fill of the selected header.
  test('the × of a closable accordion header has 3:1 contrast against the header fill', async ({ page }) => {
    await page.goto('/tabbox.zul', { waitUntil: 'domcontentloaded' });
    // Child chain: a descendant selector would also match the tabs of a strip tabbox nested in a panel.
    const header = '.z-tabbox-accordion > .z-tabpanels > .z-tabpanel > .z-tab';
    const glyphOf = (state: string) => page.locator(`${header}${state} > .z-tab-content > .z-tab-button > .z-tab-icon`).first();
    await glyphOf('.z-tab-selected').waitFor({ state: 'attached', timeout: 10000 });

    const contrastOf = (glyph: Locator) => glyph.evaluate(el => {
      const parse = (css: string) => css.match(/[\d.]+/g)!.map(Number);
      const over = (top: number[], bottom: number[]) => {
        const alpha = top.length > 3 ? top[3] : 1;
        return [0, 1, 2].map(i => top[i] * alpha + bottom[i] * (1 - alpha));
      };
      const luminance = (rgb: number[]) => {
        const [r, g, b] = rgb.map(c => { const v = c / 255; return v <= 0.03928 ? v / 12.92 : ((v + 0.055) / 1.055) ** 2.4; });
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
      };
      // The fill is the header's background composited over the first opaque ancestor.
      const layers: number[][] = [];
      for (let node: Element | null = el.closest('.z-tab'); node; node = node.parentElement) {
        const layer = parse(getComputedStyle(node).backgroundColor);
        if (layer.length > 3 && layer[3] === 0) continue;
        layers.push(layer);
        if (layer.length === 3 || layer[3] === 1) break;
      }
      const fill = layers.reduceRight((below, layer) => over(layer, below), [255, 255, 255]);
      const glyphColor = getComputedStyle(el, '::before').backgroundColor;
      const glyph = over(parse(glyphColor), fill);
      const [lighter, darker] = [luminance(glyph), luminance(fill)].sort((a, b) => b - a);
      return { glyphColor, contrast: (lighter + 0.05) / (darker + 0.05) };
    });

    const selected = await contrastOf(glyphOf('.z-tab-selected'));
    expect(selected.contrast, `selected header × ${selected.glyphColor}`).toBeGreaterThanOrEqual(3);
    const unselected = await contrastOf(glyphOf(':not(.z-tab-selected):not(.z-tab-disabled)'));
    expect(unselected.contrast, `unselected header × ${unselected.glyphColor}`).toBeGreaterThanOrEqual(3);
  });

  // A disabled tab does not close, so its close icon must not look or feel clickable: no hover colour,
  // no hover circle, no pointer cursor (pointer-events: none on the wrapper, inherited by the icon).
  test('the close icon of a disabled tab ignores the pointer, an enabled one does not (strip and accordion)', async ({ page }) => {
    await page.goto('/tabbox.zul', { waitUntil: 'domcontentloaded' });
    const accordionTab = '.z-tabbox-accordion > .z-tabpanels > .z-tabpanel > .z-tab';
    await page.locator(`${accordionTab}.z-tab-disabled .z-tab-button > .z-tab-icon`).first()
      .waitFor({ state: 'attached', timeout: 10000 });

    const pointerOf = (tab: Locator) => tab.locator('.z-tab-button > .z-tab-icon').first().evaluate(el => {
      const cs = getComputedStyle(el);
      return { pointerEvents: cs.pointerEvents, cursor: cs.cursor };
    });

    // The preview page has no disabled closable strip tab: disable a closable top-mold tab through its class, which is
    // all the rule keys on.
    const closableStripTabs = page.locator('.z-tabbox-top > .z-tabs .z-tab:has(> .z-tab-content > .z-tab-button)');
    const enabledStrip = await pointerOf(closableStripTabs.nth(0));
    await closableStripTabs.nth(1).evaluate(el => el.classList.add('z-tab-disabled'));
    const disabledStrip = await pointerOf(closableStripTabs.nth(1));

    const disabledAccordion = await pointerOf(page.locator(`${accordionTab}.z-tab-disabled:has(> .z-tab-content > .z-tab-button)`).first());
    const enabledAccordion = await pointerOf(page.locator(`${accordionTab}:not(.z-tab-disabled):has(> .z-tab-content > .z-tab-button)`).first());

    for (const [name, disabled] of [['strip', disabledStrip], ['accordion', disabledAccordion]] as const) {
      expect(disabled.pointerEvents, `disabled ${name} close icon pointer-events`).toBe('none');
      expect(disabled.cursor, `disabled ${name} close icon cursor`).not.toBe('pointer');
    }
    for (const [name, enabled] of [['strip', enabledStrip], ['accordion', enabledAccordion]] as const) {
      expect(enabled.pointerEvents, `enabled ${name} close icon pointer-events`).toBe('auto');
      expect(enabled.cursor, `enabled ${name} close icon cursor`).toBe('pointer');
    }
  });

  // Follow-up 44d: an invalid daterangebox shows the same double border as the other inputs. A wrapper draws it as an
  // overlay on its 1px border, so the box keeps its size (follow-up 50).
  test('invalid daterangebox shows the double border', async ({ page }) => {
    await page.goto('/pv/daterangebox-content.zul', { waitUntil: 'domcontentloaded' });
    const invalid = page.locator('.z-daterangebox.z-daterangebox-invalid');
    await expect(invalid).toHaveCount(2);   // the class arrives through an AuInvoke after mount (ZK-6133 workaround)
    const borders = await invalid.evaluateAll(els => els.map(el => {
      const cs = getComputedStyle(el, '::before');
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

  // Follow-up 47: a real constraint failure puts z-combobox-invalid / z-bandbox-invalid on the <input>,
  // not on the wrapper that the sclass-based preview pages style. Driven the way a user does it.
  test('a real invalid combobox and bandbox show the double border (one box, seam open)', async ({ page }) => {
    await page.goto('/pv/input-invalid-guard.zul', { waitUntil: 'networkidle' });
    for (const widget of ['combobox', 'bandbox']) {
      const input = page.locator(`.iig-single .z-${widget}-input`);
      await input.click();
      await page.keyboard.type('a');
      await page.keyboard.press('Backspace');
      await page.locator('body').click({ position: { x: 600, y: 400 } });
      await expect(input).toHaveClass(new RegExp(`z-${widget}-invalid`));
    }
    const border = (selector: string, side: 'Top' | 'Left' | 'Right', pseudo: '::before' | null = null) => page.locator(selector).evaluate((el, [s, p]) => {
      const cs = getComputedStyle(el, p);
      return `${cs[`border${s}Style` as 'borderTopStyle']} ${cs[`border${s}Width` as 'borderTopWidth']}`;
    }, [side, pseudo] as const);

    expect(await border('.iig-single .z-bandbox', 'Top', '::before'), 'bandbox box (overlay)').toBe('double 3px');
    expect(await border('.iig-single .z-combobox-input', 'Top'), 'combobox input top').toBe('double 3px');
    expect(await border('.iig-single .z-combobox-button', 'Top'), 'combobox button top').toBe('double 3px');
    expect(await border('.iig-single .z-combobox-input', 'Right'), 'seam, input side').toMatch(/^none /);
    expect(await border('.iig-single .z-combobox-button', 'Left'), 'seam, button side').toMatch(/^none /);
    expect(await border('.iig-single .z-combobox-button', 'Right'), 'combobox button right').toBe('double 3px');
  });

  // Follow-up 55: the inputgroup border shorthand must not take the double border from an invalid input in the group.
  test('a real invalid input in an inputgroup shows the double border', async ({ page }) => {
    await page.goto('/pv/input-invalid-guard.zul', { waitUntil: 'networkidle' });
    for (const widget of ['textbox', 'intbox', 'longbox', 'doublebox', 'decimalbox', 'combobox']) {
      const input = page.locator(widget === 'combobox' ? '.iig-group-combobox .z-combobox-input' : `.iig-group-${widget} input.z-${widget}`);
      await input.click();
      await page.keyboard.type('1');
      await page.keyboard.press('Backspace');
      await page.locator('body').click({ position: { x: 600, y: 700 } });
      await expect(input).toHaveClass(/z-\w+-invalid/);
      const border = await input.evaluate(el => {
        const cs = getComputedStyle(el);
        return `${cs.borderTopStyle} ${cs.borderTopWidth}`;
      });
      expect(border, `${widget} in an inputgroup`).toBe('double 3px');
    }
  });

  // Follow-up 50: the double border of (1c) must not make an invalid input larger than the valid one. The class goes where
  // ZK puts it: on the control, or on the <input> inside the wrapper; the daterangebox takes it on its root.
  // Each input is compared with its own valid state: the heights differ (spinner 43px, compact timebox 37px, against 40px or 32px).
  const INVALID_INPUTS = ['intbox', 'decimalbox', 'doublebox', 'longbox', 'textbox', 'passwordbox', 'datebox', 'timebox',
    'timepicker', 'spinner', 'doublespinner', 'bandbox', 'daterangebox', 'combobox'];
  const sizeOf = (page: Page, name: string) => page.locator(`.isg-${name}`).first().evaluate(root => {
    const boxes = { root, input: root.querySelector('.z-combobox-input'), button: root.querySelector('.z-combobox-button') };
    return Object.fromEntries(Object.entries(boxes).filter(([, el]) => el).map(([key, el]) => {
      const { width, height } = el!.getBoundingClientRect();
      return [key, { width, height }];
    }));
  });
  for (const name of INVALID_INPUTS) {
    for (const density of ['default', 'compact', 'tablet']) {
      test(`an invalid ${name} is as large as the valid one in ${density} density${density === 'tablet' ? ' @tablet' : ''}`, async ({ page }) => {
        await page.goto('/pv/invalid-size-guard.zul', { waitUntil: 'networkidle' });
        await page.evaluate(() => document.fonts.ready.then(() => true));
        if (density === 'compact') await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
        const valid = await sizeOf(page, name);
        const invalidClass = `z-${name === 'passwordbox' ? 'textbox' : name}-invalid`;
        await page.locator(`.isg-${name}`).first().evaluate((root, cls) => {
          const host = root.matches('input') || root.matches('.z-daterangebox') ? root : root.querySelector('input')!;
          host.classList.add(cls);
        }, invalidClass);
        const doubled = await page.locator(`.isg-${name}`).first().evaluate(root => {
          const bordered = root.querySelector('.z-combobox-input') ?? root;
          const own = getComputedStyle(bordered);
          return own.borderTopStyle === 'double' ? 'double' : getComputedStyle(bordered, '::before').borderTopStyle;
        });
        expect(doubled, `the invalid ${name} shows the double border, so the size check is not vacuous`).toBe('double');
        expect(await sizeOf(page, name)).toEqual(valid);
      });
    }
  }

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

  // Follow-up 45 F1 (D21 = B). Fixture: web/pv/separator-toolbar-guard.zul
  const SEPARATOR_TOOLBAR_FIXTURE = '/pv/separator-toolbar-guard.zul';
  async function openSeparatorToolbarFixture(page: Page) {
    await page.goto(SEPARATOR_TOOLBAR_FIXTURE, { waitUntil: 'domcontentloaded' });
    await page.locator('.gs-outside-row').waitFor();
    await page.evaluate(() => document.fonts.ready);
  }

  test('a bar between toolbar items is a CanvasText divider of 1px x 24px, in both toolbar orientations', async ({ page }) => {
    await openSeparatorToolbarFixture(page);
    const canvasText = await systemColor(page, 'CanvasText');
    const expected = [
      { wrapper: 'gs-horizontal-bar', width: 1, height: 24 },
      { wrapper: 'gs-vertical-bar', width: 1, height: 24 },
      { wrapper: 'gs-toolbar-vertical', width: 24, height: 1 },
      { wrapper: 'gs-vertical-toolbar-vertical-bar', width: 24, height: 1 },
    ];
    for (const { wrapper, width, height } of expected) {
      const measured = await page.locator(`.${wrapper} .z-separator`).evaluate(el => {
        const box = el.getBoundingClientRect();
        return { width: box.width, height: box.height, background: getComputedStyle(el).backgroundColor };
      });
      expect(measured, wrapper).toEqual({ width, height, background: canvasText });
    }
  });

  test('a bar between toolbar items draws no line through the toolbar row', async ({ page }) => {
    await openSeparatorToolbarFixture(page);
    for (const wrapper of ['gs-horizontal-bar', 'gs-toolbar-vertical']) {
      const content = await page.locator(`.${wrapper} .z-separator-horizontal-bar`)
        .evaluate(el => getComputedStyle(el, '::before').content);
      expect(content, `${wrapper}: the full-width pseudo-element line of a bar outside a toolbar`).toBe('none');
    }
  });

  test('a bar in a panel-mold toolbar paints no CanvasText block, as in normal mode', async ({ page }) => {
    await openSeparatorToolbarFixture(page);
    const background = await page.locator('.gs-panel .z-separator').evaluate(el => getComputedStyle(el).backgroundColor);
    expect(background, 'a panel-mold bar is transparent').toBe('rgba(0, 0, 0, 0)');
  });

  test('a vertical bar outside a toolbar is one continuous line, not ticks at its ends', async ({ page }) => {
    await openSeparatorToolbarFixture(page);
    // The `&nbsp;` in the box gets a text backplate under forced colours; it used to cover the middle of the line.
    const bar = page.locator('.gs-outside-row .z-separator');
    const height = (await bar.boundingBox())!.height;
    const inkRows = await darkInkRows(page, await bar.screenshot());
    expect(inkRows, `the ${height}px bar must paint ink on every row`).toBe(height);
  });
});
