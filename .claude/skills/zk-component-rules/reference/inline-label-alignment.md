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

## The container matrix

| Container | mechanism | outcome |
|-----------|-----------|---------|
| bare `<div>` (block) | one inline line box; each field placed by its own `vertical-align`/baseline | each field by its own `vertical-align` / baseline: depends on the field and the theme |
| `<hlayout>` (default `valign="top"`) | `.z-hlayout-inner` cells both `vertical-align:top` | every label sits at the top of its cell, above the field centre |
| `<hlayout valign="middle">` | ZK adds `.z-valign-middle`; core CSS → `.z-hlayout-inner { vertical-align:middle }` | label and field centred, for every field type and theme |

The field type **does** matter in a bare `<div>` (a baseline context), and this is the
subtle part:

- A **single-line input** centres in a bare `<div>` when the theme gives the field root
  `vertical-align:middle`. Which roots carry it is theme design: see `doc/spec/DESIGN.md`
  (the label-alignment bullets). A field root left on its baseline is aligned by its first line of text
  (the input text) if it is inline-flex, but by its bottom edge if it is an inline-block whose
  `overflow` is not `visible` — Safari 18 does this for `overflow: clip` too — so such a field rises
  above the text line.
- A **caption-bearing control** (`radiogroup`, `checkbox`) does **not**: its baseline is the
  caption text baseline ("Yes"/"No"), which sits below the box centre, so a
  baseline-aligned label drops low.

`<hlayout valign="middle">` ignores baselines and aligns each box at its middle, so
every field type centres the same way.

> **Measurement caveat:** measure at a real window width. A collapsed zero-size
> viewport stacks the inline content, which gives bogus offsets for the bare `<div>`
> case and wraps radio buttons into artificial rows.

Source: `Hlayout.java:29` / `Hlayout.ts:23` default `valign="top"`; `Hlayout.ts:73`
emits `.z-valign-middle`. `hbox`/`vbox` expose the same `valign` attribute (`Box.ts`).

## Radio/checkbox really do align differently in a baseline context

Unlike a single-line input, a radio/checkbox's baseline is its **caption text**
baseline, which sits below the box centre. So in any baseline-driven container
(a bare `<div>`, or default `valign="top"`) the companion label lands lower than it
would beside a textbox/combobox — a real geometric difference, not just a
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

To centre a label against any field, wrap the label and the field in a container
that **declares centre cross-alignment**. This works for every field type in every
theme:

- a flex row — `<div style="display: flex; align-items: center;">` with a `gap` value set
  (flex collapses inter-element whitespace, so without a gap the label and field touch), or
- `<hlayout valign="middle">` / `<hbox valign="middle">` (equivalent ZK-attribute form).

Whether a theme lets some rows skip the wrapper, because its field roots
centre in a bare `<div>`, is theme design. For Marble see `doc/spec/DESIGN.md`
(the label-alignment bullets). Wrapping a row that a bare `<div>` centres anyway
is harmless, so a column may use one wrapper for every row. Never rely on the
default `hlayout` top for any label:field pair. See `components/hlayout-vlayout.md` for the
`.z-hlayout-inner` spacing/valign contract.
