# stepbar

A horizontal (or vertical) sequence of numbered/iconed step markers connected by lines, used to communicate progress through a multi-step flow. Composite of two widgets: `<stepbar>` is the container; `<step>` children render each marker.

ZK-EE only (ships in `zkmax.jar`).

## DOM structure

```
.z-stepbar                            (<div>, root; role="list"; flex container of steps)
└─ .z-step                            (<div>, one per child <step>; role="none"; flex item)
   └─ .z-step-content                 (<div>, UNCONDITIONALLY present; role="listitem";
   |                                   aria-labelledby="<uuid>-title"; id="<uuid>-content")
   |  ├─ .z-step-icon                 (<span>, circular marker; id="<uuid>-icon";
   |  |                                also carries .z-step-icon-empty initially)
   |  └─ .z-step-title                (<span>, label text; id="<uuid>-title")
```

CRITICAL: `.z-step-content` is **always present** regardless of the `wrappedLabels` attribute. The mold function (`mold/step.js`) emits the `-content` div unconditionally. Every CSS selector that targets icon or title must include `.z-step-content` in the path:

- Correct: `.z-step-content > .z-step-icon`
- Correct: `.z-step .z-step-content > .z-step-icon`
- Wrong: `.z-step > .z-step-icon` (no direct child relationship — `.z-step-content` is always in between)
- Wrong: `.z-step .z-step-icon` (works as a descendant selector but hides the structural truth)

The `.z-stepbar-wrapped-label` class (added to the root when `wrappedLabels="true"`) changes the **layout** of `.z-step-content` and `.z-step` (e.g. switching icon + title to column direction so the title wraps below the icon), but does NOT add or remove any DOM elements. The `.z-step-content` wrapper exists in both modes.

The connectors between steps are NOT separate elements. They are `::before` (and, in wrapped-label mode, `::after`) pseudo-elements on `.z-step` and/or `.z-step-content`. In the default layout the connector is `.z-step::before`. In **wrapped-label mode** the connector is typically `.z-step-content::before` / `::after` (absolutely-positioned overlays centred on the circle mid-height), while `.z-step::before` / `::after` remain as flex-spacing holders. First-step `::before` and last-step `::after` must always be hidden by the theme regardless of which element hosts them.

## State classes

State classes are JS-toggled on the **`.z-step`** root by `Stepbar.setActiveIndex()` (linear mode) and by `Step.setComplete()` / `setError()`:

- `.z-step-active` — added to the single step whose index equals `activeIndex`. Toggled via `_toggleActive()` whenever `activeIndex` changes; only one step carries it at a time.
- `.z-step-complete` — added to every step before the active one when `linear="true"` (the default). Server-side `updateCompleteStatus()` calls `step.setComplete(i < activeIndex)` for each child. In `linear="false"` mode, `complete` is author-controlled per step.
- `.z-step-error` — added when `step.error="true"`.

There is **no** `.z-step-completed` and **no** `.z-step-inactive`. Upcoming (not-yet-reached) steps carry no state class — style them via the bare `.z-step` selector.

The icon element gets one of these content classes (mutually exclusive, set by `_adjustIconContent()`):

- `.z-step-icon-empty` — default; no other indicator is present (upcoming step).
- `.z-icon-check` — when the step is complete (and no custom `iconSclass`).
- `.z-icon-exclamation` — when the step is in error (and no custom `iconSclass`).
- whatever was passed in `iconSclass="…"` — replaces all of the above.

These content classes are added in addition to (not replacing) the base `.z-step-icon` class on the same `<i>`. To target the "blank circle" upcoming state in CSS use `.z-step-icon.z-step-icon-empty` or simply `.z-step:not(.z-step-active):not(.z-step-complete):not(.z-step-error) .z-step-icon`.

## Stepbar-level modifier classes

Toggled on `.z-stepbar` root via `domClass_()` / runtime setters:

