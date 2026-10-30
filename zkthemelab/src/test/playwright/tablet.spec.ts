import { test, expect, Locator, Page } from '@playwright/test';

// Runs under the `tablet` Playwright project (iPad viewport + mobile UA). ZK
// delivers zkmax/css/tablet.css.dsp as a DISABLED <link> and sets zk.mobile
// from the UA; its client runtime enables the link on DOMContentLoaded when
// zk.mobile is true. So on a mobile UA the tablet stylesheet applies; on a
// desktop UA it stays disabled (see the tablet-isolation guard in
// screenshot.spec.ts). Three assertion kinds:
//   1. the tablet stylesheet is actually enabled/loaded,
//   2. key controls meet the MD3 touch target (>= 44px),
//   3. visual baselines at tablet size.
// Tablet baselines use a `-tablet.png` filename suffix so they land at distinct
// flat paths (doc/screenshots/<comp>-tablet.png) and never collide with the
// desktop suite's `<comp>-gallery.png` — the snapshot path template has no
// {projectName} segment.

const MD3_MIN_TOUCH = 44;

async function tabletCssLoaded(page: Page): Promise<boolean> {
  // ZK enables the disabled <link> on DOMContentLoaded; once enabled the sheet
  // is fetched and appears in document.styleSheets.
  return page.evaluate(() =>
    [...document.styleSheets]
      .map(s => s.href || '')
      .some(h => h.includes('zkmax/css/tablet.css')));
}

async function heightOf(page: Page, selector: string): Promise<number> {
  const el = page.locator(selector).first();
  await el.waitFor({ state: 'visible' });
  return el.evaluate(n => parseFloat(getComputedStyle(n).height));
}

// -------------------------------------------------------
// Stylesheet enabling — the foundation of every other assertion
// -------------------------------------------------------
test.describe('tablet-stylesheet', () => {
  test('tablet.css is enabled on a mobile UA', async ({ page }) => {
    await page.goto('/button.zul');
    await page.waitForLoadState('networkidle');
    expect(await tabletCssLoaded(page)).toBe(true);
  });
});

// -------------------------------------------------------
// Touch sizing — computed heights meet the MD3 minimum
// -------------------------------------------------------
type SizeCase = { name: string; url: string; selector: string };

const sizeCases: SizeCase[] = [
  { name: 'button',   url: '/button.zul',   selector: '.z-button' },
  { name: 'textbox',  url: '/textbox.zul',  selector: '.z-textbox' },
  { name: 'combobox', url: '/combobox.zul', selector: '.z-combobox-input' },
  { name: 'datebox',  url: '/datebox.zul',  selector: '.z-datebox-input' },
];

test.describe('tablet-touch-size', () => {
  for (const { name, url, selector } of sizeCases) {
    test(`${name} meets MD3 minimum touch target`, async ({ page }) => {
      await page.goto(url);
      await page.waitForLoadState('networkidle');
      expect(await tabletCssLoaded(page)).toBe(true);
      expect(await heightOf(page, selector)).toBeGreaterThanOrEqual(MD3_MIN_TOUCH);
    });
  }
});

// -------------------------------------------------------
// No horizontal overflow — the fixed-width `pv-cols-*` state-gallery grids
// (matrix.zul) were ~1072px wide, exceeding a phone/tablet viewport. When
// document content is wider than the viewport the mobile engine widens the
// layout viewport and scales the whole page down; that same scale makes the
// layout viewport TALLER than the visible area, producing a phantom *vertical*
// scrollbar even though no element sticks out. Asserting no horizontal overflow
// guards the responsive `pv.css` fix (fluid minmax() columns) that keeps the
// galleries within the viewport width.
// -------------------------------------------------------
const overflowPages = [
  '/combobox.zul',
  '/datebox.zul',
  '/timebox.zul',
  '/spinner.zul',
  '/textbox.zul',
];

test.describe('tablet-no-horizontal-overflow', () => {
  for (const url of overflowPages) {
    test(`${url} content fits the viewport width`, async ({ page }) => {
      await page.goto(url);
      await page.waitForLoadState('networkidle');
      const { scrollW, clientW } = await page.evaluate(() => {
        const de = document.scrollingElement || document.documentElement;
        return { scrollW: de.scrollWidth, clientW: de.clientWidth };
      });
      // 1px tolerance for sub-pixel rounding.
      expect(scrollW, `${url}: document is ${scrollW - clientW}px wider than the viewport`)
        .toBeLessThanOrEqual(clientW + 1);
    });
  }
});

// -------------------------------------------------------
// On a phone the State Matrix must SCROLL horizontally, not collapse the document.
// The matrix is a single `z-grid-cols-auto` grid inside a `z-overflow-x-auto`
// container. Inputs render at their natural (min-content) width — they are not
// stretched to fill — so when the columns don't fit a narrow viewport the grid
// overflows its OWN box and scrolls, while the document never overflows (the
// scroll is contained), so no phantom vertical scrollbar. Guards that the matrix
// container stays internally scrollable and its inputs don't collapse to nothing.
// -------------------------------------------------------
test.describe('tablet-matrix-scroll-usable', () => {
  test('matrix scrolls horizontally and inputs stay usable on a phone', async ({ page }) => {
    await page.goto('/combobox.zul');
    await page.waitForLoadState('networkidle');
    await page.setViewportSize({ width: 390, height: 800 });
    await page.waitForTimeout(300);
    const r = await page.evaluate(() => {
      const grid = document.querySelector('.z-grid-cols-auto') as HTMLElement;
      const boxes = [...grid.querySelectorAll('.z-combobox')].map(c => c.getBoundingClientRect());
      // worst horizontal overlap between two comboboxes sitting on the same row
      let maxOverlap = 0;
      for (let i = 0; i < boxes.length; i++)
        for (let j = i + 1; j < boxes.length; j++) {
          const a = boxes[i], b = boxes[j];
          const sameRow = Math.min(a.bottom, b.bottom) - Math.max(a.top, b.top) > 2;
          if (sameRow)
            maxOverlap = Math.max(maxOverlap, Math.min(a.right, b.right) - Math.max(a.left, b.left));
        }
      return {
        overflow: grid.scrollWidth - grid.clientWidth,
        minComboW: Math.min(...boxes.map(b => Math.round(b.width))),
        maxOverlap: Math.round(maxOverlap),
      };
    });
    // The matrix overflows its own box (so it scrolls) instead of squeezing.
    expect(r.overflow, `matrix should overflow→scroll (got ${r.overflow}px)`).toBeGreaterThan(50);
    // Each combobox renders at its natural width — inputs are no longer stretched
    // to fill, so we assert they didn't collapse to zero (no fixed floor anymore).
    expect(r.minComboW, `narrowest combobox is ${r.minComboW}px`).toBeGreaterThan(80);
    // Columns size to their content (`--zk-col-min:min-content`), so inputs never
    // overlap their neighbours on a narrow viewport — the grid scrolls instead.
    expect(r.maxOverlap, `comboboxes overlap by ${r.maxOverlap}px`).toBeLessThanOrEqual(1);
  });
});

// -------------------------------------------------------
// On a phone the row-LABEL column must stay readable. The matrix grid is
// `auto repeat(N, minmax(0, 1fr))`; the `auto` label track sizes to its content,
// so on a narrow viewport the label cell keeps its min-content width while the
// value columns overflow and the grid scrolls. Guards that the label text stays
// readable and unclipped (textbox has long labels like "Placeholder" — the worst
// case). Row wrappers are now `z-d-contents` (header is the first one, skipped).
// -------------------------------------------------------
test.describe('tablet-matrix-label-readable', () => {
  test('the row-label column keeps its width on a phone (not collapsed to 0)', async ({ page }) => {
    await page.goto('/textbox.zul');
    await page.waitForLoadState('networkidle');
    await page.setViewportSize({ width: 390, height: 800 });
    await page.waitForTimeout(300);
    const r = await page.evaluate(() => {
      const grid = document.querySelector('.z-grid-cols-auto') as HTMLElement;
      // Each row wrapper is a `z-d-contents` element; the first is the column-header
      // row, so skip it. The label is the first child cell of each data row.
      const labels = [...grid.querySelectorAll('.z-d-contents')].slice(1)
        .map(row => row.firstElementChild as HTMLElement)
        .filter(Boolean);
      const widths = labels.map(l => Math.round(l.getBoundingClientRect().width));
      return {
        count: labels.length,
        minLabelW: widths.length ? Math.min(...widths) : -1,
        // the widest label's own scrollWidth — if the track collapsed the text
        // would overflow its box (scrollWidth > clientWidth)
        clipped: labels.some(l => l.scrollWidth - l.clientWidth > 1),
      };
    });
    expect(r.count, 'label cells found').toBeGreaterThan(0);
    // the label column must hold a usable width, not collapse toward 0
    expect(r.minLabelW, `narrowest label cell is ${r.minLabelW}px`).toBeGreaterThanOrEqual(40);
    // and no label text is clipped inside its own cell
    expect(r.clipped, 'a label cell clips its own text').toBe(false);
  });
});

