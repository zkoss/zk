import { test, expect, chromium, Browser, BrowserContext, Locator, Page } from '@playwright/test';

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
//  - a checkmark Listbox row and a checkmark Tree row are as tall as the Grid row in default, compact and tablet
//    density, the painted box is centred on the label and on a control in the first cell within 1px, under the default type scale,
//    a 16px/24px one and Arial, and on a tablet the box keeps its 24px height (follow-up 28);
//  - a Tree row holding text, a textbox or a button is as tall as the Grid row in default, compact and tablet density
//    (follow-ups 34 and 38);
//  - a Listbox group row is as tall as a Grid group row (within the 0.5px of the Grid group's border-top) and its toggle
//    icon starts on the keyline of the data-row text, in default, compact and tablet density (follow-up 40);
//  - the checkbox of a selectable Listbox group (groupSelect) paints the box of the row checkbox, unchecked, hovered
//    and checked, sits on the keyline of the row checkbox, is centred on the group label, and leaves the toggle arrow
//    alone (follow-up 41);
//  - the first cell of a checkmark Listbox row keeps its text, or its image when it has one, on the keyline of the
//    header label and 4px between the image and its label and between a label and a link, in the same three
//    densities and type scales (follow-up 28, D18);
//  - in a centred and a right-aligned checkmark first column the box of a checkbox-only row is under the header box, and
//    the box of a row with an image after it is where the first-cell compensation puts it, desktop and tablet (rule 4
//    of listbox.css and its `:last-child` narrowing, NS14 round 2, D18);
//  - an image-only header column that carries the column-menu button, at the documented minimum width (56px on
//    desktop, 76px on a tablet, contracts/listbox.md), shows the whole 16px image clear of the button, and a
//    select-all-only header at its minimum width (68px / 88px) paints the whole box inside the content box and clear of the
//    button (follow-up 30);
//  - an image + sort + menu header keeps the label, arrow and button in that order on a tablet too (follow-up 30);
//  - a header that holds a native control cuts a long label before the sort arrow and the menu button, keeps the
//    control visible and clickable at any overflow, and keeps the whole focus ring of the control (follow-up 35);
//  - footer text starts where the body text of the same column starts (`border-spacing: 2px` of the UA
//    table shifted Grid and Listbox footers right).
// Fixture: web/pv/mesh-header-footer-guard.zul

const FIXTURE = '/pv/mesh-header-footer-guard.zul';
const TOLERANCE_PX = 1;
const ARROW_SIZE_PX = 12;
const MIN_LABEL_ARROW_GAP_PX = 2;

// The header padding sits on the content div, as in IceBlue, so the room the header reserves for the arrow
// and the menu button is the content's padding-right: the label box ends at the content box, not at the border box.
const contentBoxRight = (content: Element) =>
  content.getBoundingClientRect().right - parseFloat(getComputedStyle(content).paddingRight);
