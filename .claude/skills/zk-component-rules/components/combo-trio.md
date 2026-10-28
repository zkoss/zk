# datebox + timebox + spinner (the combo trio)

These three composite input components share an identical DOM pattern. Anything true for one applies to the others, unless explicitly noted.

## Shared DOM structure

```
.z-{datebox|timebox|spinner}                  (root span — Type A bordered wrapper)
├─ input.z-{c}-input                          (the text field)
└─ a.z-{c}-button                             (calendar / clock / arrow button)
```

`doublespinner` follows the same pattern as `spinner` with a different value type.

## Border location: root

These are **Type A** for inplace. The border lives on the root `<span>`. See `reference/inplace-state.md`.

## `buttonVisible="false"`

Standard pattern: `.z-{c}-disabled` class is added to the button element (`.z-{c}-button.z-{c}-disabled`). See `reference/buttonVisible-attribute.md`.

## Input width: the inner `<input>` ships size-less

**Not every combo-trio `<input>` is size-less.** `InputWidget.textAttrs_()` writes `size="N"` whenever `cols > 0`, and **datebox and timebox always have one**: the `Datebox` constructor calls `setCols(11)`, and `Timebox.renderProperties` derives the default from the formatted value length (5 when empty). Only inputs left at `_cols = 0` (spinner, combobox, bandbox, and the two `<input>`s of daterangebox) render no `size`, so their *intrinsic* width is the browser UA default (`size=20` ≈ 20ch). `ComboWidget.redraw_` itself writes only `class` / `aria-*` / `autocomplete` / `value`; the `size` comes from `textAttrs_()`.

**`field-sizing: content` makes the browser ignore `size`**, so a theme that applies it to datebox/timebox silently disables the `cols` API (ZK-6112). The default datebox `size=11` clips formats longer than ~11 characters (ZK-6178).

Consequence for a theme: a `flex: 1; min-width: 0` on the input (a common pattern) collapses it to a **fixed** width inside the shrink-wrapped root that ignores content — a short value, an empty field, and a long-format value all render the same width, and a value longer than that fixed width is **clipped** (measured: datebox `yyyy/MM/dd HH:mm` needs 132px in a 108px input → truncated). Any theme must therefore pick an explicit input-width policy: either let the input follow its `size` (datebox/timebox — never `field-sizing: content`, which overrides `cols`), **hug its content** where no `size` is rendered (`field-sizing: content` + a `min-width` floor; `flex: 1 1 auto` so it still fills when `hflex`/width-forced), or accept/bound a fixed width. This is structural (true for every theme); *which* components hug is a theme decision — see this theme's `DESIGN.md §10`.

## Focus

Use `:focus-within` on the root. The actual focus target is the inner input. See `reference/focus-vs-focus-within.md`.

## Inplace + no-button combination

datebox / timebox / spinner / doublespinner **all support** the combination `buttonVisible="false"` + `inplace="true"`. Preview pages must show a real component here, not `—`.

## Spinner button positioning

Spinner buttons are rendered to the right of the input. When `buttonVisible="false"`, the up/down arrow buttons are hidden but the input remains editable; the user can still type a value or use keyboard arrow keys.

## Bundle

All four (`datebox`, `timebox`, `spinner`, `doublespinner`) live in `combo.css.dsp` alongside combobox and bandbox. A single CSS edit affects all six components. See `reference/css-file-bundling.md`.

## Data model (for preview pages)

To show a meaningful inplace value, the page needs a sample value:

```xml
<zscript><![CDATA[
java.util.Date sampleDate = new java.util.Date(125, 0, 15);
java.util.Date sampleTime = new java.util.Date(0, 0, 1, 10, 30, 0);
]]></zscript>
<datebox inplace="true" value="${sampleDate}"/>
<timebox inplace="true" value="${sampleTime}"/>
<spinner inplace="true" value="5"/>
<doublespinner inplace="true" value="3.5"/>
```

Without a value, inplace inputs render blank — not useful for visual verification.

## Mobile/tablet: datebox & timebox swap to a wheel picker

On a touch UA, ZK replaces the desktop calendar/stepper with an iOS-style
scrolling **wheel picker** (`.z-calendar-wheel-*` / `.z-timebox-wheel-*`) and
forces the input `readonly`. Different DOM, its own geometry invariants (3-row
scroll columns, JS-driven bottom-sheet positioning), no MD3/Mira analog — any ZK
theme must style it separately in the tablet bundle. See
`reference/mobile-wheel-picker.md`. (spinner keeps its desktop stepper on touch —
only datebox and timebox use the wheel.)
