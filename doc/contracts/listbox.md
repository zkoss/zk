# Component: listbox (theme design)
tier: T1
category: selection
preview: ${PREVIEW_URL}/listbox.zul
rules: see .claude/skills/zk-component-rules/components/data-components.md
shared-css-file: zul/src/main/resources/web/js/zul/sel/css/listbox.css
contract-approved: true
zk-version: 10.2.1-jakarta

## References
- MUI CSS: List.css / Table.css
- DESIGN.md sections: §1, §2, §7, §10, §11

## Expected values
Mirror grid expected values c1–c12 with selectors → `.z-listitem`, `.z-listcell`, `.z-listheader`.

### Selection (list-row family)
Listbox row is the LIST-ROW selection family per
`reference/selected-state-families.md`. MUST use `secondary-container` background
and `on-secondary-container` text (MD3 `md.comp.list.list-item.selected.container.color`;
revised 2026-10-06, was `primary-container`) — never a hardcoded rgba, never
`primary-container`. (Pre-2026-05-29 the CSS shipped a hardcoded
`rgba(55,111,208,0.12)`; if the evaluator sees that literal again, FAIL.)

| id | selector | property | expected |
|----|----------|----------|----------|
| s1 | `.z-listitem.z-listitem-selected` | background-color | `rgb(201, 213, 234)` (≈ `--zk-color-secondary-container`) |
| s2 | `.z-listitem.z-listitem-selected .z-listcell-content` | color | `rgb(21, 30, 45)` (≈ `--zk-color-on-secondary-container`) |
| s3 | `.z-listitem.z-listitem-selected` | background-color | MUST NOT be `rgba(55, 111, 208, 0.12)` (the pre-fix hardcoded literal) |
| s4 | `.z-listitem.z-listitem-selected` | background-color | MUST NOT be `rgb(214, 228, 255)` (= `--zk-color-primary-container`) — the pre-2026-10-06 value |

### Checkmark column (checkbox in `multiple`, radio in single selection)

`checkmark="true"` renders a selection control in the first cell. ZK forks the icon
class on selection mode: `multiple` → `z-icon-check` (checkbox), single → `z-icon-radio`
(radio). See `data-components.md` → "Checkmark column renders a checkbox … OR a radio".
Both must be real Material controls — NOT the generic flat mask glyph (which has no
border and `background-color: rgba(0,0,0,.87)` on `::before`).

**Preview anchors:** the listbox page's "Checkmark" gallery (multiple) and the
single-selection checkmark case.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| cm1 | multi → real checkbox box | `.z-listitem-checkbox .z-listitem-icon.z-icon-check::before` | border-width / border-radius | border ≈ `1.5px` solid `outline`; `border-radius: 2px`; `mask-image: none` | computedStyle |
| cm2 | multi selected → filled box | `.z-listitem-selected .z-listitem-icon.z-icon-check::before` | background-color | `rgb(55, 111, 208)` (= `--zk-color-primary`) with a checkmark `background-image` | computedStyle |
| cm3 | single → real radio circle | `.z-listitem-radio .z-listitem-icon.z-icon-radio::before` | border-radius | `50%` (circle), `mask-image: none`, NOT `rgba(0,0,0,.87)` flat glyph | computedStyle |
| cm4 | single selected → filled dot | `.z-listitem-radio.z-listitem-selected .z-listitem-icon.z-icon-radio::before` (or `::after`) | — | border-color/inner-dot = `--zk-color-primary` | computedStyle |
| cm5 | header checkbox aligned with row | `.z-listheader-icon.z-icon-check` vs `.z-listitem-icon.z-icon-check` | center X | `Math.round(headerIcon.centerX) === Math.round(rowIcon.centerX)` (±1px) | getBoundingClientRect |
| cm6 | no stray ellipsis beside checkbox | `.z-listcell:has(.z-listitem-checkable) > .z-listcell-content` | text-overflow | `clip` (NOT `ellipsis` — the narrow checkable cell overflows its content box by ~8px, so an ellipsis paints a leftover "…" to the right of the checkbox) | computedStyle |

**Preview anchor for cm6:** the `multiple="true" checkmark="true"` listbox in the
"Sorting and Resizable Headers" section of `listbox-header.zul` (narrow `width="40px"`
checkbox column with empty `<listcell/>` first cells).

