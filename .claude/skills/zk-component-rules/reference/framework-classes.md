# ZK Framework Global CSS Classes — Canonical Registry

**ZK version pinned: 10.2.1-jakarta**
Regenerate this file when the target ZK version changes. Sources are under
`/Users/hawk/Documents/workspace/ZK10/zk/` (tag 10.2.1-jakarta).

This file is the **spec layer** of the three-layer framework-class compliance check
that ships with this skill. It generalises and supersedes
`css-flex-classes.md` for the flex family; cross-links to `zk-core-emitted-selectors.md`
for bundling rules.

---

## Why this registry exists

ZK's client-side JS/TS engine toggles certain CSS classes at runtime (adding and removing
them on live DOM nodes). A theme that omits, renames, or hard-codes around such a class
silently breaks ZK behaviour — **no error, just wrong layout**.

The proven failure: Marble renamed `.z-flex` → `.z-d-flex` in a utility-class pass.
ZK's css-flex engine added `.z-flex` to containers at runtime, but the CSS rule no longer
matched, so every `hflex`/`vflex` container stayed `display: block`. Drag-persistence in
`Splitlayout` was a secondary casualty (see `css-flex-classes.md` hard rule 2).

---

## JS-toggled CONTRACT classes (theme MUST define verbatim)

A class is marked **contract** only when the research found actual `addClass` / `removeClass`
/ `className =` calls that add and remove it at runtime. The theme **must** define these
selectors with values that are functionally equivalent to stock — and must never hard-code
their effect onto a component root that ZK JS manages.

| Class | Stock source | JS/TS toggling site | Required CSS declaration | Notes |
|-------|-------------|---------------------|--------------------------|-------|
| `.z-flex` | `footer.less` | `zk/flex.ts:applyCSSFlex` / `clearCSSFlex` | `display: flex` | Container of hflex/vflex children |
| `.z-flex > :not(.z-flex-item)` | `footer.less` | same (selector implied by `.z-flex`) | `flex-shrink: 0` | Non-flex siblings inside a flex container must not shrink |
| `.z-flex-row` | `footer.less` | `zk/flex.ts:applyCSSFlex` / `clearCSSFlex` | `flex-direction: row` | Added alongside `.z-flex` |
| `.z-flex-column` | `footer.less` | `zk/flex.ts:applyCSSFlex` / `clearCSSFlex` | `flex-direction: column` | Added alongside `.z-flex` |
| `.z-flex-item` | `footer.less` | `zk/flex.ts:applyCSSFlex` / `clearCSSFlex` | `flex: 1 1 0; min-height: 0; min-width: 0` | Added to each flexed child; removed before ZK writes inline px sizes |
| `.z-dragged` | `norm.less` (stock styles it) | `zk/widget.ts:cloneDrag_` / `uncloneDrag_` (4361/4371) | de-emphasise source — Marble: `opacity: .4` (stock used color+background) | Applied to the drag-source node while drag is in flight |
| `.z-drag-over` | — (JS hook; **no stock CSS**) | `zk/widget.ts:dropEffect_` (4311) | theme-defined drop-target highlight | Added to the drop-target root when a draggable is over it |
| `.z-drag-ghost` | — (JS hook; **no stock CSS**) | `zk/widget.ts:ghost` clone path (461) | theme-defined floating clone | The body-appended clone `#zk_ddghost` for **non-TR** drags |
| `.z-drop-ghost` | — (JS hook; **no stock CSS**) | `zk/widget.ts:ghost` (440) | theme-defined message chip | Root of the message ghost `#zk_ddghost` for **TR** (listitem/row) drags |
| `.z-drop-content` | — (JS hook; **no stock CSS**) | `zk/widget.ts:ghost` (441) | theme-defined inner wrapper | Inner wrapper inside the message ghost |
| `.z-drop-icon` | — (JS hook; **no stock CSS**) | `zk/widget.ts:ghost` (441) + `DD_dragging` (507/511) | theme-defined mask glyph | Icon span `#zk_ddghost-img`; toggles `z-icon-plus-circle`/`z-icon-ban` |
| `.z-drop-text` | — (JS hook; **no stock CSS**) | `zk/widget.ts:ghost` (438) | theme-defined message text | Message text span inside the ghost |
| `.z-drop-allow` | — (JS hook; **no stock CSS**) | `zk/widget.ts:DD_dragging` (505) | theme-defined valid-drop state | Toggled on the message ghost over a valid target |
| `.z-drop-disallow` | — (JS hook; **no stock CSS**) | `zk/widget.ts:ghost` (440) + `DD_dragging` (509) | theme-defined invalid-drop state | Initial + invalid state on the message ghost |
| `.z-word-nowrap` | `norm.less` | `zul/mesh/Frozen.ts:bind_` / `unbind_` (lines 191/216) | `white-space: nowrap` | Added to grid/listbox body + foot when `<frozen>` is bound |
| `.z-renderdefer` | `norm.less` | `zk/widget.ts:redraw_` (line 2940) | sizing + spinner background | Placeholder emitted while `_renderdefer` timer has not yet fired |

