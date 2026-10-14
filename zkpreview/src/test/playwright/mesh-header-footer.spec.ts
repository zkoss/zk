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
//  - under the tablet project (zkmax `tablet/_mesh.css` must not give `.z-listheader-checkable` a width or
//    height, or the inline-block select-all span picks it up): the label of a select-all header clears the box, the header is
//    not taller than a plain Listbox header beyond the known offset, and a centred box lines up with the rows;
//  - a Tree header holding only the select-all box in a 40px column shows the whole 16px box, at the x of the
//    row checkbox, with no ellipsis painted, on desktop and on a tablet (D15);
//  - Grid and Listbox headers with a column-menu button keep the label box clear of the button and of the sort
//    arrow, and the sort arrow clear of the button, on desktop and on a tablet where the button is 44px (D11);
//  - a Listbox header with a select-all box is as tall as the Grid header with the same labels, its labels sit
//    at the same top offset and the painted box is centred on the label, on desktop and on a tablet (D16);
//  - every checkmark Listbox header keeps its select-all box unclipped (`.z-listheader-content` does not
//    overflow vertically) on the font-size of the header, so zkmax cannot size the glyph box of the span;
//  - a select-all header that also holds the sort arrow and/or the column-menu button keeps its label box
//    clear of them (D11 follow-through);
//  - a Tree header is as tall as the Grid header with the same labels in default, compact and tablet density,
//    and on a tablet the data rows are as tall as well (D17);
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

// Top of the first text node's rendered text.
function textTop(page: Page, cell: string): Promise<number> {
  return page.locator(cell).first().evaluate((el) => {
    const text = document.createTreeWalker(el, NodeFilter.SHOW_TEXT, {
      acceptNode: (node) => (node.textContent?.trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP),
    }).nextNode() as Text;
    const range = document.createRange();
    range.selectNodeContents(text);
    return range.getBoundingClientRect().top;
  });
}

// A Listbox with a select-all box next to a Grid with the same labels: same header height, labels of one
// row at one top offset, box centred on its label.
function registerSelectAllHeaderVersusGrid() {
  test('.mhf-listbox-vs-grid: the select-all header is as tall as the Grid header', async ({ page }) => {
    await open(page);
    const headerHeight = (root: string, header: string) => page.locator(`${root} ${header}:nth-child(1)`)
      .evaluate((th) => th.getBoundingClientRect().height);
    const listbox = await headerHeight('.mhf-listbox-vs-grid', 'th.z-listheader');
    const grid = await headerHeight('.mhf-grid-vs-listbox', 'th.z-column');
    expect(listbox, `listbox header ${listbox}, grid header ${grid}`).toBeCloseTo(grid, 1);
  });

  test('.mhf-listbox-vs-grid: the labels of the header row share one top offset', async ({ page }) => {
    await open(page);
    const first = await textTop(page, '.mhf-listbox-vs-grid th.z-listheader:nth-child(1) .z-listheader-content');
    const second = await textTop(page, '.mhf-listbox-vs-grid th.z-listheader:nth-child(2) .z-listheader-content');
    expect(Math.abs(first - second), `Name top ${first}, Type top ${second}`).toBeLessThanOrEqual(0.1);
  });

  test('.mhf-listbox-vs-grid: the painted select-all box is centred on its label', async ({ page }) => {
    await open(page);
    const header = '.mhf-listbox-vs-grid th.z-listheader:nth-child(1)';
    // The 16px glyph is the icon's ::before, which getBoundingClientRect cannot see. A zero-size marker in
    // the icon gives the baseline the glyph hangs from; the glyph box is `vertical-align` below it.
    const boxCentre = await page.locator(`${header} .z-listheader-icon`).evaluate((icon) => {
      const marker = document.createElement('span');
      marker.style.cssText = 'display:inline-block;width:0;height:0';
      icon.appendChild(marker);
      const baseline = marker.getBoundingClientRect().bottom;
      marker.remove();
      const glyph = getComputedStyle(icon, '::before');
      const bottom = baseline - parseFloat(glyph.verticalAlign);
      return bottom - parseFloat(glyph.height) / 2;
    });
    const labelCentre = await page.locator(`${header} .z-listheader-content`).evaluate((el) => {
      const text = document.createTreeWalker(el, NodeFilter.SHOW_TEXT, {
        acceptNode: (node) => (node.textContent?.trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP),
      }).nextNode() as Text;
      const range = document.createRange();
      range.selectNodeContents(text);
      const rect = range.getBoundingClientRect();
      return (rect.top + rect.bottom) / 2;
    });
    expect(Math.abs(boxCentre - labelCentre), `box centre ${boxCentre}, label centre ${labelCentre}`)
      .toBeLessThanOrEqual(TOLERANCE_PX);
  });
}

