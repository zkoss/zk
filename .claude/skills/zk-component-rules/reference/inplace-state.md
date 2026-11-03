# Inplace state

The `inplace="true"` attribute on input components makes the field look like plain text until the user focuses it. This is **ZK-specific**; any theme that targets ZK must handle it.

## Mechanism (how ZK applies inplace)

ZK's client-side JS toggles a state class on the root element:

- **On page load / blur**: ZK adds `.z-{component}-inplace` to the root → the field should look like plain text
- **On focus**: ZK removes `.z-{component}-inplace` → normal input styling re-applies

Therefore `.z-{component}-inplace` and `:focus` / `:focus-within` **never coexist** on the same element. Do not write `.z-{component}.z-{component}-inplace:focus { ... }` — that rule never matches anything.

## Three CSS patterns

Different components carry their border on different DOM nodes. Pick the pattern matching where the border lives, **not** the visual taxonomy ("combo input vs date input"):

The CSS for each type is in the component's own file under `zul/src/main/resources/web/js/zul/inp/css/` (`datebox.css`, `combobox.css`, `input.css`); search for `-inplace` there.

### Type A — Root-bordered wrapper

Border is on the root `<span>`. The component has an auxiliary button (calendar, dropdown, spinner arrows) that must be hidden in inplace mode.

Applies to: **datebox, timebox, spinner, doublespinner, bandbox**.

Clear `border-color`, `background-color` and `box-shadow` on `.z-{c}.z-{c}-inplace`, and hide `.z-{c}-button`.

### Type B — Input-child-bordered wrapper

The border lives on the inner `.z-{c}-input` element, but the root may also carry `background-color`. Both must be cleared.

Applies to: **combobox**.

Clear `background-color` on the root, clear `border-color`, `background-color` and `box-shadow` on `.z-{c}-input`, and hide `.z-{c}-button`.

### Type C — Direct input elements

The element IS an `<input>` — no inner wrapper. No auxiliary button to hide.

Applies to: **textbox, intbox, decimalbox, doublebox, longbox, passwordbox**.

Clear `border-color`, `background-color` and `box-shadow` on `.z-{c}.z-{c}-inplace`.

## Always-do checklist for inplace CSS

1. Border-color, background-color, box-shadow → `transparent` / `none` on the root inplace class (and inner input for Type B).
2. `:hover` override that re-applies `border-color: transparent` — without this, mousing over flashes a border.
3. `display: none` on the auxiliary button (Types A and B).
4. Place the **Inplace section** in the CSS file **after** Disabled/Readonly and **before** Open-state rules. Order matters for cascade.
5. Read the canonical patterns from ZK's iceBlue theme as the source of truth, not from another component theme. Extract the IceBlue sheet from the tracked jar as described in `../../marble-theme/reference/contract-tiers.md` (section "Locating the iceblue CSS source"). The sheets here are `combo.css.dsp` (combobox, datebox, timebox, spinner, bandbox) and `input.css.dsp` (textbox, intbox, decimalbox, doublebox, longbox, passwordbox).

## Verification notes

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
