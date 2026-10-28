# groupbox

A box with a caption and a content area. Similar to panel but lighter weight (no toolbar, simpler header).

## DOM structure

```
.z-groupbox
├─ .z-groupbox-header            (caption row — optional)
└─ .z-groupbox-content           (the body — NOT .z-groupbox-body)
```

## Critical: content area is `.z-groupbox-content`

Not `.z-groupbox-body`. Same trap as panel's `.z-panelchildren`.

## Caption

If the groupbox has a `<caption>` child, ZK renders it as `.z-groupbox-header > .z-groupbox-title`. Without a caption, the header is absent.

## State modifier classes (on root)

- `.z-groupbox-collapsed` — content hidden, only header visible
- `.z-groupbox-readonly` — readonly state
- `.z-groupbox-notitle` — rendered without a title/header
- `.z-groupbox-3d` — 3D visual variant

## Title structure

When a caption is present, the header expands to:

```
.z-groupbox-header
└─ .z-groupbox-title
   └─ .z-groupbox-title-content   (the actual title text wrapper)
```

## Bundle

`groupbox.css.dsp`.
