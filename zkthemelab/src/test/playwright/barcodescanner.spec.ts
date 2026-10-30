import { test, expect } from '@playwright/test';

// Guard for zkmax barscanner/css/barcodescanner.css (ZK-6112, zkdemo B14 Z3): the video fills the
// scanner and the detection canvas lies over it. Runs with Chrome's fake 640x480 camera (see the
// `barcodescanner` project in playwright.config.ts).
// Fixture: web/pv/barcodescanner-guard.zul

const FIXTURE = '/pv/barcodescanner-guard.zul';

test('the video fills the scanner and the canvas covers the video', async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.waitForFunction(() => document.querySelector<HTMLVideoElement>('.z-barcodescanner-video')!.videoWidth > 0);
  const { inner, video, canvas } = await page.locator('.z-barcodescanner').evaluate((el) => {
    const r = (s: string) => {
      const b = el.querySelector(s)!.getBoundingClientRect();
      return { x: b.x, y: b.y, width: b.width, height: b.height };
    };
    return { inner: { width: el.clientWidth, height: el.clientHeight }, video: r('.z-barcodescanner-video'), canvas: r('.z-barcodescanner-canvas') };
  });
  expect({ width: video.width, height: video.height }).toEqual(inner);
  expect(canvas).toEqual(video);
});
