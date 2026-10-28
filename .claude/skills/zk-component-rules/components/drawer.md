# drawer

`zkmax.wgt.Drawer` — a sliding overlay panel (EE). `<drawer position="right|left|top|bottom"/>`.

## Closing is gated on a CSS `transitionend` — the theme MUST animate `.z-drawer-real`

This is the single most important fact. ZK's `Drawer.setVisible(false)` does **not** hide the panel directly. It registers a one-shot listener and waits:

```js
jq(real).one('transitionend', () => { super.setVisible(false); zkn.undoVParent(); });
// then it removes the open class:
jq(n).toggleClass(this.$s('open'), false);
```

So the close sequence is: remove `.z-drawer-open` → **a CSS transition on `.z-drawer-real` must fire and complete** → `transitionend` → panel gets `display:none`.

If the theme defines **no transition / no transform change** between open and closed on `.z-drawer-real`, `transitionend` never fires and the drawer **never closes** (it stays `display:block`; mask-click, close button, and `close()` all appear dead). The mask *does* have an `onClick`→`close` handler wired in `bind_` — the bug is never in the handler, it's the missing animation.

Minimum required CSS:

```css
.z-drawer-real { transition: transform <ms> <easing>; }
.z-drawer-right  .z-drawer-real { transform: translateX(100%); }   /* closed */
.z-drawer-left   .z-drawer-real { transform: translateX(-100%); }
.z-drawer-top    .z-drawer-real { transform: translateY(-100%); }
.z-drawer-bottom .z-drawer-real { transform: translateY(100%); }
```

## Open state uses a COMPOUND selector, not a descendant one

When open, ZK adds `z-drawer-open` to the **same root element** that already carries `z-drawer-<position>` (e.g. `class="z-drawer z-drawer-right z-drawer-open"`). The open transform reset must out-specify the per-position closed rule:

```css
/* CORRECT — compound (both classes on root) → specificity (0,2,0)+descendant beats the closed rule */
.z-drawer-open.z-drawer-right .z-drawer-real { transform: none; }

/* WRONG — descendant ties on specificity with `.z-drawer-right .z-drawer-real`
   and loses on source order, leaving the panel stuck off-screen */
.z-drawer-open .z-drawer-real { transform: none; }
```

The stock ZK iceblue theme uses the compound form for exactly this reason.

## DOM structure

```
<aside class="z-drawer z-drawer-<pos> [z-drawer-open]">   (position:fixed, full-viewport)
  <div class="z-drawer-mask [z-drawer-mask-enabled]">     (backdrop; onClick→close)
  <div class="z-drawer-real">                             (the sliding panel — animate THIS)
    <div class="z-drawer-header"><div class="z-drawer-title">
    <div class="z-drawer-close">                          (only if closable=true; onClick→close)
    <div class="z-drawer-container"><div class="z-drawer-cave"> … children …
```

- Closed state is `display:none` on the root (ZK toggles it), so a full-viewport `position:fixed` root is safe.

## The mask element is ALWAYS rendered — gate the visible scrim on `.z-drawer-mask-enabled`

`_mask` defaults **true**. The mold ALWAYS emits the `.z-drawer-mask` element; it only appends the `z-drawer-mask-enabled` modifier when `_mask` is true (`this._mask ? this.$s('mask-enabled') : ''`), and `setMask(false)` strips that modifier — it never removes the element. So a theme that makes the scrim visible on the bare `.z-drawer-mask` (or on `.z-drawer-open .z-drawer-mask`) will show a backdrop even for `mask="false"`.

```css
/* CORRECT — scrim visibility keys off the modifier */
.z-drawer-mask { background-color: …; opacity: 0; }   /* transparent by default */
.z-drawer-open .z-drawer-mask.z-drawer-mask-enabled { opacity: 1; }

/* WRONG — mask="false" still shows the backdrop */
.z-drawer-open .z-drawer-mask { opacity: 1; }
```

The mask retains its `onClick→close` handler regardless of `mask-enabled`, so click-outside-to-close still works when the scrim is invisible (matches stock ZK).

## Close button (`closable=true`)

The mold emits the close as a **sibling of `.z-drawer-header`** (both children of `.z-drawer-real`), not inside the header:

```html
<div class="z-drawer-close" role="button" tabindex="0" aria-label="…"><i class="z-icon-times"></i></div>
```

It is `display:none` inline until `closable=true` (`setClosable` toggles it). A drawer is an MD3 **side sheet** → the close icon button belongs at the **top-trailing corner of the header** (`doc/spec/md3-close-affordance-placement.md` §2). Since `.z-drawer-real` is a positioned ancestor, `position:absolute; top; right` on `.z-drawer-close` anchors the corner placement independently of whether the header is visible (header is `display:none` when title is empty). The glyph is the ZK icon-font `<i class="z-icon-times">` (sized by the container's `font-size`).

## Bundle

`drawer.css.dsp` (source `js/zkmax/wgt/css/drawer.css`).
