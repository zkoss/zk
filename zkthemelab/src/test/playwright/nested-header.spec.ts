import { test, expect, Locator, Page } from '@playwright/test';

// Guard for zul window.css, groupbox.css and panel.css: a state class on the root of a window, groupbox or panel
// (header-less, no border, overlapped, modal, highlighted, 3d) styles the header or content of that container only.
// The container in the default state nested inside it keeps its own look, so each case holds an inner container that
// no state class applies to. The outer container still wears the rule: the second assertion of each test.
// ZK adds z-window-noheader / z-groupbox-notitle only when the container renders no header, so a descendant rule on
// that class can only ever hit a nested container.
// Fixture: web/pv/nested-header-guard.zul

const FIXTURE = '/pv/nested-header-guard.zul';

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
});

const style = (locator: Locator, property: string) =>
  locator.evaluate((el, prop) => getComputedStyle(el).getPropertyValue(prop), property);

/** The computed value of `property` when it is set to `value` (a `var(--zk-…)` or an expression over tokens). */
async function resolve(page: Page, property: string, value: string): Promise<string> {
  return page.evaluate(([prop, val]) => {
    const probe = document.createElement('span');
    probe.style.setProperty(prop, val);
    document.body.appendChild(probe);
    const resolved = getComputedStyle(probe).getPropertyValue(prop);
    probe.remove();
    return resolved;
  }, [property, value]);
}

/** The header, content or head of the outer container and of the container nested in it. */
const parts = (page: Page, outer: string, root: string, part: string) => ({
  outer: page.locator(`.${outer}.${root} > .${root}-${part}`),
  inner: page.locator(`.${outer} .nh-inner > .${root}-${part}`),
});

test('window — a titled window nested in a header-less window keeps its title bar', async ({ page }) => {
  const inner = page.locator('.nh-noheader .z-window .z-window-header');
  await expect(inner).toHaveCount(1);
  expect(await style(inner, 'display')).not.toBe('none');
});

test('groupbox — a captioned groupbox nested in a title-less groupbox keeps its header', async ({ page }) => {
  const inner = page.locator('.nh-notitle .z-groupbox .z-groupbox-header');
  await expect(inner).toHaveCount(1);
  expect(await style(inner, 'display')).not.toBe('none');
});

test('groupbox — a captioned groupbox nested in a title-less groupbox keeps its square top corners', async ({ page }) => {
  const content = page.locator('.nh-notitle .z-groupbox .z-groupbox-content');
  await expect(content).toHaveCount(1);
  expect(await style(content, 'border-top-left-radius')).toBe('0px');
});

test('window — a titled window nested in a borderless, header-less window keeps the padding of its content', async ({ page }) => {
  const { outer, inner } = parts(page, 'nh-noborder-noheader', 'z-window', 'content');
  await expect(inner).toHaveCount(1);
  expect(await style(inner, 'padding-top'), 'nested').toBe(await resolve(page, 'padding-top', 'var(--zk-spacing-4)'));
  expect(await style(outer, 'padding-top'), 'the chrome-less window itself').toBe('0px');
});

test('window — a titled window nested in a borderless window keeps the line under its title bar', async ({ page }) => {
  const { outer, inner } = parts(page, 'nh-noborder', 'z-window', 'header');
  await expect(inner).toHaveCount(1);
  expect(await style(inner, 'border-bottom-width'), 'nested').toBe('1px');
  expect(await style(outer, 'border-bottom-width'), 'the borderless window itself').toBe('0px');
});

for (const mode of ['overlapped', 'modal']) {
  test(`window — a window nested in an ${mode} window keeps the default cursor on its title bar`, async ({ page }) => {
    const { outer, inner } = parts(page, `nh-${mode}`, 'z-window', 'header');
    await expect(inner).toHaveCount(1);
    expect(await style(inner, 'cursor'), 'nested').toBe('default');
    expect(await style(outer, 'cursor'), `the ${mode} window itself`).toBe('move');
  });
}

test('window — a window nested in a highlighted window keeps the surface title bar', async ({ page }) => {
  const { outer, inner } = parts(page, 'nh-highlighted', 'z-window', 'header');
  await expect(inner).toHaveCount(1);
  expect(await style(inner, 'background-color'), 'nested').toBe(await resolve(page, 'background-color', 'var(--zk-window-bg)'));
  expect(await style(inner, 'color')).toBe(await resolve(page, 'color', 'var(--zk-window-header-fg)'));
  expect(await style(outer, 'background-color'), 'the highlighted window itself').toBe(await resolve(page, 'background-color', 'var(--zk-color-primary-container)'));
});

test('window — a window with a caption nested in a highlighted window keeps the colour of its own caption', async ({ page }) => {
  const caption = (scope: string, part: string) => page.locator(`${scope} > .z-window-header .${part}`);
  const outer = '.nh-highlighted-caption';
  const inner = '.nh-highlighted-caption .nh-inner';
  await expect(caption(inner, 'z-caption-content')).toHaveCount(1);
  const own = await resolve(page, 'color', 'var(--zk-window-header-fg)');
  const onContainer = await resolve(page, 'color', 'var(--zk-color-on-primary-container)');

  for (const part of ['z-caption-content', 'z-label']) {
    expect(await style(caption(inner, part), 'color'), `nested ${part}`).toBe(own);
    expect(await style(caption(outer, part), 'color'), `${part} of the highlighted window itself`).toBe(onContainer);
  }
});

test('groupbox — a groupbox nested in a 3d groupbox keeps the header of the default mold', async ({ page }) => {
  const { outer, inner } = parts(page, 'nh-3d', 'z-groupbox', 'header');
  await expect(inner).toHaveCount(1);
  expect(await style(inner, 'background-color'), 'nested').toBe(await resolve(page, 'background-color', 'var(--zk-groupbox-bg)'));
  expect(await style(inner, 'border-bottom-color'), 'nested').toBe(await resolve(page, 'border-bottom-color', 'var(--zk-groupbox-border-color)'));
  expect(await style(outer, 'background-color'), 'the 3d groupbox itself').toBe(await resolve(page, 'background-color', 'var(--zk-color-surface-container-low)'));
});

test('panel — a titled panel nested in a borderless panel keeps the line under its head', async ({ page }) => {
  const outer = page.locator('.nh-panel-noborder > .z-panel-head');
  const inner = page.locator('.nh-panel-noborder .nh-inner > .z-panel-head');
  await expect(inner).toHaveCount(1);
  expect(await style(inner, 'border-bottom-width'), 'nested').toBe('1px');
  expect(await style(outer, 'border-bottom-width'), 'the borderless panel itself').toBe('0px');
});
