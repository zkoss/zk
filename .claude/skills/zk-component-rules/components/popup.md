# popup

A generic floating container that opens positioned relative to a trigger element.

## DOM structure

```
.z-popup                   (root — position: absolute)
└─ .z-popup-content        (content wrapper — carries box-shadow)
```

## Popup positioning

ZK positions the popup via JS (absolute coordinates). Do not rely on CSS `top`/`left` — they are overwritten at runtime. Style only the visual appearance (background, shadow, border-radius).

See `reference/floating-popup-in-body.md` for the detach-to-`<body>` pattern.

## CSS file

`popup.css.dsp`

## Note

Stub entry — full documentation pending spec-author run (Phase 3/4).
See `doc/spec-author-pipeline-plan.md (retired with the template)`.
