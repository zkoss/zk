import { test, expect, Locator, Page } from '@playwright/test';

// Guard for zul window.css, groupbox.css and panel.css: a state class on the root of a window, groupbox or panel
// (header-less, no border, overlapped, modal, highlighted, 3d) styles the header or content of that container only.
// The container in the default state nested inside it keeps its own look, so each case holds an inner container that
// no state class applies to. The outer container still wears the rule: the second assertion of each test.
// ZK adds z-window-noheader / z-groupbox-notitle only when the container renders no header, so a descendant rule on
// that class can only ever hit a nested container.
// Fixture: web/pv/nested-header-guard.zul
//
// The tabbox, listbox and grid tests at the end of the file hold the same rule for the orient classes of a tabbox
// (web/pv/tabbox-nested-guard.zul) and the odd-row stripe of a listbox and a grid (web/pv/listbox-nested-guard.zul,
// web/pv/grid-nested-guard.zul): a widget nested in a part of another one looks like the same widget on its own.

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

const NESTED_TABBOX_FIXTURE = '/pv/tabbox-nested-guard.zul';

/** Each outer orient with the orient of the tabbox in its first tabpanel; `reference` is that tabbox on its own. */
const NESTED_TABBOXES = [
  { outer: 'left', inner: 'top', reference: 'top' },
  { outer: 'right', inner: 'top', reference: 'top' },
  { outer: 'bottom', inner: 'top', reference: 'top' },
  { outer: 'top', inner: 'top', reference: 'top' },
  { outer: 'bottom-left', inner: 'left', reference: 'left' },
  { outer: 'left-bottom', inner: 'bottom', reference: 'bottom' },
];

const SIDES = ['top', 'right', 'bottom', 'left'];
const TABBOX_PROPERTIES = [
  'display', 'flex-direction', 'flex-grow', 'flex-shrink', 'flex-basis', 'align-self', 'justify-content', 'min-width', 'width',
  'color', 'overflow-x',
  'border-top-left-radius', 'border-top-right-radius', 'border-bottom-right-radius', 'border-bottom-left-radius',
  ...SIDES.flatMap(side => [`padding-${side}`, `margin-${side}`, `border-${side}-width`, `border-${side}-style`, `border-${side}-color`]),
];

/** Part name → [selector below the tabbox root, index among its matches]. */
type TabboxParts = Record<string, [string, number]>;

const STRIP_PARTS: TabboxParts = {
  tabs: [':scope > .z-tabs', 0],
  'tabs-content': [':scope > .z-tabs > .z-tabs-content', 0],
  'tab 1 (selected)': [':scope > .z-tabs .z-tab', 0],
  'tab 2 (closable)': [':scope > .z-tabs .z-tab', 1],
  'tab 3 (disabled)': [':scope > .z-tabs .z-tab', 2],
  'tab-content 1': [':scope > .z-tabs .z-tab-content', 0],
  'tab-content 2': [':scope > .z-tabs .z-tab-content', 1],
  'tab-button': [':scope > .z-tabs .z-tab-button', 0],
  tabpanels: [':scope > .z-tabpanels', 0],
};

const ACCORDION_PARTS: TabboxParts = {
  tabpanels: [':scope > .z-tabpanels', 0],
  'tabpanel 1': [':scope > .z-tabpanels > .z-tabpanel', 0],
  'tab 1 (selected)': [':scope > .z-tabpanels > .z-tabpanel > .z-tab', 0],
  'tab 2 (closable)': [':scope > .z-tabpanels > .z-tabpanel > .z-tab', 1],
  'tab 3 (disabled)': [':scope > .z-tabpanels > .z-tabpanel > .z-tab', 2],
  'tab-content 1': [':scope > .z-tabpanels > .z-tabpanel > .z-tab > .z-tab-content', 0],
  'tab-content 2': [':scope > .z-tabpanels > .z-tabpanel > .z-tab > .z-tab-content', 1],
  'tab-button': [':scope > .z-tabpanels > .z-tabpanel > .z-tab .z-tab-button', 0],
};

/** The computed values of the tabbox root and of the given parts of it. */
function measureTabbox(tabbox: Locator, parts: TabboxParts): Promise<Record<string, string>> {
  return tabbox.evaluate((root, { parts, properties }) => {
    const values: Record<string, string> = {};
    const elements: Record<string, Element | undefined> = { tabbox: root };
    for (const [name, [selector, index]] of Object.entries(parts) as [string, [string, number]][])
      elements[name] = root.querySelectorAll(selector)[index];
    for (const [name, el] of Object.entries(elements)) {
      if (!el) throw new Error(`missing part: ${name}`);
      const style = getComputedStyle(el);
      for (const property of properties) values[`${name} ${property}`] = style.getPropertyValue(property);
    }
    return values;
  }, { parts, properties: TABBOX_PROPERTIES });
}

async function expectSameTabbox(nested: Locator, alone: Locator, parts: TabboxParts): Promise<void> {
  await expect(nested).toHaveCount(1);
  await expect(alone).toHaveCount(1);
  const nestedValues = await measureTabbox(nested, parts);
  const aloneValues = await measureTabbox(alone, parts);
  const differing = Object.keys(aloneValues)
    .filter(key => nestedValues[key] !== aloneValues[key])
    .map(key => `${key}: nested ${nestedValues[key]}, alone ${aloneValues[key]}`);
  expect(differing).toEqual([]);
}

