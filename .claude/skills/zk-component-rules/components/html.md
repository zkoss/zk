# html

`Html` (`<html>`, `zul.utl.Html`) — renders raw HTML markup inside
`<div class="z-html">…</div>` (no escaping). Used for rich text / CMS-style content.

## No css-uri → must be globally bundled

`Html` has **no `<css-uri>` in `lang.xml`** (stock ZK has no per-component stylesheet for
it). A theme must load `js/zul/layout/css/html.css` itself (for example through a global
bundle); a 1:1 auto-scanned `html.css.dsp` is **never requested by ZK**, leaving the
embedded markup completely unstyled. See
`reference/component-css-must-be-bundled-or-css-uri.md`.

## What its CSS provides

`zul/src/main/resources/web/js/zul/layout/css/html.css` holds the rules for `.z-html` and for the common embedded tags (headings, `p`, `a`, `code`/`pre`, tables). This is the bulk of the styling lost when the file is orphaned: raw HTML falls back to UA defaults.
