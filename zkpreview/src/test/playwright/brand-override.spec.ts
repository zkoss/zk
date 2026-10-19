import { test, expect, Page, TestInfo } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import { AGGREGATE_PAGES } from './aggregate-pages';

// Brand-colour override guard (doc/marble-test-gaps-round2-plan.md section 6).
//
// The promise (doc/spec/brand-override.md): overriding the one seed --zk-color-primary re-derives the
// whole primary family. Two checks keep it:
//   1. Residual scan, one test per preview page. The page is shot with the stock seed, then
//      <html data-brand="copper"> is set (no reload, same layout) and the same area is shot again.
//      A pixel is a RESIDUAL when the stock pixel equals a stock primary-family colour and the copper
//      pixel is byte-identical: that colour did not follow the seed, so something hardcodes it.
//   2. Derived-token test: every derived primary-family token moves with the seed, lands on the tone
//      the spec prescribes and keeps the seed's hue.
// Only the resting page is looked at; pop-ups, hover and focus states are not part of this spec.

// The tests are named after the page (`<page>`), which is the form select-tests.mjs maps to a page.
const WEB_DIR = path.resolve(__dirname, '../../main/webapp/web');
const pages = fs.readdirSync(WEB_DIR)
  .filter(f => f.endsWith('.zul'))
  .map(f => f.replace(/\.zul$/, ''))
  .filter(name => !AGGREGATE_PAGES.includes(name))
  .sort();

const BRAND = 'copper';
const COPPER_SEED_RGB = [180, 83, 9]; // _colors.css :root[data-brand="copper"] #b45309

// The stock primary-family tokens; every one of them is derived from --zk-color-primary.
const FAMILY_TOKENS = [
  '--zk-color-primary',
  '--zk-color-primary-container',
  '--zk-color-on-primary-container',
  '--zk-color-secondary',
  '--zk-color-secondary-container',
  '--zk-color-on-secondary-container',
  '--zk-color-inverse-primary',
  '--zk-color-primary-fill',
  '--zk-color-secondary-fill',
  '--zk-codeeditor-dark-border-color-focus',
];

// Per-channel distance within which a stock pixel counts as "this family colour".
const CHANNEL_TOLERANCE = 2;
// Residual pixels allowed per page. Measured first, see doc/marble-brand-batch1-report.md.
const RESIDUAL_MAX_PIXELS = 0;
// Extra viewport height over the document height so the clip never needs a scroll (as section-shots.ts).
const VIEWPORT_SLACK = 1000;
// Chromium cannot capture a texture taller than this.
const MAX_CLIP_HEIGHT = 16000;
// Hidden (visibility, so layout does not move) before BOTH shots: content pixels are not theme colour, and the
// carousel and cropper photos matched a family colour within +-2. visibility:hidden removes only the image itself,
// so components drawn over an image (carousel indicators, the cropper toolbar) stay scanned; a Playwright mask would
// paint the whole bounding box and hide them.
const CONTENT_SELECTOR = 'img, canvas, video';
// CodeMirror text anti-aliasing coincides with a family colour; only .cm-content (the text lines and caret) is hidden,
// the gutter and the editor background stay scanned. The editor background #0b1021 is set per element by the
// preview page (zkpreview/src/main/webapp/web/codeeditor.zul:137), so it is not the theme's job to follow the seed.
const EXTRA_HIDDEN: Record<string, string> = { codeeditor: '.cm-content' };
// The page whose family-pixel count must be positive: proves the family colours were read and matched.
const CANARY_PAGE = 'button';
const CLUSTER_CELL = 8;
const MAX_REPORTED_CLUSTERS = 20;

const { PNG } = require('playwright-core/lib/utilsBundle');

type Rgb = [number, number, number];

interface Cluster {
  x: number; y: number; width: number; height: number; pixels: number;
  /** index of one residual pixel of the cluster */
  firstPixel: number;
  element?: string;
  /** colour, background-color and border-color computed on that element, in the stock state */
  computed?: string;
  /** stock colour of the cluster's first residual pixel and the family token it equals */
  stockRgb?: string;
  matchedToken?: string;
}

async function settle(page: Page): Promise<void> {
  await page.evaluate(() => document.fonts.ready.then(() => true));
  // Same settling as gallery-scan.spec.ts: no running transition, progressmeter fills at their value.
  await page.addStyleTag({ content: '*{transition-duration:0s !important}' });
  await page.waitForFunction(() => Array.from(document.querySelectorAll('.z-progressmeter')).every((el) => {
    const v = el.getAttribute('aria-valuenow');
    const fill = el.querySelector('.z-progressmeter-image') as HTMLElement | null;
    return v === null || !fill || fill.style.width === `${v}%`;
  }));
}

