---
name: zk-component-rules
description: Use when implementing or verifying CSS for any ZK Framework component. Documents ZK's component characteristics, DOM structure, state-handling mechanics, attribute support, CSS file bundling, and quirks — independent of any specific theme. Theme-builders for ZK (Material, Sapphire, custom corporate themes) consult this skill to learn what ZK renders before deciding how it should look. Excludes theme-specific values (colors, spacing, tokens) by design.
---

# ZK Component Rules

You are reading the index of a knowledge base about **how ZK Framework components actually render and behave**. Theme values (colors, sizes, tokens) live in `doc/spec/DESIGN.md` or per-theme references — this skill never repeats them.

## When to use

Invoke this skill whenever you are about to:

- Write or modify CSS for a ZK component
- Verify a ZK component's computed styles match expected values
- Decide a selector for a state (`disabled`, `readonly`, `invalid`, `inplace`, `:hover`, etc.)
- Choose which CSS file to edit when components share a bundle
- Build a preview/test page that exercises component states
- Diagnose a styling regression that depends on DOM structure

If the question is "what should this look like?" — that is a **theme** question; not in scope.
If the question is "where does ZK put the class / what selector applies / what state classes does ZK emit" — that is a **component-rules** question; right place.

## Two-category documentation rule

Every component-knowledge doc is **exactly one** of two categories, and should declare which up front:

- **Category 1 — ZK Component Rules (theme-PORTABLE):** structural/behavioral facts true of any theme (DOM, selectors, state-class enumeration, attribute support, CSS bundling, framework quirks). These live in **this skill**.
- **Category 2 — Theme Design (theme-SPECIFIC):** the values and rules for one theme (colors, spacing, tokens, MUI/Mira refs, visual mockups). These live in `doc/contracts/<comp>.md` + `doc/spec/DESIGN.md`.

Keeping the boundary strict is what makes the portable ZK facts reusable across themes and keeps theme-specific values out of the skill.

## How to use

1. **Read this index first.** It tells you which file covers what.
2. **Load only the files you need** for the component or topic at hand. Each file is independently loadable; do not pre-load everything.
3. **Treat rule files as authoritative.** If you find a contradiction with sub-agent prompts or scattered docs, the rule file wins (and please open an issue/PR to update both).

## Authoring discipline

Before writing or modifying any `components/<comp>.md` (or its companion `doc/contracts/<comp>.md`; when the component has no contract, zk-spec-author writes one first), read **`../marble-theme/reference/contract-tiers.md`** (the Marble contract process). It defines:

- The two-tier contract model (this skill = theme-portable; `doc/contracts/` = theme-specific)
- A/B/C/D predicate classification (Structural / Relational / State-differs / Token-bound)
- What MUST refuse to enter a component-rules file (hex, tokens, design refs, "mirrors iceblue")
- How to mine iceblue CSS as a structural-signal source without importing visual values (with path-search-then-ask fallback — iceblue CSS path is environment-dependent and must not be hardcoded)

Both `zk-spec-author` (when authoring) and `zk-theme-evaluator` (when verifying) must follow this discipline.

## Index

### Cross-cutting reference (`reference/`)