### Detailed required CSS for contract classes

```css
/* footer.less — must load LAST (after component CSS) */
.z-flex { display: flex; }
.z-flex > :not(.z-flex-item) { flex-shrink: 0; }
.z-flex-row { flex-direction: row; }
.z-flex-column { flex-direction: column; }
.z-flex-item { flex: 1 1 0; min-height: 0; min-width: 0; }

/* norm.less */
.z-dragged { color: <drag-color>; background: none no-repeat scroll 0 0 <drag-bg>; }
.z-drag-over { background: <drag-hover-bg> !important; }
/* .z-drag-ghost, .z-drop-allow, .z-drop-disallow — see decorative section for full block */
.z-word-nowrap { white-space: nowrap; }
/* .z-renderdefer — see decorative section (spinner background-image) */
```

Marble location for the flex block: `zul/src/main/resources/web/zul/css/base/_cssflex.css`
(bundled into `footer.css.dsp` via `footerFiles` in `scripts/build-css.js`).
This is byte-equivalent to stock — **do not modify**.

### Hard rules for contract classes

1. **Never rename these selectors.** Theme utilities may use any naming (`.z-d-flex`, …)
   as *additions*, not replacements. The JS-toggled names must exist under their exact names.
2. **Never hard-code the effect of a JS-toggled class onto a component root ZK manages.**
   E.g. `.z-splitlayout { display: flex }` looks equivalent — but ZK *removes* `.z-flex` at
   drag-end. A hard-coded `display: flex` survives the class removal and defeats the mechanism.
3. **Never give a JS-sized child `flex-basis: 0` in component CSS.** `flex: 1 1 0` on an
   element whose inline `width`/`height` ZK writes makes the browser ignore the inline size.
   `.z-flex-item` is the only legitimate carrier because ZK removes it before writing inline
   sizes (see `css-flex-classes.md` for the full analysis).

---

## Decorative global classes (theme may style freely)

These classes appear in ZK's stock global CSS but are **not** runtime-toggled by JS via
`addClass`/`removeClass`. They are either:
- Set once at render time (static class in mold JS / server-rendered HTML), or
- Used purely as a CSS selector target that ZK JS reads but never writes.

A theme that omits them loses visual polish but does not break functionality.