// -------------------------------------------------------
// Mobile WHEEL picker — datebox/timebox swap the desktop grid calendar / stepper
// for an iOS-style scroll wheel (.z-calendar-wheel-* / .z-timebox-wheel-*,
// rendered by zkmax/touch/{datebox,timebox}-touch.ts) and make the input
// readonly. The wheel mold had no theme CSS: .z-*-wheel-list had no bounded
// height, so its <ul> (200+ <li>×40px) collapsed to ~5079px; ZK's bottom-sheet
// popup (top=innerHeight; height=cave.offsetHeight; translateY(-height)) then
// slid that 5079px off-screen (y≈-3949) → "no usable display". These guard the
// _wheel.css fix: the list stays bounded (3 visible rows) and the sheet lands on
// screen. Also guards that the mobile-readonly trigger stays interactive (the
// desktop readonly rule had pointer-events:none, making every mobile datebox
// look disabled).
// -------------------------------------------------------
// A REAL tap on the trigger (the reported interaction) — also exercises the
// pointer-events fix end-to-end. setOpen() via the widget API is avoided here: it
// skips the touch event flow that captures the current viewport height, so the
// bottom-sheet anchor can go stale on a programmatic open.
async function tapFirstTrigger(page: Page, btnSel: string): Promise<void> {
  await page.evaluate(() => window.scrollTo(0, 0));
  const btn = page.locator(btnSel).first();
  await btn.waitFor({ state: 'visible' });
  const box = await btn.boundingBox();
  await page.touchscreen.tap(box!.x + box!.width / 2, box!.y + box!.height / 2);
  await page.waitForTimeout(1000); // bottom-sheet slide is delayed + animated
}

type WheelCase = { name: string; url: string; listSel: string; btnSel: string; inputSel: string };
const wheelCases: WheelCase[] = [
  { name: 'datebox', url: '/datebox.zul',
    listSel: '.z-calendar-wheel-list', btnSel: '.z-datebox .z-datebox-button',
    inputSel: '.z-datebox .z-datebox-input' },
  { name: 'timebox', url: '/timebox.zul',
    listSel: '.z-timebox-wheel-list', btnSel: '.z-timebox .z-timebox-button',
    inputSel: '.z-timebox .z-timebox-input' },
];

// Geometry of the OPEN bottom-sheet popup (the one ZK detached to <body> and
// rendered with content — found by a non-zero rect).
async function openSheetGeometry(page: Page) {
  return page.evaluate(() => {
    const pps = [...document.querySelectorAll('[id$="-pp"]')] as HTMLElement[];
    const pp = pps.find(p => p.getBoundingClientRect().height > 0) || pps[0];
    const r = pp?.getBoundingClientRect();
    const vh = window.innerHeight;
    return {
      vh,
      top: r ? Math.round(r.top) : null,
      bottom: r ? Math.round(r.bottom) : null,
      height: r ? Math.round(r.height) : null,
      // positive = sheet bottom is BELOW the viewport (clipped OK/Cancel row)
      overshoot: r ? Math.round(r.bottom - vh) : null,
    };
  });
}

for (const { name, url, listSel, btnSel, inputSel } of wheelCases) {
  test.describe(`tablet-${name}-wheel`, () => {
    test('picker opens on-screen with a bounded scroll wheel', async ({ page }) => {
      await page.goto(url);
      await page.waitForLoadState('networkidle');
      await tapFirstTrigger(page, btnSel);

      const r = await page.evaluate((ls) => {
        const lists = [...document.querySelectorAll(ls)] as HTMLElement[];
        const pp = lists[0]?.closest('[id$="-pp"]') as HTMLElement
          || document.querySelector('.z-datebox-popup, .z-timebox-popup') as HTMLElement;
        const rect = pp?.getBoundingClientRect();
        const vh = window.innerHeight;
        return {
          listCount: lists.length,
          maxListH: lists.length ? Math.max(...lists.map(l => Math.round(l.getBoundingClientRect().height))) : -1,
          vh,
          popTop: rect ? Math.round(rect.top) : null,
          popH: rect ? Math.round(rect.height) : null,
          // how much of the sheet actually overlaps the viewport
          visibleH: rect ? Math.round(Math.min(rect.bottom, vh) - Math.max(rect.top, 0)) : null,
        };
      }, listSel);

      // the wheel actually rendered
      expect(r.listCount, 'wheel-list columns rendered').toBeGreaterThan(0);
      // each scroll column shows ~3 rows (40px li) — never the full unbounded list
      expect(r.maxListH, `tallest wheel-list is ${r.maxListH}px (should be ~120, not the full ~5000px list)`)
        .toBeLessThanOrEqual(240);
      // bounded sheet (the bug rendered a ~5000px popup), top edge on-screen (the
      // bug put it at y≈-3949), and substantially visible. ZK anchors the sheet
      // to its own jq.innerHeight(), which the emulated viewport reports ~18px
      // shorter — so assert overlap, not an exact bottom edge.
      expect(r.popH!, `popup is ${r.popH}px tall (must fit the viewport)`).toBeLessThan(r.vh);
      expect(r.popTop!, `popup top is ${r.popTop}px (off-screen above)`).toBeGreaterThanOrEqual(0);
      expect(r.popTop!, `popup top is ${r.popTop}px (below the viewport)`).toBeLessThan(r.vh);
      expect(r.visibleH!, `only ${r.visibleH}px of the ${r.popH}px sheet is on-screen`)
        .toBeGreaterThanOrEqual(Math.round(r.popH! * 0.8));
    });

    test('mobile-readonly trigger stays interactive', async ({ page }) => {
      await page.goto(url);
      await page.waitForLoadState('networkidle');
      const pe = await page.evaluate((sel) => {
        const btn = document.querySelector(sel) as HTMLElement;
        return btn ? getComputedStyle(btn).pointerEvents : 'no-button';
      }, btnSel);
      // on mobile ZK makes every input readonly; the trigger must NOT be disabled
      expect(pe, 'trigger button pointer-events').not.toBe('none');
    });

    // Tapping the INPUT must land the sheet flush to the viewport bottom, exactly
    // like tapping the ICON. ZK's CalendarPop._syncPosition anchors the sheet with
    // top = innerHeight + scrollY then re-parents via makeVParent, which inflates
    // the inline top by ~18px; the icon tap self-corrects via a second onSize sync
    // but the input tap (focus suppresses the resize) stayed at top+18 → the sheet
    // overshot below the fold, clipping the OK/Cancel row. The _wheel.css fix pins
    // the sheet to bottom:0 so BOTH tap targets land identically.
    test('tapping the input lands the sheet flush (not below the fold)', async ({ page }) => {
      await page.goto(url);
      await page.waitForLoadState('networkidle');
      await tapFirstTrigger(page, inputSel);
      const r = await openSheetGeometry(page);

      expect(r.height!, 'sheet rendered').toBeGreaterThan(0);
      // the bug parked the bottom ~18px below the viewport; allow 2px sub-pixel slack
      expect(r.overshoot!, `input-tap sheet overshoots ${r.overshoot}px below the viewport`)
        .toBeLessThanOrEqual(2);
      // and it must not be pushed up off the top either
      expect(r.top!, `sheet top is ${r.top}px`).toBeGreaterThanOrEqual(0);
      expect(r.bottom!, `sheet bottom is ${r.bottom}px (viewport ${r.vh})`)
        .toBeGreaterThanOrEqual(r.vh - 2);
    });
  });
}

