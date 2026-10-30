import { test, expect, type Page } from '@playwright/test';

// Guard for zkmax med/css/camera.css (ZK-6112, zkdemo B14 Z1/Z2/Z9): the camera stays hidden until
// its preview starts, the video fills the camera's width, and the recording hint shows the state.
// Runs with Chrome's fake 640x480 camera (see the `camera` project in playwright.config.ts).
// Fixture: web/pv/camera-guard.zul

declare const zk: any;

const FIXTURE = '/pv/camera-guard.zul';
const MIN_HINT_PX = 16;

const camera = (page: Page) => page.locator('.z-camera');

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

async function startPreview(page: Page) {
  await page.evaluate(() => zk.$('$cam').requestCamera(undefined, 0));
  await expect(camera(page)).toHaveClass(/z-camera-preview/);
  await page.waitForFunction(() => document.querySelector<HTMLVideoElement>('.z-camera-real')!.videoWidth > 0);
}

test('the camera stays hidden until the preview starts', async ({ page }) => {
  await expect(camera(page)).toBeHidden();
});

test('the video fills the camera width', async ({ page }) => {
  await startPreview(page);
  const { inner, video } = await camera(page).evaluate((el) => ({
    inner: el.clientWidth,
    video: el.querySelector('.z-camera-real')!.getBoundingClientRect().width,
  }));
  expect(video).toBe(inner);
});

for (const [action, state] of [['start', 'recording'], ['pause', 'pause']] as const) {
  test(`${action}() shows the ${state} hint`, async ({ page }) => {
    await startPreview(page);
    await page.evaluate(() => zk.$('$cam').start());
    if (action === 'pause') await page.evaluate(() => zk.$('$cam').pause());
    const hint = page.locator(`.z-camera-${state}`);
    await expect(hint).toBeVisible();
    const box = (await hint.boundingBox())!;
    const cam = (await camera(page).boundingBox())!;
    expect(box.width).toBeGreaterThanOrEqual(MIN_HINT_PX);
    expect(box.height).toBeGreaterThanOrEqual(MIN_HINT_PX);
    // Drawn over the video, inside the camera.
    expect(box.x + box.width).toBeLessThanOrEqual(cam.x + cam.width);
    expect(box.y).toBeGreaterThanOrEqual(cam.y);
  });
}