## Sizable header resize affordance (`<listhead sizable="true">`)

ZK lets the user drag the rightmost 8px of each header cell to resize it, toggling
`.z-listheader-sizing` on the **TH** only while the pointer is in that zone (no persistent
"sizable" class exists). The theme MUST give a hover-time affordance — there is no resting
column divider to scope. See `data-components.md` → "Sizable header → resize affordance".

**Preview anchor:** the "Sorting and Resizable Headers" section of `listbox-header.zul`
(`<listhead sizable="true">`).

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| sz1 | resize cursor on the sizing state | `.z-listheader.z-listheader-sizing` (add the class manually to a `.z-listheader`, then read) | cursor | `col-resize` | computedStyle after `el.classList.add('z-listheader-sizing')` |
| sz2 | sizing state is NOT positioned (would collapse the TH) | `.z-listheader.z-listheader-sizing` | position | NOT `absolute` (must stay a state on the TH; `position:absolute;width:4px` deforms the column) | computedStyle |

## Narrow icon / image header column (no squeeze, no row inflation)

A fixed-width icon column (`width="40px"`) holding a header `image="…16x16.png"` must not
shrink the image below its intrinsic size nor inflate the header row. The default 16px side
padding leaves only 8px content → image squished to 8px + content wraps → row 53→73px. Fix
= collapse side padding for image columns. See `data-components.md` → "Narrow icon /
checkbox / image column".

**Preview anchor:** the first two `<listhead sizable="true">` listboxes in the "Sorting and
Resizable Headers" section (first `<listheader width="40px" image="…ArrowsUpDown-16x16.png">`).

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| ni1 | image header column padding collapsed | `.z-listheader:has(img)` | padding-left / padding-right | `4px` (= `--zk-spacing-1`), NOT `16px` | computedStyle |
| ni2 | header image renders at intrinsic size | `.z-listheader img` (16×16 source) | width / height | `16px` × `16px` (not shrunk to ~8px) | getBoundingClientRect |
| ni3 | image header row not inflated | image-header `.z-listhead` row height vs a no-image `.z-listhead` row | height | equal (≈53px) — the image must not make the header taller | getBoundingClientRect |
| ni4 | checkbox/icon header stays single-line | `.z-listheader:has(.z-listheader-checkable)`, `.z-listheader:has(img)` | white-space | `nowrap` (default `normal` wraps the over-wide icon span → row inflates) | computedStyle |
| ni5 | select-all checkbox header row not inflated | the `multiple checkmark` listbox's `.z-listhead` (40px select-all column) | height | ≈53px, equal to a text-only header row (NOT ~74px) | getBoundingClientRect |
| ni6 | image-only header with the column-menu button, at the minimum width | `.z-listheader:has(.z-listheader-button) img` in a `<listhead menupopup="auto">` | width, and its right edge against the button's left edge | `16px` wide and clear of the button, at 56px (desktop) / 76px (tablet) | getBoundingClientRect (`mesh-header-footer.spec.ts`, `.mhf-listbox-icon-menu`) |
| ni7 | select-all-only header with the column-menu button, at the minimum width | `.z-listheader` holding only `.z-listheader-checkable`, in a `<listhead menupopup="auto">` | icon right edge against the content box's right edge, and against the button's left edge | whole 16px box inside the content box (the content box paints nothing beyond its right edge) and clear of the button, at 68px (desktop) / 88px (tablet) | getBoundingClientRect (`mesh-header-footer.spec.ts`, `.mhf-listbox-select-all-only-menu-*`) |

### Minimum width of an image-only header column with the menu button

A `<listhead menupopup="…">` (and the same for Grid `<columns menupopup="…">`) puts the column-menu button in
every header, and the header's `padding-right` reserves room for it: `--zk-spacing-1` + the button + `--zk-spacing-2`
(24px button on desktop, 44px touch button on a tablet) on top of the 4px each side of an image column. A 16px
image therefore needs a column of at least **56px on desktop and 76px on a tablet** (measured on freshly rendered
columns; 2px narrower shows a 14px image, and on a tablet a column of 60px or less hides it). Below that width the
layout is not supported: the image shrinks to the room left. Marble does not engineer a smaller column (no hiding
of the button, no overlap of the button over the image); give the column the minimum width, or leave `menupopup`
off the head. This applies to Listbox and Grid alike; Tree has no column-menu button.

