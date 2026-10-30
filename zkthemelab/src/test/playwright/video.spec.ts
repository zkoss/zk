import { test, expect } from '@playwright/test';

// Guard for zkmax med/css/video.css (ZK-6112, zkdemo B13 Z2): the <video> fills the size set by
// width/height, clipToFit crops instead of letterboxing, and dimBackground keeps the video above
// its own mask.
// Fixture: web/pv/video-guard.zul

const FIXTURE = '/pv/video-guard.zul';

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

test('width and height size the video, not only the outer box', async ({ page }) => {
  const box = await page.locator('.sized .z-video-real').boundingBox();
  expect(box).toMatchObject({ width: 200, height: 100 });
});

test('clipToFit crops the video to the box', async ({ page }) => {
  const fit = await page.locator('.clip .z-video-real')
    .evaluate((el) => getComputedStyle(el).objectFit);
  expect(fit).toBe('cover');
});

test('dimBackground keeps the video above the mask', async ({ page }) => {
  const real = page.locator('.dim .z-video-real');
  const box = (await real.boundingBox())!;
  const hit = await page.evaluate(([x, y]) => {
    const el = document.elementFromPoint(x, y);
    return el ? `${el.tagName.toLowerCase()}.${el.className}` : 'none';
  }, [box.x + box.width / 2, box.y + box.height / 2]);
  expect(hit).toContain('z-video-real');
});
