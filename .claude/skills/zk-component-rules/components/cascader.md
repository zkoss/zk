# cascader

A hierarchical dropdown picker that presents a TreeModel as a sequence of side-by-side column panels (caves). The user navigates level by level; selection is finalised only when a leaf node is chosen. The widget renders as a read-only trigger (not a text input) that shows the selected path as a slash-joined label, or a placeholder when no item is selected.

## Edition: EE only (zkmax.jar)

Lives in `org.zkoss.zkmax.zul.Cascader`. Not available in CE or PE. See `reference/edition-availability.md`.

## Trigger is a read-only display area

The visible widget body is a **read-only `<div>` display area**; users cannot type into it. The text shown is `.z-cascader-label` (selected path) or `.z-cascader-placeholder` (empty state), both `<div>` elements. Write the trigger CSS for `<div>` children only. The widget states are disabled, open and focus.

## DOM structure (from mold/cascader.js + Cascader.ts `_redrawpp`)

```
.z-cascader[.z-cascader-disabled][.z-cascader-open][.z-cascader-focus]   ← root <div>, tabindex
├─ .z-cascader-label        <div>  ← selected path text; hidden (display:none) when no selection
├─ .z-cascader-placeholder  <div>  ← shown (display:inline-block) when no selection; hidden otherwise
├─ .z-cascader-icon         <i>   ← caret / times icon (class swapped by JS between z-icon-caret-down, z-icon-caret-right, z-icon-times)
└─ .z-cascader-popup .z-cascader-shadow   <div id="…-pp">
    └─ .z-cascader-cave+    <ul>  (one per visible tree level; rendered side by side)
        └─ .z-cascader-item[.z-cascader-selected][.z-cascader-active]   <li> (one per node at this level)
            ├─ text content (raw HTML via zUtl.encodeXML(item.content))
            └─ .z-cascader-icon  <i class="z-icon-caret-right">   ← only on non-leaf nodes
```

### Item text is a bare text node — NO wrapper element

`_renderItems0` writes `out.push(zUtl.encodeXML(item.content))` directly between the `<li>` open tag and the optional icon `<i>`. There is **no `<label>`, `<span>`, or any element** around the item text. Consequences for theme CSS:

- Selectors like `.z-cascader-item > label` or `.z-cascader-item > span` match **nothing** — any `flex: 1` / `text-overflow` rule placed on them is dead code.
- In a flex item row, the text becomes an anonymous flex item that cannot be targeted. To position the expand icon independently of label length (e.g. anchored at the trailing edge), the rule must live **on the icon itself** — `margin-left: auto` is the working mechanism.
- Per-item text truncation (`text-overflow: ellipsis`) is not achievable via CSS alone for this widget — there is no element to clip.

### Notes on the label/placeholder toggle

`Cascader.ts` `bind_()` and `setSelectedUuids()` use direct `jq(...).css('display', ...)` to toggle `.z-cascader-label` and `.z-cascader-placeholder`. The inline style **overrides** any CSS `display` rule on those elements. Authors may style their appearance (color, font, padding) but must not rely on CSS-only `display` toggling for these two elements.

**Initial-render display gap (pre-selected model)**: `mold/cascader.js` keys both inline styles off `this._placeholderVisible`, but `Cascader.java` **never renders `placeholderVisible`** (its sibling `Searchbox.java` does — line ~612), so the flag is always `undefined` at redraw and BOTH divs are emitted with empty `style` — both visible. `bind_()` then patches only the **empty-selection** branch (`_selectedUuids.length == 0` → hide label, show placeholder); when a selection is **pre-set on the model**, the empty placeholder div stays visible alongside the label until the first interactive `setSelectedUuids`/`setLabel` writes inline styles. Consequences for theme CSS:

- Any rule granting the placeholder horizontal space (`flex: 1`, a width, large padding) makes the invisible-but-rendered placeholder squeeze the label at initial load — the classic symptom is a pre-selected path showing `text-overflow: ellipsis` that "fixes itself" after one interactive selection. A content-sized `inline-block` label/placeholder avoids it, because an empty placeholder takes ~0px.
- A stylesheet-level guard is safe and sufficient: `.z-cascader:has(.z-cascader-label:not(:empty)) .z-cascader-placeholder { display: none }`. It only acts when no inline `display` exists (= initial render); all interactive flows write inline styles, which win the cascade.
- The same gap means `placeholder="…"` text + pre-selected model shows **both texts side by side** at initial load in any theme that doesn't guard — this is the upstream rendering gap, not a theme bug.
- **Sibling — searchbox:** `searchbox` has the identical label/placeholder-`<div>` swap. The difference: `Searchbox.java` **does** render `placeholderVisible`, so its mold emits the inline hint in the no-selection state and leaves both empty-inline only when selected — meaning a plain base `.z-searchbox-placeholder { display:none }` (mirroring stock ZK less) is sufficient there, no `:has()` needed. Cascader needs the `:has()` form precisely because `Cascader.java` never emits the hint. See `components/searchbox.md` ("Label vs placeholder visibility is JS-toggled…").
### Popup detachment

