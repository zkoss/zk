// Per-worker collector of Chromium CSS rule usage, one JSON file per test.
//
// Rule usage is read through CDP directly, not page.coverage: page.coverage keeps the stylesheet texts of earlier
// documents (resetOnNavigation:false) but loses their usage, and it reports only used ranges.
//
// The collector must not change what a test sees, so while the test body runs it sends nothing to the page except what
// CDP needs to follow it: the CSS/DOM/Page agents stay enabled and CSS.getStyleSheetText is called once for each
// stylesheet that is added. There is no timer and no polling (a 100 ms poll shifted popup windows by a pixel and made
// screenshot tests flaky). Tracking is started once per page; usage is read with CSS.takeCoverageDelta (rules that became used since the previous call) only at moments
// the test is not looking:
//   - before every Page.goto/reload/setContent/goBack/goForward, while the old document is certainly alive;
//   - before Page.close, BrowserContext.close, Browser.close and at test end, after forcing a style update, because
//     takeCoverageDelta does not and a rule that first matched in the last frame would be missed;
// A navigation the page starts itself (location.href=, a link click, a form submit) therefore LOSES the usage of the
// earlier document (a read on its navigation request never wins the race against the commit of the next document), and
// a page that closes itself (window.close()) cannot be read at all. Both are detected and written to the per-test JSON
// (scriptNavigations, selfClosedPages) so the report flags those tests instead of hiding the gap.
// No style update is forced before a navigation: the next document is then what the test sees, and a forced style
// recalculation could start transitions or fire their events in the old one. A rule that first matches in the last frame
// before a patched navigation can be missed.
//
// A coverage run must leave the tree alone: Page/Locator.screenshot lose their `path` option, because
// forced-colors-gallery.spec.ts rewrites doc/screenshots/*-forced-colors.png through it (--ignore-snapshots does not
// stop that). The screenshot is still taken and returned, so no coverage is lost.
import * as crypto from 'node:crypto';
import * as fs from 'node:fs';
import * as path from 'node:path';
import type { Browser, BrowserContext, CDPSession, Page, TestInfo } from 'playwright/test';

export const COVERAGE_ENABLED = process.env.CSS_COVERAGE === '1';

// The IceBlue bundle is served as /_zkiju-iceblue/zul/css/zk.wcs and its other sheets under /iceblue/.
const ICEBLUE_URL = /\/_zkiju-iceblue\/|\/iceblue\//;

// __dirname is src/test/playwright/css-coverage; Playwright compiles specs to CommonJS here.
const OUT_DIR = process.env.CSS_COVERAGE_DIR ?? path.resolve(__dirname, '../../../../test-results/css-coverage');
const SHEET_DIR = path.join(OUT_DIR, 'sheets');

type Theme = 'marble' | 'iceblue';

/** The used rules of one stylesheet content, as [startOffset, endOffset] pairs keyed by "start-end". */
interface SheetUsage {
  hash: string;
  theme: Theme;
  url: string;
  used: Map<string, [number, number]>;
}

/** Everything one test touched, merged across all its pages. */
interface TestRecord {
  project: string;
  title: string;
  file: string;
  sheets: Map<string, SheetUsage>; // "<theme>:<content hash>" -> usage; one content can be seen by both themes
  pages: number;
  inlineSheets: number;
  scriptNavigations: number; // main-frame navigations the page started itself, after a page with stylesheets (usage lost)
  selfClosedPages: number; // pages that closed before the collector could read their last usage
}

/** A stylesheet seen on a page: its text is fetched when it is added, since it may be gone later. */
interface SeenSheet {
  url: string;
  text: Promise<string | null>;
  document: number; // index of the main-frame document that loaded it, which decides its theme
}

let currentTest: TestRecord | null = null;
const pageCoverages = new WeakMap<Page, PageCoverage>();
const instrumented = Symbol('cssCoverageInstrumented');

export function beginTest(testInfo: TestInfo): TestRecord {
  currentTest = {
    project: testInfo.project.name,
    title: testInfo.titlePath.slice(1).join(' > '),
    file: path.basename(testInfo.file),
    sheets: new Map(),
    pages: 0,
    inlineSheets: 0,
    scriptNavigations: 0,
    selfClosedPages: 0,
  };
  return currentTest;
}

export async function endTest(record: TestRecord, testInfo: TestInfo): Promise<void> {
  await flushAllOpenPages();
  await Promise.all(selfClosedFlushes);
  currentTest = null;
  writeTestFile(record, testInfo);
}

/** Patches the Browser class once; contexts, pages and closes are patched lazily from the instances it creates. */
export function instrumentBrowser(browser: Browser): void {
  const proto = Object.getPrototypeOf(browser);
  if (proto[instrumented]) return;
  proto[instrumented] = true;

  const newContext = proto.newContext;
  proto.newContext = async function (this: Browser, ...args: unknown[]) {
    const context: BrowserContext = await newContext.apply(this, args);
    instrumentContext(context);
    return context;
  };
  const close = proto.close;
  proto.close = async function (this: Browser, ...args: unknown[]) {
    await Promise.all(this.contexts().map(flushContext));
    return close.apply(this, args);
  };
}

