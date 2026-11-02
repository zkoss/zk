# Floating popups attach to `<body>`, not to the trigger

Many ZK components (combobox, datebox, timebox, bandbox, chosenbox, cascader, menupopup, datebox calendar, etc.) render their popup as a **direct child of `<body>`** at runtime — not nested under the trigger element. This is done so the popup can escape `overflow: hidden` / stacking contexts.

## Verified DOM shape

For chosenbox (also applies to combobox, datebox, etc.):

```html
<body>
  <span class="z-chosenbox">                          ← trigger
    <input class="z-chosenbox-input"/>
    …
  </span>
  …
  <div class="z-chosenbox-popup"                       ← popup, sibling of trigger inside <body>
       id="…-pp"
       style="width: 200px; left: 206px; top: 244px;  ← ZK sets these inline
              visibility: visible; display: block;">
    …
  </div>
</body>
```

`parentElement.tagName === 'BODY'` for the popup element after first open. The trigger and popup are **siblings**, not parent-child.

## Implication: the popup CSS MUST declare `position: absolute`

ZK sets the popup's `left`/`top` (and often `width`) **inline** at open time — but it does **not** set `position`. Positioning offsets only take effect on a positioned box, so the theme CSS is responsible for supplying `position: absolute` (or `fixed`). If the popup rule omits it, the element computes `position: static`, the inline `left`/`top` are inert, and the popup drops into normal `<body>` flow — rendering at the **bottom-left of the page**, after all body content.

```css
.z-{component}-popup {
    position: absolute;   /* REQUIRED — ZK's inline left/top are dead without it */
    /* … surface, shape, elevation … */
}
```

`z-index` is different: ZK writes an inline `z-index` (from `_topZIndex()`) on every floating widget when it shows, so the CSS value is only a fallback for the first paint. See `doc/spec/zindex-scale.md`.

This is a ZK structural fact, not a theme choice: **any** theme that styles a detached popup and forgets `position` hits the identical bottom-left bug. Verify every `*-popup` **base** rule declares `position` — a modifier rule (`.z-x-open .z-x-popup`) inheriting from a positioned base is fine, but a base rule without `position` is the bug.

A `width: max-content !important` workaround can mask a missing `position` by capping the width. Under `position: absolute`, ZK's inline `width: auto` shrink-fits to content, so the workaround is not needed.

## Implication: percentage-based widths reference `<body>`

Because the popup's offset parent is `<body>`, any percentage size in CSS resolves against the body box:

| CSS on popup        | What you intended       | What it actually does                       |
|---------------------|-------------------------|---------------------------------------------|
| `width: 100%`       | "match the trigger"     | match the viewport (body width, ~1900px)    |
| `min-width: 100%`   | "at least trigger wide" | at least viewport wide → popup spans page   |
| `max-width: 100%`   | "no wider than trigger" | no wider than viewport (effectively no cap) |

This is the same trap as `position: absolute` children whose offsetParent is `<body>` — except here the relocation is done by ZK at runtime, not by CSS, so a theme author who only looked at the static markup will not predict it.

## Why this isn't a bug in ZK

ZK already sets the popup's pixel width inline at runtime to match the trigger (e.g. `style="width: 200px"`). The theme does **not** need to provide its own width rule — and if it does, the CSS rule will override the inline style (since inline `style` loses to a CSS rule of the same specificity that uses `!important`, but here even a non-`!important` CSS rule wins over inline if the inline value is *smaller* than a `min-width` constraint — `min-width` always wins over `width`).

## The rule

For any popup that ZK detaches to `<body>`:

1. **Do not write `width`, `min-width`, `max-width` in terms of percentage on the popup root.** ZK sets the absolute pixel width inline.
2. If you need a floor (e.g. "popups should be at least 160px so a short label doesn't collapse the dropdown"), use a fixed pixel value: `min-width: 160px`. Never `min-width: 100%`.
3. If you need a ceiling (e.g. "popups never wider than 480px"), use a fixed pixel value: `max-width: 480px`. Never `max-width: 100%`.
4. `width`/`height` of the popup *content* (rows, items) can use percentages — they resolve against the popup, which has a known pixel size.

## Verification (live DOM, confirmed 2026-05-14)

For chosenbox at `${PREVIEW_URL}/chosenbox.zul`:

- `document.querySelector('.z-chosenbox').getBoundingClientRect().width` → 200
- `document.querySelector('.z-chosenbox-popup').parentElement.tagName` → `"BODY"`
- `document.querySelector('.z-chosenbox-popup').style.width` → `"200px"` (ZK inline)
- Before fix: rendered width = 1904 (because CSS `min-width: 100%` resolves to body)
- After fix (rule removed): rendered width = 200 (matches inline)

## Components known to use this pattern

