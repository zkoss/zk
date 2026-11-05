import { test, expect, type Page, type BrowserContext, type CDPSession } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import { AGGREGATE_PAGES } from './aggregate-pages';

// Scan-driven guard for keyboard focus rings.
// The navbar defect that prompted this was reported by a human
// looking at one page: the ring was drawn OUTSIDE the item's box, the item was
// exactly as wide as the container holding it, and the container clipped its
// overflow — so the ring was cut on every side. That shape is not unique to
// navbar (see the skill's reference/focus-ring-clipping.md for the at-risk DOM
// shapes), and finding the rest one design review at a time is the slow way.
//
// This spec finds them all mechanically. Two parts, both driven by the outline
// focus rules the LOADED stylesheets declare:
//
//   A. Ring exists, per component selector: every selector below is checked on one
//      representative page, so a rule that is deleted, turned into `outline: none` or
//      overridden fails instead of silently dropping out of the scan.
//   B. Ring not clipped, per page that has such an element (the clip scan):
//
//   1. Read the LOADED stylesheets and collect every selector that carries a
//      `:focus-visible` rule declaring an outline. That is exactly the
//      population at risk — elements the theme actually draws a ring for.
//   2. For each element matching one, force `:focus-visible` through CDP
//      (`CSS.forcePseudoState`), so the ring is really applied and every value
//      read back is the real computed one. Nothing is inferred from CSS text,
//      and no interaction is needed — which matters, because reaching a given
//      widget's focus by scripted Tab/click is exactly the part that does not
//      generalise (navbar, for one, has a roving tabindex: Tab enters the whole
//      widget once and arrow keys move inside).
//   3. Inflate the element's rect by `outline-offset + outline-width` and walk
//      its ancestors. If an ancestor that clips has a smaller region on any side
//      it clips, the ring is clipped — report it. An ancestor clips through
//      `overflow` per axis, `contain: paint` (also via `strict` and `content`) and
//      `clip-path`; it clips at its padding box (`clip-path`: border box).
//
// `:focus-visible` is forced on the FOCUS HOST of each rule, not on the ring's subject: for
// `.z-slider:focus-visible .z-slider-button` the host is the slider root and the subject its thumb. Forcing it on the
// subject would match nothing of ours and measure Chrome's own `outline: auto` ring instead.
//
// A second pass repeats step 2 under forced-colors emulation for items that are
// SELECTED as well as focused: the selection fill and `--zk-focus-ring` both
// remap to the system `Highlight` colour there, so a ring that is perfectly
// visible in normal mode can vanish. The selected state is a class in every one
// of these families, so the pass adds the class rather than driving a real
// selection — the cascade it produces is identical, which is what is under test.
//
// The selector → page map and the page list live in doc/focus-ring-outline-selectors.json,
// regenerated with FOCUS_SCAN_UPDATE=1 (see below). Components that show focus without an
// outline (a 2px border, a state layer) have no selector there and so no test at all.
//
// Requires the preview app on ${PREVIEW_URL}
//   withjdk.sh 17 mvn test exec:java@preview-app
// Run: npm run test:focus-scan

const WEB_DIR = path.resolve(__dirname, '../../main/webapp/web');

// Pages this scan cannot read anything useful from.
const SKIP = new Set([
  ...AGGREGATE_PAGES,
  // Non-visual / structural / meta pages, and the pages whose content is an
  // opaque third-party viewport (nothing of ours to focus).
  'area', 'html', 'iframe', 'imagemap',
  // Hardware / external-resource / animated pages — the DOM is not stable
  // enough for a geometry assertion.
  'camera', 'barcodescanner', 'captcha', 'video', 'audio', 'fileupload',
  'loading', 'loadingbar',
  // Auto-generated icon catalogue: thousands of nodes, no focusables of interest.
  'icons-lucide',
]);

const pages = fs.readdirSync(WEB_DIR)
  .filter(f => f.endsWith('.zul'))
  .map(f => f.replace(/\.zul$/, ''))
  .filter(name => !SKIP.has(name))
  .sort();

// KNOWN, UNFIXED clips — the backlog this scan produced on its first run
// (2026-09-06). They are recorded so the scan can land GREEN and start guarding
// against NEW clips immediately, instead of sitting red until the whole backlog
// is cleared. It is a shrinking list, not a waiver list:
//
//   - a finding NOT in the baseline fails the run (a new regression);
//   - a baseline entry that no longer reproduces ALSO fails the run, with
//     "fixed — delete this line", so the list cannot quietly rot.
//
// Regenerate the file
// after a fix with:  FOCUS_SCAN_UPDATE=1 npm run test:focus-scan
const BASELINE_FILE = path.resolve(__dirname, '../../../doc/focus-ring-known-clips.json');
// The outline focus selectors the theme declares, each with the page that proves it, and the
// pages that have at least one such element. Regenerated by the same FOCUS_SCAN_UPDATE=1 run.
const OUTLINE_FILE = path.resolve(__dirname, '../../../doc/focus-ring-outline-selectors.json');
const UPDATING = process.env.FOCUS_SCAN_UPDATE === '1';

type Baseline = Record<string, string[]>;   // page → sorted keys
const baseline: Baseline = fs.existsSync(BASELINE_FILE)
  ? JSON.parse(fs.readFileSync(BASELINE_FILE, 'utf8')).clips
  : {};

type OutlineSelector = {
  page: string;        // representative page that proves the selector
  component: string;   // ZK widget name of the first visible match (`navitem`, `toolbarbutton`)
  raw: string;         // the rule's selector as written, `:focus-visible` included — it says where the focus goes
};
type OutlineSelectors = {
  selectors: Record<string, OutlineSelector>;   // selector (`:focus-visible` stripped) → page, component and raw selector
  pages: string[];                     // pages with at least one visible outline-focus element
  unrendered: string[];                // outline selectors that match nothing visible on any page
};
const outlineSelectors: OutlineSelectors = readOutlineSelectors();

function readOutlineSelectors(): OutlineSelectors {
  if (UPDATING) return { selectors: {}, pages: [], unrendered: [] };
  if (!fs.existsSync(OUTLINE_FILE)) {
    throw new Error(`${OUTLINE_FILE} is missing — generate it with FOCUS_SCAN_UPDATE=1 npm run test:focus-scan`);
  }
  return JSON.parse(fs.readFileSync(OUTLINE_FILE, 'utf8'));
}

/** Coarse, layout-stable identity for a finding: which element, clipped by what. */
const keyOf = (f: { selector: string; clipper: string }) =>
  `${f.selector} ⊂ ${f.clipper.replace(/\s+\(unclassed[^)]*\)/, ' (unclassed)')}`;

/**
 * Fallback component name when a matching element has no ZK widget:
 * `.z-button.z-button-hover` → `button`; a selector without a `z-` class is its own name.
 */
const componentOf = (selector: string) => /\.z-([\w-]+)/.exec(selector)?.[1] ?? selector;

type Finding = {
  page: string;
  selector: string;
  tag: string;
  cls: string;
  offset: number;
  width: number;
  clipper: string;
  overflow: string;
  short: number;
};

