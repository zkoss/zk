# Inplace state

The `inplace="true"` attribute on input components makes the field look like plain text until the user focuses it. This is **ZK-specific** — there is no MD3 / MUI / Sapphire / Mira equivalent. Any theme that targets ZK must handle it.

## Mechanism (how ZK applies inplace)

ZK's client-side JS toggles a state class on the root element:

- **On page load / blur**: ZK adds `.z-{component}-inplace` to the root → the field should look like plain text
- **On focus**: ZK removes `.z-{component}-inplace` → normal input styling re-applies

Therefore `.z-{component}-inplace` and `:focus` / `:focus-within` **never coexist** on the same element. Do not write `.z-{component}.z-{component}-inplace:focus { ... }` — that rule never matches anything.

## Three CSS patterns

Different components carry their border on different DOM nodes. Pick the pattern matching where the border lives, **not** the visual taxonomy ("combo input vs date input"):

### Type A — Root-bordered wrapper

Border is on the root `<span>`. The component has an auxiliary button (calendar, dropdown, spinner arrows) that must be hidden in inplace mode.

Applies to: **datebox, timebox, spinner, doublespinner, bandbox**.

```css
.z-datebox.z-datebox-inplace {
    border-color: transparent;
    background-color: transparent;
    box-shadow: none;
}
.z-datebox.z-datebox-inplace:hover {
    border-color: transparent;       /* suppress hover border flash */
}
.z-datebox.z-datebox-inplace .z-datebox-button {
    display: none;                   /* hide the calendar icon */
}
```

### Type B — Input-child-bordered wrapper

The border lives on the inner `.z-{c}-input` element, but the root may also carry `background-color`. Both must be cleared.

Applies to: **combobox**.

```css
.z-combobox.z-combobox-inplace {
    background-color: transparent;                 /* root carries bg too */
}
.z-combobox.z-combobox-inplace .z-combobox-input {
    border-color: transparent;
    background-color: transparent;
    box-shadow: none;
}
.z-combobox.z-combobox-inplace:hover .z-combobox-input {
    border-color: transparent;
}
.z-combobox.z-combobox-inplace .z-combobox-button {
    display: none;
}
```

### Type C — Direct input elements

The element IS an `<input>` — no inner wrapper. No auxiliary button to hide.

Applies to: **textbox, intbox, decimalbox, doublebox, longbox, passwordbox**.

```css
.z-textbox.z-textbox-inplace,
.z-intbox.z-intbox-inplace,
.z-decimalbox.z-decimalbox-inplace,
.z-doublebox.z-doublebox-inplace,
.z-longbox.z-longbox-inplace,
.z-passwordbox.z-passwordbox-inplace {
    border-color: transparent;
    background-color: transparent;
    box-shadow: none;
}
.z-textbox.z-textbox-inplace:hover,
/* ...same selector list... */ {
    border-color: transparent;
}
```

## Always-do checklist for inplace CSS

1. Border-color, background-color, box-shadow → `transparent` / `none` on the root inplace class (and inner input for Type B).
2. `:hover` override that re-applies `border-color: transparent` — without this, mousing over flashes a border.
3. `display: none` on the auxiliary button (Types A and B).
4. Place the **Inplace section** in the CSS file **after** Disabled/Readonly and **before** Open-state rules. Order matters for cascade.
5. Read the canonical patterns from ZK's iceBlue theme as the source of truth, not from another component theme:
   - `combo.css.dsp` (combobox, datebox, timebox, spinner, bandbox)
   - `input.css.dsp` (textbox, intbox, decimalbox, doublebox, longbox, passwordbox)
   - Location: `/Users/hawk/Documents/workspace/ZK10/zk/zul/codegen/resources/web/js/zul/inp/css/`

## Evaluator notes

- **Measure in blurred state.** Don't focus the element before reading computed styles — focus removes the inplace class. Navigate, wait for settle, then `getComputedStyle()`.
- **Selector to query**: `.z-{component}.z-{component}-inplace` (root with the inplace class). For Type B, also query the child input.
- **Required passing assertions**:
  - `border-color` resolves to `transparent` or `rgba(0,0,0,0)`
  - `background-color` resolves to `transparent` or `rgba(0,0,0,0)`
  - Auxiliary button (where applicable) has `display: none`

## Preview page conventions

When laying out a preview matrix that includes inplace:

- Inplace is a **column** (alongside Default / Disabled / Readonly / Invalid), not a row.
- For "No button" variant rows, the Inplace + No-button combination is **valid** for bandbox/datebox/timebox/spinner — show a real component, not `—`.

See `reference/preview-state-matrix.md` for the full preview-page convention.