async function nextFrames(page: Page): Promise<void> {
  await page.evaluate(() => new Promise<void>(resolve =>
    requestAnimationFrame(() => requestAnimationFrame(() => resolve()))));
  await page.evaluate(() => document.fonts.ready.then(() => true));
}

/** The family tokens as sRGB 8-bit: a computed relative colour may serialise as oklch(), so a canvas converts it. */
async function readFamilyColors(page: Page): Promise<Rgb[]> {
  return page.evaluate((tokens) => {
    const ctx = document.createElement('canvas').getContext('2d', { willReadFrequently: true })!;
    const SENTINEL = '#010203'; // a colour the canvas keeps when it cannot parse the assigned one
    return tokens.map((token) => {
      const probe = document.createElement('div');
      probe.style.backgroundColor = `var(${token})`;
      document.body.appendChild(probe);
      const computed = getComputedStyle(probe).backgroundColor;
      probe.remove();
      ctx.clearRect(0, 0, 1, 1);
      ctx.fillStyle = SENTINEL;
      ctx.fillStyle = computed;
      if (!computed || ctx.fillStyle === SENTINEL) throw new Error(`${token} did not resolve to a colour: "${computed}"`);
      ctx.fillRect(0, 0, 1, 1);
      const [r, g, b] = ctx.getImageData(0, 0, 1, 1).data;
      return [r, g, b] as [number, number, number];
    });
  }, FAMILY_TOKENS);
}

function isFamilyColor(data: Buffer, offset: number, family: Rgb[]): boolean {
  return family.some(([r, g, b]) =>
    Math.abs(data[offset] - r) <= CHANNEL_TOLERANCE
    && Math.abs(data[offset + 1] - g) <= CHANNEL_TOLERANCE
    && Math.abs(data[offset + 2] - b) <= CHANNEL_TOLERANCE);
}

function isIdentical(a: Buffer, b: Buffer, offset: number): boolean {
  return a[offset] === b[offset] && a[offset + 1] === b[offset + 1] && a[offset + 2] === b[offset + 2];
}

function countFamilyPixels(stock: any, family: Rgb[]): number {
  let n = 0;
  for (let offset = 0; offset < stock.data.length; offset += 4) if (isFamilyColor(stock.data, offset, family)) n++;
  return n;
}

/** Marks residual pixels; returns the mask (1 byte per pixel) and the count. */
function findResiduals(stock: any, copper: any, family: Rgb[]): { mask: Uint8Array; count: number } {
  const mask = new Uint8Array(stock.width * stock.height);
  let count = 0;
  for (let i = 0; i < mask.length; i++) {
    const offset = i * 4;
    if (isFamilyColor(stock.data, offset, family) && isIdentical(stock.data, copper.data, offset)) {
      mask[i] = 1;
      count++;
    }
  }
  return { mask, count };
}

/** Groups residual pixels into clusters: cells of CLUSTER_CELL px that touch (8-neighbourhood) are one cluster. */
function clusterResiduals(mask: Uint8Array, width: number, height: number): Cluster[] {
  const cols = Math.ceil(width / CLUSTER_CELL);
  const rows = Math.ceil(height / CLUSTER_CELL);
  const cellPixels = new Map<number, number>();
  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      if (!mask[y * width + x]) continue;
      const cell = Math.floor(y / CLUSTER_CELL) * cols + Math.floor(x / CLUSTER_CELL);
      cellPixels.set(cell, (cellPixels.get(cell) ?? 0) + 1);
    }
  }
  const seen = new Set<number>();
  const clusters: Cluster[] = [];
  for (const start of cellPixels.keys()) {
    if (seen.has(start)) continue;
    seen.add(start);
    const queue = [start];
    const cells: number[] = [];
    while (queue.length) {
      const cell = queue.pop()!;
      cells.push(cell);
      const cx = cell % cols;
      const cy = Math.floor(cell / cols);
      for (let dy = -1; dy <= 1; dy++) {
        for (let dx = -1; dx <= 1; dx++) {
          const nx = cx + dx;
          const ny = cy + dy;
          if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue;
          const neighbour = ny * cols + nx;
          if (cellPixels.has(neighbour) && !seen.has(neighbour)) { seen.add(neighbour); queue.push(neighbour); }
        }
      }
    }
    clusters.push(boundingBox(cells, mask, width, height, cols, cellPixels));
  }
  return clusters.sort((a, b) => b.pixels - a.pixels);
}

