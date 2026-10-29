# Component: goldenlayout (theme design)
tier: T3
category: layout
preview: ${PREVIEW_URL}/goldenlayout.zul
rules: see .claude/skills/zk-component-rules/components/goldenlayout.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/goldenlayout/GoldenLayout.ts
  - zkmax/goldenlayout/GoldenPanel.ts
js-source-hash: eb19cb70ef8ce069ca77fec0e7fc4310476b3f6848acd2fcb85418ae9081bd58
closest-sibling: tbeditor (T3 strategy — rewrite lib-internal DOM under wrapper scope); tabbox (tab-strip visual pattern); panel (panel-card visual pattern); splitter family (DESIGN.md §14)

## References
- MUI CSS: no analog — GoldenLayout is a dockable IDE-style multi-pane layout. No MUI equivalent. Reference MUI `Tabs.css` for the tab-strip active-indicator convention (underline on active tab) and `Divider.css` for the splitter handle. See DESIGN.md §8 for novel-component policy.
- DESIGN.md sections: §3 (color roles — surface, outline, primary), §4 (spacing), §5 (corner radii — `--zk-shape-card` for header/panel corners), §7 (typography — label-large tab labels), §8 (state layers — tab hover), §9 (motion), §10 (density — card padding), §14 (splitter family)
- Iceblue baseline: doc/contracts/baselines/goldenlayout-iceblue.png
- HTML contract: doc/contracts/goldenlayout.html (not in the repository)

## Theme-bridge decision

GoldenLayoutJS (v2.x) does NOT expose CSS custom properties. It ships structural CSS bundled in its JS (`goldenlayout.js`). ZK's iceblue theme does NOT import a GoldenLayout light/dark theme file — it overrides `lm_*` selectors directly, scoped under `.z-goldenlayout`. Marble follows the same approach:

```yaml
theme-bridge:
  strategy: direct-override    # write all lm_* rules under .z-goldenlayout scope
  available: false             # GoldenLayout 2.x has no CSS variable API
  variables: {}
wrapper-selectors:
  - .z-goldenlayout            # ZK widget root
  - .z-goldenpanel             # ZK panel content node (inside lm_content)
  - .z-goldenlayout-dragProxy  # drag ghost (at document root during drag)
  - .z-goldenlayout-dropdown   # overflow tab dropdown list
  - .z-goldenlayout-dropTargetIndicator  # drag target highlight
styled-selectors:
  # All scoped under .z-goldenlayout or .z-goldenlayout-* prefix
  - .z-goldenlayout .lm_header
  - .z-goldenlayout .lm_tab
  - .z-goldenlayout .lm_tab.lm_active
  - .z-goldenlayout .lm_tab:hover
  - .z-goldenlayout .lm_controls > li
  - .z-goldenlayout .lm_splitter
  - .z-goldenlayout .lm_splitter:hover
  - .z-goldenpanel
```

## Design Contract

