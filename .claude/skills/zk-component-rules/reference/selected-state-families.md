# Selected-state families (MD3)

MD3 does not have a single "selected" colour. It has **four families**, each
with its own visual treatment, depending on what kind of selection the
component represents. Components in the same family MUST share the same
selected-state tokens; components in different families MUST NOT be unified.

This rule was added 2026-05-29 after a cross-component audit found:
- `listitem` used a hardcoded `rgba(55,111,208,0.12)` (token violation)
- `searchbox` used `secondary-container` while `combobox`/`tree`/`menu` used
  `primary-container` for the same kind of "highlighted dropdown row"

**Revised 2026-10-06 (Jess design review batch 2, ruling D10-A).** The list-row
family moved from `primary-container` to `secondary-container`. The 2026-05-29
decision followed MUI, not MD3; MD3's list-item token
`md.comp.list.list-item.selected.container.color` is `secondary-container`
(label `on-secondary-container`; material-web tokens v34, the menu tokens are
the same). The "one family for every row" rule below is unchanged; only the
colour role changed.

See `doc/skill-gaps.md` (2026-05-29 row) for the gap log, the cross-component
audit table and the rationale.

## The three families

| Family | Semantic | Background token | Text/icon token | Extra |
|--------|----------|------------------|-----------------|-------|
| **List-row** ("the row I'm on") | A row that's currently selected in a list or dropdown — typically the cursor target, often arrow-key-navigable. | `--zk-color-secondary-container` | `--zk-color-on-secondary-container` | hover = 8% state layer on top. MD3 `md.comp.list.list-item.selected.container.color` / `label-text.color` |
| **Chip — resting** ("a tag I've added") | An individual chip/tag in a multi-select picker, in its default unfocused state. This theme follows **MUI** (MuiChip-filled), which uses a neutral tonal surface, NOT secondary-container. (Pure MD3 would use `secondary-container` for filter-chip-selected; this theme is MUI-visually-aligned.) | `--zk-color-surface-container-high` | `--zk-color-on-surface` | — |
| **Chip — keyboard-focused** ("the chip I'm about to delete/edit") | A chip that has keyboard focus inside a multi-select picker. Treated as a list-row pick for keyboard navigation: it's "the row I'm on" within the chip strip. | `--zk-color-secondary-container` | `--zk-color-on-secondary-container` | only on `:focus-visible` / equivalent class |
| **Single-point picker** ("the one chosen value") | The unique chosen value in a picker (e.g. the day the user picked in a calendar). Strong visual emphasis because the answer is singular. | `--zk-color-primary` (FILLED shape — solid disc/circle/square) | `--zk-color-on-primary` | typically a fully-rounded fill, not a rectangular row tint |
| **Navigation** ("the destination I'm on") | The persistently-active item in a navigation rail / drawer / sidebar. One item stays highlighted to show the *current location*, independent of keyboard focus — unlike a list-row it is not the arrow-key cursor, it is where you are. | MD3 canonical = a rounded **active-indicator container** (pure MD3 nav drawer fills it with `secondary-container`). **This theme** uses a 12%-tint container: `color-mix(--zk-color-primary 12%, transparent)`. | `--zk-color-primary` | + `font-weight: 600`. **No left-edge accent bar** — the rounded tonal container is the whole marker (MD3 / MUI ListItemButton). |

> **Why three families?** A row in a 200-row listbox should not scream — a
> light container tint is enough. A filter chip needs to differentiate itself
> from "list-row currently focused" so users can see at a glance "this is a
> tag I picked, not the cursor". A single-point pick (calendar day, rating
> star) is the answer to "what did the user choose?" — it deserves full
> visual weight (filled primary).

## Component → family mapping

### List-row family — `secondary-container`
| Component | Selector | CSS file |
|-----------|----------|----------|
| Listbox row | `.z-listitem-selected`, `.z-selected` | `js/zul/sel/css/listbox.css` |
| Combobox dropdown option | `.z-comboitem-selected` | `js/zul/inp/css/combobox.css` |
| Tree row | `.z-treerow-selected` | `js/zul/sel/css/tree.css` |
| Menu item (active) | `.z-menuitem-content:active`, `.z-menuitem-over` | `js/zul/menu/css/menu.css` |
| Searchbox dropdown item | `.z-searchbox-selected` | `js/zkmax/inp/css/searchbox.css` |
| Chosenbox dropdown option (focus) | `.z-chosenbox-item-focus`, `.z-chosenbox-option-focus` | `js/zkmax/inp/css/chosenbox.css` |
| **Selectbox option (native `<select>` popup)** | `.z-selectbox option:checked` | `js/zul/wgt/css/selectbox.css` |

