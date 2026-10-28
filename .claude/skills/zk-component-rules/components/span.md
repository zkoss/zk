# span

`Span` (`<span>`, `zul.box.Span`) — an inline container rendered as `<span class="z-span">`.

## No css-uri → must be globally bundled

`Span` has **no `<css-uri>` in `lang.xml`** (stock ZK styles `.z-span` in the global
`norm.less`). Marble must load `js/zul/box/css/span.css` via the global bundle (`normFiles`
→ `norm.css.dsp`); a 1:1 auto-scanned `span.css.dsp` is **never requested by ZK**. See
`reference/component-css-must-be-bundled-or-css-uri.md`. (Orphan sweep 2026-06-30.)

## What its CSS provides

- `.z-span` — `display: inline`, `box-sizing: border-box`, `font-size: inherit`,
  `color: inherit` (a transparent inline wrapper that doesn't disturb surrounding text).

> Not to be confused with `Separator`/`Space`: `<space>` renders `.z-separator`, not
> `.z-span`. And `.z-span` compound refs in `_dnd.css`/`menu.css` (e.g.
> `.z-north .z-toolbar .z-span`) are NOT this file's own rule.
