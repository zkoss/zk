import { test, expect, type Page } from '@playwright/test';

// Guard for zul fud/css/fileupload.css (ZK-6112): the picked-file rows of the Fileupload.get dialog,
// written by zul.fud.ModalFileViewer, follow MD3 like the rest of the dialog: rows that do not double
// their borders, rounded corners, the dialog's body type for the file name, a Lucide mask icon instead
// of the raster progress2.gif / attachment.gif, the remove action at the trailing end, and a gap
// before the dialog's action buttons.
// Page: web/fileupload.zul, its "Fileupload.get(3)" button (the dialog takes up to three files)

const PAGE = '/fileupload.zul';
const TOLERANCE_PX = 1;

const file = (name: string, kb: number) =>
  ({ name, mimeType: 'text/plain', buffer: Buffer.alloc(kb * 1024, 'a') });

async function pick(page: Page, name: string) {
  await page.locator('.z-window input[type=file]').setInputFiles([file(name, 4)]);
}

// Picks one file and holds its multipart POST, so the row stays in the uploading state.
async function pickAndHold(page: Page, name: string): Promise<() => void> {
  let release!: () => void;
  const gate = new Promise<void>((r) => { release = r; });
  await page.route('**/zkau*', async (route) => {
    if ((route.request().headers()['content-type'] ?? '').includes('multipart')) await gate;
    await route.continue();
  });
  await pick(page, name);
  await expect(page.locator('.z-fileupload-size')).toBeVisible();
  return release;
}

async function pickDone(page: Page, names: string[]) {
  for (const [i, name] of names.entries()) {
    await pick(page, name);
    await expect(page.locator('.z-fileupload-file')).toHaveCount(i + 1);
    await expect(page.locator('.z-fileupload-size')).toHaveCount(0);
  }
}

test.beforeEach(async ({ page }) => {
  const resp = await page.goto(PAGE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.getByRole('button', { name: 'Fileupload.get(3)', exact: true }).click();
  await expect(page.locator('.z-window')).toBeVisible();
});

test('fileupload — picked-file rows do not double the border between them', async ({ page }) => {
  await pickDone(page, ['first.txt', 'second.txt']);
  const { gap, bottom, top } = await page.locator('.z-fileupload-file').evaluateAll((rows) => {
    const [a, b] = rows.map((r) => r.getBoundingClientRect());
    return {
      gap: b.top - a.bottom,
      bottom: parseFloat(getComputedStyle(rows[0]).borderBottomWidth),
      top: parseFloat(getComputedStyle(rows[1]).borderTopWidth),
    };
  });
  // Either the rows stand apart, or the edge they share is drawn once.
  if (gap < 4) expect(bottom + top, `rows touch with ${bottom}px + ${top}px borders`).toBeLessThanOrEqual(1);
});

test('fileupload — a picked-file row has rounded corners', async ({ page }) => {
  await pickDone(page, ['first.txt']);
  const radius = await page.locator('.z-fileupload-file').evaluate((el) => getComputedStyle(el).borderTopLeftRadius);
  expect(radius).not.toBe('0px');
});

test('fileupload — the file name uses the dialog body type size', async ({ page }) => {
  await pickDone(page, ['first.txt']);
  const body = await page.locator('.z-window-content > .z-label').evaluate((el) => getComputedStyle(el).fontSize);
  const name = await page.locator('.z-fileupload-file').evaluate((el) => getComputedStyle(el).fontSize);
  expect(name).toBe(body);
});

test('fileupload — the remove action sits at the trailing end of the row', async ({ page }) => {
  await pickDone(page, ['first.txt']);
  const { cancelLeft, cancelRight, nameRight, rowRight } = await page.locator('.z-fileupload-file').evaluate((row) => {
    const cancel = row.querySelector('.z-fileupload-cancel')!;
    const text = [...cancel.parentElement!.childNodes].find((n) => n.nodeType === Node.TEXT_NODE && n.textContent!.trim())!;
    const range = document.createRange();
    range.selectNodeContents(text);
    const cs = getComputedStyle(row);
    return {
      cancelLeft: cancel.getBoundingClientRect().left,
      cancelRight: cancel.getBoundingClientRect().right,
      nameRight: range.getBoundingClientRect().right,
      rowRight: row.getBoundingClientRect().right - parseFloat(cs.borderRightWidth) - parseFloat(cs.paddingRight),
    };
  });
  expect(cancelLeft, 'remove action comes after the file name').toBeGreaterThan(nameRight);
  expect(Math.abs(rowRight - cancelRight), `remove action ends at ${cancelRight}, row content at ${rowRight}`)
    .toBeLessThanOrEqual(TOLERANCE_PX);
});

// Uploading shows the framework spinner (.z-loading-icon's z-loading-spin); uploaded shows a mask icon.
for (const state of ['uploading', 'done'] as const) {
  test(`fileupload — the ${state} row draws its icon in CSS, not the raster gif`, async ({ page }) => {
    let release: (() => void) | undefined;
    if (state === 'uploading') release = await pickAndHold(page, 'first.txt');
    else await pickDone(page, ['first.txt']);
    const { imgWidth, mask, animation } = await page.locator('.z-fileupload-file').evaluate((row) => ({
      imgWidth: row.querySelector('.z-fileupload-icon')!.getBoundingClientRect().width,
      mask: getComputedStyle(row, '::before').maskImage,
      animation: getComputedStyle(row, '::before').animationName,
    }));
    release?.();
    expect(imgWidth, 'the progress2.gif / attachment.gif image is not shown').toBe(0);
    if (state === 'uploading') expect(animation).toBe('z-loading-spin');
    else expect(mask).toContain('url(');
  });
}

test('fileupload — the uploading size line uses the supporting text style', async ({ page }) => {
  const release = await pickAndHold(page, 'first.txt');
  const { size, sizeColor, variant } = await page.locator('.z-fileupload-size').evaluate((el) => {
    const cs = getComputedStyle(el);
    const probe = document.createElement('span');
    probe.style.color = 'var(--zk-color-on-surface-variant)';
    el.appendChild(probe);
    const variant = getComputedStyle(probe).color;
    probe.remove();
    return { size: cs.fontSize, sizeColor: cs.color, variant };
  });
  const small = await page.locator('.z-window').evaluate((el) =>
    getComputedStyle(el).getPropertyValue('--zk-typescale-body-small-size').trim());
  release();
  expect(size).toBe(small);
  expect(sizeColor).toBe(variant);
});

test('fileupload — the file list keeps a gap before the action buttons', async ({ page }) => {
  await pickDone(page, ['first.txt']);
  const listBottom = await page.locator('.z-fileupload-file').evaluate((el) => el.parentElement!.getBoundingClientRect().bottom);
  const buttonsTop = await page.locator('.z-window-content .z-button', { hasText: 'Upload' })
    .evaluate((el) => el.getBoundingClientRect().top);
  const gap = await page.locator('.z-window').evaluate((el) =>
    parseFloat(getComputedStyle(el).getPropertyValue('--zk-spacing-2')));
  expect(buttonsTop - listBottom, 'space between the last file row and the Upload button').toBeGreaterThanOrEqual(gap);
});
