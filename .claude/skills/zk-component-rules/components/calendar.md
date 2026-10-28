# calendar

The date-grid widget — used standalone (`<calendar/>`) or as the popup inside `<datebox/>`.

## `.z-calendar-today` is NOT on day cells

ZK only adds `.z-calendar-today` to the **header month/year button**, never to the day cells. CSS rules targeting day cells with that class never match anything:

```css
/* This rule has NO EFFECT — the class is never on .z-calendar-cell */
.z-calendar-cell.z-calendar-today { outline: 2px solid <primary>; }
```

To highlight "today" among day cells, you must use a different mechanism (typically a custom `sclass` injected by the developer, or no highlight at all). This is a framework-level limitation of the current ZK calendar renderer.

## Cell width arithmetic

The calendar's content area has a max-width of ~268px. With 7 columns (Sun–Sat), each cell should be **268 / 7 ≈ 38.28px** to fit without overflow. Choose 38px, or round to a divisor of 7 if you want pixel-perfect grids.

## DOM structure (simplified)

```
.z-calendar
├─ .z-calendar-header           (month/year nav)
│   └─ button.z-calendar-today  (only place .z-calendar-today appears)
├─ .z-calendar-weekdays         (Sun Mon ... Sat row)
└─ .z-calendar-content
    └─ .z-calendar-row * 6
        └─ .z-calendar-cell * 7
```

## State and variant classes

Cell state classes (on `.z-calendar-cell`):
- `.z-calendar-selected` — the selected date
- `.z-calendar-weekend` — Saturday/Sunday columns
- `.z-calendar-weekday` — Mon–Fri columns
- `.z-calendar-outrange` — date outside the min/max range (disabled)
- `.z-calendar-outside` — date from the previous/next month (shown to fill row)
- `.z-calendar-wk` — the week-of-year cell (when `weekOfYear="true"`)

View mode classes (on root, when clicking month/year to navigate):
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
