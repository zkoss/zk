# daterangebox

A two-ended date-range picker (EE). Renders as a single bordered row with two text inputs (begin/end) separated by a literal separator glyph, plus an optional trigger button. Clicking either input or the trigger opens a body-detached popup containing one or more side-by-side calendar month panels (and, when time selection is on, a matching row of time pickers) plus a footer of Clear/Today/Cancel buttons. There is no Apply button — completing a range (picking a second, later date) auto-commits after a short delay; Cancel discards the in-progress pick.

## DOM structure

Root (always present, rendered by `redraw()`):

```
div.z-daterangebox[role=group]
├─ input.z-daterangebox-input.z-daterangebox-begin      (begin text field; native disabled/readOnly mirror the widget state)
├─ span.z-daterangebox-separator[aria-hidden="true"]    (literal separator text, e.g. "~" — decorative only)
├─ input.z-daterangebox-input.z-daterangebox-end        (end text field)
├─ a.z-daterangebox-button[role=button][tabindex=0][aria-haspopup=dialog]   (trigger — present in the initial DOM ONLY when buttonVisible=true at render time; see Attribute support)
│  └─ i.z-icon-calendar
└─ div.sr-only[role=status][aria-live=polite][aria-atomic=true]   (visually-hidden live region; no longer rendered by the widget — za11y appends it on init, so it is absent without za11y)
```

`role="group"` is the default; an app-supplied `domExtraAttrs.role` overrides it (rare).

