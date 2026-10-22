import { test, expect, Page } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import { AGGREGATE_PAGES } from './aggregate-pages';
import { expectVariantSectionShots, VARIANT_PAGES } from './variant-gallery';

// Compact density guard (doc/marble-test-gaps-round2-plan.md section 10, doc/spec/data-dense-mode.md).
//
//   1. compact-gallery: sectioned screenshots of the representative pages with <html data-density="compact">.
//   2. compact-height: on every non-aggregate page, each rendered control of a ladder tier has the height of
//      its semantic alias (tokens/_sizing.css), read at runtime from the computed custom property.
// The tests are named after the page (`<page>`), the form select-tests.mjs maps to a page.

const WEB_DIR = path.resolve(__dirname, '../../main/webapp/web');
const pages = fs.readdirSync(WEB_DIR)
  .filter(f => f.endsWith('.zul'))
  .map(f => f.replace(/\.zul$/, ''))
  .filter(name => !AGGREGATE_PAGES.includes(name))
  .sort();

const applyCompact = () => { document.documentElement.dataset.density = 'compact'; };

test.describe('compact-gallery', () => {
  for (const comp of VARIANT_PAGES) {
    test(comp, async ({ page }) => {
      await expectVariantSectionShots(page, comp, 'compact', applyCompact);
    });
  }
});

// ---------------------------------------------------------------------------------------------------------
// Height check
// ---------------------------------------------------------------------------------------------------------

interface Tier {
  name: string;
  /** The custom property whose runtime value is the expected height (box tiers). */
  alias: string;
  selector: string;
  /** The tab overlaps the tab-strip divider by its 1px negative bottom margin (tabbox.css .z-tab), so the alias is its height less that overlap. */
  overlapsDivider?: boolean;
  /** Pixels on top of the alias that the element's height includes (a border of the neighbour it copies its height from). */
  extraHeight?: number;
}

// class -> alias mapping, from the consumers of each alias in the component CSS (grep `var(--zk-<alias>)`):
//   input        --zk-input-height          the field boxes; the CSS reads --zk-control-height (32px literal in compact)
//                                           for the outer box and --zk-input-height only for the inner field (datebox.css:54)
//   button       --zk-button-height         button.css:18, :113; sm/lg/icon below
//   toolbar      --zk-toolbar-height        toolbar.css:38, :82 (horizontal; vertical has no fixed height)
//   menubar      --zk-menubar-height        menu.css:60, :81, :128, :189
//   tab          --zk-tab-height            tabbox.css:58, :70, :86 (top/bottom tabs; left/right/accordion are not measured)
//   groupbox-header  --zk-groupbox-header-height   groupbox.css:29 (min-height) and the padding that follows it (groupbox.css:26)
//   window-/panel-header: padding-driven, see PADDED_HEADER_TIERS
//   paging       --zk-paging-control-size   paging.css:41, :42
// Box heights are compared as `height` or `height - own vertical borders`: a bar whose child fills the alias
// and whose own border sits outside it (toolbar 40 + 1px) is on the alias.
const BOX_TIERS: Tier[] = [
  { name: 'input', alias: '--zk-input-height',
    selector: 'input.z-textbox, input.z-intbox, input.z-longbox, input.z-doublebox, input.z-decimalbox, '
      + '.z-combobox, .z-bandbox, .z-datebox, .z-timebox, .z-spinner, .z-doublespinner, .z-selectbox, '
      + '.z-searchbox, .z-chosenbox, .z-daterangebox' },
  { name: 'button', alias: '--zk-button-height',
    selector: '.z-button:not(.z-button-sm):not(.z-button-lg):not(.z-button-icon):not(.z-button-fab):not(.z-button-xs)' },
  { name: 'button-sm', alias: '--zk-button-height-sm',
    selector: '.z-button-sm.z-button:not(.z-button-icon):not(.z-button-fab)' },
  { name: 'button-lg', alias: '--zk-button-height-lg',
    selector: '.z-button-lg.z-button:not(.z-button-icon):not(.z-button-fab)' },
  { name: 'icon-button', alias: '--zk-icon-button-size',
    selector: '.z-button-icon.z-button:not(.z-button-sm):not(.z-button-lg)' },
  { name: 'fab', alias: '--zk-fab-size',
    selector: '.z-button-fab.z-button:not(.z-button-sm):not(.z-button-xs)' },
  { name: 'toolbar', alias: '--zk-toolbar-height', selector: '.z-toolbar:not(.z-toolbar-vertical):not(.z-toolbar-tabs)' },
  // A toolbar inside a tabbox gets the height of the tab strip as an inline style (Tabs.ts:430-437, tb.style.height =
  // tabs.offsetHeight): the tab alias plus the strip's 1px bottom border (tabbox.css .z-tabs).
  { name: 'toolbar-tabs', alias: '--zk-tab-height', extraHeight: 1, selector: '.z-toolbar-tabs' },
  { name: 'menubar', alias: '--zk-menubar-height', selector: '.z-menubar-horizontal' },
  { name: 'tab', alias: '--zk-tab-height', overlapsDivider: true,
    selector: '.z-tabbox-top > .z-tabs .z-tab, .z-tabbox-bottom > .z-tabs .z-tab' },
  { name: 'groupbox-header', alias: '--zk-groupbox-header-height', selector: '.z-groupbox:not(.z-groupbox-3d) .z-groupbox-header' },
  // The 3D mold header is panel-like but denser: it binds the md rung (groupbox.css .z-groupbox-3d .z-groupbox-header), 40 / 32.
  { name: 'groupbox-3d-header', alias: '--zk-control-height-md', selector: '.z-groupbox-3d .z-groupbox-header' },
  { name: 'paging-button', alias: '--zk-paging-control-size', selector: '.z-paging-button' },
];

