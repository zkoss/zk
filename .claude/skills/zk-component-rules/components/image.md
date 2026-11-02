# image

`Image` (`<image>`/`<img>`, `zul.wgt.Image`) — renders an image inside
`<span class="z-image"><img…></span>`.

## No css-uri → must be globally bundled

`Image` has **no `<css-uri>` in `lang.xml`** (stock ZK styles `.z-image` in the global
norm stylesheet). A theme must load `js/zul/wgt/css/image.css` itself; a 1:1 auto-scanned
`image.css.dsp` is **never requested by ZK**. See
`reference/component-css-must-be-bundled-or-css-uri.md`. The rules are in
`zul/src/main/resources/web/js/zul/wgt/css/image.css`.
