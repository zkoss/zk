# Component: carousel (theme design)
tier: T2
category: media
preview: ${PREVIEW_URL}/carousel.zul
rules: see .claude/skills/zk-component-rules/components/carousel.md
contract-approved: true
zk-version: 10.4.0-jakarta
js-source-files:
  - zul/src/main/resources/web/js/zul/wgt/Carousel.ts
  - zul/src/main/resources/web/js/zul/wgt/Carouselitem.ts
  - zul/src/main/resources/web/js/zul/wgt/mold/carousel.js
  - zul/src/main/resources/web/js/zul/wgt/mold/carouselitem.js
js-source-hash: e79a0be9cabed99f4674e4249156511d432caa24e3984ef45146cef5ef676574
closest-sibling: none — novel flex-track + peek-past-the-end loop-clone composition; no
  MUI analog exists at all (MUI ships no carousel primitive). Decompose for parts only:
  `paging` (prev/next arrow-button pair, one disabled at each non-looping boundary) +
  `rating` (row of individually-styled, data-index-addressed, resting-vs-selected
  affordances — the closest precedent for the indicator dots).
shared-css-file: js/zul/wgt/css/carousel.css
siblings: [carouselitem]
mockup-needed: Y — no ZKDoc canonical image exists for carousel (`ZK_component_ref` has
  no `carousel.md` entry and `zk_component_ref/images/` has no `ZKCompRef_Carousel*.png`;
  confirmed by directory search), so condition (1) of the mockup-decision rule fires
  automatically. The zkbooks demo screenshot
  (`DOC/zkbooks/componentreference/screenshots/carousel.png`) shows only ZK's own
  unthemed stock rendering, which is not a design ground truth. The HTML mockup below is
  therefore the only visual reference for this contract.

## References
- MUI CSS: **no analog** — MUI ships no carousel component; confirmed absent from
  `/Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/INDEX.md`
  and no `*carousel*` file exists anywhere under that tree. This is why the component is
  tier T2 (ZK-only; DESIGN.md tokens + sibling-token coherence), not T1.
- ZK stock LESS (structural/behavioral reference, read carefully, not copied): `Carousel.ts`
  §"less/carousel.less" at
  `/Users/hawk/Documents/workspace/ZK10/zk/zul/src/main/resources/web/js/zul/wgt/less/carousel.less`
  — informs three theme decisions below (chevron technique, indicator touch target,
  reduced-motion handling); see "Design Contract" for what Marble keeps vs. changes.