| Class | Stock source | JS/TS reference | Required CSS (stock values) | Notes |
|-------|-------------|-----------------|----------------------------|-------|
| `.z-borderbox` | `norm.less` (line 19) | none — pure CSS attribution pattern | `box-sizing: border-box` | Applies to `[class^="z-"]` globally; `.z-borderbox` is the opt-in for non-ZK elements |
| `.z-inline-block` | `norm.less` (line 399) | set via `setSclass("z-inline-block")` in ZUL — not toggled by framework | `display: inline-block; vertical-align: top` | Helper for ensuring dimension on inline elements |
| `.z-word-wrap` | `norm.less` | not toggled at runtime by framework JS | `word-wrap: break-word` | Declarative utility |
| `.z-overflow-hidden` | `norm.less` | not toggled at runtime by framework JS | `overflow: hidden` | Declarative utility |
| `.z-dd-stackup` | `norm.less` | `zk/drag.ts` sets `stackup.className = 'z-dd-stackup'` at construction (line 607) — created once, not toggled | `position: absolute; left:0; top:0; width:100%; height:100%; z-index: 16800; background-image: 1px transparent GIF` | Invisible overlay during drag to capture mouse events in iframes |
| `.z-temp` | `norm.less` | `HtmlPageRenders.java` renders it server-side; `crashmsg.ts` only reads `querySelectorAll('.z-temp')`, never toggles | `position: fixed; top:0; left:0; width:100%; height:100%; background:…; opacity:0.6` | Initial loading scrim; server-emitted on page load, replaced by `z-loading` block after ZK mounts |
| `.z-initing` | `norm.less` | `zk/zk.ts` `_showprgb` appends a `<div class="z-initing">` as a child of the `z-temp`/`z-loading` block when `_zk.pi` flag is set — emitted once, not subsequently toggled | `position: absolute; right:10px; bottom:10px; width:60px; height:60px; background:… no-repeat center` | Page-initialisation indicator spinner |
| `.z-loading` | `norm.less` | `zk/utl.ts:progressbox` creates it in innerHTML — not a runtime toggle on existing nodes | `position: absolute; top:0; left:0; z-index:31000; background:…; border-radius:…; cursor:wait` | showBusy message box (global). **Bundling note**: must be in `normFiles` — see `zk-core-emitted-selectors.md` |
| `.z-loading-indicator` | `norm.less` | created by `progressbox` | `padding:…; text-align:center; color:…; background:…` | Inner content of showBusy box |
| `.z-loading-icon` | `norm.less` | created by `progressbox` | `display:block; position:relative; left:50%; width:…; height:…; background: spinner-gif` | Spinner element. Theme must supply a spinner image or CSS animation |
| `.z-apply-loading` | `norm.less` | `zk/effect.ts:Mask._draw` creates it in innerHTML | `position:absolute; z-index:89500; background:…; border-radius:…; cursor:wait` | Per-component showBusy overlay. **Bundling note**: must be in `normFiles` |
| `.z-apply-loading-indicator` | `norm.less` | created by `Mask._draw` | `font:…; padding:…; position:relative; overflow:hidden` | |
| `.z-apply-loading-icon` | `norm.less` | created by `Mask._draw` | `display:inline-block; position:absolute; width:…; height:…; background: spinner-gif` | |
| `.z-apply-mask` | `norm.less` | created by `Mask._draw` (line 292) | `position:absolute; top:0; left:0; width:100%; height:100%; background:…; opacity:0.6; z-index:89000` | Scrim for per-component busy overlay |
| `.z-modal-mask` | `norm.less` | `zk/utl.ts:progressbox` and `zk/drag.ts` create it in innerHTML | `position:fixed; inset:0; top:0!important; left:0!important; width:100%; height:100%; background:…; opacity:0.6` | Full-page modal scrim. See `zk-core-emitted-selectors.md` for the inline `top/left` quirk requiring `!important` |
| `.z-error` | `norm.less` | `zk/zk.ts:_Erbx` creates via innerHTML; `display:none` is MANDATORY (slideDown needs hidden start) | `display:none; position:absolute; top:0; left:40%; z-index:9999999; width:450px; …` | JS debug error panel. See `zk-core-emitted-selectors.md` for full DOM structure |
| `.z-log` | `norm.less` | `zk/zk.ts` sets `logBox.className = 'z-log'` (line 304) — created once for `zk.log()` debug output | `position:absolute; right:10px; bottom:5px; width:50%; z-index:99000; text-align:right` | `zk.log()` debug output box |
| `.noscript` | `norm.less` | static HTML `<noscript>` tag — no JS | `position:absolute; top:0; left:0; width:100%; height:100%; background:#E0E1E3; opacity:0.6; z-index:32000; text-align:center` | No-JS fallback overlay |
| `.z-focus-a` | `norm.less` | rendered in mold JS (listbox, tree, window, calendar, menupopup) as a static class — never toggled | `font-size:0!important; width:1px; height:1px; …; position:absolute; overflow:hidden` | Invisible focus anchor for keyboard navigation; must exist but must be visually hidden |
| `.z-clear` | `norm.less` | rendered in mold JS (toolbar, toolbarpanel, frozen, tabbox) as a static class — never toggled | `font-size:0; width:0; height:0; line-height:0; overflow:hidden; clear:both` | Float-clearfix helper |
| `.z-macro` | `norm.less` | emitted by `MacroWidget` as root class — static render | `display:inline-block; min-width:1px` | Root container for macro components |
| `.z-scrollbar` | `norm.less` | `zul/Scrollbar.ts` renders it in innerHTML — static class | `display:none; position:absolute; line-height:1` (plus `-vertical`, `-horizontal`, `-indicator`, etc. sub-classes) | Custom scrollbar widget; styles must cover all `.z-scrollbar-*` sub-classes |
| `.z-upload` | `norm.less` | `zul/Upload.ts:_createUploadHTML` renders `<span class="z-upload">` | `font-size:0; display:inline-block; width:0; height:0; margin:0; padding:0; position:relative` | Invisible file-input wrapper for upload buttons |
| `.z-upload-icon` | `norm.less` | static in upload widget | `background-image: progress-meter gif; overflow:hidden` | Upload progress indicator |
| `.z-fileupload-add` / `.z-fileupload-remove` | `norm.less` | `zul/Upload.ts` sets `sclass='z-fileupload-remove …'` and `sclass='z-fileupload-progress'` — set at widget creation time | `color:#1096BC; width:16px; height:17px; cursor:pointer` / `width:300px` | File-upload dialog buttons |
| `.z-fileupload-progress` | `norm.less` | same | `width:300px` | |
| `.z-fileupload-manager` | `norm.less` | `zul/Upload.ts:setSclass('z-fileupload-manager')` — set once, not toggled | `width:350px` | |
| `.z-drop-content` / `.z-drop-icon` / `.z-drop-text` | `norm.less` | rendered once in drag ghost DOM (widget.ts line 441) | padding, sizing, font styles | Children of the `.z-drop-ghost` element |
| `.z-drop-ghost` | `norm.less` | created once per drag operation in `createDragGhost_` (widget.ts line 440) | `list-style:none` via `.z-drag > &-ghost` | The floating ghost panel shown during drag |

