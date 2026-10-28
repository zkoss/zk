import { test, expect, type Locator } from '@playwright/test';

// Component Theme Variables: the --zk-<comp>-* knob contract, asserted on each
// component's own preview page (there is no dedicated fixture page).
// One technique covers CTV-1/2/5: read the stock value, inject an unlayered :root
// override after the theme bundle, read again. A knob declared on the component
// element, or a hardcoded value, would shadow the inherited :root value and fail
// the second read. Region isolation is plain CSS inheritance and is not re-tested.
// Floating widgets (messagebox, popup, drawer, ...) are reparented to the page root, so
// :root is their only override path. A state the preview page does not render is created
// with a real click or the client widget API, as the comment in that test says.
// See doc/spec/component-theme-variables.md.

const PURPLE = 'rgb(103, 80, 164)'; // #6750a4
const bgOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).backgroundColor);
const radiusOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).borderTopLeftRadius);
const colorOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).color);
const borderColorOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).borderTopColor);

test.describe('Component Theme Variables (borrowed preview pages)', () => {
  test('button — stock values, then :root override reaches it, disabled keeps its treatment', async ({ page }) => {
    await page.goto('/button.zul', { waitUntil: 'networkidle' });
    await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important;animation:none!important}' });
    const btn = page.locator('.z-button:not([disabled])').first();
    const disabled = page.locator('.z-button[disabled]').first();

    expect(await radiusOf(btn)).toBe('4px'); // stock --zk-shape-button
    expect(await bgOf(btn)).toBe('rgb(55, 111, 208)'); // stock --zk-color-primary
    const disabledBg = await bgOf(disabled);

    await page.addStyleTag({ content: ':root{--zk-button-bg:#6750a4; --zk-button-radius:9999px}' });
    expect(await radiusOf(btn)).toBe('9999px');
    expect(await bgOf(btn)).toBe(PURPLE);
    expect(await bgOf(disabled)).toBe(disabledBg); // CTV-7: disabled does not read the fill knob
  });

  test('messagebox — stock values, then :root override reaches the page-root dialog', async ({ page }) => {
    await page.goto('/messagebox.zul', { waitUntil: 'networkidle' });
    await page.getByRole('button', { name: 'Information', exact: true }).click();
    const dlg = page.locator('.z-messagebox-window').first();
    await expect(dlg).toBeVisible();
    await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important;animation:none!important}' });
    const icon = dlg.locator('.z-messagebox-information').first();

    expect(await radiusOf(dlg)).toBe('4px'); // stock --zk-shape-dialog
    expect(await bgOf(dlg)).toBe('rgb(255, 255, 255)'); // stock --zk-color-surface
    const iconColor = await colorOf(icon);

    await page.addStyleTag({ content: ':root{--zk-messagebox-bg:#6750a4}' });
    expect(await bgOf(dlg)).toBe(PURPLE);
    expect(await colorOf(icon)).toBe(iconColor); // CTV-7: status icon color is not knob-driven
  });
});

// ── Batch 1 (S2): one default instance per component, assertions on the element or a plain child.
// Each test: read stock values, inject the region's knobs at :root after the theme bundle, read again.
const SCOPED_PURPLE = PURPLE;
const borderBottomColorOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).borderBottomColor);
const pseudoBgOf = (loc: Locator, pseudo: string) =>
  loc.evaluate((el, p) => getComputedStyle(el, p).backgroundColor, pseudo);

// Preview pages whose file name differs from the component name.
const PAGES: Record<string, string> = {"input": "textbox"};

