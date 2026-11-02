# progressmeter

A horizontal progress bar.

## Fill class is `.z-progressmeter-image`, not `.z-progressmeter-bar`

The element that grows with the value uses class `.z-progressmeter-image`. Easy to guess wrong because no image is actually involved — the name is historical.

```css
/* WRONG — does not match */
.z-progressmeter-bar { background: <primary>; }

/* CORRECT */
.z-progressmeter-image { background: <primary>; }
```

## DOM structure

```
.z-progressmeter
└─ .z-progressmeter-image         (width grows with value; the visible fill)
```

The track itself is `.z-progressmeter` (which contains the fill); no separate `-track` element.

## Indeterminate state

With `indeterminate="true"`, ZK adds `.z-progressmeter-indeterminate` to the root (`Progressmeter.ts`, `domClass_` and `setIndeterminate`).

## Loading bar variant

Loadingbar (EE, zkmax) is a client widget started through `org.zkoss.zkmax.ui.util.Loadingbar`, not a ZUL tag. It extends `zul.wgt.Progressmeter` (`zkmax/wgt/Loadingbar.ts`). DOM: `.z-loadingbar > span.z-loadingbar-image.z-loadingbar-colorbar + span.z-loadingbar-effectbar.z-loadingbar-colorbar`; `-indeterminate` comes from Progressmeter. Progressmeter itself is CE (`lang.xml`).

## Bundle

`progressmeter.css.dsp`.