const SORTED_COLUMN = 2;
const ICON_IMAGE_PX = 16;

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
      const paddingRight = (selector: string) => page.locator(`${selector} .z-listheader-content`)
        .evaluate((content) => parseFloat(getComputedStyle(content).paddingRight));
      const selectAll = await paddingRight(`${root} th.z-listheader:nth-child(1)`);
      const plainHeader = await paddingRight(plain);
      expect(selectAll, `select-all ${selectAll}px, plain ${plainHeader}px`).toBe(plainHeader);
    });

    test(`${root}: the label box ends before ${controls.join(' and ')}`, async ({ page }) => {
      await open(page);
      const header = page.locator(`${root} th.z-listheader:nth-child(1)`);
      const labelBoxRight = await header.locator('.z-listheader-content').evaluate(contentBoxRight);
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
  const right = (page: Page, selector: string) => page.locator(selector).evaluate(contentBoxRight);

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

// A header that holds a native control keeps the control usable and its focus ring whole (follow-up 35). The ring
// (2px outline + 2px offset) lies 4px outside the control, so the header content clips with `overflow: clip` and a 4px
// `overflow-clip-margin` where the browser has the property. Safari does not: there the content stays `overflow: visible`.
// `text-overflow: clip` goes with it: an ellipsis hides a control that does not fit, so the label is cut at the first
// obstacle instead (the sort arrow or the menu button) and the control stays visible and clickable.
const RING_REACH_PX = 4;
const RING_COLOUR_TOLERANCE = 24;
const INK_TOLERANCE = 48;
const LABEL_OBSTACLE_TOLERANCE_PX = 1;
const CELL_BORDER_INSET_PX = 3;
const BORDER_STRIP_START_PX = 8;
const BORDER_STRIP_WIDTH_PX = 20;
const BORDER_STRIP_HEIGHT_PX = 3;
const MIN_BORDER_INK_PIXELS = BORDER_STRIP_WIDTH_PX / 2;
const OVERFLOWS_PX = [1, 20];

interface ControlHeader {
  root: string;
  header: string;
  content: string;
  sortIcon: string;
  menuButton: string;
  // 1-based header columns, see the fixture: the sorted long label, an unsorted one (Grid and Listbox), the textbox.
  sortedColumn: number;
  unsortedColumn?: number;
  nameColumn: number;
}

const CONTROL_HEADERS: ControlHeader[] = [
  { root: '.mhf-grid-control', header: 'th.z-column', content: '.z-column-content', sortIcon: '.z-column-sorticon',
    menuButton: '.z-column-button', sortedColumn: 1, unsortedColumn: 3, nameColumn: 4 },
  { root: '.mhf-listbox-control', header: 'th.z-listheader', content: '.z-listheader-content', sortIcon: '.z-listheader-sorticon',
    menuButton: '.z-listheader-button', sortedColumn: 1, unsortedColumn: 3, nameColumn: 4 },
  { root: '.mhf-tree-control', header: 'th.z-treecol', content: '.z-treecol-content', sortIcon: '.z-treecol-sorticon',
    menuButton: '.z-treecol-button', sortedColumn: 1, nameColumn: 3 },
];
const RING_COLUMN = 2;

const supportsClipMargin = (page: Page) => page.evaluate(() => CSS.supports('overflow-clip-margin', '4px'));

// Decodes a PNG in the page and hands its RGBA pixels to `read`, so a spec can look at what is painted.
function decodePixels<T, A>(
  page: Page, png: Buffer, argument: A,
  read: (pixels: Uint8ClampedArray, width: number, height: number, argument: A) => T,
): Promise<T> {
  return page.evaluate(async ({ base64, source, argument }) => {
    const image = new Image();
    image.src = `data:image/png;base64,${base64}`;
    await image.decode();
    const canvas = document.createElement('canvas');
    canvas.width = image.width;
    canvas.height = image.height;
    const context = canvas.getContext('2d')!;
    context.drawImage(image, 0, 0);
    const { data } = context.getImageData(0, 0, image.width, image.height);
    return new Function('pixels', 'width', 'height', 'argument', `return (${source})(pixels, width, height, argument)`)(data, image.width, image.height, argument);
  }, { base64: png.toString('base64'), source: read.toString(), argument });
}

// The colour of the pixel at (x, y) of the page, read off a screenshot.
async function pixelAt(page: Page, x: number, y: number): Promise<number[]> {
  const png = await page.screenshot({ fullPage: true, clip: { x: Math.floor(x), y: Math.floor(y), width: 1, height: 1 } });
  return decodePixels(page, png, null, (pixels) => Array.from(pixels.slice(0, 3)));
}

// How many pixels of a page region differ from `background` by more than `tolerance`, and the rightmost such column.
async function inkIn(page: Page, clip: { x: number; y: number; width: number; height: number }, background: number[]) {
  const png = await page.screenshot({ fullPage: true, animations: 'disabled', clip });
  return decodePixels(page, png, { background, tolerance: INK_TOLERANCE }, (pixels, width, height, { background, tolerance }) => {
    let count = 0;
    let rightmost = -1;
    for (let y = 0; y < height; y++) {
      for (let x = 0; x < width; x++) {
        const i = (y * width + x) * 4;
        const difference = Math.abs(pixels[i] - background[0]) + Math.abs(pixels[i + 1] - background[1]) + Math.abs(pixels[i + 2] - background[2]);
        if (difference > tolerance) {
          count++;
          rightmost = Math.max(rightmost, x);
        }
      }
    }
    return { count, rightmost };
  });
}

function registerHeaderControlStaysUsableAndRingWhole() {
  for (const spec of CONTROL_HEADERS) {
    const { root, header, content } = spec;
    const th = (column: number) => `${root} ${header}:nth-child(${column})`;

    // The label ink, read with the controls and obstacles hidden: where is the label painted, and where is the first
    // obstacle (the sort arrow or the menu button, whichever is wider than 0 and leftmost).
    async function labelAgainstObstacle(page: Page, column: number, hover: boolean) {
      const cell = page.locator(th(column));
      if (hover) await cell.hover();
      await page.addStyleTag({ content: '*, *::before, *::after { transition: none !important; }' });
      const measured = await cell.evaluate((el, { sortIcon, menuButton }) => {
        const rect = el.getBoundingClientRect();
        const obstacles = [...el.querySelectorAll(`${sortIcon}, ${menuButton}`)]
          .map((obstacle) => obstacle.getBoundingClientRect())
          .filter((box) => box.width > 0)
          .map((box) => box.left - rect.left);
        for (const hidden of el.querySelectorAll(`${sortIcon}, ${menuButton}, button, input`)) (hidden as HTMLElement).style.visibility = 'hidden';
        return { left: rect.left, top: rect.top + window.scrollY, width: rect.width, height: rect.height, obstacle: Math.min(...obstacles) };
      }, spec);
      // Inset: the cell border (a column divider, the bottom border) is ink too, and not the label.
      const clip = {
        x: measured.left, y: measured.top + CELL_BORDER_INSET_PX,
        width: measured.width - CELL_BORDER_INSET_PX, height: measured.height - 2 * CELL_BORDER_INSET_PX,
      };
      const background = await pixelAt(page, measured.left + 2, measured.top + CELL_BORDER_INSET_PX);
      const ink = await inkIn(page, clip, background);
      return { ...measured, ink };
    }

    for (const [state, column, hover] of [
      ['sorted', spec.sortedColumn, false],
      ...(spec.unsortedColumn ? [['not sorted', spec.unsortedColumn, false], ['not sorted and hovered', spec.unsortedColumn, true]] : []),
    ] as [string, number, boolean][]) {
      test(`${root}: a long label (${state}) is cut before the first obstacle`, async ({ page }) => {
        await open(page);
        test.skip(!(await supportsClipMargin(page)), 'Safari has no overflow-clip-margin: the header keeps overflow visible there, a long label paints under the arrow as before');
        const { obstacle, ink } = await labelAgainstObstacle(page, column, hover);
        expect(Number.isFinite(obstacle), 'the header has a sort arrow or a menu button wider than 0').toBe(true);
        expect(ink.rightmost, 'the label is painted').toBeGreaterThan(0);
        expect(ink.rightmost, `label ink ends at x=${ink.rightmost}, the first obstacle starts at x=${obstacle}`)
          .toBeLessThanOrEqual(obstacle + LABEL_OBSTACLE_TOLERANCE_PX);
      });
    }

    test(`${root}: the focus ring of a control is whole`, async ({ page }) => {
      await open(page);
      const button = page.locator(`${th(RING_COLUMN)} button`);
      await page.keyboard.press('Tab');
      await button.focus();
      await page.addStyleTag({ content: '*, *::before, *::after { transition: none !important; }' });
      const box = await button.evaluate((el) => {
        const rect = el.getBoundingClientRect();
        return {
          centreX: rect.left + rect.width / 2, top: rect.top + window.scrollY, bottom: rect.bottom + window.scrollY,
          colour: (getComputedStyle(el).outlineColor.match(/[\d.]+/g) ?? []).slice(0, 3).map(Number),
        };
      });
      const middleOfRing = RING_REACH_PX - 1;
      for (const [side, y] of [['top', box.top - middleOfRing], ['bottom', box.bottom + middleOfRing - 1]] as const) {
        const painted = await pixelAt(page, box.centreX, y);
        const difference = painted.reduce((sum, channel, i) => sum + Math.abs(channel - box.colour[i]), 0);
        expect(difference, `${side} of the ring is ${JSON.stringify(painted)}, outline ${JSON.stringify(box.colour)}`)
          .toBeLessThanOrEqual(RING_COLOUR_TOLERANCE);
      }
    });

    for (const overflow of OVERFLOWS_PX) {
      test(`${root}: a textbox ${overflow}px wider than the content box stays visible and clickable`, async ({ page }) => {
        await open(page);
        const input = page.locator(`${th(spec.nameColumn)} input`);
        await input.evaluate((el) => el.scrollIntoView({ block: 'center' }));
        const clipMargin = (await supportsClipMargin(page)) ? RING_REACH_PX : 0;
        const probe = await input.evaluate((el, { over, contentSelector }) => {
          const cellContent = el.closest(contentSelector) as HTMLElement;
          const input = el as HTMLInputElement;
          const contentRight = () => cellContent.getBoundingClientRect().left + cellContent.clientWidth
            - parseFloat(getComputedStyle(cellContent).paddingRight);
          // Where a browser sizes the columns by their content, the width of the textbox moves the content box: settle it.
          input.style.width = '';
          for (let pass = 0; pass < 3; pass++) {
            input.style.width = `${input.getBoundingClientRect().width + contentRight() + over - input.getBoundingClientRect().right}px`;
          }
          const rect = input.getBoundingClientRect();
          return { left: rect.left, right: rect.right, top: rect.top, pageTop: rect.top + window.scrollY, height: rect.height, contentRight: contentRight() };
        }, { over: overflow, contentSelector: content });
        // Hit-test first: a full-page screenshot may move the scroll position in some engines.
        const visibleRight = Math.min(probe.right, probe.contentRight + clipMargin);
        const hit = await input.evaluate((el, point) => document.elementFromPoint(point.x, point.y) === el, {
          x: (probe.left + visibleRight) / 2, y: probe.top + probe.height / 2,
        });
        expect(hit, 'elementFromPoint at the visible centre of the textbox is the textbox').toBe(true);
        // The top border of the textbox, away from its rounded corners and from the label: an ellipsis paints at the baseline
        // and never reaches it.
        const background = await pixelAt(page, probe.left + BORDER_STRIP_START_PX, probe.pageTop - BORDER_STRIP_START_PX);
        const border = { x: probe.left + BORDER_STRIP_START_PX, y: probe.pageTop, width: BORDER_STRIP_WIDTH_PX, height: BORDER_STRIP_HEIGHT_PX };
        const ink = await inkIn(page, border, background);
        expect(ink.count, `the top border of the textbox paints ${ink.count} pixels`).toBeGreaterThanOrEqual(MIN_BORDER_INK_PIXELS);
      });
    }
  }
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

const TOUCH_BOX_PX = 24;
const MIN_IMAGE_COLUMN_DESKTOP = 1;
const MIN_IMAGE_COLUMN_TABLET = 2;

interface CheckmarkRows {
  name: string;
  root: string;
  row: string;
  icon: string;
  checkable: string;
  text: string;
  gridRow: string;
}

const CHECKMARK_ROWS: CheckmarkRows[] = [
  {
    name: 'listbox', root: '.mhf-listbox-vs-grid', row: 'tr.z-listitem', icon: '.z-listitem-icon',
    checkable: '.z-listitem-checkable', text: '.z-listcell-content', gridRow: '.mhf-grid-vs-listbox tr.z-row',
  },
  {
    name: 'tree', root: '.mhf-tree-checkmark', row: 'tr.z-treerow', icon: '.z-treerow-icon',
    checkable: '.z-treerow-checkable', text: '.z-treecell-content', gridRow: '.mhf-grid-vs-tree tr.z-row',
  },
];

interface ControlRows {
  name: string;
  controlRoot: string;
  controlGridRoot: string;
  icon: string;
}

const CONTROL_ROWS: ControlRows[] = [
  { name: 'listbox', controlRoot: '.mhf-listbox-control-row', controlGridRoot: '.mhf-grid-control-row', icon: '.z-listitem-icon' },
  { name: 'tree', controlRoot: '.mhf-tree-control-row', controlGridRoot: '.mhf-grid-control-row', icon: '.z-treerow-icon' },
];

const GLYPH_PX = 16;

// The type scales a checkmark row must stay centred under: the box is centred by the flex row of its cell, not by
// margins tuned to one font and one line height (NS13 F1).
const TYPE_SCALES: Record<string, string> = {
  default: '',
  '16px/24px': ':root{--zk-typescale-body-medium-size:16px;--zk-typescale-body-medium-line-height:24px}',
  Arial: ':root{--zk-typescale-font-family: Arial}',
};

// A row's own separator lines are not part of the box that is measured.
const ROW_EDGE_PX = 3;
const INK_THRESHOLD = 150;

// The vertical centre and height of the box a checkmark row paints, read off a screenshot of the icon's column: how the
// box is placed (inline, flex, margins) does not matter, and a box that is merely laid out well but not painted is not
// counted. The ink is every pixel that differs from the row background.
async function paintedBox(page: Page, tr: Locator, icon: string): Promise<{ centre: number; height: number }> {
  const column = await tr.evaluate((row, iconSelector) => {
    const rowRect = row.getBoundingClientRect();
    const iconRect = row.querySelector(iconSelector)!.getBoundingClientRect();
    return { x: iconRect.left, width: iconRect.width, top: rowRect.top + window.scrollY, height: rowRect.height };
  }, icon);
  const top = column.top + ROW_EDGE_PX;
  const png = await page.screenshot({
    fullPage: true,
    clip: { x: column.x, y: top, width: column.width, height: column.height - 2 * ROW_EDGE_PX },
  });
  const ink = await page.evaluate(async ({ base64, threshold }) => {
    const image = new Image();
    image.src = `data:image/png;base64,${base64}`;
    await image.decode();
    const canvas = document.createElement('canvas');
    canvas.width = image.width;
    canvas.height = image.height;
    const context = canvas.getContext('2d')!;
    context.drawImage(image, 0, 0);
    const { data } = context.getImageData(0, 0, image.width, image.height);
    let first = -1;
    let last = -1;
    for (let y = 0; y < image.height; y++) {
      for (let x = 0; x < image.width; x++) {
        const i = (y * image.width + x) * 4;
        const difference = Math.abs(data[i] - data[0]) + Math.abs(data[i + 1] - data[1]) + Math.abs(data[i + 2] - data[2]);
        if (difference <= threshold) continue;
        if (first < 0) first = y;
        last = y;
      }
    }
    return { first, last, pixels: image.height };
  }, { base64: png.toString('base64'), threshold: INK_THRESHOLD });
  expect(ink.first, 'the checkmark box paints nothing in the icon column').toBeGreaterThanOrEqual(0);
  const scale = ink.pixels / (column.height - 2 * ROW_EDGE_PX);
  return { centre: top + (ink.first + ink.last + 1) / 2 / scale, height: (ink.last - ink.first + 1) / scale };
}

// The vertical centre of the first text of `text`, or of the box of the control that `text` selects when `control` is set.
function targetCentre(tr: Locator, selectors: { text: string; control?: boolean }): Promise<number> {
  return tr.evaluate((row, { text, control }) => {
    const centre = (rect: DOMRect) => (rect.top + rect.bottom) / 2;
    if (control) return centre(row.querySelector(text)!.getBoundingClientRect());
    const textNode = document.createTreeWalker(row.querySelector(text)!, NodeFilter.SHOW_TEXT, {
      acceptNode: (node) => (node.textContent?.trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP),
    }).nextNode() as Text;
    const label = document.createRange();
    label.selectNodeContents(textNode);
    return centre(label.getBoundingClientRect());
  }, selectors);
}

async function expectBoxCentredOn(page: Page, tr: Locator, icon: string, target: { text: string; control?: boolean }, what: string) {
  const box = await paintedBox(page, tr, icon);
  const targetY = await targetCentre(tr, target);
  expect(Math.abs(box.height - GLYPH_PX), `painted box height ${box.height}`).toBeLessThanOrEqual(TOLERANCE_PX);
  expect(Math.abs(box.centre - targetY), `box centre ${box.centre}, ${what} centre ${targetY}`).toBeLessThanOrEqual(TOLERANCE_PX);
}

const FIRST_CELL_ROOT = '.mhf-listbox-first-cell';
const FIRST_CELL_GAP_MIN_PX = 3;
const FIRST_CELL_GAP_MAX_PX = 5;

interface FirstCellGeometry {
  textLefts: number[];
  textRights: number[];
  imageLeft: number | null;
  imageRight: number | null;
  linkLeft: number | null;
}

// Horizontal positions in the content of a cell: where the glyphs of every non-blank text node start and end (a Range
// from the first character that is not a space or a no-break space, as ZK renders `&nbsp;` before the text of a
// cell and the header label), the image and the link.
function firstCellGeometry(page: Page, contentSelector: string): Promise<FirstCellGeometry> {
  return page.locator(contentSelector).first().evaluate((content) => {
    const walker = document.createTreeWalker(content, NodeFilter.SHOW_TEXT, {
      acceptNode: (node) => (node.textContent?.trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP),
    });
    const textRects: DOMRect[] = [];
    for (let node = walker.nextNode() as Text | null; node; node = walker.nextNode() as Text | null) {
      const range = document.createRange();
      range.setStart(node, node.data.search(/[^\s\u00a0]/));
      range.setEnd(node, node.length);
      textRects.push(range.getBoundingClientRect());
    }
    const image = content.querySelector('img')?.getBoundingClientRect();
    const link = content.querySelector('a')?.getBoundingClientRect();
    return {
      textLefts: textRects.map((rect) => rect.left),
      textRights: textRects.map((rect) => rect.right),
      imageLeft: image?.left ?? null,
      imageRight: image?.right ?? null,
      linkLeft: link?.left ?? null,
    };
  });
}

function expectGapBetween(gap: number, what: string) {
  expect(gap, `${what} gap ${gap}`).toBeGreaterThanOrEqual(FIRST_CELL_GAP_MIN_PX);
  expect(gap, `${what} gap ${gap}`).toBeLessThanOrEqual(FIRST_CELL_GAP_MAX_PX);
}

// Rows with an image or several inline items in the first cell (D18). Their items are flex items, which trim the
// whitespace between them: the 4px gap stands in for it, and two compensations keep the text on the keyline.
function registerCheckmarkFirstCell(prepare: (page: Page) => Promise<void>, inDensity: string) {
  const headerTextLeft = async (page: Page) =>
    (await firstCellGeometry(page, `${FIRST_CELL_ROOT} th.z-listheader .z-listheader-content`)).textLefts[0];
  const rowGeometry = (page: Page, row: string) => firstCellGeometry(page, `${FIRST_CELL_ROOT} ${row} .z-listcell-content`);

  test(`${FIRST_CELL_ROOT}: the text of a checkmark row is on the keyline of the header label in ${inDensity}`, async ({ page }) => {
    await prepare(page);
    const cell = await rowGeometry(page, '.mhf-text-row');
    const header = await headerTextLeft(page);
    expect(Math.abs(cell.textLefts[0] - header), `text left ${cell.textLefts[0]}, header text left ${header}`).toBeLessThanOrEqual(TOLERANCE_PX);
  });

  test(`${FIRST_CELL_ROOT}: the image of a checkmark row is on the keyline and 4px from its label in ${inDensity}`,
    async ({ page }) => {
      await prepare(page);
      const cell = await rowGeometry(page, '.mhf-image-row');
      const header = await headerTextLeft(page);
      expect(Math.abs(cell.imageLeft! - header), `image left ${cell.imageLeft}, header text left ${header}`).toBeLessThanOrEqual(TOLERANCE_PX);
      expectGapBetween(cell.textLefts[0] - cell.imageRight!, 'image to label');
    });

  test(`${FIRST_CELL_ROOT}: a label and a link in a checkmark row are 4px apart in ${inDensity}`, async ({ page }) => {
    await prepare(page);
    const cell = await rowGeometry(page, '.mhf-inline-row');
    expectGapBetween(cell.linkLeft! - cell.textRights[0], 'label to link');
  });

  for (const row of ['.mhf-text-row', '.mhf-image-row', '.mhf-inline-row']) {
    test(`${FIRST_CELL_ROOT} ${row}: the row is as tall as the Grid row and the painted box is centred on the text in ${inDensity}`,
      async ({ page }) => {
        await prepare(page);
        const listbox = await boxHeight(page, `${FIRST_CELL_ROOT} ${row}`);
        const grid = await boxHeight(page, `.mhf-grid-first-cell ${row}`);
        expect(Math.abs(listbox - grid), `listbox row ${listbox}, grid row ${grid}`).toBeLessThanOrEqual(SUB_PIXEL_PX);
        await expectBoxCentredOn(page, page.locator(`${FIRST_CELL_ROOT} ${row}`).first(), '.z-listitem-icon',
          { text: '.z-listcell-content' }, 'text');
      });
  }
}

// In a centred or right-aligned first column, the checkmark of a row that holds only the box stays under the header
// box (rule 4 of listbox.css), and the box of a row with an image after it stays where the compensation of the first
// cell puts it, not 2px / 4px to the left (rule 4 is narrowed to `:last-child`, NS14 round 2). The expected x of the
// image row is the x of the same row with the compensated padding set inline from the tokens: it wins over rule 4
// wherever rule 4 applies, so the two agree only while rule 4 leaves the row alone.
function registerCheckmarkBoxInAlignedFirstColumn() {
  const ICON = '.z-listitem-icon';
  const SAME_POSITION_PX = 0.5;
  const COMPENSATED_PADDING = 'calc(var(--zk-spacing-3) - var(--zk-spacing-1))';
  const boxLeft = (page: Page, selector: string) => page.locator(selector).first()
    .evaluate((icon) => icon.getBoundingClientRect().left);

  for (const align of ['center', 'right']) {
    const root = `.mhf-listbox-align-${align}`;

    test(`${root}: the box of a checkbox-only row is under the header box`, async ({ page }) => {
      await open(page);
      const header = await boxLeft(page, `${root} th.z-listheader .z-listheader-icon`);
      const row = await boxLeft(page, `${root} .mhf-box-row ${ICON}`);
      expect(Math.abs(row - header), `header box x ${header}, row box x ${row}`).toBeLessThanOrEqual(TOLERANCE_PX);
    });

    test(`${root}: the box of an image row stays where the first-cell compensation puts it`, async ({ page }) => {
      await open(page);
      const span = `${root} .mhf-image-row .z-listitem-checkable`;
      const actual = await boxLeft(page, `${span} ${ICON}`);
      await page.locator(span).evaluate((el, padding) => { el.style.paddingInlineEnd = padding; }, COMPENSATED_PADDING);
      const compensated = await boxLeft(page, `${span} ${ICON}`);
      expect(Math.abs(actual - compensated), `box x ${actual}, with the compensated padding ${compensated}`)
        .toBeLessThanOrEqual(SAME_POSITION_PX);
    });
  }
}

// A select-all-only column of 24 to 160px: in a centred column the header box and the row box share one centre
// (cm5 of contracts/listbox.md), also where the 40px checkable span overflows the cell, where a centred row splits the
// overflow both ways and the header must do the same (follow-up 52). A left-aligned column keeps its box on the keyline,
// 16px from the column edge in the header and in the row.
function registerCheckmarkBoxColumns(density: 'default' | 'compact' | 'tablet') {
  const WIDTHS_PX = [24, 40, 48, 160];
  const KEYLINE_PX = 16;
  const boxRect = (page: Page, root: string, selector: string) => page.locator(`${root} ${selector}`).first()
    .evaluate((icon) => {
      const { left, right } = icon.getBoundingClientRect();
      return { left, centre: (left + right) / 2 };
    });
  const columnLeft = (page: Page, root: string) => page.locator(`${root} th.z-listheader`).first()
    .evaluate((th) => th.getBoundingClientRect().left);
  const prepare = async (page: Page) => {
    await open(page);
    if (density === 'compact') await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
    await page.evaluate(() => document.fonts.ready.then(() => true));
  };

  for (const width of WIDTHS_PX) {
    const centred = `.mhf-box-column-center-${width}`;
    test(`${centred}: the header box and the row box of a centred ${width}px checkmark column share one centre in ${density} density`,
      async ({ page }) => {
        await prepare(page);
        const header = await boxRect(page, centred, 'th.z-listheader .z-listheader-icon');
        const row = await boxRect(page, centred, 'tr.z-listitem .z-listitem-icon');
        expect(Math.abs(header.centre - row.centre), `header box centre ${header.centre}, row box centre ${row.centre}`)
          .toBeLessThanOrEqual(TOLERANCE_PX);
      });

    const left = `.mhf-box-column-left-${width}`;
    test(`${left}: a left-aligned ${width}px checkmark column keeps both boxes on the keyline in ${density} density`, async ({ page }) => {
      await prepare(page);
      const edge = await columnLeft(page, left);
      const header = await boxRect(page, left, 'th.z-listheader .z-listheader-icon');
      const row = await boxRect(page, left, 'tr.z-listitem .z-listitem-icon');
      expect(header.left - edge, `header box ${header.left}, column edge ${edge}`).toBeCloseTo(KEYLINE_PX, 0);
      expect(row.left - edge, `row box ${row.left}, column edge ${edge}`).toBeCloseTo(KEYLINE_PX, 0);
    });
  }
}

// A hand-written `style="text-align:center"` (no space after the colon) centres the box of a checkmark cell as the column
// `align="center"` does: the flex alignment rules of tree.css take both spellings (follow-up 59, F1).
function registerTreeCellStyleWithoutSpace() {
  test('.mhf-tree-style-no-space: the box of a cell with style="text-align:center" sits on the centre of the cell', async ({ page }) => {
    await open(page);
    const root = '.mhf-tree-style-no-space';
    const box = await page.locator(`${root} tr.z-treerow .z-treerow-icon`).first()
      .evaluate((icon) => { const { left, right } = icon.getBoundingClientRect(); return (left + right) / 2; });
    const cell = await page.locator(`${root} tr.z-treerow td.z-treecell`).first()
      .evaluate((td) => { const { left, right } = td.getBoundingClientRect(); return (left + right) / 2; });
    expect(Math.abs(box - cell), `box centre ${box}, cell centre ${cell}`).toBeLessThanOrEqual(TOLERANCE_PX);
  });
}

// The same for a checkmark Tree: the header box and the row box of a centred select-all-only column share one centre, the
// column centre (cm6 of contracts/tree.md; the row box sat at the left edge, or 12px left of centre in a 160px column, and
// the header box right of centre, follow-up 54), and in a left-aligned column both boxes stay on the keyline.
function registerTreeCheckmarkBoxColumns(density: 'default' | 'compact' | 'tablet') {
  const WIDTHS_PX = [24, 40, 48, 160];
  const boxRect = (page: Page, root: string, selector: string) => page.locator(`${root} ${selector}`).first()
    .evaluate((icon) => {
      const { left, right } = icon.getBoundingClientRect();
      return { left, centre: (left + right) / 2 };
    });
  const columnRect = (page: Page, root: string) => page.locator(`${root} th.z-treecol`).first()
    .evaluate((th) => {
      const { left, right } = th.getBoundingClientRect();
      return { left, centre: (left + right) / 2 };
    });
  const prepare = async (page: Page) => {
    await open(page);
    if (density === 'compact') await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
    await page.evaluate(() => document.fonts.ready.then(() => true));
  };

  for (const width of WIDTHS_PX) {
    const centred = `.mhf-tree-box-column-center-${width}`;
    test(`${centred}: the header box and the row box of a centred ${width}px checkmark Tree column both sit on the column centre in ${density} density`,
      async ({ page }) => {
        await prepare(page);
        const column = await columnRect(page, centred);
        const header = await boxRect(page, centred, 'th.z-treecol .z-treecol-icon');
        const row = await boxRect(page, centred, 'tr.z-treerow .z-treerow-icon');
        expect(Math.abs(header.centre - row.centre), `header box centre ${header.centre}, row box centre ${row.centre}`)
          .toBeLessThanOrEqual(TOLERANCE_PX);
        expect(Math.abs(row.centre - column.centre), `row box centre ${row.centre}, column centre ${column.centre}`)
          .toBeLessThanOrEqual(TOLERANCE_PX);
      });

    const left = `.mhf-tree-box-column-left-${width}`;
    test(`${left}: a left-aligned ${width}px checkmark Tree column keeps the header box and the row box on one keyline in ${density} density`,
      async ({ page }) => {
        await prepare(page);
        const header = await boxRect(page, left, 'th.z-treecol .z-treecol-icon');
        const row = await boxRect(page, left, 'tr.z-treerow .z-treerow-icon');
        expect(Math.abs(header.left - row.left), `header box ${header.left}, row box ${row.left}`).toBeLessThanOrEqual(TOLERANCE_PX);
      });
  }

  // A centred first column that carries a label is not a box-only column: the rules above must leave its rows as they were
  // (16px / 12px / 24px of cell padding in front of the box, 24px of indent per nesting level), and its header must keep the
  // select-all box inside the column, the 4px gap to the label and the ellipsis (follow-up 59). A flex header put the box of the
  // 100px column 61px outside it and painted no ellipsis (an anonymous flex item cannot show one: the container is not `block`).
  const ROW_BOX_OFFSET_PX = { default: 16, compact: 12, tablet: 24 }[density];
  const LABEL_BOX_GAP_PX = 4;
  const INDENT_PER_LEVEL_PX = 24;
  const labelled = async (page: Page, root: string) => {
    await prepare(page);
    return page.locator(root).evaluate((tree) => {
      const edges = (el: Element | Range) => { const { left, right } = el.getBoundingClientRect(); return { left, right }; };
      const column = tree.querySelector('th.z-treecol')!;
      const content = tree.querySelector('.z-treecol-content')!;
      const label = document.createRange();
      label.selectNodeContents([...content.childNodes].filter(n => n.nodeType === Node.TEXT_NODE).pop()!);
      const style = getComputedStyle(content);
      const rows = [...tree.querySelectorAll('tr.z-treerow')].map(tr => ({
        box: edges(tr.querySelector('.z-treerow-icon')!),
        text: edges(tr.querySelector('.z-treecell-text')!),
        spacerWidths: [...tr.querySelector('td')!.querySelectorAll('.z-tree-spacer')].map(sp => (sp as HTMLElement).offsetWidth),
      }));
      return {
        column: edges(column), box: edges(tree.querySelector('th .z-treecol-icon')!), label: edges(label),
        display: style.display, textOverflow: style.textOverflow, overflow: style.overflow,
        overflowing: content.scrollWidth > content.clientWidth, rows,
      };
    });
  };

  for (const width of [100, 160]) {
    const root = `.mhf-tree-label-${width}`;
    test(`${root}: the header of a centred ${width}px labelled Tree column keeps its box inside the column, the ${LABEL_BOX_GAP_PX}px gap and the ellipsis in ${density} density`,
      async ({ page }) => {
        const found = await labelled(page, root);
        expect(found.box.left, `header box ${found.box.left}, column ${found.column.left}`).toBeGreaterThanOrEqual(found.column.left);
        expect(found.box.right, `header box ${found.box.right}, column ${found.column.right}`).toBeLessThanOrEqual(found.column.right);
        expect(Math.abs(found.label.left - found.box.right - LABEL_BOX_GAP_PX), `box ${found.box.right}, label ${found.label.left}`)
          .toBeLessThanOrEqual(TOLERANCE_PX);
        expect(found.display, 'only a block container paints the ellipsis').toBe('block');
        expect(found.textOverflow).toBe('ellipsis');
        expect(found.overflow).toBe('hidden');
        expect(found.overflowing, 'the long label of the 100px column overflows and is cut with the ellipsis, the short one fits')
          .toBe(width === 100);
      });

    test(`${root}: the rows of a centred ${width}px labelled Tree column keep their cell padding and their indent in ${density} density`,
      async ({ page }) => {
        const found = await labelled(page, root);
        // The nested rows of the 100px column overflow it: their boxes are centred with the indent, as before follow-up 54.
        const keyline = width === 100 ? found.rows.slice(0, 1) : found.rows;
        for (const row of keyline) {
          expect(Math.abs(row.box.left - found.column.left - ROW_BOX_OFFSET_PX), `row box ${row.box.left}, column ${found.column.left}`)
            .toBeLessThanOrEqual(TOLERANCE_PX);
        }
        for (const [level, row] of found.rows.entries()) {
          expect(row.spacerWidths.length >= (level > 0 ? 1 : 0), `level ${level} has its spacers`).toBe(true);
          expect(row.spacerWidths.every(w => w > 0), `level ${level}: spacers ${JSON.stringify(row.spacerWidths)} are shown`).toBe(true);
        }
        if (width === 160) {
          const [parent, child, grandchild] = found.rows;
          expect(child.text.left - parent.text.left, 'indent of the child').toBeCloseTo(INDENT_PER_LEVEL_PX, 0);
          expect(grandchild.text.left - child.text.left, 'indent of the grandchild').toBeCloseTo(INDENT_PER_LEVEL_PX, 0);
        }
      });
  }

  // Indentation carries the hierarchy, so the row rules centre a level-0 leaf cell only: a container row and a child row of a
  // centred first column keep the indent they had before follow-up 54. A centred cell grows by one 24px indent per level and
  // sits on the column centre, so its box moves by half of it, 12px, per level. Centring the box of every cell put the box of
  // a container row 12px left of the centre and the boxes of level 0 and 1 on one x.
  const nestedRows = (page: Page, root: string) => page.locator(root).evaluate((tree) =>
    [...tree.querySelectorAll('tr.z-treerow')].map((tr) => {
      const box = tr.querySelector('.z-treerow-icon')!.getBoundingClientRect();
      const component = tr.querySelector('.z-treecell-content > :is(input, .z-label)')?.getBoundingClientRect();
      return { boxLeft: box.left, componentLeft: component?.left };
    }));
  const HALF_INDENT_PX = INDENT_PER_LEVEL_PX / 2;

  for (const kind of ['check', 'radio']) {
    const root = `.mhf-tree-nested-box-${kind}`;
    test(`${root}: the boxes of a centred ${kind} column move ${HALF_INDENT_PX}px left per nesting level in ${density} density`, async ({ page }) => {
      await prepare(page);
      const rows = await nestedRows(page, root);
      for (const level of [1, 2]) {
        expect(rows[level - 1].boxLeft - rows[level].boxLeft, `level ${level - 1} box ${rows[level - 1].boxLeft}, level ${level} box ${rows[level].boxLeft}`)
          .toBeCloseTo(HALF_INDENT_PX, 0);
      }
    });
  }

  test(`.mhf-tree-nested-component: a centred column of boxes and components keeps ${INDENT_PER_LEVEL_PX}px of indent per level in ${density} density`,
    async ({ page }) => {
      await prepare(page);
      const rows = await nestedRows(page, '.mhf-tree-nested-component');
      const indents = rows.map(row => row.componentLeft! - row.boxLeft);
      for (const level of [1, 2]) {
        expect(indents[level] - indents[level - 1], `box to component: ${JSON.stringify(indents)}`).toBeCloseTo(INDENT_PER_LEVEL_PX, 0);
      }
    });

  // The centring rules apply to a tree without a container row only (follow-up 59, NS30): beside a container, whose box keeps its
  // indent, a centred level-0 leaf sat 12px right of it, one nesting step the wrong way. In a tree with a container, open or
  // closed, the leaf box is on the keyline of the container boxes and the header is as before follow-up 54, its box right of the
  // column centre (the 16px / 4px padding and the 4px margin of the box).
  const MIXED = '.mhf-tree-mixed-box';
  const HEADER_BOX_RIGHT_OF_CENTRE_PX = { default: 4, compact: 2, tablet: 6 }[density];
  const mixedBoxes = async (page: Page) => {
    await prepare(page);
    return page.locator(MIXED).evaluate((tree) => {
      const boxLeft = (tr: Element) => tr.querySelector('.z-treerow-icon')!.getBoundingClientRect().left;
      const rows = [...tree.querySelectorAll('tr.z-treerow')];
      return {
        containers: rows.filter(tr => tr.querySelector('.z-tree-icon')).map(boxLeft),
        leaf: boxLeft(rows[rows.length - 1]),
      };
    });
  };

  test(`${MIXED}: the box of a level-0 leaf is on the keyline of the open and the closed container beside it in ${density} density`, async ({ page }) => {
    const { containers, leaf } = await mixedBoxes(page);
    expect(containers, 'an open and a closed container').toHaveLength(2);
    for (const container of containers) {
      expect(Math.abs(leaf - container), `leaf box ${leaf}, container boxes ${JSON.stringify(containers)}`).toBeLessThanOrEqual(TOLERANCE_PX);
    }
  });

  test(`${MIXED}: the header box of a tree with a container sits ${HEADER_BOX_RIGHT_OF_CENTRE_PX}px right of the column centre in ${density} density`,
    async ({ page }) => {
      await prepare(page);
      const column = await columnRect(page, MIXED);
      const header = await boxRect(page, MIXED, 'th.z-treecol .z-treecol-icon');
      expect(header.centre - column.centre, `header box centre ${header.centre}, column centre ${column.centre}`)
        .toBeCloseTo(HEADER_BOX_RIGHT_OF_CENTRE_PX, 0);
    });

  // In a tree of leaves only, the labelled header unit (box, 4px gap, label) is centred on the column (follow-up 59, NS31): the
  // box is inside the column, the gap to the label is 4px, the long label is cut with the ellipsis. The nested
  // `.mhf-tree-label-100` and `.mhf-tree-label-160` trees have a container row and therefore the pre-follow-up-54 header, so only these two guard it.
  for (const width of [100, 160]) {
    const root = `.mhf-tree-label-flat-${width}`;
    test(`${root}: the header unit of a centred ${width}px labelled Tree column of leaves is centred on the column in ${density} density`,
      async ({ page }) => {
        const found = await labelled(page, root);
        // The unit starts at the margin of the box, which is as wide as the gap to the label.
        const unitLeft = found.box.left - LABEL_BOX_GAP_PX;
        const unitCentre = (unitLeft + found.label.right) / 2;
        const columnCentre = (found.column.left + found.column.right) / 2;
        expect(found.box.left, `header box ${found.box.left}, column ${found.column.left}`).toBeGreaterThanOrEqual(found.column.left);
        expect(found.box.right, `header box ${found.box.right}, column ${found.column.right}`).toBeLessThanOrEqual(found.column.right);
        expect(Math.abs(found.label.left - found.box.right - LABEL_BOX_GAP_PX), `box ${found.box.right}, label ${found.label.left}`)
          .toBeLessThanOrEqual(TOLERANCE_PX);
        expect(found.textOverflow).toBe('ellipsis');
        expect(found.overflowing, 'the long label of the 100px column overflows and is cut with the ellipsis, the short one fits')
          .toBe(width === 100);
        if (width === 160) {
          expect(Math.abs(unitCentre - columnCentre), `unit centre ${unitCentre}, column centre ${columnCentre}`).toBeLessThanOrEqual(TOLERANCE_PX);
        } else {
          expect(Math.abs(unitLeft - found.column.left), `unit left ${unitLeft}, column ${found.column.left}`).toBeLessThanOrEqual(TOLERANCE_PX);
        }
      });
  }

  // A tree is judged on its own rows (follow-up 59, NS31): an outer tree of leaves whose cell holds a tree with a container row
  // keeps its centring, and the inner tree, which has a container row, keeps its own pre-follow-up-54 keyline.
  const OUTER = '.mhf-tree-outer-box';
  const INNER = '.mhf-tree-inner-box';
  test(`${OUTER}: a flat centred box column whose cell holds a tree with a container row keeps both boxes on the column centre in ${density} density`,
    async ({ page }) => {
      await prepare(page);
      const column = await columnRect(page, OUTER);
      const header = await boxRect(page, OUTER, 'th.z-treecol .z-treecol-icon');
      const row = await boxRect(page, OUTER, 'tr.z-treerow .z-treerow-icon');
      expect(Math.abs(header.centre - column.centre), `header box centre ${header.centre}, column centre ${column.centre}`).toBeLessThanOrEqual(TOLERANCE_PX);
      expect(Math.abs(row.centre - column.centre), `row box centre ${row.centre}, column centre ${column.centre}`).toBeLessThanOrEqual(TOLERANCE_PX);
    });

  test(`${INNER}: the tree inside keeps its own keyline, the leaf box on the container box and the header box ${HEADER_BOX_RIGHT_OF_CENTRE_PX}px right of the column centre in ${density} density`,
    async ({ page }) => {
      await prepare(page);
      const column = await columnRect(page, INNER);
      const header = await boxRect(page, INNER, 'th.z-treecol .z-treecol-icon');
      const boxes = await page.locator(`${INNER} tr.z-treerow .z-treerow-icon`).evaluateAll(
        icons => icons.map(icon => icon.getBoundingClientRect().left));
      expect(boxes, 'container, child and leaf rows').toHaveLength(3);
      expect(Math.abs(boxes[2] - boxes[0]), `leaf box ${boxes[2]}, container box ${boxes[0]}`).toBeLessThanOrEqual(TOLERANCE_PX);
      expect(header.centre - column.centre, `header box centre ${header.centre}, column centre ${column.centre}`)
        .toBeCloseTo(HEADER_BOX_RIGHT_OF_CENTRE_PX, 0);
    });
}

// A centred select-all-only column that carries the column-menu button keeps the box where the inline header span puts
// it, in the cell the button leaves: 16px from the column edge at 68px, and at 88px 20px on desktop and 16px on a tablet
// (the 44px button). Centring it on the whole cell, as a centred column without the button is, moved it 6px left
// (follow-up 52; the 68px tablet box then left the column). Offsets measured before follow-up 52.
function registerCentredMenuHeaderBox(density: 'default' | 'compact' | 'tablet') {
  const BOX_OFFSET_PX: Record<number, number> = density === 'tablet' ? { 68: 16, 88: 16 } : { 68: 16, 88: 20 };
  for (const width of [68, 88]) {
    const root = `.mhf-listbox-centre-menu-${width}`;
    test(`${root}: the header box of a centred select-all-only column with the menu button stays ${BOX_OFFSET_PX[width]}px from the column edge in ${density} density`,
      async ({ page }) => {
        await open(page);
        if (density === 'compact') await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
        await page.evaluate(() => document.fonts.ready.then(() => true));
        const offset = await page.locator(`${root} th.z-listheader`).first().evaluate((th) =>
          th.querySelector('.z-listheader-icon')!.getBoundingClientRect().left - th.getBoundingClientRect().left);
        expect(offset, `header box ${offset}px from the column edge`).toBeCloseTo(BOX_OFFSET_PX[width], 0);
      });
  }
}

// A checkmark row is as tall as the row without the box, and the painted box is centred on the label and on a control
// in the first cell. Follow-up 28: `vertical-align: middle` (desktop) and the 24px touch box (tablet) grew the Listbox
// line box; asymmetric margins that undid it only fitted Inter 13px/20px (NS13 F1).
function registerCheckmarkRowsMatchGrid(density: 'default' | 'compact' | 'tablet') {
  for (const [scaleName, scaleCss] of Object.entries(TYPE_SCALES)) {
    const inDensity = scaleName === 'default' ? `${density} density` : `${density} density with the ${scaleName} type scale`;
    const prepare = async (page: Page) => {
      await open(page);
      if (scaleCss) await page.addStyleTag({ content: scaleCss });
      if (density === 'compact') await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
      await page.evaluate(() => document.fonts.ready.then(() => true));
    };

    for (const { name, root, row, icon, text, gridRow } of CHECKMARK_ROWS) {
      test(`${root}: the checkmark ${name} row is as tall as the grid row in ${inDensity}`, async ({ page }) => {
        await prepare(page);
        const checkmark = await boxHeight(page, `${root} ${row}`);
        const expected = await boxHeight(page, gridRow);
        expect(Math.abs(checkmark - expected), `checkmark row ${checkmark}, grid row ${expected}`).toBeLessThanOrEqual(SUB_PIXEL_PX);
      });

      test(`${root}: the painted box of the checkmark ${name} row is centred on its label in ${inDensity}`, async ({ page }) => {
        await prepare(page);
        await expectBoxCentredOn(page, page.locator(`${root} ${row}`).first(), icon, { text }, 'label');
      });
    }

    // M1: a taller control in the first cell (textbox, button) takes the row height, and the box stays centred on it.
    for (const { name, controlRoot, controlGridRoot, icon } of CONTROL_ROWS) {
      for (const control of ['textbox', 'button']) {
        const rowSelector = `${controlRoot} .mhf-${control}-row`;
        const controlSelector = control === 'textbox' ? '.z-textbox' : '.z-button';
        test(`${controlRoot}: the painted box of the checkmark ${name} row is centred on a ${control} in the first cell in ${inDensity}`,
          async ({ page }) => {
            await prepare(page);
            await expectBoxCentredOn(page, page.locator(rowSelector).first(), icon, { text: controlSelector, control: true }, control);
          });

        if (name === 'listbox') {
          test(`${controlRoot}: the checkmark listbox row with a ${control} is as tall as the grid row in ${inDensity}`, async ({ page }) => {
            await prepare(page);
            const listbox = await boxHeight(page, `${controlRoot} .mhf-${control}-row`);
            const grid = await boxHeight(page, `${controlGridRoot} .mhf-${control}-row`);
            expect(Math.abs(listbox - grid), `listbox row ${listbox}, grid row ${grid}`).toBeLessThanOrEqual(SUB_PIXEL_PX);
          });
        }
      }
    }

    registerCheckmarkFirstCell(prepare, inDensity);
  }
}

// On a tablet the row box keeps the 24px touch height, although it no longer stretches the row.
function registerCheckmarkBoxKeepsTouchHeight() {
  for (const { name, root, row, checkable } of CHECKMARK_ROWS) {
    test(`${root}: the checkmark ${name} row box keeps the ${TOUCH_BOX_PX}px touch height`, async ({ page }) => {
      await open(page);
      const height = await boxHeight(page, `${root} ${row} ${checkable}`);
      expect(height).toBe(TOUCH_BOX_PX);
    });
  }
}

type Density = 'default' | 'compact' | 'tablet';

async function openInDensity(page: Page, density: Density) {
  await open(page);
  if (density === 'compact') await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
}

// A Tree row is as tall as the Grid row with the same content: the Tree pads its cell content as the Grid does and has no
// row-height floor, so a textbox or a button in the first cell is not shorter than in the Grid (follow-ups 34 and 38).
function registerTreeRowsMatchGrid(density: Density) {
  const cases = [
    { name: 'text', tree: '.mhf-tree-vs-grid tr.z-treerow', grid: '.mhf-grid-vs-tree tr.z-row' },
    { name: 'textbox', tree: '.mhf-tree-plain-control-row .mhf-textbox-row', grid: '.mhf-grid-control-row .mhf-textbox-row' },
    { name: 'button', tree: '.mhf-tree-plain-control-row .mhf-button-row', grid: '.mhf-grid-control-row .mhf-button-row' },
  ];
  for (const { name, tree, grid } of cases) {
    test(`${tree}: a tree row with ${name} is as tall as the grid row in ${density} density`, async ({ page }) => {
      await openInDensity(page, density);
      const treeHeight = await boxHeight(page, tree);
      const gridHeight = await boxHeight(page, grid);
      expect(Math.abs(treeHeight - gridHeight), `tree row ${treeHeight}, grid row ${gridHeight}`).toBeLessThanOrEqual(SUB_PIXEL_PX);
    });
  }
}

// The Grid group row has a border-top that the Listbox group row lacks; the 0.5px it adds is accepted.
const GROUP_ROW_RESIDUAL_PX = 0.5;

// A Listbox group row pads once, as the Grid group row does: it is as tall as the Grid group row and its toggle icon
// starts where the Grid group's toggle starts, and in default density where the text of a data row starts (follow-up 40).
function registerListboxGroupRows(density: Density) {
  test(`.mhf-listbox-group: the listbox group row is as tall as the grid group row in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    const listbox = await boxHeight(page, '.mhf-listbox-group tr.z-listgroup');
    const grid = await boxHeight(page, '.mhf-grid-group tr.z-group');
    expect(Math.abs(listbox - grid), `listbox group ${listbox}, grid group ${grid}`).toBeLessThanOrEqual(GROUP_ROW_RESIDUAL_PX + SUB_PIXEL_PX);
  });

  test(`.mhf-listbox-group: the group toggle icon starts where the grid group toggle starts in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    const left = (selector: string) => page.locator(selector).first().evaluate((el) => el.getBoundingClientRect().left);
    const listbox = await left('.mhf-listbox-group tr.z-listgroup span.z-listgroup-icon');
    const grid = await left('.mhf-grid-group tr.z-group .z-group-icon');
    expect(Math.abs(listbox - grid), `listbox group icon x ${listbox}, grid group icon x ${grid}`).toBeLessThanOrEqual(TOLERANCE_PX);
  });

  test(`.mhf-listbox-group-check: the toggle arrow of a plain group is under the row checkbox in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    const left = (selector: string) => page.locator(selector).first().evaluate((el) => el.getBoundingClientRect().left);
    const arrow = await left('.mhf-listbox-group-check tr.z-listgroup span.z-listgroup-icon');
    const box = await left('.mhf-listbox-group-check tr.z-listitem i.z-icon-check');
    expect(Math.abs(arrow - box), `group arrow x ${arrow}, row box x ${box}`).toBeLessThanOrEqual(TOLERANCE_PX);
  });

  // A group row pads like a data row in default and compact density (follow-up 46); a tablet keeps the fixed 16px
  // sideways against the 20px of its data cells, so the keyline is shared by the first two only.
  if (density === 'tablet') return;
  test(`.mhf-listbox-group: the group toggle icon starts on the keyline of the row text in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    const icon = await page.locator('.mhf-listbox-group tr.z-listgroup span.z-listgroup-icon').first()
      .evaluate((el) => el.getBoundingClientRect().left);
    const rowText = await textLeft(page, '.mhf-listbox-group tr.z-listitem .z-listcell-content');
    expect(Math.abs(icon - rowText), `group icon x ${icon}, row text x ${rowText}`).toBeLessThanOrEqual(TOLERANCE_PX);
  });

  // Follow-up 46 F2: a checkmark Listbox nested in a later cell is no reason to keep the 16px of a checkmark group.
  test(`.mhf-listbox-group-nested: the group toggle icon of a plain Listbox starts on the keyline of the row text in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    const icon = await page.locator('.mhf-listbox-group-nested tr.z-listgroup span.z-listgroup-icon').first()
      .evaluate((el) => el.getBoundingClientRect().left);
    const rowText = await textLeft(page, '.mhf-listbox-group-nested tr.z-listitem .z-listcell-content');
    expect(Math.abs(icon - rowText), `group icon x ${icon}, row text x ${rowText}`).toBeLessThanOrEqual(TOLERANCE_PX);
  });

  test(`.mhf-grid-group: the group toggle icon starts on the keyline of the row text in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    const icon = await page.locator('.mhf-grid-group tr.z-group .z-group-icon').first()
      .evaluate((el) => el.getBoundingClientRect().left);
    const rowText = await textLeft(page, '.mhf-grid-group tr.z-row .z-row-content');
    expect(Math.abs(icon - rowText), `group icon x ${icon}, row text x ${rowText}`).toBeLessThanOrEqual(TOLERANCE_PX);
  });

  // Compact only: in default density a group row keeps its 8px 16px against the 16px of a data cell (follow-up 46).
  if (density !== 'compact') return;
  test('.mhf-group: the group row pads like a data row in Grid and Listbox in compact density', async ({ page }) => {
    await openInDensity(page, density);
    const padding = (selector: string) => page.locator(selector).first().evaluate((el) => {
      const style = getComputedStyle(el);
      return [style.paddingTop, style.paddingRight, style.paddingBottom, style.paddingLeft].join(' ');
    });
    expect(await padding('.mhf-grid-group td.z-group-inner'), 'grid group padding').toBe(await padding('.mhf-grid-group tr.z-row .z-row-content'));
    expect(await padding('.mhf-listbox-group tr.z-listgroup td'), 'listbox group padding').toBe(await padding('.mhf-listbox-group tr.z-listitem .z-listcell-content'));
  });
}

