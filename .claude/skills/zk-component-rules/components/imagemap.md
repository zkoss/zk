# imagemap

`Imagemap` (`<imagemap>`, `zul.wgt.Imagemap`) — an image with clickable regions, rendered as
`<span class="z-imagemap"><img…></span>` with absolutely-positioned area overlays.

## No css-uri → must be globally bundled

`Imagemap` has **no `<css-uri>` in `lang.xml`** (stock ZK styles `.z-imagemap` in the global
`norm.less`). Marble must load `js/zul/wgt/css/imagemap.css` via the global bundle
(`normFiles` → `norm.css.dsp`); a 1:1 auto-scanned `imagemap.css.dsp` is **never requested
by ZK**. See `reference/component-css-must-be-bundled-or-css-uri.md`. (Orphan sweep
2026-06-30.)

## What its CSS provides

- `.z-imagemap` — `display: inline-block`, **`position: relative`** (the positioning context
  the clickable area overlays depend on — the most important rule lost when orphaned),
  `max-width: 100%`, `vertical-align: middle`.
- `.z-imagemap img` — `display: block`, `max-width: 100%`, `height: auto`.
