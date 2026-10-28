# toolbarbutton

A button designed for use inside toolbars. Renders with lighter styling than a regular `<button>`.

## DOM structure

```
.z-toolbarbutton
└─ .z-toolbarbutton-content        (text/icon content wrapper)
```

## State classes

- `.z-toolbarbutton-checked` — toggled/active state (when `mode="toggle"` or `mode="check"`)
- Standard pseudo-classes: `:hover`, `:focus`, `:active`, `[disabled]`

## Critical: CSS file is `footer.css.dsp`

Despite the component name, toolbarbutton styles are delivered by **`footer.css.dsp`** — a historical naming quirk from early ZK versions. Do not look for a `toolbarbutton.css.dsp`; it does not exist.

## Note

Stub entry — full documentation pending spec-author run (Phase 3/4).
See `doc/spec-author-pipeline-plan.md (retired with the template)`.