/** A loaded rule that draws an outline on focus: the selector as written and its subject selector. */
type FocusRule = {
  raw: string;        // `.z-slider:focus-visible .z-slider-button`
  selector: string;   // `:focus-visible` stripped, matches the element that wears the ring
};

/** Collect every loaded rule that draws an outline on focus. */
async function focusRules(page: Page): Promise<FocusRule[]> {
  return page.evaluate(() => {
    const out = new Map<string, string>();
    // The selector without its `:not(…)` groups. A `:focus-visible` inside one negates focus
    // (`.z-anchornav button.z-anchornav-link-active:not(:focus-visible)` marks the link while it is
    // NOT focused), so that rule draws no focus ring.
    const withoutNot = (sel: string) => {
      let rest = '';
      for (let i = 0; i < sel.length;) {
        if (!sel.startsWith(':not(', i)) { rest += sel[i++]; continue; }
        let depth = 0;
        let j = i + ':not'.length;
        for (; j < sel.length; j++) {
          if (sel[j] === '(') depth++;
          else if (sel[j] === ')' && --depth === 0) break;
        }
        i = j + 1;
      }
      return rest;
    };
    type AnyRule = CSSStyleRule & { cssRules?: CSSRuleList; styleSheet?: CSSStyleSheet };
    const walk = (list: CSSRuleList) => {
      for (const r of Array.from(list) as AnyRule[]) {
        // @import — the imported sheet hangs off .styleSheet, not .cssRules.
        if (r.styleSheet) {
          try { walk(r.styleSheet.cssRules); } catch { /* cross-origin */ }
          continue;
        }
        // A rule inside a media query that does not match (forced-colors, print…) does not apply
        // here, so it must not make its selector look like it draws a ring — `.z-selectbox`
        // draws a ring only under forced-colors, and an inset box-shadow otherwise.
        if (r instanceof CSSMediaRule && !window.matchMedia(r.media.mediaText).matches) continue;
        // NB: a plain CSSStyleRule also exposes .cssRules in current Chrome (CSS
        // Nesting), so "has cssRules" does NOT mean "is a grouping at-rule".
        // Consider the rule itself whenever it has a selector, and recurse
        // independently — that covers @layer/@media/@supports and nesting alike.
        if (r.selectorText && r.selectorText.includes(':focus-visible')) {
          const css = r.style.cssText;
          // Only rules that actually paint a ring. `outline: none` disables one.
          if (/\boutline\b/.test(css) && !/outline(-style)?\s*:\s*(none|0)\b/.test(css)) {
            for (const raw of r.selectorText.split(',')) {
              const sel = raw.trim();
              if (!withoutNot(sel).includes(':focus-visible')) continue;
              if (sel.includes('::')) continue;             // targets a pseudo-element
              out.set(sel, sel.replace(/:focus-visible/g, '').trim());
            }
          }
        }
        if (r.cssRules && r.cssRules.length) walk(r.cssRules);
      }
    };
    for (const sheet of Array.from(document.styleSheets)) {
      try { walk(sheet.cssRules); } catch { /* cross-origin sheet */ }
    }
    return [...out].map(([raw, selector]) => ({ raw, selector })).filter(rule => rule.selector);
  });
}

/**
 * Tag every element matching the subject selector of one of `rules` with `data-fs-idx`, so the same
 * element can be addressed from both CDP and the page. Returns one entry per tagged element.
 */
async function tagCandidates(page: Page, rules: FocusRule[]) {
  return page.evaluate((all) => {
    const seen = new Set<Element>();
    const rows: { idx: number; selector: string; raw: string }[] = [];
    for (const { selector, raw } of all) {
      let matches: Element[];
      try { matches = Array.from(document.querySelectorAll(selector)); } catch { continue; }
      for (const el of matches) {
        if (seen.has(el)) continue;
        const r = el.getBoundingClientRect();
        if (r.width < 1 || r.height < 1) continue;       // hidden / zero-area variant
        const idx = rows.length;
        el.setAttribute('data-fs-idx', String(idx));
        seen.add(el);
        rows.push({ idx, selector, raw });
      }
    }
    return rows;
  }, rules);
}

type Candidate = { idx: number; selector: string; raw: string };

/**
 * Tag with `data-fs-force` the element that must hold `:focus-visible` for each candidate's rule to match
 * it: the candidate itself, the ancestor that matches an earlier compound of the selector
 * (`.z-slider:focus-visible .z-slider-button`), or the element inside the `:has()` owner that matches the
 * `:has()` argument (`.z-checkbox:has(input:focus-visible) > .z-checkbox-mold`). Returns the number of
 * distinct hosts and the candidates whose host could not be found.
 */
async function tagFocusHosts(page: Page, candidates: Candidate[]) {
  return page.evaluate((rows) => {
    const stripFocus = (s: string) => s.replace(/:focus-visible/g, '').trim();
    // Split at the top-level combinators only: not inside (), [] or a quoted string.
    const splitCompounds = (selector: string) => {
      const compounds: string[] = [];
      const combinators: string[] = [];
      let current = '';
      let pending = '';
      let depth = 0;
      let quote = '';
      for (const ch of selector.trim()) {
        if (quote) {
          current += ch;
          if (ch === quote) quote = '';
          continue;
        }
        if (ch === '"' || ch === "'") quote = ch;
        else if (ch === '(' || ch === '[') depth++;
        else if (ch === ')' || ch === ']') depth--;
        const isCombinator = depth === 0 && !quote && (/\s/.test(ch) || '>+~'.includes(ch));
        if (isCombinator) {
          if (current) { compounds.push(current); current = ''; pending = ''; }
          if (compounds.length) pending = '>+~'.includes(ch) ? ch : (pending || ' ');
          continue;
        }
        if (pending) { combinators.push(pending); pending = ''; }
        current += ch;
      }
      if (current) compounds.push(current);
      return { compounds, combinators };
    };
    // The `:has()` argument that contains `:focus-visible`, or null when the compound holds it itself.
    const hasArgumentWithFocus = (compound: string) => {
      const start = compound.indexOf(':has(');
      if (start < 0) return null;
      let depth = 0;
      for (let i = start + ':has'.length; i < compound.length; i++) {
        if (compound[i] === '(') depth++;
        if (compound[i] === ')' && --depth === 0) {
          const arg = compound.slice(start + ':has('.length, i);
          return arg.includes(':focus-visible') ? arg : null;
        }
      }
      return null;
    };
    const findHost = (subject: Element, raw: string): Element | string => {
      const { compounds, combinators } = splitCompounds(raw);
      const focusAt = compounds.findIndex(c => c.includes(':focus-visible'));
      if (focusAt < 0) return 'no :focus-visible in the selector';
      let owner: Element | null = subject;
      for (let i = compounds.length - 1; i > focusAt && owner; i--) {
        if (combinators[i - 1] !== ' ' && combinators[i - 1] !== '>') return `unsupported combinator "${combinators[i - 1]}"`;
        owner = owner.parentElement?.closest(stripFocus(compounds[i - 1])) ?? null;
      }
      if (!owner) return 'no ancestor matches the compound that holds :focus-visible';
      const arg = hasArgumentWithFocus(compounds[focusAt]);
      if (arg === null) return owner;
      const inside = stripFocus(arg);
      return owner.querySelector(/^[>+~]/.test(inside) ? `:scope ${inside}` : inside)
        ?? 'nothing inside the owner matches the :has() argument';
    };
    const hosts = new Map<Element, number>();
    const unresolved: string[] = [];
    for (const { idx, raw } of rows) {
      const subject = document.querySelector(`[data-fs-idx="${idx}"]`)!;
      const host = findHost(subject, raw);
      if (typeof host === 'string') {
        unresolved.push(`${raw} — ${host}`);
        continue;
      }
      if (!hosts.has(host)) {
        hosts.set(host, hosts.size);
        host.setAttribute('data-fs-force', String(hosts.get(host)));
      }
    }
    return { hosts: hosts.size, unresolved: [...new Set(unresolved)] };
  }, candidates);
}

