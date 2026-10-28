# Component: datebox (theme design)
tier: T1
category: input
preview: ${PREVIEW_URL}/datebox.zul
rules: see .claude/skills/zk-component-rules/components/combo-trio.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: /Users/hawk/.../static-css-output/Inputs/OutlinedInput.css
- DESIGN.md sections: §1, §2, §5, §7, §9, §10, §11

## Expected values
Same input metrics as combobox c1–c8 (just selectors → `.z-datebox-*`). Calendar popup styled separately; cross-reference `calendar` contract.

### Input width follows `cols` (ZK-6112)
The date `<input>` always renders a `size` (`Datebox` constructor `setCols(11)`; `textAttrs_()` writes
`size="N"` when `cols > 0`). `field-sizing: content` makes the browser ignore `size`, so an explicit
`cols` had no effect; Marble therefore does **not** use it on datebox (see `DESIGN.md §10`). A format
longer than ~11 characters is clipped by the default `cols` unless the app raises it (core defect: ZK-6178).

| id | selector | property | expected |
|----|----------|----------|----------|
| dw1 | `.z-datebox-input` | field-sizing | **not** `content` (default `fixed`) so `size` drives the width |
| dw2 | `.z-datebox-input` | flex + min-width | `flex: 1 1 auto` (still **grows to fill** an `hflex`/width-forced root) AND a `min-width` date floor (`~6.5em`) keeping a small `cols` a usable target |

**Outcome (M1):** `cols=30` renders **wider** than `cols=8`. Guarded by
`screenshot.spec.ts › datebox › input width follows cols (no field-sizing: content)` and
`B110_ZK_6112_DateTimeboxColsTest`.

### Timezone `<select>` focus — Mechanism A (gap 2026-07-14)
The in-popup timezone `<select>` (`.z-datebox-timezone > select`) is a native, intrinsically-sized
control → it MUST use Mechanism A, not a 2px focus border (which would grow the box on both axes and
shift its text). See `reference/focus-affordance-no-layout-shift.md` (§ `<select>`).

| id | selector | property | expected |
|----|----------|----------|----------|
| tz1 | `.z-datebox-timezone > select:focus` | border-width | **1px** (unchanged from rest — NOT 2px) |
| tz2 | `.z-datebox-timezone > select:focus` | box-shadow | `inset 0 0 0 1px var(--zk-color-primary)` (the ring) |
| tz3 | `.z-datebox-timezone > select` | transition-property | must **NOT** include `border-width` |

Guarded by `screenshot.spec.ts › input focus (no layout shift) › datebox timezone <select>`.

### Open-state (icon-click) affordance — Mechanism A (gap 2026-07-14)
ZK adds `z-datebox-open` to the wrapper `<span>` when the calendar icon is clicked. The open-state
affordance MUST be **identical** to the input-click `:focus-within` affordance (1px border + inset
primary ring) — NOT a `border-width: 2px` bump, which grows the border-box on a min-height-pinned
composite input and reads as a thicker ring / layout shift. See
`reference/focus-affordance-no-layout-shift.md` ("The trap").

| id | selector | property | expected |
|----|----------|----------|----------|
| op1 | `.z-datebox.z-datebox-open` | border-width | **1px** (unchanged from rest — NOT 2px) |
| op2 | `.z-datebox.z-datebox-open` | box-shadow | `inset 0 0 0 1px var(--zk-color-primary)` (same ring as `:focus-within`) |

Guarded by `screenshot.spec.ts › input focus (no layout shift) › datebox open state (icon click)`.

## States to evaluate
- [ ] default, hover, focus, disabled, readonly, invalid, open
- [ ] inplace (see `reference/inplace-state.md`)
- [ ] buttonVisible-false (calendar icon hidden — see `reference/buttonVisible-attribute.md`; verify `.z-datebox-button.z-datebox-disabled` has `display: none`)

## Tablet / mobile wheel picker (touch UA only)
On a mobile UA ZK swaps the grid calendar for a scrolling **wheel picker** and
forces the input `readonly`. Open the popup on tablet to verify (see
`reference/mobile-wheel-picker.md`). Guarded by `tablet.spec.ts`
→ `tablet-datebox-wheel`.
- [ ] `.z-calendar-wheel-list` height ≈ 120px (3 rows; NEVER the full unbounded list) and `li` height = list height ÷ 3 (the `offsetHeight/3` centering invariant)
- [ ] opened popup lands on-screen (bottom sheet, not pushed off-screen by an over-tall wheel)
- [ ] **both tap targets land flush**: tapping the **input** AND tapping the **icon** both open the sheet flush to the viewport bottom (`bottom ≈ innerHeight`, overshoot ≤ 2px). ZK's inline `top` is inflated ~18px by `makeVParent` and only self-corrects on an icon tap — the theme must PIN the sheet (`position:fixed; bottom:0`) so the input tap doesn't overshoot below the fold (clipping OK/Cancel)
- [ ] centred `.z-calendar-wheel-list-selected` row is visible above the `.z-calendar-wheel-line` band (list `z-index` > line)
- [ ] mobile-readonly trigger stays interactive: `.z-datebox-readonly .z-datebox-button` is NOT `pointer-events: none`; field is not greyed like the desktop readonly state
- [ ] footer `.z-calendar-wheel-left` (confirm) = filled primary, `.z-calendar-wheel-right` (cancel) = tonal
