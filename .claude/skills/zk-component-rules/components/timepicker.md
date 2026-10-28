# timepicker

A PE composite input component that renders a text field plus a clock-icon button. Clicking the **button** drops down a scrollable list of time options at fixed intervals — a click on the field/input does **not** open it (see "Popup open is decided in JS" in Notes). The widget class extends `zul.inp.FormatWidget` (→ `InputWidget` → `zul.Widget`) — **not** `ComboWidget`. It only borrows ComboWidget's DOM through its mold (`mold/timepicker.js` is literally `zul.inp.ComboWidget.$redraw`), so the DOM is structurally identical to combobox/timebox/datebox while the click/keyboard *behavior* is NOT inherited from ComboWidget. The popup is populated at runtime by `_renderOptions()` and contains plain `<li>` items, not ZK child widgets.

## DOM structure

```
span.z-timepicker                         (root — inline-block; carries all state classes)
├─ input.z-timepicker-input               (text input; type="text"; user-typed or option-selected value)
│    (.z-timepicker-input-full / hidden-button state is unreachable — buttonVisible not settable; see Attribute support)
├─ a.z-timepicker-button                  (clock icon button; role="button"; tabindex="-1")
│    └─ i.z-timepicker-icon.z-icon-clock-o  (icon glyph; aria-hidden)
└─ div#<uuid>-pp.z-timepicker-popup       (DETACHED to <body> at runtime; display:none at rest)
     └─ ul#<uuid>-cave.z-timepicker-content  (option list)
          └─ li.z-timepicker-option        (one per time slot; repeated N times)
               [class also includes .z-timepicker-option-selected on the currently-selected item]
```

The popup element (`-pp`) is detached to `<body>` via `zk(pp).makeVParent()` when opened and re-attached on close. At open, the popup also receives the root element's full className string merged with its own (so `.z-timepicker-open` propagates to both root and popup). The popup width is set inline to at least the trigger width.