- `.z-stepbar-linear` — present when `linear="true"` (default). Drives the "auto-complete all earlier steps" semantics.
- `.z-stepbar-wrapped-label` — present when `wrappedLabels="true"`. Does **not** add `.z-step-content` (it is always present). Instead, changes the layout so that `.z-step-content` switches from row direction (icon and title side-by-side) to column direction (icon above, title below). In this layout mode the connector pseudo-elements **double up**: `.z-step::before` / `::after` continue to occupy flex space (providing the horizontal gap between adjacent steps), AND `.z-step-content::before` / `::after` are absolutely positioned to draw the visible connector line at the circle's vertical midpoint. Theming must address both layers — hiding the inline layer alone leaves the visible overlay drifting; styling only `.z-step::before` leaves the wrapped-mode connector invisible.
- `.z-stepbar-vertical` — present when `orient="vertical"`. Flips the flex direction on the root and on each step; the connector pseudo-elements change from horizontal lines to vertical lines (height/width swap).

## Attribute support

| Attribute | Effect on DOM/classes |
|-----------|-----------------------|
| `activeIndex` (Stepbar) | JS adds `.z-step-active` to the matching child, removes from siblings, then re-evaluates complete classes (linear mode). |
| `linear` (Stepbar) | Adds/removes `.z-stepbar-linear` on root. Drives whether earlier siblings auto-receive `.z-step-complete`. |
| `wrappedLabels` (Stepbar) | Adds/removes `.z-stepbar-wrapped-label` on root. The `.z-step-content` wrapper is always present in the DOM regardless; this flag only governs its layout direction (horizontal inline vs column-stacked). |
| `orient` (Stepbar, `horizontal` or `vertical`) | Adds `.z-stepbar-vertical` when vertical. `horizontal` is the default and adds no class. |
| `title` (Step) | Text node inside `.z-step-title`. |
| `complete` (Step) | Toggles `.z-step-complete` on the step root and swaps the icon's content class to/from `.z-icon-check`. In linear mode, server-side recomputes this from `activeIndex`; do not set it manually. |
| `error` (Step) | Toggles `.z-step-error` on the step root and swaps the icon's content class to/from `.z-icon-exclamation`. |
| `iconSclass` (Step) | Replaces the default content class on the icon `<i>` with the supplied class name. Overrides the check/exclamation icon. |

`disabled` is not supported.

## Composition invariants

- **One active at a time.** When `activeIndex` changes, JS removes `.z-step-active` from every other step before adding it to the new one. CSS that styles `.z-step-active` may safely assume uniqueness.
- **Complete-before-active is computed, not authored** (in `linear="true"` mode). Server iterates children on every active-index change and sets `complete` to `i < activeIndex`. CSS must therefore treat `.z-step-complete` as the "behind the cursor" marker, not as "user manually marked done".
- **Connector is rendered by the step that follows it** — `::before` on `.z-step` (or `.z-step-content::before` when wrapped) draws the line on the step's leading edge. The very first step's `::before` and the very last step's `::after` MUST be hidden, otherwise stray line segments appear at the ends.
- **Flex children share width equally.** Each `.z-step` is `flex: 1`. The step's `<i>` and `<span>` are intrinsic-width; the connector pseudo-element consumes the remaining row space via `flex: 1`. Theming that overrides `flex` on `.z-step` breaks the connector layout.
- **Click activation is gated by linear-mode.** Every step binds an `onClick` listener, but `Step._activate()` early-returns when `stepbar.isLinear()` is true. CSS hover/active affordances should therefore be applied only inside `.z-stepbar:not(.z-stepbar-linear)`.
- **Icon's base class is reset on every state change.** `_adjustIconContent()` does `nIcon.className = this.$s('icon')` first, then adds the appropriate content class. Any extra classes a theme injects onto the icon `<span>` element via JS will be wiped — style only through CSS rules, not by DOM-poking the icon node.
- **`.z-step` must establish a local stacking context.** Iceblue achieves this via `z-index: 0` on `.z-step`; modern themes may equivalently use `isolation: isolate` or any other stacking-context trigger. Without one, the absolutely-positioned connector pseudo-elements from one step can paint across adjacent step content in browsers that promote `position: absolute` descendants to the root stacking context. The mechanism is theme choice; the **existence** of a local stacking context on `.z-step` is structural.

