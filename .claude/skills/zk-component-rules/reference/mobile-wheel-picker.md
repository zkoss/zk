# Mobile wheel picker (datebox / timebox on a touch UA)

On a **mobile/tablet User-Agent**, ZK does not render the desktop date/time UI for
datebox and timebox. It swaps in a completely different **iOS-style scrolling
wheel picker**, rendered by `zkmax/touch/datebox-touch.ts` /
`zkmax/touch/timebox-touch.ts`. This is **ZK-specific** — MD3 / MUI / Mira have no
wheel-picker analog, so a theme driven only off those references will never style
it and the picker ships broken. Any theme that targets ZK on touch devices must
handle it.

This is an **EE / zkmax** feature (the touch behaviors live in zkmax) and only
activates when ZK detects `zk.mobile` from the request UA — the same trigger that
enables `zkmax/css/tablet.css.dsp`. So all wheel CSS belongs in the **tablet
bundle**, never in the desktop component CSS.

## Two things change on a touch UA

### 1. Every datebox/timebox input becomes `readonly`
ZK forces `readonly` on the `<input>` (and adds `.z-{c}-readonly` to the **root**)
to suppress the soft keyboard — the wheel popup is the only input path. This is
**not** the developer's `readonly="true"`; ZK applies it to *every* datebox/
timebox. Consequence for theming: the desktop "readonly = inert, greyed,
non-interactive trigger" treatment is **wrong** on mobile — it makes a normal
field look disabled and, if the trigger button gets `pointer-events:none`, the
picker can't be opened. Neutralize the readonly appearance on touch and keep the
trigger interactive (ZK's own canonical theme does this via
`.z-datebox-input[readonly]`). Genuinely-`.z-{c}-disabled` fields keep their inert
look — that's the only "blocked" signal left on mobile.

### 2. The popup is a wheel picker, not a grid calendar / stepper
The popup DOM is `.z-calendar-wheel-*` (datebox) / `.z-timebox-wheel-*` (timebox),
**not** `.z-calendar-cell` or the desktop timebox button. Structure (identical for
both, just a different prefix):

```
.z-{datebox|timebox}-popup
  [.z-calendar]                       (datebox only — wraps the wheel)
    .z-{c}-wheel-cave                 (popup height is driven by THIS node's offsetHeight)
      .z-{c}-wheel-body               (horizontal row of columns; legacy display:box)
        .z-{c}-wheel-date|time        (column group + the centre line)
          .z-{c}-wheel-line           (selection indicator: 0-height el pinned at top:50%)
          .z-{c}-wheel-list           (one scroll column)
            ul > li[data-val]         (each row; ZK adds .z-{c}-wheel-list-selected to the centred li)
          .z-{c}-wheel-list           (… more columns: y/m/d or h/m/s/ampm)
        i.z-timebox                   (datebox datetime formats only — inline time wheel; display:none for date-only)
      .z-{c}-wheel-footer             (the two action buttons)
        button.z-{c}-wheel-button.z-{c}-wheel-left    (confirm / Set — style as primary)
        button.z-{c}-wheel-button.z-{c}-wheel-right   (cancel)
```

## The `offsetHeight / 3` invariant (do NOT break)
`datebox-touch.ts` / `timebox-touch.ts` center the selected value with:

```
slot = wheelList.offsetHeight / 3;     // the list ALWAYS shows exactly 3 rows
scroller.scrollTo(0, -value * slot);   // and scrolls by whole slots
```

So **the `<li>` height must equal `wheel-list` height ÷ 3**, or the selected value
stops aligning with the centre band. The canonical proportion is **120px list /
40px li**. ZK reads `offsetHeight` live (no hardcoded px), so any 3×N pairing
works — but list-height and li-height must stay locked at 3:1.

## The popup box geometry — trust ZK's width/height, but PIN the vertical anchor
The touch popup is a **bottom sheet**. ZK sets these *inline* via JS on open:

```
pp.style.width  = innerWidth - 20;                 // full-width
pp.style.height = cave.offsetHeight + padBorder;   // == the wheel content height
pp.style.top    = innerHeight + scrollY;           // just below the fold …
pp.style.transform = translateY(-height);          // … then slide up into view
$pp.zk.makeVParent();                              // detach popup to <body>
```