// -------------------------------------------------------
// Mobile combobox BOTTOM-SHEET — on a mobile UA zkmax/touch/combo-touch.ts swaps
// Combobox.open into a bottom-sheet: `_syncPosition` anchors the popup at
// top = (ios?window.innerHeight:innerHeight) + window.scrollY, calls makeVParent()
// to detach it to <body>, then slides it up via transform:translateY(-outerHeight)
// so the bottom edge rests at the viewport bottom. Same makeVParent top-inflation
// + baked-in scrollY failure mode as the wheel picker (see above): on a scrolled
// page the sheet lands too low and clips its bottom rows. The _inputs.css fix pins
// the popup with `position:fixed; inset:auto 10px 0` so it is immune to BOTH the
// inflation and scrollY. (Selectbox is a native <select> — browser picker, no ZK
// popup; bandbox has no touch mold and keeps anchored positioning — neither is
// affected, so combobox is the only input fixed here.)
// -------------------------------------------------------
async function openComboSheet(page: Page, scrollY = 0): Promise<void> {
  await page.evaluate((y) => window.scrollTo(0, y), scrollY);
  const btn = page.locator('.z-combobox .z-combobox-button').first();
  await btn.waitFor({ state: 'visible' });
  const box = await btn.boundingBox();
  await page.touchscreen.tap(box!.x + box!.width / 2, box!.y + box!.height / 2);
  await page.waitForTimeout(600); // open is delayed (Bug ZK-2711) + animated
}

async function comboPopupPosition(page: Page): Promise<string | null> {
  return page.evaluate(() => {
    const pp = ([...document.querySelectorAll('.z-combobox-popup')] as HTMLElement[])
      .find(p => p.getBoundingClientRect().height > 0);
    return pp ? getComputedStyle(pp).position : null;
  });
}

test.describe('tablet-combobox-sheet', () => {
  test('opens as a bottom-sheet pinned flush to the viewport bottom', async ({ page }) => {
    await page.goto('/combobox.zul');
    await page.waitForLoadState('networkidle');
    await openComboSheet(page);
    const r = await openSheetGeometry(page);

    expect(r.height!, 'sheet rendered').toBeGreaterThan(0);
    // the popup must be CSS-pinned, not riding ZK's inline top
    expect(await comboPopupPosition(page), 'popup uses position:fixed').toBe('fixed');
    // bounded, fully on-screen, bottom flush to the viewport
    expect(r.height!, `sheet is ${r.height}px tall (must fit the viewport)`).toBeLessThan(r.vh);
    expect(r.top!, `sheet top is ${r.top}px (off-screen above)`).toBeGreaterThanOrEqual(0);
    expect(r.overshoot!, `sheet overshoots ${r.overshoot}px below the viewport`).toBeLessThanOrEqual(2);
    expect(r.bottom!, `sheet bottom is ${r.bottom}px (viewport ${r.vh})`)
      .toBeGreaterThanOrEqual(r.vh - 2);
  });

  test('stays flush to the bottom when the page is scrolled', async ({ page }) => {
    await page.goto('/combobox.zul');
    await page.waitForLoadState('networkidle');
    // ZK's anchor bakes window.scrollY into the inline top; position:fixed;bottom:0
    // ignores it. Open from a scrolled position and assert it still lands flush.
    await openComboSheet(page, 400);
    const r = await openSheetGeometry(page);

    expect(r.height!, 'sheet rendered').toBeGreaterThan(0);
    expect(r.overshoot!, `scrolled-page sheet overshoots ${r.overshoot}px below the viewport`)
      .toBeLessThanOrEqual(2);
    expect(r.top!, `sheet top is ${r.top}px (off-screen above)`).toBeGreaterThanOrEqual(0);
  });
});

// -------------------------------------------------------
// Visual baselines at tablet size — capture the page wrapper (.z-p-8)
// -------------------------------------------------------
// maxDiffPixelRatio: opt-in tolerance for pages with a known sub-1% render
// flake (font-load / CSS-transition settle timing). tabbox's selection-indicator
// transition produces ~176px (0.01 ratio) of transient diff; a 0.02 ceiling
// absorbs it while any real regression (far larger) still fails. Other pages keep
// zero tolerance. See memory: window/panel/tabbox/toast are the known-flaky set.
type VisualCase = { name: string; url: string; maxDiffPixelRatio?: number };

// COVERAGE RULE: every component whose rendering changes on a mobile UA must
// have a tablet baseline. The authoritative source of "what changes" is the
// tablet bundle `src/main/resources/web/zkmax/css/tablet/` — every root `.z-*`
// class targeted by a partial there maps to a page below. When a partial starts
// (or stops) styling a component, add (or remove) its page here.
//   _inputs.css    → textbox, intbox, longbox, doublebox, decimalbox,
//                    passwordbox(=textbox page), combobox, bandbox, datebox,
//                    timebox, spinner, doublespinner
//   _selection.css → checkbox, radiogroup
//   _slider.css    → slider (touch-enlarged knob)
//   _buttons.css   → button, combobutton, toolbar(.z-toolbarbutton),
//                    fileupload(.z-uploadbutton — SKIPped: non-deterministic)
//   _mesh.css      → listbox, grid, tree(.z-treecell/.z-treecol), paging
//                    (+ checkable cells, tree/group/detail toggle icons)
//   _scrollbar.css → biglistbox
//   _calendar.css  → calendar
//   _menu.css      → menubar (menu/menuitem tap rows + glyphs)
//   _tabbox.css    → tabbox (tab tap floor, tab image/icon, close, scroll arms)
//   _window.css    → window, panel
//   _feedback.css  → errorbox (close), notification (close)
// selectbox has NO tablet CSS (native <select> — OS handles touch); its entry
// below is a width-834 render that just guards it doesn't regress at tablet size.
// notification's only tablet delta is the .z-notification-close button, which the
// static gallery does not render — its entry is a surface guard; the close-button
// geometry is verified by computed-style probe, not this baseline.
const visualCases: VisualCase[] = [
  // form inputs (_inputs.css)
  { name: 'tablet-textbox',       url: '/textbox.zul' },
  { name: 'tablet-intbox',        url: '/intbox.zul' },
  { name: 'tablet-longbox',       url: '/longbox.zul' },
  { name: 'tablet-doublebox',     url: '/doublebox.zul' },
  { name: 'tablet-decimalbox',    url: '/decimalbox.zul' },
  { name: 'tablet-combobox',      url: '/combobox.zul' },
  { name: 'tablet-bandbox',       url: '/bandbox.zul' },
  { name: 'tablet-datebox',       url: '/datebox.zul' },
  { name: 'tablet-timebox',       url: '/timebox.zul' },
  { name: 'tablet-spinner',       url: '/spinner.zul' },
  { name: 'tablet-doublespinner', url: '/doublespinner.zul' },
  { name: 'tablet-calendar',      url: '/calendar.zul' },
  // selection controls (_selection.css)
  { name: 'tablet-checkbox',      url: '/checkbox.zul' },
  { name: 'tablet-radiogroup',    url: '/radiogroup.zul' },
  // buttons (_buttons.css)
  { name: 'tablet-button',        url: '/button.zul' },
  { name: 'tablet-combobutton',   url: '/combobutton.zul' },
  { name: 'tablet-toolbar',       url: '/toolbar.zul' },
  // menu (_menu.css)
  { name: 'tablet-menubar',       url: '/menubar.zul' },
  // tabbox (_tabbox.css) — known sub-1% selection-indicator flake, see type note
  { name: 'tablet-tabbox',        url: '/tabbox.zul', maxDiffPixelRatio: 0.02 },
  // mesh: data grids/lists/tree + paging (_mesh.css)
  { name: 'tablet-listbox',       url: '/listbox.zul' },
  { name: 'tablet-grid',          url: '/grid.zul' },
  { name: 'tablet-tree',          url: '/tree.zul' },
  { name: 'tablet-paging',        url: '/paging.zul' },
  { name: 'tablet-biglistbox',    url: '/biglistbox.zul' },
  // containers (_window.css)
  // window has a pre-existing ~1% timing flake (unchanged by this work — the page
  // embeds none of the touch-enlarged components); same tolerance as tabbox.
  { name: 'tablet-window',        url: '/window.zul', maxDiffPixelRatio: 0.02 },
  { name: 'tablet-panel',         url: '/panel.zul' },
  // feedback (_feedback.css)
  { name: 'tablet-errorbox',      url: '/errorbox.zul' },
  { name: 'tablet-notification',  url: '/notification.zul' },
  // slider (_slider.css — touch-enlarged knob)
  { name: 'tablet-slider',        url: '/slider.zul' },
  // no tablet CSS — width-834 regression guard only
  { name: 'tablet-selectbox',     url: '/selectbox.zul' },
  // NOTE: scrollview is deliberately NOT here. A goto-and-shoot never paints its
  // overlay scrollbar (_barPos parks it at opacity 0 at rest), so a generic case
  // would bank a baseline of the very thing it is meant to show. Its baseline is
  // cut in the tablet-scrollview-affordance block below, with the bar revealed.
];