function boundingBox(cells: number[], mask: Uint8Array, width: number, height: number, cols: number,
                     cellPixels: Map<number, number>): Cluster {
  let minX = width, minY = height, maxX = 0, maxY = 0, pixels = 0, firstPixel = -1;
  for (const cell of cells) {
    pixels += cellPixels.get(cell)!;
    const x0 = (cell % cols) * CLUSTER_CELL;
    const y0 = Math.floor(cell / cols) * CLUSTER_CELL;
    for (let y = y0; y < Math.min(y0 + CLUSTER_CELL, height); y++) {
      for (let x = x0; x < Math.min(x0 + CLUSTER_CELL, width); x++) {
        if (!mask[y * width + x]) continue;
        if (firstPixel < 0) firstPixel = y * width + x;
        minX = Math.min(minX, x); maxX = Math.max(maxX, x);
        minY = Math.min(minY, y); maxY = Math.max(maxY, y);
      }
    }
  }
  return { x: minX, y: minY, width: maxX - minX + 1, height: maxY - minY + 1, pixels, firstPixel };
}

/** The element under each cluster centre, asked in the stock state. */
async function describeElements(page: Page, clusters: Cluster[], clip: { x: number; y: number }): Promise<void> {
  const centres = clusters.map(c => [clip.x + c.x + c.width / 2, clip.y + c.y + c.height / 2]);
  const descriptions = await page.evaluate((points) => points.map(([x, y]) => {
    const el = document.elementFromPoint(x, y);
    if (!el) return { element: '(none)', computed: '' };
    const chain: string[] = [];
    for (let cur: Element | null = el; cur && chain.length < 4; cur = cur.parentElement) {
      chain.push(cur.tagName.toLowerCase() + (cur.className && typeof cur.className === 'string'
        ? '.' + cur.className.trim().split(/\s+/).join('.') : ''));
    }
    const style = getComputedStyle(el);
    return { element: chain.join(' < '),
      computed: `color ${style.color}; background ${style.backgroundColor}; border ${style.borderTopColor}` };
  }), centres);
  clusters.forEach((c, i) => { c.element = descriptions[i].element; c.computed = descriptions[i].computed; });
}

function describeColors(clusters: Cluster[], stock: any, family: Rgb[]): void {
  for (const c of clusters) {
    const o = c.firstPixel * 4;
    c.stockRgb = `rgb(${stock.data[o]}, ${stock.data[o + 1]}, ${stock.data[o + 2]})`;
    const index = family.findIndex(([r, g, b]) =>
      Math.abs(stock.data[o] - r) <= CHANNEL_TOLERANCE && Math.abs(stock.data[o + 1] - g) <= CHANNEL_TOLERANCE
      && Math.abs(stock.data[o + 2] - b) <= CHANNEL_TOLERANCE);
    c.matchedToken = FAMILY_TOKENS[index];
  }
}

async function attachResidualDebug(testInfo: TestInfo, comp: string, stock: any, mask: Uint8Array, count: number,
                                   clusters: Cluster[]): Promise<void> {
  const debug = new PNG({ width: stock.width, height: stock.height });
  for (let i = 0; i < mask.length; i++) {
    const o = i * 4;
    const marked = mask[i] === 1;
    debug.data[o] = marked ? 255 : Math.round(stock.data[o] * 0.35 + 165);
    debug.data[o + 1] = marked ? 0 : Math.round(stock.data[o + 1] * 0.35 + 165);
    debug.data[o + 2] = marked ? 0 : Math.round(stock.data[o + 2] * 0.35 + 165);
    debug.data[o + 3] = 255;
  }
  const pngPath = testInfo.outputPath(`${comp}-residual.png`);
  const jsonPath = testInfo.outputPath(`${comp}-residual.json`);
  fs.writeFileSync(pngPath, PNG.sync.write(debug));
  fs.writeFileSync(jsonPath, JSON.stringify({ residualPixels: count, clusters: clusters.slice(0, MAX_REPORTED_CLUSTERS) }, null, 2));
  await testInfo.attach(`${comp}-residual.png`, { path: pngPath, contentType: 'image/png' });
  await testInfo.attach(`${comp}-residual.json`, { path: jsonPath, contentType: 'application/json' });
}

