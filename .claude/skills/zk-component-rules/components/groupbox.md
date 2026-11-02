# groupbox

A box with a caption and a content area. Similar to panel but lighter weight (no toolbar, simpler header).

## DOM structure

```
.z-groupbox
├─ .z-groupbox-header            (present when the groupbox has a `title` or a <caption> child)
└─ .z-groupbox-content           (the body — NOT .z-groupbox-body)
```

## Critical: content area is `.z-groupbox-content`

Not `.z-groupbox-body`. Same trap as panel's `.z-panelchildren`.

## Header content: caption vs title

`mold/groupbox.js` renders the header when there is a `title` or a `<caption>` child, and the two cases have different content:

- With a `<caption>` child: `.z-groupbox-header > .z-caption` (the caption's own redraw; see `components/caption.md`).
- With the `title` attribute (no caption): `.z-groupbox-header > .z-groupbox-title > .z-groupbox-title-content` (the title text).
- With neither, no header is rendered and the root gets `.z-groupbox-notitle`.

## State modifier classes

- `.z-groupbox-collapsed` — on the root; only in the default mold, when the groupbox is not open. Content hidden, only header visible.
- `.z-groupbox-readonly` — on the **header** (not the root), added by the mold when `closable=false`.
- `.z-groupbox-notitle` — on the root when there is no title and no caption.
- `.z-groupbox-3d` — on the root, only in the non-default mold (`mold="3d"`).

## Bundle

`groupbox.css.dsp`.