for (const { name, url, maxDiffPixelRatio } of visualCases) {
  // The tablet gallery lands flat alongside the desktop shot, with a -tablet.png
  // suffix so it never collides with the desktop <comp>-gallery.png:
  // doc/screenshots/button-tablet.png.
  const comp = name.replace(/^tablet-/, '');
  test.describe(name, () => {
    test('gallery', async ({ page }) => {
      await page.goto(url);
      await page.waitForLoadState('networkidle');
      await page.evaluate(() => document.fonts.ready.then(() => true));
      await expect(page.locator('.z-p-8').first())
        .toHaveScreenshot(`${comp}-tablet.png`, maxDiffPixelRatio ? { maxDiffPixelRatio } : {});
    });
  });
}

// -------------------------------------------------------
// Colorbox popup dismiss on touch — ZK 10.2.1-jakarta's Colorbox.closePopup /
// onHide only call undoVParent(); they do NOT reset the inline display/position
// that openPopup set. On desktop undoVParent's style restore hides the reattached
// popup, but on the mobile (iPad/Safari) UA it does not, so the popup stays
// display:block (looks un-closed) and leaves a small `.z-palette-button` artifact.
// Theme workaround (floating-popup-in-body pattern): the OPEN popup is detached to
// <body>, so force-hiding the popup while it is RE-ATTACHED inside .z-colorbox
// only ever hides the closed popup. These guard that workaround on a touch UA.
// -------------------------------------------------------
test.describe('tablet-colorbox-dismiss', () => {
  test('outside tap closes the popup (no display:block left on the reattached popup)', async ({ page }) => {
    await page.goto('/colorbox.zul');
    await page.waitForLoadState('networkidle');

    // open must still show (the open popup is detached to <body>)
    const opened = await page.evaluate(() => {
      const w = (window as any).zk.Widget.$(document.querySelector('.z-colorbox'));
      w.openPopup();
      const pp = w.$n('pp') as HTMLElement;
      return getComputedStyle(pp).display !== 'none' && (pp.parentElement as HTMLElement).tagName === 'BODY';
    });
    expect(opened).toBe(true);

    // tap an empty/content area away from the colorbox
    await page.touchscreen.tap(500, 300);
    await page.waitForTimeout(350);

    const dismissed = await page.evaluate(() => {
      const w = (window as any).zk.Widget.$(document.querySelector('.z-colorbox'));
      return { hidden: getComputedStyle(w.$n('pp') as HTMLElement).display === 'none', open: w._open };
    });
    expect(dismissed.open).toBe(false);
    expect(dismissed.hidden).toBe(true);
  });

  test('selecting a color leaves no visible popup/palette-button artifact', async ({ page }) => {
    await page.goto('/colorbox.zul');
    await page.waitForLoadState('networkidle');

    await page.evaluate(() => {
      const w = (window as any).zk.Widget.$(document.querySelector('.z-colorbox'));
      w.openPopup();
      const pp = w.$n('pp') as HTMLElement;
      const sw = pp.querySelector('.z-colorpalette-color, [data-color]') as HTMLElement;
      if (sw) ['mousedown', 'mouseup', 'click'].forEach(t => sw.dispatchEvent(new MouseEvent(t, { bubbles: true, cancelable: true })));
    });
    await page.waitForTimeout(400);

    const visibleArtifacts = await page.evaluate(() =>
      [...document.querySelectorAll('[class*="palette-button"], .z-colorbox-popup')]
        .filter(e => {
          const r = (e as HTMLElement).getBoundingClientRect();
          return r.width > 0 && r.height > 0 && getComputedStyle(e as HTMLElement).display !== 'none';
        })
        .map(e => e.className.toString()));
    expect(visibleArtifacts).toEqual([]);
  });
});

