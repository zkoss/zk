# Component: coachmark (theme design)
tier: T2
category: feedback
preview: ${PREVIEW_URL}/coachmark.zul
rules: see .claude/skills/zk-component-rules/components/coachmark.md
contract-approved: true  # close-affordance redesign approved 2026-07-15 (neutral surface approved earlier same day)
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/src/main/resources/web/js/zkmax/nav/Coachmark.ts
  - zkmax/src/main/resources/web/js/zkmax/nav/mold/coachmark.js
js-source-hash: 6673edf25b28d7ea66babb6c1b873cf9b1a96c0b3791dac8ecedac5e059f125e
closest-sibling: none — novel guided-tour card pattern; MD3-mapped to rich tooltip (neutral surface + text actions)
mockup-needed: Y
mockup-rationale: ZKDoc has Coachmark-4.png; Marble aligns the card to the MD3 rich-tooltip pattern — a neutral surface-container card so nested action widgets render with their standard styling. Mockup pins the neutral-surface design intent (prominence comes from elevation above the scrim, not a brand fill).

## References
- MUI CSS: no direct analog — MUI has Tooltip.css (bare tooltip) and Popover.css but neither has
  a guided-tour card with pointer arrow + mask + close button. MUI tour/popover content sits on
  `background.paper` (neutral), which matches the neutral-surface choice below.
- MD3 pattern: **rich tooltip** — an elevated card carrying text + one or more action buttons on a
  neutral `surface-container`, with the action rendered as a standard `primary` text button. MD3
  reserves saturated brand fills for single-purpose, non-composable attention surfaces (Snackbar →
  `inverse-surface` + one `inverse-primary` action); a coachmark hosts arbitrary widgets so it belongs
  in the neutral rich-tooltip family, not the snackbar family.
- doc/spec/DESIGN.md sections: "Surface Palette" and "Text Colors" (colors), "Corner Radii" (card radius), "Elevation / Shadow", "Spacing Scale (4dp baseline)", "Motion"
- ZKDoc canonical: https://github.com/zkoss/zkdoc/blob/master/zk_component_ref/images/Coachmark-4.png
- Iceblue baseline: doc/contracts/baselines/coachmark-iceblue.png
- HTML contract: doc/contracts/coachmark.html

## Design Contract