| File | Topic |
|------|-------|
| `reference/state-classes.md` | How ZK marks `disabled` / `readonly` / `invalid` — class-based vs attribute-based per component family |
| `reference/inplace-state.md` | The `inplace` attribute: when ZK adds `.z-{comp}-inplace`, and the three CSS patterns (A: root-bordered, B: child-bordered, C: direct-input) |
| `reference/buttonVisible-attribute.md` | Which input components support `buttonVisible="false"` and where ZK puts the resulting class |
| `reference/focus-vs-focus-within.md` | Composite controls require `:focus-within`; CDP measurement caveat; a focus affordance that changes `border-width` resizes composite inputs (use an inset `box-shadow`) |
| `reference/focus-ring-clipping.md` | `outline` paints OUTSIDE the border box, so a full-bleed focusable (nav link, `<tr>` row, popup item) whose ancestor clips overflow gets its ring cut — use a negative `outline-offset`, or an inset box-shadow on a child that has a paint area. Includes the at-risk DOM shapes and how to assert ring geometry (a colour-only assertion passes on a ring nobody can see; a MISSING `:focus-visible` rule also looks like a wrong one, since the UA draws its own outward ring)
| `reference/edition-availability.md` | Which jar carries CE / PE / EE components (zul.jar / zkex.jar / zkmax.jar); how to look up the jar that registers a component, EE `<extends>` overrides of CE components, internal component names, and the hbox / vbox / splitter deprecation |
| `reference/css-file-bundling.md` | Shared `.css.dsp` outputs (several components in one file) and delivery quirks (a component CSS with no `css-uri`, splitter rules living in `box.css`) |
| `reference/component-css-must-be-bundled-or-css-uri.md` | A component CSS needs a `css-uri` or a global bundle entry; otherwise it is emitted but never loaded, with no error |
| `reference/zk-core-emitted-selectors.md` | Classes written by ZK core (`zk/utl.ts`, `zk/effect.ts`, `zk/zk.ts`, `zk/widget.ts`), not by a widget — busy box, modal mask, error panel, renderdefer — and the DOM and positioning they need |
| `reference/inline-label-alignment.md` | Label-to-field vertical alignment is decided by the container (`div`, `hlayout valign`), not by `.z-label` |
| `reference/viewport-height-fill.md` | Why `html` and `body` need a definite `height: 100%` (ZK outside-click dismiss, no `100vh` scrollbar) |
| `reference/zul-template-patterns.md` | `<apply templateURI>` shadow scoping, content-partial pattern, template name scoping |
| `reference/inline-flex-overflow.md` | Why inline-level input components shrink to content width in a grid cell unless the layout gives them `width: 100%` |
| `reference/class-name-quirks.md` | Non-intuitive z-* class names (`.z-panelchildren`, `.z-row-content`, `.z-rating-icon`, `.z-progressmeter-image`, etc.) — guard against guessing |
| `reference/preview-state-matrix.md` | Convention for preview pages: which states are columns, which are rows, what `—` means |
| `reference/floating-popup-in-body.md` | ZK detaches popups (combobox/datebox/timebox/bandbox/chosenbox/cascader/menupopup…) to `<body>` at runtime, so percentage-based widths reference viewport — never `width: 100%` / `min-width: 100%` on the popup root |
| `reference/css-dsp-pipeline.md` | `.css.dsp` files are not DSP-processed — any `${…}` / `<%@ %>` in a source `.css` will silently delete the entire file from the WCS bundle. Use `/zkau/web/...` absolute URLs or data: URIs for image refs |
| `reference/theme-override-is-replace.md` | When a theme provides a CSS at a stock widget's `<css-uri>` path, ZK serves only the theme version — the stock file is NOT loaded. Theme CSS must re-author every structural rule (positioning, orientation, geometry) the widget JS depends on; partial overrides silently break the widget |
| `reference/css-flex-classes.md` | `.z-flex`/`.z-flex-row`/`.z-flex-column`/`.z-flex-item` are JS-toggled framework classes (`zk/flex.ts`), not theme utilities — define verbatim, never rename, never hard-code their effect on a JS-managed root, never `flex-basis: 0` on JS-sized children, never theme-default margins on flex-capable widgets (ZK subtracts them via `calc()`) |
| `reference/framework-classes.md` | **Canonical registry** of the global JS-toggled contract classes a theme MUST define verbatim, plus the decorative global classes (generalises css-flex-classes.md). Includes the two-tier compliance procedure. Machine-readable mirror: `reference/framework-classes.json`. Checker: `tools/check-framework-classes.mjs --theme-css-dir <compiled-css>` (CI-gateable Tier-1 presence check) |
| `reference/mobile-wheel-picker.md` | On a touch UA, datebox/timebox swap the desktop calendar/stepper for a scrolling **wheel picker** (`.z-calendar-wheel-*` / `.z-timebox-wheel-*`) and force the input `readonly`. Different DOM, JS-driven bottom-sheet geometry, `offsetHeight/3` row invariant — must be styled in the tablet bundle |

### Component-specific (`components/`)

Load only the file for the component you are working on. The file name is the component name (`ls components/`). The edition tags below follow the jar that registers each component (zkex = PE and EE, zkmax = EE only); see `reference/edition-availability.md`.

Files with no summary row below: `components/biglistbox.md`, `columnlayout.md`, `div.md`, `drawer.md`, `dropupload.md`, `errorbox.md`, `html.md`, `image.md`, `imagemap.md`, `label.md`, `scrollbar.md`, `span.md`.

