import { test, expect } from '@playwright/test';
import fs from 'fs';
import path from 'path';

// Font-size equivalence oracle for the z-text-* size renumber
// (doc/spec/utility-naming.md §3.2, Gate 2).
//
// Why this exists: every other rename in that series REMOVES the old name, so a missed call
// site becomes an orphan and the coverage guard fires. D3 is different — it is a SHIFT within a
// closed set. After it, `z-text-2xl` still exists; it just means 24px instead of 22px. A missed
// call site therefore resolves to a valid-but-wrong class, silently, and no existing check can
// see it. Screenshots cannot close the gap either: screenshot.spec.ts compares state crops at
// maxDiffPixels: 20, and a 13px -> 14px label on a short string can land inside that floor.
//
// The oracle: a rename is a visual no-op, so the DISTRIBUTION of computed font sizes on every
// page must be byte-identical before and after. A histogram is used rather than a per-element
// list because it is compact, order-independent, and still catches inherited changes — if a
// container's size class shifts, every descendant that inherits from it moves bucket too.
//
// Capture the baseline BEFORE editing anything:
//   UPDATE_FONT_BASELINE=1 npx playwright test --project=font-size
// Then, after the rename, run it with no env var. Any diff names the page and the buckets.

// Beside the focus-ring baseline, in zkthemelab/doc/ — three levels up from src/test/playwright.
const BASELINE_FILE = path.resolve(__dirname, '../../../doc/font-size-baseline.json');
const WEB_DIR = path.resolve(__dirname, '../../main/webapp/web');
const UPDATING = process.env.UPDATE_FONT_BASELINE === '1';

// Pages whose content is not deterministic across runs (hardware, external resources, or a
// live clock). They would produce a histogram that differs from itself, which is worse than
// no coverage — the same exclusions gallery-scan.spec.ts uses for the same reason.
const SKIP = new Set([
  'camera', 'barcodescanner', 'captcha', 'video', 'audio', 'fileupload',
  'loading', 'loadingbar', 'runtime-error',
  // Not non-deterministic, but too slow: it applies every other page (~39,000 elements) and takes
  // ~14 s to settle alone, so under parallel workers it overruns the 15 s settle wait. Every page
  // it aggregates is measured on its own here, so nothing goes uncovered.
  'preview',
]);

// web/pv/* are content fragments, not pages — they are <include>d by their host page and some
// 500 on their own (pv/cascader-content.zul does). render-smoke.spec.ts covers none of them for
// the same reason. Nothing is lost: each fragment's font sizes are measured through the host
// page that includes it, which IS covered here.
function zulPages(dir: string, prefix = ''): string[] {
  const out: string[] = [];
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    if (e.isDirectory()) {
      if (e.name === 'pv') continue;
      out.push(...zulPages(path.join(dir, e.name), `${prefix}${e.name}/`));
    } else if (e.name.endsWith('.zul') && !SKIP.has(e.name.replace(/\.zul$/, ''))) {
      out.push(`${prefix}${e.name}`);
    }
  }
  return out;
}

const pages = zulPages(WEB_DIR).sort();

type Histogram = Record<string, number>;
const captured: Record<string, Histogram> = {};

const baseline: Record<string, Histogram> = !UPDATING && fs.existsSync(BASELINE_FILE)
  ? JSON.parse(fs.readFileSync(BASELINE_FILE, 'utf8')).pages
  : {};

test.describe('font-size equivalence', () => {
  // Serial only while capturing, because the capture aggregates into one module-level object
  // and parallel workers would each fill their own copy. While comparing, run the default mode:
  // a serial group SKIPS everything after a failure, and a gate has to report every diff, not
  // just the first one.
  test.describe.configure({ mode: UPDATING ? 'serial' : 'default' });

  for (const p of pages) {
    test(p, async ({ page }) => {
      const resp = await page.goto(`/${p}`, { waitUntil: 'networkidle' });
      // The suite runs serial (the capture aggregates into one module-level object), so a
      // throw here would abort every page after it. While capturing, record the bad status
      // instead and let the run finish; while comparing, a status change is a real failure.
      if (UPDATING && resp?.status() !== 200) {
        captured[p] = { [`HTTP ${resp?.status()}`]: 1 };
        return;
      }
      expect(resp?.status(), `${p} returned HTTP ${resp?.status()}`).toBe(200);
      // The Inter web font changes metrics but not computed font-size; still, wait for it so
      // the page has finished its layout before measuring.
      await page.evaluate(() => document.fonts.ready.then(() => true));

      // networkidle is not enough on the big generated catalogues (icons-lucide.zul and
      // icons.zul render ~2000 tiles): ZK is still appending nodes when it fires, so the
      // histogram comes out short and the page disagrees with itself between runs. Wait for
      // the DOM to stop growing instead — two identical counts in a row.
      await expect.poll(async () => {
        const n = await page.evaluate(() => document.querySelectorAll('*').length);
        await page.waitForTimeout(100);
        const m = await page.evaluate(() => document.querySelectorAll('*').length);
        return n === m ? n : -1;
      }, { message: `${p}: DOM never stopped changing`, timeout: 15_000 }).toBeGreaterThan(0);

      const hist: Histogram = await page.evaluate(() => {
        const h: Record<string, number> = {};
        for (const el of Array.from(document.querySelectorAll('*'))) {
          const fs = getComputedStyle(el).fontSize;
          h[fs] = (h[fs] ?? 0) + 1;
        }
        return h;
      });

      if (UPDATING) {
        captured[p] = hist;
        return;
      }

      const want = baseline[p];
      expect(want, `${p} has no baseline entry — regenerate with UPDATE_FONT_BASELINE=1`)
        .toBeTruthy();
      expect(hist, `${p}: the computed font-size distribution changed. A rename must be a `
        + `visual no-op, so a diff here means a call site was missed or rewritten to the `
        + `wrong step of the scale.`).toEqual(want);
    });
  }

  test.afterAll(() => {
    if (!UPDATING) return;
    fs.writeFileSync(BASELINE_FILE,
      JSON.stringify({
        note: 'Computed font-size histogram per preview page. Oracle for the z-text-* '
          + 'renumber (doc/spec/utility-naming.md R7). Regenerate with '
          + 'UPDATE_FONT_BASELINE=1 npx playwright test --project=font-size',
        pages: Object.fromEntries(Object.keys(captured).sort().map(k => [k, captured[k]])),
      }, null, 2) + '\n');
  });
});