test.describe('brand-residual', () => {
  for (const comp of pages) {
    test(comp, async ({ page }, testInfo) => {
      await page.goto(`/${comp}.zul`, { waitUntil: 'networkidle' });
      await settle(page);
      const size = await page.evaluate(() => ({
        width: Math.ceil(document.documentElement.scrollWidth),
        height: Math.ceil(document.documentElement.scrollHeight),
      }));
      await page.setViewportSize({ width: Math.max(size.width, 1280), height: size.height + VIEWPORT_SLACK });
      await nextFrames(page);
      const clip = { x: 0, y: 0, width: size.width, height: Math.min(size.height, MAX_CLIP_HEIGHT) };

      expect(await page.evaluate(() => document.documentElement.dataset.brand),
        'the page must start with the stock seed').toBeUndefined();
      const family = await readFamilyColors(page);
      await page.addStyleTag({
        content: `${[CONTENT_SELECTOR, EXTRA_HIDDEN[comp]].filter(Boolean).join(', ')}{visibility:hidden !important}`,
      });
      const shoot = async () => PNG.sync.read(await page.screenshot({ clip, animations: 'disabled' }));
      expect(size.height, `${comp}.zul is taller than the scanned clip; raise MAX_CLIP_HEIGHT`).toBeLessThanOrEqual(MAX_CLIP_HEIGHT);
      const stock = await shoot();

      await page.evaluate((brand) => { document.documentElement.dataset.brand = brand; }, BRAND);
      await nextFrames(page);
      const copper = await shoot();
      expect(copper.width, 'both shots use the same clip').toBe(stock.width);
      expect(copper.height).toBe(stock.height);

      const { mask, count } = findResiduals(stock, copper, family);
      if (count > RESIDUAL_MAX_PIXELS) {
        const clusters = clusterResiduals(mask, stock.width, stock.height);
        await page.evaluate(() => { delete document.documentElement.dataset.brand; });
        await nextFrames(page);
        await describeElements(page, clusters.slice(0, MAX_REPORTED_CLUSTERS), clip);
        describeColors(clusters, stock, family);
        await attachResidualDebug(testInfo, comp, stock, mask, count, clusters);
        console.log(`RESIDUAL ${comp}: ${count} px, ${clusters.length} clusters; top: ${JSON.stringify(clusters.slice(0, 5))}`);
      }
      const familyPixels = countFamilyPixels(stock, family);
      testInfo.annotations.push({ type: 'residual', description: String(count) });
      testInfo.annotations.push({ type: 'family-pixels', description: String(familyPixels) });
      if (comp === CANARY_PAGE) {
        expect(familyPixels, `${comp}.zul shows no stock primary-family pixel: the family colours were not read or matched`)
          .toBeGreaterThan(0);
      }
      expect(count, `${comp}.zul: pixels that keep a stock primary-family colour under data-brand="${BRAND}"`)
        .toBeLessThanOrEqual(RESIDUAL_MAX_PIXELS);
    });
  }
});

// ---------------------------------------------------------------------------------------------
// Derived tokens
// ---------------------------------------------------------------------------------------------

// Absolute tones from doc/spec/brand-override.md / _colors.css; primary-fill is the seed's lightness capped at 0.54.
const TONE_TOLERANCE = 0.01;
// The hue is inherited unchanged; the browser serialises it with 6 significant digits, so 0.5 degrees is generous.
const HUE_TOLERANCE_DEGREES = 0.5;
const FILL_LIGHTNESS_CAP = 0.54;
// Chroma is the seed's chroma times a factor (_colors.css); the secondary containers chain off secondary (0.41).
// Tolerance 0.003 absolute: the smallest expected chroma is 0.0286 (about 10%), the browser serialises 6 significant
// digits, and a wrong factor such as 0.25 -> 0.6 moves chroma by 0.05.
const CHROMA_TOLERANCE = 0.003;
const DERIVED_TOKENS: Record<string, { tone: number | 'fill'; chroma: number }> = {
  '--zk-color-primary-container': { tone: 0.92, chroma: 0.25 },
  '--zk-color-on-primary-container': { tone: 0.23, chroma: 0.45 },
  '--zk-color-secondary': { tone: 0.54, chroma: 0.41 },
  '--zk-color-secondary-container': { tone: 0.87, chroma: 0.41 * 0.48 },
  '--zk-color-on-secondary-container': { tone: 0.235, chroma: 0.41 * 0.48 },
  '--zk-color-inverse-primary': { tone: 0.81, chroma: 0.58 },
  '--zk-color-primary-fill': { tone: 'fill', chroma: 1 },
  '--zk-color-secondary-fill': { tone: 'fill', chroma: 0.41 },
  '--zk-codeeditor-dark-border-color-focus': { tone: 0.8, chroma: 1 },
};

