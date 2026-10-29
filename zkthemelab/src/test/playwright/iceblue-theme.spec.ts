import { test, expect } from '@playwright/test';

// IceBlue on ZK 11: guards against Marble wiring leaking into a theme jar (ZK-6112).
//   1. StandardThemeProvider inserts Marble's reset.css for every theme; under iceblue it resolves to
//      ~./iceblue/zul/css/reset.css, which the jar does not ship, so every page logs a 404.
//   2. zk.wcs no longer lists ~./zul/font/font-awesome.css.dsp, the only sheet that defines
//      iceblue's z-icon-* glyphs, and zul no longer ships the ~./zul/less/font/ files it points at.
// The iceblue jar is a runtimeOnly dependency (build.gradle); the zktheme cookie selects it per
// request (ThemeFns.getCurrentTheme reads the cookie before org.zkoss.theme.preferred).

const PAGE = '/smoke.zul';

test.beforeEach(async ({ context, baseURL }) => {
  await context.addCookies([{ name: 'zktheme', value: 'iceblue', url: baseURL! }]);
});

test('the iceblue cookie serves the iceblue theme', async ({ page }) => {
  const resp = await page.goto(PAGE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  // StandardThemeProvider injects the theme into the zk.wcs URL (/_zkiju-iceblue/zul/css/zk.wcs), and the
  // bundle must hold rules: a theme name in a URL does not prove that any CSS loaded
  const wcs = await page.evaluate(() => [...document.styleSheets]
    .filter((s) => /\/_zkiju-iceblue\/zul\/css\/zk\.wcs/.test(s.href ?? ''))
    .map((s) => s.cssRules.length));
  expect(wcs.length, 'one iceblue zk.wcs link').toBe(1);
  expect(wcs[0], 'rules in the iceblue zk.wcs').toBeGreaterThan(0);
});

test('an iceblue page requests no missing resource', async ({ page }) => {
  const failed: string[] = [];
  page.on('response', (r) => {
    if (r.status() >= 400) failed.push(`${r.status()} ${new URL(r.url()).pathname}`);
  });
  await page.goto(PAGE, { waitUntil: 'networkidle' });
  await page.evaluate(() => document.fonts.ready);
  expect(failed).toEqual([]);
});

test('iceblue draws the z-icon-* glyphs', async ({ page }) => {
  await page.goto(PAGE, { waitUntil: 'networkidle' });
  await page.evaluate(() => document.fonts.ready);
  const icon = await page.locator('.z-combobox-icon').first().evaluate((el) => {
    const before = getComputedStyle(el, '::before');
    const families = before.fontFamily.split(',').map((f) => f.trim().replace(/["']/g, ''));
    const faces = [...document.fonts].filter((f) => families.includes(f.family.replace(/["']/g, '')));
    return {
      cls: el.className,
      content: before.content,
      fontFamily: before.fontFamily,
      faces: faces.map((f) => `${f.family}:${f.status}`),
    };
  });
  expect(icon.content, JSON.stringify(icon)).not.toMatch(/^(none|normal|"")$/);
  expect(icon.faces.some((f) => f.endsWith(':loaded')), JSON.stringify(icon)).toBe(true);
});
