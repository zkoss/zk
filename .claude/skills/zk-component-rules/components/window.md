# window

A floating or embedded window, optionally with a title bar.

## DOM structure

```
.z-window[.z-window-{mold}]
├─ .z-window-header              (title bar — optional)
│   ├─ (plain text node — the title)
│   └─ .z-window-icon-...        (close/min/max buttons)
└─ .z-window-content             (the body)
```

## Critical: no `.z-window-title` element

The title text in `.z-window-header` is rendered as a **plain text node**, not wrapped in a `.z-window-title` element. Therefore:

```css
/* WRONG — no element matches */
.z-window-title { font-weight: 500; }

/* CORRECT — style the header directly */
.z-window-header { font-weight: 500; }
```

If you need to style only the title (not the icon buttons), you cannot do it with CSS alone — you would need to wrap the title in a span via ZUL/template customization.

**Consequence for wrapping:** because the title is a bare text node, any `white-space: nowrap` / `overflow` / `text-overflow` rule you scope to a caption wrapper (e.g. `.z-caption-content`, `.z-label`) will **not** reach the title-mold text. A long title in a narrow window then wraps to multiple lines and inflates the header height. Put `white-space: nowrap` on `.z-window-header` itself (the flex container) — the anonymous text flex item inherits it; the window's `overflow: hidden` clips any excess. The same applies to `.z-panel-header` (panel title mold). Groupbox's title mold *does* use a wrapper (`.z-groupbox-title-content`), so it is unaffected.

## Modes

Window has **five runtime modes** set via the `mode` attribute (not `mold`):

- `mode="embedded"` (default) — inline with surrounding layout flow; participates in document flow
- `mode="overlapped"` — floats over content; draggable
- `mode="popup"` — popup-style floating layer; auto-closes on outside interaction
- `mode="modal"` — modal dialog; blocks interaction with the rest of the page (scrim/mask rendered)
- `mode="highlighted"` — emphasized floating window (no input blocking)

Each adds `.z-window-{mode}` to the root: `.z-window-embedded`, `.z-window-overlapped`, `.z-window-popup`, `.z-window-modal`, `.z-window-highlighted`.

**Spatial semantics:** `embedded` is the only mode that does NOT float. The other four are visually layered above the page.

## Border attribute

- `border="normal"` (default) — outlined frame; root has no extra class
- `border="none"` — no outline; adds `.z-window-noborder` to the root

`border` is a **visual frame** attribute only. It does NOT determine whether the window floats — that is the role of `mode`. A theme MUST NOT make border control elevation/shadow (see project design rules).

## Shadow rendering

ZK has a `setShadow(boolean)` API (default `true`). When `true` and the window is in a floating mode, ZK instantiates a separate `zk.eff.Shadow` widget that renders a sibling DOM node next to the window — this is independent of CSS `box-shadow` on `.z-window`. Themes typically rely on CSS `box-shadow` on the mode class and may ignore the legacy Shadow widget output, but be aware the extra DOM exists when inspecting.

## Other modifier classes

- `.z-window-noheader` — header hidden (when no title and no caption child)
- `.z-window-header-move` — header is being dragged

## Header icon buttons

- `.z-window-icons` — icon container in the header
- `.z-window-close` — close button
- `.z-window-minimize` — minimize button
- `.z-window-maximize` — maximize button
- `.z-window-header-move` — header is being dragged

## Drag / resize state classes

- `.z-window-move-ghost` — ghost overlay shown while dragging
- `.z-window-resize-faker` — ghost overlay shown while resizing

## Bundle

`window.css.dsp`.
