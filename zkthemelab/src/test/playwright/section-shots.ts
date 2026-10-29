import { expect, Page, test } from '@playwright/test';

// Sectioned gallery screenshots (D7).
//
// A whole-page gallery shot tolerates maxDiffPixelRatio * page-area pixels (tree 16,481 px), so
// the budget grows with page height and a 1px border change on one control hides inside it. Here
// every section gets its own shot and an absolute pixel budget.
//
// Section = the parent element of a standard heading row `.z-text-uppercase.z-border-bottom`
// inside the page's `.z-p-8` wrapper. The first child of the wrapper, when it holds no heading,
// is the page title and is shot as section 00.
//
// Coverage guard: every direct child of the wrapper must be the page title or a section holding
// exactly one standard heading. A child that is neither (a demo block added without a heading,
// or one holding two headings) fails the test instead of going unshot or sharing one shot.

const HEADING = '.z-text-uppercase.z-border-bottom';

// Margin (px) shot around each section so a shadow or focus ring that overflows the section box
// is covered (elevation-3 reaches 16px: offset 4 + blur 12). The gap between two sections must be
// at least PAD, otherwise a neighbour's content would enter the clip; expectSectionShots checks it.
const PAD = 16;

// Extra viewport height (px) over the document height; see expectSectionShots.
const VIEWPORT_SLACK = 1000;

export interface Section {
  /** Index among the direct children of `.z-p-8`. */
  index: number;
  title: string;
}

const slug = (s: string) => s
  .toLowerCase()
  .replace(/[^a-z0-9]+/g, '-')
  .slice(0, 48)
  .replace(/^-+|-+$/g, '');

/** Lists the shot units of the page and fails when a direct child of `.z-p-8` is not covered. */
export async function listSections(page: Page, comp: string): Promise<Section[]> {
  const units = await page.evaluate((heading) => {
    const root = document.querySelector('.z-p-8') as HTMLElement;
    return Array.from(root.children).map((child, i) => {
      const hs = Array.from(child.matches(heading) ? [child] : child.querySelectorAll(heading));
      // A child that takes no space (a hidden <popup>, a <zscript> output) has nothing to shoot.
      const box = child.getBoundingClientRect();
      const rendered = getComputedStyle(child).display !== 'none' && (box.width > 0 || box.height > 0);
      return { i, rendered, headings: hs.length, title: hs.length ? (hs[0].textContent || '').trim() : null };
    });
  }, HEADING);
  // A heading-less child that takes no space (a hidden <popup>, a <zscript> output) has nothing to
  // shoot. A child WITH a heading that takes no space is a section that went missing: fail.
  const shown = units.filter(u => u.rendered || u.headings > 0);
  // Only the first shown child may be the heading-less page title.
  const bad = shown.filter((u, n) => u.headings > 1 || !u.rendered || (u.headings === 0 && n !== 0));
  expect(
    bad.map(u => `child ${u.i}: ${u.headings} headings${u.rendered ? '' : ', not rendered'}`),
    `${comp}.zul: every direct child of .z-p-8 must hold exactly one ${HEADING} heading and take space (only the first rendered child may be a heading-less title)`
  ).toEqual([]);
  return shown.map(u => ({ index: u.i, title: u.title ?? 'title' }));
}

/**
 * One screenshot per section, `<comp>-<nn>-<slug>.png`, each with an absolute `maxDiffPixels`
 * budget. Set SECTION_NOISE=1 to measure instead of assert: every section is compared with a
 * budget of 0 and its changed-pixel count is printed as `NOISE <name> <count>`.
 * Section numbers in the file names count the skipped sections too, so a skip never renames the rest.
 */