// Follow-up 46 F1: a tablet keeps the group rows of a tablet in every density, as it does the data rows (zkmax
// `tablet/_mesh.css` sets the cell padding on `.z-grid` and `.z-listbox`, which shadows the compact tokens).
function registerTabletGroupRowsIgnoreCompact() {
  const cases = [
    { name: 'grid', row: '.mhf-grid-group tr.z-group', icon: '.mhf-grid-group tr.z-group .z-group-icon' },
    { name: 'listbox', row: '.mhf-listbox-group tr.z-listgroup', icon: '.mhf-listbox-group tr.z-listgroup span.z-listgroup-icon' },
  ];
  for (const { name, row, icon } of cases) {
    test(`.mhf-${name}-group: the group row and its toggle icon are the same in compact density as in default density on a tablet`, async ({ page }) => {
      await open(page);
      const measure = async () => ({
        height: await boxHeight(page, row),
        iconLeft: await page.locator(icon).first().evaluate((el) => el.getBoundingClientRect().left),
      });
      const tablet = await measure();
      await page.evaluate(() => document.documentElement.setAttribute('data-density', 'compact'));
      expect(await measure()).toEqual(tablet);
    });
  }
}

const GROUP_SELECT = '.mhf-listbox-group-select';
const GROUP_CHECKBOX = `${GROUP_SELECT} tr.z-listgroup:not(.z-listgroup-disabled) i.z-listgroup-icon.z-icon-check`;
const ITEM_CHECKBOX = `${GROUP_SELECT} tr.z-listitem i.z-listitem-icon.z-icon-check`;
const CHECKBOX_BOX_PROPERTIES = [
  'width', 'height', 'box-sizing', 'border-top-width', 'border-top-style', 'border-top-color', 'border-top-left-radius',
  'background-color', 'background-image', 'background-size', 'mask-image',
];