Popup — **not present in the DOM until the first time the user opens it** (focuses an input, clicks the trigger, or the server calls `setOpen(true)`). Once created it is re-parented to `<body>` (so it isn't clipped by an `overflow:hidden` ancestor such as a window or layout region) and stays there — including across close/reopen — until the widget unbinds, at which point it is removed from `<body>` again.

```
div.z-daterangebox-popup[role=dialog][aria-modal=false][tabindex=-1]   (--panels custom property set inline = numberOfMonths; .z-daterangebox-popup-linked class added unconditionally at bind — see Notes)
├─ div.z-daterangebox-popup-panels     (grid host; N × .z-calendar children, N = numberOfMonths — see components/calendar.md)
├─ div.z-daterangebox-popup-times      (grid host; 2 × .z-timebox children in document order [begin, end] — only populated/shown when showTime is on; otherwise empty and inline display:none)
└─ div.z-daterangebox-popup-footer
   ├─ button.z-daterangebox-popup-clear   (present only when allowEmpty === "both")
   ├─ button.z-daterangebox-popup-today   (present only when showTodayLink === true)
   └─ button.z-daterangebox-popup-cancel  (always present)
```

The footer is a 1–3 slot row (Clear is conditional, Today is conditional, Cancel is unconditional); a runtime toggle of `allowEmpty` or `showTodayLink` inserts/removes the corresponding button in place without a full popup re-render.

Range-highlight state lives on the **Calendar panel's own cells**, not on any daterangebox-prefixed class — `.z-calendar-cell` inside `.z-daterangebox-popup-panels` can carry `.z-cell-range-begin`, `.z-cell-range-end`, `.z-cell-range-mid`, `.z-cell-range-preview-mid`, `.z-cell-range-preview-end` (the last two only while hovering a candidate end date with no committed end yet, gated by `hoverPreview`). These five classes are managed by the Calendar widget itself (see `components/calendar.md`) and are exercised meaningfully only by daterangebox — a standalone `<calendar>` or a `<datebox>` popup never sets a range, so a theme that only styled `.z-calendar-selected` will have never seen them before.

## State classes

On the root `.z-daterangebox`:

- `.z-daterangebox-focused` — added when either input receives focus (`focus` handler), removed only when the popup closes. This is a **manually managed class**, not `:focus-within` — it stays applied while the popup is open even if focus moves onto a popup calendar cell (the popup is a detached body-mounted sibling, outside the root's subtree, so `:focus-within` on the root would never see it anyway).
- `.z-daterangebox-invalid` — a **whole-widget** flag: applied when either input currently holds text that fails to parse. Clearing one input's bad text does NOT necessarily remove the class — it is only removed once **neither** input holds unparseable text (checked per-side on every clear attempt).
- `.z-daterangebox-disabled` — mirrors the `disabled` property; applied/removed on the root only (there is no native `disabled` attribute on the root — it's a `<div>` — so this class is the only signal; the two `<input>` children separately receive the native `disabled` attribute).
- `.z-daterangebox-readonly` — mirrors the `readonly` property; applied/removed on the root; the two `<input>` children separately receive the native `readOnly` property.

There is **no `-open` root class** — unlike sibling combo-pattern widgets (see `components/combo-trio.md`), daterangebox does not toggle an "open" modifier on the root when the popup shows. Popup visibility is tracked purely by the popup's own `display` (inline style) and its detachment/attachment to `<body>`.

On the popup root `.z-daterangebox-popup`:
- `.z-daterangebox-popup-linked` — added once, unconditionally, the first time the popup binds. There is currently no widget-level toggle that removes it or that would render "unlinked" panels; every instance of this popup carries the class.

On calendar cells inside the popup panels (managed by the Calendar widget, not daterangebox itself): `.z-cell-range-begin`, `.z-cell-range-end`, `.z-cell-range-mid` (committed range), `.z-cell-range-preview-mid`, `.z-cell-range-preview-end` (hover preview of a not-yet-committed end date).

## Attribute support

- `disabled="true"` → adds `.z-daterangebox-disabled` to the root; sets native `disabled` on both input elements; forces the popup closed if it was open; ignores subsequent open requests (focus/click/`setOpen(true)`) while disabled.
- `readonly="true"` → adds `.z-daterangebox-readonly` to the root; sets native `readOnly` on both input elements; forces the popup closed if it was open; ignores subsequent open requests while readonly.
- `buttonVisible` → **two different mechanisms depending on when the flag is false**:
  - If `false` at initial render, the trigger `<a class="z-daterangebox-button">` element is **omitted from the DOM entirely** (not rendered, not hidden).
  - If toggled to `false` at runtime (after the button already exists), the element **stays in the DOM** and is hidden via an inline `style.display = 'none'` — no class is added or removed for this. A theme rule that only targets a `-disabled`-style modifier class on the button (the pattern used by combo-trio siblings) will not see this runtime-toggle path; it must tolerate the inline style instead.
- `numberOfMonths` (1..configured max, default 2) → controls how many `.z-calendar` panels are appended to `.z-daterangebox-popup-panels`, and is mirrored as the inline custom property `--panels` on the popup root — the grid's column count is CSS-var-driven, not class-driven. A CSS rule that hardcodes a fixed `grid-template-columns` count instead of consuming `--panels` will not track a non-default panel count.
- `showTime` → toggles the popup's `.z-daterangebox-popup-times` container between empty/`display:none` and populated/visible with two `.z-timebox` children.
- `allowEmpty` → gates presence of the footer's Clear button (`.z-daterangebox-popup-clear`) — present only when the resolved value is `"both"`.
- `showTodayLink` → gates presence of the footer's Today button (`.z-daterangebox-popup-today`).
- `weekOfYear` → forwarded to every popup Calendar panel (see `components/calendar.md`'s `.z-calendar-wk` cell).
- `separator` → literal text content of `.z-daterangebox-separator` (an `aria-hidden` decorative span — not read by assistive tech; the accessible announcement of the current range lives in the za11y-appended `.sr-only` live region instead).
- `position` → popup anchor token; purely a JS-computed inline `top`/`left`, no class involved.
- `placeholder` / `beginPlaceholder` / `endPlaceholder` → also become the input's `aria-label` when the corresponding placeholder is non-empty (a placeholder is otherwise the input's only visible cue, so it doubles as the accessible name).

## Composition invariants

- The popup and its calendar panel count must always agree: the number of `.z-calendar` children under `.z-daterangebox-popup-panels` equals the inline `--panels` custom property on the popup root, which equals `numberOfMonths`. A theme must read column count from `--panels`, not assume a fixed 2.
- The `.z-daterangebox-popup-times` grid must use the **same column count** as `.z-daterangebox-popup-panels` (both consume `--panels`) so each Timebox lines up under its corresponding Calendar panel.
- The popup is a `<body>`-mounted sibling of everything else on the page (not a DOM descendant of `.z-daterangebox`) whenever it exists — a theme must not rely on any ancestor/descendant CSS relationship between `.z-daterangebox` and `.z-daterangebox-popup`; positioning is entirely inline-style/JS-driven.
- The trigger button, when present, must remain focusable (native `tabindex="0"` on an `<a>` without `href`) — this is required both for keyboard access and because closing the popup can programmatically restore focus to it.
- The live region is a `div.sr-only` appended by za11y (the widget no longer renders `.z-daterangebox-status`; ZK-6161). A theme must not `display:none` it; WCAG 4.1.3 depends on it staying in the accessibility tree (visually hidden via `.sr-only` is fine; DOM-absent is not).
- **The two `<input>` elements ship with no `size` or `width`.** `redraw()` (`Daterangebox.ts`) writes only `class`, `aria-label`, `placeholder`, and `autocomplete` on each input — never a `size` attribute — so each falls back to the browser default `size=20` (~20ch, ~172px). Because there are **two** of them (plus separator + trigger), an unbounded theme renders a field roughly twice a single-input datebox's width; and if the theme also centers the input text, a short date sits mid-box with large symmetric gutters. **Any theme must bound the input width** — e.g. `field-sizing: content` (hug the date; degrades to the ~20ch default where unsupported) or an explicit width — or accept the ~20ch-per-input default. This is structural (true for every theme), not a Marble color/spacing choice.

## Sibling decomposition

- Root chrome (bordered row wrapping input(s) + trigger button): mirrors `combo-trio` (datebox/timebox/spinner) — see `components/combo-trio.md` — but is a **two-input** variant of that pattern (begin input + separator + end input, instead of one input) rather than a literal reuse.
- Popup calendar panels and their range-highlight cell classes: reuse `.z-calendar` wholesale — see `components/calendar.md` (including the `z-cell-range-*` classes documented there under range-highlighting).
- Popup time row: reuses `.z-timebox` — see `components/combo-trio.md`.
- Popup footer buttons (`-clear` / `-today` / `-cancel`): no existing sibling owns a "1-3 slot footer button row inside a detached popup" pattern verbatim; these are plain `<button type="button">` elements local to this component with no other structural analog in the current component set.

## Contract

`js/zkmax/db/css/daterangebox.css.dsp` — standalone file; **not** bundled into `combo.css.dsp` alongside datebox/timebox/spinner/combobox/bandbox, despite the shared input+button visual family. A CSS edit to `combo.css.dsp` has no effect on daterangebox and vice versa.

## Edition

EE (`org.zkoss.zkmax.zul.Daterangebox`, widget class `zkmax.db.Daterangebox`; ships in `zkmax.jar`).

## Notes

- `numberOfMonths`, `showTime`, `allowEmpty`, `showTodayLink`, `weekOfYear` can all change at runtime; each has its own narrow DOM patch path (rebuild panels, rebuild times, rebuild footer) rather than a full popup re-render — a theme relying on a full-DOM-recreate assumption to "reset" styling on these transitions will not get one.
- The invalid indicator is whole-widget but the underlying condition is per-input; do not assume `.z-daterangebox-invalid` toggling off means *both* inputs just became valid simultaneously — it only means neither currently holds bad text (one could have been valid all along).
- The component rejects any ZUL child added by the application (`beforeChildAdded` throws) — the two inputs, the popup, its calendar panels and its Timeboxes are exclusively framework-managed; there is no supported way to inject extra markup into this widget's subtree via ZUL.
- No wheel-picker / mobile fallback exists for this component (unlike datebox/timebox — see `reference/mobile-wheel-picker.md`); on a touch UA it renders the same detached grid-calendar popup as desktop. A `<576px` viewport instead promotes the popup to a full-viewport modal layout (panels stack to one column, footer pins to the bottom) — same DOM, no new classes, purely a layout breakpoint.
- **User `sclass` is dropped from the root DOM — confirmed ZK bug [ZK-6133](https://zkoss.atlassian.net/browse/ZK-6133).** Unlike datebox (where `sclass="z-datebox-invalid"` merges into the root as `z-datebox-invalid z-datebox`), daterangebox's client `redraw` builds the root `class` from `getZclass()` only and passes `domAttrs_({domClass:true})` (which suppresses the normal `domClass_()` emission that would append the sclass). Verified live (ZK 10.4.0): a `<daterangebox sclass="...">` renders `class="z-daterangebox"` with the sclass absent from the root *and every descendant*, while datebox/textbox/combobox/bandbox all include it. Consequence: state classes like `.z-daterangebox-invalid` cannot be forced declaratively for a static demo — they only appear via the real invalid condition (unparseable input). The invalid CSS itself is correct (verified by forcing the class in devtools). The preview (`pv/daterangebox-content.zul`) therefore leaves the Invalid column unstyled with a comment pointing at ZK-6133; make it a live demo once that's fixed.

## Data model (for preview)

`value` is a `DateRange` (begin/end `java.util.Date`, either may be `null`); there is no plain `value=` ZUL attribute setter — use `beginValue` / `endValue`, or set the value programmatically:

```xml
<zscript><![CDATA[
java.util.Date sampleBegin = new java.util.Date(125, 0, 10);  // 2025-01-10
java.util.Date sampleEnd   = new java.util.Date(125, 0, 15);  // 2025-01-15
]]></zscript>
<daterangebox beginValue="${sampleBegin}" endValue="${sampleEnd}"/>
<daterangebox numberOfMonths="3"/>
<daterangebox showTime="true"/>
<daterangebox showTodayLink="true"/>
<daterangebox allowEmpty="none"/>
<daterangebox buttonVisible="false"/>
<daterangebox disabled="true" beginValue="${sampleBegin}" endValue="${sampleEnd}"/>
<daterangebox readonly="true" beginValue="${sampleBegin}" endValue="${sampleEnd}"/>
```

Without a seeded begin/end value, both inputs render blank and the popup opens with no highlighted range — not useful for visually verifying the `z-cell-range-*` states, which require an open popup with a committed (or hovered) range. A generator/evaluator preview page needs at least one row with a pre-seeded range **and** the popup pinned open (e.g. via a Prove-It button or `open="true"`) to exercise those cell classes at all.
