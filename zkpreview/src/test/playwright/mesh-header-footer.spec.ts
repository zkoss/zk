import { test, expect, Page } from '@playwright/test';

// Guard for the Grid, Listbox and Tree header and footer CSS (ZK-6112, D11-D13):
//  - the sort arrow is 12x12 in all three (grid.css gave Grid 14px through `.z-column [class*="z-icon-"]`);
//  - the label box of a narrow sorted column stops short of the arrow, with and without a column-menu
//    button and with an image in the header (Grid and Listbox clip with an ellipsis; a Tree header with an
//    image clips without one, the accepted trade-off of 5830d574ee because it keeps the flex centring);
//  - a narrow Listbox header stays on one line, as tall as the Grid header;
//  - a Listbox header holding only the select-all box in a 40px column paints no ellipsis and keeps the box
//    inside the header cell;
//  - a Tree header holding an image, an icon or the select-all box keeps the label centred on it and the
//    header at the height the old flex layout gave it;
//  - under the tablet project (zkmax `tablet/_mesh.css` sizes `.z-listheader-checkable` to 24px, which the
//    inline-block select-all span would pick up): the label of a select-all header clears the box, the header is
//    not taller than a plain Listbox header beyond the known offset, and a centred box lines up with the rows;
//  - footer text starts where the body text of the same column starts (`border-spacing: 2px` of the UA
//    table shifted Grid and Listbox footers right).
// Fixture: web/pv/mesh-header-footer-guard.zul

const FIXTURE = '/pv/mesh-header-footer-guard.zul';
const TOLERANCE_PX = 1;
const ARROW_SIZE_PX = 12;
const MIN_LABEL_ARROW_GAP_PX = 2;
const SORTED_COLUMN = 2;

interface Mesh {
  root: string;
  header: string;
  sortIcon: string;
  headerContent: string;
  bodyCell: string;
  footerCell?: string;
  // Tree indents its first body column for the open icon, so the footer cannot line up there.
  footerColumns?: number[];
}

const GRID: Mesh = {
  root: '.mhf-grid', header: 'th.z-column', sortIcon: '.z-column-sorticon i',
  headerContent: '.z-column-content', bodyCell: 'tr.z-row > td', footerCell: 'td.z-footer',
};
const GRID_MENU: Mesh = { ...GRID, root: '.mhf-grid-menu', footerCell: undefined };
const LISTBOX: Mesh = {
  root: '.mhf-listbox', header: 'th.z-listheader', sortIcon: '.z-listheader-sorticon i',
  headerContent: '.z-listheader-content', bodyCell: 'tr.z-listitem > td', footerCell: 'td.z-listfooter',
};
const LISTBOX_MENU: Mesh = { ...LISTBOX, root: '.mhf-listbox-menu', footerCell: undefined };
const GRID_IMAGE: Mesh = { ...GRID, root: '.mhf-grid-image', footerCell: undefined };
const GRID_IMAGE_MENU: Mesh = { ...GRID, root: '.mhf-grid-image-menu', footerCell: undefined };
const LISTBOX_IMAGE: Mesh = { ...LISTBOX, root: '.mhf-listbox-image', footerCell: undefined };
const LISTBOX_IMAGE_MENU: Mesh = { ...LISTBOX, root: '.mhf-listbox-image-menu', footerCell: undefined };
const TREE: Mesh = {
  root: '.mhf-tree', header: 'th.z-treecol', sortIcon: '.z-treecol-sorticon i',
  headerContent: '.z-treecol-content', bodyCell: 'tr.z-treerow > td', footerCell: 'td.z-treefooter',
  footerColumns: [2, 3],
};
const TREE_IMAGE: Mesh = { ...TREE, root: '.mhf-tree-image', footerCell: undefined };

interface TreeHeaderCase {
  name: string;
  tree: string;
  column: number;
  media: string;
  // Column of the same tree that holds a plain label, the reference header height.
  plainColumn: number;
}

const TREE_HEADER_CASES: TreeHeaderCase[] = [
  { name: '16px image', tree: '.mhf-tree-media', column: 2, media: 'img', plainColumn: 1 },
  { name: '24px image', tree: '.mhf-tree-media', column: 3, media: 'img', plainColumn: 1 },
  { name: '30px image', tree: '.mhf-tree-media', column: 4, media: 'img', plainColumn: 1 },
  { name: 'iconSclass', tree: '.mhf-tree-media', column: 5, media: 'i[class*="z-icon-"]:not(.z-treecol-sorticon *)', plainColumn: 1 },
  { name: 'select-all box', tree: '.mhf-tree-checkable', column: 1, media: '.z-treecol-checkable', plainColumn: 2 },
];

async function open(page: Page) {
  const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.evaluate(() => document.fonts.ready.then(() => true));
}