/**
 * The candidates whose computed outline is the browser's own `auto` ring: Chrome gives every element with a
 * forced `:focus-visible` one, and the theme never draws `outline: auto`, so the theme rule did not apply.
 */
async function browserDefaultRings(page: Page, candidates: Candidate[]): Promise<Candidate[]> {
  const idxs = await page.evaluate(() =>
    Array.from(document.querySelectorAll('[data-fs-idx]'))
      .filter(el => getComputedStyle(el).outlineStyle === 'auto')
      .map(el => Number(el.getAttribute('data-fs-idx'))));
  return candidates.filter(c => idxs.includes(c.idx));
}

/** Fail with the rules whose focus host is unknown: forcing the wrong element would measure the browser's ring. */
function assertHostsFound(unresolved: string[]) {
  if (unresolved.length === 0) return;
  throw new Error(
    'cannot tell which element must hold :focus-visible for these selector(s) — extend tagFocusHosts:\n' +
      unresolved.map(u => `  ${u}`).join('\n'));
}

function assertNoBrowserDefaultRing(defaults: Candidate[], where: string) {
  if (defaults.length === 0) return;
  throw new Error(
    `${where}: computed outline is the browser default ring (auto), so the theme rule did not apply with ` +
      `:focus-visible forced on the host — wrong host or a rule that no longer matches:\n` +
      defaults.map(c => `  ${c.raw}`).join('\n'));
}

/** The subset of `selectors` that match at least one visible element on the page. */
async function matchingSelectors(page: Page, selectors: string[]): Promise<string[]> {
  return page.evaluate((sels) => sels.filter((sel) => {
    try {
      return Array.from(document.querySelectorAll(sel)).some((el) => {
        const r = el.getBoundingClientRect();
        return r.width >= 1 && r.height >= 1;
      });
    } catch { return false; }
  }), selectors);
}

/**
 * The ZK widget name of the first visible element each selector matches, or null when that element
 * belongs to no widget. `zk.Widget.$` walks up from a DOM node, so a part such as
 * `.z-navitem-content` resolves to its `navitem`.
 */
async function widgetNames(page: Page, selectors: string[]): Promise<Map<string, string | null>> {
  const names = await page.evaluate((sels) => sels.map((sel) => {
    const el = Array.from(document.querySelectorAll(sel)).find((e) => {
      const r = e.getBoundingClientRect();
      return r.width >= 1 && r.height >= 1;
    });
    const widget = el && (window as any).zk.Widget.$(el);
    if (widget && widget.widgetName !== 'native') return [sel, widget.widgetName as string] as const;
    // A static `<h:div class="z-confirmpopup">` mock-up has no widget of its own: name the
    // nearest ancestor's root class (`z-<comp>`, no further hyphen) instead.
    for (let a: Element | null = el ?? null; a; a = a.parentElement) {
      const root = Array.from(a.classList).find(c => /^z-[a-z]+$/.test(c));
      if (root) return [sel, root.slice(2)] as const;
    }
    return [sel, null] as const;
  }), selectors);
  return new Map(names);
}

/** Force (or release) `:focus-visible` on every tagged candidate. */
async function forceFocus(
  cdp: CDPSession, count: number, on: boolean,
  attr = 'data-fs-idx', pseudo = ['focus-visible'],
) {
  const { root } = await cdp.send('DOM.getDocument', { depth: -1 });
  for (let i = 0; i < count; i++) {
    const { nodeId } = await cdp.send('DOM.querySelector', {
      nodeId: root.nodeId,
      selector: `[${attr}="${i}"]`,
    });
    if (!nodeId) continue;
    await cdp.send('CSS.forcePseudoState', {
      nodeId,
      forcedPseudoClasses: on ? pseudo : [],
    });
  }
}

/**
 * With the pseudo-class forced, measure every candidate's real ring and find the
 * nearest ancestor that clips it.
 */
