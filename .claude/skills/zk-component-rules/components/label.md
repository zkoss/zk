# label

`Label` (`<label>`, `zul.wgt.Label`) — an inline text widget rendered as
`<span class="z-label">…</span>`. Extremely common (hundreds per page).

## No css-uri → must be globally bundled

`Label` has **no `<css-uri>` in `lang.xml`** (stock ZK styles `.z-label` in the global
norm stylesheet). So a theme must load `js/zul/wgt/css/label.css` itself (for example
through a global bundle); a 1:1 auto-scanned `label.css.dsp` is **never requested by
ZK** and every label renders with inherited/browser defaults. See
`reference/component-css-must-be-bundled-or-css-uri.md`. The rules are in
`zul/src/main/resources/web/js/zul/wgt/css/label.css`.