| File | Components covered |
|------|--------------------|
| `components/bandbox.md` | bandbox (popup vs bandpopup, button class location) |
| `components/borderlayout.md` | borderlayout (JS absolute-positioning engine, 5 regions, splitter bar + pill button DOM, collapsed placeholder, z-index map, do-not-flex rule) |
| `components/box.md` | hbox + vbox (default mold is nested TABLES — never change `display`; `-chdex`/`-chdex2` anatomy; no `-cell` classes exist; splitter drag depends on the table chain) |
| `components/breadcrumb.md` | breadcrumb + breadcrumbitem (CE, new in ZK 11.0.0) — T1 nav trail, client-only `maxItems` collapse-to-ellipsis (`.z-breadcrumb-ellipsis` never appears in the mold, only injected post-mount), `data-zk-bc-hidden`/`data-zk-bc-injected` attribute-driven hide (not inline style), item content has no label-wrapping element, disabled forces `<span>` even when `href` is set |
| `components/button.md` | button (vertical orient via `<br/>`, `:has(br)` selector) |
| `components/calendar.md` | calendar (z-calendar-today not on cells, cell-width arithmetic) |
| `components/carousel.md` | carousel + carouselitem (CE, new in ZK 11.0.0) — T2 slideshow; flex track + peek-past-the-end loop clones (`.z-carousel-clone` drops `.z-carouselitem`, inserted client-side only); arrows/indicators structurally absent below 2 slides regardless of `showArrows`/`showIndicators`; `[disabled]` on arrows is a native attribute (loop="false" boundary only), not a class; `.z-carouselitem-active`/`.z-carousel-indicator-active` are client-added on every bind, never in server HTML; `domIcon_()` is dead code for carouselitem; ARIA/live-status entirely EE `za11y`-gated in CE |
| `components/checkbox.md` | checkbox + 3 molds (checkbox, switch, toggle) — mold-prefixed state classes |
| `components/cascader.md` | cascader (EE) — hierarchical tree-picker, read-only trigger + side-by-side cave columns, `.z-cascader-label` / `.z-cascader-placeholder` (not `-input`), `.z-cascader-disabled` class, popup detached to body |
| `components/chosenbox.md` | chosenbox (chip class = `.z-chosenbox-item`, EE-only) |
| `components/codeeditor.md` | codeeditor (CE, new in ZK 11.0.0) — T3 CodeMirror 6 wrapper; ZK's own mold renders only the root + an empty `.z-codeeditor-cave` mount point, everything below (`.cm-editor`/`.cm-scroller`/`.cm-gutters`/`.cm-content`) is CodeMirror's own DOM injected async inside `zk.afterMount`; no readonly class exists (editable compartment only); `lineNumbers=false` removes the gutter subtree outright, not display:none; CodeMirror's own base theme is injected UNLAYERED at runtime so a handful of named anchor classes need `!important` to restyle, while per-token syntax spans and `.ͼ*` hash classes are off-limits (inline-styled/generated, not a stable selector) |
| `components/colorbox.md` | colorbox (current swatch + button + popup w/ picker+palette, geometry-locked sprites, PE) |
| `components/combobox.md` | combobox (split-border, comboitem variants) |
| `components/combobutton.md` | combobutton (pointer-events: auto required for popup click) |
| `components/combo-trio.md` | datebox + timebox + spinner (shared `.z-{c}-input` / `.z-{c}-button` pattern) |
| `components/daterangebox.md` | daterangebox (EE) — two-input root (begin/end) + detached multi-panel calendar popup, `--panels` CSS var drives grid column count, no root `-open` class, `buttonVisible=false` toggles inline style not a class, range-highlight reuses `.z-cell-range-*` from calendar.md |
| `components/data-components.md` | grid + listbox + tree (z-*-odd preferred over nth-child, shared row-based structure) |
| `components/groupbox.md` | groupbox (content area is `.z-groupbox-content`) |
| `components/menubar.md` | menubar (`<ul><li>` wrapper structure) |
| `components/navbar.md` | navbar + nav + navitem + navseparator (EE) — unclassed submenu cave (`.z-nav > ul` only), content is the `<a role=menuitem>` not the `<li>`, TWO content classes (`.z-nav-content` + `.z-navitem-content`) that always need the same rules, `[disabled]` attribute with no `-disabled` class, `za11y` roving tabindex (one Tab stop, arrow keys inside) so Tab-walking never reaches a group header, cave visibility driven by jQuery INLINE display |
| `components/organigram.md` | organigram (EE) — recursive `.z-orgchildren > .z-orgitem > .z-orgnode`, pseudo-element connector bus, `-close` not `-open`, `-non-selectable`, novel T2 |
| `components/portallayout.md` | portallayout + portalchildren (EE) — transparent multi-column drag-drop shell, `.z-portalchildren-frame` titled column, counter badge, ghost/placeholder drag classes |
| `components/panel.md` | panel (content wrapper is `.z-panelchildren`) |
| `components/pdfviewer.md` | pdfviewer (PE) — T3 thin chrome over opaque PDF.js viewport, opacity-driven toolbar reveal, fullscreen pseudo-class |
| `components/progressmeter.md` | progressmeter (CE; fill is `.z-progressmeter-image`) + loadingbar (EE client widget started by `Loadingbar.java`, not a ZUL tag) |
| `components/rating.md` | rating (`.z-rating-icon` not `-star`, orient="vertical") |
| `components/searchbox.md` | searchbox (multi-select trigger + detached popup with search input + checkbox list, EE-only) |
| `components/selectbox.md` | selectbox (native `<select>` element — popup options NOT styleable) |
| `components/separator.md` | separator (transparent base, horizontal-bar variant) |
| `components/signature.md` | signature (EE) — T3 two-canvas wrapper + absolute toolbar strip, native `<button>` tools (no ZK state classes), `.z-signature-toolbar-hide` toggle |
| `components/slider.md` | slider (no disabled/invalid sclass; 4 molds: default/sphere/scale CE + knob EE; knob = SVG rotary dial, CSS-themable boundary documented; rangeslider / multislider disabled = root `-disabled` class) |
| `components/splitter.md` | splitter (cursor mode, ghost element, delivery merged into box.css, drag persists via px on parent-box `<td>`s — table chain required) |
| `components/stepbar.md` | stepbar + step (EE) — circle-marker + pseudo-element connector pattern, `.z-step-complete` not `-completed`, `.z-step-icon` not `-number` |
| `components/tabbox.md` | tabbox (5 orientation classes including accordion) |
| `components/tbeditor.md` | tbeditor (EE) — T3 chrome around Trumbowyg-injected DOM, prefix-namespaced classes (`z-tbeditor-*`), fullscreen body-attach |
| `components/toast-notification.md` | toast / notification (9 position classes, severity classes) |
| `components/toolbar.md` | toolbar (padding lives on `.z-toolbar-content`, not root) |
| `components/window.md` | window (no `.z-window-title` element — plain text in header) |
| `components/goldenlayout.md` | goldenlayout + goldenpanel (EE) — T3 dockable multi-pane layout, `.lm_*` selectors ARE styleable under `.z-goldenlayout` scope; NO forbidden-selectors on lm_* |
| `components/a.md` | a / link (`.z-a`, `[disabled]` via CSS pointer-events) — stub |
| `components/absolutelayout.md` | absolutelayout (JS positions children with `position: absolute`) — stub |
| `components/anchorlayout.md` | anchorlayout + anchorchildren — flat DOM (no body wrapper), `anchor=` JS inline-width sizing (% and delta); stock ZK uses `float: left` |
| `components/caption.md` | caption (no `.z-caption-text` element — plain text) — stub |
| `components/hlayout-vlayout.md` | hlayout / vlayout + hbox / vbox (`.z-hlayout-inner` wrapper) — stub |
| `components/inputgroup.md` | inputgroup (`display: inline-flex` root, `.z-inputgroup-text`) — stub |
| `components/paging.md` | paging (`.z-paging-previous` NOT `-prev` — naming trap) — stub |
| `components/popup.md` | popup (generic floating container, JS-positioned) — stub |
| `components/toolbarbutton.md` | toolbarbutton (`.z-toolbarbutton-checked`, CSS in `footer.css.dsp`) — stub |
| `components/splitlayout.md` | splitlayout (EE) — T1 two-pane resizable container, cave-top/bottom/left/right, splitter bar with collapse button and three icon idiom |
| `components/cropper.md` | cropper (EE) — T3 Jcrop-backed image cropper; only `.z-cropper` wrapper and `.z-cropper-toolbar` are theme-owned; `.z-cropper-canvas` does NOT exist (actual Jcrop holder is `.z-cropper-holder`); toolbar is JS-positioned and hidden at rest |
| `components/timepicker.md` | timepicker (EE) — T1 combo-pattern input + clock-button + detached time-option-list popup; `.z-timepicker-disabled` appears on root (component disabled) AND on `<a>` button (buttonVisible=false — different semantics); popup is raw `<li>` items not ZK widgets; separate CSS file `zkmax/inp/css/timepicker.css` not merged into combo.css.dsp |
| `components/coachmark.md` | coachmark (EE) — T2 guided-tour card; `.z-coachmark` is transparent positional wrapper; `.z-coachmark-content` is the visual card; `.z-coachmark-open` drives animation; pointer arrow on `.z-coachmark-pointer.z-coachmark-{up\|down\|left\|right}`; mask is a `zk.eff.FullMask` sibling in `<body>`; CSS in `zkmax/nav/css/coachmark.css` |
| `components/tablelayout.md` | tablelayout + tablechildren (EE) — HTML `<table>`-based grid layout; root is `<table>` (never override display); cells are `<td>.z-tablechildren`; `<tr>` rows carry no ZK class; colspan/rowspan are native HTML attrs; gutter via `border-spacing`; no state classes |
| `components/rowlayout.md` | rowlayout + rowchildren (EE) — 12-column proportional-grid layout; `.z-rowlayout` float-clearfix container; `.z-rowchildren.colspanN[.offsetN]` float children; JS writes all inline widths — do NOT override with CSS `!important`; own file `zkmax/layout/css/rowlayout.css.dsp` |
| `components/confirmpopup.md` | confirmpopup (CE, new in ZK 11.0.0) — T2 anchored popover card (extends but mostly overrides `Popup`); no `.z-popup-content` wrapper, header/body/footer are direct root children; optional header/icon/message; two-layer CSS-triangle arrow re-anchored via JS on placement change; severity enum (`info/success/warning/error/neutral`, default `warning`) shared with native badge/chip, recolors only the icon; fixed cancel-then-ok DOM order independent of `defaultFocus`; CE mold emits no ARIA (za11y EE add-on layers `role="alertdialog"`); own file `confirmpopup.css.dsp` |
| `components/linelayout.md` | linelayout + lineitem (EE) — T2 vertical/horizontal timeline; tripartite column structure (first/cave/last); `.z-linelayout-vertical` / `.z-linelayout-horizontal` orientation classes; `.z-lineitem-point` circle with hidden state; first/last area `<div>`s are moved OUT of cave at bind_() via virtual-parent architecture; new CSS at `zkmax/layout/css/linelayout.css` |
| `components/cardlayout.md` | cardlayout (EE) — one `.z-cardlayout-inner` per child, JS-positioned with inline `left`/`top`; inner must be absolute inside a relative, overflow-hidden root |
| `components/fileupload.md` | fileupload (CE) — Button subclass with no `multiple` property; multi-file upload is set through the `upload` attribute string |

