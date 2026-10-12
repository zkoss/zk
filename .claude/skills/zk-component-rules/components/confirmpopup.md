# confirmpopup

A lightweight inline confirmation popup, anchored to a trigger element with a directional arrow —
lighter-weight than a modal `Messagebox`. Extends `Popup` but overrides its own DOM, open/close, and
class-composition logic almost entirely; it is a floating card with an optional header, an
icon+message body, and a Cancel/OK footer. Fires `onOK` when confirmed, `onCancel` on any other
dismissal path (Cancel click, Escape, click-outside, programmatic close). New in ZK 10.4.0.

## DOM structure

```
.z-confirmpopup                          (<div> root — extends Popup; absolute-positioned/anchored by the Popup base)
├─ .z-confirmpopup-arrow                 (<span> — arrow/pointer; drawn as two stacked CSS triangles via ::before/::after,
│                                          content: ""; outer layer = border-like edge, inner layer = fill, to hide the seam)
├─ .z-confirmpopup-header                (<div id="{uuid}-header"> — OPTIONAL: present only when `header` is set/non-empty)
├─ .z-confirmpopup-body                  (<div> — unconditional wrapper around icon + message)
│   ├─ .z-confirmpopup-icon              (<i> — OPTIONAL: present only when `iconSclass` resolves to a non-empty string;
│   │                                      carries a SECOND literal class equal to the raw `iconSclass` value, e.g.
│   │                                      `class="z-confirmpopup-icon z-icon-exclamation-triangle"`)
│   └─ .z-confirmpopup-message           (<div id="{uuid}-message"> — OPTIONAL: present only when `message` is set/non-empty)
└─ .z-confirmpopup-footer                (<div> — unconditional wrapper; DOM order is FIXED regardless of `defaultFocus`)
    ├─ <button id="{uuid}-cancel" class="z-confirmpopup-cancel">   (always precedes ok in DOM order)
    └─ <button id="{uuid}-ok" class="z-confirmpopup-ok">
```

Notes:
- Unlike the generic `popup` (`.z-popup > .z-popup-content`, see `components/popup.md`), confirmpopup has
  **no separate content wrapper** — header/body/footer are direct children of the root. Do not assume the
  `.z-popup-content` pattern applies here.
- `header`, `icon`, and `message` are each independently optional; a confirmpopup with no header, no icon
  (empty `iconSclass`) and no message renders only the arrow + empty body + footer. Theme selectors must not
  assume any of the three are always present.
- Both `<button>` elements are plain native `<button type="button">`, not ZK `Button` widgets — no `.z-button`
  ancestor classes are involved.

## Root modifier classes

Exactly one class from each of the following two sets is always present on the root (never absent, never
more than one per set):

- `.z-confirmpopup-{severity}` — `severity` ∈ `info | success | warning | error | neutral` (default `warning`).
  The same five-token severity enum is used by ZK's other severity-driven widgets (`badge`, `chip`).
