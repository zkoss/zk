# coachmark

A guided-tour overlay component that positions an annotated card relative to a target element,
highlights that target by raising its z-index above a full-page mask, and provides a close
button plus optional child content (label, action button).

## DOM structure

```
.z-coachmark                          (<div> root — absolute-positioned wrapper; transparent bg; no shadow)
├─ .z-coachmark-pointer               (<div> arrow/pointer triangle — border-trick CSS triangle; aria-hidden)
├─ .z-coachmark-content               (<div id="uuid-cave"> visual card — carries bg, radius, shadow, padding)
│   ├─ [child ZK widgets]             (arbitrary children: .z-label, .z-button, etc. — slotted by the ZUL author)
│   └─ ...
└─ .z-coachmark-close                 (<div id="uuid-cls"> close button — role="button", tabindex="0")
    └─ <i class="z-icon-times ...">   (close icon — rendered as a <i> with z-icon-times + internal icon class)
```

Notes:
- `.z-coachmark` itself is a positional shell — `position: absolute`, initially `opacity: 0; visibility: hidden`.
- Visual card appearance lives on `.z-coachmark-content`, not the root.
- A `.z-coachmark-mask` element is injected separately (via `zk.eff.FullMask`) as a sibling under
  `<body>` at runtime when the coachmark opens. It is NOT a child of `.z-coachmark`.
- Close button sub-element `<i id="uuid-clsIcon">` carries both an internal icon class and
  `z-icon-times`. Clicks on either `uuid-cls` or `uuid-clsIcon` trigger close.

## State classes

- `.z-coachmark-open` — added to `.z-coachmark` root when the coachmark is open (via `_open()`).
  The animation runs here. Removed on close via `_close()`.

The pointer direction classes are added to `.z-coachmark-pointer` (not root):
- `.z-coachmark-up`    — pointer points upward (coachmark is below the target)
- `.z-coachmark-down`  — pointer points downward (coachmark is above the target)
- `.z-coachmark-left`  — pointer points left (coachmark is to the right of the target)
- `.z-coachmark-right` — pointer points right (coachmark is to the left of the target)

No disabled or readonly states. No focus/active state classes — these use only pseudo-classes
on `.z-coachmark-close`.

## Attribute support

- `visible="false"` — coachmark starts closed; open programmatically with `self.open()` or
  `setVisible(true)`. When visible goes to `true`, ZK calls `_open()` which adds `.z-coachmark-open`.
- `target="id"` — JS raises the target element's `z-index` above the mask when open; restores it on close.
- `position="before_center|after_center|..."` — controls which side of the target the coachmark appears on.
  JS-resolves to a pointer direction and injects `paddingTop/Bottom/Left/Right` on `.z-coachmark` root to
  make room for the pointer triangle.
- `next="id"` — triggers the next coachmark in a guided sequence.

## Composition invariants

- `.z-coachmark` root has `position: absolute` and JS-set `top`/`left`. Do NOT override these with CSS.
- `.z-coachmark-pointer` is sized/positioned entirely by JS (`_fixarrow()`). The theme controls only its
  border-color (which sets the pointer triangle fill). The border-width is JS-hardcoded at 10px.
- `.z-coachmark-content` should keep `position: relative` (matches the ZK LESS source). Height is `100%` (inherits from root sizing).
- `.z-coachmark-close` is a **sibling of the content**, a direct child of the `.z-coachmark` root, and is
  `position: absolute` resolved against the **root** (NOT the content box). The theme must pin it to the
  top-right corner (`top`/`right`); a missing `position:absolute` flows it as a full-width row below the
  content.
