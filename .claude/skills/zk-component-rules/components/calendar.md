# calendar

The date-grid widget — used standalone (`<calendar/>`) or as the popup inside `<datebox/>`.

## `.z-calendar-today` is the today-link row, not a day cell

`.z-calendar-today` appears only when `showTodayLink=true`. It sits on the today-link row, the same `div` that carries `.z-calendar-header` (`mold/calendar.js`). It is never on a day cell, so a rule such as `.z-calendar-cell.z-calendar-today` matches nothing.

To highlight "today" among day cells, use a different mechanism (typically a custom `sclass` injected by the developer, or no highlight at all).

## Cell width arithmetic

The 7 columns (Sun–Sat) share the calendar's content width; size the cells by dividing that width by 7 so the grid fits without overflow. Pick a cell width that is a whole divisor if you want pixel-perfect grids.

## DOM structure (simplified; `mold/calendar.js`, `Calendar.ts`)

```
.z-calendar
├─ .z-focus-a
├─ .z-calendar-header                   (month/year nav)
│   ├─ a.z-calendar-left
│   ├─ a.z-calendar-title
│   └─ a.z-calendar-right
├─ table.z-calendar-body                (day view; month/year/decade views add a modifier on this table)
│   ├─ thead > tr > th.z-calendar-weekday | .z-calendar-weekend   (Sun Mon ... Sat)
│   └─ tbody > tr * 6                   (tr has no class)
│       └─ td.z-calendar-cell * 7
└─ .z-calendar-header.z-calendar-today  (only when showTodayLink=true; contains a.z-calendar-title)
```

## State and variant classes

Cell state classes (on `td.z-calendar-cell`):
- `.z-calendar-selected` — the selected date
- `.z-calendar-weekend` — Saturday/Sunday columns
- `.z-calendar-weekday` — Mon–Fri columns
- `.z-calendar-disabled` — date outside the min/max range or otherwise not selectable
- `.z-calendar-outside` — date from the previous/next month (shown to fill row)
- `z-cell-range-begin|-end|-mid|-preview-mid|-preview-end` — range highlight classes that a caller such as Daterangebox adds to day cells (`RANGE_HIGHLIGHT_CLASSES` in `Calendar.ts`)

Root class:
- `.z-calendar-wk` — on the **root** when `weekOfYear="true"` (`domClass_`)

View mode classes (on `table.z-calendar-body`, not on the root):
- `.z-calendar-decade` — decade picker view
- `.z-calendar-month` — month picker view
- `.z-calendar-year` — year picker view

## Day number is a bare text node — circular highlight needs a clip

Each `.z-calendar-cell` is a `<td>` whose day number is a **bare text node** (no inner `<span>` wrapper). With `border-collapse`, a `background-color` on the cell fills the entire column-width × row-height rectangle and touches its neighbours and the grid edge — there is no element to size down to a circle.

Two theme-independent ways to get a centred, inset highlight:

- **`background-clip: content-box` + padding** (simplest, no stacking issues): give the cell uniform padding and `background-clip: content-box`; the hover/selected `background-color` then clips to the content box (cell minus padding), and `border-radius` rounds it into a circle/pill. The padding becomes the gap.
- **`::before` circle**: a positioned pseudo behind the text. Requires the cell to form a stacking context (`position: relative; z-index: 0`) so a `z-index: -1` pseudo stays local; otherwise it paints behind the calendar surface and disappears. Prefer the clip approach unless you need a ring.

Month/year/decade picker cells are wider (pills, not circles): pin them to `background-clip: border-box` so the global content-box clip doesn't shrink them.

## When used in datebox

Datebox's popup is the same calendar widget rendered inside the dropdown. Styling the calendar styles both standalone and datebox-popup usages.

## Mobile/tablet: this grid is replaced by a wheel picker

Everything above describes the **desktop** grid calendar. On a touch UA the
datebox does NOT render `.z-calendar-cell` at all — ZK swaps in an iOS-style
scrolling **wheel picker** (`.z-calendar-wheel-*`) with its own DOM and geometry
rules. A theme that styles only the grid will ship a broken mobile picker. See
`reference/mobile-wheel-picker.md`.

## Bundle

`calendar.css.dsp`.
