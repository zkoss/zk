# tbeditor

A WYSIWYG rich-text editor built on the Trumbowyg jQuery plugin. ZK wraps Trumbowyg in a two-element shell (`z-tbeditor` > `z-tbeditor-cnt`); the rest of the DOM — the toolbar button pane, the `contenteditable` editor area, and the hidden `<textarea>` — is injected by Trumbowyg on `bind_`. All Trumbowyg CSS class names are prefixed with `z-tbeditor-` because `_preprocessConfig()` forces `config.prefix = 'z-tbeditor-'`.

ZK-EE only (ships in `zkmax.jar`). Requires the Trumbowyg plugin and DOMPurify to be loaded.

## DOM structure

```
.z-tbeditor                          (<div>, ZK root; contains the Trumbowyg box)
└─ #uuid-cnt                         (<div>, the container Trumbowyg is initialised on)
   └─ .z-tbeditor-box                (<div>, Trumbowyg outer box — injected by plugin on bind_)
      ├─ .z-tbeditor-button-pane     (<ul>, the formatting toolbar; z-index: 11)
      │   └─ .z-tbeditor-button-group  (<li> groups of toolbar buttons)
      │       └─ <button>            (individual icon buttons; 35×35px targets)
      └─ .z-tbeditor-editor          (<div contenteditable="true">, the editable area)
         (siblings)
      └─ .z-tbeditor-textarea        (<textarea>, hidden in editor-visible mode; shown in HTML-source mode)
```

Visual states add these modifier classes to `.z-tbeditor-box`:
- `.z-tbeditor-editor-visible` — normal editing mode; the `<textarea>` is collapsed to 1px/25% to allow copy-paste, not truly hidden.
- `.z-tbeditor-editor-hidden` — "view HTML source" mode; the editor is `display:none`, the textarea is shown.
- `.z-tbeditor-fullscreen` — fullscreen overlay mode; `position:fixed`, 100vw × 100vh.
- `.z-tbeditor-disabled` — editor read-only; toolbar button opacity drops, textarea background cleared.
- `.z-tbeditor-box-blur` — editor does not have focus; text is optionally blurred (when resetCss option is active).

## State classes

- `.z-tbeditor-editor-visible` — on `.z-tbeditor-box` when editing. This is the default post-bind_ state.
- `.z-tbeditor-editor-hidden` — on `.z-tbeditor-box` when the "view HTML source" toolbar button is active.
- `.z-tbeditor-fullscreen` — on `.z-tbeditor-box` (and added to `<body>` via `.z-tbeditor-body-fullscreen`) when the fullscreen button is activated.
- `.z-tbeditor-disabled` — on `.z-tbeditor-box` when the editor is put into disabled/read-only state.
- `.z-tbeditor-box-blur` — on `.z-tbeditor-box` when focus is lost from the editor area.
- `.z-tbeditor-active` — on individual `<button>` elements when that formatting command is active (e.g. Bold is toggled on).
- `.z-tbeditor-disable` — on `.z-tbeditor-button-pane` when the entire toolbar is deactivated.
- `.z-tbeditor-open-dropdown` — on a `<button>` that has an attached dropdown; an `::after` caret is rendered.
- `.z-tbeditor-dark` — on `.z-tbeditor-box` for opt-in dark variant; not toggled by ZK server-side — must be set via `config`.

States rely on Trumbowyg plugin JS toggling these classes; ZK's server side does not toggle them (except for the `disabled` wrapper class on the box).

## Attribute support

- `width="…"` — applied to `.z-tbeditor` root via `jq(this.$n_()).width(value)` by `_setSize`.
- `height="…"` — computes `contentHeight` by subtracting the button-pane height and box margins; applied as `min-height` to `#uuid-cnt`, the sibling `<textarea>`, and the box parent via `_assignMinHeight`.
- `hflex` / `vflex` — handled by `setFlexSize_`. Height-flex computes content height the same way as `height` attribute.
- `value="…"` — HTML string; loaded into Trumbowyg on bind_ via `trumbowyg('html', value)` or `trumbowyg('empty')`.
- `config` — Trumbowyg options object set server-side only. The `prefix` key is always overridden to `'z-tbeditor-'` by `_preprocessConfig()`.

`disabled`, `readonly`, `inplace`, `buttonVisible` are **not** supported — there is no ZK attribute that maps to these. The disabled state on the editor can be achieved by passing Trumbowyg config options; it does not add a class to the ZK root element, only to `.z-tbeditor-box`.

