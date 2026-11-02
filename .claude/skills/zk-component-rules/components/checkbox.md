# checkbox

Four molds rendered by the same component: default checkbox, switch, toggle, tristate.

## Four molds

| Mold | Markup | Visual |
|------|--------|--------|
| (default) | `<checkbox label="..."/>` | Square box with checkmark |
| switch | `<checkbox mold="switch" label="..."/>` | iOS-style on/off switch |
| toggle | `<checkbox mold="toggle" label="..."/>` | Binary toggle button (looks like a pressed/unpressed button) |
| tristate | `<checkbox mold="tristate" label="..."/>` | Square box with three states: unchecked / checkmark (checked) / dash (indeterminate) |

`Checkbox.ts` `domClass_` always adds `.z-checkbox-{mold}` to the root, so the default mold carries `.z-checkbox-default`.

## The mold element is always EMPTY

All three molds share one renderer (`zul/wgt/mold/checkbox.js`): it emits
`<span id="-mold" class="z-checkbox-mold"></span>` with **no content**, and the text label is always a
sibling `<label class="z-checkbox-content">` *outside* the mold. Consequences for any theme:

- A "toggle button" visual can never carry its label inside the button — the label always sits beside it.
- Any glyph the mold shows (checkmark, switch thumb, toggle icon) must be injected via
  `::before` / `::after` pseudo-elements on `.z-checkbox-mold`.
- `mold="toggle"` is therefore a *toggle icon button* whose icon the theme supplies, not a
  labeled toggle button.

## State classes are **mold-prefixed**

This is a non-intuitive ZK choice. `Checkbox.ts` builds the state class as
`getMoldPrefix_() + state`, where the prefix is `''` for the default mold and `'<mold>-'`
otherwise (`getClassNameByState_()`):

- `.z-checkbox-disabled` ✓ (default mold)
- `.z-checkbox-switch-on` ✓ (switch when checked)
- `.z-checkbox-switch-disabled` ✓
- `.z-checkbox-toggle-off` ✓ (toggle when unchecked)
- `.z-checkbox-toggle-on` ✓
- `.z-checkbox-tristate-off` / `.z-checkbox-tristate-on` / `.z-checkbox-tristate-indeterminate` ✓ (tristate)
- `.z-checkbox-on` / `.z-checkbox-off` ✓ (default mold: the prefix is empty, so the state class is `$s('on')` / `$s('off')`)

When writing CSS for switch / toggle / **tristate**, **always include the mold in the state class**.

### tristate is the one mold that has an `-indeterminate` state class

Unlike the default mold (whose `indeterminate="true"` produces the *unprefixed*
`.z-checkbox-indeterminate`), the tristate mold produces the mold-prefixed
`.z-checkbox-tristate-indeterminate` (and `-on` / `-off`). A theme that styles only the
unprefixed default-mold classes will render **every** tristate state as a bare unchecked
box — checked and indeterminate become indistinguishable. Style the tristate `-on`
(checkmark) and `-indeterminate` (dash) classes explicitly, mirroring the default mold.
(Caught 2026-07-14.)

## Checked state is class-based in every mold

The default mold also carries `.z-checkbox-on` / `.z-checkbox-off` on the root; the mold-prefix rule above is the same for all molds (`.z-checkbox-switch-on`, `.z-checkbox-toggle-off`, and so on).

## DOM structure (default mold)

```
span.z-checkbox
├─ input#<uuid>-real[type="checkbox"]        (hidden native input; no class)
├─ span.z-checkbox-mold                      (the visual indicator box)
└─ label.z-checkbox-content                  (the label text)
```

## Radio is similar but uses on/off classes

`<radio>` always emits `.z-radio-on` / `.z-radio-off` (no inner-input `:checked` reliance), and `<radiogroup>` holds them.

## Bundle

`checkbox.css.dsp` (source `checkbox.css`) carries both checkbox and radio rules.

`<radio>` and `<radiogroup>` use the same source stylesheet as checkbox (`zul/src/main/resources/web/js/zul/wgt/css/checkbox.css`; there is no `radio.css`). When you change a rule there, keep radio-only selectors separate from checkbox ones. The radio visual is the `input[type="radio"]` element itself; there is no `-mold` element (see the comment at `checkbox.css:220`).
