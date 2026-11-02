# Component design notes (Marble)

Marble-specific reasons and values for single components. They used to sit in the theme-independent
`zk-component-rules` skill; that skill now holds only ZK structure, so the Marble choices live here.
One section per component, reason first. A value appears only where it is a design decision or a
measurement; for the rest, read the component CSS (most sections name the file). Cross-component
rules are in `selected-state-families.md`, `popup-tokens.md`, `focus-affordance-no-layout-shift.md`
and `doc/spec/DESIGN.md`.

## anchorlayout

Marble replaces ZK's `float: left` on `.z-anchorchildren` with `display: flex; flex-wrap: wrap` on
the root. ZK writes inline percentage widths on `.z-anchorchildren`, so a flex item keeps that width
only with `flex-shrink: 0` (otherwise the flex algorithm compresses a child below the JS-set width),
and `min-width: 0` prevents flex blowout when a child's content is wider than its width. There is no
`gap`: with inline percentage widths any gap would overflow the row and wrap 50%+50% or 33%x3.
(`zul/.../layout/css/anchorlayout.css`)

## biglistbox

WScroll reads part sizes, so the theme keeps them boxed and only decides the look. The
scrollbar is ZK's own (`zul.WScroll`), not a native one; Marble presents it as a thin MD3 overlay.
The track divs (`-vertical` / `-horizontal`) are `visibility:hidden` and draw nothing; only the
`-drag` thumb is `visibility:visible`: a rounded 8px pill in `--zk-color-outline-variant` with no
groove (`::before { content:none }`), matching the document scrollbar's resting look. Arrow buttons
are hidden (`display:none`); this is safe because they sit inside the fixed-height `-drag`, so
`edrag.offsetHeight` is unchanged.

Three parts must keep a real box, because WScroll reads their size or `offsetTop`:

- `-pos` stays boxed but invisible (`visibility:hidden`, `display:block !important`) and the same
  size as `-drag`: WScroll computes `_gap = edrag.offsetHeight - epos.offsetHeight` once at
  construction, and a zero `-pos` collapses the thumb.
- `-endbar` is `position:absolute; visibility:hidden`, not `display:none`: `display:none` zeroes its
  `offsetTop`, so the thumb's end clamp goes negative.
- `.z-biglistbox` and `.z-biglistbox-outer` are `position:relative`: the wscroll bars are
  `position:absolute`, and without a positioned ancestor they anchor to the viewport.

(`../zkcml/zkmax/.../big/css/biglistbox.css`)

## button

- `orient="vertical"` has no class, only a `<br/>`, so the recipe is
  `.z-button:has(br) { flex-direction: column; gap: 2px; }` and `.z-button:has(br) br { display: none; }`.
  The base `.z-button` has `gap: var(--zk-spacing-2)` for the MD3 icon/image-to-label gap (the graphic
  and the label are separated only by a whitespace text node, which collapses in the flex box); the
  `:has(br)` rule sets its own smaller stacked gap that wins on specificity.
- A filled `.z-button` hover raises the state layer to opacity 0.12 (the global
  `--zk-state-hover-opacity` is 0.08) and lifts to `--zk-elevation-2`. Flat variants (outlined, text,
  icon) and FAB are excluded and do not change elevation. (`zul/.../wgt/css/button.css`)

## calendar

The 7 columns share the content width. `.z-calendar` has `max-width: 268px` (border box)
with a 1px border, which leaves 266px, so a cell is 266 / 7 = 38px. The cap does not apply when ZK writes an inline `width` (the
`width` or `hflex` attribute): the `:is([style^="width"], …)` rule sets `max-width: none` and the
cells then follow the given width. The hover/selected disc stays a fixed 32px circle (6px smaller
than the 38px cell) so it does not scale with the cell width. (`zul/.../db/css/calendar.css`)

## cascader

- `.z-cascader-popup` is detached to `<body>` on open (`makeVParent()`), so its structural rules are
  unscoped; only the closed-state `display:none` is scoped to `.z-cascader .z-cascader-popup`.
- With a pre-selected model ZK emits both the label and the placeholder with an empty inline style,
  and the empty placeholder keeps `flex: 1` and squeezes the label into an ellipsis.
  `.z-cascader:has(.z-cascader-label:not(:empty)) .z-cascader-placeholder { display:none }` hides it
  at initial render; later toggles write inline `display` styles that win over this rule.