## Composition invariants

- **Box fills root.** `.z-tbeditor-box` is `display:block; width:100%; position:relative` (from Trumbowyg defaults). The theme must not change `position` — Trumbowyg absolutely positions the overlay and modal elements relative to the box.
- **Button-pane is z-index:11.** Theme must not reduce the z-index of `.z-tbeditor-button-pane` below 11; the overlay (`z-tbeditor-overlay`) and dropdown (`.z-tbeditor-dropdown`) use z-index 10 and 11 respectively.
- **Height is JS-computed.** When `height` or `vflex` is set, ZK JS subtracts the button-pane's `outerHeight` from the total and applies the remainder as `min-height` on the content div. If the theme changes the button-pane height, `_calcEditorHeight` will use the new measured value correctly — but the theme must not apply `height` (only `min-height`) to the editor or textarea, or JS arithmetic will conflict.
- **Textarea sibling to editor.** Trumbowyg always renders both `.z-tbeditor-editor` and `.z-tbeditor-textarea` inside `.z-tbeditor-box`. They are siblings, not nested. The editor-visible / editor-hidden classes govern which is visible.
- **SVG icons path.** The SVG sprite path is set by `jq.trumbowyg.svgPath = zk.ajaxURI(…)` on every `bind_`. If the theme supplies icon overrides they must be delivered as a separate SVG sprite via the `config.svgPath` option, not by replacing the sprite URI.
- **Fullscreen attaches to body.** In fullscreen mode `.z-tbeditor-box` is `position:fixed` and a `.z-tbeditor-body-fullscreen` class is added to `<body>` to suppress body scrolling. The theme must account for the fact that the box leaves its stacking context.
- **onSize callback.** `tbeditor` listens to `zWatch.onSize`. When the parent container resizes and neither `vflex` nor `hflex` is set, the JS recalculates `width` / `min-height`. The theme must not fight this with `!important` on `width` or `height` on the box.

## Relational invariants

Theme-agnostic geometric/quantitative predicates. Verify by measurement with the stated tolerance; the absolute pixel value is theme choice but the relation must hold.