Coachmark is an MD3 **rich-tooltip** guided-tour card on a **neutral surface**. The visual card
element (`.z-coachmark-content`) uses `--zk-coachmark-bg` (→ `--zk-color-surface-container-low`) as its background
and `--zk-coachmark-fg` (→ `--zk-color-on-surface`) as its text color. Prominence above the mask comes from **elevation +
shadow on the dark scrim** — the same mechanism every MD3 dialog relies on — not from a brand fill.
The decisive benefit of the neutral surface: **nested action widgets render with their STANDARD
theme styling** (a `.z-button` keeps its `--zk-color-primary` fill, a `.z-combobutton` keeps both
halves), so the theme needs **no per-widget colour inversion** and any future slotted control works
without a bespoke rule. Corner radius is `--zk-coachmark-radius` (→ `--zk-shape-card`), consistent with all card/alert
surfaces in Marble. Elevation is `--zk-elevation-2` (dropdown level) since the card floats above the
page but is not a modal dialog. Padding is `--zk-spacing-4` for comfortable reading. The
pointer arrow triangle inherits the same neutral background color via `border-color` so it reads as
an extension of the card. The close button (`×`) is a **circular MD3 icon-button affordance**
(`--zk-control-height-xs`, `--zk-shape-corner-full`), `--zk-color-on-surface-variant` glyph,
with an on-surface `::before` **state layer** on hover and a `--zk-focus-ring` on `:focus-visible`
(it is keyboard-focusable via `tabindex=0`). It is pinned at a **symmetric `--zk-spacing-2` inset** inside the
content's top-right corner in every pointer direction: the close is absolutely positioned against
the `.z-coachmark` root, and ZK injects 20px of padding on the pointer side of the root
(`Coachmark.ts _fixPadding`: `10 + borderWidth/2`, pointer border hardcoded 10px), so the
pointer-side offset adds exactly that 20px (`--zk-spacing-5`) to keep the inset even — a plain
`+16px` bump lands 4px short and reads as crowded. The full-page mask dims the content beneath,
guiding attention to the target. It is a `zk.eff.FullMask` node (`z-modal-mask z-coachmark-mask`; `Coachmark.ts` adds the second
class). Marble paints it with `--zk-color-scrim` through the shared `.z-modal-mask` rule in
`misc.css`. The mask must use the system scrim token for theme-wide consistency.
Entrance animation: the card uses the ZK default `expand` keyframe animation
(scale 0 → 1 + opacity 0 → 1). The duration, delay, and easing come from ZK's
built-in coachmark JS, not from `--zk-motion-*` tokens. Reduced motion is handled by the
global rule in `tokens/_motion.css`.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-coachmark-content` has `background-color ≠ transparent AND background-color ≠ var(--zk-color-primary)` — a **light neutral surface** (all RGB channels ≥ 200), NOT the primary brand fill; it reads as a distinct card via its elevation shadow on the scrim | visual identity — "reads as a rich-tooltip card, prominence from elevation not a brand fill" |
| M2 | `.z-coachmark-content` bbox `height ≥ 40px AND width ≥ 120px` when containing at least one `.z-label` child | card must have minimum legible body size |
| M3 | `.z-coachmark-close` bbox is non-zero (`width ≥ 12px AND height ≥ 12px`) AND **`width ≤ 32px`** (it is a corner affordance, NOT a full-width row) AND `bbox.right ≤ (coachmark-content.bbox.right + 4px)` AND **`bbox.top ≤ (coachmark-content.bbox.top + 32px)`** — close button is visible and pinned at the TOP-right of the card (not flowed below the content) | close affordance must be reachable at the top-right corner; a static full-width close collapsed to the card bottom must FAIL |
| M4 | `.z-coachmark-pointer` bbox is non-zero (`width ≥ 10px AND height ≥ 10px`) when `.z-coachmark-open` is present | pointer arrow must be visible when open |
| M7 | `.z-coachmark-pointer` computed `position === 'absolute'` — the mold JS (`_fixarrow`) writes inline `top`/`left` on the pointer to align the triangle with the target; with `position:static` those coordinates are ignored and the triangle collapses to the card's left edge (does NOT point at the target) | pointer must actually point at the target |
| M5 | `.z-coachmark.z-coachmark-open` has `opacity > 0` (animation fill-mode forwards preserves final state) | card must not remain invisible after opening |
| M6 | Text nodes inside `.z-coachmark-content` have WCAG contrast ≥ 4.5:1 against the `background-color` of `.z-coachmark-content` | legibility on the neutral surface (`on-surface` on `surface-container-low`) |
| M8 | Every filled action control inside `.z-coachmark-content` — `.z-button` AND each half of a `.z-combobutton` (`.z-combobutton-content`, `.z-combobutton-button`) — has `background-color` with WCAG contrast ≥ 3:1 against the card's `background-color` AND is **NOT inverted to the card's own surface color** (its fill ≈ the standard global `--zk-color-primary`, not the card background) | on the neutral surface the STANDARD filled control already contrasts, so no inversion rule is needed; this guard proves the neutral choice works AND catches a regression where the card is re-colored without handling nested widgets (the old primary-on-primary vanish) |
| M9 | `.z-coachmark-close` is a **circular icon-button target**: `width ≥ 24px AND height ≥ 24px AND border-radius ≥ width/2`, pinned at a **symmetric** corner inset (`\|topInset − rightInset\| ≤ 1.5px`, where `topInset = close.top − content.top` and `rightInset = content.right − close.right`, both ≈ 8px) in EVERY pointer direction, and it exposes a state layer that fades in on `:hover` (`::before` opacity 0 → > 0) | the close must be a real affordance, not a bare glyph, and must sit evenly in the corner — the pointer-side offset compensates for the 20px ZK injects, not a guess (`+16px` left it 4px asymmetric). Catches the "feels-off" corner-crowding regression numerically |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-coachmark-content` | background-color | `var(--zk-coachmark-bg)` (→ `--zk-color-surface-container-low`) | doc/spec/DESIGN.md "Surface Palette" — neutral rich-tooltip surface; prominence from elevation on the scrim, not a brand fill |
| c2 | `.z-coachmark-content` | color | `var(--zk-coachmark-fg)` (→ `--zk-color-on-surface`) | doc/spec/DESIGN.md "Text Colors" — on-surface text on the neutral card |
| c3 | `.z-coachmark-content` | border-radius | `var(--zk-coachmark-radius)` (→ `--zk-shape-card`) | doc/spec/DESIGN.md "Corner Radii" — card radius for container surfaces |
| c4 | `.z-coachmark-content` | box-shadow | `var(--zk-coachmark-elevation)` (→ `--zk-elevation-2`) | doc/spec/DESIGN.md "Elevation / Shadow" — dropdown/hover elevation for floating card |
| c5 | `.z-coachmark-content` | padding | `var(--zk-spacing-4) calc(var(--zk-spacing-2) + var(--zk-control-height-xs) + var(--zk-spacing-2)) var(--zk-spacing-4) var(--zk-spacing-4)` (right padding is the `calc(...)` term) | doc/spec/DESIGN.md "Spacing Scale (4dp baseline)" — comfortable body padding; right padding reserves the inset + the close circle + a gap before the text (mirrors ZK default's asymmetric `@coachmarkPaddingRight`) |
| c6 | `.z-coachmark-pointer.z-coachmark-up` | border-bottom-color | `var(--zk-coachmark-bg)` | pointer triangle must match card background |
| c7 | `.z-coachmark-pointer.z-coachmark-down` | border-top-color | `var(--zk-coachmark-bg)` | pointer triangle must match card background |
| c8 | `.z-coachmark-pointer.z-coachmark-left` | border-right-color | `var(--zk-coachmark-bg)` | pointer triangle must match card background |
| c9 | `.z-coachmark-pointer.z-coachmark-right` | border-left-color | `var(--zk-coachmark-bg)` | pointer triangle must match card background |
| c10 | `.z-coachmark-close` | color | `var(--zk-color-on-surface-variant)` | close icon must be legible on the neutral surface |
| c11 | `.z-coachmark-close` | cursor | `pointer` | interaction contract — close is clickable |
| c19 | `.z-coachmark-close` | width, height | `var(--zk-control-height-xs)` | a real icon-button hit target, not a bare glyph (the token doc's documented "close button" size) |
| c20 | `.z-coachmark-close` | border-radius | `var(--zk-shape-corner-full)` (renders as a full circle) | circular MD3 icon-button affordance |
| c21 | `.z-coachmark-close::before` | background-color, opacity | `var(--zk-color-on-surface)` at `opacity: 0` (rest) → `var(--zk-state-hover-opacity)` on `:hover`, `var(--zk-state-focus-opacity)` on `:focus-visible` | MD3 state layer — an on-surface overlay that reads on the neutral card (an opaque container swap would not, the card is already a surface-container tone) |
| c22 | `.z-coachmark-close:focus-visible` | outline | `var(--zk-focus-ring)`, `outline-offset: -2px` | keyboard focus indicator — the close is `tabindex=0` and had none before |
| c12 | `.z-coachmark-mask` | background-color | `var(--zk-color-scrim)` | shared `.z-modal-mask` rule, `misc.css` — the mask must use the system scrim token for theme-wide consistency |
| c13 | `.z-coachmark` | position | `absolute` | structural — JS-positioned wrapper |
| c14 | `.z-coachmark` | opacity | `0` (when NOT `.z-coachmark-open`) | closed state must be invisible |
| c15 | `.z-coachmark-pointer` | position | `absolute` | REQUIRED so the mold JS's inline `top`/`left` apply — otherwise the triangle does not align to the target |
| c16 | `.z-coachmark-close` | position | `absolute` | close is pinned to the card's top-right corner relative to the `.z-coachmark` root; static positioning flows it to the card bottom |
| c17 | `.z-coachmark-content .z-button` | background-color | `var(--zk-color-primary)` | nested button uses the STANDARD filled style — NO inversion; the neutral card makes the primary fill contrast naturally (see M8) |
| c18 | `.z-coachmark-content .z-combobutton-content`, `.z-coachmark-content .z-combobutton-button` | background-color | `var(--zk-color-primary)` | nested combobutton uses the STANDARD filled style on both halves — NO inversion (see M8) |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| open (default on page load) | `.z-coachmark.z-coachmark-open` | c1, c2, c3, c4, c5, c10, c11, c15, c16, c17, c19, c20, c22, M8, M9 |
| nested-button | `.z-coachmark-content .z-button` | c17, M8 |
| nested-combobutton | `.z-coachmark-content .z-combobutton` | c18, M8 |
| closed | `.z-coachmark` (without `.z-coachmark-open`) | c14 (opacity = 0) |
| pointer-up | `.z-coachmark-pointer.z-coachmark-up` | c6, c15 |
| pointer-down | `.z-coachmark-pointer.z-coachmark-down` | c7, c15 |
| pointer-left | `.z-coachmark-pointer.z-coachmark-left` | c8, c15 |
| pointer-right | `.z-coachmark-pointer.z-coachmark-right` | c9, c15 |
| mask-visible | `.z-coachmark-mask` | c12 |
| close-hover | `.z-coachmark-close:hover` | c21 (state layer `::before` opacity 0 → 0.08); cursor: pointer |

## States to evaluate
- [ ] open (with .z-coachmark-open)
- [ ] nested-button (.z-button inside .z-coachmark-content — must contrast with the card)
- [ ] nested-combobutton (.z-combobutton inside .z-coachmark-content — both halves must contrast)
- [ ] closed (without .z-coachmark-open)
- [ ] pointer-up
- [ ] pointer-down
- [ ] pointer-left
- [ ] pointer-right
- [ ] mask visible (`z-modal-mask` dim overlay, `--zk-color-scrim`)
- [ ] close-hover (state layer `::before` fades in)
- [ ] close-focus (focus-visible ring)