- `.z-confirmpopup-placement-{placement}` — `placement` ∈ `top | bottom | left | right` (default `top`). The
  arrow points from the popup toward the trigger; `placement` describes which side of the trigger the popup
  box occupies (`top` = box above the trigger, arrow on the box's bottom edge, etc.).

## State classes

- `.z-confirmpopup-open` — added to the root when the popup opens (imperative `addClass`-style, mirroring the
  base `Popup`'s own visibility contract), removed on close. **Not present in the server-rendered / first-paint
  markup** — a static DOM check against initial HTML must not expect it. If `severity` or `placement` change
  while the popup is open, the whole class list on root is rewritten (see Composition invariants) and
  `.z-confirmpopup-open` is re-emitted so a live property change does not silently strip the open-state class.

No disabled/readonly states exist for this component.

## Attribute support

- `header` (optional string) → presence/absence of the `.z-confirmpopup-header` div. `null` or empty string
  clears it (no title row rendered).
- `message` (optional string) → presence/absence of the `.z-confirmpopup-message` div. `null` or empty string
  clears it.
- `iconSclass` (default `z-icon-exclamation-triangle`) → presence/absence of the `.z-confirmpopup-icon`
  element. `null` restores the default icon; an explicit empty string suppresses the icon entirely (no `<i>`
  rendered) — `null` and `""` are NOT equivalent, unlike most ZK sclass properties.
- `severity` (enum, default `warning`) → root modifier class, see above. Any other value is rejected server-side.
- `placement` (enum, default `top`) → root modifier class, see above; also drives live re-anchoring (see
  Composition invariants). Any other value is rejected server-side.
- `defaultFocus` (enum `ok | cancel`, default `ok`) → does NOT affect DOM order (footer order is always
  cancel-then-ok) — it only controls (a) which button receives focus on open, and (b) which button Enter
  activates when focus is currently outside both buttons (header/message/root).

## Composition invariants

- The popup box carries a margin, in the direction opposite the placement, sized to exactly offset the
  arrow's own thickness — so the arrow tip touches the trigger edge with no visible gap and no visible overlap.
  Do not add independent spacing between the box and the arrow.
- The arrow's position along the cross-axis is **not static CSS** (`left: 50%` alone is insufficient) — JS
  recalculates and writes an inline offset so the arrow tip stays aligned with the trigger's center even when
  the box itself has been shifted off-center by the framework's viewport-clamp logic. The offset is clamped so
  the arrow cannot slide past the box's own edges. The theme must not counteract this with `!important` or
  `position: static` on the arrow.
- Changing `placement` on an already-open popup re-anchors the box to the new side AND re-points the arrow —
  theme rules keyed on `.z-confirmpopup-placement-*` must apply correctly to a live class swap on the mounted
  element, not only at first render.
- Focus trap while open: Tab / Shift+Tab cycle only between the Cancel and OK buttons (in that DOM order).
  Escape closes the popup (firing `onCancel`). Enter activates whichever button currently has focus; if focus
  is elsewhere inside the popup (header or message text), Enter instead activates the `defaultFocus` button.
  This is a full keyboard modal-trap contract — the theme must not defeat it by making non-button descendants
  focusable.
- `header` / `message` / `iconSclass` changes made while the popup is open are **deferred** — no live DOM patch
  occurs; the new markup only appears on the next open (or an explicit rerender). `severity` / `placement`
  changes, by contrast, **do** apply live via an immediate full class-list rewrite on root. Theme CSS must not
  assume all seven settable properties repaint with the same timing.
- The icon element's selector must combine `.z-confirmpopup-icon` with descendant/child combinators from the
  severity root class (e.g. `.z-confirmpopup-info .z-confirmpopup-icon`) — the raw `iconSclass` value shows up
  as a second, author/icon-set-controlled class on the same element and must not be relied upon for severity
  color hooks.
- Only the icon changes appearance per severity in ZK's own reference styling — the box background/border and
  the footer buttons are severity-invariant. A theme MAY choose to also vary other properties by severity, but
  the icon-only baseline is the structural default to diverge from deliberately, not accidentally.

## Sibling decomposition

- Extends `Popup` for its positioning/anchoring primitives (see `components/popup.md`), but overrides
  `open()`/`close()`/class-composition almost entirely — do not assume confirmpopup reuses `Popup`'s own
  `.z-popup` / `.z-popup-content` visual classes; they are absent here.
- The `severity` enum (`info/success/warning/error/neutral`) is shared verbatim with ZK's native `badge`
  and `chip` components — the same five tokens, same meaning, same default (`warning` here vs `info` on
  badge/chip — check the per-component default, the enum itself is identical).

## Contract

Shipped in `confirmpopup.css.dsp` (package `zul.wgt`; ZK core requests `css/confirmpopup.css.dsp`, which
resolves to this component's own file — not bundled into any other component's `.css.dsp`).

## Edition

CE. `role="alertdialog"` / `aria-modal` / `aria-labelledby` (pointed at the `-header`/`-message` ids the CE
mold already emits) are layered on by the za11y add-on (EE) — the CE mold itself emits no ARIA role or
`aria-*` attribute.

## Notes

- The CE mold renders NO ARIA attributes at all; do not assume `role="alertdialog"` is present when auditing
  CE-only DOM. The mold intentionally keeps the `-header`/`-message` ids stable specifically so the EE za11y
  layer has stable `aria-labelledby` targets to point at.
- Button text is the locale-resolved `msgzul.OK` / `msgzul.CANCEL` string, not a per-instance label property —
  there is no way to relabel the buttons from the ZUL/Java API.
- Rerender-on-change is deferred while the popup is open specifically to avoid destroying focus and timer state
  mid-display (see Composition invariants) — this is a deliberate quirk, not a bug, and a theme/test harness
  probing a live "set message while open" scenario should expect the OLD message to remain visible until close.