// The box a checkbox pseudo-element paints, as computed style: the group checkbox must read the same values as the row
// checkbox in the same Listbox.
function checkboxBox(page: Page, selector: string): Promise<Record<string, string>> {
  return page.locator(selector).first().evaluate((icon, properties) => {
    const style = getComputedStyle(icon, '::before');
    return Object.fromEntries(properties.map((property) => [property, style.getPropertyValue(property)]));
  }, CHECKBOX_BOX_PROPERTIES);
}

// A selectable group (groupSelect) paints the checkbox of the row checkbox, in every state, and keeps the toggle arrow
// as it was (follow-up 41). The icon of the group checkbox also carries `z-listgroup-icon`, as the arrow's span does.
function registerGroupCheckbox(density: Density) {
  test(`${GROUP_SELECT}: the unchecked group checkbox paints the box of the row checkbox in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    expect(await checkboxBox(page, GROUP_CHECKBOX)).toEqual(await checkboxBox(page, ITEM_CHECKBOX));
  });

  test(`${GROUP_SELECT}: the hovered group checkbox paints the box of the hovered row checkbox in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    await page.locator(`${GROUP_SELECT} tr.z-listgroup`).first().hover();
    const group = await checkboxBox(page, GROUP_CHECKBOX);
    await page.locator(`${GROUP_SELECT} tr.z-listitem`).first().hover();
    expect(group).toEqual(await checkboxBox(page, ITEM_CHECKBOX));
  });

  test(`${GROUP_SELECT}: the checked group checkbox paints the box of the checked row checkbox in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    await page.locator(`${GROUP_SELECT} tr.z-listgroup span.z-listgroup-checkable`).first().click();
    await expect(page.locator(`${GROUP_SELECT} tr.z-listgroup-selected`)).toHaveCount(1);
    await page.locator(`${GROUP_SELECT} tr.z-listitem span.z-listitem-checkable`).first().click();
    await expect(page.locator(`${GROUP_SELECT} tr.z-listitem-selected`)).toHaveCount(1);
    await page.mouse.move(0, 0);
    const group = await checkboxBox(page, GROUP_CHECKBOX);
    expect(group['background-image'], 'the checked group checkbox paints its tick').toContain('check-white');
    expect(group).toEqual(await checkboxBox(page, ITEM_CHECKBOX));
  });

  test(`${GROUP_SELECT}: the group checkbox is under the row checkbox and centred on the group label in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    await page.evaluate(() => document.fonts.ready.then(() => true));
    const left = (selector: string) => page.locator(selector).first().evaluate((icon) => icon.getBoundingClientRect().left);
    const groupLeft = await left(GROUP_CHECKBOX);
    const itemLeft = await left(ITEM_CHECKBOX);
    expect(Math.abs(groupLeft - itemLeft), `group box x ${groupLeft}, row box x ${itemLeft}`).toBeLessThanOrEqual(TOLERANCE_PX);
    const size = (selector: string) => page.locator(selector).first().evaluate((icon) => {
      const rect = icon.getBoundingClientRect();
      return `${rect.width}x${rect.height}`;
    });
    expect(await size(GROUP_CHECKBOX), 'icon box of the group checkbox').toBe(await size(ITEM_CHECKBOX));
    await expectBoxCentredOn(page, page.locator(`${GROUP_SELECT} tr.z-listgroup`).first(), 'i.z-listgroup-icon.z-icon-check',
      { text: '.z-listcell-content' }, 'group label');
  });

  test(`${GROUP_SELECT}: the checkbox of a disabled group keeps its border colour on hover in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    const box = `${GROUP_SELECT} tr.z-listgroup-disabled i.z-listgroup-icon.z-icon-check`;
    const border = () => page.locator(box).evaluate((icon) => getComputedStyle(icon, '::before').borderTopColor);
    await page.locator(box).scrollIntoViewIfNeeded();
    await page.mouse.move(0, 0);
    const atRest = await border();
    const { x, y } = await page.locator(box).evaluate((icon) => {
      const rect = icon.getBoundingClientRect();
      return { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 };
    });
    await page.mouse.move(x, y);
    expect(await page.locator(`${GROUP_SELECT} tr.z-listgroup-disabled`).evaluate((row) => row.matches(':hover')),
      'the pointer is over the disabled group row').toBe(true);
    expect(await border(), `border at rest ${atRest}`).toBe(atRest);
  });

  test(`${GROUP_SELECT}: the disabled group keeps the checkbox box and dims it like a disabled row in ${density} density`, async ({ page }) => {
    await openInDensity(page, density);
    const disabled = `${GROUP_SELECT} tr.z-listgroup-disabled`;
    expect(await checkboxBox(page, `${disabled} i.z-listgroup-icon.z-icon-check`)).toEqual(await checkboxBox(page, ITEM_CHECKBOX));
    const opacity = await page.locator(`${disabled} span.z-listgroup-checkable`)
      .evaluate((span) => ({
        painted: Number(getComputedStyle(span).opacity),
        token: Number(getComputedStyle(document.documentElement).getPropertyValue('--zk-state-disabled-opacity')),
      }));
    expect(opacity.painted, 'opacity of the disabled group checkbox').toBe(opacity.token);
  });
}

// zkmax `tablet/_mesh.css` sizes the toggle arrow through `.z-listgroup-icon`, a class the group checkbox's icon also
// carries. The checkbox icon is left out of that rule; this is a non-regression guard that the arrow keeps its size with
// and without groupSelect, so it passes before and after the change.
function registerGroupArrowIgnoresGroupSelect() {
  test(`${GROUP_SELECT}: the toggle arrow is the size of the arrow of a group without groupSelect`, async ({ page }) => {
    await open(page);
    const size = (selector: string) => page.locator(selector).first().evaluate((arrow) => {
      const rect = arrow.getBoundingClientRect();
      return `${rect.width}x${rect.height}`;
    });
    expect(await size(`${GROUP_SELECT} tr.z-listgroup span.z-listgroup-icon`))
      .toBe(await size('.mhf-listbox-group tr.z-listgroup span.z-listgroup-icon'));
  });
}

// The documented minimum width of an image-only header column with the menu button (contracts/listbox.md): the
// whole 16px image shows and ends before the button.
function registerImageOnlyMenuHeaderAtMinimumWidth(column: number) {
  const cases = [
    { root: '.mhf-grid-icon-menu', header: 'th.z-column', button: '.z-column-button' },
    { root: '.mhf-listbox-icon-menu', header: 'th.z-listheader', button: '.z-listheader-button' },
  ];
  for (const { root, header, button } of cases) {
    test(`${root}: an image-only column of the minimum width shows the whole image clear of the menu button`, async ({ page }) => {
      await open(page);
      const th = `${root} ${header}:nth-child(${column})`;
      const image = await page.locator(`${th} img`).evaluate((img) => {
        const rect = img.getBoundingClientRect();
        return { width: rect.width, left: rect.left, right: rect.right };
      });
      const buttonLeft = await page.locator(`${th} ${button}`).evaluate((el) => el.getBoundingClientRect().left);
      const headerLeft = await page.locator(th).evaluate((el) => el.getBoundingClientRect().left);
      expect.soft(image.width, 'image width').toBe(ICON_IMAGE_PX);
      expect.soft(image.left - headerLeft, 'image left of the header edge').toBeGreaterThanOrEqual(0);
      expect(buttonLeft - image.right, `image right ${image.right}, button left ${buttonLeft}`).toBeGreaterThanOrEqual(0);
    });
  }
}

// The documented minimum width of a select-all-only header with the menu button (contracts/listbox.md): the whole
// 16px box lies inside the header cell and inside the content box, whose `overflow: hidden` paints nothing beyond
// its right edge, and ends before the button.
function registerSelectAllOnlyMenuHeaderAtMinimumWidth(root: string) {
  test(`${root}: a select-all-only column of the minimum width shows the whole box clear of the menu button`, async ({ page }) => {
    await open(page);
    const measured = await page.locator(`${root} th.z-listheader:nth-child(1)`).evaluate((th) => {
      const icon = th.querySelector('.z-listheader-icon')!.getBoundingClientRect();
      const cell = th.getBoundingClientRect();
      const content = th.querySelector('.z-listheader-content')!;
      return {
        iconLeft: icon.left, iconRight: icon.right, iconWidth: icon.width, cellLeft: cell.left, cellRight: cell.right,
        buttonLeft: th.querySelector('.z-listheader-button')!.getBoundingClientRect().left,
        contentWidth: content.clientWidth, contentRight: content.getBoundingClientRect().right,
      };
    });
    expect.soft(measured.contentWidth, 'content box width').toBeGreaterThanOrEqual(measured.iconWidth);
    expect.soft(measured.iconLeft >= measured.cellLeft && measured.iconRight <= measured.cellRight,
      `icon ${measured.iconLeft}-${measured.iconRight}, cell ${measured.cellLeft}-${measured.cellRight}`).toBe(true);
    expect.soft(measured.iconRight, `icon right ${measured.iconRight}, content right ${measured.contentRight}`)
      .toBeLessThanOrEqual(measured.contentRight);
    expect(measured.buttonLeft - measured.iconRight, `icon right ${measured.iconRight}, button left ${measured.buttonLeft}`)
      .toBeGreaterThanOrEqual(0);
  });
}

// An image + sort + menu header (`GRID_IMAGE_MENU`, `LISTBOX_IMAGE_MENU`): the label box, the sort arrow and the
// menu button keep that order and the 16px image is not squeezed.
function registerImageSortMenuHeaderOrder(meshes: Mesh[]) {
  for (const mesh of meshes) {
    test(`${mesh.root}: the label box ends before the arrow, the arrow before the menu button, and the image is whole`, async ({ page }) => {
      await open(page);
      const header = sortedHeader(mesh);
      const left = (selector: string) => page.locator(`${header} ${selector}`).evaluate((el) => el.getBoundingClientRect().left);
      const right = (selector: string) => page.locator(`${header} ${selector}`).evaluate((el) => el.getBoundingClientRect().right);
      const contentRight = (selector: string) => page.locator(`${header} ${selector}`).evaluate(contentBoxRight);
      const button = mesh === GRID_IMAGE_MENU ? '.z-column-button' : '.z-listheader-button';
      const labelRight = await contentRight(mesh.headerContent);
      const arrowLeft = await left(mesh.sortIcon);
      const arrowRight = await right(mesh.sortIcon);
      const buttonLeft = await left(button);
      const imageWidth = await page.locator(`${header} img`).evaluate((img) => img.getBoundingClientRect().width);
      expect.soft(arrowLeft - labelRight, `label box right ${labelRight}, arrow left ${arrowLeft}`).toBeGreaterThanOrEqual(MIN_LABEL_ARROW_GAP_PX);
      expect.soft(buttonLeft - arrowRight, `arrow right ${arrowRight}, button left ${buttonLeft}`).toBeGreaterThanOrEqual(MIN_LABEL_ARROW_GAP_PX);
      expect(imageWidth).toBe(ICON_IMAGE_PX);
    });
  }
}

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
      const labelBoxRight = await page.locator(`${header} ${mesh.headerContent}`).evaluate(contentBoxRight);
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
        contentHeight: content.clientHeight - parseFloat(getComputedStyle(content).paddingTop)
          - parseFloat(getComputedStyle(content).paddingBottom),
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
  registerHeaderControlStaysUsableAndRingWhole();
  registerTreeHeaderMatchesGrid();
  registerCheckmarkRowsMatchGrid('default');
  registerCheckmarkRowsMatchGrid('compact');
  for (const density of ['default', 'compact'] as const) {
    registerTreeRowsMatchGrid(density);
    registerListboxGroupRows(density);
    registerGroupCheckbox(density);
  }
  registerCheckmarkBoxInAlignedFirstColumn();
  registerCheckmarkBoxColumns('default');
  registerTreeCheckmarkBoxColumns('default');
  registerTreeCellStyleWithoutSpace();
  registerCheckmarkBoxColumns('compact');
  registerTreeCheckmarkBoxColumns('compact');
  registerCentredMenuHeaderBox('default');
  registerCentredMenuHeaderBox('compact');
  registerImageOnlyMenuHeaderAtMinimumWidth(MIN_IMAGE_COLUMN_DESKTOP);
  registerSelectAllOnlyMenuHeaderAtMinimumWidth('.mhf-listbox-select-all-only-menu-desktop');
  registerImageSortMenuHeaderOrder([GRID_IMAGE_MENU, LISTBOX_IMAGE_MENU]);

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
    registerCheckmarkRowsMatchGrid('tablet');
    registerTreeRowsMatchGrid('tablet');
    registerListboxGroupRows('tablet');
    registerTabletGroupRowsIgnoreCompact();
    registerGroupCheckbox('tablet');
    registerGroupArrowIgnoresGroupSelect();
    registerCheckmarkBoxInAlignedFirstColumn();
    registerCheckmarkBoxColumns('tablet');
    registerTreeCheckmarkBoxColumns('tablet');
    registerCentredMenuHeaderBox('tablet');
    registerCheckmarkBoxKeepsTouchHeight();
    registerImageOnlyMenuHeaderAtMinimumWidth(MIN_IMAGE_COLUMN_TABLET);
    registerSelectAllOnlyMenuHeaderAtMinimumWidth('.mhf-listbox-select-all-only-menu-tablet');
    registerImageSortMenuHeaderOrder([GRID_IMAGE_MENU, LISTBOX_IMAGE_MENU]);

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

// Frozen columns (ZK-4212, ZK-3864): the right frozen cells are `position: sticky` and the cells that stay
// in place cover the cells scrolled behind them. Rows built with <cell> render TD.z-cell, which grid.css
// positions for ZK-3864 and which must still be sticky.
const FROZEN_FIXTURE = '/pv/frozen-cells-guard.zul';
const FROZEN_WHEEL_DELTA_X = 40;
const FROZEN_ALIGN_PX = 1;
const FROZEN_MESHES = [
  { root: '.fz-grid-both', header: 'th.z-column', body: 'tbody tr.z-row:first-child td' },
  { root: '.fz-grid-right', header: 'th.z-column', body: 'tbody tr.z-row:first-child td' },
  { root: '.fz-listbox', header: 'th.z-listheader', body: 'tbody tr.z-listitem:first-child td' },
  { root: '.fz-tree', header: 'th.z-treecol', body: 'tbody tr.z-treerow:first-child td' },
];

async function openFrozenFixture(page: Page) {
  const resp = await page.goto(FROZEN_FIXTURE, { waitUntil: 'networkidle' });
  expect(resp?.status()).toBe(200);
  await page.evaluate(() => document.fonts.ready.then(() => true));
}

// A real wheel over the frozen scroll tray moves ZK's frozen scrollbar.
async function wheelFrozenTray(page: Page, root: string) {
  const tray = page.locator(`${root} .z-frozen-inner`);
  await tray.scrollIntoViewIfNeeded();
  const box = (await tray.boundingBox())!;
  await page.mouse.move(box.x + box.width / 2, box.y + box.height / 2);
  await page.mouse.wheel(FROZEN_WHEEL_DELTA_X, 0);
  await expect.poll(() => tray.evaluate((el) => el.scrollLeft)).toBeGreaterThan(0);
}

// What a cell paints: its own colour, and the colour of a one-colour gradient layer on top of it (the tree bar's
// outline-variant strip), composited over the backgrounds of the ancestors up to the first opaque one.
// `expectedOver` is a custom property that must lie over the ancestors instead; null means the ancestors alone.
interface Paint { painted: string; expected: string; ownAlpha: number }

function paintOfStickyCell(page: Page, selector: string, expected: { token: string | null; over: string | null }): Promise<Paint> {
  return page.locator(selector).first().evaluate((cell, { token, over }) => {
    type Rgba = [number, number, number, number];
    const parse = (css: string): Rgba => {
      const m = css.match(/rgba?\(([^)]+)\)/)!;
      const [r, g, b, a = 1] = m[1].split(/[ ,/]+/).filter(Boolean).map(Number);
      return [r, g, b, a];
    };
    const over_ = (top: Rgba, bottom: Rgba): Rgba => {
      const a = top[3] + bottom[3] * (1 - top[3]);
      if (a === 0) return [0, 0, 0, 0];
      const mix = (i: number) => (top[i] * top[3] + bottom[i] * bottom[3] * (1 - top[3])) / a;
      return [mix(0), mix(1), mix(2), a];
    };
    const tokenColour = (name: string): Rgba => {
      const probe = document.createElement('div');
      probe.style.backgroundColor = `var(${name})`;
      document.body.appendChild(probe);
      const colour = parse(getComputedStyle(probe).backgroundColor);
      probe.remove();
      return colour;
    };
    const behind = (el: Element | null): Rgba => {
      let colour: Rgba = [0, 0, 0, 0];
      for (; el && colour[3] < 1; el = el.parentElement) colour = over_(colour, parse(getComputedStyle(el).backgroundColor));
      return colour;
    };
    const style = getComputedStyle(cell);
    const gradient = style.backgroundImage.match(/linear-gradient\((rgba?\([^)]+\))/);
    const own = parse(style.backgroundColor);
    const painted = over_(gradient ? parse(gradient[1]) : [0, 0, 0, 0], over_(own, behind(cell.parentElement)));
    const ground = token ? tokenColour(token) : behind(cell.parentElement);
    const expectedColour = over ? over_(tokenColour(over), ground) : ground;
    const text = (c: Rgba) => c.map((v) => Math.round(v)).join(',');
    return { painted: text(painted), expected: text(expectedColour), ownAlpha: own[3] };
  }, expected);
}

// Cells and bars that stay in place, with the colour each must show: the row behind them for the header,
// the footer row and the auxhead band; the filler of the columns row is the one tinted bar.
const FROZEN_STICKY_CELLS = [
  { root: '.fz-grid-both', selector: 'th.z-column.z-frozen-right-col', token: null, over: null },
  { root: '.fz-grid-both', selector: 'th.z-auxheader.z-frozen-right-col', token: null, over: null },
  { root: '.fz-grid-both', selector: 'td.z-footer.z-frozen-right-col', token: null, over: null },
  { root: '.fz-grid-both', selector: 'td.z-footer:first-child', token: null, over: null },
  { root: '.fz-grid-both', selector: 'th.z-auxhead-bar.z-frozen-right-col', token: null, over: null },
  { root: '.fz-grid-both', selector: 'th.z-columns-bar.z-frozen-right-col', token: '--zk-color-surface-container', over: null },
  { root: '.fz-grid-both', selector: 'td.z-foot-bar.z-frozen-right-col', token: null, over: null },
  { root: '.fz-grid-right', selector: 'th.z-column.z-frozen-right-col', token: null, over: null },
  { root: '.fz-listbox', selector: 'th.z-listheader.z-frozen-right-col', token: null, over: null },
  { root: '.fz-listbox', selector: 'td.z-listfooter.z-frozen-right-col', token: null, over: null },
  { root: '.fz-listbox', selector: 'td.z-listfooter:first-child', token: null, over: null },
  { root: '.fz-listbox', selector: 'th.z-listhead-bar.z-frozen-right-col', token: null, over: null },
  { root: '.fz-listbox', selector: 'td.z-listfoot-bar.z-frozen-right-col', token: null, over: null },
  { root: '.fz-tree', selector: 'th.z-treecol.z-frozen-right-col', token: null, over: null },
  { root: '.fz-tree', selector: 'td.z-treefooter.z-frozen-right-col', token: null, over: null },
  { root: '.fz-tree', selector: 'td.z-treefooter:first-child', token: null, over: null },
  { root: '.fz-tree', selector: 'th.z-treecols-bar.z-frozen-right-col', token: null, over: '--zk-color-outline-variant' },
  { root: '.fz-tree', selector: 'td.z-treefoot-bar.z-frozen-right-col', token: null, over: null },
];

test.describe('frozen columns', () => {
  // The scrollbar-gutter filler cells are 0px wide under Playwright's default --hide-scrollbars, and
  // `test.use({ launchOptions })` cannot be scoped to a describe, so these tests launch their own browser.
  let browser: Browser;
  let context: BrowserContext;
  let page: Page;

  test.beforeAll(async () => {
    browser = await chromium.launch({ ignoreDefaultArgs: ['--hide-scrollbars'] });
  });
  test.afterAll(() => browser.close());
  test.beforeEach(async ({ baseURL }) => {
    context = await browser.newContext({ baseURL });
    page = await context.newPage();
  });
  test.afterEach(() => context.close());

  for (const mesh of FROZEN_MESHES) {
    test(`${mesh.root}: the right frozen body cell stays under its header cell after a wheel scroll`, async () => {
      await openFrozenFixture(page);
      await wheelFrozenTray(page, mesh.root);
      const left = (selector: string) => page.locator(`${mesh.root} ${selector}.z-frozen-right-col`).first()
        .evaluate((el) => el.getBoundingClientRect().left);
      const header = await left(mesh.header);
      const body = await left(mesh.body);
      expect(Math.abs(header - body), `header x ${header}, body x ${body}`).toBeLessThanOrEqual(FROZEN_ALIGN_PX);
    });
  }

  for (const { root, selector, token, over } of FROZEN_STICKY_CELLS) {
    test(`${root} ${selector}: paints the opaque colour of the row behind it`, async () => {
      await openFrozenFixture(page);
      await wheelFrozenTray(page, root);
      const width = await page.locator(`${root} ${selector}`).first().evaluate((el) => el.getBoundingClientRect().width);
      expect(width, 'the cell must take width, else a filler cell paints nothing and the test is vacuous').toBeGreaterThan(0);
      const paint = await paintOfStickyCell(page, `${root} ${selector}`, { token, over });
      expect(paint.ownAlpha, 'own background-color must be opaque so scrolled cells do not show through').toBe(1);
      expect(paint.painted).toBe(paint.expected);
    });
  }

  test('.fz-grid-both: the left frozen <cell> body cell stays under its header cell after a wheel scroll', async () => {
    await openFrozenFixture(page);
    await wheelFrozenTray(page, '.fz-grid-both');
    const left = (selector: string) => page.locator(`.fz-grid-both ${selector}`).first()
      .evaluate((el) => el.getBoundingClientRect().left);
    const header = await left('th.z-column.z-frozen-col');
    const body = await left('tbody tr.z-row:first-child td.z-cell');
    expect(Math.abs(header - body), `header x ${header}, body x ${body}`).toBeLessThanOrEqual(FROZEN_ALIGN_PX);
  });
});