async function measureClipping(page: Page) {
  return page.evaluate(() => {
    const results: {
      idx: number; tag: string; cls: string; offset: number; width: number;
      clipper: string | null; overflow: string; short: number;
    }[] = [];
    const createsContainingBlock = (e: Element, includePositioned: boolean) => {
      const s = getComputedStyle(e);
      return (includePositioned && s.position !== 'static')
        || s.transform !== 'none' || s.translate !== 'none' || s.rotate !== 'none' || s.scale !== 'none'
        || s.perspective !== 'none' || s.filter !== 'none'
        || /\b(layout|paint|strict|content)\b/.test(s.contain)
        || /\b(transform|perspective|filter)\b/.test(s.willChange);
    };
    /**
     * The ancestors whose overflow can clip `el`: those on its containing-block chain.
     * An absolutely positioned box is not clipped by an ancestor that sits between it and its
     * containing block; a fixed box escapes every ancestor except a transformed one.
     */
    const clippingCandidates = (el: Element): Element[] => {
      const chain: Element[] = [];
      let box: Element = el;
      while (box.parentElement && box.parentElement !== document.documentElement) {
        const position = getComputedStyle(box).position;
        let anchor: Element | null = box.parentElement;
        if (position === 'absolute' || position === 'fixed') {
          const includePositioned = position === 'absolute';
          while (anchor && anchor !== document.documentElement
                 && !createsContainingBlock(anchor, includePositioned)) {
            anchor = anchor.parentElement;
          }
        }
        if (!anchor || anchor === document.documentElement) break;
        chain.push(anchor);
        box = anchor;
      }
      return chain;
    };
    /**
     * The region an ancestor lets painting through, per axis; an axis it does not clip is unbounded.
     * `overflow` (other than `visible`) and `contain: paint` clip at the padding box, which
     * `overflow: clip` extends by `overflow-clip-margin`; `clip-path` clips at the border box (the
     * shape is taken as that box). Returns null when the ancestor clips nothing.
     */
    const clipRegion = (p: Element) => {
      const pcs = getComputedStyle(p);
      const box = p.getBoundingClientRect();
      const px = (value: string) => parseFloat(value) || 0;
      const clipMargin = (overflow: string) =>
        overflow === 'clip' ? px(pcs.overflowClipMargin.split(/\s+/).pop() ?? '') : 0;
      const paintContained = /\b(paint|strict|content)\b/.test(pcs.contain);
      const region = { left: -Infinity, right: Infinity, top: -Infinity, bottom: Infinity };
      const reasons: string[] = [];
      if (paintContained || pcs.overflowX !== 'visible') {
        region.left = box.left + px(pcs.borderLeftWidth) - clipMargin(pcs.overflowX);
        region.right = box.right - px(pcs.borderRightWidth) + clipMargin(pcs.overflowX);
      }
      if (paintContained || pcs.overflowY !== 'visible') {
        region.top = box.top + px(pcs.borderTopWidth) - clipMargin(pcs.overflowY);
        region.bottom = box.bottom - px(pcs.borderBottomWidth) + clipMargin(pcs.overflowY);
      }
      if (pcs.overflowX !== 'visible' || pcs.overflowY !== 'visible') reasons.push(`overflow ${pcs.overflowX}/${pcs.overflowY}`);
      if (paintContained) reasons.push(`contain ${pcs.contain}`);
      if (pcs.clipPath !== 'none') {
        region.left = Math.max(region.left, box.left);
        region.right = Math.min(region.right, box.right);
        region.top = Math.max(region.top, box.top);
        region.bottom = Math.min(region.bottom, box.bottom);
        reasons.push('clip-path');
      }
      return reasons.length ? { region, reasons: reasons.join(', ') } : null;
    };
    for (const el of Array.from(document.querySelectorAll('[data-fs-idx]'))) {
      const cs = getComputedStyle(el);
      const width = parseFloat(cs.outlineWidth) || 0;
      const offset = parseFloat(cs.outlineOffset) || 0;
      // No ring drawn (the rule did not win, or the element is in a state that
      // suppresses it) — nothing to clip.
      if (cs.outlineStyle === 'none' || width === 0) continue;
      // An inset ring is inside the element's own box; it can never be clipped.
      if (offset + width <= 0) continue;
      const r = el.getBoundingClientRect();
      const ring = {
        top: r.top - offset - width,
        left: r.left - offset - width,
        right: r.right + offset + width,
        bottom: r.bottom + offset + width,
      };
      let clipper: string | null = null;
      let overflow = '';
      let short = 0;
      for (const p of clippingCandidates(el)) {
        const clip = clipRegion(p);
        if (!clip) continue;
        const { left, right, top, bottom } = clip.region;
        const s = Math.max(left - ring.left, ring.right - right, top - ring.top, ring.bottom - bottom);
        if (s > 0.5) {
          const own = typeof p.className === 'string' ? p.className.trim() : '';
          const host = p.parentElement && typeof p.parentElement.className === 'string'
            ? p.parentElement.className.trim().split(/\s+/)[0]
            : '';
          clipper = own ? `${p.tagName}.${own.split(/\s+/).join('.')}`
                        : `${p.tagName} (unclassed, inside .${host})`;
          overflow = clip.reasons;
          short = Math.round(s * 100) / 100;
          break;
        }
      }
      if (!clipper) continue;
      const cls = typeof el.className === 'string' ? el.className.trim() : '';
      results.push({
        idx: Number(el.getAttribute('data-fs-idx')),
        tag: el.tagName, cls, offset, width, clipper, overflow, short,
      });
    }
    return results;
  });
}

function writeJson(file: string, doc: object) {
  fs.writeFileSync(file, JSON.stringify(doc, null, 2) + '\n');
}

/** Rewrite the clip baseline from a full scan. Untouched when no clip changed, so a refresh does not churn it. */
function writeKnownClips(clips: Baseline) {
  if (JSON.stringify(clips) === JSON.stringify(baseline)) return;
  writeJson(BASELINE_FILE, {
    note: 'Known, unfixed clipped focus rings. Generated by ' +
      'src/test/playwright/focus-ring-scan.spec.ts (FOCUS_SCAN_UPDATE=1). ' +
      'A shrinking backlog, not a waiver list.',
    generated: new Date().toISOString().slice(0, 10),
    clips,
  });
}

/** The page that proves a selector: the one named after its component when it matches there, else the first. */
function representativePage(component: string, matchedOn: string[]): string {
  return matchedOn.includes(component) ? component : matchedOn[0];
}

function writeOutlineSelectors(
  loaded: Set<string>, matchedOn: Map<string, string[]>, components: Map<string, string>,
  raws: Map<string, string>, listedPages: string[],
) {
  const selectors = Object.fromEntries([...matchedOn.entries()]
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([selector, onPages]) => {
      const component = components.get(selector)!;
      return [selector, { page: representativePage(component, onPages), component, raw: raws.get(selector)! }];
    }));
  writeJson(OUTLINE_FILE, {
    note: 'Outline focus selectors the theme declares (`:focus-visible` stripped), each with the page that ' +
      'proves it and the selector as written (`raw`), and the pages that have at least one such element. Generated by ' +
      'src/test/playwright/focus-ring-scan.spec.ts (FOCUS_SCAN_UPDATE=1). `unrendered` selectors match no ' +
      'visible element on any preview page (a preview gap) and are not tested.',
    generated: new Date().toISOString().slice(0, 10),
    selectors,
    pages: listedPages.sort(),
    unrendered: [...loaded].filter(sel => !matchedOn.has(sel)).sort(),
  });
}

async function openPage(page: Page, name: string) {
  await page.goto(`/${name}.zul`, { waitUntil: 'domcontentloaded' });
  await page.waitForSelector('.z-page', { timeout: 15000 });
  await page.evaluate(() => document.fonts.ready.then(() => true));
}

/** Measure the clipped rings on the page that is open: one entry per distinct (element, clipper) pair. */
async function scanClips(page: Page, context: BrowserContext, comp: string, rules: FocusRule[]) {
  const candidates = await tagCandidates(page, rules);
  if (candidates.length === 0) return { candidates, detail: new Map<string, string>() };

  const { hosts, unresolved } = await tagFocusHosts(page, candidates);
  assertHostsFound(unresolved);
  const cdp = await context.newCDPSession(page);
  await cdp.send('DOM.enable');
  await cdp.send('CSS.enable');
  await forceFocus(cdp, hosts, true, 'data-fs-force');
  assertNoBrowserDefaultRing(await browserDefaultRings(page, candidates), `${comp}.zul`);
  const raw = await measureClipping(page);
  await forceFocus(cdp, hosts, false, 'data-fs-force');
  await cdp.detach();

  const byIdx = new Map(candidates.map(c => [c.idx, c.selector]));
  const findings: Finding[] = raw
    .map(r => ({ page: comp, selector: byIdx.get(r.idx) ?? '?', ...r, clipper: r.clipper! }));

  // One line per distinct (element, clipper) pair; a page can repeat the same
  // pair dozens of times and that adds nothing to the report.
  const detail = new Map<string, string>();
  for (const f of findings) {
    detail.set(keyOf(f),
      `${f.selector} (${f.tag}.${f.cls.split(/\s+/).join('.')}) ` +
      `outline ${f.width}px @ offset ${f.offset}px is clipped by ${f.clipper} ` +
      `(${f.overflow}) — overshoots by ${f.short}px`);
  }
  return { candidates, detail };
}

