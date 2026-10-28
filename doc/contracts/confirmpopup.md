# Component: confirmpopup (theme design)
tier: T2
category: feedback
preview: ${PREVIEW_URL}/confirmpopup.zul
rules: see .claude/skills/zk-component-rules/components/confirmpopup.md
contract-approved: true
zk-version: 10.4.0-jakarta
js-source-files:
  - zul/src/main/resources/web/js/zul/wgt/Confirmpopup.ts
  - zul/src/main/resources/web/js/zul/wgt/mold/confirmpopup.js
js-source-hash: 94c95bafa04e432f2b2afba0dad516cc2efbfb65b30473f3c50e6e5ee7804085
closest-sibling: popup (positioning/chrome base only — see skill Sibling decomposition); severity enum shared with native badge/chip
shared-css-file: zul/wgt/css/confirmpopup.css
mockup-needed: Y — no ZKDoc canonical image found for ConfirmPopup (component new in ZK 10.4.0, not yet documented in zkdoc/zk_component_ref); HTML mockup and iceblue baseline PNG were explicitly OUT OF SCOPE for this authoring pass per user instruction — produce doc/contracts/confirmpopup.html and doc/contracts/baselines/confirmpopup-iceblue.png in a follow-up pass before flipping contract-approved.

## References
- MUI CSS: `Surfaces/Popover.css` (anchored floating panel — elevation/positioning analog; MUI's Popover
  has no directional arrow, so the arrow treatment has no direct MUI counterpart) + `Feedback/DialogActions.css`,
  `Feedback/DialogContent.css`, `Feedback/DialogTitle.css` (footer button row / body / header layout analogs)
  at /Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/
- DESIGN.md sections: §3 (Brand & Status Colors — severity → status-color mapping), §4 (Spacing Scale),
  §5 (Corner Radii), §6 (Elevation/Shadow), §7 (Typography), §8 (State-Layer Overlays)
- Precedent: `doc/contracts/coachmark.md` — the closest existing Marble contract for an anchored,
  arrow-pointing floating card (neutral surface + elevation-2 + shape-card), reused here for consistency.
- Native severity mapping precedent: `zul/src/main/resources/web/js/zul/wgt/css/chip.css` (`--zk-chip-color`
  per severity) — confirmpopup's icon color reuses the identical severity→token mapping so the same
  severity token always resolves to the same color across badge/chip/confirmpopup.
- ZKDoc canonical: none found — ConfirmPopup is new in ZK 10.4.0 and has no entry yet under
  `/Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/`.
- Iceblue baseline: doc/contracts/baselines/confirmpopup-iceblue.png (NOT captured this pass — out of scope).
- HTML contract: doc/contracts/confirmpopup.html (NOT authored this pass — out of scope).

## Design Contract

ConfirmPopup is a small anchored **popover card**, not a modal dialog: a neutral
`--zk-color-surface-container-low` surface (the same neutral-card choice as `coachmark`, so all of Marble's
anchored floating cards read as one family) with `--zk-color-on-surface` text, `--zk-shape-card` corner
radius, and `--zk-elevation-2` (dropdown-level) shadow providing the sense of floating above the page — no
border is drawn on the card itself; the shadow alone supplies visual separation, consistent with MUI's
Popover pattern. The directional arrow's two-layer CSS triangle (outer + inner, per the skill's structural
description) uses `--zk-color-outline-variant` for the thin outer edge and the card's own
`--zk-color-surface-container-low` for the inner fill, so the arrow reads as a seamless extension of the
card rather than a separate shape. The optional header row is set in `--zk-typescale-title-small` weight
(500) on `--zk-color-on-surface`, separated from the body by a `--zk-color-outline-variant` divider — the
same divider token closes the footer row from the body above it. Severity recolors **only the icon**
(matching the ZK reference behavior recorded in the skill file) via the identical severity→token mapping
`chip.css` already established: `info → --zk-color-status-info`, `success → --zk-color-success`,
`warning → --zk-color-warning`, `danger → --zk-color-error`, `secondary → --zk-color-on-surface-variant` —
this keeps a given severity token resolving to the same color everywhere in the theme. The footer's two
buttons are deliberately unequal emphasis, mirroring the standard MD3 confirm/cancel action hierarchy: OK is
a filled, high-emphasis action (`--zk-color-primary` background, `--zk-color-on-primary` text,
`--zk-shape-button` radius) and Cancel is a low-emphasis outlined action (transparent background,
`--zk-color-outline` border, `--zk-color-on-surface-variant` text) — so the destructive/committing action is
never visually confusable with the dismissal action, independent of which one currently holds keyboard focus
via `defaultFocus`. Both buttons carry a `--zk-focus-ring` on `:focus-visible`, required because the popup's
own focus trap (see skill) can land keyboard focus on either button.

