# Preview page state-matrix convention

Preview pages serve as the visual ground-truth for state coverage. The harness Evaluator drives Chrome against these pages, so consistent layout makes verification scriptable.

## Layout: states are columns, variants are rows

```
                Default    Disabled   Readonly   Invalid    Inplace
   Variant-1       ●          ●          ●          ●         ●
   Variant-2       ●          ●          ●          ●         ●
   Variant-3       ●          ●          ●          ●         ●
```

- **Columns** = states (Default, Disabled, Readonly, Invalid, Inplace).
- **Rows** = variants of the component (With button / No button / Multiline / Placeholder / mold variants).

Do **not** put Inplace as a row. Multiple historical preview pages got this wrong; the convention is column-as-state.

## When a state is not applicable

If a combination is intentionally not exercised (e.g. some component does not support that state), render a single em-dash:

```xml
<div>—</div>
```

But **first** verify the combination really is unsupported. Most "—" cells in legacy pages were defensive guesses; the actual ZK component supports the combination. Examples:

- bandbox + buttonVisible="false" + inplace="true" → **valid** (was wrongly `—`)
- datebox + buttonVisible="false" + inplace="true" → **valid**
- timebox + buttonVisible="false" + inplace="true" → **valid**
- spinner + buttonVisible="false" + inplace="true" → **valid**
- colorbox + inplace → genuinely unsupported (no `inplace` attribute)
- slider + disabled → genuinely unsupported (use `[disabled]` style, no sclass)

Default to showing a real component; reserve `—` for known-unsupported combinations.

## Standard column sets

| Component family | Cols |
|------------------|------|
| Text inputs (textbox, intbox, longbox, decimalbox, doublebox) | Default, Disabled, Readonly, Invalid, Inplace |
| Combo inputs (combobox, bandbox, datebox, timebox, spinner) | Default, Disabled, Readonly, Invalid, Inplace |
| Select (selectbox, searchbox, chosenbox, cascader) | Default, Disabled |
| Slider | Default (only) — slider has no sclass for the other states |
| Rating | Default, Disabled, Readonly |
| Checkbox / Radio | Default, Disabled, Checked, Checked+Disabled (4 cols) |
| Button | Default, Hover, Active, Focused, Disabled (special — captured via screenshots, not static markup) |

## Grid column widths

The shared `pv/matrix.zul` template chooses widths based on the number of columns:

- `pv-cols-1`: 120px label + 200px cell
- `pv-cols-2`: 120px label + 2× 160px cells
- `pv-cols-2-wide`: 150px label + 2× 220px cells (use via `colsSuffix="-wide"`)
- `pv-cols-3`, `pv-cols-4`, `pv-cols-5`: same pattern, 160px (default) or 220px (`-wide`) per cell

If components overflow their cell, pass `colsSuffix="-wide"` to the `<apply>` call. If they still overflow (because they are inline-flex), see `inline-flex-overflow.md`.

## Section structure

Each preview page may have multiple sections. Recommended order:

1. **States** — the main matrix.
2. **Variants** — orient/mold/buttonVisible alternates that aren't on the state axis.
3. **Freeform** — comboitem variants, error-box examples, etc. (no matrix; use `z-d-flex` wrappers).

## Overview pages

The `inputs.zul` overview page references **content partials** (`pv/{component}-content.zul`) for each input component, instead of inlining the matrices. This avoids duplication. See `reference/zul-template-patterns.md` for the partial pattern.
