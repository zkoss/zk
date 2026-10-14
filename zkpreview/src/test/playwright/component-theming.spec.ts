import { test, expect, type Locator } from '@playwright/test';

// Component Theme Variables — proof for the button pilot (Tier 1 #1, see
// doc/spec/component-theme-variables.md).
// Verifies the --zk-button-* knob contract:
//   1. a REGIONAL override (custom props on a container) restyles only that
//      subtree's buttons, leaving sibling defaults untouched;
//   2. a WHOLE-APP override (:root, injected after norm.css.dsp) wins the cascade;
//   3. ZERO REGRESSION — an un-overridden button renders stock Marble values;
//   4. disabled treatment is preserved inside an overridden region.
// Requires the preview app on ${PREVIEW_URL}
//   withjdk.sh 17 mvn test exec:java@preview-app

const PAGE = '/component-theming.zul';
const SCOPED_PURPLE = 'rgb(103, 80, 164)'; // #6750a4 set on the scoped container
const STOCK_RADIUS = '4px';                // --zk-shape-button (extra-small corner)

const radiusOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).borderTopLeftRadius);
const bgOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).backgroundColor);
const borderColorOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).borderTopColor);
const borderBottomColorOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).borderBottomColor);
const borderLeftColorOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).borderLeftColor);
const colorOf = (loc: Locator) => loc.evaluate((el) => getComputedStyle(el).color);
const pseudoBgOf = (loc: Locator, pseudo: string) =>
  loc.evaluate((el, p) => getComputedStyle(el, p).backgroundColor, pseudo);

