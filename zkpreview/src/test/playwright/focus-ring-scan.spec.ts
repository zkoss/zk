import { test, expect, type Page, type CDPSession } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

// Scan-driven guard for keyboard focus rings (doc/skill-gaps.md 2026-09-04,
// decision D13). The navbar defect that prompted this was reported by a human
// looking at one page: the ring was drawn OUTSIDE the item's box, the item was
// exactly as wide as the container holding it, and the container clipped its
// overflow — so the ring was cut on every side. That shape is not unique to
// navbar (see the skill's reference/focus-ring-clipping.md for the at-risk DOM
// shapes), and finding the rest one design review at a time is the slow way.
//
// This spec finds them all mechanically, on every preview page:
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
//      its ancestors. If an ancestor that clips overflow has a smaller rect on
//      any side, the ring is clipped — report it.
//
// A second pass repeats step 2 under forced-colors emulation for items that are
// SELECTED as well as focused: the selection fill and `--zk-focus-ring` both
// remap to the system `Highlight` colour there, so a ring that is perfectly
// visible in normal mode can vanish. The selected state is a class in every one
// of these families, so the pass adds the class rather than driving a real
// selection — the cascade it produces is identical, which is what is under test.
//
// Requires the preview app on ${PREVIEW_URL}
//   withjdk.sh 17 mvn test exec:java@preview-app
// Run: npm run test:focus-scan

const WEB_DIR = path.resolve(__dirname, '../../main/webapp/web');

// Pages this scan cannot read anything useful from.
const SKIP = new Set([
  // Non-visual / structural / meta pages, and the pages whose content is an
  // opaque third-party viewport (nothing of ours to focus).
  'area', 'html', 'iframe', 'imagemap', 'overview', 'preview', 'inputs',
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
// Fixing these is tracked in doc/focus-ring-clip-backlog.md. Regenerate the file
// after a fix with:  FOCUS_SCAN_UPDATE=1 npm run test:focus-scan
const BASELINE_FILE = path.resolve(__dirname, '../../../doc/focus-ring-known-clips.json');
const UPDATING = process.env.FOCUS_SCAN_UPDATE === '1';

type Baseline = Record<string, string[]>;   // page → sorted keys
const baseline: Baseline = fs.existsSync(BASELINE_FILE)
  ? JSON.parse(fs.readFileSync(BASELINE_FILE, 'utf8')).clips
  : {};

/** Coarse, layout-stable identity for a finding: which element, clipped by what. */
const keyOf = (f: { selector: string; clipper: string }) =>
  `${f.selector} ⊂ ${f.clipper.replace(/\s+\(unclassed[^)]*\)/, ' (unclassed)')}`;

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

/**
 * Collect the base selectors (with `:focus-visible` stripped) of every loaded
 * rule that draws an outline on focus.
 */
async function focusSelectors(page: Page): Promise<string[]> {
  return page.evaluate(() => {
    const out = new Set<string>();
    type AnyRule = CSSStyleRule & { cssRules?: CSSRuleList; styleSheet?: CSSStyleSheet };
    const walk = (list: CSSRuleList) => {
      for (const r of Array.from(list) as AnyRule[]) {
        // @import — the imported sheet hangs off .styleSheet, not .cssRules.
        if (r.styleSheet) {
          try { walk(r.styleSheet.cssRules); } catch { /* cross-origin */ }
          continue;
        }
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
              if (!sel.includes(':focus-visible')) continue;
              if (sel.includes('::')) continue;             // targets a pseudo-element
              out.add(sel.replace(/:focus-visible/g, '').trim());
            }
          }
        }
        if (r.cssRules && r.cssRules.length) walk(r.cssRules);
      }
    };
    for (const sheet of Array.from(document.styleSheets)) {
      try { walk(sheet.cssRules); } catch { /* cross-origin sheet */ }
    }
    return [...out].filter(Boolean);
  });
}

/**
 * Tag every element matching one of `selectors` with `data-fs-idx`, so the same
 * element can be addressed from both CDP and the page. Returns one entry per
 * tagged element.
 */
