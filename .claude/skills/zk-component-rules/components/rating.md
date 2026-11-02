# rating

A row (or column) of star icons; user clicks a star to set a value.

## Star class is `.z-rating-icon`, not `.z-rating-star`

Each star renders with class `.z-rating-icon`. The "star" vocabulary is the user-facing concept; the ZK class uses "icon" because the same renderer is used for non-star variants.

## States

State classes are on each icon `<i>`, not on the root (source: `zul/wgt/mold/rating.js`, `Rating.ts`):

```
<i class="z-rating-icon z-icon-star [z-rating-selected] [z-rating-disabled] [z-rating-readonly]">
```

- `disabled="true"` → `.z-rating-disabled` on every icon
- `readonly="true"` → `.z-rating-readonly` on every icon
- Stars up to the current value get `.z-rating-selected`
- Hovered stars get `.z-rating-hover` (toggled by JS during the hover preview)

## Configurable max

`max` attribute controls the number of stars (default 5):

```xml
<rating max="3" rating="2"/>
<rating max="10" rating="7"/>
```

`rating` attribute is the current value.

## Orientation

`orient="vertical"` stacks stars vertically. The root always carries an orientation class: `.z-rating-horizontal` (default) or `.z-rating-vertical`.

## Bundle

`rating.css.dsp`.
