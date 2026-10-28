# carousel

A slideshow container: `Carousel` renders a flex track of `Carouselitem` slides, with
optional previous/next arrow buttons, optional position-indicator dots, and an
always-present but visually-hidden status announcer span. Advances via click, arrow
keys, swipe/drag, indicator click, or an optional autoplay timer; can wrap continuously
(loop) using head/tail clone slides installed and removed at runtime.

## Edition

CE — ships in `zul.jar` (`org.zkoss.zul.Carousel` / `org.zkoss.zul.Carouselitem`,
package `zul.wgt`). New in ZK 10.4.0 (`@since 10.4.0`). No PE/EE counterpart component,
but the **ARIA/accessibility layer is EE-gated**: see "Accessibility posture" below.

## DOM structure (from mold/carousel.js + mold/carouselitem.js, reconciled against Carousel.ts runtime mutations)

```
div.z-carousel.z-carousel-{horizontal|vertical}.z-carousel-effect-{slide|fade|none}[tabindex="0"]
├─ div#{uuid}-track.z-carousel-track
│   ├─ div.z-carousel-clone[inert]                    ← CLIENT-INJECTED ONLY, loop mode.
│   │                                                    A clone of the LAST real item,
│   │                                                    inserted at the FRONT of the
│   │                                                    track (see "Clone geometry" below).
│   │                                                    Deliberately carries NO
│   │                                                    `.z-carouselitem`/`-active` class.
│   ├─ div.z-carouselitem[.z-carouselitem-active]      ← one per Carouselitem child, in
│   │   ├─ img[alt=""][aria-hidden="true"]                document order (see "Item
│   │   ├─ div.z-carouselitem-label                       content" below)
│   │   └─ (redraw of any nested child widgets)
│   ├─ div.z-carouselitem[.z-carouselitem-active]
│   │   └─ ...
│   └─ div.z-carousel-clone[inert]                    ← CLIENT-INJECTED ONLY, loop mode.
│                                                         A clone of the FIRST real item,
│                                                         appended at the END of the track.
├─ button#{uuid}-prev.z-carousel-arrow.z-carousel-arrow-prev[type="button"][disabled]
│   └─ span                                            ← literal "‹" (U+2039) fallback text,
│                                                          visually suppressed by the theme
│                                                          in favor of a CSS-drawn chevron
│                                                          (see "Arrow glyph" below)
├─ button#{uuid}-next.z-carousel-arrow.z-carousel-arrow-next[type="button"][disabled]
│   └─ span                                            ← literal "›" (U+203A) fallback text
├─ div#{uuid}-indicators.z-carousel-indicators
│   ├─ button.z-carousel-indicator[.z-carousel-indicator-active][data-index="0"][type="button"]
│   ├─ button.z-carousel-indicator[.z-carousel-indicator-active][data-index="1"][type="button"]
│   └─ ... one per slide
└─ span#{uuid}-status.z-carousel-status                ← always present, visually hidden
                                                           (sr-only clip pattern); see
                                                           "Accessibility posture" below
```

`.z-carousel-arrow-prev` / `-next` and `div.z-carousel-indicators` are **omitted entirely**
from both the server mold AND the live DOM whenever there are fewer than 2 real slides
(`count > 1` gates both, independent of the `showArrows`/`showIndicators` attribute values —
see "Attribute support"). `.z-carousel-clone` nodes are **client-injected only** (never in
the server-rendered HTML); a theme that only reads the mold output will not see them.

## Item content (from mold/carouselitem.js)

`Carouselitem` extends `zul.LabelImageWidget` (Java: `LabelImageElement`), but the item
mold does **not** use the shared `domContent_()` bundler that other `LabelImageWidget`
components (e.g. `Breadcrumbitem`) use. It calls only two of the base class's helpers,
directly, in this fixed order:

1. `domImage_()` — `<img src="…" align="absmiddle" alt="" aria-hidden="true">` when
   `image` is set. The image is always marked **decorative** (`alt=""`,
   `aria-hidden="true"`) — it never carries the slide's accessible name itself.
2. `.z-carouselitem-label` — a real wrapping `<div>` (unlike breadcrumb's bare label text
   node) containing the encoded `label` string, rendered only when `label` is non-empty.
