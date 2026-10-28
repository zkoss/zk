# Inline label ↔ field vertical alignment — a container concern

When a stock `<label>` (or any inline widget) sits on the same line as a taller
field (`textbox`, `combobox`, `radiogroup`, …), its vertical position is decided
by the **container's** layout mode, **not** by the label or the field. This is a
ZK/CSS fact — true for every theme — so never try to fix label↔field alignment by
styling `.z-label`.

## Why `.z-label` must stay baseline / untouched

`.z-label` is a generic inline text run: it appears mid-sentence, as a wrapping
multi-line caption, and inside grid/mesh cells. Its default is
`display:inline; vertical-align:baseline`. Forcing `vertical-align:middle` or
flex-centring on `.z-label` globally would corrupt all of those other uses. The
alignment decision belongs to whatever wraps the pair.

## The container matrix (measured at a real viewport, `label-center − field-center`)

| Container | mechanism | textbox | combobox | radiogroup |
|-----------|-----------|:-------:|:--------:|:----------:|
| bare `<div>` (block) | one inline line box; each field placed by its own `vertical-align`/baseline | −1.5 (centred) | 0 (centred) | **+5 (low)** |
| `<hlayout>` (default `valign="top"`) | `.z-hlayout-inner` cells both `vertical-align:top` | −10 | −10 | −10 |
| `<hlayout valign="middle">` | ZK adds `.z-valign-middle`; core CSS → `.z-hlayout-inner { vertical-align:middle }` | 0 | 0 | 0 |

The field type **does** matter in a bare `<div>` (a baseline context), and this is the
subtle part:

- A **single-line input** centres in a bare `<div>`: `textbox` because the theme sets
  `.z-textbox { vertical-align:middle }`; `combobox` because its `inline-flex` baseline
  (from the inner `<input>` text) coincides with its box centre.
- A **caption-bearing control** (`radiogroup`, `checkbox`) does **not**: its baseline is the
  caption text baseline ("Yes"/"No"), which sits ~6px **below** the box centre, so a
  baseline-aligned label drops ~5px low.

`<hlayout valign="middle">` ignores baselines (box-middle alignment) → uniform 0 for every
field type. So switching a row from a bare `<div>` to `valign="middle"` visibly moves **only**
the radio/checkbox case — the inputs were already centred.

> **Measurement caveat:** measure at a real window width. A collapsed 0×0 MCP viewport stacks
> the inline content and reports a bogus uniform ~−30px for the `<div>` (and wraps radios to a
> fake 80px). See memory `reference_screenshot_capture_hang`.

Source: `Hlayout.java:29` / `Hlayout.ts:23` default `valign="top"`; `Hlayout.ts:73`
emits `.z-valign-middle`; the input's `vertical-align:middle` is the theme's
`input.css`. `hbox`/`vbox` expose the same `valign` attribute (`Box.ts`).

## Radio/checkbox really do align differently in a baseline context

Unlike a single-line input, a radio/checkbox's baseline is its **caption text**
baseline, which sits ~6px below the box centre. So in any baseline-driven container
(a bare `<div>`, or default `valign="top"`) the companion label lands lower than it
would beside a textbox/combobox — a real geometric difference (~+5px), not just a
perceptual one. The glyph being centred in a tall box amplifies how "off" it looks.
Don't chase a radio-specific alignment fix; declare the container's valign (middle)
so box-middle alignment replaces the baseline.

## Why a separate `<label>` is even needed

Most ZK inputs (textbox, combobox, datebox, spinner, …) have **no `label`
attribute**, so a field caption has to be an adjacent `<label>` component — which
is exactly why this alignment question arises. Only selection controls (`radio`,
`checkbox`) carry their own `label` attribute, and those captions are part of the
control and centred by the component itself.

## The rule

The fix is **only required for selection controls** (radio / checkbox). A label
beside a `textbox`/`combobox` already centres in a plain block `<div>` — leave it.
For a radio/checkbox row, wrap it in a container that **declares centre
cross-alignment** (verified 0px):

- a flex row — `<div sclass="z-d-flex z-items-center z-gap-3">` (add `z-gap-*`: flex
  collapses inter-element whitespace, so without it the label and field touch), or
- `<hlayout valign="middle">` / `<hbox valign="middle">` (equivalent ZK-attribute form).

Applying the wrapper to an input row is harmless but unnecessary (you may do it
uniformly across a mixed column for consistency). Never rely on the default
`hlayout` top for any label:field pair. See `components/hlayout-vlayout.md` for the
`.z-hlayout-inner` spacing/valign contract.