The same applies to a header that holds only the select-all box (`checkmark="true" multiple="true"`, no label, no
image) and carries the menu button (ni7): the box starts 16px into the cell (the 12px padding of its own span plus
the 4px header padding), so the content box has to reach 32px, and the reservation for the button comes on top:
a column of at least **68px on desktop (32px + 36px) and 88px on a tablet (32px + 56px)**. Measured on freshly
rendered columns, with the width of the painted box read off the screenshot as well: the box is whole from 68px
(88px), 2px narrower it paints 14px, and at the earlier 60px (80px) only its first 8px, because the content box's
`overflow: hidden` ends at 24px and the button is still clear of it. The header keeps the button and its reservation
because the button is the same element as in the labelled headers, and nothing in the DOM (no class, no
attribute) tells a select-all-only header from a labelled one, so CSS cannot hide the button on it alone.

## States to evaluate
- [ ] default rows, header, hover, selected, focus, disabled, striped, frozen columns
- [ ] checkmark column: multiple (checkbox) + single (radio); header↔row X-alignment

## Frozen columns (shared CSS: mesh/css/frozen.css)

ZK `<frozen columns="N"/>` — same bleed-through mechanism as grid. Fix: opaque backgrounds
on all frozen header and body cells.

**Preview anchor:** the listbox page's "Frozen Columns" section.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| f1 | frozen header cell opaque | `.z-listheader.z-frozen-col` | `backgroundColor` | ≠ `rgba(0, 0, 0, 0)` | computedStyle |
| f2 | frozen body cell opaque (pre-scroll) | `.z-listbox:has(.z-listbox-frozen) .z-listitem .z-listcell` | `backgroundColor` | ≠ `rgba(0, 0, 0, 0)` | computedStyle |
| f3 | selected frozen cell opaque | `.z-listbox:has(.z-listbox-frozen) .z-listitem.z-listitem-selected .z-listcell` (skip if absent) | `backgroundColor` | ≠ `rgba(0, 0, 0, 0)` | computedStyle |
| f4 | no bleed-through after scroll | scroll `.z-listbox-body` to `scrollLeft=350`, wait 300ms | visual | no text from Col C visible inside frozen Col A/B area | scroll-trigger screenshot |

**scroll-trigger procedure for f4:**
```js
const lb = document.querySelector('.z-listbox:has(.z-listbox-frozen) .z-listbox-body');
if (lb) lb.scrollLeft = 350;
```
Wait 300 ms, then screenshot. FAIL if any text from a non-frozen column overlaps the frozen column area.

## Sticky header (`z-sticky-header`)

ZK-4795 (since 9.6.0): `sclass="z-sticky-header"` pins the column header to the top
of the scroll ancestor (page or an `overflow:auto` wrapper) as rows scroll under it.
This is a **bare opt-in class** — ZK's `zul/less` ships NO rule; the implementation
lives in the `zkmax` addon, so the theme MUST supply it. See
`data-components.md` → "Sticky header (`z-sticky-header`)".

**Preview anchor:** the listbox page's "Sticky Header (z-sticky-header)" section
(`listbox-header.zul`), a `<listbox sclass="z-sticky-header">` inside a `height:200px;
overflow-y:auto` scroller.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| sh1 | header pins on scroll | `.z-listbox.z-sticky-header .z-listbox-header` | `position` | `sticky` (with `top: 0px`) | computedStyle |
| sh2 | header opaque (no bleed-through) | `.z-listbox.z-sticky-header .z-listbox-header` | `background-color` | ≠ `rgba(0, 0, 0, 0)` — rows must not show through the pinned header | computedStyle |
| sh3 | root un-clips header | `.z-listbox.z-sticky-header` | `overflow` | `visible` (else the header is clipped and cannot escape to stick) | computedStyle |

## Auxhead divider (shared CSS: mesh/css/auxhead.css)

The header table is `border-collapse: separate` (so frozen-col TH `box-shadow` /
`border-inline-end` paint). In `separate` mode browsers do NOT paint borders on a TR — so
the auxhead row divider MUST live on the `.z-auxheader` **TH cell** `border-bottom`, not on
`.z-auxhead` (the TR). Without this, two adjacent `<auxhead>` rows merge into one tonal band.
See `data-components.md` → "Header-row dividers go on the TH".

