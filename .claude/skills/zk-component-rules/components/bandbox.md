# bandbox

A composite input: text field + dropdown button + bandpopup content area.

## DOM structure

```
.z-bandbox                       (root span — Type A bordered wrapper)
├─ input.z-bandbox-input         (the text field)
├─ a.z-bandbox-button            (the dropdown button)
└─ .z-bandbox-popup              (visual frame for the popup)
   └─ .z-bandpopup               (content wrapper — user-supplied children)
```

## Critical rule: never border `.z-bandpopup`

The visual frame is `.z-bandbox-popup`. The inner `.z-bandpopup` is the **content** wrapper. Adding `border` or `box-shadow` to `.z-bandpopup` produces a double border. Style the frame, not the content.

## `buttonVisible="false"`

Sets `.z-bandbox-disabled` class on the **button element** (`.z-bandbox-button.z-bandbox-disabled`), not on the root. See `reference/buttonVisible-attribute.md`.

## Inplace

Type A pattern (root-bordered). See `reference/inplace-state.md`.

The combination `buttonVisible="false"` + `inplace="true"` is **valid** — show a real component in preview matrices, not `—`.

## State classes

- `.z-bandbox-disabled` on root for `disabled="true"`
- `[readonly]` on the inner `<input>` for `readonly="true"`
- `.z-bandbox-invalid` on the inner `<input>` after a failed constraint (on the root when set through `sclass`)
- `.z-bandbox-inplace` on root when `inplace="true"` and unfocused
- `.z-bandbox-open` on root when the popup is open

## Focus

Use `:focus-within` on the root — focus lands on the inner input, not the root. See `reference/focus-vs-focus-within.md`.

## Bundle

Lives in `combo.css.dsp` alongside combobox, datebox, timebox, spinner. Edits to bandbox styles cascade to the others. See `reference/css-file-bundling.md`.
