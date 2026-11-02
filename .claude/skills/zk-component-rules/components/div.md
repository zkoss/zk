# div

`Div` (`<div>`, `zul.box.Div`) — a block container rendered as `<div class="z-div">`.

## No css-uri → must be globally bundled

`Div` has **no `<css-uri>` in `lang.xml`** (stock ZK styles `.z-div` in the global
`norm.less`). Marble must load `js/zul/box/css/div.css` via the global bundle (`normFiles` →
`norm.css.dsp`); a 1:1 auto-scanned `div.css.dsp` is **never requested by ZK**. See
`reference/component-css-must-be-bundled-or-css-uri.md`. (Orphan sweep 2026-06-30: `.z-div`
own rule served by no stylesheet across 26 rendered elements.)

## What its CSS provides

- `.z-div` — `box-sizing: border-box` (so authored width/padding behave predictably).

## Layout primitives add no padding to the container

`.z-div`, `.z-hlayout` and `.z-vlayout` set no padding on the container itself. The only padding ZK writes on these layouts is the inline spacing between children: `hlayout` writes `padding-right` and `vlayout` writes `padding-bottom` on each non-last inner element (default `spacing` 5px). Content therefore sits at the container edge. Authors who need an inset wrap the content in a padded element.
