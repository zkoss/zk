# image

`Image` (`<image>`/`<img>`, `zul.wgt.Image`) — renders a bare
`<img class="z-image">` (`mold/image.js`); the widget's root node is the `<img>` itself
(`Image.getImageNode()` returns `$n()`), so there is no wrapper element.

## No css-uri → must be globally bundled

`Image` has **no `<css-uri>` in `lang.xml`** (stock ZK styles `.z-image` in the global
norm stylesheet). A theme must load `js/zul/wgt/css/image.css` itself; a 1:1 auto-scanned
`image.css.dsp` is **never requested by ZK**. See
`reference/component-css-must-be-bundled-or-css-uri.md`. The rules are in
`zul/src/main/resources/web/js/zul/wgt/css/image.css`.
