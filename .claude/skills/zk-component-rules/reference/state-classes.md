# State classes: disabled / readonly / invalid

ZK does **not** use a single convention for state. Each component family chooses one of three mechanisms:

## 1. Attribute-based (rare)

The HTML attribute is the selector target. Use `[disabled]` or `[readonly]` directly.

- **slider** (and `multislider`): No `.z-slider-disabled` class is emitted. Style via `.z-slider[disabled]`.
- **rangeslider**: same.
- Native `<input>` elements also expose the attribute, but ZK usually emits a class too — see #2.

## 2. Class-based with `.z-{component}-{state}` suffix

Most components fall here. ZK adds a state class on the root element.

- `.z-checkbox-disabled` — on the root span of checkbox
- `.z-radio-on` / `.z-radio-off` — for radio's checked/unchecked
- `.z-button-disabled` — on button root

**Do not** assume the HTML `disabled` attribute is also present at the selector level you care about — the attribute may be on the inner `<input>`, not on the root the class is on. Always prefer the class selector.

## 3. Added by ZK to the input node (`-invalid`)

A constraint failure is automatic: `InputWidget._markError` adds `z-{component}-invalid` to the **input node** (`getInputNode()`), and removes it when the value is valid again. Which element that is depends on the widget:

- **Single-input widgets** (textbox, intbox, longbox, decimalbox, doublebox, passwordbox): the `<input>` is the root, so the class sits next to the base class: `.z-intbox.z-intbox-invalid`.
- **Composite widgets** (combobox, bandbox, datebox, timebox, timepicker, spinner, doublespinner): the class lands on the inner `<input>`, `span.z-combobox > input.z-combobox-input.z-combobox-invalid`. A border drawn on the wrapper needs `:has()`: `.z-datebox:has(.z-datebox-invalid)`.
- **daterangebox**: its `domClass_` puts the class on the root `<div>`.

A developer can also write `sclass="z-{component}-invalid"`, which puts the class on the **wrapper** of a composite widget. Preview pages use this to show the state without a constraint. A theme that wants both to work lists both forms: `.z-combobox:is(.z-combobox-invalid, :has(> .z-combobox-invalid))`.

## Checkbox mold-prefixed state classes

The checkbox component has three molds (`checkbox`, `switch`, `toggle`). State classes are **mold-prefixed**, not bare:

- `.z-checkbox-disabled` ✓ (default mold)
- `.z-checkbox-switch-on` ✓
- `.z-checkbox-toggle-off` ✓
- `.z-checkbox-on` ✗ (never emitted — checked state lives on the inner `<input>`'s `checked` attribute)

When styling non-default molds, never assume the bare class works.

## Readonly visual baseline

Most inputs (textbox, intbox, datebox, timebox, spinner, combobox, bandbox) emit `[readonly]` on the inner `<input>`. The theme convention is to tint the background to distinguish from default (otherwise the user cannot tell readonly from editable). This is a **theme decision** — the rule here is only that the readonly attribute is the selector hook.

## Which components support which state

| Component | disabled | readonly | invalid |
|-----------|----------|----------|---------|
| textbox / intbox / longbox / decimalbox / doublebox / passwordbox | ✓ class | ✓ attr | ✓ ZK-added |
| combobox | ✓ class | ✓ attr | ✓ ZK-added |
| bandbox | ✓ class | ✓ attr | ✓ ZK-added |
| datebox / timebox | ✓ class | ✓ attr | ✓ ZK-added |
| spinner / doublespinner | ✓ class | ✓ attr | ✓ ZK-added |
| selectbox | ✓ attr  | — | — |
| searchbox | ✓ attr | — | — |
| chosenbox | ✓ attr  | — | — |
| cascader | ✓ attr  | — | — |
| colorbox | ✓ attr  | — | — |
| slider | ✓ attr | — | — |
| rating | ✓ class | ✓ class | — |
| checkbox (any mold) | ✓ class | — | — |
| radio | ✓ class | — | — |
| button | ✓ class | — | — |

If a state is `—`, the component does **not** support it. Do not add a preview cell or CSS rule for an unsupported combination.