async function loadedFocusRules(page: Page) {
  const rules = await focusRules(page);
  expect(
    rules.length,
    'no :focus-visible outline rules found in the loaded stylesheets — the theme CSS did not load',
  ).toBeGreaterThan(0);
  return rules;
}

if (UPDATING) {
  // One serial test, so the two baseline files are each written once and never raced by parallel workers.
  test('regenerate the focus-ring baselines', async ({ page, context }) => {
    test.setTimeout(10 * 60_000);
    const loaded = new Set<string>();
    const matchedOn = new Map<string, string[]>();
    const components = new Map<string, string>();
    const raws = new Map<string, string>();
    const listedPages: string[] = [];
    const clips: Baseline = {};
    for (const comp of pages) {
      await openPage(page, comp);
      const rules = await loadedFocusRules(page);
      const selectors = rules.map(rule => rule.selector);
      for (const { raw, selector } of rules) {
        if (raws.has(selector) && raws.get(selector) !== raw) {
          throw new Error(`two rules share the subject selector ${selector}: ${raws.get(selector)} and ${raw}`);
        }
        raws.set(selector, raw);
        loaded.add(selector);
      }
      const matching = await matchingSelectors(page, selectors);
      if (matching.length === 0) continue;
      listedPages.push(comp);
      const names = await widgetNames(page, matching);
      for (const sel of matching) {
        matchedOn.set(sel, [...(matchedOn.get(sel) ?? []), comp]);
        if (!components.has(sel)) components.set(sel, names.get(sel) ?? componentOf(sel));
      }
      const { detail } = await scanClips(page, context, comp, rules);
      if (detail.size) clips[comp] = [...detail.keys()].sort();
    }
    writeKnownClips(clips);
    writeOutlineSelectors(loaded, matchedOn, components, raws, listedPages);
  });
}

// Part A. One test per outline selector, on the page that proves it.
test.describe('focus-ring outline', () => {
  for (const [selector, { page: comp, component, raw }] of Object.entries(outlineSelectors.selectors)) {
    test(`${component} — ${selector} draws a focus outline`, async ({ page, context }) => {
      await openPage(page, comp);
      const target = await page.evaluate((sel) => {
        const el = Array.from(document.querySelectorAll(sel)).find((e) => {
          const r = e.getBoundingClientRect();
          return r.width >= 1 && r.height >= 1;
        });
        el?.setAttribute('data-fs-idx', '0');
        return !!el;
      }, selector);
      expect(target, `${selector} matches no visible element on ${comp}.zul — the element or the page changed`).toBe(true);

      const { hosts, unresolved } = await tagFocusHosts(page, [{ idx: 0, selector, raw }]);
      assertHostsFound(unresolved);
      const cdp = await context.newCDPSession(page);
      await cdp.send('DOM.enable');
      await cdp.send('CSS.enable');
      await forceFocus(cdp, hosts, true, 'data-fs-force');
      const ring = await page.evaluate(() => {
        const cs = getComputedStyle(document.querySelector('[data-fs-idx="0"]')!);
        return { style: cs.outlineStyle, width: parseFloat(cs.outlineWidth) || 0 };
      });
      await forceFocus(cdp, hosts, false, 'data-fs-force');
      await cdp.detach();

      // The rule itself must still be loaded: without it the browser's own focus ring can
      // still paint an outline, and the computed check alone would pass. `auto` is that ring.
      const ruleLoaded = (await focusRules(page)).some(rule => rule.raw === raw);
      expect(
        ruleLoaded && ring.style !== 'none' && ring.style !== 'auto' && ring.width > 0,
        `${selector} on ${comp}.zul: computed outline-style ${ring.style}, outline-width ${ring.width}px with ` +
          `:focus-visible forced on the host of ${raw}; ` +
          (ring.style === 'auto'
            ? 'outline-style auto is the browser default ring — the Marble rule did not apply.'
            : ruleLoaded
            ? 'its :focus-visible outline rule is loaded but another rule overrides it.'
            : 'no loaded :focus-visible rule declares an outline for it any more (rule deleted or turned into ' +
              'outline: none). If that is intended, regenerate with FOCUS_SCAN_UPDATE=1.'),
      ).toBe(true);
    });
  }
});

// Part B. One test per page that has an outline-focus element: is any ring clipped?
test.describe('focus-ring scan', () => {
  // One test per page keeps a failure pointing at the page that owns it, and
  // lets a single bad page be re-run on its own.
  for (const comp of outlineSelectors.pages) {
    test(`${comp} — focus ring not clipped`, async ({ page, context }) => {
      await openPage(page, comp);
      const rules = await loadedFocusRules(page);

      const unknown = (await matchingSelectors(page, rules.map(rule => rule.selector)))
        .filter(sel => !(sel in outlineSelectors.selectors));
      expect(
        unknown,
        `new outline focus selector(s) on ${comp}.zul — regenerate with FOCUS_SCAN_UPDATE=1:\n` +
          unknown.map(sel => `  ${sel}`).join('\n'),
      ).toEqual([]);

      const { candidates, detail } = await scanClips(page, context, comp, rules);
      expect(
        candidates.length,
        `${comp}.zul is listed as having outline focus elements but has none — the page lost them; ` +
          'regenerate with FOCUS_SCAN_UPDATE=1 if that is intended',
      ).toBeGreaterThan(0);
      const seen = [...detail.keys()].sort();

      const known = baseline[comp] ?? [];
      const added = seen.filter(k => !known.includes(k));
      const gone = known.filter(k => !seen.includes(k));

      expect(
        added,
        'NEW clipped focus ring(s) on this page:\n' +
          added.map(k => `  ${detail.get(k)}`).join('\n') +
          '\n\nA ring drawn outside a box that has no room is invisible to the ' +
          'keyboard user. Draw it inside (negative outline-offset), or stop the ' +
          'ancestor clipping. See the skill\'s reference/focus-ring-clipping.md.',
      ).toEqual([]);

      expect(
        gone,
        'These clips are FIXED — delete them from doc/focus-ring-known-clips.json ' +
          `(or run FOCUS_SCAN_UPDATE=1 npm run test:focus-scan):\n` +
          gone.map(k => `  ${k}`).join('\n'),
      ).toEqual([]);
    });
  }
});

// The clipping model itself, on synthetic markup appended to a page that has the theme loaded. Each case
// puts a `.z-button` in a box that is exactly as large as the button, so the 4px ring has no room: a positive case must be
// reported, a negative one must not. The first four gaps were missed by the scan before: standalone `translate`, `rotate`
// and `scale` make a containing block (the clipper above is on the chain of an absolutely positioned button), `contain:
// paint` and `clip-path` clip, `overflow` clips at the padding box (a border thicker than the ring hid it), and
// `overflow-x: clip` leaves the y axis visible.
const FIXTURE_PAGE = '/button.zul';
const BUTTON = '<button class="z-button" style="display:block;width:100%;height:100%;margin:0;box-sizing:border-box">Go</button>';
const ABSOLUTE_BUTTON = BUTTON.replace('style="', 'style="position:absolute;left:0;top:0;');
const BOX = 'width:80px;height:36px';