// Left edge of the first text node's rendered text, which is what the eye compares.
function textLeft(page: Page, cell: string): Promise<number> {
  return page.locator(cell).first().evaluate((el) => {
    const text = document.createTreeWalker(el, NodeFilter.SHOW_TEXT, {
      acceptNode: (node) => (node.textContent?.trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP),
    }).nextNode() as Text;
    const range = document.createRange();
    range.selectNodeContents(text);
    return range.getBoundingClientRect().left;
  });
}

const sortedHeader = (mesh: Mesh) => `${mesh.root} ${mesh.header}:nth-child(${SORTED_COLUMN})`;

test.describe('mesh header and footer', () => {
  for (const mesh of [GRID, LISTBOX, TREE]) {
    test(`${mesh.root}: footer text starts where the body text starts`, async ({ page }) => {
      await open(page);
      for (const column of mesh.footerColumns ?? [1, 2, 3]) {
        const body = await textLeft(page, `${mesh.root} ${mesh.bodyCell}:nth-child(${column})`);
        const footer = await textLeft(page, `${mesh.root} ${mesh.footerCell}:nth-child(${column})`);
        expect(Math.abs(footer - body), `column ${column}: body x ${body}, footer x ${footer}`)
          .toBeLessThanOrEqual(TOLERANCE_PX);
      }
    });
  }

  for (const mesh of [GRID, LISTBOX, TREE]) {
    test(`${mesh.root}: the sort arrow is ${ARROW_SIZE_PX}x${ARROW_SIZE_PX}`, async ({ page }) => {
      await open(page);
      const size = await page.locator(`${sortedHeader(mesh)} ${mesh.sortIcon}`).evaluate((icon) => {
        const style = getComputedStyle(icon, '::before');
        return { width: parseFloat(style.width), height: parseFloat(style.height) };
      });
      expect(size).toEqual({ width: ARROW_SIZE_PX, height: ARROW_SIZE_PX });
    });
  }

  for (const mesh of [GRID, GRID_MENU, LISTBOX, LISTBOX_MENU, TREE,
    GRID_IMAGE, GRID_IMAGE_MENU, LISTBOX_IMAGE, LISTBOX_IMAGE_MENU, TREE_IMAGE]) {
    test(`${mesh.root}: the label box of a narrow sorted column ends before the arrow`, async ({ page }) => {
      await open(page);
      const header = sortedHeader(mesh);
      const arrowLeft = await page.locator(`${header} ${mesh.sortIcon}`)
        .evaluate((icon) => icon.getBoundingClientRect().left);
      // The box, not the text: it is what the header's padding-right reserves, so the result does not
      // depend on how long the label is.
      const labelBoxRight = await page.locator(`${header} ${mesh.headerContent}`)
        .evaluate((content) => content.getBoundingClientRect().right);
      expect(arrowLeft - labelBoxRight, `label box right ${labelBoxRight}, arrow left ${arrowLeft}`)
        .toBeGreaterThanOrEqual(MIN_LABEL_ARROW_GAP_PX);
    });
  }

  for (const [listbox, grid] of [[LISTBOX, GRID], [LISTBOX_MENU, GRID_MENU]]) {
    test(`${listbox.root}: the narrow sorted header stays on one line, as tall as the ${grid.root} header`, async ({ page }) => {
      await open(page);
      const headerHeight = (mesh: Mesh) => page.locator(sortedHeader(mesh))
        .evaluate((header) => header.getBoundingClientRect().height);
      expect(await headerHeight(listbox)).toBeCloseTo(await headerHeight(grid), 0);
    });
  }

  // The columns of a tree share one header row, so a column is measured with its siblings hidden.
  async function measureAlone(page: Page, tree: string, column: number, media: string) {
    return page.locator(`${tree} th.z-treecol:nth-child(${column})`).evaluate((header, mediaSelector) => {
      const siblings = Array.from(header.parentElement!.children).filter((th) => th !== header) as HTMLElement[];
      siblings.forEach((th) => { th.style.display = 'none'; });
      const content = header.querySelector('.z-treecol-content')!;
      const labelText = document.createTreeWalker(content, NodeFilter.SHOW_TEXT, {
        acceptNode: (node) => (node.textContent?.trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP),
      }).nextNode() as Text;
      const label = document.createRange();
      label.setStart(labelText, labelText.data.search(/\S/));
      label.setEnd(labelText, labelText.length);
      const centre = (rect: DOMRect) => (rect.top + rect.bottom) / 2;
      const mediaRect = content.querySelector(mediaSelector)?.getBoundingClientRect();
      const result = {
        headerHeight: header.getBoundingClientRect().height,
        contentHeight: content.getBoundingClientRect().height,
        labelCentre: centre(label.getBoundingClientRect()),
        mediaCentre: mediaRect && centre(mediaRect),
        mediaHeight: mediaRect?.height ?? 0,
      };
      siblings.forEach((th) => { th.style.display = ''; });
      return result;
    }, media);
  }

  for (const { name, tree, column, media, plainColumn } of TREE_HEADER_CASES) {
    test(`${tree}: the label is centred on the ${name} and the header keeps the height the image needs`, async ({ page }) => {
      await open(page);
      const header = await measureAlone(page, tree, column, media);
      const plain = await measureAlone(page, tree, plainColumn, 'none');
      expect(header.mediaCentre, 'the header holds the media').not.toBeNull();
      expect(Math.abs(header.labelCentre - header.mediaCentre!),
        `label centre ${header.labelCentre}, media centre ${header.mediaCentre}`).toBeLessThanOrEqual(TOLERANCE_PX);
      // A plain header is as tall as its label; media taller than that stretches it by the difference.
      const expectedHeight = plain.headerHeight + Math.max(0, header.mediaHeight - plain.contentHeight);
      expect(header.headerHeight, `header ${header.headerHeight}, plain ${plain.headerHeight}, media ${header.mediaHeight}`)
        .toBeCloseTo(expectedHeight, 0);
    });
  }

  test('.mhf-listbox-checkable: a select-all-only 40px header paints no ellipsis and holds the whole box', async ({ page }) => {
    await open(page);
    const header = page.locator('.mhf-listbox-checkable th.z-listheader:nth-child(1)');
    const measured = await header.evaluate((th) => {
      const content = th.querySelector('.z-listheader-content')!;
      const icon = th.querySelector('.z-listheader-icon')!.getBoundingClientRect();
      const cell = th.getBoundingClientRect();
      return {
        overflows: content.scrollWidth > content.clientWidth,
        textOverflow: getComputedStyle(content).textOverflow,
        scrollWidth: content.scrollWidth,
        clientWidth: content.clientWidth,
        iconInsideCell: icon.left >= cell.left && icon.right <= cell.right,
        iconLeft: icon.left, iconRight: icon.right, cellLeft: cell.left, cellRight: cell.right,
      };
    });
    // An ellipsis only paints when the content overflows.
    expect(measured.overflows && measured.textOverflow === 'ellipsis',
      `scrollWidth ${measured.scrollWidth} > clientWidth ${measured.clientWidth} with text-overflow ${measured.textOverflow}`)
      .toBe(false);
    expect(measured.iconInsideCell,
      `icon ${measured.iconLeft}-${measured.iconRight}, cell ${measured.cellLeft}-${measured.cellRight}`).toBe(true);
  });

  // Tagged @tablet: the `mesh-header-footer` project skips it, `mesh-header-footer-tablet` runs only it.
  test.describe('select-all header on a tablet @tablet', () => {
    // Listbox headers are known to be taller than Grid and plain Listbox headers by 1.17px (desktop) and
    // 1.67px (tablet); that is a separate issue, this guard only stops it from growing.
    const MAX_HEADER_HEIGHT_OFFSET_PX = 2;

    test('.mhf-listbox-select-all-label: the label starts at or after the right edge of the box', async ({ page }) => {
      await open(page);
      const header = page.locator('.mhf-listbox-select-all-label th.z-listheader:nth-child(1)');
      const iconRight = await header.locator('.z-listheader-icon').evaluate((icon) => icon.getBoundingClientRect().right);
      const labelLeft = await textLeft(page, '.mhf-listbox-select-all-label th.z-listheader:nth-child(1) .z-listheader-content');
      expect(labelLeft, `label left ${labelLeft}, box right ${iconRight}`).toBeGreaterThanOrEqual(iconRight);
    });

    test('.mhf-listbox-select-all-label: the select-all header is not taller than a plain Listbox header', async ({ page }) => {
      await open(page);
      const headerHeight = (root: string) => page.locator(`${root} th.z-listheader:nth-child(1)`)
        .evaluate((th) => th.getBoundingClientRect().height);
      const selectAll = await headerHeight('.mhf-listbox-select-all-label');
      const plain = await headerHeight('.mhf-listbox-plain');
      expect(selectAll - plain, `select-all ${selectAll}, plain ${plain}`).toBeLessThanOrEqual(MAX_HEADER_HEIGHT_OFFSET_PX);
    });

    test('.mhf-listbox-select-all-centre: the centred box lines up with the row checkbox', async ({ page }) => {
      await open(page);
      const iconLeft = (selector: string) => page.locator(selector).first()
        .evaluate((icon) => icon.getBoundingClientRect().left);
      const header = await iconLeft('.mhf-listbox-select-all-centre th.z-listheader .z-listheader-icon');
      const row = await iconLeft('.mhf-listbox-select-all-centre tr.z-listitem .z-listitem-icon');
      expect(Math.abs(header - row), `header box x ${header}, row box x ${row}`).toBeLessThanOrEqual(TOLERANCE_PX);
    });
  });
});