`.z-cascader-popup` follows the standard ZK floating-popup rule. When `open()` is called, `zkpp.makeVParent()` re-parents the popup to `<body>`, and `pp.style.minWidth` is set to `node.offsetWidth` (trigger width) inline. See `reference/floating-popup-in-body.md`:

- Do **not** set `width: 100%`, `max-width: 100%`, or `min-width: 100%` on `.z-cascader-popup` — those resolve against `<body>`.
- When closed, `.z-cascader .z-cascader-popup { display: none }` hides it (scoped selector in ZK CSS; this rule must be preserved in theme CSS).

## State classes

Root `.z-cascader`:

| State | Class / mechanism |
|-------|-------------------|
| disabled | `.z-cascader-disabled` class (toggled by `setDisabled` via `toggleClass`). Also sets `[disabled]` attribute and `tabindex="-1"`. |
| open (dropdown visible) | `.z-cascader-open` (added in `open()`, removed in `close()`) |
| focused | `.z-cascader-focus` (added in `_focusinContent`, removed in `_focusoutContent`) |

Popup item `.z-cascader-item`:

| State | Class |
|-------|-------|
| selected (part of current selection path) | `.z-cascader-selected` — set during `_doSelectItem` / `_renderItems0` |
| keyboard-highlighted | `.z-cascader-active` — moved by `_shiftSelection`; removed on `close()` |

The trigger states are only disabled, open and focus.

## Icon class swaps

The trigger icon `.z-cascader-icon` is a single `<i>` element. JS (`_updateIconClass`) swaps between three font-icon classes:

| Trigger state | Icon class |
|--------------|------------|
| closed, no selection | `z-icon-caret-down` |
| open, no selection | `z-icon-caret-right` |
| has selection (any state) | `z-icon-times` (clear button — interactive; `pointer-events: auto`) |

The popup-item expand icon also uses `.z-cascader-icon` but is scoped as `.z-cascader-item .z-cascader-icon` — always `z-icon-caret-right`, static.

## Attribute support

- `disabled="true"` → `.z-cascader-disabled` on root + `[disabled]` attribute + `tabindex="-1"`
- `placeholder="…"` → text in `.z-cascader-placeholder`
- `open="true"` → triggers `open()` which adds `.z-cascader-open` and detaches popup to body

## Composition invariants

- **Popup min-width equals trigger width**: JS sets `pp.style.minWidth = node.offsetWidth + 'px'` — popup is never narrower than trigger.
- **Caves render side by side**: each `.z-cascader-cave` is `display: inline-block; vertical-align: top`. The popup grows horizontally as the user navigates deeper levels.
- **First cave has no left border**: `.z-cascader-cave:first-child { border-left: none }` — this rule separates column-separator borders from the leftmost column.
- **Popup is hidden in closed state via scoped rule**: `.z-cascader .z-cascader-popup { display: none }` — theme must preserve this rule (or an equivalent) to avoid the popup flashing before `open()` is called.
- **Non-leaf items render an expand icon**: `_renderItems0` adds `<i class="z-cascader-icon z-icon-caret-right">` only when `itemsIdMap[uuid].childrenId.length > 0`.

## Sibling decomposition

- **Trigger chrome (border, radius, height, label/placeholder layout)**: mirrors `searchbox` — same read-only trigger + popup pattern; see `components/searchbox.md`.
- **Popup panel + item list**: structural parallel to `searchbox` popup cave + items.

## Bundle

`cascader.css.dsp` — dedicated file in `zkmax/inp/css/`. Not shared with searchbox (which has its own `searchbox.css.dsp`). The project theme file is `../zkcml/zkmax/src/main/resources/web/js/zkmax/inp/css/cascader.css`.

## Keyboard model

The keydown handler is in `Cascader.ts`. What matters for a theme: Arrow Up/Down move `.z-cascader-active` inside the current column, Arrow Left closes the rightmost cave and Arrow Right opens the children of the active item.

## State-differs invariants

- **selected vs unselected items**: `.z-cascader-selected` items must differ from siblings in at least one of `{color, background-color, font-weight}`.
- **active (keyboard-highlighted) vs resting items**: `.z-cascader-active` must differ from resting in at least one of `{background-color, border-color, outline}`.
- **open vs closed trigger**: `.z-cascader-open` state must produce a visually distinct trigger from the default resting state — at minimum a border-color or box-shadow change.
- **disabled vs enabled trigger**: `.z-cascader-disabled` must produce `opacity < 1` OR visually suppressed color on the trigger content.
- **focus vs unfocused trigger**: `.z-cascader-focus` must produce a visible focus indicator — border-color change OR outline OR box-shadow ring.
- **icon-times (clear) vs icon-caret-down**: trigger icon must be visually distinct between clear-action state and open-caret state; both are rendered using font-icon classes on the same `<i>` element.

## Relational invariants

- When popup is open: popup bbox.top is either immediately below or immediately above the trigger (no gap > 4px, no overlap > 4px with trigger body).
- Caves inside popup are horizontally adjacent: `|cave[n].bbox.right − cave[n+1].bbox.left| ≤ 2px`.
- Popup min-width ≥ trigger width: `popup.getBoundingClientRect().width ≥ trigger.getBoundingClientRect().width − 2`.