test.describe('Component Theme Variables (borrowed preview pages, batch 1)', () => {
  test.beforeEach(async ({ page }, testInfo) => {
    const url = testInfo.title.split(' — ')[0];
    await page.goto(`/${PAGES[url] ?? url}.zul`, { waitUntil: 'networkidle' });
    if (url === 'codeeditor') await page.waitForSelector('.z-codeeditor .cm-editor');
    await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important;animation:none!important}' });
  });

  test('input — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-textbox').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-input-radius:12px;--zk-input-border-color:#6750a4;--zk-input-border-color-hover:#6750a4}' });
    expect(await radiusOf(c0)).toBe('12px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('window — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-window').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await bgOf(c1)).toBe('rgb(255, 255, 255)'); // stock
    await page.addStyleTag({ content: ':root{--zk-window-radius:16px;--zk-window-bg:#eef2ff;--zk-window-border-color:#6750a4}' });
    expect(await radiusOf(c0)).toBe('16px');
    expect(await bgOf(c1)).toBe('rgb(238, 242, 255)');
  });

  test('grid — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-grid').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('6px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    await page.addStyleTag({ content: ':root{--zk-grid-radius:0px;--zk-grid-border-color:#6750a4;--zk-grid-stripe-bg:#efe6ff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('listbox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-listbox').first();
    const c0 = root;
    const c1 = root;
    const c2 = root.locator('.z-listitem-selected').first();
    expect(await radiusOf(c0)).toBe('6px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await bgOf(c2)).toBe('oklch(0.87 0.0317461 260.564)'); // stock
    await page.addStyleTag({ content: ':root{--zk-listbox-radius:0px;--zk-listbox-border-color:#6750a4;--zk-listbox-selected-bg:#6750a4;--zk-listbox-selected-fg:#ffffff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c2)).toBe(SCOPED_PURPLE);
  });

  test('tree — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-tree').first();
    const c0 = root;
    const c1 = root;
    const c2 = root.locator('.z-treerow-selected').first();
    expect(await radiusOf(c0)).toBe('6px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await bgOf(c2)).toBe('oklch(0.87 0.0317461 260.564)'); // stock
    await page.addStyleTag({ content: ':root{--zk-tree-radius:0px;--zk-tree-border-color:#6750a4;--zk-tree-selected-bg:#6750a4;--zk-tree-selected-fg:#ffffff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c2)).toBe(SCOPED_PURPLE);
  });

  test('panel — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-panel').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('6px'); // stock
    expect(await bgOf(c1)).toBe('rgb(255, 255, 255)'); // stock
    await page.addStyleTag({ content: ':root{--zk-panel-radius:0px;--zk-panel-border-color:#6750a4;--zk-panel-bg:#eef2ff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await bgOf(c1)).toBe('rgb(238, 242, 255)');
  });

  test('groupbox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-groupbox').first();
    const c0 = root;
    // The frame border is drawn by the header and the content, not by the root (B95-ZK-4505),
    // so read its colour from the header.
    const c1 = root.locator('.z-groupbox-header').first();
    expect(await radiusOf(c0)).toBe('6px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    await page.addStyleTag({ content: ':root{--zk-groupbox-radius:0px;--zk-groupbox-border-color:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('combobox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-combobox-input').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-combobox-radius:0px;--zk-combobox-border-color:#6750a4;--zk-combobox-selected-bg:#6750a4;--zk-combobox-selected-fg:#ffffff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('datebox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-datebox').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-datebox-radius:0px;--zk-datebox-border-color:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('timebox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-timebox').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-timebox-radius:0px;--zk-timebox-border-color:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('spinner — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-spinner').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-spinner-radius:0px;--zk-spinner-border-color:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('bandbox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-bandbox').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-bandbox-radius:0px;--zk-bandbox-border-color:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('daterangebox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-daterangebox').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-daterangebox-radius:0px;--zk-daterangebox-border-color:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('menubar — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-menubar').first();
    const c0 = root;
    expect(await bgOf(c0)).toBe('rgb(240, 244, 250)'); // stock
    await page.addStyleTag({ content: ':root{--zk-menubar-bg:#6750a4}' });
    expect(await bgOf(c0)).toBe(SCOPED_PURPLE);
  });

  test('avatar — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-avatar').first();
    const c0 = root;
    expect(await bgOf(c0)).toBe('oklch(0.92 0.0403278 260.564)'); // stock
    await page.addStyleTag({ content: ':root{--zk-avatar-bg:#6750a4;--zk-avatar-fg:#ffffff}' });
    expect(await bgOf(c0)).toBe(SCOPED_PURPLE);
  });

  test('rating — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-rating').first();
    const c0 = root.locator('.z-rating-selected').first();
    expect(await colorOf(c0)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-rating-accent:#6750a4}' });
    expect(await colorOf(c0)).toBe(SCOPED_PURPLE);
  });

  test('progressmeter — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-progressmeter').first();
    const c0 = root;
    const c1 = root.locator('.z-progressmeter-image').first();
    expect(await bgOf(c0)).toBe('oklch(0.92 0.0403278 260.564)'); // stock
    expect(await bgOf(c1)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-progressmeter-bg:#efe6ff;--zk-progressmeter-fill:#6750a4}' });
    expect(await bgOf(c0)).toBe('rgb(239, 230, 255)');
    expect(await bgOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('toolbar — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-toolbar').first();
    const c0 = root;
    const c1 = root;
    expect(await bgOf(c0)).toBe('rgb(255, 255, 255)'); // stock
    expect(await borderBottomColorOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    await page.addStyleTag({ content: ':root{--zk-toolbar-bg:#eef2ff;--zk-toolbar-border-color:#6750a4}' });
    expect(await bgOf(c0)).toBe('rgb(238, 242, 255)');
    expect(await borderBottomColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('slider — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-slider').first();
    const c0 = root.locator('.z-slider-center').first();
    const c1 = root.locator('.z-slider-center').first();
    const c2 = root.locator('.z-slider-button').first();
    expect(await radiusOf(c0)).toBe('9999px'); // stock
    expect(await bgOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await bgOf(c2)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-slider-track-bg:#efe6ff;--zk-slider-accent:#6750a4;--zk-slider-radius:0px}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await bgOf(c1)).toBe('rgb(239, 230, 255)');
    expect(await bgOf(c2)).toBe(SCOPED_PURPLE);
  });

  test('rangeslider — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-rangeslider').first();
    const c0 = root.locator('.z-rangeslider-track').first();
    const c1 = root.locator('.z-rangeslider-track').first();
    const c2 = root.locator('.z-sliderbuttons-area').first();
    const c3 = root.locator('.z-sliderbuttons-button').first();
    expect(await radiusOf(c0)).toBe('9999px'); // stock
    expect(await bgOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await bgOf(c2)).toBe('rgb(55, 111, 208)'); // stock
    expect(await bgOf(c3)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-rangeslider-track-bg:#efe6ff;--zk-rangeslider-accent:#6750a4;--zk-rangeslider-radius:0px}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await bgOf(c1)).toBe('rgb(239, 230, 255)');
    expect(await bgOf(c2)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c3)).toBe(SCOPED_PURPLE);
  });

  test('multislider — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-multislider').first();
    const c0 = root.locator('.z-multislider-track').first();
    const c1 = root.locator('.z-multislider-track').first();
    const c2 = root.locator('.z-sliderbuttons-area').first();
    const c3 = root.locator('.z-sliderbuttons-button').first();
    expect(await radiusOf(c0)).toBe('9999px'); // stock
    expect(await bgOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await bgOf(c2)).toBe('rgb(55, 111, 208)'); // stock
    expect(await bgOf(c3)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-multislider-track-bg:#efe6ff;--zk-multislider-accent:#6750a4;--zk-multislider-radius:0px}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await bgOf(c1)).toBe('rgb(239, 230, 255)');
    expect(await bgOf(c2)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c3)).toBe(SCOPED_PURPLE);
  });

  test('a — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-a').first();
    const c0 = root;
    expect(await colorOf(c0)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-a-fg:#6750a4}' });
    expect(await colorOf(c0)).toBe(SCOPED_PURPLE);
  });

  test('timepicker — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-timepicker').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-timepicker-radius:0px;--zk-timepicker-border-color:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('cascader — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-cascader').first();
    const c0 = root;
    const c1 = root;
    const c2 = root.locator('.z-cascader-label').first();
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    expect(await colorOf(c2)).toBe('rgba(0, 0, 0, 0.87)'); // stock
    await page.addStyleTag({ content: ':root{--zk-cascader-radius:0px;--zk-cascader-border-color:#6750a4;--zk-cascader-fg:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c2)).toBe(SCOPED_PURPLE);
  });

  test('searchbox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-searchbox').first();
    const c0 = root;
    const c1 = root;
    const c2 = root.locator('.z-searchbox-label').first();
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    expect(await colorOf(c2)).toBe('rgba(0, 0, 0, 0.87)'); // stock
    await page.addStyleTag({ content: ':root{--zk-searchbox-radius:0px;--zk-searchbox-border-color:#6750a4;--zk-searchbox-fg:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c2)).toBe(SCOPED_PURPLE);
  });

  test('colorbox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-colorbox').first();
    const c0 = root;
    const c1 = root;
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-colorbox-radius:0px;--zk-colorbox-border-color:#6750a4;--zk-colorbox-border-color-hover:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('biglistbox — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-biglistbox').first();
    const c0 = root;
    const c1 = root;
    const c2 = root.locator('.z-biglistbox-header').first();
    expect(await radiusOf(c0)).toBe('6px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await colorOf(c2)).toBe('rgba(0, 0, 0, 0.6)'); // stock
    await page.addStyleTag({ content: ':root{--zk-biglistbox-radius:0px;--zk-biglistbox-border-color:#6750a4;--zk-biglistbox-header-fg:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c2)).toBe(SCOPED_PURPLE);
  });

  test('breadcrumb — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-breadcrumb').first();
    const c0 = root.locator('.z-breadcrumbitem > a').first();
    const c1 = root.locator('.z-breadcrumbitem > span').first();
    expect(await colorOf(c0)).toBe('rgba(0, 0, 0, 0.6)'); // stock
    expect(await colorOf(c1)).toBe('rgba(0, 0, 0, 0.87)'); // stock
    await page.addStyleTag({ content: ':root{--zk-breadcrumb-fg:#6750a4;--zk-breadcrumb-fg-hover:#6750a4;--zk-breadcrumb-current-fg:#6750a4}' });
    expect(await colorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('carousel — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-carousel').first();
    const c0 = root.locator('.z-carousel-arrow').first();
    const c1 = root.locator('.z-carousel-indicator-active').first();
    expect(await bgOf(c0)).toBe('rgba(0, 0, 0, 0.5)'); // stock
    expect(await pseudoBgOf(c1, '::before')).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-carousel-arrow-bg:#6750a4;--zk-carousel-indicator-active-bg:#6750a4}' });
    expect(await bgOf(c0)).toBe(SCOPED_PURPLE);
    expect(await pseudoBgOf(c1, '::before')).toBe(SCOPED_PURPLE);
  });

  test('codeeditor — stock values, then :root override reaches it', async ({ page }) => {
    const root = page.locator('.z-codeeditor').first();
    const c0 = root;
    const c1 = root;
    const c2 = root.locator('.cm-gutters').first();
    expect(await bgOf(c0)).toBe('rgb(255, 255, 255)'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    expect(await bgOf(c2)).toBe('rgb(247, 249, 252)'); // stock
    await page.addStyleTag({ content: ':root{--zk-codeeditor-bg:#6750a4;--zk-codeeditor-border-color:#6750a4;--zk-codeeditor-gutter-bg:#6750a4}' });
    expect(await bgOf(c0)).toBe(SCOPED_PURPLE);
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c2)).toBe(SCOPED_PURPLE);
  });

});

