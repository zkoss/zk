# errorbox

`zul.inp.Errorbox` — the inline validation tooltip ZK pops next to an input when a constraint fails.

## Served CSS lives in the inp bundle, not the obvious `errorbox.css`

Errorbox styles are duplicated in two source files:

- `js/zul/wgt/css/errorbox.css` → `errorbox.css.dsp`
- `js/zul/inp/css/input.css`     → `input.css.dsp`  ← **this is the one the widget actually loads**

`Errorbox` is a `zul.inp.*` widget, so its styling is served from the **inp** bundle (`input.css.dsp`). Editing only `wgt/css/errorbox.css` changes nothing visible. Always fix `inp/css/input.css` (and keep the wgt copy in sync so they don't diverge). Verify against the served `zul/codegen/resources/web/js/zul/inp/css/input.css.dsp`.

## The pointer arrow must NOT be hidden with stylesheet `display:none`

ZK reveals the arrow in `Errorbox._fixarrow` with:

```js
jq(pointer).show();
```

jQuery `.show()` only **clears an inline `display`** value — it cannot beat a stylesheet rule `.z-errorbox-pointer { display: none }`. So if the theme hides the pointer via a stylesheet, the arrow is **permanently invisible** even though ZK positions it correctly.

Correct base: keep the pointer `display: block`. With all four `border` sides `transparent`, the 0×0 triangle is invisible anyway; ZK adds a direction class (`z-errorbox-up|down|left|right`) which colours **one** side into a visible beak. Colour it with the error border colour (not the faint `error-container`) so it reads against a light page.

`_fixarrow` sets the pointer's inline `top/left/right/bottom` (e.g. `bottom:-4px` for a down arrow) and node padding per direction — the theme only supplies `display`, `border` size, and colour.

## `_fixarrow` rewrites the `.z-errorbox` OUTER padding per direction — make it symmetric, don't zero it

On every reposition `Errorbox._fixarrow` runs:

```js
node.style.padding = '0';
// then, depending on which side faces the field:
s.paddingLeft  = `${pw}px`;   // beak points left  (box is to the RIGHT of the field)
s.paddingRight = `${pw}px`;   // beak points right (box is to the LEFT of the field)
s.paddingTop   = `${ph}px`;   // beak points up    (box is BELOW the field)
s.paddingBottom= `${ph}px`;   // beak points down  (box is ABOVE the field)
```

`pw`/`ph` ≈ 8px (ZK computes `pw = 2 + pointerBorderWidth/2`; with a 6px pointer border that is `2 + 6 = 8`). The padding lands on the **outer `.z-errorbox`** node — the one with `display:table`. Because `.z-errorbox-content` is the `table-cell`, that padding **shifts the content** by 8px on the padded side. But `.z-errorbox-icon` and `.z-errorbox-close` are absolutely positioned against `.z-errorbox` and **don't move**. Net effect: the icon→text gap and close→edge gap drift 8px depending on whether the box landed right/left/above/below the field — so the *same* errorbox looks different in each position (icon 4px from text when the box is on the right, 12px when below — user-visible).

**The padding is load-bearing for the beak — do NOT just zero it.** The pointer is positioned at `left/top:-4px` and the triangle is `2*6 = 12px` wide, so its base sits at `-4 + 12 = 8px` from the box edge. That 8px is exactly `pw`: ZK insets the content by `pw` so the triangle base lands **flush at the content edge**, with the triangle entirely outside. Set `.z-errorbox { padding: 0 }` and the content fills to the box edge while the beak base stays at 8px → the beak ends up **8px INSIDE the content** (visibly wrong — the arrow appears inside the box).

The correct fix keeps the beak room but makes it **symmetric on all four sides**, then re-adds it to the icon/close offsets so they track the (now uniformly inset) content:

```css
.z-errorbox {
    --zk-errorbox-beak: 8px;                       /* = ZK's pw/ph = 2 + the 6px pointer border */
    padding: var(--zk-errorbox-beak) !important;   /* beats ZK's inline one-sided padding */
}
.z-errorbox-icon  { left:  calc(var(--zk-spacing-3) + var(--zk-errorbox-beak)); }  /* 12px from content */
.z-errorbox-close { right: calc(var(--zk-spacing-1) + var(--zk-errorbox-beak)); }  /* 4px from content */
```

`!important` is required (same reason as the pointer `display`): ZK writes the padding as an **inline** style every reposition, and a normal stylesheet rule loses to inline. The shorthand `padding !important` also overrides ZK's inline `paddingLeft`/`paddingTop`/… longhands. With symmetric padding the content is inset equally in every direction, so the beak lands flush at whichever content edge it points to, and the icon/close keep a constant offset from the content — only the pointer direction differs. The `8px` is coupled to the `6px` pointer border via ZK's `pw = 2 + border` formula; if you change the pointer border, update `--zk-errorbox-beak` to match. Regression guard: `screenshot.spec.ts › errorbox-position-invariance` (asserts both the constant gaps and zero beak intrusion).

## DOM structure

```
<div class="z-errorbox">                      (display:table; ZK positions it as a floating popup)
  <div class="z-errorbox-pointer z-errorbox-<dir>"/>   (the beak; keep display:block)
  <i   class="z-errorbox-icon z-icon-exclamation-triangle"/>  (absolute, left edge of content)
  <div class="z-errorbox-content" title="…">message</div>
  <div class="z-errorbox-close"><i class="z-errorbox-icon z-icon-times"/></div>  (if closable)
```

The warning `<i>` is absolutely positioned over the content's left padding; leave enough `content` left-padding (≥ icon-left + icon-width + gap) so the text never collides with it.

## Forcing a live errorbox for verification

Synthetic typing + Tab is unreliable in automation (focus juggling). Drive it through the widget instead:

```js
var w = zk.Widget.$(document.querySelector('input.z-textbox'));
w.focus(); w.setValue('x'); w.setValue(''); w.fireOnChange({});  // → z-textbox-invalid + errorbox
```

## Bundle

`input.css.dsp` (authoritative); mirror in `errorbox.css.dsp`.