## Outcome assertions

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | `.z-confirmpopup` has visible framing: `border-width ≥ 1px` OR `box-shadow ≠ none` | must read as a floating card distinguished from the page background beneath it |
| M2 | `.z-confirmpopup` bbox `width ≥ 160px AND height ≥ 60px` | minimum legible card size — catches a collapsed/zero-size popup when message/header are both empty |
| M3 | `.z-confirmpopup-body` bbox `width ≥ 0.9 × .z-confirmpopup` content-box width | body fills the card horizontally — no dead space beside the message |
| M4 | when `.z-confirmpopup-icon` is present: `\|icon.bbox.top − message.bbox.top\| ≤ 4px` (top-aligned icon+text row) AND `icon.bbox.right ≤ message.bbox.left` (icon precedes message, no overlap) | icon and message must read as one aligned row, not stacked or overlapping |
| M5 | `.z-confirmpopup-cancel` and `.z-confirmpopup-ok` sit on one row: `\|cancel.bbox.top − ok.bbox.top\| ≤ 2px`, `cancel.bbox.right ≤ ok.bbox.left` (DOM/tab order preserved left-to-right), and `.z-confirmpopup-footer.bbox.right − ok.bbox.right ≤ 24px` (right-aligned as a group) | footer must read as one right-aligned button row matching the fixed cancel-then-ok DOM order |
| M6 | `.z-confirmpopup-ok` and `.z-confirmpopup-cancel` differ in at least one of `{background-color, border-color, box-shadow}` | the committing action (OK) must be visually higher-emphasis than the dismissing action (Cancel) — they must never look identical |
| M7 | `.z-confirmpopup-arrow` has non-zero bbox (`width ≥ 4px AND height ≥ 4px`) in every placement, AND its bbox touches or overlaps `.z-confirmpopup`'s bbox on the placement-facing edge (it is allowed — by design — to protrude outside the card toward the trigger, so containment is NOT required, only attachment) | arrow is the only visual indicator of which element the popup is anchored to; it must never render as zero-size or fully detached from the card |
| M8 | sampling `.z-confirmpopup-icon` computed `color` across all five severities, at least 3 distinct pairwise values are found | severity must be visually distinguishable, not a class toggle with no visual effect |
| M-icon-visible | when `iconSclass` is non-empty: `.z-confirmpopup-icon` bbox is non-zero (`width ≥ 6px AND height ≥ 6px`) AND `bbox.bottom ≤ root.bbox.bottom + 2px` AND `bbox.right ≤ root.bbox.right` | the icon must actually render inside the card, not just exist as a DOM node with collapsed/invisible geometry |
| M-header-divider-visible | when `.z-confirmpopup-header` is present: its `border-bottom-width ≥ 1px` (or an adjacent `.z-confirmpopup-body` top border/shadow supplies the same separation) | header must read as a distinct title row, not run together with the message body |

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-confirmpopup` | background-color | `var(--zk-color-surface-container-low)` | DESIGN.md §3 — neutral popover card, consistent with coachmark precedent |
| c2 | `.z-confirmpopup` | color | `var(--zk-color-on-surface)` | DESIGN.md §3 |
| c3 | `.z-confirmpopup` | border-radius | `var(--zk-shape-card)` | DESIGN.md §5 — card radius for container surfaces |
| c4 | `.z-confirmpopup` | box-shadow | `var(--zk-elevation-2)` (aka `var(--zk-elevation-dropdown)`) | DESIGN.md §6 — dropdown/popover elevation |
| c5 | `.z-confirmpopup` | border | `none` | shadow alone supplies separation (MUI Popover pattern); satisfies M1 via box-shadow |
| c6 | `.z-confirmpopup-header` | border-bottom | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §3/§11 — divider token between header and body |
| c7 | `.z-confirmpopup-header` | font-size, font-weight | `var(--zk-typescale-title-small-size)`, `var(--zk-typescale-title-small-weight)` (500) | DESIGN.md §7 |
| c8 | `.z-confirmpopup-header` | color | `var(--zk-color-on-surface)` | DESIGN.md §3 |
| c9 | `.z-confirmpopup-footer` | border-top | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §3/§11 — divider between body and footer |
| c10 | `.z-confirmpopup-info .z-confirmpopup-icon` | color | `var(--zk-color-status-info)` | DESIGN.md §3 — same mapping as `chip.css` `.z-chip-info` |
| c11 | `.z-confirmpopup-success .z-confirmpopup-icon` | color | `var(--zk-color-success)` | DESIGN.md §3 — same mapping as `chip.css` `.z-chip-success` |
| c12 | `.z-confirmpopup-warning .z-confirmpopup-icon` | color | `var(--zk-color-warning)` | DESIGN.md §3 — same mapping as `chip.css` `.z-chip-warning` |
| c13 | `.z-confirmpopup-error .z-confirmpopup-icon` | color | `var(--zk-color-error)` | DESIGN.md §3 — same mapping as `chip.css` `.z-chip-error` |
| c14 | `.z-confirmpopup-neutral .z-confirmpopup-icon` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §3 — same mapping as `chip.css` `.z-chip-neutral` |
| c15 | `.z-confirmpopup-ok` | background-color | `var(--zk-color-primary)` | DESIGN.md §3 — high-emphasis committing action |
| c16 | `.z-confirmpopup-ok` | color | `var(--zk-color-on-primary)` | DESIGN.md §3 |
| c17 | `.z-confirmpopup-ok`, `.z-confirmpopup-cancel` | border-radius | `var(--zk-shape-button)` | DESIGN.md §5 |
| c18 | `.z-confirmpopup-cancel` | background-color | `transparent` | DESIGN.md §3 — low-emphasis dismiss action |
| c19 | `.z-confirmpopup-cancel` | border | `1px solid var(--zk-color-outline)` | DESIGN.md §3 |
| c20 | `.z-confirmpopup-cancel` | color | `var(--zk-color-on-surface-variant)` | DESIGN.md §3 |
| c21 | `.z-confirmpopup-ok:focus-visible`, `.z-confirmpopup-cancel:focus-visible` | outline | `var(--zk-focus-ring)` | DESIGN.md §8 — keyboard focus indicator, required by the popup's own Tab-trap |
| c22 | `.z-confirmpopup-arrow::before` (outer layer) | border-color | `var(--zk-color-outline-variant)` (on the placement-facing side; transparent on the other three) | DESIGN.md §11 — thin edge so the arrow doesn't disappear against a similarly-toned page background |
| c23 | `.z-confirmpopup-arrow::after` (inner layer) | border-color | `var(--zk-color-surface-container-low)` (on the placement-facing side; transparent on the other three) | matches c1 — arrow reads as a seamless extension of the card fill |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (no header, no message, warning severity, top placement) | `.z-confirmpopup.z-confirmpopup-warning.z-confirmpopup-placement-top` | c1, c2, c3, c4, c5, c12, c15–c20, M1, M2, M7 |
| with-header | `.z-confirmpopup-header` | c6, c7, c8, M-header-divider-visible |
| with-message | `.z-confirmpopup-message` | M3 |
| with-icon | `.z-confirmpopup-icon` | M4, M-icon-visible |
| severity-info | `.z-confirmpopup-info .z-confirmpopup-icon` | c10, M8 |
| severity-success | `.z-confirmpopup-success .z-confirmpopup-icon` | c11, M8 |
| severity-warning | `.z-confirmpopup-warning .z-confirmpopup-icon` | c12, M8 |
| severity-error | `.z-confirmpopup-error .z-confirmpopup-icon` | c13, M8 |
| severity-neutral | `.z-confirmpopup-neutral .z-confirmpopup-icon` | c14, M8 |
| placement-top | `.z-confirmpopup-placement-top` | c22, c23, M7 |
| placement-bottom | `.z-confirmpopup-placement-bottom` | c22, c23, M7 |
| placement-left | `.z-confirmpopup-placement-left` | c22, c23, M7 |
| placement-right | `.z-confirmpopup-placement-right` | c22, c23, M7 |
| footer-buttons | `.z-confirmpopup-footer` | c9, c15–c20, M5, M6 |
| ok-focus-visible | `.z-confirmpopup-ok:focus-visible` | c21 |
| cancel-focus-visible | `.z-confirmpopup-cancel:focus-visible` | c21 |
| open (`.z-confirmpopup-open` present) | `.z-confirmpopup.z-confirmpopup-open` | M1, M2, M7 |

## States to evaluate
- [ ] default (no header, no message, warning severity, top placement)
- [ ] with-header
- [ ] with-message
- [ ] with-icon
- [ ] severity-info
- [ ] severity-success
- [ ] severity-warning
- [ ] severity-error
- [ ] severity-neutral
- [ ] placement-top
- [ ] placement-bottom
- [ ] placement-left
- [ ] placement-right
- [ ] footer-buttons (ok vs cancel emphasis, right-aligned row)
- [ ] ok-focus-visible
- [ ] cancel-focus-visible
- [ ] open