// ── Batch 2 (S2): floating widgets opened from their own preview page's buttons, and selectbox.
// Floating widgets are reparented to the page root, so :root is the only override path.
test.describe('Component Theme Variables (borrowed preview pages, batch 2)', () => {
  const open = async (page: import('@playwright/test').Page, url: string, button: string) => {
    await page.goto(url, { waitUntil: 'networkidle' });
    await page.getByRole('button', { name: button, exact: true }).first().click();
  };
  const settle = (page: import('@playwright/test').Page) =>
    page.addStyleTag({ content: '*,*::before,*::after{transition:none!important;animation:none!important}' });

  test('popup — stock values, then :root override reaches it', async ({ page }) => {
    await open(page, '/popup.zul', 'Show Popup');
    const popup = page.locator('.z-popup.z-popup-open');
    await expect(popup).toBeVisible();
    await settle(page);
    // The surface (radius, fill, border) is on .z-popup-content; the outer .z-popup is a square box.
    const pop = popup.locator('.z-popup-content');

    expect(await radiusOf(pop)).toBe('4px'); // stock --zk-shape-corner-extra-small
    expect(await bgOf(pop)).toBe('rgb(255, 255, 255)'); // stock --zk-color-surface
    expect(await borderColorOf(pop)).toBe('rgba(0, 0, 0, 0.12)'); // stock --zk-color-outline-variant

    await page.addStyleTag({ content: ':root{--zk-popup-bg:#6750a4}' });
    expect(await bgOf(pop)).toBe(PURPLE);
  });

  test('drawer — stock values, then :root override reaches it', async ({ page }) => {
    await open(page, '/drawer.zul', 'Open Left');
    const real = page.locator('.z-drawer.z-drawer-open .z-drawer-real');
    await expect(real).toBeVisible();
    await settle(page);
    const header = real.locator('.z-drawer-header').first();

    expect(await bgOf(real)).toBe('rgb(255, 255, 255)'); // stock --zk-color-surface
    expect(await borderBottomColorOf(header)).toBe('rgba(0, 0, 0, 0.12)'); // stock --zk-color-outline-variant
    expect(await colorOf(header)).toBe('rgba(0, 0, 0, 0.87)'); // stock --zk-color-on-surface

    await page.addStyleTag({ content: ':root{--zk-drawer-header-fg:#6750a4}' });
    expect(await colorOf(header)).toBe(PURPLE);
  });

  test('coachmark — stock values, then :root override reaches it', async ({ page }) => {
    // coachmark.zul opens its first coachmark on load (visible by default, with a modal mask).
    await page.goto('/coachmark.zul', { waitUntil: 'networkidle' });
    const card = page.locator('.z-coachmark.z-coachmark-open .z-coachmark-content').first();
    await expect(card).toBeVisible();
    await settle(page);

    expect(await radiusOf(card)).toBe('6px'); // stock --zk-shape-card
    expect(await bgOf(card)).toBe('rgb(247, 249, 252)'); // stock --zk-color-surface-container-low
    expect(await colorOf(card)).toBe('rgba(0, 0, 0, 0.87)'); // stock --zk-color-on-surface

    await page.addStyleTag({ content: ':root{--zk-coachmark-bg:#6750a4}' });
    expect(await bgOf(card)).toBe(PURPLE);
  });

  test('confirmpopup — stock values, then :root override reaches it', async ({ page }) => {
    await open(page, '/confirmpopup.zul', 'Info');
    const pop = page.locator('.z-confirmpopup.z-confirmpopup-open');
    await expect(pop).toBeVisible();
    await settle(page);
    const ok = pop.locator('.z-confirmpopup-ok');
    const cancel = pop.locator('.z-confirmpopup-cancel');
    const icon = pop.locator('.z-confirmpopup-icon');

    expect(await radiusOf(pop)).toBe('6px'); // stock --zk-shape-card
    expect(await bgOf(pop)).toBe('rgb(247, 249, 252)'); // stock --zk-color-surface-container-low
    expect(await colorOf(pop)).toBe('rgba(0, 0, 0, 0.87)'); // stock --zk-color-on-surface
    expect(await bgOf(ok)).toBe('rgb(55, 111, 208)'); // stock --zk-color-primary
    expect(await colorOf(ok)).toBe('rgb(255, 255, 255)'); // stock --zk-color-on-primary
    expect(await borderColorOf(cancel)).toBe('rgba(0, 0, 0, 0.23)'); // stock --zk-color-outline
    expect(await colorOf(cancel)).toBe('rgba(0, 0, 0, 0.6)'); // stock --zk-color-on-surface-variant
    const iconColor = await colorOf(icon);

    await page.addStyleTag({ content: ':root{--zk-confirmpopup-bg:#6750a4}' });
    expect(await bgOf(pop)).toBe(PURPLE);
    expect(await colorOf(icon)).toBe(iconColor); // CTV-7: severity icon color is not knob-driven
  });

  // notification.zul and toast.zul render three static demo cards (info, warning, error) on load, so
  // the card a button opens is the LAST match, not the first. The Java APIs (Notification.show,
  // Toast.show) turn a null type into "info", so the card the public API opens is the Info one; the
  // Info notification reads the bg/fg knobs (like the toast), while warning/error keep their semantic colors.
  test('notification — stock values, then :root bg, fg and radius overrides reach the Info card', async ({ page }) => {
    await page.goto('/notification.zul', { waitUntil: 'networkidle' });
    const before = await page.locator('.z-notification').count();
    await page.getByRole('button', { name: 'Info', exact: true }).first().click();
    await expect(page.locator('.z-notification')).toHaveCount(before + 1);
    const card = page.locator('.z-notification-info .z-notification-content').last();
    await expect(card).toBeVisible();
    await settle(page);

    expect(await radiusOf(card)).toBe('4px'); // stock --zk-shape-corner-extra-small
    expect(await bgOf(card)).toBe('rgb(224, 232, 244)'); // stock --zk-color-surface-container-highest
    expect(await colorOf(card)).toBe('rgba(0, 0, 0, 0.87)'); // stock --zk-color-on-surface

    await page.addStyleTag({ content: ':root{--zk-notification-bg:#eef2ff;--zk-notification-fg:#1a1a2e;--zk-notification-radius:0px}' });
    expect(await radiusOf(card)).toBe('0px');
    expect(await bgOf(card)).toBe('rgb(238, 242, 255)');
    expect(await colorOf(card)).toBe('rgb(26, 26, 46)');
  });

  for (const type of ['Warning', 'Error']) {
    test(`notification — ${type} keeps its semantic colors under a :root bg/fg override`, async ({ page }) => {
      await page.goto('/notification.zul', { waitUntil: 'networkidle' });
      const before = await page.locator('.z-notification').count();
      await page.getByRole('button', { name: type, exact: true }).first().click();
      await expect(page.locator('.z-notification')).toHaveCount(before + 1);
      const card = page.locator(`.z-notification-${type.toLowerCase()} .z-notification-content`).last();
      await expect(card).toBeVisible();
      await settle(page);
      const bg = await bgOf(card);
      const fg = await colorOf(card);

      await page.addStyleTag({ content: ':root{--zk-notification-bg:#eef2ff;--zk-notification-fg:#1a1a2e}' });
      expect(await bgOf(card)).toBe(bg); // CTV-7: severity fill is not knob-driven
      expect(await colorOf(card)).toBe(fg);
    });
  }

  test('toast — stock values, then :root bg, fg, radius and accent overrides reach it', async ({ page }) => {
    await page.goto('/toast.zul', { waitUntil: 'networkidle' });
    const before = await page.locator('.z-toast').count();
    await page.getByRole('button', { name: 'Info', exact: true }).first().click();
    await expect(page.locator('.z-toast')).toHaveCount(before + 1);
    const card = page.locator('.z-toast-info .z-toast-content').last();
    const icon = page.locator('.z-toast-info > .z-toast-icon').last();
    await expect(card).toBeVisible();
    await settle(page);

    expect(await radiusOf(card)).toBe('4px'); // stock --zk-shape-corner-extra-small
    expect(await bgOf(card)).toBe('rgb(224, 232, 244)'); // stock toast surface
    expect(await colorOf(card)).toBe('rgba(0, 0, 0, 0.87)'); // stock --zk-color-on-surface
    expect(await colorOf(icon)).not.toBe(PURPLE); // info-type accent resolves to a color-mix, not asserted literally

    await page.addStyleTag({ content: ':root{--zk-toast-bg:#eef2ff;--zk-toast-fg:#1a1a2e;--zk-toast-radius:0px;--zk-toast-accent:#6750a4}' });
    expect(await radiusOf(card)).toBe('0px');
    expect(await bgOf(card)).toBe('rgb(238, 242, 255)');
    expect(await colorOf(card)).toBe('rgb(26, 26, 46)');
    expect(await colorOf(icon)).toBe(PURPLE);
  });

  // --zk-selectbox-* is consumed by <listbox mold="select"> (.z-select), not by .z-selectbox.
  test('selectbox — stock values, then :root override reaches .z-select', async ({ page }) => {
    await page.goto('/listbox.zul', { waitUntil: 'networkidle' });
    await settle(page);
    const sel = page.locator('.z-select').first();

    expect(await radiusOf(sel)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(sel)).toBe('rgba(0, 0, 0, 0.23)'); // stock --zk-color-outline

    await page.addStyleTag({ content: ':root{--zk-selectbox-radius:0px;--zk-selectbox-border-color:#6750a4}' });
    expect(await radiusOf(sel)).toBe('0px');
    expect(await borderColorOf(sel)).toBe(PURPLE);
  });
});