## Relational invariants

Theme-agnostic geometric/quantitative predicates. Verify by measurement with the stated tolerance; absolute pixel values are theme choice but the relations must hold.

- **Step circle is a true circle (not an ellipse).** On `.z-step-icon`, computed `width` and `height` must be equal to within ±1px, and `border-radius` must be `50%` (or a value ≥ `width/2`). A rectangle or oval breaks the marker's visual language.
- **Connectors are vertically centred on the circle.** In the default (non-wrapped-label) layout, the connector `::before` pseudo-element's vertical midpoint must align with the centre of `.z-step-icon`: `|connector.centerY − icon.centerY| ≤ 2px`. (Enforced by `align-items: center` on `.z-step-content`; a theme that removes that declaration will cause connectors to drift to the top or bottom of the row.)
- **Connector vertical thickness is much less than the circle diameter.** `connector.height ≤ icon.height / 4`. Connectors are narrow strokes, not bars; equal-height connectors and circles are not a valid implementation of the progress-bar metaphor this widget represents.
- **Connector spans the full space between adjacent circles.** The connector `::before` on a non-first `.z-step` must have a computed width that fills the gap between the left edge of the step's circle and the right edge of the preceding step's circle: `connector.width + icon.width ≈ step.offsetWidth` (within ±4px). `flex: 1` on the connector pseudo-element enforces this; a theme that removes that `flex` declaration produces zero-width connectors.
- **First step has no leading connector.** `.z-step:first-child::before` must render with either `display: none` or a computed `width` / `height` of 0 (or `content` absent). Any other value introduces a stray line segment before the first circle.
- **Last step has no trailing connector (wrapped-label mode).** `.z-stepbar-wrapped-label .z-step:last-child::after` must render with `display: none`. In non-wrapped mode, no `::after` connector exists by design.
- **In wrapped-label mode, the connector is vertically centred at the circle mid-height.** Each `.z-step-content::before` and `.z-step-content::after` connector must have its top offset equal to `(circle.height − connector.height) / 2` relative to `.z-step-content`, within ±2px. The formula is theme-agnostic: whatever circle size and connector thickness the theme chooses, the `top` offset must be half the difference between them — any theme must preserve this arithmetic relationship, not hard-code an absolute pixel offset.
- **In wrapped-label mode, connector endpoints abut the circle without gap or overlap.** The right end of `.z-step-content::before` is `calc(50% − circle.width/2)` from the left of `.z-step-content`; the left end of `.z-step-content::after` is `calc(50% + circle.width/2)`. Connectors must not visually merge with the circle or leave a visible gap between connector end and circle edge (tolerance ±2px).
- **Vertical orientation: connectors become vertical stripes.** When `.z-stepbar-vertical` is present, the connector `::before` must have a computed height that is non-zero and at least as tall as the icon diameter (min-height ≥ icon.height), and a computed width ≤ connector.height / 4 (i.e. the width/height roles swap from horizontal mode). The connector must remain visible (non-transparent colour and non-zero dimensions). **This requires `.z-step` itself to be `flex-direction: column`** so the `::before` stacks *above* `.z-step-content` (the icon+title row) and reads as a line joining this step's circle to the previous one — a vertical step left as `flex-direction: row` places the stripe *beside* the content instead of between the circles (the connector then connects nothing). A theme that keeps the horizontal `flex-direction: row` on the vertical step is structurally wrong regardless of the stripe's dimensions.
- **Vertical orientation: connector is horizontally centred under the icon.** When `.z-stepbar-vertical` is present, the connector `::before`'s horizontal centre must align with the icon's horizontal centre: `|connector.centerX − icon.centerX| ≤ 2px`. Because the vertical step is a left-aligned column (`align-items: flex-start`) the icon sits at the step's leading edge; the connector stripe must carry a `margin-left` ≈ `icon.width/2 − connector.width/2` to sit under the circle rather than at the step's left edge. The arithmetic is theme-agnostic (whatever circle size and stroke width the theme chooses); a connector pinned to the step's left edge with no centring margin is a defect.
- **Icon and title do not overlap.** Within a single `.z-step`, the bounding rect of `.z-step-icon` and the bounding rect of `.z-step-title` must not intersect. A gap ≥ 2px between them (provided by `margin-right` on the icon or `gap` on the flex step) is required.
- **Step items share equal width in the default configuration.** In a stepbar where no inline `width` or `flex` has been overridden, all `.z-step` elements must have the same computed `offsetWidth` within ±1px. This is enforced by `flex: 1` on each step; a theme that changes the `flex` shorthand to a non-uniform value breaks equal distribution. (Exception: first/last child may differ if the theme uses `flex: 0 1 auto` for edge steps — a valid layout technique to suppress the leading connector slot on the first step.)