// Window and panel headers are padding-driven (density.md "Padding-driven headers"): the alias is their min-height and
// the 24px title line plus `--zk-header-padding-y` above and below is what sets the height (56px panel, 57px window with
// its border in comfortable; the floor, 40 / 48, in compact). So the rule is not "height equals the alias" but "height is
// at least the alias and the vertical padding is the density token", which fails for a header that stops following the density.
interface PaddedHeaderTier { name: string; alias: string; paddingToken: string; selector: string }
const PADDED_HEADER_TIERS: PaddedHeaderTier[] = [
  { name: 'window-header', alias: '--zk-window-header-height', paddingToken: '--zk-header-padding-y', selector: '.z-window-header' },
  { name: 'panel-header', alias: '--zk-panel-header-height', paddingToken: '--zk-header-padding-y', selector: '.z-panel-header' },
];

// Data rows have no ladder alias: their height is the cell padding plus the text line (density.md, layer 3),
// 52.5px in the default tier against the 40px md rung. Expected = vertical cell padding read from the padding
// token + the cell line height; a row may add its 1px border (and a half pixel of border collapse).
interface RowTier { name: string; paddingToken: string; selector: string }
const ROW_TIERS: RowTier[] = [
  { name: 'grid-row', paddingToken: '--zk-grid-cell-padding', selector: 'tr.z-row:not(.z-detail-faker)' },
  { name: 'listitem', paddingToken: '--zk-listbox-cell-padding', selector: 'tr.z-listitem' },
  { name: 'treerow', paddingToken: '--zk-tree-cell-padding', selector: 'tr.z-treerow' },
];
const ROW_BORDER_ALLOWANCE = 1;
const HEIGHT_EPSILON = 0.01;

interface Deviation {
  tier: string;
  element: string;
  section: string;
  height: number;
  expected: number;
}