// ── Batch 3 (S2): components with state or several parts. A state that the component's own preview
// page does not render is created by a real interaction or the client widget API (see the comments).
const STOCK_RADIUS = '4px';
const STEPBAR_ACCENT_FG = 'rgb(26, 26, 26)'; // #1a1a1a override (default --zk-stepbar-accent-fg is white)
const PAGES3: Record<string, string> = {"toolbarbutton": "toolbar", "radio": "radiogroup", "nav": "navbar", "fisheye": "fisheyebar"};
declare const zk: any;

test.describe('Component Theme Variables (borrowed preview pages, batch 3)', () => {
  test.beforeEach(async ({ page }, testInfo) => {
    const comp = testInfo.title.split(' — ')[0];
    await page.goto(`/${PAGES3[comp] ?? comp}.zul`, { waitUntil: 'networkidle' });
    await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important;animation:none!important}' });
  });

  test('tabbox — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-tabs').first();
    const c1 = page.locator('.z-tab-selected').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await borderBottomColorOf(c0)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await colorOf(c1)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-tab-border-color:#6750a4;--zk-tab-accent:#6750a4}' });
    expect(await borderBottomColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('paging — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-paging-selected').first();
    await c0.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('9999px'); // stock
    expect(await bgOf(c0)).toBe('oklch(0.92 0.0403278 260.564)'); // stock
    expect(await colorOf(c0)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-paging-radius:4px;--zk-paging-selected-bg:#6750a4;--zk-paging-selected-fg:#ffffff}' });
    expect(await radiusOf(c0)).toBe('4px');
    expect(await bgOf(c0)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c0)).toBe('rgb(255, 255, 255)');
  });

  test('combobutton — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-combobutton').first();
    const c1 = page.locator('.z-combobutton-content').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await bgOf(c1)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-combobutton-bg:#6750a4;--zk-combobutton-fg:#ffffff;--zk-combobutton-overlay-color:#ffffff;--zk-combobutton-radius:9999px}' });
    expect(await radiusOf(c0)).toBe('9999px');
    expect(await bgOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('inputgroup — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-inputgroup').first().locator('.z-inputgroup-text').first();
    await c0.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    expect(await bgOf(c0)).toBe('rgb(247, 249, 252)'); // stock
    expect(await colorOf(c0)).toBe('rgba(0, 0, 0, 0.6)'); // stock
    await page.addStyleTag({ content: ':root{--zk-inputgroup-radius:0px;--zk-inputgroup-border-color:#6750a4;--zk-inputgroup-text-bg:#efe6ff;--zk-inputgroup-text-fg:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c0)).toBe('rgb(239, 230, 255)');
    expect(await colorOf(c0)).toBe(SCOPED_PURPLE);
  });

  test('calendar — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-calendar').first();
    const c1 = page.locator('.z-calendar').first().locator('.z-calendar-selected').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('6px'); // stock
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await pseudoBgOf(c1, '::before')).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-calendar-radius:0px;--zk-calendar-border-color:#6750a4;--zk-calendar-accent:#6750a4;--zk-calendar-accent-fg:#ffffff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await pseudoBgOf(c1, '::before')).toBe(SCOPED_PURPLE);
  });

  test('toolbarbutton — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-toolbarbutton').first();
    const c1 = page.locator('.z-toolbarbutton-checked').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await colorOf(c0)).toBe('rgb(55, 111, 208)'); // stock
    expect(await radiusOf(c0)).toBe('9999px'); // stock
    expect(await bgOf(c1)).toBe('oklch(0.92 0.0403278 260.564)'); // stock
    expect(await colorOf(c1)).toBe('oklch(0.23 0.0725901 260.564)'); // stock
    await page.addStyleTag({ content: ':root{--zk-toolbarbutton-fg:#6750a4;--zk-toolbarbutton-radius:0px;--zk-toolbarbutton-checked-bg:#6750a4;--zk-toolbarbutton-checked-fg:#ffffff}' });
    expect(await colorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await radiusOf(c0)).toBe('0px');
    expect(await bgOf(c1)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c1)).toBe('rgb(255, 255, 255)');
  });

  test('checkbox — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-checkbox-off').first().locator('.z-checkbox-mold').first();
    const c1 = page.locator('.z-checkbox-on').first().locator('.z-checkbox-mold').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.6)'); // stock
    expect(await bgOf(c1)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-checkbox-border-color:#6750a4;--zk-checkbox-accent:#6750a4}' });
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('radio — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-radio-off').first().locator('input[type="radio"]').first();
    const c1 = page.locator('.z-radio-on').first().locator('input[type="radio"]').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.6)'); // stock
    expect(await borderColorOf(c1)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-radio-border-color:#6750a4;--zk-radio-accent:#6750a4}' });
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('nav — stock values, then :root override reaches it', async ({ page }) => {
    // navbar.zul renders no selected item; select one with a real click.
    await page.locator('.z-navbar .z-navitem').first().click();
    const c0 = page.locator('.z-navbar').first();
    const c1 = page.locator('.z-navbar').first().locator('.z-navitem-content').first();
    const c2 = page.locator('.z-navbar').first().locator('.z-navitem-selected > .z-navitem-content').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    await c2.waitFor({ state: 'attached' });
    expect(await bgOf(c0)).toBe('rgb(247, 249, 252)'); // stock
    expect(await radiusOf(c1)).toBe('8px'); // stock
    expect(await bgOf(c2)).toBe('color(srgb 0.215686 0.435294 0.815686 / 0.12)'); // stock
    expect(await colorOf(c2)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-navbar-bg:#efe6ff;--zk-navitem-radius:0px;--zk-navitem-selected-bg:#6750a4;--zk-navitem-selected-fg:#ffffff}' });
    expect(await bgOf(c0)).toBe('rgb(239, 230, 255)');
    expect(await radiusOf(c1)).toBe('0px');
    expect(await bgOf(c2)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c2)).toBe('rgb(255, 255, 255)');
  });

  test('anchornav — stock values, then :root override reaches it', async ({ page }) => {
    // anchornav.zul renders no active link; click the second link (the first stays the resting link).
    // The active class is added after the scroll animation, so wait for it.
    const links = page.locator('.z-anchornav').first().locator('a.z-a');
    const c0 = links.first();
    const c1 = links.nth(1);
    await c0.waitFor({ state: 'attached' });
    await c1.click();
    await expect(c1).toHaveClass(/z-anchornav-link-active/);
    await page.waitForTimeout(300); // the link color has a transition
    expect(await colorOf(c0)).toBe('rgba(0, 0, 0, 0.6)'); // stock
    expect(await colorOf(c1)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-anchornav-fg:#6750a4;--zk-anchornav-accent:#6750a4}' });
    await page.waitForTimeout(300);
    expect(await colorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('anchornav — button link: the active link gets an accent outline, :root override reaches it', async ({ page }) => {
    // The second anchornav on anchornav.zul has button links; a filled button cannot show the state by text color.
    const buttons = page.locator('.z-anchornav').nth(1).locator('button');
    const c0 = buttons.first();
    const c1 = buttons.nth(1);
    await c0.waitFor({ state: 'attached' });
    await c1.click();
    await expect(c1).toHaveClass(/z-anchornav-link-active/);
    await page.mouse.move(0, 0); // the button's hover rule is not what is measured
    await page.waitForTimeout(300);
    const outlineOf = (loc: Locator) => loc.evaluate((el) => { const c = getComputedStyle(el); return `${c.outlineStyle} ${c.outlineWidth} ${c.outlineColor}`; });
    expect(await outlineOf(c0)).toMatch(/^none /); // stock: the resting link has no outline
    expect(await outlineOf(c1)).toBe('solid 2px rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-anchornav-accent:#6750a4}' });
    expect(await outlineOf(c1)).toBe(`solid 2px ${SCOPED_PURPLE}`);
  });

  test('stepbar — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-stepbar').first().locator('.z-step:not(.z-step-active):not(.z-step-complete):not(.z-step-error)').first();
    const c1 = page.locator('.z-stepbar').first().locator('.z-step:not(.z-step-active):not(.z-step-complete):not(.z-step-error)').first().locator('.z-step-icon');
    const c2 = page.locator('.z-step-complete').first().locator('.z-step-icon');
    const c3 = page.locator('.z-stepbar').first().locator('.z-step-active').first();
    const c4 = page.locator('.z-step-complete').first().locator('.z-step-title');
    const c5 = page.locator('.z-stepbar').first().locator('.z-step-active').first().locator('.z-step-title');
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    await c2.waitFor({ state: 'attached' });
    await c3.waitFor({ state: 'attached' });
    await c4.waitFor({ state: 'attached' });
    await c5.waitFor({ state: 'attached' });
    expect(await pseudoBgOf(c0, '::before')).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    expect(await bgOf(c2)).toBe('rgb(55, 111, 208)'); // stock
    expect(await colorOf(c2)).toBe('rgb(255, 255, 255)'); // stock
    expect(await pseudoBgOf(c3, '::before')).toBe('rgb(55, 111, 208)'); // stock
    expect(await colorOf(c4)).toBe('rgba(0, 0, 0, 0.6)'); // stock
    expect(await colorOf(c5)).toBe('rgba(0, 0, 0, 0.87)'); // stock
    await page.addStyleTag({ content: ':root{--zk-stepbar-connector-color:#6750a4;--zk-stepbar-icon-border-color:#6750a4;--zk-stepbar-fg:#6750a4;--zk-stepbar-fg-active:#6750a4;--zk-stepbar-accent:#6750a4;--zk-stepbar-accent-fg:#1a1a1a}' });
    expect(await pseudoBgOf(c0, '::before')).toBe(SCOPED_PURPLE);
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c2)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c2)).toBe(STEPBAR_ACCENT_FG);
    expect(await pseudoBgOf(c3, '::before')).toBe(SCOPED_PURPLE);
    expect(await colorOf(c4)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c5)).toBe(SCOPED_PURPLE);
  });

  test('fisheye — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-fisheye-text').first();
    const c1 = page.locator('.z-fisheye-image').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await colorOf(c0)).toBe('rgb(240, 244, 250)'); // stock
    expect(await radiusOf(c1)).toBe('4px'); // stock
    await page.addStyleTag({ content: ':root{--zk-fisheye-fg:#6750a4;--zk-fisheye-radius:0px}' });
    expect(await colorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await radiusOf(c1)).toBe('0px');
  });

  test('pdfviewer — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-pdfviewer').first();
    const c1 = page.locator('.z-pdfviewer').first().locator('.z-pdfviewer-toolbar').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('12px'); // stock
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await bgOf(c1)).toBe('rgb(232, 238, 247)'); // stock
    await page.addStyleTag({ content: ':root{--zk-pdfviewer-radius:0px;--zk-pdfviewer-border-color:#6750a4;--zk-pdfviewer-toolbar-bg:#6750a4;--zk-pdfviewer-toolbar-fg:#ffffff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('tbeditor — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-tbeditor-box').first();
    const c1 = page.locator('.z-tbeditor-box').first().locator('.z-tbeditor-button-pane').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('8px'); // stock
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    expect(await bgOf(c1)).toBe('rgb(240, 244, 250)'); // stock
    await page.addStyleTag({ content: ':root{--zk-tbeditor-radius:0px;--zk-tbeditor-border-color:#6750a4;--zk-tbeditor-toolbar-bg:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c1)).toBe(SCOPED_PURPLE);
  });

  test('signature — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-signature').first();
    await c0.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('12px'); // stock
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    await page.addStyleTag({ content: ':root{--zk-signature-radius:0px;--zk-signature-border-color:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
  });

  test('cropper — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-cropper').first();
    const c1 = page.locator('.z-cropper').first().locator('.z-cropper-toolbar').first();
    const c2 = page.locator('.z-cropper').first().locator('.z-cropper-crop > a').first();
    const c3 = page.locator('.z-cropper').first().locator('.z-cropper-cancel > a').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    await c2.waitFor({ state: 'attached' });
    await c3.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await bgOf(c1)).toBe('rgb(240, 244, 250)'); // stock
    expect(await colorOf(c2)).toBe('rgb(55, 111, 208)'); // stock
    expect(await colorOf(c3)).toBe('rgba(0, 0, 0, 0.6)'); // stock
    await page.addStyleTag({ content: ':root{--zk-cropper-radius:0px;--zk-cropper-border-color:#6750a4;--zk-cropper-toolbar-bg:#6750a4;--zk-cropper-accent:#ffffff;--zk-cropper-fg:#ffffff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c1)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c2)).toBe('rgb(255, 255, 255)');
    expect(await colorOf(c3)).toBe('rgb(255, 255, 255)');
  });

  test('dropupload — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-dropupload').first();
    await c0.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('4px'); // stock
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.23)'); // stock
    expect(await bgOf(c0)).toBe('rgba(0, 0, 0, 0)'); // stock
    await page.addStyleTag({ content: ':root{--zk-dropupload-radius:0px;--zk-dropupload-border-color:#6750a4;--zk-dropupload-bg:#6750a4}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c0)).toBe(SCOPED_PURPLE);
  });

  test('organigram — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-organigram').first().locator('.z-orgnode').first();
    const c1 = page.locator('.z-orgitem-selected > .z-orgnode').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('6px'); // stock
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await bgOf(c1)).toBe('oklch(0.92 0.0403278 260.564)'); // stock
    expect(await colorOf(c1)).toBe('oklch(0.23 0.0725901 260.564)'); // stock
    await page.addStyleTag({ content: ':root{--zk-organigram-radius:0px;--zk-organigram-border-color:#6750a4;--zk-organigram-selected-bg:#6750a4;--zk-organigram-selected-fg:#ffffff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c1)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c1)).toBe('rgb(255, 255, 255)');
  });

  test('goldenlayout — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-goldenlayout').first().locator('.lm_header').first();
    const c1 = page.locator('.z-goldenlayout').first().locator('.z-goldenpanel').first();
    const c2 = page.locator('.z-goldenlayout').first().locator('.lm_tab.lm_active').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    await c2.waitFor({ state: 'attached' });
    expect(await bgOf(c0)).toBe('rgb(240, 244, 250)'); // stock
    expect(await radiusOf(c1)).toBe('6px'); // stock
    expect(await borderColorOf(c1)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await colorOf(c2)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-goldenlayout-radius:0px;--zk-goldenlayout-border-color:#6750a4;--zk-goldenlayout-header-bg:#6750a4;--zk-goldenlayout-accent:#6750a4}' });
    expect(await bgOf(c0)).toBe(SCOPED_PURPLE);
    expect(await radiusOf(c1)).toBe('0px');
    expect(await borderColorOf(c1)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c2)).toBe(SCOPED_PURPLE);
  });

  test('portallayout — stock values, then :root override reaches it', async ({ page }) => {
    const c0 = page.locator('.z-portalchildren-frame').first();
    const c1 = page.locator('.z-portallayout').first().locator('.z-portalchildren-counter-on').first();
    await c0.waitFor({ state: 'attached' });
    await c1.waitFor({ state: 'attached' });
    expect(await radiusOf(c0)).toBe('6px'); // stock
    expect(await borderColorOf(c0)).toBe('rgba(0, 0, 0, 0.12)'); // stock
    expect(await bgOf(c1)).toBe('oklch(0.92 0.0403278 260.564)'); // stock
    expect(await colorOf(c1)).toBe('rgb(55, 111, 208)'); // stock
    await page.addStyleTag({ content: ':root{--zk-portallayout-radius:0px;--zk-portallayout-border-color:#6750a4;--zk-portallayout-counter-bg:#6750a4;--zk-portallayout-counter-fg:#ffffff}' });
    expect(await radiusOf(c0)).toBe('0px');
    expect(await borderColorOf(c0)).toBe(SCOPED_PURPLE);
    expect(await bgOf(c1)).toBe(SCOPED_PURPLE);
    expect(await colorOf(c1)).toBe('rgb(255, 255, 255)');
  });

  test('chosenbox — stock values, then :root override reaches it, including the focused chip', async ({ page }) => {
    const box = page.locator('.z-chosenbox').first();
    const chip = box.locator('.z-chosenbox-item').first();
    await chip.waitFor({ state: 'attached' });

    expect(await radiusOf(box)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(box)).toBe('rgba(0, 0, 0, 0.23)'); // stock --zk-color-outline
    expect(await bgOf(chip)).toBe('rgb(232, 238, 247)'); // stock resting chip fill

    await page.addStyleTag({ content: ':root{--zk-chosenbox-radius:0px;--zk-chosenbox-border-color:#6750a4;--zk-chosenbox-item-bg:#efe6ff;--zk-chosenbox-item-focus-bg:#6750a4}' });
    expect(await radiusOf(box)).toBe('0px');
    expect(await borderColorOf(box)).toBe(SCOPED_PURPLE);
    expect(await bgOf(chip)).toBe('rgb(239, 230, 255)'); // #efe6ff item-bg override

    // Clicking a chip arms it for delete (.z-chosenbox-item-focus), the defining state.
    await chip.click();
    expect(await bgOf(chip)).toBe(SCOPED_PURPLE);
  });

  test('badge — stock fill, then :root override reaches the default badge but not a severity badge', async ({ page }) => {
    const info = page.locator('.z-badge-info > .z-badge-indicator').first();
    const success = page.locator('.z-badge-success > .z-badge-indicator').first();
    await info.waitFor({ state: 'attached' });
    await success.waitFor({ state: 'attached' });
    const infoBg = await bgOf(info);
    const successBg = await bgOf(success);
    expect(infoBg).not.toBe(SCOPED_PURPLE);

    await page.addStyleTag({ content: ':root{--zk-badge-bg:#6750a4}' });
    expect(await bgOf(info)).toBe(SCOPED_PURPLE);
    expect(await bgOf(success)).toBe(successBg); // CTV-7: a non-default severity keeps its semantic color
  });
});