const CHECKMARK_LISTBOXES = ['.mhf-listbox-checkable', '.mhf-listbox-select-all-label', '.mhf-listbox-select-all-centre',
  '.mhf-listbox-vs-grid', '.mhf-listbox-select-all-sort', '.mhf-listbox-select-all-menu',
  '.mhf-listbox-select-all-sort-menu'];

// The select-all box must not be cut by the header content (overflow hidden) and the span must not be
// resized by zkmax, which put the 16px glyph on a 24px baseline and clipped its bottom edge.
function registerSelectAllBoxIsNotClipped() {
  for (const root of CHECKMARK_LISTBOXES) {
    test(`${root}: the select-all box is not clipped and the span keeps the header font-size`, async ({ page }) => {
      await open(page);
      const measured = await page.locator(`${root} th.z-listheader:nth-child(1)`).evaluate((th) => {
        const content = th.querySelector('.z-listheader-content')!;
        return {
          scrollHeight: content.scrollHeight, clientHeight: content.clientHeight,
          headerFontSize: getComputedStyle(th).fontSize,
          boxFontSize: getComputedStyle(th.querySelector('.z-listheader-checkable')!).fontSize,
        };
      });
      expect.soft(measured.scrollHeight, 'content clips the box vertically').toBeLessThanOrEqual(measured.clientHeight);
      expect(measured.boxFontSize).toBe(measured.headerFontSize);
    });
  }
}

// A select-all header holding the sort arrow and/or the column-menu button reserves the same room as the
// plain header with the same controls (D11), and the label box ends before the controls.
function registerSelectAllHeaderClearsArrowAndButton() {
  const cases: Array<{ root: string; plain: string; controls: string[] }> = [
    { root: '.mhf-listbox-select-all-sort', plain: '.mhf-listbox th.z-listheader:nth-child(2)', controls: ['.z-listheader-sorticon'] },
    { root: '.mhf-listbox-select-all-menu', plain: '.mhf-listbox-menu th.z-listheader:nth-child(1)', controls: ['.z-listheader-button'] },
    {
      root: '.mhf-listbox-select-all-sort-menu', plain: '.mhf-listbox-menu th.z-listheader:nth-child(2)',
      controls: ['.z-listheader-sorticon', '.z-listheader-button'],
    },
  ];
  for (const { root, plain, controls } of cases) {
    test(`${root}: the right padding is the one of the plain header with ${controls.join(' and ')}`, async ({ page }) => {
      await open(page);
      const paddingRight = (selector: string) => page.locator(selector)
        .evaluate((th) => parseFloat(getComputedStyle(th).paddingRight));
      const selectAll = await paddingRight(`${root} th.z-listheader:nth-child(1)`);
      const plainHeader = await paddingRight(plain);
      expect(selectAll, `select-all ${selectAll}px, plain ${plainHeader}px`).toBe(plainHeader);
    });

    test(`${root}: the label box ends before ${controls.join(' and ')}`, async ({ page }) => {
      await open(page);
      const header = page.locator(`${root} th.z-listheader:nth-child(1)`);
      const labelBoxRight = await header.locator('.z-listheader-content')
        .evaluate((content) => content.getBoundingClientRect().right);
      for (const control of controls) {
        const controlLeft = await header.locator(control).evaluate((el) => el.getBoundingClientRect().left);
        expect(controlLeft - labelBoxRight, `label box right ${labelBoxRight}, ${control} left ${controlLeft}`)
          .toBeGreaterThanOrEqual(MIN_LABEL_ARROW_GAP_PX);
      }
      if (controls.length < 2) return;
      const arrowRight = await header.locator('.z-listheader-sorticon i').evaluate((el) => el.getBoundingClientRect().right);
      const buttonLeft = await header.locator('.z-listheader-button').evaluate((el) => el.getBoundingClientRect().left);
      expect(buttonLeft - arrowRight, `arrow right ${arrowRight}, button left ${buttonLeft}`)
        .toBeGreaterThanOrEqual(MIN_LABEL_ARROW_GAP_PX);
    });
  }
}