// -------------------------------------------------------
// Scrollview — the touch path is where its styling actually lives.
// On a DESKTOP UA `bind_()` writes an inline `overflow: auto` and the browser
// scrolls natively, so the stylesheet contributes nothing visible and the desktop
// gallery shot is byte-identical with or without it. On a MOBILE UA the root stays
// `overflow: hidden` while `_move()` translates the cave, and `_addBar()` builds an
// overlay scrollbar that is the ONLY scroll affordance the user gets.
//
// These guard doc/contracts/scrollview.md M1-M6. They exist because the contract
// previously asserted only `overflow` and `background-color` — two values ZK's own
// JS and the CSS defaults already produce — so scrollview was recorded VERIFIED
// while its stylesheet was an empty placeholder file (tasks/harness/work-status.md).
// Measured against that empty stylesheet (RED re-run 2026-09-12): M1, M3, M4, M5
// and M8 fail; M2 and M6 pass on it by construction, because they assert an
// ABSENCE (no surface, no layout space) that an empty file also satisfies —
// they guard against future decoration, not against a missing stylesheet.
// -------------------------------------------------------
test.describe('tablet-scrollview-affordance', () => {
  test('the overlay scrollbar is present, proportional and inert to touch', async ({ page }) => {
    await page.goto('/scrollview.zul');
    await page.waitForLoadState('networkidle');
    expect(await tabletCssLoaded(page)).toBe(true);
    // _refresh() builds the bar from a 200ms timer fired by onSize; wait for the
    // element rather than racing a fixed sleep.
    await page.waitForSelector('.z-scrollview-scrollbar', { state: 'attached', timeout: 10000 });

    const m = await page.evaluate(() => {
      const root = document.querySelector('.z-scrollview') as HTMLElement;
      const cave = root.querySelector('.z-scrollview-content') as HTMLElement;
      const bar = root.querySelector('.z-scrollview-scrollbar') as HTMLElement;
      const ind = root.querySelector('.z-scrollview-scrollbar-indicator') as HTMLElement;
      const load = root.querySelector('.z-scrollview-load') as HTMLElement;
      const rs = getComputedStyle(root), is = getComputedStyle(ind);
      const rr = root.getBoundingClientRect(), br = bar.getBoundingClientRect(),
            ir = ind.getBoundingClientRect();
      // M5: who actually receives a touch at the thumb's centre?
      const hit = document.elementFromPoint(ir.left + ir.width / 2, ir.top + ir.height / 2);
      const alpha = (c: string) => {
        const mm = c.match(/rgba?\(([^)]+)\)/);
        if (!mm) return 1;
        const p = mm[1].split(',').map(s => parseFloat(s));
        return p.length > 3 ? p[3] : 1;
      };
      return {
        // M1
        rootH: rr.height, overflow: rs.overflow,
        scrollDelta: root.scrollHeight - root.clientHeight,
        // M2
        rootBgAlpha: alpha(rs.backgroundColor),
        rootBorders: [rs.borderTopWidth, rs.borderRightWidth, rs.borderBottomWidth, rs.borderLeftWidth]
          .map(v => parseFloat(v)),
        rootShadow: rs.boxShadow,
        // M3
        barW: br.width, barH: br.height,
        barInFrame: br.right <= rr.right + 0.5 && br.top >= rr.top - 0.5 && br.bottom <= rr.bottom + 0.5,
        // M4
        trackH: bar.clientHeight, thumbH: ir.height, caveH: cave.offsetHeight,
        thumbAlpha: alpha(is.backgroundColor),
        // M5
        hitIsBar: !!hit && !!hit.closest('.z-scrollview-scrollbar'),
        // M6
        loadArea: load ? (() => { const r = load.getBoundingClientRect(); return r.width * r.height; })() : 0,
      };
    });

    // M1 — a real scroll container holding more than it shows.
    expect(m.rootH, 'root has height').toBeGreaterThan(0);
    expect(['auto', 'hidden', 'scroll']).toContain(m.overflow);
    expect(m.scrollDelta, 'content must exceed the viewport').toBeGreaterThanOrEqual(1);

    // M2 — a viewport, not a card: no surface of its own.
    expect(m.rootBgAlpha, 'root background must be transparent').toBe(0);
    expect(m.rootBorders, 'root must have no border').toEqual([0, 0, 0, 0]);
    expect(m.rootShadow, 'root must have no elevation').toBe('none');

    // M3 — the overlay bar is present and inside the root.
    expect(m.barW, `bar width ${m.barW}px`).toBeGreaterThanOrEqual(4);
    expect(m.barH, 'bar height').toBeGreaterThan(0);
    expect(m.barInFrame, 'bar must sit inside the root bbox').toBe(true);

    // M4 — the thumb is a proportional position indicator, and visible.
    expect(m.thumbH, `thumb ${m.thumbH}px`).toBeGreaterThanOrEqual(8);
    expect(m.thumbH, 'thumb must be shorter than its track').toBeLessThanOrEqual(m.trackH - 1);
    expect(m.thumbAlpha, 'thumb must have a visible fill').toBeGreaterThan(0);
    const ratio = m.thumbH / m.trackH, expected = m.trackH / m.caveH;
    expect(Math.abs(ratio - expected),
      `thumb/track ${ratio.toFixed(3)} vs track/content ${expected.toFixed(3)}`)
      .toBeLessThanOrEqual(0.05);

    // M5 — a read-out, not a drag handle: it must not swallow the touch stream.
    expect(m.hitIsBar, 'the scrollbar must not receive the touch at its own centre').toBe(false);

    // M6 — the load hint takes no layout space until an overscroll reveals it.
    expect(m.loadArea, 'load hint must be zero-area at rest').toBe(0);
  });

  // M8 — the touch path must actually scroll. Playwright's touchscreen API has
  // no drag, so the touch sequence is driven through CDP. Found 2026-09-12: with
  // the cave's transform at `none`, doTouchStart_ parsed the computed matrix to
  // NaN and every later _move() wrote an invalid translate3d — the bar appeared,
  // the content never moved, and M1–M6 above all stayed green. Presence and
  // geometry are not the interaction; this row performs it.
  test('a touch drag moves the content', async ({ page }) => {
    await page.goto('/scrollview.zul');
    await page.waitForLoadState('networkidle');
    await page.waitForSelector('.z-scrollview-scrollbar', { state: 'attached', timeout: 10000 });

    // Scroll-axis offset of the cave, from its computed transform (`none` → 0).
    const caveY = () => page.evaluate(() => {
      const t = getComputedStyle(document.querySelector('.z-scrollview-content') as HTMLElement).transform;
      const m = t.match(/matrix\(([^)]+)\)/);
      return m ? parseFloat(m[1].split(',')[5]) : 0;
    });
    const before = await caveY();
    const [x, y] = await page.evaluate(() => {
      const r = (document.querySelector('.z-scrollview') as HTMLElement).getBoundingClientRect();
      return [r.left + r.width / 2, r.top + r.height / 2];
    });

    const cdp = await page.context().newCDPSession(page);
    const at = (yy: number) => ({ touchPoints: [{ x, y: yy, id: 1 }] });
    await cdp.send('Input.dispatchTouchEvent', { type: 'touchStart', ...at(y) });
    for (let i = 1; i <= 10; i++) {
      await cdp.send('Input.dispatchTouchEvent', { type: 'touchMove', ...at(y - i * 20) });
      await page.waitForTimeout(16);
    }
    await cdp.send('Input.dispatchTouchEvent', { type: 'touchEnd', touchPoints: [] });
    await page.waitForTimeout(500);

    const after = await caveY();
    const pos = await page.evaluate(() =>
      (window as unknown as { zk: { Widget: { $: (n: Element | null) => { _pos: number[] } } } })
        .zk.Widget.$(document.querySelector('.z-scrollview'))._pos);
    expect(pos.every(Number.isFinite), `_pos must be finite, got [${pos}]`).toBe(true);
    expect(before - after, `content must move up by >= 50px, moved ${before - after}px`)
      .toBeGreaterThanOrEqual(50);
  });

  // The only baseline that can SEE this component's styling. The desktop gallery
  // shot is identical with or without the stylesheet (the desktop path is all
  // structure), and at rest ZK parks the bar at opacity 0 — so reveal it, at the
  // resting scroll position, before shooting. Same reasoning as the pop-up-layer
  // captures: a state that only exists transiently still has to be pinned.
  test('gallery — overlay scrollbar revealed', async ({ page }) => {
    await page.goto('/scrollview.zul');
    await page.waitForLoadState('networkidle');
    await page.evaluate(() => document.fonts.ready.then(() => true));
    await page.waitForSelector('.z-scrollview-scrollbar', { state: 'attached', timeout: 10000 });
    await page.evaluate(() => {
      // _barPos(pos, dir, true) hides the bar after _addBar(); it only turns
      // opaque while a touch drag is in flight. Force the visible state WITHOUT
      // scrolling, so the shot stays deterministic (no in-flight transform).
      const bar = document.querySelector('.z-scrollview-scrollbar') as HTMLElement;
      bar.style.opacity = '1';
    });
    await expect(page.locator('.z-p-8').first()).toHaveScreenshot('scrollview-tablet.png');
  });
});

// -------------------------------------------------------
// Pop-ups that look different on a tablet. The desktop pop-up baselines (popup-gallery.spec.ts) never see them: the
// mobile UA swaps the combobox dropdown, the datebox calendar and the timebox stepper for bottom sheets and wheels,
// and zkmax tablet/*.css resizes the touch targets inside every other pop-up. The pop-up is opened by a real tap
// (page.touchscreen.tap) and shot with a PAD margin, like the desktop shots; the assertions are the properties that
// make the pop-up a tablet one. Why these tests live here and not in a new spec file: a new file needs a
// `testMatch` entry in playwright.config.ts, and a change there makes select-tests.mjs answer `full`. Each title
// names the page it opens (`x.zul:`), which is what select-tests.mjs reads to map a CSS change to these tests.
// Rationale and measurements: tasks/marble-tablet-popup-batch5-report.md.
// -------------------------------------------------------
const POPUP_PAD = 12;

// Absolute count of differing pixels tolerated per pop-up shot; measured in tasks/marble-tablet-popup-batch5-report.md.
const POPUP_MAX_DIFF = process.env.POPUP_MAX_DIFF !== undefined ? Number(process.env.POPUP_MAX_DIFF) : 10;
// Per-pixel colour distance, 0.02 as in the brand and compact projects: 0.05 let an 8% state layer on a 24px close button pass.
// POPUP_THRESHOLD overrides it for the noise measurement.
const POPUP_THRESHOLD = process.env.POPUP_THRESHOLD !== undefined ? Number(process.env.POPUP_THRESHOLD) : 0.02;

type Box = { x: number; y: number; width: number; height: number };

async function openTabletPage(page: Page, url: string): Promise<void> {
  await page.goto(url);
  await page.waitForLoadState('networkidle');
  // A cold server answers networkidle before the widgets are bound; a tap then does nothing.
  await page.waitForFunction(() => {
    const zk = (window as any).zk;
    return zk && zk.booted && !zk.loading && !zk.mounting && !zk.processing;
  });
  await page.evaluate(() => document.fonts.ready.then(() => true));
}

/** A real tap on the centre of `target`, scrolled into view first. */
async function tapCentre(page: Page, target: Locator): Promise<void> {
  await target.scrollIntoViewIfNeeded();
  const box = await target.boundingBox();
  await page.touchscreen.tap(box!.x + box!.width / 2, box!.y + box!.height / 2);
}

/** Box of an element once it has stopped moving: sheets slide up with a JS-set transform that CSS cannot disable. */
async function settledBox(page: Page, target: Locator): Promise<Box> {
  const SAME_READS = 3;
  let previous = '';
  let same = 0;
  for (let attempt = 0; attempt < 50; attempt++) {
    const box = await target.boundingBox();
    const current = JSON.stringify(box);
    same = box && current === previous ? same + 1 : 0;
    if (box && same >= SAME_READS) return box;
    previous = current;
    await page.waitForTimeout(100);
  }
  throw new Error('settledBox: the element kept moving for 5 s');
}

/**
 * The only visible element matching `selector`, shot with a POPUP_PAD margin that stops at the viewport edge (a bottom
 * sheet has no room below it). `classes` must exist in the DOM now: the shot is only worth taking if it can paint them.
 */