### Chip family — `surface-container-high` (resting) / `secondary-container` (focused)
This theme follows MUI's chip treatment (MuiChip-filled is neutral surface,
not secondary-container). MD3 filter-chip-selected uses `secondary-container`,
but pure-MD3 styling is intentionally not used here for chips.

| Component | Selector | Resting bg | Focused bg | CSS file |
|-----------|----------|------------|------------|----------|
| Chosenbox already-picked chip | `.z-chosenbox-item` | `surface-container-high` | `.z-chosenbox-item-focus` → `secondary-container` | `js/zkmax/inp/css/chosenbox.css` |
| Any future multi-select chip | (TBD) | (TBD) | (TBD) | (TBD) |

### Single-point picker family — filled `primary`
| Component | Selector | CSS file |
|-----------|----------|----------|
| Calendar selected day | `.z-calendar-cell.z-calendar-selected` | `js/zul/db/css/calendar.css` |
| Rating selected star | `.z-rating-button-selected` (TBD) | `js/zul/wgt/css/rating.css` |
| Radio | `.z-radio:checked` | `js/zul/wgt/css/radio.css` |

### Navigation family — tint container + accent bar
| Component | Selector | CSS file |
|-----------|----------|----------|
| Navbar item (active) | `.z-navitem-selected > .z-navitem-content` | `js/zkmax/nav/css/nav.css` |
| Navbar group (open/active) | `.z-nav-selected > .z-nav-content`, `.z-nav-open > .z-nav-content` | `js/zkmax/nav/css/nav.css` |

> **Portable rule vs theme choice.** The *classification* (navigation is its own
> selection family — do not unify it with list-row) is MD3-portable. The active
> marker is the **rounded tonal container alone** (the tint fill + primary text +
> weight 600); the exact tint value is this theme's choice, recorded in
> `doc/spec/DESIGN.md §16`. **No left-edge accent bar** — an earlier iteration
> added one (a `border-left` that curled into an arc on the rounded box, then a
> straight `::after` strip), but a bar stacked on the MD3 pill is redundant and
> clashes at the corners, so it was dropped (design review 2026-07-07). Do not
> re-introduce a left accent. See `doc/skill-gaps.md`.

> **Navigation-active ≠ list-row focus.** A common confusion: the `.z-listitem`
> blue *left line* is not its selected state — the selected state is a
> `secondary-container` **fill** (list-row family, above). The left line is the
> **focus** indicator (`box-shadow: inset 3px 0 0 var(--zk-color-primary)` on the
> first cell), a separate affordance that happens to look like the navbar's
> selected accent. Do not "unify" them; they express different things.

## Required CSS pattern

```css
/* List-row family — example */
.z-{component}-selected {
    background-color: var(--zk-color-secondary-container);
    color: var(--zk-color-on-secondary-container);
}
.z-{component}-selected:hover {
    background-color: color-mix(in srgb,
        var(--zk-color-on-secondary-container) 8%,
        var(--zk-color-secondary-container));
}

/* Chip family — resting chip uses surface-container-high / on-surface */

/* Single-point picker — fill, don't tint */
.z-{component}-selected {
    background-color: var(--zk-color-primary);
    color: var(--zk-color-on-primary);
    /* usually combined with border-radius: 50% or shape-corner-full */
}
```

## Forbidden patterns

1. **Hardcoded RGBA / hex for any selected-state background.** Every colour
   must be a token. Hardcoded values cannot survive a theme switch and bypass
   the contract evaluator. (Example violation: the old
   `rgba(55,111,208,0.12)` in `listbox.css` before 2026-05-29.)

2. **Cross-family unification.** Do not "make all selected states the same
   colour for consistency" — the families exist to express different
   semantics. A reviewer who asks "why is searchbox selected teal but
   listitem blue?" is asking the right question; the answer is family
   classification, not "we forgot to standardise".

3. **Picking a family by intuition rather than by component semantic.** When
   adding a new selectable component, look at this checklist:
   - Is the user navigating a list of options with arrow keys / clicks, one
     of which is "current"? → **list-row family**.
   - Is the selected thing rendered as a free-floating tag/chip separate from
     a list? → **chip family**.
   - Is there exactly one selected value at any time, displayed in-place
     (not as a tag)? → **single-point picker family**.

## Contract assertion template

Add this row to any new contract for a list-row component:

```
| selected | background-color | rgb(<secondary-container-rgb>) |
| selected | color            | rgb(<on-secondary-container-rgb>) |
```

For chip components, resting state uses `surface-container-high` /
`on-surface`; focused state uses `secondary-container` / `on-secondary-container`
(a list-row pick; the resting chip is MUI-aligned, not pure MD3 filter-chip).
For single-point pickers, use `primary` / `on-primary`.

Evaluator should fail loudly if a list-row component's selected state
resolves to `primary-container` — that means someone has reverted to the
pre-2026-10-06 rule (list-row = `primary-container`, which followed MUI, not MD3).
