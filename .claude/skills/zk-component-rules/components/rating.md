# rating

A row (or column) of star icons; user clicks a star to set a value.

## Star class is `.z-rating-icon`, not `.z-rating-star`

Each star renders with class `.z-rating-icon`. The "star" vocabulary is the user-facing concept; the ZK class uses "icon" because the same renderer is used for non-star variants.

## States

- `disabled="true"` → `.z-rating-disabled` on root
- `readonly="true"` → `.z-rating-readonly` on root
- Checked stars get `.z-rating-icon-checked` on each icon element
- Hovered stars get `.z-rating-icon-hover` (JS class-toggling during hover preview)
- Stars beyond the current value get `.z-rating-icon-empty`

## Configurable max

`max` attribute controls the number of stars (default 5):

```xml
<rating max="3" rating="2"/>
<rating max="10" rating="7"/>
```

`rating` attribute is the current value.

## Orientation

`orient="vertical"` stacks stars vertically. The orientation class is on the root: `.z-rating-vertical` (default is implicit horizontal).

## Bundle

`rating.css.dsp`.
