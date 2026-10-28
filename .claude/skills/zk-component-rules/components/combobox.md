# combobox

Like bandbox, but the dropdown contains structured `<comboitem>` children rather than freeform content.

## DOM structure

```
.z-combobox                       (root span — Type B: border lives on the child input)
├─ input.z-combobox-input         (the text field with border)
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
theme; only `<gap>` is a theme choice (DESIGN.md).

## Border location: child input, not root

Combobox is the **Type B** inplace pattern: the border lives on `.z-combobox-input`, not on the root. The root may still carry `background-color`. When clearing styles for inplace, clear both root background AND child border/background. See `reference/inplace-state.md`.

## Split-border with the button

Visually the input and button look like one rectangle. CSS implements this by giving:

- `.z-combobox-input`: left + top + bottom borders (and left half of radius)
- `.z-combobox-button`: right border (and right half of radius)

This avoids the double-border that would result from bordering each child fully. When the focus state changes the border, both elements must be updated together.

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
- `.z-combobox-invalid` on the inner `<input>` after a failed constraint (on the root when set through `sclass`)
- `.z-combobox-inplace` on root when inplace + unfocused
- `.z-combobox-open` on root when popup is open

## Bundle

`combo.css.dsp`. See `reference/css-file-bundling.md`.
