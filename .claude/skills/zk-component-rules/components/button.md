# button

A standard `<button>` element wrapped by ZK's `.z-button` class. Also rendered by `toolbarbutton`, `combobutton`, and (with different classes) inside `panel` / `window` title bars.

## `orient="vertical"` DOM quirk

When `orient="vertical"` is set, ZK inserts a `<br/>` element between the icon and the label inside the button; the `<br/>` is the only marker:

```html
<button class="z-button">
  <i class="z-icon-..."></i>
  <br/>                          <!-- the only marker that orient is vertical -->
  Label
</button>
```

To style vertical buttons, select the button with `:has(br)` (the only reliable hook) and stack its children in a column.

## State classes

- `disabled="true"` sets the native `[disabled]` attribute on the `<button>` (`Button.ts`, `mold/button.js`); ZK adds no `.z-button-disabled` class.
- Hover / focus / active rely on pseudo-classes (`:hover`, `:focus-visible`, `:active`) on the rendered `<button>` element. The root IS the button, so `:focus` (not `:focus-within`) works.

## Icon support

`iconSclass="z-icon-..."` adds an `<i>` element with that class before the label. The icon and label sit inside the button as siblings.

`image="url"` adds an `<img class="z-button-image">` element before the label instead.

### Graphic-to-label separator is a collapsible plain space

The graphic (`<i class="z-icon-*">` or `<img class="z-button-image">`) and the label are adjacent flex children of `.z-button` separated only by a **plain space text node**:

```html
<button class="z-button"><img class="z-button-image"> Save</button>
```

That lone space **collapses** in an `align-items:center` flex container, so the graphic ends up glued to the label — there is no wrapper element around the label and no built-in margin. To space them, put a `gap` on the flex button container, **not** a `margin` on the graphic:

- `gap` is **order-independent**, so the one rule also spaces `dir="reverse"` — which reorders the DOM to `Save <img>` (a markup reorder, not CSS). A per-element `margin-right` would land on the wrong side under reverse and need `dir`-aware handling.
- Text-only (single text node) and icon-only (single graphic) buttons are unaffected — `gap` needs ≥2 flex items.
- Vertical (`orient="vertical"`) replaces the space with `<br>`; a `:has(br)` rule that sets its own stacked gap wins on specificity.

Same mechanism on **combobutton** (`.z-combobutton-content`: image + label). **toolbarbutton** wraps its icon+label in `.z-toolbarbutton-content`, which carries its own content `gap`.

## Bundle

`button.css.dsp` — one-to-one mapping.

## Related buttons

- **toolbarbutton** — `lang.xml` declares `toolbar.css.dsp`; the rules themselves come from `toolbarbutton.css`, which the build concatenates into `footer.css.dsp`. Inherits much from button but adds `.z-toolbarbutton-*` classes.
- **combobutton** — has its own quirk; see `components/combobutton.md`.