3. Any nested child widgets (`isChildable()` returns `true` on the server side) — redrawn
   after the image/label, in document order. A slide may contain arbitrary markup (e.g. a
   `vlayout` with its own `label` + `button`), not just image/label.

**`domIcon_()` is never called by the item mold.** `Carouselitem` inherits
`iconSclass`/`iconSclasses`/`iconTooltip` getters/setters from the shared base class (they
compile and can be set from a ZUL page or Java without error), but they have **zero visual
effect** — the mold simply never emits the `<i>`/icon-stack markup those properties would
otherwise produce. Do not build theme CSS or documentation around an icon appearing on a
carouselitem; it cannot, regardless of attribute values.

## State classes

- `.z-carouselitem-active` — added to the active item's own root `<div>` by
  `Carousel._applyActiveClass()`, which runs during `bind_()` (**every** bind, not only on
  user interaction) and again on every active-index change. **The raw server-rendered
  HTML never contains this class** — it is applied by client JS immediately after mount,
  but a snapshot of the server response (or a test that inspects markup before JS executes)
  will see zero items marked active. Exactly one non-clone item carries this class at any
  time (or zero, only when there are no slides at all).
  - The modifier is derived from the item's **own** `getZclass()` + `'-active'`, not from
    a hardcoded `z-carouselitem-active` string and not from the carousel's zclass — a
    custom `zclass` on `Carouselitem` changes which class gets toggled.
- `.z-carousel-indicator-active` — added to the indicator `<button>` whose index matches
  the active slide, by the same `_applyActiveClass()` call. Derived from the **carousel's**
  own zclass (`this.$s('indicator-active')`), not the item's.
- `.z-carousel-clone` — added to head/tail clone nodes only (see "Clone geometry"); these
  nodes deliberately **drop** `.z-carouselitem` (and any `-active` modifier) so that
  selectors scoped to `.z-carouselitem` never match a clone.
- `[disabled]` on `.z-carousel-arrow-prev` / `-next` — a **native HTML attribute**, not a
  `.z-carousel-arrow-disabled` class. Only ever set when `loop="false"`, and only at the
  respective boundary (prev disabled at index 0, next disabled at the last index). When
  `loop="true"`, both arrows always have the attribute removed unconditionally (arrows
  wrap, so neither boundary is ever a dead end).
- `[inert]` on `.z-carousel-clone` nodes — JS-injected (not from the mold), removes the
  clone subtree from sequential focus navigation and the accessibility tree so a keyboard
  user cannot tab into a duplicate button/link/input that happens to live inside a cloned
  slide.
- There is no `.z-carousel-open`/`-collapsed`/`-dragging` class exposed anywhere; drag
  state is tracked only in JS internals and via a transient inline `transform`/`transition`
  on the track, never as a class.

## Attribute support

- `activeIndex` (Carousel) → drives which item gets `.z-carouselitem-active` /
  `.z-carousel-indicator-active` and the track's transform offset. Out-of-range values are
  clamped (server: `WrongValueException`; client: clamp + `zk.error` warning) rather than
  silently accepted.
- `autoplay` (Carousel) → starts/stops an internal timer; **no DOM class or attribute**
  reflects this state.
- `interval` (Carousel) → autoplay tick period; no DOM effect beyond timing. Values below
  500ms are rejected server-side, clamped client-side.
- `showArrows` (Carousel) → gates rendering of `.z-carousel-arrow-prev`/`-next`, **but
  only when combined with `count > 1`** — a single-slide carousel never shows arrows
  regardless of this attribute. Changing it triggers a full client rerender.
- `showIndicators` (Carousel) → gates `.z-carousel-indicators`, same `count > 1` gating as
  arrows. Changing it triggers a full client rerender.
- `loop` (Carousel) → controls whether `.z-carousel-clone` nodes exist (installed/removed
  at runtime, **not** via a full rerender) and whether the arrows can ever carry
  `[disabled]`.
- `pause` (Carousel) → whether autoplay pauses while the pointer hovers the root; no DOM
  class.
- `keyboard` (Carousel) → whether the root's keydown handler reacts to arrow keys / Space;
  no DOM class.