### Bundling note for decorative core-emitted classes

Several decorative classes are emitted by ZK core (not by any widget), so no per-widget
CSS file auto-load will include them. They **must** live in a file registered in `normFiles`
(Marble: `scripts/build-css.js`). Affected families: `.z-loading*`, `.z-apply-loading*`,
`.z-apply-mask`, `.z-modal-mask`, `.z-error`, `.z-renderdefer`, `.z-initing`, `.z-temp`,
`.z-log`. Full analysis in `zk-core-emitted-selectors.md`.

---

## Classes present in norm.less but not z-prefixed

These are in stock ZK global CSS but don't follow the `z-` convention; a theme should
include them for completeness but they are never JS-toggled as ZK framework mechanics:

| Class | Source | Purpose |
|-------|--------|---------|
| `.noscript` | `norm.less` | No-JS overlay |
| `.gecko .z-draggable-over > *` | `norm.less` | Firefox drag selection fix |
| `.mobile *` | `norm.less` | Remove tap highlight colour on mobile |

---

## Candidates from the brief that were reclassified

| Candidate | Classification | Reason |
|-----------|---------------|--------|
| `.z-inline-block` | decorative | Only set via `setSclass()` by application code or mold templates, never via framework `addClass`/`removeClass` at runtime |
| `.z-word-wrap` | decorative | Appears in norm.less as a utility; no JS source found toggling it at framework level |
| `.z-overflow-hidden` | decorative | Utility class in norm.less; no framework JS toggles it |
| `.z-temp` | decorative | Server-rendered by `HtmlPageRenders.java`; `crashmsg.ts` only reads it, never writes |
| `.z-initing` | decorative | Appended once by `zk.ts _showprgb` during page init; never added/removed in response to runtime state changes |
| `.z-modal-mask` | decorative | Written into innerHTML by `progressbox` / `Mask._draw`; never toggled on an existing node |
| `.z-loading*` / `.z-apply-*` | decorative | Same — created in innerHTML, never runtime-toggled |
| `.z-error` | decorative | Created once by `_Erbx`; `display:none` initial state is mandatory (slideDown mechanism), but the class itself is never added/removed |
| `.z-dd-stackup` | decorative | `drag.ts` sets `className` once at construction, not toggled |
| `.z-borderbox` | decorative | Pure CSS attribution pattern; no JS sets it |
| `.z-focus-a` | decorative | Written into mold HTML at render time; never added/removed by framework JS |
| `.z-clear` | decorative | Written into mold HTML at render time |
| `.z-macro` | decorative | Root class of MacroWidget — static render |
| `.z-scrollbar` | decorative | Written into Scrollbar innerHTML once on render |
| `.z-drag-ghost` | contract* | `widget.ts:createDragGhost_` calls `jq(dgelm).addClass('z-drag-ghost')` — technically added at start of drag but not subsequently toggled; borderline; classified contract because it's a framework `addClass` call |