const MODEL_CASES: { name: string; html: string; clipped: boolean }[] = [
  ...[['translate', '0px 0px'], ['rotate', '0deg'], ['scale', '1']].map(([property, value]) => ({
    name: `${property} makes a containing block, so the overflow clipper above an absolutely positioned control clips it`,
    html: `<div style="position:relative"><div style="overflow:hidden;${BOX}">` +
      `<div style="${property}:${value};width:100%;height:100%">${ABSOLUTE_BUTTON}</div></div></div>`,
    clipped: true,
  })),
  ...['paint', 'strict', 'content'].map((value) => ({
    name: `contain: ${value} clips the ring`,
    html: `<div style="contain:${value};${BOX}">${BUTTON}</div>`,
    clipped: true,
  })),
  { name: 'clip-path clips the ring', html: `<div style="clip-path:inset(0);${BOX}">${BUTTON}</div>`, clipped: true },
  {
    name: 'overflow clips at the padding box, so a border around it does not hide the clip',
    html: `<div style="overflow:hidden;box-sizing:content-box;${BOX};border:8px solid #ccc">${BUTTON}</div>`,
    clipped: true,
  },
  {
    name: 'overflow-x: clip clips the ring on the x axis',
    html: `<div style="overflow-x:clip;overflow-y:visible;${BOX}">${BUTTON}</div>`,
    clipped: true,
  },
  {
    name: 'overflow-x: clip leaves the y axis visible, so a ring that only overshoots in y is not clipped',
    html: `<div style="overflow-x:clip;overflow-y:visible;width:200px"><div style="${BOX};margin:0 auto">${BUTTON}</div></div>`,
    clipped: false,
  },
  {
    name: 'a ring inside the padding box of a clipper with room is not clipped',
    html: `<div style="overflow:hidden;box-sizing:content-box;width:80px;height:36px;padding:8px">${BUTTON}</div>`,
    clipped: false,
  },
];

test.describe('focus-ring scan — clipping model', () => {
  for (const { name, html, clipped } of MODEL_CASES) {
    test(name, async ({ page, context }) => {
      await page.goto(FIXTURE_PAGE, { waitUntil: 'domcontentloaded' });
      await page.waitForSelector('.z-page', { timeout: 15000 });
      await page.evaluate(() => document.fonts.ready.then(() => true));
      await page.evaluate((markup) => {
        const host = document.createElement('div');
        host.style.cssText = 'position:relative;margin:40px';
        host.innerHTML = markup;
        document.body.append(host);
        host.querySelector('button')!.setAttribute('data-fs-idx', '0');
      }, html);
      const cdp = await context.newCDPSession(page);
      await cdp.send('DOM.enable');
      await cdp.send('CSS.enable');
      await forceFocus(cdp, 1, true);
      const found = await measureClipping(page);
      await forceFocus(cdp, 1, false);
      await cdp.detach();
      expect(found.length, `findings: ${JSON.stringify(found)}`).toBe(clipped ? 1 : 0);
    });
  }
});

