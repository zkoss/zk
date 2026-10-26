# Component: tabbox (theme design)
tier: T1
category: navigation
preview: ${PREVIEW_URL}/tabbox.zul
rules: see .claude/skills/zk-component-rules/components/tabbox.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - js/zul/tab/Tabbox.ts
  - js/zul/tab/Tabs.ts
  - js/zul/tab/Tab.ts
  - js/zul/tab/Tabpanel.ts
  - js/zul/tab/Tabpanels.ts
  - js/zul/tab/mold/tabbox.js
js-source-hash: b8c3e13387160ac1e1200238b77c3576e1e6090d20554b24085f883b041fdef6
closest-sibling: none — distinct chrome+payload composition; sibling reuse limited

## References
- MUI CSS: `/Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/Tabs.css`
- DESIGN.md sections: §1 (color), §5 (spacing), §7 (state layers), §11 (focus)
- Iceblue baseline: `doc/contracts/baselines/tabbox-iceblue.png`
- HTML contract: `doc/contracts/tabbox.html`

## Design Contract

Tabs use a borderless strip with an underline indicator on the active tab (`top`/`bottom` orients) or a side-bar indicator on the active tab (`left`/`right` orients). Inactive tabs use on-surface-variant text colour and tint with the primary state-layer on hover. The active tab and its 2px indicator use `--zk-color-primary`. The strip carries a 1px outline-variant divider along its content edge.

Closable tabs place the close button **trailing the label** with a reduced trailing inset — closable geometry is **16/4/8** (leading inset / label–icon gap / trailing inset), per the dismiss-affordance placement convention (`doc/spec/md3-close-affordance-placement.md`; user ruling 2026-06-05, mirrors goldenlayout close-7). ZK emits `.z-tab-button` *before* `.z-tab-text` in the DOM (iceblue repositions it absolutely; Marble flips it with flex `order`), so the trailing position must be asserted, not assumed — the pre-2026-06-05 theme shipped the × on the *leading* side (`× Tab1`) because nothing checked the button's side. In `left`/`right`/`vertical` orients the tab bar is wider than the label, so the × additionally hugs the bar's trailing edge (`margin-left: auto`). In accordion mold a closable header shows a 32px (24px in compact) × button **instead of the chevron** (owner ruling 2026-10-25, supersedes the 2026-06-07 "never shown" ruling): WCAG 2.5.8 needs a ≥ 24×24 close target, and Tab.ts binds the close click on the `<i>` inside the button, so a header without one has no pointer way to close it. The × is centred on the chevron's keyline (16px from the header edge); non-closable headers keep the chevron (see c11a, c35 and `doc/spec/md3-close-affordance-placement.md`). The close target is the `<i id=…-cls>` (not the 16px wrapper): in strip molds it is a 24×24 box centred on the wrapper, taken out of flow so the 16/4/8 geometry is unchanged (c36).

Scroll buttons in strip mode are flat, square-ish hit-targets that sit at the leading and trailing edges of the strip. They display only the chevron glyph (no border, no background) at `--zk-color-on-surface-variant`, picking up the primary state-layer tint on hover (matching the inactive-tab hover treatment). They are always present in the DOM in strip modes but are visually hidden until ZK adds `.z-tabbox-scroll` to the root. When the strip has reached an end-of-scroll position there is no class to react to, so the buttons remain at their full default appearance and clicks are silent no-ops (acceptable trade-off — ZK does not surface this state).

Accordion mold uses a flat bordered container (no border-radius — children span the container's full width with square corners; rounding the parent leaves visible sharp corners and the `overflow: hidden` workaround is incompatible with ZK's slideDown animation). Each header is a single-line row; the selected header tints to `--zk-color-primary-container` with `--zk-color-on-primary-container` text. Section dividers between headers use the same 1px outline-variant rule used elsewhere in the theme.

Icon-only tabs (`iconSclass` set, no `label`) render the icon centred in the tab cell at the same height as text tabs.