export async function expectSectionShots(
  page: Page, comp: string, maxDiffPixels: number,
  { skipTitle, ...options }: Record<string, unknown> & { skipTitle?: RegExp } = {},
): Promise<void> {
  // `skipTitle` leaves out sections that cannot be shot statically (an autoplaying carousel); they
  // still count as covered by the guard.
  const listed = await listSections(page, comp);
  const sections = listed.map((s, n) => ({ ...s, n })).filter(s => !skipTitle?.test(s.title));
  // Each section is a clipped full-page capture (about 1s on a tall page); the default 30s test
  // timeout would cut a page with over a hundred sections short.
  test.setTimeout(30_000 + 2_000 * sections.length);
  const root = page.locator('.z-p-8').first();
  // ZK recomputes widget sizes on every window resize, and some demos respond to each resize
  // differently (tabbox: the tab strip is 103px wide before the first resize and 87px after;
  // anchorlayout: the first section grows by 111px per resize). Playwright captures a page taller
  // than the viewport with captureBeyondViewport, which resizes the page on every shot, so what a
  // section looks like would depend on how many shots came before it (a baseline write takes two
  // per image, a comparison one). Make the viewport taller than the page instead: one resize here,
  // then every shot fits the viewport and changes nothing. The slack covers the growth the resize
  // itself causes.
  const width = page.viewportSize()?.width ?? 1280;
  const docHeight = await page.evaluate(() => document.documentElement.scrollHeight);
  await page.setViewportSize({ width, height: docHeight + VIEWPORT_SLACK });
  await page.evaluate(() => new Promise<void>((resolve) => {
    setTimeout(() => requestAnimationFrame(() => requestAnimationFrame(() => resolve())), 200);
  }));
  // A page can still be taller than the viewport (something outside .z-p-8 anchored to the viewport
  // bottom adds scroll height), and Playwright then resizes it on every shot. Record the full box
  // of every direct child now and compare after the last shot: a page that drifts per resize (in
  // height, width or position) fails.
  const childBoxes = () => page.evaluate(() => Array.from(document.querySelector('.z-p-8')!.children)
    .map((c) => { const r = c.getBoundingClientRect(); return [r.x, r.y, r.width, r.height]; }));
  const boxesBefore = await childBoxes();
  const noise = !!process.env.SECTION_NOISE;
  let prevBottom: number | null = null;
  for (const s of sections) {
    const name = `${comp}-${String(s.n).padStart(2, '0')}-${slug(s.title)}.png`;
    // Page coordinates (a fullPage clip is page-relative; boundingBox() is viewport-relative).
    const box = await page.evaluate((i) => {
      const r = (document.querySelector('.z-p-8') as HTMLElement).children[i].getBoundingClientRect();
      return { x: r.x + scrollX, y: r.y + scrollY, width: r.width, height: r.height };
    }, s.index);
    const x = Math.max(0, Math.floor(box.x) - PAD);
    const y = Math.max(0, Math.floor(box.y) - PAD);
    const clip = {
      x, y,
      width: Math.ceil(box.x + box.width) + PAD - x,
      height: Math.ceil(box.y + box.height) + PAD - y,
    };
    if (prevBottom !== null) {
      expect(box.y - prevBottom, `${name}: gap to the previous section is smaller than the ${PAD}px clip margin`)
        .toBeGreaterThanOrEqual(PAD);
    }
    prevBottom = box.y + box.height;
    const shot = { ...options, fullPage: true, clip };
    if (noise) {
      try {
        await expect(page).toHaveScreenshot(name, { ...shot, maxDiffPixels: 0 });
        console.log(`NOISE ${name} 0`);
      } catch (e) {
        const m = /(\d+) pixels/.exec(String((e as Error).message));
        console.log(`NOISE ${name} ${m ? m[1] : 'ERR'}`);
      }
    } else {
      await expect(page, name).toHaveScreenshot(name, { ...shot, maxDiffPixels });
    }
  }
  expect(await childBoxes(), `${comp}.zul: section boxes changed while shooting; the page reacts to the resize of each shot`)
    .toEqual(boxesBefore);
}