(The borrowed `ComboWidget.$redraw` would, under `buttonVisible="false"`, add `z-timepicker-disabled` to the button and `z-timepicker-input-full` to the input — but Timepicker's server component exposes no `setButtonVisible`, so this state is unreachable from ZUL. See Attribute support.)

## State classes

All state classes are placed on the **root** `span.z-timepicker` unless noted otherwise.

- `.z-timepicker-disabled` — on the root when `disabled="true"`. (The base widget would also add it to the `<a>` button under `buttonVisible="false"`, but that attribute is not settable on Timepicker — see Attribute support below.)
- `.z-timepicker-readonly` — on the root. **Present by default on the client even when `readonly` is unset.** As of ZK 10.3 (ZK-6006) the constructor no longer forces readonly; instead `Timepicker.renderProperties()` renders `readonly=true` to the client whenever `_readonly == null` (unset), so a bare `<timepicker/>` still shows this class and a non-typeable input **on the client** — while the *server* `isReadonly()` returns `false` and the docs call the default `false`. That three-way mismatch is **ZK-6122**. Setting `readonly="false"` explicitly makes `_readonly` non-null, drops the class, and yields an editable input (the ZK-6122 workaround); `readonly="true"` keeps it. Consequence: CSS keyed off `.z-timepicker-readonly` still applies to bare instances, so **readonly must not look disabled** — it reads as an active outlined field (only the popup-pick affordance differs).
- `.z-timepicker-inplace` — on the root when `inplace="true"` AND the input is blurred (unfocused). Removed on focus.
- `.z-timepicker-invalid` — on the `<input>` child (NOT the root) when a constraint violation is detected. Verified in the live DOM: the root carries `z-timepicker z-timepicker-readonly` while the input carries `z-timepicker-input z-timepicker-invalid`. To tint the root's outlined border for the error state, use `.z-timepicker:has(.z-timepicker-invalid)`.
- `.z-timepicker-open` — on the root AND on the popup element when the popup is open.
- `.z-timepicker-option-selected` — on the `<li>` that matches the current input value (placed by `_syncOptionSelected()`; updated by keyboard navigation).

States rely on `:hover` and `:focus` / `:focus-within` pseudo-classes for hover and focus affordances — ZK adds no marker class for hover or focus.

## Attribute support

- `disabled="true"` → adds `.z-timepicker-disabled` to root span; component ignores clicks.
- `readonly="true"` → adds `.z-timepicker-readonly` to root; the popup still opens (pick, don't type). `readonly="false"` → editable, typeable input (drops the class). With the attribute **unset**, the client still renders `readonly=true` (via `renderProperties`, not the constructor) while server `isReadonly()` returns `false` — the ZK-6122 inconsistency; see State classes above.
- `inplace="true"` → adds/removes `.z-timepicker-inplace` on the root based on focus.
- `buttonVisible` — **NOT a settable ZUL attribute on Timepicker.** Although the mold borrows `ComboWidget.$redraw` (whose redraw carries the buttonVisible machinery), the *client* widget class is `FormatWidget`-based (**not** `ComboWidget`) and the *server* component `Timepicker.java` extends `DateTimeFormatInputElement` (→ `FormatInputElement` → `InputElement`) and exposes **no `setButtonVisible`**. Putting `buttonVisible="false"` in ZUL throws a 500 (no setter). Consequently the `.z-timepicker-button.z-timepicker-disabled` / `.z-timepicker-input-full` state is **structurally unreachable** for Timepicker — do not author CSS or contract assertions against it (the existing `.z-timepicker-button.z-timepicker-disabled { display:none }` rule in timepicker.css is dead code).
- `constraint="…"` → when violated adds `.z-timepicker-invalid` to the `<input>` child (not the root).
- `format="…"` — controls the display format of time options (e.g. `HH:mm`, `hh:mm:ss a`).
- `interval="N"` — seconds between popup options; default 3600 (1 hour).
- `min="…"` / `max="…"` — bounds the popup option list.

## Composition invariants

- Popup detachment: `div#-pp` is detached to `<body>` via `makeVParent()` on open. Trigger and popup are siblings under `document.body` while open. Any CSS using descendant selectors from `.z-timepicker` will NOT reach the popup — use `.z-timepicker-popup` as an independent root selector. Because ZK sets the popup's `left`/`top` inline but NOT `position`, `.z-timepicker-popup` MUST declare `position: absolute` + a z-index, or the inline offsets are inert and the popup renders at the page bottom-left — see `reference/floating-popup-in-body.md`.
- Root clips to its border-radius (`overflow: hidden`) to trim the focus-ring overlay (and any child background) to the rounded corners. Historically the readonly input carried an opaque `--zk-color-surface-container` background whose square corners bled past the left radius, which is why clipping was added; the theme now renders readonly as an **active, transparent-input** field (see `doc/contracts/timepicker.md` + `doc/spec/component-state-model.md`), so the input is transparent in every state and `overflow: hidden` remains only to clip the `::after` focus ring.
- Focus affordance must NOT grow the box. The root is `box-sizing: border-box` with `min-height: 36px` and min-height-pinned children; a `border-width: 1px→2px` increase on focus shrinks the content box 2px and the children force the root 2px taller. Keep the border 1px and render the focus ring as an inset box-shadow. This trap is family-wide (datebox/timebox/bandbox/spinner) — see `reference/focus-affordance-no-layout-shift.md`.
- Timepicker draws the focus ring on an always-present `::after` **overlay** (transparent at rest, `pointer-events:none`, clipped to the rounded shape by `overflow:hidden`), painted above both children so the ring reads uniformly with the border staying 1px — no layout shift. See `reference/focus-affordance-no-layout-shift.md`.
- Popup minimum width: JS sets `pp.style.width` to at least `tpn.offsetWidth` (the trigger width). The popup is never narrower than the trigger.
- The clock icon glyph is `z-icon-clock-o` (font icon). The `getIconSclass()` method always returns this — it is not user-configurable.
- The root is `display: inline-block` (structural constraint; ZK's size-management assumes inline-block on the trigger).
- The popup max-height is capped at `350px` by `_fixsz()` (JS-enforced; overflow should be `auto`).
- The input covers the full root width minus the button width. (The button is always present — `buttonVisible="false"` is not settable on Timepicker, so the full-width input variant never occurs.)
- Focus target: the actual focus is on the inner `<input>` element. Use `:focus-within` on the root to style the focused-composite state.

## Sibling decomposition

timepicker's DOM is structurally identical to timebox and the combo-trio pattern:
- Input chrome (input + button): mirrors `combo-trio` — see `components/combo-trio.md`
- Popup option list: mirrors `combobox` popup — same `div#-pp` / `ul#-cave` / `li.z-*-option` pattern; see `components/combobox.md`
- State-class conventions (disabled, readonly, inplace, invalid): follow the same pattern as `combo-trio`

The only structural differences from `timebox`:
1. The popup is a scrollable list of pre-computed time options (`<li>` items) rather than child ZK widgets.
2. The component lives in `zkmax.jar` (PE), not in the CE `combo.css.dsp` bundle.
3. The CSS file is `zkmax/inp/css/timepicker.css` (separate file, not merged into `combo.css.dsp`).

## Contract

`../zkcml/zkmax/src/main/resources/web/js/zkmax/inp/css/timepicker.css`

This is a standalone file (not bundled into `combo.css.dsp`). It must be registered in `lang-addon.xml` under the zkmax package.

## Edition

PE (ZK Max — `zkmax.jar`). The ZKDoc edition badge says "ee" but the Java class is in `org.zkoss.zkmax.zul.Timepicker` which ships in PE+. Treat as PE.

## Notes

- **State class disambiguation**: `.z-timepicker-disabled` *would* appear on two different elements with different meanings — (a) on the root span when the widget is disabled, and (b) on the `<a>` button under `buttonVisible="false"`. For Timepicker only case (a) is reachable: `buttonVisible` is not settable (no server-side setter), so `.z-timepicker-button.z-timepicker-disabled` never occurs in practice. The disambiguation matters for true ComboElement widgets (combobox/datebox/timebox), not Timepicker.
- **Popup is NOT a ZK widget tree**: The popup `<li>` items are raw HTML strings injected via `$cave.append(out)` in `_renderOptions()`. They carry only `.z-timepicker-option` (and optionally `.z-timepicker-option-selected`). There are no child widgets, no additional ZK class injections.
- **Hover class injection**: ZK's `InputWidget` injects `.z-timepicker-hover` on the input element during hover (via `_hoverIn`/`_hoverOut`). This JS-injected class is available as a styling hook when cross-element hover coordination is needed (e.g. synchronizing input border color change with button border color change on hover). Themes may use either this class or the CSS `:hover` pseudo-class on the root.
- **Open class propagation**: On open, `pp.className = tpn.className + ' ' + pp.className` copies the root's classes onto the popup element. This means `.z-timepicker-open` appears on both root and popup, and user-set sclasses propagate to the popup automatically.
- **inplace + readonly combination**: Both states can coexist. The `_inplace` flag causes a mousedown listener on the button — `_doBtnMouseDown` sets `_inplaceIgnore` so a button click can still open the popup even in inplace mode.
- **Popup open is decided in JS, not CSS — and only the button opens it**: the **only** click-to-open handler is `_doBtnClick`, a DOM listener `bind_` attaches exclusively to the clock-button node (`Timepicker.ts:326`, gated only on `!_disabled`). There is **no** field/input open-on-click path: `Timepicker.doClick_:365` handles only the option-click case, then calls `super.doClick_` — and because Timepicker extends `FormatWidget` → `InputWidget` → `zul.Widget` (**not** `ComboWidget`), `super` resolves to the base `zul.Widget.doClick_` (widget.ts:5039), which just fires `onClick` to the server and propagates to the parent — no popup. So a click on the input only focuses/fires `onClick`; only the icon opens the popup. This is a class-hierarchy fact: Timepicker never inherits ComboWidget's readonly whole-control open (`ComboWidget.doClick_:786`, which combobox/datebox use), even though its mold borrows `ComboWidget.$redraw` for DOM. `_buttonVisible` exists as a field (Timepicker.ts:29) but is never consulted for click-open. CSS is never in the open path (the theme sets `pointer-events:none` only on `.z-timepicker-disabled`).