test.describe('Component Theme Variables', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(PAGE, { waitUntil: 'networkidle' });
    // Kill transitions so getComputedStyle reads settled values, not a start frame.
    await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important;animation:none!important}' });
  });

  test('regional override restyles scoped buttons only', async ({ page }) => {
    const def = page.getByRole('button', { name: 'Default Primary' });
    const scoped = page.getByRole('button', { name: 'Scoped Primary' });

    // Scoped button picks up BOTH knobs (radius + fill).
    expect(await radiusOf(scoped)).toBe('9999px');
    expect(await bgOf(scoped)).toBe(SCOPED_PURPLE);

    // Sibling default is UNTOUCHED — the override lives on the scoped box and is
    // inherited only by its subtree, never leaking up to the default row.
    expect(await radiusOf(def)).toBe(STOCK_RADIUS);
    expect(await bgOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('disabled button keeps its disabled treatment inside a scoped region', async ({ page }) => {
    const scopedDisabled = page.getByRole('button', { name: 'Scoped Disabled' });
    // The disabled rule doesn't read the bg knob → stays disabled-container, not purple.
    expect(await bgOf(scopedDisabled)).not.toBe(SCOPED_PURPLE);
  });

  test('whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.getByRole('button', { name: 'Default Primary' });
    expect(await radiusOf(def)).toBe(STOCK_RADIUS); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade (unlayered vs unlayered → source order) and reach the default button.
    await page.addStyleTag({ content: ':root{--zk-button-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── input: per-state border-color model ──────────────────────────────────
  test('input — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-textbox').first();
    const scoped = page.locator('div[style*="--zk-input-radius"] .z-textbox').first();

    expect(await radiusOf(scoped)).toBe('12px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  // ── window: surface + chrome model (elevation stays mode-driven) ──────────
  test('window — regional surface/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-window').first();
    const scoped = page.locator('div[style*="--zk-window-radius"] .z-window').first();

    expect(await radiusOf(scoped)).toBe('16px');
    expect(await bgOf(scoped)).toBe('rgb(238, 242, 255)'); // #eef2ff

    expect(await radiusOf(def)).toBe('4px'); // stock extra-small corner
    expect(await bgOf(def)).not.toBe('rgb(238, 242, 255)');
  });

  // ── grid: outlined-table lines + fills ────────────────────────────────────
  test('grid — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-grid').first();
    const scoped = page.locator('div[style*="--zk-grid-radius"] .z-grid').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('6px'); // stock --zk-shape-card
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  // ── listbox: data table + row selection ──────────────────────────────────
  test('listbox — regional frame + selection override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-listbox').first();
    const scoped = page.locator('div[style*="--zk-listbox-radius"] .z-listbox').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scoped.locator('.z-listitem-selected').first())).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('6px'); // stock --zk-shape-card
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(def.locator('.z-listitem-selected').first())).not.toBe(SCOPED_PURPLE);
  });

  // ── tree: tree table + row selection ──────────────────────────────────────
  test('tree — regional frame + selection override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-tree').first();
    const scoped = page.locator('div[style*="--zk-tree-radius"] .z-tree').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scoped.locator('.z-treerow-selected').first())).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('6px'); // stock --zk-shape-card
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(def.locator('.z-treerow-selected').first())).not.toBe(SCOPED_PURPLE);
  });

  // ── panel + groupbox: surface containers ─────────────────────────────────
  // Panel renders border-less by default (.z-panel-noborder), so assert the fill
  // + radius knobs (border-color has no border to paint on the stock panel).
  test('panel — regional surface/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-panel').first();
    const scoped = page.locator('div[style*="--zk-panel-radius"] .z-panel').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await bgOf(scoped)).toBe('rgb(238, 242, 255)'); // #eef2ff

    expect(await radiusOf(def)).toBe('6px'); // stock --zk-shape-card
    expect(await bgOf(def)).not.toBe('rgb(238, 242, 255)');
  });

  test('groupbox — regional frame override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-groupbox').first();
    const scoped = page.locator('div[style*="--zk-groupbox-radius"] .z-groupbox').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('6px'); // stock --zk-shape-card
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  // ── combobox: outlined field (dropdown popup/selection also knob-driven) ──
  test('combobox — regional field override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-combobox-input').first();
    const scoped = page.locator('div[style*="--zk-combobox-radius"] .z-combobox-input').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  // ── dropdown-input family (datebox/timebox/spinner/bandbox): wrapper-border
  // model — border on the root wrapper, focus = inset ring. Same field knobs as
  // combobox; radius + resting border-color are the observable A/B here. ────────
  test('datebox — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-datebox').first();
    const scoped = page.locator('div[style*="--zk-datebox-radius"] .z-datebox').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('timebox — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-timebox').first();
    const scoped = page.locator('div[style*="--zk-timebox-radius"] .z-timebox').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('spinner — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-spinner').first();
    const scoped = page.locator('div[style*="--zk-spinner-radius"] .z-spinner').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('bandbox — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-bandbox').first();
    const scoped = page.locator('div[style*="--zk-bandbox-radius"] .z-bandbox').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  // ── daterangebox (EE, zkmax): two-ended date-range picker field + range-calendar
  // popup. Same wrapper-border knob vocabulary as datebox/timebox/spinner/bandbox —
  // radius + resting border-color are the observable A/B here (the trigger button's
  // radius reuses the same radius knob). ────────────────────────────────────────
  test('daterangebox — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-daterangebox').first();
    const scoped = page.locator('div[style*="--zk-daterangebox-radius"] .z-daterangebox').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-corner-extra-small
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('daterangebox — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-daterangebox').first();
    expect(await radiusOf(def)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every daterangebox instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-daterangebox-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── tab: nav chrome + active accent (indicator + selected label) ──────────
  test('tabbox — regional accent/border override, sibling untouched', async ({ page }) => {
    const defBar = page.locator('.z-tabs').first();
    const defSel = page.locator('.z-tab-selected').first();
    const scopedBox = page.locator('div[style*="--zk-tab-accent"]');
    const scopedBar = scopedBox.locator('.z-tabs').first();
    const scopedSel = scopedBox.locator('.z-tab-selected').first();

    expect(await borderBottomColorOf(scopedBar)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedSel)).toBe(SCOPED_PURPLE); // selected label = accent

    expect(await borderBottomColorOf(defBar)).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(defSel)).not.toBe(SCOPED_PURPLE);
  });

  // ── menu: menubar surface (popup/selected-item also knob-driven) ──────────
  test('menubar — regional surface override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-menubar').first();
    const scoped = page.locator('div[style*="--zk-menubar-bg"] .z-menubar').first();

    expect(await bgOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(def)).not.toBe(SCOPED_PURPLE);
  });

  // ── avatar: base defaults hoisted from the .z-avatar element to :root, so a
  // region override now reaches it (chip base is hoisted too, but ZK stamps a
  // default severity class that element-shadows it — not regionally demonstrable).
  test('avatar — regional fill override (hoisted base knob), sibling untouched', async ({ page }) => {
    const def = page.locator('.z-avatar').first();
    const scoped = page.locator('div[style*="--zk-avatar-bg"] .z-avatar').first();
    expect(await bgOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(def)).not.toBe(SCOPED_PURPLE);
  });

  // ── badge: default (info) fill is routed through the base --zk-badge-bg (no
  // .z-badge-info rule), so a region override reaches a default badge; explicit
  // non-default severities pin --zk-badge-bg on the indicator and stay semantic.
  test('badge — default fill region-overridable, non-default severity pinned', async ({ page }) => {
    const scopedBox = page.locator('div[style*="--zk-badge-bg"]');
    const scopedDefault = scopedBox.locator('.z-badge-info > .z-badge-indicator').first();
    const scopedSuccess = scopedBox.locator('.z-badge-success > .z-badge-indicator').first();
    const outsideDefault = page.locator('.z-badge-info > .z-badge-indicator').first();

    // Default (info) badge inside the region takes the override.
    expect(await bgOf(scopedDefault)).toBe(SCOPED_PURPLE);
    // A non-default severity keeps its semantic color despite the region override.
    expect(await bgOf(scopedSuccess)).not.toBe(SCOPED_PURPLE);
    // A default badge outside the region is untouched.
    expect(await bgOf(outsideDefault)).not.toBe(SCOPED_PURPLE);
  });

  // ── rating: star glyph fill only (no bg/border/radius). Resting stars read
  // --zk-rating-fg; selected/hover swap to --zk-rating-accent (the defining state).
  test('rating — regional accent override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-rating').first();
    const scoped = page.locator('div[style*="--zk-rating-accent"] .z-rating').first();

    // Scoped rating's selected stars pick up the accent override.
    expect(await colorOf(scoped.locator('.z-rating-selected').first())).toBe(SCOPED_PURPLE);

    // Sibling default rating's selected stars are untouched.
    expect(await colorOf(def.locator('.z-rating-selected').first())).not.toBe(SCOPED_PURPLE);
  });

  test('rating — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defSelected = page.locator('.z-rating').first().locator('.z-rating-selected').first();
    expect(await colorOf(defSelected)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every rating instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-rating-accent:#6750a4}' });
    expect(await colorOf(defSelected)).toBe(SCOPED_PURPLE);
  });

  // ── progressmeter: MD3 linear progress (track + fill). Color variants
  // (secondary/success/warning/error) keep their own semantic colors, same
  // treatment as button's color variants — not knob-driven. ─────────────────
  test('progressmeter — regional track/fill override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-progressmeter').first();
    const scoped = page.locator('div[style*="--zk-progressmeter-bg"] .z-progressmeter').first();

    expect(await bgOf(scoped)).toBe('rgb(239, 230, 255)'); // #efe6ff track override
    expect(await bgOf(scoped.locator('.z-progressmeter-image').first())).toBe(SCOPED_PURPLE);

    expect(await bgOf(def)).not.toBe('rgb(239, 230, 255)');
    expect(await bgOf(def.locator('.z-progressmeter-image').first())).not.toBe(SCOPED_PURPLE);
  });

  test('progressmeter — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defFill = page.locator('.z-progressmeter').first().locator('.z-progressmeter-image').first();
    expect(await bgOf(defFill)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every progressmeter instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-progressmeter-fill:#6750a4}' });
    expect(await bgOf(defFill)).toBe(SCOPED_PURPLE);
  });

  // ── paging: pager button shape/fg + selected (current-page) fill/text.
  // Demo uses mold="os" so numbered page buttons (and .z-paging-selected)
  // render — the default mold only shows prev/next + a jump-to-page input,
  // with no page numbers. Disabled buttons are intentionally not knob-driven
  // (same convention as button/input). ──────────────────────────────────────
  test('paging — regional radius/selected override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-paging').first();
    const scoped = page.locator('div[style*="--zk-paging-radius"] .z-paging').first();

    const defSelected = def.locator('.z-paging-selected').first();
    const scopedSelected = scoped.locator('.z-paging-selected').first();

    expect(await radiusOf(scopedSelected)).toBe('4px');
    expect(await bgOf(scopedSelected)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedSelected)).toBe('rgb(255, 255, 255)');

    expect(await radiusOf(defSelected)).toBe('9999px'); // stock --zk-shape-corner-full
    expect(await bgOf(defSelected)).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(defSelected)).not.toBe('rgb(255, 255, 255)');
  });

  test('paging — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defSelected = page.locator('.z-paging').first().locator('.z-paging-selected').first();
    expect(await bgOf(defSelected)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every paging instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-paging-selected-bg:#6750a4}' });
    expect(await bgOf(defSelected)).toBe(SCOPED_PURPLE);
  });

  // ── combobutton: split button (filled base) — shares the button family's
  // overlay state model. Toolbar mold is a color variant (like button's
  // outlined/text variants) and disabled stays on base tokens — both
  // intentionally not knob-driven, same convention as button. ─────────────
  test('combobutton — regional bg/fg/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-combobutton-content').first();
    const scoped = page.locator('div[style*="--zk-combobutton-radius"] .z-combobutton-content').first();
    const scopedRoot = page.locator('div[style*="--zk-combobutton-radius"] .z-combobutton').first();

    expect(await radiusOf(scopedRoot)).toBe('9999px');
    expect(await bgOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(page.locator('.z-combobutton').first())).toBe(STOCK_RADIUS);
    expect(await bgOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('combobutton — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defContent = page.locator('.z-combobutton-content').first();
    expect(await bgOf(defContent)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every combobutton instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-combobutton-bg:#6750a4}' });
    expect(await bgOf(defContent)).toBe(SCOPED_PURPLE);
  });

  // ── selectbox: native <select> element (Listbox "select" mold, .z-select).
  // Renders as a single native select (no wrapper); state is a per-state
  // border-color change (no overlay), same model as Input. ──────────────────
  test('selectbox — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-select').first();
    const scoped = page.locator('div[style*="--zk-selectbox-radius"] .z-select').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('selectbox — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-select').first();
    expect(await radiusOf(def)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every selectbox instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-selectbox-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── inputgroup: input + addon(s) combined into a single field. Border color
  // and radius are shared by the addon and the grouped input (one continuous
  // outline); the addon also carries its own fill/text pair. Focus stays on
  // the global focus-ring token (same as button/window/grid), not a knob. ──
  test('inputgroup — regional border/radius/addon override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-inputgroup').first();
    const scoped = page.locator('div[style*="--zk-inputgroup-radius"] .z-inputgroup').first();

    const defAddon = def.locator('.z-inputgroup-text').first();
    const scopedAddon = scoped.locator('.z-inputgroup-text').first();

    expect(await radiusOf(scopedAddon)).toBe('0px');
    expect(await borderColorOf(scopedAddon)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scopedAddon)).toBe('rgb(239, 230, 255)'); // #efe6ff
    expect(await colorOf(scopedAddon)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(defAddon)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(defAddon)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(defAddon)).not.toBe('rgb(239, 230, 255)');
    expect(await colorOf(defAddon)).not.toBe(SCOPED_PURPLE);
  });

  test('inputgroup — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defAddon = page.locator('.z-inputgroup').first().locator('.z-inputgroup-text').first();
    expect(await radiusOf(defAddon)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every inputgroup instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-inputgroup-radius:0px}' });
    expect(await radiusOf(defAddon)).toBe('0px');
  });

  // ── calendar: self-contained month grid + nav header. zk-calendar-accent /
  // zk-calendar-accent-fg is a single defining-state pair — it colors the
  // selected day's disc fill (::before) + text, the today ring, and the
  // Today-link label together (same "one knob, several roles" pattern as
  // tab's accent). ──────────────────────────────────────────────────────────
  test('calendar — regional border/radius/accent override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-calendar').first();
    const scoped = page.locator('div[style*="--zk-calendar-radius"] .z-calendar').first();

    const defSelected = def.locator('.z-calendar-selected').first();
    const scopedSelected = scoped.locator('.z-calendar-selected').first();

    // --zk-calendar-accent-fg (on-primary) is #ffffff stock, same as the demo's
    // override value, so it can't distinguish scoped from default here — the
    // disc fill (accent) + shell border/radius already prove region scoping.
    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await pseudoBgOf(scopedSelected, '::before')).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('6px'); // stock --zk-shape-card
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await pseudoBgOf(defSelected, '::before')).not.toBe(SCOPED_PURPLE);
  });

  test('calendar — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defSelected = page.locator('.z-calendar').first().locator('.z-calendar-selected').first();
    expect(await pseudoBgOf(defSelected, '::before')).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every calendar instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-calendar-accent:#6750a4}' });
    expect(await pseudoBgOf(defSelected, '::before')).toBe(SCOPED_PURPLE);
  });

  // ── toolbar: chrome bar — fill + shared divider-line knob (border-color drives
  // the bar's own edge; the app-bar context variant keeps its own colors, not
  // knob-driven — same convention as combobutton's toolbar mold). ─────────────
  test('toolbar — regional bg/border override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-toolbar').first();
    const scoped = page.locator('div[style*="--zk-toolbar-bg"] .z-toolbar').first();

    expect(await bgOf(scoped)).toBe('rgb(238, 242, 255)'); // #eef2ff
    expect(await borderBottomColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await bgOf(def)).not.toBe('rgb(238, 242, 255)');
    expect(await borderBottomColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('toolbar — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-toolbar').first();
    expect(await borderBottomColorOf(def)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every toolbar instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-toolbar-border-color:#6750a4}' });
    expect(await borderBottomColorOf(def)).toBe(SCOPED_PURPLE);
  });

  // ── toolbarbutton: icon/text button rendered inside toolbar chrome (its own
  // widget/knob family, not a button variant). fg drives both the resting
  // text/icon color and the currentColor state-layer overlay; checked-bg/-fg
  // is the one defining state (mode="toggle" checked="true"). ─────────────
  test('toolbarbutton — regional fg/radius/checked override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-toolbarbutton').first();
    const defChecked = page.locator('.z-toolbarbutton-checked').first();
    const scoped = page.locator('div[style*="--zk-toolbarbutton-fg"] .z-toolbarbutton').first();
    const scopedChecked = page.locator('div[style*="--zk-toolbarbutton-fg"] .z-toolbarbutton-checked').first();

    expect(await colorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await radiusOf(scoped)).toBe('0px');
    expect(await bgOf(scopedChecked)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedChecked)).toBe('rgb(255, 255, 255)');

    expect(await colorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await radiusOf(def)).toBe('9999px'); // stock --zk-shape-corner-full
    expect(await bgOf(defChecked)).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(defChecked)).not.toBe('rgb(255, 255, 255)');
  });

  test('toolbarbutton — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-toolbarbutton').first();
    expect(await radiusOf(def)).toBe('9999px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every toolbarbutton instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-toolbarbutton-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── slider: MD3 range input (track + fill + thumb). zk-slider-accent covers
  // BOTH the active fill and the thumb (same "one knob, several roles" pattern
  // as tab/calendar's accent); disabled stays on opacity only, not knob-driven
  // (same convention as button/input/rating). ──────────────────────────────
  test('slider — regional track/accent/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-slider').first();
    const scoped = page.locator('div[style*="--zk-slider-radius"] .z-slider').first();

    const defTrack = def.locator('.z-slider-center').first();
    const scopedTrack = scoped.locator('.z-slider-center').first();
    const defThumb = def.locator('.z-slider-button').first();
    const scopedThumb = scoped.locator('.z-slider-button').first();

    expect(await radiusOf(scopedTrack)).toBe('0px');
    expect(await bgOf(scopedTrack)).toBe('rgb(239, 230, 255)'); // #efe6ff track override
    expect(await bgOf(scopedThumb)).toBe(SCOPED_PURPLE); // accent override (fill + thumb)

    expect(await radiusOf(defTrack)).toBe('9999px'); // stock --zk-shape-corner-full
    expect(await bgOf(defTrack)).not.toBe('rgb(239, 230, 255)');
    expect(await bgOf(defThumb)).not.toBe(SCOPED_PURPLE);
  });

  test('slider — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defThumb = page.locator('.z-slider').first().locator('.z-slider-button').first();
    expect(await bgOf(defThumb)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every slider instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-slider-accent:#6750a4}' });
    expect(await bgOf(defThumb)).toBe(SCOPED_PURPLE);
  });

  // ── rangeslider (PE): MD3 dual-thumb range input — shares the Slider
  // family's track/accent/radius/elevation vocabulary. zk-rangeslider-accent
  // covers BOTH the active-range fill (between the two thumbs) AND each
  // thumb's fill (same "one knob, several roles" pattern as tab/calendar/
  // slider's accent); disabled stays on opacity only, not knob-driven (same
  // convention as button/input/rating/slider). ──────────────────────────────
  test('rangeslider — regional track/accent/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-rangeslider').first();
    const scoped = page.locator('div[style*="--zk-rangeslider-radius"] .z-rangeslider').first();

    const defTrack = def.locator('.z-rangeslider-track').first();
    const scopedTrack = scoped.locator('.z-rangeslider-track').first();
    const defArea = def.locator('.z-sliderbuttons-area').first();
    const scopedArea = scoped.locator('.z-sliderbuttons-area').first();
    const defThumb = def.locator('.z-sliderbuttons-button').first();
    const scopedThumb = scoped.locator('.z-sliderbuttons-button').first();

    expect(await radiusOf(scopedTrack)).toBe('0px');
    expect(await bgOf(scopedTrack)).toBe('rgb(239, 230, 255)'); // #efe6ff track override
    expect(await bgOf(scopedArea)).toBe(SCOPED_PURPLE); // accent override (active-range fill)
    expect(await bgOf(scopedThumb)).toBe(SCOPED_PURPLE); // accent override (thumb fill)

    expect(await radiusOf(defTrack)).toBe('9999px'); // stock --zk-shape-corner-full
    expect(await bgOf(defTrack)).not.toBe('rgb(239, 230, 255)');
    expect(await bgOf(defArea)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(defThumb)).not.toBe(SCOPED_PURPLE);
  });

  test('rangeslider — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defThumb = page.locator('.z-rangeslider').first().locator('.z-sliderbuttons-button').first();
    expect(await bgOf(defThumb)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every rangeslider instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-rangeslider-accent:#6750a4}' });
    expect(await bgOf(defThumb)).toBe(SCOPED_PURPLE);
  });

  // ── multislider (EE): MD3 multi-range slider — shares the Slider/
  // Rangeslider family's track/accent/radius/elevation vocabulary (same
  // Sliderbuttons sub-widget markup). zk-multislider-accent covers BOTH the
  // active-range fill AND each thumb's fill (same "one knob, several roles"
  // pattern as tab/calendar/slider/rangeslider's accent); disabled stays on
  // opacity only, not knob-driven (same convention as button/input/rating/
  // slider/rangeslider). ──────────────────────────────────────────────────
  test('multislider — regional track/accent/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-multislider').first();
    const scoped = page.locator('div[style*="--zk-multislider-radius"] .z-multislider').first();

    const defTrack = def.locator('.z-multislider-track').first();
    const scopedTrack = scoped.locator('.z-multislider-track').first();
    const defArea = def.locator('.z-sliderbuttons-area').first();
    const scopedArea = scoped.locator('.z-sliderbuttons-area').first();
    const defThumb = def.locator('.z-sliderbuttons-button').first();
    const scopedThumb = scoped.locator('.z-sliderbuttons-button').first();

    expect(await radiusOf(scopedTrack)).toBe('0px');
    expect(await bgOf(scopedTrack)).toBe('rgb(239, 230, 255)'); // #efe6ff track override
    expect(await bgOf(scopedArea)).toBe(SCOPED_PURPLE); // accent override (active-range fill)
    expect(await bgOf(scopedThumb)).toBe(SCOPED_PURPLE); // accent override (thumb fill)

    expect(await radiusOf(defTrack)).toBe('9999px'); // stock --zk-shape-corner-full
    expect(await bgOf(defTrack)).not.toBe('rgb(239, 230, 255)');
    expect(await bgOf(defArea)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(defThumb)).not.toBe(SCOPED_PURPLE);
  });

  test('multislider — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defThumb = page.locator('.z-multislider').first().locator('.z-sliderbuttons-button').first();
    expect(await bgOf(defThumb)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every multislider instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-multislider-accent:#6750a4}' });
    expect(await bgOf(defThumb)).toBe(SCOPED_PURPLE);
  });

  // ── checkbox: default mold — resting border/text + checked/indeterminate
  // accent (fill + border + hover-ring tint), the defining state. Switch/toggle
  // molds and radio/radiogroup are out of scope for this pass. ────────────────
  test('checkbox — regional border/accent override, sibling untouched', async ({ page }) => {
    const defOff = page.locator('.z-checkbox-off').first();
    const defOn = page.locator('.z-checkbox-on').first();
    const scopedOff = page.locator('div[style*="--zk-checkbox-accent"] .z-checkbox-off').first();
    const scopedOn = page.locator('div[style*="--zk-checkbox-accent"] .z-checkbox-on').first();

    // Scoped instances pick up both knobs (resting border + checked fill).
    expect(await borderColorOf(scopedOff.locator('.z-checkbox-mold').first())).toBe(SCOPED_PURPLE);
    expect(await bgOf(scopedOn.locator('.z-checkbox-mold').first())).toBe(SCOPED_PURPLE);

    // Sibling defaults outside the region are untouched.
    expect(await borderColorOf(defOff.locator('.z-checkbox-mold').first())).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(defOn.locator('.z-checkbox-mold').first())).not.toBe(SCOPED_PURPLE);
  });

  test('checkbox — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defMold = page.locator('.z-checkbox-on').first().locator('.z-checkbox-mold').first();
    expect(await bgOf(defMold)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every checkbox instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-checkbox-accent:#6750a4}' });
    expect(await bgOf(defMold)).toBe(SCOPED_PURPLE);
  });

  // ── radio: no z-radio-mold element — input[type="radio"] IS the visual.
  // Resting (unselected) reads the border/text knobs; selected swaps the
  // border-color (+ ring/inner-dot fill, same rule) to a single accent, the
  // defining state, same "one knob, several roles" precedent as checkbox's
  // accent. ────────────────────────────────────────────────────────────────
  test('radio — regional border/accent override, sibling untouched', async ({ page }) => {
    const defOff = page.locator('.z-radio-off').first();
    const defOn = page.locator('.z-radio-on').first();
    const scopedOff = page.locator('div[style*="--zk-radio-accent"] .z-radio-off').first();
    const scopedOn = page.locator('div[style*="--zk-radio-accent"] .z-radio-on').first();

    // Scoped instances pick up both knobs (resting border + selected accent border).
    expect(await borderColorOf(scopedOff.locator('input[type="radio"]').first())).toBe(SCOPED_PURPLE);
    expect(await borderColorOf(scopedOn.locator('input[type="radio"]').first())).toBe(SCOPED_PURPLE);

    // Sibling defaults outside the region are untouched.
    expect(await borderColorOf(defOff.locator('input[type="radio"]').first())).not.toBe(SCOPED_PURPLE);
    expect(await borderColorOf(defOn.locator('input[type="radio"]').first())).not.toBe(SCOPED_PURPLE);
  });

  test('radio — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defInput = page.locator('.z-radio-on').first().locator('input[type="radio"]').first();
    expect(await borderColorOf(defInput)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every radio instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-radio-accent:#6750a4}' });
    expect(await borderColorOf(defInput)).toBe(SCOPED_PURPLE);
  });

  // ── messagebox: MD3 alert dialog (Messagebox.show()). Messagebox.show()
  // always parents its Window to the page root (Executions.createComponents(
  // ..., desktop.getFirstPage(), ...)), never nested inside the invoking
  // button's container, so — unlike every other family above — a REGIONAL
  // (container) override cannot reach it; only the whole-app :root override
  // applies. There is no "sibling untouched" test here because there is no
  // scoped instance to compare against (see the tracker for this caveat). ──
  test('messagebox — zero-regression defaults', async ({ page }) => {
    await page.getByRole('button', { name: 'Show Messagebox' }).click();
    const dlg = page.locator('.z-messagebox-window').filter({ hasText: 'Component Theming Demo' });
    await expect(dlg).toBeVisible();

    expect(await radiusOf(dlg)).toBe(STOCK_RADIUS); // stock --zk-shape-dialog (extra-small corner)
    expect(await bgOf(dlg)).toBe('rgb(255, 255, 255)'); // stock --zk-color-surface
    expect(await borderBottomColorOf(dlg.locator('.z-window-header').first())).toBe('rgba(0, 0, 0, 0.12)'); // stock --zk-color-outline-variant
  });

  test('messagebox — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    await page.getByRole('button', { name: 'Show Messagebox' }).click();
    const dlg = page.locator('.z-messagebox-window').filter({ hasText: 'Component Theming Demo' });
    await expect(dlg).toBeVisible();
    expect(await bgOf(dlg)).not.toBe(SCOPED_PURPLE); // baseline before override
    const icon = dlg.locator('.z-messagebox-information').first();
    const iconColorBefore = await colorOf(icon);

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach the dialog even though it is not a descendant of any
    // container in the page (Messagebox.show() attaches it to the page root).
    await page.addStyleTag({ content: ':root{--zk-messagebox-bg:#6750a4}' });
    expect(await bgOf(dlg)).toBe(SCOPED_PURPLE);

    // The icon-type color is a semantic status color, not knob-driven — it
    // must stay unaffected by the bg override (state integrity, CTV-7).
    expect(await colorOf(icon)).toBe(iconColorBefore);
  });

  // ── popup: floating surface (tooltips/dropdowns/overlays). On open(), Popup.ts
  // reparents the widget's real DOM node to document.body (zk.makeVParent()) —
  // verified empirically (the open .z-popup's parentElement is BODY, not its
  // authored ZUL ancestor) — so, like messagebox (a different root cause), a
  // REGIONAL (container) override cannot reach it; only the whole-app :root
  // override applies. There is no "sibling untouched" test here for the same
  // reason as messagebox (see the tracker). The <popup> widget also lazily
  // creates its DOM node on first open. The tooltip variant (.z-popup-tooltip)
  // is a distinct color variant, not knob-driven. ──────────────────────────────
  test('popup — zero-regression defaults', async ({ page }) => {
    await page.getByRole('button', { name: 'Show Popup', exact: true }).click();
    const def = page.locator('.z-popup.z-popup-open');
    await expect(def).toBeVisible();

    expect(await radiusOf(def)).toBe(STOCK_RADIUS); // stock --zk-shape-corner-extra-small
    expect(await bgOf(def)).toBe('rgb(255, 255, 255)'); // stock --zk-color-surface
    expect(await borderColorOf(def)).toBe('rgba(0, 0, 0, 0.12)'); // stock --zk-color-outline-variant
  });

  test('popup — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    await page.getByRole('button', { name: 'Show Popup', exact: true }).click();
    const def = page.locator('.z-popup.z-popup-open');
    await expect(def).toBeVisible();
    expect(await bgOf(def)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach the popup even though it is not a descendant of any
    // container in the page (open() reparents it to document.body).
    await page.addStyleTag({ content: ':root{--zk-popup-bg:#6750a4}' });
    expect(await bgOf(def)).toBe(SCOPED_PURPLE);
  });

  // ── notification: floating alert card (Clients.showNotification()). Only
  // the untyped/default state reads these knobs — the info/warning/error type
  // variants pin their own bg/fg via higher-specificity compound
  // selectors (same treatment as button/progressmeter's color variants), so a
  // typed notification is unaffected by an override. Unlike messagebox/popup,
  // the demo markup is a plain static div (not a JS-reparented widget), so
  // region scoping works normally here.
  // No accent knob: the left accent stripe was removed for MD3 fidelity, and an
  // untyped notification renders no icon glyph (ZK picks the glyph class from its
  // type map), so there is nothing left for an accent color to paint. ─────────
  test('notification — regional bg/fg/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-notification').first().locator('.z-notification-content').first();
    const scoped = page
      .locator('div[style*="--zk-notification-bg"] .z-notification')
      .first()
      .locator('.z-notification-content')
      .first();

    // Scoped card picks up every knob (bg + fg + radius).
    expect(await bgOf(scoped)).toBe('rgb(238, 242, 255)'); // #eef2ff
    expect(await colorOf(scoped)).toBe('rgb(26, 26, 46)'); // #1a1a2e
    expect(await radiusOf(scoped)).toBe('0px');

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await bgOf(def)).not.toBe('rgb(238, 242, 255)');
    expect(await colorOf(def)).not.toBe('rgb(26, 26, 46)');
    expect(await radiusOf(def)).toBe(STOCK_RADIUS); // stock --zk-shape-corner-extra-small
  });

  test('notification — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defContent = page.locator('.z-notification').first().locator('.z-notification-content').first();
    expect(await bgOf(defContent)).not.toBe('rgb(103, 80, 164)'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every untyped notification instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-notification-bg:#6750a4}' });
    expect(await bgOf(defContent)).toBe('rgb(103, 80, 164)');
  });

  // ── toast: MD3 snackbar (zkmax). Every toast always carries a type variant
  // (Toast.show() defaults null → "info"), so these knobs drive the INFO
  // (default) variant only — warning/error pin their own bg/fg/accent via
  // higher-specificity rules, unaffected by an override (same convention as
  // notification's type variants). Demo markup is a plain static div (not a
  // JS-reparented widget), so region scoping works normally here. ──────────
  test('toast — regional bg/fg/radius/accent override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-toast').first().locator('.z-toast-content').first();
    const scoped = page
      .locator('div[style*="--zk-toast-bg"] .z-toast')
      .first()
      .locator('.z-toast-content')
      .first();
    // --zk-toast-accent colors the LEADING icon (a direct child of .z-toast).
    // The close button's icon is pinned to --zk-color-on-surface by a separate
    // rule, so `> .z-toast-icon` is what the knob actually reaches.
    const defIcon = page.locator('.z-toast').first().locator('> .z-toast-icon').first();
    const scopedIcon = page
      .locator('div[style*="--zk-toast-bg"] .z-toast')
      .first()
      .locator('> .z-toast-icon')
      .first();

    // Scoped toast picks up every knob (bg + fg + radius + accent icon).
    expect(await bgOf(scoped)).toBe('rgb(238, 242, 255)'); // #eef2ff
    expect(await colorOf(scoped)).toBe('rgb(26, 26, 46)'); // #1a1a2e
    expect(await radiusOf(scoped)).toBe('0px');
    expect(await colorOf(scopedIcon)).toBe(SCOPED_PURPLE);

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await bgOf(def)).not.toBe('rgb(238, 242, 255)');
    expect(await colorOf(def)).not.toBe('rgb(26, 26, 46)');
    expect(await radiusOf(def)).toBe(STOCK_RADIUS); // stock --zk-shape-corner-extra-small
    expect(await colorOf(defIcon)).not.toBe(SCOPED_PURPLE);
  });

  test('toast — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defIcon = page.locator('.z-toast').first().locator('> .z-toast-icon').first();
    expect(await colorOf(defIcon)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every info-type toast instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-toast-accent:#6750a4}' });
    expect(await colorOf(defIcon)).toBe(SCOPED_PURPLE);
  });

  // ── a (anchor/link): text-only (no bg/border/radius). Resting AND hover
  // color read the same --zk-a-fg knob (both are the same value today).
  test('a — regional fg override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-a').first();
    const scoped = page.locator('div[style*="--zk-a-fg"] .z-a').first();

    // Scoped link picks up the fg override.
    expect(await colorOf(scoped)).toBe(SCOPED_PURPLE);

    // Sibling default link is untouched — the override lives on the scoped box
    // and is inherited only by its subtree, never leaking up to the default row.
    expect(await colorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('a — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-a').first();
    expect(await colorOf(def)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every link instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-a-fg:#6750a4}' });
    expect(await colorOf(def)).toBe(SCOPED_PURPLE);
  });

  // ── timepicker (EE, zkmax): outlined time field + time-list popup. Same
  // wrapper-border knob vocabulary as datebox/timebox/spinner/bandbox —
  // radius + resting border-color are the observable A/B here (the
  // clock-trigger button's radius reuses the same radius knob). ───────────
  test('timepicker — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-timepicker').first();
    const scoped = page.locator('div[style*="--zk-timepicker-radius"] .z-timepicker').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-corner-extra-small
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('timepicker — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-timepicker').first();
    expect(await radiusOf(def)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every timepicker instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-timepicker-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── chosenbox (EE, zkmax): multi-select input field + option popup. Same
  // wrapper-border knob vocabulary as datebox/timebox/spinner/bandbox/
  // daterangebox/timepicker — radius + resting border-color are the
  // observable A/B for the wrapper here. Selected chips get their own
  // resting/defining-state fill pair (item-bg / item-focus-bg, the latter
  // applied once a chip is clicked, armed for keyboard delete). ─────────────
  test('chosenbox — regional border/radius/chip override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-chosenbox').first();
    const scoped = page.locator('div[style*="--zk-chosenbox-radius"] .z-chosenbox').first();

    const defItem = def.locator('.z-chosenbox-item').first();
    const scopedItem = scoped.locator('.z-chosenbox-item').first();

    // Scoped wrapper picks up border/radius; scoped chip picks up the resting item-bg.
    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scopedItem)).toBe('rgb(239, 230, 255)'); // #efe6ff item-bg override

    // Clicking a chip arms it for delete (.z-chosenbox-item-focus) — the
    // defining state — which should pick up the item-focus-bg override.
    await scopedItem.click();
    expect(await bgOf(scopedItem)).toBe(SCOPED_PURPLE);

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(defItem)).not.toBe('rgb(239, 230, 255)');
    await defItem.click();
    expect(await bgOf(defItem)).not.toBe(SCOPED_PURPLE);
  });

  test('chosenbox — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-chosenbox').first();
    expect(await radiusOf(def)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every chosenbox instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-chosenbox-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── cascader (EE, zkmax): read-only trigger + right-expanding tree popup.
  // Same wrapper-border knob vocabulary as datebox/timebox/spinner/bandbox/
  // daterangebox/timepicker/chosenbox — radius + resting border-color are
  // the observable A/B for the wrapper here; zk-cascader-fg also colors the
  // trigger's selected-path label text (a pre-selected model gives it
  // visible content). ──────────────────────────────────────────────────────
  test('cascader — regional border/radius/fg override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-cascader').first();
    const scoped = page.locator('div[style*="--zk-cascader-radius"] .z-cascader').first();

    const defLabel = def.locator('.z-cascader-label').first();
    const scopedLabel = scoped.locator('.z-cascader-label').first();

    // Scoped trigger picks up border/radius; scoped label picks up the fg override.
    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedLabel)).toBe(SCOPED_PURPLE);

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(defLabel)).not.toBe(SCOPED_PURPLE);
  });

  test('cascader — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-cascader').first();
    expect(await radiusOf(def)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every cascader instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-cascader-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── searchbox (EE, zkmax): multi-select dropdown trigger + detached search
  // popup. Same wrapper-border knob vocabulary as datebox/timebox/spinner/
  // bandbox/daterangebox/timepicker/chosenbox/cascader — radius + resting
  // border-color are the observable A/B for the wrapper here; zk-searchbox-fg
  // also colors the trigger's selected-label text (a pre-selected model gives
  // it visible content). ──────────────────────────────────────────────────────
  test('searchbox — regional border/radius/fg override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-searchbox').first();
    const scoped = page.locator('div[style*="--zk-searchbox-radius"] .z-searchbox').first();

    const defLabel = def.locator('.z-searchbox-label').first();
    const scopedLabel = scoped.locator('.z-searchbox-label').first();

    // Scoped trigger picks up border/radius; scoped label picks up the fg override.
    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedLabel)).toBe(SCOPED_PURPLE);

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(defLabel)).not.toBe(SCOPED_PURPLE);
  });

  test('searchbox — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-searchbox').first();
    expect(await radiusOf(def)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every searchbox instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-searchbox-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── drawer (EE, zkmax): sliding side-sheet panel + header + close button.
  // Drawer.prototype.setVisible() reparents the ENTIRE .z-drawer root to the
  // floating root (document.body) on open() and moves it back on close()
  // (confirmed empirically: an open drawer's parentElement is BODY, not its
  // authored ZUL ancestor) — so, like messagebox/popup (a different root
  // cause each time), a REGIONAL (container) override cannot reach it; only
  // the whole-app :root override applies. There is no "sibling untouched"
  // test here for the same reason as messagebox/popup (see the tracker). ────
  test('drawer — zero-regression defaults', async ({ page }) => {
    await page.getByRole('button', { name: 'Open Drawer' }).click();
    const real = page.locator('.z-drawer.z-drawer-open .z-drawer-real');
    await expect(real).toBeVisible();

    expect(await bgOf(real)).toBe('rgb(255, 255, 255)'); // stock --zk-color-surface
    const header = real.locator('.z-drawer-header').first();
    expect(await borderBottomColorOf(header)).toBe('rgba(0, 0, 0, 0.12)'); // stock --zk-color-outline-variant
    expect(await colorOf(header)).toBe('rgba(0, 0, 0, 0.87)'); // stock --zk-color-on-surface
  });

  test('drawer — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    await page.getByRole('button', { name: 'Open Drawer' }).click();
    const real = page.locator('.z-drawer.z-drawer-open .z-drawer-real');
    await expect(real).toBeVisible();
    const header = real.locator('.z-drawer-header').first();
    expect(await colorOf(header)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach the drawer even though it is not a descendant of any
    // container in the page while open (setVisible() reparents it to document.body).
    await page.addStyleTag({ content: ':root{--zk-drawer-header-fg:#6750a4}' });
    expect(await colorOf(header)).toBe(SCOPED_PURPLE);
  });

  // ── nav (EE, zkmax): navbar container + collapsible nav group + leaf
  // navitem sidebar navigation. zk-navitem-fg / -radius are shared by both
  // the group header link (.z-nav-content) and the leaf item link
  // (.z-navitem-content, same value today); the selected leaf item is the
  // defining state — a rounded tonal pill, zk-navitem-selected-bg / -fg (no
  // left-edge accent bar — see doc/contracts/navbar.md c7). Renders in
  // place (no client-side reparenting), so region scoping works normally. ──
  test('nav — regional bg/radius/selected override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-navbar').first();
    const scoped = page.locator('div[style*="--zk-navbar-bg"] .z-navbar').first();

    const defContent = def.locator('.z-navitem-content').first();
    const scopedContent = scoped.locator('.z-navitem-content').first();
    const defSelected = def.locator('.z-navitem-selected > .z-navitem-content').first();
    const scopedSelected = scoped.locator('.z-navitem-selected > .z-navitem-content').first();

    // Scoped navbar picks up the container bg + item radius; the scoped
    // selected item picks up the defining-state fill/text pair.
    expect(await bgOf(scoped)).toBe('rgb(239, 230, 255)'); // #efe6ff navbar-bg override
    expect(await radiusOf(scopedContent)).toBe('0px');
    expect(await bgOf(scopedSelected)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedSelected)).toBe('rgb(255, 255, 255)');

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await bgOf(def)).toBe('rgb(247, 249, 252)'); // stock --zk-color-surface-container-low
    expect(await radiusOf(defContent)).toBe('8px'); // stock --zk-shape-corner-small
    expect(await bgOf(defSelected)).toBe('color(srgb 0.215686 0.435294 0.815686 / 0.12)'); // stock color-mix 12% primary
    expect(await colorOf(defSelected)).toBe('rgb(55, 111, 208)'); // stock --zk-color-primary
  });

  test('nav — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-navbar').first();
    const defSelected = def.locator('.z-navitem-selected > .z-navitem-content').first();
    expect(await colorOf(defSelected)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every navbar instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-navitem-selected-fg:#6750a4}' });
    expect(await colorOf(defSelected)).toBe(SCOPED_PURPLE);
  });

  // ── anchornav (EE, zkmax): anchor-link navigation list built on a plain
  // listbox. zk-anchornav-accent is a single defining-state knob that colors
  // BOTH the active item's left border indicator and the item's own link
  // text (border-left WIDTH stays hardcoded, same convention as tab's
  // border-bottom-color knob); zk-anchornav-fg is the resting item link's
  // text color. Renders in place (no client-side reparenting), so region
  // scoping works normally. ──
  test('anchornav — regional fg/accent override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-anchornav').first();
    const scoped = page.locator('div[style*="--zk-anchornav-fg"] .z-anchornav').first();

    const defRestingLink = def.locator('.z-a').first();
    const scopedRestingLink = scoped.locator('.z-a').first();
    const defSelectedRow = def.locator('.z-listitem-selected').first();
    const scopedSelectedRow = scoped.locator('.z-listitem-selected').first();
    const defSelectedLink = defSelectedRow.locator('.z-a').first();
    const scopedSelectedLink = scopedSelectedRow.locator('.z-a').first();

    // Scoped anchornav picks up the resting link color (fg) + the defining
    // accent (indicator border-left color + selected link text color).
    expect(await colorOf(scopedRestingLink)).toBe(SCOPED_PURPLE);
    expect(await borderLeftColorOf(scopedSelectedRow)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedSelectedLink)).toBe(SCOPED_PURPLE);

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await colorOf(defRestingLink)).toBe('rgba(0, 0, 0, 0.6)'); // stock --zk-color-on-surface-variant
    expect(await borderLeftColorOf(defSelectedRow)).toBe('rgb(55, 111, 208)'); // stock --zk-color-primary
    expect(await colorOf(defSelectedLink)).toBe('rgb(55, 111, 208)'); // stock --zk-color-primary
  });

  test('anchornav — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-anchornav').first();
    const defSelectedLink = def.locator('.z-listitem-selected .z-a').first();
    expect(await colorOf(defSelectedLink)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every anchornav instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-anchornav-accent:#6750a4}' });
    expect(await colorOf(defSelectedLink)).toBe(SCOPED_PURPLE);
  });

  // ── stepbar (EE, zkmax): connected-circle step-progress indicator.
  // zk-stepbar-accent is a single defining-state knob that colors the
  // active/complete icon fill + border AND the lit connector leading into
  // them, paired with zk-stepbar-accent-fg for the glyph inside that filled
  // circle (same "one knob, several roles" precedent as tab/calendar/nav's
  // accent). zk-stepbar-connector-color is the resting (upcoming) connector
  // line; zk-stepbar-icon-border-color is the upcoming icon's ring color.
  // zk-stepbar-fg is the resting AND complete title text; zk-stepbar-fg-active
  // is the active title text. Renders in place (no client-side reparenting),
  // so region scoping works normally. The demo uses activeIndex=1 with 3
  // steps: step 1 is complete, step 2 is active, step 3 is upcoming. ──
  const STEPBAR_ACCENT_FG = 'rgb(26, 26, 26)'; // #1a1a1a override (default --zk-stepbar-accent-fg is white)

  test('stepbar — regional connector/icon/title override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-stepbar').first();
    const scoped = page.locator('div[style*="--zk-stepbar-accent"] .z-stepbar').first();

    const defComplete = def.locator('.z-step-complete').first();
    const defActive = def.locator('.z-step-active').first();
    const defUpcoming = def.locator('.z-step:not(.z-step-active):not(.z-step-complete):not(.z-step-error)').first();
    const scopedComplete = scoped.locator('.z-step-complete').first();
    const scopedActive = scoped.locator('.z-step-active').first();
    const scopedUpcoming = scoped.locator('.z-step:not(.z-step-active):not(.z-step-complete):not(.z-step-error)').first();

    // Scoped stepbar picks up the resting connector/icon-border colors (on
    // the upcoming step), the defining accent (complete/active icon fill +
    // border, and the lit connector leading into the active step), the
    // accent-fg glyph color, and the fg/fg-active title colors.
    expect(await pseudoBgOf(scopedUpcoming, '::before')).toBe(SCOPED_PURPLE);
    expect(await borderColorOf(scopedUpcoming.locator('.z-step-icon'))).toBe(SCOPED_PURPLE);
    expect(await bgOf(scopedComplete.locator('.z-step-icon'))).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedComplete.locator('.z-step-icon'))).toBe(STEPBAR_ACCENT_FG);
    expect(await pseudoBgOf(scopedActive, '::before')).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedComplete.locator('.z-step-title'))).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedActive.locator('.z-step-title'))).toBe(SCOPED_PURPLE);

    // Sibling default is untouched — the override lives on the scoped box
    // and is inherited only by its subtree, never leaking up to the default row.
    expect(await pseudoBgOf(defUpcoming, '::before')).toBe('rgba(0, 0, 0, 0.12)'); // stock --zk-color-outline-variant
    expect(await borderColorOf(defUpcoming.locator('.z-step-icon'))).toBe('rgba(0, 0, 0, 0.23)'); // stock --zk-color-outline
    expect(await bgOf(defComplete.locator('.z-step-icon'))).toBe('rgb(55, 111, 208)'); // stock --zk-color-primary
    expect(await colorOf(defComplete.locator('.z-step-icon'))).toBe('rgb(255, 255, 255)'); // stock --zk-color-on-primary
    expect(await pseudoBgOf(defActive, '::before')).toBe('rgb(55, 111, 208)'); // stock --zk-color-primary
    expect(await colorOf(defComplete.locator('.z-step-title'))).toBe('rgba(0, 0, 0, 0.6)'); // stock --zk-color-on-surface-variant
    expect(await colorOf(defActive.locator('.z-step-title'))).toBe('rgba(0, 0, 0, 0.87)'); // stock --zk-color-on-surface
  });

  test('stepbar — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-stepbar').first();
    const defComplete = def.locator('.z-step-complete').first();
    expect(await bgOf(defComplete.locator('.z-step-icon'))).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every stepbar instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-stepbar-accent:#6750a4}' });
    expect(await bgOf(defComplete.locator('.z-step-icon'))).toBe(SCOPED_PURPLE);
  });

  // ── coachmark (EE, zkmax): guided-tour rich-tooltip card pointing at a
  // target element. Coachmark's _open() calls zk.makeVParent() on the root
  // .z-coachmark node (confirmed in the compiled zkmax widget bundle),
  // reparenting the ENTIRE root (content + pointer + close button) to the
  // floating root (document.body) on open() and moves it back with
  // undoVParent() on close() — so, like drawer (a different root cause each
  // time), a REGIONAL (container) override cannot reach it; only the
  // whole-app :root override applies. There is no "sibling untouched" test
  // here for the same reason as drawer/messagebox/popup (see the tracker). ──
  test('coachmark — zero-regression defaults', async ({ page }) => {
    await page.getByRole('button', { name: 'Show Coachmark' }).click();
    const card = page.locator('.z-coachmark.z-coachmark-open .z-coachmark-content');
    await expect(card).toBeVisible();

    expect(await radiusOf(card)).toBe('6px'); // stock --zk-shape-card
    expect(await bgOf(card)).toBe('rgb(247, 249, 252)'); // stock --zk-color-surface-container-low
    expect(await colorOf(card)).toBe('rgba(0, 0, 0, 0.87)'); // stock --zk-color-on-surface
  });

  test('coachmark — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    await page.getByRole('button', { name: 'Show Coachmark' }).click();
    const card = page.locator('.z-coachmark.z-coachmark-open .z-coachmark-content');
    await expect(card).toBeVisible();
    expect(await bgOf(card)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach the coachmark even though it is not a descendant of any
    // container in the page while open (_open() reparents it to document.body).
    await page.addStyleTag({ content: ':root{--zk-coachmark-bg:#6750a4}' });
    expect(await bgOf(card)).toBe(SCOPED_PURPLE);
  });

  // ── colorbox (PE, zkex): color-picker swatch trigger + gradient/palette
  // popup. No focus/open border state exists (only resting + hover), so
  // there is no border-color-focus knob; radius + resting border-color are
  // the observable A/B for the wrapper here, the same wrapper-border knob
  // vocabulary as datebox/timebox/spinner/bandbox/daterangebox. ────────────
  test('colorbox — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-colorbox').first();
    const scoped = page.locator('div[style*="--zk-colorbox-radius"] .z-colorbox').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-input
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('colorbox — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-colorbox').first();
    expect(await radiusOf(def)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every colorbox instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-colorbox-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── biglistbox: virtual/lazy-loading data table (EE, zkmax) ─────────────────
  test('biglistbox — regional border/radius/header-fg override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-biglistbox').first();
    const scoped = page.locator('div[style*="--zk-biglistbox-radius"] .z-biglistbox').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scoped.locator('.z-biglistbox-header').first())).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('6px'); // stock --zk-shape-card
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(def.locator('.z-biglistbox-header').first())).not.toBe(SCOPED_PURPLE);
  });

  test('biglistbox — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-biglistbox').first();
    expect(await radiusOf(def)).toBe('6px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every biglistbox instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-biglistbox-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── fisheye / fisheyebar (PE, zkex): magnetic dock icon bar. Magnification
  // is JS-driven (mousemove sets inline width/height), so there is no
  // hover/selected color state to expose — the item label text and the
  // icon's corner radius are the observable A/B here; the bar's own
  // background stays transparent by design, not knob-driven. ──────────────
  test('fisheye — regional label/radius override, sibling untouched', async ({ page }) => {
    const defText = page.locator('.z-fisheye-text').first();
    const defImage = page.locator('.z-fisheye-image').first();
    const scopedText = page.locator('div[style*="--zk-fisheye-radius"] .z-fisheye-text').first();
    const scopedImage = page.locator('div[style*="--zk-fisheye-radius"] .z-fisheye-image').first();

    expect(await colorOf(scopedText)).toBe(SCOPED_PURPLE);
    expect(await radiusOf(scopedImage)).toBe('0px');

    expect(await colorOf(defText)).not.toBe(SCOPED_PURPLE);
    expect(await radiusOf(defImage)).toBe('4px'); // stock --zk-shape-corner-extra-small
  });

  test('fisheye — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defImage = page.locator('.z-fisheye-image').first();
    expect(await radiusOf(defImage)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every fisheye instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-fisheye-radius:0px}' });
    expect(await radiusOf(defImage)).toBe('0px');
  });

  // ── pdfviewer (PE, zkex): PDF document viewer — root wrapper + scrollable
  // canvas area + a floating, bottom-centred toolbar. The border-color knob
  // is shared by the root wrapper's own border and the toolbar separator's
  // border-left (one knob, several roles); the canvas area and floating
  // toolbar are separate surfaces with their own bg/radius knobs. ─────────
  test('pdfviewer — regional border/radius/toolbar override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-pdfviewer').first();
    const scoped = page.locator('div[style*="--zk-pdfviewer-radius"] .z-pdfviewer').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scoped.locator('.z-pdfviewer-toolbar').first())).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('12px'); // stock --zk-shape-corner-medium
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(def.locator('.z-pdfviewer-toolbar').first())).not.toBe(SCOPED_PURPLE);
  });

  test('pdfviewer — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-pdfviewer').first();
    expect(await radiusOf(def)).toBe('12px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every pdfviewer instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-pdfviewer-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── tbeditor (EE, zkmax): MD3 outlined rich-text editor — .z-tbeditor-box
  // wrapper (border-color/radius) + the toolbar pane's own tonal fill
  // (--zk-tbeditor-toolbar-bg). bg/fg are shared by the wrapper, editor
  // canvas, source textarea, and dropdown text (one knob, several roles). ──
  test('tbeditor — regional border/radius/toolbar override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-tbeditor-box').first();
    const scoped = page.locator('div[style*="--zk-tbeditor-radius"] .z-tbeditor-box').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scoped.locator('.z-tbeditor-button-pane').first())).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('8px'); // stock --zk-shape-corner-small
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(def.locator('.z-tbeditor-button-pane').first())).not.toBe(SCOPED_PURPLE);
  });

  test('tbeditor — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-tbeditor-box').first();
    expect(await radiusOf(def)).toBe('8px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every tbeditor instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-tbeditor-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── signature (EE, zkmax): signature-pad canvas field — .z-signature root
  // wrapper. The wrapper's only border states are resting + focus-within (no
  // hover), so there is no border-color-hover knob; radius + resting
  // border-color are the observable A/B here, the same wrapper-border knob
  // vocabulary as colorbox/datebox/timebox/spinner/bandbox/daterangebox. ───
  test('signature — regional border/radius override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-signature').first();
    const scoped = page.locator('div[style*="--zk-signature-radius"] .z-signature').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('12px'); // stock --zk-shape-corner-medium
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('signature — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-signature').first();
    expect(await radiusOf(def)).toBe('12px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every signature instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-signature-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── cropper (EE, zkmax): image-crop field — .z-cropper root wrapper
  // (border-color/radius only, no bg) + a floating, pill-shaped toolbar with
  // two text action links. The toolbar is the component's one filled surface
  // (its own bg/radius knobs); the Crop (confirming) link reads the defining
  // accent, the Cancel (dismissive) link reads the resting/neutral fg. ─────
  test('cropper — regional border/radius/toolbar/text override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-cropper').first();
    const scoped = page.locator('div[style*="--zk-cropper-radius"] .z-cropper').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scoped.locator('.z-cropper-toolbar').first())).toBe(SCOPED_PURPLE);
    expect(await colorOf(scoped.locator('.z-cropper-crop > a').first())).toBe('rgb(255, 255, 255)');
    expect(await colorOf(scoped.locator('.z-cropper-cancel > a').first())).toBe('rgb(255, 255, 255)');

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-corner-extra-small
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(def.locator('.z-cropper-toolbar').first())).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(def.locator('.z-cropper-crop > a').first())).not.toBe('rgb(255, 255, 255)');
    expect(await colorOf(def.locator('.z-cropper-cancel > a').first())).not.toBe('rgb(255, 255, 255)');
  });

  test('cropper — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-cropper').first();
    expect(await radiusOf(def)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every cropper instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-cropper-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── dropupload (EE, zkmax): HTML5 drag-and-drop file-upload drop zone —
  // .z-dropupload root is the ONLY themeable surface (border-color/radius/
  // background only; ZK emits no drag-over or disabled state class, so there
  // is no hover/focus/disabled knob, and no fg/text knob either). ──────────
  test('dropupload — regional border/radius/bg override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-dropupload').first();
    const scoped = page.locator('div[style*="--zk-dropupload-radius"] .z-dropupload').first();

    expect(await radiusOf(scoped)).toBe('0px');
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scoped)).toBe(SCOPED_PURPLE);

    expect(await radiusOf(def)).toBe('4px'); // stock --zk-shape-corner-extra-small
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(def)).not.toBe(SCOPED_PURPLE);
  });

  test('dropupload — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-dropupload').first();
    expect(await radiusOf(def)).toBe('4px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every dropupload instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-dropupload-radius:0px}' });
    expect(await radiusOf(def)).toBe('0px');
  });

  // ── organigram (EE, zkmax): org-chart tree — .z-orgnode card
  // (bg/fg/radius/border-color, hover swaps border-color-hover/hover-bg) +
  // connector lines (bus + drop segments share the same border-color knob as
  // the card's resting border — one knob, several roles). Selected is the
  // defining state — its own bg/border/fg triad; the selected node's icon
  // reads the same selected-fg. Renders in place (no client-side
  // reparenting), so region scoping works normally. ───────────────────────
  test('organigram — regional border/radius/selected override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-organigram').first();
    const scoped = page.locator('div[style*="--zk-organigram-radius"] .z-organigram').first();

    const defNode = def.locator('.z-orgnode').first();
    const scopedNode = scoped.locator('.z-orgnode').first();
    const defSelected = def.locator('.z-orgitem-selected > .z-orgnode').first();
    const scopedSelected = scoped.locator('.z-orgitem-selected > .z-orgnode').first();

    // Scoped organigram picks up the card radius/border-color, and the
    // scoped selected node picks up the defining-state fill/text pair.
    expect(await radiusOf(scopedNode)).toBe('0px');
    expect(await borderColorOf(scopedNode)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scopedSelected)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedSelected)).toBe('rgb(255, 255, 255)');

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await radiusOf(defNode)).toBe('6px'); // stock --zk-shape-card
    expect(await borderColorOf(defNode)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(defSelected)).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(defSelected)).not.toBe('rgb(255, 255, 255)');
  });

  test('organigram — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-organigram').first();
    const defNode = def.locator('.z-orgnode').first();
    expect(await radiusOf(defNode)).toBe('6px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every organigram instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-organigram-radius:0px}' });
    expect(await radiusOf(defNode)).toBe('0px');
  });

  // ── goldenlayout (EE, zkmax): dockable tab layout — .lm_header tab strip
  // (bg + shared border-color) + .lm_tab (resting/hover/active accent) +
  // .z-goldenpanel content card (bg + the same shared border-color/radius as
  // the header). Two panels share one area, so GL stacks them as tabs and
  // the first-added panel starts active — exercising the accent knob.
  // Renders in place (no client-side reparenting), so region scoping works
  // normally. ────────────────────────────────────────────────────────────
  test('goldenlayout — regional header/panel/accent override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-goldenlayout').first();
    const scoped = page.locator('div[style*="--zk-goldenlayout-radius"] .z-goldenlayout').first();

    const defHeader = def.locator('.lm_header').first();
    const scopedHeader = scoped.locator('.lm_header').first();
    const defPanel = def.locator('.z-goldenpanel').first();
    const scopedPanel = scoped.locator('.z-goldenpanel').first();
    const defActiveTab = def.locator('.lm_tab.lm_active').first();
    const scopedActiveTab = scoped.locator('.lm_tab.lm_active').first();

    // Scoped goldenlayout picks up the header fill, the panel's shared
    // radius/border-color, and the active tab's accent text color.
    expect(await bgOf(scopedHeader)).toBe(SCOPED_PURPLE);
    expect(await radiusOf(scopedPanel)).toBe('0px');
    expect(await borderColorOf(scopedPanel)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedActiveTab)).toBe(SCOPED_PURPLE);

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await bgOf(defHeader)).not.toBe(SCOPED_PURPLE);
    expect(await radiusOf(defPanel)).toBe('6px'); // stock --zk-shape-card
    expect(await borderColorOf(defPanel)).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(defActiveTab)).not.toBe(SCOPED_PURPLE);
  });

  test('goldenlayout — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-goldenlayout').first();
    const defPanel = def.locator('.z-goldenpanel').first();
    expect(await radiusOf(defPanel)).toBe('6px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every goldenlayout instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-goldenlayout-radius:0px}' });
    expect(await radiusOf(defPanel)).toBe('0px');
  });

  // ── portallayout (EE, zkmax): transparent multi-column drag-drop dashboard
  // shell — .z-portalchildren-frame is the component's ONLY card surface
  // (shown only when a column carries a title attribute): bg/border-color/
  // radius style the frame, fg styles its title text. The panel-count badge
  // (.z-portalchildren-counter-on, counterVisible="true") is a distinct
  // sub-part with its own bg/fg/radius triad. Renders in place (no
  // client-side reparenting — only the drag ghost is prepended to <body>,
  // and only during an active drag), so region scoping works normally. ────
  test('portallayout — regional frame/counter override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-portallayout').first();
    const scoped = page.locator('div[style*="--zk-portallayout-radius"] .z-portallayout').first();

    const defFrame = def.locator('.z-portalchildren-frame').first();
    const scopedFrame = scoped.locator('.z-portalchildren-frame').first();
    const defCounter = def.locator('.z-portalchildren-counter-on').first();
    const scopedCounter = scoped.locator('.z-portalchildren-counter-on').first();

    // Scoped portallayout picks up the frame's radius/border-color and the
    // panel-count badge's bg/fg pair.
    expect(await radiusOf(scopedFrame)).toBe('0px');
    expect(await borderColorOf(scopedFrame)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scopedCounter)).toBe(SCOPED_PURPLE);
    expect(await colorOf(scopedCounter)).toBe('rgb(255, 255, 255)');

    // Sibling default is untouched — the override lives on the scoped box and
    // is inherited only by its subtree, never leaking up to the default row.
    expect(await radiusOf(defFrame)).toBe('6px'); // stock --zk-shape-card
    expect(await borderColorOf(defFrame)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(defCounter)).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(defCounter)).not.toBe('rgb(255, 255, 255)');
  });

  test('portallayout — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const def = page.locator('.z-portallayout').first();
    const defFrame = def.locator('.z-portalchildren-frame').first();
    expect(await radiusOf(defFrame)).toBe('6px'); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every portallayout instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-portallayout-radius:0px}' });
    expect(await radiusOf(defFrame)).toBe('0px');
  });

  // ── confirmpopup (native, CE): lightweight anchored confirmation popover
  // (Popover analog, not a modal dialog). Confirmpopup extends Popup and its
  // open() calls super.open(), inheriting Popup's own zk.makeVParent() call,
  // which reparents the widget's real DOM node to document.body — the
  // identical root cause as popup, its closest sibling — so, like
  // messagebox/popup/coachmark, a REGIONAL (container) override cannot reach
  // it; only the whole-app :root override applies. There is no "sibling
  // untouched" test here for the same reason as messagebox/popup/coachmark
  // (see the tracker). The severity icon color is a semantic status color
  // (identical mapping to chip/badge), intentionally not knob-driven. ───────
  test('confirmpopup — zero-regression defaults', async ({ page }) => {
    await page.getByRole('button', { name: 'Show Confirmpopup', exact: true }).click();
    const pop = page.locator('.z-confirmpopup.z-confirmpopup-open');
    await expect(pop).toBeVisible();

    expect(await radiusOf(pop)).toBe('6px'); // stock --zk-shape-card
    expect(await bgOf(pop)).toBe('rgb(247, 249, 252)'); // stock --zk-color-surface-container-low
    expect(await colorOf(pop)).toBe('rgba(0, 0, 0, 0.87)'); // stock --zk-color-on-surface

    const ok = pop.locator('.z-confirmpopup-ok');
    const cancel = pop.locator('.z-confirmpopup-cancel');
    expect(await bgOf(ok)).toBe('rgb(55, 111, 208)'); // stock --zk-color-primary
    expect(await colorOf(ok)).toBe('rgb(255, 255, 255)'); // stock --zk-color-on-primary
    expect(await borderColorOf(cancel)).toBe('rgba(0, 0, 0, 0.23)'); // stock --zk-color-outline
    expect(await colorOf(cancel)).toBe('rgba(0, 0, 0, 0.6)'); // stock --zk-color-on-surface-variant
  });

  test('confirmpopup — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    await page.getByRole('button', { name: 'Show Confirmpopup', exact: true }).click();
    const pop = page.locator('.z-confirmpopup.z-confirmpopup-open');
    await expect(pop).toBeVisible();
    expect(await bgOf(pop)).not.toBe(SCOPED_PURPLE); // baseline before override
    const icon = pop.locator('.z-confirmpopup-icon');
    const iconColorBefore = await colorOf(icon);

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach the popover even though it is not a descendant of any
    // container in the page (Popup.open()'s makeVParent() attaches it to
    // document.body).
    await page.addStyleTag({ content: ':root{--zk-confirmpopup-bg:#6750a4}' });
    expect(await bgOf(pop)).toBe(SCOPED_PURPLE);

    // The severity icon color is a semantic status color, not knob-driven — it
    // must stay unaffected by the bg override (state integrity, CTV-7).
    expect(await colorOf(icon)).toBe(iconColorBefore);
  });

  // ── breadcrumb (native, CE): plain inline nav trail (borderless text chrome,
  // same curation choice as `a`/`caption` — no bg/border/radius knob to expose).
  // --zk-breadcrumb-fg covers the separator + ellipsis button (resting) + link
  // item (resting); --zk-breadcrumb-current-fg is the one defining-state knob
  // (the terminal "you are here" item). Renders in place (no client-side
  // reparenting), so region scoping works normally. ─────────────────────────
  test('breadcrumb — regional fg/current-fg override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-breadcrumb').first();
    const scoped = page.locator('div[style*="--zk-breadcrumb-fg"] .z-breadcrumb').first();

    // Scoped breadcrumb's link picks up the fg override; its terminal (current)
    // item picks up the current-fg override.
    expect(await colorOf(scoped.locator('.z-breadcrumbitem > a').first())).toBe(SCOPED_PURPLE);
    expect(await colorOf(scoped.locator('.z-breadcrumbitem > span').first())).toBe(SCOPED_PURPLE);

    // Sibling default breadcrumb is untouched — the override lives on the scoped
    // box and is inherited only by its subtree, never leaking up to the default row.
    expect(await colorOf(def.locator('.z-breadcrumbitem > a').first())).not.toBe(SCOPED_PURPLE);
    expect(await colorOf(def.locator('.z-breadcrumbitem > span').first())).not.toBe(SCOPED_PURPLE);
  });

  test('breadcrumb — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defLink = page.locator('.z-breadcrumb').first().locator('.z-breadcrumbitem > a').first();
    expect(await colorOf(defLink)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every breadcrumb instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-breadcrumb-fg:#6750a4}' });
    expect(await colorOf(defLink)).toBe(SCOPED_PURPLE);
  });

  // ── carousel (native, CE): full-bleed slideshow frame with overlay arrows/
  // indicators/caption atop arbitrary slide content — none of the overlay chrome
  // keys off surface/on-surface tokens. --zk-carousel-arrow-bg is the arrow's
  // resting scrim fill; --zk-carousel-indicator-active-bg is the active
  // indicator dot's fill (read off its ::before pseudo-element, not the button
  // itself). Renders in place (no client-side reparenting), so region scoping
  // works normally. ─────────────────────────────────────────────────────────
  test('carousel — regional arrow-bg/indicator-active-bg override, sibling untouched', async ({ page }) => {
    const def = page.locator('.z-carousel').first();
    const scoped = page.locator('div[style*="--zk-carousel-arrow-bg"] .z-carousel').first();

    // Scoped carousel's arrow picks up the arrow-bg override; its active
    // indicator dot picks up the indicator-active-bg override.
    expect(await bgOf(scoped.locator('.z-carousel-arrow').first())).toBe(SCOPED_PURPLE);
    expect(await pseudoBgOf(scoped.locator('.z-carousel-indicator-active').first(), '::before')).toBe(SCOPED_PURPLE);

    // Sibling default carousel is untouched — the override lives on the scoped
    // box and is inherited only by its subtree, never leaking up to the default row.
    expect(await bgOf(def.locator('.z-carousel-arrow').first())).not.toBe(SCOPED_PURPLE);
    expect(await pseudoBgOf(def.locator('.z-carousel-indicator-active').first(), '::before')).not.toBe(SCOPED_PURPLE);
  });

  test('carousel — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    const defArrow = page.locator('.z-carousel').first().locator('.z-carousel-arrow').first();
    expect(await bgOf(defArrow)).not.toBe(SCOPED_PURPLE); // baseline before override

    // An adopter's :root override, injected after the theme bundle, must win the
    // cascade and reach every carousel instance, including the default one.
    await page.addStyleTag({ content: ':root{--zk-carousel-arrow-bg:#6750a4}' });
    expect(await bgOf(defArrow)).toBe(SCOPED_PURPLE);
  });

  // ── Codeeditor (CE @since ZK 11.0.0) ───────────────────────────────────────
  // Fifteen knobs; these probe one from each of the two families that must not be
  // homogenized — `--zk-codeeditor-bg`/`-border-color` (Marble's own chrome names)
  // and `-gutter-bg`. The gutter is the interesting one: CodeMirror paints
  // `.cm-gutters` from its OWN unlayered runtime StyleModule, so the theme only
  // reaches it through an `!important` rule; if that rule were ever dropped the
  // knob would silently stop working while the root kept honouring its override.
  // Deliberately NOT probed here: the focus ring, which needs an interaction and is
  // already covered — in painted pixels — by `screenshot.spec.ts › codeeditor`.
  // CodeMirror mounts asynchronously, so wait for `.cm-editor` before reading.
  // ───────────────────────────────────────────────────────────────────────────
  test('codeeditor — regional bg/border/gutter override, sibling untouched', async ({ page }) => {
    await page.waitForSelector('.z-codeeditor .cm-editor');
    const def = page.locator('.z-codeeditor').first();
    const scoped = page.locator('div[style*="--zk-codeeditor-bg"] .z-codeeditor').first();

    expect(await bgOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await borderColorOf(scoped)).toBe(SCOPED_PURPLE);
    expect(await bgOf(scoped.locator('.cm-gutters').first())).toBe(SCOPED_PURPLE);

    // The sibling default editor must be untouched — the override lives on the
    // scoped box and is inherited only by its subtree.
    expect(await bgOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await borderColorOf(def)).not.toBe(SCOPED_PURPLE);
    expect(await bgOf(def.locator('.cm-gutters').first())).not.toBe(SCOPED_PURPLE);
  });

  test('codeeditor — whole-app :root override wins (loaded after norm.css.dsp)', async ({ page }) => {
    await page.waitForSelector('.z-codeeditor .cm-editor');
    const def = page.locator('.z-codeeditor').first();
    expect(await bgOf(def)).not.toBe(SCOPED_PURPLE); // baseline before override

    await page.addStyleTag({ content: ':root{--zk-codeeditor-bg:#6750a4;--zk-codeeditor-gutter-bg:#6750a4}' });
    expect(await bgOf(def)).toBe(SCOPED_PURPLE);
    // Reaches through CodeMirror's unlayered gutter rule too, not just the root.
    expect(await bgOf(def.locator('.cm-gutters').first())).toBe(SCOPED_PURPLE);
  });
});