async function tabletPopupShot(page: Page, selector: string, classes: string[], file: string): Promise<Locator> {
  const popup = page.locator(`${selector}:visible`);
  await expect(popup, `exactly one visible ${selector}`).toHaveCount(1);
  const absent = await page.evaluate(cs => cs.filter(c => !document.querySelector(`.${CSS.escape(c)}`)), classes);
  expect(absent, 'classes absent from the DOM after opening').toEqual([]);
  const box = await settledBox(page, popup);
  const viewport = page.viewportSize()!;
  const x = Math.max(0, box.x - POPUP_PAD);
  const y = Math.max(0, box.y - POPUP_PAD);
  await expect(page).toHaveScreenshot(file, {
    clip: {
      x, y,
      width: Math.min(viewport.width, box.x + box.width + POPUP_PAD) - x,
      height: Math.min(viewport.height, box.y + box.height + POPUP_PAD) - y,
    },
    animations: 'disabled',
    maxDiffPixels: POPUP_MAX_DIFF,
    threshold: POPUP_THRESHOLD,
  });
  return popup;
}

/** The widget next to a small caption label, e.g. labelledWidget(page, 'With icon', 'z-combobox'). */
const labelledWidget = (page: Page, caption: string, widgetClass: string): Locator =>
  page.locator(`span:text-is("${caption}"), label:text-is("${caption}")`).first()
    .locator(`xpath=ancestor::div[.//*[contains(concat(' ', normalize-space(@class), ' '), ' ${widgetClass} ')]][1]`)
    .locator(`.${widgetClass}`).first();

/** Enabled, editable instance that is not in a state variant: the "Default" cell of a matrix row. */
const PLAIN_WIDGET = ':not([class*="-disabled"]):not([class*="-readonly"]):not([class*="-invalid"]):not([class*="-inplace"])';

/** The bottom sheet of ZK's touch molds: pinned to the viewport bottom, 10px in from both sides. */
async function expectBottomSheet(page: Page, popup: Locator): Promise<void> {
  const viewport = page.viewportSize()!;
  await expect(popup, 'sheet is fixed to the viewport').toHaveCSS('position', 'fixed');
  await expect(popup, 'sheet left inset').toHaveCSS('left', '10px');
  await expect(popup, 'sheet right inset').toHaveCSS('right', '10px');
  await expect(popup, 'sheet bottom inset').toHaveCSS('bottom', '0px');
  const box = await settledBox(page, popup);
  expect(box.y + box.height, 'sheet bottom edge sits on the viewport bottom').toBeGreaterThanOrEqual(viewport.height - 2);
  expect(box.y + box.height, 'sheet does not hang below the viewport').toBeLessThanOrEqual(viewport.height + 2);
  expect(box.width, 'sheet spans the viewport minus both insets').toBeCloseTo(viewport.width - 20, 0);
}

/** The sheet is as tall as the JS asked for (inline height): a max-height inherited from the desktop rule would hide the last rows. */
async function expectSheetKeepsJsHeight(popup: Locator): Promise<void> {
  const { rendered, requested } = await popup.evaluate(el => ({ rendered: el.getBoundingClientRect().height, requested: parseFloat(el.style.height) }));
  expect(requested, 'the touch mold sets an inline height').toBeGreaterThan(0);
  expect(rendered, 'sheet is as tall as its inline height').toBeCloseTo(requested, 0);
}

/** More items than the sheet shows: the last row that fits completely ends on the sheet's bottom edge, none is cut. */
async function expectLastVisibleRowEndsOnSheetBottom(popup: Locator): Promise<void> {
  const { items, rowsInside, lastInsideBottom, sheetBottom } = await popup.evaluate(el => {
    const sheet = el.getBoundingClientRect();
    const rows = Array.from(el.querySelectorAll('.z-comboitem'), row => row.getBoundingClientRect());
    const inside = rows.filter(row => row.bottom <= sheet.bottom + 1);
    return { items: rows.length, rowsInside: inside.length, lastInsideBottom: inside[inside.length - 1].bottom, sheetBottom: sheet.bottom };
  });
  expect(items, 'more items than the 5 rows of the sheet').toBeGreaterThan(5);
  expect(rowsInside, 'the sheet shows 5 complete rows').toBe(5);
  expect(Math.abs(lastInsideBottom - sheetBottom), 'the 5th row ends on the sheet bottom edge').toBeLessThanOrEqual(1);
}

/** Every box in `targets` is at least `min` px tall (and wide, when `alsoWide`): the MD3 touch target of the tablet CSS. */
async function expectTouchTargets(targets: Locator, min: number, what: string, alsoWide = false): Promise<void> {
  const boxes = await targets.evaluateAll(els => els.map(e => {
    const r = e.getBoundingClientRect();
    return { width: Math.round(r.width), height: Math.round(r.height) };
  }));
  expect(boxes.length, `${what} found`).toBeGreaterThan(0);
  for (const b of boxes) {
    expect(b.height, `${what} height`).toBeGreaterThanOrEqual(min);
    if (alsoWide) expect(b.width, `${what} width`).toBeGreaterThanOrEqual(min);
  }
}

