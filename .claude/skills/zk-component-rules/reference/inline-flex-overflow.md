# Inline-level inputs in grid cells

An inline-level component (`display: inline-block` or `inline-flex`) in a CSS grid cell shrinks to its content width instead of filling the cell. In a tight grid layout the empty space around it can look like a gap, and a neighbouring cell's component can appear to overlap it.

Which ZK input roots are inline-level is decided by the theme's `display` value on each component root (bandbox, combobox, datebox, timebox, spinner, textbox family, searchbox, selectbox). Check the component's own CSS file for `display`.

## Symptom

A `display: grid` parent with `grid-template-columns: 120px 200px 200px` reserves 200px per cell, but an inline-level component inside the cell shrinks to its content width (for example ~140px), leaving 60px of empty space.

## Fix

Force the component to fill its grid cell width: a layout-level rule such as `.my-grid > div > .z-bandbox { width: 100%; }` (one selector per component, or a shared class). A descendant rule has specificity 0,2,0, so it overrides a single-class `display`/`width` rule on the component root (0,1,0). Inline ZUL `width="..."` attributes still win over it (they become inline styles), so explicit per-component sizing remains possible.

## Where to apply

- **Preview pages**: put the rule in the page-local layout, not in the theme CSS. Production layouts do not always want full-width inputs.
- **Production layouts**: when authoring a form, either set `width="100%"` per component or wrap each input in a flex container with `flex: 1`.

## Why not change the ZK default?

An inline-level root is the correct default for ZK inputs because it lets them sit inline with text labels (`<label>Name: <textbox/></label>` flows nicely). Forcing `display: block` or `width: 100%` at framework level would break the most common usage. The grid-cell width fix is a **layout context** decision, not a framework decision.