## Reading ZK source for a component

A component is a `.ts` widget, a `mold/*.js` template and a `css/*.css` stylesheet in the same package under `zul/src/main/resources/web/js/zul/{pkg}/` (`Component.ts`, `mold/component.js`, `css/component.css`). `ls zul/src/main/resources/web/js/zul/` lists the packages. EE components live under `../zkcml/{zkex,zkmax}/src/main/resources/web/js/`.

- **Class names come from `$s()`.** `this.$s('name')` in a mold or widget produces `z-{component}-{name}` (for example `this.$s('selected')` in Button gives `z-button-selected`). Search the mold and `domClass_()` for `$s(` to get the real class list.
- **A subclass often has no mold of its own.** It reuses its parent's mold (Radio reuses Checkbox's, so there is no separate radio mold or radio stylesheet). Read the parent when a component has no `mold/` file.
- **Check the parent class for shared behaviour.** Intbox and Decimalbox extend `NumberInputWidget`, not Textbox.

## What this skill contains

Only theme-independent ZK facts: DOM structure, selectors, state classes, attribute support, CSS bundling, and how ZK JS behaves. Values and design choices (colors, token names, spacing, borders, radius, shadows, transitions, fonts, design-system references) belong in **theme** documentation: the `marble-theme` skill and `doc/spec/DESIGN.md` for the current theme. If another theme is built on ZK, it keeps its own documentation; this skill stays the same.

## How to add a new rule

1. Decide: is the rule **a ZK property** (how the framework renders) or **a theme choice** (what looks good)?
   - ZK property → goes here.
   - Theme choice → goes in `doc/spec/DESIGN.md`.
2. Decide scope: cross-cutting → `reference/<topic>.md`; single component → `components/<name>.md`.
3. Add the rule with a one-line statement plus a code example or selector snippet. Cite the source (component source file, official doc) so future maintainers can verify.
4. Update this index if you added a new file.
