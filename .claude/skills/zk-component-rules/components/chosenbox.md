# chosenbox

Multi-select combobox: shows selected items as chips inside the input, with a dropdown for picking new items.

## Edition: EE only

Lives in `zkmax.jar`. Not available in CE or PE builds. CSS generated for chosenbox in a CE theme will not be exercised. See `reference/edition-availability.md`.

## DOM structure

```
.z-chosenbox
├─ .z-chosenbox-content          (chip container + input)
│   ├─ .z-chosenbox-item         (a selected item chip)  *  N
│   └─ .z-chosenbox-inp          (the search input)
└─ .z-chosenbox-button           (dropdown arrow, optional)
```

## Chip class quirk

Selected items render as chips with class **`.z-chosenbox-item`**, not `.z-chosenbox-tag` or `.z-chosenbox-chip`. Easy to guess wrong.

Each chip has `.z-chosenbox-item-content` inside and an embedded close button. The focused chip gets `.z-chosenbox-item-focus`.

## Dropdown structure

```
.z-chosenbox-select              (the dropdown list)
└─ .z-chosenbox-option           (a list item)
   └─ (highlighted: .z-chosenbox-option-hover)
.z-chosenbox-empty               (shown when no matches)
.z-chosenbox-empty-creatable     (shown when no match but creatable)
.z-chosenbox-popup-hidden        (on popup when closed)
```

## State classes

- `.z-chosenbox-disabled` on root for `disabled="true"`
- `.z-chosenbox-focus` on root when focused

## Creatable mode

`creatable="true"` allows the user to enter a value not in the model and have it appear as a new chip. Set `noResultsText` and `createMessage` to control the "no match — create new" prompt in the dropdown.

## Width handling

Chosenbox renders wide by default because chips wrap. In preview matrices, give it explicit width (e.g. `width="200px"`) and use `colsSuffix="-wide"` to make the matrix column wide enough.

## Bundle

`chosenbox.css.dsp` in zkmax.