/** Runs in the page. Returns the controls whose height is not the expected one, and how many were measured. */
function measureTiers([boxTiers, headerTiers, rowTiers, epsilon, borderAllowance]: [Tier[], PaddedHeaderTier[], RowTier[], number, number]) {
  const probe = document.createElement('div');
  probe.style.cssText = 'position:absolute;visibility:hidden';
  document.body.appendChild(probe);
  const px = (property: string, value: string) => {
    probe.style.cssText = `position:absolute;visibility:hidden;${property}:var(${value})`;
    return parseFloat(getComputedStyle(probe)[property as 'height']);
  };
  const sectionOf = (el: Element) => {
    const root = document.querySelector('.z-p-8');
    const section = root ? Array.from(root.children).find(c => c.contains(el)) : null;
    const heading = section?.querySelector('.z-text-uppercase.z-border-bottom');
    return (heading?.textContent ?? 'title').trim();
  };
  const signature = (el: Element) => `${el.tagName.toLowerCase()}.${Array.from(el.classList).join('.')}`;
  const shown = (el: Element) => {
    const box = el.getBoundingClientRect();
    return box.height > 0 && box.width > 0 && getComputedStyle(el).visibility !== 'hidden';
  };
  const deviations: Deviation[] = [];
  const contentRows: Deviation[] = [];
  const measured: Record<string, number> = {};

  for (const tier of boxTiers) {
    const expected = px('height', tier.alias) + (tier.extraHeight ?? 0);
    for (const el of Array.from(document.querySelectorAll(tier.selector)).filter(shown)) {
      measured[tier.name] = (measured[tier.name] ?? 0) + 1;
      const style = getComputedStyle(el);
      const height = el.getBoundingClientRect().height;
      const borders = parseFloat(style.borderTopWidth) + parseFloat(style.borderBottomWidth);
      const overlap = tier.overlapsDivider ? parseFloat(style.marginTop) + parseFloat(style.marginBottom) : 0;
      const onAlias = Math.abs(height - expected) < epsilon || Math.abs(height - borders - expected) < epsilon
        || Math.abs(height + overlap - expected) < epsilon;
      if (!onAlias) deviations.push({ tier: tier.name, element: signature(el), section: sectionOf(el), height, expected });
    }
  }

  for (const tier of headerTiers) {
    const expected = px('height', tier.alias);
    const paddingY = px('height', tier.paddingToken);
    for (const el of Array.from(document.querySelectorAll(tier.selector)).filter(shown)) {
      measured[tier.name] = (measured[tier.name] ?? 0) + 1;
      const style = getComputedStyle(el);
      const height = el.getBoundingClientRect().height;
      const onRule = height >= expected - epsilon
        && Math.abs(parseFloat(style.paddingTop) - paddingY) < epsilon
        && Math.abs(parseFloat(style.paddingBottom) - paddingY) < epsilon;
      if (!onRule) deviations.push({ tier: tier.name, element: signature(el), section: sectionOf(el), height, expected });
    }
  }

  for (const tier of rowTiers) {
    const paddingY = (() => {
      probe.style.cssText = `position:absolute;visibility:hidden;padding:var(${tier.paddingToken})`;
      const style = getComputedStyle(probe);
      return parseFloat(style.paddingTop) + parseFloat(style.paddingBottom);
    })();
    for (const row of Array.from(document.querySelectorAll(tier.selector)).filter(shown)) {
      const cell = row.querySelector('td');
      if (!cell) continue;
      measured[tier.name] = (measured[tier.name] ?? 0) + 1;
      const lineHeight = parseFloat(getComputedStyle(cell).lineHeight);
      const expected = paddingY + lineHeight;
      const height = row.getBoundingClientRect().height;
      // A cell whose content is taller than one text line makes a taller row by itself (an image, a wrapped text, a
      // nested component); that is the content's height, not the density. The cell padding is on the inner
      // `div.z-*-content` (grid.css:299-311, listbox.css:312-322, tree.css:220-231; the td itself has none), so the
      // content is that div's scroll height minus its own padding.
      const contentTaller = Array.from(row.querySelectorAll(':scope > td')).some((td) => {
        const box = td.querySelector(':scope > div') ?? td;
        const style = getComputedStyle(box);
        const inner = box.scrollHeight - parseFloat(style.paddingTop) - parseFloat(style.paddingBottom);
        return inner > lineHeight + 1;
      });
      const onRule = height >= expected - epsilon && height <= expected + borderAllowance + epsilon;
      const deviation = { tier: tier.name, element: signature(row), section: sectionOf(row), height, expected };
      if (!onRule) (contentTaller ? contentRows : deviations).push(deviation);
    }
  }
  probe.remove();
  return { deviations, contentRows, measured };
}

async function waitForCompactLayout(page: Page): Promise<void> {
  await page.evaluate(() => document.fonts.ready.then(() => true));
  await page.evaluate(applyCompact);
  // ZK recomputes widget sizes on a window resize (section-shots.ts); the resize makes JS-sized widgets follow the density.
  const size = page.viewportSize() ?? { width: 1280, height: 720 };
  await page.setViewportSize({ width: size.width + 1, height: size.height });
  await page.setViewportSize(size);
  await page.evaluate(() => new Promise<void>(resolve =>
    setTimeout(() => requestAnimationFrame(() => requestAnimationFrame(() => resolve())), 200)));
}

