import { test, expect, type Page } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

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
  'area', 'html', 'iframe', 'imagemap',
  'scrollbar', 'overview', 'preview', 'inputs',
  'camera', 'barcodescanner', 'captcha', 'video', 'audio', 'fileupload', 'loading', 'loadingbar',
]);

// ZK animates a progressmeter's value bar with a jQuery animation that `animations: 'disabled'` does not stop, and
// networkidle can return before it ends. Resolves once every bar has kept its width for two animation frames; a page
// without a progressmeter resolves at once.
async function waitForProgressmeterBars(page: Page): Promise<void> {
  await page.evaluate(() => new Promise<void>(resolve => {
    const bars = [...document.querySelectorAll('.z-progressmeter-image')];
    if (!bars.length) return resolve();
    const widths = () => bars.map(bar => bar.getBoundingClientRect().width).join();
    let previous = widths();
    let stableFrames = 0;
    const onFrame = () => {
      const current = widths();
      stableFrames = current === previous ? stableFrames + 1 : 0;
      previous = current;
      if (stableFrames >= 2) resolve();
      else requestAnimationFrame(onFrame);
    };
    requestAnimationFrame(onFrame);
  }));
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