**Width and height are reliable — let them stand.** The classic failure mode is a
*height* problem: if `.z-{c}-wheel-list` has **no bounded height**, its `<ul>`
(200+ `<li>`) collapses to its full content height (~5000px). Then
`cave.offsetHeight ≈ 5000`, the sheet slides 5000px up, and the whole picker lands
off-screen (`y ≈ -3949`) → "tapping the datebox produces no usable display". The
fix is purely **bounding the wheel-list height** (the `offsetHeight/3` invariant).

### The vertical `top` is NOT reliable — pin the sheet to the viewport bottom
`pp.style.top = innerHeight + scrollY` is computed correctly, but the
`$pp.zk.makeVParent()` call that immediately follows (it re-parents the popup to
`<body>`, via the iOS `_fixedVParent` override) **inflates the inline `top` by
~18px** — an offset-parent delta. That inflation is only undone if a **second**
`_syncPosition` fires (from `onSize`). Critically, **which element you tap decides
whether the second sync happens**:

- Tap the **icon** (a `<button>`) → a resize/onSize fires → second sync corrects
  `top` (e.g. 1130 → 1112) → sheet lands flush. ✓
- Tap the **input** (a readonly `<input>`) → focusing it suppresses the extra
  resize → no second sync → `top` stays inflated → the sheet sits ~18px **below
  the viewport bottom**, clipping the OK/Cancel row. ✗

(The inflation magnitude varies with the popup's content height — datebox ≈ 18px,
timebox can be 130px+.) Because this is a ZK-JS coordinate quirk a CSS theme
cannot reach, **do not trust ZK's inline `top`**. Pin the sheet to the viewport
bottom yourself:

```css
.z-datebox-popup:has(.z-calendar-wheel-cave),
.z-timebox-popup:has(.z-timebox-wheel-cave) {
    position: fixed !important;   /* beat ZK's inline position:absolute */
    left: 10px !important; right: 10px !important;   /* centre (datebox-touch never sets left) */
    top: auto !important; bottom: 0 !important;       /* immune to makeVParent inflation + scrollY */
    width: auto !important; transform: none !important;
    animation: <slide-up-keyframe>;  /* replace ZK's now-overridden translateY transition */
}
```

`position:fixed; bottom:0` is robust against BOTH the makeVParent inflation and a
scrolled page (`scrollY`), and it centres the sheet. A CSS keyframe restores the
slide-up (overriding `transform` with `!important` disables ZK's translateY).
This is theme-agnostic: any ZK touch theme needs it.

## Stacking-context trap (selection band hides the centred row)
If you fade the column edges with `mask-image` on `.z-{c}-wheel-list`, the mask
**creates a stacking context** that traps the inner `<ul>` below the
`.z-{c}-wheel-line` band (`z-index:1`). The band then paints over the centred row
and it reads as an empty slot. Give the **list** a `z-index` above the line
(`.z-{c}-wheel-list { z-index: 2 }`) so the transparent list (and its text) sits
above the band and the band shows through behind the text.

## Why the harness missed this historically
- No MD3/Mira analog → the evaluator/generator never looked for `.z-*-wheel-*`.
- The tablet datebox test only measured input height + page overflow; it **never
  opened the popup**, so the unstyled multi-thousand-px wheel was never exercised.
  Tablet tests must *open* datebox/timebox popups, not just measure the field.
- When the popup *was* finally opened, the test tapped the **icon** — the one tap
  target that self-corrects via the second sync — so the input-tap overshoot
  survived. **Test BOTH tap targets** (input and icon); they take different code
  paths and only one reveals the mis-anchor.

See: theme CSS `zkmax/css/tablet/_wheel.css`; mobile-readonly neutralization in
`zkmax/css/tablet/_inputs.css`; tests `tablet.spec.ts` describe
`tablet-datebox-wheel` / `tablet-timebox-wheel` (each asserts BOTH icon-tap and
input-tap land flush); gap log `doc/skill-gaps.md` (2026-06-15 + same-day
follow-up). Related unfixed: combobox/selectbox bottom-sheet share the same
makeVParent path and also overshoot — logged for a dedicated pass.
