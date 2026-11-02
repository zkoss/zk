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
- A mask element (`.z-modal-mask` + `.z-coachmark-mask`, via `zk.eff.FullMask`) is injected separately as a sibling under
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
  border-color (which sets the pointer triangle fill). The border-width is CSS-owned, but `_fixPadding()` reads
  it (see the padding-compensation bullet below), so a change to it changes the injected padding.
- `.z-coachmark-content` should keep `position: relative` (matches the ZK LESS source). Height is `100%` (inherits from root sizing).
- `.z-coachmark-close` is a **sibling of the content**, a direct child of the `.z-coachmark` root, and is
  `position: absolute` resolved against the **root** (NOT the content box). The theme must pin it to the
  top-right corner (`top`/`right`); a missing `position:absolute` flows it as a full-width row below the
  content.
- **Pinning the close evenly requires compensating for the root padding ZK injects on the pointer side.**
  Because the close is positioned against the *root* but must sit at a consistent inset inside the *content*
  box, and the root grows padding on whichever side the pointer occupies, a plain `top/right: <inset>` lands the
  close at an *asymmetric* inset on pointer-up / pointer-right cards. The injected padding is
  `10 + pointer border width/2` per `_fixPadding()` in `Coachmark.ts` (`pw = 10 + zk(p).borderWidth() / 2`, read
  from the pointer's actual CSS border, not a JS constant); with a 10px pointer border that is 20px. So the
  compensation must add exactly the injected padding, and must follow the pointer's border width, via the
  general-sibling selectors `.z-coachmark-up ~ .z-coachmark-close { top: calc(<inset> + <padding>) }` and
  `.z-coachmark-right ~ .z-coachmark-close { right: calc(<inset> + <padding>) }` (general-sibling because the
  pointer precedes the close in DOM order). Pointer-down / pointer-left inject padding on the bottom/left, which
  does not affect the top-right corner, so those need no bump.
- The close is **keyboard-focusable** (`tabindex=0`, `role=button`) so it needs a visible `:focus-visible`
  indicator.
- `.z-coachmark-pointer` MUST be `position: absolute` (the mold JS `_fixarrow()` writes inline `top`/`left`
  to align the triangle with the target; `position:static` discards those coordinates and the triangle
  collapses to the card's left edge, no longer pointing at the target).
- **A coachmark hosts arbitrary widgets, so a neutral card fill avoids per-widget rules.** On a neutral card
  every nested filled control (`.z-button`, `.z-combobutton`, …) renders with its standard styling and
  contrasts automatically. If a theme fills `.z-coachmark-content` with a saturated surface instead, the nested
  filled controls take the same fill and vanish (a `.z-combobutton` on both halves, `.z-combobutton-content` and
  `.z-combobutton-button`), so the theme must invert each one through a descendant rule
  (`.z-coachmark-content .z-button`, and so on): swap background and text, flip any state layer, keep the
  `:focus-visible` ring visible and re-tint any divider keyed to the old fill.
- The mask is a sibling of `.z-coachmark` in `<body>`, created by `zk.eff.FullMask`. FullMask gives it
  `.z-modal-mask` (the shared scrim rule), and `Coachmark.ts` then adds `.z-coachmark-mask` (`jq(this.$n_('mask')).addClass(this.$s('mask'))`).
  Its z-index is set to `(coachmark z-index - 1)`. Style the scrim colour through `.z-modal-mask`; `.z-coachmark-mask` is available for coachmark-only changes.
- Animation: `.z-coachmark-open` triggers the open entrance. The base `opacity: 0; visibility: hidden` on
  `.z-coachmark` must be preserved — they are the closed state. The animation sets `opacity: 1; visibility: visible`
  via `animation-fill-mode: forwards`.

## Sibling decomposition

none — novel guided-tour card pattern. No ZK sibling shares primitives with coachmark.

## Contract

`zkmax/nav/css/coachmark.css` (separate file, zkmax-specific — NOT bundled in any standard `.css.dsp`).
The theme provides this file under `../zkcml/zkmax/src/main/resources/web/js/zkmax/nav/css/coachmark.css`.

## Edition

EE — registered by `zkmax/src/main/resources/metainfo/zk/lang-addon.xml` (`zkmax.jar`).

## Notes

- The CSS file is `../zkcml/zkmax/src/main/resources/web/js/zkmax/nav/css/coachmark.css`.
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
