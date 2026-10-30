# Authoring ZUL pages for the preview app

The preview corpus — `zkthemelab/src/main/webapp/web/*.zul`, 162 pages — is the theme's completion
criterion, not the component list. Two reasons: some components never get a page of their own
because they are sub-components (listitem), and some styled features map to no single component
(notification). **The theme is done only when every preview page is verified.** These are the
rules that keep those pages honest.

## Compose from utilities; never invent page-local CSS

Build `sclass` from the built-in `z-*` utilities rather than adding a `<style>` block to the page.
The complete list is the generated manifest `zul/codegen/resources/web/zul/css/utility-classes.json`
(written by `node scripts/build-css.js --module zul`; names only:
`jq -r '.classes[].name' <file>`). Reading the sources in
`zul/src/main/resources/web/zul/css/utility/*.css` for a class's intent is fine, but do not grep them
for the list — `_print.css` also names component classes that are not utilities.

| Need | Use |
|---|---|
| background | `z-bg-*` (`z-bg-surface-variant`, `z-bg-primary-container`, …) |
| spacing | `z-p-*` / `z-m-*` / `z-gap-*`, and `z-vstack` / `z-hstack` for rhythm |
| radius | `z-rounded*` |
| typography | `z-fs-*` / `z-fw-*` / `z-text-*` |
| layout | `z-d-flex` + `z-flex-*` / `z-justify-*` / `z-align-*`; `z-d-grid` + `z-grid-cols-auto` |

Approximate is fine — 13px becomes `z-fs-body-sm`, a 6px radius becomes `z-rounded`. The rule
exists because `stack.zul` once grew three `u-*` classes that duplicated utilities already
shipped; each ad-hoc class drifts from the tokens and hides what should have been a question.

**If no utility fits, stop and ask** — "there is no `z-bg-tertiary-container`; add one, or use
`z-bg-secondary-container`?" A gap in the utility set is a discussion, not a licence for a
`<style>` block. Per-instance custom-property overrides (`style="--zk-cols: 3"`) are the
*intended* way to drive the grid utilities, not a violation. The existing helpers in
`usecase/usecase.css` predate the rule and are tolerated; the rule targets *new* CSS.

## Two parse traps that present as a 404, not an error

The preview controller reports a ZUL that fails to parse as a plain **404 JSON**, so a page you
just created and cannot load is almost certainly a parse error, not a missing file (new `.zul`
files are served live; the resolver is not cached). Run `xmllint --noout <file>.zul` first.

1. **Inline HTML needs the `h:` prefix.** `<b>`, `<code>`, `<i>`, `<pre>` are not ZK components;
   unprefixed they throw "component not found". Use `<h:b>`, `<h:code>`, `<h:pre>` with
   `xmlns:h="native"` on the root. Native elements take `class`, ZK widgets take `sclass`.
2. **An XML comment cannot contain `--`.** A decorative `<!-- section ------ -->` is illegal XML
   and kills the whole parse. Use `=` runs. `--` inside `<![CDATA[ … ]]>` is fine — which is how
   a code sample showing `--zk-cols` is written safely inside `<h:pre>`.

## Well-formed is not rendering

`xmllint` proves XML well-formedness only. ZK **semantic** errors surface at compose time as
**HTTP 500**: an unsupported mold (`<panel mold="3d">` — Panel has only `default`; `3d` belongs to
Groupbox), or an attribute with no setter (`disabled="true"` on Signature or Tbeditor — neither has
`setDisabled`). Fast check with the app up:

```bash
curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8085/<page>.zul
```

The Playwright **`smoke`** project (`render-smoke.spec.ts`) does this for every page and asserts a
composed body. **When you add a preview page, add it to that spec's page list.**

## ZK wraps raw text in a Label — typography on the wrapper is dead

Two facts that combine into a silent trap:

1. Raw text inside a ZK `<div>` is not a text node. `<div sclass="brand">Marble</div>` renders a
   `zul.wgt.Label` child — identical DOM to an explicit `<label value="Marble"/>`.
2. `.z-label` explicitly declares `font-family`, `font-size`, `font-weight`, `line-height` and
   `color`. An element's own declaration always beats an inherited value; layers and specificity
   are irrelevant, because inheritance is not a cascade competitor.

So **`font-size`, `font-weight` and `color` set on a container never reach its text** — only
properties `.z-label` does not declare (`letter-spacing`, `padding`, `text-align`) land. The
sidebar wordmark once declared 18px / 700 / primary on its wrapper and rendered at 13px / 400 /
on-surface for months, with nothing erroring.

