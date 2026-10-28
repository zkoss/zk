# Component: cropper (theme design)
tier: T3
category: media
preview: ${PREVIEW_URL}/cropper.zul
rules: see .claude/skills/zk-component-rules/components/cropper.md
contract-approved: true  # user-approved 2026-06-04 (contract-audit GATE2: PASS, critical=0); ## Visual outcome + ## Outcome assertions added 2026-06-05 per user feedback (recipe-agnostic rows describing stock ZK/Jcrop anatomy — no theme-value change; outcome-pass pending next evaluator run); toolbar MD3 floating-toolbar redesign (c4 revised, c5 replaced, c7–c12 new, M5 revised) + dead-space M7 added 2026-06-05 per user feedback round 2 (prompt2 v2) — re-eval pending
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/cropper/Cropper.ts
  - zkmax/cropper/mold/cropper.js
js-source-hash: c72d379f96cb3f5f3d91f8df64d52c7ea3ae678f37d776d2e478252841a017e5
closest-sibling: none — T3 media wrapper; closest pattern is signature (toolbar-over-canvas approach) and pdfviewer (thin chrome over library-owned content)
mockup-needed: Y  # Marble diverges from the ZKDoc baseline — ZKCompRef_Cropper.png shows a borderless, unrounded wrapper; Marble adds the Paper.outlined card treatment (c1/c2/c3)