## State-differs invariants

Theme-agnostic predicates that two states must be visually distinguishable. Render both states and compare computed styles on the specified selector; assert that **at least one** of the listed properties differs. The disjunction gives themes design freedom while keeping the state machine legible.

- **`active` vs `upcoming` (icon).** On `.z-step-active .z-step-icon` compared to `.z-step:not(.z-step-active):not(.z-step-complete):not(.z-step-error) .z-step-icon`, at least one of: `background-color`, `border-color` (any side), `color`, `box-shadow` — must differ. A theme using fill-colour change alone or a fill-vs-ring contrast both satisfy the disjunction.
- **`complete` vs `upcoming` (icon).** On `.z-step-complete .z-step-icon` compared to the upcoming baseline, at least one of: `background-color`, `border-color`, `color` — must differ. Additionally, the glyph content must differ: upcoming carries `.z-step-icon-empty` (no visible character), complete carries `.z-icon-check` (a check glyph). A theme that renders both states with identical chrome and the same absence of a glyph makes the progress state invisible.
- **`active` vs `complete` (icon).** Although both may use the same fill colour, at least the icon glyph must differ: active carries whatever icon content the step is configured with (or the empty circle), complete always carries `.z-icon-check`. These two states must not appear identical to the eye — if fill and border are identical, the glyph difference is the sole required discriminator.
- **`error` vs `upcoming` (icon).** On `.z-step-error .z-step-icon` compared to upcoming: at least one of `background-color`, `border-color`, `color` — must differ AND the glyph class must differ (error uses `.z-icon-exclamation`, upcoming uses `.z-step-icon-empty`). Both differences should be present, not just one, because an error state must communicate urgency — but the evaluator enforces the disjunction.
- **`error` vs `active` (icon).** At least one of `background-color`, `border-color`, `color` — must differ. An error step may not be styled identically to the active step; the visual separation is critical for usability.
- **`active` title vs `upcoming` title.** On `.z-step-active .z-step-title` compared to `.z-step-title` (resting), at least one of: `color`, `font-weight`, `font-size` — must differ. Active steps must call more attention to their label than upcoming ones.
- **`error` title vs `upcoming` title.** On `.z-step-error .z-step-title` compared to `.z-step-title` (resting), at least one of: `color`, `font-weight` — must differ. Error state must communicate abnormality through the label as well as the icon.
- **`connector` lit (active/complete) vs `connector` unlit (upcoming).** On `.z-stepbar-linear .z-step-active::before` / `.z-stepbar-linear .z-step-complete::before` compared to `.z-step::before` (upcoming): computed `background-color` (or `border-color` if the connector is a border rather than a filled rect) must differ. A connector that does not change colour between upcoming and complete renders the progress arc invisible.
- **Non-linear hover vs resting (icon or step).** In `.z-stepbar:not(.z-stepbar-linear)`, when hovering over a `.z-step`, at least one of `background-color`, `border-color`, `color`, `box-shadow` on `.z-step-icon` (or on `.z-step` itself) must differ from the resting state. Any theme must ensure some hover affordance in non-linear mode; typical selectors are `.z-stepbar:not(.z-stepbar-linear) .z-step-complete .z-step-icon:hover` and `.z-step .z-icon-check:hover`.
- **Non-linear active-press vs hover (icon).** In `.z-stepbar:not(.z-stepbar-linear)`, the `:active` computed style on a step icon must differ from its `:hover` computed style in at least one of `background-color`, `border-color`, `color`. Themes that omit the `:active` rule make press indistinguishable from hover, removing tactile feedback.
- **`cursor` in linear vs non-linear mode.** Computed `cursor` on `.z-step` inside `.z-stepbar-linear` must equal `default` (or `auto`); computed `cursor` on `.z-step` inside `.z-stepbar:not(.z-stepbar-linear)` must equal `pointer`. The canonical selector is `.z-stepbar:not(.z-stepbar-linear) .z-step { cursor: pointer }`. This is both a C-tier state-differs predicate and an interaction contract.