**Put typography on the label** (`sclass="z-text-xl z-fw-bold z-text-primary"`). When auditing,
treat "font or colour declared on a container of ZK text" as a suspected dead declaration and
verify with `getComputedStyle` on the `.z-label`, not the wrapper. Cheap probe without a browser:
write a throwaway `.zul` into `zkthemelab/build/inplaceWebapp/` (gitignored, served live) and `curl`
it to read the widget tree.

## ZK `hflex` / `vflex` overwrite CSS `flex-direction`

ZK realises `vflex` by setting the **parent** to `display:flex; flex-direction:column` (`hflex` →
`row`) and tagging each sized child `z-flex-item`. A child carrying `vflex="1"` therefore flips a
`z-flex-row` parent to **column** — computed style shows `flex-direction: column` even though the
class says row, and the children stack.

**Rule:** inside a CSS-flex container built from utilities, the direct children carry only
`z-flex-*` (`z-flex-1` to fill; the default `align-items: stretch` gives equal height), never ZK
`hflex`/`vflex`. Use a plain `<div>` as the container, not `<vlayout>` — it wraps every child in a
`.z-vlayout-inner` div that breaks the `z-flex-1` chain. (`<hlayout>` children are
`inline-block`, so `z-flex-1` never grows there either — see the `zk-component-rules` skill.)

## Widgets carry no default margins

Stock ZK widgets have zero margin and ZK's sizing JS assumes it (`zk/flex.ts` writes
`calc(100% - marginHeight)` on both axes in row mode). A theme-global `margin-block-end` on
container widgets was tried and produced a 12px hole before every splitter; it was **deleted**,
not patched. Spacing is opt-in: `z-vstack` / `z-hstack` / `z-mb-*`, or `<vlayout spacing>`.
Never add a margin to a bare widget selector. The full lesson is `reference/pitfalls.md` §7;
the policy is `doc/spec/spacing-policy.md`.

## The State Matrix is generic utilities — `pv.css` is gone

`pv.css` and every `pv-*` class were deleted. Do not look for them or recreate them. The
label-column + N-value-column matrix used by the `pv/*-content.zul` templates is:

- container: `z-d-grid z-grid-cols-auto z-overflow-x-auto z-gap-x-6 z-gap-y-2 z-items-start`
  with inline `style="--zk-cols: N; --zk-col-min: min-content"`;
- section title: `z-grid-col-full` (`grid-column: 1 / -1`);
- each row and the header row: `z-d-contents` (`display: contents`), which flattens the wrapper
  so every cell is a direct grid child and the `auto` label column is sized once across all rows.
  The header row is the *first* `.z-d-contents`; tests `.slice(1)` past it.

**`--zk-col-min: min-content` is required.** Without it the `minmax(0, 1fr)` tracks collapse on a
narrow viewport and the natural-width inputs overlap. A fixed px floor is fragile (an input wider
than the floor still overlaps); `min-content` sizes each column to its input, so cells never
collapse and the matrix scrolls horizontally on mobile. `tablet-matrix-scroll-usable` guards it.

The general lesson: `z-grid-cols-auto` aligns its `auto` column only when all cells share **one**
grid. Rows that are separate wrappers need `display: contents` (or subgrid) to flatten.

## The mobile phantom scrollbar is horizontal overflow, not a theme bug

A vertical scrollbar on mobile with nothing sticking out is a side effect of **horizontal** content
overflow: the mobile engine widens the layout viewport to the content, scales the page down, and
the same scale makes the layout viewport taller than the screen. Ratios prove it —
`scrollW/clientW == scrollH/clientH`. Contain the wide content in a `z-overflow-x-auto` box and
both overflows drop to zero. The viewport meta *is* present on mobile (ZK's `viewport="auto"`
emits it for the mobile language; a desktop-UA `curl` misleadingly shows none), and `_reset.css`'s
`html, body { height: 100% }` is correct — **do not switch it to `dvh`**; `100%` tracks a desktop
horizontal scrollbar, `vh`/`dvh` overflow past it.

## Renaming, deleting and probing preview pages

- After you rename or delete a zkthemelab `.zul`, also delete the stale copy in `zkthemelab/build/inplaceWebapp` and confirm the old URL returns 404. Gretty's inplace copy only adds and updates files; it never deletes them.
- Probe pages never go in `src/main/webapp`. Anything there is copied to `build/inplaceWebapp`, and deleting the source does not stop the copy from being served. Put probes directly in `build/inplaceWebapp`, and delete them after use.
- Interactive demos on a preview page use server-side listeners (`onClick`, `onCheck`) that call `Notification.show(..., "middle_center", ...)` to show the result, so the reviewer can see it. Import `Notification` yourself in the zscript.