interface Oklch { l: number; c: number; h: number }

/** A computed colour: oklch() as serialised by the browser, or an sRGB colour converted to OKLCH. */
function parseOklch(computed: string): Oklch {
  const oklch = computed.match(/^oklch\(([\d.]+) ([\d.]+) ([\d.]+)/);
  if (oklch) return { l: +oklch[1], c: +oklch[2], h: +oklch[3] };
  const rgb = computed.match(/^rgba?\((\d+), (\d+), (\d+)/);
  if (!rgb) throw new Error(`unexpected colour serialisation: ${computed}`);
  return rgbToOklch([+rgb[1], +rgb[2], +rgb[3]]);
}

function rgbToOklch([r8, g8, b8]: number[]): Oklch {
  const lin = (v: number) => { const s = v / 255; return s <= 0.04045 ? s / 12.92 : ((s + 0.055) / 1.055) ** 2.4; };
  const [r, g, b] = [lin(r8), lin(g8), lin(b8)];
  const l = Math.cbrt(0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b);
  const m = Math.cbrt(0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b);
  const s = Math.cbrt(0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b);
  const L = 0.2104542553 * l + 0.793617785 * m - 0.0040720468 * s;
  const a = 1.9779984951 * l - 2.428592205 * m + 0.4505937099 * s;
  const bb = 0.0259040371 * l + 0.7827717662 * m - 0.808675766 * s;
  return { l: L, c: Math.hypot(a, bb), h: (Math.atan2(bb, a) * 180 / Math.PI + 360) % 360 };
}

function hueDistance(a: number, b: number): number {
  const d = Math.abs(a - b) % 360;
  return Math.min(d, 360 - d);
}

async function readComputedTokens(page: Page): Promise<Record<string, string>> {
  return page.evaluate((tokens) => {
    const out: Record<string, string> = {};
    for (const token of tokens) {
      const probe = document.createElement('div');
      probe.style.backgroundColor = `var(${token})`;
      document.body.appendChild(probe);
      out[token] = getComputedStyle(probe).backgroundColor;
      probe.remove();
    }
    return out;
  }, Object.keys(DERIVED_TOKENS));
}

test.describe('brand-derived-tokens', () => {
  test('derived primary-family tokens follow the copper seed', async ({ page }) => {
    await page.goto('/button.zul', { waitUntil: 'networkidle' });
    const stock = await readComputedTokens(page);
    await page.evaluate((brand) => { document.documentElement.dataset.brand = brand; }, BRAND);
    await nextFrames(page);
    const copper = await readComputedTokens(page);

    const seed = rgbToOklch(COPPER_SEED_RGB);
    const failures: string[] = [];
    for (const [token, { tone, chroma }] of Object.entries(DERIVED_TOKENS)) {
      const expectedL = tone === 'fill' ? Math.min(seed.l, FILL_LIGHTNESS_CAP) : tone;
      const actual = parseOklch(copper[token]);
      if (copper[token] === stock[token]) failures.push(`${token} is unchanged by data-brand: ${copper[token]}`);
      if (Math.abs(actual.l - expectedL) > TONE_TOLERANCE) {
        failures.push(`${token} L=${actual.l.toFixed(4)}, spec tone ${expectedL.toFixed(4)} (+-${TONE_TOLERANCE})`);
      }
      if (Math.abs(actual.c - seed.c * chroma) > CHROMA_TOLERANCE) {
        failures.push(`${token} C=${actual.c.toFixed(4)}, seed C ${seed.c.toFixed(4)} x ${chroma.toFixed(4)} = ${(seed.c * chroma).toFixed(4)} (+-${CHROMA_TOLERANCE})`);
      }
      if (hueDistance(actual.h, seed.h) > HUE_TOLERANCE_DEGREES) {
        failures.push(`${token} hue=${actual.h.toFixed(2)}, copper seed hue ${seed.h.toFixed(2)} (+-${HUE_TOLERANCE_DEGREES})`);
      }
    }
    expect(failures, failures.join('\n')).toEqual([]);
  });
});
