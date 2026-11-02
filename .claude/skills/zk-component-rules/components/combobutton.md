# combobutton

A button with an attached dropdown arrow. Clicking the main area fires `onClick`; clicking the arrow area (`-btn` / `-icon`) opens a popup.

## DOM structure

From `mold/combobutton.js` and `Combobutton.ts`:

```
.z-combobutton
└─ span#-real.z-combobutton-content     (main area: image, icon, label text — fires onClick)
   ├─ img.z-combobutton-image           (when image is set)
   ├─ span#-txt.z-combobutton-text      (label)
   └─ span#-btn.z-combobutton-button    (arrow trigger area — opens the popup)
      └─ i#-icon.z-combobutton-icon.z-icon-caret-down
```

The arrow trigger is `span#-btn.z-combobutton-button`. `Combobutton.doClick_` opens or closes the popup when the click target is `-btn` or `-icon` (or when the popup is already open); a click anywhere else in the widget fires `onClick`.

## Pointer-events: the clickable elements must keep them

`.z-combobutton-button` and `.z-combobutton-content` must keep `pointer-events: auto` (the default). Setting `pointer-events: none` on either (for example as a side effect of a hover state-layer built from an absolutely-positioned `::before` overlay) breaks click routing: clicks are swallowed by the overlay and never reach the element, so `onClick` or the popup-open handler does not fire.

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

`.z-combobutton-icon` is the caret `<i>` inside `.z-combobutton-button`; the mold writes `z-icon-caret-down` on it.

## Bundle

`combobutton.css.dsp`.
