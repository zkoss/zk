# ZK Framework Global CSS Classes — Canonical Registry

The registry follows the ZK version in `gradle.properties`. Regenerate this file when the target ZK version changes, by re-enumerating ZK's stock global CSS and re-grepping the client JS for `addClass` / `removeClass` / `className =` calls.

This file is the **spec layer** of the three-layer framework-class compliance check
that ships with this skill. It generalises and supersedes
`css-flex-classes.md` for the flex family; cross-links to `zk-core-emitted-selectors.md`
for bundling rules.

---

## Why this registry exists

ZK's client-side JS/TS engine toggles certain CSS classes at runtime (adding and removing
them on live DOM nodes). A theme that omits, renames, or hard-codes around such a class
silently breaks ZK behaviour — **no error, just wrong layout**.

The proven failure: a theme renamed `.z-flex` → `.z-d-flex` in a utility-class pass.
ZK's css-flex engine added `.z-flex` to containers at runtime, but the CSS rule no longer
matched, so every `hflex`/`vflex` container stayed `display: block`. Drag-persistence in
`Splitlayout` was a secondary casualty (see `css-flex-classes.md` hard rule 2).

---

## JS-toggled CONTRACT classes (theme MUST define verbatim)

A class is marked **contract** only when the research found actual `addClass` / `removeClass`
/ `className =` calls that add and remove it at runtime. The theme **must** define these
selectors with values that are functionally equivalent to stock — and must never hard-code
their effect onto a component root that ZK JS manages. The machine-readable list is
`framework-classes.json`.

| Class | JS/TS toggling site | Required CSS declaration | Notes |
|-------|---------------------|--------------------------|-------|
| `.z-flex` | `zk/flex.ts:applyCSSFlex` / `clearCSSFlex` | `display: flex` | Container of hflex/vflex children |
| `.z-flex > :not(.z-flex-item)` | same (selector implied by `.z-flex`) | `flex-shrink: 0` | Non-flex siblings inside a flex container must not shrink |
| `.z-flex-row` | `zk/flex.ts:applyCSSFlex` / `clearCSSFlex` | `flex-direction: row` | Added alongside `.z-flex` |
| `.z-flex-column` | `zk/flex.ts:applyCSSFlex` / `clearCSSFlex` | `flex-direction: column` | Added alongside `.z-flex` |
| `.z-flex-item` | `zk/flex.ts:applyCSSFlex` / `clearCSSFlex` | `flex: 1 1 0; min-height: 0; min-width: 0` | Added to each flexed child; removed before ZK writes inline px sizes |
| `.z-dragged` | `zk/widget.ts:cloneDrag_` / `uncloneDrag_` | theme-defined de-emphasis of the drag source | Applied to the drag-source node while drag is in flight |
| `.z-drag-over` | `zk/widget.ts:dropEffect_` (JS hook; **no stock CSS**) | theme-defined drop-target highlight | Added to the drop-target root when a draggable is over it |
| `.z-drag-ghost` | `zk/widget.ts` ghost clone path (JS hook; **no stock CSS**) | theme-defined floating clone | The body-appended clone `#zk_ddghost` for **non-TR** drags |
| `.z-drop-ghost` | `zk/widget.ts:ghost` (JS hook; **no stock CSS**) | theme-defined message chip | Root of the message ghost `#zk_ddghost` for **TR** (listitem/row) drags |
| `.z-drop-content` | `zk/widget.ts:ghost` (JS hook; **no stock CSS**) | theme-defined inner wrapper | Inner wrapper inside the message ghost |
| `.z-drop-icon` | `zk/widget.ts:ghost` + `DD_dragging` (JS hook; **no stock CSS**) | theme-defined mask glyph | Icon span `#zk_ddghost-img`; toggles `z-icon-plus-circle`/`z-icon-ban` |
| `.z-drop-text` | `zk/widget.ts:ghost` (JS hook; **no stock CSS**) | theme-defined message text | Message text span inside the ghost |
| `.z-drop-allow` | `zk/widget.ts:DD_dragging` (JS hook; **no stock CSS**) | theme-defined valid-drop state | Toggled on the message ghost over a valid target |
| `.z-drop-disallow` | `zk/widget.ts:ghost` + `DD_dragging` (JS hook; **no stock CSS**) | theme-defined invalid-drop state | Initial + invalid state on the message ghost |
| `.z-word-nowrap` | `zul/mesh/Frozen.ts:bind_` / `unbind_` | `white-space: nowrap` | Added to grid/listbox body + foot when `<frozen>` is bound |
| `.z-renderdefer` | `zk/widget.ts:redraw_` | sizing + spinner background | Placeholder emitted while `_renderdefer` timer has not yet fired |