// Heights that differ from the alias in either density, listed so the check is exact.
// Key: `<comfortable|compact>|<page>|<tier>|<section>|<height>`.
// EXCEPTIONS are not a density defect, each with the reason. KNOWN_DEFECTS are Marble CSS that does not follow the
// density; they pin today's value (a changed height fails, and a fixed one fails as stale, so the entry gets removed).
// Nothing is excluded without a line here.
interface Listed { reason: string; keys: string[] }
const EXCEPTIONS: Listed[] = [
  {
    reason: 'orient=vertical button: icon above the label, stacked content is taller than the alias (its min-height is --zk-control-height-lg, button.css:331)',
    keys: [
      'comfortable|button|button|States|48',
      'compact|button|button|States|46.25',
    ],
  },
  {
    reason: 'the caption holds a 30px image next to the text (content-driven): the groupbox header is the image plus its padding',
    keys: [
      'comfortable|caption|groupbox-header|Caption in Groupbox|60',
      'compact|caption|groupbox-header|Caption in Groupbox|46',
    ],
  },
  {
    reason: 'the caption holds a button next to the text (content-driven): the groupbox header is the button plus its padding',
    keys: [
      'comfortable|caption|groupbox-header|Caption in Non-Closable Groupbox|61',
      'compact|caption|groupbox-header|Caption in Non-Closable Groupbox|47',
    ],
  },
  {
    reason: 'two wrapped rows of 28px chips; content-driven',
    keys: [
      'comfortable|chosenbox|input|States|70',
      'compact|chosenbox|input|States|64',
    ],
  },
  {
    reason: 'the page sets style="height:60px" on the button',
    keys: [
      'comfortable|hlayout|button|Vertical Alignment (valign)|60',
      'compact|hlayout|button|Vertical Alignment (valign)|60',
    ],
  },
  {
    reason: 'the button fills the height of the input group beside it (inputgroup.css:48 min-height --zk-control-height)',
    keys: [
      'comfortable|inputgroup|button|Examples|40',
      'compact|inputgroup|button|Examples|32',
    ],
  },
  {
    reason: 'responsive grid (responsiveColumns): the cells of a row are stacked, so the row is the sum of its cells',
    keys: [
      'comfortable|responsive-grid|grid-row|Basic stacking (narrow container)|226',
      'comfortable|responsive-grid|grid-row|Paging + footer|226',
      'comfortable|responsive-grid|grid-row|Per-column hide (responsiveVisible="false")|151',
      'comfortable|responsive-grid|grid-row|responsiveColumns="sm-1 md-2 lg-none"|226',
      'compact|responsive-grid|grid-row|Basic stacking (narrow container)|166',
      'compact|responsive-grid|grid-row|Paging + footer|166',
      'compact|responsive-grid|grid-row|Per-column hide (responsiveVisible="false")|111',
      'compact|responsive-grid|grid-row|responsiveColumns="sm-1 md-2 lg-none"|166',
    ],
  },
  {
    reason: 'the page sets the skeleton box size inline (style="height:36px" / "height:32px")',
    keys: [
      'compact|skeleton|button|Loaded versus skeleton|32',
      'compact|skeleton|button|Ordinary element roots|36',
    ],
  },
  {
    reason: 'a tab whose caption carries an image (content-driven: 66px comfortable, 52px compact); the toolbar-tabs box follows it through its inline height',
    keys: [
      'comfortable|tabbox-misc|tab|Horizontal with Caption in Tab|66',
      'comfortable|tabbox-misc|toolbar-tabs|Horizontal with Caption in Tab|66',
      'compact|tabbox-misc|tab|Horizontal with Caption in Tab|52',
      'compact|tabbox-misc|toolbar-tabs|Horizontal with Caption in Tab|52',
    ],
  },
];
const KNOWN_DEFECTS: Listed[] = [];
const listedKeys = (listed: Listed[]) => listed.flatMap(entry => entry.keys);

type Density = 'comfortable' | 'compact';
const DENSITIES: Density[] = ['comfortable', 'compact'];
const deviationKey = (density: Density, page: string, d: Deviation) => `${density}|${page}|${d.tier}|${d.section}|${d.height}`;

/** Measures the page in the default density (the page as loaded), then in compact. */
async function measureBothDensities(page: Page): Promise<Record<Density, Awaited<ReturnType<typeof measureOnce>>>> {
  await page.evaluate(() => document.fonts.ready.then(() => true));
  const comfortable = await measureOnce(page);
  await waitForCompactLayout(page);
  const compact = await measureOnce(page);
  return { comfortable, compact };
}
const measureOnce = (page: Page) =>
  page.evaluate(measureTiers, [BOX_TIERS, PADDED_HEADER_TIERS, ROW_TIERS, HEIGHT_EPSILON, ROW_BORDER_ALLOWANCE]);