function instrumentContext(context: BrowserContext): void {
  const proto = Object.getPrototypeOf(context);
  if (!proto[instrumented]) {
    proto[instrumented] = true;
    const newPage = proto.newPage;
    proto.newPage = async function (this: BrowserContext, ...args: unknown[]) {
      const page: Page = await newPage.apply(this, args);
      await coverageOf(page).ready;
      return page;
    };
    const close = proto.close;
    proto.close = async function (this: BrowserContext, ...args: unknown[]) {
      await flushContext(this);
      return close.apply(this, args);
    };
  }
  context.on('page', (popup) => { coverageOf(popup); }); // window.open and target=_blank pages
}

const NAVIGATING_METHODS = ['goto', 'reload', 'setContent', 'goBack', 'goForward'];

/** Pages are only reachable through an instance, so the first page seen carries the Page and Locator patches. */
function instrumentPage(page: Page): void {
  const proto = Object.getPrototypeOf(page);
  if (proto[instrumented]) return;
  proto[instrumented] = true;
  const close = proto.close;
  proto.close = async function (this: Page, ...args: unknown[]) {
    await pageCoverages.get(this)?.flushFinal();
    return close.apply(this, args);
  };
  for (const name of NAVIGATING_METHODS) {
    const navigate = proto[name];
    proto[name] = async function (this: Page, ...args: unknown[]) {
      const coverage = pageCoverages.get(this);
      await coverage?.takeUsage();
      coverage?.expectNavigation();
      try {
        return await navigate.apply(this, args);
      } finally {
        coverage?.navigationDone();
      }
    };
  }
  dropScreenshotPath(proto);
  dropScreenshotPath(Object.getPrototypeOf(page.locator(':root')));
}

/** The screenshot is still taken and returned; it is just not written, so no coverage run touches doc/screenshots. */
function dropScreenshotPath(proto: { screenshot: (options?: object) => Promise<Buffer> }): void {
  const screenshot = proto.screenshot;
  proto.screenshot = function (this: unknown, options: { path?: string } = {}) {
    const { path: _path, ...withoutPath } = options;
    return screenshot.call(this, withoutPath);
  };
}

function coverageOf(page: Page): PageCoverage {
  let coverage = pageCoverages.get(page);
  if (!coverage) {
    instrumentPage(page);
    coverage = new PageCoverage(page, currentTest);
    pageCoverages.set(page, coverage);
  }
  return coverage;
}

const openPages = new Set<PageCoverage>();
const selfClosedFlushes = new Set<Promise<void>>();

async function flushAllOpenPages(): Promise<void> {
  await Promise.all([...openPages].map((coverage) => coverage.flushFinal()));
}

async function flushContext(context: BrowserContext): Promise<void> {
  await Promise.all(context.pages().map((page) => pageCoverages.get(page)?.flushFinal()));
}

class PageCoverage {
  readonly ready: Promise<void>;
  private session: CDPSession | null = null;
  private readonly sheets = new Map<string, SeenSheet>(); // CDP styleSheetId -> sheet
  private used = new Map<string, Map<string, [number, number]>>(); // styleSheetId -> used ranges not yet flushed
  private inlineSheets = 0;
  private scriptNavigations = 0;
  private queue: Promise<void> = Promise.resolve();
  private closed = false;
  private counted = false;
  private finalFlushed = false;
  private document = 0; // index of the current main-frame document
  private sheetsInDocument = 0; // stylesheets with a URL the current document loaded
  private navigationExpected = false; // a patched navigating method is running

  constructor(private readonly page: Page, private readonly owner: TestRecord | null) {
    openPages.add(this);
    page.on('close', () => this.onClose());
    this.ready = this.start().catch(() => undefined);
  }

  private async start(): Promise<void> {
    const session = await this.page.context().newCDPSession(this.page);
    session.on('CSS.styleSheetAdded', ({ header }) => this.onStyleSheetAdded(session, header));
    session.on('Page.frameNavigated', ({ frame }) => { if (!frame.parentId) this.onMainFrameNavigated(); });
    await session.send('Page.enable');
    await session.send('DOM.enable');
    await session.send('CSS.enable');
    await session.send('CSS.startRuleUsageTracking');
    this.session = session;
  }

  private onStyleSheetAdded(session: CDPSession, header: { styleSheetId: string; sourceURL: string }): void {
    if (!header.sourceURL) {
      this.inlineSheets++;
      return;
    }
    const text = session.send('CSS.getStyleSheetText', { styleSheetId: header.styleSheetId })
      .then((response) => response.text, () => null);
    this.sheets.set(header.styleSheetId, { url: header.sourceURL, text, document: this.document });
    this.sheetsInDocument++;
  }

  /** A navigation no patched method started loses the earlier document's last usage; it is counted, not hidden. */
  private onMainFrameNavigated(): void {
    if (this.navigationExpected) this.navigationExpected = false;
    else if (this.sheetsInDocument > 0) this.scriptNavigations++;
    this.document++;
    this.sheetsInDocument = 0;
  }

  expectNavigation(): void {
    this.navigationExpected = true;
  }