- DESIGN.md sections: §2 (Text Colors), §4 (Spacing Scale), §5 (Corner Radii —
  `--zk-shape-card`), §7 (Typography — "Small helper / caption" role), §8 (State-Layer
  Overlays — `--zk-state-hover-opacity`, `--zk-state-disabled-opacity`), §9 (Motion —
  reduced-motion is already handled centrally, see Design Contract), §10 (Density).
  **Correction from an earlier pass:** the overlay tokens themselves (`--zk-color-scrim`,
  `--zk-color-inverse-surface`, `--zk-color-inverse-on-surface`) are defined only in
  `zul/src/main/resources/web/zul/css/tokens/_colors.css` — DESIGN.md §1's Surface Palette
  table has no scrim row at all (the word "scrim" appears exactly once in DESIGN.md, in
  coachmark's prose, not as a token-table entry), so this contract now cites the token
  file directly rather than a non-existent §1 entry.
- `doc/spec/brand-override.md` — documents `--zk-color-on-primary` as a token integrators
  are expected to repoint to a **dark** value for a light-primary rebrand ("a light preset
  would also need `--zk-color-on-primary`"). This is the reason the light overlay voice
  below is `--zk-color-inverse-on-surface`, not `--zk-color-on-primary` — see Design
  Contract.
- ZKDoc canonical: none found — carousel is new in ZK 10.4.0 and has no entry yet under
  `/Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/`.
- zkbooks demo (usage patterns only, not visual ground truth):
  `DOC/zkbooks/componentreference/src/main/webapp/essential/carousel.zul`.
- Iceblue baseline: doc/contracts/baselines/carousel-iceblue.png — **NOT captured this
  pass**. The `preview-app-iceblue` profile was not running (confirmed:
  `curl ${PREVIEW_URL}/carousel.zul` returned no response) and no `carousel.zul` preview
  page exists yet in either the Marble or iceblue app to render — there is nothing to
  screenshot yet regardless of the profile. See "Structural surprises" in the authoring
  summary for the follow-up needed before this file can be produced.
- HTML contract: doc/contracts/carousel.html.

## Design Contract

Carousel is a full-bleed media frame: the root clips to a `--zk-carousel-radius`
(`--zk-shape-card`, 6px) rounded rectangle so it reads as one coherent hero unit regardless
of what photographic content the author drops inside, matching the theme's general
"default to 6px for any container" rule (DESIGN.md §5) rather than the tighter 4px
interactive-control radius — a carousel is a content frame, not a control. Because the
slides underneath are arbitrary, unpredictable imagery, none of the overlay chrome (arrows,
indicators, caption label) can safely key off surface/on-surface tokens the way an
ordinary control does — a `--zk-color-primary` arrow would vanish against a blue photo. All
three overlay pieces instead speak exactly two voices, both already established
theme-wide: `--zk-color-scrim` (the theme's existing translucent-black overlay token) for
every dark overlay fill — arrow resting background, inactive indicator dot, caption
backdrop — and `--zk-color-inverse-on-surface` (the theme's existing brand-**independent**
light-on-dark foreground token, `#f0f4fa`) for every light foreground — arrow chevron
color, resting indicator ring, caption text. The active indicator is the one exception: a `--zk-color-primary`
pill ringed with `--zk-color-on-primary` (see "Indicators"). **Correction from an earlier pass:** the light
voice was originally `--zk-color-on-primary`, which reads identically against the default
Marble palette (`#ffffff` vs. `#f0f4fa` is not a visible difference) but is the *wrong*
role — `doc/spec/brand-override.md` documents `--zk-color-on-primary` as a token
integrators are expected to repoint to a **dark** value for a light-primary rebrand
("a light preset would also need `--zk-color-on-primary`"). Under that supported
customization path, every carousel overlay foreground would flip dark-on-dark against the
always-dark `--zk-color-scrim` fill — precisely the "vanishes against an arbitrary
background" failure this component's whole design goal is to prevent, just triggered by a
brand override instead of photo content. `--zk-color-inverse-on-surface` is architecturally
insulated from the primary seed (a literal hex, never re-derived by `oklch(from
--zk-color-primary …)`), which is the guarantee "legible against ANY slide" actually needs
to extend to "legible under ANY brand override" too. This is a deliberate reuse, not
independent color choices per region: one dark voice + one light voice, applied
consistently, is what makes arrows/indicators/caption read as one overlay system rather
than three unrelated widgets. Each pairing is exposed as its own knob rather than a single
shared pair, because arrows, indicators, and the caption are independently positioned and
an integrator overriding one region (say, a dark-glass indicator row) should not be forced
to also change the arrows.

**Chevron technique — kept, not switched to a masked icon.** ZK's stock LESS draws the
‹/› glyph via a `::before` pseudo-element sized by two `border` sides (not `border:
transparent` on all four sides, so there is no CSS-triangle transparent-side artifact to
worry about) rotated 45°, colored via `border-color: currentColor`, with the mold's own
literal `‹`/`›` text zeroed out via `font-size: 0`. Marble keeps this technique rather than
switching to a `mask-image`-based Lucide icon, for two reasons: (1) it needs no new
icon-set entry and stays crisp at any size purely from geometry; (2) it is measurably
*safer* under `forced-colors` than the alternative — a `border-color: currentColor` glyph
rides on the browser's own forced remap of the `color` property (which the UA maps to a
legible foreground system color), whereas a `background-color: currentColor` +
`mask-image` icon (the existing central guard's documented "(2d)" failure mode) gets its
`background-color` force-mapped to `Canvas` — the page background, not a foreground role —
and vanishes. Choosing the border technique means the chevron needs **no new
forced-colors guard at all**; see "Forced colors" below for what *does* need one.

**Arrow hover** darkens the existing scrim rather than layering a `currentColor`
state-layer overlay (DESIGN.md §8's usual mechanism): the arrow's foreground is light
(`--zk-color-inverse-on-surface`), so a standard light-tinted overlay would lighten the
dark scrim toward white — the wrong direction for a "press this" affordance.
`--zk-carousel-arrow-bg-hover` is instead its own literal, deliberately-darker
translucent-black knob (matching the existing precedent of
`--zk-grid-row-hover-bg`/`--zk-combobox-item-hover-bg`, both literal rgba hover knobs, not
derived from a base token) — this is the one non-token-rooted color value in the whole
component, and it exists because "darken an already-translucent overlay" has no
equivalent token today. The hover fill change is itself transitioned
(`background-color var(--zk-motion-duration-standard) var(--zk-motion-easing-standard)`,
c31) — DESIGN.md §9's default interactive pair, same as every other hover transition in
the theme.

**Indicators** keep ZK stock's WCAG-motivated split between the *visible* 8px dot and its
*hit area*: the clickable `<button>` stays a 24×24 CSS-pixel box (WCAG 2.1 SC 2.5.8 Target
Size Minimum — this is an accessibility **floor**, not a density choice, so it is not
bound to the compact-mode ladder; see "Density" below) while only the centered 8px `::before`
disc is visibly painted. Resting = an 8px circle, `--zk-carousel-indicator-bg` fill (scrim) with a
`--zk-carousel-indicator-ring` ring (inverse-on-surface). Active = a 22px pill
(`--zk-shape-corner-full`) with a `--zk-carousel-indicator-active-bg` fill (primary) and a
`--zk-carousel-indicator-active-ring` ring (on-primary). The old light-filled active dot looked
hollow on light slides; a brand-coloured fill reads on both light and dark slides, and the
pill shape marks the current slide without relying on colour alone. The 22px width keeps the
pill inside the 24px hit area. `-indicator-active-bg` keeps its meaning as the active
indicator's fill, so overriding it recolours the active pill; `-indicator-ring` only
recolours the resting dots' ring. Inactive dots are unchanged from before the pill.

**Indicator hover** cannot reuse the arrow's "darken the fill" recipe unmodified: an
indicator's fill is *already* one of two different colors depending on active/resting
state (scrim on resting, primary on active), so a single darker-fill hover knob
would be correct for one state and wrong for the other — darkening an
already-saturated active pill toward grey would read as "disabled," not "hovered."
Instead, hover is carried by a **geometry** cue that works identically regardless of which
fill is currently applied: the visible dot scales up (`transform: scale(1.25)` on
`::before`, c30), transitioning on the same standard duration/easing pair as everything
else (c29). This is the same category of move as the arrow's own departure from the
"currentColor state-layer" default (DESIGN.md §8's usual *mechanism*, not its intent) —
the state-differs *requirement* (hover must be visually distinguishable from resting) is
satisfied by whichever mechanism actually works for the component's specific color model,
and a size cue is the one mechanism that is fill-color-agnostic by construction. The active pill scales to about 28px on hover, 2px past each side of
its 24px button; the button and `.z-carousel-indicators` are `overflow: visible`, so it is not
clipped (accepted).

**Caption label** (`.z-carouselitem-label`) is a small opaque-scrim **chip** anchored to
the bottom-left of the slide (`--zk-spacing-4`, exactly matching ZK stock's 16px — no
departure needed here), padded `--zk-spacing-1 --zk-spacing-2` (4px/8px — a minor
departure from ZK stock's 4px/10px, rounded to the theme's spacing scale rather than kept
as an odd 10px), and corner-rounded at `--zk-shape-card` (6px). **Correction from an
earlier pass:** the radius was originally `--zk-shape-corner-extra-small` (4px, an exact
match to ZK stock's own value) — but that contradicts both this contract's own "chip"
classification of the element and DESIGN.md §5's explicit rule ("Chip, search box → 6px,
`--zk-shape-card`"; "default to 6px for any container/card; 4px for interactive controls"
— the same rule already correctly applied to the root in c1). A caption chip is a small
container, not an interactive control, so it takes the container radius, not the control
radius; "mirrors ZK stock's 4px" was not a valid `source` to begin with, per this theme's
own authoring discipline (see the Spacing paragraph below, which already avoids that
mistake for offsets). The label's text uses `--zk-typescale-body-small` (12px/400/16px,
c32) — DESIGN.md §7 frames the intended role as "Small helper / caption" (11px/400), but no
shipped token is literally named or sized for that row; `body-small` is the nearest actual
`--zk-typescale-*` token embodying the same "small, regular-weight, non-emphasized" role
(as opposed to `label-small`, which is also 11px but carries a 500 emphasis weight suited
to nav/list labels, not a photo caption).

**Spacing departures from ZK stock, and why:** arrow edge-offset and indicator row
bottom-offset both move from ZK's 10px to `--zk-spacing-2` (8px) — a deliberate landing on
the theme's 4dp spacing scale (DESIGN.md §4) rather than "mirrors ZK stock," which is not a
valid `source` per this theme's authoring discipline. The indicator gap between dots
likewise moves from ZK's 6px to `--zk-spacing-2` (8px) for the same reason.

**Motion:** the track's slide transform and the fade effect's opacity both transition on
`--zk-motion-duration-standard` (250ms) / `--zk-motion-easing-standard`, the theme's
default interactive pair (DESIGN.md §9) — no new duration/easing tokens are needed. ZK
stock's own per-component `@media (prefers-reduced-motion: reduce)` block (which zeroes
the track/fade transitions) is **redundant with, and superseded by,** Marble's existing
*central* reduced-motion rule in `tokens/_motion.css` — a universal `*, *::before, *::after`
selector that already forces every transition/animation theme-wide to a near-zero duration
when the OS preference is set. Marble does not need to reproduce ZK's component-local
carve-out; the central mechanism already covers the carousel track and the fade opacity
transition with zero additional CSS. (The JS-level autoplay-stops-on-reduced-motion
behavior in `Carousel.ts#_startTimer` is unaffected either way — that is JS logic, not
CSS, and works identically regardless of theme.)

## Outcome assertions

Carousel is a structurally rich composite (track + clones + overlay arrows + overlay
indicators + overlay caption, several of which are client-injected and easy to mis-position
independently of any single selector's computed style), so it earns a full outcome-row set
rather than a `visual-goal: trivial` declaration.

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-carousel` has non-zero bbox AND computed `overflow` is `hidden` (or `clip`) on both axes | the root must actually behave as a clipped media frame — an unclipped root would let slides bleed outside the rounded corners, breaking the "one hero unit" read |
| M2 | Exactly one non-clone `.z-carouselitem` (i.e. `.z-carouselitem:not(.z-carousel-clone)`) carries the active modifier at a time, AND that item's bbox is within ±2px of the root's content-box on all four edges | the "current slide" must be the one actually filling the visible frame — a stray blank frame (wrong transform offset, wrong item marked active) is the single worst carousel failure mode |
| M3 | The active item's rendered bbox covers ≥ 95% of the root's content-box area | slides must fill the frame — no letterboxing/dead space around the "active" content |
| M4 | When both arrows are present: `.z-carousel-arrow-prev` and `.z-carousel-arrow-next` bboxes do not intersect the `.z-carousel-indicators` bbox, AND (for `orient="horizontal"`) both arrows' vertical centers are within 4px of the root's vertical center | arrows must read as edge-docked controls, not stray floating boxes that drift into the indicator row or off-center |
| M5 | When indicators are present: every `.z-carousel-indicator`'s vertical center is within 2px of every other indicator's vertical center, AND their horizontal order matches ascending `data-index` order | the indicator row must read as one coherent horizontal control, not a jumbled cluster |
| M-arrow-prev-visible | `.z-carousel-arrow-prev`'s `::before` chevron shape has a resolved bbox with `width ≥ 6px AND height ≥ 6px`, and that bbox is fully inside `.z-carousel-arrow-prev`'s own bbox | the mold ships a literal `‹` text glyph that Marble deliberately hides (`font-size: 0`) in favor of this CSS-drawn shape — this row exists specifically to catch the shape silently failing to render (e.g. a typo'd `border-left`/`border-bottom` pair) and leaving the button glyph-less, which the hidden text glyph would not surface visually |
| M-arrow-next-visible | same as `M-arrow-prev-visible`, for `.z-carousel-arrow-next`'s `::before` | ditto, other direction |
| M-indicator-dot-visible | every `.z-carousel-indicator`'s `::before` dot has a resolved bbox with `width ≥ 6px AND height ≥ 6px`, centered (±2px) within the indicator button's own bbox | the visible 8px dot is deliberately smaller than its 24px hit area (WCAG 2.5.8) — this row catches the dot collapsing to zero size while the (still-clickable) button remains, which would look like the indicator row silently lost its markers |
| M-label-visible | whenever a `.z-carouselitem-label` is present on the active item: its bbox is non-zero AND `bbox.bottom ≤ activeItem.bbox.bottom` AND `bbox.right ≤ activeItem.bbox.right` (i.e. the caption never bleeds outside its own slide) | the caption is positionally anchored (`bottom`/`left` offsets) rather than flow-laid-out, so a sizing regression could push it outside the visible slide entirely |

> Note on the mandatory glyph-row rule: the arrow chevron's `::before` uses `content: ''`
> (an empty string — the shape is drawn entirely by `border` geometry, not by a Unicode
> character in `content`), so the literal-glyph-match check (checklist item 3 of the
> glyph rule) does not apply verbatim here — there is no `content` string to match against
> prose. The **visibility** variant of the rule (non-zero, in-frame bbox) is the correct
> analogous safeguard for a geometry-drawn icon and is what `M-arrow-prev-visible` /
> `M-arrow-next-visible` / `M-indicator-dot-visible` implement instead.

## Expected values

| id | selector | property | expected (token preferred) | token-rooted? | source |
|----|----------|----------|----------------------------|---------------|--------|
| c1 | `.z-carousel` | border-radius | `var(--zk-carousel-radius)` → `var(--zk-shape-card)` (6px) | yes | DESIGN.md §5 |
| c2 | `.z-carousel` | overflow | `hidden` | no | structural clip, not a design token |
| c3 | `.z-carousel-track` | transition | `transform var(--zk-motion-duration-standard) var(--zk-motion-easing-standard)` | yes | DESIGN.md §9 |
| c4 | `.z-carousel-effect-fade .z-carouselitem` | transition | `opacity var(--zk-motion-duration-standard) var(--zk-motion-easing-standard)` | yes | DESIGN.md §9 |
| c5 | `.z-carousel-arrow` | width / height | `var(--zk-carousel-arrow-size)` → `var(--zk-control-height-sm)` (32px) | yes | DESIGN.md §10 — density-bound, see Cross-cutting §Density. **Corrected** from `--zk-control-height-xs` (28px) to align with the cited `paging` sibling's own alias (`--zk-paging-control-size: var(--zk-control-height-sm)`) — see Cross-cutting §Density |
| c6 | `.z-carousel-arrow` | border-radius | `var(--zk-shape-corner-full)` | yes | DESIGN.md §5 — circular control |
| c7 | `.z-carousel-arrow` | background-color | `var(--zk-carousel-arrow-bg)` → `var(--zk-color-scrim)` | yes | `tokens/_colors.css` (scrim is not a DESIGN.md §1 table row — see References) |
| c8 | `.z-carousel-arrow` | color | `var(--zk-carousel-arrow-fg)` → `var(--zk-color-inverse-on-surface)` | yes | `tokens/_colors.css`; brand-independent by design — see Design Contract / `brand-override.md`. **Corrected** from `--zk-color-on-primary` |
| c9 | `.z-carousel-arrow:hover` | background-color | `var(--zk-carousel-arrow-bg-hover)` (`rgba(0, 0, 0, 0.65)`, literal) | no — see Design Contract's "one non-token-rooted value" note | curated; no darker-scrim token exists today |
| c10 | `.z-carousel-arrow:focus-visible` | outline | `var(--zk-focus-ring)` | yes | DESIGN.md §8 — global focus convention, not knob-driven |
| c11 | `.z-carousel-arrow::before` | border-left / border-bottom (prev) or border-right / border-top (next) | `2px solid currentColor` | no | geometry, not a color/spacing token — color rides on c8 via `currentColor` |
| c12 | `.z-carousel-arrow[disabled]` | opacity | `var(--zk-state-disabled-opacity)` (0.38) | yes | DESIGN.md §8 — reuses the global disabled convention, not knob-driven |
| c13 | `.z-carousel-arrow-prev`, `.z-carousel-arrow-next` | left / right (offset from edge) | `var(--zk-spacing-2)` (8px) | yes | DESIGN.md §4 — departs from ZK stock's 10px to land on the spacing scale |
| c14 | `.z-carousel-indicators` | bottom (offset from edge) | `var(--zk-spacing-2)` (8px) | yes | DESIGN.md §4 — same departure as c13 |
| c15 | `.z-carousel-indicators` | gap | `var(--zk-spacing-2)` (8px) | yes | DESIGN.md §4 — departs from ZK stock's 6px |
| c16 | `.z-carousel-indicator` | width / height (hit area) | `24px` (literal) | no | WCAG 2.1 SC 2.5.8 accessibility floor — see Cross-cutting §Density; not density-bound |
| c17 | `.z-carousel-indicator::before` | width / height (visible dot) | `8px` (literal) | no | decorative sub-hit-area mark, not a themed role |
| c18 | `.z-carousel-indicator::before` | background-color | `var(--zk-carousel-indicator-bg)` → `var(--zk-color-scrim)` | yes | `tokens/_colors.css` (see c7) |
| c19 | `.z-carousel-indicator::before` | border-color | `var(--zk-carousel-indicator-ring)` → `var(--zk-color-inverse-on-surface)` | yes | resting ring has its own knob — see Design Contract. **Corrected** from `--zk-color-on-primary` |
| c20 | `.z-carousel-indicator-active::before` | background-color | `var(--zk-carousel-indicator-active-bg)` → `var(--zk-color-primary)` | yes | DESIGN.md §2 — the defining state; primary pill so it reads on light slides — see Design Contract |
| c21 | `.z-carousel-indicator-active::before` | border-color | `var(--zk-carousel-indicator-active-ring)` → `var(--zk-color-on-primary)` | yes | ring contrasts with the primary fill, mirrors c18/c19 |
| c21a | `.z-carousel-indicator-active::before` | width / border-radius | `22px` (literal) / `var(--zk-shape-corner-full)` | no | pill shape marks the active state without colour alone; stays inside the 24px hit area |
| c22 | `.z-carousel-indicator:focus-visible` | outline | `var(--zk-focus-ring)` | yes | DESIGN.md §8 — global focus convention |
| c23 | `.z-carouselitem-label` | background-color | `var(--zk-carousel-label-bg)` → `var(--zk-color-scrim)` | yes | `tokens/_colors.css` (see c7) |
| c24 | `.z-carouselitem-label` | color | `var(--zk-carousel-label-fg)` → `var(--zk-color-inverse-on-surface)` | yes | `tokens/_colors.css`; brand-independent — see Design Contract. **Corrected** from `--zk-color-on-primary` |
| c25 | `.z-carouselitem-label` | bottom / left | `var(--zk-spacing-4)` (16px) | yes | DESIGN.md §4 — exact match to ZK stock |
| c26 | `.z-carouselitem-label` | padding | `var(--zk-spacing-1) var(--zk-spacing-2)` (4px/8px) | yes | DESIGN.md §4 — departs from ZK stock's 4px/10px |
| c27 | `.z-carouselitem-label` | border-radius | `var(--zk-carousel-radius)` → `var(--zk-shape-card)` (6px) | yes | DESIGN.md §5 — "Chip, search box → 6px"; the container/chip radius, not the interactive-control radius. **Corrected** from `--zk-shape-corner-extra-small` (4px), which contradicted the contract's own "chip" classification and c1's identical rule |
| c28 | `.z-carousel > img`, `.z-carouselitem > img` | width / height / object-fit | `100%` / `100%` / `cover` | no | layout keywords, not design tokens |
| c29 | `.z-carousel-indicator::before` | transition | `transform, background-color, border-color var(--zk-motion-duration-standard) var(--zk-motion-easing-standard)` | yes | DESIGN.md §9 — enables the c30 hover cue and the resting/active fill swap (c18–c21) to animate smoothly |
| c30 | `.z-carousel-indicator:hover::before` | transform | `scale(1.25)` | no | geometric hover cue, fill-color-agnostic by construction — see Design Contract "Indicator hover" |
| c31 | `.z-carousel-arrow` | transition | `background-color var(--zk-motion-duration-standard) var(--zk-motion-easing-standard)` | yes | DESIGN.md §9 — the c9 hover-darken transition |
| c32 | `.z-carouselitem-label` | font-size / font-weight / line-height | `var(--zk-typescale-body-small-size)` / `-weight` / `-line-height` (12px / 400 / 16px) | yes | DESIGN.md §7 ("Small helper / caption" role, 11px/400 intent) — `body-small` is the nearest shipped token to that role; see Design Contract |
| c33 | `.z-carousel-effect-none .z-carousel-track` | transition | `none` | no | structural effect-variant override, not a design token — see skill's "Effect gates structural behavior" |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (root frame) | `.z-carousel` | c1, c2, M1 |
| slide-effect track | `.z-carousel-effect-slide .z-carousel-track` | c3 |
| fade-effect item | `.z-carousel-effect-fade .z-carouselitem` | c4 |
| active slide | `.z-carouselitem-active` | M2, M3 |
| arrow default | `.z-carousel-arrow` | c5, c6, c7, c8, c11 |
| arrow hover | `.z-carousel-arrow:hover` | c9, c31 |
| arrow focus-visible | `.z-carousel-arrow:focus-visible` | c10 |
| arrow disabled (loop=false boundary) | `.z-carousel-arrow[disabled]` | c12 |
| arrow glyph rendered | `.z-carousel-arrow-prev::before`, `.z-carousel-arrow-next::before` | M-arrow-prev-visible, M-arrow-next-visible |
| arrows docked | `.z-carousel-arrow-prev`, `.z-carousel-arrow-next` | c13, M4 |
| indicators row | `.z-carousel-indicators` | c14, c15, M5 |
| indicator default (resting) | `.z-carousel-indicator` | c16, c17, c18, c19, c29, M-indicator-dot-visible |
| indicator hover (resting or active dot) | `.z-carousel-indicator:hover` | c30 |
| indicator active | `.z-carousel-indicator-active` | c20, c21 |
| indicator focus-visible | `.z-carousel-indicator:focus-visible` | c22 |
| caption label present | `.z-carouselitem-label` | c23, c24, c25, c26, c27, c32, M-label-visible |
| single-slide (no arrows/indicators) | `.z-carousel:has(.z-carouselitem):not(:has(.z-carousel-arrow))` | structural — confirms arrows/indicators are absent, not merely hidden, when count ≤ 1 |
| vertical orientation | `.z-carousel-vertical` | c3 (transform axis differs; same transition token) |
| effect="none" track (instant jump, no transition) | `.z-carousel-effect-none .z-carousel-track` | c33 |

## Preview anchors

**No preview ZUL exists yet.** `zkpreview/src/main/webapp/web/carousel.zul` is absent — this is a
brand-new component with no existing preview page in either the Marble or iceblue app.
Before the Evaluator can run against this contract, a preview page must be authored that
exercises every row in "States to evaluate" below, including at minimum:

- a default instance with ≥3 slides (image + label on at least one slide, a nested-widget
  slide like the zkbooks demo's `vlayout` on another) so arrows/indicators actually mount;
- a `loop="false"` instance parked at `activeIndex="0"` (or the last index) so the
  boundary-disabled arrow state is reachable without a client interaction script;
- one instance per `effect` value (`slide`, `fade`, `none`);
- an `orient="vertical"` instance;
- a single-slide instance (`showArrows="true" showIndicators="true"` explicitly set, to
  prove the `count > 1` gate suppresses them regardless);
- an `autoplay="true"` instance (for the Evaluator's own manual/visual confirmation only —
  the M-rows above are all static-frame assertions and do not depend on autoplay timing).

Suggested anchor strategy (matching the project's post-`pv.css` convention of plain
state-matrix markup, no page-local classes — see `reference_pv_css_dissolved`): give each
demo instance a stable `id` (e.g. `#pv-carousel-default`, `#pv-carousel-loop-false`,
`#pv-carousel-effect-fade`, `#pv-carousel-vertical`, `#pv-carousel-single-slide`) rather
than relying on positional selectors.

## DOM key selectors

```
.z-carousel                                  ← root <div>, tabindex="0"
.z-carousel-horizontal / .z-carousel-vertical
.z-carousel-effect-slide / -fade / -none
.z-carousel-track                            ← flex track, direct child of root
.z-carousel-clone                            ← CLIENT-INJECTED ONLY (loop mode); drops .z-carouselitem
.z-carouselitem                              ← one per slide, inside track
.z-carouselitem-active                       ← CLIENT-ADDED on every bind_(), never in server HTML
.z-carouselitem-label                        ← real wrapping <div>, not bare text
.z-carousel-arrow / -arrow-prev / -arrow-next    ← <button>, sibling of .z-carousel-track (not inside it)
.z-carousel-arrow[disabled]                  ← native attribute, not a class; loop="false" boundary only
.z-carousel-arrow::before                    ← CSS-drawn chevron (border technique)
.z-carousel-indicators                       ← <div>, sibling of .z-carousel-track
.z-carousel-indicator / -indicator-active    ← <button data-index="N">
.z-carousel-indicator::before                ← the visible dot (button itself is the larger hit area)
.z-carousel-status                           ← sr-only <span>, always present, inert without EE za11y
```

## Cross-cutting features

### Component Theme Variables
ctv: shipped
ctv-knobs: --zk-carousel-radius, --zk-carousel-arrow-bg, --zk-carousel-arrow-bg-hover,
  --zk-carousel-arrow-fg, --zk-carousel-indicator-bg, --zk-carousel-indicator-ring,
  --zk-carousel-indicator-active-bg, --zk-carousel-indicator-active-ring, --zk-carousel-label-bg, --zk-carousel-label-fg
ctv-probe: { knob: --zk-carousel-arrow-fg, property: color, value: rgb(255, 0, 0) }

Ten knobs covering every color role the Design Contract identifies: one shape knob
(`-radius`, shared by the root frame **and** the caption chip — both are "containers" under
DESIGN.md §5's container/control rule, so one knob moves both together; see c1/c27) and
nine color knobs split three ways by overlay region (arrow / indicator /
label), each region pairing a "dark voice" (defaults to `--zk-color-scrim`) with a "light
voice" (defaults to `--zk-color-inverse-on-surface` — **corrected** from
`--zk-color-on-primary`, which is brand-coupled; see Design Contract), except the active
indicator pill (`-indicator-active-bg` / `-indicator-active-ring`, primary / on-primary) and
`-arrow-bg-hover`, which is the one literal (non-token-rooted) default, matching the
existing `--zk-grid-row-hover-bg`-style hover-literal precedent. The indicator has four
knobs, one per paint: `-indicator-bg` (resting fill), `-indicator-ring` (resting ring),
`-indicator-active-bg` (active pill fill, primary) and `-indicator-active-ring` (active ring,
on-primary); the active pair is the one place the overlay uses brand tokens.
Hover/focus/disabled opacities and the
chevron/dot geometry itself stay on base tokens or literal geometry, not knob-driven (same
convention as button/input/rating) — the one exception is the indicator's hover cue
(`transform: scale(1.25)`, c30), which is geometry rather than a color/opacity token
specifically *because* it must work identically against either of the two fill knobs (see
Design Contract "Indicator hover").

### Density
density: bound (arrows only) — indicators are an accessibility-floor exception, see below.
density-tokens: --zk-carousel-arrow-size (NEW alias to add to `tokens/_sizing.css`,
  `--zk-carousel-arrow-size: var(--zk-control-height-sm);` — 32px at the default/comfortable
  tier, following the same "component-type → ladder rung" pattern as `--zk-fab-size` →
  `--zk-control-height-xl`)

The arrow button has a genuine intrinsic control height (a fixed circular hit target, not
sized by its text content, the same distinguishing test the Density spec uses elsewhere) —
so per the checklist's own rule it does not qualify for the "no intrinsic control-height"
N/A carve-out that, e.g., breadcrumb's link/ellipsis or `a` use. `--zk-control-height-sm`
(32px) is the alias to use, not `-xs` (28px): the contract's own `closest-sibling` citation
names `paging` as the precedent for the arrow pair, and paging's own established alias in
this exact token system is `--zk-paging-control-size: var(--zk-control-height-sm)`
(`_sizing.css`'s comment for `-sm` is literally "paging controls, window-close" — `-xs` is
commented "sm buttons, close buttons, separators," not navigation arrows). **Corrected**
from an earlier pass that picked `-xs` for closest raw-pixel proximity to ZK stock's fixed
36px — that optimizes for legacy-px proximity over the sibling-token coherence this tier's
methodology is supposed to use, and 32px is in any case closer to 36px than 28px is.
Landing on the ladder (rather than an off-ladder 36px literal) means a whole-app compact
override reaches the carousel arrows too, the same way it already reaches every other
icon-only control.

**Indicators are the one deliberate exception.** The 24×24px indicator hit area is not a
density/comfort choice to begin with — it is WCAG 2.1 SC 2.5.8's *minimum* target size, a
accessibility floor. Binding it to the compact-mode ladder would let a customer's
`data-density="compact"` override shrink it below that floor, which the theme must never
allow for any control (the same reasoning `--zk-touch-target-min` in the tablet layer is
never subject to a compact override, just the opposite direction). `--zk-carousel-indicator`
sizing therefore stays a fixed literal (c16/c17 in Expected values), not a ladder-bound
alias, and is exempt from density scaling in both directions.

### Forced colors
fc-risk: [background-affordance, selection]
fc-guards: needed —
  `.z-carousel-arrow { border: 1px solid ButtonText; }` (and a disabled pairing,
  `.z-carousel-arrow[disabled] { color: GrayText; border-color: GrayText; }`, mirroring the
  existing `.z-button`/`.z-button[disabled]` block "(3)" in `tokens/_forced-colors.css`);
  `.z-carousel-indicator::before { background-color: GrayText; border-color: GrayText; }`
  plus `.z-carousel-indicator-active::before { background-color: Highlight; border-color:
  Highlight; }` (mirroring the existing slider track/fill block "(4)" in the same file).

Two distinct risks, deliberately **not** including `mask-glyph` or `box-shadow-focus` —
see the Design Contract's chevron-technique reasoning for why those two candidate risks
were ruled out (the border-technique glyph rides safely on forced-colors' `color` remap,
and focus is already `outline`-based, not `box-shadow`-based, so it force-maps cleanly via
the existing global `--zk-focus-ring` override):

- **`background-affordance`** — the arrow button has `border: 0`; its entire circular
  boundary against an arbitrary (and, under forced-colors, always-`Canvas`) page background
  is carried by `background-color` alone. Once forced-colors force-maps that background to
  `Canvas`, the arrow becomes an invisible hit target with only the (still-visible, per the
  chevron reasoning above) chevron floating with no boundary around it. This is
  structurally identical to the existing "(3) Buttons have no border" fix already applied
  to `.z-button` — carousel's arrow needs the same treatment under its own selector, since
  it is not itself `.z-button`.
- **`selection`** — the indicator's active/resting distinction is carried *only* by which
  of two fills is currently applied (plus the pill shape of the active one; the fills still
  need to differ) — the same
  pattern the existing guard already fixes for listbox/tree/paging/navitem rows (block
  "(2a)") and for slider's track/fill (block "(4)"). Both `::before` fill and its paired
  ring color must be repointed or the active pill becomes indistinguishable from a resting
  dot once both force-map toward the same neutral.

**Why `.z-carouselitem-label` is deliberately not a third risk.** The caption chip also
paints an unbordered `background-color`-only shape atop arbitrary imagery — structurally
the same category as the two risks above — but it does not need a guard: it is
non-interactive (no click/keyboard affordance depends on it staying visible), and unlike
the indicator's fill-only *state distinction*, the label carries its information as literal
text, not as a color/fill difference. Once forced-colors force-maps its `background-color`
and `color` to a system pair, the *pairing itself* (whatever it resolves to) stays
self-consistently legible — text-on-its-own-background contrast is preserved by
construction, the same reasoning that already excludes ordinary text surfaces (e.g. a
tooltip body) from needing a guard elsewhere in `_forced-colors.css`. If a future revision
made the label interactive (e.g. clickable for a slide detail view), this exemption would
need to be revisited.

### Brand override
brand-allowed-literals: `rgba(0, 0, 0, 0.65)` — `--zk-carousel-arrow-bg-hover`'s default,
  reason: matches the established `--zk-grid-row-hover-bg` / `--zk-combobox-item-hover-bg`
  literal-hover-color precedent (a hand-picked darkening of the scrim token with no
  existing token to derive from); it is a **knob default**, so a brand/region override can
  still replace it outright — this is not an un-overridable hardcoded value, merely a
  non-token-rooted *default*.

Every other design-bearing value in the Expected-values table is either a `var(--zk-*)`
token or an explicitly-literal non-color value (layout keywords, geometry, the WCAG-floor
indicator hit-area size).

### Tablet
tablet: needs-specific — both the arrows and the indicators are genuinely small discrete
tap targets that sit directly atop swipeable, single-purpose media content (unlike, e.g.,
`breadcrumb`'s inline text items, which have Tab-order fallbacks). The tablet bundle
(`zkmax/css/tablet/_buttons.css` precedent — icon-only controls grow to `--zk-touch-target-min`,
44px, via padding around an unchanged visible glyph, not by resizing the glyph itself)
should extend both `.z-carousel-arrow` (currently `--zk-carousel-arrow-size`, 32px at rest)
and `.z-carousel-indicator` (currently a fixed 24px, already at the WCAG *minimum* but
below MD3's *comfortable* touch tier) up to `--zk-touch-target-min` on a touch UA, in both
cases via padding/box size around the unchanged visible chevron/dot mark — matching the
identical growth pattern already applied to every other small icon-only control in the
tablet bundle (and the same precedent breadcrumb's ellipsis button cites). The underlying
pointer-drag swipe gesture (`Carousel.ts`'s Pointer Events handling) is unaffected either
way — it works identically at any tap-target size.

## States to evaluate
- [ ] default (root frame, clipped + rounded)
- [ ] slide-effect track transition
- [ ] fade-effect item transition
- [ ] active slide (fills frame, unique)
- [ ] arrow default
- [ ] arrow hover
- [ ] arrow focus-visible
- [ ] arrow disabled (loop=false boundary)
- [ ] arrow glyph rendered (both directions)
- [ ] arrows docked (no overlap with indicators, vertically centered)
- [ ] indicators row (aligned, ordered)
- [ ] indicator default (resting)
- [ ] indicator hover (resting dot and active dot both scale)
- [ ] indicator active
- [ ] indicator focus-visible
- [ ] caption label present (in-frame, typography + chip radius)
- [ ] single-slide instance (arrows/indicators structurally absent)
- [ ] vertical orientation
- [ ] effect="none" track (instant jump, no transition)
