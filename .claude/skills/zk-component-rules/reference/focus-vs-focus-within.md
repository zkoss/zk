# `:focus` vs `:focus-within`

For composite ZK components (root span + inner `<input>` + auxiliary button), the input that receives the keyboard focus is **inside** the root, not the root itself. The root therefore never matches `:focus` — it only matches `:focus-within`.

## Composite components requiring `:focus-within`

- **combobox**: `.z-combobox:focus-within { ... }`
- **bandbox**: `.z-bandbox:focus-within { ... }`
- **datebox**: `.z-datebox:focus-within { ... }`
- **timebox**: `.z-timebox:focus-within { ... }`
- **spinner / doublespinner**: `.z-spinner:focus-within { ... }`

If you write `.z-datebox:focus`, the rule never fires (focus is on the inner input, not the root).

## Direct-input components use `:focus`

- **textbox, intbox, decimalbox, doublebox, longbox, passwordbox**: the root IS the input, so `.z-textbox:focus { ... }` works.
- Same for `<button>` ZK button (focus lands on the rendered `<button>` element).

## Measurement caveat

Chrome DevTools Protocol cannot reliably trigger `:focus-within` via synthetic events on the root. To verify the rule:

1. Call `.focus()` on the inner input element (find via the `.z-{component}-input` selector).
2. Then read `getComputedStyle()` on the **root** element.
3. Because focus is now inside the root, `:focus-within` matches and the styles apply.

Alternatively, verify the rule exists by reading the stylesheet directly via `document.styleSheets` — sometimes more reliable than triggering the state.

## Anti-pattern

Do NOT write rules like:

```css
.z-datebox-input:focus { border-color: <primary>; }
```

This styles only the inner input element — but the visible border is on the root span (Type A from inplace-state.md), so the focus indicator is not visible. Always raise the focus styles to the root via `:focus-within`.

## A focus affordance that changes `border-width` resizes composite inputs

This is a CSS box-model fact that follows from the DOM above, so it holds for every theme.

With `box-sizing: border-box`, raising `border-width` (for example 1px to 2px) on focus grows the border inward and shrinks the content box. What that does depends on the DOM:

- **Single `<input>` (textbox, intbox, …)**: the box's `width: auto` grows horizontally. Shrink the padding by the same amount on each compensated axis to absorb it.
- **Composite input** (a root wrapping an `<input>` child and a button child): the children are pinned at a `min-height` equal to the content box at rest. When the border shrinks the content box, the children cannot shrink with it, so they force the root taller. Padding compensation on the root does not help, because the growth comes from the children.

Keep the border width constant in every state and draw the extra pixel of the ring as an inset `box-shadow`. A box-shadow is not a layout box, so nothing shifts. Use `inset`: an outset ring reaches into neighbour space and is clipped by any ancestor with `overflow: hidden`.

- An inset ring on the root paints below the children. A child with an opaque background hides the ring on its side. Draw the ring on an always-present `::after` overlay (`position: absolute; inset: 0; border-radius: inherit; pointer-events: none`) on a root with `position: relative; overflow: hidden`, so it paints above the children. A widget whose payload is a third-party editor or viewer mounted into a cave (codeeditor, tbeditor, pdfviewer) has opaque children by construction.
- `getComputedStyle(root).boxShadow` reports the same value whether the ring is visible or hidden under a child. To check visibility, take a screenshot of the element and count ring-coloured pixels inward from the middle of each edge.
- To read the computed `box-shadow` of a pseudo-element, disable its transition with a rule on the pseudo-element. `el.style.transition = 'none'` does not reach `::after`.
- A native `<select>` is intrinsically sized, so a thicker focus border grows the box on both axes. Use the inset `box-shadow` there too.
- A group of independently styled inputs (inputgroup) must stop each child's own focus `border-width` and `padding` change from applying inside the group. The container owns the focus ring.
- A JS-added `-open` class (`z-datebox-open`, `z-bandbox-open`, `z-combobox-open`) is an emphasis state like `:focus`. Use the same mechanism for it, so clicking the icon and clicking the input look the same.

**Never transition `border-width`.** If padding compensates a border change, padding snaps while `border-width` eases, so the content edge is off by up to 1px mid-animation. Animate only non-layout properties (`border-color`, `box-shadow`), and let `border-width` and `padding` change together without a transition. A check of the settled state does not catch this. Also assert that `transition-property` does not include `border-width`.
