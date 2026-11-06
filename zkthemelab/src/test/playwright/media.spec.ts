import { test, expect, type Page } from '@playwright/test';

// Media components: the video, camera and barcode scanner size their media to the component box.
// The `media` project grants a fake camera and microphone, which camera and barcodescanner need.
// Each component keeps its own fixture and helpers inside its describe.

declare const zk: any;

// Guard for zkmax med/css/video.css (ZK-6112, zkdemo B13 Z2): the <video> fills the size set by
// width/height, clipToFit crops instead of letterboxing, and dimBackground keeps the video above
// its own mask.
// Fixture: web/pv/video-guard.zul
test.describe('video', () => {
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
});

// Guard for zkmax med/css/camera.css (ZK-6112, zkdemo B14 Z1/Z2/Z9): the camera stays hidden until
// its preview starts, the video fills the camera's width, and the recording hint shows the state.
// Runs with Chrome's fake 640x480 camera (see the `media` project in playwright.config.ts).
// Fixture: web/pv/camera-guard.zul
test.describe('camera', () => {
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

  // _forced-colors.css (2e): forced colors map the mask's background-color fill to Canvas, which
  // hides the hint; the rule re-points it to CanvasText.
  test('the recording hint stays visible in forced colors', async ({ page }) => {
    await startPreview(page);
    await page.evaluate(() => zk.$('$cam').start());
    await expect(page.locator('.z-camera-recording')).toBeVisible();
    await page.emulateMedia({ forcedColors: 'active' });
    const { canvasText, fill } = await page.evaluate(() => {
      const probe = document.createElement('span');
      probe.style.color = 'CanvasText';
      document.body.append(probe);
      const canvasText = getComputedStyle(probe).color;
      probe.remove();
      return { canvasText, fill: getComputedStyle(document.querySelector('.z-camera-recording')!, '::before').backgroundColor };
    });
    expect(fill).toBe(canvasText);
  });
});

// Guard for zkmax barscanner/css/barcodescanner.css (ZK-6112, zkdemo B14 Z3): the video fills the
// scanner and the detection canvas lies over it. Runs with Chrome's fake 640x480 camera (see the
// `media` project in playwright.config.ts).
// Fixture: web/pv/barcodescanner-guard.zul
test.describe('barcodescanner', () => {
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
});
