# separator

A spacer or visual divider, rendered by `<separator/>` or `<space/>`.

## Base class is **transparent** — `.z-separator` alone has no color

`.z-separator` is the base class, applied to any `<separator/>` or `<space/>`. It carries spacing only — no border or background. To make a separator visible, the `bar="true"` attribute (or equivalent) adds a modifier class:

- `.z-separator-horizontal-bar` — visible horizontal divider line
- `.z-separator-vertical-bar` — visible vertical divider line (rare)

```css
.z-separator-horizontal-bar { border-top: 1px solid <outline>; }
```

The bare `.z-separator` is only spacing; don't add color to it directly.

## Vertical separator is spacer-only

`<space/>` (which renders as `<separator orient="vertical"/>`) is **purely spacing**. Adding `background-color` makes a thick coloured block — not what users expect. Keep `.z-separator-vertical` transparent unless explicitly marked as a bar.

## Bundle

Bundled with box/layout output, often in `box.css.dsp`.
