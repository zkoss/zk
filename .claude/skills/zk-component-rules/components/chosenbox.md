# chosenbox

Multi-select combobox: shows selected items as chips inside the input, with a dropdown for picking new items.

## Edition: EE only

Lives in `zkmax.jar`. Not available in CE or PE builds. CSS generated for chosenbox in a CE theme will not be exercised. See `reference/edition-availability.md`.

## DOM structure

From `mold/chosenbox.js` and `Chosenbox.ts`; the root element is an `<i>`.

```
.z-chosenbox
├─ .z-chosenbox-item * N              (chips; created by JS, inserted before the input)
│   ├─ .z-chosenbox-item-content
│   └─ .z-chosenbox-button.z-chosenbox-delete   (the chip's close button)
│       └─ i.z-chosenbox-icon.z-icon-times
├─ input#-inp.z-chosenbox-input       (the search input; the id suffix is -inp, the class is -input)
├─ .z-chosenbox-textcontent           (hidden field used to size the input width)
├─ .z-chosenbox-popup.z-chosenbox-shadow[.z-chosenbox-popup-hidden]   (div#-pp)
│   ├─ .z-chosenbox-empty
│   └─ .z-chosenbox-select
└─ span.z-chosenbox-inplace.z-chosenbox-hide
```

## Chip class quirk

Selected items render as chips with class **`.z-chosenbox-item`**, not `.z-chosenbox-tag` or `.z-chosenbox-chip`. Easy to guess wrong.

`.z-chosenbox-button` is the chip's close button, not a dropdown arrow. The focused chip gets `.z-chosenbox-item-focus`.

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