test.describe('compact-height', () => {
  for (const comp of pages) {
    test(comp, async ({ page }, testInfo) => {
      await page.goto(`/${comp}.zul`, { waitUntil: 'networkidle' });
      const results = await measureBothDensities(page);
      testInfo.annotations.push({ type: 'measured', description: JSON.stringify(results.compact.measured) });
      if (process.env.HEIGHT_REPORT) {
        for (const density of DENSITIES) {
          for (const d of results[density].deviations) console.log(`HEIGHT ${JSON.stringify({ density, page: comp, ...d })}`);
          for (const d of results[density].contentRows) console.log(`CONTENT ${JSON.stringify({ density, page: comp, ...d })}`);
        }
        return;
      }
      const known = new Set([...listedKeys(EXCEPTIONS), ...listedKeys(KNOWN_DEFECTS)]);
      const unexpected = DENSITIES.flatMap(density => results[density].deviations
        .filter(d => !known.has(deviationKey(density, comp, d)))
        .map(d => `${deviationKey(density, comp, d)} ${d.element}: ${d.height}px, alias ${d.expected}px`));
      expect(unexpected, `${comp}.zul: controls that are not on their alias height (comfortable and compact)`).toEqual([]);
      const present = new Set(DENSITIES.flatMap(density => results[density].deviations.map(d => deviationKey(density, comp, d))));
      const stale = [...known].filter(k => DENSITIES.some(density => k.startsWith(`${density}|${comp}|`)) && !present.has(k));
      expect(stale, `${comp}.zul: listed deviations that no longer occur; remove them from EXCEPTIONS / KNOWN_DEFECTS`).toEqual([]);
    });
  }
});

// No preview page renders an icon button or a FAB (`sclass="z-button-icon"` / `z-button-fab` appear in no .zul), so
// these two tiers get their controls from the test: plain buttons with the theme's classes, in the button page.
test.describe('compact-height-synthetic', () => {
  test('button: icon button and FAB without a preview instance', async ({ page }) => {
    await page.goto('/button.zul', { waitUntil: 'networkidle' });
    await page.evaluate(() => {
      for (const classes of ['z-button z-button-icon', 'z-button z-button-fab']) {
        const button = document.createElement('button');
        button.type = 'button';
        button.className = classes;
        button.innerHTML = '<i class="z-icon-settings" aria-hidden="true"></i>';
        document.querySelector('.z-p-8')!.appendChild(button);
      }
    });
    const results = await measureBothDensities(page);
    for (const density of DENSITIES) {
      const { deviations, measured } = results[density];
      expect(measured['icon-button'], `${density}: the injected icon button was measured`).toBe(1);
      expect(measured['fab'], `${density}: the injected FAB was measured`).toBe(1);
      const own = deviations.filter(d => d.tier === 'icon-button' || d.tier === 'fab');
      expect(own.map(d => `${density} ${d.tier} ${d.element}: ${d.height}px, alias ${d.expected}px`)).toEqual([]);
    }
  });
});

// Toolbar overflow button under compact: with overflowPopup="true" the overflow button stays inside the toolbar.
// Toolbar.ts:243 budgets `jq(toolbar).width() - jq(overflow button).width()` and sums the children with
// `outerWidth(true)` (:255), which count the toolbar's content box and the children's margins but not the padding and
// flex gap of `.z-toolbar-content`. In overflow mode the inset therefore sits on the toolbar and the gap is a margin on
// each item (toolbar.css .z-toolbar-overflowpopup); before that fix the button landed at x 344-374 against a toolbar
// right edge of 332 in compact (doc/marble-compact-brand-batch3-verification.md F2-a).
test.describe('compact-geometry', () => {
  test('toolbar: overflow button stays inside the toolbar', async ({ page }) => {
    await page.goto('/toolbar.zul', { waitUntil: 'networkidle' });
    await waitForCompactLayout(page);
    const { toolbar, button } = await page.evaluate(() => {
      const bar = document.querySelector('.z-toolbar-overflowpopup')!;
      const edge = (el: Element) => { const r = el.getBoundingClientRect(); return { left: r.left, right: r.right }; };
      return { toolbar: edge(bar), button: edge(bar.querySelector('.z-toolbar-overflowpopup-button')!) };
    });
    expect(button.right, 'overflow button right edge against the toolbar right edge').toBeLessThanOrEqual(toolbar.right + 0.5);
    expect(button.left).toBeGreaterThanOrEqual(toolbar.left);
  });
});