> Revised 2026-06-03 to match the ZKDoc canonical image (`/Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/ZKCompRef_GoldenLayout.png`). Previous version asked for an "outer wrapper card", which contradicts ZK's default look (per-panel cards). See "Naming history" below.
>
> Revised 2026-06-04 per Gate 2 design review (findings #1–#2 — prose↔table contradictions; user ruling 2026-06-04):
> - Finding #1: prose corrected to `surface-container` header — the tonal step is intentional (table row hdr-1 and CSS were already correct).
> - Finding #2: table rows panel-3/panel-4 corrected `corner-large` → `corner-small` — uniform card radius per the prose; the `corner-large` bottom corners were residue from the removed outer-wrapper-card design (see DR-2 in `.claude/skills/marble-theme/reference/harness-decisions.md`), and their cited "DESIGN.md §6 bottom-corner" rationale does not exist in DESIGN.md.
>
> Revised 2026-06-04 (second pass) per user design-consistency rulings:
> - **Tabs adopt the Tabbox visual family** (decomposition: GL has no MD3 analog, so each part maps to its nearest ZK sibling): `label-large` typography (hdr-7 + new hdr-7b), MD3 state-layer hover replacing the border-frame hover (tab-4/tab-h2/tab-h3/tab-h4 retired/revised; new tab-sl-* rows), per-tab close-icon hover → `on-surface` (close-2). The 2026-06-04 Gate-2 rulings stand: `surface-container` strip (hdr-1) + `surface`-filled active tab (tab-a2).
> - **Panel card aligns to the Panel component standard**: corner radius `corner-small` (8px) → `--zk-shape-card` (6px, DESIGN.md §5 card standard) on panel corners, header top corners, and active-tab top corners; padding → 16/16/24 (`spacing-4/4/6`, DESIGN.md §10 card standard — the previous 20px/16px was an iceblue legacy value).
> - **Splitter adopts the unified splitter-family colors** (DESIGN.md §14): `surface-container` idle, primary hover tint, pressed tint while dragging (spl-1/2/3). ~~The `::before` dot marker stays — structural exception, `.lm_splitter` cannot host the family's actuator pill (no button element).~~ (superseded 2026-06-05, see below)
>
> Revised 2026-06-05 per user design review:
> - **Header controls adopt the panel-icon family** (user ruling: consistent size with panel/window): 28×28 buttons, `corner-full` radius, Lucide SVG mask icons at 14px — `expand` for maximize (replacing the `⤢` text glyph; M15 revised), `x` for close (replacing `×`). Hover keeps the `primary` color shift and gains a state-layer circle. New M18 pins size parity.
> - **Per-tab close icon adopts full `.z-tab-button` geometry parity** (was colors-only): 16×16 `corner-full` button, centered 12px Lucide `x` mask, hover background `color-mix(in srgb, var(--zk-color-on-surface) 12%, transparent)`; `margin-left: spacing-1` retained (tabbox parity). M14 revised from content-glyph to mask+bbox check.
> - **Splitter completes the family alignment**: the 2026-06-04 "no pill" exception was over-broad — GL has no `setBtnPos_` JS centering, so the actuator pill is drawable via `::before` (pill) + `::after` (grip dots, Lucide `ellipsis-vertical`/`ellipsis` mask at opacity 1, replacing the imperceptible opacity-.5 text dots). Resize cursors (`col-resize`/`row-resize`) added — GL's bundled CSS that normally provides them is never loaded in ZK. M12 strengthened to perceivability.
> - **Header height must equal the `min-height` that `bind_` snapshots**: rendered header was 45px (tab content 12+20+12 = 44px > border-box min-height 44; +1px border) while `config.dimensions.headerHeight` = 44 — GL's inline child heights ran 1px past the flex-shrunk `.lm_items` (overflow:hidden), clipping `.z-goldenpanel`'s bottom border. Tabs now stretch into the strip instead of driving its height (`.lm_tab` padding `0 spacing-4`, `.lm_tabs` align-self stretch). New M17; tab-2 revised.
> - **Base structural block restored** (5th gap, found during verification of the above): goldenlayout-base.css is never loaded in ZK; ZK's upstream codegen `goldenlayout.css.dsp` reproduces it verbatim, but Marble had dropped it — maximize rendered in-flow at half width (`.lm_maximised` had no `position: absolute`, and the rule additionally needs ≥(0,4,0) specificity to beat `.lm_item.lm_stack`), the drop-target indicator was a permanently visible dashed line, and `.lm_transition_indicator` sat in-flow at `<body>` level. Same root-cause family as the 2026-06-05 cropper gap ("library ships no stylesheet; upstream codegen carries the structural rules"). New rows max-2/max-3, base-1…base-7; M19/M20 added; max-1 corrected (was mislabeled "JS-driven; do not change").
>
> Revised 2026-06-06 per user design review — adjacent-surface differentiation re-rulings:
> - **Splitter bar = transparent gutter** (supersedes the §14 family `surface-container` idle fill for GL, spl-1 2026-06-04): the bar abuts `surface-container` `.lm_header` strips, so the family fill erased the boundary instead of marking it — the 8px band and the header read as one region (worst on `lm_vertical`). GL panels are self-bordered cards on the canvas; the splitter region is a card gutter. Pill + grips remain the affordance; hover/drag tints become translucent mixes over `transparent` (spl-1/2/3 corrected). New outcome M21: the splitter idle background must differ from the adjacent `.lm_header` background — the adjacent-surface-contrast check both prior rulings lacked.
> - **Active tab = full Tabbox parity** (supersedes the 2026-06-04 Gate-2 "selected page" `surface` fill, tab-a2): the fill equals `--zk-color-background` (#fff == #fff in this palette), so the active tab read as a hole punched in the strip connecting to the page, not a selected tab. MD3 primary-tab canon: selection = indicator + label color, never a fill. tab-a2 corrected to `transparent`; tab-a7/a8 (top radii on the fill) retired.

GoldenLayout renders as a **grid of bordered panels** — each `.z-goldenpanel` is its own visual card; the outer `.z-goldenlayout` is a **transparent layout container with no outer framing of its own**. Every panel = `.lm_header` tab strip on top + content area below, sharing a 1px `outline-variant` border on all four sides and `--zk-shape-card` (6px) radius applied uniformly to all four corners — the theme's card standard, matching the Panel component (DESIGN.md §5). Panel content background is `surface` (white); the `.lm_header` strip background is `surface-container` — a deliberate tonal step that differentiates the tab strip from the content area — with a single 1px `outline-variant` bottom edge separating the strip from content. Tab labels use `label-large` typography, matching the Tabbox tab family. (The earlier "active tab's `surface` fill reads as the selected page" reading was superseded 2026-06-06 — `surface` equals the page background in this palette, so the fill read as a hole, not a selection.)

Tabs (`.lm_tab`) follow the **Tabbox tab family**: no side borders, no border-frame hover. Inactive tab label color is `on-surface-variant` at `label-large` (size + weight); hover shifts the label to `on-surface` and reveals an MD3 **state layer** (a `primary`-colored `::before` overlay at `--zk-state-hover-opacity`), exactly as `.z-tab` does. Active tab (`.lm_active`) follows `.z-tab-selected` exactly (full Tabbox parity, user re-ruling 2026-06-06): label flips to `primary`, the background stays **transparent** (the strip shows through — the earlier `surface` fill is retired; it equaled the page background and read as a hole in the strip), and a **2px `primary` underline** renders via `::after` pseudo-element, mirroring the MUI Tabs active-indicator convention. The close icon `×` (`.lm_close_tab::before`) sits immediately after the label, using `on-surface-variant` at rest and `on-surface` on hover (Tabbox close-button convention).

Header right-controls are a **fixed cluster of two icon buttons** anchored to the header's right edge (within `--zk-spacing-2` of `.lm_header`'s right edge), following the **panel-icon family** (DESIGN.md §12 control-icon-button table; user ruling 2026-06-05 — GL's 44px strip is closest to panel's 48px header): each is a 28×28 box with `corner-full` radius hosting a 14px **Lucide SVG mask icon** drawn by its `::after` pseudo — `expand` for maximize (`.lm_maximise`; same icon panel renders via `z-icon-expand`; the earlier `⤢`/U+2922 text glyph is retired) and `x` for close (`.lm_controls .lm_close`; same as panel's `z-icon-times`). Both use `on-surface-variant` at rest; hover shifts the icon to `primary` and reveals a state-layer circle (`color-mix(in srgb, var(--zk-color-on-surface) 8%, transparent)` — panel's `surface-container` hover fill is invisible here because the strip itself is `surface-container`). They are visually separated from the tab strip on the left by flexible whitespace, not by a divider. They must be visible on every panel header in the default state.

Per-tab close icon: every `.lm_tab` rendered from a `closable="true"` `<goldenpanel>` carries a close button (`.lm_close_tab`) immediately after the tab label with **full `.z-tab-button` geometry parity** (tabbox convention, user ruling 2026-06-05): a 16×16 box, `corner-full` radius, `margin-left: spacing-1`, hosting a centered 12px Lucide `x` mask icon via `::before`; `on-surface-variant` at rest, `on-surface` + `color-mix(in srgb, var(--zk-color-on-surface) 12%, transparent)` background circle on hover. The icon pseudo must render a non-zero bounding box (`width ≥ 8px AND height ≥ 8px`) with a non-none mask-image — declared color alone does not satisfy this requirement. The icon must be **concentric with the button box** (M22) — and because ZK's GL fork force-writes inline `display: inline-block` on `.lm_close_tab`, the centering mechanism must not depend on the element's `display` (flex centering on the element is dead CSS; center the pseudo with `position: absolute; inset: 0; margin: auto` instead). Closable tabs reduce the trailing padding to `spacing-2` (8px) so the close button sits nearer the edge than the 16px text inset — closable geometry is **16/4/8** (leading inset / label–icon gap / trailing inset), per the dismiss-affordance placement convention (`doc/spec/md3-close-affordance-placement.md`; user ruling 2026-06-05, close-7). **Parity-anchor note**: the tabbox leading-× deviation found 2026-06-05 was corrected the same day (tabbox contract c31–c35 + M1) — `.z-tab-button` now trails the label via flex `order: 1` with the same 16/4/8 geometry, so the parity anchor and GL agree again.

Splitter handles (`.lm_splitter`) follow the **splitter family** (DESIGN.md §14) for affordance and interaction, with a GL-specific idle fill (user re-ruling 2026-06-06): the bar at rest is **transparent** — GL panels are self-bordered cards, so the splitter region reads as a card *gutter*; the family `surface-container` fill abutted same-colored `.lm_header` strips and erased the boundary it was meant to mark. The bar carries the family's **actuator pill** — drawn via `::before` (cross-axis = bar thickness 8px, long-axis 28px, `corner-full`, `outline-variant` fill) with grip dots via `::after` (8px Lucide `ellipsis-vertical`/`ellipsis` mask, `on-surface-variant`, **opacity 1**) — and `col-resize`/`row-resize` cursors (`lm_horizontal`/`lm_vertical` respectively; GL's bundled CSS that normally provides cursors is never loaded in ZK, so the theme must set them). On hover the bar tints with a translucent primary mix (`color-mix` over `transparent` at the family hover opacity), the pill fills `primary`, and the dots flip to `on-primary`. The drag-active state (`lm_dragging`) uses the same translucent mix at pressed opacity. Remaining structural exception: no collapse caret (GoldenLayout has no collapse feature). Unlike the three ZK-mold splitters, GL has no `setBtnPos_` JS centering — the pseudo-pill is centered purely in CSS.

Header height sync (structural invariant): `GoldenLayout.ts bind_` snapshots the computed `min-height` of `.lm_header` into `config.dimensions.headerHeight` and sizes every `.lm_items`/`.lm_content` inline from it. The **rendered** header height must therefore equal that `min-height` exactly — tabs stretch into the strip (`.lm_tabs` align-self stretch, `.lm_tab` zero block padding) rather than driving its height, so 44px total = 43px content + 1px border-bottom under border-box. Any delta (e.g. tab block-padding pushing content past `min-height − border`) shifts GL's inline sizing by that delta and `.lm_items { overflow: hidden }` clips the panel's bottom border (user finding 2026-06-05; see M17).

