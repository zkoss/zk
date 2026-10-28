# toolbar

A horizontal bar holding toolbar buttons.

## Orientation classes

The root gets an orientation modifier: `.z-toolbar-horizontal` (default) or `.z-toolbar-vertical`.

## Padding lives on `.z-toolbar-content`, not on root

The toolbar root carries `min-height` and flex layout; the inner `.z-toolbar-content` carries the horizontal padding:

```css
.z-toolbar         { min-height: 48px; display: flex; align-items: center; }
.z-toolbar-content { padding: 0 16px; }
```

Putting padding on `.z-toolbar` instead of `.z-toolbar-content` breaks the flex layout when toolbarbuttons need to align flush to the edges.

## Context class: `.z-toolbar-tabs` (parent is Tabbox)

When the toolbar's parent widget is a `Tabbox`, `Toolbar.domClass_` appends a `.z-toolbar-tabs` class to the root. This is a context marker, not a behavioural class — the theme decides what it means visually. The conventional iceblue/MD3 styling is to absolutely position the toolbar so it overlays the right (or bottom, for `.z-tabbox-bottom`) of the tab-strip row:

```css
.z-tabbox > .z-toolbar.z-toolbar-tabs { position: absolute; top: 0; right: 0; z-index: 1; }
.z-tabbox-bottom > .z-toolbar.z-toolbar-tabs { top: auto; bottom: 0; }
```

This is REQUIRED for `Tabs._scrollcheck`'s width arithmetic — see `tabbox.md` Layout invariants. The rules live in `tabbox.css` (alongside other tabbox-layout rules), not in `toolbar.css`, because they are tabbox-context-scoped.

The class is NOT added for vertical or accordion tabbox molds (those don't render a toolbar at all).

## Toolbarseparator

`<toolbarseparator/>` renders a vertical divider inside the toolbar. **PE / EE only** — not available in CE. Will throw a render exception in a CE build. See `reference/edition-availability.md`.

For CE-compatible separators inside a toolbar, use a custom `<separator vertical="true" />` or a styled `<space/>`.

## Bundle

`toolbar.css.dsp`. Toolbarbuttons inside the toolbar are styled by `footer.css.dsp` (yes, the file is named footer despite covering toolbar buttons — historical naming).