- `orient` (Carousel) → `"horizontal"` (default) or `"vertical"`; drives the
  `.z-carousel-horizontal`/`-vertical` class, the track's flex-direction, which arrow keys
  are intercepted (Left/Right vs Up/Down), and the axis used for drag/swipe and the track
  transform (`translateX` vs `translateY`).
- `effect` (Carousel) → `"slide"` (default), `"fade"`, or `"none"`; drives the
  `.z-carousel-effect-*` class. This is not purely cosmetic — see "Effect gates structural
  behavior" below.
- `disabled`/`readonly`/`invalid`/`inplace` — **none of these exist** for either widget.

## Composition invariants

### Effect gates structural behavior, not just a CSS transition choice

`effect` selects one of three fundamentally different rendering/interaction modes:

- **`slide`** (default) — items sit in the flex track at `flex: 0 0 100%` each; the track
  itself is transformed (`translateX`/`translateY`) to bring the active item into the
  overflow-clipped viewport. Loop clones are installed **only** in this mode (clone
  install/remove is gated on `effect === 'slide'` at runtime — switching away removes any
  existing clones, switching back re-installs them if `loop` is also true). Pointer-based
  drag/swipe listeners are likewise installed **only** when `effect === 'slide'`; switching
  effects at runtime tears them down or re-installs them to match.
- **`fade`** — items are taken out of flex flow (`position: absolute`, full-bleed inset)
  and cross-fade via opacity; the track's own transform is neutralized. No clones, no
  drag/swipe (there is no spatial axis to drag along).
  - The active item's `.z-carouselitem-active` classing is unaffected; it drives which
    absolute-positioned item is opaque, not which one is transformed into view.
- **`none`** — same track-transform mechanism as `slide` (an instant jump, no transition),
  but with clones/drag still gated off since neither installs for anything but
  `effect === 'slide'`.

A theme or test must not assume the track's transform is meaningful in `fade` mode, nor
assume clones exist in any mode but `slide` + `loop=true` + ≥2 slides.

### Clone geometry (loop mode)

When `loop="true"`, `effect="slide"`, and there are ≥2 real slides, two extra DOM nodes are
inserted directly into the track (client-side only, never in the server mold output):

- A clone of the **last** real item is inserted **before the first real item** (DOM order:
  `[tailClone, item₀, item₁, …, itemₙ]`).
- A clone of the **first** real item is appended **after the last real item** (DOM order
  becomes `[tailClone, item₀, …, itemₙ, headClone]`).

This is the standard "peek past the end" loop trick: scrolling backward past the first
real item reveals the tail clone (visually identical to the real last item), then the
track silently snaps to the real last item's position once the transition ends; scrolling
forward past the last real item works the mirror-image way via the head clone. Consequence
for CSS/selectors: **positional selectors are offset by one** whenever the tail clone is
present (`:nth-child`, `:first-child` on the track's children no longer correspond to
"slide index 0" — the real first slide is the *second* child). Each clone: has its `id`
attribute stripped (and the `id` stripped from every ided descendant, to avoid duplicate
IDs), drops the item's own zclass/-active classes, gains `.z-carousel-clone` +
`[inert]`, and keeps the same flex sizing as a real item so the track's one-slot-per-item
math stays consistent.

### Keyboard navigation model

The root `<div>` carries `tabindex="0"` **emitted server-side** (via `domAttrs_()`, not a
client `setAttribute` call), so it is keyboard-focusable from the very first paint frame.
While focused, and only when `keyboard="true"`:

- `ArrowRight` (or `ArrowDown` when `orient="vertical"`) → next slide.
- `ArrowLeft` (or `ArrowUp` when `orient="vertical"`) → previous slide.
- `Space` → toggles a **transient pause** of autoplay (independent of the `autoplay`
  attribute itself, which is the server-authoritative setting) — only meaningful when
  `autoplay="true"`.
- All three are suppressed when the keydown's target is (or is nested inside) an
  interactive control within a slide (`input`, `textarea`, `select`, `button`, `a`, or
  `[contenteditable]`) — the carousel does not hijack keys meant for a slide's own
  content. Pointer-drag start applies the identical guard.

### Pointer-drag / swipe

Installed on the track only when `effect === 'slide'` (see above), using Pointer Events
(`pointerdown`/`pointermove`/`pointerup`/`pointercancel`), not mouse or touch events
specifically. A drag past roughly 20% of the viewport's relevant dimension (or a shorter
drag with sufficient velocity) commits to the next/previous slide; otherwise the track
snaps back. The same "don't steal input from a nested interactive control" guard as the
keyboard model applies to `pointerdown`.