interface MenuHeader {
  name: string;
  header: string;
  sortIcon?: string;
  button: string;
  content: string;
}

const MENU_HEADERS: MenuHeader[] = [
  { name: 'grid, button only', header: '.mhf-grid-menu th.z-column:nth-child(1)', button: '.z-column-button', content: '.z-column-content' },
  {
    name: 'grid, sort and button', header: '.mhf-grid-menu th.z-column:nth-child(2)', button: '.z-column-button',
    content: '.z-column-content', sortIcon: '.z-column-sorticon i',
  },
  {
    name: 'listbox, button only', header: '.mhf-listbox-menu th.z-listheader:nth-child(1)', button: '.z-listheader-button',
    content: '.z-listheader-content',
  },
  {
    name: 'listbox, sort and button', header: '.mhf-listbox-menu th.z-listheader:nth-child(2)', button: '.z-listheader-button',
    content: '.z-listheader-content', sortIcon: '.z-listheader-sorticon i',
  },
];

// The column-menu button is 24px on desktop and 44px on a tablet (zkmax tablet/_mesh.css); the header's right
// padding and the sort arrow's offset must follow it, or the label and the arrow run under the button box.
function registerMenuHeaderClearsArrowAndButton() {
  const left = (page: Page, selector: string) => page.locator(selector).evaluate((el) => el.getBoundingClientRect().left);
  const right = (page: Page, selector: string) => page.locator(selector).evaluate((el) => el.getBoundingClientRect().right);

  for (const { name, header, button, content, sortIcon } of MENU_HEADERS) {
    test(`${name}: the label box ends before the menu button`, async ({ page }) => {
      await open(page);
      const labelRight = await right(page, `${header} ${content}`);
      const buttonLeft = await left(page, `${header} ${button}`);
      expect(buttonLeft - labelRight, `label box right ${labelRight}, button left ${buttonLeft}`)
        .toBeGreaterThanOrEqual(MIN_LABEL_ARROW_GAP_PX);
    });

    if (!sortIcon) continue;
    test(`${name}: the label box ends before the sort arrow and the arrow before the menu button`, async ({ page }) => {
      await open(page);
      const labelRight = await right(page, `${header} ${content}`);
      const arrowLeft = await left(page, `${header} ${sortIcon}`);
      const arrowRight = await right(page, `${header} ${sortIcon}`);
      const buttonLeft = await left(page, `${header} ${button}`);
      expect(arrowLeft - labelRight, `label box right ${labelRight}, arrow left ${arrowLeft}`)
        .toBeGreaterThanOrEqual(MIN_LABEL_ARROW_GAP_PX);
      expect(buttonLeft - arrowRight, `arrow right ${arrowRight}, button left ${buttonLeft}`)
        .toBeGreaterThanOrEqual(MIN_LABEL_ARROW_GAP_PX);
    });
  }
}

