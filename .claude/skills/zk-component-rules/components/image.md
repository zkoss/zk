# image

`Image` (`<image>`/`<img>`, `zul.wgt.Image`) — renders an image inside
`<span class="z-image"><img…></span>`.

## No css-uri → must be globally bundled

`Image` has **no `<css-uri>` in `lang.xml`** (stock ZK styles `.z-image` in the global
`norm.less`). Marble must load `js/zul/wgt/css/image.css` via the global bundle (`normFiles`
→ `norm.css.dsp`); a 1:1 auto-scanned `image.css.dsp` is **never requested by ZK**. See
`reference/component-css-must-be-bundled-or-css-uri.md`. (Orphan sweep 2026-06-30.)

## What its CSS provides

- `.z-image` — `display: inline-block`, `max-width: 100%`, `vertical-align: middle`,
  `border-radius: var(--zk-shape-corner-extra-small)` (the MD3 rounding lost when orphaned).
- `.z-image img` — `display: block`, `max-width: 100%`, `height: auto` (responsive image).
