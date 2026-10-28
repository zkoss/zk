# Inline-flex overflow in grid cells

Many ZK input components default to `display: inline-flex` (or `inline-block`). In a CSS grid cell, this causes the component to shrink to content width instead of filling the cell — which leads to visible overlaps when placed in tight grid layouts.

## Components affected

Components that render as `inline-flex` / `inline-block` root spans:

- `.z-bandbox`
- `.z-combobox`
- `.z-datebox`
- `.z-timebox`
- `.z-spinner`, `.z-doublespinner`
- `.z-textbox` (when not multiline)
- `.z-intbox`, `.z-longbox`, `.z-doublebox`, `.z-decimalbox`
- `.z-searchbox`
- `.z-selectbox`

## Symptom

A `display: grid` parent with `grid-template-columns: 120px 200px 200px` reserves 200px per cell, but the inline-flex component inside the cell shrinks to ~140px (its content width), leaving 60px of empty space — and the next cell's component visually overlaps the gap if the layout is tight.

## Fix

Force the component to fill its grid cell width:

```css
.pv-row > div > .z-bandbox,
.pv-row > div > .z-combobox,
.pv-row > div > .z-datebox,
.pv-row > div > .z-timebox,
.pv-row > div > .z-spinner,
.pv-row > div > .z-doublespinner,
.pv-row > div > .z-textbox,
.pv-row > div > .z-intbox,
.pv-row > div > .z-longbox,
.pv-row > div > .z-doublebox,
.pv-row > div > .z-decimalbox,
.pv-row > div > .z-searchbox,
.pv-row > div > .z-selectbox { width: 100%; }
```

A descendant rule (`.pv-row > div > .z-bandbox`) has specificity 0,2,0, overriding the framework's `.z-bandbox { display: inline-flex }` rule (0,1,0). Inline ZUL `width="..."` attributes still win over this (they become inline styles), so explicit per-component sizing remains possible.

## Where to apply

- **Preview pages**: include this rule in the page-local CSS (e.g. `pv.css`). Do not push it into the theme CSS — production layouts do not always want full-width inputs.
- **Production layouts**: when authoring a form, either set `width="100%"` per component or wrap each input in a flex container with `flex: 1`.

## Why not change the ZK default?

`inline-flex` is the correct default for ZK inputs because it lets them sit inline with text labels (`<label>Name: <textbox/></label>` flows nicely). Forcing `display: block` or `width: 100%` at framework level would break the most common usage. The grid-cell width fix is a **layout context** decision, not a framework decision.
