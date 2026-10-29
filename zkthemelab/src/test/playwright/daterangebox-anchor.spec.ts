import { test, expect, type Page } from '@playwright/test';

// Guard for daterangebox.css (ZK-6112, ZK-6134): every month panel in the popup keeps
// .z-calendar-selected on its 1st, and calendar.css paints that class as a primary disc. When the
// 1st falls inside a range band it must paint like the band, not like a selected day.
// Fixture: web/pv/daterangebox-anchor-guard.zul

const FIXTURE = '/pv/daterangebox-anchor-guard.zul';
const POPUP = '.z-daterangebox-popup';
const DAY = 'td.z-calendar-cell:not(.z-calendar-outside)';

async function open(page: Page, box: number) {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  // the hover layer fades in; read its settled value
  await page.addStyleTag({ content: '*, *::before { transition: none !important; }' });
  await page.locator('.z-daterangebox-button').nth(box).click();
  await expect(page.locator(`${POPUP}-panels .z-calendar`).first()).toBeVisible();
}

function day(page: Page, panel: number, n: number) {
  return page.locator(`${POPUP}-panels .z-calendar`).nth(panel)
    .locator(DAY).filter({ hasText: new RegExp(`^${n}$`) });
}

// The cell's text colour and its ::before fill, which is where Marble paints day highlights.
async function paintOf(page: Page, selector: string) {
  const cell = page.locator(selector).first();
  await expect(cell).toBeVisible();
  return cell.evaluate((el) => {
    const b = getComputedStyle(el, '::before');
    const s = getComputedStyle(el);
    return { color: s.color, weight: s.fontWeight, fill: b.backgroundColor, opacity: b.opacity };
  });
}

test('the 1st inside a hover preview band paints like the band', async ({ page }) => {
  await open(page, 0);
  await day(page, 0, 20).click();
  await day(page, 1, 5).hover();
  const anchor = `${POPUP} .z-calendar-cell.z-calendar-selected.z-cell-range-preview-mid`;
  await expect(page.locator(anchor), 'precondition: the preview band covers the next panel\'s 1st')
    .toHaveCount(1);
  const band = `${POPUP} .z-cell-range-preview-mid:not(.z-calendar-selected):not(.z-calendar-today)`;
  expect(await paintOf(page, anchor)).toEqual(await paintOf(page, band));
});

test('the 1st inside a committed range paints like the band', async ({ page }) => {
  await open(page, 1);
  const anchor = `${POPUP} .z-calendar-cell.z-calendar-selected.z-cell-range-mid`;
  await expect(page.locator(anchor), 'precondition: the 25 Jan to 5 Feb range covers 1 Feb')
    .toHaveCount(1);
  const band = `${POPUP} .z-cell-range-mid:not(.z-calendar-selected):not(.z-calendar-today)`;
  expect(await paintOf(page, anchor)).toEqual(await paintOf(page, band));
});