test.describe('tablet-popup', () => {
  // ---- combobox: bottom sheet (tablet/_inputs.css) ----
  // 8 items on pv/popup-tablet-fixtures.zul: the sheet shows 5 rows (5 x 60px), so the list scrolls and a row meets the bottom edge.
  test('combobox: tap the arrow of a list with more items than the sheet shows, the 5th row ends on the sheet bottom (pv/popup-tablet-fixtures.zul)', async ({ page }) => {
    await openTabletPage(page, '/pv/popup-tablet-fixtures.zul');
    await tapCentre(page, page.locator('.z-combobox-button').first());
    await expectSheetKeepsJsHeight(page.locator('.z-combobox-popup:visible'));
    await expectLastVisibleRowEndsOnSheetBottom(page.locator('.z-combobox-popup:visible'));
    const popup = await tabletPopupShot(page, '.z-combobox-popup', ['z-combobox-popup', 'z-comboitem'], 'combobox-tablet-popup-sheet.png');
    await expectBottomSheet(page, popup);
    await expectTouchTargets(popup.locator('.z-comboitem'), 44, 'comboitem');
  });

  test('combobox: the bottom sheet of 8 items with a description (pv/popup-tablet-fixtures.zul)', async ({ page }) => {
    await openTabletPage(page, '/pv/popup-tablet-fixtures.zul');
    await tapCentre(page, page.locator('.z-combobox-button').nth(1));
    await expectSheetKeepsJsHeight(page.locator('.z-combobox-popup:visible'));
    await expectLastVisibleRowEndsOnSheetBottom(page.locator('.z-combobox-popup:visible'));
    const popup = await tabletPopupShot(page, '.z-combobox-popup', ['z-combobox-popup', 'z-comboitem', 'z-comboitem-inner'], 'combobox-tablet-popup-description.png');
    await expectBottomSheet(page, popup);
    await expectTouchTargets(popup.locator('.z-comboitem'), 44, 'comboitem with a description');
  });

  // ---- datebox and timebox: wheel in a bottom sheet (tablet/_wheel.css) ----
  test('datebox.zul: tap the button to open the date wheel', async ({ page }) => {
    await openTabletPage(page, '/datebox.zul');
    await tapCentre(page, labelledWidget(page, 'Seeded value (content-fit)', 'z-datebox').locator('.z-datebox-button'));
    const popup = await tabletPopupShot(page, '.z-datebox-popup',
      ['z-calendar-wheel-cave', 'z-calendar-wheel-list', 'z-calendar-wheel-list-selected', 'z-calendar-wheel-line', 'z-calendar-wheel-button'],
      'datebox-tablet-popup-wheel.png');
    await expectBottomSheet(page, popup);
    // a wheel, not the grid of the desktop calendar
    await expect(popup.locator('.z-calendar-wheel-cave')).toHaveCount(1);
    await expect(popup.locator('td')).toHaveCount(0);
    await expectTouchTargets(popup.locator('.z-calendar-wheel-button'), 44, 'wheel button');
  });

  test('datebox.zul: the date wheel with the time in the popup', async ({ page }) => {
    await openTabletPage(page, '/datebox.zul');
    await tapCentre(page, labelledWidget(page, 'Time in popup (yyyy/MM/dd HH:mm)', 'z-datebox').locator('.z-datebox-button'));
    // the date band and the time band meet edge to edge, with square inner corners: no notch between them
    const sheet = page.locator('.z-datebox-popup:visible');
    await expect(sheet, 'exactly one visible datebox popup').toHaveCount(1);
    const dateBand = sheet.locator('.z-calendar-wheel-date .z-calendar-wheel-line');
    const timeBand = sheet.locator('.z-timebox-wheel-time .z-timebox-wheel-line');
    await settledBox(page, sheet);
    const [dateBox, timeBox] = [(await dateBand.boundingBox())!, (await timeBand.boundingBox())!];
    expect(dateBox.x + dateBox.width, 'the date band ends where the time band starts').toBeCloseTo(timeBox.x, 0);
    for (const corner of ['top-right', 'bottom-right']) await expect(dateBand, `date band ${corner} corner`).toHaveCSS(`border-${corner}-radius`, '0px');
    for (const corner of ['top-left', 'bottom-left']) await expect(timeBand, `time band ${corner} corner`).toHaveCSS(`border-${corner}-radius`, '0px');
    const popup = await tabletPopupShot(page, '.z-datebox-popup',
      ['z-calendar-wheel-cave', 'z-calendar-wheel-list-selected', 'z-timebox-wheel-time'],
      'datebox-tablet-popup-wheel-time.png');
    await expectBottomSheet(page, popup);
    // the time wheel is nested in the date wheel
    await expect(popup.locator('.z-calendar-wheel-cave .z-timebox-wheel-time')).toHaveCount(1);
    await expectTouchTargets(popup.locator('.z-calendar-wheel-button'), 44, 'wheel button');
  });

  test('timebox.zul: tap the button to open the time wheel', async ({ page }) => {
    await openTabletPage(page, '/timebox.zul');
    await tapCentre(page, page.locator('.z-timebox .z-timebox-button').first());
    const popup = await tabletPopupShot(page, '.z-timebox-popup',
      ['z-timebox-wheel-cave', 'z-timebox-wheel-list', 'z-timebox-wheel-list-selected', 'z-timebox-wheel-line', 'z-timebox-wheel-button'],
      'timebox-tablet-popup-wheel.png');
    await expectBottomSheet(page, popup);
    await expectTouchTargets(popup.locator('.z-timebox-wheel-button'), 44, 'wheel button');
  });

  // ---- colorbox: palette and picker ----
  // The popup slides in with `transition: transform 300ms` from top = viewport height, so a read taken right after
  // an openPopup() API call finds it below the viewport (y 1112): that is why the NS22 measurement saw no visible
  // z-colorbox-popup. A tap and settledBox() find it at rest.
  test('colorbox.zul: tap to open the palette', async ({ page }) => {
    await openTabletPage(page, '/colorbox.zul');
    await tapCentre(page, page.locator('.z-colorbox:not(.z-colorbox-disabled)').first());
    const popup = await tabletPopupShot(page, '.z-colorbox-popup',
      ['z-colorbox-popup', 'z-colorpalette', 'z-colorpalette-color', 'z-colorpalette-input'], 'colorbox-tablet-popup-palette.png');
    await expect(popup, 'detached to <body> as a pop-up layer, pinned to the viewport').toHaveCSS('position', 'fixed');
    expect(await popup.evaluate(el => el.parentElement!.tagName)).toBe('BODY');
    const box = await settledBox(page, popup);
    expect(box.y + box.height, 'palette rests on the viewport bottom').toBeCloseTo(page.viewportSize()!.height, -1);
  });

  test('colorbox.zul: tap the picker icon to switch the popup to the picker', async ({ page }) => {
    await openTabletPage(page, '/colorbox.zul');
    await tapCentre(page, page.locator('.z-colorbox:not(.z-colorbox-disabled)').nth(1));
    await tapCentre(page, page.locator('.z-colorbox-pickericon:visible'));
    // The picker pins its RGB / HSV / HEX rows with fixed offsets; the 48px touch inputs must not overlap or leave the picker.
    const picker = page.locator('.z-colorpicker:visible');
    await expect(picker).toHaveCount(1);
    await settledBox(page, picker);
    const rows = await Promise.all(['rgb', 'hsv', 'hex'].map(async row => (await picker.locator(`.z-colorpicker-${row} .z-colorpicker-input`).first().boundingBox())!));
    const pickerBox = (await picker.boundingBox())!;
    expect(rows[0].y + rows[0].height, 'RGB row ends above the HSV row').toBeLessThanOrEqual(rows[1].y);
    expect(rows[1].y + rows[1].height, 'HSV row ends above the HEX row').toBeLessThanOrEqual(rows[2].y);
    expect(rows[2].y + rows[2].height, 'HEX input ends inside the picker').toBeLessThanOrEqual(pickerBox.y + pickerBox.height);
    const popup = await tabletPopupShot(page, '.z-colorbox-popup', ['z-colorpicker'], 'colorbox-tablet-popup-picker.png');
    await expect(popup.locator('.z-colorpicker:visible')).toHaveCount(1);
    await expect(popup.locator('.z-colorpalette-color:visible')).toHaveCount(0);
  });

  test('colorbox: tap to open the popup on a scrolled page, the page does not scroll (pv/popup-tablet-fixtures.zul)', async ({ page }) => {
    await openTabletPage(page, '/pv/popup-tablet-fixtures.zul');
    const trigger = page.locator('.z-colorbox');
    await page.evaluate(() => window.scrollTo(0, 100));
    const scrollBefore = await page.evaluate(() => window.scrollY);
    expect(scrollBefore, 'the fixture page is scrollable').toBeGreaterThan(0);
    await tapCentre(page, trigger);
    const popupBox = await settledBox(page, page.locator('.z-colorbox-popup:visible'));
    expect(popupBox.y + popupBox.height, 'the popup rests on the viewport bottom, not on the bottom of the page').toBeCloseTo(page.viewportSize()!.height, -1);
    const viewportWidth = page.viewportSize()!.width;
    const [leftGap, rightGap] = [popupBox.x, viewportWidth - (popupBox.x + popupBox.width)];
    expect(Math.abs(leftGap - rightGap), `the sheet is centred: left gap ${leftGap}px, right gap ${rightGap}px`).toBeLessThanOrEqual(1);
    const content = await page.locator('.z-colorbox-popup:visible').evaluate(el => {
      const padding = parseFloat(getComputedStyle(el).paddingLeft) + parseFloat(getComputedStyle(el).paddingRight);
      const border = parseFloat(getComputedStyle(el).borderLeftWidth) + parseFloat(getComputedStyle(el).borderRightWidth);
      const widest = Math.max(...Array.from(el.children, child => child.getBoundingClientRect().width));
      return widest + padding + border;
    });
    expect(popupBox.width, 'the sheet is as wide as its content').toBeLessThanOrEqual(content + 1);
    expect(popupBox.width, 'the sheet is clearly narrower than the viewport').toBeLessThan(viewportWidth * 0.6);
    expect(await page.evaluate(() => window.scrollY), 'opening the popup does not scroll the page').toBe(scrollBefore);
    const triggerBox = (await trigger.boundingBox())!;
    expect(triggerBox.y, 'the trigger is still in the viewport').toBeGreaterThanOrEqual(0);
    expect(triggerBox.y + triggerBox.height, 'the trigger is still in the viewport').toBeLessThanOrEqual(page.viewportSize()!.height);
  });

  // ---- menu, combobutton, column menu: the rows grow to the touch target (tablet/_menu.css, _buttons.css, _mesh.css) ----
  const menuLabel = (page: Page, label: string) => page.getByText(label, { exact: true }).first();

  test('menubar.zul: tap a menu to open its pop-up', async ({ page }) => {
    await openTabletPage(page, '/menubar.zul');
    await tapCentre(page, menuLabel(page, 'Project'));
    const popup = await tabletPopupShot(page, '.z-menupopup', ['z-menupopup', 'z-menupopup-content', 'z-menuitem-content'], 'menubar-tablet-popup-open.png');
    await expectTouchTargets(popup.locator('.z-menuitem-content'), 44, 'menuitem');
  });

  test('menubar.zul: tap a menu with a checked menuitem', async ({ page }) => {
    await openTabletPage(page, '/menubar.zul');
    await tapCentre(page, menuLabel(page, 'View'));
    await tapCentre(page, page.locator('.z-menuitem:visible', { hasText: 'Sort by Name' })); // autocheck closes the menu
    await tapCentre(page, menuLabel(page, 'View'));
    const popup = await tabletPopupShot(page, '.z-menupopup', ['z-menupopup', 'z-menuitem-checked', 'z-menuitem-icon'], 'menubar-tablet-popup-open-checked.png');
    await expectTouchTargets(popup.locator('.z-menuitem-content'), 44, 'menuitem');
    // the check mark grows to the touch icon size (24px)
    await expectTouchTargets(popup.locator('.z-menuitem-checked .z-menuitem-icon'), 24, 'check mark', true);
  });

  test('combobutton.zul: tap the arrow to open the menu', async ({ page }) => {
    await openTabletPage(page, '/combobutton.zul');
    const arrow = page.locator('.z-combobutton:not(.z-combobutton-disabled):not(.z-combobutton-toolbar) .z-combobutton-button').first();
    await expectTouchTargets(arrow, 44, 'combobutton arrow', true);
    await tapCentre(page, arrow);
    const popup = await tabletPopupShot(page, '.z-menupopup', ['z-combobutton-open', 'z-menupopup'], 'combobutton-tablet-popup-open.png');
    await expectTouchTargets(popup.locator('.z-menuitem-content'), 44, 'menuitem');
  });

  test('grid.zul: tap the column menu button to open the column menu', async ({ page }) => {
    await openTabletPage(page, '/grid.zul');
    const button = page.locator('.z-column-button:visible').first();
    await expectTouchTargets(button, 44, 'column menu button', true);
    await tapCentre(page, button);
    const popup = await tabletPopupShot(page, '.z-menupopup', ['z-menupopup', 'z-columns-menuascending', 'z-menuitem-checkable'], 'grid-tablet-popup-column-menu.png');
    await expectTouchTargets(popup.locator('.z-menuitem-content:visible'), 44, 'column menu item');
  });

  test('listbox.zul: tap the header menu button to open the header menu', async ({ page }) => {
    await openTabletPage(page, '/listbox.zul');
    const button = page.locator('.z-listheader-button:visible').first();
    await expectTouchTargets(button, 44, 'header menu button', true);
    await tapCentre(page, button);
    const popup = await tabletPopupShot(page, '.z-menupopup', ['z-menupopup', 'z-listheader-button'], 'listbox-tablet-popup-header-menu.png');
    await expectTouchTargets(popup.locator('.z-menuitem-content:visible'), 44, 'header menu item');
  });

  // ---- errorbox and notification: the close button grows to the touch icon size (tablet/_feedback.css) ----
  test('errorbox.zul: tap a field, leave it empty and tap away to open the errorbox', async ({ page }) => {
    await openTabletPage(page, '/errorbox.zul');
    await tapCentre(page, page.locator('input[placeholder="Required field"]'));
    // Type a character and delete it: the constraint fails on blur and ZK opens the errorbox. The keys stand for the soft keyboard.
    await page.keyboard.type('a');
    await page.keyboard.press('Backspace');
    await page.touchscreen.tap(400, 20);
    const popup = await tabletPopupShot(page, '.z-errorbox.z-errorbox-open', ['z-errorbox-open', 'z-errorbox-close'], 'errorbox-tablet-popup-open.png');
    await expectTouchTargets(popup.locator('.z-errorbox-close'), 24, 'errorbox close button', true);
  });

  test('notification.zul: tap Info to show a notification', async ({ page }) => {
    // notification.zul auto-closes after 3000 ms (Notification.ts setTimeout): drop exactly that timer, as popup-gallery.spec.ts does.
    const AUTO_CLOSE_MS = 3000;
    await page.addInitScript(ms => {
      const realSetTimeout = window.setTimeout.bind(window);
      window.setTimeout = ((handler: TimerHandler, delay?: number, ...args: unknown[]) =>
        delay === ms ? 0 : realSetTimeout(handler, delay, ...args)) as typeof window.setTimeout;
    }, AUTO_CLOSE_MS);
    await openTabletPage(page, '/notification.zul');
    await tapCentre(page, page.getByRole('button', { name: 'Info', exact: true }));
    await expect(page.locator('.z-notification-open')).toHaveCount(1);
    const popup = await tabletPopupShot(page, '.z-notification-open', ['z-notification-open', 'z-notification-close'], 'notification-tablet-popup-info.png');
    await expectTouchTargets(popup.locator('.z-notification-close'), 24, 'notification close button', true);
  });

  // ---- window: the modal window's header and header buttons (tablet/_window.css) ----
  test('window.zul: tap to open a modal window (pv/popup-api-fixtures.zul)', async ({ page }) => {
    await openTabletPage(page, '/pv/popup-api-fixtures.zul');
    await tapCentre(page, page.getByRole('button', { name: 'Open modal window', exact: true }));
    await expect(page.locator('.z-window-modal')).toHaveCount(1);
    const viewport = page.viewportSize()!;
    expect(await page.locator('.z-modal-mask').boundingBox(), 'the scrim covers the viewport')
      .toEqual({ x: 0, y: 0, width: viewport.width, height: viewport.height });
    const popup = await tabletPopupShot(page, '.z-window-modal', ['z-window-modal', 'z-modal-mask', 'z-window-header', 'z-window-icon'], 'window-tablet-popup-modal.png');
    await expectTouchTargets(popup.locator('.z-window-header'), 56, 'window header');
    await expectTouchTargets(popup.locator('.z-window-icon'), 44, 'window close button', true);
  });

  // ---- confirmpopup: the OK and Cancel buttons grow to the touch target (tablet/_buttons.css) ----
  test('confirmpopup.zul: tap to open the confirm pop-up (pv/popup-tablet-fixtures.zul)', async ({ page }) => {
    await openTabletPage(page, '/pv/popup-tablet-fixtures.zul');
    await tapCentre(page, page.getByRole('button', { name: 'Delete item', exact: true }));
    const popup = await tabletPopupShot(page, '.z-confirmpopup-open',
      ['z-confirmpopup-open', 'z-confirmpopup-ok', 'z-confirmpopup-cancel'], 'confirmpopup-tablet-popup-open.png');
    await expectTouchTargets(popup.locator('.z-confirmpopup-ok, .z-confirmpopup-cancel'), 44, 'confirm button');
  });
});

