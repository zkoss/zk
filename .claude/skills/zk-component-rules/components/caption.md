# caption

A title bar child component, typically placed inside `window`, `panel`, or `groupbox` to provide a styled header.

## DOM structure

```
.z-caption
└─ .z-caption-content              (div#-cave — content wrapper)
   ├─ img.z-caption-image          (only when image= is set)
   ├─ <i> icon                     (only when iconSclass is set)
   └─ title text                   (see below)
```

`Caption.ts` `domContent_()` builds the content. With no image and no icon, the title is a plain text node inside `.z-caption-content`. With an image or icon, the title is wrapped in `span.z-caption-label`. There is no `.z-caption-text` element. This follows the same pattern as `.z-window-header`. When the parent is not a default-mold groupbox, `mold/caption.js` also emits the parent's `-icons` container (collapse / minimize / maximize / close buttons) after `.z-caption-content`.

## CSS file

`caption.css.dsp`