## Sibling decomposition

stepbar has no ZK component sibling that shares its layout primitive (connected horizontal markers). It borrows:

- **Circular marker rendering** — same `border-radius: 50%`, font-icon-inside-a-box pattern used by `rating` (`.z-rating-icon`) and the `<i>` markers in `checkbox`/`radio` molds. See `components/rating.md`.
- **State-class-on-root, descendant-selects-icon** — same pattern as `rating` (`.z-rating-disabled` on root, `.z-rating-icon` styled via `.z-rating-disabled .z-rating-icon`). The theme should *not* try to put state classes on the icon itself.
- **Vertical-orientation modifier on root** — same convention as `rating` (`.z-rating-vertical`), `tabbox` (`.z-tabbox-vertical`), `slider` (`.z-slider-vertical`).

There is no ZK component sibling with shared bundling. External design-system analogs (where the theme should look for visual cues) belong in the bundle's References block, not here.

## Bundle

`stepbar.css.dsp` — declared in zkmax's `lang-addon.xml` as the `css-uri` for the `stepbar` widget's `default` mold. `step` has no `css-uri` of its own; all step-level styles ride in `stepbar.css.dsp`.

## Edition

EE (zkmax). Loading this widget requires a valid ZK EE license; absent that, ZK falls back to nothing (the widget does not register).

## Notes

- **`.z-step-content` is always in the DOM.** It is emitted unconditionally by `mold/step.js`. Any CSS selector targeting `.z-step-icon` or `.z-step-title` that omits `.z-step-content` in the path will fail specificity or structural tests. Correct forms: `.z-step-content > .z-step-icon` or `.z-step .z-step-content > .z-step-icon`.
- **`.z-step-icon` is a `<span>`, not `<i>`.** The mold emits `<span id="...-icon" class="...icon ...icon-empty" ...>`. External write-ups that use `<i>` are inaccurate. Font-icon classes (`.z-icon-check`, `.z-icon-exclamation`, etc.) are added to this `<span>` at runtime by `_adjustIconContent()`.
- Legacy bundles and external write-ups sometimes refer to `.z-step-completed` and `.z-step-number`. Neither exists. The actual classes are `.z-step-complete` (no trailing "d") and `.z-step-icon`.
- `_adjustIconContent()` runs on `bind_` and on every state setter, so the icon's class always reflects the current state — there is no transient "wrong icon" window the theme needs to mask.
- `Step.title` setter only mutates the text node of `.z-step-title`; the wrapper element is never destroyed. Theme `:empty` selectors that rely on the title element disappearing will not fire.
- The component name is **stepbar** (singular root, plural steps) — the file `stepbar.css.dsp` covers both `<stepbar>` and `<step>` markup.
- **`setOrient()` triggers a full `rerender()`**, unlike `setLinear()` and `setWrappedLabels()` which use `toggleClass`. The DOM is torn down and rebuilt when `orient` changes at runtime. This means any cached `querySelector` references to step children become stale after an orient change. CSS is unaffected (rules apply to freshly-rendered DOM immediately), but JS-driven measurement or per-step state applied outside ZK must be re-applied in the post-render callback.
- `setOrient()` validates its argument: only `"horizontal"` and `"vertical"` are accepted; any other value calls `zk.error()` and returns without modifying state. `orient` is available since ZK 10.2.0.