---

## Overlap / contradiction notes vs. existing reference files

- **`css-flex-classes.md`**: fully subsumed here. The `.z-flex*` family entries in this file
  are identical to that reference. That file's hard rules 1–3 and margin-subtraction analysis
  remain the authoritative deep-dive; this registry is the summary row.
- **`zk-core-emitted-selectors.md`**: no contradiction. This registry adds bundling-note
  reminders but defers to that file for the full pipeline analysis, DOM structure, z-index
  requirements, and the `.z-modal-mask` `!important` pin.
- **`state-classes.md`**: no overlap — state classes (`.z-checkbox-disabled`, etc.) are
  component-scoped and per-widget; they are not global framework classes and are not in this
  registry.
- **`class-name-quirks.md`**: no overlap — that file covers non-intuitive component class
  names; this registry covers only globally-defined classes from norm.less / footer.less.

---

## Compliance procedure (how to verify a theme)

This registry is the **spec** layer. To check whether a theme honours it, run the two-tier check.
The spec + checker describe ZK, not any one theme — they are reusable across every ZK theme.

### Tier 1 — automated presence check (deterministic, CI-gateable)

`tools/check-framework-classes.mjs` reads `framework-classes.json` and scans a theme's
**compiled** CSS output, reporting each contract class as OK / WARN / MISS.

```bash
node .claude/skills/zk-component-rules/tools/check-framework-classes.mjs \
  --theme-css-dir <theme>/target/classes/web/<themename>
```

- `MISS` → the contract class is undefined in the served CSS (the splitlayout-bug failure mode).
  Exit code 1. Any similarly-named classes are listed as possible renames.
- `WARN` (`PRESENT_NO_KEYPROP`) → the class is defined but its key declaration is absent — it may
  be overridden or hard-coded around; escalate to Tier 2.
- `OK` → defined with its key property present.

Point `--theme-css-dir` at the COMPILED bundle dir (`.css.dsp` + `.css`), not the source tree —
this also catches classes that were authored but dropped by the bundler.

### Tier 2 — semantic review (agent-guided; not automatable)

Tier 1 cannot catch the case where a class IS defined but a component **hard-codes its effect onto
a JS-managed container**, defeating ZK's runtime add/remove. Canonical failure: splitlayout had
`display:flex` hard-coded on its root, so when drag-end called `clearCSSFlex()` (removing `.z-flex`)
the root stayed flex and the JS-written inline px sizes never took — the drag never persisted. The
class existed; the hard-coded effect broke it.

Review rule — for every contract class, confirm no component CSS:
1. hard-codes the class's effect (e.g. `display:flex`) onto a container ZK adds/removes the class on;
2. sets a value that fights the JS-set inline style (e.g. `flex-basis:0` on a JS-sized child — see
   `css-flex-classes.md`);
3. renames the class to a utility-style name and references the rename from component CSS.

Containers to review: any root whose children use positive `hflex`/`vflex`; splitlayout caves;
borderlayout region content; grid/listbox frozen meshes; drag sources and drop targets.

## Using this on a NEW theme

The spec + checker are theme-independent. To onboard a new ZK theme:
1. Keep this skill (`zk-component-rules`) in the new theme's repo.
2. Confirm the registry's pinned ZK version matches the theme's target ZK; if not, regenerate
   (re-enumerate norm.less / footer.less / _reset.less + re-grep the client JS for that version).
3. Build the theme, run Tier 1 against its compiled CSS dir.
4. Run the Tier 2 review on any MISS/WARN.
5. Report the PASS/FAIL result (the checker output is the record — it is reproducible by
   re-running the command, so there is no need to persist it as a separate audit doc).