- The popup is capped at `calc(100% - var(--zk-spacing-4))` and scrolls sideways (`overflow: auto
  hidden`) because at 320px the two caves are wider than the viewport (WCAG 1.4.10 Reflow).
- The expand chevron on an item carries `margin-left: auto`, because the item text is a bare text
  node and the icon itself must push to the trailing edge.

(`../zkcml/zkmax/.../inp/css/cascader.css`)

## coachmark

A coachmark hosts arbitrary widgets, so Marble keeps the card neutral: it is an MD3 rich tooltip, and
`.z-coachmark-content` uses a neutral surface (`--zk-coachmark-bg`, which is
`--zk-color-surface-container-low`; text `--zk-coachmark-fg`). Prominence comes from the elevation
shadow on the dark scrim (`z-modal-mask` coloured by `--zk-color-scrim`), not from a brand fill. A
coloured card would need per-widget colour inversion for the nested `.z-button` and `.z-combobutton`;
the neutral card avoids that, so nested filled controls keep their standard styling and Marble ships
no inversion rules.

- The close button sits at a symmetric inset of `var(--zk-spacing-2)` from the content corner.
  `Coachmark.ts _fixPadding` gives the root padding of 10 + borderWidth/2 = 20px on the pointer side,
  so `.z-coachmark-up ~ .z-coachmark-close` and `.z-coachmark-right ~ .z-coachmark-close` add
  `var(--zk-spacing-5)` (`calc(var(--zk-spacing-2) + var(--zk-spacing-5))`) to keep the inset
  symmetric in every pointer direction.
- The close is an icon-button affordance (circular hit target, `--zk-control-height-xs`), with an
  on-surface `::before` overlay as the hover/focus state layer: an opaque container swap would be
  invisible on a card that is already a neutral surface-container tone.
