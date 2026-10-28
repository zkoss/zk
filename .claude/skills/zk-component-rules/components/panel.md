# panel

A card-style container with an optional title bar and a content area.

## DOM structure

```
.z-panel[.z-panel-noheader][.z-panel-collapsed][.z-panel-noborder][.z-panel-noframe][.z-panel-shadow]
├─ [HAS title or caption]
│   └─ .z-panel-head
│       └─ .z-panel-header[.z-panel-header-move]
│           ├─ .z-caption          (if <caption> child widget set)
│           │   └─ ... caption content + .z-panel-icons
│           └─ [plain text node + .z-panel-icons]  (if title= attribute set, no caption child)
│               └─ .z-panel-icons
│                   ├─ .z-panel-icon.z-panel-expand     (if collapsible="true")
│                   ├─ .z-panel-icon.z-panel-minimize   (if minimizable="true")
│                   ├─ .z-panel-icon.z-panel-maximize   (if maximizable="true")
│                   └─ .z-panel-icon.z-panel-close      (if closable="true")
├─ [NO title AND NO caption]
│   └─ .z-panel-drag-button        (drag handle — see below)
└─ .z-panel-body
    ├─ .z-panel-top                (tbar toolbar — only when <toolbar mold="panel"> as tbar)
    ├─ .z-panelchildren            (content wrapper)
    ├─ .z-panel-bottom             (bbar toolbar)
    └─ .z-panel-footer             (fbar toolbar)
```

## Critical: content area is `.z-panelchildren`

ZK uses `.z-panelchildren` for the inner content wrapper, **not** `.z-panel-content`. This is the most common selector mistake.

```css
/* WRONG — no effect */
.z-panel-content { padding: 16px; }

/* CORRECT */
.z-panelchildren { padding: 16px; }
```

## `z-panel-drag-button` — the no-title drag handle

When the panel has **neither** a `title=` attribute **nor** a `<caption>` child widget, the mold renders a `.z-panel-drag-button` element **instead of** `.z-panel-head`. This element is purely a JS drag-wire anchor — its `id="{uuid}-drag-button"` is used by `Panel._doGhost` to initiate dragging.

**It must always be `display: none` in CSS.** The default DK theme (`panel.css.dsp` in ZK source) sets `.z-panel-drag-button { display: none }`. Themes that omit this rule will render a visible minus-icon strip at the top of every no-title panel.

When `.z-panel-drag-button` is in the DOM, ZK also adds `.z-panel-noheader` to the panel root (see `Panel.domClass_()` in `Panel.ts`). This state class can be used for no-header-specific layout rules, but the drag button itself must remain hidden.

```css
/* Required in every theme */
.z-panel-drag-button { display: none; }
```

## State and modifier classes on the root

- `.z-panel-noheader` — added when panel has no `title=` and no `<caption>` child; `.z-panel-drag-button` is in the DOM instead of `.z-panel-head`
- `.z-panel-collapsed` — body hidden, head (or drag-button) still visible
- `.z-panel-shadow` — drop shadow variant
- `.z-panel-noborder` — border and shadow removed
- `.z-panel-noframe` — no border-radius (`border="normal"` in ZUL)

## Drag / resize interaction classes

- `.z-panel-header-move` — added to `.z-panel-header` while the panel is being dragged
- `.z-panel-move-ghost` — ghost overlay during drag
- `.z-panel-move-block` — full-viewport cursor-lock div during drag
- `.z-panel-resize-faker` — ghost overlay during resize

## Header icon buttons

Base class is `.z-panel-icon` (`$s('icon')` in the mold). Additional specific classes:

- `.z-panel-expand` — collapse/expand toggle (shows when `collapsible="true"`)
- `.z-panel-minimize` — minimize button
- `.z-panel-maximize` — maximize button
- `.z-panel-close` — close button

All four are direct children of `.z-panel-icons`, itself inside `.z-panel-header`.

## Bundle

`panel.css.dsp`.