- **Box is a vertical flex column.** `.z-tbeditor-box` must have `display: flex` and `flex-direction: column`. The button pane is the first child (rendered above the content frame). A theme that changes `flex-direction` or switches to a non-flex layout breaks the chrome-on-top / content-below geometry that the JS height calculations depend on.
- **Button pane spans full box width.** Computed `width` of `.z-tbeditor-button-pane` equals computed `clientWidth` of `.z-tbeditor-box` ± 2px. Iceblue achieves this with `width: 100%` on the pane; any theme approach that achieves full-width coverage satisfies the invariant.
- **Button pane has a measurable positive height.** Computed `min-height` (or actual rendered height) of `.z-tbeditor-button-pane` must be > 0. The `_calcEditorHeight` JS call reads `tbPane.outerHeight()` and subtracts it from the editor's total height; a zero-height pane causes the editor to extend to the full box height and clips the button row. (Iceblue sets `min-height: 36px`.)
- **Button pane is a positioning context.** `.z-tbeditor-button-pane` must have `position: relative`. The pane's `::after` separator pseudo-element uses `position: absolute; top: <pane-height>` to draw a hairline below the pane. If the pane is not a positioning context the separator floats to the nearest ancestor context and may appear at the wrong layer. (Iceblue evidence: `position: relative` explicit in `.z-tbeditor-button-pane`.)
- **Toolbar button targets are square (1:1 aspect ratio).** Each `<button>` inside `.z-tbeditor-button-pane` must have computed `height ≈ width` (± 2px). The buttons hold SVG icons; a height significantly shorter than width clips the SVG vertically; a width significantly shorter than height creates an oddly tall non-square target. (Iceblue: `35px × 35px`; a theme may use a different uniform size but must maintain the square ratio.)
- **Button height matches group-separator height.** The `::before` pseudo-element that separates button groups (`.z-tbeditor-button-group:not(:empty) + .z-tbeditor-button-group::before`) must have a height equal to the button height ± 2px, so the separator line spans the full button row height without protruding. (Iceblue: separator `height: 35px` matching button `height: 35px`.)
- **Content frame fills remaining space below the pane.** In a height-constrained editor (`height` or `vflex` set), `.z-tbeditor-editor` (or `.z-tbeditor-editor-box` in newer DOM variants) must occupy all space below the pane: `editor.offsetTop + editor.clientHeight ≈ box.clientHeight ± 2px`. This is a layout invariant driven by the flex child expanding to fill the column.
- **Fullscreen box covers the full viewport.** When `.z-tbeditor-fullscreen` is present on `.z-tbeditor-box`, computed `width` and `height` of the box must equal `window.innerWidth` and `window.innerHeight` ± 2px, and the box must be positioned at the top-left corner of the viewport (`top ≈ 0, left ≈ 0`). (Iceblue: `position: fixed; top: 0; left: 0; width: 100%; height: 100%`.) The theme must not override `position` on `.z-tbeditor-fullscreen.z-tbeditor-box`.
- **In fullscreen, content area fills height below the pane.** `.z-tbeditor-fullscreen .z-tbeditor-editor` (and `.z-tbeditor-textarea`) must occupy all viewport height minus the pane height: `editor.offsetHeight ≈ window.innerHeight − pane.offsetHeight ± 2px`. (Iceblue: `height: calc(100% - 37px) !important`.)
- **Dropdown appears directly below its triggering button.** When `.z-tbeditor-dropdown` is visible, its `top` coordinate must be ≥ the triggering button's bottom edge, and its `left` coordinate must be within ± 4px of the triggering button's left edge. (Iceblue: `margin-left: -1px` aligns the dropdown to the button's left border; the `z-index: 11` ensures it paints above the pane.)
- **Box border-radius does not clip the button-pane's bottom edge.** If the theme applies `border-radius` to `.z-tbeditor-box` and `overflow: hidden`, the clipping must only round the outer corners of the entire box, not cut into the flat bottom edge of the button pane. Verify: the pane's bottom separator line (the `::after` hairline) must be fully visible with no arc clipping.

## State-differs invariants

Theme-agnostic predicates asserting that two states must be visually distinguishable. Verify by rendering both states and comparing computed styles on the specified selector; assert that **at least one** of the listed properties differs. The disjunction gives themes design freedom while keeping the state machine readable.

- **`editor-visible` vs `editor-hidden` (view-HTML-source mode).** When `.z-tbeditor-editor-hidden` is on `.z-tbeditor-box`:
  - `.z-tbeditor-editor` must have `display: none` (invisible).
  - `.z-tbeditor-textarea` must be visible (`display` ≠ `none` AND `opacity` ≠ 0 AND `height` > 1px).
  Both are invariants, not disjunctions — the visibility swap is a hard contract, not a theme-choice surface. (Iceblue: `.z-tbeditor-editor-hidden .z-tbeditor-editor { display: none }` + `.z-tbeditor-editor-hidden .z-tbeditor-textarea { display: block }`.)
- **`editor-visible` textarea collapsing.** When `.z-tbeditor-editor-visible` is on `.z-tbeditor-box`, `.z-tbeditor-textarea` must be effectively invisible: `opacity: 0` OR `height ≤ 1px` OR both. The textarea is kept in the DOM (for copy-paste accessibility) but must not be interactable. At least one suppression mechanism must be present.
- **Toolbar button resting vs `hover`/`focus`.** On `.z-tbeditor-button-pane button:hover` (or `:focus`) compared to the same button at rest, at least one of: `background-color`, `box-shadow`, `outline` — must differ. Themes may use any surface-layer technique (background fill, ring, shadow) to signal the interactive affordance.
- **Toolbar button resting vs `active` (toggled formatting command).** On `.z-tbeditor-button-pane button.z-tbeditor-active` compared to the same button at rest (no `.z-tbeditor-active`), at least one of: `background-color`, `color`, `box-shadow`, `border-color` — must differ. The active state communicates that a text-formatting mode (e.g. Bold) is currently engaged; it must be visually distinct from both resting and hover.
- **Toolbar button `active` vs `hover`.** If a button simultaneously carries `.z-tbeditor-active` and is hovered, the visual result may match one of the two states (theme choice), but neither resting, hover, nor active may all look identical — at least one pair must be distinguishable.
- **Disabled toolbar buttons vs enabled.** On `.z-tbeditor-disabled .z-tbeditor-button-pane button:not(.z-tbeditor-not-disable)` compared to the same button without `.z-tbeditor-disabled` on the box, at least one of: `opacity` (< 1 vs 1), `color`, `fill` (on the inner SVG) — must differ. Additionally, computed `cursor` must equal `default` (not `pointer`). (Iceblue: `opacity: .2; cursor: default`.)
- **Disabled buttons must not show hover response.** Computed style of `.z-tbeditor-disabled .z-tbeditor-button-pane button:hover` for `background-color`, `box-shadow`, and `outline` must equal the disabled-resting style (i.e. no hover affordance is added when the editor is disabled). Themes that apply hover via `.z-tbeditor-button-pane button:hover` must guard with `:not(.z-tbeditor-disabled *)` or an equivalent negative selector.
- **`.z-tbeditor-box` default vs `disabled` border/appearance.** When `.z-tbeditor-disabled` is on `.z-tbeditor-box`, at least one of: `border-color`, `opacity` (on the whole box or pane), `background-color` — must differ from the default state, to communicate that the editor is not editable.
- **Fullscreen vs in-place box.** When `.z-tbeditor-fullscreen` is on `.z-tbeditor-box`, `position` must equal `fixed` (hardened invariant from Trumbowyg). Additionally, at least one of: `border-width`/`border-style` (iceblue removes the border entirely in fullscreen), `box-shadow` (elevation increase), `background-color` — must differ from the in-place box, to signal the mode change.
- **`.z-tbeditor-box-blur` vs focused editor.** When `.z-tbeditor-box-blur` is present, the rendered text content of `.z-tbeditor-editor` must be less legible than when focused. At least one of: text `color: transparent` with `text-shadow`, reduced `opacity` on the editor, or `filter: blur(…)` — must be active. This invariant exists only when the `resetCss` Trumbowyg option is enabled; themes that opt into blur-on-unfocus must implement one of these mechanisms.
- **Group separator visible between adjacent groups.** The `::before` pseudo-element of `.z-tbeditor-button-group:not(:empty) + .z-tbeditor-button-group` must have a non-transparent `background-color` (or `border-color`) and a rendered `width ≥ 1px` and `height > 0`. At least one visual property distinguishing the separator from the pane background must be present — themes may use a line, a dot, or a gap, but some demarcation between adjacent non-empty groups is required.

## Sibling decomposition

tbeditor is a two-sibling pattern:

- **Outer chrome** (border, radius, background of the wrapper box): mirrors the `window` (embedded mold) pattern for structural chrome — see `components/window.md`.
- **Button row** (toolbar button strip across the top): mirrors the `toolbar` pattern for a horizontal strip of icon buttons — see `components/toolbar.md`. The internal button targets are 35×35px Trumbowyg `<button>` elements, analogous to toolbarbuttons.
- **Content area** (`.z-tbeditor-editor` / `.z-tbeditor-textarea`): opaque to the theme — style only background and text color at the box level; do not alter `contenteditable` layout.

## Bundle

`tbeditor.css.dsp` — declared in zkmax's `lang-addon.xml` as the `css-uri` for the `tbeditor` widget's default mold. The legacy Less source is `less/tbeditor.less`; the theme replaces it entirely.

## Edition

EE (zkmax). Requires a valid ZK EE license. The widget is registered under the `zkmax.tbeditor` package.

## Notes

- All Trumbowyg internal class names are namespaced to `z-tbeditor-` via `config.prefix`. The prefix substitution means the LESS source's `@{zprefix}-*` patterns all resolve to `z-tbeditor-*`. Do not confuse Trumbowyg's upstream class names (e.g. `trumbowyg-box`) with the actual rendered names (`z-tbeditor-box`).
- The `#uuid-cnt` element (`$n_('cnt')`) has an element-id suffix of `-cnt`. Its jQuery reference is stored as `_jqCnt` and is the target of `min-height` writes. Do not apply `overflow: hidden` to this element — it clips the dropdown and modal.
- ZK fires the `onChange` event on blur (mapping to Trumbowyg's `tbwblur`) and `onChanging` on each `tbwchange` (roughly every keystroke). There is no `:invalid` or error state injected by ZK.
- The dark variant (`.z-tbeditor-dark`) is Trumbowyg-native. It is not controlled by a ZK attribute and is not part of the standard theme surface; theme CSS should not conflict with it unless explicitly extending dark-mode support.
- `_calcEditorHeight` queries `tbBox.css('marginTop')`, `marginBottom`, and `border` — these are read from computed styles. If the theme sets `margin` or `border` on `.z-tbeditor-box`, those values will be measured and factored out automatically.