  navigationDone(): void {
    this.navigationExpected = false;
  }

  /** A page that closes on its own cannot be read any more: keep what was read before and count the loss. */
  private onClose(): void {
    this.closed = true;
    openPages.delete(this);
    if (this.finalFlushed) return;
    const owner = this.owner ?? currentTest;
    if (owner) owner.selfClosedPages++;
    const flush = this.flush().finally(() => selfClosedFlushes.delete(flush));
    selfClosedFlushes.add(flush);
  }

  /**
   * Collects the rules used since the previous call; calls are serialised so two reads never interleave.
   * `forceStyleUpdate` first flushes pending style changes (takeCoverageDelta does not), which is only safe once the
   * test no longer looks at the page.
   */
  takeUsage(forceStyleUpdate = false): Promise<void> {
    this.queue = this.queue.then(async () => {
      await this.ready;
      if (!this.session || this.closed) return;
      if (forceStyleUpdate) {
        await this.session.send('Runtime.evaluate', { expression: 'document.getAnimations().length' })
          .catch(() => undefined);
      }
      try {
        const { coverage } = await this.session.send('CSS.takeCoverageDelta');
        for (const rule of coverage) {
          if (!rule.used) continue;
          let ranges = this.used.get(rule.styleSheetId);
          if (!ranges) this.used.set(rule.styleSheetId, ranges = new Map());
          ranges.set(`${rule.startOffset}-${rule.endOffset}`, [rule.startOffset, rule.endOffset]);
        }
      } catch {
        // the page closed meanwhile
      }
    });
    return this.queue;
  }

  /** A document is IceBlue when it loaded at least the IceBlue bundle, whichever way the theme was chosen. */
  private themesByDocument(): Map<number, Theme> {
    const themes = new Map<number, Theme>();
    for (const sheet of this.sheets.values()) {
      if (ICEBLUE_URL.test(sheet.url)) themes.set(sheet.document, 'iceblue');
    }
    return themes;
  }

  /** The last read, taken after a style update, for a page that is about to close or whose test has ended. */
  async flushFinal(): Promise<void> {
    if (this.closed) return;
    this.finalFlushed = true;
    await this.takeUsage(true);
    await this.flush();
  }

  /**
   * Moves everything collected so far into the owning test. A loaded stylesheet is recorded even with no used rule, so
   * a sheet no test ever matches still shows up as fully unused. What is flushed is detached first, so usage that
   * arrives while this awaits a stylesheet text goes to the next flush instead of being lost.
   */
  async flush(): Promise<void> {
    await this.takeUsage();
    const owner = this.owner ?? currentTest;
    if (!owner) return;
    if (!this.counted) owner.pages++;
    this.counted = true;
    owner.inlineSheets += this.inlineSheets;
    owner.scriptNavigations += this.scriptNavigations;
    this.inlineSheets = 0;
    this.scriptNavigations = 0;
    const used = this.used;
    this.used = new Map();
    const themes = this.themesByDocument();
    for (const [styleSheetId, sheet] of [...this.sheets]) {
      const text = await sheet.text;
      if (text === null) continue;
      const hash = storeSheetText(text);
      const theme = themes.get(sheet.document) ?? 'marble';
      const key = `${theme}:${hash}`;
      let usage = owner.sheets.get(key);
      if (!usage) owner.sheets.set(key, usage = { hash, theme, url: normalizeUrl(sheet.url), used: new Map() });
      for (const [rangeKey, range] of used.get(styleSheetId) ?? []) usage.used.set(rangeKey, range);
    }
  }
}

/** Stylesheet URLs carry the session id (`;jsessionid=...`), which differs on every run. */
function normalizeUrl(url: string): string {
  return url.replace(/;jsessionid=[^?#]*/, '');
}

/** Keeps each distinct stylesheet text once, named by its content hash; safe when workers write at the same time. */
function storeSheetText(text: string): string {
  const hash = crypto.createHash('sha1').update(text).digest('hex');
  const file = path.join(SHEET_DIR, `${hash}.css`);
  if (!fs.existsSync(file)) writeAtomically(file, text);
  return hash;
}

function writeTestFile(record: TestRecord, testInfo: TestInfo): void {
  const sheets = [...record.sheets.values()].map((usage) => ({
    hash: usage.hash,
    theme: usage.theme,
    url: usage.url,
    used: [...usage.used.values()].flat(),
  }));
  const json = JSON.stringify({
    project: record.project,
    title: record.title,
    file: record.file,
    status: testInfo.status,
    retry: testInfo.retry,
    pages: record.pages,
    inlineSheets: record.inlineSheets,
    scriptNavigations: record.scriptNavigations,
    selfClosedPages: record.selfClosedPages,
    sheets,
  });
  const name = `${record.project}--${testInfo.testId}--r${testInfo.retry}.json`;
  writeAtomically(path.join(OUT_DIR, name), json);
}

function writeAtomically(file: string, content: string): void {
  fs.mkdirSync(path.dirname(file), { recursive: true });
  const temporary = `${file}.${process.pid}.tmp`;
  fs.writeFileSync(temporary, content);
  fs.renameSync(temporary, file);
}
