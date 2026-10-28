# caption

A title bar child component, typically placed inside `window`, `panel`, or `groupbox` to provide a styled header.

## DOM structure

```
.z-caption
└─ .z-caption-content              (content wrapper — plain text node inside, no .z-caption-text child)
   ├─ .z-caption-label             (label text element)
   └─ .z-caption-image             (image element, if image= attribute set)
```

## Critical: no `.z-caption-text` element

The title text renders as a plain text node (or inside `.z-caption-label`), NOT inside a `.z-caption-text` wrapper. This follows the same pattern as `.z-window-header`.

## CSS file

`caption.css.dsp`

## Note

Stub entry — full documentation pending spec-author run (Phase 3/4).
See `doc/spec-author-pipeline-plan.md (retired with the template)`.