for (const { outer, inner, reference } of NESTED_TABBOXES) {
  test(`tabbox — a ${inner} tabbox nested in a ${outer} tabbox looks like the same tabbox on its own`, async ({ page }) => {
    await page.goto(NESTED_TABBOX_FIXTURE, { waitUntil: 'networkidle' });
    await expectSameTabbox(page.locator(`.nt-outer-${outer} .nt-inner`), page.locator(`.nt-ref-${reference}`), STRIP_PARTS);
  });
}

for (const outer of ['left', 'right', 'bottom']) {
  test(`tabbox — an accordion tabbox nested in a ${outer} tabbox looks like the same accordion on its own`, async ({ page }) => {
    await page.goto(NESTED_TABBOX_FIXTURE, { waitUntil: 'networkidle' });
    await expectSameTabbox(page.locator(`.nt-outer-${outer}-accordion .nt-inner`), page.locator('.nt-ref-accordion'), ACCORDION_PARTS);
  });
}

test('tabbox — the orient of a tabbox still shapes its own tab bar and tabpanels', async ({ page }) => {
  await page.goto(NESTED_TABBOX_FIXTURE, { waitUntil: 'networkidle' });
  const tabs = (orient: string) => page.locator(`.nt-outer-${orient} > .z-tabs`);
  const tabpanels = (orient: string) => page.locator(`.nt-outer-${orient} > .z-tabpanels`);
  expect(await style(tabs('left'), 'flex-direction'), 'left').toBe('column');
  expect(await style(tabs('left'), 'border-right-width'), 'left').toBe('1px');
  expect(await style(tabs('left'), 'min-width'), 'left').toBe('120px');
  expect(await style(tabs('right'), 'flex-direction'), 'right').toBe('column');
  expect(await style(tabs('right'), 'border-left-width'), 'right').toBe('1px');
  expect(await style(tabs('bottom'), 'border-top-width'), 'bottom').toBe('1px');
  expect(await style(tabs('bottom'), 'border-bottom-width'), 'bottom').toBe('0px');
  expect(await style(tabpanels('bottom'), 'border-top-left-radius'), 'bottom panels, top corner').not.toBe('0px');
  expect(await style(tabpanels('bottom'), 'border-bottom-left-radius'), 'bottom panels, bottom corner').toBe('0px');
  expect(await style(tabs('top'), 'flex-direction'), 'top').toBe('row');
});

const NESTED_LISTBOX_FIXTURE = '/pv/listbox-nested-guard.zul';

test('listbox — selected rows of a listbox nested in an odd-striped row keep the selected fill', async ({ page }) => {
  await page.goto(NESTED_LISTBOX_FIXTURE, { waitUntil: 'networkidle' });
  // A row fill transitions on hover; read it settled.
  await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important}' });
  const rows = (scope: string) => page.locator(`.${scope} > .z-listbox-body tr.z-listitem`);
  const nested = rows('nl-inner');
  const alone = rows('nl-ref');
  await expect(nested).toHaveCount(4);
  await expect(alone).toHaveCount(4);
  await expect(page.locator('.nl-outer > .z-listbox-body tr.z-listbox-odd:has(.nl-inner)'), 'the nested listbox sits in an odd-striped row').toHaveCount(1);

  const selectedFill = await style(alone.nth(0), 'background-color');
  const restFill = await style(alone.nth(2), 'background-color');
  expect(selectedFill, 'the selected fill differs from a resting row').not.toBe(restFill);
  // Rows 1 and 3 are at odd positions among their siblings, rows 2 and 4 at even ones; rows 1 and 2 are selected.
  for (const index of [0, 1])
    expect(await style(nested.nth(index), 'background-color'), `selected row ${index + 1}`).toBe(selectedFill);
  expect(await style(nested.nth(2), 'background-color'), 'unselected row 3').toBe(restFill);

  // A hovered row, selected or not, takes the hover fill of the same row of the listbox on its own.
  for (const index of [0, 2]) {
    await alone.nth(index).hover();
    const hoverFill = await style(alone.nth(index), 'background-color');
    await nested.nth(index).hover();
    expect(await style(nested.nth(index), 'background-color'), `hovered row ${index + 1}`).toBe(hoverFill);
  }
});

const NESTED_GRID_FIXTURE = '/pv/grid-nested-guard.zul';

test('grid — rows of a grid nested in an odd-striped row take the hover fill', async ({ page }) => {
  await page.goto(NESTED_GRID_FIXTURE, { waitUntil: 'networkidle' });
  // A row fill transitions on hover; read it settled.
  await page.addStyleTag({ content: '*,*::before,*::after{transition:none!important}' });
  const rows = (scope: string) => page.locator(`.${scope} > .z-grid-body tr.z-row`);
  const nested = rows('ng-inner');
  const alone = rows('ng-ref');
  await expect(nested).toHaveCount(4);
  await expect(alone).toHaveCount(4);
  await expect(page.locator('.ng-outer > .z-grid-body tr.z-grid-odd:has(.ng-inner)'), 'the nested grid sits in an odd-striped row').toHaveCount(1);

  // Rows 1 and 3 are at odd positions among their siblings, the position a descendant rule on .z-grid-odd would hit.
  for (const index of [0, 1, 2, 3]) {
    await alone.nth(index).hover();
    const hoverFill = await style(alone.nth(index), 'background-color');
    await nested.nth(index).hover();
    expect(await style(nested.nth(index), 'background-color'), `hovered row ${index + 1}`).toBe(hoverFill);
  }
});
