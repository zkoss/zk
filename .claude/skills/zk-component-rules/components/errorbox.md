# errorbox

`zul.inp.Errorbox` — the inline validation tooltip ZK pops next to an input when a constraint fails.

## Served CSS lives in the inp bundle

`Errorbox` is a `zul.inp.*` widget and `lang.xml` registers no `css-uri` for it, so its styling is served from the **inp** bundle, `input.css.dsp`. The served errorbox rules are the errorbox block in `zul/src/main/resources/web/js/zul/inp/css/input.css`. `wgt/css/errorbox.css` is excluded from the build (`extraMergedFiles` in `scripts/build-css.js` only removes it from the 1:1 scan) and is not served; no `errorbox.css.dsp` exists.

## The pointer arrow must NOT be hidden with stylesheet `display:none`

ZK reveals the arrow in `Errorbox._fixarrow` with:

```js
jq(pointer).show();
```

jQuery `.show()` only **clears an inline `display`** value — it cannot beat a stylesheet rule `.z-errorbox-pointer { display: none }`. So if the theme hides the pointer via a stylesheet, the arrow is **permanently invisible** even though ZK positions it correctly.

Correct base: keep the pointer `display: block`. With all four `border` sides `transparent`, the 0×0 triangle is invisible anyway; ZK adds a direction class (`z-errorbox-up|down|left|right`) which colours **one** side into a visible beak. Colour it with the error border colour so it reads against a light page.

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

`pw`/`ph` ≈ 8px (ZK computes `pw = 2 + border/2`, where `border` is `zk(pointer).borderWidth()` — left + right summed; a 12px sum gives `2 + 6 = 8`). The padding lands on the **outer `.z-errorbox`** node — the one with `display:table`. Because `.z-errorbox-content` is the `table-cell`, that padding **shifts the content** by 8px on the padded side. But `.z-errorbox-icon` and `.z-errorbox-close` are absolutely positioned against `.z-errorbox` and **don't move**. Net effect: the icon→text gap and close→edge gap drift 8px depending on whether the box landed right/left/above/below the field — so the *same* errorbox looks different in each position (icon 4px from text when the box is on the right, 12px when below — user-visible).

**The padding is load-bearing for the beak — do NOT just zero it.** The pointer is positioned at `left/top:-4px` and the triangle is `2*6 = 12px` wide, so its base sits at `-4 + 12 = 8px` from the box edge. That 8px is exactly `pw`: ZK insets the content by `pw` so the triangle base lands **flush at the content edge**, with the triangle entirely outside. Set `.z-errorbox { padding: 0 }` and the content fills to the box edge while the beak base stays at 8px → the beak ends up **8px INSIDE the content** (visibly wrong — the arrow appears inside the box).

The correct fix keeps the beak room but makes it **symmetric on all four sides** (`padding: <beak> !important` on `.z-errorbox`, where `<beak>` = ZK's `pw`/`ph`, i.e. `2 + pointer border/2`), then re-adds `<beak>` to the icon `left` and close `right` offsets so they track the (now uniformly inset) content. The input stylesheet (`js/zul/inp/css/input.css`, errorbox block) shows the current rule.

`!important` is required (same reason as the pointer `display`): ZK writes the padding as an **inline** style every reposition, and a normal stylesheet rule loses to inline. The shorthand `padding !important` also overrides ZK's inline `paddingLeft`/`paddingTop`/… longhands. With symmetric padding the content is inset equally in every direction, so the beak lands flush at whichever content edge it points to, and the icon/close keep a constant offset from the content — only the pointer direction differs. The beak padding is coupled to the pointer border via ZK's `pw = 2 + border/2` formula (with a 6px pointer border that is 8px); if you change the pointer border, update the beak padding to match. Regression guard: `screenshot.spec.ts › errorbox-position-invariance` (asserts both the constant gaps and zero beak intrusion).

## DOM structure

```
<div class="z-errorbox">                      (display:table; ZK positions it as a floating popup)
  <div class="z-errorbox-pointer z-errorbox-<dir>"/>   (the beak; keep display:block)
  <i   class="z-errorbox-icon z-icon-exclamation-triangle"/>  (absolute, left edge of content)
  <div class="z-errorbox-content" title="…">message</div>
  <div class="z-errorbox-close"><i class="z-errorbox-icon z-icon-times"/></div>  (if closable)
```

The warning `<i>` is absolutely positioned over the content's left padding; leave enough `content` left-padding (≥ icon-left + icon-width + gap) so the text never collides with it.

## Forcing a live errorbox for a check

Synthetic typing plus Tab is unreliable for triggering a constraint error in a test. Drive the widget directly instead: `w.setValue('x'); w.setValue(''); w.fireOnChange({})`.

## Bundle

`input.css.dsp` (see "Served CSS lives in the inp bundle").