**Preview anchor:** the "Auxhead" section of `listbox-header.zul` (if it stacks two
`<auxhead>` rows). If the page has no adjacent-auxhead demo, mark ax1 `SKIPPED` — do not FAIL.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| ax1 | auxhead row divider paints (on the TH) | `.z-auxheader` | `border-bottom` | `1px solid` `--zk-color-outline-variant` (so adjacent aux rows separate under `border-collapse: separate`) | computedStyle |
| ax2 | visible-last auxheader has no inline-end border | `.z-auxheader:has(+ .z-auxhead-bar)` | `border-inline-end` (border-right) | `0px` / none — ZK appends a zero-width `.z-auxhead-bar` filler TH, so the visible-last auxheader is NOT `:last-child`; a right border left on it doubles with the container frame border (two parallel 1px lines on the right edge). | computedStyle |
| hs1 | header table has no inter-cell gap | `.z-listbox-header table` | `border-spacing` | `0px` (UA default 2px would leak white gaps between cells/rows under separate border-collapse) | computedStyle |

## Select mold (`<listbox mold="select">` → native `<select class="z-select">`)

`mold="select"` renders a native `<select>` (no table DOM), identical to Selectbox. With
`rows="1"` it is a **dropdown** (`size="1"`) and gets the `base-select` popup treatment. With
`rows="N>1"` it is `size="N"` — an **in-page list box** that shows its options inline; a
`<listgroup>` becomes an `<optgroup>`. base-select is popup-only, so the sized list must opt
OUT of its inline-flex trigger layout (otherwise the optgroups flow side-by-side into columns).
See `components/selectbox.md` → "Sized / multiple select is an in-page list box".

**Preview anchor:** the "Select Mold with Optgroup (listgroup)" section of `listbox-header.zul`
(the `rows="4"` listbox). sm1/sm2 only meaningful in a `base-select`-supporting browser
(Chrome 130+); mark `SKIPPED` otherwise.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| sm1 | sized list lays options vertically | `.z-select[size]:not([size="1"])` | `display` | `block` (NOT `flex` — a flex-row select flows the `<optgroup>`s into 2+ columns) | computedStyle |
| sm2 | single-row select keeps base-select | `.z-select[size="1"]` | `display` | `flex` / `inline-flex` (the dropdown trigger; base-select still applies) | computedStyle (base-select browsers) |
| sm3 | selected option = list-row family (secondary-container) | `.z-select option:checked` | `background-color` | `--zk-color-secondary-container` (list-row family). MUST equal `.z-selectbox option:checked` (same native-select control). Read the **CSS rule** (`document.styleSheets`), not the live `<option>` — `getComputedStyle` on an `<option>` returns transparent. | CSS-rule lookup |
| sm4 | focus ring causes no layout shift | `.z-select:focus` | `border-width` / box | `border-width` stays `1px` (ring drawn via `box-shadow: inset 0 0 0 1px primary`, NOT a 2px border — a `<select>` is auto-sized so a 2px border grows the box). Outer dims at `:focus` == at rest (±0px). | computedStyle + bbox rest-vs-focus |

## Outer frame (container)

Default = standalone **outlined** card: border, NO shadow (never both). No-border variant
(`z-listbox-noborder`) and panel/groupbox ancestry strip the border. ZK emits no border
attribute for listbox (unlike window's `z-window-noborder`) → the variant is a theme sclass
mirroring that naming. See `doc/spec/DESIGN.md §11`.

| id | check | selector | property | expected | method |
|----|-------|----------|----------|----------|--------|
| fr1 | default outlined | `.z-listbox` | `border` | `1px solid` `--zk-color-outline-variant` | computedStyle |
| fr2 | never border + shadow | `.z-listbox` | `box-shadow` | `none` | computedStyle |
| fr3 | noborder variant strips frame | `.z-listbox.z-listbox-noborder` | `border` | `none` (for nesting in a bounded parent) | computedStyle |
| fr4 | auto-flat inside panel/groupbox | `.z-panel-body .z-listbox`, `.z-groupbox .z-listbox` | `border` | `none` | computedStyle |
