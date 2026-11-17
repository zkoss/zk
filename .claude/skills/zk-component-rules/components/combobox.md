# combobox

Like bandbox, but the dropdown contains structured `<comboitem>` children rather than freeform content.

## DOM structure

```
.z-combobox                       (root span — Marble draws the whole outline here)
├─ input.z-combobox-input         (the text field, no border)
└─ a.z-combobox-button            (the dropdown arrow)
```

Popup (rendered in `<body>`, not inside `.z-combobox`):

```
.z-combobox-popup
└─ ul.z-combobox-content
   └─ li.z-comboitem                  (display:flex, align-items:center)
      ├─ span.z-comboitem-icon        (only when iconSclass set)
      ├─ span.z-comboitem-image       (when image set; ALSO emitted EMPTY otherwise — see below)
      └─ span.z-comboitem-text        (label; description goes inside as `<br/>` + span.z-comboitem-inner)
      (selected item gets .z-comboitem-selected on the li)
```

### Leading icon/image spacing — never use container `gap`

The `<li>` is a flex row; the leading element (`.z-comboitem-icon` or
`.z-comboitem-image`) and `.z-comboitem-text` are adjacent flex children with
**no built-in separation** — without a theme rule the glyph touches the label.

The trap: the mold (`comboitem.js`) emits an **empty** `.z-comboitem-image`
span for a *plain* item too ("if no image specified, we still output the image
for backward compatibility"). So a flex `gap` on `.z-comboitem` would turn that
zero-width span into a **phantom indent** on every plain item. Space the leading
element with `margin-right` on it, not container `gap` — and guard the image
with `:not(:empty)` so the always-present empty span stays flush:

```css
.z-comboitem-icon,
.z-comboitem-image:not(:empty) { margin-right: <gap>; flex-shrink: 0; }
```

The icon span is only rendered when `iconSclass` is set, so it always has
content (no `:not(:empty)` guard needed). This whole structure holds for any
theme; only `<gap>` is a theme choice.

## Border location: root, not child input

Marble draws the combobox outline on the root: the root carries the 1px border and its radius, and focus, open and invalid are an inset `box-shadow` on the same root. The input and the button draw no border. A split border (the input and the button each carry half) would need a `clip-path` on the input, which Chrome applies at a sub-pixel position and so cuts a border row. For the inplace styles of this root, see `reference/inplace-state.md`.

## Input and button are two elements

The input and the button are separate elements, but the outline belongs to the root. A state that changes the outline (focus, open, invalid) changes the root only; the input and the button need no matching rule.

## `buttonVisible="false"`

`.z-combobox-disabled` on the button element. Same pattern as bandbox.

## Comboitem variants

Comboitems can render with:

- **Plain**: `<comboitem label="Item 1"/>`
- **Description**: `<comboitem label="..." description="..."/>` → renders the description on a second line
- **Icon**: `<comboitem label="..." iconSclass="z-icon-..."/>` → renders an icon before the label

The icon class is whatever the developer sets in `iconSclass` (any class is allowed, conventionally a `z-icon-*` from ZK's icon set).

## State classes

- `.z-combobox-disabled` on root
- `[readonly]` on the inner input
- `.z-combobox-invalid` on the inner `<input>` after a failed constraint
- `.z-combobox-inplace` on root when inplace + unfocused
- `.z-combobox-open` on root when popup is open

## Bundle

`combo.css.dsp`. See `reference/css-file-bundling.md`.