- **inp**: combobox, bandbox, datebox (calendar popup), timebox (clock popup), chosenbox, cascader popup
- **menu**: menupopup, menubar dropdowns
- **wgt**: popup (the explicit `<popup>` tag), notification
- **wnd**: window (modal/embedded popup positioning)
- **layout**: borderlayout collapsed slideout

When the bundle for any of these components is written, the popup-width expected value must verify rendered width matches the trigger width (or whatever ZK's inline sets), not full-viewport.

## JS-driven drop-direction flip (downward vs upward)

ZK popups don't always extend downward. Most ZK popup widgets contain a JS `_repositionPopup` (or equivalent) that measures viewport space and flips the anchor between `after_*` (below trigger) and `before_*` (above trigger) when bottom space runs out.

Examples:

- **Searchbox** (`Searchbox.ts _repositionPopup`): explicit `vPosition = 'after'` else `'before'` based on `screenY + screenHeight - inpTop - inpHeight > ppHeight`.
- **Combobox** (`Combobox.ts:183`): can call `zk(popup).position(input, 'before_start')` to flip above the input.
- **Datebox, bandbox, chosenbox**: rely on `zk.Widget.position()` default which has similar flip logic.

**This means CSS cannot pin popups to "always drop downward".** The `top: 100%` / `left: 0` you'd normally write is unreliable because ZK inline-sets `top` and `left` on the popup at open time, and those inline styles win.

To force a fixed direction (e.g. always-downward) you must override the widget JS in a theme JS layer — there is no pure-CSS path.

## Hiding a detached popup: scope `display:none` to the ATTACHED state

Most detached popups (combobox, bandbox, datebox, timebox, chosenbox, …) are
hidden by **ZK itself** — the mold/JS writes inline `style="display:none"` and
toggles it. For those, the theme only styles appearance and must NOT add its own
`display:none` (it would fight ZK's inline toggle).

A few popups — notably **searchbox** — are rendered by the mold with **no inline
display style**, so the theme is responsible for the hidden state. When that is
the case, the hide rule MUST be scoped to the popup *while it is still attached
inside the trigger*:

```css
.z-searchbox-popup { display: flex; /* visible by default */ … }

/* Hidden only while attached (initial paint + closed state). */
.z-searchbox .z-searchbox-popup { display: none; }
```

**Why the descendant scope, not a bare class.** ZK's `open()` calls
`makeVParent()`, which re-parents the popup to `<body>`; `close()` calls
`undoVParent()`, which moves it back inside the trigger. So:

| Lifecycle state | Popup parent | `.z-searchbox .z-searchbox-popup` matches? | Result |
|-----------------|--------------|--------------------------------------------|--------|
| initial paint / closed | inside trigger | yes | hidden ✅ |
| open | `<body>` | no | visible ✅ |

**The anti-pattern that breaks it** — a bare hide plus an open-class-scoped show:

```css
/* WRONG */
.z-searchbox-popup { display: none; }
.z-searchbox-open .z-searchbox-popup { display: flex; }
```

The `-open` class lives on the **trigger**, but after `makeVParent()` the popup
is no longer a descendant of the trigger, so the show rule stops matching and the
base `display:none` wins. The detached popup is then `display:none` at the exact
moment `_repositionPopup()` measures its height — height reads `0`, which makes
`slideDown` anchor the reveal **bottom-up** and mis-position the popup well below
the trigger. The broken floating lifecycle also lets outside-clicks fail to close
it. (Caught 2026-06-11 on searchbox; both the upward-reveal and the
won't-close-on-outside-click reports traced to this one rule.)

**Rule:** if the theme owns a detached popup's hidden state, scope the hide to
`.z-{comp} .z-{comp}-popup` (attached), never the bare `.z-{comp}-popup`. Verify
that `getComputedStyle(popup).display !== 'none'` while open and that the open
popup's `top >= trigger.bottom` when there is room below.


## When a widget's close path does not hide the popup

A popup that ZK re-attaches with `undoVParent()` but never hides itself needs the theme to hide it while it is attached inside the widget. Before adding such a rule, read the widget's current close method (`closePopup` / `onHide`) and confirm that it does not write `display:none`. `zkex` `Colorbox.ts` writes `pp.style.display = 'none'` on close (lines 220, 236, 269, 299), so a closed colorbox popup does not keep an inline `display:block`. The older workaround for it (`.z-colorbox > .z-colorbox-popup { display: none !important }`, written when `closePopup` / `onHide` left an inline `display:block` on a mobile UA) is no longer needed; re-verify on a touch UA before removing it.

If such a rule is still needed: it must be scoped to the attached state (`.z-{comp} > .z-{comp}-popup`), because the open popup is detached to `<body>` and the attached-scope selector then never matches it. `!important` is required only when ZK leaves an inline `display:block` that a plain rule cannot beat. Do not use it on a widget that shows its popup in place; the rule would hide it permanently. A desktop-only dismiss test does not catch a mobile-only failure; write the guard in a touch-UA project and tap for real.
