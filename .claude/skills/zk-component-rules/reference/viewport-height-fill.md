# Viewport-height fill: `html { height: 100% }` + `body { height: 100% }`, never `100vh`

**Rule:** the base reset must give the page two co-operating declarations:

```css
html { height: 100%; }       /* definite height — required, see below */
body { height: 100%; }       /* NOT 100vh, NOT min-height (see root page below) */
```

`body` must end up **as tall as the viewport** (long content overflows into the viewport scroll, as in IceBlue). Two
distinct things break if it collapses to content height — one cosmetic, one
functional.

## Why `body` must fill the viewport — the functional reason (ZK dismiss)

ZK closes an open floating popup (colorbox, combobox, datebox, bandbox,
menupopup, …) on an outside mousedown — but only when the click lands inside the
body box. ZK core `mount.ts` `_docMouseDown` guards the auto-close with:

```js
if (dEvent.clientX <= body.clientWidth && dEvent.clientY <= body.clientHeight)
    Widget.mimicMouseDown_(...);   // fires onFloatUp → closes popups
```

The `clientY <= body.clientHeight` test is meant to ignore scrollbar clicks. But
if `body` has collapsed to its **content height**, every click in the empty area
below the content is `clientY > body.clientHeight` → the auto-close is skipped →
**the popup never closes when you click below the content.** Clicking *above*
(over real content) still closes it — producing the tell-tale "dismiss only works
above the popup, not below it" symptom. This is theme-wide: it hits every ZK
floating popup, not one component.

## Why `body` must fill the viewport — the cosmetic reason (no scrollbar footgun)

`body` also paints the page background. To fill the viewport without a spurious
vertical scrollbar, use `min-height: 100%`, **never `100vh`**. `100vh` is the
*full* viewport height and is **blind to scrollbars**: the moment any wide
content triggers a horizontal scrollbar, the visible area shrinks by the
scrollbar thickness but `100vh` stays pinned, so body now exceeds the visible
area and a **spurious vertical scrollbar** appears on a page whose content fits.
(`100dvh` has the same flaw — it accounts for browser UI chrome, not scrollbars.)
`min-height: 100%` resolves against the containing block, which **does** shrink
with the scrollbar.

## Why `height`, not `min-height` — the root page

ZK's root `zk.Page` div renders inline `height:100%`. A percentage height resolves only against a
**definite** parent height: `body { min-height: 100% }` is not definite, so `.z-page` collapsed to
content height, a press on the empty page area hit BODY (no widget) and ZK fired `onFloatUp`
without `{triggerByClick}` (non-closable Toast never dismissed, ZK-6084). IceBlue's
`html, body { height: 100% }` makes `.z-page` viewport-tall; Marble matches it with `body { height: 100% }`.

## The trap: `min-height: 100%` needs a definite containing-block height

A percentage `min-height` resolves to **0** when its containing block's height is
*indefinite*. `body`'s containing block is `<html>`. So `html { min-height: 100% }`
is **not enough** — `min-height` leaves html's height indefinite, body's
`min-height: 100%` computes to 0, and body collapses to content height (causing
the ZK dismiss bug above). `html` MUST declare a **definite** `height: 100%`
(which resolves against the viewport, the initial containing block). Only then
does `body { min-height: 100% }` resolve to the viewport height.

`html { height: 100% }` is itself scrollbar-aware (it resolves against the
content area, not the scrollbar-blind `vh` viewport), so it does not reintroduce
the footgun.

Background still covers the whole viewport via **CSS background propagation**:
with no background on `html`, `body`'s `background-color` is propagated to the
canvas.

## Applies to

Any theme's base reset (`base/_reset.css`). Theme-agnostic: a Sapphire or
corporate-dark reset hits the identical bugs. First seen on the colorbox preview
page — the fixed-width preview matrix (`pv/matrix.zul`,
`grid-template-columns: 120px repeat(N,160px)`) overflows narrow viewports
horizontally (surfacing the `100vh` scrollbar footgun), and a later
`body { min-height: 100% }` *without* `html { height: 100% }` collapsed body to
content height (surfacing the ZK dismiss-below-content bug).