### Detailed required CSS for contract classes

```css
/* must load LAST (after component CSS) */
.z-flex { display: flex; }
.z-flex > :not(.z-flex-item) { flex-shrink: 0; }
.z-flex-row { flex-direction: row; }
.z-flex-column { flex-direction: column; }
.z-flex-item { flex: 1 1 0; min-height: 0; min-width: 0; }

.z-word-nowrap { white-space: nowrap; }
/* .z-dragged, .z-drag-over, .z-drag-ghost, .z-drop-allow, .z-drop-disallow: theme-defined look */
/* .z-renderdefer: sizing + spinner background (see the decorative section) */
```

The flex block must be byte-equivalent to stock — **do not modify**.

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

A theme that omits them loses visual polish but does not break functionality. Stock values
are in ZK's stock global CSS; the table gives only what each class is for and how ZK uses it.

| Class | JS/TS reference | Notes |
|-------|-----------------|-------|
| `.z-borderbox` | none — pure CSS attribution pattern | `box-sizing: border-box`; applies to `[class^="z-"]` globally; `.z-borderbox` is the opt-in for non-ZK elements |
| `.z-inline-block` | set via `setSclass("z-inline-block")` in ZUL — not toggled by framework | Helper for ensuring dimension on inline elements |
| `.z-word-wrap` | not toggled at runtime by framework JS | Declarative utility |
| `.z-overflow-hidden` | not toggled at runtime by framework JS | Declarative utility |
| `.z-dd-stackup` | `zk/drag.ts` sets `stackup.className = 'z-dd-stackup'` at construction — created once, not toggled | Invisible overlay during drag to capture mouse events in iframes |
| `.z-temp` | `HtmlPageRenders.java` renders it server-side; `crashmsg.ts` only reads `querySelectorAll('.z-temp')`, never toggles | Initial loading scrim; server-emitted on page load, replaced by `z-loading` block after ZK mounts |
| `.z-initing` | `zk/zk.ts` `_showprgb` appends a `<div class="z-initing">` as a child of the `z-temp`/`z-loading` block when `_zk.pi` flag is set — emitted once, not subsequently toggled | Page-initialisation indicator spinner |
| `.z-loading` | `zk/utl.ts:progressbox` creates it in innerHTML — not a runtime toggle on existing nodes | showBusy message box (global). **Bundling note**: must be in the global bundle served by `zk.wcs` — see `zk-core-emitted-selectors.md` |
| `.z-loading-indicator` | created by `progressbox` | Inner content of showBusy box |
| `.z-loading-icon` | created by `progressbox` | Spinner element. Theme must supply a spinner image or CSS animation |
| `.z-apply-loading` | `zk/effect.ts:Mask._draw` creates it in innerHTML | Per-component showBusy overlay. **Bundling note**: must be in the global bundle served by `zk.wcs` |
| `.z-apply-loading-indicator` | created by `Mask._draw` | |
| `.z-apply-loading-icon` | created by `Mask._draw` | |
| `.z-apply-mask` | created by `Mask._draw` | Scrim for per-component busy overlay |
| `.z-modal-mask` | `zk/utl.ts:progressbox` and `zk/drag.ts` create it in innerHTML | Full-page modal scrim. See `zk-core-emitted-selectors.md` for the inline `top/left` quirk requiring `!important` |
| `.z-error` | `zk/zk.ts:_Erbx` creates via innerHTML; `display:none` is MANDATORY (slideDown needs hidden start) | JS debug error panel. See `zk-core-emitted-selectors.md` for full DOM structure |
| `.z-log` | `zk/zk.ts` sets `logBox.className = 'z-log'` — created once for `zk.log()` debug output | `zk.log()` debug output box |
| `.noscript` | static HTML `<noscript>` tag — no JS | No-JS fallback overlay |
| `.z-focus-a` | rendered in mold JS (listbox, tree, window, calendar, menupopup) as a static class — never toggled | Invisible focus anchor for keyboard navigation; must exist but must be visually hidden |
| `.z-clear` | rendered in mold JS (toolbar, toolbarpanel, frozen, tabbox) as a static class — never toggled | Float-clearfix helper |
| `.z-macro` | emitted by `MacroWidget` as root class — static render | Root container for macro components |
| `.z-scrollbar` | `zul/Scrollbar.ts` renders it in innerHTML — static class | Custom scrollbar widget; styles must cover all `.z-scrollbar-*` sub-classes (`-vertical`, `-horizontal`, `-indicator`, …) |
| `.z-upload` | `zul/Upload.ts:_createUploadHTML` renders `<span class="z-upload">` | Invisible file-input wrapper for upload buttons |
| `.z-upload-icon` | static in upload widget | Upload progress indicator |
| `.z-fileupload-add` / `.z-fileupload-remove` | `zul/Upload.ts` sets `sclass='z-fileupload-remove …'` and `sclass='z-fileupload-progress'` — set at widget creation time | File-upload dialog buttons |
| `.z-fileupload-progress` | same | |
| `.z-fileupload-manager` | `zul/Upload.ts:setSclass('z-fileupload-manager')` — set once, not toggled | |
| `.z-drop-content` / `.z-drop-icon` / `.z-drop-text` | rendered once in drag ghost DOM (`widget.ts`) | Children of the `.z-drop-ghost` element |
| `.z-drop-ghost` | created once per drag operation in `createDragGhost_` (`widget.ts`) | The floating ghost panel shown during drag |