async function tagCandidates(page: Page, selectors: string[]) {
  return page.evaluate((sels) => {
    const seen = new Map<Element, string>();
    const rows: { idx: number; selector: string }[] = [];
    for (const sel of sels) {
      let matches: Element[];
      try { matches = Array.from(document.querySelectorAll(sel)); } catch { continue; }
      for (const el of matches) {
        if (seen.has(el)) continue;
        const r = el.getBoundingClientRect();
        if (r.width < 1 || r.height < 1) continue;       // hidden / zero-area variant
        const idx = rows.length;
        el.setAttribute('data-fs-idx', String(idx));
        seen.set(el, sel);
        rows.push({ idx, selector: sel });
      }
    }
    return rows;
  }, selectors);
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
        || s.transform !== 'none' || s.perspective !== 'none' || s.filter !== 'none'
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
        const pcs = getComputedStyle(p);
        if (pcs.overflowX === 'visible' && pcs.overflowY === 'visible') continue;
        const pr = p.getBoundingClientRect();
        // `overflow: clip` paints up to `overflow-clip-margin` beyond the padding box; `hidden` ignores it.
        const margin = pcs.overflowX === 'clip' && pcs.overflowY === 'clip'
          ? parseFloat(pcs.overflowClipMargin.split(/\s+/).pop() ?? '') || 0
          : 0;
        const s = Math.max(
          pr.left - margin - ring.left, ring.right - (pr.right + margin),
          pr.top - margin - ring.top, ring.bottom - (pr.bottom + margin),
        );
        if (s > 0.5) {
          const own = typeof p.className === 'string' ? p.className.trim() : '';
          const host = p.parentElement && typeof p.parentElement.className === 'string'
            ? p.parentElement.className.trim().split(/\s+/)[0]
            : '';
          clipper = own ? `${p.tagName}.${own.split(/\s+/).join('.')}`
                        : `${p.tagName} (unclassed, inside .${host})`;
          overflow = `${pcs.overflowX}/${pcs.overflowY}`;
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

/**
 * Rewrite one page's entry in the baseline file. Called only under
 * FOCUS_SCAN_UPDATE=1, one page at a time, so the file is read back each time
 * rather than held in memory across tests.
 */
function writeBaselineEntry(page: string, keys: string[]) {
  const doc = fs.existsSync(BASELINE_FILE)
    ? JSON.parse(fs.readFileSync(BASELINE_FILE, 'utf8'))
    : { note: '', generated: '', clips: {} };
  if (keys.length) doc.clips[page] = keys;
  else delete doc.clips[page];
  doc.note =
    'Known, unfixed clipped focus rings. Generated by ' +
    'src/test/playwright/focus-ring-scan.spec.ts (FOCUS_SCAN_UPDATE=1). ' +
    'A shrinking backlog, not a waiver list — see doc/focus-ring-clip-backlog.md.';
  doc.generated = new Date().toISOString().slice(0, 10);
  doc.clips = Object.fromEntries(Object.entries(doc.clips).sort());
  fs.writeFileSync(BASELINE_FILE, JSON.stringify(doc, null, 2) + '\n');
}

test.describe('focus-ring scan', () => {
  // One test per page keeps a failure pointing at the page that owns it, and
  // lets a single bad page be re-run on its own.
  for (const comp of pages) {
    test(comp, async ({ page, context }) => {
      await page.goto(`/${comp}.zul`, { waitUntil: 'domcontentloaded' });
      await page.waitForSelector('.z-page', { timeout: 15000 });
      await page.evaluate(() => document.fonts.ready.then(() => true));

      const selectors = await focusSelectors(page);
      expect(
        selectors.length,
        'no :focus-visible outline rules found in the loaded stylesheets — the theme CSS did not load',
      ).toBeGreaterThan(0);

      const candidates = await tagCandidates(page, selectors);
      test.skip(candidates.length === 0, 'no focusable element on this page draws an outline');

      const cdp = await context.newCDPSession(page);
      await cdp.send('DOM.enable');
      await cdp.send('CSS.enable');
      await forceFocus(cdp, candidates.length, true);
      const raw = await measureClipping(page);
      await forceFocus(cdp, candidates.length, false);
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
          `(overflow ${f.overflow}) — overshoots by ${f.short}px`);
      }
      const seen = [...detail.keys()].sort();

      if (UPDATING) {
        writeBaselineEntry(comp, seen);
        return;
      }

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
