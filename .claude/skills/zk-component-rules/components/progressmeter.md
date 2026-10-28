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

If `value=""` or no value, ZK animates the fill with a striped pattern. The class `.z-progressmeter-indeterminate` is added to the root.

## Loading bar variant

`<loadingbar>` is a separate thin-bar component (PE). Its classes:
- `.z-loadingbar` — root (usually fixed, full-width top bar)
- `.z-loadingbar-colorbar` — the animated fill
- `.z-loadingbar-indeterminate` — indeterminate animation state

## Bundle

`progressmeter.css.dsp`.