// -------------------------------------------------------
// The bottom sheets are position:fixed. A page wider than the screen makes the browser enlarge the layout viewport that
// fixed boxes are measured against (window.innerHeight 1668 against 1112 visible here), so `bottom: 0` sank below the
// visible area. The sheet must sit in the VISIBLE area, which is visualViewport.
// -------------------------------------------------------
test.describe('tablet-popup-page-wider-than-screen', () => {
  test.use({ viewport: { width: 600, height: 1112 } });

  const sheets = [
    { name: 'colorbox', trigger: '.z-colorbox', popup: '.z-colorbox-popup' },
    { name: 'combobox', trigger: '.z-combobox-button', popup: '.z-combobox-popup' },
    { name: 'datebox wheel', trigger: '.z-datebox-button', popup: '.z-datebox-popup' },
  ];

  test('the colorbox, combobox and datebox sheets stay inside the visible area (pv/popup-tablet-overflow-fixtures.zul)', async ({ page }) => {
    for (const { name, trigger, popup } of sheets) {
      await openTabletPage(page, '/pv/popup-tablet-overflow-fixtures.zul');
      const layoutWidth = await page.evaluate(() => document.documentElement.scrollWidth);
      expect(layoutWidth, 'the fixture page is wider than the 600px screen').toBeGreaterThan(page.viewportSize()!.width);
      await tapCentre(page, page.locator(trigger));
      await settledBox(page, page.locator(`${popup}:visible`));

      const { sheet, visible, scrollY } = await page.locator(`${popup}:visible`).evaluate(el => {
        const rect = el.getBoundingClientRect();
        const vv = window.visualViewport!;
        return {
          sheet: { left: rect.left, right: rect.right, bottom: rect.bottom },
          visible: { left: vv.offsetLeft, right: vv.offsetLeft + vv.width, bottom: vv.offsetTop + vv.height },
          scrollY: window.scrollY,
        };
      });
      expect(Math.abs(sheet.bottom - visible.bottom), `${name}: the sheet bottom sits on the bottom of the visible area`).toBeLessThanOrEqual(1);
      expect(sheet.left, `${name}: the sheet starts inside the visible area`).toBeGreaterThanOrEqual(visible.left - 1);
      expect(sheet.right, `${name}: the sheet ends inside the visible area`).toBeLessThanOrEqual(visible.right + 1);
      expect(scrollY, `${name}: opening the sheet does not scroll the page`).toBe(0);
      const triggerBox = (await page.locator(trigger).boundingBox())!;
      expect(triggerBox.y + triggerBox.height, `${name}: the trigger is still in the visible area`).toBeLessThanOrEqual(visible.bottom);
    }
  });
});
