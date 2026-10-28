# html

`Html` (`<html>`, `zul.utl.Html`) — renders raw HTML markup inside
`<div class="z-html">…</div>` (no escaping). Used for rich text / CMS-style content.

## No css-uri → must be globally bundled

`Html` has **no `<css-uri>` in `lang.xml`** (stock ZK has no per-component stylesheet for
it). Marble must load `js/zul/layout/css/html.css` via the global bundle (`normFiles` →
`norm.css.dsp`); a 1:1 auto-scanned `html.css.dsp` is **never requested by ZK**, leaving the
embedded markup completely unstyled. See
`reference/component-css-must-be-bundled-or-css-uri.md`. (Orphan sweep 2026-06-30.)

## What its CSS provides

- `.z-html` — block, typescale `body-medium`, `color: --zk-color-on-surface`, `box-sizing`.
- Normalizes common embedded tags: `h1`–`h4` (typescale headline/title), `p` margins,
  `a` (`--zk-color-primary`, underline on hover), `code`/`pre` (mono + surface-container
  background + radius), `table`/`th`/`td` (collapsed borders via `--zk-color-outline-variant`,
  `th` on `--zk-color-surface-container-low`). This is the bulk of the styling lost when
  orphaned — raw HTML falls back to UA defaults.