- **Pinning the close evenly requires compensating for the root padding ZK injects on the pointer side.**
  Because the close is positioned against the *root* but must sit at a consistent inset inside the *content*
  box, and the root grows padding on whichever side the pointer occupies, a plain `top/right: 8px` lands the
  close at an *asymmetric* inset on pointer-up / pointer-right cards. The injected padding is a **fixed 20px**
  on the pointer side: `_fixPadding()` in `Coachmark.ts` sets `ph = pw = 10 + borderWidth/2`, and the pointer
  border is hardcoded at 10px → `10 + 10 = 20px`. So the compensation must add **exactly 20px** (not a guessed
  `+16px`) via the general-sibling selectors `.z-coachmark-up ~ .z-coachmark-close { top: calc(8px + 20px) }`
  and `.z-coachmark-right ~ .z-coachmark-close { right: calc(8px + 20px) }` (general-sibling because the pointer
  precedes the close in DOM order). Pointer-down / pointer-left inject padding on the bottom/left, which does
  not affect the top-right corner, so those need no bump. This yields a symmetric 8px inset in all four
  directions. (ZK's own default uses a `+16px` bump, which is 4px short of the padding it injects — do not copy it.)
- The close is **keyboard-focusable** (`tabindex=0`, `role=button`) so it needs a visible `:focus-visible`
  indicator, and it should be a real **icon-button affordance** (a circular hit target with a hover/focus
  state layer), not a bare glyph — reuse the theme's icon-button convention rather than styling the raw `×`.
  On a *neutral* card the state layer must be a semi-transparent **on-surface overlay** (`::before`), because
  an opaque container swap (e.g. `surface-container`) is invisible against a card that is already a
  surface-container tone.
- `.z-coachmark-pointer` MUST be `position: absolute` (the mold JS `_fixarrow()` writes inline `top`/`left`
  to align the triangle with the target; `position:static` discards those coordinates and the triangle
  collapses to the card's left edge, no longer pointing at the target). `z-index: 100`.
- **Prefer a neutral card so nested controls need NO inversion (default).** A coachmark is an MD3
  *rich tooltip* — an elevated card carrying text + action widgets. Fill `.z-coachmark-content` with a
  **neutral** surface (e.g. `--zk-color-surface-container-low` / `on-surface`); prominence above the mask
  comes from the elevation shadow on the dark scrim, not a brand fill. On a neutral card, every nested
  filled control (`.z-button`, `.z-combobutton`, …) renders with its STANDARD styling and contrasts
  automatically — the theme writes **no** per-widget rule, and any future slotted control just works.
  This is the recommended default because a coachmark hosts *arbitrary* widgets, so per-widget special-casing
  does not scale.
- **IF (and only if) the card is filled with a brand/saturated surface, every child filled control must be
  inverted.** This is the fallback for a deliberately colored card. Any child filled control inherits the
  *global* filled style — and the default `.z-button` is itself `--zk-color-primary` on `--zk-color-on-primary`
  with `border:none`. Same-fill-on-same-fill makes the button vanish (only its label floats; no shape, no
  affordance). The same holds for **every** filled action widget, not just `.z-button`: a `.z-combobutton` is
  primary-on-primary on BOTH halves (`.z-combobutton-content` + `.z-combobutton-button`) and vanishes too.
  On a colored card the theme MUST invert each child filled control via a descendant rule
  (`.z-coachmark-content .z-button`, `.z-coachmark-content .z-combobutton …`): swap bg↔text, flip the
  state-layer `::before` to the surface color, flip the `:focus-visible` outline to the on-surface color so
  the ring stays visible, and re-tint any sub-divider that was keyed to the old fill (combobutton's
  label↔arrow divider was `rgba(255,255,255,.3)` — invisible on a white fill, retint to `--zk-color-outline`).
  This is a *consequence of choosing a colored surface*, not a coachmark-specific quirk — it applies to any
  colored popup that hosts arbitrary action widgets. (Audit-and-extend: when a new filled control type is
  slotted into a colored coachmark, check its at-rest fill against the card and add an inverse rule.)
  Marble takes the neutral-card default, so it ships none of these inversion rules (see doc/skill-gaps.md 2026-07-15).
- The mask (`.z-coachmark-mask`) is a sibling of `.z-coachmark` in `<body>`, created by `zk.eff.FullMask`.
  Its z-index is set to `(coachmark z-index - 1)`. The theme may style it; the selector is `.z-coachmark-mask`.
- Animation: `.z-coachmark-open` triggers the open entrance. The base `opacity: 0; visibility: hidden` on
  `.z-coachmark` must be preserved — they are the closed state. The animation sets `opacity: 1; visibility: visible`
  via `animation-fill-mode: forwards`.

## Sibling decomposition

none — novel guided-tour card pattern. No ZK sibling shares primitives with coachmark.

## Contract

`zkmax/nav/css/coachmark.css` (separate file, zkmax-specific — NOT bundled in any standard `.css.dsp`).
The theme provides this file under `../zkcml/zkmax/src/main/resources/web/js/zkmax/nav/css/coachmark.css`.

## Edition

PE (zkmax package — requires PE license or higher per ZKDoc edition badge)

## Notes

- The old contract incorrectly listed `shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/wgt/css/coachmark.css`
  and selectors `.z-coachmark-title`, `.z-coachmark-body`, `.z-coachmark-button`. None of these exist in the
  live DOM. The real CSS file is `../zkcml/zkmax/src/main/resources/web/js/zkmax/nav/css/coachmark.css`.
- Children rendered inside `.z-coachmark-content` are arbitrary ZK widgets (`.z-label`, `.z-button`, etc.)
  placed by the ZUL author. The theme does not control their class names — style them via child combinators
  or the children's own component CSS files.
- The mold (`mold/coachmark.js`) renders the full DOM including `role="tooltip"` on the root and
  `role="button"` + `tabindex="0"` on the close div — accessibility-ready out of the box.
- ZK makes the target component interactive above the mask by temporarily setting `position: relative`
  and a high `z-index` on its DOM node. This is JS-driven; the theme cannot rely on the target having
  any particular positioning at rest.
- `.z-coachmark-icon` is present in the ZK LESS source with `position: absolute; z-index: 1;` — it appears
  to be an additional highlight/glow element around the target, but is not present in the mold output.
  Treat as unused unless confirmed in a live DOM inspection.
