import { test, expect, type Page } from '@playwright/test';

// Guard for the input fields' top and bottom border at a sub-pixel offset (ZK-6112). Chrome snaps a border
// to whole device pixels but applies a clip-path at the exact fractional position, so a clip on the field
// cut part of the snapped top or bottom border row away, and that edge rendered lighter and thinner than the
// other one. The combobox input carried such a clip to keep its border from running under the button.
// Fixture: web/pv/field-border-subpixel-guard.zul

const FIXTURE = '/pv/field-border-subpixel-guard.zul';
// A timepicker copies its sclass to its hidden popup too.
const FIELDS = '.fbs-field:visible';
// Tall enough for the whole fixture, so every field is measured at the position the fixture gives it,
// without a scroll in between.
test.use({ viewport: { width: 1280, height: 1800 } });
// Two grey levels of the same 1px border differ by a few units at most after anti-aliasing of the corners;
// a half-clipped row lands about 50 units lighter.
const TOLERANCE = 12;
// Lighter than this is background, not border.
const BACKGROUND = 235;

type Edge = { x: number; top: number; bottom: number };

async function edges(page: Page, index: number): Promise<{ name: string; y: number; cols: Edge[] }> {
  const field = page.locator(FIELDS).nth(index);
  const box = (await field.boundingBox())!;
  const name = await field.evaluate((el) => [...el.classList].find((c) => /^z-[a-z]+$/.test(c)) ?? el.className);
  const clip = { x: Math.floor(box.x), y: Math.floor(box.y) - 2, width: Math.ceil(box.width) + 1, height: Math.ceil(box.height) + 4 };
  const png = await page.screenshot({ clip });
  const cols = await page.evaluate(async (b64) => {
    const img = new Image();
    img.src = `data:image/png;base64,${b64}`;
    await img.decode();
    const canvas = document.createElement('canvas');
    canvas.width = img.width;
    canvas.height = img.height;
    const ctx = canvas.getContext('2d')!;
    ctx.drawImage(img, 0, 0);
    const { data, width, height } = ctx.getImageData(0, 0, img.width, img.height);
    const lum = (x: number, y: number) => {
      const i = (y * width + x) * 4;
      return Math.round(0.299 * data[i] + 0.587 * data[i + 1] + 0.114 * data[i + 2]);
    };
    const darkest = (x: number, from: number, to: number) => {
      let m = 255;
      for (let y = from; y < to; y++) m = Math.min(m, lum(x, y));
      return m;
    };
    // Stay clear of the rounded corners; the last column sits over a trigger button where there is one.
    return [0.15, 0.4, 0.65].map((f) => Math.round(width * f)).concat(width - 14).map((x) => ({
      x,
      top: darkest(x, 0, 5),
      bottom: darkest(x, height - 5, height),
    }));
  }, png.toString('base64'));
  return { name, y: box.y, cols };
}

test('every field draws its top and bottom border alike at a sub-pixel offset', async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.mouse.move(0, 0);
  const count = await page.locator(FIELDS).count();
  expect(count).toBeGreaterThan(0);
  const failures: string[] = [];
  for (let i = 0; i < count; i++) {
    const { name, y, cols } = await edges(page, i);
    if (process.env.FBS_DEBUG) console.log(name, y.toFixed(2), cols.map((c) => `${c.top}/${c.bottom}`).join(' '));
    for (const c of cols) {
      if (c.top >= BACKGROUND && c.bottom >= BACKGROUND) continue;
      if (Math.abs(c.top - c.bottom) > TOLERANCE)
        failures.push(`${name} at y=${y.toFixed(2)}, x+${c.x}: top ${c.top}, bottom ${c.bottom}`);
    }
  }
  expect(failures).toEqual([]);
});