### Bundling note for decorative core-emitted classes

Several decorative classes are emitted by ZK core (not by any widget), so no per-widget
CSS file auto-load will include them. They **must** live in a file that is part of the global bundle
served by `zk.wcs`. Affected families: `.z-loading*`, `.z-apply-loading*`,
`.z-apply-mask`, `.z-modal-mask`, `.z-error`, `.z-renderdefer`, `.z-initing`, `.z-temp`,
`.z-log`. Full analysis in `zk-core-emitted-selectors.md`.

---

## Classes in ZK's stock global CSS that are not z-prefixed

These don't follow the `z-` convention; a theme should include them for completeness but
they are never JS-toggled as ZK framework mechanics:

| Class | Purpose |
|-------|---------|
| `.noscript` | No-JS overlay |
| `.gecko .z-draggable-over > *` | Firefox drag selection fix |
| `.mobile *` | Remove tap highlight colour on mobile |

---

## Candidates from the brief that were reclassified

| Candidate | Classification | Reason |
|-----------|---------------|--------|
| `.z-inline-block` | decorative | Only set via `setSclass()` by application code or mold templates, never via framework `addClass`/`removeClass` at runtime |
| `.z-word-wrap` | decorative | Utility class in stock global CSS; no JS source found toggling it at framework level |
| `.z-overflow-hidden` | decorative | Utility class in stock global CSS; no framework JS toggles it |
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
  names; this registry covers only globally-defined classes from ZK's stock global CSS.

---

## Compliance procedure (how to verify a theme)

This registry is the **spec** layer. To check whether a theme honours it, run the two-tier check.
The spec + checker describe ZK, not any one theme — they are reusable across every ZK theme.

### Tier 1 — automated presence check (deterministic, CI-gateable)

`tools/check-framework-classes.mjs` reads `framework-classes.json` and scans a theme's
**compiled** CSS output, reporting each contract class as OK / WARN / MISS.

```bash
node .claude/skills/zk-component-rules/tools/check-framework-classes.mjs \
  --theme-css-dir zul/codegen/resources/web
```

For the EE sheets, point `--theme-css-dir` at `../zkcml/zkmax/codegen/resources/web`.

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
2. Confirm the registry matches the theme's target ZK version (`gradle.properties`); if not, regenerate
   (re-enumerate ZK's stock global CSS + re-grep the client JS for that version).
3. Build the theme, run Tier 1 against its compiled CSS dir.
4. Run the Tier 2 review on any MISS/WARN.
5. Report the PASS/FAIL result (the checker output is the record — it is reproducible by
   re-running the command, so there is no need to persist it as a separate audit doc).
