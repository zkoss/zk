# label

`Label` (`<label>`, `zul.wgt.Label`) — an inline text widget rendered as
`<span class="z-label">…</span>`. Extremely common (hundreds per page).

## No css-uri → must be globally bundled

`Label` has **no `<css-uri>` in `lang.xml`** (stock ZK styles `.z-label` in the global
`norm.less`). So Marble must load `js/zul/wgt/css/label.css` through the global bundle
(`normFiles` → `norm.css.dsp`); a 1:1 auto-scanned `label.css.dsp` is **never requested by
ZK** and every label renders with inherited/browser defaults. See
`reference/component-css-must-be-bundled-or-css-uri.md`. (Found unstyled across 611 elements
in the 2026-06-30 orphan sweep.)

## What its CSS provides

- `.z-label` — typescale `body-medium` (family/size/weight/line-height), `color:
  --zk-color-on-surface`, `word-break: break-word`.
- `.z-label-pre` — `white-space: pre-wrap` + mono family (`<label pre="true">`).
- `.z-label-multiline` — `display: block` + `pre-wrap` (`<label multiline="true">`).
