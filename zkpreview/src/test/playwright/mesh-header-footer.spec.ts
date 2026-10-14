import { test, expect, Page } from '@playwright/test';

// Guard for the Grid, Listbox and Tree header and footer CSS (ZK-6112, D11-D13):
//  - the sort arrow is 12x12 in all three (grid.css gave Grid 14px through `.z-column [class*="z-icon-"]`);
//  - the label box of a narrow sorted column stops short of the arrow, with and without a column-menu
//    button and with an image in the header (Grid and Tree clip, Listbox wraps);
//  - a Tree header holding an image, an icon or the select-all box keeps the label centred on it and the
//    header at the height the old flex layout gave it;
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
      // depend on how long the label is or whether the header wraps (Listbox) or clips (Grid, Tree).
      const labelBoxRight = await page.locator(`${header} ${mesh.headerContent}`)
        .evaluate((content) => content.getBoundingClientRect().right);
      expect(arrowLeft - labelBoxRight, `label box right ${labelBoxRight}, arrow left ${arrowLeft}`)
        .toBeGreaterThanOrEqual(MIN_LABEL_ARROW_GAP_PX);
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
});