## Expected values

### Strip mold — c1–c8

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-tab-content` | padding | `var(--zk-spacing-3) var(--zk-spacing-4)` (12px 16px) | DESIGN.md §5 |
| c2 | `.z-tab-content` | font-size | `var(--zk-typescale-label-large-size)` (14px) | DESIGN.md §1 |
| c3 | `.z-tab-content` | font-weight | `var(--zk-font-weight-medium)` (500) | DESIGN.md §1 |
| c4 | `.z-tab` | min-height | 48px | MUI Tabs.css |
| c5 | `.z-tab.z-tab-selected` | color | `var(--zk-color-primary)` | DESIGN.md §1 |
| c6 | `.z-tab.z-tab-selected` | border-bottom (top orient) | `2px solid var(--zk-color-primary)` | DESIGN.md §1 |
| c7 | `.z-tabbox-top > .z-tabs` | border-bottom | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §1 |
| c8 | `.z-tab:hover` | background-color | `--zk-color-primary` @ `var(--zk-state-hover-opacity)` (state-layer tint) | DESIGN.md §7 |

### Accordion mold — c9–c17

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c9  | `.z-tabbox-accordion` | border | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §1 |
| c10 | `.z-tabbox-accordion` | border-radius | `0` (see c22k — flat container; do NOT use `--zk-shape-corner-medium` here) | DESIGN.md §1 + skill — Layout invariants |
| c11 | `.z-tabbox-accordion .z-tab` | min-height | 52px | DESIGN.md §5 |
| c11a | `.z-tabbox-accordion .z-tab-button` | width / height / order / margin-right | `max(24px, min(32px, --zk-tab-height − 16px))` (32px at 48px, 24px at compact 40px) / same / `2` / half the size, negative (−16px / −12px) — a closable accordion header shows the × (replaces the 2026-06-07 `display: none`, retired by owner ruling 2026-10-25). Centre on the chevron keyline (16px from the header edge); the circle stays inside the header so it is not clipped by `.z-tab { overflow: hidden }`, and the header keeps `--zk-tab-height` like a non-closable one. 40px would stick out 4px. Applies only to the accordion's own header (`.z-tabbox-accordion > .z-tabpanels > .z-tabpanel > .z-tab > .z-tab-content > .z-tab-button`, same chain for c35 and the selected colour); a tabbox nested in a panel keeps its own rules | owner ruling 2026-10-25, supersedes user ruling 2026-06-07 |
| c12 | `.z-tabbox-accordion .z-tab` | background-color | `var(--zk-color-surface)` | DESIGN.md §1 |
| c13 | `.z-tabbox-accordion .z-tab` | color | `var(--zk-color-on-surface)` | DESIGN.md §1 |
| c14 | `.z-tabbox-accordion .z-tab.z-tab-selected` | background-color | `var(--zk-color-primary-container)` | DESIGN.md §1 |
| c15 | `.z-tabbox-accordion .z-tab.z-tab-selected` | color | `var(--zk-color-on-primary-container)` | DESIGN.md §1 |
| c16 | `.z-tabbox-accordion .z-tabpanel-content` | padding | `var(--zk-spacing-4)` (16px) | DESIGN.md §5 |
| c17 | `.z-tabbox-accordion .z-tabpanel + .z-tabpanel` | border-top | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §1 |

**Nested isolation (owner ruling 2026-10-25, principle V6 same role same look).** The accordion selectors in c11–c17, c22h, c22i, c35 and c11a are shorthand: in `tabbox.css` each is a child-combinator chain from the accordion root (`.z-tabbox-accordion > .z-tabpanels > .z-tabpanel > .z-tab > .z-tab-content …`). A strip tabbox nested in an accordion panel therefore looks and measures exactly like the same tabbox outside it, and an accordion nested in anything equals an un-nested accordion. Do not write accordion rules as descendant selectors.

### Orientation indicators — c18–c21

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c18 | `.z-tabbox-bottom .z-tab.z-tab-selected` | border-top | `2px solid var(--zk-color-primary)` (indicator on top edge) | DESIGN.md §1 |
| c19 | `.z-tabbox-left .z-tab.z-tab-selected` | border-right | `2px solid var(--zk-color-primary)` (indicator on right edge) | DESIGN.md §1 |
| c20 | `.z-tabbox-right .z-tab.z-tab-selected` | border-left | `2px solid var(--zk-color-primary)` (indicator on left edge) | DESIGN.md §1 |
| c21 | `.z-tabbox-left > .z-tabs`, `.z-tabbox-right > .z-tabs` | border-bottom | `none` (the strip divider is on the inner edge, supplied by c22-row vertical separators) | DESIGN.md §1 |

### Scroll buttons — c22–c30 (re-spec — replaces removed "hide all 4" rule)

The scroll buttons are visible whenever ZK adds `.z-tabbox-scroll` to the root. They are rendered as glyph-only hit targets — no border, no fill — and tint with the primary state-layer on hover, matching inactive-tab hover.

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c22 | `.z-tabbox-scroll > .z-tabbox-left-scroll`, `.z-tabbox-scroll > .z-tabbox-right-scroll` | display | `flex` (centred glyph) | this contract |
| c22a | `.z-tabbox-left-scroll, .z-tabbox-right-scroll, .z-tabbox-up-scroll, .z-tabbox-down-scroll` | position | `absolute` | skill — Layout invariants |
| c22b | `.z-tabbox-left-scroll` `left`/`top`; `.z-tabbox-right-scroll` `right`/`top`; `.z-tabbox-up-scroll` `left`/`top`; `.z-tabbox-down-scroll` `left`/`bottom` | anchor edge | `0` (each axis) | skill — Layout invariants |
| c22c | `.z-tabbox-scroll.z-tabbox-top > .z-tabs, .z-tabbox-scroll.z-tabbox-bottom > .z-tabs` | margin | `0 40px` (room for left/right arrow overlays; margin NOT padding so ZK's inline `width` calc is not compounded) | skill — Layout invariants |
| c22d | `.z-tabbox-scroll.z-tabbox-left > .z-tabs, .z-tabbox-scroll.z-tabbox-right > .z-tabs` | margin | `40px 0` (room for up/down arrow overlays) | skill — Layout invariants |
| c22e | `.z-tabbox > .z-toolbar.z-toolbar-tabs` | position | `absolute` (top:0, right:0; `.z-tabbox-bottom` override `top:auto; bottom:0`) — required for `Tabs._scrollcheck` width math | skill — Layout invariants |
| c22f | `.z-tabbox-top, .z-tabbox-bottom, .z-tabbox-left, .z-tabbox-right` | overflow | `hidden` (clip excess on strip molds; accordion mold is EXCLUDED — its sections must grow in normal flow) | skill — Layout invariants |
| c22g | `.z-tabbox-scroll > .z-tabbox-up-scroll, .z-tabbox-scroll > .z-tabbox-down-scroll` | min-width | `120px` (matches `.z-tabs` min-width in vertical mode — ZK propagates `tabs.style.width` to scroll buttons, which can be narrower than the rendered strip if min-width isn't matched) | skill — Layout invariants |
| c22h | `.z-tabbox-accordion .z-tabpanel` | min-height | `0` (overrides base `.z-tabpanel { min-height: 80px }` so `Tabpanel._fixPanelHgh` doesn't clamp selected cave height to 0) | skill — Layout invariants |
| c22i | `.z-tabbox-accordion .z-tab-content` | padding | `0` (overrides base `.z-tab-content { padding: 12px 16px }`; outer `.z-tab` already supplies vertical padding so doubling it inflates header) | skill — Layout invariants |
| c22j | `.z-tabbox-right > .z-tabbox-up-scroll, .z-tabbox-right > .z-tabbox-down-scroll` | left, right | `left: auto; right: 0;` (overrides shared default `left: 0`; the scroll buttons MUST anchor on the tabs' side — `.z-tabbox-left`/`.z-tabbox-vertical` keep the default; `.z-tabbox-right` flips them) | skill — Layout invariants |
| c22k | `.z-tabbox-accordion` | border-radius | none (must NOT be set — children are square-cornered and parent rounding leaves visible sharp corners; MD3 expansion-panel groups are flat at the edges; `overflow: hidden` workaround is forbidden because it breaks `jq.slideDown`) | skill — Layout invariants |
| c23 | `.z-tabbox-scroll > .z-tabbox-up-scroll`, `.z-tabbox-scroll > .z-tabbox-down-scroll` | display | `flex` (centred glyph) | this contract |
| c24 | `.z-tabbox-left-scroll`, `.z-tabbox-right-scroll` | width | 40px | MUI TabScrollButton.css |
| c25 | `.z-tabbox-left-scroll`, `.z-tabbox-right-scroll` | min-height | 48px (matches `.z-tab` min-height — c4) | DESIGN.md §5 |
| c26 | `.z-tabbox-up-scroll`, `.z-tabbox-down-scroll` | height | 40px | MUI TabScrollButton.css |
| c27 | `.z-tabbox-left-scroll, .z-tabbox-right-scroll, .z-tabbox-up-scroll, .z-tabbox-down-scroll` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §1 |
| c28 | `.z-tabbox-left-scroll, .z-tabbox-right-scroll, .z-tabbox-up-scroll, .z-tabbox-down-scroll` | background | `transparent` | DESIGN.md §1 |
| c29 | scroll buttons `> i` (chevron glyph) | font-size | `var(--zk-typescale-body-medium-size)` (13px per DESIGN.md §7) | this contract |
| c30 | scroll buttons `:hover` | background-color | `--zk-color-primary` @ `var(--zk-state-hover-opacity)` (state-layer tint) | DESIGN.md §7 |

Visibility rule: when root does NOT carry `.z-tabbox-scroll`, the four directional buttons must be `display: none` regardless of whether the DOM nodes exist.

Endpoint-disabled note: ZK does not emit any "can't scroll further" class (see skill entry). The contract therefore does NOT spec a separate disabled appearance — buttons retain c27/c28 at endpoints. If the user later requests a dimmed-at-endpoint look, that requires either JS instrumentation or a CSS-only `:has()` heuristic; deferred (Q3).

### Close button — c31–c35 (added 2026-06-05, failing-first)

Executes the deferred tabbox side-flip pass (`doc/skill-gaps.md` 2026-06-05 goldenlayout+tabbox row). All rows are expected to FAIL until the same-day CSS iteration lands.

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c31 | `.z-tab-button` | order | `1` | ZK emits the button BEFORE `.z-tab-text` in the DOM (skill — DOM structure); flex `order` flips it to trailing. Dismiss affordances trail the content (`doc/spec/md3-close-affordance-placement.md`) |
| c32 | `.z-tab-button` | margin-left | `0` (label–icon gap supplied solely by `.z-tab-content { gap: var(--zk-spacing-1) }`) | closable geometry 16/4/8 — the previous `margin-left: spacing-1` was the gap *in leading position*; once trailing, it would double the gap to 8px |
| c33 | `.z-tab-content:has(> .z-tab-button)` | padding-right | `var(--zk-spacing-2)` | trailing inset = half the 16px text inset (16/4/8), mirrors GL close-7. Cascade note: accordion's c22i (`padding: 0`) has equal specificity (0,2,0) and MUST come later in the file so accordion headers stay at padding 0 |
| c34 | `.z-tabbox-left .z-tab-content`, `.z-tabbox-vertical .z-tab-content`, `.z-tabbox-right .z-tab-content` | flex | `1 1 auto` (content row spans the full bar width) | prerequisite for c34a — without it the content box shrink-wraps and `margin-left: auto` has no room to act |
| c34a | `.z-tabbox-left .z-tab-button`, `.z-tabbox-vertical .z-tab-button`, `.z-tabbox-right .z-tab-button` | margin-left | `auto` | vertical-orient bars are ≥ 120px wide — order alone leaves the × adjacent to a short label mid-bar; `auto` margin pushes it to the bar's trailing edge |
| c35 | `.z-tabbox-accordion .z-tab-content::after` | order / display | `2`; `display: none` on `.z-tab-content:has(> .z-tab-button)::after` | non-closable header: label (`flex: 1`) … chevron (`order: 2`). Closable header: label … × (c11a) — the × replaces the chevron (owner ruling 2026-10-25, supersedes the 2026-06-07 ruling under which the × was hidden and the chevron kept) |
| c36 | `.z-tab-button > .z-tab-icon` | position / width / height | `absolute` centred (`top/left: 50%`, `translate(-50%,-50%)`) / `24px` / `24px` | WCAG 2.5.8 hit area for strip molds (top/bottom/left/right/vertical). The `<i>` is the click target (Tab.ts `_doCloseClick`); out of flow so c31–c34a geometry and the 18px glyph are unchanged; 24px ends 4px inside the tab edge (8px trailing inset − 4px). In accordion the `<i>` fills the 32px (compact 24px) wrapper | owner ruling 2026-10-25 |

## Outcome assertions

Outcome-level predicates gating VERIFIED (first backfilled M-row for this pre-outcome-format contract; same protocol as `doc/contracts/goldenlayout.md` §Outcome assertions).

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | for every visible closable tab (`.z-tab` containing a `.z-tab-button`): the button's bbox is **trailing** (`button.bbox.left ≥ text.bbox.right`) — AND in strip molds (`top`/`bottom`) the trailing inset `tab.bbox.right − button.bbox.right` is `8px ±2`; in `left`/`right`/`vertical` orients the same inset measured against the tab bar is `10px ±3` (8px padding + up to 2px indicator border); in accordion mold a closable header's button is visible, 32×32 (24×24 in compact), its centre 16px (±1) from the header's right edge, and the chevron of that header is not rendered; a non-closable header keeps the chevron (c11a, c35; owner ruling 2026-10-25, supersedes user ruling 2026-06-07). In every mold the close `<i>` (`$n('cls')`) is ≥ 24×24 and inside its tab (c36, WCAG 2.5.8). | Added 2026-06-05 — pre-fix the × rendered on the *leading* side (measured: `gap label→icon = −50px`, trailing inset 50px) while c11a (visibility-only) passed; the button's *side* was never asserted in any row. Encoded failing-first per `doc/skill-feedback-loop.md` Step 1. |

## State matrix

| state | selector | checks |
|-------|----------|--------|
| strip — default | `.z-tab` | c1, c2, c3, c4 |
| strip — selected (top) | `.z-tabbox-top .z-tab.z-tab-selected` | c5, c6 |
| strip — tab bar | `.z-tabbox-top > .z-tabs` | c7 |
| strip — hover | `.z-tab:hover` | c8 |
| strip — closable | `.z-tab-content:has(> .z-tab-button)` | c31, c32, c33, M1 |
| vertical — closable | `.z-tabbox-left/.z-tabbox-vertical/.z-tabbox-right` closable tab | c34, c34a, M1 |
| accordion — container | `.z-tabbox-accordion` | c9, c10 |
| accordion — header default | `.z-tabbox-accordion .z-tab` | c11, c12, c13 |
| accordion — header selected | `.z-tabbox-accordion .z-tab.z-tab-selected` | c14, c15 |
| accordion — panel content | `.z-tabbox-accordion .z-tabpanel-content` | c16 |
| accordion — closable header | `.z-tabbox-accordion .z-tab .z-tab-button` | c11a, c35, M1 |
| close target (all molds) | `.z-tab-button > .z-tab-icon` | c36, M1 |
| accordion — section divider | `.z-tabbox-accordion .z-tabpanel + .z-tabpanel` | c17 |
| bottom — selected indicator | `.z-tabbox-bottom .z-tab.z-tab-selected` | c18 |
| left — selected indicator | `.z-tabbox-left .z-tab.z-tab-selected` | c19 |
| right — selected indicator | `.z-tabbox-right .z-tab.z-tab-selected` | c20 |
| left/right — tab strip border | `.z-tabbox-left > .z-tabs`, `.z-tabbox-right > .z-tabs` | c21 |
| scroll — horizontal visible | `.z-tabbox-scroll.z-tabbox-top` | c22, c22a, c22b, c22c, c24, c25, c27, c28, c29 |
| scroll — vertical visible | `.z-tabbox-scroll.z-tabbox-left`, `.z-tabbox-scroll.z-tabbox-right` | c23, c22a, c22b, c22d, c26, c27, c28, c29 |
| scroll — layout regression | scroll-button `offsetTop` must equal 0 (horizontal) / scroll-button must NOT introduce extra flex rows pushing `.z-tabpanels` down | layout-regression sweep (evaluator §1.6) |
| scroll — hover | scroll-button `:hover` | c30 |
| scroll — hidden (no overflow) | tabbox without `.z-tabbox-scroll` | scroll buttons `display: none` |

## States to evaluate

### Strip mold
- [ ] default (c1–c4)
- [ ] hover (c8)
- [ ] selected, top orient (c5, c6)
- [ ] disabled
- [ ] tab bar border, top orient (c7)
- [ ] focus-visible
- [ ] bottom orientation indicator (c18)
- [ ] left orientation indicator (c19, c21)
- [ ] right orientation indicator (c20, c21)
- [ ] tabscroll=false — scroll buttons must be absent from DOM and `.z-tabbox-scroll` never appears; overflow clips silently
- [ ] closable: × trails the label, 16/4/8 geometry, all orients incl. image tabs (c31–c34a, M1)

### Accordion mold
- [ ] container border + radius (c9, c10)
- [ ] header default state (c11, c12, c13)
- [ ] header selected/expanded (c14, c15)
- [ ] panel content area (c16)
- [ ] section divider (c17)
- [ ] header hover (state-layer tint on `var(--zk-color-on-surface)`)
- [ ] header disabled (opacity + cursor)
- [ ] closable: 32×32 (compact 24×24) close button visible, header height unchanged, hover circle not clipped by the header (c11a)
- [ ] closable: × replaces the chevron on the keyline; non-closable keeps the chevron (c35, M1)
- [ ] close `<i>` ≥ 24×24 and inside its tab in every mold (c36, M1)

### Scroll buttons
- [ ] horizontal scroll arrows visible when overflow + `.z-tabbox-scroll` on root (c22, c24, c25, c27, c28, c29)
- [ ] horizontal scroll arrows hidden when no overflow (root lacks `.z-tabbox-scroll`)
- [ ] vertical scroll arrows visible when overflow + `.z-tabbox-scroll` on root, left orient (c23, c26, c27, c28, c29)
- [ ] vertical scroll arrows visible when overflow + `.z-tabbox-scroll` on root, right orient (c23, c26, c27, c28, c29)
- [ ] scroll button hover state-layer (c30)
- [ ] chevron glyph renders (depends on `z-icon-chevron-{left,right,up,down}` font mapping in `_icons.css`)
- [ ] right-scroll button positions to the left of toolbar when toolbar present (ZK sets inline `style.right` — CSS must not override)

### Icon-only tabs
- [ ] horizontal: `iconSclass` icon centred in tab cell, same height as text tabs
- [ ] vertical: icon centred in narrow tab strip (e.g. `width="48px"` on `<tabs>`)
