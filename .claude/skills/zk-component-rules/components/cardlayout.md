# cardlayout

`Cardlayout` (`<cardlayout>`, `zkmax.layout.Cardlayout`) — an Enterprise (EE) layout that shows one child at a time and slides the cards with JavaScript. Its CSS is registered by the EE lang addon (`css/cardlayout.css.dsp`).

## DOM

```
.z-cardlayout                          (root)
├─ .z-cardlayout-inner                 (one per child; id = `${cardlayout uuid}-${child uuid}-chdex`)
├─ .z-cardlayout-inner
└─ ...
```

## Positioning

`_setChdexPosition` writes an inline `left` (horizontal orientation) or `top` (vertical orientation) on each inner element, in px: `(child index − selected index) × width` (or height). These inline offsets only take effect if each inner element is `position: absolute` inside a root that is `position: relative; overflow: hidden`. Without that, every card renders in normal flow and all cards stack.