When `areas` is set (e.g. `"AAB / AAB / CCD"`), the resulting grid honors the implied flex ratios: a 2-cell-wide area is `~2×` the width of a 1-cell-wide neighbor; a 2-cell-tall area is `~2×` the height of a 1-cell-tall neighbor.

The drag-proxy tab (`.z-goldenlayout-dragProxy`) lifts the dragged tab out with `elevation-2` shadow and the `primary` underline to confirm identity during drag. The drop-target indicator (`.z-goldenlayout-dropTargetIndicator`) uses a 1px dashed `primary` border with a 10% `primary` fill (light blue tint) to mark the landing zone — matches the north / east / south / west / stack drop affordance shown in the ZKDoc reference images. The overflow dropdown (`.z-goldenlayout-dropdown`) uses `surface` background, `elevation-2` shadow, `corner-small` radius, and `surface-container-high` hover on list items.

Transitions on tab label color, splitter background, and icon color use `short2` duration with `standard` easing.

## Outcome assertions

Outcome-level predicates that gate `VERIFIED`: failing any row blocks VERIFIED even if all D-tier rows below pass. Predicates are deliberately disjunctive / tolerance-based — they assert *outcome*, not *recipe*. Row IDs use the `M` prefix (originally "macro-scale outcome"; retained as a stable identifier across all eval reports). The evaluator measures these from bounding-box geometry; see the `### 3b-outcome` step in `.claude/agents/zk-theme-evaluator.md` for the enforcement protocol.

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | every visible `.z-goldenpanel` has visible framing: (`border-width ≥ 1px` AND `border-color ≠ transparent`) OR `box-shadow ≠ none` — AND the outer `.z-goldenlayout` has **no** redundant outer card (its border-width = 0 OR border-color = transparent) | per-panel cards, not outer wrapper card — corrects the earlier "outer card" mistake by matching the ZKDoc reference image |
| M2 | `.z-goldenlayout` bbox height ≥ 300px AND ≥ 80% of its parent's available height when parent has explicit height | layout engaged — wrapper is not collapsed to header-only |
| M3 | `.z-goldenlayout > .lm_goldenlayout` bbox covers ≥ 95% of `.z-goldenlayout` content-box on both axes | GoldenLayout's root fills the wrapper, no dead space |
| M4 | within each `.lm_header`, all visible `.lm_tab` siblings have `bbox.top` range ≤ 2px AND no two tab bboxes overlap horizontally by > 1px | tab strip is a single horizontal row, no text collisions |
| M5 | for any two `.lm_item.lm_stack` siblings anywhere under `.z-goldenlayout`, their bboxes do not overlap each other by > 1px on **both** axes | panels dock (split/tile) rather than stacking at z-index — covers cross-stack overlap that within-container check missed during the pilot |
| M6 | no two visible text-bearing nodes anywhere under `.z-goldenlayout` have bboxes that overlap by > 1px on both axes | catch-all: no overlapping labels, no z-fighting text |
| M7 | for every visible `.lm_tab`, label text node has WCAG contrast ratio ≥ 4.5:1 against its computed background-color | tab labels are legible |
| M8 | every visible `<li>` element anywhere under `.z-goldenlayout` has `list-style-type: none` OR `display !== list-item` | covers `.lm_tabs > li.lm_tab`, `.lm_controls > li`, and dropdown items — the pilot found bullet markers on tab `<li>` elements that the original dropdown-only check missed |
| M9 | every visible `.lm_header` contains a `.lm_controls` cluster with ≥ 2 visible icon children (maximize + close) — **except** a stack whose panels are all `closable="false"`, where GoldenLayout JS inline-hides `.lm_close` and exactly 1 visible icon (maximize) is correct — AND the cluster's bbox.right is within `var(--zk-spacing-2)` (8px) of `.lm_header`'s content-box right edge | maximize ↗ + close × icons are present and right-anchored — matches ZKDoc reference image. Non-closable exemption added per user ruling 2026-06-04 (iter-14 M9-partial): hiding the close control on a `closable="false"` stack is correct library behavior, not a defect |
| M10 | every visible `.lm_tab.lm_active` has a generated `::after` pseudo-element whose computed `background-color` equals (or color-mix-derives from) `var(--zk-color-primary)`, AND whose `height` is `2px` ± 0.5px | active-tab underline indicator (MUI Tabs convention) is actually rendered, not just declared |
| M11 | when `areas` is set on the preview goldenlayout (`AAB / AAB / CCD`), the bbox width of panel-A's stack is `≥ 1.6× AND ≤ 2.4×` panel-B's stack width AND the AB-row height is `≥ 1.6× AND ≤ 2.4×` the CD-row height | the areas-grid attribute is honored — flex ratios from area counts produce the documented layout |
| M12 | every visible `.lm_splitter` has bbox.width > 0 AND bbox.height > 0 (not collapsed) AND its `::before` renders a non-zero-bbox actuator pill whose computed `background-color` equals `var(--zk-color-outline-variant)` (≠ the bar's own background) AND its `::after` renders grip dots with non-none `mask-image` at computed `opacity` ≥ 0.99 AND the bar's computed `cursor` is `col-resize` (`.lm_horizontal`) / `row-resize` (`.lm_vertical`) | strengthened 2026-06-05 (user finding: dots at opacity .5 were imperceptible and cursor stayed `auto` — the old "non-zero bbox" predicate passed while the affordance was invisible): the marker must be *perceivable* and the family pill + cursors must be present (DESIGN.md §14) |
| M13 | within every `.lm_header`, the `.lm_controls` cluster sits on the **same horizontal row as the tabs**: the vertical **midpoint** of `lm_controls.bbox` is within `±4px` of the vertical midpoint of the first visible `.lm_tab`'s bbox, AND `.lm_header.bbox.height ≤ 48px` (single-row header). | Promoted from §3d AI visual finding 2026-06-03 iter-11: M9 geometry passed but icons rendered on a SECOND row below the tab strip because `.lm_header` lacked `display:flex; align-items:center`. M9 only checks X-axis right-anchoring; M13 adds the Y-axis "same row" assertion that M9 was missing. Refined 2026-06-04 (user-approved, iter-14 evaluator note): top/bottom-edge comparison is geometrically misleading for different-height elements (controls=20px vs tab=46px, both centered — edges differ by 13px while midpoints differ by 0px); midpoint comparison measures the actual intent. |
| M14 | every visible `.lm_tab` rendered from a closable panel has a `.lm_close_tab` descendant whose own bbox is `16×16 ±1px` AND whose `::before` pseudo has non-none computed `mask-image` (or `-webkit-mask-image`) AND a `getBoundingClientRect()` of `width ≥ 8px AND height ≥ 8px`. | Originally promoted from user iter-12 finding 2026-06-03 (0×0 invisible `::before` while color checks passed). Revised 2026-06-05: the `×` is now a Lucide `x` mask icon inside a `.z-tab-button`-parity 16×16 button (so the `content`-glyph predicate no longer applies — `content` is `""` with a mask); geometry parity is what gives the icon its breathing room from the title (user finding #2). |
| M15 | `.lm_controls .lm_maximise::after` has non-none computed `mask-image` whose data-URI contains the Lucide `expand` path signature (`M21 16v5h-5`), AND `.lm_controls .lm_close::after` mask-image contains the Lucide `x` path signature (`M18 6 6 18`). FAIL if either icon renders as a text glyph (non-empty `content`) or any other mask. | Originally pinned the `⤢` U+2922 text-glyph literal (iter-12, vs ZKDoc image). Revised 2026-06-05 per user consistency ruling: controls adopt the panel-icon family — the SAME Lucide icons panel renders (`z-icon-expand` → `expand`, `z-icon-times` → `x`) drawn as data-URI masks; the path-signature pin replaces the glyph-literal pin so a drift back to text glyphs (or a wrong icon) still fails. |
| M16 | every visible `.z-goldenpanel`'s bounding rect is fully contained within the `.z-goldenlayout` wrapper's content-box: `panel.bbox.bottom ≤ wrapper.bbox.bottom + 1px` AND `panel.bbox.right ≤ wrapper.bbox.right + 1px`. | Promoted from user iter-12 finding 2026-06-03: panel `border-bottom-*` was declared (D-tier panel-3/panel-4 PASSED) but the bottom edge rendered off-screen because the wrapper's effective viewport was shorter than the panel's content. Token rows measure properties on (potentially clipped) elements; M16 closes the "border declared but not visible" gap. Mirrors the new sanity-tier check `card-clipped-by-viewport` in `scripts/eval-sanity-tier.js`. |
| M17 | every visible `.z-goldenpanel` (and its `.lm_content` parent) is fully contained within its nearest `.lm_items` ancestor's bbox: `panel.bbox.bottom ≤ items.bbox.bottom + 0.5px` — AND every visible `.lm_header`'s rendered bbox height equals its computed `min-height` ± 0.5px. | Added 2026-06-05 (user finding #4): the panel's bottom border was clipped 1px by `.lm_items` `overflow:hidden` while M16 passed — M16 compares against the *wrapper* (144px taller), not the *clipping ancestor*. Root cause is the second clause: `bind_` snapshots `.lm_header`'s `min-height` into `config.dimensions.headerHeight`, so any delta between rendered height (was 45px — content-driven + border) and `min-height` (44px) desyncs every GL inline height by that delta. |
| M18 | every visible `.lm_controls > li.lm_maximise` and `li.lm_close` has a bbox of `28×28 ±1px` AND computed `border-radius` ≥ half its height (circular). | Added 2026-06-05 (user finding #1): controls were 20×20 with text glyphs while panel/window render 28×28/32×32 Lucide icon buttons; no row asserted size parity with the panel-icon family (DESIGN.md §12 control-icon-button table — GL adopts the panel size, whose 48px header is closest to GL's 44px strip). |
| M19 | at rest (no drag in progress), every GL utility node is invisible: `.z-goldenlayout-dropTargetIndicator` and `.lm_transition_indicator` (a `<body>`-level node) have `display: none` OR zero-area bbox, AND no `.lm_maximise_placeholder` is visible. | Added 2026-06-05: Marble omitted the goldenlayout-base.css structural block (ZK's codegen `goldenlayout.css.dsp` carries it verbatim; the library CSS itself is never loaded) — the drop-target indicator rendered as a permanent full-width dashed line below the layout, visible in every prior screenshot but asserted by nothing. |
| M20 | after clicking `.lm_maximise` on a stack: the `.lm_maximised` stack's bbox covers ≥ 98% of `.lm_goldenlayout`'s bbox on both axes AND every other stack's visible area is covered (z-index ≥ 40 on the maximised stack) AND the maximise control's `::after` mask-image switches to the Lucide `minimize-2` (restore) data-URI. Clicking again restores the original layout. | Added 2026-06-05: maximize was visibly broken (stack stayed a flex item at half width, siblings shifted aside) because `.lm_maximised { position: absolute }` was missing — and a (0,2,0) fix STILL fails because `.lm_item.lm_stack { position: relative }` outranks it; M-row forces interaction-level verification, not just declared properties. |
| M21 | every visible `.lm_splitter`'s computed idle `background-color` differs from the computed `background-color` of any `.lm_header` it abuts (bbox edges within 2px): equal rgba values (after resolving `transparent` against the shared backdrop) FAIL. | Added 2026-06-06 (user finding #1 round 2): bar and header were both `surface-container` — the contract *asserted* the merging colors as expected values (spl-1 + hdr-1), so every declared-property row passed while the boundary visibly vanished. Adjacent-surface contrast is an outcome only a comparison row can see. |
| M22 | for every visible `.lm_close_tab`, the rendered `::before` icon box is **concentric** with the element's own bbox: icon-box center within `±1px` of element-box center on **both** axes — measured with the element's live inline style **intact** (ZK's GL fork writes inline `display:inline-block` in the Tab constructor; the evaluator must NOT normalize/strip it before measuring). The hover state-layer circle is the element's own background, so concentricity at rest == concentricity under hover. | Added 2026-06-05 (user finding: hover circle appears but the `x` icon sits off-center). M14 asserts the icon *exists* at ≥ 8px with a mask; close-3/4 assert the 16×16 button — nothing asserted icon↔button alignment. Root cause was invisible to declared-property rows: the stylesheet's `inline-flex` centering rule was overridden by the JS-written inline `display:inline-block` (`zkmax goldenlayout/ext/goldenlayout.js` Tab ctor — a ZK addition absent from upstream GL), so the icon fell back to inline-flow placement (start-aligned + baseline-shifted). MD3 icon-button anatomy requires the glyph optically centered in (concentric with) its circular state layer. |

### Evaluation notes
- M1 inverts the previous "outer wrapper card" assumption. Existing `wrap-1`..`wrap-3` rows (border / corner-large / elevation on `.z-goldenlayout`) now contradict M1 — they should be REMOVED from the Expected values table in the next CSS iteration. The card visual moves to `.z-goldenpanel` (already covered by `panel-1`..`panel-4`; needs an additional `box-shadow: var(--zk-elevation-1)` row on `.z-goldenpanel` to match the ZKDoc image's per-panel framing).
- M2/M3 require the preview page to provide explicit height (`hflex`/`vflex` or pixel height on the parent). If the preview lacks this, M2 may report a configuration warning rather than a hard fail.
- M5 may report SKIP when the preview only renders one panel per stack.
- M8 may report SKIP when no overflow condition is reached. The current preview at `goldenlayout.zul` shows the dropdown on the second area — the test must reach it.
- M11 only applies when the preview ZUL actually sets `areas` (currently it does — see `goldenlayout.zul` with `areas="A A B / A A B / C C D"`).
- **M11 & M16 — ACCEPTED upstream library limitations (user ruling 2026-06-23, won't-fix theme-side).** Verified still failing (M11: top-row stacks render equal width, not 2:1; M16: bottom panel overshoots wrapper by ~3px). Both are produced by GoldenLayout writing inline `width%`/`height` at runtime in `zkmax/.../GoldenLayout.ts`; CSS cannot correct them without `!important` + breaking drag-resize, and no theme change can fix them. Same disposition as M9-partial. These rows stay documented but are **not theme defects** — do not re-investigate them as CSS bugs.
- M12 was strengthened 2026-06-05 (pill + perceivable grips + cursors); M14/M15 now expect Lucide masks instead of text glyphs; M17/M18 are new. All of these are expected to FAIL until the 2026-06-05 CSS iteration lands — failing-first.
- M21 added 2026-06-06 (with corrected spl-1/2/3 + tab-a2 and retired tab-a7/a8) — expected to FAIL until the 2026-06-06 CSS iteration lands; failing-first.
- M22 added 2026-06-05 — expected to FAIL until the close-icon centering CSS lands (pre-fix probe: icon spans x 0–12 in the 16px box → center 2px left, plus baseline-driven vertical offset from the inherited 20px line-height); failing-first. Measurement: the pseudo box can be derived from computed styles only when it is absolutely positioned (`inset: 0; margin: auto` resolves to used margins of `2px`); otherwise sample the painted-pixel centroid of the mask glyph.

## Expected values

> wrap-1, wrap-2, wrap-3 removed 2026-06-03: the outer-card pattern (border / corner-large / elevation on `.z-goldenlayout`) was wrong — the wrapper is a transparent layout container. Per-panel framing moved to `.z-goldenpanel` (see panel-shadow-1, panel-top-radius-1, panel-top-radius-2 rows below). hdr-3, hdr-4 updated: corner-large → corner-small to match the panel card's top corners.

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| wrap-4 | `.z-goldenlayout` | background-color | `transparent` | revised 2026-06-03 — wrapper is a layout container, no fill |
| wrap-5 | `.z-goldenlayout` | overflow | `visible` | revised 2026-06-03 — per-panel cards handle their own clipping |
| hdr-1 | `.z-goldenlayout .lm_header` | background-color | `var(--zk-color-surface-container)` | DESIGN.md §3 (iceblue: colorBackground3 = #fff; Marble uses surface-container for visible strip) |
| hdr-2 | `.z-goldenlayout .lm_header` | border-bottom | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §3 |
| hdr-3 | `.z-goldenlayout .lm_header` | border-top-left-radius | `var(--zk-shape-card)` | revised 2026-06-04 — Panel-alignment ruling: theme card standard 6px (DESIGN.md §5); matches panel top corners |
| hdr-4 | `.z-goldenlayout .lm_header` | border-top-right-radius | `var(--zk-shape-card)` | revised 2026-06-04 — Panel-alignment ruling: theme card standard 6px (DESIGN.md §5); matches panel top corners |
| hdr-5 | `.z-goldenlayout .lm_header` | min-height | `44px` | structural — JS reads this at bind_ to set config.dimensions.headerHeight; must not change after theme is set |
| hdr-6 | `.z-goldenlayout .lm_header` | overflow | `hidden` | structural |
| hdr-7 | `.z-goldenlayout .lm_header` | font-size | `var(--zk-typescale-label-large-size)` | revised 2026-06-04 — Tabbox-alignment ruling: tab labels use label-large like `.z-tab` (DESIGN.md §7) |
| hdr-7b | `.z-goldenlayout .lm_header` | font-weight | `var(--zk-typescale-label-large-weight)` | added 2026-06-04 — Tabbox-alignment ruling (DESIGN.md §7) |
| hdr-flex-1 | `.z-goldenlayout .lm_header` | display | `flex` | structural — enables tab strip + controls to co-exist on one row (added 2026-06-03 iter-11 fix) |
| hdr-flex-2 | `.z-goldenlayout .lm_header` | flex-direction | `row` | structural — horizontal layout for tabs + controls (added 2026-06-03 iter-11 fix) |
| hdr-flex-3 | `.z-goldenlayout .lm_header` | align-items | `center` | structural — vertically centers tabs and icons in header strip (added 2026-06-03 iter-11 fix) |
| hdr-tabs-flex | `.z-goldenlayout .lm_tabs` | flex | `1 1 auto` | structural — tabs expand to fill remaining row width, pushing controls to right edge (added 2026-06-03 iter-11 fix) |
| hdr-controls-flex | `.z-goldenlayout .lm_controls` | flex | `0 0 auto` | structural — controls cluster fixed-size, right-anchored (added 2026-06-03 iter-11 fix) |
| tab-1 | `.z-goldenlayout .lm_tab` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §3 (iceblue: textColorLight = rgba(0,0,0,0.57)) |
| tab-2 | `.z-goldenlayout .lm_tab` | padding | `0 var(--zk-spacing-4)` | revised 2026-06-05 — header-height-sync ruling (M17): block padding must not drive header height past `min-height`; tabs stretch into the strip instead (was `spacing-3 spacing-4`) |
| hdr-tabs-stretch | `.z-goldenlayout .lm_tabs` | align-self | `stretch` | added 2026-06-05 — tabs fill the header's content row (43px) so the strip, not tab content, owns the height (M17) |
| tab-3 | `.z-goldenlayout .lm_tab` | cursor | `pointer` | usability |
| tab-4 | (retired 2026-06-04 — Tabbox-alignment ruling: state-layer hover replaces the border-frame; no reserved transparent border) | — | — | — |
| tab-5 | `.z-goldenlayout .lm_tab` | transition | `color var(--zk-motion-duration-short3) var(--zk-motion-easing-standard)` | revised 2026-06-04 — Tabbox-alignment ruling: same short3 timing as `.z-tab` (DESIGN.md §9) |
| tab-sl-1 | `.z-goldenlayout .lm_tab::before` | content | `''` | added 2026-06-04 — MD3 state layer, mirrors `.z-tab::before` (DESIGN.md §8) |
| tab-sl-2 | `.z-goldenlayout .lm_tab::before` | background-color | `var(--zk-color-primary)` | added 2026-06-04 — state-layer color, mirrors `.z-tab::before` (DESIGN.md §8) |
| tab-sl-3 | `.z-goldenlayout .lm_tab::before` | opacity | `0` | added 2026-06-04 — state layer invisible at rest (DESIGN.md §8) |
| tab-h1 | `.z-goldenlayout .lm_tab:hover` | color | `var(--zk-color-on-surface)` | DESIGN.md §3 (iceblue: textColorDefault = rgba(0,0,0,0.9)) |
| tab-h2 | `.z-goldenlayout .lm_tab:hover::before` | opacity | `var(--zk-state-hover-opacity)` | revised 2026-06-04 — Tabbox-alignment ruling: state-layer hover replaces border-frame (DESIGN.md §8) |
| tab-h3 | (retired 2026-06-04 — border-frame hover removed; see tab-h2) | — | — | — |
| tab-h4 | (retired 2026-06-04 — border-frame hover removed; see tab-h2) | — | — | — |
| tab-a1 | `.z-goldenlayout .lm_tab.lm_active` | color | `var(--zk-color-primary)` | DESIGN.md §3 (iceblue: colorPrimary = #0093F9 → primary) |
| tab-a2 | `.z-goldenlayout .lm_tab.lm_active` | background-color | `transparent` | corrected 2026-06-06 (was `surface` — "selected page" ruling 2026-06-04): `surface` equals `--zk-color-background` in this palette, so the fill read as a hole in the strip; full `.z-tab-selected` parity — selection = `primary` label + 2px underline, never a fill (MD3 primary-tab canon; gap log 2026-06-06) |
| tab-a3 | `.z-goldenlayout .lm_tab.lm_active::after` | display | `block` | structural |
| tab-a4 | `.z-goldenlayout .lm_tab.lm_active::after` | height | `2px` | DESIGN.md §3 (iceblue: 2px underline — keep exact; MUI Tabs uses 2px indicator) |
| tab-a5 | `.z-goldenlayout .lm_tab.lm_active::after` | background-color | `var(--zk-color-primary)` | DESIGN.md §3 |
| tab-a6 | `.z-goldenlayout .lm_tab.lm_active::after` | content | `''` | structural |
| ~~tab-a7~~ | ~~`.z-goldenlayout .lm_tab.lm_active`~~ | ~~border-top-left-radius~~ | ~~`var(--zk-shape-card)`~~ | retired 2026-06-06 — radii existed to round the `surface` fill (tab-a2); with a transparent active tab they are invisible and assert nothing |
| ~~tab-a8~~ | ~~`.z-goldenlayout .lm_tab.lm_active`~~ | ~~border-top-right-radius~~ | ~~`var(--zk-shape-card)`~~ | retired 2026-06-06 — see tab-a7 |
| close-1 | `.z-goldenlayout .lm_close_tab` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §3 |
| close-2 | `.z-goldenlayout .lm_close_tab:hover` | color | `var(--zk-color-on-surface)` | revised 2026-06-04 — Tabbox-alignment ruling: same hover as `.z-tab-button` (was `primary`, no rationale) |
| close-3 | `.z-goldenlayout .lm_close_tab` | width | `16px` | added 2026-06-05 — `.z-tab-button` geometry parity (user finding #2: bare glyph sat flush against title) |
| close-4 | `.z-goldenlayout .lm_close_tab` | height | `16px` | added 2026-06-05 — `.z-tab-button` geometry parity |
| close-5 | `.z-goldenlayout .lm_close_tab` | border-radius | `var(--zk-shape-corner-full)` | added 2026-06-05 — `.z-tab-button` geometry parity |
| close-6 | `.z-goldenlayout .lm_close_tab:hover` | background-color | `color-mix(in srgb, var(--zk-color-on-surface) 12%, transparent)` | added 2026-06-05 — `.z-tab-button` hover circle parity |
| close-7 | `.z-goldenlayout .lm_tab:has(> .lm_close_tab)` | padding-right | `var(--zk-spacing-2)` | added 2026-06-05 — user ruling: the close button sits nearer the edge than the text inset, per the dismiss-affordance convention (`doc/spec/md3-close-affordance-placement.md`: MD3 input chip 8dp vs 16dp; MUI deleteIcon 5px vs 12px; Chrome tabs ~6–8px). Closable-tab geometry = 16/4/8 (leading inset / label–icon gap / trailing inset); non-closable tabs keep symmetric `0 spacing-4`. `:has(> .lm_close_tab)` discriminates exactly — GL removes the close node entirely for `closable="false"` |
| close-glyph-1 | `.z-goldenlayout .lm_close_tab::before` | mask-image | Lucide `x` data-URI (path signature `M18 6 6 18`) | revised 2026-06-05 — was `content: "\00d7"` text glyph; now the same Lucide `x` panel/tabbox icons resolve to (M14/M15) |
| close-glyph-2 | `.z-goldenlayout .lm_close_tab::before` | position | `absolute` (computed `display` will be `block` — blockified) | revised 2026-06-05 with M22 — was `display: inline-block` (2026-06-03, "pseudo must occupy box"); the abs-centered pseudo (`inset: 0; margin: auto`) guarantees both the box (M14) and concentricity (M22) regardless of the host's JS-written inline display |
| close-glyph-3 | `.z-goldenlayout .lm_close_tab::before` | width | `12px` | revised 2026-06-05 — mask icon size (was font-size 14px); matches `.z-tab-button`'s 12px icon |
| close-glyph-4 | `.z-goldenlayout .lm_close_tab::before` | height | `12px` | added 2026-06-05 — mask icon size |
| max-glyph-1 | `.z-goldenlayout .lm_controls .lm_maximise::after` | mask-image | Lucide `expand` data-URI (path signature `M21 16v5h-5`) | revised 2026-06-05 — was `content: "\2922"` (⤢) text glyph; now the same Lucide `expand` panel's maximize (`z-icon-expand`) resolves to (M15) |
| close-ctrl-glyph-1 | `.z-goldenlayout .lm_controls .lm_close::after` | mask-image | Lucide `x` data-URI (path signature `M18 6 6 18`) | added 2026-06-05 — same Lucide `x` panel's close (`z-icon-times`) resolves to (M15) |
| ctrl-1 | `.z-goldenlayout .lm_controls > li` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §3 |
| ctrl-2 | `.z-goldenlayout .lm_controls > li:hover` | color | `var(--zk-color-primary)` | DESIGN.md §3 |
| ctrl-3 | `.z-goldenlayout .lm_controls > li` | cursor | `pointer` | usability |
| ctrl-4 | `.z-goldenlayout .lm_controls > li` | width | `28px` | added 2026-06-05 — panel-icon family parity (user finding #1; DESIGN.md §12 control-icon-button table; M18) |
| ctrl-5 | `.z-goldenlayout .lm_controls > li` | height | `28px` | added 2026-06-05 — panel-icon family parity (M18) |
| ctrl-6 | `.z-goldenlayout .lm_controls > li` | border-radius | `var(--zk-shape-corner-full)` | added 2026-06-05 — panel-icon family parity (M18) |
| ctrl-7 | `.z-goldenlayout .lm_controls > li:hover` | background-color | `color-mix(in srgb, var(--zk-color-on-surface) 8%, transparent)` | added 2026-06-05 — state-layer hover circle; panel's `surface-container` hover fill is invisible on the `surface-container` strip, so the mix-based layer is used (DESIGN.md §8) |
| ctrl-8 | `.z-goldenlayout .lm_controls > li::after` | width | `14px` | added 2026-06-05 — mask-icon size parity with panel (14px icon in 28px button) |
| ctrl-9 | `.z-goldenlayout .lm_controls > li::after` | height | `14px` | added 2026-06-05 — mask-icon size parity with panel |
| spl-1 | `.z-goldenlayout .lm_splitter` | background-color | `transparent` | corrected 2026-06-06 (was `surface-container`, §14 family fill 2026-06-04 — which equaled the abutting `.lm_header` bg and erased the boundary, user finding round 2): GL panels are self-bordered cards, the splitter region is a card gutter; M21 asserts the contrast outcome. Full circle from the original pre-family `transparent` — but now WITH pill/grips/cursors carrying the affordance |
| spl-2 | `.z-goldenlayout .lm_splitter:hover` | background-color | `color-mix(in srgb, var(--zk-color-primary) calc(var(--zk-state-hover-opacity) * 100%), transparent)` | revised 2026-06-06 — translucent family hover tint over the transparent gutter (was mixed with `surface-container`) |
| spl-3 | `.z-goldenlayout .lm_splitter.lm_dragging` | background-color | `color-mix(in srgb, var(--zk-color-primary) calc(var(--zk-state-pressed-opacity) * 100%), transparent)` | revised 2026-06-06 — translucent family pressed tint (was mixed with `surface-container`) |
| spl-cursor-1 | `.z-goldenlayout .lm_splitter.lm_horizontal` | cursor | `col-resize` | added 2026-06-05 — family spec (DESIGN.md §14); GL's bundled CSS that normally provides cursors is never loaded in ZK (was `auto` — user finding #3) |
| spl-cursor-2 | `.z-goldenlayout .lm_splitter.lm_vertical` | cursor | `row-resize` | added 2026-06-05 — family spec |
| spl-pill-1 | `.z-goldenlayout .lm_splitter::before` | background-color | `var(--zk-color-outline-variant)` | added 2026-06-05 — family actuator pill, idle fill (DESIGN.md §14; supersedes the over-broad "no pill" exception) |
| spl-pill-2 | `.z-goldenlayout .lm_splitter::before` | border-radius | `var(--zk-shape-corner-full)` | added 2026-06-05 — family pill shape |
| spl-pill-3 | `.z-goldenlayout .lm_splitter.lm_horizontal::before` | width / height | `8px` / `28px` (cross-axis = bar thickness, long-axis 28px; vertical bar) | added 2026-06-05 — family pill size |
| spl-pill-4 | `.z-goldenlayout .lm_splitter.lm_vertical::before` | width / height | `28px` / `8px` (horizontal bar) | added 2026-06-05 — family pill size |
| spl-pill-5 | `.z-goldenlayout .lm_splitter:hover::before` | background-color | `var(--zk-color-primary)` | added 2026-06-05 — family pill hover fill |
| spl-grip-1 | `.z-goldenlayout .lm_splitter.lm_horizontal::after` | mask-image | Lucide `ellipsis-vertical` data-URI | added 2026-06-05 — grip dots replace the opacity-.5 text glyph (user finding #3: imperceptible); family grip icons |
| spl-grip-2 | `.z-goldenlayout .lm_splitter.lm_vertical::after` | mask-image | Lucide `ellipsis` data-URI | added 2026-06-05 — grip dots, horizontal bar |
| spl-grip-3 | `.z-goldenlayout .lm_splitter::after` | width / height | `8px` / `8px` | added 2026-06-05 — family grip icon size |
| spl-grip-4 | `.z-goldenlayout .lm_splitter::after` | background-color (mask fill) | `var(--zk-color-on-surface-variant)` at `opacity: 1` | added 2026-06-05 — family: grips always visible (was opacity .5) |
| spl-grip-5 | `.z-goldenlayout .lm_splitter:hover::after` | background-color (mask fill) | `var(--zk-color-on-primary)` | added 2026-06-05 — family: icons flip to on-primary on pill hover |
| panel-1 | `.z-goldenpanel` | background-color | `var(--zk-color-surface)` | DESIGN.md §3 (iceblue: colorBackground3 = white) |
| panel-2 | `.z-goldenpanel` | border | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §3 (iceblue: goldenLayoutBorderColor = #d2d2d2 → outline-variant) |
| panel-top-radius-1 | `.z-goldenpanel` | border-top-left-radius | `var(--zk-shape-card)` | revised 2026-06-04 — Panel-alignment ruling: theme card standard 6px (DESIGN.md §5); matches .lm_header top radius |
| panel-top-radius-2 | `.z-goldenpanel` | border-top-right-radius | `var(--zk-shape-card)` | revised 2026-06-04 — Panel-alignment ruling: theme card standard 6px (DESIGN.md §5); matches .lm_header top radius |
| panel-3 | `.z-goldenpanel` | border-bottom-left-radius | `var(--zk-shape-card)` | revised 2026-06-04 — Panel-alignment ruling: uniform card radius at the theme standard 6px (was `corner-small` 8px) |
| panel-4 | `.z-goldenpanel` | border-bottom-right-radius | `var(--zk-shape-card)` | revised 2026-06-04 — Panel-alignment ruling: uniform card radius at the theme standard 6px (was `corner-small` 8px) |
| panel-shadow-1 | `.z-goldenpanel` | box-shadow | `var(--zk-elevation-1)` | added 2026-06-03 — per-panel card elevation (moved from outer wrapper) |
| panel-5 | `.z-goldenpanel` | padding | `var(--zk-spacing-4) var(--zk-spacing-4) var(--zk-spacing-6)` | revised 2026-06-04 — Panel-alignment ruling: MD3 card standard 16/16/24 (DESIGN.md §10, matches panel.md c7); previous 20px/16px was iceblue legacy |
| panel-6 | `.z-goldenpanel` | overflow | `auto` | structural (content scroll) |
| panel-7 | `.z-goldenpanel` | box-sizing | `border-box` | structural |
| max-1 | `.z-goldenlayout .lm_item.lm_stack.lm_maximised` | position | `absolute` | revised 2026-06-05 — NOT JS-driven: the THEME must supply it (goldenlayout-base.css is never loaded; ZK's codegen `goldenlayout.css.dsp` reproduces it). Selector needs ≥(0,4,0) specificity to beat `.z-goldenlayout .lm_item.lm_stack { position: relative }` — a bare `.lm_maximised` rule loses and the stack stays a half-width flex item |
| max-2 | `.z-goldenlayout .lm_item.lm_stack.lm_maximised` | top / left / z-index | `0` / `0` / `40` | added 2026-06-05 — base-CSS structural block |
| max-3 | `.z-goldenlayout .lm_maximised .lm_controls .lm_maximise::after` | mask-image | Lucide `minimize-2` data-URI (restore icon; panel's `z-icon-compress` equivalent) | added 2026-06-05 — maximised state flips maximize → restore (iceblue: FA `\f066`) |
| base-1 | `.z-goldenlayout .lm_maximise_placeholder` | display | `none` | added 2026-06-05 — base-CSS structural block |
| base-2 | `.lm_dragging, .lm_dragging *` | cursor / user-select | `move !important` / `none` | added 2026-06-05 — base-CSS structural block (GL adds `lm_dragging` to `<body>` during drag; prevents text selection) |
| base-3 | `.lm_transition_indicator` | display | `none` (selector UNSCOPED — GL appends the node to `<body>`, outside `.z-goldenlayout`) | added 2026-06-05 — base-CSS structural block; was a visible in-flow element at rest |
| base-4 | `.z-goldenlayout-dropTargetIndicator` | display / position / z-index | `none` / `absolute` / `20` | added 2026-06-05 — base-CSS structural block; without it the indicator was a visible in-flow dashed line at rest (GL toggles display inline during drag) |
| base-5 | `.z-goldenlayout-dragProxy` | position / top / left / z-index | `absolute` / `0` / `0` / `30` | added 2026-06-05 — base-CSS structural block |
| base-6 | `.z-goldenlayout .lm_splitter` | z-index | `2` | added 2026-06-05 — base-CSS structural block (splitter above pane content) |
| base-7 | `.z-goldenlayout .lm_controls .lm_tabdropdown::before` | mask-image | Lucide `chevron-down` data-URI, 14×14 mask box | added 2026-06-05 — overflow-dropdown trigger icon (iceblue: FA `\f107`); GL inline-hides the `<li>` until tabs overflow, but the icon must exist for when it shows |
| proxy-1 | `.z-goldenlayout-dragProxy .lm_tab` | box-shadow | `var(--zk-elevation-2)` | DESIGN.md §6 (iceblue: 0 0 5px rgba(0,0,0,0.19)) |
| proxy-2 | `.z-goldenlayout-dragProxy .lm_tab` | background-color | `var(--zk-color-surface-container)` | DESIGN.md §3 |
| proxy-3 | `.z-goldenlayout-dragProxy .lm_tab::after` | background-color | `var(--zk-color-primary)` | DESIGN.md §3 (active indicator on dragged tab) |
| proxy-4 | `.z-goldenlayout-dragProxy > .lm_content` | box-shadow | `var(--zk-elevation-2)` | DESIGN.md §6 |
| proxy-5 | `.z-goldenlayout-dragProxy > .lm_content` | background-color | `var(--zk-color-surface)` | DESIGN.md §3 |
| drop-1 | `.z-goldenlayout-dropTargetIndicator` | border | `1px dashed var(--zk-color-primary)` | DESIGN.md §3 (iceblue: colorPrimary) |
| drop-2 | `.z-goldenlayout-dropTargetIndicator .lm_inner` | background-color | `color-mix(in srgb, var(--zk-color-primary) 10%, transparent)` | DESIGN.md §3 (iceblue: rgba(0,147,249,0.15)) |
| dd-1 | `.z-goldenlayout-dropdown` | background-color | `var(--zk-color-surface)` | DESIGN.md §3 |
| dd-2 | `.z-goldenlayout-dropdown` | box-shadow | `var(--zk-elevation-2)` | DESIGN.md §6 (iceblue: 0 2px 4px rgba(0,0,0,0.5)) |
| dd-3 | `.z-goldenlayout-dropdown` | border-radius | `var(--zk-shape-corner-small)` | DESIGN.md §6 |
| dd-4 | `.z-goldenlayout-dropdown` | padding | `var(--zk-spacing-1)` | DESIGN.md §5 (iceblue: 4px) |
| dd-5 | `.z-goldenlayout-dropdown > li:hover` | background-color | `var(--zk-color-surface-container-high)` | DESIGN.md §3 (iceblue: colorPrimaryLighter — Marble maps to surface-container-high for hover) |
| dd-6 | `.z-goldenlayout-dropdown > li` | padding | `var(--zk-spacing-2) var(--zk-spacing-2)` | DESIGN.md §5 (iceblue: 8px) |

## Accepted MD3 deviations

> User-only additions (same spirit as `contract-approved`) — the orchestrator and agents never add entries here. Gate 2 skips deviations listed in this section.

(none yet)

## State matrix

| state | selector | property ids |
|-------|----------|--------------|
| wrapper default | `.z-goldenlayout` | wrap-4, wrap-5 |
| header strip | `.z-goldenlayout .lm_header` | hdr-1, hdr-2, hdr-3, hdr-4, hdr-5, hdr-6, hdr-7, hdr-7b, hdr-flex-1, hdr-flex-2, hdr-flex-3 |
| tab strip flex | `.z-goldenlayout .lm_tabs` | hdr-tabs-flex, hdr-tabs-stretch |
| header controls flex | `.z-goldenlayout .lm_controls` | hdr-controls-flex |
| tab default | `.z-goldenlayout .lm_tab` | tab-1, tab-2, tab-3, tab-5, tab-sl-1, tab-sl-2, tab-sl-3 |
| tab hover | `.z-goldenlayout .lm_tab:hover` | tab-h1, tab-h2 |
| tab active | `.z-goldenlayout .lm_tab.lm_active` | tab-a1, tab-a2, tab-a3, tab-a4, tab-a5, tab-a6 |
| tab closable | `.z-goldenlayout .lm_tab:has(> .lm_close_tab)` | close-7 |
| close icon default | `.z-goldenlayout .lm_close_tab` | close-1, close-3, close-4, close-5, close-glyph-1, close-glyph-2, close-glyph-3, close-glyph-4 |
| close icon hover | `.z-goldenlayout .lm_close_tab:hover` | close-2, close-6 |
| header controls default | `.z-goldenlayout .lm_controls > li` | ctrl-1, ctrl-3, ctrl-4, ctrl-5, ctrl-6, ctrl-8, ctrl-9, max-glyph-1, close-ctrl-glyph-1 |
| header controls hover | `.z-goldenlayout .lm_controls > li:hover` | ctrl-2, ctrl-7 |
| splitter default | `.z-goldenlayout .lm_splitter` | spl-1, spl-cursor-1, spl-cursor-2, spl-pill-1, spl-pill-2, spl-pill-3, spl-pill-4, spl-grip-1, spl-grip-2, spl-grip-3, spl-grip-4 |
| splitter hover | `.z-goldenlayout .lm_splitter:hover` | spl-2, spl-pill-5, spl-grip-5 |
| splitter dragging | `.z-goldenlayout .lm_splitter.lm_dragging` | spl-3 |
| panel content | `.z-goldenpanel` | panel-1, panel-2, panel-top-radius-1, panel-top-radius-2, panel-3, panel-4, panel-shadow-1, panel-5, panel-6, panel-7 |
| maximised stack | `.z-goldenlayout .lm_item.lm_stack.lm_maximised` | max-1, max-2, max-3 |
| base structural (rest) | `.z-goldenlayout-dropTargetIndicator`, `.lm_transition_indicator`, `.lm_maximise_placeholder`, `.lm_dragging` | base-1, base-2, base-3, base-4, base-5, base-6, base-7 |
| drag proxy tab | `.z-goldenlayout-dragProxy .lm_tab` | proxy-1, proxy-2, proxy-3 |
| drag proxy content | `.z-goldenlayout-dragProxy > .lm_content` | proxy-4, proxy-5 |
| drop target | `.z-goldenlayout-dropTargetIndicator` | drop-1, drop-2 |
| overflow dropdown | `.z-goldenlayout-dropdown` | dd-1, dd-2, dd-3, dd-4 |
| dropdown item hover | `.z-goldenlayout-dropdown > li:hover` | dd-5 |

## States to evaluate
- [ ] default — outer border, wrapper corner-radius, header strip visible
- [ ] tab default — inactive tab label color (on-surface-variant)
- [ ] tab hover — label shifts to on-surface, primary state-layer overlay at hover opacity (no border frame)
- [ ] tab active — primary label color, 2px primary underline indicator, transparent background (strip shows through — `.z-tab-selected` parity)
- [ ] close icon on tab — 16×16 `.z-tab-button`-parity button, 12px Lucide `x` mask, on-surface-variant, hover on-surface + bg circle
- [ ] header controls — 28×28 panel-icon-parity buttons, 14px Lucide `expand`/`x` masks, on-surface-variant, hover primary + state-layer circle
- [ ] splitter default — transparent gutter bar (≠ adjacent `.lm_header` bg, M21) + outline-variant actuator pill + visible grip dots + col/row-resize cursor (splitter-family affordance, DESIGN.md §14; GL gutter exception 2026-06-06)
- [ ] splitter hover — translucent primary hover tint over the transparent gutter; pill fills primary, grip dots flip to on-primary
- [ ] panel content — surface background, outline-variant border, correct padding
- [ ] maximised stack — stack fills full layout area
- [ ] drag proxy — elevation-2 shadow on tab and content ghost
- [ ] drop target indicator — dashed primary border, 10% primary fill
- [ ] overflow dropdown — surface background, elevation-2, corner-small

## T3 wrapper boundary

goldenlayout is T3: GoldenLayoutJS owns and writes the `lm_*` DOM tree. ZK only renders `.z-goldenlayout` (the root `<div>`) and `.z-goldenpanel` (panel content nodes). All other visible elements are GoldenLayout-injected.

Because GoldenLayout 2.x exposes no CSS custom properties, Marble must restyle all internal `lm_*` elements directly via scoped selectors. The previous `forbidden-selectors: .lm_*` rule was incorrect — those selectors MUST be styled to achieve visual parity with the iceblue baseline.

```yaml
wrapper-selectors:
  - .z-goldenlayout
  - .z-goldenpanel
  - .z-goldenlayout-dragProxy
  - .z-goldenlayout-dropdown
  - .z-goldenlayout-dropTargetIndicator
styled-internal-selectors:
  # These are GoldenLayout-injected but must be restyled under .z-goldenlayout scope:
  - .z-goldenlayout .lm_header
  - .z-goldenlayout .lm_tab
  - .z-goldenlayout .lm_tab.lm_active
  - .z-goldenlayout .lm_tab:hover
  - .z-goldenlayout .lm_controls
  - .z-goldenlayout .lm_splitter
forbidden-selectors:
  # Do NOT set geometry (position/top/left/width/height) on these — GoldenLayout sets them inline:
  - ".z-goldenlayout .lm_stack[style]"   # inline geometry — never override
  - ".z-goldenlayout .lm_items[style]"   # inline geometry — never override
  - ".z-goldenlayout .lm_content[style]" # inline geometry — never override
theme-bridge:
  strategy: direct-override
  available: false
  notes: >
    GoldenLayout 2.x has no CSS variable API. All styling is via direct selector
    overrides scoped under .z-goldenlayout. No GoldenLayout theme file is imported.
```
