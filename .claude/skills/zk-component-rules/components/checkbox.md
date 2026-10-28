# checkbox

Four molds rendered by the same component: default checkbox, switch, toggle, tristate.

## Four molds

| Mold | Markup | Visual |
|------|--------|--------|
| (default) | `<checkbox label="..."/>` | Square box with checkmark |
| switch | `<checkbox mold="switch" label="..."/>` | iOS-style on/off switch |
| toggle | `<checkbox mold="toggle" label="..."/>` | Binary toggle button (looks like a pressed/unpressed button) |
| tristate | `<checkbox mold="tristate" label="..."/>` | Square box with three states: unchecked / checkmark (checked) / dash (indeterminate) |

The mold adds `.z-checkbox-{mold}` to the root. Default mold gets no mold modifier.

## The mold element is always EMPTY

All three molds share one renderer (`zul/wgt/mold/checkbox.js`): it emits
`<label class="z-checkbox-mold"></label>` with **no content**, and the text label is always a
sibling `<label class="z-checkbox-content">` *outside* the mold. Consequences for any theme:

- A "toggle button" visual can never carry its label inside the button (unlike MUI ToggleButton
  / MD3 toggle button) — the label always sits beside it.
- Any glyph the mold shows (checkmark, switch thumb, toggle icon) must be injected via
  `::before` / `::after` pseudo-elements on `.z-checkbox-mold`.
- Closest Material analog for `mold="toggle"` is therefore a *toggle icon button* whose icon
  the theme supplies, not a labeled toggle button.

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
- `.z-checkbox-on` ✗ (does not exist — checked state for default mold lives on the inner `<input>`'s `checked` attribute)

When writing CSS for switch / toggle / **tristate**, **always include the mold in the state class**.

### tristate is the one mold that has an `-indeterminate` state class

Unlike the default mold (whose `indeterminate="true"` produces the *unprefixed*
`.z-checkbox-indeterminate`), the tristate mold produces the mold-prefixed
`.z-checkbox-tristate-indeterminate` (and `-on` / `-off`). A theme that styles only the
unprefixed default-mold classes will render **every** tristate state as a bare unchecked
box — checked and indeterminate become indistinguishable. Style the tristate `-on`
(checkmark) and `-indeterminate` (dash) classes explicitly, mirroring the default mold.
(Caught 2026-07-14 — see `doc/skill-gaps.md`.)

## Checked state for default mold

The default mold's checked state comes from `:checked` on the inner `<input type="checkbox">`, not from a class:

```css
.z-checkbox input:checked + .z-checkbox-content::before {
    /* checkmark styles */
}
```

(The exact selector depends on the DOM rendered by the version; check `doc/component-dom-structures.md (retired with the template)`.)

For switch and toggle, the on/off state IS class-based: `.z-checkbox-switch-on` / `.z-checkbox-switch-off` etc.

## DOM structure (default mold)

```
.z-checkbox
├─ input.z-checkbox-input[type="checkbox"]   (hidden native input)
├─ .z-checkbox-mold                          (the visual indicator box)
└─ .z-checkbox-content                       (the label text)
```

## Radio is similar but uses on/off classes

`<radio>` always emits `.z-radio-on` / `.z-radio-off` (no inner-input `:checked` reliance), and `<radiogroup>` holds them.

## Bundle

`checkbox.css.dsp` for checkbox; `radio.css.dsp` for radio.
