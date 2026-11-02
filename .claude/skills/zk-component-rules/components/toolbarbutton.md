# toolbarbutton

A button designed for use inside toolbars. Renders with lighter styling than a regular `<button>`.

## DOM structure

```
.z-toolbarbutton
└─ .z-toolbarbutton-content        (text/icon content wrapper)
```

## State classes

- `.z-toolbarbutton-checked` — toggled/active state (when `mode="toggle"`)
- Standard pseudo-classes: `:hover`, `:focus`, `:active`, `[disabled]`

## Critical: CSS file is `footer.css.dsp`

Despite the component name, toolbarbutton styles are delivered by **`footer.css.dsp`** — a historical naming quirk from early ZK versions. `lang.xml` declares `toolbar.css.dsp`; the rules themselves come from `js/zul/wgt/css/toolbarbutton.css`, which the build concatenates into `footer.css.dsp`.
