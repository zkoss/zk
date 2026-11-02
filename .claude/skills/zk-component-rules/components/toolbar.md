# toolbar

A horizontal bar holding toolbar buttons.

## Orientation classes

Only a vertical toolbar gets an orientation class: `.z-toolbar-vertical`. A horizontal toolbar has no modifier class. Other root modifiers (`Toolbar.domClass_` in `zul/wgt/Toolbar.ts`): `.z-toolbar-panel` (panel mold), `.z-toolbar-overflowpopup` (overflow popup) and `.z-toolbar-tabs` (see below). The alignment class sits on the inner `.z-toolbar-content`.

## Padding lives on `.z-toolbar-content`, not on root

The toolbar root carries `min-height` and the flex layout; the inner `.z-toolbar-content` carries the horizontal padding. Putting padding on `.z-toolbar` instead of `.z-toolbar-content` breaks the flex layout when toolbarbuttons need to align flush to the edges.

## Context class: `.z-toolbar-tabs` (parent is Tabbox)

When the toolbar's parent widget is a `Tabbox`, `Toolbar.domClass_` appends a `.z-toolbar-tabs` class to the root. This is a context marker, not a behavioural class. The toolbar must be absolutely positioned so it overlays the right (or bottom, for `.z-tabbox-bottom`) of the tab-strip row.

This is REQUIRED for `Tabs._scrollcheck`'s width arithmetic — see `tabbox.md` Layout invariants. The rules live in `tabbox.css` (alongside other tabbox-layout rules), not in `toolbar.css`, because they are tabbox-context-scoped.

The class is NOT added for vertical or accordion tabbox molds (those don't render a toolbar at all).

## Separators inside a toolbar

Use `<separator orient="vertical" bar="true"/>` for a vertical divider inside a toolbar (or a styled `<space/>`). See `components/separator.md`.

A `<separator bar="true"/>` inside a toolbar, horizontal or vertical `orient`, renders as a divider. A `<separator/>` without `bar` stays a flex spacer, so it does not draw a line. The divider size and colour are in `toolbar.css`.

## Bundle

`toolbar.css.dsp`. Toolbarbuttons inside the toolbar are styled by `footer.css.dsp` (yes, the file is named footer despite covering toolbar buttons — historical naming).
