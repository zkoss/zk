# combobutton

A button with an attached dropdown arrow. Clicking the button fires `onClick`; clicking the arrow opens a popup.

## DOM structure

```
.z-combobutton
├─ .z-combobutton-button         (the main button — fires onClick)
└─ .z-combobutton-arrow          (the dropdown trigger — opens popup)
```

## Critical: pointer-events must stay on the button element

`.z-combobutton-button` must have `pointer-events: auto` (the default). Setting `pointer-events: none` on it (e.g. as a side-effect of a hover-state-layer pattern that uses an absolutely-positioned `::before` overlay) breaks click routing:

- Clicks on `.z-combobutton-button` get swallowed by the overlay and never reach the button element.
- The onClick handler does not fire.
- Worse, the click may bubble to the parent and trigger the arrow's popup-open handler instead.

When designing hover overlays (the `::before` state-layer pattern), make sure the overlay element has `pointer-events: none`, **not** the button element. Pattern:

```css
.z-combobutton-button { position: relative; pointer-events: auto; }
.z-combobutton-button::before {
    content: '';
    position: absolute;
    inset: 0;
    pointer-events: none;        /* overlay must be transparent to clicks */
    /* hover styles via opacity */
}
```

## State classes

- `.z-combobutton-open` on root when the dropdown is open
- `.z-combobutton-toolbar` on root when placed inside a toolbar

## Icon child

`.z-combobutton-icon` renders inside `.z-combobutton-button` when `iconSclass` is set.

## Bundle

`combobutton.css.dsp`.
