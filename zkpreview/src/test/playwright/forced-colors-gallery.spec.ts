import { test, expect, type Page } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import { AGGREGATE_PAGES } from './aggregate-pages';

// Forced-colors VISUAL review pass (see doc/spec/forced-colors.md).
//
// The sibling forced-colors.spec.ts is the MECHANICAL gate (computed-style
// probes). This spec is the complementary HUMAN-review pass: it captures ONE
// screenshot per preview page under Windows High-Contrast emulation so every
// component can be eyeballed at once. Pair the output with the normal-mode
// gallery.png via `npm run review:forced-colors` (scripts/build-forced-colors-
// review.js) which builds doc/forced-colors-review.html — a single side-by-side
// scroll (normal | forced-colors).
//
// These screenshots are REVIEW ARTIFACTS, not committed CI baselines — so we
// write them directly with page.screenshot({path}) (no toHaveScreenshot diff
// noise). The mechanical regression gate stays forced-colors.spec.ts.
//
// Pages are auto-discovered by scanning src/test/resources/web/*.zul at
// collection time (same as gallery-scan.spec.ts) so a new page is covered the
// moment it is added. Unlike gallery-scan we do NOT subtract COVERED_ELSEWHERE:
// button/checkbox/listbox/datebox/window/etc. are the MOST forced-colors-
// sensitive components and there is no duplicate-baseline concern here.
//
// Requires the preview app on ${PREVIEW_URL}
//   withjdk.sh 17 mvn test exec:java@preview-app
// Run: npm run capture:forced-colors

const WEB_DIR = path.resolve(__dirname, '../../main/webapp/web');
const SNAP_DIR = path.resolve(__dirname, '../../../doc/screenshots');

// Keep this SKIP set identical to gallery-scan.spec.ts: pages a static gallery
// screenshot can't meaningfully or stably capture (non-visual primitives,
// hardware/external-resource/animated pages that produce flaky shots).
const SKIP = new Set([
  ...AGGREGATE_PAGES,
  'area', 'html', 'iframe', 'imagemap',
  'scrollbar',
  'camera', 'barcodescanner', 'captcha', 'video', 'audio', 'fileupload', 'loading', 'loadingbar',
]);

// ZK steps a progressmeter's value bar up to its value with a jQuery animation, and the bar's width then transitions;
// `animations: 'disabled'` stops neither early enough, and networkidle can return before they end. Resolves once every
// visible determinate bar has kept its width for two animation frames and that width is the widget's value as a
// fraction of the meter's content box (percentage widths exclude the padding). The box comes from the bounding rect:
// clientWidth is an integer and never matches a fractional width (a z-flex-1 meter) within the tolerance. The value comes from the widget, not
// from aria-valuenow, which only za11y (EE) adds. An indeterminate meter's bar animates forever, so it is skipped, and
// a page without a progressmeter resolves at once. The frames are awaited inside one evaluate: waitForFunction treats
// the Promise of an async predicate as truthy and would return on its first poll.
// The tail of the width transition changes by less than a pixel per frame, so a looser tolerance returns before the end.
const PROGRESSMETER_WIDTH_TOLERANCE_PX = 0.1;
const PROGRESSMETER_WAIT_TIMEOUT_MS = 10_000;

async function waitForProgressmeterBars(page: Page): Promise<void> {
  await page.evaluate(async ({ tolerance, timeout }) => {
    const nextFrame = () => new Promise(resolve => requestAnimationFrame(resolve));
    const meters = [...document.querySelectorAll('.z-progressmeter:not(.z-progressmeter-indeterminate)')]
      .filter(meter => meter.getClientRects().length > 0 && meter.querySelector('.z-progressmeter-image'));
    const barWidths = () => meters.map(meter => meter.querySelector('.z-progressmeter-image')!.getBoundingClientRect().width);
    const expectedWidth = (meter: Element) => {
      const style = getComputedStyle(meter);
      const horizontalInsets = ['borderLeftWidth', 'borderRightWidth', 'paddingLeft', 'paddingRight']
        .reduce((sum, property) => sum + parseFloat(style[property as 'paddingLeft']), 0);
      const contentWidth = meter.getBoundingClientRect().width - horizontalInsets;
      const widget = (window as unknown as { zk: { Widget: { $(n: Element): { getValue(): number } } } }).zk.Widget.$(meter);
      return contentWidth * widget.getValue() / 100;
    };
    const deadline = performance.now() + timeout;
    for (;;) {
      const before = barWidths();
      await nextFrame();
      await nextFrame();
      const after = barWidths();
      if (meters.every((meter, i) => before[i] === after[i] && Math.abs(after[i] - expectedWidth(meter)) <= tolerance)) return;
      if (performance.now() > deadline) throw new Error(`a progressmeter bar did not settle within ${timeout}ms`);
    }
  }, { tolerance: PROGRESSMETER_WIDTH_TOLERANCE_PX, timeout: PROGRESSMETER_WAIT_TIMEOUT_MS });
}

const pages = fs.readdirSync(WEB_DIR)
  .filter(f => f.endsWith('.zul'))
  .map(f => f.replace(/\.zul$/, ''))
  .filter(name => !SKIP.has(name))
  .sort();

test.describe('forced-colors-gallery (visual review)', () => {
  // Emulate WHCM at runtime. This CDP-backed call is the reliable path — the
  // context-option form (use.forcedColors) does not take effect for the page
  // fixture in this runner setup. See forced-colors.spec.ts.
  test.beforeEach(async ({ page }) => {
    await page.emulateMedia({ forcedColors: 'active' });
  });

  for (const comp of pages) {
    test(comp, async ({ page }) => {
      await page.goto(`/${comp}.zul`, { waitUntil: 'networkidle' });
      // Wait for the Inter web font to settle so the shot isn't taken mid font-swap.
      await page.evaluate(() => document.fonts.ready.then(() => true));
      await waitForProgressmeterBars(page);
      const wrapper = page.locator('.z-p-8').first();
      await expect(
        wrapper,
        `${comp}.zul has no .z-p-8 wrapper — give it one or add "${comp}" to SKIP in forced-colors-gallery.spec.ts`
      ).toBeVisible();
      // Review artifact (not a committed baseline): write directly.
      await wrapper.screenshot({
        path: path.join(SNAP_DIR, `${comp}-forced-colors.png`),
        animations: 'disabled',
      });
    });
  }
});