// A ring can be painted over by a neighbour as well as clipped: the scan above cannot see that. The toolbarbutton of
// listbox-header.zul is followed by a `.z-menubar` (z-index 100) that covered the 4px of its ring reaching into it.
// Read off a screenshot: what is painted in the ring band is the ring colour.
test.describe('focus-ring scan — ring painted over by a neighbour', () => {
  test('listbox-header: the ring of a toolbarbutton is painted over the menubar that follows it', async ({ page }) => {
    await page.goto('/listbox-header.zul', { waitUntil: 'domcontentloaded' });
    const button = page.locator('.z-listheader .z-toolbarbutton').first();
    await button.waitFor();
    await page.evaluate(() => document.fonts.ready.then(() => true));
    await page.addStyleTag({ content: '*, *::before, *::after { transition: none !important; }' });
    await button.scrollIntoViewIfNeeded();
    await page.keyboard.press('Tab');
    await button.focus();
    const ring = await button.evaluate((el) => {
      const style = getComputedStyle(el);
      const rect = el.getBoundingClientRect();
      return {
        x: rect.left + rect.width / 2, bottom: rect.bottom + window.scrollY,
        reach: (parseFloat(style.outlineWidth) || 0) + (parseFloat(style.outlineOffset) || 0),
        colour: (style.outlineColor.match(/[\d.]+/g) ?? []).slice(0, 3).map(Number),
        menubarBelow: !!el.parentElement!.querySelector('.z-menubar'),
      };
    });
    expect(ring.menubarBelow, 'the toolbarbutton has a menubar next to it').toBe(true);
    // The pixel row just inside the outer edge of the ring, under the toolbarbutton.
    const png = await page.screenshot({
      fullPage: true, clip: { x: Math.floor(ring.x), y: Math.floor(ring.bottom + ring.reach - 1), width: 1, height: 1 },
    });
    const painted = await page.evaluate(async (base64) => {
      const image = new Image();
      image.src = `data:image/png;base64,${base64}`;
      await image.decode();
      const canvas = document.createElement('canvas');
      canvas.width = 1;
      canvas.height = 1;
      const context = canvas.getContext('2d')!;
      context.drawImage(image, 0, 0);
      return Array.from(context.getImageData(0, 0, 1, 1).data.slice(0, 3));
    }, png.toString('base64'));
    const difference = painted.reduce((sum, channel, i) => sum + Math.abs(channel - ring.colour[i]), 0);
    expect(difference, `ring bottom is ${JSON.stringify(painted)}, outline ${JSON.stringify(ring.colour)}`).toBeLessThanOrEqual(24);
  });

  // A menubar before the toolbarbutton in the same container covers the side of the ring that reaches towards it, so the
  // pixel just inside the outer edge of the ring, left of the button, is read.
  test('a toolbarbutton that follows a menubar paints its whole ring over the menubar', async ({ page }) => {
    await page.goto('/pv/toolbarbutton-menubar-before-guard.zul', { waitUntil: 'domcontentloaded' });
    const button = page.locator('.tmb-row .z-toolbarbutton');
    await button.waitFor();
    await page.evaluate(() => document.fonts.ready.then(() => true));
    await page.addStyleTag({ content: '*, *::before, *::after { transition: none !important; }' });
    await page.keyboard.press('Tab');
    await button.focus();
    const ring = await button.evaluate((el) => {
      const style = getComputedStyle(el);
      const rect = el.getBoundingClientRect();
      return {
        left: rect.left + window.scrollX, y: rect.top + window.scrollY + rect.height / 2,
        reach: (parseFloat(style.outlineWidth) || 0) + (parseFloat(style.outlineOffset) || 0),
        colour: (style.outlineColor.match(/[\d.]+/g) ?? []).slice(0, 3).map(Number),
        menubarBefore: !!el.parentElement!.querySelector('.z-menubar'),
      };
    });
    expect(ring.menubarBefore, 'the toolbarbutton has a menubar before it').toBe(true);
    const png = await page.screenshot({
      fullPage: true, clip: { x: Math.floor(ring.left - ring.reach + 1), y: Math.floor(ring.y), width: 1, height: 1 },
    });
    const painted = await page.evaluate(async (base64) => {
      const image = new Image();
      image.src = `data:image/png;base64,${base64}`;
      await image.decode();
      const canvas = document.createElement('canvas');
      canvas.width = 1;
      canvas.height = 1;
      const context = canvas.getContext('2d')!;
      context.drawImage(image, 0, 0);
      return Array.from(context.getImageData(0, 0, 1, 1).data.slice(0, 3));
    }, png.toString('base64'));
    const difference = painted.reduce((sum, channel, i) => sum + Math.abs(channel - ring.colour[i]), 0);
    expect(difference, `ring left edge is ${JSON.stringify(painted)}, outline ${JSON.stringify(ring.colour)}`).toBeLessThanOrEqual(24);
  });

  // The `z-index: 101` that lifts the ring above the menubar belongs to a toolbarbutton that has a menubar after it. On any
  // other toolbarbutton it paints a keyboard-focused button over the opt-in `.z-sticky-header` header (z-index 2) once the
  // row has been scrolled under it.
  test('a focused toolbarbutton scrolled under a sticky header stays under it', async ({ page }) => {
    await page.goto('/pv/toolbarbutton-sticky-guard.zul', { waitUntil: 'domcontentloaded' });
    const button = page.locator('.tsg-scroller .z-toolbarbutton').nth(4);
    await button.waitFor();
    await page.evaluate(() => document.fonts.ready.then(() => true));
    await page.addStyleTag({ content: '*, *::before, *::after { transition: none !important; }' });
    await page.keyboard.press('Tab');
    await button.evaluate((el) => {
      const scroller = el.closest('.tsg-scroller') as HTMLElement;
      const header = scroller.querySelector('.z-listbox-header') as HTMLElement;
      const buttonTop = el.getBoundingClientRect().top - scroller.getBoundingClientRect().top + scroller.scrollTop;
      scroller.scrollTop = buttonTop - header.getBoundingClientRect().height / 2;
      (el as HTMLElement).focus({ preventScroll: true });
    });
    const topmost = await button.evaluate((el) => {
      const rect = el.getBoundingClientRect();
      const header = el.closest('.tsg-scroller')!.querySelector('.z-listbox-header')!.getBoundingClientRect();
      const hit = document.elementFromPoint(rect.left + rect.width / 2, rect.top + rect.height / 2);
      return {
        focused: el.matches(':focus-visible'), underHeader: rect.top + rect.height / 2 < header.bottom,
        header: !!hit?.closest('.z-listbox-header'), hit: hit?.className,
      };
    });
    expect(topmost.focused, 'the toolbarbutton has keyboard focus').toBe(true);
    expect(topmost.underHeader, 'the centre of the toolbarbutton is under the header').toBe(true);
    expect(topmost.header, `the header is on top of the button, the topmost element is "${topmost.hit}"`).toBe(true);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// Second pass: selected AND focused, under forced-colors.
//
// In Windows High-Contrast the selection fill and `--zk-focus-ring` both remap
// to the system `Highlight` colour, so a focus ring that is perfectly visible in
// normal mode can paint Highlight-on-Highlight and vanish on the one item a
// keyboard user is most likely to be sitting on — the current row / page / node.
// navbar hit this (doc/spec/forced-colors.md case 8); these are the other
// families that block (2a) of tokens/_forced-colors.css fills with Highlight AND
// that draw a real outline on focus.
//
// The selected state is a plain class in every one of them, so the pass adds the
// class rather than driving a real selection: what is under test is the cascade
// the two rules produce together, and that is identical either way.
//
// Tree and listbox rows are never DOM-focused: keyboard focus sits on the hidden
// `a.z-focus-a` inside the widget, and the row ring comes from
// `.z-tree:has(.z-focus-a:focus-visible) .z-treerow.z-treerow-focus`. Those
// entries therefore set `addCls` (the row's focus class) and `host` (the widget
// whose `.z-focus-a` gets the forced focus) instead of forcing focus on the row.
//
// Families deliberately NOT listed, with the reason:
//   - accordion tab (.z-tab): focus is a `::before` state-layer opacity, not an
//     outline. forced-colors drops pseudo-element backgrounds outright, so its
//     focus is invisible in WHCM whether or not the tab is selected — a
//     different gap (no outline at all), not this collision.
//   - searchbox: `.z-searchbox-focus` is the root's focus class while
//     `.z-searchbox-selected` is a row inside the popup — different elements, so
//     the two colours never meet.
const SELECTED_FAMILIES = [
  { name: 'navbar item',     page: 'navbar',     focus: '.z-navitem-content', on: 'parent', cls: 'z-navitem-selected' },
  { name: 'tree row',        page: 'tree',       focus: '.z-treerow',         on: 'self',   cls: 'z-treerow-selected', addCls: 'z-treerow-focus', host: '.z-tree' },
  { name: 'listbox row',     page: 'listbox',    focus: '.z-listitem',        on: 'self',   cls: 'z-listitem-selected', addCls: 'z-listitem-focus', host: '.z-listbox' },
  { name: 'paging button',   page: 'paging',     focus: '.z-paging-button',   on: 'self',   cls: 'z-paging-selected' },
  { name: 'organigram node', page: 'organigram', focus: '.z-orgnode',         on: 'parent', cls: 'z-orgitem-selected' },
] as const satisfies readonly {
  name: string; page: string; focus: string; on: 'self' | 'parent'; cls: string;
  addCls?: string; host?: string;
}[];

test.describe('focus-ring scan — selected + focused under forced-colors', () => {
  for (const fam of SELECTED_FAMILIES) {
    test(`${fam.page}: ${fam.name}`, async ({ page, context }) => {
      await page.emulateMedia({ forcedColors: 'active' });
      await page.goto(`/${fam.page}.zul`, { waitUntil: 'domcontentloaded' });
      await page.waitForSelector(fam.focus, { timeout: 15000 });
      await page.evaluate(() => document.fonts.ready.then(() => true));

      // The selected class and the forced focus change background-color; a
      // transition (.z-orgnode has 0.25s) would make the sample below land
      // mid-fade and read a blend instead of the settled fill.
      await page.addStyleTag({ content: '*, *::before, *::after { transition: none !important; }' });

      const ok = await page.evaluate((f) => {
        const el = document.querySelector(f.focus);
        if (!el) return false;
        const target = f.on === 'parent' ? el.parentElement : el;
        if (!target) return false;
        target.classList.add(f.cls);
        el.setAttribute('data-fs-idx', '0');
        if ('host' in f) {
          target.classList.add(f.addCls);
          const a = el.closest(f.host)?.querySelector('.z-focus-a');
          if (!a) return false;
          a.setAttribute('data-fs-force', '0');
        }
        return true;
      }, fam);
      expect(ok, `no ${fam.focus} on ${fam.page}.zul`).toBe(true);

      const cdp = await context.newCDPSession(page);
      await cdp.send('DOM.enable');
      await cdp.send('CSS.enable');
      if ('host' in fam) await forceFocus(cdp, 1, true, 'data-fs-force', ['focus', 'focus-visible']);
      else await forceFocus(cdp, 1, true);

      const m = await page.evaluate(() => {
        const el = document.querySelector('[data-fs-idx="0"]')!;
        el.scrollIntoView({ block: 'center' });
        const cs = getComputedStyle(el);
        const rgb = (s: string) => (s.match(/[\d.]+/g) ?? []).slice(0, 3).map(Number);
        const opaque = (s: string) => {
          const parts = s.match(/[\d.]+/g) ?? [];
          return !(s === 'transparent' || (parts.length === 4 && Number(parts[3]) === 0));
        };
        const width = parseFloat(cs.outlineWidth) || 0;
        const offset = parseFloat(cs.outlineOffset) || 0;
        // What the ring is painted OVER depends on which side of the border box
        // it lands on: an inset ring covers the element's own fill (or a child's,
        // if a child paints there — a selected table row fills its cells, not the
        // <tr>), an outset ring covers whatever is outside the element. Rather
        // than reason about it, hit-test the middle of the ring band: the outline
        // itself is not hit-tested, so elementFromPoint returns exactly what the
        // ring is drawn on top of.
        const r = el.getBoundingClientRect();
        const inward = offset + width <= 0;
        const d = inward ? Math.abs(offset) + width / 2 : offset + width / 2;
        // Clamp into the viewport: elementFromPoint returns null outside it, and
        // a null backdrop silently compares the ring against black — which reads
        // as a defect on a dark ring. The assertion on `fillFrom` below turns
        // that into an explicit failure rather than a false positive.
        const clamp = (v: number, hi: number) => Math.max(1, Math.min(v, hi - 1));
        const x = clamp(inward ? r.left + d : r.left - d, window.innerWidth);
        const y = clamp((r.top + r.bottom) / 2, window.innerHeight);
        let fill = 'transparent';
        let from = '(nothing)';
        for (let p = document.elementFromPoint(x, y); p; p = p.parentElement) {
          const bg = getComputedStyle(p).backgroundColor;
          if (opaque(bg)) {
            fill = bg;
            from = (typeof p.className === 'string' && p.className.trim()) || p.tagName;
            break;
          }
        }
        return {
          outlineStyle: cs.outlineStyle, width, offset,
          ring: rgb(cs.outlineColor), fill: rgb(fill), fillFrom: from.slice(0, 40),
        };
      });
      if ('host' in fam) await forceFocus(cdp, 1, false, 'data-fs-force');
      else await forceFocus(cdp, 1, false);
      await cdp.detach();

      test.skip(
        m.outlineStyle === 'none' || m.width === 0,
        `${fam.focus} draws no outline on focus — nothing to collide`,
      );

      // Without a real backdrop there is nothing to compare the ring against, and
      // the comparison below would quietly measure it against black.
      expect(
        m.fillFrom,
        'could not sample what the ring is painted on — the element was probably ' +
          'off-screen when measured, so this run proves nothing',
      ).not.toBe('(nothing)');

      const delta = m.ring.reduce((a, c, i) => a + Math.abs(c - (m.fill[i] ?? 0)), 0);
      expect(
        delta,
        `ring ${JSON.stringify(m.ring)} (offset ${m.offset}px) vs the fill it is painted on, ` +
          `${JSON.stringify(m.fill)} from .${m.fillFrom} — ` +
          'the focus ring is invisible on the selected item under forced-colors. ' +
          'Re-point it to HighlightText in tokens/_forced-colors.css block (2a focus).',
      ).toBeGreaterThan(200);   // navbar measured 78 when broken, 687 when fixed
    });
  }
});

// ─────────────────────────────────────────────────────────────────────────────
// Keyboard focus row of a listbox / tree, driven with the real mouse and keyboard.
//
// `.z-listbox:has(.z-focus-a:focus-visible) .z-listitem.z-listitem-focus` (and the tree's
// `.z-treerow-focus` twin) match only after the widget has put its `-focus` class on a row, which a
// forced `:focus-visible` cannot do, so the outline scan above lists them as unrendered. Here a click
// makes a row the widget's focus row (keyboard focus moves to the hidden `.z-focus-a`, but a mouse
// focus is not `:focus-visible`, so no ring), and ArrowDown then turns it into a keyboard focus.
// A click, not Tab: when the selected item is the last one (as in the first tree of tree.zul), Tab
// onto the widget and ArrowDown focuses no row, in listbox and tree alike.
const KEYBOARD_ROWS = [
  { component: 'listbox', host: '.z-listbox', row: '.z-listitem', focusCls: 'z-listitem-focus' },
  { component: 'tree', host: '.z-tree', row: '.z-treerow', focusCls: 'z-treerow-focus' },
] as const;

test.describe('focus-ring — keyboard focus row', () => {
  for (const k of KEYBOARD_ROWS) {
    test(`${k.component} — keyboard focus row draws a focus outline, a clicked row does not`, async ({ page }) => {
      await openPage(page, k.component);
      const widget = page.locator(k.host).first();
      await widget.locator(k.row).first().click();
      await page.mouse.move(0, 0);

      // The ring the theme draws for focus, read from the token so the test holds no literal.
      const measure = () => page.evaluate(({ host, focusCls }) => {
        const probe = document.createElement('div');
        probe.style.outline = 'var(--zk-focus-ring)';
        document.body.append(probe);
        const p = getComputedStyle(probe);
        const ring = `${p.outlineStyle} ${p.outlineWidth} ${p.outlineColor}`;
        probe.remove();
        const rows = document.querySelector(host)!.querySelectorAll(`.${focusCls}`);
        const cs = rows[0] && getComputedStyle(rows[0]);
        return {
          ring,
          focusRows: rows.length,
          outline: cs ? `${cs.outlineStyle} ${cs.outlineWidth} ${cs.outlineColor}` : '(no focus row)',
          keyboard: !!document.activeElement?.matches(`${host} .z-focus-a:focus-visible`),
        };
      }, { host: k.host, focusCls: k.focusCls });

      const clicked = await measure();
      expect(clicked.focusRows, `a click makes one ${k.row} the widget's focus row`).toBe(1);
      expect(clicked.keyboard, 'a mouse focus is not :focus-visible').toBe(false);
      expect(clicked.outline, 'a clicked row wears no focus ring').toMatch(/^none /);

      await page.keyboard.press('ArrowDown');
      await expect.poll(async () => (await measure()).keyboard, 'ArrowDown makes the focus a keyboard focus').toBe(true);
      const keyed = await measure();
      expect(keyed.focusRows).toBe(1);
      expect(keyed.outline, `the keyboard focus row wears --zk-focus-ring (${keyed.ring})`).toBe(keyed.ring);
    });
  }
});
