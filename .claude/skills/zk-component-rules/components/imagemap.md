# imagemap

`Imagemap` (`<imagemap>`, `zul.wgt.Imagemap`) — an image with clickable regions, rendered as
`<span class="z-imagemap"><img…></span>` with absolutely-positioned area overlays.

## No css-uri → must be globally bundled

`Imagemap` has **no `<css-uri>` in `lang.xml`** (stock ZK styles `.z-imagemap` in the global
norm stylesheet). A theme must load `js/zul/wgt/css/imagemap.css` itself; a 1:1 auto-scanned
`imagemap.css.dsp` is **never requested by ZK**. See
`reference/component-css-must-be-bundled-or-css-uri.md`.

## Why `position: relative` matters

`.z-imagemap` needs **`position: relative`**: it is the positioning context the clickable area overlays depend on, and the most important rule lost when the file is orphaned. The other rules are in `zul/src/main/resources/web/js/zul/wgt/css/imagemap.css`.