## References
- ZKDoc canonical: /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/cropper.md (PE component, since 8.6.0; built-in toolbar = Crop + Cancel buttons via `toolbarVisible`)
- ZKDoc image: /Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/ZKCompRef_Cropper.png — confirms the floating Crop/Cancel toolbar near the selection; wrapper itself is borderless in stock ZK (Marble's border/radius is an intentional divergence, hence mockup-needed: Y)
- MUI CSS: no analog — no MUI image-cropper component. Reference `Paper.css` for the wrapper card convention (border, border-radius, overflow) and see DESIGN.md §8 for novel-component policy.
- DESIGN.md sections: §3 (color roles — surface, outline-variant), §5 (spacing), §6 (shape — extra-small corner for wrapper)
- Iceblue baseline: doc/contracts/baselines/cropper-iceblue.png
- HTML contract: doc/contracts/cropper.html

## T3 wrapper boundary

cropper is T3: Jcrop library owns and writes all crop-selection DOM at runtime, **but ships no stylesheet** — theme CSS is the only source of structural rules for that subtree (boundary corrected 2026-06-05 after M3 found all 8 handles at 0×0; the earlier blanket "forbidden" list was over-broad). Geometry must match the upstream codegen `cropper.css.dsp` verbatim (`/Users/hawk/Documents/workspace/ZK10/zkcml/zkmax/codegen/resources/web/js/zkmax/cropper/css/cropper.css.dsp`); only decorative values (colors, opacity) are theme choices.

```yaml
restyle-freely:                      # fully theme-owned design surface
  - .z-cropper
  - .z-cropper-toolbar
  - .z-cropper-toolbar > ul
  - .z-cropper-toolbar > ul > li
  - .z-cropper-toolbar > ul > li > a
structural-required:                 # Jcrop-injected, unstyled by Jcrop — theme MUST ship upstream geometry; decorative values only may diverge
  - .z-cropper-handle      # 6×6 blocks + 8 per-ordinal position/transform rules
  - .jcrop-dragbar         # 6px bars stretched per axis + per-ordinal transforms
  - .z-cropper-area        # outline 1px solid white; cursor: move
  - .z-cropper-vline       # 1px vertical guide line
  - .z-cropper-hline       # 1px horizontal guide line
  - .z-cropper-tracker     # 100%×100% event overlay
  - .z-cropper-holder      # cursor: crosshair; never set width/height (JS-owned); img max-width: none
forbidden-selectors:
  - .z-cropper-canvas      # DOES NOT EXIST — absent from live DOM (confirmed eval 2026-05-13)
```

## Design Contract

The cropper renders as a **bordered image area** with an optional floating action toolbar. The wrapper `.z-cropper` is a contained card: thin `outline-variant` border on all four sides with `extra-small` corner radius (4px), and `overflow: hidden` to clip the Jcrop-injected content within the rounded boundary. No shadow or fill — the image itself is the content.

The floating toolbar (`.z-cropper-toolbar`) appears only when a crop selection is active; it is JS-positioned near the selection rectangle. It follows the **MD3 floating-toolbar** pattern (the contextual-actions-over-content idiom — MD3 has no cropper component): a `surface-container` pill (`corner-full`) lifted with `elevation-2`, `spacing-1` inner padding, and `spacing-2`/`spacing-1` margin so it clears the selection edge by 8px (the JS measures `outerWidth(true)`, so margin is the sanctioned gap mechanism — upstream parity). The action items ("crop", "cancel") are compact MD3 **text buttons**: 32px min-height, `0 spacing-3` padding, `corner-full`, label-large typescale, capitalized labels. Crop is the confirming action and reads in `primary`; cancel is dismissive and reads in `on-surface-variant`. Both carry the theme's standard hover/pressed state layer (`::before` overlay at `--zk-state-hover-opacity` / `--zk-state-pressed-opacity`, per button.css convention).

The transition on toolbar show/hide is JS-controlled (`display: none ↔ block`) with no CSS animation in the current implementation.

## Visual outcome

What a user must see, independent of theme styling (ground truth: `images/ZKCompRef_Cropper.png`):

1. **Source image** — displayed at its natural size inside the wrapper.
2. **Crop selection** — the user drags out a rectangular region marking the area to be cropped. The selection interior shows the image at **full brightness**, while everything outside it is **dimmed** (Jcrop renders a reduced-opacity background image with a full-opacity duplicate clipped to the selection).
3. **8 resize handles** — square control points around the selection rectangle (4 corners + 4 edge midpoints, `.z-cropper-handle.ord-*`), each draggable to resize. Edge dragbars (`.jcrop-dragbar.ord-*`) additionally allow edge-resizing; the selection interior is draggable to move the whole region.
4. **Action toolbar** — while a selection is active, a small toolbar appears adjacent to (typically just below) the selection rectangle with exactly two actions: **crop** (confirm) and **cancel** (release selection). With no selection, the toolbar is hidden and the image renders undimmed with no handles.

> **Measurement note**: the selection/handle/dim elements are Jcrop-injected (`structural-required` list below) — outcome rows MAY measure them (bounding boxes, counts, opacity); they exist precisely to catch missing/broken structural CSS (e.g. the 2026-06-05 M3 failure: no `.z-cropper-handle` rules → all 8 handles 0×0) or theme CSS side-effects (`overflow: hidden` clipping, a reset killing the dim mask).

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | the visible image — `.z-cropper-holder img` after Jcrop init (Jcrop hides the source `.z-cropper > img` with `display: none` and shows its duplicate in the holder) — bbox ≥ 48×48px AND fully inside `.z-cropper` root bbox | source image visible and in-frame (selector precision 2026-06-05: measuring the hidden source img reads 0×0) |
| M2 | with selection active: the Jcrop background image (or shade element) renders dimmer than the selection interior — bg `opacity < 1` OR a shade element with `background ≠ transparent` overlays the holder, AND the duplicate image inside the selection container has `opacity = 1` | selection reads as a highlighted region against a dimmed surround |
| M3 | exactly 8 `.z-cropper-handle` elements exist; each bbox ≥ 4×4px AND inside the `.z-cropper-holder` bbox | all 8 resize control points renderable and in-frame |
| M4 | with selection active: `.z-cropper-toolbar` is displayed AND contains exactly 2 `li` items (`.z-cropper-crop`, `.z-cropper-cancel`), each with non-empty text and a non-zero bbox not clipped out of the viewport | both actions (crop, cancel) visible — guards c3 `overflow: hidden` interplay |
| M5 | toolbar bbox lies between 4px and 24px from the selection rectangle bbox edge (adjacent, with visible clearance — neither flush nor floating elsewhere) | toolbar visually associated with the selection but not glued to it (revised 2026-06-05: old `≤24px` passed even when flush — Gate-2 Suggested finding promoted) |
| M6 | with no selection: `.z-cropper-toolbar` computed `display: none` AND no `.z-cropper-handle` visible | rest state is clean — image only, no orphaned chrome |
| M7 | `.z-cropper` root bbox width ≤ `.z-cropper-holder` bbox width + 4px, AND holder bbox ≥ 48×48px | wrapper hugs the image — no dead space inside the outlined card, and no collapse. Three page-level failure modes (all hit 2026-06-05): `width` attr > naturalWidth (ZK caps the img, never upscales → dead space); column-flex parent stretching the root to full width; NO width at all (`onSize → resizeImage(root size)` feedback loop collapses the holder to 0 — cropper requires an externally determined size equal to the image's natural size; see skill Composition invariants) |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-cropper` | border | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §3 (outline-variant = rgba(0,0,0,0.12) — thin wrapper edge) |
| c2 | `.z-cropper` | border-radius | `var(--zk-shape-corner-extra-small)` | DESIGN.md §6 (extra-small = 4px; small visual card, not a large panel) |
| c3 | `.z-cropper` | overflow | `hidden` | structural — required for border-radius to clip Jcrop-injected content |
| c6 | `.z-cropper` | display | `inline-block` | structural — upstream codegen; shrink-wraps the border to the image (found 2026-06-05: block root rendered 600px border around a 300px image) |
| c4 | `.z-cropper-toolbar` | background-color | `var(--zk-color-surface-container)` | MD3 floating-toolbar container color (revised 2026-06-05; was `surface` strip) |
| c5 | `.z-cropper-toolbar` | box-shadow | `var(--zk-elevation-2)` | MD3 floating toolbar lifts with elevation (replaced 2026-06-05; was `border-top` — a docked-strip separator, wrong idiom for a floating pill) |
| c7 | `.z-cropper-toolbar` | border-radius | `var(--zk-shape-corner-full)` | MD3 floating toolbar = pill |
| c8 | `.z-cropper-toolbar` | margin | `var(--zk-spacing-2) var(--zk-spacing-1)` | 8px clearance from the selection edge — `_positionToolbar` measures `outerWidth(true)`, so margin is the sanctioned gap mechanism (upstream parity; Gate-2 Suggested promoted 2026-06-05) |
| c9 | `.z-cropper-crop > a` | color | `var(--zk-color-primary)` | confirming action = primary text button (DESIGN.md §3) |
| c10 | `.z-cropper-cancel > a` | color | `var(--zk-color-on-surface-variant)` | dismissive action = low-emphasis text button |
| c11 | `.z-cropper-toolbar > ul > li > a:hover::before` | opacity | `var(--zk-state-hover-opacity)` (0.08; `:active` → `var(--zk-state-pressed-opacity)` 0.12) | standard theme state-layer pattern (button.css `::before` convention; Gate-2 Suggested promoted 2026-06-05) |
| c12 | `.z-cropper-toolbar > ul > li > a` | border-radius | `var(--zk-shape-corner-full)` | MD3 text button in a floating toolbar — pill items, 32px min-height, label-large |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (no selection) | `.z-cropper` | c1, c2, c3, c6 |
| toolbar visible (selection active) | `.z-cropper-toolbar` | c4, c5, c7, c8 |
| toolbar items (selection active) | `.z-cropper-crop > a`, `.z-cropper-cancel > a` | c9, c10, c12 |
| toolbar item hover/pressed | `.z-cropper-toolbar > ul > li > a` | c11 |

## States to evaluate
- [ ] default with image loaded (border, border-radius, overflow on wrapper; M1, M6, M7)
- [ ] toolbar visible (floating-pill container c4/c5/c7/c8 + item rows c9/c10/c12 with active selection; M2–M5 — requires a live selection: use the preview page's pre-set `x/y/w/h` selection or drag one out before measuring)
- [ ] toolbar item hover and pressed (c11 — `::before` state-layer opacity)