// A select-all-only 40px Tree header (D15): the whole 16px box lies inside the content box, at the x of the
// row checkbox, and the header paints no ellipsis. The content box overflows by the trailing `&nbsp;` (scrollWidth
// 29 against clientWidth 16 on a tablet), so overflow cannot stand for the ellipsis; the pixels do: the header
// looks the same with `text-overflow: clip`.
function registerTreeSelectAllOnlyHeader() {
  const HEADER = '.mhf-tree-checkbox-only th.z-treecol:nth-child(1)';

  test('.mhf-tree-checkbox-only: a select-all-only 40px header holds the whole box at the x of the row box', async ({ page }) => {
    await open(page);
    const measured = await page.locator(HEADER).evaluate((th) => {
      const content = th.querySelector('.z-treecol-content')!.getBoundingClientRect();
      const icon = th.querySelector('.z-treecol-icon')!.getBoundingClientRect();
      const rowIcon = th.closest('.z-tree')!.querySelector('.z-treerow-icon')!.getBoundingClientRect();
      return {
        iconLeft: icon.left, iconRight: icon.right, rowIconLeft: rowIcon.left,
        contentLeft: content.left, contentRight: content.right,
      };
    });
    expect.soft(measured.iconLeft >= measured.contentLeft && measured.iconRight <= measured.contentRight,
      `icon ${measured.iconLeft}-${measured.iconRight}, content ${measured.contentLeft}-${measured.contentRight}`).toBe(true);
    expect(Math.abs(measured.iconLeft - measured.rowIconLeft),
      `header box x ${measured.iconLeft}, row box x ${measured.rowIconLeft}`).toBeLessThanOrEqual(TOLERANCE_PX);
  });

  test('.mhf-tree-checkbox-only: a select-all-only 40px header paints no ellipsis', async ({ page }) => {
    await open(page);
    const header = page.locator(HEADER);
    const content = header.locator('.z-treecol-content');
    const withEllipsis = await header.screenshot({ animations: 'disabled' });
    await content.evaluate((el) => { el.style.textOverflow = 'clip'; });
    const clipped = await header.screenshot({ animations: 'disabled' });
    expect(withEllipsis.equals(clipped), 'the header looks different with text-overflow clip, so an ellipsis is painted').toBe(true);
  });
}

const SUB_PIXEL_PX = 0.01;

const boxHeight = (page: Page, selector: string) => page.locator(selector).first()
  .evaluate((el) => el.getBoundingClientRect().height);

// Tree and Grid with the same labels: both header rows must come out at the same height.
function registerTreeHeaderMatchesGrid() {
  const compare = async (page: Page) => {
    const tree = await boxHeight(page, '.mhf-tree-vs-grid th.z-treecol');
    const grid = await boxHeight(page, '.mhf-grid-vs-tree th.z-column');
    expect(Math.abs(tree - grid), `tree header ${tree}, grid header ${grid}`).toBeLessThanOrEqual(SUB_PIXEL_PX);
  };

  test('.mhf-tree-vs-grid: the tree header is as tall as the grid header', async ({ page }) => {
    await open(page);
    await compare(page);
  });

  test('.mhf-tree-vs-grid: the tree header is as tall as the grid header in compact density', async ({ page }) => {
    await open(page);
    await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
    await compare(page);
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

  registerTreeSelectAllOnlyHeader();
  registerSelectAllHeaderVersusGrid();
  registerSelectAllBoxIsNotClipped();
  registerSelectAllHeaderClearsArrowAndButton();
  registerMenuHeaderClearsArrowAndButton();
  registerTreeHeaderMatchesGrid();

  // Tagged @tablet: the `mesh-header-footer` project skips it, `mesh-header-footer-tablet` runs only it.
  test.describe('select-all header on a tablet @tablet', () => {
    // A select-all Listbox header is as tall as a plain one; the tolerance only absorbs sub-pixel rounding.
    const MAX_HEADER_HEIGHT_OFFSET_PX = 0.01;

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

    registerTreeSelectAllOnlyHeader();
    registerSelectAllHeaderVersusGrid();
    registerSelectAllBoxIsNotClipped();
    registerSelectAllHeaderClearsArrowAndButton();
    registerMenuHeaderClearsArrowAndButton();
    registerTreeHeaderMatchesGrid();

    test('.mhf-tree-vs-grid: the tree data row is as tall as the grid data row', async ({ page }) => {
      await open(page);
      const tree = await boxHeight(page, '.mhf-tree-vs-grid tr.z-treerow');
      const grid = await boxHeight(page, '.mhf-grid-vs-tree tr.z-row');
      expect(Math.abs(tree - grid), `tree row ${tree}, grid row ${grid}`).toBeLessThanOrEqual(SUB_PIXEL_PX);
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