### Autoplay pause coordination

Autoplay (`_startTimer`) refuses to (re)start while **any** of: a drag is in progress, the
user has explicitly paused via Space, `pause="true"` and the pointer currently hovers the
root, the document tab is hidden (`document.hidden`), or the OS
`prefers-reduced-motion: reduce` media query matches. These are independent guards checked
every time a start is attempted (hover-leave, visibility-return, drag-end, a live
`autoplay`/`interval`/`pause` attribute change) — a theme does not need to model this, but
a test that programmatically starts autoplay must account for all of them.

## Accessibility posture (CE vs EE)

The CE mold emits **no ARIA attributes at all** for either widget — no `role`, no
`aria-label`, no `aria-roledescription`, no `aria-current`. All of the following are
layered on exclusively by the **EE `za11y` add-on**, not present in a CE-only application:

- `role="group"` + `aria-roledescription="slide"` + a positional `aria-label` (e.g. "Slide
  2 of 5") on each `.z-carouselitem`.
- ARIA labelling of the prev/next arrow buttons (e.g. "Previous slide" / "Next slide").
- `aria-current` (or equivalent) on the active `.z-carousel-indicator`.
- Turning `.z-carousel-status` into a live region and writing "Slide N of M" text into it
  on every active-index change (`Carousel._updateStatus()` is a documented no-op in CE;
  the span exists, and is correctly hidden per the sr-only pattern, but nothing ever
  writes into it without the EE add-on).

A CE-only carousel is fully mouse/keyboard/touch operable but is accessibility-silent to
assistive technology beyond the native semantics of a focusable `<div>` and plain
`<button>` elements.

## Sibling decomposition

No existing ZK component shares carousel's exact composition (flex track of full-bleed
slides + peek-past-the-end loop clones + overlay chrome). Two components supply partial
precedent for individual pieces, not the whole:

- **`paging`** (`components/paging.md`) — the prev/next arrow-button idiom (a pair of
  small icon-only buttons flanking a collection, one disabled at each boundary when
  wrapping is off) is structurally analogous to carousel's arrows, though paging's arrows
  are inline chrome, not an absolutely-positioned overlay atop unrelated content.
- **`rating`** (`components/rating.md`) — the indicator dots (a horizontal row of small,
  individually clickable, resting-vs-selected icon-like affordances with `data-index`
  semantics) are the closest precedent for carousel's indicator row, though rating's icons
  are inline glyphs, not standalone `<button>` elements with a fill-only state
  distinction.

## Bundle

`js/zul/wgt/css/carousel.css` → own `carousel.css.dsp` (registered via an explicit
`<css-uri>` on the `carousel` component only; `carouselitem` has no `<css-uri>` of its own
in `lang.xml`, so its styling must live in the same file as `carousel`'s).

## Notes

- **Arrow glyph is CSS-drawn, not the DOM's literal text.** The mold always emits a
  literal `‹` (U+2039) / `›` (U+203A) character inside each arrow's `<span>` — a
  progressive-enhancement fallback. The reference implementation visually suppresses that
  text (zero font-size) and draws the chevron shape entirely via a `::before`
  pseudo-element sized by `border` + `rotate(45deg)` (an "L-shape rotated to a
  checkmark-like wedge" technique), rather than via the DOM's own glyph or a masked icon
  asset. A theme is free to keep or replace this technique, but should not assume the
  visible chevron comes from the button's text content — it does not, once themed.
- **`domIcon_()` is dead code for carouselitem** — see "Item content" above.
- **Images are always `alt=""` / `aria-hidden="true"`** — never build a design assuming
  the `<img>` itself is announced; the slide's accessible name (EE-only) comes from the
  positional `aria-label`, not the image.
- **A single-slide carousel renders no arrows and no indicators**, even with
  `showArrows="true" showIndicators="true"` explicitly set — the `count > 1` gate applies
  unconditionally in the mold.
- **`.z-carousel-status` is always in the DOM but is inert in CE** — do not treat its
  absence of content as a bug; it requires the EE `za11y` add-on to ever contain text.