- `.z-coachmark-pointer` is `position:absolute` (the mold JS `_fixarrow` writes inline `top/left`; with
  a static position the triangle collapses to the card's left edge) and `z-index: 100`.

(`../zkcml/zkmax/.../nav/css/coachmark.css`)

## codeeditor

CodeMirror injects its base theme as an unlayered `<style>`, which outranks every `@layer`, so
`codeeditor.css` uses `!important` only on the few properties CodeMirror sets (gutters, `.cm-scroller`
font and line-height, the focused-editor outline). The focus ring is drawn on `.z-codeeditor::after`
(root `position: relative; overflow: hidden`, `pointer-events: none`) rather than as an inset
box-shadow on the root: an inset shadow paints under the children, and CodeMirror's gutters (light)
and `.cm-editor` (dark surface) are opaque and flush against the border. Measured 2026-09-08 with the
ring on the root: the light editor showed 2px on three sides and 1px along the gutter, and the dark
editor lost the ring on all four sides. `getComputedStyle(root)` reported the same box-shadow in the
broken state, so the guards in `screenshot.spec.ts` measure painted pixels.

The caret is not styled. `.cm-cursor` / `.cm-dropCursor` come from CodeMirror's `drawSelection`, which
the CE build does not include, so rules for them were dead and were removed. The browser's native
caret paints (pure black on light, pure white on dark); this is an accepted deviation decided
2026-09-08. If it is revisited, set `caret-color` on `.cm-content`, not a `.cm-cursor` rule.
(`zul/.../code/css/codeeditor.css`)

## colorbox

Colorbox is a PE component (`zkex`). `.z-colorbox-icon` sets its own SVG mask
(`--_icon: url(~./zkex/img/marble/colorbox-icon.svg)`, `background-color: currentColor`) and clears
`::before`; it does not use a `z-icon-*` alias. The geometry for the picker/palette is in
`../zkcml/zkex/src/main/resources/web/js/zkex/inp/css/colorbox.css`.

## combobox, bandbox, datebox, timebox, spinner (combo family)

- **Combobox root.** `.z-combobox` is `display: inline-block` with the button absolutely positioned at
  its right edge, not an `inline-flex` container. A `position:absolute` root (ZK-5612) computes
  `display: flex` for `inline-flex`, and `zk(n).offsetHeight()` trusts the real height of a SPAN only
  when its display is `block`; otherwise it measures a detached clone that collapses to 0, and the popup
  opened over the input. The root keeps no padding because the `hflex="min"` math adds root padding to
  the width; the room for the button is the input's right padding instead.
- **Split border (combobox).** Input and button are two elements: `.z-combobox-input` has the left,
  top and bottom borders (and the left half of the radius), `.z-combobox-button` has the right
  border (and the right half), so the seam is not doubled; focus must update both. The input spans the
  whole root and is clipped with `clip-path` so its top and bottom border do not run under the button.
  Which combo components hug their content (`field-sizing: content`) is a Marble decision, see
  `doc/spec/DESIGN.md`.
- **`buttonVisible="false"`.** Marble draws the border of combobox on the input and button
  (Pattern S, split border) and the border of bandbox, datebox, timebox, spinner and doublespinner on
  the root (Pattern R). With Pattern S, hiding the button removes the right edge, so Marble restores
  it on the input through ZK's `.z-{c}-input-full` hook:
  `.z-combobox-input.z-combobox-input-full { border-right: 1px solid var(--zk-color-outline); border-radius: var(--zk-combobox-radius); }`
  (also `padding-right` and `clip-path: none`).
  For Pattern S components the No-button check must verify both that the button is hidden and that
  the input still has a four-side border with a uniform corner radius. For Pattern R components only
  the first check applies. Marble also sets `cursor:pointer` on a readonly combo input to signal that
  a click opens the list; in the readonly + `buttonVisible="false"` corner case nothing opens, and
  Marble accepts that cursor as a rare edge.
- **Root display.** Marble sets `display: inline-flex` on the bandbox, datebox, timebox and spinner
  roots and `display: inline-block` on the combobox root (see above). In a grid cell these roots
  shrink to content width, so a layout that wants them to fill the cell sets `width: 100%` on them.
  Preview pages use the `z-grid-cols-auto` layout in `zkthemelab/src/main/webapp/web/pv/matrix.zul`.

## cropper

Cropper is an EE component (`zkmax`). Jcrop ships no stylesheet, so the theme must carry the structural
rules for the Jcrop-injected subtree (handles, drag bars, lines, tracker; geometry taken from the
upstream cropper stylesheet). The white handle fill and selection outline are intentional literals
(Jcrop's visibility convention over the dimmed image). `.z-cropper img` needs `max-width: none`:
under the global `img { max-width: 100% }` the source image resolves against the shrink-to-fit root
and settles at width 0 (B86-ZK-4269).

The toolbar is an MD3 floating pill on `--zk-cropper-toolbar-bg` (`surface-container`) with compact
text buttons (`min-height: 32px`, fully rounded, on-hover state layer). Only the text colour carries
meaning: crop uses the accent colour (`--zk-cropper-accent`, the confirming action), cancel uses the
foreground colour (`--zk-cropper-fg`). JS positions the toolbar (`position: absolute`) and measures
`outerWidth(true)`, so its `margin` is the clearance from the selection edge.
(`../zkcml/zkmax/.../cropper/css/cropper.css`)

## grid, listbox, tree (data components)

- Sizable header accent: ZK toggles `.z-column-sizing` on the TH itself while the pointer is in the
  rightmost 8px drag zone, so it is not a separate handle and must not be positioned or sized (an old
  `position:absolute; width:4px` rule deformed the header cell). The rule is the resize cursor plus
  `box-shadow: inset -2px 0 0 var(--zk-color-primary)`.
- Header cell padding sits on `.z-column-content`, not on the TH: widget JS reads the TH as a border
  box (`MeshWidget._getMinWd`, Frozen's hidden-column check, `HeaderWidget._insizer`), and a padded TH
  makes a `hflex="min"` or hidden column 32px too wide.
- Narrow icon column: a fixed-width icon column minus the default side padding (`--zk-grid-cell-padding`,
  16px) squeezes the header image below its intrinsic size and inflates the row, so
  `.z-column:has(img) .z-column-content` collapses the side padding to `var(--zk-spacing-1)`.
- Header button slot padding: `calc(var(--zk-spacing-1) + 24px + var(--zk-spacing-2))`, the room for the
  24px column-menu button.
- A sticky header (`z-sticky-header`, ZK-4795) needs an opaque background, `var(--zk-grid-bg)`, because
  Marble's default header is transparent and scrolling rows would bleed through.
- The frozen body spacer (`.z-frozen-body`) has a faint `surface-container-low` background to mark the
  frozen pane.
- Row height follows the data-cell padding tokens in `tokens/_sizing.css` (`--zk-grid-cell-padding`,
  `--zk-listbox-cell-padding`, `--zk-tree-cell-padding`: 16px at the default density, a smaller value in
  the compact tier); grid, listbox and tree use the same value so their rows match.
- Frame policy: outlined (1px border, no shadow) by default, `z-*-noborder` to strip it
  (`doc/spec/DESIGN.md`). A border and a shadow are never combined (MD3 outlined is not elevated).
- The header table is emitted unclassed (`#…-headtbl`), not as `.z-grid-header-inner`, so rules for
  the zero border-spacing target `.z-grid-header table`.
- Marble's CSS-drawn checkable box uses `border-radius: 2px` (listbox and tree); the radio circle uses
  `50%`.

## drawer

A drawer is an MD3 side sheet, so the close icon button belongs at the top-trailing corner of the
header (`doc/spec/md3-close-affordance-placement.md`, section 2).

## errorbox

The errorbox styles live in the errorbox block of `inp/css/input.css`.
The beak padding must stay symmetric: `Errorbox._fixarrow()` writes padding on only the beak-facing
side, which would shift the table-cell content while the absolutely positioned icon and close button
stay put, so `.z-errorbox { --zk-errorbox-beak: 8px; padding: var(--zk-errorbox-beak) !important; }`
(ZK's own beak size: 2 + the 6px pointer border). The icon and close add the same beak back:
`.z-errorbox-icon { left: calc(var(--zk-spacing-3) + var(--zk-errorbox-beak)); }` (12px from content),
`.z-errorbox-close { right: calc(var(--zk-spacing-1) + var(--zk-errorbox-beak)); }` (4px from content).
The beak is coloured with the error border colour, not the faint `error-container`, which is too faint
on a light page.

## inputgroup

Adjacent bordered children double the seam, and the axis decides which border to drop: horizontal
(default) drops `border-left` on the successor; vertical (`orient="vertical"`) drops `border-top`. A
horizontal-only collapse rule must be scoped with `.z-inputgroup:not(.z-inputgroup-vertical)`,
otherwise the vertical textbox loses its left border. The group's `:focus-within` outline owns the
focus affordance, so grouped children keep a constant 1px border and rest padding in every state.
(`zul/.../wgt/css/inputgroup.css`)

## linelayout

`linelayout.css` fully replaces the stock IceBlue linelayout CSS, so every structural rule the widget
JS depends on is re-authored there. The ZK-side invariants are in `zk-component-rules`
`components/linelayout.md`. Marble's choices are a 24px primary-filled point, a 2px
`--zk-color-outline-variant` rail centred in the cave, and a `--zk-spacing-3` gutter between the
content and the points. (`../zkcml/zkmax/.../layout/css/linelayout.css`)

## paging

The divider between the paging bar and the rows is drawn on the host component, not in `paging.css`,
with the outline-variant colour. The last row strips its own `border-bottom` (`.z-row:last-child`,
`.z-treerow:last-child`, `.z-listitem:last-child`), so without a host border the bar floats against
the rows. Grid and tree draw it on `.z-<comp>-paging-top .z-paging` (`border-bottom`) and
`.z-<comp>-paging-bottom .z-paging` (`border-top`); listbox draws `border-top` on its own
`.z-listbox-paging-bottom` wrapper. Keep the divider on all three.

## popup

The `.z-popup-content` wrapper carries the box-shadow. For the popup surface token see
`popup-tokens.md`.

## portallayout

Portallayout is an EE component (`zkmax`). ZK gives columns no spacing, so the theme supplies every
gutter, all `--zk-spacing-3` (12px):

- Vertical orient: `padding-left` on each column after the first. Padding, not margin, because the
  JS-set inline percentage widths plus the root's `overflow:hidden` would overflow or clip with a
  margin.
- Horizontal orient: `padding-top` on each row after the first, and `margin-left` on `.z-panel +
  .z-panel` for side-by-side panels. This one must be a margin: the card chrome lives on `.z-panel`
  itself, so padding would open space inside the card. Side-by-side panels must budget for the gutter
  in their widths (for example `width="calc(50% - 6px)"` for two-up).
- `.z-portalchildren-content > .z-panel` has `margin-bottom` of the same token: widgets carry no
  theme-default margin, so portallayout supplies its own stacking gap.

(`../zkcml/zkmax/.../layout/css/portallayout.css`)

## scrollbar

The native scrollbar is styled globally in `base/_reset.css` with `::-webkit-scrollbar*`, and for
frozen grid/listbox columns with `.z-frozen-inner::-webkit-scrollbar*` in `mesh/css/frozen.css`.

## selectbox (and listbox `mold="select"`)

- A `<select>` option is "the row I'm on", so `.z-selectbox option:checked` and
  `.z-select option:checked` use the list-row family colours (`--zk-color-secondary-container` /
  `--zk-color-on-secondary-container`; a chip-family colour would be wrong); see
  `selected-state-families.md`, row "Selectbox option". Caught 2026-06-11 when only `.z-selectbox`
  had been updated.
- The focus ring is a 1px border plus `box-shadow: inset 0 0 0 1px var(--zk-color-primary)`
  (mechanism A in `focus-affordance-no-layout-shift.md`), never a 2px border, because a `<select>` is
  intrinsically sized and a border-width change grows the box.
- In `::picker(select)`, `:open` is a pseudo-class on the originating `<select>`, not on `::picker`;
  chain it on the select side (`select:open::picker(select)`). `::picker(select):not(:open)` always
  matches and hid the popup.

## signature

Signature is an EE component (`zkmax`). The tool-button `<i>` classes
`z-signature-tool-button-{undo,save,clear}` are not `z-icon-*`, so the generic
`[class^="z-icon-"]::before` mask machinery does not apply and the icon would render blank. Marble sets
`--_icon` on each of the three classes to a local SVG
(`url(~./zkmax/img/marble/signature-{undo,save,clear}.svg)`) and draws it through
`.z-signature-tool-button-icon::before { mask: var(--_icon) … }`; the label is `display:none` because
the buttons are icon-only. (`../zkcml/zkmax/.../signature/css/signature.css`)

## slider, multislider

Marble stretches each multislider `.z-sliderbuttons` container across the full rail, above the track,
so it would be the target of every press and `trackJump_` (which accepts only the track, a mark or the
area) would never move a handle. The container is therefore `pointer-events:none` and its children
(`-button`, `-area`) are `pointer-events:auto`. The same applies to the marks container: it is
`pointer-events:none` and the mark label re-enables it, because `Multislider.trackJump_` jumps only
when the click target is a mark label. (`../zkcml/zkmax/.../slider/css/multislider.css`)

## splitlayout

Splitlayout is an EE component (`zkmax`). The root must not hard-code `display: flex` or
`flex-direction`: ZK's css-flex engine adds `z-flex` + `z-flex-row|column` at runtime when children use
`hflex`/`vflex` and removes them at splitter drag-end, so the dragged pane sizes persist as inline px
on the caves. For the same reason the caves carry no flex of their own and `flex-basis: 0` is forbidden
(the browser would ignore the inline sizes and the drag would snap back to 50/50).

The pill is centred by CSS on the long axis (`top/left: 50%` + `transform`, with
`margin-left/top: 0 !important`): `Splitlayout.ts setBtnPos_` writes an inline margin on that axis,
and on the vertical-orient bar `offsetWidth` is 0 at that moment, so JS would leave the pill at the
left edge. An axis must be owned entirely by JS or entirely by CSS. The bar and pill reuse the
`--zk-splitter-*` tokens from `zul/src/main/resources/web/zul/css/tokens/_splitter.css`
(`-thickness`, `-bar-bg`, `-pill-*`, `-icon-size`), so splitlayout and `.z-splitter` share the same
choices. The bar fill is the divider (no border; see `doc/spec/DESIGN.md` section 14).
(`../zkcml/zkmax/.../layout/css/splitlayout.css`)

## splitter

Never change `display` on `.z-hbox` / `.z-vbox`. The default mold is nested tables, and its inline
`width/height:100%` chain and Splitter's drag persistence (px written onto adjacent `<td>`s) both
need native table layout; Marble only resets the border-spacing, as stock ZK does. The pill follows the
same axis rule as splitlayout: `Splitter.ts setBtnPos_` writes an inline margin on the bar's long
axis, so the CSS only positions the pill (`position:absolute; top:0; left:0`) and adds no centring
transform on that axis, otherwise the two stack and push the pill to the end of the bar.
(`zul/.../box/css/box.css`)

## tabbox

- `.z-tabbox` is `display:flex; flex-direction:column`, which is correct only when the toolbar and the
  scroll arrows are `position:absolute`: `Tabs._scrollcheck` sets the `.z-tabs` inline width to
  `tabbox.offsetWidth - toolbar.offsetWidth`, so the toolbar (`.z-toolbar-tabs`) must overlay the strip
  and not share the flex row. The `.z-toolbar-tabs` rules therefore live in `tabbox.css`.
- Scroll arrows overlay 40px leading and trailing slots. Marble reserves them with
  `.z-tabbox-scroll.z-tabbox-top > .z-tabs { margin: 0 40px; }` (also `-bottom`; for `-left` / `-right`: `margin: 40px 0`), the arrow width;
  IceBlue reserves `--zk-tabbox-icon-size` (24px). The reserve is a margin, not padding, because ZK sets
  the width on the `.z-tabs` content box and padding would compound and shrink the usable area.
- Opaque scroll buttons: ZK reserves arrow space via `_getArrowSize()`, but its measurement can lag the
  rendered arrow width and leave the last tab a few px under the arrow. The arrows have
  `background-color: var(--zk-color-surface)` so the tab scrolls cleanly under the button (the MUI
  pattern), and the hover state layer is mixed over the surface
  (`color-mix(…, var(--zk-color-surface))`), not over `transparent`.
- Accordion: the base `.z-tabpanel { min-height: 80px }` must be reset to 0, otherwise
  `Tabpanel._fixPanelHgh` subtracts inflated sibling heights from the tabbox height and clamps the
  selected cave's height to 0; the strip-mode `.z-tab-content { padding: 12px 16px }` must be reset to
  0 in accordion because `.z-tab` already supplies vertical padding (double padding bloats the header to
  about 60px). MD3 expansion-panel groups are flat at the outer edges, so `.z-tabbox-accordion` has no
  `border-radius`. A closable accordion header shows the close button in place of the chevron; see
  `doc/spec/md3-close-affordance-placement.md`.
- Close button position: ZK emits `.z-tab-button` before `.z-tab-text`, so Marble puts the × trailing
  with flex `order: 1` on `.z-tab-button` (the accordion chevron uses `order: 2` to stay outermost);
  IceBlue uses absolute positioning. The trailing-close convention is MD3 chip / MUI / Chrome tabs.

(`zul/.../tab/css/tabbox.css`)

## timepicker

Timepicker is an EE component (`zkmax`). The root clips with `overflow: hidden` so the square corners of
its children (button hover background, focus-ring overlay) do not bleed past the root's border radius.
The focus ring is a `::after` overlay (`inset 0 0 0 1px`) painted above both children, and the border
stays 1px, so focus causes no layout shift (`focus-affordance-no-layout-shift.md`). The root has
`min-height: var(--zk-control-height)`. Marble renders readonly as an active, transparent-input field in
every state (`doc/contracts/timepicker.md`, `doc/spec/component-state-model.md`). ZK hard-codes the
input at `size="5"`, which fits `HH:mm` only; `field-sizing: content` with `flex: 1 1 auto` and a
`min-width: 5em` floor lets the input hug a longer value such as `HH:mm:ss` without clipping and still
fills a width-forced field. The input is readonly, so there is no per-keystroke width jitter; where
`field-sizing` is unsupported it falls back to the earlier look.
(`../zkcml/zkmax/.../inp/css/timepicker.css`)

## toast, notification

- No left accent stripe: MD3 alerts and snackbars carry severity with the leading icon plus the
  container tint, not a leading colour bar. Severity cues are the icon colour and the container fill.
  Icon colours use `on-*-container` (warning, error) or a mix toward on-surface (info), because the
  bright status tones fell below the contrast floor on their own fill.
- Card geometry (`.z-notification-content`): the icon is `position:absolute; left:16px` (measured from
  `.z-notification`, so `.z-notification` carries no padding) and 20px wide, and the content's left
  padding is 52px (16 + 20 + 16). Toast uses `padding: 12px 48px`.
- `min-height` is `--zk-notification-height` (48px, MD3 touch target), taller than one line, so the
  message is centred with `display: block; align-content: center`. It is not a flex container: flex
  wraps each contiguous inline run of an HTML message in an anonymous block item, which strips the
  spaces around inline markup ("do <span>HTML</span>." becomes "doHTML.").
- The info type is the default (`Notification.show()` and `Toast.show` turn a null type into "info"),
  so it uses the `--zk-notification-*` / `--zk-toast-*` knobs and the pointer arrows follow the fill; warning
  and error keep their semantic containers.

(`zul/.../wgt/css/notification.css`, `toast.css`)
