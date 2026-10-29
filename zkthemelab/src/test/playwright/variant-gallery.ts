import { expect, Page } from '@playwright/test';
import { expectSectionShots } from './section-shots';

// Sectioned screenshots of the representative pages in a theme variant (compact density, brand colour).
// Same shots as gallery-scan.spec.ts (section-shots.ts), with the variant name in the file name:
// `<page>-<variant>-<NN>-<slug>.png`.

// The page list. `textbox` stands for the aggregate
// `inputs.zul`, which a component CSS change never selects (D12).
export const VARIANT_PAGES = [
  'textbox', 'button', 'grid', 'listbox', 'tree', 'tabbox', 'toolbar', 'menubar',
  'window', 'panel', 'groupbox', 'paging', 'navbar', 'datebox', 'combobox',
];

// Absolute per-section pixel budget, inherited from gallery-scan.spec.ts. The noise measured over three separate
// processes was 0 px in every section, so 20 is not derived
// from it.
export const VARIANT_SECTION_MAX_DIFF_PIXELS = 20;

/** Loads `/<page>.zul`, settles it like gallery-scan.spec.ts, applies the variant and shoots every section. */
export async function expectVariantSectionShots(
  page: Page, comp: string, variant: string, applyVariant: () => void,
): Promise<void> {
  await page.goto(`/${comp}.zul`, { waitUntil: 'networkidle' });
  await page.evaluate(() => document.fonts.ready.then(() => true));
  await page.addStyleTag({ content: '*{transition-duration:0s !important}' });
  await page.waitForFunction(() => Array.from(document.querySelectorAll('.z-progressmeter')).every((el) => {
    const v = el.getAttribute('aria-valuenow');
    const fill = el.querySelector('.z-progressmeter-image') as HTMLElement | null;
    return v === null || !fill || fill.style.width === `${v}%`;
  }));
  await expect(page.locator('.z-p-8').first(), `${comp}.zul has no .z-p-8 wrapper`).toBeVisible();
  await page.evaluate(applyVariant);
  // Two frames and the fonts: the variant changes metrics, and the sectioned shots measure the layout next.
  await page.evaluate(() => new Promise<void>(resolve =>
    requestAnimationFrame(() => requestAnimationFrame(() => resolve()))));
  await page.evaluate(() => document.fonts.ready.then(() => true));
  await expectSectionShots(page, `${comp}-${variant}`, VARIANT_SECTION_MAX_DIFF_PIXELS, {
    animations: 'disabled',
    // The page title row has no density or brand: its shot is byte-identical to the stock gallery's.
    skipTitle: /^title$/,
  });
}
